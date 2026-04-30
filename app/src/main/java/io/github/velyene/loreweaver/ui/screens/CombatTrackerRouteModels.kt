/*
 * FILE: CombatTrackerRouteModels.kt
 *
 * TABLE OF CONTENTS:
 * 1. Tracker screen model
 * 2. Screen-model mapping helper
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.util.EncounterDifficultyResult
import io.github.velyene.loreweaver.ui.viewmodels.ActionResolutionType
import io.github.velyene.loreweaver.ui.viewmodels.CombatTurnStep
import io.github.velyene.loreweaver.ui.viewmodels.CombatUiState
import io.github.velyene.loreweaver.ui.viewmodels.CombatViewModel
import io.github.velyene.loreweaver.ui.viewmodels.EncounterLifecycle
import io.github.velyene.loreweaver.ui.viewmodels.PendingTurnAction

internal data class TrackerScreenModel(
	val isCombatActive: Boolean,
	val encounterId: String?,
	val encounterName: String,
	val encounterLifecycle: EncounterLifecycle,
	val notes: String,
	val combatants: List<CombatantState>,
	val availableCharacters: List<CharacterEntry>,
	val encounterDifficulty: EncounterDifficultyResult?,
	val round: Int,
	val turnIndex: Int,
	val statuses: List<String>,
	val turnStep: CombatTurnStep,
	val pendingAction: PendingTurnAction?,
	val selectedTargetId: String?,
	val onNotesChange: (String) -> Unit,
	val onStartEncounter: (String?) -> Unit,
	val onTogglePartyMember: (CharacterEntry) -> Unit,
	val onAddEnemy: (String, Int, Int) -> Unit,
	val onRemoveCombatant: (String) -> Unit,
	val onSelectAction: (String) -> Unit,
	val onSelectTarget: (String) -> Unit,
	val onApplyActionResult: (ActionResolutionType, Int?) -> Unit,
	val onClearPendingTurn: () -> Unit,
	val onNextTurn: () -> Unit,
	val onHpChange: (String, Int) -> Unit,
	val onAddCondition: (String, String, Int?, Boolean) -> Unit,
	val onRemoveCondition: (String, String) -> Unit,
	val onEndEncounter: () -> Unit
)

@Composable
internal fun rememberTrackerScreenModel(
	uiState: CombatUiState,
	encounterId: String?,
	viewModel: CombatViewModel,
	onEndEncounter: () -> Unit
): TrackerScreenModel {
	// The screen model snapshots the view-model state into a UI-focused contract so the
	// route can keep setup/live rendering logic simple and stable across recompositions.
	return remember(uiState, encounterId, viewModel, onEndEncounter) {
		TrackerScreenModel(
			isCombatActive = uiState.isCombatActive,
			encounterId = encounterId,
			encounterName = uiState.currentEncounterName,
			encounterLifecycle = uiState.encounterLifecycle,
			notes = uiState.encounterNotes,
			combatants = uiState.combatants,
			availableCharacters = uiState.availableCharacters,
			encounterDifficulty = uiState.encounterDifficulty,
			round = uiState.currentRound,
			turnIndex = uiState.currentTurnIndex,
			statuses = uiState.activeStatuses,
			turnStep = uiState.turnStep,
			pendingAction = uiState.pendingAction,
			selectedTargetId = uiState.selectedTargetId,
			onNotesChange = viewModel::updateNotes,
			onStartEncounter = viewModel::startEncounter,
			onTogglePartyMember = { character ->
				val alreadyAdded = uiState.combatants.any { it.characterId == character.id }
				if (alreadyAdded) {
					viewModel.removeCombatant(character.id)
				} else {
					viewModel.addParty(listOf(buildPartyCombatant(character)))
				}
			},
			onAddEnemy = viewModel::addEnemy,
			onRemoveCombatant = viewModel::removeCombatant,
			onSelectAction = viewModel::selectAction,
			onSelectTarget = viewModel::selectTarget,
			onApplyActionResult = viewModel::applyActionResult,
			onClearPendingTurn = viewModel::clearPendingTurn,
			onNextTurn = viewModel::nextTurn,
			onHpChange = viewModel::updateCombatantHp,
			onAddCondition = viewModel::addCondition,
			onRemoveCondition = viewModel::removeCondition,
			onEndEncounter = { viewModel.saveAndPauseEncounter(onEndEncounter) }
		)
	}
}

