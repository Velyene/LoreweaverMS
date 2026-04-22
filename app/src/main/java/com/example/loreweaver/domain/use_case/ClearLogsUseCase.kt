package com.example.loreweaver.domain.use_case

import com.example.loreweaver.domain.repository.CampaignRepository
import com.example.loreweaver.domain.repository.LogsRepository
import javax.inject.Inject

class ClearLogsUseCase @Inject constructor(
	private val repository: LogsRepository
) {
	constructor(repository: CampaignRepository) : this(repository as LogsRepository)

	suspend operator fun invoke() {
		repository.clearLogs()
	}
}
