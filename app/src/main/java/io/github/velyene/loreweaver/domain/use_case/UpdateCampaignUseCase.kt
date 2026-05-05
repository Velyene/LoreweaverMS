package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.repository.CampaignsRepository
import javax.inject.Inject

class UpdateCampaignUseCase @Inject constructor(
	private val repository: CampaignsRepository
) {
	suspend operator fun invoke(campaign: Campaign) {
		require(campaign.title.isNotBlank()) { ValidationMessages.CAMPAIGN_NAME_EMPTY_MESSAGE }
		repository.updateCampaign(campaign)
	}
}

