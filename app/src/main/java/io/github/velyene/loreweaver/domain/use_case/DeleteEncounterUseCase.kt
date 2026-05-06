package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.repository.EncountersRepository
import javax.inject.Inject

class DeleteEncounterUseCase @Inject constructor(
	private val repository: EncountersRepository
) {
	suspend operator fun invoke(encounter: Encounter) {
		repository.deleteEncounter(encounter)
	}
}

