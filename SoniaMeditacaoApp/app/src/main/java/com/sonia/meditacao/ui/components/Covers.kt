package com.sonia.meditacao.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

val CoverGradients: List<Pair<Color, Color>> = listOf(
    Color(0xFF6D5BD0) to Color(0xFF2A1E63),
    Color(0xFF2E7D8A) to Color(0xFF123A44),
    Color(0xFFC08A4B) to Color(0xFF5A3418),
    Color(0xFF8A4B7C) to Color(0xFF3E1B38),
    Color(0xFF4B8A6D) to Color(0xFF173B2E),
    Color(0xFF4B6AC0) to Color(0xFF1B2A5A),
)

private val CoverIcons: List<ImageVector> = listOf(
    Icons.Filled.Spa, Icons.Filled.WaterDrop, Icons.Filled.WbTwilight,
    Icons.Filled.Air, Icons.Filled.NightsStay, Icons.Filled.Favorite
)

@Composable
fun MeditationCover(index: Int, modifier: Modifier = Modifier, iconSize: Int = 34) {
    val (c1, c2) = CoverGradients[index.mod(CoverGradients.size)]
    val icon = CoverIcons[index.mod(CoverIcons.size)]
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(c1, c2))),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size((iconSize + 26).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(iconSize.dp))
        }
    }
}

fun formatMs(ms: Long): String {
    if (ms <= 0) return "0:00"
    val s = (ms / 1000).toInt()
    return "%d:%02d".format(s / 60, s % 60)
}
