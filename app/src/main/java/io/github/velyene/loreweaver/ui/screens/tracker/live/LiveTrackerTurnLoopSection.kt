/*
 * FILE: LiveTrackerTurnLoopSection.kt
 *
 * TABLE OF CONTENTS:
 * 1. Turn loop section
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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

@Composable
internal fun TurnLoopSection(
	turnStep: CombatTurnStep,
	participant: LiveParticipantUiModel,
	pendingAction: PendingTurnAction?,
	selectedTarget: LiveParticipantUiModel?,
	targetableParticipants: List<LiveParticipantUiModel>,
	onSelectAction: (String) -> Unit,
	onSelectTarget: (String) -> Unit,
	onApplyActionResult: (ActionResolutionType, Int?) -> Unit,
	onClearPendingTurn: () -> Unit
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
			text = turnStepSummary(turnStep),
			style = MaterialTheme.typography.bodyMedium,
			fontWeight = FontWeight.Medium,
			color = MaterialTheme.colorScheme.onPrimaryContainer
		)
		Spacer(modifier = Modifier.height(10.dp))

		when (turnStep) {
			CombatTurnStep.SELECT_ACTION -> {
				Text(
					text = stringResource(R.string.encounter_available_actions_title),
					style = MaterialTheme.typography.labelSmall,
					color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
				)
				Spacer(modifier = Modifier.height(6.dp))
				ParticipantActionChips(
					actionLabels = participant.actionLabels,
					onAction = onSelectAction
				)
			}

			CombatTurnStep.SELECT_TARGET -> {
				Text(
					text = pendingAction?.let {
						stringResource(R.string.encounter_target_prompt_with_action, it.name)
					} ?: stringResource(R.string.encounter_target_prompt),
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onPrimaryContainer
				)
				Spacer(modifier = Modifier.height(8.dp))
				TargetSelectorChips(
					targets = targetableParticipants,
					selectedTargetId = selectedTarget?.combatant?.characterId,
					onSelectTarget = onSelectTarget
				)
			}

			CombatTurnStep.APPLY_RESULT -> {
				ActionResultControls(
					selectedTarget = selectedTarget,
					onApplyActionResult = onApplyActionResult
				)
			}

			CombatTurnStep.READY_TO_END -> {
				Text(
					text = selectedTarget?.let {
						stringResource(
							R.string.encounter_turn_ready_summary,
							pendingAction?.name.orEmpty(),
							it.combatant.name
						)
					} ?: stringResource(R.string.encounter_turn_ready_no_target),
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onPrimaryContainer
				)
			}
		}

		if (pendingAction != null) {
			Spacer(modifier = Modifier.height(8.dp))
			TextButton(onClick = onClearPendingTurn) {
				Text(text = stringResource(R.string.encounter_clear_turn_selection_button))
			}
		}
	}
}

