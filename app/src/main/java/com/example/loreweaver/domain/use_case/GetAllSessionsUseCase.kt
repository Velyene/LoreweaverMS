package com.example.loreweaver.domain.use_case

import com.example.loreweaver.domain.model.SessionRecord
import com.example.loreweaver.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSessionsUseCase @Inject constructor(
	private val repository: SessionsRepository
) {
	operator fun invoke(): Flow<List<SessionRecord>> {
		return repository.getAllSessions()
	}
}
