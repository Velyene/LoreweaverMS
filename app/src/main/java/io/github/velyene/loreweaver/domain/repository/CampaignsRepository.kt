package io.github.velyene.loreweaver.domain.repository

import io.github.velyene.loreweaver.domain.model.Campaign
import kotlinx.coroutines.flow.Flow

interface CampaignsRepository {
	fun getAllCampaigns(): Flow<List<Campaign>>
	suspend fun getCampaignById(id: String): Campaign?
	suspend fun insertCampaign(campaign: Campaign)
	suspend fun updateCampaign(campaign: Campaign)
	suspend fun deleteCampaign(campaign: Campaign)
}
