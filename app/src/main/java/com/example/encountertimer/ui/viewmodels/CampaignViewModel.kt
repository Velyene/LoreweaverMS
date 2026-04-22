/*
 * FILE: CampaignViewModel.kt
 *
 * TABLE OF CONTENTS:
 * 1. UI State Definition (CampaignUiState)
 * 2. Campaign View Model (CampaignViewModel)
 *    a. Core Campaign Management (List, Detail)
 *       - selectCampaign (main entry point)
 *       - handleCampaignNotFound
 *       - loadCampaignEncounters
 *       - loadCampaignSessions (reactive flatMapLatest)
 *       - loadCampaignNotes
 *    b. Encounter & Note Logic
 *    c. Session History Handling
 */

package com.example.encountertimer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.encountertimer.domain.model.Campaign
import com.example.encountertimer.domain.model.Encounter
import com.example.encountertimer.domain.model.Note
import com.example.encountertimer.domain.model.RemoteItem
import com.example.encountertimer.domain.model.SessionRecord
import com.example.encountertimer.domain.use_case.AddCampaignUseCase
import com.example.encountertimer.domain.use_case.AddEncounterUseCase
import com.example.encountertimer.domain.use_case.AddMonstersToEncounterUseCase
import com.example.encountertimer.domain.use_case.AddNoteUseCase
import com.example.encountertimer.domain.use_case.DeleteNoteUseCase
import com.example.encountertimer.domain.use_case.GetAllSessionsUseCase
import com.example.encountertimer.domain.use_case.GetCampaignByIdUseCase
import com.example.encountertimer.domain.use_case.GetCampaignsUseCase
import com.example.encountertimer.domain.use_case.GetEncountersForCampaignUseCase
import com.example.encountertimer.domain.use_case.GetNotesForCampaignUseCase
import com.example.encountertimer.domain.use_case.GetSessionsForEncounterUseCase
import com.example.encountertimer.domain.use_case.UpdateNoteUseCase
import com.example.encountertimer.ui.util.NOTE_TYPE_LOCATION
import com.example.encountertimer.ui.util.NOTE_TYPE_LORE
import com.example.encountertimer.ui.util.NOTE_TYPE_NPC
import com.example.encountertimer.ui.util.CAMPAIGN_NOT_FOUND_MESSAGE
import com.example.encountertimer.ui.util.parseNpcExtra
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CampaignUiState(
	val campaigns: List<Campaign> = emptyList(),
	val selectedCampaign: Campaign? = null,
	val linkedEncounters: List<Encounter> = emptyList(),
	val sessions: List<SessionRecord> = emptyList(),
	val notes: List<Note> = emptyList(),
	val isLoading: Boolean = false,
	val error: String? = null,
	val onRetry: (() -> Unit)? = null
)

@HiltViewModel
class CampaignViewModel @Inject constructor(
	private val getCampaignsUseCase: GetCampaignsUseCase,
	private val getCampaignByIdUseCase: GetCampaignByIdUseCase,
	private val getEncountersForCampaignUseCase: GetEncountersForCampaignUseCase,
	private val getNotesForCampaignUseCase: GetNotesForCampaignUseCase,
	private val getSessionsForEncounterUseCase: GetSessionsForEncounterUseCase,
	private val getAllSessionsUseCase: GetAllSessionsUseCase,
	private val addCampaignUseCase: AddCampaignUseCase,
	private val addEncounterUseCase: AddEncounterUseCase,
	private val addMonstersToEncounterUseCase: AddMonstersToEncounterUseCase,
	private val addNoteUseCase: AddNoteUseCase,
	private val updateNoteUseCase: UpdateNoteUseCase,
	private val deleteNoteUseCase: DeleteNoteUseCase
) : ViewModel() {
	private val _uiState = MutableStateFlow(CampaignUiState())
	val uiState: StateFlow<CampaignUiState> = _uiState.asStateFlow()
	private var encountersJob: Job? = null
	private var sessionsJob: Job? = null
	private var notesJob: Job? = null

	init {
		loadCampaigns()
		loadAllSessions()
	}

	private fun loadAllSessions() {
		viewModelScope.launch {
			getAllSessionsUseCase().collect { sessions ->
				_uiState.update { it.copy(sessions = sessions) }
			}
		}
	}

	private fun loadCampaigns() {
		viewModelScope.launch {
			beginLoading()
			try {
				getCampaignsUseCase().collect { campaigns ->
					_uiState.update { it.copy(campaigns = campaigns, isLoading = false) }
				}
			} catch (e: Exception) {
				reportError(
					message = formatError("Failed to load campaigns", e),
					onRetry = ::loadCampaigns
				)
			}
		}
	}

	fun selectCampaign(campaignId: String) {
		viewModelScope.launch {
			beginLoading()
			try {
				val campaign = getCampaignByIdUseCase(campaignId)
				if (campaign == null) {
					handleCampaignNotFound(campaignId)
					return@launch
				}

				_uiState.update { it.copy(selectedCampaign = campaign) }
				observeSelectedCampaign(campaignId)
				_uiState.update { it.copy(isLoading = false) }

			} catch (e: Exception) {
				reportError(
					message = formatError("Critical error", e),
					onRetry = retrySelectCampaign(campaignId)
				)
			}
		}
	}

	private fun beginLoading() {
		_uiState.update { it.copy(isLoading = true, error = null, onRetry = null) }
	}

	private fun reportError(message: String, onRetry: (() -> Unit)? = null) {
		_uiState.update {
			it.copy(
				isLoading = false,
				error = message,
				onRetry = onRetry
			)
		}
	}

	private fun formatError(prefix: String, exception: Exception): String {
		val detail = exception.localizedMessage ?: exception.message ?: "Unknown error"
		return "$prefix: $detail"
	}

	private fun retrySelectCampaign(campaignId: String): () -> Unit = { selectCampaign(campaignId) }

	private fun observeSelectedCampaign(campaignId: String) {
		cancelCampaignObservers()
		loadCampaignEncounters(campaignId)
		loadCampaignSessions(campaignId)
		loadCampaignNotes(campaignId)
	}

	private fun cancelCampaignObservers() {
		encountersJob?.cancel()
		sessionsJob?.cancel()
		notesJob?.cancel()
	}

	private fun launchCampaignObserver(
		campaignId: String,
		errorPrefix: String,
		assignJob: (Job) -> Unit,
		collector: suspend () -> Unit
	) {
		val job = viewModelScope.launch {
			try {
				collector()
			} catch (e: Exception) {
				reportError(
					message = formatError(errorPrefix, e),
					onRetry = retrySelectCampaign(campaignId)
				)
			}
		}
		assignJob(job)
	}

	private fun launchActionWithError(
		errorPrefix: String,
		action: suspend () -> Unit
	) {
		viewModelScope.launch {
			try {
				action()
			} catch (e: Exception) {
				reportError(formatError(errorPrefix, e))
			}
		}
	}

	/**
	 * Handles the case when a campaign is not found.
	 */
	private fun handleCampaignNotFound(campaignId: String) {
		reportError(
			message = CAMPAIGN_NOT_FOUND_MESSAGE,
			onRetry = retrySelectCampaign(campaignId)
		)
	}

	/**
	 * Loads encounters for a campaign.
	 */
	private fun loadCampaignEncounters(campaignId: String) {
		launchCampaignObserver(
			campaignId = campaignId,
			errorPrefix = "Failed to load encounters",
			assignJob = { encountersJob = it }
		) {
			getEncountersForCampaignUseCase(campaignId).collect { encounters ->
				_uiState.update { it.copy(linkedEncounters = encounters) }
			}
		}
	}

	/**
	 * Loads sessions for a campaign reactively.
	 * Re-subscribes whenever encounter list changes.
	 */
	private fun loadCampaignSessions(campaignId: String) {
		launchCampaignObserver(
			campaignId = campaignId,
			errorPrefix = "Failed to load sessions",
			assignJob = { sessionsJob = it }
		) {
			@OptIn(ExperimentalCoroutinesApi::class)
			getEncountersForCampaignUseCase(campaignId)
				.flatMapLatest { encounters ->
					encounters.combineSessions()
				}
				.collect { allSessions ->
					_uiState.update { it.copy(sessions = allSessions) }
				}
		}
	}

	/**
	 * Loads notes for a campaign.
	 */
	private fun loadCampaignNotes(campaignId: String) {
		launchCampaignObserver(
			campaignId = campaignId,
			errorPrefix = "Failed to load notes",
			assignJob = { notesJob = it }
		) {
			getNotesForCampaignUseCase(campaignId).collect { notes ->
				_uiState.update { it.copy(notes = notes) }
			}
		}
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	private fun List<Encounter>.combineSessions() = if (isEmpty()) {
		flowOf(emptyList())
	} else {
		combine(map { encounter -> getSessionsForEncounterUseCase(encounter.id) }) { arrays: Array<List<SessionRecord>> ->
			arrays.flatMap(List<SessionRecord>::toList).sortedByDescending(SessionRecord::date)
		}
	}

	fun clearError() {
		_uiState.update { it.copy(error = null) }
	}

	fun addCampaign(name: String, description: String) {
		launchActionWithError("Failed to add campaign") {
			addCampaignUseCase(name, description)
		}
	}

	fun addEncounter(campaignId: String, name: String) {
		launchActionWithError("Failed to add encounter") {
			addEncounterUseCase(campaignId, name)
		}
	}

	/**
	 * Creates a new encounter with selected monsters from the remote rules API.
	 * Fetches monster stats and adds them to the encounter automatically.
	 */
	fun addEncounterWithMonsters(
		campaignId: String,
		name: String,
		selectedMonsters: List<RemoteItem>
	) {
		viewModelScope.launch {
			try {
				// Create the encounter and get its ID
				val encounterId = addEncounterUseCase(campaignId, name)

				// Add the selected monsters to the encounter
				if (selectedMonsters.isNotEmpty()) {
					addMonstersToEncounterUseCase(encounterId, selectedMonsters)
					_uiState.update {
						it.copy(error = "Encounter created with ${selectedMonsters.size} monsters!")
					}
				}
			} catch (e: Exception) {
				_uiState.update {
					it.copy(error = "Failed to create encounter: ${e.message}")
				}
			}
		}
	}

	fun addNote(campaignId: String, content: String, type: String, extra: String = "") {
		launchActionWithError("Failed to add note") {
			val note = createNote(campaignId, content, type, extra)
			addNoteUseCase(note)
		}
	}

	fun updateNote(note: Note) {
		launchActionWithError("Failed to update note") {
			updateNoteUseCase(note)
		}
	}

	fun deleteNote(note: Note) {
		launchActionWithError("Failed to delete note") {
			deleteNoteUseCase(note)
		}
	}

	private fun createNote(campaignId: String, content: String, type: String, extra: String): Note {
		return when (type) {
			NOTE_TYPE_LORE -> Note.Lore(
				campaignId = campaignId,
				content = content,
				historicalEra = extra
			)

			NOTE_TYPE_NPC -> {
				val (faction, attitude) = parseNpcExtra(extra)
				Note.NPC(
					campaignId = campaignId,
					content = content,
					faction = faction,
					attitude = attitude
				)
			}

			NOTE_TYPE_LOCATION -> Note.Location(
				campaignId = campaignId,
				content = content,
				region = extra
			)

			else -> Note.General(campaignId = campaignId, content = content)
		}
	}

	override fun onCleared() {
		cancelCampaignObservers()
		super.onCleared()
	}
}

