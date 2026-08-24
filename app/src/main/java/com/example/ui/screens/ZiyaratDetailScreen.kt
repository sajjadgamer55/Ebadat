package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.staticdata.ZiyaratData
import com.example.ui.theme.*
import com.example.ui.viewmodel.WorshipViewModel

@Composable
fun ZiyaratDetailScreen(
    ziyarahId: String,
    viewModel: WorshipViewModel,
    modifier: Modifier = Modifier
) {
    val item = ZiyaratData.items.find { it.id == ziyarahId } ?: ZiyaratData.items.first()
    val fontSize by viewModel.readerFontSize.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val isFav = favorites.any { it.targetId == item.id }

    val laanCount by viewModel.ashuraLaanCount.collectAsState()
    val salaamCount by viewModel.ashuraSalaamCount.collectAsState()

    var isMarkedDone by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Controls Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Font Size Controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.decreaseFontSize() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text(text = "A-", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    Text(
                        text = "${fontSize}sp",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    IconButton(
                        onClick = { viewModel.increaseFontSize() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text(text = "A+", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }

                // Action buttons: Complete & Favorite
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            viewModel.toggleFavorite(item.id, "ZIYARAH", item.title, item.subtitle)
                        }
                    ) {
                        Icon(
                            imageVector = if (isFav) Icons.Outlined.Star else Icons.Outlined.StarBorder,
                            contentDescription = "المفضلة",
                            tint = if (isFav) GoldBase else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    FilledTonalButton(
                        onClick = {
                            isMarkedDone = true
                            viewModel.markItemRead(item.id, "ZIYARAH")
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (isMarkedDone) Emerald700 else MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isMarkedDone) "تمت الزيارة" else "تحديد كمكتمل",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }

        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(listOf(GoldBase, Emerald700))
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Text
            Text(
                text = item.fullText,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = fontSize.sp,
                    lineHeight = (fontSize * 1.85).sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Normal
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Justify,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ziyarah_text_container")
            )

            // If has 100 Counters (Ziyarat Ashura)
            if (item.has100Counters) {
                Spacer(modifier = Modifier.height(30.dp))

                // 1. اللعن (100 مرة)
                Dedicated100CounterCard(
                    sectionTitle = "ورد اللعن (١٠٠ مرة)",
                    sectionSubtitle = "اللهم العن أول ظالم ظلم حق محمد وآل محمد...",
                    verseText = item.laanText,
                    currentCount = laanCount,
                    targetCount = 100,
                    onTap = { viewModel.incrementAshuraLaan() },
                    onUndo = { viewModel.decrementAshuraLaan() },
                    onReset = { viewModel.resetAshuraLaan() },
                    testTagPrefix = "laan"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 2. السلام (100 مرة)
                Dedicated100CounterCard(
                    sectionTitle = "ورد السلام (١٠٠ مرة)",
                    sectionSubtitle = "السلام عليك يا أبا عبد الله وعلى الأرواح التي حلت بفنائك...",
                    verseText = item.salaamText,
                    currentCount = salaamCount,
                    targetCount = 100,
                    onTap = { viewModel.incrementAshuraSalaam() },
                    onUndo = { viewModel.decrementAshuraSalaam() },
                    onReset = { viewModel.resetAshuraSalaam() },
                    testTagPrefix = "salaam"
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun Dedicated100CounterCard(
    sectionTitle: String,
    sectionSubtitle: String,
    verseText: String,
    currentCount: Int,
    targetCount: Int,
    onTap: () -> Unit,
    onUndo: () -> Unit,
    onReset: () -> Unit,
    testTagPrefix: String
) {
    val isDone = currentCount >= targetCount

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                if (isDone) listOf(Emerald600, GoldBase) else listOf(
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
            ),
            width = if (isDone) 2.dp else 1.dp
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("${testTagPrefix}_100_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = sectionTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = verseText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Serif,
                        lineHeight = 26.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Big Tap Circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = if (isDone) listOf(Emerald600, Emerald800) else listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .clickable(onClick = onTap)
                    .testTag("${testTagPrefix}_tap_btn")
            ) {
                CircularProgressIndicator(
                    progress = { (currentCount.toFloat() / targetCount.toFloat()).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxSize(),
                    color = if (isDone) GoldBase else Emerald500,
                    trackColor = Color.Transparent,
                    strokeWidth = 6.dp
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$currentCount",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        ),
                        color = if (isDone) Color.White else MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "من $targetCount",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDone) GoldLight else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isDone) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Emerald100,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "✓ تم إتمام الـ ١٠٠ مرة بنجاح",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Emerald900
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = onUndo,
                    enabled = currentCount > 0,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Undo, contentDescription = "تراجع", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "تراجع", style = MaterialTheme.typography.labelSmall)
                }

                OutlinedButton(
                    onClick = onReset,
                    enabled = currentCount > 0,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "إعادة", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "إعادة", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
