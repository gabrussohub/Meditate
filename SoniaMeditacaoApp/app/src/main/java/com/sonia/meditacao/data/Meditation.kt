package com.sonia.meditacao.data

import kotlinx.serialization.Serializable

/**
 * Modelo único de meditação.
 *
 * COMO ADICIONAR NOVOS ÁUDIOS (2 formas):
 *
 * 1) FORMA FÁCIL (dentro do app, sem rebuild):
 *    Toque em "+" na tela inicial -> escolha o arquivo mp3/m4a -> dê um título.
 *    O app copia para o armazenamento interno e salva em CustomAudioStore.
 *
 * 2) FORMA DEV (bundled no APK):
 *    a) Copie o .mp3/.m4a para app/src/main/assets/audio/
 *    b) Adicione UMA linha em MeditationCatalog.bundled (abaixo)
 *    c) Rebuild o app.
 */
@Serializable
data class Meditation(
    val id: String,          // único, ex: "med-11"
    val title: String,       // ex: "Gratidão da Manhã"
    val subtitle: String = "", // ex: "10 min • Respiração"
    val description: String = "",
    val durationLabel: String = "", // ex: "~12 min" (opcional, exibição)
    val assetPath: String? = null,  // ex: "audio/AUD-20250415-WA0014.mp3" (bundled)
    val filePath: String? = null,   // caminho absoluto (áudios importados pelo app)
    val isCustom: Boolean = false,
    val gradientIndex: Int = 0
)
