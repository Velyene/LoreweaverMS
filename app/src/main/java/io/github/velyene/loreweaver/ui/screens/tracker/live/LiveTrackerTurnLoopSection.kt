/*
 * FILE: LiveTrackerTurnLoopSection.kt
 *
 * TABLE OF CONTENTS:
 * 1. Turn Loop Section
 * 2. Action Result Controls
 * 3. Target Selection Chips
 * 4. Participant Action Chips
 * 5. Turn Step Summaries
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.viewmodels.ActionResolutionType
import io.github.velyene.loreweaver.ui.viewmodels.CombatTurnStep
import io.github.velyene.loreweaver.ui.viewmodels.PendingTurnAction

@Suppress("LongParameterList", "kotlin:S107")
internal data class TurnLoopSectionState(
	val turnStep: CombatTurnStep,
	val canGoToPreviousTurn: Boolean,
	val pendingAction: PendingTurnAction?,
	val selectedTarget: LiveParticipantUiModel?,
	val targetableParticipants: List<LiveParticipantUiModel>,
)

internal data class TurnLoopSectionCallbacks(
	val onSelectAction: (String) -> Unit,
	val onSelectTarget: (String) -> Unit,
	val onApplyActionResult: (ActionResolutionType, Int?) -> Unit,
	val onPreviousTurn: () -> Unit,
	val onNextTurn: () -> Unit,
	val onClearPendingTurn: () -> Unit,
)

@Composable
@Suppress("LongParameterList", "kotlin:S107")
internal fun TurnLoopSection(
	participant: LiveParticipantUiModel,
	state: TurnLoopSectionState,
	callbacks: TurnLoopSectionCallbacks,
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.18f), RoundedCornerShape(10.dp))
			.padding(12.dp)
	) {
		Text(
			text = stringResource(R.string.encounter_turn_flow_title),
			style = MaterialTheme.typography.labelSmall,
			color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
			modifier = Modifier.semantics { heading() }
		)
		Spacer(modifier = Modifier.height(6.dp))
		Text(
			text = turnStepSummary(state.turnStep),
			style = MaterialTheme.typography.bodyMedium,
			fontWeight = FontWeight.Medium,
			color = MaterialTheme.colorScheme.onPrimaryContainer
		)
		Spacer(modifier = Modifier.height(4.dp))
		Text(
			text = stringResource(R.string.encounter_turn_flow_shared_tracker_supporting_text),
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f)
		)
		Spacer(modifier = Modifier.height(10.dp))

		when (state.turnStep) {
			CombatTurnStep.SELECT_ACTION -> {
				Text(
					text = stringResource(R.string.encounter_available_actions_title),
					style = MaterialTheme.typography.labelSmall,
					color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
				)
				Spacer(modifier = Modifier.height(6.dp))
				ParticipantActionChips(
					actionLabels = participant.actionLabels,
					onAction = callbacks.onSelectAction
				)
			}

			CombatTurnStep.SELECT_TARGET -> {
				Text(
					text = state.pendingAction?.let {
						stringResource(R.string.encounter_target_prompt_with_action, it.name)
					} ?: stringResource(R.string.encounter_target_prompt),
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onPrimaryContainer
				)
				Spacer(modifier = Modifier.height(8.dp))
				TargetSelectorChips(
					targets = state.targetableParticipants,
					selectedTargetId = state.selectedTarget?.combatant?.characterId,
					onSelectTarget = callbacks.onSelectTarget
				)
			}

			CombatTurnStep.APPLY_RESULT -> {
				ActionResultControls(
					selectedTarget = state.selectedTarget,
					onApplyActionResult = callbacks.onApplyActionResult
				)
			}

			CombatTurnStep.READY_TO_END -> {
				Text(
					text = state.selectedTarget?.let {
						stringResource(
							R.string.encounter_turn_ready_summary,
							state.pendingAction?.name.orEmpty(),
							it.combatant.name
						)
					} ?: stringResource(R.string.encounter_turn_ready_no_target),
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onPrimaryContainer
				)
				Spacer(modifier = Modifier.height(10.dp))
				Button(
					onClick = callbacks.onNextTurn,
					modifier = Modifier.fillMaxWidth(),
					colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
				) {
					Text(
						text = stringResource(R.string.encounter_turn_finish_button),
						color = MaterialTheme.colorScheme.onSecondary,
						fontWeight = FontWeight.Bold
					)
				}
				Spacer(modifier = Modifier.height(6.dp))
				Text(
					text = stringResource(R.string.encounter_turn_finish_supporting_text),
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f)
				)
			}
		}

		if (!state.pendingAction?.useSummary.isNullOrBlank()) {
			Spacer(modifier = Modifier.height(8.dp))
			Text(
				text = state.pendingAction.useSummary,
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f)
			)
		}

		if (state.pendingAction != null) {
			Spacer(modifier = Modifier.height(8.dp))
			TextButton(onClick = callbacks.onClearPendingTurn) {
				Text(text = stringResource(R.string.encounter_clear_turn_selection_button))
			}
		}

		Spacer(modifier = Modifier.height(8.dp))
		OutlinedButton(
			onClick = callbacks.onPreviousTurn,
			enabled = state.canGoToPreviousTurn,
			modifier = Modifier.fillMaxWidth()
		) {
			Text(text = stringResource(R.string.encounter_previous_turn_button))
		}
		Text(
			text = stringResource(R.string.encounter_previous_turn_supporting_text),
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f)
		)
	}
}
