/*
 * FILE: LiveTrackerView.kt
 *
 * TABLE OF CONTENTS:
 * 1. Live tracker root entry point
 * 2. Main scrollable content list
 * 3. Main encounter panels
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.ui.screens.visibleVerticalScrollbar

internal const val LIVE_TRACKER_CONTENT_LIST_TAG = "live_tracker_content_list"

@Composable
internal fun LiveTrackerView(
	state: LiveTrackerViewState,
	callbacks: LiveTrackerCallbacks
) {
	val labels = rememberLiveTrackerLabels()
	val participants = rememberLiveParticipants(
		combatants = state.combatants,
		availableCharacters = state.availableCharacters,
		labels = labels,
	)
	val participantIds = remember(participants) {
		participants.map { it.combatant.characterId }
	}
	val currentParticipant = remember(participants, state.turnIndex) {
		participants.getOrNull(state.turnIndex)
	}
	val nextParticipant = remember(participants, state.turnIndex) {
		if (participants.size > 1) {
			upcomingTurnWindow(participants, state.turnIndex, windowSize = 2).getOrNull(1)
		} else {
			null
		}
	}
	val selectedTarget = remember(participants, state.selectedTargetId, currentParticipant, state.pendingAction) {
		participants.firstOrNull { it.combatant.characterId == state.selectedTargetId }
			?: if (state.pendingAction?.allowsSelfTarget == true) currentParticipant else null
	}
	val targetableParticipants = remember(participants, currentParticipant, state.pendingAction) {
		participants.filter { participant ->
			when {
				currentParticipant == null -> false
				state.pendingAction == null -> false
				state.pendingAction.allowsSelfTarget -> {
					!participant.isEliminated ||
						participant.combatant.characterId == currentParticipant.combatant.characterId
				}
				else -> {
					participant.combatant.characterId != currentParticipant.combatant.characterId &&
						!participant.isEliminated
				}
			}
		}
	}
	val selectableTargetIds = remember(targetableParticipants) {
		targetableParticipants.mapTo(mutableSetOf()) { it.combatant.characterId }
	}
	val rosterParticipants = remember(participants, currentParticipant) {
		participants.filterNot { it.combatant.characterId == currentParticipant?.combatant?.characterId }
	}
	val contentListState = rememberLazyListState()
	var focusedCombatantId by rememberSaveable { mutableStateOf<String?>(null) }
	val shouldAutoEnableCompactMode = participants.size >= COMPACT_BATTLE_MODE_THRESHOLD
	val isCompactBattleMode = shouldAutoEnableCompactMode

	LaunchedEffect(state.turnIndex, state.selectedTargetId, participantIds) {
		focusedCombatantId = when {
			state.selectedTargetId != null && participantIds.contains(state.selectedTargetId) -> state.selectedTargetId
			currentParticipant != null -> currentParticipant.combatant.characterId
			focusedCombatantId != null && participantIds.contains(focusedCombatantId) -> focusedCombatantId
			else -> participantIds.firstOrNull()
		}
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(horizontal = 16.dp, vertical = 20.dp)
			.testTag(LIVE_TRACKER_ROOT_TAG),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		TurnTrackerStrip(
			round = state.round,
			participants = participants,
			turnIndex = state.turnIndex,
			focusedCombatantId = focusedCombatantId,
			onFocusCombatant = { focusedCombatantId = it },
			modifier = Modifier.fillMaxWidth()
		)

		Spacer(modifier = Modifier.height(12.dp))
		LiveTrackerContentList(
			state = LiveTrackerContentListState(
				encounterName = state.encounterName,
				encounterNotes = state.encounterNotes,
				statuses = state.statuses,
				canGoToPreviousTurn = state.canGoToPreviousTurn,
				turnStep = state.turnStep,
				pendingAction = state.pendingAction,
				selectedTargetId = state.selectedTargetId,
				focusedCombatantId = focusedCombatantId,
				isCompactBattleMode = isCompactBattleMode,
				participants = participants,
				currentParticipant = currentParticipant,
				nextParticipant = nextParticipant,
				selectedTarget = selectedTarget,
				targetableParticipants = targetableParticipants,
				selectableTargetIds = selectableTargetIds,
				rosterParticipants = rosterParticipants
			),
			callbacks = callbacks,
			onFocusCombatant = { focusedCombatantId = it },
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f)
				.visibleVerticalScrollbar(contentListState),
			contentListState = contentListState
		)
	}
}

@Composable
private fun LiveTrackerContentList(
	state: LiveTrackerContentListState,
	callbacks: LiveTrackerCallbacks,
	onFocusCombatant: (String) -> Unit,
	modifier: Modifier = Modifier,
	contentListState: LazyListState
) {
	val partyParticipants = remember(state.participants) { state.participants.filter(LiveParticipantUiModel::isPlayer) }
	val enemyParticipants = remember(state.participants) { state.participants.filterNot(LiveParticipantUiModel::isPlayer) }
	val partyAlive = remember(partyParticipants) { partyParticipants.count { !it.isEliminated } }
	val enemyAlive = remember(enemyParticipants) { enemyParticipants.count { !it.isEliminated } }

	LazyColumn(
		state = contentListState,
		modifier = modifier.testTag(LIVE_TRACKER_CONTENT_LIST_TAG),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		item {
			BattlePulseCard(
				partyAlive = partyAlive,
				partyTotal = partyParticipants.size,
				enemyAlive = enemyAlive,
				enemyTotal = enemyParticipants.size,
				currentParticipantName = state.currentParticipant?.combatant?.name,
				selectedTargetName = state.selectedTarget?.combatant?.name,
				turnStep = state.turnStep,
				pendingActionName = state.pendingAction?.name,
				modifier = Modifier.fillMaxWidth()
			)
		}

		item {
			CurrentParticipantPanel(
				state = CurrentParticipantPanelState(
					encounterName = state.encounterName,
					participant = state.currentParticipant,
					nextParticipant = state.nextParticipant,
					canGoToPreviousTurn = state.canGoToPreviousTurn,
					pendingAction = state.pendingAction,
					selectedTarget = state.selectedTarget,
					turnStep = state.turnStep,
					targetableParticipants = state.targetableParticipants
				),
				callbacks = callbacks
			)
		}

		item {
			SecondaryPartyPanel(
				partyMembers = partyParticipants,
				currentParticipantId = state.currentParticipant?.combatant?.characterId,
				selectedTargetId = state.selectedTargetId,
				focusedCombatantId = state.focusedCombatantId,
				isCompactBattleMode = state.isCompactBattleMode,
				selectableTargetIds = state.selectableTargetIds,
				onSelectTarget = callbacks.onSelectTarget,
				onFocusCombatant = onFocusCombatant,
				onHpChange = callbacks.onHpChange,
			)
		}

		item {
			EnemyPanel(
				enemies = enemyParticipants,
				currentParticipantId = state.currentParticipant?.combatant?.characterId,
				selectedTargetId = state.selectedTargetId,
				focusedCombatantId = state.focusedCombatantId,
				isCompactBattleMode = state.isCompactBattleMode,
				selectableTargetIds = state.selectableTargetIds,
				onSelectTarget = callbacks.onSelectTarget,
				onFocusCombatant = onFocusCombatant,
				onHpChange = callbacks.onHpChange,
			)
		}

		item {
			BattlefieldRosterPanel(
				state = BattlefieldRosterPanelState(
					rosterParticipants = state.rosterParticipants,
					selection = BattlefieldRosterSelectionState(
						currentParticipantId = state.currentParticipant?.combatant?.characterId,
						selectedTargetId = state.selectedTargetId,
						focusedCombatantId = state.focusedCombatantId,
						isCompactBattleMode = state.isCompactBattleMode,
						selectableTargetIds = state.selectableTargetIds,
					),
				),
				callbacks = BattlefieldRosterPanelCallbacks(
					onSelectTarget = callbacks.onSelectTarget,
					onFocusCombatant = onFocusCombatant,
					onHpChange = callbacks.onHpChange,
					onSetHp = callbacks.onSetHp,
					onMarkDefeated = callbacks.onMarkDefeated,
					onAddParticipantNote = callbacks.onAddParticipantNote,
					onDuplicateEnemy = callbacks.onDuplicateEnemy,
					onRemoveCombatant = callbacks.onRemoveCombatant,
					onAddCondition = callbacks.onAddCondition,
					onRemoveCondition = callbacks.onRemoveCondition,
				),
			)
		}

		item {
			EncounterNotesCard(encounterNotes = state.encounterNotes)
		}

		item {
			CombatLogSection(
				encounterNotes = state.encounterNotes,
				statuses = state.statuses
			)
		}
	}
}

