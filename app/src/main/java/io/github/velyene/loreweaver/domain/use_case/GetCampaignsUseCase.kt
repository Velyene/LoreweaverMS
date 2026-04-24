package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.repository.CampaignsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCampaignsUseCase @Inject constructor(
	private val repository: CampaignsRepository
) {
	operator fun invoke(): Flow<List<Campaign>> {
		return repository.getAllCampaigns()
	}
}
