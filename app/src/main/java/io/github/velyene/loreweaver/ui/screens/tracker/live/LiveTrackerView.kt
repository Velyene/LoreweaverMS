/*
 * FILE: LiveTrackerView.kt
 *
 * TABLE OF CONTENTS:
 * 1. Live tracker root entry point
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
	val trackerState = rememberLiveTrackerUiState(
		combatants = state.combatants,
		availableCharacters = state.availableCharacters,
		turnIndex = state.turnIndex,
		pendingAction = state.pendingAction,
		selectedTargetId = state.selectedTargetId
	)
	val participants = trackerState.participants
	val participantIds = remember(participants) {
		participants.map { it.combatant.characterId }
	}
	val contentListState = rememberLazyListState()
	var focusedCombatantId by rememberSaveable { mutableStateOf<String?>(null) }

	LaunchedEffect(state.turnIndex, state.selectedTargetId, participantIds) {
		focusedCombatantId = when {
			state.selectedTargetId != null && participantIds.contains(state.selectedTargetId) -> state.selectedTargetId
			trackerState.currentParticipant != null -> trackerState.currentParticipant.combatant.characterId
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
			onFocusCombatant = { id -> focusedCombatantId = id },
			modifier = Modifier.fillMaxWidth()
		)

		Spacer(modifier = Modifier.height(12.dp))
		LiveTrackerContentList(
			encounterName = state.encounterName,
			encounterNotes = state.encounterNotes,
			statuses = state.statuses,
			turnStep = state.turnStep,
			pendingAction = state.pendingAction,
			selectedTargetId = state.selectedTargetId,
			trackerState = trackerState,
			callbacks = callbacks,
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f)
				.visibleVerticalScrollbar(contentListState),
			contentListState = contentListState
		)
	}
}
