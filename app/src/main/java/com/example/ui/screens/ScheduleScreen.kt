package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.staticdata.DayScheduleItem
import com.example.data.staticdata.ScheduleData
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.WorshipViewModel
import java.util.Calendar

@Composable
fun ScheduleScreen(
    viewModel: WorshipViewModel,
    modifier: Modifier = Modifier
) {
    val items = ScheduleData.weeklySchedule
    val currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner for daily continuous worship
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Emerald800),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GoldLight,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "الورد الثابت في كل يوم",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldLight
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• زيارة عاشوراء المباركة (مع عدادي اللعن والسلام ١٠٠ مرة)\n• مناجاة التائبين للإمام السجاد (ع)",
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                        color = Color.White
                    )
                }
            }
        }

        item {
            Text(
                text = "جدول الأيام السبعة",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Days List
        items(items, key = { it.dayOfWeek }) { dayItem ->
            val isToday = dayItem.dayOfWeek == currentDayOfWeek
            DayScheduleCard(
                dayItem = dayItem,
                isToday = isToday,
                onOpenSurah = { viewModel.navigateTo(AppDestination.QuranReader(dayItem.quranSurahId)) },
                onOpenMunajat = { viewModel.navigateTo(AppDestination.DuaDetail(dayItem.munajatId)) }
            )
        }
    }
}

@Composable
fun DayScheduleCard(
    dayItem: DayScheduleItem,
    isToday: Boolean,
    onOpenSurah: () -> Unit,
    onOpenMunajat: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isToday) Emerald900 else MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                if (isToday) listOf(GoldBase, Emerald400) else listOf(
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                )
            ),
            width = if (isToday) 2.dp else 1.dp
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isToday) 4.dp else 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("schedule_card_${dayItem.dayOfWeek}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        tint = if (isToday) GoldLight else Emerald700,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "يوم ${dayItem.dayNameArabic}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = if (isToday) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }

                if (isToday) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = GoldBase
                    ) {
                        Text(
                            text = "اليوم الحالي",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Emerald900
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action 1: Surah
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isToday) Emerald800 else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenSurah)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = null,
                            tint = if (isToday) GoldLight else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = dayItem.quranSurahName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isToday) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "قراءة السورة ↤",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isToday) GoldLight else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action 2: Munajat
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isToday) Emerald800 else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenMunajat)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = if (isToday) GoldLight else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = dayItem.munajatName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isToday) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "قراءة المناجاة ↤",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isToday) GoldLight else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}
