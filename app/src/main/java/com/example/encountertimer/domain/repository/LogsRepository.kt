package com.example.encountertimer.domain.repository

import com.example.encountertimer.domain.model.LogEntry
import kotlinx.coroutines.flow.Flow

interface LogsRepository {
	fun getAllLogs(): Flow<List<LogEntry>>
	suspend fun insertLog(log: LogEntry)
	suspend fun clearLogs()
}

