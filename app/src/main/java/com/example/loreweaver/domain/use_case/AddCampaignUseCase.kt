package com.example.loreweaver.domain.use_case

import com.example.loreweaver.domain.model.Campaign
import com.example.loreweaver.domain.repository.CampaignsRepository
import javax.inject.Inject

class AddCampaignUseCase @Inject constructor(
	private val repository: CampaignsRepository
) {
	suspend operator fun invoke(name: String, description: String) {
		if (name.isBlank()) {
			throw IllegalArgumentException("Campaign name cannot be empty")
		}
		repository.insertCampaign(Campaign(title = name, description = description))
	}
}
