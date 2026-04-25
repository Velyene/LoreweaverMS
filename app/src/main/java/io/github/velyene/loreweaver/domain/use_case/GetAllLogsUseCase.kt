package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.LogEntry
import io.github.velyene.loreweaver.domain.repository.CampaignRepository
import io.github.velyene.loreweaver.domain.repository.LogsRepository
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
