package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.repository.EncountersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEncountersForCampaignUseCase @Inject constructor(
	private val repository: EncountersRepository
) {
	operator fun invoke(campaignId: String): Flow<List<Encounter>> {
		return repository.getEncountersForCampaign(campaignId)
	}
}
