package com.example.loreweaver.domain.use_case

import com.example.loreweaver.domain.model.Encounter
import com.example.loreweaver.domain.repository.EncountersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEncountersForCampaignUseCase @Inject constructor(
	private val repository: EncountersRepository
) {
	operator fun invoke(campaignId: String): Flow<List<Encounter>> {
		return repository.getEncountersForCampaign(campaignId)
	}
}
