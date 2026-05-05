/*
 * FILE: CampaignRepositoryImpl.kt
 *
 * TABLE OF CONTENTS:
 * 1. Class: CampaignRepositoryImpl
 * 2. Value: campaignDao
 * 3. Value: encounterDao
 * 4. Value: characterDao
 * 5. Value: sessionDao
 * 6. Value: noteDao
 * 7. Value: logDao
 * 8. Value: encounterIds
 */

package io.github.velyene.loreweaver.data.repository

import io.github.velyene.loreweaver.data.dao.CampaignDao
import io.github.velyene.loreweaver.data.dao.CharacterDao
import io.github.velyene.loreweaver.data.dao.EncounterDao
import io.github.velyene.loreweaver.data.dao.LogDao
import io.github.velyene.loreweaver.data.dao.NoteDao
import io.github.velyene.loreweaver.data.dao.SessionDao
import io.github.velyene.loreweaver.data.mapper.toDomain
import io.github.velyene.loreweaver.data.mapper.toEntity
import io.github.velyene.loreweaver.data.repository.RepositoryConstants.MAX_LOG_ENTRIES
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterSnapshot
import io.github.velyene.loreweaver.domain.model.LogEntry
import io.github.velyene.loreweaver.domain.model.Note
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.repository.CampaignRepository
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

	override fun getAllEncounters(): Flow<List<Encounter>> =
		encounterDao.getAllEncounters().map { entities -> entities.map { it.toDomain() } }

	override suspend fun getCampaignById(id: String): Campaign? =
		campaignDao.getCampaignById(id)?.toDomain()

	override suspend fun insertCampaign(campaign: Campaign) {
		campaignDao.insertCampaign(campaign.toEntity())
	}

	override suspend fun updateCampaign(campaign: Campaign) {
		val existingEntity = campaignDao.getCampaignById(campaign.id)
		campaignDao.insertCampaign(
			campaign.toEntity().let { entity ->
				if (existingEntity != null) {
					entity.copy(createdAt = existingEntity.createdAt)
				} else {
					entity
				}
			}
		)
	}

	override suspend fun deleteCampaign(campaign: Campaign) {
		val encounterIds = encounterDao.getEncounterIdsForCampaign(campaign.id)
		if (encounterIds.isNotEmpty()) {
			sessionDao.clearEncounterReferences(encounterIds)
		}
		campaignDao.deleteCampaignById(campaign.id)
	}

	override fun getEncountersForCampaign(campaignId: String): Flow<List<Encounter>> =
		encounterDao.getEncountersForCampaign(campaignId)
			.map { entities -> entities.map { it.toDomain() } }

	override suspend fun insertEncounter(encounter: Encounter) {
		upsertEncounter(encounter)
	}

	override suspend fun updateEncounter(encounter: Encounter) {
		upsertEncounter(encounter)
	}

	override suspend fun deleteEncounter(encounter: Encounter) {
		sessionDao.clearEncounterReferences(listOf(encounter.id))
		encounterDao.getEncounterById(encounter.id)?.let { entity ->
			encounterDao.deleteEncounter(entity)
		}
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

	override suspend fun getSessionById(sessionId: String): SessionRecord? =
		sessionDao.getSessionById(sessionId)?.toDomain()

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
		if (encounterId.isBlank()) return
		val encounter = encounterDao.getEncounterById(encounterId)
		encounter?.let {
			encounterDao.updateEncounter(it.copy(isActive = true))
		}
	}

	override suspend fun getRecentSession(): SessionRecord? =
		sessionDao.getRecentSession()?.toDomain()

	override suspend fun getRecentSessionForEncounter(encounterId: String): SessionRecord? =
		sessionDao.getRecentSessionForEncounter(encounterId)?.toDomain()

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

	private suspend fun upsertEncounter(encounter: Encounter) {
		val existingEntity = encounterDao.getEncounterById(encounter.id)
		val targetEntity = encounter.toEntity().let { candidate ->
			if (existingEntity != null) {
				candidate.copy(createdAt = existingEntity.createdAt)
			} else {
				candidate
			}
		}

		if (existingEntity == null) {
			encounterDao.insertEncounter(targetEntity)
		} else {
			encounterDao.updateEncounter(targetEntity)
		}
	}
}
