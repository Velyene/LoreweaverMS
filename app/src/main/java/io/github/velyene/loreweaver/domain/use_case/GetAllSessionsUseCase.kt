package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSessionsUseCase @Inject constructor(
	private val repository: SessionsRepository
) {
	operator fun invoke(): Flow<List<SessionRecord>> {
		return repository.getAllSessions()
	}
}
