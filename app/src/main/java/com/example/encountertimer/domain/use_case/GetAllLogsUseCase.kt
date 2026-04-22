package com.example.encountertimer.domain.use_case

import com.example.encountertimer.domain.model.LogEntry
import com.example.encountertimer.domain.repository.CampaignRepository
import com.example.encountertimer.domain.repository.LogsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllLogsUseCase @Inject constructor(
	private val repository: LogsRepository
) {
	constructor(repository: CampaignRepository) : this(repository as LogsRepository)

	operator fun invoke(): Flow<List<LogEntry>> {
		return repository.getAllLogs()
	}
}


