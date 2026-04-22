package com.example.encountertimer.data.repository

import com.example.encountertimer.data.dao.CampaignDao
import com.example.encountertimer.data.dao.CharacterDao
import com.example.encountertimer.data.dao.EncounterDao
import com.example.encountertimer.data.dao.LogDao
import com.example.encountertimer.data.dao.NoteDao
import com.example.encountertimer.data.dao.SessionDao
import com.example.encountertimer.data.mapper.toDomain
import com.example.encountertimer.data.mapper.toEntity
import com.example.encountertimer.data.repository.RepositoryConstants.MAX_LOG_ENTRIES
import com.example.encountertimer.domain.model.Campaign
import com.example.encountertimer.domain.model.CharacterEntry
import com.example.encountertimer.domain.model.CombatantState
import com.example.encountertimer.domain.model.Encounter
import com.example.encountertimer.domain.model.EncounterSnapshot
import com.example.encountertimer.domain.model.LogEntry
import com.example.encountertimer.domain.model.Note
import com.example.encountertimer.domain.model.SessionRecord
import com.example.encountertimer.domain.repository.CampaignRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CampaignRepositoryImpl(
	private val campaignDao: CampaignDao,
	private val encounterDao: EncounterDao,
	private val characterDao: CharacterDao,
	private val sessionDao: SessionDao,
	private val noteDao: NoteDao,
	private val logDao: LogDao
) : CampaignRepository {

	override fun getAllCampaigns(): Flow<List<Campaign>> =
		campaignDao.getAllCampaigns().map { entities -> entities.map { it.toDomain() } }

	override suspend fun getCampaignById(id: String): Campaign? =
		campaignDao.getCampaignById(id)?.toDomain()

	override suspend fun insertCampaign(campaign: Campaign) {
		campaignDao.insertCampaign(campaign.toEntity())
	}

	override fun getEncountersForCampaign(campaignId: String): Flow<List<Encounter>> =
		encounterDao.getEncountersForCampaign(campaignId)
			.map { entities -> entities.map { it.toDomain() } }

	override suspend fun insertEncounter(encounter: Encounter) {
		encounterDao.insertEncounter(encounter.toEntity())
	}

	override suspend fun getEncounterById(encounterId: String): Encounter? =
		encounterDao.getEncounterById(encounterId)?.toDomain()

	override suspend fun addCombatantsToEncounter(
		encounterId: String,
		combatants: List<CombatantState>
	) {
		// Sort combatants by initiative (descending order)
		val sortedCombatants = combatants.sortedByDescending { it.initiative }

		// Create an initial snapshot with the monsters
		val snapshot = EncounterSnapshot(
			combatants = sortedCombatants,
			currentTurnIndex = 0,
			currentRound = 1
		)

		// Create a session record to store the initial state
		val session = SessionRecord(
			encounterId = encounterId,
			title = "Initial Setup - ${combatants.size} monsters added",
			log = listOf("Encounter prepared with ${combatants.size} monsters"),
			snapshot = snapshot
		)

		// Insert the session
		sessionDao.insertSession(session.toEntity())
	}

	override fun getSessionsForEncounter(encounterId: String): Flow<List<SessionRecord>> =
		sessionDao.getSessionsForEncounter(encounterId)
			.map { entities -> entities.map { it.toDomain() } }

	override fun getAllSessions(): Flow<List<SessionRecord>> =
		sessionDao.getAllSessions().map { entities -> entities.map { it.toDomain() } }

	override suspend fun insertSessionRecord(session: SessionRecord) {
		sessionDao.insertSession(session.toEntity())
	}

	override fun getNotesForCampaign(campaignId: String): Flow<List<Note>> =
		noteDao.getNotesForCampaign(campaignId).map { entities -> entities.map { it.toDomain() } }

	override suspend fun insertNote(note: Note) {
		noteDao.insertNote(note.toEntity())
	}

	override suspend fun updateNote(note: Note) {
		noteDao.updateNote(note.toEntity())
	}

	override suspend fun deleteNote(note: Note) {
		noteDao.deleteNote(note.toEntity())
	}

	override fun getAllCharacters(): Flow<List<CharacterEntry>> =
		characterDao.getAllCharacters().map { entities -> entities.map { it.toDomain() } }

	override suspend fun getCharacterById(id: String): CharacterEntry? =
		characterDao.getCharacterById(id)?.toDomain()

	override suspend fun insertCharacter(character: CharacterEntry) {
		characterDao.insertCharacter(character.toEntity())
	}

	override suspend fun updateCharacter(character: CharacterEntry) {
		characterDao.updateCharacter(character.toEntity())
	}

	override suspend fun deleteCharacter(character: CharacterEntry) {
		characterDao.deleteCharacter(character.toEntity())
	}

	override suspend fun getActiveEncounter(): Encounter? =
		encounterDao.getActiveEncounter()?.toDomain()

	override suspend fun setActiveEncounter(encounterId: String) {
		encounterDao.clearActiveEncounters()
		val encounter = encounterDao.getEncounterById(encounterId)
		encounter?.let {
			encounterDao.updateEncounter(it.copy(isActive = true))
		}
	}

	override suspend fun getRecentSession(): SessionRecord? =
		sessionDao.getRecentSession()?.toDomain()

	override fun getAllLogs(): Flow<List<LogEntry>> =
		logDao.getAllLogs().map { entities -> entities.map { it.toDomain() } }

	override suspend fun insertLog(log: LogEntry) {
		logDao.insertLog(log.toEntity())
		val count = logDao.getLogCount()
		if (count > MAX_LOG_ENTRIES) {
			logDao.deleteOldestLogs(count - MAX_LOG_ENTRIES)
		}
	}

	override suspend fun clearLogs() {
		logDao.clearLogs()
	}
}
