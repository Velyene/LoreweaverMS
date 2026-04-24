package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.repository.EncountersRepository
import javax.inject.Inject


class AddEncounterUseCase @Inject constructor(
	private val repository: EncountersRepository
) {
	/**
	 * Creates a new encounter and returns its ID.
	 * @return The ID of the created encounter
	 */
	suspend operator fun invoke(campaignId: String, name: String): String {
		require(name.isNotBlank()) { ValidationMessages.ENCOUNTER_NAME_EMPTY_MESSAGE }
		val encounter = Encounter(campaignId = campaignId, name = name)
		repository.insertEncounter(encounter)
		return encounter.id
	}
}
