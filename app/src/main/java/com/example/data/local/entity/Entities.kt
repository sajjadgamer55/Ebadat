package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dhikr_items")
data class DhikrEntity(
    @PrimaryKey val id: String,
    val title: String,
    val targetCount: Int,
    val currentCount: Int = 0,
    val isCompleted: Boolean = false,
    val category: String = "يومي",
    val instruction: String = "",
    val isUnlimited: Boolean = false,
    val orderIndex: Int = 0,
    val isFavorite: Boolean = false,
    val lastUpdatedDate: String = ""
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,
    val title: String,
    val targetCount: Int = 1,
    val currentCount: Int = 0,
    val timeOfDay: String = "خلال اليوم",
    val daysOfWeek: String = "1,2,3,4,5,6,7",
    val reminderEnabled: Boolean = false,
    val reminderTime: String = "08:00",
    val isCompletedToday: Boolean = false,
    val orderIndex: Int = 0
)

@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val stepsJson: String, // serialized routine items
    val orderIndex: Int = 0
)

@Entity(tableName = "daily_logs")
data class DailyLogEntity(
    @PrimaryKey val date: String, // "yyyy-MM-dd"
    val completedCount: Int = 0,
    val totalCount: Int = 10,
    val totalTasbihCount: Int = 0,
    val nightPrayerCompleted: Boolean = false,
    val bedtimeCompleted: Boolean = false,
    val quranCompleted: Boolean = false,
    val ziyarahCompleted: Boolean = false,
    val streakCount: Int = 1
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: String,
    val type: String, // DHIKR, QURAN, ZIYARAH, DUA, HADITH, HABIT
    val targetId: String,
    val title: String,
    val subtitle: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reading_progress")
data class ReadingProgressEntity(
    @PrimaryKey val targetId: String,
    val type: String, // QURAN, ZIYARAH, DUA
    val scrollPosition: Int = 0,
    val isCompleted: Boolean = false,
    val fontSizeSp: Int = 18,
    val lastReadDate: String = ""
)

@Entity(tableName = "bedtime_records")
data class BedtimeRecordEntity(
    @PrimaryKey val date: String,
    val ayatKursiCount: Int = 0,
    val yasinRead: Boolean = false,
    val habit1Done: Boolean = false,
    val habit2Done: Boolean = false,
    val habit3Done: Boolean = false,
    val isAllDone: Boolean = false
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val themeMode: String = "SYSTEM", // LIGHT, DARK, SYSTEM
    val fontSizeDeltaSp: Int = 0,
    val vibrateOnTap: Boolean = true,
    val autoAdvanceDhikr: Boolean = false,
    val notificationEnabled: Boolean = true,
    val morningReminderTime: String = "05:30",
    val sunsetReminderTime: String = "18:30",
    val bedtimeReminderTime: String = "22:00",
    val keepScreenAwake: Boolean = true
)
