package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.repository.CampaignRepository
import io.github.velyene.loreweaver.domain.repository.EncountersRepository
import io.github.velyene.loreweaver.domain.repository.SessionsRepository
import io.github.velyene.loreweaver.domain.util.Resource
import javax.inject.Inject

const val NO_ACTIVE_ENCOUNTER_MESSAGE = "No active encounter found"

class GetActiveEncounterUseCase @Inject constructor(
	private val encountersRepository: EncountersRepository,
	private val sessionsRepository: SessionsRepository
) {
	constructor(repository: CampaignRepository) : this(
		encountersRepository = repository,
		sessionsRepository = repository
	)

	suspend operator fun invoke(): Resource<Pair<Encounter, SessionRecord?>> {
		return try {
			val encounter = encountersRepository.getActiveEncounter()
			if (encounter != null) {
				val session = sessionsRepository.getRecentSession()
				Resource.Success(Pair(encounter, session))
			} else {
				Resource.Error(NO_ACTIVE_ENCOUNTER_MESSAGE)
			}
		} catch (e: Exception) {
			Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
		}
	}
}
