package com.example.loreweaver.domain.repository

import com.example.loreweaver.domain.model.LogEntry
import kotlinx.coroutines.flow.Flow

interface LogsRepository {
	fun getAllLogs(): Flow<List<LogEntry>>
	suspend fun insertLog(log: LogEntry)
	suspend fun clearLogs()
}
