package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.WorshipViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: WorshipViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by viewModel.settings.collectAsState()
            val isDark = when (settings.themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> false
            }

            WirdiTheme(darkTheme = isDark) {
                WorshipMainScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorshipMainScreen(viewModel: WorshipViewModel) {
    val currentDestination by viewModel.currentDestination.collectAsState()
    val isTopLevel = currentDestination in listOf(
        AppDestination.Home,
        AppDestination.Dhikr,
        AppDestination.Quran,
        AppDestination.Ziyarat,
        AppDestination.Duas
    )

    BackHandler(enabled = !isTopLevel || currentDestination != AppDestination.Home) {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("worship_main_screen"),
        topBar = {
            TopHeaderBar(
                destination = currentDestination,
                onBack = { viewModel.navigateBack() },
                onNavigateSearch = { viewModel.navigateTo(AppDestination.Search) },
                onNavigateFavorites = { viewModel.navigateTo(AppDestination.Favorites) },
                onNavigateSettings = { viewModel.navigateTo(AppDestination.Settings) },
                onNavigateStats = { viewModel.navigateTo(AppDestination.Statistics) },
                onNavigateHistory = { viewModel.navigateTo(AppDestination.History) }
            )
        },
        bottomBar = {
            if (isTopLevel) {
                GeometricBottomNavigation(
                    currentDestination = currentDestination,
                    onSelect = { viewModel.navigateTo(it) }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentDestination,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { destination ->
                when (destination) {
                    AppDestination.Home -> HomeScreen(viewModel = viewModel)
                    AppDestination.Dhikr -> DhikrScreen(viewModel = viewModel)
                    AppDestination.Quran -> QuranScreen(viewModel = viewModel)
                    is AppDestination.QuranReader -> QuranReaderScreen(
                        viewModel = viewModel,
                        surahId = destination.surahId
                    )
                    AppDestination.Ziyarat -> ZiyaratScreen(viewModel = viewModel)
                    is AppDestination.ZiyaratDetail -> ZiyaratDetailScreen(
                        viewModel = viewModel,
                        ziyarahId = destination.ziyarahId
                    )
                    AppDestination.Duas -> DuaScreen(viewModel = viewModel)
                    is AppDestination.DuaDetail -> DuaDetailScreen(
                        viewModel = viewModel,
                        duaId = destination.duaId
                    )
                    AppDestination.NightPrayer -> NightPrayerScreen(viewModel = viewModel)
                    AppDestination.Schedule -> ScheduleScreen(viewModel = viewModel)
                    AppDestination.BedtimeHabits -> BedtimeScreen(viewModel = viewModel)
                    AppDestination.Habits -> HabitsScreen(viewModel = viewModel)
                    AppDestination.Routines -> RoutineScreen(viewModel = viewModel)
                    is AppDestination.RoutinePlayer -> RoutinePlayerScreen(
                        viewModel = viewModel,
                        routineId = destination.routineId
                    )
                    AppDestination.Hadiths -> HadithScreen(viewModel = viewModel)
                    AppDestination.Favorites -> FavoritesScreen(viewModel = viewModel)
                    AppDestination.History -> HistoryScreen(viewModel = viewModel)
                    AppDestination.Statistics -> StatsScreen(viewModel = viewModel)
                    AppDestination.CalendarView -> HistoryScreen(viewModel = viewModel)
                    AppDestination.Search -> SearchScreen(viewModel = viewModel)
                    AppDestination.Settings -> SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopHeaderBar(
    destination: AppDestination,
    onBack: () -> Unit,
    onNavigateSearch: () -> Unit,
    onNavigateFavorites: () -> Unit,
    onNavigateSettings: () -> Unit,
    onNavigateStats: () -> Unit,
    onNavigateHistory: () -> Unit
) {
    val isHome = destination == AppDestination.Home
    val title = when (destination) {
        AppDestination.Home -> "وِردي"
        AppDestination.Dhikr -> "الأذكار والتسبيحات"
        AppDestination.Quran -> "القرآن الكريم"
        is AppDestination.QuranReader -> "تلاوة القرآن"
        AppDestination.Ziyarat -> "الزيارات المأثورة"
        is AppDestination.ZiyaratDetail -> "الزيارة"
        AppDestination.Duas -> "الأدعية والمناجاة"
        is AppDestination.DuaDetail -> "الدعاء"
        AppDestination.NightPrayer -> "صلاة الليل"
        AppDestination.Schedule -> "جدول العبادات"
        AppDestination.BedtimeHabits -> "عادات وحصن النوم"
        AppDestination.Habits -> "عاداتي ومحاسبتي"
        AppDestination.Routines -> "روتيني الخاص"
        is AppDestination.RoutinePlayer -> "مشغل الورد"
        AppDestination.Hadiths -> "الأحاديث والفضائل"
        AppDestination.Favorites -> "المفضلة"
        AppDestination.History -> "سجل العبادات"
        AppDestination.CalendarView -> "التقويم العبادي"
        AppDestination.Statistics -> "الإحصائيات والتقدم"
        AppDestination.Search -> "البحث الشامل"
        AppDestination.Settings -> "الإعدادات"
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Emerald900,
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Emerald900,
                            Emerald800
                        )
                    )
                )
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Leading Section: Back Button (if not Home) or Logo/App Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isHome) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Emerald700.copy(alpha = 0.6f))
                                .testTag("nav_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = GoldLight
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = if (isHome) 22.sp else 19.sp
                            ),
                            color = Color.White
                        )
                        if (isHome) {
                            Text(
                                text = "تطبيق العبادات والأذكار الشخصي",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp
                                ),
                                color = GoldLight.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // Trailing Action Buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = onNavigateSearch,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Emerald700.copy(alpha = 0.5f))
                            .testTag("top_search_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "بحث",
                            tint = GoldLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onNavigateFavorites,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Emerald700.copy(alpha = 0.5f))
                            .testTag("top_favorites_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = "المفضلة",
                            tint = GoldLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onNavigateStats,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Emerald700.copy(alpha = 0.5f))
                            .testTag("top_stats_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.BarChart,
                            contentDescription = "الإحصائيات",
                            tint = GoldLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onNavigateSettings,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Emerald700.copy(alpha = 0.5f))
                            .testTag("top_settings_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "الإعدادات",
                            tint = GoldLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

data class NavItem(
    val destination: AppDestination,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun GeometricBottomNavigation(
    currentDestination: AppDestination,
    onSelect: (AppDestination) -> Unit
) {
    val items = listOf(
        NavItem(AppDestination.Home, "الرئيسية", Icons.Filled.Home, Icons.Outlined.Home),
        NavItem(AppDestination.Dhikr, "الأذكار", Icons.Filled.Circle, Icons.Outlined.Circle),
        NavItem(AppDestination.Quran, "القرآن", Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
        NavItem(AppDestination.Ziyarat, "الزيارات", Icons.Filled.Mosque, Icons.Outlined.Mosque),
        NavItem(AppDestination.Duas, "الأدعية", Icons.Filled.VolunteerActivism, Icons.Outlined.VolunteerActivism)
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = Emerald900,
        shadowElevation = 8.dp
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            modifier = Modifier.height(72.dp)
        ) {
            items.forEach { item ->
                val isSelected = currentDestination == item.destination
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onSelect(item.destination) },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Emerald900,
                        unselectedIconColor = TextLightSecondary,
                        selectedTextColor = GoldLight,
                        unselectedTextColor = TextLightSecondary.copy(alpha = 0.7f),
                        indicatorColor = GoldBase
                    ),
                    modifier = Modifier.testTag("nav_item_${item.title}")
                )
            }
        }
    }
}

