# Sonia – Sônia Meditação 🧘‍♀️

App Android moderno e elegante para ouvir suas meditações quando quiser.

## O que o app faz
- 🏠 Tela inicial acolhedora ("Olá, Sônia ✨") com busca
- ✨ Destaque do dia (escolhe uma meditação diferente por dia)
- ▶ Player completo: play/pause, voltar/avançar 10s, próxima/anterior, barra de progresso
- ⏩ Velocidade 0.75x / 1x / 1.25x / 1.5x
- ⏱ Timer de sono (5/10/15/30/60 min — pausa sozinho, ideal p/ dormir ouvindo)
- ♥ Favoritas + filtro "Favoritas"
- ➕ **Adicionar novos áudios depois, sem programar** (botão dourado "+")
- 🔔 Toca em background, com controles na notificação e na tela de bloqueio
- 🎨 Design calmo: fundo azul-noite, dourado suave, capas em degradê (sem precisar de fotos)

## Como abrir e gerar o APK

1. Instale o **Android Studio** (versão recente, ex: Ladybug+).
2. `File > Open` → abra a pasta **`SoniaMeditacaoApp`**.
3. Aguarde o Gradle sincronizar (primeira vez baixa dependências).
4. **Coloque seus 10 áudios no app:**
   - Copie os arquivos desta pasta (`AUD-*.mp3 / .m4a`) para:
     ```
     SoniaMeditacaoApp/app/src/main/assets/audio/
     ```
   - Os nomes precisam ser **iguais** aos do `MeditationCatalog.kt`:
     `AUD-20250415-WA0014.mp3`, `AUD-20250424-WA0024.m4a`, etc.
     (Ou edite o `MeditationCatalog.kt` para o nome que você usar.)
5. Conecte o celular com depuração USB **ou** use `Run ▶` no emulador.
6. Para gerar o APK instalável: `Build > Build App Bundles/APKs > Build APK(s)` →
   o arquivo sai em `app/build/outputs/apk/debug/app-debug.apk`. Envie para o celular e instale.

> Os arquivos de áudio somam ~77 MB, por isso não foram duplicados automaticamente —
> basta copiar uma vez como acima.

## Como adicionar NOVOS áudios depois (2 jeitos)

### Jeito 1 — dentro do app (mais fácil, sem rebuild) ✅ recomendado
1. Abra o app → toque no botão **dourado "+"**.
2. Escolha o mp3/m4a no celular.
3. Digite um título (ex: "Gratidão da noite") → **Salvar**.
4. Pronto! Entra na lista com selo "adicionada por você". Pode favoritar, ouvir e até apagar (🗑).

### Jeito 2 — fixo no código (vira parte do APK)
1. Copie o novo arquivo para `app/src/main/assets/audio/NovoAudio.mp3`.
2. Abra `app/src/main/java/com/sonia/meditacao/data/MeditationCatalog.kt`.
3. Duplique um bloco `Meditation(...)`, mude `id`, `title` e `assetPath`.
4. Rebuild. Exemplo comentado no fim do arquivo (`med-11`).

## Trocar os títulos
Edite só o campo `title` / `subtitle` de cada item em `MeditationCatalog.kt` — ex:
```kotlin
Meditation(id = "med-01", title = "Minha Meditação da Manhã", ...)
```

## Estrutura do projeto
```
SoniaMeditacaoApp/
  settings.gradle.kts / build.gradle.kts / gradle.properties
  app/build.gradle.kts
  app/src/main/AndroidManifest.xml
  app/src/main/assets/audio/        ← coloque os mp3/m4a aqui
  app/src/main/java/com/sonia/meditacao/
    MainActivity.kt                 ← tela + diálogo de importação
    data/Meditation.kt              ← modelo
    data/MeditationCatalog.kt       ← ★ EDITE AQUI p/ novos áudios fixos
    data/CustomAudioStore.kt        ← importação + favoritas (DataStore)
    player/MeditationService.kt     ← background + notificação
    player/PlayerViewModel.kt       ← lógica do player (Media3)
    ui/theme/Theme.kt               ← cores elegantes
    ui/components/Covers.kt         ← capas em degradê
    ui/home/HomeScreen.kt           ← lista inicial
    ui/player/PlayerSheet.kt        ← mini-player + player expandido
```

## Tecnologias
Kotlin • Jetpack Compose (Material3) • Media3 ExoPlayer + MediaSession • DataStore • Kotlinx Serialization

Feito com carinho para a Sônia ouvir suas meditações com um toque. 🌙
