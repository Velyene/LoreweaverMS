package com.example.encountertimer.domain.use_case

import com.example.encountertimer.domain.model.Encounter
import com.example.encountertimer.domain.repository.EncountersRepository
import javax.inject.Inject

class AddEncounterUseCase @Inject constructor(
	private val repository: EncountersRepository
) {
	/**
	 * Creates a new encounter and returns its ID.
	 * @return The ID of the created encounter
	 */
	suspend operator fun invoke(campaignId: String, name: String): String {
		val encounter = Encounter(campaignId = campaignId, name = name)
		repository.insertEncounter(encounter)
		return encounter.id
	}
}

