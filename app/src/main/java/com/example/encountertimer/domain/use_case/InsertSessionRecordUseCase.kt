package com.example.encountertimer.domain.use_case

import com.example.encountertimer.domain.model.SessionRecord
import com.example.encountertimer.domain.repository.CampaignRepository
import com.example.encountertimer.domain.repository.SessionsRepository
import javax.inject.Inject

class InsertSessionRecordUseCase @Inject constructor(
	private val repository: SessionsRepository
) {
	constructor(repository: CampaignRepository) : this(repository as SessionsRepository)

	suspend operator fun invoke(session: SessionRecord) {
		repository.insertSessionRecord(session)
	}
}

