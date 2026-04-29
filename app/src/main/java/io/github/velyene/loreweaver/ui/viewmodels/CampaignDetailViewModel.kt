/*
 * FILE: CampaignDetailViewModel.kt
 *
 * TABLE OF CONTENTS:
 * 1. ViewModel state and campaign selection
 * 2. Encounter, session, and note observation pipelines
 * 3. Error reporting and retry helpers
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
import io.github.velyene.loreweaver.ui.util.AppText
import io.github.velyene.loreweaver.ui.util.campaignNotFoundMessage
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
	private val getSessionsForEncounterUseCase: GetSessionsForEncounterUseCase,
	private val appText: AppText
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
					handleCampaignNotFound(campaignId)
					return@launch
				}

				_uiState.update { it.copy(selectedCampaign = campaign) }
				observeSelectedCampaign(campaignId)
				_uiState.update { it.copy(isLoading = false) }
			} catch (e: CancellationException) {
				throw e
			} catch (e: Exception) {
				reportError(
					message = formatCampaignError(appText, R.string.campaign_error_critical, e),
					onRetry = retrySelectCampaign(campaignId)
				)
			}
		}
	}

	fun clearError() {
		_uiState.update(CampaignDetailUiState::clearErrorState)
	}

	private fun reportError(message: String, onRetry: (() -> Unit)? = null) {
		_uiState.update { it.withError(message, onRetry) }
	}

	private fun retrySelectCampaign(campaignId: String): () -> Unit = { selectCampaign(campaignId) }

	private fun handleCampaignNotFound(campaignId: String) {
		reportError(
			message = campaignNotFoundMessage(appText),
			onRetry = retrySelectCampaign(campaignId)
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
		@androidx.annotation.StringRes errorPrefixResId: Int,
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
					message = formatCampaignError(appText, errorPrefixResId, e),
					onRetry = retrySelectCampaign(campaignId)
				)
			}
		}
		assignJob(job)
	}

	private fun loadCampaignEncounters(campaignId: String) {
		launchCampaignObserver(
			campaignId = campaignId,
			errorPrefixResId = R.string.campaign_error_load_encounters,
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
			errorPrefixResId = R.string.campaign_error_load_sessions,
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
			errorPrefixResId = R.string.campaign_error_load_notes,
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
		// Each encounter exposes its own live session flow. Combine them so the campaign detail
		// screen reacts to updates across all linked encounters and still shows one recency-sorted list.
		combine(map { encounter -> getSessionsForEncounterUseCase(encounter.id) }) { arrays: Array<List<SessionRecord>> ->
			arrays.flatMap(List<SessionRecord>::toList).sortedByDescending(SessionRecord::date)
		}
	}

	override fun onCleared() {
		cancelCampaignObservers()
		super.onCleared()
	}
}

