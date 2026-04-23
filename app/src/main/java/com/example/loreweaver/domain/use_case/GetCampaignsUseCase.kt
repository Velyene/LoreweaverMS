package com.example.loreweaver.domain.use_case

import com.example.loreweaver.domain.model.Campaign
import com.example.loreweaver.domain.repository.CampaignsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCampaignsUseCase @Inject constructor(
	private val repository: CampaignsRepository
) {
	operator fun invoke(): Flow<List<Campaign>> {
		return repository.getAllCampaigns()
	}
}
