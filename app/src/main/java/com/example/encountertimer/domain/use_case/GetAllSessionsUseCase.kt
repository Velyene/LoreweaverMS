package com.example.encountertimer.domain.use_case

import com.example.encountertimer.domain.model.SessionRecord
import com.example.encountertimer.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSessionsUseCase @Inject constructor(
	private val repository: SessionsRepository
) {
	operator fun invoke(): Flow<List<SessionRecord>> {
		return repository.getAllSessions()
	}
}
