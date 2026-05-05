/*
 * FILE: LiveTrackerCurrentParticipantPanel.kt
 *
 * TABLE OF CONTENTS:
 * 1. Current Participant Panel
 * 2. Summary and Resource Sections
 * 3. DM Priority Overview
 * 4. Quick HP Controls and Action Dialogs
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.screens.AddConditionDialog
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.QUICK_HP_BUTTON_HEIGHT_DP
import io.github.velyene.loreweaver.ui.theme.AntiqueGold
import kotlin.math.abs

private const val LOW_HP_THRESHOLD = 0.25f

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
	var showManualHpDialog by remember(participant.combatant.characterId) { mutableStateOf(false) }
	var showNoteDialog by remember(participant.combatant.characterId) { mutableStateOf(false) }
	var showDamageHealDialog by remember(participant.combatant.characterId) { mutableStateOf(false) }
	var showParticipantDetailDialog by remember(participant.combatant.characterId) { mutableStateOf(false) }

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
			if (state.encounterName.isNotBlank()) {
				Text(
					text = state.encounterName,
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f)
				)
				Spacer(modifier = Modifier.height(4.dp))
			}
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
			Spacer(modifier = Modifier.height(6.dp))
			Text(
				text = stringResource(R.string.encounter_active_panel_shared_tracker_supporting_text),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.86f)
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
				participant = participant,
				state = TurnLoopSectionState(
					turnStep = state.turnStep,
					canGoToPreviousTurn = state.canGoToPreviousTurn,
					pendingAction = state.pendingAction,
					selectedTarget = state.selectedTarget,
					targetableParticipants = state.targetableParticipants,
				),
				callbacks = TurnLoopSectionCallbacks(
					onSelectAction = callbacks.onSelectAction,
					onSelectTarget = callbacks.onSelectTarget,
					onApplyActionResult = callbacks.onApplyActionResult,
					onPreviousTurn = callbacks.onPreviousTurn,
					onNextTurn = callbacks.onNextTurn,
					onClearPendingTurn = callbacks.onClearPendingTurn,
				),
			)

			Spacer(modifier = Modifier.height(12.dp))
			DmPriorityOverviewSection(
				participant = participant,
				nextParticipant = state.nextParticipant,
			)

			Spacer(modifier = Modifier.height(12.dp))
			Text(
				text = stringResource(R.string.encounter_dm_control_panel_title),
				style = MaterialTheme.typography.titleSmall,
				fontWeight = FontWeight.SemiBold,
				color = MaterialTheme.colorScheme.onPrimaryContainer,
			)
			Spacer(modifier = Modifier.height(4.dp))
			Text(
				text = stringResource(R.string.encounter_dm_control_panel_supporting_text),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.84f),
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

			Spacer(modifier = Modifier.height(8.dp))
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				OutlinedButton(
					onClick = { showParticipantDetailDialog = true },
					modifier = Modifier.weight(1f)
				) {
					Text(text = stringResource(R.string.encounter_participant_details_button))
				}
				OutlinedButton(
					onClick = { showDamageHealDialog = true },
					modifier = Modifier.weight(1f)
				) {
					Text(text = stringResource(R.string.encounter_damage_heal_button))
				}
			}
			Spacer(modifier = Modifier.height(8.dp))
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				OutlinedButton(
					onClick = { showManualHpDialog = true },
					modifier = Modifier.weight(1f)
				) {
					Text(text = stringResource(R.string.encounter_manual_hp_button))
				}
				OutlinedButton(
					onClick = { callbacks.onMarkDefeated(participant.combatant.characterId) },
					modifier = Modifier.weight(1f)
				) {
					Text(text = stringResource(R.string.encounter_mark_defeated_button))
				}
			}
			Spacer(modifier = Modifier.height(8.dp))
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				OutlinedButton(
					onClick = { showAddConditionDialog = true },
					modifier = Modifier.weight(1f)
				) {
					Text(text = stringResource(R.string.encounter_apply_status_button))
				}
				OutlinedButton(
					onClick = { showNoteDialog = true },
					modifier = Modifier.weight(1f)
				) {
					Text(text = stringResource(R.string.encounter_add_note_button))
				}
				OutlinedButton(
					onClick = callbacks.onAdvanceRound,
					modifier = Modifier.weight(1f)
				) {
					Text(text = stringResource(R.string.encounter_end_round_button))
				}
			}

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
			participantName = participant.combatant.name,
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

	if (showDamageHealDialog) {
		HpAdjustmentDialog(
			participantName = participant.combatant.name,
			onConfirm = { delta, note ->
				callbacks.onHpChange(participant.combatant.characterId, delta)
				if (note.isNotBlank()) {
					val actionLabel = if (delta < 0) "Damage" else "Healing"
					callbacks.onAddParticipantNote(
						participant.combatant.characterId,
						"$actionLabel note (${abs(delta)}): $note"
					)
				}
				showDamageHealDialog = false
			},
			onDismiss = { showDamageHealDialog = false },
		)
	}

	if (showManualHpDialog) {
		ManualHpDialog(
			participantName = participant.combatant.name,
			currentHp = participant.combatant.currentHp,
			maxHp = participant.combatant.maxHp,
			onConfirm = { hp ->
				callbacks.onSetHp(participant.combatant.characterId, hp)
				showManualHpDialog = false
			},
			onDismiss = { showManualHpDialog = false },
		)
	}

	if (showParticipantDetailDialog) {
		ParticipantDetailDialog(
			participant = participant,
			onDismiss = { showParticipantDetailDialog = false },
			actions = ParticipantDetailDialogActions(
				onOpenDamageHeal = {
					showParticipantDetailDialog = false
					showDamageHealDialog = true
				},
				onOpenSetHp = {
					showParticipantDetailDialog = false
					showManualHpDialog = true
				},
				onOpenApplyStatus = {
					showParticipantDetailDialog = false
					showAddConditionDialog = true
				},
				onOpenEditNotes = {
					showParticipantDetailDialog = false
					showNoteDialog = true
				},
				onMarkDefeated = {
					callbacks.onMarkDefeated(participant.combatant.characterId)
					showParticipantDetailDialog = false
				},
				onRestoreFullHp = {
					callbacks.onSetHp(participant.combatant.characterId, participant.combatant.maxHp)
					showParticipantDetailDialog = false
				},
				onRemoveCondition = callbacks.onRemoveCondition,
			),
		)
	}

	if (showNoteDialog) {
		ParticipantNoteDialog(
			participantName = participant.combatant.name,
			onConfirm = { note ->
				callbacks.onAddParticipantNote(participant.combatant.characterId, note)
				showNoteDialog = false
			},
			onDismiss = { showNoteDialog = false },
		)
	}
}

@Composable
internal fun SummaryStatRow(primaryLabel: String, secondaryLabel: String) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		MiniSummaryCard(label = primaryLabel, modifier = Modifier.weight(1f))
		MiniSummaryCard(label = secondaryLabel, modifier = Modifier.weight(1f))
	}
}

@Composable
internal fun MiniSummaryCard(label: String, modifier: Modifier = Modifier) {
	Box(
		modifier = modifier
			.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
			.padding(horizontal = 12.dp, vertical = 10.dp),
		contentAlignment = Alignment.Center
	) {
		Text(
			text = label,
			style = MaterialTheme.typography.bodyMedium,
			fontWeight = FontWeight.Medium,
			color = MaterialTheme.colorScheme.onPrimaryContainer
		)
	}
}

@Composable
internal fun ResourceLinesSection(resourceLines: List<String>) {
	Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
		resourceLines.forEach { line ->
			Text(
				text = line,
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onPrimaryContainer
			)
		}
	}
}

@Composable
private fun DmPriorityOverviewSection(
	participant: LiveParticipantUiModel,
	nextParticipant: LiveParticipantUiModel?,
) {
	val dangerSignals = buildList {
		when {
			participant.isEliminated -> add(
				stringResource(R.string.encounter_danger_signal_defeated, participant.combatant.name),
			)

			participant.combatant.maxHp > 0 &&
				participant.combatant.currentHp.toFloat() / participant.combatant.maxHp <= LOW_HP_THRESHOLD -> add(
				stringResource(
					R.string.encounter_danger_signal_low_hp,
					participant.combatant.name,
					participant.combatant.currentHp,
					participant.combatant.maxHp,
				),
			)
		}
		if (participant.combatant.conditions.isNotEmpty()) {
			add(
				stringResource(
					R.string.encounter_danger_signal_conditions,
					participant.combatant.conditions.size,
				),
			)
		}
		if (participant.persistentConditions.isNotEmpty()) {
			add(
				stringResource(
					R.string.encounter_danger_signal_persistent,
					participant.persistentConditions.size,
				),
			)
		}
		if (isEmpty()) {
			add(stringResource(R.string.encounter_danger_signal_stable))
		}
	}

	Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
		Text(
			text = stringResource(R.string.encounter_dm_priority_title),
			style = MaterialTheme.typography.titleSmall,
			fontWeight = FontWeight.SemiBold,
			color = MaterialTheme.colorScheme.onPrimaryContainer,
		)
		Text(
			text = stringResource(R.string.encounter_dm_priority_supporting_text),
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.84f),
		)
		SummaryStatRow(
			primaryLabel = stringResource(R.string.encounter_dm_priority_active_now, participant.combatant.name),
			secondaryLabel = nextParticipant?.let { upcoming ->
				stringResource(R.string.encounter_dm_priority_next_up, upcoming.combatant.name)
			} ?: stringResource(R.string.encounter_dm_priority_next_up_none),
		)
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
				.padding(horizontal = 12.dp, vertical = 10.dp),
			verticalArrangement = Arrangement.spacedBy(4.dp),
		) {
			Text(
				text = stringResource(R.string.encounter_danger_signals_title),
				style = MaterialTheme.typography.labelSmall,
				color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f),
			)
			dangerSignals.forEach { line ->
				Text(
					text = "• $line",
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onPrimaryContainer,
				)
			}
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

