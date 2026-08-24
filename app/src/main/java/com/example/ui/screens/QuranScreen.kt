package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.staticdata.QuranData
import com.example.data.staticdata.QuranSurahItem
import com.example.ui.theme.Emerald700
import com.example.ui.theme.GoldBase
import com.example.ui.theme.GoldDark
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.WorshipViewModel

@Composable
fun QuranScreen(
    viewModel: WorshipViewModel,
    modifier: Modifier = Modifier
) {
    val surahs = QuranData.surahs
    val favorites by viewModel.favorites.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "الأوراد والآيات المخصوصة",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Special Verses Section (آية الكرسي، أول 10 آيات كهف، آية 110)
        val specialVerses = surahs.filter { it.isPractice }
        items(specialVerses, key = { it.id }) { item ->
            val isFav = favorites.any { it.targetId == item.id }
            QuranItemCard(
                item = item,
                isFavorite = isFav,
                onCardClick = { viewModel.navigateTo(AppDestination.QuranReader(item.id)) },
                onToggleFavorite = {
                    viewModel.toggleFavorite(item.id, "QURAN", item.title, item.description)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "السور القرآنية المباركة",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Regular Surahs
        val regularSurahs = surahs.filter { !it.isPractice }
        items(regularSurahs, key = { it.id }) { item ->
            val isFav = favorites.any { it.targetId == item.id }
            QuranItemCard(
                item = item,
                isFavorite = isFav,
                onCardClick = { viewModel.navigateTo(AppDestination.QuranReader(item.id)) },
                onToggleFavorite = {
                    viewModel.toggleFavorite(item.id, "QURAN", item.title, item.description)
                }
            )
        }
    }
}

@Composable
fun QuranItemCard(
    item: QuranSurahItem,
    isFavorite: Boolean,
    onCardClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                )
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .testTag("quran_item_${item.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (item.practiceTime.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.practiceTime,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GoldDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Outlined.Star else Icons.Outlined.StarBorder,
                    contentDescription = "المفضلة",
                    tint = if (isFavorite) GoldBase else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
