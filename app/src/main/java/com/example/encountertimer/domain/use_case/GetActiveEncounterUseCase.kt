package com.example.encountertimer.domain.use_case

import com.example.encountertimer.domain.model.Encounter
import com.example.encountertimer.domain.model.SessionRecord
import com.example.encountertimer.domain.repository.CampaignRepository
import com.example.encountertimer.domain.repository.EncountersRepository
import com.example.encountertimer.domain.repository.SessionsRepository
import com.example.encountertimer.domain.util.Resource
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
