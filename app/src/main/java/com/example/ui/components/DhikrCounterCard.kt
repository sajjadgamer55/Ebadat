package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DhikrEntity
import com.example.ui.theme.*

@Composable
fun DhikrCounterCard(
    dhikr: DhikrEntity,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onReset: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompleted = dhikr.isCompleted
    val progress = if (dhikr.isUnlimited) 1f else {
        if (dhikr.targetCount > 0) (dhikr.currentCount.toFloat() / dhikr.targetCount.toFloat()).coerceIn(0f, 1f) else 0f
    }
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dhikr_card_${dhikr.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                colors = if (isCompleted) listOf(Emerald500, GoldBase) else listOf(
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
            ),
            width = if (isCompleted) 1.5.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row: Title and Favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = dhikr.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            lineHeight = 26.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                    if (dhikr.instruction.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = dhikr.instruction,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                color = GoldDark
                            ),
                            textAlign = TextAlign.Start
                        )
                    }
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.testTag("fav_btn_${dhikr.id}")
                ) {
                    Icon(
                        imageVector = if (dhikr.isFavorite) Icons.Outlined.Star else Icons.Outlined.StarBorder,
                        contentDescription = "إضافة للمفضلة",
                        tint = if (dhikr.isFavorite) GoldBase else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Large Circular Interactive Tap Counter
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = if (isCompleted) listOf(Emerald600, Emerald800) else listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .border(
                        width = 4.dp,
                        brush = Brush.sweepGradient(
                            colors = if (isCompleted) listOf(GoldBase, Emerald400, GoldBase) else listOf(
                                GoldBase.copy(alpha = 0.4f),
                                Emerald600.copy(alpha = 0.4f),
                                GoldBase.copy(alpha = 0.4f)
                            )
                        ),
                        shape = CircleShape
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = GoldBase),
                        onClick = onIncrement
                    )
                    .testTag("dhikr_tap_${dhikr.id}")
            ) {
                // Circular Progress Indicator around button
                if (!dhikr.isUnlimited) {
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.fillMaxSize(),
                        color = if (isCompleted) GoldBase else Emerald500,
                        trackColor = Color.Transparent,
                        strokeWidth = 6.dp
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${dhikr.currentCount}",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        ),
                        color = if (isCompleted) Color.White else MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (dhikr.isUnlimited) "مفتوح" else "من ${dhikr.targetCount}",
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp),
                        color = if (isCompleted) GoldLight else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Completion Banner
            AnimatedVisibility(visible = isCompleted) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Emerald100.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Emerald700,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "✓ تم إكمال الذكر",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Emerald900
                            )
                        )
                    }
                }
            }

            // Bottom Action Bar: Undo & Reset
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDecrement,
                    enabled = dhikr.currentCount > 0,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("undo_btn_${dhikr.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Undo,
                        contentDescription = "تراجع",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "تراجع", style = MaterialTheme.typography.labelMedium)
                }

                OutlinedButton(
                    onClick = onReset,
                    enabled = dhikr.currentCount > 0,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("reset_btn_${dhikr.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "إعادة",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "إعادة", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
