package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.WorshipViewModel

@Composable
fun BedtimeScreen(
    viewModel: WorshipViewModel,
    modifier: Modifier = Modifier
) {
    val bedtimeRecord by viewModel.bedtimeRecord.collectAsState()

    val ayatKursiCount = bedtimeRecord?.ayatKursiCount ?: 0
    val isYasinDone = bedtimeRecord?.yasinRead ?: false
    val habit1 = bedtimeRecord?.habit1Done ?: false
    val habit2 = bedtimeRecord?.habit2Done ?: false
    val habit3 = bedtimeRecord?.habit3Done ?: false

    val isAllDone = (ayatKursiCount >= 5) && isYasinDone && habit1 && habit2 && habit3

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = if (isAllDone) Emerald900 else MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        if (isAllDone) listOf(GoldBase, Emerald500) else listOf(
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    )
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(if (isAllDone) Emerald700 else MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bedtime,
                            contentDescription = null,
                            tint = if (isAllDone) GoldBase else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "عادات وحصن قبل النوم",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (isAllDone) Color.White else MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isAllDone) "✓ تم إكمال عادات قبل النوم بنجاح" else "أتمم الورد المأثور لتحصين الليل والاستيقاظ لصلاة الفجر",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isAllDone) GoldLight else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 1. Ayat Al Kursi 5 Times
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "آية الكرسي قبل النوم (٥ مرات)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "حفظ وتحصين وطمأنينة حتى الصباح",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        TextButton(onClick = { viewModel.navigateTo(AppDestination.QuranReader("ayat_kursi_sleep")) }) {
                            Text(text = "قراءة الآية", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { viewModel.incrementBedtimeAyatKursi() },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("bedtime_ayat_kursi_btn")
                        ) {
                            Text(
                                text = if (ayatKursiCount >= 5) "✓ تم ٥ مرات ($ayatKursiCount)" else "قراءة مرة ($ayatKursiCount / 5)",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }

                        LinearProgressIndicator(
                            progress = { (ayatKursiCount.toFloat() / 5f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (ayatKursiCount >= 5) Emerald600 else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // 2. Surah Yasin
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "سورة يس المباركة",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "قلب القرآن وأمان من كل سوء في المنام",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(
                            onClick = { viewModel.navigateTo(AppDestination.QuranReader("surah_yasin")) },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(text = "فتح سورة يس للقراءة ↤", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Switch(
                        checked = isYasinDone,
                        onCheckedChange = { viewModel.toggleBedtimeYasin() },
                        modifier = Modifier.testTag("bedtime_yasin_switch")
                    )
                }
            }
        }

        // 3. Checklist for other bedtime practices
        item {
            Text(
                text = "قائمة سنن وآداب النوم",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    BedtimeCheckItem(
                        title = "الوضوء وتسبيح سيدة نساء العالمين (ع)",
                        subtitle = "٣٤ الله أكبر، ٣٣ الحمد لله، ٣٣ سبحان الله",
                        isChecked = habit1,
                        onToggle = { viewModel.toggleBedtimeHabit1() }
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    BedtimeCheckItem(
                        title = "قراءة الإخلاص والمعوذتين والتوحيد",
                        subtitle = "حصن الليل ودفع الوساوس",
                        isChecked = habit2,
                        onToggle = { viewModel.toggleBedtimeHabit2() }
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    BedtimeCheckItem(
                        title = "قراءة آخر آية من سورة الكهف (الآية ١١٠)",
                        subtitle = "نية الاستيقاظ لصلاة الليل وصلاة الفجر",
                        isChecked = habit3,
                        onToggle = { viewModel.toggleBedtimeHabit3() },
                        onAction = { viewModel.navigateTo(AppDestination.QuranReader("surah_kahf_110")) }
                    )
                }
            }
        }
    }
}

@Composable
fun BedtimeCheckItem(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onToggle: () -> Unit,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (onAction != null) {
                TextButton(onClick = onAction, contentPadding = PaddingValues(0.dp)) {
                    Text(text = "قراءة الآية ↤", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Checkbox(
            checked = isChecked,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(checkedColor = Emerald700)
        )
    }
}
