package com.example.encountertimer.domain.use_case

import com.example.encountertimer.domain.model.Campaign
import com.example.encountertimer.domain.repository.CampaignsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCampaignsUseCase @Inject constructor(
	private val repository: CampaignsRepository
) {
	operator fun invoke(): Flow<List<Campaign>> {
		return repository.getAllCampaigns()
	}
}
