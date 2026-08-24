package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DhikrDao {
    @Query("SELECT * FROM dhikr_items ORDER BY orderIndex ASC")
    fun getAllDhikrFlow(): Flow<List<DhikrEntity>>

    @Query("SELECT * FROM dhikr_items ORDER BY orderIndex ASC")
    suspend fun getAllDhikrList(): List<DhikrEntity>

    @Query("SELECT * FROM dhikr_items WHERE id = :id")
    suspend fun getDhikrById(id: String): DhikrEntity?

    @Query("SELECT * FROM dhikr_items WHERE isFavorite = 1")
    fun getFavoriteDhikrFlow(): Flow<List<DhikrEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDhikrs(items: List<DhikrEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDhikr(item: DhikrEntity)

    @Update
    suspend fun updateDhikr(item: DhikrEntity)

    @Delete
    suspend fun deleteDhikr(item: DhikrEntity)

    @Query("UPDATE dhikr_items SET currentCount = :count, isCompleted = :isCompleted, lastUpdatedDate = :date WHERE id = :id")
    suspend fun updateCount(id: String, count: Int, isCompleted: Boolean, date: String)

    @Query("UPDATE dhikr_items SET currentCount = 0, isCompleted = 0")
    suspend fun resetAllDailyCounts()
}

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY orderIndex ASC")
    fun getAllHabitsFlow(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits ORDER BY orderIndex ASC")
    suspend fun getAllHabitsList(): List<HabitEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabits(habits: List<HabitEntity>)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("UPDATE habits SET currentCount = 0, isCompletedToday = 0")
    suspend fun resetDailyHabits()
}

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routines ORDER BY orderIndex ASC")
    fun getAllRoutinesFlow(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routines WHERE id = :id")
    suspend fun getRoutineById(id: String): RoutineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutines(routines: List<RoutineEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity)

    @Update
    suspend fun updateRoutine(routine: RoutineEntity)

    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity)
}

@Dao
interface DailyLogDao {
    @Query("SELECT * FROM daily_logs ORDER BY date DESC")
    fun getAllLogsFlow(): Flow<List<DailyLogEntity>>

    @Query("SELECT * FROM daily_logs WHERE date = :date")
    suspend fun getLogByDate(date: String): DailyLogEntity?

    @Query("SELECT * FROM daily_logs WHERE date = :date")
    fun getLogByDateFlow(date: String): Flow<DailyLogEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: DailyLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<DailyLogEntity>)
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY timestamp DESC")
    fun getAllFavoritesFlow(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    fun isFavoriteFlow(id: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    suspend fun isFavorite(id: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(fav: FavoriteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorites(favs: List<FavoriteEntity>)

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteFavoriteById(id: String)
}

@Dao
interface ReadingProgressDao {
    @Query("SELECT * FROM reading_progress WHERE targetId = :targetId")
    suspend fun getProgress(targetId: String): ReadingProgressEntity?

    @Query("SELECT * FROM reading_progress WHERE targetId = :targetId")
    fun getProgressFlow(targetId: String): Flow<ReadingProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: ReadingProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAllProgress(list: List<ReadingProgressEntity>)
}

@Dao
interface BedtimeRecordDao {
    @Query("SELECT * FROM bedtime_records WHERE date = :date")
    fun getRecordFlow(date: String): Flow<BedtimeRecordEntity?>

    @Query("SELECT * FROM bedtime_records WHERE date = :date")
    suspend fun getRecord(date: String): BedtimeRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRecord(record: BedtimeRecordEntity)
}

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun getSettingsFlow(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 1")
    suspend fun getSettings(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AppSettingsEntity)
}
