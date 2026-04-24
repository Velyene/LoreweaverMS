package io.github.velyene.loreweaver.domain.repository

import io.github.velyene.loreweaver.domain.model.LogEntry
import kotlinx.coroutines.flow.Flow

interface LogsRepository {
	fun getAllLogs(): Flow<List<LogEntry>>
	suspend fun insertLog(log: LogEntry)
	suspend fun clearLogs()
}
