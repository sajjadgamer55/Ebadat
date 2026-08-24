package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NightsStay
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.WorshipViewModel

@Composable
fun NightPrayerScreen(
    viewModel: WorshipViewModel,
    modifier: Modifier = Modifier
) {
    val todayLog by viewModel.todayLog.collectAsState()
    val isNightPrayerDone = todayLog?.nightPrayerCompleted ?: false

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Status Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isNightPrayerDone) Emerald900 else MaterialTheme.colorScheme.surface
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        if (isNightPrayerDone) listOf(GoldBase, Emerald500) else listOf(
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    ),
                    width = if (isNightPrayerDone) 2.dp else 1.dp
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("night_prayer_hero_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                if (isNightPrayerDone) Emerald700 else MaterialTheme.colorScheme.primaryContainer
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.NightsStay,
                            contentDescription = null,
                            tint = if (isNightPrayerDone) GoldBase else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "صلاة الليل وأنس السحر",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isNightPrayerDone) Color.White else MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isNightPrayerDone) "✓ تم أداء صلاة الليل لهذا اليوم بنجاح وتقبل الله طاعتكم" else "لم تُنجز صلاة الليل بعد لليوم",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isNightPrayerDone) GoldLight else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = { viewModel.toggleNightPrayer() },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isNightPrayerDone) Emerald600 else MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("toggle_night_prayer_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isNightPrayerDone) "إلغاء التحديد (تغيير الحالة)" else "تحديد كمنجزة اليوم",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // Method & Steps Card
        item {
            Text(
                text = "كيفية صلاة الليل (١١ ركعة)",
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
                Column(modifier = Modifier.padding(18.dp)) {
                    PrayerStepItem(
                        number = "١",
                        title = "نافلة الليل (٨ ركعات)",
                        desc = "تُصلى ركعتين ركعتين كصلاة الصبح، يُستحب قراءة التوحيد بعد الحمد."
                    )
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    PrayerStepItem(
                        number = "٢",
                        title = "صلاة الشفع (ركعتان)",
                        desc = "الركعة الأولى: الحمد وسورة الفلق. الركعة الثانية: الحمد وسورة الناس."
                    )
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    PrayerStepItem(
                        number = "٣",
                        title = "صلاة الوتر (ركعة واحدة)",
                        desc = "الحمد ثم التوحيد (٣ مرات) والفلق والناس، ثم القنوت الطويل بالاستغفار ٧٠ مرة وقول (العفو) ٣٠٠ مرة والدعاء لأربعين مؤمناً."
                    )
                }
            }
        }

        // Virtues Card
        item {
            Text(
                text = "فضائل قيام الليل",
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "• صلاة الليل تُبيض الوجه، وتُطيب الريح، وتجلب الرزق، وتقضي الدين، وتذهب بالهم، وتجلو البصر.",
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• شرف المؤمن صلاته بالليل، وعزه كف الأذى عن الناس.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Emerald800
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun PrayerStepItem(number: String, title: String, desc: String) {
    Row(verticalAlignment = Alignment.Top) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
