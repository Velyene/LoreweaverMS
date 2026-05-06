/*
 * FILE: LiveTrackerBattlePulse.kt
 *
 * TABLE OF CONTENTS:
 * 1. Battle Pulse Summary Card
 * 2. Battle Pulse Messaging Helpers
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.viewmodels.CombatTurnStep

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun BattlePulseCard(
	partyAlive: Int,
	partyTotal: Int,
	enemyAlive: Int,
	enemyTotal: Int,
	currentParticipantName: String?,
	selectedTargetName: String?,
	turnStep: CombatTurnStep,
	pendingActionName: String?,
	modifier: Modifier = Modifier
) {
	val focusMessage = battlePulseFocusMessage(
		turnStep = turnStep,
		currentParticipantName = currentParticipantName,
		pendingActionName = pendingActionName,
		selectedTargetName = selectedTargetName
	)

	Card(
		modifier = modifier,
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(modifier = Modifier.padding(12.dp)) {
			Text(
				text = stringResource(R.string.encounter_battle_pulse_title),
				style = MaterialTheme.typography.labelSmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.semantics { heading() }
			)
			Spacer(modifier = Modifier.height(6.dp))
			Text(
				text = focusMessage,
				style = MaterialTheme.typography.bodyMedium,
				fontWeight = FontWeight.Medium,
				color = MaterialTheme.colorScheme.onSurface
			)
			Spacer(modifier = Modifier.height(10.dp))
			FlowRow(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(8.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				BattlePulseMetricCard(
					label = stringResource(R.string.encounter_battle_pulse_party_up_label),
					value = "$partyAlive/$partyTotal"
				)
				BattlePulseMetricCard(
					label = stringResource(R.string.encounter_battle_pulse_enemies_up_label),
					value = "$enemyAlive/$enemyTotal"
				)
				BattlePulseMetricCard(
					label = stringResource(R.string.encounter_battle_pulse_phase_label),
					value = battlePulseTurnStepSummary(turnStep)
				)
			}
		}
	}
}

@Composable
private fun battlePulseTurnStepSummary(turnStep: CombatTurnStep): String {
	return when (turnStep) {
		CombatTurnStep.SELECT_ACTION -> stringResource(R.string.encounter_turn_step_select_action)
		CombatTurnStep.SELECT_TARGET -> stringResource(R.string.encounter_turn_step_select_target)
		CombatTurnStep.APPLY_RESULT -> stringResource(R.string.encounter_turn_step_apply_result)
		CombatTurnStep.READY_TO_END -> stringResource(R.string.encounter_turn_step_ready_to_end)
	}
}

@Composable
private fun battlePulseFocusMessage(
	turnStep: CombatTurnStep,
	currentParticipantName: String?,
	pendingActionName: String?,
	selectedTargetName: String?
): String {
	return when (turnStep) {
		CombatTurnStep.SELECT_ACTION -> currentParticipantName?.let {
			stringResource(R.string.encounter_battle_pulse_select_action_message, it)
		} ?: stringResource(R.string.encounter_battle_pulse_ready_message)

		CombatTurnStep.SELECT_TARGET -> if (currentParticipantName != null && pendingActionName != null) {
			stringResource(
				R.string.encounter_battle_pulse_select_target_message,
				currentParticipantName,
				pendingActionName
			)
		} else {
			stringResource(R.string.encounter_battle_pulse_ready_message)
		}

		CombatTurnStep.APPLY_RESULT -> if (
			currentParticipantName != null &&
			pendingActionName != null &&
			selectedTargetName != null
		) {
			stringResource(
				R.string.encounter_battle_pulse_apply_result_message,
				currentParticipantName,
				pendingActionName,
				selectedTargetName
			)
		} else {
			stringResource(R.string.encounter_battle_pulse_ready_message)
		}

		CombatTurnStep.READY_TO_END -> if (
			currentParticipantName != null &&
			pendingActionName != null &&
			selectedTargetName != null
		) {
			stringResource(
				R.string.encounter_battle_pulse_ready_to_end_message,
				currentParticipantName,
				pendingActionName,
				selectedTargetName
			)
		} else {
			stringResource(R.string.encounter_battle_pulse_ready_message)
		}
	}
}

@Composable
private fun BattlePulseMetricCard(label: String, value: String) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
		Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
			Text(
				text = label,
				style = MaterialTheme.typography.labelSmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Spacer(modifier = Modifier.height(2.dp))
			Text(
				text = value,
				style = MaterialTheme.typography.bodyMedium,
				fontWeight = FontWeight.Bold,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
	}
}



