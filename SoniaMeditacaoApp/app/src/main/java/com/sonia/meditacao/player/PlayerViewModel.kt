package com.sonia.meditacao.player

import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.sonia.meditacao.data.CustomAudioStore
import com.sonia.meditacao.data.Meditation
import com.sonia.meditacao.data.MeditationCatalog
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PlayerUiState(
    val current: Meditation? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val speed: Float = 1f,
    val sleepMinutesLeft: Int? = null
)

class PlayerViewModel(context: Context) : ViewModel() {

    private val appContext = context.applicationContext
    private val store = CustomAudioStore(appContext)

    private var controller: MediaController? = null
    private var progressJob: Job? = null
    private var sleepJob: Job? = null

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _showFavoritesOnly = MutableStateFlow(false)
    val showFavoritesOnly = _showFavoritesOnly.asStateFlow()

    private val _playerState = MutableStateFlow(PlayerUiState())
    val playerState: StateFlow<PlayerUiState> = _playerState.asStateFlow()

    private val _all = MutableStateFlow(MeditationCatalog.bundled)
    val allMeditations: StateFlow<List<Meditation>> = _all.asStateFlow()

    val favorites = store.favorites.stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())
    val lastPlayedId = store.lastPlayedId.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val visibleMeditations: StateFlow<List<Meditation>> =
        combine(_all, _query, _showFavoritesOnly, favorites) { list, q, favOnly, favs ->
            var out = list
            if (favOnly) out = out.filter { favs.contains(it.id) }
            if (q.isNotBlank()) {
                out = out.filter {
                    it.title.contains(q, ignoreCase = true) ||
                        it.subtitle.contains(q, ignoreCase = true)
                }
            }
            out
        }.stateIn(viewModelScope, SharingStarted.Eagerly, MeditationCatalog.bundled)

    init {
        viewModelScope.launch {
            store.customAudios.collect { custom ->
                _all.value = MeditationCatalog.bundled + custom
            }
        }
        connect()
    }

    private fun connect() {
        val token = SessionToken(appContext, ComponentName(appContext, MeditationService::class.java))
        val future = MediaController.Builder(appContext, token).buildAsync()
        future.addListener({
            controller = future.get()
            controller?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _playerState.value = _playerState.value.copy(isPlaying = isPlaying)
                    if (isPlaying) startProgress() else stopProgress()
                }
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) {
                        playNext(auto = true)
                    }
                }
            })
            _playerState.value = _playerState.value.copy(
                isPlaying = controller?.isPlaying == true
            )
        }, MoreExecutors.directExecutor())
    }

    fun setQuery(v: String) { _query.value = v }
    fun toggleFavoritesOnly() { _showFavoritesOnly.value = !_showFavoritesOnly.value }

    fun play(meditation: Meditation) {
        val c = controller ?: return
        viewModelScope.launch { store.setLastPlayed(meditation.id) }

        // Se já é a atual, só alterna play/pause
        if (_playerState.value.current?.id == meditation.id) {
            togglePlayPause()
            return
        }
        val metadata = MediaMetadata.Builder()
            .setTitle(meditation.title)
            .setArtist(meditation.subtitle.ifBlank { "Sônia Meditação" })
            .build()

        val uri = when {
            meditation.filePath != null -> "file://${meditation.filePath}"
            meditation.assetPath != null -> "asset:///${meditation.assetPath}"
            else -> return
        }
        val item = MediaItem.Builder().setUri(uri).setMediaMetadata(metadata).build()
        c.setMediaItem(item)
        c.prepare()
        c.playbackSpeed = _playerState.value.speed
        c.play()
        _playerState.value = _playerState.value.copy(current = meditation, positionMs = 0L)
        startProgress()
    }

    fun togglePlayPause() {
        val c = controller ?: return
        if (c.isPlaying) c.pause() else c.play()
    }

    fun seekTo(ms: Long) { controller?.seekTo(ms.coerceAtLeast(0L)) }

    fun forward10() { controller?.let { it.seekTo(it.currentPosition + 10_000) } }
    fun back10() { controller?.let { it.seekTo((it.currentPosition - 10_000).coerceAtLeast(0)) } }

    fun playNext(auto: Boolean = false) {
        val list = visibleMeditations.value.ifEmpty { _all.value }
        val cur = _playerState.value.current ?: return
        val idx = list.indexOfFirst { it.id == cur.id }
        if (idx == -1) return
        val next = list.getOrNull(idx + 1) ?: if (auto) null else list.firstOrNull()
        if (next != null) play(next) else controller?.pause()
    }

    fun playPrev() {
        val list = visibleMeditations.value.ifEmpty { _all.value }
        val cur = _playerState.value.current ?: return
        val idx = list.indexOfFirst { it.id == cur.id }
        val prev = list.getOrNull(idx - 1) ?: list.lastOrNull()
        if (prev != null) play(prev)
    }

    fun setSpeed(speed: Float) {
        controller?.playbackSpeed = speed
        _playerState.value = _playerState.value.copy(speed = speed)
    }

    fun toggleFavorite(id: String) {
        viewModelScope.launch { store.toggleFavorite(id) }
    }

    fun isFavorite(id: String): Boolean = favorites.value.contains(id)

    fun startSleepTimer(minutes: Int) {
        sleepJob?.cancel()
        _playerState.value = _playerState.value.copy(sleepMinutesLeft = minutes)
        sleepJob = viewModelScope.launch {
            var left = minutes
            while (left > 0) {
                delay(60_000)
                left--
                _playerState.value = _playerState.value.copy(sleepMinutesLeft = left.takeIf { it > 0 })
            }
            controller?.pause()
            _playerState.value = _playerState.value.copy(sleepMinutesLeft = null)
        }
    }

    fun cancelSleepTimer() {
        sleepJob?.cancel()
        _playerState.value = _playerState.value.copy(sleepMinutesLeft = null)
    }

    suspend fun importAudio(uri: android.net.Uri, title: String): Meditation =
        store.importAudio(uri, title)

    fun deleteCustom(m: Meditation) {
        viewModelScope.launch {
            if (_playerState.value.current?.id == m.id) controller?.pause()
            store.deleteCustom(m)
        }
    }

    private fun startProgress() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (true) {
                val c = controller
                if (c != null) {
                    _playerState.value = _playerState.value.copy(
                        positionMs = c.currentPosition.coerceAtLeast(0),
                        durationMs = (if (c.duration > 0) c.duration else 0),
                        isPlaying = c.isPlaying
                    )
                }
                delay(500)
            }
        }
    }

    private fun stopProgress() { /* mantém última posição visível */ }

    override fun onCleared() {
        progressJob?.cancel()
        controller?.release()
        super.onCleared()
    }
}
