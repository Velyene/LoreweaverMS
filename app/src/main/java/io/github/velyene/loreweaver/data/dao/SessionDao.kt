package io.github.velyene.loreweaver.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.velyene.loreweaver.data.entities.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
	@Query("SELECT * FROM session_records WHERE encounterId = :encounterId ORDER BY date DESC")
	fun getSessionsForEncounter(encounterId: String): Flow<List<SessionEntity>>

	@Query("SELECT * FROM session_records ORDER BY date DESC")
	fun getAllSessions(): Flow<List<SessionEntity>>

	@Query("SELECT * FROM session_records ORDER BY date DESC LIMIT 1")
	suspend fun getRecentSession(): SessionEntity?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertSession(session: SessionEntity): Long

	@Delete
	suspend fun deleteSession(session: SessionEntity): Int
}
