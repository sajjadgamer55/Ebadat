package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.entity.*
import com.example.data.local.populateInitialData
import com.example.data.staticdata.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

data class RoutineStep(
    val title: String,
    val count: Int,
    val instruction: String
)

data class RoutineWithSteps(
    val id: String,
    val title: String,
    val description: String,
    val steps: List<RoutineStep>,
    val orderIndex: Int
)

class WorshipRepository(private val context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val dhikrDao = db.dhikrDao()
    private val habitDao = db.habitDao()
    private val routineDao = db.routineDao()
    private val dailyLogDao = db.dailyLogDao()
    private val favoriteDao = db.favoriteDao()
    private val readingProgressDao = db.readingProgressDao()
    private val bedtimeRecordDao = db.bedtimeRecordDao()
    private val appSettingsDao = db.appSettingsDao()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    fun getTodayDate(): String {
        return dateFormat.format(Date())
    }

    // --- Ensure database initialized ---
    suspend fun ensureInitialized() = withContext(Dispatchers.IO) {
        val existing = dhikrDao.getAllDhikrList()
        if (existing.isEmpty()) {
            populateInitialData(db)
        }
    }

    // --- Dhikr Operations ---
    fun getAllDhikrFlow(): Flow<List<DhikrEntity>> = dhikrDao.getAllDhikrFlow()

    suspend fun incrementDhikr(id: String, vibrate: Boolean = true): DhikrEntity? = withContext(Dispatchers.IO) {
        val item = dhikrDao.getDhikrById(id) ?: return@withContext null
        val newCount = item.currentCount + 1
        val isCompleted = if (item.isUnlimited) false else (newCount >= item.targetCount)
        val updated = item.copy(
            currentCount = newCount,
            isCompleted = isCompleted,
            lastUpdatedDate = getTodayDate()
        )
        dhikrDao.updateDhikr(updated)
        recordDhikrTapInDailyLog()
        return@withContext updated
    }

    suspend fun decrementDhikr(id: String): DhikrEntity? = withContext(Dispatchers.IO) {
        val item = dhikrDao.getDhikrById(id) ?: return@withContext null
        if (item.currentCount <= 0) return@withContext item
        val newCount = item.currentCount - 1
        val isCompleted = if (item.isUnlimited) false else (newCount >= item.targetCount)
        val updated = item.copy(
            currentCount = newCount,
            isCompleted = isCompleted,
            lastUpdatedDate = getTodayDate()
        )
        dhikrDao.updateDhikr(updated)
        return@withContext updated
    }

    suspend fun resetDhikr(id: String): DhikrEntity? = withContext(Dispatchers.IO) {
        val item = dhikrDao.getDhikrById(id) ?: return@withContext null
        val updated = item.copy(currentCount = 0, isCompleted = false)
        dhikrDao.updateDhikr(updated)
        return@withContext updated
    }

    suspend fun resetAllDailyDhikrs() = withContext(Dispatchers.IO) {
        dhikrDao.resetAllDailyCounts()
    }

    suspend fun addCustomDhikr(title: String, targetCount: Int, category: String, instruction: String) = withContext(Dispatchers.IO) {
        val id = "custom_dhikr_" + System.currentTimeMillis()
        val dhikr = DhikrEntity(
            id = id,
            title = title,
            targetCount = targetCount,
            currentCount = 0,
            isCompleted = false,
            category = category,
            instruction = instruction,
            isUnlimited = targetCount <= 0,
            orderIndex = 100
        )
        dhikrDao.insertDhikr(dhikr)
    }

    suspend fun deleteDhikr(dhikr: DhikrEntity) = withContext(Dispatchers.IO) {
        dhikrDao.deleteDhikr(dhikr)
    }

    // --- Habits Operations ---
    fun getAllHabitsFlow(): Flow<List<HabitEntity>> = habitDao.getAllHabitsFlow()

    suspend fun toggleHabitCompleted(id: String) = withContext(Dispatchers.IO) {
        val list = habitDao.getAllHabitsList()
        val habit = list.find { it.id == id } ?: return@withContext
        val newStatus = !habit.isCompletedToday
        val newCount = if (newStatus) habit.targetCount else 0
        habitDao.updateHabit(habit.copy(isCompletedToday = newStatus, currentCount = newCount))
        updateDailyLogProgress()
    }

    suspend fun incrementHabit(id: String) = withContext(Dispatchers.IO) {
        val list = habitDao.getAllHabitsList()
        val habit = list.find { it.id == id } ?: return@withContext
        val newCount = habit.currentCount + 1
        val isCompleted = newCount >= habit.targetCount
        habitDao.updateHabit(habit.copy(currentCount = newCount, isCompletedToday = isCompleted))
        updateDailyLogProgress()
    }

    suspend fun addHabit(
        title: String,
        targetCount: Int,
        timeOfDay: String,
        reminderEnabled: Boolean,
        reminderTime: String
    ) = withContext(Dispatchers.IO) {
        val id = "habit_" + System.currentTimeMillis()
        val habit = HabitEntity(
            id = id,
            title = title,
            targetCount = targetCount,
            currentCount = 0,
            timeOfDay = timeOfDay,
            reminderEnabled = reminderEnabled,
            reminderTime = reminderTime,
            isCompletedToday = false,
            orderIndex = 50
        )
        habitDao.insertHabit(habit)
    }

    suspend fun updateHabit(habit: HabitEntity) = withContext(Dispatchers.IO) {
        habitDao.updateHabit(habit)
    }

    suspend fun deleteHabit(habit: HabitEntity) = withContext(Dispatchers.IO) {
        habitDao.deleteHabit(habit)
    }

    // --- Routines Operations ---
    fun getAllRoutinesFlow(): Flow<List<RoutineEntity>> = routineDao.getAllRoutinesFlow()

    suspend fun getRoutineWithSteps(id: String): RoutineWithSteps? = withContext(Dispatchers.IO) {
        val r = routineDao.getRoutineById(id) ?: return@withContext null
        val steps = parseRoutineSteps(r.stepsJson)
        RoutineWithSteps(
            id = r.id,
            title = r.title,
            description = r.description,
            steps = steps,
            orderIndex = r.orderIndex
        )
    }

    suspend fun saveRoutine(title: String, description: String, steps: List<RoutineStep>) = withContext(Dispatchers.IO) {
        val id = "routine_" + System.currentTimeMillis()
        val jsonArray = JSONArray()
        for (s in steps) {
            val obj = JSONObject()
            obj.put("title", s.title)
            obj.put("count", s.count)
            obj.put("instruction", s.instruction)
            jsonArray.put(obj)
        }
        val entity = RoutineEntity(
            id = id,
            title = title,
            description = description,
            stepsJson = jsonArray.toString(),
            orderIndex = 10
        )
        routineDao.insertRoutine(entity)
    }

    private fun parseRoutineSteps(json: String): List<RoutineStep> {
        val list = mutableListOf<RoutineStep>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    RoutineStep(
                        title = obj.optString("title", ""),
                        count = obj.optInt("count", 1),
                        instruction = obj.optString("instruction", "")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    // --- Bedtime Record Operations ---
    fun getBedtimeRecordFlow(date: String = getTodayDate()): Flow<BedtimeRecordEntity?> =
        bedtimeRecordDao.getRecordFlow(date)

    suspend fun updateBedtimeRecord(
        ayatKursiCount: Int,
        yasinRead: Boolean,
        habit1: Boolean,
        habit2: Boolean,
        habit3: Boolean
    ) = withContext(Dispatchers.IO) {
        val date = getTodayDate()
        val isAllDone = ayatKursiCount >= 5 && yasinRead
        val record = BedtimeRecordEntity(
            date = date,
            ayatKursiCount = ayatKursiCount,
            yasinRead = yasinRead,
            habit1Done = habit1,
            habit2Done = habit2,
            habit3Done = habit3,
            isAllDone = isAllDone
        )
        bedtimeRecordDao.saveRecord(record)
        updateDailyLogProgress()
    }

    // --- Night Prayer (صلاة الليل) ---
    fun getTodayLogFlow(): Flow<DailyLogEntity?> = dailyLogDao.getLogByDateFlow(getTodayDate())
    fun getAllLogsFlow(): Flow<List<DailyLogEntity>> = dailyLogDao.getAllLogsFlow()

    suspend fun toggleNightPrayer(date: String = getTodayDate()) = withContext(Dispatchers.IO) {
        val log = dailyLogDao.getLogByDate(date) ?: DailyLogEntity(date = date)
        val newNight = !log.nightPrayerCompleted
        val updated = log.copy(nightPrayerCompleted = newNight)
        dailyLogDao.insertLog(updated)
    }

    // --- Reading Progress & Bookmarks ---
    suspend fun getReadingProgress(targetId: String): ReadingProgressEntity? = withContext(Dispatchers.IO) {
        readingProgressDao.getProgress(targetId)
    }

    fun getReadingProgressFlow(targetId: String): Flow<ReadingProgressEntity?> =
        readingProgressDao.getProgressFlow(targetId)

    suspend fun saveReadingProgress(targetId: String, type: String, scrollPosition: Int, isCompleted: Boolean, fontSizeSp: Int) = withContext(Dispatchers.IO) {
        val entity = ReadingProgressEntity(
            targetId = targetId,
            type = type,
            scrollPosition = scrollPosition,
            isCompleted = isCompleted,
            fontSizeSp = fontSizeSp,
            lastReadDate = getTodayDate()
        )
        readingProgressDao.saveProgress(entity)
        if (type == "QURAN" && isCompleted) {
            markQuranDoneToday()
        } else if (type == "ZIYARAH" && isCompleted) {
            markZiyarahDoneToday()
        }
    }

    private suspend fun markQuranDoneToday() {
        val date = getTodayDate()
        val log = dailyLogDao.getLogByDate(date) ?: DailyLogEntity(date = date)
        dailyLogDao.insertLog(log.copy(quranCompleted = true))
    }

    private suspend fun markZiyarahDoneToday() {
        val date = getTodayDate()
        val log = dailyLogDao.getLogByDate(date) ?: DailyLogEntity(date = date)
        dailyLogDao.insertLog(log.copy(ziyarahCompleted = true))
    }

    private suspend fun recordDhikrTapInDailyLog() {
        val date = getTodayDate()
        val log = dailyLogDao.getLogByDate(date) ?: DailyLogEntity(date = date)
        val updated = log.copy(totalTasbihCount = log.totalTasbihCount + 1)
        dailyLogDao.insertLog(updated)
    }

    private suspend fun updateDailyLogProgress() {
        val date = getTodayDate()
        val dhikrs = dhikrDao.getAllDhikrList()
        val habits = habitDao.getAllHabitsList()
        val completedDhikrs = dhikrs.count { it.isCompleted }
        val completedHabits = habits.count { it.isCompletedToday }
        val total = dhikrs.size + habits.size
        val done = completedDhikrs + completedHabits

        val log = dailyLogDao.getLogByDate(date) ?: DailyLogEntity(date = date)
        dailyLogDao.insertLog(
            log.copy(
                completedCount = done,
                totalCount = if (total > 0) total else 10
            )
        )
    }

    // --- Favorites Operations ---
    fun getAllFavoritesFlow(): Flow<List<FavoriteEntity>> = favoriteDao.getAllFavoritesFlow()
    fun isFavoriteFlow(id: String): Flow<Boolean> = favoriteDao.isFavoriteFlow(id)

    suspend fun toggleFavorite(id: String, type: String, title: String, subtitle: String = "") = withContext(Dispatchers.IO) {
        val exists = favoriteDao.isFavorite(id)
        if (exists) {
            favoriteDao.deleteFavoriteById(id)
        } else {
            favoriteDao.insertFavorite(
                FavoriteEntity(
                    id = id,
                    type = type,
                    targetId = id,
                    title = title,
                    subtitle = subtitle
                )
            )
        }
    }

    // --- App Settings ---
    fun getSettingsFlow(): Flow<AppSettingsEntity?> = appSettingsDao.getSettingsFlow()

    suspend fun updateSettings(settings: AppSettingsEntity) = withContext(Dispatchers.IO) {
        appSettingsDao.saveSettings(settings)
    }

    // --- Backup & Restore (JSON Export & Import) ---
    suspend fun exportDataJson(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("version", 1)
        root.put("exportDate", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(Date()))

        // Dhikrs
        val dhikrs = dhikrDao.getAllDhikrList()
        val dhikrsArray = JSONArray()
        for (d in dhikrs) {
            val obj = JSONObject()
            obj.put("id", d.id)
            obj.put("title", d.title)
            obj.put("targetCount", d.targetCount)
            obj.put("currentCount", d.currentCount)
            obj.put("isCompleted", d.isCompleted)
            obj.put("category", d.category)
            obj.put("instruction", d.instruction)
            obj.put("isUnlimited", d.isUnlimited)
            obj.put("isFavorite", d.isFavorite)
            dhikrsArray.put(obj)
        }
        root.put("dhikrs", dhikrsArray)

        // Habits
        val habits = habitDao.getAllHabitsList()
        val habitsArray = JSONArray()
        for (h in habits) {
            val obj = JSONObject()
            obj.put("id", h.id)
            obj.put("title", h.title)
            obj.put("targetCount", h.targetCount)
            obj.put("currentCount", h.currentCount)
            obj.put("timeOfDay", h.timeOfDay)
            obj.put("daysOfWeek", h.daysOfWeek)
            obj.put("reminderEnabled", h.reminderEnabled)
            obj.put("reminderTime", h.reminderTime)
            obj.put("isCompletedToday", h.isCompletedToday)
            habitsArray.put(obj)
        }
        root.put("habits", habitsArray)

        // Settings
        val settings = appSettingsDao.getSettings()
        if (settings != null) {
            val sObj = JSONObject()
            sObj.put("themeMode", settings.themeMode)
            sObj.put("fontSizeDeltaSp", settings.fontSizeDeltaSp)
            sObj.put("vibrateOnTap", settings.vibrateOnTap)
            sObj.put("autoAdvanceDhikr", settings.autoAdvanceDhikr)
            sObj.put("morningReminderTime", settings.morningReminderTime)
            sObj.put("sunsetReminderTime", settings.sunsetReminderTime)
            sObj.put("bedtimeReminderTime", settings.bedtimeReminderTime)
            root.put("settings", sObj)
        }

        return@withContext root.toString(2)
    }

    suspend fun importDataJson(jsonString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            if (root.has("dhikrs")) {
                val array = root.getJSONArray("dhikrs")
                val list = mutableListOf<DhikrEntity>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        DhikrEntity(
                            id = obj.getString("id"),
                            title = obj.getString("title"),
                            targetCount = obj.getInt("targetCount"),
                            currentCount = obj.optInt("currentCount", 0),
                            isCompleted = obj.optBoolean("isCompleted", false),
                            category = obj.optString("category", "يومي"),
                            instruction = obj.optString("instruction", ""),
                            isUnlimited = obj.optBoolean("isUnlimited", false),
                            orderIndex = i,
                            isFavorite = obj.optBoolean("isFavorite", false)
                        )
                    )
                }
                if (list.isNotEmpty()) dhikrDao.insertDhikrs(list)
            }

            if (root.has("habits")) {
                val array = root.getJSONArray("habits")
                val list = mutableListOf<HabitEntity>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        HabitEntity(
                            id = obj.getString("id"),
                            title = obj.getString("title"),
                            targetCount = obj.getInt("targetCount"),
                            currentCount = obj.optInt("currentCount", 0),
                            timeOfDay = obj.optString("timeOfDay", "خلال اليوم"),
                            daysOfWeek = obj.optString("daysOfWeek", "1,2,3,4,5,6,7"),
                            reminderEnabled = obj.optBoolean("reminderEnabled", false),
                            reminderTime = obj.optString("reminderTime", "08:00"),
                            isCompletedToday = obj.optBoolean("isCompletedToday", false),
                            orderIndex = i
                        )
                    )
                }
                if (list.isNotEmpty()) habitDao.insertHabits(list)
            }
            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }
}
