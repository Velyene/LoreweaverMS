package com.example.loreweaver.domain.use_case

import com.example.loreweaver.domain.model.Encounter
import com.example.loreweaver.domain.repository.EncountersRepository
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
