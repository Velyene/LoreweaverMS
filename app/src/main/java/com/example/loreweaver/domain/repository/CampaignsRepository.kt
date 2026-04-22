package com.example.loreweaver.domain.repository

import com.example.loreweaver.domain.model.Campaign
import kotlinx.coroutines.flow.Flow

interface CampaignsRepository {
	fun getAllCampaigns(): Flow<List<Campaign>>
	suspend fun getCampaignById(id: String): Campaign?
	suspend fun insertCampaign(campaign: Campaign)
}
