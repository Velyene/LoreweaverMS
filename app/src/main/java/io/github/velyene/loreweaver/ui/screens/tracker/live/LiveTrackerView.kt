/*
 * FILE: LiveTrackerView.kt
 *
 * TABLE OF CONTENTS:
 * 1. Live tracker view entry point
 * 2. Derived tracker-state hookup
 * 3. Overview and footer composition
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.screens.tracker.components.TrackerModeBadge
import io.github.velyene.loreweaver.ui.screens.visibleVerticalScrollbar

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
	val contentListState = rememberLazyListState()

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		TrackerModeBadge(
			label = stringResource(R.string.combat_tracker_badge_label),
			containerColor = MaterialTheme.colorScheme.primary,
			contentColor = MaterialTheme.colorScheme.onPrimary
		)
		Spacer(modifier = Modifier.height(16.dp))

		TurnTrackerStrip(
			round = state.round,
			participants = trackerState.participants,
			turnIndex = state.turnIndex,
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

		Spacer(modifier = Modifier.height(12.dp))

		EncounterActionButtons(
			turnStep = state.turnStep,
			onNextTurn = callbacks.onNextTurn,
			onEnd = callbacks.onEnd
		)
	}
}



