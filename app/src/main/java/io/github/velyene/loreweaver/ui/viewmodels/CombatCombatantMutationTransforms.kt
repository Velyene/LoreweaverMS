/*
 * FILE: CombatCombatantMutationTransforms.kt
 *
 * TABLE OF CONTENTS:
 * 1. Combatant list mutation transforms
 * 2. HP transforms
 *
 * Note: withRemovedCombatant, withUpdatedTempHp → CombatRosterSupport.kt
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.model.CombatantState

internal fun CombatUiState.withAddedCombatants(additionalCombatants: List<CombatantState>): CombatUiState {
	val updatedCombatants = (combatants + additionalCombatants).distinctBy(CombatantState::characterId)
	return copy(
		combatants = updatedCombatants,
		currentTurnIndex = normalizedTurnIndex(updatedCombatants, currentTurnIndex)
	)
}

internal fun CombatUiState.withAddedCombatant(combatant: CombatantState): CombatUiState {
	val updatedCombatants = combatants + combatant
	return copy(
		combatants = updatedCombatants,
		currentTurnIndex = normalizedTurnIndex(updatedCombatants, currentTurnIndex)
	)
}

internal fun CombatUiState.withUpdatedCombatant(
	characterId: String,
	transform: (CombatantState) -> CombatantState
): CombatUiState {
	return copy(
		combatants = combatants.map { combatant ->
			if (combatant.characterId == characterId) transform(combatant) else combatant
		}
	)
}

internal fun CombatUiState.applyHpDelta(characterId: String, delta: Int): Pair<CombatUiState, HpChangeResult>? {
	val combatant = combatants.find { it.characterId == characterId } ?: return null
	val newHp = (combatant.currentHp + delta).coerceIn(0, combatant.maxHp)
	val updatedState = copy(
		combatants = combatants.map { currentCombatant ->
			if (currentCombatant.characterId == characterId) {
				currentCombatant.copy(currentHp = newHp)
			} else {
				currentCombatant
			}
		}
	)
	return updatedState to HpChangeResult(
		combatant = combatant,
		oldHp = combatant.currentHp,
		newHp = newHp
	)
}

