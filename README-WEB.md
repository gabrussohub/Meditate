# Sônia Meditação — Web App (GitHub Pages) 🌙

Sem Android Studio! É um site que vira "app" no celular (PWA instalável).

## 1) Testar agora no PC (2 min)
1. Duplo-clique em `copiar-audios-web.bat` (copia os 10 áudios para `docs/audio/`).
2. Na pasta `docs/`, rode um servidor local — escolha um:
   - Python: `python -m http.server 8000` dentro de `docs/`, abra `http://localhost:8000`
   - VS Code: extensão "Live Server" → abrir `docs/index.html`
   - Ou só abrir `index.html` (funciona, mas o `meditations.json` pode bloquear no `file://` em alguns navegadores — prefira o servidor).

## 2) Publicar no GitHub — passo a passo detalhado 🛰️

> Tempo estimado: 10–15 min na primeira vez. Depois, atualizar é 1 min.
> O que vai acontecer: seus arquivos sobem para o GitHub → o GitHub publica
> a pasta `docs/` como site → você recebe um link `https://SEU-USUARIO.github.io/sonia-meditacao/`.

### PASSO 0 — Preparar a pasta (no seu PC)
1. Abra a pasta `Sônia Meditação` no Explorador de Arquivos.
2. Duplo-clique em `copiar-audios-web.bat` e aguarde "Pronto!".
3. Confira: entre em `docs/audio/` — devem estar lá os **10 arquivos** `AUD-*.mp3/.m4a`.
   - Se faltar algum, o app mostra um aviso amarelo em vez de travar. Mas o ideal é ter os 10.
4. (Opcional, recomendado) Teste local antes de publicar:
   - Dentro da pasta `docs/`, clique na barra de endereço do Explorador, digite `cmd` + Enter.
   - Na janela preta, digite `python -m http.server 8000` + Enter.
   - Abra o Chrome em `http://localhost:8000` — o app deve abrir com as 10 meditações.
   - Feche com `Ctrl+C` quando terminar.

### PASSO 1 — Criar conta no GitHub (se ainda não tem)
1. Acesse https://github.com → **Sign up**.
2. Use seu e-mail, crie senha e nome de usuário (ex: `sonia-meditacao`).
   Anote seu **nome de usuário** — ele vai no link do site.
3. Confirme o e-mail que o GitHub envia.

### PASSO 2 — Criar o repositório (a "caixa" do projeto)
1. Logada no GitHub, clique no **＋** no canto superior direito → **New repository**.
2. Preencha:
   - **Repository name:** `sonia-meditacao` (ou outro, sem espaços nem acentos)
   - **Public** ✅ (importante: GitHub Pages grátis exige repositório público)
   - **NÃO** marque "Add a README file" (sua pasta já tem READMEs)
3. Clique **Create repository**.
4. O GitHub mostra uma página com comandos. **Deixe essa aba aberta** — você usa o endereço dela no passo seguinte. Ele é tipo:
   `https://github.com/SEU-USUARIO/sonia-meditacao.git`

### PASSO 3 — Enviar os arquivos (3 opções, da mais fácil à avançada)

**OPÇÃO A — GitHub Desktop (recomendada p/ quem não usa terminal) 🖥️**
1. Instale https://desktop.github.com (é grátis, da própria GitHub).
2. Abra o GitHub Desktop → **File → Add local repository** → escolha a pasta `Sônia Meditação`.
   - Se pedir, confirme "create a repository here".
3. **IMPORTANTE:** confira que a pasta `docs/audio/` com os 10 áudios está incluída
   (são ~77 MB — o primeiro envio demora alguns minutos, é normal).
4. Embaixo à esquerda escreva o resumo: `Primeira versão do app` → **Commit to main**.
5. Clique **Publish repository** → desmarque "Keep this code private" (precisa ser público) → **Publish**.
6. Pronto — pule para o PASSO 4.

**OPÇÃO B — Pelo site do GitHub (sem instalar nada) 🌐**
> ⚠️ Limite: o upload pelo site aceita bem arquivos de até ~25 MB cada (os seus têm no máx. ~13 MB, então passa). Mas são 77 MB no total — pode demorar e às vezes falha. Se falhar, use a Opção A.
1. Na página do repositório criado → **Add file → Upload files**.
2. Arraste **o conteúdo** da pasta `Sônia Meditação`: as pastas `docs/`, `.github/` e os arquivos `.bat`/`README`.
   - ⚠️ Arraste em 2 levas para não travar: 1ª) `docs/audio/` (os 10 áudios), 2ª) o resto.
3. Em cada leva, escreva a mensagem `Adiciona audios` / `Adiciona app` e clique **Commit changes**.
4. Pronto — pule para o PASSO 4.

**OPÇÃO C — Terminal/git (rápida, p/ quem já usa) ⌨️**
```bash
cd "Sônia Meditação"
git init -b main
git add .
git commit -m "Sonia Meditacao web app"
git remote add origin https://github.com/SEU-USUARIO/sonia-meditacao.git
git push -u origin main
```

### PASSO 4 — Ligar o GitHub Pages (1 clique) ⚡
Você tem **2 caminhos** (escolha UM):

**CAMINHO 1 — Automático via Actions (recomendado, já configurado)**
1. No repositório → aba **Settings** (última, no topo) → menu lateral **Pages**.
2. Em **Build and deployment → Source**, selecione **GitHub Actions**.
3. Pronto. O arquivo `.github/workflows/deploy-pages.yml` (já está na sua pasta) publica a pasta `docs/` sozinho a cada `push`.
4. Acompanhe: aba **Actions** → clique no workflow "Deploy Pages" → aguarde o ✅ verde (~1–2 min).

**CAMINHO 2 — Clássico por branch (mais simples de entender)**
1. Em **Settings → Pages → Source**, selecione **Deploy from a branch**.
2. Em **Branch**, selecione `main` + pasta `/docs` → **Save**.
3. Aguarde ~1–2 min.

> Os dois funcionam. Se um confundir, use o outro. Não ative os dois ao mesmo tempo.

### PASSO 5 — Pegar seu link e testar 🔗
1. Volte em **Settings → Pages** — no topo aparece:
   `Your site is live at https://SEU-USUARIO.github.io/sonia-meditacao/`
2. Abra o link no PC **e** no celular. Devem aparecer as 10 meditações.
3. Teste: buscar, favoritar ♥, dar play, bloquear a tela (o áudio continua + mostra controle na tela de bloqueio).

### PASSO 6 — Instalar no Android (vira "app") 📲
1. No **Chrome do celular**, abra seu link.
2. Toque no menu **⋮** (três pontos) → **Adicionar à tela inicial** ou **Instalar app**.
3. Confirme. Nasce o ícone 🌙 na home — abre em tela cheia, sem barra do navegador.

### PASSO 7 — Atualizar depois (novo áudio/título) 🔄
- **Via GitHub Desktop:** coloque o mp3 em `docs/audio/` + edite 1 linha em `docs/meditations.json` → Commit → **Push origin**. O site atualiza em 1–2 min.
- **Via site:** no repositório → navegue até `docs/audio/` → **Add file → Upload files** (o mp3) → depois edite `docs/meditations.json` no lápis ✏️ → **Commit changes**.
- Formato da linha nova:
  ```json
  { "id": "med-11", "title": "Nova Meditação", "subtitle": "Tema • 10 min",
    "description": "Descrição curta.", "file": "audio/NovaMeditacao.mp3", "gradient": 4 }
  ```

### ❓ Se der errado (diagnóstico rápido)
| Sintoma | Causa provável | O que fazer |
|---|---|---|
| Página 404 | Pages ainda publicando ou Source errado | Aguarde 2 min, F5; confira Settings → Pages (Source + branch `/docs` ou Actions com ✅ verde) |
| Abre o app mas sem áudio / aviso amarelo | `docs/audio/` vazio no GitHub | Confira se os 10 arquivos subiram (aba Code → docs/audio). Refaça o PASSO 3 |
| 404 num áudio específico | Nome do arquivo ≠ `meditations.json` | Nome no `file:` deve ser idêntico (maiúsculas, `.mp3` vs `.m4a`) |
| Push trava / erro de tamanho | Arquivo > 100 MB ou internet caiu | Nenhum áudio seu passa de ~13 MB; tente GitHub Desktop em vez do site |
| Repositório privado sem Pages | Pages grátis = repo público | Settings → General → Danger Zone → Change visibility → Public |
| Site antigo aparecendo | Cache do navegador | Ctrl+Shift+R (PC) ou limpar cache; no celular feche e reabra o app instalado |

## 3) Instalar no Android (vira "app")
1. Abra o link no Chrome do celular.
2. Menu ⋮ → **Adicionar à tela inicial** / **Instalar app**.
3. Pronto: ícone 🌙, abre em tela cheia, continua tocando com a tela bloqueada (Media Session) e funciona offline (áudios já ouvidos ficam em cache).

## 4) Adicionar novos áudios depois
**Para todos (no GitHub):**
1. Suba o novo mp3/m4a em `docs/audio/` (ex: `NovaMeditacao.mp3`).
2. Edite `docs/meditations.json`, copie uma linha e ajuste:
   ```json
   { "id": "med-11", "title": "Nova Meditação", "subtitle": "Tema • 10 min",
     "description": "Descrição curta.", "file": "audio/NovaMeditacao.mp3", "gradient": 4 }
   ```
   `gradient` é 0–5 (cor da capinha). Commit → o site atualiza sozinho em 1–2 min.

**Só neste aparelho (sem GitHub):**
- Botão dourado **＋** no app → escolhe o áudio → título → Salvar. Fica guardado no aparelho (IndexedDB) e pode apagar depois. Ideal para testar áudios novos antes de publicar.

## Estrutura
```
docs/
  index.html            ← tela
  styles.css            ← visual elegante
  app.js                ← player, busca, favoritas, timer, offline
  meditations.json      ← ★ EDITE AQUI p/ novos áudios (é só 1 linha por áudio)
  audio/                ← os mp3/m4a (via copiar-audios-web.bat)
  manifest.webmanifest  ← PWA instalável
  service-worker.js     ← offline + tela de bloqueio
  icon.svg
.github/workflows/deploy-pages.yml ← publica sozinho no Pages
```

## Notas
- Total dos áudios: ~77 MB. GitHub aceita (limite 100 MB/arquivo; o maior tem ~13 MB). Se um dia estourar, me avise que migramos os áudios para outro hospedeiro (ex: Cloudinary/R2) sem mudar o app.
- Títulos: edite `title`/`subtitle` em `meditations.json` à vontade.
- Pastas `SoniaMeditacaoApp/` (versão Android nativa) pode ser apagada se não for usar — ou mantida.
