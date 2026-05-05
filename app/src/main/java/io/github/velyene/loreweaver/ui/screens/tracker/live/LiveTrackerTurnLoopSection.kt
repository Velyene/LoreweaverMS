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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
					onApplyActionResult = callbacks.onApplyActionResult,
					onNextTurn = callbacks.onNextTurn
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

@Composable
@Suppress("UNUSED_VALUE")
private fun ActionResultControls(
	selectedTarget: LiveParticipantUiModel?,
	onApplyActionResult: (ActionResolutionType, Int?) -> Unit,
	onNextTurn: () -> Unit
) {
	var amountInput by rememberSaveable(selectedTarget?.combatant?.characterId) { mutableStateOf("") }
	val parsedAmount = amountInput.toIntOrNull()
	val quickAmounts = remember { listOf(5, 10, 15) }
	val resolveAndAdvance: (ActionResolutionType, Int?) -> Unit = { resultType, amount ->
		onApplyActionResult(resultType, amount)
		onNextTurn()
	}

	Text(
		text = selectedTarget?.let {
			stringResource(R.string.encounter_apply_result_prompt, it.combatant.name)
		} ?: stringResource(R.string.encounter_apply_result_prompt_no_target),
		style = MaterialTheme.typography.bodyMedium,
		color = MaterialTheme.colorScheme.onPrimaryContainer
	)
	Spacer(modifier = Modifier.height(8.dp))
	Text(
		text = stringResource(R.string.encounter_result_quick_amounts_title),
		style = MaterialTheme.typography.labelSmall,
		color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f)
	)
	Spacer(modifier = Modifier.height(6.dp))
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		quickAmounts.forEach { amount ->
			OutlinedButton(
				onClick = { amountInput = amount.toString() },
				modifier = Modifier.weight(1f)
			) {
				Text(text = amount.toString())
			}
		}
	}
	Spacer(modifier = Modifier.height(8.dp))
	OutlinedTextField(
		value = amountInput,
		onValueChange = { amountInput = it.filter(Char::isDigit) },
		label = { Text(stringResource(R.string.encounter_result_amount_label)) },
		keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
		modifier = Modifier.fillMaxWidth()
	)
	Spacer(modifier = Modifier.height(8.dp))
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		OutlinedButton(
			onClick = { onApplyActionResult(ActionResolutionType.MISS, null) },
			modifier = Modifier.weight(1f)
		) {
			Text(text = stringResource(R.string.encounter_result_miss_button))
		}
		Button(
			onClick = { onApplyActionResult(ActionResolutionType.DAMAGE, parsedAmount) },
			modifier = Modifier.weight(1f)
		) {
			Text(text = stringResource(R.string.encounter_result_damage_button))
		}
		Button(
			onClick = { onApplyActionResult(ActionResolutionType.HEAL, parsedAmount) },
			modifier = Modifier.weight(1f),
			colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
		) {
			Text(
				text = stringResource(R.string.encounter_result_heal_button),
				color = MaterialTheme.colorScheme.onSecondary
			)
		}
	}
	Spacer(modifier = Modifier.height(10.dp))
	Text(
		text = stringResource(R.string.encounter_result_finish_row_title),
		style = MaterialTheme.typography.labelSmall,
		color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f)
	)
	Spacer(modifier = Modifier.height(6.dp))
	Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
		OutlinedButton(
			onClick = { resolveAndAdvance(ActionResolutionType.MISS, null) },
			modifier = Modifier.fillMaxWidth()
		) {
			Text(text = stringResource(R.string.encounter_result_miss_end_button))
		}
		Button(
			onClick = { resolveAndAdvance(ActionResolutionType.DAMAGE, parsedAmount) },
			enabled = parsedAmount != null,
			modifier = Modifier.fillMaxWidth()
		) {
			Text(text = stringResource(R.string.encounter_result_damage_end_button))
		}
		Button(
			onClick = { resolveAndAdvance(ActionResolutionType.HEAL, parsedAmount) },
			enabled = parsedAmount != null,
			modifier = Modifier.fillMaxWidth(),
			colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
		) {
			Text(
				text = stringResource(R.string.encounter_result_heal_end_button),
				color = MaterialTheme.colorScheme.onSecondary
			)
		}
	}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TargetSelectorChips(
	targets: List<LiveParticipantUiModel>,
	selectedTargetId: String?,
	onSelectTarget: (String) -> Unit
) {
	FlowRow(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		targets.forEach { target ->
			TargetChip(
				name = target.combatant.name,
				isSelected = target.combatant.characterId == selectedTargetId,
				onClick = { onSelectTarget(target.combatant.characterId) }
			)
		}
	}
}

@Composable
private fun TargetChip(name: String, isSelected: Boolean, onClick: () -> Unit) {
	val containerColor = if (isSelected) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant
	val contentColor = if (isSelected) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
	Box(
		modifier = Modifier
			.background(containerColor, RoundedCornerShape(20.dp))
			.clickable(onClick = onClick)
			.padding(horizontal = 14.dp, vertical = 8.dp)
	) {
		Text(
			text = name,
			color = contentColor,
			fontSize = 12.sp,
			fontWeight = FontWeight.Medium
		)
	}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ParticipantActionChips(
	actionLabels: List<String>,
	onAction: (String) -> Unit
) {
	FlowRow(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		actionLabels.forEach { actionLabel ->
			ActionChip(label = actionLabel, onClick = { onAction(actionLabel) })
		}
	}
}

@Composable
private fun ActionChip(label: String, onClick: () -> Unit) {
	Box(
		modifier = Modifier
			.background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(20.dp))
			.clickable(onClick = onClick)
			.semantics { contentDescription = label }
			.padding(horizontal = 14.dp, vertical = 8.dp)
	) {
		Text(
			text = label,
			color = MaterialTheme.colorScheme.onSecondaryContainer,
			fontSize = 12.sp,
			fontWeight = FontWeight.Medium
		)
	}
}

@Composable
private fun turnStepSummary(turnStep: CombatTurnStep): String {
	return when (turnStep) {
		CombatTurnStep.SELECT_ACTION -> stringResource(R.string.encounter_turn_step_select_action)
		CombatTurnStep.SELECT_TARGET -> stringResource(R.string.encounter_turn_step_select_target)
		CombatTurnStep.APPLY_RESULT -> stringResource(R.string.encounter_turn_step_apply_result)
		CombatTurnStep.READY_TO_END -> stringResource(R.string.encounter_turn_step_ready_to_end)
	}
}
