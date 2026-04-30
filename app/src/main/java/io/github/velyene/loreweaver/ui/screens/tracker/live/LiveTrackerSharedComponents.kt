/*
 * FILE: LiveTrackerSharedComponents.kt
 *
 * TABLE OF CONTENTS:
 * 1. Resource and action chip helpers
 * 2. HP/result controls and target chips
 * 3. Summary cards, log section, and footer actions
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.ACTION_BUTTON_HEIGHT_DP
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.LOG_HEIGHT_DP
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.QUICK_HP_BUTTON_HEIGHT_DP
import io.github.velyene.loreweaver.ui.screens.visibleVerticalScrollbar
import io.github.velyene.loreweaver.ui.viewmodels.ActionResolutionType
import io.github.velyene.loreweaver.ui.viewmodels.CombatTurnStep

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ResourceLinesSection(resourceLines: List<String>) {
	FlowRow(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(6.dp),
		verticalArrangement = Arrangement.spacedBy(6.dp)
	) {
		resourceLines.forEach { line ->
			Box(
				modifier = Modifier
					.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
					.padding(horizontal = 10.dp, vertical = 6.dp)
			) {
				Text(
					text = line,
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onPrimaryContainer
				)
			}
		}
	}
}

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
internal fun QuickHpControls(
	characterId: String,
	onHpChange: (characterId: String, delta: Int) -> Unit
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(4.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = stringResource(R.string.hp_label),
			fontSize = 12.sp,
			modifier = Modifier.padding(end = 4.dp)
		)
		listOf(-5, -1, 1, 5).forEach { delta ->
			OutlinedButton(
				onClick = { onHpChange(characterId, delta) },
				modifier = Modifier
					.weight(1f)
					.height(QUICK_HP_BUTTON_HEIGHT_DP.dp),
				contentPadding = PaddingValues(0.dp)
			) {
				Text(if (delta > 0) "+$delta" else delta.toString(), fontSize = 12.sp)
			}
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
internal fun SummaryStatRow(primaryLabel: String, secondaryLabel: String) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		SummaryStatCard(
			label = primaryLabel,
			modifier = Modifier.weight(1f)
		)
		SummaryStatCard(
			label = secondaryLabel,
			modifier = Modifier.weight(1f)
		)
	}
}

@Composable
private fun SummaryStatCard(label: String, modifier: Modifier = Modifier) {
	Card(
		modifier = modifier,
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.18f))
	) {
		Text(
			text = label,
			modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
			style = MaterialTheme.typography.bodyMedium,
			fontWeight = FontWeight.Medium,
			color = MaterialTheme.colorScheme.onPrimaryContainer
		)
	}
}

@Composable
internal fun CombatLogSection(statuses: List<String>) {
	val listState = rememberLazyListState()

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.height(LOG_HEIGHT_DP.dp)
			.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
			.padding(8.dp)
	) {
		Text(
			text = stringResource(R.string.session_combat_log_title),
			color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
			style = MaterialTheme.typography.labelSmall,
			modifier = Modifier.semantics { heading() }
		)
		LazyColumn(
			state = listState,
			modifier = Modifier.visibleVerticalScrollbar(listState)
		) {
			itemsIndexed(statuses.reversed(), key = { index, status -> "$index-$status" }) { _, status ->
				Text(
					text = stringResource(R.string.combat_log_bullet, status),
					color = if (status.contains("!")) MaterialTheme.colorScheme.primary
					else MaterialTheme.colorScheme.onSurface,
					fontSize = 11.sp,
					modifier = Modifier.padding(vertical = 1.dp)
				)
			}
		}
	}
}

@Composable
internal fun EncounterActionButtons(
	turnStep: CombatTurnStep,
	onNextTurn: () -> Unit,
	onEnd: () -> Unit
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Button(
			onClick = onNextTurn,
			modifier = Modifier
				.weight(1f)
				.height(ACTION_BUTTON_HEIGHT_DP.dp),
			colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
		) {
			Text(
				text = if (turnStep == CombatTurnStep.READY_TO_END) {
					stringResource(R.string.end_turn_button)
				} else {
					stringResource(R.string.encounter_skip_turn_button)
				},
				color = MaterialTheme.colorScheme.onPrimary
			)
		}
		Button(
			onClick = onEnd,
			modifier = Modifier
				.weight(1f)
				.height(ACTION_BUTTON_HEIGHT_DP.dp),
			colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
		) {
			Text(
				text = stringResource(R.string.end_encounter_button),
				color = MaterialTheme.colorScheme.onSecondary
			)
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
internal fun turnStepSummary(turnStep: CombatTurnStep): String {
	return when (turnStep) {
		CombatTurnStep.SELECT_ACTION -> stringResource(R.string.encounter_turn_step_select_action)
		CombatTurnStep.SELECT_TARGET -> stringResource(R.string.encounter_turn_step_select_target)
		CombatTurnStep.APPLY_RESULT -> stringResource(R.string.encounter_turn_step_apply_result)
		CombatTurnStep.READY_TO_END -> stringResource(R.string.encounter_turn_step_ready_to_end)
	}
}

