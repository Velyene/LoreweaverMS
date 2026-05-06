/*
 * FILE: LiveTrackerDmActionDialogs.kt
 *
 * TABLE OF CONTENTS:
 * 1. Dialog and Sheet Tags
 * 2. HP Management Dialogs
 * 3. Participant Note Dialog
 * 4. Participant Detail Dialog
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.screens.StatusChipFlowRow
import io.github.velyene.loreweaver.ui.screens.persistentStatusChipModels

internal const val PARTICIPANT_DETAIL_SHEET_TAG = "participant_detail_sheet"
internal const val HP_ADJUSTMENT_SHEET_TAG = "hp_adjustment_sheet"

internal data class ParticipantDetailDialogActions(
	val onOpenDamageHeal: () -> Unit,
	val onOpenSetHp: () -> Unit,
	val onOpenApplyStatus: () -> Unit,
	val onOpenEditNotes: () -> Unit,
	val onMarkDefeated: () -> Unit,
	val onRestoreFullHp: () -> Unit,
	val onRemoveCondition: (String, String) -> Unit,
	val onDuplicateEnemy: (() -> Unit)? = null,
	val onRemoveCombatant: (() -> Unit)? = null,
)

@Composable
internal fun ManualHpDialog(
	participantName: String,
	currentHp: Int,
	maxHp: Int,
	onConfirm: (Int) -> Unit,
	onDismiss: () -> Unit,
) {
	var hpInput by remember(currentHp, maxHp) { mutableStateOf(currentHp.toString()) }
	val parsedHp = hpInput.toIntOrNull()?.coerceIn(0, maxHp)

	AlertDialog(
		onDismissRequest = onDismiss,
		title = {
			Text(
				text = stringResource(R.string.encounter_manual_hp_dialog_title, participantName),
				modifier = Modifier.semantics { heading() },
			)
		},
		text = {
			Column {
				Text(
					text = stringResource(R.string.encounter_manual_hp_dialog_supporting_text, currentHp, maxHp),
					modifier = Modifier.padding(bottom = 8.dp),
				)
				OutlinedTextField(
					value = hpInput,
					onValueChange = { hpInput = it.filter(Char::isDigit) },
					label = { Text(stringResource(R.string.encounter_result_amount_label)) },
					keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
					modifier = Modifier.fillMaxWidth(),
				)
			}
		},
		confirmButton = {
			TextButton(onClick = { parsedHp?.let(onConfirm) }, enabled = parsedHp != null) {
				Text(text = stringResource(R.string.save_button))
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(text = stringResource(R.string.cancel_button))
			}
		},
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HpAdjustmentDialog(
	participantName: String,
	onConfirm: (delta: Int, note: String) -> Unit,
	onDismiss: () -> Unit,
) {
	var deltaInput by remember { mutableStateOf("") }
	var noteInput by remember { mutableStateOf("") }
	val parsedDelta = deltaInput.toIntOrNull()?.takeIf { it != 0 }

	ModalBottomSheet(
		onDismissRequest = onDismiss,
		modifier = Modifier.testTag(HP_ADJUSTMENT_SHEET_TAG),
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 20.dp, vertical = 8.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Text(
				text = stringResource(R.string.encounter_hp_adjust_dialog_title, participantName),
				modifier = Modifier.semantics { heading() },
			)
			HorizontalDivider()
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.heightIn(max = 420.dp)
					.verticalScroll(rememberScrollState()),
				verticalArrangement = Arrangement.spacedBy(12.dp),
			) {
				Text(text = stringResource(R.string.encounter_hp_adjust_dialog_supporting_text))
				Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
					listOf(-5, -10, 5, 10).forEach { delta ->
						OutlinedButton(
							onClick = { deltaInput = delta.toString() },
							modifier = Modifier.weight(1f),
						) {
							Text(text = if (delta > 0) "+$delta" else delta.toString())
						}
					}
				}
				OutlinedTextField(
					value = deltaInput,
					onValueChange = { value ->
						deltaInput = when {
							value.isBlank() -> ""
							value == "-" -> value
							value.matches(Regex("-?\\d+")) -> value
							else -> deltaInput
						}
					},
					label = { Text(stringResource(R.string.encounter_hp_adjust_amount_label)) },
					keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
					modifier = Modifier.fillMaxWidth(),
					placeholder = { Text(stringResource(R.string.encounter_hp_adjust_amount_placeholder)) },
				)
				OutlinedTextField(
					value = noteInput,
					onValueChange = { noteInput = it },
					label = { Text(stringResource(R.string.encounter_hp_adjust_note_label)) },
					modifier = Modifier.fillMaxWidth(),
					placeholder = { Text(stringResource(R.string.encounter_hp_adjust_note_placeholder)) },
				)
			}
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(8.dp),
			) {
				TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
					Text(text = stringResource(R.string.cancel_button))
				}
				TextButton(
					onClick = { parsedDelta?.let { onConfirm(it, noteInput.trim()) } },
					enabled = parsedDelta != null,
					modifier = Modifier.weight(1f),
				) {
					Text(text = stringResource(R.string.encounter_hp_adjust_apply_button))
				}
			}
		}
	}
}

@Composable
internal fun ParticipantNoteDialog(
	participantName: String,
	onConfirm: (String) -> Unit,
	onDismiss: () -> Unit,
) {
	var noteInput by remember { mutableStateOf("") }
	val trimmedNote = noteInput.trim()

	AlertDialog(
		onDismissRequest = onDismiss,
		title = {
			Text(
				text = stringResource(R.string.encounter_add_note_dialog_title, participantName),
				modifier = Modifier.semantics { heading() },
			)
		},
		text = {
			OutlinedTextField(
				value = noteInput,
				onValueChange = { noteInput = it },
				label = { Text(stringResource(R.string.notes_label)) },
				modifier = Modifier.fillMaxWidth(),
			)
		},
		confirmButton = {
			TextButton(onClick = { onConfirm(trimmedNote) }, enabled = trimmedNote.isNotBlank()) {
				Text(text = stringResource(R.string.add_button))
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(text = stringResource(R.string.cancel_button))
			}
		},
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ParticipantDetailDialog(
	participant: LiveParticipantUiModel,
	onDismiss: () -> Unit,
	actions: ParticipantDetailDialogActions,
) {
	ModalBottomSheet(
		onDismissRequest = onDismiss,
		modifier = Modifier.testTag(PARTICIPANT_DETAIL_SHEET_TAG),
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 20.dp, vertical = 8.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Text(
				text = stringResource(R.string.encounter_participant_detail_title, participant.combatant.name),
				modifier = Modifier.semantics { heading() },
			)
			HorizontalDivider()
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.heightIn(max = 560.dp)
					.verticalScroll(rememberScrollState()),
				verticalArrangement = Arrangement.spacedBy(12.dp),
			) {
				Text(text = participant.typeLabel)
				Text(
					text = stringResource(
						R.string.encounter_participant_detail_hp_summary,
						participant.combatant.currentHp,
						participant.combatant.maxHp,
					),
				)
				if (participant.resourceLines.isNotEmpty()) {
					Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
						Text(text = stringResource(R.string.encounter_participant_detail_resources_title))
						participant.resourceLines.forEach { line ->
							Text(text = line)
						}
					}
				}

				Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
					Text(text = stringResource(R.string.encounter_participant_detail_statuses_title))
					CombatantConditionsRow(
						combatant = participant.combatant,
						persistentConditions = participant.persistentConditions,
						onRemoveCondition = { characterId, conditionName, _ ->
							actions.onRemoveCondition(characterId, conditionName)
						},
						onAddConditionClick = actions.onOpenApplyStatus,
					)
				}

				Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
					Text(text = stringResource(R.string.character_status_persistent_effects_title))
					if (participant.persistentConditions.isEmpty()) {
						Text(text = stringResource(R.string.character_status_persistent_effects_empty))
					} else {
						StatusChipFlowRow(statuses = persistentStatusChipModels(participant.persistentConditions))
					}
				}

				Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
					Text(text = stringResource(R.string.notes_label))
					Text(text = participant.notes.ifBlank { stringResource(R.string.encounter_participant_notes_empty_message) })
				}

				Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
					OutlinedButton(onClick = actions.onOpenDamageHeal, modifier = Modifier.weight(1f)) {
						Text(text = stringResource(R.string.encounter_damage_heal_button))
					}
					OutlinedButton(onClick = actions.onOpenSetHp, modifier = Modifier.weight(1f)) {
						Text(text = stringResource(R.string.encounter_manual_hp_button))
					}
				}
				Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
					OutlinedButton(onClick = actions.onOpenApplyStatus, modifier = Modifier.weight(1f)) {
						Text(text = stringResource(R.string.encounter_apply_status_button))
					}
					OutlinedButton(onClick = actions.onOpenEditNotes, modifier = Modifier.weight(1f)) {
						Text(text = stringResource(R.string.encounter_edit_notes_button))
					}
				}
				Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
					OutlinedButton(onClick = actions.onMarkDefeated, modifier = Modifier.weight(1f)) {
						Text(text = stringResource(R.string.encounter_mark_defeated_button))
					}
					OutlinedButton(onClick = actions.onRestoreFullHp, modifier = Modifier.weight(1f)) {
						Text(text = stringResource(R.string.encounter_restore_full_hp_button))
					}
				}
				actions.onDuplicateEnemy?.let { duplicateEnemy ->
					Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
						OutlinedButton(onClick = duplicateEnemy, modifier = Modifier.weight(1f)) {
							Text(text = stringResource(R.string.encounter_duplicate_enemy_button))
						}
						actions.onRemoveCombatant?.let { removeCombatant ->
							OutlinedButton(onClick = removeCombatant, modifier = Modifier.weight(1f)) {
								Text(text = stringResource(R.string.remove_button))
							}
						}
					}
				}
			}
			TextButton(
				onClick = onDismiss,
				modifier = Modifier.fillMaxWidth(),
			) {
				Text(text = stringResource(R.string.done_button))
			}
		}
	}
}

