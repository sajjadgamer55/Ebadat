package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.*
import com.example.data.repository.RoutineStep
import com.example.data.repository.RoutineWithSteps
import com.example.data.repository.WorshipRepository
import com.example.data.staticdata.*
import com.example.util.VibratorHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

sealed class AppDestination {
    object Home : AppDestination()
    object Dhikr : AppDestination()
    object Quran : AppDestination()
    data class QuranReader(val surahId: String) : AppDestination()
    object Ziyarat : AppDestination()
    data class ZiyaratDetail(val ziyarahId: String) : AppDestination()
    object Duas : AppDestination()
    data class DuaDetail(val duaId: String) : AppDestination()
    object NightPrayer : AppDestination()
    object Schedule : AppDestination()
    object BedtimeHabits : AppDestination()
    object Habits : AppDestination()
    object Routines : AppDestination()
    data class RoutinePlayer(val routineId: String) : AppDestination()
    object Hadiths : AppDestination()
    object Favorites : AppDestination()
    object History : AppDestination()
    object CalendarView : AppDestination()
    object Statistics : AppDestination()
    object Search : AppDestination()
    object Settings : AppDestination()
}

data class SearchResultItem(
    val title: String,
    val subtitle: String,
    val category: String,
    val destination: AppDestination
)

class WorshipViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WorshipRepository(application)

    // Navigation State
    private val _currentDestination = MutableStateFlow<AppDestination>(AppDestination.Home)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    private val _navigationHistory = mutableListOf<AppDestination>()

    // Global App Settings
    val settings: StateFlow<AppSettingsEntity> = repository.getSettingsFlow()
        .map { it ?: AppSettingsEntity() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AppSettingsEntity()
        )

    // Dhikr State
    val dhikrs: StateFlow<List<DhikrEntity>> = repository.getAllDhikrFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDhikrCategory = MutableStateFlow("الكل")
    val selectedDhikrCategory: StateFlow<String> = _selectedDhikrCategory.asStateFlow()

    // Habits State
    val habits: StateFlow<List<HabitEntity>> = repository.getAllHabitsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Routines State
    val routines: StateFlow<List<RoutineEntity>> = repository.getAllRoutinesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Bedtime Record
    val bedtimeRecord: StateFlow<BedtimeRecordEntity?> = repository.getBedtimeRecordFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Today's Log & Logs history
    val todayLog: StateFlow<DailyLogEntity?> = repository.getTodayLogFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allLogs: StateFlow<List<DailyLogEntity>> = repository.getAllLogsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Favorites
    val favorites: StateFlow<List<FavoriteEntity>> = repository.getAllFavoritesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Ziyarat Ashura Dedicated Independent 100-Counters
    private val _ashuraLaanCount = MutableStateFlow(0)
    val ashuraLaanCount: StateFlow<Int> = _ashuraLaanCount.asStateFlow()

    private val _ashuraSalaamCount = MutableStateFlow(0)
    val ashuraSalaamCount: StateFlow<Int> = _ashuraSalaamCount.asStateFlow()

    // Routine Player State
    private val _activeRoutine = MutableStateFlow<RoutineWithSteps?>(null)
    val activeRoutine: StateFlow<RoutineWithSteps?> = _activeRoutine.asStateFlow()

    private val _currentRoutineStepIndex = MutableStateFlow(0)
    val currentRoutineStepIndex: StateFlow<Int> = _currentRoutineStepIndex.asStateFlow()

    private val _currentRoutineStepCount = MutableStateFlow(0)
    val currentRoutineStepCount: StateFlow<Int> = _currentRoutineStepCount.asStateFlow()

    private val _isRoutinePaused = MutableStateFlow(false)
    val isRoutinePaused: StateFlow<Boolean> = _isRoutinePaused.asStateFlow()

    private val _isRoutineCompleted = MutableStateFlow(false)
    val isRoutineCompleted: StateFlow<Boolean> = _isRoutineCompleted.asStateFlow()

    // Global Search Query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Reader Font Size Delta
    private val _readerFontSize = MutableStateFlow(20)
    val readerFontSize: StateFlow<Int> = _readerFontSize.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureInitialized()
        }
    }

    // --- Navigation ---
    fun navigateTo(destination: AppDestination) {
        if (_currentDestination.value != destination) {
            _navigationHistory.add(_currentDestination.value)
            _currentDestination.value = destination
        }
    }

    fun navigateBack(): Boolean {
        if (_navigationHistory.isNotEmpty()) {
            _currentDestination.value = _navigationHistory.removeAt(_navigationHistory.size - 1)
            return true
        }
        if (_currentDestination.value != AppDestination.Home) {
            _currentDestination.value = AppDestination.Home
            return true
        }
        return false
    }

    // --- Dhikr Actions ---
    fun incrementDhikr(id: String) {
        viewModelScope.launch {
            if (settings.value.vibrateOnTap) {
                VibratorHelper.vibrate(getApplication(), 35)
            }
            val updated = repository.incrementDhikr(id)
            if (settings.value.autoAdvanceDhikr && updated != null && updated.isCompleted) {
                // Find next dhikr in list
                val list = dhikrs.value
                val currentIndex = list.indexOfFirst { it.id == id }
                if (currentIndex >= 0 && currentIndex < list.size - 1) {
                    // Signal or auto-highlight next
                }
            }
        }
    }

    fun decrementDhikr(id: String) {
        viewModelScope.launch {
            repository.decrementDhikr(id)
        }
    }

    fun resetDhikr(id: String) {
        viewModelScope.launch {
            repository.resetDhikr(id)
        }
    }

    fun resetAllDailyDhikrs() {
        viewModelScope.launch {
            repository.resetAllDailyDhikrs()
        }
    }

    fun setDhikrCategory(category: String) {
        _selectedDhikrCategory.value = category
    }

    fun addCustomDhikr(title: String, targetCount: Int, category: String, instruction: String) {
        viewModelScope.launch {
            repository.addCustomDhikr(title, targetCount, category, instruction)
        }
    }

    fun deleteDhikr(dhikr: DhikrEntity) {
        viewModelScope.launch {
            repository.deleteDhikr(dhikr)
        }
    }

    // --- Ziyarat Ashura 100 Counters ---
    fun incrementAshuraLaan() {
        if (_ashuraLaanCount.value < 100) {
            if (settings.value.vibrateOnTap) VibratorHelper.vibrate(getApplication(), 35)
            _ashuraLaanCount.value += 1
        }
    }

    fun decrementAshuraLaan() {
        if (_ashuraLaanCount.value > 0) {
            _ashuraLaanCount.value -= 1
        }
    }

    fun resetAshuraLaan() {
        _ashuraLaanCount.value = 0
    }

    fun incrementAshuraSalaam() {
        if (_ashuraSalaamCount.value < 100) {
            if (settings.value.vibrateOnTap) VibratorHelper.vibrate(getApplication(), 35)
            _ashuraSalaamCount.value += 1
        }
    }

    fun decrementAshuraSalaam() {
        if (_ashuraSalaamCount.value > 0) {
            _ashuraSalaamCount.value -= 1
        }
    }

    fun resetAshuraSalaam() {
        _ashuraSalaamCount.value = 0
    }

    // --- Habits ---
    fun toggleHabit(id: String) {
        viewModelScope.launch {
            if (settings.value.vibrateOnTap) VibratorHelper.vibrate(getApplication(), 35)
            repository.toggleHabitCompleted(id)
        }
    }

    fun incrementHabit(id: String) {
        viewModelScope.launch {
            if (settings.value.vibrateOnTap) VibratorHelper.vibrate(getApplication(), 35)
            repository.incrementHabit(id)
        }
    }

    fun addHabit(title: String, count: Int, time: String, reminder: Boolean, reminderTime: String) {
        viewModelScope.launch {
            repository.addHabit(title, count, time, reminder, reminderTime)
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    // --- Bedtime Checklist ---
    fun incrementBedtimeAyatKursi() {
        viewModelScope.launch {
            if (settings.value.vibrateOnTap) VibratorHelper.vibrate(getApplication(), 35)
            val current = bedtimeRecord.value
            val newCount = (current?.ayatKursiCount ?: 0) + 1
            repository.updateBedtimeRecord(
                ayatKursiCount = newCount,
                yasinRead = current?.yasinRead ?: false,
                habit1 = current?.habit1Done ?: false,
                habit2 = current?.habit2Done ?: false,
                habit3 = current?.habit3Done ?: false
            )
        }
    }

    fun toggleBedtimeYasin() {
        viewModelScope.launch {
            val current = bedtimeRecord.value
            val newStatus = !(current?.yasinRead ?: false)
            repository.updateBedtimeRecord(
                ayatKursiCount = current?.ayatKursiCount ?: 0,
                yasinRead = newStatus,
                habit1 = current?.habit1Done ?: false,
                habit2 = current?.habit2Done ?: false,
                habit3 = current?.habit3Done ?: false
            )
        }
    }

    fun toggleBedtimeHabit1() {
        viewModelScope.launch {
            val current = bedtimeRecord.value
            repository.updateBedtimeRecord(
                ayatKursiCount = current?.ayatKursiCount ?: 0,
                yasinRead = current?.yasinRead ?: false,
                habit1 = !(current?.habit1Done ?: false),
                habit2 = current?.habit2Done ?: false,
                habit3 = current?.habit3Done ?: false
            )
        }
    }

    fun toggleBedtimeHabit2() {
        viewModelScope.launch {
            val current = bedtimeRecord.value
            repository.updateBedtimeRecord(
                ayatKursiCount = current?.ayatKursiCount ?: 0,
                yasinRead = current?.yasinRead ?: false,
                habit1 = current?.habit1Done ?: false,
                habit2 = !(current?.habit2Done ?: false),
                habit3 = current?.habit3Done ?: false
            )
        }
    }

    fun toggleBedtimeHabit3() {
        viewModelScope.launch {
            val current = bedtimeRecord.value
            repository.updateBedtimeRecord(
                ayatKursiCount = current?.ayatKursiCount ?: 0,
                yasinRead = current?.yasinRead ?: false,
                habit1 = current?.habit1Done ?: false,
                habit2 = current?.habit2Done ?: false,
                habit3 = !(current?.habit3Done ?: false)
            )
        }
    }

    // --- Night Prayer ---
    fun toggleNightPrayer() {
        viewModelScope.launch {
            if (settings.value.vibrateOnTap) VibratorHelper.vibrate(getApplication(), 40)
            repository.toggleNightPrayer()
        }
    }

    // --- Routine Player ---
    fun startRoutine(routineId: String) {
        viewModelScope.launch {
            val r = repository.getRoutineWithSteps(routineId)
            if (r != null && r.steps.isNotEmpty()) {
                _activeRoutine.value = r
                _currentRoutineStepIndex.value = 0
                _currentRoutineStepCount.value = 0
                _isRoutinePaused.value = false
                _isRoutineCompleted.value = false
                navigateTo(AppDestination.RoutinePlayer(routineId))
            }
        }
    }

    fun tapRoutineCount() {
        val routine = _activeRoutine.value ?: return
        if (_isRoutinePaused.value || _isRoutineCompleted.value) return
        val currentStep = routine.steps.getOrNull(_currentRoutineStepIndex.value) ?: return

        if (settings.value.vibrateOnTap) VibratorHelper.vibrate(getApplication(), 35)

        val nextCount = _currentRoutineStepCount.value + 1
        _currentRoutineStepCount.value = nextCount

        if (nextCount >= currentStep.count) {
            // Next step or finish
            if (_currentRoutineStepIndex.value < routine.steps.size - 1) {
                _currentRoutineStepIndex.value += 1
                _currentRoutineStepCount.value = 0
            } else {
                _isRoutineCompleted.value = true
            }
        }
    }

    fun togglePauseRoutine() {
        _isRoutinePaused.value = !_isRoutinePaused.value
    }

    fun skipRoutineStep() {
        val routine = _activeRoutine.value ?: return
        if (_currentRoutineStepIndex.value < routine.steps.size - 1) {
            _currentRoutineStepIndex.value += 1
            _currentRoutineStepCount.value = 0
        } else {
            _isRoutineCompleted.value = true
        }
    }

    fun resetRoutineStep() {
        _currentRoutineStepCount.value = 0
    }

    fun restartRoutine() {
        _currentRoutineStepIndex.value = 0
        _currentRoutineStepCount.value = 0
        _isRoutinePaused.value = false
        _isRoutineCompleted.value = false
    }

    // --- Favorites ---
    fun toggleFavorite(id: String, type: String, title: String, subtitle: String = "") {
        viewModelScope.launch {
            if (settings.value.vibrateOnTap) VibratorHelper.vibrate(getApplication(), 30)
            repository.toggleFavorite(id, type, title, subtitle)
        }
    }

    fun isFavorite(id: String): Flow<Boolean> = repository.isFavoriteFlow(id)

    // --- Reader Settings ---
    fun increaseFontSize() {
        if (_readerFontSize.value < 36) {
            _readerFontSize.value += 2
        }
    }

    fun decreaseFontSize() {
        if (_readerFontSize.value > 14) {
            _readerFontSize.value -= 2
        }
    }

    fun markItemRead(targetId: String, type: String) {
        viewModelScope.launch {
            repository.saveReadingProgress(
                targetId = targetId,
                type = type,
                scrollPosition = 0,
                isCompleted = true,
                fontSizeSp = _readerFontSize.value
            )
        }
    }

    // --- Settings & Preferences ---
    fun updateThemeMode(mode: String) {
        viewModelScope.launch {
            val s = settings.value.copy(themeMode = mode)
            repository.updateSettings(s)
        }
    }

    fun toggleVibration(enabled: Boolean) {
        viewModelScope.launch {
            val s = settings.value.copy(vibrateOnTap = enabled)
            repository.updateSettings(s)
        }
    }

    fun toggleAutoAdvance(enabled: Boolean) {
        viewModelScope.launch {
            val s = settings.value.copy(autoAdvanceDhikr = enabled)
            repository.updateSettings(s)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            val s = settings.value.copy(notificationEnabled = enabled)
            repository.updateSettings(s)
        }
    }

    fun updateReminderTimes(morning: String, sunset: String, bedtime: String) {
        viewModelScope.launch {
            val s = settings.value.copy(
                morningReminderTime = morning,
                sunsetReminderTime = sunset,
                bedtimeReminderTime = bedtime
            )
            repository.updateSettings(s)
        }
    }

    // --- Backup & Restore ---
    suspend fun exportDataJson(): String {
        return repository.exportDataJson()
    }

    suspend fun importDataJson(jsonString: String): Boolean {
        return repository.importDataJson(jsonString)
    }

    // --- Search ---
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    val searchResults: StateFlow<List<SearchResultItem>> = _searchQuery
        .map { query ->
            if (query.isBlank()) return@map emptyList<SearchResultItem>()
            val q = query.trim()
            val results = mutableListOf<SearchResultItem>()

            // Dhikrs
            dhikrs.value.forEach {
                if (it.title.contains(q, ignoreCase = true) || it.instruction.contains(q, ignoreCase = true)) {
                    results.add(
                        SearchResultItem(
                            title = it.title,
                            subtitle = if (it.isUnlimited) "عداد مفتوح — ${it.instruction}" else "العدد المطلوب: ${it.targetCount} مرة",
                            category = "الأذكار والتسبيحات",
                            destination = AppDestination.Dhikr
                        )
                    )
                }
            }

            // Quran
            QuranData.surahs.forEach {
                if (it.title.contains(q, ignoreCase = true) || it.description.contains(q, ignoreCase = true) || it.fullText.contains(q, ignoreCase = true)) {
                    results.add(
                        SearchResultItem(
                            title = it.title,
                            subtitle = it.description,
                            category = "القرآن الكريم",
                            destination = AppDestination.QuranReader(it.id)
                        )
                    )
                }
            }

            // Ziyarat
            ZiyaratData.items.forEach {
                if (it.title.contains(q, ignoreCase = true) || it.subtitle.contains(q, ignoreCase = true) || it.fullText.contains(q, ignoreCase = true)) {
                    results.add(
                        SearchResultItem(
                            title = it.title,
                            subtitle = it.subtitle,
                            category = "الزيارات",
                            destination = AppDestination.ZiyaratDetail(it.id)
                        )
                    )
                }
            }

            // Duas
            DuaData.items.forEach {
                if (it.title.contains(q, ignoreCase = true) || it.subtitle.contains(q, ignoreCase = true) || it.fullText.contains(q, ignoreCase = true)) {
                    results.add(
                        SearchResultItem(
                            title = it.title,
                            subtitle = it.subtitle,
                            category = it.category,
                            destination = AppDestination.DuaDetail(it.id)
                        )
                    )
                }
            }

            // Hadiths
            HadithData.items.forEach {
                if (it.title.contains(q, ignoreCase = true) || it.narrationText.contains(q, ignoreCase = true) || it.virtueBenefit.contains(q, ignoreCase = true)) {
                    results.add(
                        SearchResultItem(
                            title = it.title,
                            subtitle = it.practiceTiming,
                            category = "الأحاديث والفضائل",
                            destination = AppDestination.Hadiths
                        )
                    )
                }
            }

            // Habits
            habits.value.forEach {
                if (it.title.contains(q, ignoreCase = true)) {
                    results.add(
                        SearchResultItem(
                            title = it.title,
                            subtitle = "الوقت: ${it.timeOfDay} — العدد: ${it.targetCount}",
                            category = "عاداتي",
                            destination = AppDestination.Habits
                        )
                    )
                }
            }

            results
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
