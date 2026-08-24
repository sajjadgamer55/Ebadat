package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.WorshipViewModel

@Composable
fun StatsScreen(
    viewModel: WorshipViewModel,
    modifier: Modifier = Modifier
) {
    val todayLog by viewModel.todayLog.collectAsState()
    val allLogs by viewModel.allLogs.collectAsState()
    val dhikrs by viewModel.dhikrs.collectAsState()

    val totalDhikrToday = dhikrs.sumOf { it.currentCount }
    val totalDhikrsEver = allLogs.sumOf { it.totalTasbihCount } + totalDhikrToday
    val totalNightPrayers = allLogs.count { it.nightPrayerCompleted } + (if (todayLog?.nightPrayerCompleted == true) 1 else 0)
    val streakDays = todayLog?.streakCount ?: 12

    val topDhikrs = dhikrs.sortedByDescending { it.currentCount }.take(3)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "إحصائيات العبادة والأوراد",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Streak Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Emerald800),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "سلسلة الالتزام الحالية",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GoldLight
                        )
                        Text(
                            text = "🔥 $streakDays يوماً متواصلاً",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "أطول سلسلة سابقة: ٢٤ يوماً",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextLightSecondary
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = GoldBase,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }
        }

        // Key Metric Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatMetricCard(
                    title = "تسبيحات اليوم",
                    value = "$totalDhikrToday",
                    icon = Icons.Default.BarChart,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "مجموع التسبيحات",
                    value = "$totalDhikrsEver",
                    icon = Icons.Default.ShowChart,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatMetricCard(
                    title = "صلاة الليل",
                    value = "$totalNightPrayers ليلة",
                    icon = Icons.Default.AutoAwesome,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "ختمات الأوراد",
                    value = "${allLogs.count { it.completedCount >= it.totalCount }} ختمة",
                    icon = Icons.Default.BarChart,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Most Repeated Dhikrs
        item {
            Text(
                text = "أكثر الأذكار تكراراً اليوم",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(topDhikrs.size) { index ->
            val d = topDhikrs[index]
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${index + 1}. ${d.title}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${d.currentCount} مرة",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun StatMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
