/*
 * FILE: CombatViewModelTestSupport.kt
 *
 * TABLE OF CONTENTS:
 * 1. Shared test constants and factory helpers
 * 2. FakeCombatCampaignRepository
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterStatus
import io.github.velyene.loreweaver.domain.model.LogEntry
import io.github.velyene.loreweaver.domain.model.Note
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.repository.CampaignRepository
import io.github.velyene.loreweaver.domain.use_case.GetActiveEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCharacterByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCharactersUseCase
import io.github.velyene.loreweaver.domain.use_case.GetEncounterByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetSessionsForEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertLogUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertSessionRecordUseCase
import io.github.velyene.loreweaver.domain.use_case.SetActiveEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.UpdateCharacterUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

internal const val HERO_ID = "hero-1"
internal const val HERO_NAME = "Hero"
internal const val GOBLIN_ID = "goblin-1"
internal const val GOBLIN_NAME = "Goblin"
internal const val MONSTER_ID = "monster-1"

internal fun createCombatViewModel(repository: FakeCombatCampaignRepository): CombatViewModel {
	return CombatViewModel(
		getCharactersUseCase = GetCharactersUseCase(repository),
		getActiveEncounterUseCase = GetActiveEncounterUseCase(repository),
		getCharacterByIdUseCase = GetCharacterByIdUseCase(repository),
		getEncounterByIdUseCase = GetEncounterByIdUseCase(repository),
		getSessionsForEncounterUseCase = GetSessionsForEncounterUseCase(repository),
		insertEncounterUseCase = InsertEncounterUseCase(repository),
		setActiveEncounterUseCase = SetActiveEncounterUseCase(repository),
		insertLogUseCase = InsertLogUseCase(repository),
		insertSessionRecordUseCase = InsertSessionRecordUseCase(repository),
		updateCharacterUseCase = UpdateCharacterUseCase(repository),
		appText = fakeAppText
	)
}

internal fun combatant(id: String, name: String, initiative: Int, hp: Int): CombatantState {
	return CombatantState(
		characterId = id,
		name = name,
		initiative = initiative,
		currentHp = hp,
		maxHp = hp
	)
}

internal class FakeCombatCampaignRepository : CampaignRepository {
	private val campaignsFlow = MutableStateFlow<List<Campaign>>(emptyList())
	private val charactersFlow = MutableStateFlow<List<CharacterEntry>>(emptyList())
	private val allSessionsFlow = MutableStateFlow<List<SessionRecord>>(emptyList())
	private val logsFlow = MutableStateFlow<List<LogEntry>>(emptyList())
	private val encountersById = mutableMapOf<String, Encounter>()
	private val sessionsByEncounterId = mutableMapOf<String, List<SessionRecord>>()
	var updateCharacterDelayMillis: Long = 0
	val activeEncounterId: String?
		get() = activeEncounter?.id
	private var activeEncounter: Encounter? = Encounter(
		id = "encounter-1",
		name = "Fallback",
		status = EncounterStatus.ACTIVE
	)
	private var recentSession: SessionRecord? = null

	override fun getAllCampaigns(): Flow<List<Campaign>> = campaignsFlow

	override suspend fun getCampaignById(id: String): Campaign? =
		campaignsFlow.value.firstOrNull { it.id == id }

	override suspend fun insertCampaign(campaign: Campaign) {
		campaignsFlow.value += campaign
	}

	override fun getEncountersForCampaign(campaignId: String): Flow<List<Encounter>> =
		flowOf(encountersById.values.filter { it.campaignId == campaignId })

	override suspend fun getEncounterById(encounterId: String): Encounter? = encountersById[encounterId]

	override suspend fun insertEncounter(encounter: Encounter) {
		encountersById[encounter.id] = encounter
	}

	override suspend fun addCombatantsToEncounter(
		encounterId: String,
		combatants: List<CombatantState>
	) = Unit

	override fun getSessionsForEncounter(encounterId: String): Flow<List<SessionRecord>> =
		flowOf(sessionsByEncounterId[encounterId].orEmpty())

	override fun getAllSessions(): Flow<List<SessionRecord>> = allSessionsFlow

	override suspend fun insertSessionRecord(session: SessionRecord) {
		allSessionsFlow.value += session
		session.encounterId?.let { encounterId ->
			sessionsByEncounterId[encounterId] = listOf(session) + sessionsByEncounterId[encounterId].orEmpty()
		}
		recentSession = session
	}

	override fun getNotesForCampaign(campaignId: String): Flow<List<Note>> = flowOf(emptyList())

	override suspend fun insertNote(note: Note) = Unit

	override suspend fun updateNote(note: Note) = Unit

	override suspend fun deleteNote(note: Note) = Unit

	override fun getAllCharacters(): Flow<List<CharacterEntry>> = charactersFlow

	override suspend fun getCharacterById(id: String): CharacterEntry? =
		charactersFlow.value.firstOrNull { it.id == id }

	override suspend fun insertCharacter(character: CharacterEntry) = Unit

	override suspend fun updateCharacter(character: CharacterEntry) {
		delay(updateCharacterDelayMillis)
		charactersFlow.value = charactersFlow.value.map { existing ->
			if (existing.id == character.id) character else existing
		}
	}

	override suspend fun deleteCharacter(character: CharacterEntry) = Unit

	override suspend fun getActiveEncounter(): Encounter? = activeEncounter

	override suspend fun setActiveEncounter(encounterId: String) {
		activeEncounter = encountersById[encounterId]
	}

	override suspend fun getRecentSession(): SessionRecord? = recentSession

	override fun getAllLogs(): Flow<List<LogEntry>> = logsFlow

	override suspend fun insertLog(log: LogEntry) {
		logsFlow.value += log
	}

	override suspend fun clearLogs() {
		logsFlow.value = emptyList()
	}

	fun setCharacters(characters: List<CharacterEntry>) {
		charactersFlow.value = characters
	}

	fun getStoredCharacter(id: String): CharacterEntry? =
		charactersFlow.value.firstOrNull { it.id == id }

	fun getStoredEncounter(id: String): Encounter? = encountersById[id]

	fun setEncounter(encounter: Encounter) {
		encountersById[encounter.id] = encounter
	}

	fun setSessions(encounterId: String, sessions: List<SessionRecord>) {
		setSessionsForEncounter(encounterId, sessions)
	}

	fun setSessionsForEncounter(encounterId: String, sessions: List<SessionRecord>) {
		sessionsByEncounterId[encounterId] = sessions
		allSessionsFlow.value = sessions
		recentSession = sessions.firstOrNull()
	}
}

