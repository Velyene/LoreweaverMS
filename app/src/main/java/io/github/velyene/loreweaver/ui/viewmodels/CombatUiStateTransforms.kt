/*
 * FILE: CombatUiStateTransforms.kt
 *
 * TABLE OF CONTENTS:
 * 1. CombatUiState turn helpers
 * 2. Target and encounter presentation helpers
 * 3. Condition-duration and turn-index utilities
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Encounter

internal fun CombatUiState.advanceTurn(): CombatUiState {
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
		activeStatuses = activeStatuses + expiredMessages + "Round ${currentRound + 1} begins"
	)
}

internal fun CombatUiState.currentCombatant(): CombatantState? =
	combatants.getOrNull(currentTurnIndex)

internal fun CombatUiState.resolveSelectedTarget(): CombatantState? {
	val activePendingAction = pendingAction ?: return null
	val resolvedTargetId = selectedTargetId ?: if (activePendingAction.allowsSelfTarget) {
		currentCombatant()?.characterId
	} else {
		null
	}
	return combatants.firstOrNull { it.characterId == resolvedTargetId }
}

internal fun CombatUiState.clearPendingTurnState(): CombatUiState = copy(
	turnStep = CombatTurnStep.SELECT_ACTION,
	pendingAction = null,
	selectedTargetId = null
)

internal fun CombatUiState.withEncounterPresentation(
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

internal fun decrementConditionDurations(combatants: List<CombatantState>): Pair<List<CombatantState>, List<String>> {
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

internal fun normalizedTurnIndex(combatants: List<CombatantState>, requestedIndex: Int): Int {
	return if (combatants.isEmpty()) 0 else requestedIndex.coerceIn(0, combatants.lastIndex)
}

