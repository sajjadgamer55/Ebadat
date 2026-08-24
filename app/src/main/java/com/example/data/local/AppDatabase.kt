package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.*
import com.example.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        DhikrEntity::class,
        HabitEntity::class,
        RoutineEntity::class,
        DailyLogEntity::class,
        FavoriteEntity::class,
        ReadingProgressEntity::class,
        BedtimeRecordEntity::class,
        AppSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dhikrDao(): DhikrDao
    abstract fun habitDao(): HabitDao
    abstract fun routineDao(): RoutineDao
    abstract fun dailyLogDao(): DailyLogDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun readingProgressDao(): ReadingProgressDao
    abstract fun bedtimeRecordDao(): BedtimeRecordDao
    abstract fun appSettingsDao(): AppSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wirdi_islamic_db"
                )
                .addCallback(DatabaseCallback(context.applicationContext))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val context: Context
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                populateInitialData(getDatabase(context))
            }
        }
    }
}

suspend fun populateInitialData(db: AppDatabase) {
    val dhikrDao = db.dhikrDao()
    val defaultDhikrs = listOf(
        DhikrEntity(
            id = "dhikr_salawat",
            title = "اللهم صل على محمد وآل محمد",
            targetCount = 100,
            currentCount = 0,
            isCompleted = false,
            category = "يومي",
            instruction = "أعظم الأذكار وبركة الأوقات وقضاء الحوائج",
            isUnlimited = false,
            orderIndex = 1
        ),
        DhikrEntity(
            id = "dhikr_istighfar_100",
            title = "أستغفر الله ربي وأتوب إليه",
            targetCount = 100,
            currentCount = 0,
            isCompleted = false,
            category = "يومي",
            instruction = "تفريج الهموم ومغفرة الذنوب وسعة الرزق",
            isUnlimited = false,
            orderIndex = 2
        ),
        DhikrEntity(
            id = "dhikr_tahlil_100",
            title = "لا إله إلا الله",
            targetCount = 100,
            currentCount = 0,
            isCompleted = false,
            category = "يومي",
            instruction = "أفضل الذكر وكلمة الإخلاص وتجديد الإيمان",
            isUnlimited = false,
            orderIndex = 3
        ),
        DhikrEntity(
            id = "dhikr_mashaallah",
            title = "ما شاء الله لا قوة إلا بالله",
            targetCount = 100,
            currentCount = 0,
            isCompleted = false,
            category = "يومي",
            instruction = "حفظ النعم ودفع العين والحسد والشرور",
            isUnlimited = false,
            orderIndex = 4
        ),
        DhikrEntity(
            id = "dhikr_malik_haqq",
            title = "لا إله إلا الله الملك الحق المبين",
            targetCount = 100,
            currentCount = 0,
            isCompleted = false,
            category = "يومي",
            instruction = "الأمان من الفقر وأنس وحشة القبر واستجلاب الغنى",
            isUnlimited = false,
            orderIndex = 5
        ),
        DhikrEntity(
            id = "dhikr_yunus",
            title = "لا إله إلا أنت سبحانك إني كنت من الظالمين",
            targetCount = 100,
            currentCount = 0,
            isCompleted = false,
            category = "يومي",
            instruction = "الذكر اليونسي لتفريج الكروب والنجاة من الغم",
            isUnlimited = false,
            orderIndex = 6
        ),
        DhikrEntity(
            id = "dhikr_tasbihat_arbaa",
            title = "سبحان الله والحمد لله ولا إله إلا الله والله أكبر",
            targetCount = 100,
            currentCount = 0,
            isCompleted = false,
            category = "يومي",
            instruction = "التسبيحات الأربع — الباقيات الصالحات وغراس الجنة",
            isUnlimited = false,
            orderIndex = 7
        ),
        DhikrEntity(
            id = "dhikr_istighfar_400",
            title = "استغفر الله الذي لا إله إلا هو وحده لا شريك له وأتوب إليه",
            targetCount = 400,
            currentCount = 0,
            isCompleted = false,
            category = "يومي",
            instruction = "ورد الاستغفار العظيم لفتح أبواب العلم والرزق",
            isUnlimited = false,
            orderIndex = 8
        ),
        DhikrEntity(
            id = "dhikr_ya_fattah_70",
            title = "يا فتاح",
            targetCount = 70,
            currentCount = 0,
            isCompleted = false,
            category = "صباحي",
            instruction = "بعد صلاة الصبح، ضع يدك اليمنى على قلبك.",
            isUnlimited = false,
            orderIndex = 9
        ),
        DhikrEntity(
            id = "dhikr_sunset_unlimited",
            title = "لا إله إلا الله، لا حول ولا قوة إلا بالله",
            targetCount = 0,
            currentCount = 0,
            isCompleted = false,
            category = "غروب",
            instruction = "يفضل وقت الغروب (عداد مفتوح وغير محدود)",
            isUnlimited = true,
            orderIndex = 10
        )
    )
    dhikrDao.insertDhikrs(defaultDhikrs)

    val habitDao = db.habitDao()
    val defaultHabits = listOf(
        HabitEntity(
            id = "habit_ayat_kursi_morning",
            title = "آية الكرسي بعد الاستيقاظ (٥ مرات)",
            targetCount = 5,
            currentCount = 0,
            timeOfDay = "الصباح",
            daysOfWeek = "1,2,3,4,5,6,7",
            reminderEnabled = true,
            reminderTime = "05:45",
            isCompletedToday = false,
            orderIndex = 1
        ),
        HabitEntity(
            id = "habit_ya_fattah",
            title = "يا فتاح ٧٠ مرة بعد صلاة الصبح باليد على القلب",
            targetCount = 70,
            currentCount = 0,
            timeOfDay = "الصباح",
            daysOfWeek = "1,2,3,4,5,6,7",
            reminderEnabled = true,
            reminderTime = "06:00",
            isCompletedToday = false,
            orderIndex = 2
        ),
        HabitEntity(
            id = "habit_ziyarat_ashura",
            title = "زيارة عاشوراء اليومية",
            targetCount = 1,
            currentCount = 0,
            timeOfDay = "خلال اليوم",
            daysOfWeek = "1,2,3,4,5,6,7",
            reminderEnabled = true,
            reminderTime = "09:00",
            isCompletedToday = false,
            orderIndex = 3
        ),
        HabitEntity(
            id = "habit_daily_quran",
            title = "تلاوة ورد القرآن لليوم",
            targetCount = 1,
            currentCount = 0,
            timeOfDay = "خلال اليوم",
            daysOfWeek = "1,2,3,4,5,6,7",
            reminderEnabled = true,
            reminderTime = "14:00",
            isCompletedToday = false,
            orderIndex = 4
        ),
        HabitEntity(
            id = "habit_sunset_dhikr",
            title = "أذكار وقت الغروب (لا إله إلا الله، لا حول ولا قوة إلا بالله)",
            targetCount = 100,
            currentCount = 0,
            timeOfDay = "المساء",
            daysOfWeek = "1,2,3,4,5,6,7",
            reminderEnabled = true,
            reminderTime = "18:15",
            isCompletedToday = false,
            orderIndex = 5
        ),
        HabitEntity(
            id = "habit_bedtime_routine",
            title = "عادات قبل النوم (آية الكرسي ٥ مرات + سورة يس)",
            targetCount = 1,
            currentCount = 0,
            timeOfDay = "قبل النوم",
            daysOfWeek = "1,2,3,4,5,6,7",
            reminderEnabled = true,
            reminderTime = "22:30",
            isCompletedToday = false,
            orderIndex = 6
        ),
        HabitEntity(
            id = "habit_night_prayer",
            title = "صلاة الليل والمناجاة في السحر",
            targetCount = 1,
            currentCount = 0,
            timeOfDay = "قبل النوم",
            daysOfWeek = "1,2,3,4,5,6,7",
            reminderEnabled = true,
            reminderTime = "04:30",
            isCompletedToday = false,
            orderIndex = 7
        )
    )
    habitDao.insertHabits(defaultHabits)

    val routineDao = db.routineDao()
    val defaultRoutines = listOf(
        RoutineEntity(
            id = "routine_morning",
            title = "روتيني الصباحي",
            description = "الورد المبارك بعد صلاة الفجر والاستيقاظ",
            stepsJson = """[
                {"title": "آية الكرسي", "count": 5, "instruction": "قراءة آية الكرسي ٥ مرات بنية الحفظ والبركة"},
                {"title": "يا فتاح", "count": 70, "instruction": "ضع يدك اليمنى على صدرك وقل يا فتاح ٧٠ مرة"},
                {"title": "أستغفر الله ربي وأتوب إليه", "count": 100, "instruction": "الاستغفار ١٠٠ مرة لتفريج الهم وتيسير الرزق"},
                {"title": "اللهم صل على محمد وآل محمد", "count": 100, "instruction": "الصلاة على النبي وآله ١٠٠ مرة"}
            ]""",
            orderIndex = 1
        ),
        RoutineEntity(
            id = "routine_evening",
            title = "روتين المساء والغروب",
            description = "التحصين والذكر وقت اصفرار الشمس والغروب",
            stepsJson = """[
                {"title": "لا إله إلا الله، لا حول ولا قوة إلا بالله", "count": 100, "instruction": "ذكر مبارك وقت الغروب لدفع البلاء والهم"},
                {"title": "لا إله إلا الله الملك الحق المبين", "count": 100, "instruction": "الأمان من الفقر واستجلاب الغنى"},
                {"title": "سبحان الله والحمد لله ولا إله إلا الله والله أكبر", "count": 100, "instruction": "الباقيات الصالحات وغراس الجنة"}
            ]""",
            orderIndex = 2
        ),
        RoutineEntity(
            id = "routine_bedtime",
            title = "روتين قبل النوم",
            description = "عادات النوم المباركة وحصن الليل",
            stepsJson = """[
                {"title": "آية الكرسي", "count": 5, "instruction": "٥ مرات للحفظ حتى تصبح"},
                {"title": "سورة يس", "count": 1, "instruction": "تلاوة سورة يس المباركة"},
                {"title": "تسبيح سيدة نساء العالمين", "count": 100, "instruction": "٣٤ الله أكبر، ٣٣ الحمد لله، ٣٣ سبحان الله"},
                {"title": "آية الكهف ١١٠", "count": 1, "instruction": "للاستيقاظ لصلاة الفجر وصلاة الليل"}
            ]""",
            orderIndex = 3
        )
    )
    routineDao.insertRoutines(defaultRoutines)

    val settingsDao = db.appSettingsDao()
    settingsDao.saveSettings(
        AppSettingsEntity(
            id = 1,
            themeMode = "SYSTEM",
            fontSizeDeltaSp = 0,
            vibrateOnTap = true,
            autoAdvanceDhikr = false,
            notificationEnabled = true,
            morningReminderTime = "05:30",
            sunsetReminderTime = "18:30",
            bedtimeReminderTime = "22:00",
            keepScreenAwake = true
        )
    )
}
