package com.sonia.meditacao.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sonia.meditacao.data.Meditation
import com.sonia.meditacao.ui.components.MeditationCover
import com.sonia.meditacao.ui.theme.BgDeep
import com.sonia.meditacao.ui.theme.Gold
import com.sonia.meditacao.ui.theme.TextSoft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    items: List<Meditation>,
    query: String,
    onQuery: (String) -> Unit,
    favoritesOnly: Boolean,
    onToggleFavOnly: () -> Unit,
    favorites: Set<String>,
    lastPlayedId: String?,
    currentId: String?,
    isPlaying: Boolean,
    onPlay: (Meditation) -> Unit,
    onToggleFav: (String) -> Unit,
    onDelete: (Meditation) -> Unit,
    onAddClick: () -> Unit,
    onOpenPlayer: () -> Unit,
    playerBar: @Composable () -> Unit
) {
    Scaffold(
        containerColor = BgDeep,
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick, containerColor = Gold) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar áudio", tint = Color(0xFF221C10))
            }
        },
        bottomBar = { playerBar() }
    ) { pad ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(pad),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text("Boa hora para respirar 🌙", color = TextSoft, fontSize = 14.sp)
                Text(
                    "Olá, Sônia ✨",
                    color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold
                )
                Text("Suas meditações favoritas, num toque.", color = TextSoft, fontSize = 14.sp)
                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = onQuery,
                    placeholder = { Text("Buscar meditação…") },
                    leadingIcon = { Icon(Icons.Filled.Search, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !favoritesOnly,
                        onClick = { if (favoritesOnly) onToggleFavOnly() },
                        label = { Text("Todas") }
                    )
                    FilterChip(
                        selected = favoritesOnly,
                        onClick = { if (!favoritesOnly) onToggleFavOnly() },
                        label = { Text("♥ Favoritas") }
                    )
                    Spacer(Modifier.weight(1f))
                    Text("${items.size} áudios", color = TextSoft, fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.CenterVertically))
                }

                if (lastPlayedId != null) {
                    Spacer(Modifier.height(6.dp))
                    Text("Toque em ▶ para continuar de onde parou.", color = TextSoft, fontSize = 12.sp)
                }
            }

            // Destaque do dia
            if (items.isNotEmpty() && query.isBlank() && !favoritesOnly) {
                item {
                    val pick = items[(System.currentTimeMillis() / 86_400_000 % items.size).toInt()]
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        modifier = Modifier.fillMaxWidth().clickable { onPlay(pick); onOpenPlayer() }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF6D5BD0), Color(0xFF2A1E63), Color(0xFFC08A4B))
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            Column {
                                Text("✨ DESTAQUE DE HOJE", color = Color.White.copy(0.8f), fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold)
                                Text(pick.title, color = Color.White, fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold)
                                Text(pick.subtitle, color = Color.White.copy(0.8f), fontSize = 14.sp)
                                Spacer(Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(Gold)
                                            .padding(horizontal = 18.dp, vertical = 8.dp)
                                    ) {
                                        Text("▶ Ouvir agora", fontWeight = FontWeight.Bold,
                                            color = Color(0xFF221C10))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            items(items, key = { it.id }) { m ->
                MeditationRow(
                    m = m,
                    isFav = favorites.contains(m.id),
                    isCurrent = currentId == m.id,
                    isPlaying = isPlaying && currentId == m.id,
                    onPlay = { onPlay(m) },
                    onToggleFav = { onToggleFav(m.id) },
                    onDelete = { onDelete(m) }
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "＋ Para adicionar novos áudios, toque no botão dourado abaixo.",
                    color = TextSoft, fontSize = 12.sp
                )
                Spacer(Modifier.height(60.dp))
            }
        }
    }
}

@Composable
private fun MeditationRow(
    m: Meditation,
    isFav: Boolean,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onToggleFav: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) Color(0xFF2A2748) else Color(0xFF1D1C38)
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onPlay)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MeditationCover(m.gradientIndex, modifier = Modifier.size(64.dp), iconSize = 26)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(m.title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text(
                    if (m.subtitle.isBlank()) "Meditação guiada" else m.subtitle,
                    color = TextSoft, fontSize = 13.sp
                )
                if (m.isCustom) Text("• adicionada por você", color = Gold, fontSize = 11.sp)
                if (isCurrent && isPlaying) Text("● tocando agora", color = Gold, fontSize = 11.sp)
            }
            IconButton(onClick = onToggleFav) {
                Icon(
                    if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favoritar",
                    tint = if (isFav) Color(0xFFE88AA0) else TextSoft
                )
            }
            if (m.isCustom) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Apagar", tint = TextSoft)
                }
            } else {
                Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Gold,
                    modifier = Modifier.size(30.dp))
            }
        }
    }
}
