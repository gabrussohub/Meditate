package com.sonia.meditacao.data

/**
 * ★★★ EDITE AQUI PARA ADICIONAR NOVOS ÁUDIOS FIXOS ★★★
 *
 * Passo a passo:
 * 1. Coloque o arquivo em: app/src/main/assets/audio/NovoAudio.mp3
 * 2. Copie um dos blocos abaixo, mude id/title/assetPath.
 * 3. gradientIndex pode ser 0..5 (muda a cor da capa).
 *
 * Os 10 áudios iniciais já estão mapeados abaixo.
 */
object MeditationCatalog {

    val bundled: List<Meditation> = listOf(
        Meditation(
            id = "med-01",
            title = "Acalmar a Mente",
            subtitle = "Respiração • Recomeço",
            description = "Uma meditação suave para desacelerar e voltar ao presente.",
            durationLabel = "",
            assetPath = "audio/AUD-20250415-WA0014.mp3",
            gradientIndex = 0
        ),
        Meditation(
            id = "med-02",
            title = "Noite Tranquila",
            subtitle = "Relaxamento profundo",
            description = "Solte o dia e prepare o corpo para um sono leve.",
            durationLabel = "",
            assetPath = "audio/AUD-20250424-WA0024.m4a",
            gradientIndex = 1
        ),
        Meditation(
            id = "med-03",
            title = "Gratidão da Manhã",
            subtitle = "Comece o dia leve",
            description = "Desperte com gratidão e intenção positiva.",
            durationLabel = "",
            assetPath = "audio/AUD-20250429-WA0022.mp3",
            gradientIndex = 2
        ),
        Meditation(
            id = "med-04",
            title = "Paz Interior",
            subtitle = "Coração sereno",
            description = "Encontre um refúgio de calma dentro de você.",
            durationLabel = "",
            assetPath = "audio/AUD-20250708-WA0060.mp3",
            gradientIndex = 3
        ),
        Meditation(
            id = "med-05",
            title = "Respirar e Sentir",
            subtitle = "Atenção plena",
            description = "Observe a respiração e acolha cada sensação.",
            durationLabel = "",
            assetPath = "audio/AUD-20250717-WA0006.mp3",
            gradientIndex = 4
        ),
        Meditation(
            id = "med-06",
            title = "Leveza",
            subtitle = "Soltar tensões",
            description = "Alivie ombros, mente e coração.",
            durationLabel = "",
            assetPath = "audio/AUD-20250826-WA0022.mp3",
            gradientIndex = 5
        ),
        Meditation(
            id = "med-07",
            title = "Presença",
            subtitle = "Aqui e agora",
            description = "Uma pausa curta para voltar ao momento presente.",
            durationLabel = "",
            assetPath = "audio/AUD-20251007-WA0045.mp3",
            gradientIndex = 0
        ),
        Meditation(
            id = "med-08",
            title = "Descanso Profundo",
            subtitle = "Entrega e sono",
            description = "Ideal para ouvir deitada, antes de dormir.",
            durationLabel = "",
            assetPath = "audio/AUD-20251028-WA0052.m4a",
            gradientIndex = 1
        ),
        Meditation(
            id = "med-09",
            title = "Coração Aberto",
            subtitle = "Amor e compaixão",
            description = "Cultive carinho por você e pelos outros.",
            durationLabel = "",
            assetPath = "audio/AUD-20260303-WA0090.m4a",
            gradientIndex = 2
        ),
        Meditation(
            id = "med-10",
            title = "Clareza",
            subtitle = "Mente leve",
            description = "Limpe o ruído mental e reencontre o foco suave.",
            durationLabel = "",
            assetPath = "audio/AUD-20260602-WA0012.mp3",
            gradientIndex = 3
        )

        // ── EXEMPLO: para adicionar a meditação 11, descomente e ajuste ──
        // Meditation(
        //     id = "med-11",
        //     title = "Nova Meditação",
        //     subtitle = "Tema • 10 min",
        //     description = "Descrição curta e acolhedora.",
        //     assetPath = "audio/NovoAudio.mp3",
        //     gradientIndex = 4
        // ),
    )
}
