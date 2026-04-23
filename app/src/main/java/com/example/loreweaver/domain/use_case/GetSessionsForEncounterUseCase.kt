package com.example.loreweaver.domain.use_case

import com.example.loreweaver.domain.model.SessionRecord
import com.example.loreweaver.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSessionsForEncounterUseCase @Inject constructor(
	private val repository: SessionsRepository
) {
	operator fun invoke(encounterId: String): Flow<List<SessionRecord>> {
		return repository.getSessionsForEncounter(encounterId)
	}
}
