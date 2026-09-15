package com.sonia.meditacao

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sonia.meditacao.player.PlayerViewModel
import com.sonia.meditacao.ui.home.HomeScreen
import com.sonia.meditacao.ui.player.MiniPlayer
import com.sonia.meditacao.ui.player.PlayerSheet
import com.sonia.meditacao.ui.theme.SoniaTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }

        setContent {
            SoniaTheme {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    App()
                }
            }
        }
    }
}

@Composable
private fun App() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val vm: PlayerViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PlayerViewModel(context.applicationContext) as T
    })

    val items by vm.visibleMeditations.collectAsState()
    val query by vm.query.collectAsState()
    val favOnly by vm.showFavoritesOnly.collectAsState()
    val favs by vm.favorites.collectAsState()
    val lastId by vm.lastPlayedId.collectAsState()
    val pState by vm.playerState.collectAsState()

    var showPlayer by remember { mutableStateOf(false) }
    var showAdd by remember { mutableStateOf(false) }
    var pendingUri by remember { mutableStateOf<Uri?>(null) }
    var newTitle by remember { mutableStateOf("") }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {}
            pendingUri = uri
            newTitle = ""
            showAdd = true
        }
    }

    HomeScreen(
        items = items,
        query = query,
        onQuery = vm::setQuery,
        favoritesOnly = favOnly,
        onToggleFavOnly = vm::toggleFavoritesOnly,
        favorites = favs,
        lastPlayedId = lastId,
        currentId = pState.current?.id,
        isPlaying = pState.isPlaying,
        onPlay = { vm.play(it); },
        onToggleFav = vm::toggleFavorite,
        onDelete = vm::deleteCustom,
        onAddClick = { picker.launch(arrayOf("audio/*", "application/octet-stream")) },
        onOpenPlayer = { if (pState.current != null) showPlayer = true },
        playerBar = {
            MiniPlayer(
                state = pState,
                onToggle = vm::togglePlayPause,
                onOpen = { showPlayer = true }
            )
        }
    )

    if (showPlayer && pState.current != null) {
        val cur = pState.current!!
        PlayerSheet(
            state = pState,
            isFav = favs.contains(cur.id),
            onDismiss = { showPlayer = false },
            onToggle = vm::togglePlayPause,
            onNext = { vm.playNext(false) },
            onPrev = vm::playPrev,
            onFwd = vm::forward10,
            onBack = vm::back10,
            onSeek = vm::seekTo,
            onSpeed = vm::setSpeed,
            onToggleFav = { vm.toggleFavorite(cur.id) },
            onSleep = vm::startSleepTimer,
            onCancelSleep = vm::cancelSleepTimer
        )
    }

    if (showAdd) {
        AlertDialog(
            onDismissRequest = { showAdd = false },
            title = { Text("Adicionar meditação") },
            text = {
                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    label = { Text("Título (ex: Gratidão da noite)") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val uri = pendingUri
                    if (uri != null) {
                        scope.launch {
                            runCatching {
                                vm.importAudio(uri, newTitle.ifBlank { "Nova meditação" })
                            }
                            showAdd = false
                            pendingUri = null
                        }
                    } else showAdd = false
                }) { Text("Salvar") }
            },
            dismissButton = {
                TextButton(onClick = { showAdd = false; pendingUri = null }) { Text("Cancelar") }
            },
            modifier = Modifier.padding(8.dp)
        )
    }
}
