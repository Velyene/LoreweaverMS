package com.example.encountertimer.domain.use_case

import com.example.encountertimer.domain.model.Encounter
import com.example.encountertimer.domain.repository.EncountersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEncountersForCampaignUseCase @Inject constructor(
	private val repository: EncountersRepository
) {
	operator fun invoke(campaignId: String): Flow<List<Encounter>> {
		return repository.getEncountersForCampaign(campaignId)
	}
}
