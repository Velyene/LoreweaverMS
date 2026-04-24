package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.repository.CampaignRepository
import io.github.velyene.loreweaver.domain.repository.EncountersRepository
import io.github.velyene.loreweaver.domain.repository.SessionsRepository
import io.github.velyene.loreweaver.domain.util.Resource
import javax.inject.Inject

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
				Resource.Error("No active encounter found")
			}
		} catch (e: Exception) {
			Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
		}
	}
}
