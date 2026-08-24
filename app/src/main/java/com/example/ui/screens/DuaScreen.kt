package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.staticdata.DuaData
import com.example.data.staticdata.DuaItem
import com.example.ui.theme.GoldBase
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.WorshipViewModel

@Composable
fun DuaScreen(
    viewModel: WorshipViewModel,
    modifier: Modifier = Modifier
) {
    val items = DuaData.items
    val favorites by viewModel.favorites.collectAsState()
    var selectedTab by remember { mutableStateOf("الكل") }

    val categories = listOf("الكل", "الأدعية المأثورة", "المناجاة اليومية")

    val filteredItems = when (selectedTab) {
        "الكل" -> items
        else -> items.filter { it.category == selectedTab }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Tab row
        TabRow(
            selectedTabIndex = categories.indexOf(selectedTab).coerceAtLeast(0),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            categories.forEach { cat ->
                Tab(
                    selected = selectedTab == cat,
                    onClick = { selectedTab = cat },
                    text = {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = if (selectedTab == cat) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredItems, key = { it.id }) { item ->
                val isFav = favorites.any { it.targetId == item.id }
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
                        .clickable { viewModel.navigateTo(AppDestination.DuaDetail(item.id)) }
                        .testTag("dua_card_${item.id}")
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
                                        imageVector = Icons.Default.AutoAwesome,
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
                                    text = item.subtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                viewModel.toggleFavorite(item.id, "DUA", item.title, item.subtitle)
                            }
                        ) {
                            Icon(
                                imageVector = if (isFav) Icons.Outlined.Star else Icons.Outlined.StarBorder,
                                contentDescription = "المفضلة",
                                tint = if (isFav) GoldBase else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
