package com.example.encountertimer.domain.repository

import com.example.encountertimer.domain.model.SessionRecord
import kotlinx.coroutines.flow.Flow

interface SessionsRepository {
	fun getSessionsForEncounter(encounterId: String): Flow<List<SessionRecord>>
	fun getAllSessions(): Flow<List<SessionRecord>>
	suspend fun insertSessionRecord(session: SessionRecord)
	suspend fun getRecentSession(): SessionRecord?
}

