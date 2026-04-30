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
 *    c. Persistence (Save & Pause)
 */

package io.github.velyene.loreweaver.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Condition
import io.github.velyene.loreweaver.domain.model.DurationType
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterSnapshot
import io.github.velyene.loreweaver.domain.model.EncounterStatus
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
import io.github.velyene.loreweaver.domain.use_case.UpdateCharacterUseCase
import io.github.velyene.loreweaver.domain.util.CharacterParty
import io.github.velyene.loreweaver.domain.util.EncounterDifficulty
import io.github.velyene.loreweaver.domain.util.EncounterDifficultyResult
import io.github.velyene.loreweaver.domain.util.Resource
import io.github.velyene.loreweaver.ui.util.AppText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

private const val NO_ACTIVE_ENCOUNTER_ERROR_MESSAGE = "No active encounter found"

data class CombatUiState(
	val isCombatActive: Boolean = false,
	val currentEncounterId: String? = null,
	val currentEncounterName: String = "",
	val encounterLifecycle: EncounterLifecycle = EncounterLifecycle.DRAFT,
	val combatants: List<CombatantState> = emptyList(),
	val availableCharacters: List<CharacterEntry> = emptyList(),
	val currentTurnIndex: Int = 0,
	val currentRound: Int = 1,
	val encounterNotes: String = "",
	val activeStatuses: List<String> = emptyList(),
	val turnStep: CombatTurnStep = CombatTurnStep.SELECT_ACTION,
	val pendingAction: PendingTurnAction? = null,
	val selectedTargetId: String? = null,
	val encounterDifficulty: EncounterDifficultyResult? = null,
	val isLoading: Boolean = false,
	val error: String? = null,
	val onRetry: (() -> Unit)? = null
)

enum class CombatTurnStep {
	SELECT_ACTION,
	SELECT_TARGET,
	APPLY_RESULT,
	READY_TO_END
}

enum class EncounterLifecycle {
	DRAFT,
	ACTIVE,
	PAUSED,
	COMPLETED,
	ARCHIVED
}

enum class ActionResolutionType {
	MISS,
	DAMAGE,
	HEAL
}

data class PendingTurnAction(
	val name: String,
	val isAttack: Boolean,
	val allowsSelfTarget: Boolean
)

private data class HpChangeResult(
	val combatant: CombatantState,
	val oldHp: Int,
	val newHp: Int
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
	private val insertSessionRecordUseCase: InsertSessionRecordUseCase,
	private val updateCharacterUseCase: UpdateCharacterUseCase
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
		val selectedCharacters = state.combatants.mapNotNull { combatant ->
			charactersById[combatant.characterId]
		}
		val partyMembers = selectedCharacters.filter { it.isAdventurer() }
		val enemies = state.combatants.filter { combatant ->
			charactersById[combatant.characterId]?.isAdventurer() != true
		}
		val enemyCRMap = enemies.associate { combatant ->
			combatant.characterId to (charactersById[combatant.characterId]?.challengeRating ?: 0.0)
		}

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

		if (encounter.status != EncounterStatus.ACTIVE) {
			_uiState.update { it.copy(isLoading = false) }
			return
		}

		showEncounter(encounter, lastSession)
	}

	/**
	 * Restores combat state from a session snapshot.
	 */
	private fun restoreCombatFromSnapshot(encounter: Encounter, session: SessionRecord) {
		val snapshot = session.snapshot ?: return
		updateEncounterPresentation(
			encounter = encounter,
			combatants = snapshot.combatants,
			encounterLifecycle = EncounterLifecycle.ACTIVE,
			requestedTurnIndex = snapshot.currentTurnIndex,
			currentRound = snapshot.currentRound,
			activeStatuses = session.log,
			isCombatActive = true
		)
		updateEncounterDifficulty()
	}

	/**
	 * Handles errors when loading active encounter.
	 */
	private fun handleActiveEncounterError(message: String?) {
		reportError(
			message = if (message == NO_ACTIVE_ENCOUNTER_ERROR_MESSAGE) null else message,
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
		_uiState.update { it.beginLoading() }
	}

	private fun retryLoadEncounter(encounterId: String?): () -> Unit =
		{ loadEncounter(encounterId) }

	private fun reportError(message: String?, onRetry: (() -> Unit)? = null) {
		_uiState.update { it.withError(message, onRetry) }
	}

	private fun showEncounter(encounterId: String, lastSession: SessionRecord?) {
		if (lastSession?.snapshot != null) {
			// A saved snapshot is the source of truth for an in-progress encounter because it preserves
			// turn order, HP, and conditions that the bare encounter record does not store.
			restoreCombatFromSnapshot(encounterId, lastSession)
			return
		}

		showSetupEncounter(encounter, lastSession)
	}

	private fun showActiveEncounter(encounter: Encounter, lastSession: SessionRecord?) {
		if (lastSession?.snapshot != null) {
			restoreCombatFromSnapshot(encounter, lastSession)
			return
		}
		updateEncounterPresentation(
			encounter = encounter,
			combatants = encounter.participants,
			encounterLifecycle = EncounterLifecycle.ACTIVE,
			requestedTurnIndex = encounter.currentTurnIndex,
			currentRound = encounter.currentRound,
			activeStatuses = lastSession?.log ?: emptyList(),
			isCombatActive = true
		)
		updateEncounterDifficulty()
	}

	private fun showSetupEncounter(encounter: Encounter, lastSession: SessionRecord?) {
		val encounterCombatants = lastSession?.snapshot?.combatants ?: encounter.participants
		updateEncounterPresentation(
			encounter = encounter,
			combatants = encounterCombatants,
			encounterLifecycle = if (lastSession != null) EncounterLifecycle.PAUSED else EncounterLifecycle.DRAFT,
			requestedTurnIndex = lastSession?.snapshot?.currentTurnIndex ?: encounter.currentTurnIndex,
			currentRound = encounter.currentRound,
			activeStatuses = lastSession?.log ?: emptyList(),
			isCombatActive = false
		)
		updateEncounterDifficulty()
	}

	fun clearError() {
		_uiState.update { it.clearErrorState() }
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
					// Mark the encounter active before publishing UI state so repository-backed screens and
					// process restores observe the same active encounter ID the tracker is about to use.
					setActiveEncounterUseCase(id)
					_uiState.update {
						it.copy(
							currentEncounterId = id,
							combatants = it.combatants.sortedByDescending { c -> c.initiative },
							isCombatActive = true
						)
					}
				} catch (e: Exception) {
					reportError(formatCampaignError(appText, R.string.encounter_error_start, e))
				}
			}
		}
	}

	fun addParty(partyCombatants: List<CombatantState>) {
		_uiState.update { currentState ->
			val updatedList =
				(currentState.combatants + partyCombatants).distinctBy { it.characterId }
			currentState.copy(
				combatants = updatedList,
				currentTurnIndex = normalizedTurnIndex(updatedList, currentState.currentTurnIndex)
			)
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
		_uiState.update { state ->
			val updatedCombatants = state.combatants + enemy
			state.copy(
				combatants = updatedCombatants,
				currentTurnIndex = normalizedTurnIndex(updatedCombatants, state.currentTurnIndex)
			)
		}
		updateEncounterDifficulty()
	}

	fun removeCombatant(characterId: String) {
		_uiState.update { state ->
			val updatedCombatants = state.combatants.filter { combatant -> combatant.characterId != characterId }
			val removedCurrentCombatant = state.currentCombatant()?.characterId == characterId
			val shouldClearPendingTurn =
				updatedCombatants.isEmpty() || removedCurrentCombatant || state.selectedTargetId == characterId
			val requestedIndex = if (removedCurrentCombatant && state.currentTurnIndex > 0) {
				state.currentTurnIndex - 1
			} else {
				state.currentTurnIndex
			}
			val baseState = if (shouldClearPendingTurn) {
				state.clearPendingTurnState()
			} else {
				state
			}
			baseState.copy(
				combatants = updatedCombatants,
				currentTurnIndex = normalizedTurnIndex(updatedCombatants, requestedIndex)
			)
		}
		updateEncounterDifficulty()
	}

	fun updateCombatantHp(characterId: String, delta: Int) {
		val hpChange = applyHpDelta(characterId, delta) ?: return
		appendHpStatus(hpChange = hpChange, delta = delta)
		appendDefeatRecoveryStatus(hpChange)
	}

	fun selectAction(action: String) {
		val state = _uiState.value
		val current = state.currentCombatant() ?: return
		val pendingAction = resolvePendingAction(current.characterId, action)
		val targetCandidates = targetCandidates(state, pendingAction)

		_uiState.update {
			it.copy(
				pendingAction = pendingAction,
				selectedTargetId = null,
				turnStep = if (targetCandidates.isEmpty()) {
					CombatTurnStep.APPLY_RESULT
				} else {
					CombatTurnStep.SELECT_TARGET
				}
			)
		}
	}

	fun selectTarget(targetId: String) {
		val state = _uiState.value
		val pendingAction = state.pendingAction ?: return
		val validTarget = targetCandidates(state, pendingAction)
			.any { it.characterId == targetId }
		if (!validTarget) return

		_uiState.update {
			it.copy(
				selectedTargetId = targetId,
				turnStep = CombatTurnStep.APPLY_RESULT
			)
		}
	}

	fun clearPendingTurn() {
		_uiState.update { it.clearPendingTurnState() }
	}

	fun applyActionResult(resultType: ActionResolutionType, amount: Int? = null) {
		val state = _uiState.value
		val actor = state.currentCombatant() ?: return
		val pendingAction = state.pendingAction ?: return
		val target = state.resolveSelectedTarget() ?: return

		when (resultType) {
			ActionResolutionType.MISS -> {
				appendAndPersistStatus(
					"${actor.name} used ${pendingAction.name} on ${target.name}, but missed."
				)
			}

			ActionResolutionType.DAMAGE -> {
				val damage = amount?.coerceAtLeast(0) ?: return
				val hpChange = applyHpDelta(target.characterId, -damage) ?: return
				appendAndPersistStatus(
					"${actor.name} used ${pendingAction.name} on ${target.name} for $damage damage."
				)
				appendDefeatRecoveryStatus(hpChange)
			}

			ActionResolutionType.HEAL -> {
				val healing = amount?.coerceAtLeast(0) ?: return
				val hpChange = applyHpDelta(target.characterId, healing) ?: return
				appendAndPersistStatus(
					"${actor.name} used ${pendingAction.name} on ${target.name}, restoring $healing HP."
				)
				appendDefeatRecoveryStatus(hpChange)
			}
		}

		_uiState.update {
			it.copy(
				turnStep = CombatTurnStep.READY_TO_END,
				pendingAction = pendingAction,
				selectedTargetId = target.characterId
			)
		}
	}

	fun nextTurn() {
		_uiState.update { it.clearPendingTurnState().advanceTurn() }
	}

	/**
	 * Adds a condition to a combatant.
	 */
	fun addCondition(
		characterId: String,
		conditionName: String,
		duration: Int? = null,
		persistsAcrossEncounters: Boolean = false
	) {
		val currentRound = _uiState.value.currentRound
		if (persistsAcrossEncounters) {
			updatePersistedCharacterCondition(characterId, conditionName, shouldAddCondition = true)
		} else {
			updateCombatant(characterId) { combatant ->
				combatant.copy(
					conditions = combatant.conditions + buildEncounterCondition(
						conditionName = conditionName,
						duration = duration,
						currentRound = currentRound
					)
				)
			}
		}
		if (persistsAcrossEncounters) {
			updatePersistedCharacterCondition(
				characterId = characterId,
				conditionName = conditionName,
				shouldAddCondition = true
			)
		}

		val combatant = _uiState.value.combatants.find { it.characterId == characterId }
		combatant?.let {
			val durationText = if (duration != null) " ($duration rounds)" else ""
			val persistenceText = if (persistsAcrossEncounters) " (persistent)" else ""
			val msg = "${it.name} is now $conditionName$durationText$persistenceText"
			appendStatus(msg)
		}
	}

	/**
	 * Removes a condition from a combatant.
	 */
	fun removeCondition(
		characterId: String,
		conditionName: String,
		removePersistentCondition: Boolean = false
	) {
		updateCombatant(characterId) { combatant ->
			combatant.copy(conditions = combatant.conditions.filter { it.name != conditionName })
		}
		if (removePersistentCondition) {
			updatePersistedCharacterCondition(
				characterId = characterId,
				conditionName = conditionName,
				shouldAddCondition = false
			)
		}

	private fun buildConditionStatusMessage(
		combatantName: String,
		conditionName: String,
		duration: Int? = null,
		persistsAcrossEncounters: Boolean = false,
		wasConditionAdded: Boolean
	): String {
		if (!wasConditionAdded) {
			return "$combatantName is no longer $conditionName"
		}
		val durationText = if (duration != null) " ($duration rounds)" else ""
		val persistenceText = if (persistsAcrossEncounters) " (persistent)" else durationText
		return "$combatantName is now $conditionName$persistenceText"
	}

	private fun updatePersistedCharacterCondition(
		characterId: String,
		conditionName: String,
		shouldAddCondition: Boolean
	) {
		val character = _uiState.value.availableCharacters.firstOrNull { it.id == characterId } ?: return
		viewModelScope.launch {
			val updatedCharacter = character.copy(
				persistentConditions = if (shouldAddCondition) {
					character.persistentConditions + conditionName
				} else {
					character.persistentConditions - conditionName
				}
			)
			updateCharacterUseCase(updatedCharacter)
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

	private fun combatantById(characterId: String): CombatantState? =
		_uiState.value.combatants.find { it.characterId == characterId }

	private fun appendStatus(message: String) {
		_uiState.update { it.copy(activeStatuses = it.activeStatuses + message) }
	}

	private fun appendAndPersistStatus(message: String) {
		appendStatus(message)
		viewModelScope.launch {
			insertLogUseCase(LogEntry(message = message, type = "Combat"))
		}
	}

	private fun appendHpStatus(hpChange: HpChangeResult, delta: Int) {
		if (delta == 0) return

		val message = if (delta < 0) {
			"${hpChange.combatant.name} takes ${-delta} damage (${hpChange.newHp}/${hpChange.combatant.maxHp} HP)"
		} else {
			"${hpChange.combatant.name} recovers $delta HP (${hpChange.newHp}/${hpChange.combatant.maxHp} HP)"
		}
		appendStatus(message)
	}

	private fun appendDefeatRecoveryStatus(hpChange: HpChangeResult) {
		when {
			hpChange.newHp == 0 && hpChange.oldHp > 0 -> {
				appendStatus("${hpChange.combatant.name} has been defeated")
			}

			hpChange.oldHp == 0 && hpChange.newHp > 0 -> {
				appendStatus("${hpChange.combatant.name} is back in the fight")
			}
		}
	}

	private fun applyHpDelta(characterId: String, delta: Int): HpChangeResult? {
		val combatant = _uiState.value.combatants.find { it.characterId == characterId } ?: return null
		val newHp = (combatant.currentHp + delta).coerceIn(0, combatant.maxHp)
		_uiState.update { state ->
			state.copy(
				combatants = state.combatants.map { currentCombatant ->
					if (currentCombatant.characterId == characterId) {
						currentCombatant.copy(currentHp = newHp)
					} else {
						currentCombatant
					}
				}
			)
		}
		return HpChangeResult(
			combatant = combatant,
			oldHp = combatant.currentHp,
			newHp = newHp
		)
	}

	private fun CombatUiState.advanceTurn(): CombatUiState {
		if (combatants.isEmpty()) return this

		val nextIndex = currentTurnIndex + 1
		if (nextIndex < combatants.size) {
			return copy(currentTurnIndex = nextIndex)
		}

		val (updatedCombatants, expiredMessages) = decrementConditionDurations(combatants)
		// Conditions tick only when the round wraps, matching the encounter-wide notion of
		// "one round has passed" instead of decrementing on every individual combatant turn.
		return copy(
			currentTurnIndex = 0,
			currentRound = currentRound + 1,
			combatants = updatedCombatants,
			activeStatuses = activeStatuses + expiredMessages + "Round ${currentRound + 1} begins"
		)
	}

	private fun CombatUiState.currentCombatant(): CombatantState? =
		combatants.getOrNull(currentTurnIndex)

	private fun CombatUiState.resolveSelectedTarget(): CombatantState? {
		val activePendingAction = pendingAction ?: return null
		val resolvedTargetId = selectedTargetId ?: if (activePendingAction.allowsSelfTarget) {
			currentCombatant()?.characterId
		} else {
			null
		}
		return combatants.firstOrNull { it.characterId == resolvedTargetId }
	}

	private fun CombatUiState.clearPendingTurnState(): CombatUiState = copy(
		turnStep = CombatTurnStep.SELECT_ACTION,
		pendingAction = null,
		selectedTargetId = null
	)

	private fun resolvePendingAction(actorId: String, actionName: String): PendingTurnAction {
		val actor = _uiState.value.availableCharacters.firstOrNull { it.id == actorId }
		val customAction = actor?.actions?.firstOrNull { it.name.equals(actionName, ignoreCase = true) }
		val isAttack = customAction?.isAttack ?: defaultActionIsAttack(actionName)
		return PendingTurnAction(
			name = customAction?.name ?: actionName,
			isAttack = isAttack,
			allowsSelfTarget = !isAttack
		)
	}

	private fun targetCandidates(
		state: CombatUiState,
		pendingAction: PendingTurnAction
	): List<CombatantState> {
		val currentCombatantId = state.currentCombatant()?.characterId
		return state.combatants.filter { combatant ->
			val isSelf = combatant.characterId == currentCombatantId
			val isEligibleTarget = pendingAction.allowsSelfTarget || !isSelf
			isEligibleTarget && (combatant.currentHp > 0 || isSelf)
		}
	}

	private fun normalizedTurnIndex(combatants: List<CombatantState>, requestedIndex: Int): Int {
		return if (combatants.isEmpty()) 0 else requestedIndex.coerceIn(0, combatants.lastIndex)
	}

	private fun resolveEncounterName(existingEncounter: Encounter?, currentEncounterName: String): String {
		return existingEncounter?.name ?: currentEncounterName.ifBlank {
			"Quick Encounter ${System.currentTimeMillis()}"
		}
	}

	private fun defaultActionIsAttack(actionName: String): Boolean {
		return when (actionName.lowercase()) {
			"dodge", "help", "heal", "hide" -> false
			else -> true
		}
	}

	private fun CharacterEntry.isAdventurer(): Boolean = party == CharacterParty.ADVENTURERS

	private fun CharacterEntry.withPersistedCondition(
		conditionName: String,
		shouldAddCondition: Boolean
	): CharacterEntry {
		return if (shouldAddCondition) {
			copy(activeConditions = activeConditions + conditionName)
		} else {
			copy(activeConditions = activeConditions - conditionName)
		}
	}

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

	fun saveAndPauseEncounter(onComplete: () -> Unit) {
		val state = _uiState.value
		val encounterId = state.currentEncounterId ?: UUID.randomUUID().toString()
		val session = SessionRecord(
			encounterId = encounterId,
			title = state.currentEncounterName.ifBlank {
				"Encounter Session - ${System.currentTimeMillis()}"
			},
			log = state.activeStatuses,
			snapshot = EncounterSnapshot(
				combatants = state.combatants,
				currentTurnIndex = state.currentTurnIndex,
				currentRound = state.currentRound
			)
		)

		viewModelScope.launch {
			_uiState.update { it.copy(encounterLifecycle = EncounterLifecycle.PAUSED) }
			val existingEncounter = state.currentEncounterId?.let { getEncounterByIdUseCase(it) }
			insertEncounterUseCase(
				Encounter(
					id = encounterId,
					campaignId = existingEncounter?.campaignId,
					name = resolveEncounterName(existingEncounter, state.currentEncounterName),
					notes = state.encounterNotes,
					status = EncounterStatus.PENDING,
					currentRound = state.currentRound,
					currentTurnIndex = state.currentTurnIndex,
					participants = state.combatants
				)
			)
			insertSessionRecordUseCase(session)
			onComplete()
		}
	}

	private fun CombatUiState.withEncounterPresentation(
		encounter: Encounter,
		combatants: List<CombatantState>,
		encounterLifecycle: EncounterLifecycle,
		requestedTurnIndex: Int,
		currentRound: Int,
		activeStatuses: List<String>,
		isCombatActive: Boolean,
		resetTransientTurnState: Boolean
	): CombatUiState {
		val presentedState = copy(
			currentEncounterId = encounter.id,
			currentEncounterName = encounter.name,
			encounterLifecycle = encounterLifecycle,
			combatants = combatants,
			currentTurnIndex = normalizedTurnIndex(combatants, requestedTurnIndex),
			currentRound = currentRound.coerceAtLeast(1),
			encounterNotes = encounter.notes,
			activeStatuses = activeStatuses,
			isCombatActive = isCombatActive,
			isLoading = false
		)
		return if (resetTransientTurnState) {
			presentedState.clearPendingTurnState()
		} else {
			presentedState
		}
	}
}
