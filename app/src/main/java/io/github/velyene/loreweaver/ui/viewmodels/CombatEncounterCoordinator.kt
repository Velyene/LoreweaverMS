/*
 * FILE: CombatEncounterCoordinator.kt
 *
 * TABLE OF CONTENTS:
 * 1. Character observation and difficulty refresh
 * 2. Encounter loading and error handling
 * 3. Encounter start and pause persistence
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterStatus
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.use_case.GetActiveEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCharactersUseCase
import io.github.velyene.loreweaver.domain.use_case.GetEncounterByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetSessionsForEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertSessionRecordUseCase
import io.github.velyene.loreweaver.domain.use_case.SetActiveEncounterUseCase
import io.github.velyene.loreweaver.domain.util.Resource
import io.github.velyene.loreweaver.ui.util.AppText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val NO_ACTIVE_ENCOUNTER_ERROR_MESSAGE = "No active encounter found"

internal class CombatEncounterCoordinator(
	private val uiState: MutableStateFlow<CombatUiState>,
	private val scope: CoroutineScope,
	private val getCharactersUseCase: GetCharactersUseCase,
	private val getActiveEncounterUseCase: GetActiveEncounterUseCase,
	private val getEncounterByIdUseCase: GetEncounterByIdUseCase,
	private val getSessionsForEncounterUseCase: GetSessionsForEncounterUseCase,
	private val insertEncounterUseCase: InsertEncounterUseCase,
	private val setActiveEncounterUseCase: SetActiveEncounterUseCase,
	private val insertSessionRecordUseCase: InsertSessionRecordUseCase,
	private val appText: AppText
) {
	fun observeCharacters() {
		scope.launch {
			getCharactersUseCase().collect { characters ->
				uiState.update { it.copy(availableCharacters = characters) }
				updateEncounterDifficulty()
			}
		}
	}

	fun recalculateEncounterDifficulty() {
		updateEncounterDifficulty()
	}

	fun loadEncounter(encounterId: String? = null) {
		beginLoading()
		scope.launch {
			if (encounterId == null) {
				loadActiveEncounter()
			} else {
				loadSpecificEncounter(encounterId)
			}
		}
	}

	fun startEncounter(encounterId: String? = null) {
		if (uiState.value.combatants.isEmpty()) return

		val id = resolveEncounterId(encounterId, uiState.value.currentEncounterId)
		scope.launch {
			try {
				val currentState = uiState.value
				val existingEncounter = currentState.currentEncounterId?.let { getEncounterByIdUseCase(it) }
				val activeEncounter = buildStartedEncounter(id, currentState, existingEncounter)
				insertEncounterUseCase(activeEncounter)
				setActiveEncounterUseCase(id)
				uiState.update {
					it.copy(
						currentEncounterId = id,
						combatants = activeEncounter.participants,
						isCombatActive = true
					)
				}
			} catch (e: Exception) {
				reportError(formatCampaignError(appText, R.string.encounter_error_start, e))
			}
		}
	}

	fun saveAndPauseEncounter(onComplete: () -> Unit) {
		val state = uiState.value
		val encounterId = state.currentEncounterId ?: java.util.UUID.randomUUID().toString()
		val session = buildPausedSessionRecord(encounterId, state)

		scope.launch {
			uiState.update { it.copy(encounterLifecycle = EncounterLifecycle.PAUSED) }
			val existingEncounter = state.currentEncounterId?.let { getEncounterByIdUseCase(it) }
			insertEncounterUseCase(buildPausedEncounter(encounterId, state, existingEncounter))
			insertSessionRecordUseCase(session)
			setActiveEncounterUseCase("")
			onComplete()
		}
	}

	private fun updateEncounterDifficulty() {
		uiState.update {
			it.copy(encounterDifficulty = calculateEncounterDifficulty(it))
		}
	}

	private suspend fun loadActiveEncounter() {
		when (val result = getActiveEncounterUseCase()) {
			is Resource.Success -> handleActiveEncounterSuccess(result.data)
			is Resource.Error -> handleActiveEncounterError(result.message)
			is Resource.Loading -> Unit
		}
	}

	private fun handleActiveEncounterSuccess(data: Pair<Encounter, SessionRecord?>) {
		val (encounter, lastSession) = data
		if (encounter.status != EncounterStatus.ACTIVE) {
			uiState.update { it.copy(isLoading = false) }
			return
		}
		showEncounter(encounter, lastSession)
	}

	private fun handleActiveEncounterError(message: String?) {
		reportError(
			message = if (message == NO_ACTIVE_ENCOUNTER_ERROR_MESSAGE) null else message,
			onRetry = retryLoadEncounter(null)
		)
	}

	private suspend fun loadSpecificEncounter(encounterId: String) {
		try {
			val encounter = getEncounterByIdUseCase(encounterId)
				?: run {
					reportError(appText.getString(R.string.encounter_not_found_message))
					return
				}

			val sessions = getSessionsForEncounterUseCase(encounter.id).first()
			val lastSession = sessions.firstOrNull()
			showEncounter(encounter, lastSession)
		} catch (e: Exception) {
			reportError(
				message = formatCampaignError(appText, R.string.encounter_error_load, e),
				onRetry = retryLoadEncounter(encounterId)
			)
		}
	}

	private fun beginLoading() {
		uiState.update { it.beginLoading() }
	}

	private fun retryLoadEncounter(encounterId: String?): () -> Unit = { loadEncounter(encounterId) }

	private fun reportError(message: String?, onRetry: (() -> Unit)? = null) {
		uiState.update { it.withError(message, onRetry) }
	}

	private fun showEncounter(encounter: Encounter, lastSession: SessionRecord?) {
		val presentation = if (encounter.status == EncounterStatus.ACTIVE) {
			buildActiveEncounterPresentation(encounter, lastSession)
		} else {
			buildSetupEncounterPresentation(encounter, lastSession)
		}
		showEncounterPresentation(presentation)
	}

	private fun showEncounterPresentation(presentation: EncounterPresentation) {
		uiState.update {
			it.withEncounterPresentation(
				encounter = presentation.encounter,
				combatants = presentation.combatants,
				encounterLifecycle = presentation.encounterLifecycle,
				requestedTurnIndex = presentation.requestedTurnIndex,
				currentRound = presentation.currentRound,
				activeStatuses = presentation.activeStatuses,
				isCombatActive = presentation.isCombatActive,
				resetTransientTurnState = true
			)
		}
		updateEncounterDifficulty()
	}
}

