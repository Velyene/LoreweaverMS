/*
 * FILE: CombatViewModel.kt
 *
 * TABLE OF CONTENTS:
 * 1. UI State Definition (CombatUiState)
 * 2. Combat View Model (CombatViewModel)
 *    a. Data Observation & Loading
 *       - loadEncounter (main entry point)
 *       - loadActiveEncounter (active encounter flow)
 *       - loadSpecificEncounter (ID-based flow)
 *       - handleActiveEncounterSuccess
 *       - handleActiveEncounterError
 *       - restoreCombatFromSnapshot
 *    b. Encounter Control (Start, Action, Next Turn)
 *    c. Persistence (Save & End)
 */

package io.github.velyene.loreweaver.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Condition
import io.github.velyene.loreweaver.domain.model.DurationType
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterSnapshot
import io.github.velyene.loreweaver.domain.model.LogEntry
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.use_case.GetActiveEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCharactersUseCase
import io.github.velyene.loreweaver.domain.use_case.GetEncounterByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetSessionsForEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertLogUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertSessionRecordUseCase
import io.github.velyene.loreweaver.domain.use_case.SetActiveEncounterUseCase
import io.github.velyene.loreweaver.domain.util.CharacterParty
import io.github.velyene.loreweaver.domain.util.EncounterDifficulty
import io.github.velyene.loreweaver.domain.util.EncounterDifficultyResult
import io.github.velyene.loreweaver.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CombatUiState(
	val isCombatActive: Boolean = false,
	val currentEncounterId: String? = null,
	val combatants: List<CombatantState> = emptyList(),
	val availableCharacters: List<CharacterEntry> = emptyList(),
	val currentTurnIndex: Int = 0,
	val currentRound: Int = 1,
	val encounterNotes: String = "",
	val activeStatuses: List<String> = emptyList(),
	val encounterDifficulty: EncounterDifficultyResult? = null,
	val isLoading: Boolean = false,
	val error: String? = null,
	val onRetry: (() -> Unit)? = null
)

@HiltViewModel
class CombatViewModel @Inject constructor(
	private val getCharactersUseCase: GetCharactersUseCase,
	private val getActiveEncounterUseCase: GetActiveEncounterUseCase,
	private val getEncounterByIdUseCase: GetEncounterByIdUseCase,
	private val getSessionsForEncounterUseCase: GetSessionsForEncounterUseCase,
	private val insertEncounterUseCase: InsertEncounterUseCase,
	private val setActiveEncounterUseCase: SetActiveEncounterUseCase,
	private val insertLogUseCase: InsertLogUseCase,
	private val insertSessionRecordUseCase: InsertSessionRecordUseCase
) : ViewModel() {
	private val _uiState = MutableStateFlow(CombatUiState())
	val uiState: StateFlow<CombatUiState> = _uiState.asStateFlow()

	init {
		observeCharacters()
	}

	private fun observeCharacters() {
		viewModelScope.launch {
			getCharactersUseCase().collect { characters ->
				_uiState.update { it.copy(availableCharacters = characters) }
				updateEncounterDifficulty()
			}
		}
	}

	/**
	 * Recalculates encounter difficulty based on current combatants and party.
	 */
	private fun updateEncounterDifficulty() {
		val state = _uiState.value
		val charactersById = state.availableCharacters.associateBy(CharacterEntry::id)
		val partyMembers = state.availableCharacters.filter { it.isAdventurer() }
		val enemies = state.combatants.filter { combatant ->
			charactersById[combatant.characterId]?.isAdventurer() == false
		}
		val enemyCRMap = enemies.mapNotNull { combatant ->
			charactersById[combatant.characterId]?.challengeRating?.let { cr ->
				combatant.characterId to cr
			}
		}.toMap()

		_uiState.update {
			it.copy(
				encounterDifficulty = EncounterDifficulty.calculateDifficulty(
					partyMembers = partyMembers,
					enemies = enemies,
					enemyCRMap = enemyCRMap
				)
			)
		}
	}

	fun loadEncounter(encounterId: String? = null) {
		beginLoading()
		viewModelScope.launch {
			if (encounterId == null) {
				loadActiveEncounter()
			} else {
				loadSpecificEncounter(encounterId)
			}
		}
	}

	/**
	 * Loads the currently active encounter from the repository.
	 * Handles Resource states and restores session snapshot if available.
	 */
	private suspend fun loadActiveEncounter() {
		when (val result = getActiveEncounterUseCase()) {
			is Resource.Success -> {
				handleActiveEncounterSuccess(result.data)
			}

			is Resource.Error -> {
				handleActiveEncounterError(result.message)
			}

			is Resource.Loading -> {
				// Loading state already handled at the start of loadEncounter
			}
		}
	}

	/**
	 * Handles successful retrieval of active encounter.
	 * Restores combat state from snapshot if available.
	 */
	private fun handleActiveEncounterSuccess(
		data: Pair<Encounter, SessionRecord?>
	) {
		val (encounter, lastSession) = data

		if (encounter.status != io.github.velyene.loreweaver.domain.model.EncounterStatus.ACTIVE) {
			_uiState.update { it.copy(isLoading = false) }
			return
		}

		showEncounter(encounter.id, lastSession)
	}

	/**
	 * Restores combat state from a session snapshot.
	 */
	private fun restoreCombatFromSnapshot(encounterId: String, session: SessionRecord) {
		val snapshot = session.snapshot ?: return

		_uiState.update {
			it.copy(
				isCombatActive = true,
				currentEncounterId = encounterId,
				combatants = snapshot.combatants,
				currentTurnIndex = snapshot.currentTurnIndex,
				currentRound = snapshot.currentRound,
				activeStatuses = session.log,
				isLoading = false
			)
		}
		updateEncounterDifficulty()
	}

	/**
	 * Handles errors when loading active encounter.
	 */
	private fun handleActiveEncounterError(message: String?) {
		reportError(
			message = if (message == "No active encounter found") null else message,
			onRetry = retryLoadEncounter(null)
		)
	}

	/**
	 * Loads a specific encounter by ID.
	 * Restores session snapshot if available.
	 */
	private suspend fun loadSpecificEncounter(encounterId: String) {
		try {
			val encounter = getEncounterByIdUseCase(encounterId)
				?: run {
					reportError("Encounter not found")
					return
				}

			val sessions = getSessionsForEncounterUseCase(encounter.id).first()
			val lastSession = sessions.firstOrNull()
			showEncounter(encounter.id, lastSession)
		} catch (e: Exception) {
			reportError(
				message = formatError("Failed to load encounter", e),
				onRetry = retryLoadEncounter(encounterId)
			)
		}
	}

	private fun beginLoading() {
		_uiState.update { it.copy(isLoading = true, error = null, onRetry = null) }
	}

	private fun retryLoadEncounter(encounterId: String?): () -> Unit =
		{ loadEncounter(encounterId) }

	private fun formatError(prefix: String, exception: Exception): String {
		return "$prefix: ${exceptionDetail(exception)}"
	}

	private fun reportError(message: String?, onRetry: (() -> Unit)? = null) {
		_uiState.update {
			it.copy(
				isLoading = false,
				error = message,
				onRetry = onRetry
			)
		}
	}

	private fun showEncounter(encounterId: String, lastSession: SessionRecord?) {
		if (lastSession?.snapshot != null) {
			restoreCombatFromSnapshot(encounterId, lastSession)
			return
		}

		_uiState.update {
			it.copy(
				currentEncounterId = encounterId,
				isCombatActive = true,
				isLoading = false
			)
		}
		updateEncounterDifficulty()
	}

	fun clearError() {
		_uiState.update { it.copy(error = null) }
	}

	fun updateNotes(newNotes: String) {
		_uiState.update { it.copy(encounterNotes = newNotes) }
	}

	fun startEncounter(encounterId: String? = null) {
		if (_uiState.value.combatants.isNotEmpty()) {
			val id =
				encounterId ?: _uiState.value.currentEncounterId ?: UUID.randomUUID().toString()
			viewModelScope.launch {
				try {
					// If it's a new encounter, we might need to insert it
					if (encounterId == null && _uiState.value.currentEncounterId == null) {
						insertEncounterUseCase(
							Encounter(
								id = id,
								name = "Quick Encounter ${System.currentTimeMillis()}",
								status = io.github.velyene.loreweaver.domain.model.EncounterStatus.ACTIVE
							)
						)
					}
					setActiveEncounterUseCase(id)
					_uiState.update {
						it.copy(
							currentEncounterId = id,
							combatants = it.combatants.sortedByDescending { c -> c.initiative },
							isCombatActive = true
						)
					}
				} catch (e: Exception) {
					reportError(formatError("Failed to start encounter", e))
				}
			}
		}
	}

	fun addParty(partyCombatants: List<CombatantState>) {
		_uiState.update { currentState ->
			val updatedList =
				(currentState.combatants + partyCombatants).distinctBy { it.characterId }
			currentState.copy(combatants = updatedList)
		}
		updateEncounterDifficulty()
	}

	fun addEnemy(name: String, hp: Int, initiative: Int) {
		val enemy = CombatantState(
			characterId = UUID.randomUUID().toString(),
			name = name,
			initiative = initiative,
			currentHp = hp,
			maxHp = hp
		)
		_uiState.update { it.copy(combatants = it.combatants + enemy) }
		updateEncounterDifficulty()
	}

	fun removeCombatant(characterId: String) {
		_uiState.update {
			it.copy(combatants = it.combatants.filter { c -> c.characterId != characterId })
		}
		updateEncounterDifficulty()
	}

	fun updateCombatantHp(characterId: String, delta: Int) {
		_uiState.update { state ->
			val updated = state.combatants.map { c ->
				if (c.characterId == characterId) {
					c.copy(currentHp = (c.currentHp + delta).coerceIn(0, c.maxHp))
				} else c
			}
			state.copy(combatants = updated)
		}
	}

	fun performAction(action: String) {
		val current = _uiState.value.combatants.getOrNull(_uiState.value.currentTurnIndex)
		if (current != null) {
			val msg = "${current.name} uses $action!"
			appendStatus(msg)
			viewModelScope.launch {
				insertLogUseCase(LogEntry(message = msg, type = "Combat"))
			}
		}
	}

	fun nextTurn() {
		_uiState.update { it.advanceTurn() }
	}

	/**
	 * Adds a condition to a combatant.
	 */
	fun addCondition(characterId: String, conditionName: String, duration: Int? = null) {
		val currentRound = _uiState.value.currentRound
		updateCombatant(characterId) { combatant ->
			combatant.copy(
				conditions = combatant.conditions + Condition(
					name = conditionName,
					duration = duration,
					durationType = if (duration != null) DurationType.ROUNDS else DurationType.ENCOUNTER,
					addedOnRound = currentRound
				)
			)
		}

		val combatant = _uiState.value.combatants.find { it.characterId == characterId }
		combatant?.let {
			val durationText = if (duration != null) " ($duration rounds)" else ""
			val msg = "${it.name} is now $conditionName$durationText"
			appendStatus(msg)
		}
	}

	/**
	 * Removes a condition from a combatant.
	 */
	fun removeCondition(characterId: String, conditionName: String) {
		updateCombatant(characterId) { combatant ->
			combatant.copy(conditions = combatant.conditions.filter { it.name != conditionName })
		}

		val combatant = _uiState.value.combatants.find { it.characterId == characterId }
		combatant?.let {
			appendStatus("${it.name} is no longer $conditionName")
		}
	}

	/**
	 * Decrements all condition durations and removes expired ones.
	 * Called at the start of each new round.
	 */
	private fun decrementConditionDurations(combatants: List<CombatantState>): Pair<List<CombatantState>, List<String>> {
		val expiredMessages = mutableListOf<String>()
		val updatedCombatants = combatants.map { combatant ->
			val updatedConditions = combatant.conditions.mapNotNull { condition ->
				when {
					condition.duration == null -> condition
					condition.duration > 1 -> condition.copy(duration = condition.duration - 1)
					else -> {
						expiredMessages += "${combatant.name}'s ${condition.name} condition has expired"
						null
					}
				}
			}
			combatant.copy(conditions = updatedConditions)
		}
		return updatedCombatants to expiredMessages
	}

	private fun updateCombatant(
		characterId: String,
		transform: (CombatantState) -> CombatantState
	) {
		_uiState.update { state ->
			state.copy(
				combatants = state.combatants.map { combatant ->
					if (combatant.characterId == characterId) transform(combatant) else combatant
				}
			)
		}
	}

	private fun appendStatus(message: String) {
		_uiState.update { it.copy(activeStatuses = it.activeStatuses + message) }
	}

	private fun CombatUiState.advanceTurn(): CombatUiState {
		if (combatants.isEmpty()) return this

		val nextIndex = currentTurnIndex + 1
		if (nextIndex < combatants.size) {
			return copy(currentTurnIndex = nextIndex)
		}

		val (updatedCombatants, expiredMessages) = decrementConditionDurations(combatants)
		return copy(
			currentTurnIndex = 0,
			currentRound = currentRound + 1,
			combatants = updatedCombatants,
			activeStatuses = activeStatuses + expiredMessages
		)
	}

	private fun CharacterEntry.isAdventurer(): Boolean = party == CharacterParty.ADVENTURERS

	/**
	 * Updates temp HP for a combatant.
	 */
	fun updateTempHp(characterId: String, tempHp: Int) {
		_uiState.update { state ->
			val updated = state.combatants.map { c ->
				if (c.characterId == characterId) {
					c.copy(tempHp = tempHp.coerceAtLeast(0))
				} else c
			}
			state.copy(combatants = updated)
		}
	}

	fun saveAndEndEncounter(onComplete: () -> Unit) {
		val state = _uiState.value
		val session = SessionRecord(
			encounterId = state.currentEncounterId,
			title = "Encounter Session - ${System.currentTimeMillis()}",
			log = state.activeStatuses,
			snapshot = EncounterSnapshot(
				combatants = state.combatants,
				currentTurnIndex = state.currentTurnIndex,
				currentRound = state.currentRound
			)
		)

		viewModelScope.launch {
			insertSessionRecordUseCase(session)
			onComplete()
		}
	}
}
