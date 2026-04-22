package com.example.encountertimer.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.encountertimer.data.entities.EncounterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EncounterDao {
	@Query("SELECT * FROM encounters WHERE campaignId = :campaignId ORDER BY createdAt DESC")
	fun getEncountersForCampaign(campaignId: String): Flow<List<EncounterEntity>>

	@Query("SELECT * FROM encounters WHERE id = :encounterId")
	suspend fun getEncounterById(encounterId: String): EncounterEntity?

	@Query("SELECT * FROM encounters WHERE isActive = 1 LIMIT 1")
	suspend fun getActiveEncounter(): EncounterEntity?

	@Query("UPDATE encounters SET isActive = 0")
	suspend fun clearActiveEncounters(): Int

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertEncounter(encounter: EncounterEntity): Long

	@Update
	suspend fun updateEncounter(encounter: EncounterEntity): Int

	@Delete
	suspend fun deleteEncounter(encounter: EncounterEntity): Int
}
