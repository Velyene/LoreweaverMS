/*
 * FILE: CombatUiStateTransforms.kt
 *
 * TABLE OF CONTENTS:
 * 1. CombatUiState turn helpers
 * 2. Selected-target helpers
 * 3. Condition-duration utilities
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.model.CombatantState

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


