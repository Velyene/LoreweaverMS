package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.repository.CampaignRepository
import io.github.velyene.loreweaver.domain.repository.SessionsRepository
import javax.inject.Inject

class InsertSessionRecordUseCase @Inject constructor(
	private val repository: SessionsRepository
) {
	constructor(repository: CampaignRepository) : this(repository as SessionsRepository)

	suspend operator fun invoke(session: SessionRecord) {
		repository.insertSessionRecord(session)
	}
}
