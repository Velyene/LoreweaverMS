package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.repository.CampaignsRepository
import javax.inject.Inject


class AddCampaignUseCase @Inject constructor(
	private val repository: CampaignsRepository
) {
	suspend operator fun invoke(name: String, description: String) {
		require(name.isNotBlank()) { ValidationMessages.CAMPAIGN_NAME_EMPTY_MESSAGE }
		repository.insertCampaign(Campaign(title = name, description = description))
	}
}
