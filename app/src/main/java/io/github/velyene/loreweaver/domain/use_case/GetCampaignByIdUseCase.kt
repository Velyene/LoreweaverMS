package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.repository.CampaignsRepository
import javax.inject.Inject

class GetCampaignByIdUseCase @Inject constructor(
	private val repository: CampaignsRepository
) {
	suspend operator fun invoke(id: String): Campaign? {
		return repository.getCampaignById(id)
	}
}
