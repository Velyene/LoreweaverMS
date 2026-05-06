/*
 * FILE: SavedEncounterPickerViewModel.kt
 *
 * TABLE OF CONTENTS:
 * 1. Saved Encounter Picker UiState
 * 2. Saved Encounter Loading and Search
 * 3. Saved Encounter Selection Actions
 */

package io.github.velyene.loreweaver.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterStatus
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.use_case.GetAllEncountersUseCase
import io.github.velyene.loreweaver.domain.use_case.GetAllSessionsUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCampaignsUseCase
import io.github.velyene.loreweaver.ui.util.UiText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SavedEncounterPickerItem(
	val id: String,
	val name: String,
	val campaignTitle: String?,
	val round: Int,
	val combatantCount: Int,
	val status: EncounterStatus,
	val notesPreview: String = "",
	val lastSavedAt: Long? = null,
)

enum class SavedEncounterStatusFilter {
	ALL,
	PENDING,
	ACTIVE,
}

data class SavedEncounterPickerUiState(
	val isLoading: Boolean = true,
	val searchQuery: String = "",
	val selectedStatusFilter: SavedEncounterStatusFilter = SavedEncounterStatusFilter.ALL,
	val encounters: List<SavedEncounterPickerItem> = emptyList(),
	val error: UiText? = null,
	val onRetry: (() -> Unit)? = null,
)

@HiltViewModel
class SavedEncounterPickerViewModel @Inject constructor(
	private val getAllEncountersUseCase: GetAllEncountersUseCase,
	private val getCampaignsUseCase: GetCampaignsUseCase,
	private val getAllSessionsUseCase: GetAllSessionsUseCase,
) : ViewModel() {
	private val _uiState = MutableStateFlow(SavedEncounterPickerUiState())
	val uiState: StateFlow<SavedEncounterPickerUiState> = _uiState.asStateFlow()
	private var allEncounterItems: List<SavedEncounterPickerItem> = emptyList()

	init {
		observeSavedEncounters()
	}

	fun updateSearchQuery(query: String) {
		_uiState.update { state ->
			state.copy(
				searchQuery = query,
				encounters = filterItems(
					items = allEncounterItems,
					query = query,
					statusFilter = state.selectedStatusFilter,
				),
			)
		}
	}

	fun updateStatusFilter(statusFilter: SavedEncounterStatusFilter) {
		_uiState.update { state ->
			state.copy(
				selectedStatusFilter = statusFilter,
				encounters = filterItems(
					items = allEncounterItems,
					query = state.searchQuery,
					statusFilter = statusFilter,
				),
			)
		}
	}

	fun clearError() {
		_uiState.update { it.copy(error = null, onRetry = null) }
	}

	private fun observeSavedEncounters() {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true, error = null, onRetry = null) }
			try {
				combine(
					getAllEncountersUseCase(),
					getCampaignsUseCase(),
					getAllSessionsUseCase(),
				) { encounters, campaigns, sessions ->
					val campaignTitles = campaigns.associateBy({ it.id }, { it.title })
					val latestSessionByEncounterId = sessions
						.filter { session -> !session.encounterId.isNullOrBlank() }
						.groupBy { session -> session.encounterId.orEmpty() }
						.mapValues { (_, encounterSessions) ->
							encounterSessions.maxByOrNull(SessionRecord::date)
						}
					encounters
						.map { encounter ->
							encounter.toPickerItem(
								campaignTitle = campaignTitles[encounter.campaignId],
								latestSession = latestSessionByEncounterId[encounter.id],
							)
						}
						.sortedWith(
							compareByDescending<SavedEncounterPickerItem> { it.status == EncounterStatus.ACTIVE }
								.thenByDescending { it.lastSavedAt ?: 0L }
								.thenBy(String.CASE_INSENSITIVE_ORDER) { it.name }
						)
				}.collect { items ->
					allEncounterItems = items
					val state = _uiState.value
					_uiState.update {
						it.copy(
							isLoading = false,
							encounters = filterItems(
								items = items,
								query = state.searchQuery,
								statusFilter = state.selectedStatusFilter,
							),
						)
					}
				}
			} catch (e: CancellationException) {
				throw e
			} catch (e: Exception) {
				_uiState.update {
					it.copy(
						isLoading = false,
						error = UiText.StringResource(R.string.dm_saved_encounters_load_failed),
						onRetry = ::observeSavedEncounters,
					)
				}
			}
		}
	}

	private fun Encounter.toPickerItem(
		campaignTitle: String?,
		latestSession: SessionRecord?,
	): SavedEncounterPickerItem {
		return SavedEncounterPickerItem(
			id = id,
			name = name,
			campaignTitle = campaignTitle,
			round = currentRound,
			combatantCount = participants.size,
			status = status,
			notesPreview = encounterInfoDisplayText(notes),
			lastSavedAt = latestSession?.date,
		)
	}

	private fun filterItems(
		items: List<SavedEncounterPickerItem>,
		query: String,
		statusFilter: SavedEncounterStatusFilter,
	): List<SavedEncounterPickerItem> {
		return items.filter { item ->
			val statusMatches = when (statusFilter) {
				SavedEncounterStatusFilter.ALL -> true
				SavedEncounterStatusFilter.PENDING -> item.status == EncounterStatus.PENDING
				SavedEncounterStatusFilter.ACTIVE -> item.status == EncounterStatus.ACTIVE
			}
			val queryMatches = query.isBlank() ||
				item.name.contains(query, ignoreCase = true) ||
				(item.campaignTitle?.contains(query, ignoreCase = true) == true) ||
				item.notesPreview.contains(query, ignoreCase = true)
			statusMatches && queryMatches
		}
	}
}
