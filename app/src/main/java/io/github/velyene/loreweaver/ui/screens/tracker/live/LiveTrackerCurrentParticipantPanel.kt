/*
 * FILE: LiveTrackerCurrentParticipantPanel.kt
 *
 * TABLE OF CONTENTS:
 * 1. Current participant panel
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.screens.AddConditionDialog
import io.github.velyene.loreweaver.ui.theme.AntiqueGold

@Composable
@Suppress("UNUSED_VALUE")
internal fun CurrentParticipantPanel(
	state: CurrentParticipantPanelState,
	callbacks: LiveTrackerCallbacks
) {
	val participant = state.participant
	if (participant == null) {
		Card(
			modifier = Modifier.fillMaxWidth(),
			colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
		) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					text = state.encounterName.ifBlank { stringResource(R.string.combat_tracker_live_title) },
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold
				)
				Spacer(modifier = Modifier.height(4.dp))
				Text(
					text = stringResource(R.string.encounter_active_panel_empty_message),
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
		}
		return
	}

	var showAddConditionDialog by remember(participant.combatant.characterId) { mutableStateOf(false) }

	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
		border = BorderStroke(2.dp, AntiqueGold)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			Text(
				text = stringResource(R.string.encounter_active_panel_title),
				style = MaterialTheme.typography.labelSmall,
				color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
				modifier = Modifier.semantics { heading() }
			)
			Spacer(modifier = Modifier.height(6.dp))
			Text(
				text = participant.combatant.name,
				style = MaterialTheme.typography.headlineSmall,
				fontWeight = FontWeight.Bold,
				color = MaterialTheme.colorScheme.onPrimaryContainer
			)
			Text(
				text = participant.typeLabel,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
			)

			Spacer(modifier = Modifier.height(12.dp))
			SummaryStatRow(
				primaryLabel = stringResource(
					R.string.encounter_active_hp_summary,
					participant.combatant.currentHp,
					participant.combatant.maxHp
				),
				secondaryLabel = stringResource(
					R.string.combatant_initiative_summary,
					participant.combatant.initiative
				)
			)

			if (participant.resourceLines.isNotEmpty()) {
				Spacer(modifier = Modifier.height(10.dp))
				ResourceLinesSection(resourceLines = participant.resourceLines)
			}

			Spacer(modifier = Modifier.height(12.dp))
			TurnLoopSection(
				turnStep = state.turnStep,
				participant = participant,
				pendingAction = state.pendingAction,
				selectedTarget = state.selectedTarget,
				targetableParticipants = state.targetableParticipants,
				onSelectAction = callbacks.onSelectAction,
				onSelectTarget = callbacks.onSelectTarget,
				onApplyActionResult = callbacks.onApplyActionResult,
				onClearPendingTurn = callbacks.onClearPendingTurn
			)

			Spacer(modifier = Modifier.height(12.dp))
			Text(
				text = stringResource(R.string.conditions_label),
				style = MaterialTheme.typography.labelSmall,
				color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
			)
			Spacer(modifier = Modifier.height(4.dp))
			CombatantConditionsRow(
				combatant = participant.combatant,
				persistentConditions = participant.persistentConditions,
				onRemoveCondition = callbacks.onRemoveCondition,
				onAddConditionClick = { showAddConditionDialog = true }
			)

			Spacer(modifier = Modifier.height(12.dp))
			QuickHpControls(
				characterId = participant.combatant.characterId,
				onHpChange = callbacks.onHpChange
			)

			Spacer(modifier = Modifier.height(12.dp))
			Text(
				text = stringResource(R.string.notes_label),
				style = MaterialTheme.typography.labelSmall,
				color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
			)
			Spacer(modifier = Modifier.height(4.dp))
			Text(
				text = participant.notes.ifBlank { stringResource(R.string.encounter_participant_notes_empty_message) },
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onPrimaryContainer
			)
		}
	}

	if (showAddConditionDialog) {
		AddConditionDialog(
			onConfirm = { condition, duration, persistsAcrossEncounters ->
				callbacks.onAddCondition(
					participant.combatant.characterId,
					condition,
					duration,
					persistsAcrossEncounters
				)
				showAddConditionDialog = false
			},
			onDismiss = { showAddConditionDialog = false }
		)
	}
}

