package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.repository.CampaignRepository
import io.github.velyene.loreweaver.domain.repository.EncountersRepository
import javax.inject.Inject

class GetEncounterByIdUseCase @Inject constructor(
	private val repository: EncountersRepository
) {
	constructor(repository: CampaignRepository) : this(repository as EncountersRepository)

	suspend operator fun invoke(encounterId: String): Encounter? {
		return repository.getEncounterById(encounterId)
	}
}
