/*
 * FILE: CombatEncounterPresentationTransforms.kt
 *
 * TABLE OF CONTENTS:
 * 1. Encounter presentation transforms
 * 2. Turn-index normalization
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Encounter

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

internal fun normalizedTurnIndex(combatants: List<CombatantState>, requestedIndex: Int): Int {
	return if (combatants.isEmpty()) 0 else requestedIndex.coerceIn(0, combatants.lastIndex)
}

