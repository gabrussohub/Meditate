package com.sonia.meditacao.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val BgDeep = Color(0xFF121127)
val BgCard = Color(0xFF1D1C38)
val Gold = Color(0xFFD8C39D)
val Lilac = Color(0xFFB8A9E0)
val TextMain = Color(0xFFF5F1E8)
val TextSoft = Color(0xFFB9B4C9)

private val Scheme = darkColorScheme(
    primary = Gold,
    onPrimary = Color(0xFF221C10),
    secondary = Lilac,
    background = BgDeep,
    surface = BgCard,
    onBackground = TextMain,
    onSurface = TextMain
)

@Composable
fun SoniaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme, content = content)
}
