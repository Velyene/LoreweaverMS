/*
 * FILE: LiveTrackerModels.kt
 *
 * TABLE OF CONTENTS:
 * 1. Participant and tracker UI models
 * 2. Callback contracts
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.ui.viewmodels.ActionResolutionType
import io.github.velyene.loreweaver.ui.viewmodels.CombatTurnStep
import io.github.velyene.loreweaver.ui.viewmodels.PendingTurnAction

internal data class LiveParticipantUiModel(
	val combatant: CombatantState,
	val typeLabel: String,
	val isPlayer: Boolean,
	val isEliminated: Boolean,
	val notes: String,
	val persistentConditions: Set<String>,
	val actionLabels: List<String>,
	val resourceLines: List<String>
)

internal data class LiveTrackerViewState(
	val encounterName: String,
	val encounterNotes: String,
	val round: Int,
	val combatants: List<CombatantState>,
	val availableCharacters: List<CharacterEntry>,
	val turnIndex: Int,
	val statuses: List<String>,
	val turnStep: CombatTurnStep,
	val pendingAction: PendingTurnAction?,
	val selectedTargetId: String?
)

internal data class LiveTrackerCallbacks(
	val onSelectAction: (String) -> Unit,
	val onSelectTarget: (String) -> Unit,
	val onApplyActionResult: (ActionResolutionType, Int?) -> Unit,
	val onClearPendingTurn: () -> Unit,
	val onNextTurn: () -> Unit,
	val onHpChange: (characterId: String, delta: Int) -> Unit,
	val onAddCondition: (characterId: String, condition: String, duration: Int?, persistsAcrossEncounters: Boolean) -> Unit,
	val onRemoveCondition: (characterId: String, conditionName: String) -> Unit,
	val onEnd: () -> Unit
)

internal data class LiveTrackerUiState(
	val participants: List<LiveParticipantUiModel>,
	val currentParticipant: LiveParticipantUiModel?,
	val selectedTarget: LiveParticipantUiModel?,
	val targetableParticipants: List<LiveParticipantUiModel>,
	val selectableTargetIds: Set<String>,
	val secondaryPartyMembers: List<LiveParticipantUiModel>,
	val enemies: List<LiveParticipantUiModel>
)

internal data class CurrentParticipantPanelState(
	val encounterName: String,
	val participant: LiveParticipantUiModel?,
	val pendingAction: PendingTurnAction?,
	val selectedTarget: LiveParticipantUiModel?,
	val turnStep: CombatTurnStep,
	val targetableParticipants: List<LiveParticipantUiModel>
)

