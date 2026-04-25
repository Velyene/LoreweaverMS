package io.github.velyene.loreweaver.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.velyene.loreweaver.data.entities.CampaignEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CampaignDao {
	@Query("SELECT * FROM campaigns ORDER BY createdAt DESC")
	fun getAllCampaigns(): Flow<List<CampaignEntity>>

	@Query("SELECT * FROM campaigns WHERE id = :campaignId")
	suspend fun getCampaignById(campaignId: String): CampaignEntity?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertCampaign(campaign: CampaignEntity): Long

	@Delete
	suspend fun deleteCampaign(campaign: CampaignEntity): Int
}
