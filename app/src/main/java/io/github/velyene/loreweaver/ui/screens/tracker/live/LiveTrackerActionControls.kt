/*
 * FILE: LiveTrackerActionControls.kt
 *
 * TABLE OF CONTENTS:
 * 1. Action chips
 * 2. Target selection and result controls
 * 3. Turn-step summary
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.viewmodels.ActionResolutionType
import io.github.velyene.loreweaver.ui.viewmodels.CombatTurnStep

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ParticipantActionChips(
	actionLabels: List<String>,
	onAction: (String) -> Unit
) {
	FlowRow(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		actionLabels.forEach { actionLabel ->
			ActionChip(
				label = actionLabel,
				onClick = { onAction(actionLabel) }
			)
		}
	}
}

@Composable
internal fun ActionResultControls(
	selectedTarget: LiveParticipantUiModel?,
	onApplyActionResult: (ActionResolutionType, Int?) -> Unit
) {
	var amountInput by rememberSaveable(selectedTarget?.combatant?.characterId) { mutableStateOf("") }

	Text(
		text = selectedTarget?.let {
			stringResource(R.string.encounter_apply_result_prompt, it.combatant.name)
		} ?: stringResource(R.string.encounter_apply_result_prompt_no_target),
		style = MaterialTheme.typography.bodyMedium,
		color = MaterialTheme.colorScheme.onPrimaryContainer
	)
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
			onClick = { onApplyActionResult(ActionResolutionType.DAMAGE, amountInput.toIntOrNull()) },
			modifier = Modifier.weight(1f)
		) {
			Text(text = stringResource(R.string.encounter_result_damage_button))
		}
		Button(
			onClick = { onApplyActionResult(ActionResolutionType.HEAL, amountInput.toIntOrNull()) },
			modifier = Modifier.weight(1f),
			colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
		) {
			Text(
				text = stringResource(R.string.encounter_result_heal_button),
				color = MaterialTheme.colorScheme.onSecondary
			)
		}
	}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TargetSelectorChips(
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
internal fun turnStepSummary(turnStep: CombatTurnStep): String {
	return when (turnStep) {
		CombatTurnStep.SELECT_ACTION -> stringResource(R.string.encounter_turn_step_select_action)
		CombatTurnStep.SELECT_TARGET -> stringResource(R.string.encounter_turn_step_select_target)
		CombatTurnStep.APPLY_RESULT -> stringResource(R.string.encounter_turn_step_apply_result)
		CombatTurnStep.READY_TO_END -> stringResource(R.string.encounter_turn_step_ready_to_end)
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

