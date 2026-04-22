package com.example.encountertimer.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.encountertimer.data.entities.LogEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
	@Query("SELECT * FROM adventure_logs ORDER BY timestamp DESC")
	fun getAllLogs(): Flow<List<LogEntryEntity>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertLog(log: LogEntryEntity): Long

	@Query("DELETE FROM adventure_logs")
	suspend fun clearLogs(): Int

	@Query("SELECT COUNT(*) FROM adventure_logs")
	suspend fun getLogCount(): Int

	@Query(
		"DELETE FROM adventure_logs WHERE id IN " +
			"(SELECT id FROM adventure_logs ORDER BY timestamp ASC LIMIT :amount)"
	)
	suspend fun deleteOldestLogs(amount: Int): Int
}
