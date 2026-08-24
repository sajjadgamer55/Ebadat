package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DailyLogEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.WorshipViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    viewModel: WorshipViewModel,
    modifier: Modifier = Modifier
) {
    val allLogs by viewModel.allLogs.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: السجل, 1: التقويم

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("سجل الأيام السابقة", style = MaterialTheme.typography.titleSmall) },
                icon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(20.dp)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("التقويم التفاعلي", style = MaterialTheme.typography.titleSmall) },
                icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(20.dp)) }
            )
        }

        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (allLogs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "يبدأ تسجيل الأوراد والعبادات تلقائياً مع ممارستك اليومية.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                items(allLogs, key = { it.date }) { log ->
                    HistoryLogCard(log = log)
                }
            }
        } else {
            CalendarViewSection(logs = allLogs)
        }
    }
}

@Composable
fun HistoryLogCard(log: DailyLogEntity) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "التاريخ: ${log.date}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (log.completedCount >= log.totalCount) Emerald100 else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "${log.completedCount} من ${log.totalCount} أعمال",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (log.completedCount >= log.totalCount) Emerald900 else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusBadge(label = "صلاة الليل", isDone = log.nightPrayerCompleted)
                StatusBadge(label = "القرآن", isDone = log.quranCompleted)
                StatusBadge(label = "الزيارة", isDone = log.ziyarahCompleted)
            }

            if (log.totalTasbihCount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "مجموع التسبيحات في هذا اليوم: ${log.totalTasbihCount} تسبيحة",
                    style = MaterialTheme.typography.bodySmall,
                    color = GoldDark
                )
            }
        }
    }
}

@Composable
fun StatusBadge(label: String, isDone: Boolean) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (isDone) Emerald100 else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Text(
            text = if (isDone) "✓ $label" else "○ $label",
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (isDone) Emerald800 else MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun CalendarViewSection(logs: List<DailyLogEntity>) {
    var selectedDayDetail by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(color = Emerald600, label = "مكتمل (≥ ٨٠%)")
            LegendItem(color = GoldBase, label = "مكتمل جزئياً")
            LegendItem(color = MaterialTheme.colorScheme.surfaceVariant, label = "لا يوجد نشاط")
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Days of Month Grid (30 days example)
        val daysList = (1..30).toList()

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(daysList) { dayNumber ->
                val dayStr = if (dayNumber < 10) "0$dayNumber" else "$dayNumber"
                val matchLog = logs.find { it.date.endsWith("-$dayStr") }

                val color = when {
                    matchLog == null -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    matchLog.completedCount >= (matchLog.totalCount * 0.8) -> Emerald600
                    matchLog.completedCount > 0 -> GoldBase
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = color,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clickable {
                            selectedDayDetail = "اليوم $dayNumber: " + (matchLog?.let { "${it.completedCount} من ${it.totalCount} أعمال منجزة" } ?: "لا توجد سجلات مسجلة لهذا اليوم")
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "$dayNumber",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (matchLog != null && matchLog.completedCount > 0) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (selectedDayDetail != null) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selectedDayDetail!!,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}
