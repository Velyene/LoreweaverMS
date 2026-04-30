/*
 * FILE: CombatTrackerRouteContent.kt
 *
 * TABLE OF CONTENTS:
 * 1. Content switcher
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.runtime.Composable
import io.github.velyene.loreweaver.ui.screens.tracker.live.LiveTrackerCallbacks
import io.github.velyene.loreweaver.ui.screens.tracker.live.LiveTrackerView
import io.github.velyene.loreweaver.ui.screens.tracker.live.LiveTrackerViewState
import io.github.velyene.loreweaver.ui.screens.tracker.setup.EncounterSetupView

@Composable
internal fun TrackerContent(
	model: TrackerScreenModel
) {
	// Setup and live tracking intentionally split here so each branch can evolve as an
	// independent screen while the route continues to own shared state wiring.
	if (!model.isCombatActive) {
		EncounterSetupView(
			notes = model.notes,
			combatants = model.combatants,
			availablePartyMembers = model.availableCharacters,
			encounterDifficulty = model.encounterDifficulty,
			onNotesChange = model.onNotesChange,
			onStart = { model.onStartEncounter(model.encounterId) },
			onTogglePartyMember = model.onTogglePartyMember,
			onAddEnemy = model.onAddEnemy,
			onRemoveCombatant = model.onRemoveCombatant
		)
		return
	}

	LiveTrackerView(
		state = LiveTrackerViewState(
			encounterName = model.encounterName,
			encounterNotes = model.notes,
			round = model.round,
			combatants = model.combatants,
			availableCharacters = model.availableCharacters,
			turnIndex = model.turnIndex,
			statuses = model.statuses,
			turnStep = model.turnStep,
			pendingAction = model.pendingAction,
			selectedTargetId = model.selectedTargetId
		),
		callbacks = LiveTrackerCallbacks(
			onSelectAction = model.onSelectAction,
			onSelectTarget = model.onSelectTarget,
			onApplyActionResult = model.onApplyActionResult,
			onClearPendingTurn = model.onClearPendingTurn,
			onNextTurn = model.onNextTurn,
			onHpChange = model.onHpChange,
			onAddCondition = model.onAddCondition,
			onRemoveCondition = model.onRemoveCondition,
			onEnd = model.onEndEncounter
		)
	)
}

