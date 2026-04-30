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

@Suppress("kotlin:S107")
internal data class LiveTrackerCallbacks(
	val onAction: (String) -> Unit,
	val onNextTurn: () -> Unit,
	val onHpChange: (characterId: String, delta: Int) -> Unit,
	val onAddCondition: (characterId: String, condition: String, duration: Int?, persistsAcrossEncounters: Boolean) -> Unit,
	val onRemoveCondition: (characterId: String, conditionName: String, removePersistentCondition: Boolean) -> Unit,
	val onEnd: () -> Unit
)

@Composable
internal fun LiveTrackerView(
	round: Int,
	combatants: List<CombatantState>,
	persistentConditionsByCharacterId: Map<String, Set<String>>,
	turnIndex: Int,
	statuses: List<String>,
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

		CombatantHpList(
			combatants = combatants,
			persistentConditionsByCharacterId = persistentConditionsByCharacterId,
			turnIndex = turnIndex,
			onHpChange = callbacks.onHpChange,
			onAddCondition = callbacks.onAddCondition,
			onRemoveCondition = callbacks.onRemoveCondition,
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



