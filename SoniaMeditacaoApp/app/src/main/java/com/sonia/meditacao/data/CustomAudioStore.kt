package com.sonia.meditacao.data

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID

private val Context.dataStore by preferencesDataStore(name = "sonia_med")

/**
 * Guarda áudios importados pelo botão "+" e favoritos.
 * Tudo fica em DataStore (JSON) + arquivos copiados para filesDir/custom_audio.
 * Assim a Sônia adiciona áudios sem precisar de rebuild.
 */
class CustomAudioStore(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    private val KEY_CUSTOM = stringPreferencesKey("custom_audios_json")
    private val KEY_FAVS = stringPreferencesKey("favorites_json")
    private val KEY_LAST = stringPreferencesKey("last_played_id")

    val customAudios: Flow<List<Meditation>> =
        context.dataStore.data.map { prefs ->
            val raw = prefs[KEY_CUSTOM].orEmpty()
            if (raw.isBlank()) emptyList()
            else runCatching {
                json.decodeFromString(ListSerializer(Meditation.serializer()), raw)
            }.getOrDefault(emptyList())
        }

    val favorites: Flow<Set<String>> =
        context.dataStore.data.map { prefs ->
            val raw = prefs[KEY_FAVS].orEmpty()
            if (raw.isBlank()) emptySet()
            else runCatching {
                json.decodeFromString(ListSerializer(String.serializer()), raw).toSet()
            }.getOrDefault(emptySet())
        }

    val lastPlayedId: Flow<String?> =
        context.dataStore.data.map { it[KEY_LAST] }

    suspend fun setLastPlayed(id: String) {
        context.dataStore.edit { it[KEY_LAST] = id }
    }

    suspend fun toggleFavorite(id: String) {
        val current = favorites.first().toMutableSet()
        if (current.contains(id)) current.remove(id) else current.add(id)
        val raw = json.encodeToString(ListSerializer(String.serializer()), current.toList())
        context.dataStore.edit { it[KEY_FAVS] = raw }
    }

    /** Copia um áudio escolhido pelo usuário para o armazenamento interno. */
    suspend fun importAudio(uri: Uri, title: String, subtitle: String = "Minha meditação"): Meditation {
        val dir = File(context.filesDir, "custom_audio").apply { mkdirs() }
        val displayName = runCatching {
            context.contentResolver.query(uri, null, null, null, null)?.use { c ->
                val idx = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (c.moveToFirst() && idx >= 0) c.getString(idx) else null
            }
        }.getOrNull()
        val ext = displayName?.substringAfterLast(".", "m4a")?.lowercase()
            ?.takeIf { it in setOf("mp3", "m4a", "wav", "ogg", "opus", "mp4", "aac", "flac") }
            ?: "m4a"

        val dest = File(dir, "${UUID.randomUUID()}.$ext")
        context.contentResolver.openInputStream(uri)?.use { input ->
            dest.outputStream().use { output -> input.copyTo(output) }
        } ?: error("Não foi possível ler o arquivo")

        val meditation = Meditation(
            id = "custom-${UUID.randomUUID()}",
            title = title.ifBlank { dest.nameWithoutExtension },
            subtitle = subtitle,
            description = "Adicionada por você.",
            assetPath = null,
            filePath = dest.absolutePath,
            isCustom = true,
            gradientIndex = (0..5).random()
        )
        val updated = customAudios.first() + meditation
        val raw = json.encodeToString(ListSerializer(Meditation.serializer()), updated)
        context.dataStore.edit { it[KEY_CUSTOM] = raw }
        return meditation
    }

    suspend fun deleteCustom(meditation: Meditation): Boolean {
        if (!meditation.isCustom) return false
        meditation.filePath?.let { runCatching { File(it).delete() } }
        val updated = customAudios.first().filterNot { it.id == meditation.id }
        val raw = json.encodeToString(ListSerializer(Meditation.serializer()), updated)
        context.dataStore.edit { it[KEY_CUSTOM] = raw }
        return true
    }
}
