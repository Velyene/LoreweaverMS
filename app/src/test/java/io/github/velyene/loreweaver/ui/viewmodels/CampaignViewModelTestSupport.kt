/*
 * FILE: CampaignViewModelTestSupport.kt
 *
 * TABLE OF CONTENTS:
 * 1. Function: createCampaignListViewModel
 * 2. Function: createCampaignDetailViewModel
 * 3. Function: createCampaignEditorViewModel
 * 4. Function: createSessionDetailViewModel
 * 5. Function: createSessionSummaryViewModel
 * 6. Class: SplitFakeCampaignRepository
 * 7. Value: campaignsFlow
 * 8. Value: charactersFlow
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.LogEntry
import io.github.velyene.loreweaver.domain.model.Note
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.repository.CampaignRepository
import io.github.velyene.loreweaver.domain.use_case.AddCampaignUseCase
import io.github.velyene.loreweaver.domain.use_case.AddEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.AddMonstersToEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.AddNoteUseCase
import io.github.velyene.loreweaver.domain.use_case.DeleteCampaignUseCase
import io.github.velyene.loreweaver.domain.use_case.DeleteEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.DeleteNoteUseCase
import io.github.velyene.loreweaver.domain.use_case.GetActiveEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.GetAllSessionsUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCampaignByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCampaignsUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCharactersUseCase
import io.github.velyene.loreweaver.domain.use_case.GetEncounterByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetEncountersForCampaignUseCase
import io.github.velyene.loreweaver.domain.use_case.GetNotesForCampaignUseCase
import io.github.velyene.loreweaver.domain.use_case.GetRecentSessionUseCase
import io.github.velyene.loreweaver.domain.use_case.GetSessionByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetSessionsForEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.UpdateCampaignUseCase
import io.github.velyene.loreweaver.domain.use_case.UpdateEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.UpdateNoteUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

internal fun createCampaignListViewModel(repository: SplitFakeCampaignRepository): CampaignListViewModel {
	return CampaignListViewModel(
		getCampaignsUseCase = GetCampaignsUseCase(repository),
		getAllSessionsUseCase = GetAllSessionsUseCase(repository),
		getActiveEncounterUseCase = GetActiveEncounterUseCase(repository)
	)
}

internal fun createCampaignDetailViewModel(repository: SplitFakeCampaignRepository): CampaignDetailViewModel {
	return CampaignDetailViewModel(
		getCampaignByIdUseCase = GetCampaignByIdUseCase(repository),
		getEncountersForCampaignUseCase = GetEncountersForCampaignUseCase(repository),
		getNotesForCampaignUseCase = GetNotesForCampaignUseCase(repository),
		getSessionsForEncounterUseCase = GetSessionsForEncounterUseCase(repository)
	)
}

internal fun createCampaignEditorViewModel(repository: SplitFakeCampaignRepository): CampaignEditorViewModel {
	return CampaignEditorViewModel(
		addCampaignUseCase = AddCampaignUseCase(repository),
		updateCampaignUseCase = UpdateCampaignUseCase(repository),
		deleteCampaignUseCase = DeleteCampaignUseCase(repository),
		addEncounterUseCase = AddEncounterUseCase(repository),
		updateEncounterUseCase = UpdateEncounterUseCase(repository),
		deleteEncounterUseCase = DeleteEncounterUseCase(repository),
		addMonstersToEncounterUseCase = AddMonstersToEncounterUseCase(repository),
		addNoteUseCase = AddNoteUseCase(repository),
		updateNoteUseCase = UpdateNoteUseCase(repository),
		deleteNoteUseCase = DeleteNoteUseCase(repository)
	)
}

internal fun createSessionDetailViewModel(repository: SplitFakeCampaignRepository): SessionDetailViewModel {
	return SessionDetailViewModel(
		getSessionByIdUseCase = GetSessionByIdUseCase(repository),
		getEncounterByIdUseCase = GetEncounterByIdUseCase(repository),
		getCampaignByIdUseCase = GetCampaignByIdUseCase(repository),
		getCharactersUseCase = GetCharactersUseCase(repository),
	)
}

internal fun createSessionSummaryViewModel(repository: SplitFakeCampaignRepository): SessionSummaryViewModel {
	return SessionSummaryViewModel(
		getRecentSessionUseCase = GetRecentSessionUseCase(repository),
		getSessionByIdUseCase = GetSessionByIdUseCase(repository),
		getEncounterByIdUseCase = GetEncounterByIdUseCase(repository),
		getCampaignByIdUseCase = GetCampaignByIdUseCase(repository),
		getCharactersUseCase = GetCharactersUseCase(repository),
	)
}

internal class SplitFakeCampaignRepository : CampaignRepository {
	private val campaignsFlow = MutableStateFlow<List<Campaign>>(emptyList())
	private val charactersFlow = MutableStateFlow<List<CharacterEntry>>(emptyList())
	private val allSessionsFlow = MutableStateFlow<List<SessionRecord>>(emptyList())
	private val encountersByCampaign = mutableMapOf<String, MutableStateFlow<List<Encounter>>>()
	private val notesByCampaign = mutableMapOf<String, MutableStateFlow<List<Note>>>()
	private val sessionsByEncounter = mutableMapOf<String, MutableStateFlow<List<SessionRecord>>>()
	private var activeEncounter: Encounter? = null
	private var recentSession: SessionRecord? = null
	var allSessionsFailure: Exception? = null
	var recentSessionFailure: Exception? = null

	val insertedCharacters = mutableListOf<CharacterEntry>()
	val insertedNotes = mutableListOf<Note>()
	val updatedNotes = mutableListOf<Note>()
	val combatantsByEncounterId = mutableMapOf<String, List<CombatantState>>()

	override fun getAllCampaigns(): Flow<List<Campaign>> = campaignsFlow

	override fun getAllEncounters(): Flow<List<Encounter>> {
		return flowOf(encountersByCampaign.values.flatMap { it.value })
	}

	override suspend fun getCampaignById(id: String): Campaign? {
		return campaignsFlow.value.firstOrNull { it.id == id }
	}

	override suspend fun insertCampaign(campaign: Campaign) {
		campaignsFlow.value += campaign
	}

	override suspend fun updateCampaign(campaign: Campaign) {
		campaignsFlow.value = campaignsFlow.value.map { existing ->
			if (existing.id == campaign.id) campaign else existing
		}
	}

	override suspend fun deleteCampaign(campaign: Campaign) {
		campaignsFlow.value = campaignsFlow.value.filterNot { it.id == campaign.id }
	}

	override fun getEncountersForCampaign(campaignId: String): Flow<List<Encounter>> {
		return encountersByCampaign.getOrPut(campaignId) { MutableStateFlow(emptyList()) }
	}

	override suspend fun getEncounterById(encounterId: String): Encounter? {
		return encountersByCampaign.values
			.flatMap { it.value }
			.firstOrNull { it.id == encounterId }
	}

	override suspend fun insertEncounter(encounter: Encounter) {
		val campaignId = encounter.campaignId.orEmpty()
		val flow = encountersByCampaign.getOrPut(campaignId) { MutableStateFlow(emptyList()) }
		flow.value += encounter
		activeEncounter = when {
			encounter.status == io.github.velyene.loreweaver.domain.model.EncounterStatus.ACTIVE -> encounter
			activeEncounter?.id == encounter.id -> null
			else -> activeEncounter
		}
	}

	override suspend fun updateEncounter(encounter: Encounter) {
		val campaignId = encounter.campaignId.orEmpty()
		val flow = encountersByCampaign.getOrPut(campaignId) { MutableStateFlow(emptyList()) }
		flow.value = flow.value.map { existing ->
			if (existing.id == encounter.id) encounter else existing
		}
		activeEncounter = when {
			encounter.status == io.github.velyene.loreweaver.domain.model.EncounterStatus.ACTIVE -> encounter
			activeEncounter?.id == encounter.id -> null
			else -> activeEncounter
		}
	}

	override suspend fun deleteEncounter(encounter: Encounter) {
		val campaignId = encounter.campaignId.orEmpty()
		encountersByCampaign[campaignId]?.let { flow ->
			flow.value = flow.value.filterNot { it.id == encounter.id }
		}
		if (activeEncounter?.id == encounter.id) {
			activeEncounter = null
		}
	}

	override suspend fun addCombatantsToEncounter(
		encounterId: String,
		combatants: List<CombatantState>
	) {
		combatantsByEncounterId[encounterId] = combatants
	}

	override fun getSessionsForEncounter(encounterId: String): Flow<List<SessionRecord>> {
		return sessionsByEncounter.getOrPut(encounterId) { MutableStateFlow(emptyList()) }
	}

	override fun getAllSessions(): Flow<List<SessionRecord>> = allSessionsFailure?.let { failure ->
		flow { throw failure }
	} ?: allSessionsFlow

	override suspend fun insertSessionRecord(session: SessionRecord) {
		allSessionsFlow.value += session
		session.encounterId?.let { encounterId ->
			val flow = sessionsByEncounter.getOrPut(encounterId) { MutableStateFlow(emptyList()) }
			flow.value += session
		}
	}

	override suspend fun getSessionById(sessionId: String): SessionRecord? {
		return allSessionsFlow.value.firstOrNull { it.id == sessionId }
			?: sessionsByEncounter.values
				.flatMap { it.value }
				.firstOrNull { it.id == sessionId }
	}

	override fun getNotesForCampaign(campaignId: String): Flow<List<Note>> {
		return notesByCampaign.getOrPut(campaignId) { MutableStateFlow(emptyList()) }
	}

	override suspend fun insertNote(note: Note) {
		insertedNotes += note
		note.campaignId?.let { campaignId ->
			val flow = notesByCampaign.getOrPut(campaignId) { MutableStateFlow(emptyList()) }
			flow.value += note
		}
	}

	override suspend fun updateNote(note: Note) {
		updatedNotes += note
	}

	override suspend fun deleteNote(note: Note) = Unit

	override fun getAllCharacters(): Flow<List<CharacterEntry>> = charactersFlow

	override suspend fun getCharacterById(id: String): CharacterEntry? =
		charactersFlow.value.firstOrNull { it.id == id }

	override suspend fun insertCharacter(character: CharacterEntry) {
		insertedCharacters += character
		charactersFlow.value += character
	}

	override suspend fun updateCharacter(character: CharacterEntry) {
		charactersFlow.value = charactersFlow.value.map { existing ->
			if (existing.id == character.id) character else existing
		}
	}

	override suspend fun deleteCharacter(character: CharacterEntry) {
		charactersFlow.value = charactersFlow.value.filterNot { it.id == character.id }
	}

	override suspend fun getActiveEncounter(): Encounter? = activeEncounter

	override suspend fun setActiveEncounter(encounterId: String) {
		if (encounterId.isBlank()) {
			activeEncounter = null
			return
		}

		var resolvedActiveEncounter: Encounter? = null
		encountersByCampaign.forEach { (_, flow) ->
			flow.value = flow.value.map { encounter ->
				if (encounter.id == encounterId) {
					encounter.copy(status = io.github.velyene.loreweaver.domain.model.EncounterStatus.ACTIVE).also {
						resolvedActiveEncounter = it
					}
				} else {
					encounter.copy(status = io.github.velyene.loreweaver.domain.model.EncounterStatus.PENDING)
				}
			}
		}
		activeEncounter = resolvedActiveEncounter
	}

	override suspend fun getRecentSession(): SessionRecord? {
		recentSessionFailure?.let { throw it }
		return recentSession
	}

	override suspend fun getRecentSessionForEncounter(encounterId: String): SessionRecord? {
		return recentSession?.takeIf { it.encounterId == encounterId }
			?: sessionsByEncounter[encounterId]?.value?.maxByOrNull(SessionRecord::date)
	}

	override fun getAllLogs(): Flow<List<LogEntry>> = flowOf(emptyList())

	override suspend fun insertLog(log: LogEntry) = Unit

	override suspend fun clearLogs() = Unit

	fun setCampaigns(campaigns: List<Campaign>) {
		campaignsFlow.value = campaigns
	}

	fun setNotes(campaignId: String, notes: List<Note>) {
		notesByCampaign.getOrPut(campaignId) { MutableStateFlow(emptyList()) }.value = notes
	}

	fun setAllSessions(sessions: List<SessionRecord>) {
		allSessionsFlow.value = sessions
	}

	fun setCharacters(characters: List<CharacterEntry>) {
		charactersFlow.value = characters
	}

	fun setActiveEncounterForTest(encounter: Encounter?) {
		activeEncounter = encounter
	}

	fun setRecentSessionForTest(session: SessionRecord?) {
		recentSession = session
		session?.encounterId?.let { encounterId ->
			sessionsByEncounter.getOrPut(encounterId) { MutableStateFlow(emptyList()) }.value += session
		}
	}
}

