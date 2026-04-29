package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.repository.SessionsRepository
import javax.inject.Inject

class GetRecentSessionUseCase @Inject constructor(
	private val repository: SessionsRepository
) {
	suspend operator fun invoke(): SessionRecord? {
		return repository.getRecentSession()
	}
}

