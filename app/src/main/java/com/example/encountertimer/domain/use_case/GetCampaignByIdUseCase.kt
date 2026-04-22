package com.example.encountertimer.domain.use_case

import com.example.encountertimer.domain.model.Campaign
import com.example.encountertimer.domain.repository.CampaignsRepository
import javax.inject.Inject

class GetCampaignByIdUseCase @Inject constructor(
	private val repository: CampaignsRepository
) {
	suspend operator fun invoke(id: String): Campaign? {
		return repository.getCampaignById(id)
	}
}
