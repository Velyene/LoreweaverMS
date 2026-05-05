package io.github.velyene.loreweaver.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.use_case.GetActiveEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.GetAllSessionsUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCampaignsUseCase
import io.github.velyene.loreweaver.domain.use_case.NO_ACTIVE_ENCOUNTER_MESSAGE
import io.github.velyene.loreweaver.domain.util.Resource
import io.github.velyene.loreweaver.ui.util.UiText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CampaignListViewModel @Inject constructor(
	private val getCampaignsUseCase: GetCampaignsUseCase,
	private val getAllSessionsUseCase: GetAllSessionsUseCase,
	private val getActiveEncounterUseCase: GetActiveEncounterUseCase
) : ViewModel() {
	private val _uiState = MutableStateFlow(CampaignListUiState())
	val uiState: StateFlow<CampaignListUiState> = _uiState.asStateFlow()

	init {
		loadCampaigns()
		loadAllSessions()
		loadActiveEncounter()
	}

	fun clearError() {
		_uiState.update(CampaignListUiState::clearErrorState)
	}

	fun refreshActiveEncounter() {
		loadActiveEncounter()
	}

	private fun loadCampaigns() {
		viewModelScope.launch {
			_uiState.update(CampaignListUiState::beginLoading)
			try {
				getCampaignsUseCase().collect { campaigns ->
					_uiState.update { it.copy(campaigns = campaigns, isLoading = false) }
				}
			} catch (e: CancellationException) {
				throw e
			} catch (e: Exception) {
				reportError(
					formatCampaignError(UiText.StringResource(R.string.error_load_campaigns), e),
					::loadCampaigns
				)
			}
		}
	}

	private fun loadAllSessions() {
		viewModelScope.launch {
			_uiState.update {
				it.copy(
					sessionHistoryIsLoading = true,
					sessionHistoryOnRetry = null,
				)
			}
			try {
				getAllSessionsUseCase().collect { sessions ->
					_uiState.update {
						it.copy(
							sessions = sessions,
							sessionHistoryIsLoading = false,
							sessionHistoryOnRetry = null,
							latestCompletedSession = sessions
								.asSequence()
								.filter(SessionRecord::isCompleted)
								.maxByOrNull(SessionRecord::date),
						)
					}
				}
			} catch (e: CancellationException) {
				throw e
			} catch (e: Exception) {
				_uiState.update {
					it.copy(
						sessionHistoryIsLoading = false,
						sessionHistoryOnRetry = ::loadAllSessions,
					)
				}
			}
		}
	}

	private fun loadActiveEncounter() {
		viewModelScope.launch {
			when (val result = getActiveEncounterUseCase()) {
				is Resource.Success -> {
					val (encounter, lastSession) = result.data
					val currentCombatant = encounter.participants.getOrNull(encounter.currentTurnIndex)
					val latestChange = encounter.activeLog.lastOrNull() ?: lastSession?.log?.lastOrNull()
					_uiState.update {
						it.copy(
							hasActiveEncounter = true,
							activeEncounterName = encounter.name.ifBlank { null },
							activeEncounterRound = encounter.currentRound,
							activeEncounterCombatantCount = encounter.participants.size,
							activeEncounterTurnName = currentCombatant?.name,
							activeEncounterLatestChange = latestChange,
							activeEncounterNextStep = currentCombatant?.name?.let { combatantName ->
								"Resolve $combatantName's turn and keep initiative moving."
							} ?: "Open the tracker, verify the roster, and continue the session."
						)
					}
				}

				is Resource.Error -> {
					if (result.message == NO_ACTIVE_ENCOUNTER_MESSAGE) {
						clearActiveEncounterSummary()
					} else {
						_uiState.update {
							it.copy(
											error = UiText.DynamicString(result.message),
								onRetry = ::loadActiveEncounter
							)
						}
					}
				}

				is Resource.Loading -> Unit
			}
		}
	}

	private fun clearActiveEncounterSummary() {
		_uiState.update {
			it.copy(
				hasActiveEncounter = false,
				activeEncounterName = null,
				activeEncounterRound = null,
				activeEncounterCombatantCount = 0,
				activeEncounterTurnName = null,
				activeEncounterLatestChange = null,
				activeEncounterNextStep = null
			)
		}
	}

	private fun reportError(message: UiText, onRetry: (() -> Unit)? = null) {
		_uiState.update { it.withError(message, onRetry) }
	}
}

