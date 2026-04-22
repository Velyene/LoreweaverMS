package com.example.encountertimer.domain.use_case

import com.example.encountertimer.domain.repository.CampaignRepository
import com.example.encountertimer.domain.repository.LogsRepository
import javax.inject.Inject

class ClearLogsUseCase @Inject constructor(
	private val repository: LogsRepository
) {
	constructor(repository: CampaignRepository) : this(repository as LogsRepository)

	suspend operator fun invoke() {
		repository.clearLogs()
	}
}


