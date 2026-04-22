package com.example.loreweaver.domain.use_case

import com.example.loreweaver.domain.repository.CampaignRepository
import com.example.loreweaver.domain.repository.EncountersRepository
import javax.inject.Inject

class SetActiveEncounterUseCase @Inject constructor(
	private val repository: EncountersRepository
) {
	constructor(repository: CampaignRepository) : this(repository as EncountersRepository)

	suspend operator fun invoke(encounterId: String) {
		repository.setActiveEncounter(encounterId)
	}
}
