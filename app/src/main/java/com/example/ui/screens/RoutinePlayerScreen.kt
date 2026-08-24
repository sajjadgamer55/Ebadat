package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.WorshipViewModel

@Composable
fun RoutinePlayerScreen(
    routineId: String,
    viewModel: WorshipViewModel,
    modifier: Modifier = Modifier
) {
    val routine by viewModel.activeRoutine.collectAsState()
    val stepIndex by viewModel.currentRoutineStepIndex.collectAsState()
    val currentStepCount by viewModel.currentRoutineStepCount.collectAsState()
    val isPaused by viewModel.isRoutinePaused.collectAsState()
    val isCompleted by viewModel.isRoutineCompleted.collectAsState()

    if (routine == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val steps = routine!!.steps
    val currentStep = steps.getOrNull(stepIndex)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = routine!!.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "الخطوة ${stepIndex + 1} من ${steps.size}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { ((stepIndex + 1).toFloat() / steps.size.toFloat()).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = GoldBase,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        if (isCompleted) {
            // Completion View
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Emerald800),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = GoldLight,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "هنيئاً لك! تم إكمال الروتين بالكامل بنجاح",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.restartRoutine() },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald600)
                    ) {
                        Text(text = "إعادة الروتين من البداية")
                    }
                }
            }
        } else if (currentStep != null) {
            // Active Step Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = currentStep.title,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        if (currentStep.instruction.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = currentStep.instruction,
                                style = MaterialTheme.typography.bodyMedium,
                                color = GoldDark,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Giant Tap Counter
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(170.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Emerald700, Emerald900)
                            )
                        )
                        .clickable(enabled = !isPaused) { viewModel.tapRoutineCount() }
                        .testTag("routine_player_tap_btn")
                ) {
                    CircularProgressIndicator(
                        progress = { (currentStepCount.toFloat() / currentStep.count.toFloat()).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxSize(),
                        color = GoldBase,
                        trackColor = Color.Transparent,
                        strokeWidth = 8.dp
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$currentStepCount",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 42.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "من ${currentStep.count}",
                            style = MaterialTheme.typography.titleSmall,
                            color = GoldLight
                        )
                    }
                }
            }
        }

        // Bottom Controls: Pause, Skip, Reset, Back
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { viewModel.resetRoutineStep() },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "إعادة الخطوة")
                Spacer(modifier = Modifier.width(4.dp))
                Text("إعادة")
            }

            FilledTonalButton(
                onClick = { viewModel.togglePauseRoutine() },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isPaused) "استئناف" else "إيقاف مؤقت")
            }

            OutlinedButton(
                onClick = { viewModel.skipRoutineStep() },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.SkipNext, contentDescription = "تخطي الخطوة")
                Spacer(modifier = Modifier.width(4.dp))
                Text("تخطي")
            }
        }
    }
}
