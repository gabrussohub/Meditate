package com.sonia.meditacao.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sonia.meditacao.data.Meditation
import com.sonia.meditacao.player.PlayerUiState
import com.sonia.meditacao.ui.components.MeditationCover
import com.sonia.meditacao.ui.components.formatMs
import com.sonia.meditacao.ui.theme.Gold
import com.sonia.meditacao.ui.theme.TextSoft

@Composable
fun MiniPlayer(
    state: PlayerUiState,
    onToggle: () -> Unit,
    onOpen: () -> Unit
) {
    val cur = state.current ?: return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF2A2748))
            .clickable(onClick = onOpen)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MeditationCover(cur.gradientIndex, modifier = Modifier.size(48.dp), iconSize = 20)
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(cur.title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                maxLines = 1)
            // progresso fino
            val frac = if (state.durationMs > 0) state.positionMs.toFloat() / state.durationMs else 0f
            Box(
                modifier = Modifier.fillMaxWidth().height(3.dp)
                    .clip(RoundedCornerShape(50)).background(Color.White.copy(0.15f))
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(frac.coerceIn(0f, 1f)).height(3.dp)
                        .background(Gold)
                )
            }
        }
        IconButton(onClick = onToggle) {
            Icon(
                if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = "Play/Pause", tint = Gold, modifier = Modifier.size(32.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSheet(
    state: PlayerUiState,
    isFav: Boolean,
    onDismiss: () -> Unit,
    onToggle: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onFwd: () -> Unit,
    onBack: () -> Unit,
    onSeek: (Long) -> Unit,
    onSpeed: (Float) -> Unit,
    onToggleFav: () -> Unit,
    onSleep: (Int) -> Unit,
    onCancelSleep: () -> Unit
) {
    val cur: Meditation = state.current ?: return
    val sheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showTimer by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheet,
        containerColor = Color(0xFF1D1C38)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 26.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MeditationCover(cur.gradientIndex, modifier = Modifier.size(230.dp), iconSize = 80)
            Spacer(Modifier.height(18.dp))
            Text(cur.title, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text(cur.subtitle.ifBlank { "Sônia Meditação" }, color = TextSoft, fontSize = 15.sp)
            Spacer(Modifier.height(6.dp))

            Slider(
                value = state.positionMs.toFloat(),
                onValueChange = { onSeek(it.toLong()) },
                valueRange = 0f..(state.durationMs.coerceAtLeast(1L).toFloat()),
                modifier = Modifier.fillMaxWidth()
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatMs(state.positionMs), color = TextSoft, fontSize = 12.sp)
                Text(formatMs(state.durationMs), color = TextSoft, fontSize = 12.sp)
            }
            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onPrev) {
                    Icon(Icons.Filled.SkipPrevious, null, tint = Color.White, modifier = Modifier.size(36.dp))
                }
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.Replay10, null, tint = Color.White, modifier = Modifier.size(34.dp))
                }
                IconButton(
                    onClick = onToggle,
                    modifier = Modifier.size(74.dp).clip(CircleShape).background(Gold)
                ) {
                    Icon(
                        if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        null, tint = Color(0xFF221C10), modifier = Modifier.size(40.dp)
                    )
                }
                IconButton(onClick = onFwd) {
                    Icon(Icons.Filled.Forward10, null, tint = Color.White, modifier = Modifier.size(34.dp))
                }
                IconButton(onClick = onNext) {
                    Icon(Icons.Filled.SkipNext, null, tint = Color.White, modifier = Modifier.size(36.dp))
                }
            }
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(0.75f, 1f, 1.25f, 1.5f).forEach { s ->
                    AssistChip(
                        onClick = { onSpeed(s) },
                        label = { Text(if (s == 1f) "1x" else "${s}x") }
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onToggleFav) {
                    Icon(if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, null,
                        tint = if (isFav) Color(0xFFE88AA0) else TextSoft)
                }
                IconButton(onClick = { showTimer = !showTimer }) {
                    Icon(Icons.Filled.Timer, null,
                        tint = if (state.sleepMinutesLeft != null) Gold else TextSoft)
                }
                if (state.sleepMinutesLeft != null)
                    Text("⏱ desliga em ${state.sleepMinutesLeft} min (toque ⏱ p/ cancelar)",
                        color = Gold, fontSize = 12.sp,
                        modifier = Modifier.clickable { onCancelSleep() })
                else
                    Text("velocidade • timer • favoritar", color = TextSoft, fontSize = 12.sp)
            }

            if (showTimer && state.sleepMinutesLeft == null) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(5, 10, 15, 30, 60).forEach { m ->
                        AssistChip(onClick = { onSleep(m); showTimer = false },
                            label = { Text("${m} min") })
                    }
                }
            }
            if (!cur.description.isBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(cur.description, color = TextSoft, fontSize = 13.sp)
            }
            Spacer(Modifier.height(36.dp))
        }
    }
}
