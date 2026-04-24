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
import io.github.velyene.loreweaver.domain.use_case.DeleteNoteUseCase
import io.github.velyene.loreweaver.domain.use_case.GetAllSessionsUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCampaignByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCampaignsUseCase
import io.github.velyene.loreweaver.domain.use_case.GetEncountersForCampaignUseCase
import io.github.velyene.loreweaver.domain.use_case.GetNotesForCampaignUseCase
import io.github.velyene.loreweaver.domain.use_case.GetSessionsForEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.UpdateNoteUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

internal fun createCampaignListViewModel(repository: SplitFakeCampaignRepository): CampaignListViewModel {
	return CampaignListViewModel(
		getCampaignsUseCase = GetCampaignsUseCase(repository),
		getAllSessionsUseCase = GetAllSessionsUseCase(repository)
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
		addEncounterUseCase = AddEncounterUseCase(repository),
		addMonstersToEncounterUseCase = AddMonstersToEncounterUseCase(repository),
		addNoteUseCase = AddNoteUseCase(repository),
		updateNoteUseCase = UpdateNoteUseCase(repository),
		deleteNoteUseCase = DeleteNoteUseCase(repository)
	)
}

internal class SplitFakeCampaignRepository : CampaignRepository {
	private val campaignsFlow = MutableStateFlow<List<Campaign>>(emptyList())
	private val allSessionsFlow = MutableStateFlow<List<SessionRecord>>(emptyList())
	private val encountersByCampaign = mutableMapOf<String, MutableStateFlow<List<Encounter>>>()
	private val notesByCampaign = mutableMapOf<String, MutableStateFlow<List<Note>>>()
	private val sessionsByEncounter = mutableMapOf<String, MutableStateFlow<List<SessionRecord>>>()

	val insertedNotes = mutableListOf<Note>()
	val updatedNotes = mutableListOf<Note>()

	override fun getAllCampaigns(): Flow<List<Campaign>> = campaignsFlow

	override suspend fun getCampaignById(id: String): Campaign? {
		return campaignsFlow.value.firstOrNull { it.id == id }
	}

	override suspend fun insertCampaign(campaign: Campaign) {
		campaignsFlow.value += campaign
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
	}

	override suspend fun addCombatantsToEncounter(
		encounterId: String,
		combatants: List<CombatantState>
	) = Unit

	override fun getSessionsForEncounter(encounterId: String): Flow<List<SessionRecord>> {
		return sessionsByEncounter.getOrPut(encounterId) { MutableStateFlow(emptyList()) }
	}

	override fun getAllSessions(): Flow<List<SessionRecord>> = allSessionsFlow

	override suspend fun insertSessionRecord(session: SessionRecord) {
		allSessionsFlow.value += session
		session.encounterId?.let { encounterId ->
			val flow = sessionsByEncounter.getOrPut(encounterId) { MutableStateFlow(emptyList()) }
			flow.value += session
		}
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

	override fun getAllCharacters(): Flow<List<CharacterEntry>> = flowOf(emptyList())

	override suspend fun getCharacterById(id: String): CharacterEntry? = null

	override suspend fun insertCharacter(character: CharacterEntry) = Unit

	override suspend fun updateCharacter(character: CharacterEntry) = Unit

	override suspend fun deleteCharacter(character: CharacterEntry) = Unit

	override suspend fun getActiveEncounter(): Encounter? = null

	override suspend fun setActiveEncounter(encounterId: String) = Unit

	override suspend fun getRecentSession(): SessionRecord? = null

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
}

