/*
 * FILE: CampaignDetailViewModel.kt
 *
 * TABLE OF CONTENTS:
 * 1. Class: CampaignDetailViewModel
 * 2. Value: getCampaignByIdUseCase
 * 3. Value: getEncountersForCampaignUseCase
 * 4. Value: getNotesForCampaignUseCase
 * 5. Value: getSessionsForEncounterUseCase
 * 6. Value: _uiState
 * 7. Value: uiState
 * 8. Value: encountersJob
 */

package io.github.velyene.loreweaver.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.use_case.GetCampaignByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetEncountersForCampaignUseCase
import io.github.velyene.loreweaver.domain.use_case.GetNotesForCampaignUseCase
import io.github.velyene.loreweaver.domain.use_case.GetSessionsForEncounterUseCase
import io.github.velyene.loreweaver.ui.util.CAMPAIGN_NOT_FOUND_MESSAGE
import io.github.velyene.loreweaver.ui.util.UiText
import kotlinx.coroutines.CancellationException
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

@HiltViewModel
class CampaignDetailViewModel @Inject constructor(
	private val getCampaignByIdUseCase: GetCampaignByIdUseCase,
	private val getEncountersForCampaignUseCase: GetEncountersForCampaignUseCase,
	private val getNotesForCampaignUseCase: GetNotesForCampaignUseCase,
	private val getSessionsForEncounterUseCase: GetSessionsForEncounterUseCase
) : ViewModel() {
	private val _uiState = MutableStateFlow(CampaignDetailUiState())
	val uiState: StateFlow<CampaignDetailUiState> = _uiState.asStateFlow()

	private var encountersJob: Job? = null
	private var sessionsJob: Job? = null
	private var notesJob: Job? = null

	fun selectCampaign(campaignId: String) {
		viewModelScope.launch {
			_uiState.update(CampaignDetailUiState::beginLoading)
			try {
				val campaign = getCampaignByIdUseCase(campaignId)
				if (campaign == null) {
					handleCampaignNotFound()
					return@launch
				}

				_uiState.update { it.copy(selectedCampaign = campaign) }
				observeSelectedCampaign(campaignId)
				_uiState.update { it.copy(isLoading = false) }
			} catch (e: CancellationException) {
				throw e
			} catch (e: Exception) {
				reportError(
					message = formatCampaignError(UiText.StringResource(R.string.error_critical_prefix), e),
					onRetry = retrySelectCampaign(campaignId)
				)
			}
		}
	}

	fun clearError() {
		_uiState.update(CampaignDetailUiState::clearErrorState)
	}

	private fun reportError(message: UiText, onRetry: (() -> Unit)? = null) {
		_uiState.update { it.withError(message, onRetry) }
	}

	private fun retrySelectCampaign(campaignId: String): () -> Unit = { selectCampaign(campaignId) }

	private fun handleCampaignNotFound() {
		reportError(
			message = CAMPAIGN_NOT_FOUND_MESSAGE,
			onRetry = null
		)
	}

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
		errorPrefix: UiText,
		assignJob: (Job) -> Unit,
		collector: suspend () -> Unit
	) {
		val job = viewModelScope.launch {
			try {
				collector()
			} catch (e: CancellationException) {
				throw e
			} catch (e: Exception) {
				reportError(
					message = formatCampaignError(errorPrefix, e),
					onRetry = retrySelectCampaign(campaignId)
				)
			}
		}
		assignJob(job)
	}

	private fun loadCampaignEncounters(campaignId: String) {
		launchCampaignObserver(
			campaignId = campaignId,
			errorPrefix = UiText.StringResource(R.string.error_load_encounters),
			assignJob = { encountersJob = it }
		) {
			getEncountersForCampaignUseCase(campaignId).collect { encounters ->
				_uiState.update { it.copy(linkedEncounters = encounters) }
			}
		}
	}

	private fun loadCampaignSessions(campaignId: String) {
		launchCampaignObserver(
			campaignId = campaignId,
			errorPrefix = UiText.StringResource(R.string.error_load_sessions),
			assignJob = { sessionsJob = it }
		) {
			@OptIn(ExperimentalCoroutinesApi::class)
			getEncountersForCampaignUseCase(campaignId)
				.flatMapLatest { encounters -> encounters.combineSessions() }
				.collect { allSessions ->
					_uiState.update { it.copy(sessions = allSessions) }
				}
		}
	}

	private fun loadCampaignNotes(campaignId: String) {
		launchCampaignObserver(
			campaignId = campaignId,
			errorPrefix = UiText.StringResource(R.string.error_load_notes),
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

	override fun onCleared() {
		cancelCampaignObservers()
		super.onCleared()
	}
}

