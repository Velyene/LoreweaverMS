package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.repository.CampaignRepository
import io.github.velyene.loreweaver.domain.repository.SessionsRepository
import javax.inject.Inject

class GetSessionByIdUseCase @Inject constructor(
	private val repository: SessionsRepository
) {
	constructor(repository: CampaignRepository) : this(repository as SessionsRepository)

	suspend operator fun invoke(sessionId: String): SessionRecord? {
		return repository.getSessionById(sessionId)
	}
}

