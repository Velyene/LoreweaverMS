/*
 * FILE: CampaignViewModelTestSupport.kt
 *
 * TABLE OF CONTENTS:
 * 1. ViewModel factory helpers for tests
 * 2. Split fake campaign repository
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.LogEntry
import io.github.velyene.loreweaver.domain.model.Note
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.repository.CampaignRepository
import io.github.velyene.loreweaver.domain.use_case.AddCharacterUseCase
import io.github.velyene.loreweaver.domain.use_case.AddCampaignUseCase
import io.github.velyene.loreweaver.domain.use_case.AddEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.AddMonstersToEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.AddNoteUseCase
import io.github.velyene.loreweaver.domain.use_case.ClearLogsUseCase
import io.github.velyene.loreweaver.domain.use_case.DeleteCharacterUseCase
import io.github.velyene.loreweaver.domain.use_case.DeleteNoteUseCase
import io.github.velyene.loreweaver.domain.use_case.GetAllLogsUseCase
import io.github.velyene.loreweaver.domain.use_case.GetAllSessionsUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCampaignByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCampaignsUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCharacterByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCharactersUseCase
import io.github.velyene.loreweaver.domain.use_case.GetEncountersForCampaignUseCase
import io.github.velyene.loreweaver.domain.use_case.GetNotesForCampaignUseCase
import io.github.velyene.loreweaver.domain.use_case.GetSessionsForEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertLogUseCase
import io.github.velyene.loreweaver.domain.use_case.UpdateCharacterUseCase
import io.github.velyene.loreweaver.domain.use_case.UpdateNoteUseCase
import io.github.velyene.loreweaver.ui.util.AppText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

internal val fakeAppText: AppText = FakeAppText()

internal fun createCampaignListViewModel(repository: SplitFakeCampaignRepository): CampaignListViewModel {
	return CampaignListViewModel(
		getCampaignsUseCase = GetCampaignsUseCase(repository),
		getAllSessionsUseCase = GetAllSessionsUseCase(repository),
		appText = fakeAppText
	)
}

internal fun createCampaignDetailViewModel(repository: SplitFakeCampaignRepository): CampaignDetailViewModel {
	return CampaignDetailViewModel(
		getCampaignByIdUseCase = GetCampaignByIdUseCase(repository),
		getEncountersForCampaignUseCase = GetEncountersForCampaignUseCase(repository),
		getNotesForCampaignUseCase = GetNotesForCampaignUseCase(repository),
		getSessionsForEncounterUseCase = GetSessionsForEncounterUseCase(repository),
		appText = fakeAppText
	)
}

internal fun createCampaignEditorViewModel(repository: SplitFakeCampaignRepository): CampaignEditorViewModel {
	return CampaignEditorViewModel(
		addCampaignUseCase = AddCampaignUseCase(repository),
		addEncounterUseCase = AddEncounterUseCase(repository),
		addMonstersToEncounterUseCase = AddMonstersToEncounterUseCase(repository),
		addNoteUseCase = AddNoteUseCase(repository),
		updateNoteUseCase = UpdateNoteUseCase(repository),
		deleteNoteUseCase = DeleteNoteUseCase(repository),
		appText = fakeAppText
	)
}

internal fun createCharacterViewModel(repository: SplitFakeCampaignRepository): CharacterViewModel {
	return CharacterViewModel(
		getCharactersUseCase = GetCharactersUseCase(repository),
		getCharacterByIdUseCase = GetCharacterByIdUseCase(repository),
		addCharacterUseCase = AddCharacterUseCase(repository),
		updateCharacterUseCase = UpdateCharacterUseCase(repository),
		deleteCharacterUseCase = DeleteCharacterUseCase(repository),
		insertLogUseCase = InsertLogUseCase(repository),
		appText = fakeAppText
	)
}

internal fun createAdventureLogViewModel(repository: SplitFakeCampaignRepository): AdventureLogViewModel {
	return AdventureLogViewModel(
		getAllLogsUseCase = GetAllLogsUseCase(repository),
		clearLogsUseCase = ClearLogsUseCase(repository),
		appText = fakeAppText
	)
}

internal fun expectedErrorMessage(resId: Int, detail: String): String {
	return fakeAppText.getString(R.string.message_with_detail, fakeAppText.getString(resId), detail)
}

internal fun expectedString(resId: Int): String = fakeAppText.getString(resId)

internal fun expectedEncounterAddedWithMonstersMessage(monsterCount: Int): String {
	return fakeAppText.getQuantityString(
		R.plurals.encounter_created_with_monsters_message,
		monsterCount,
		monsterCount
	)
}

internal class SplitFakeCampaignRepository : CampaignRepository {
	private val campaignsFlow = MutableStateFlow<List<Campaign>>(emptyList())
	private val charactersFlow = MutableStateFlow<List<CharacterEntry>>(emptyList())
	private val allSessionsFlow = MutableStateFlow<List<SessionRecord>>(emptyList())
	private val logsFlow = MutableStateFlow<List<LogEntry>>(emptyList())
	private val encountersByCampaign = mutableMapOf<String, MutableStateFlow<List<Encounter>>>()
	private val notesByCampaign = mutableMapOf<String, MutableStateFlow<List<Note>>>()
	private val sessionsByEncounter = mutableMapOf<String, MutableStateFlow<List<SessionRecord>>>()
	private var clearLogsException: Exception? = null

	val insertedCharacters = mutableListOf<CharacterEntry>()
	val insertedNotes = mutableListOf<Note>()
	val updatedNotes = mutableListOf<Note>()
	val combatantsByEncounterId = mutableMapOf<String, List<CombatantState>>()

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
	) {
		combatantsByEncounterId[encounterId] = combatants
	}

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

	override fun getAllCharacters(): Flow<List<CharacterEntry>> = charactersFlow

	override suspend fun getCharacterById(id: String): CharacterEntry? =
		charactersFlow.value.firstOrNull { it.id == id }

	override suspend fun insertCharacter(character: CharacterEntry) {
		insertedCharacters += character
		charactersFlow.value += character
	}

	override suspend fun updateCharacter(character: CharacterEntry) {
		charactersFlow.value = charactersFlow.value.map {
			if (it.id == character.id) character else it
		}
	}

	override suspend fun deleteCharacter(character: CharacterEntry) {
		charactersFlow.value = charactersFlow.value.filterNot { it.id == character.id }
	}

	override suspend fun getActiveEncounter(): Encounter? = null

	override suspend fun setActiveEncounter(encounterId: String) = Unit

	override suspend fun getRecentSession(): SessionRecord? = null

	override fun getAllLogs(): Flow<List<LogEntry>> = logsFlow

	override suspend fun insertLog(log: LogEntry) {
		logsFlow.value += log
	}

	override suspend fun clearLogs() {
		clearLogsException?.let { throw it }
		logsFlow.value = emptyList()
	}

	fun setCampaigns(campaigns: List<Campaign>) {
		campaignsFlow.value = campaigns
	}

	fun setCharacters(characters: List<CharacterEntry>) {
		charactersFlow.value = characters
	}

	fun setNotes(campaignId: String, notes: List<Note>) {
		notesByCampaign.getOrPut(campaignId) { MutableStateFlow(emptyList()) }.value = notes
	}

	fun setAllSessions(sessions: List<SessionRecord>) {
		allSessionsFlow.value = sessions
	}

	fun setLogs(logs: List<LogEntry>) {
		logsFlow.value = logs
	}

	fun setClearLogsException(exception: Exception?) {
		clearLogsException = exception
	}
}

internal class FakeAppText : AppText {
	override fun getString(resId: Int, vararg formatArgs: Any): String {
		return when (resId) {
			R.string.unknown_error -> "Unknown error"
			R.string.message_with_detail -> "${formatArgs[0]}: ${formatArgs[1]}"
			R.string.campaign_not_found_message -> "Campaign not found."
			R.string.campaign_error_critical -> "Critical error"
			R.string.campaign_error_load_campaigns -> "Failed to load campaigns"
			R.string.campaign_error_load_sessions -> "Failed to load sessions"
			R.string.campaign_error_load_encounters -> "Failed to load encounters"
			R.string.campaign_error_load_notes -> "Failed to load notes"
			R.string.campaign_error_add_campaign -> "Failed to add campaign"
			R.string.campaign_error_add_encounter -> "Failed to add encounter"
			R.string.campaign_error_add_note -> "Failed to add note"
			R.string.campaign_error_update_note -> "Failed to update note"
			R.string.campaign_error_delete_note -> "Failed to delete note"
			R.string.adventure_log_error_load -> "Failed to load adventure log"
			R.string.adventure_log_error_clear -> "Failed to clear adventure log"
			R.string.encounter_not_found_message -> "Encounter not found."
			R.string.encounter_error_load -> "Failed to load encounter"
			R.string.encounter_error_start -> "Failed to start encounter"
			R.string.character_error_load -> "Failed to load characters"
			R.string.character_error_not_found -> "Character not found"
			R.string.character_error_add -> "Failed to add character"
			R.string.character_error_update -> "Failed to update character"
			R.string.character_error_delete -> "Failed to delete character"
			else -> error("Unhandled string resource id $resId in FakeAppText")
		}
	}

	override fun getQuantityString(resId: Int, quantity: Int, vararg formatArgs: Any): String {
		return when (resId) {
			R.plurals.encounter_created_with_monsters_message -> {
				if (quantity == 1) {
					"Encounter created with ${formatArgs[0]} monster!"
				} else {
					"Encounter created with ${formatArgs[0]} monsters!"
				}
			}

			else -> error("Unhandled plural resource id $resId in FakeAppText")
		}
	}
}

