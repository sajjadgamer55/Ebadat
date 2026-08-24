package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DhikrEntity
import com.example.ui.components.DhikrCounterCard
import com.example.ui.theme.Emerald700
import com.example.ui.theme.GoldBase
import com.example.ui.viewmodel.WorshipViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DhikrScreen(
    viewModel: WorshipViewModel,
    modifier: Modifier = Modifier
) {
    val dhikrs by viewModel.dhikrs.collectAsState()
    val selectedCategory by viewModel.selectedDhikrCategory.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showResetAllConfirm by remember { mutableStateOf(false) }

    val categories = listOf("الكل", "يومي", "صباحي", "غروب", "المفضلة")

    val filteredDhikrs = when (selectedCategory) {
        "الكل" -> dhikrs
        "المفضلة" -> dhikrs.filter { it.isFavorite }
        else -> dhikrs.filter { it.category == selectedCategory }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_custom_dhikr_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة ذكر مخصص")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Category Filter Chips & Reset All Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { viewModel.setDhikrCategory(category) },
                        label = {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                OutlinedButton(
                    onClick = { showResetAllConfirm = true },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "إعادة ضبط الكل",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "تصفير اليوم", style = MaterialTheme.typography.labelSmall)
                }
            }

            // Dhikr Counter Cards List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredDhikrs, key = { it.id }) { dhikr ->
                    DhikrCounterCard(
                        dhikr = dhikr,
                        onIncrement = { viewModel.incrementDhikr(dhikr.id) },
                        onDecrement = { viewModel.decrementDhikr(dhikr.id) },
                        onReset = { viewModel.resetDhikr(dhikr.id) },
                        onToggleFavorite = {
                            viewModel.toggleFavorite(
                                id = dhikr.id,
                                type = "DHIKR",
                                title = dhikr.title,
                                subtitle = "العدد: ${dhikr.targetCount}"
                            )
                        }
                    )
                }

                if (filteredDhikrs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "لا توجد أذكار في هذا القسم حالياً",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Custom Dhikr Dialog
    if (showAddDialog) {
        AddDhikrDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, count, category, instruction ->
                viewModel.addCustomDhikr(title, count, category, instruction)
                showAddDialog = false
            }
        )
    }

    // Reset All Confirmation Dialog
    if (showResetAllConfirm) {
        AlertDialog(
            onDismissRequest = { showResetAllConfirm = false },
            title = { Text(text = "تصفير عدادات اليوم") },
            text = { Text(text = "هل أنت متأكد من رغبتك في إعادة ضبط جميع عدادات الأذكار إلى الصفر لبداية يوم جديد؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetAllDailyDhikrs()
                        showResetAllConfirm = false
                    }
                ) {
                    Text(text = "نعم، تصفير", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetAllConfirm = false }) {
                    Text(text = "إلغاء")
                }
            }
        )
    }
}

@Composable
fun AddDhikrDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, count: Int, category: String, instruction: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var countStr by remember { mutableStateOf("100") }
    var isUnlimited by remember { mutableStateOf(false) }
    var instruction by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("يومي") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "إضافة ذكر أو ورد جديد",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("نص الذكر") },
                    placeholder = { Text("مثال: سبحان الله وبحمده") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "عداد مفتوح (غير محدود):", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = isUnlimited,
                        onCheckedChange = { isUnlimited = it }
                    )
                }

                if (!isUnlimited) {
                    OutlinedTextField(
                        value = countStr,
                        onValueChange = { countStr = it },
                        label = { Text("العدد المستهدف") },
                        placeholder = { Text("100") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                OutlinedTextField(
                    value = instruction,
                    onValueChange = { instruction = it },
                    label = { Text("ملاحظة أو وقت الاستحباب (اختياري)") },
                    placeholder = { Text("مثال: بعد صلاة العصر") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val count = if (isUnlimited) 0 else (countStr.toIntOrNull() ?: 100)
                        onConfirm(title.trim(), count, category, instruction.trim())
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text(text = "إضافة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "إلغاء")
            }
        }
    )
}
