// Sônia Meditação — web app (GitHub Pages + PWA)
// Como adicionar áudios novos (para todos): suba em docs/audio/ + 1 linha em meditations.json.
// Adição local (só neste aparelho): botão ＋ (salva em localStorage + IndexedDB).

const $ = (id) => document.getElementById(id);
const audio = $("audio");
const COVERS = ["☾", "❀", "✦", "♡", "◍", "〜"];

const store = {
  get lastId() { return localStorage.getItem("sonia:last"); },
  set lastId(v) { v ? localStorage.setItem("sonia:last", v) : localStorage.removeItem("sonia:last"); },
  get custom() { try { return JSON.parse(localStorage.getItem("sonia:custom") || "[]"); } catch { return []; } },
  set custom(v) { localStorage.setItem("sonia:custom", JSON.stringify(v)); },
};

// IndexedDB simples para guardar os blobs dos áudios adicionados no aparelho
const idb = {
  db: null,
  open() {
    return new Promise((res, rej) => {
      const r = indexedDB.open("sonia-med", 1);
      r.onupgradeneeded = () => r.result.createObjectStore("audios");
      r.onsuccess = () => { idb.db = r.result; res(); };
      r.onerror = () => rej(r.error);
    });
  },
  put(id, blob) {
    return new Promise((res, rej) => {
      const tx = idb.db.transaction("audios", "readwrite").objectStore("audios").put(blob, id);
      tx.onsuccess = res; tx.onerror = () => rej(tx.error);
    });
  },
  get(id) {
    return new Promise((res, rej) => {
      const tx = idb.db.transaction("audios").objectStore("audios").get(id);
      tx.onsuccess = () => res(tx.result); tx.onerror = () => rej(tx.error);
    });
  },
  del(id) {
    return new Promise((res) => {
      try {
        const tx = idb.db.transaction("audios", "readwrite").objectStore("audios").delete(id);
        tx.onsuccess = res; tx.onerror = res;
      } catch { res(); }
    });
  },
};

let BUNDLED = [], CUSTOM = store.custom, ALL = [];
let current = null, speed = 1, sleepTimer = null, sleepLeft = null;
const objUrls = {};

const fmt = (s) => {
  if (!isFinite(s) || s < 0) return "0:00";
  s = Math.floor(s);
  return `${Math.floor(s / 60)}:${String(s % 60).padStart(2, "0")}`;
};

async function init() {
  try { await idb.open(); } catch {}
  try {
    const r = await fetch("meditations.json", { cache: "no-store" });
    BUNDLED = await r.json();
  } catch { BUNDLED = []; }
  // resolve URLs dos customs
  for (const m of CUSTOM) {
    try {
      const blob = await idb.get(m.id);
      if (blob) { objUrls[m.id] = URL.createObjectURL(blob); m._url = objUrls[m.id]; }
    } catch {}
  }
  ALL = [...BUNDLED, ...CUSTOM];
  buildSpeeds(); buildTimerOpts(); bindUI();
  render();
  if (store.lastId) {
    const last = ALL.find((m) => m.id === store.lastId);
    if (last) load(last, false);
  }
  if ("mediaSession" in navigator) {
    try {
      navigator.mediaSession.setActionHandler("previoustrack", () => step(-1));
      navigator.mediaSession.setActionHandler("nexttrack", () => step(1));
      navigator.mediaSession.setActionHandler("seekbackward", () => { audio.currentTime = Math.max(0, audio.currentTime - 10); });
      navigator.mediaSession.setActionHandler("seekforward", () => { audio.currentTime += 10; });
    } catch {}
  }
}

function urlOf(m) { return m._url || m.file; }

function visible() {
  return ALL;
}

function render() {
  const list = visible();
  $("count").textContent = `${list.length} áudio(s)`;
  // destaque do dia
  const feat = $("featured");
  if (ALL.length) {
    const pick = ALL[Math.floor(Date.now() / 86400000) % ALL.length];
    feat.classList.remove("hidden");
    feat.innerHTML = `<small>✨ DESTAQUE DE HOJE</small><h2>${esc(pick.title)}</h2><p>${esc(pick.subtitle || "")}</p><span class="cta">▶ Ouvir agora</span>`;
    feat.onclick = () => { load(pick, true); openSheet(); };
  } else feat.classList.add("hidden");

  // aviso se faltar arquivo
  const missing = BUNDLED.length === 0;
  $("missingHint").classList.toggle("hidden", !missing);
  if (missing) $("missingHint").textContent = "⚠️ Nenhum áudio encontrado. Se você é a Sônia: suba os mp3/m4a em docs/audio/ (veja README). Ou toque em ＋ para adicionar do aparelho.";

  const el = $("list");
  el.innerHTML = "";
  if (!list.length) {
    el.innerHTML = `<p class="sub">Nada por aqui. Toque em ＋ para adicionar sua primeira meditação. 🌙</p>`;
    return;
  }
  for (const m of list) {
    const isCur = current && current.id === m.id;
    const card = document.createElement("div");
    card.className = "card" + (isCur ? " playing" : "");
    card.innerHTML = `
      <div class="cover md g${(m.gradient || 0) % 6}">${COVERS[(m.gradient || 0) % 6]}</div>
      <div class="meta">
        <strong>${esc(m.title)}</strong>
        <span>${esc(m.subtitle || "Meditação guiada")}</span><br>
        ${m.isCustom ? `<span class="mine">• adicionada por você</span>` : ``}
        ${isCur && !audio.paused ? `<span class="now">● tocando agora</span>` : ``}
      </div>
      ${m.isCustom ? `<button class="iconbtn" title="Apagar">🗑</button>` : `<button class="iconbtn" title="Ouvir">▶</button>`}`;
    const actBtn = card.querySelector(".iconbtn");
    card.onclick = () => load(m, true);
    actBtn.onclick = (e) => {
      e.stopPropagation();
      if (m.isCustom && actBtn.textContent === "🗑") delCustom(m);
      else load(m, true);
    };
    el.appendChild(card);
  }
}

function esc(s) { return String(s ?? "").replace(/[&<>"']/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c])); }

function load(m, autoplay) {
  current = m;
  store.lastId = m.id;
  const url = urlOf(m);
  if (audio.dataset.id !== m.id) { audio.src = url; audio.dataset.id = m.id; }
  audio.playbackRate = speed;
  $("mini").classList.remove("hidden");
  $("miniTitle").textContent = m.title;
  $("miniCover").className = `cover xs g${(m.gradient || 0) % 6}`;
  $("miniCover").textContent = COVERS[(m.gradient || 0) % 6];
  $("bigCover").className = `cover xl g${(m.gradient || 0) % 6}`;
  $("bigCover").textContent = COVERS[(m.gradient || 0) % 6];
  $("pTitle").textContent = m.title;
  $("pSub").textContent = m.subtitle || "Sônia Meditação";
  $("pDesc").textContent = m.description || "";
  if ("mediaSession" in navigator) {
    try { navigator.mediaSession.metadata = new MediaMetadata({ title: m.title, artist: m.subtitle || "Sônia Meditação", album: "Sônia Meditação" }); } catch {}
  }
  audio.onerror = () => {
    $("missingHint").classList.remove("hidden");
    $("missingHint").textContent = `⚠️ Não achei o arquivo "${m.file || m.title}". Confira se ele foi enviado para docs/audio/ no GitHub.`;
  };
  if (autoplay) audio.play().catch(() => {});
  render();
}

function step(dir) {
  const list = visible().length ? visible() : ALL;
  if (!current || !list.length) return;
  const i = list.findIndex((m) => m.id === current.id);
  const n = list[(i + dir + list.length) % list.length];
  if (n) load(n, true);
}

// ---- player UI ----
function buildSpeeds() {
  const box = $("speeds"); box.innerHTML = "";
  [0.75, 1, 1.25, 1.5].forEach((s) => {
    const b = document.createElement("button");
    b.textContent = s === 1 ? "1x" : s + "x";
    if (s === 1) b.classList.add("on");
    b.onclick = () => {
      speed = s; audio.playbackRate = s;
      box.querySelectorAll("button").forEach((x) => x.classList.remove("on"));
      b.classList.add("on");
    };
    box.appendChild(b);
  });
}
function buildTimerOpts() {
  const box = $("timerOpts"); box.innerHTML = "";
  [5, 10, 15, 30, 60].forEach((m) => {
    const b = document.createElement("button");
    b.className = "ghost"; b.textContent = m + " min";
    b.onclick = () => startSleep(m);
    box.appendChild(b);
  });
}
function startSleep(min) {
  clearInterval(sleepTimer); sleepLeft = min; updSleep();
  $("timerOpts").classList.add("hidden");
  sleepTimer = setInterval(() => {
    sleepLeft--;
    if (sleepLeft <= 0) { clearInterval(sleepTimer); sleepTimer = null; sleepLeft = null; audio.pause(); }
    updSleep();
  }, 60000);
}
function updSleep() {
  const l = $("sleepLabel");
  if (sleepLeft) { l.classList.remove("hidden"); l.textContent = `⏱ desliga em ${sleepLeft} min (toque para cancelar)`; }
  else l.classList.add("hidden");
}

function openSheet() { if (current) $("sheet").classList.remove("hidden"); }
function bindUI() {
  $("miniToggle").onclick = () => toggle();
  $("miniOpen").onclick = openSheet;
  $("sheetClose").onclick = () => $("sheet").classList.add("hidden");
  $("btnToggle").onclick = () => toggle();
  $("btnNext").onclick = () => step(1);
  $("btnPrev").onclick = () => step(-1);
  $("btnFwd").onclick = () => { audio.currentTime += 10; };
  $("btnBack").onclick = () => { audio.currentTime = Math.max(0, audio.currentTime - 10); };
  $("btnTimer").onclick = () => $("timerOpts").classList.toggle("hidden");
  $("sleepLabel").onclick = () => { clearInterval(sleepTimer); sleepTimer = null; sleepLeft = null; updSleep(); };
  $("seek").oninput = (e) => { if (audio.duration) audio.currentTime = (e.target.value / 1000) * audio.duration; };

  audio.ontimeupdate = () => {
    if (audio.duration) {
      $("seek").value = Math.floor((audio.currentTime / audio.duration) * 1000);
      $("miniProg").style.width = ((audio.currentTime / audio.duration) * 100) + "%";
    }
    $("tCur").textContent = fmt(audio.currentTime);
    $("tDur").textContent = fmt(audio.duration);
  };
  audio.onplay = audio.onpause = () => {
    const p = !audio.paused;
    $("btnToggle").textContent = p ? "⏸" : "▶";
    $("miniToggle").textContent = p ? "⏸" : "▶";
    render();
  };
  audio.onended = () => step(1);

  // adicionar
  const openAdd = () => { $("addModal").classList.remove("hidden"); };
  $("btnAddTop").onclick = openAdd;
  $("addCancel").onclick = () => $("addModal").classList.add("hidden");
  $("addSave").onclick = async () => {
    const f = $("addFile").files[0];
    const title = $("addTitle").value.trim() || (f ? f.name.replace(/\.[^.]+$/, "") : "Nova meditação");
    if (!f) { alert("Escolha um arquivo de áudio primeiro 🎧"); return; }
    const m = { id: "custom-" + Date.now(), title, subtitle: "Minha meditação", description: "Adicionada por você.", gradient: Math.floor(Math.random() * 6), isCustom: true };
    try { await idb.put(m.id, f); } catch { alert("Não consegui salvar neste navegador."); return; }
    m._url = URL.createObjectURL(f);
    CUSTOM.push(m); store.custom = CUSTOM; ALL = [...BUNDLED, ...CUSTOM];
    $("addFile").value = ""; $("addTitle").value = "";
    $("addModal").classList.add("hidden");
    render(); load(m, true); openSheet();
  };
}

async function delCustom(m) {
  if (!confirm(`Apagar "${m.title}"?`)) return;
  if (current && current.id === m.id) audio.pause();
  await idb.del(m.id);
  CUSTOM = CUSTOM.filter((x) => x.id !== m.id);
  store.custom = CUSTOM; ALL = [...BUNDLED, ...CUSTOM];
  if (current && current.id === m.id) { current = null; $("mini").classList.add("hidden"); $("sheet").classList.add("hidden"); }
  render();
}

function toggle() {
  if (!current) {
    const v = visible()[0] || ALL[0];
    if (v) { load(v, true); openSheet(); }
    return;
  }
  audio.paused ? audio.play().catch(() => {}) : audio.pause();
}

document.addEventListener("DOMContentLoaded", init);
