package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.staticdata.ScheduleData
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.WorshipViewModel

@Composable
fun HomeScreen(
    viewModel: WorshipViewModel,
    modifier: Modifier = Modifier
) {
    val dhikrs by viewModel.dhikrs.collectAsState()
    val habits by viewModel.habits.collectAsState()
    val todayLog by viewModel.todayLog.collectAsState()
    val bedtimeRecord by viewModel.bedtimeRecord.collectAsState()

    val completedDhikrs = dhikrs.count { it.isCompleted }
    val completedHabits = habits.count { it.isCompletedToday }
    val totalWork = (dhikrs.size + habits.size).coerceAtLeast(10)
    val doneWork = completedDhikrs + completedHabits
    val percentCompleted = ((doneWork.toFloat() / totalWork.toFloat()) * 100).toInt().coerceIn(0, 100)
    val remainingWork = (totalWork - doneWork).coerceAtLeast(0)
    val streakDays = todayLog?.streakCount ?: 12

    val todaySchedule = ScheduleData.getTodaySchedule()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Islamic Greeting Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Emerald800
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("greeting_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Emerald900, Emerald700, Emerald800)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "السلام عليكم ورحمة الله وبركاته",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = GoldLight
                            )
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Emerald900.copy(alpha = 0.6f),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.linearGradient(listOf(GoldBase, Emerald400))
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🔥 $streakDays يومًا متواصلًا",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        ),
                                        color = GoldLight
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "وردك اليومي",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            ),
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Progress Bar & Stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$percentCompleted% مكتمل",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = GoldBase
                            )
                            Text(
                                text = "$doneWork من $totalWork أعمال",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                color = TextLightSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { percentCompleted / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = GoldBase,
                            trackColor = Emerald900
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "المتبقي اليوم: $remainingWork أعمال للوصول للهدف الكامل",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = TextLightSecondary
                        )
                    }
                }
            }
        }

        // Night Prayer Quick Card
        item {
            val isNightPrayerDone = todayLog?.nightPrayerCompleted ?: false
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isNightPrayerDone) Emerald900 else MaterialTheme.colorScheme.surface
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        colors = if (isNightPrayerDone) listOf(GoldBase, Emerald500) else listOf(
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.toggleNightPrayer() }
                    .testTag("night_prayer_home_card")
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
                            shape = CircleShape,
                            color = if (isNightPrayerDone) Emerald700 else MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.NightsStay,
                                    contentDescription = null,
                                    tint = if (isNightPrayerDone) GoldBase else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "صلاة الليل",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isNightPrayerDone) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isNightPrayerDone) "✓ تم الإنجاز" else "لم تُنجز — اضغط للتحديد",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isNightPrayerDone) GoldLight else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    FilledTonalButton(
                        onClick = { viewModel.toggleNightPrayer() },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (isNightPrayerDone) Emerald600 else MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = if (isNightPrayerDone) "✓ تم الإنجاز" else "تحديد كمنجز",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isNightPrayerDone) Color.White else MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }

        // Today's Weekly Schedule Highlight
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(listOf(GoldBase.copy(alpha = 0.6f), Emerald500.copy(alpha = 0.6f)))
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(AppDestination.Schedule) }
                    .testTag("today_schedule_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = null,
                                tint = GoldDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "جدول عبادات اليوم (${todaySchedule.dayNameArabic})",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "عرض الكل",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "• ${todaySchedule.quranSurahName}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• ${todaySchedule.munajatName}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• زيارة عاشوراء + مناجاة التائبين",
                                style = MaterialTheme.typography.bodySmall,
                                color = GoldDark
                            )
                        }
                    }
                }
            }
        }

        // Section Title: Intelligent Time-based practices
        item {
            Text(
                text = "الأعمال بحسب الأوقات",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // 1. بعد صلاة الفجر
        item {
            TimeSlotCard(
                timeTitle = "بعد صلاة الفجر",
                icon = Icons.Outlined.WbSunny,
                items = listOf(
                    "آية الكرسي ×٥ عند الاستيقاظ",
                    "يا فتاح ×٧٠ (ضع يدك اليمنى على قلبك)",
                    "الأعمال الصباحية والصلوات على محمد وآل محمد"
                ),
                actionLabel = "بدء الورد الصباحي",
                onAction = { viewModel.startRoutine("routine_morning") }
            )
        }

        // 2. خلال اليوم
        item {
            TimeSlotCard(
                timeTitle = "خلال اليوم",
                icon = Icons.Outlined.LightMode,
                items = listOf(
                    "الأذكار والتسبيحات اليومية (١٠٠ مرة)",
                    "ورد القرآن الكريم (${todaySchedule.quranSurahName})",
                    "زيارة عاشوراء المعتبرة"
                ),
                actionLabel = "فتح الأذكار",
                onAction = { viewModel.navigateTo(AppDestination.Dhikr) }
            )
        }

        // 3. وقت الغروب
        item {
            TimeSlotCard(
                timeTitle = "وقت الغروب",
                icon = Icons.Outlined.WbTwilight,
                items = listOf(
                    "لا إله إلا الله، لا حول ولا قوة إلا بالله (الذكر المفتوح)",
                    "الاستغفار ودفع الهموم",
                    "مناجاة الشاكرين والذاكرين"
                ),
                actionLabel = "بدء أذكار الغروب",
                onAction = { viewModel.navigateTo(AppDestination.Dhikr) }
            )
        }

        // 4. قبل النوم
        item {
            TimeSlotCard(
                timeTitle = "قبل النوم",
                icon = Icons.Outlined.Bedtime,
                items = listOf(
                    "آية الكرسي ×٥ عند النوم",
                    "سورة يس المباركة",
                    "عادات وحصن قبل النوم"
                ),
                actionLabel = "عادات قبل النوم",
                onAction = { viewModel.navigateTo(AppDestination.BedtimeHabits) }
            )
        }

        // Quick Access Row
        item {
            Text(
                text = "الوصول السريع",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickTile(
                    title = "الأحاديث والفضائل",
                    icon = Icons.Outlined.MenuBook,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(AppDestination.Hadiths) }
                )
                QuickTile(
                    title = "عاداتي",
                    icon = Icons.Outlined.Checklist,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(AppDestination.Habits) }
                )
                QuickTile(
                    title = "روتيني الخاص",
                    icon = Icons.Outlined.PlayCircleOutline,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(AppDestination.Routines) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TimeSlotCard(
    timeTitle: String,
    icon: ImageVector,
    items: List<String>,
    actionLabel: String,
    onAction: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = timeTitle,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                FilledTonalButton(
                    onClick = onAction,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = actionLabel, style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            items.forEach { item ->
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) {
                    Text(
                        text = "• ",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Emerald600
                    )
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun QuickTile(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
            )
        ),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}
