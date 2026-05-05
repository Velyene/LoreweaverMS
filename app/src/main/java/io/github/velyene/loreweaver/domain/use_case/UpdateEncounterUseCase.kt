package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.repository.EncountersRepository
import javax.inject.Inject

class UpdateEncounterUseCase @Inject constructor(
	private val repository: EncountersRepository
) {
	suspend operator fun invoke(encounter: Encounter) {
		require(encounter.name.isNotBlank()) { ValidationMessages.ENCOUNTER_NAME_EMPTY_MESSAGE }
		repository.updateEncounter(encounter)
	}
}

