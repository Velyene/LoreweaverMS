package com.example.loreweaver.domain.use_case

import com.example.loreweaver.domain.model.Campaign
import com.example.loreweaver.domain.repository.CampaignsRepository
import javax.inject.Inject

class GetCampaignByIdUseCase @Inject constructor(
	private val repository: CampaignsRepository
) {
	suspend operator fun invoke(id: String): Campaign? {
		return repository.getCampaignById(id)
	}
}
