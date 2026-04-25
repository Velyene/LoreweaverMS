/*
 * FILE: CampaignNoteDialog.kt
 *
 * TABLE OF CONTENTS:
 * 1. Dialog Mode (NoteDialogMode)
 * 2. Dialog Host (NoteDialogHost)
 * 3. Entry Dialog (NoteEntryDialog)
 * 4. Submit Helper (handleNoteConfirm)
 * 5. Dialog Fields (NoteDialogFields)
 * 6. Type Helpers (initialNoteType, initialExtraField, localized helpers)
 * 7. Updated Note Mapping (buildUpdatedNote)
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.Note
import io.github.velyene.loreweaver.ui.util.NOTE_TYPES
import io.github.velyene.loreweaver.ui.util.NOTE_TYPE_GENERAL
import io.github.velyene.loreweaver.ui.util.NOTE_TYPE_LOCATION
import io.github.velyene.loreweaver.ui.util.NOTE_TYPE_LORE
import io.github.velyene.loreweaver.ui.util.NOTE_TYPE_NPC
import io.github.velyene.loreweaver.ui.util.buildNpcExtra
import io.github.velyene.loreweaver.ui.util.parseNpcExtra

internal sealed interface NoteDialogMode {
	data object Hidden : NoteDialogMode
	data object Adding : NoteDialogMode
	data class Editing(val note: Note) : NoteDialogMode
}

@Composable
internal fun NoteDialogHost(
	dialogMode: NoteDialogMode,
	onAddNote: (String, String, String) -> Unit,
	onUpdateNote: (Note) -> Unit,
	onDismiss: () -> Unit
) {
	when (dialogMode) {
		NoteDialogMode.Hidden -> Unit
		NoteDialogMode.Adding -> NoteEntryDialog(
			editingNote = null,
			onConfirm = onAddNote,
			onDismiss = onDismiss
		)

		is NoteDialogMode.Editing -> NoteEntryDialog(
			editingNote = dialogMode.note,
			onConfirm = { content, type, extra ->
				onUpdateNote(buildUpdatedNote(dialogMode.note, type, content, extra))
			},
			onDismiss = onDismiss
		)
	}
}

@Composable
private fun NoteEntryDialog(
	editingNote: Note?,
	onConfirm: (String, String, String) -> Unit,
	onDismiss: () -> Unit
) {
	var content by remember { mutableStateOf(editingNote?.content ?: "") }
	var selectedType by remember { mutableStateOf(initialNoteType(editingNote)) }
	var extraField by remember { mutableStateOf(initialExtraField(editingNote)) }
	var factionField by remember { mutableStateOf((editingNote as? Note.NPC)?.faction ?: "") }
	var attitudeField by remember { mutableStateOf((editingNote as? Note.NPC)?.attitude ?: "") }
	val canConfirmNote = canConfirmNote(
		content = content,
		selectedType = selectedType,
		factionField = factionField,
		extraField = extraField
	)

	AlertDialog(
		onDismissRequest = onDismiss,
		title = {
			Text(
				if (editingNote != null) stringResource(R.string.edit_note_title)
				else stringResource(R.string.add_note_title)
			)
		},
		text = {
			NoteDialogFields(
				selectedType = selectedType,
				onTypeChange = { selectedType = it },
				content = content,
				onContentChange = { content = it },
				extraField = extraField,
				onExtraChange = { extraField = it },
				factionField = factionField,
				onFactionChange = { factionField = it },
				attitudeField = attitudeField,
				onAttitudeChange = { attitudeField = it }
			)
		},
		confirmButton = {
			Button(
				enabled = canConfirmNote,
				onClick = {
					handleNoteConfirm(
						content = content,
						selectedType = selectedType,
						factionField = factionField,
						attitudeField = attitudeField,
						extraField = extraField,
						onConfirm = onConfirm,
						onDismiss = onDismiss
					)
				}
			) {
				Text(
					text = if (editingNote != null) stringResource(R.string.save_button)
					else stringResource(R.string.add_button)
				)
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(text = stringResource(R.string.cancel_button))
			}
		}
	)
}

private fun handleNoteConfirm(
	content: String,
	selectedType: String,
	factionField: String,
	attitudeField: String,
	extraField: String,
	onConfirm: (String, String, String) -> Unit,
	onDismiss: () -> Unit
) {
	// NPC notes persist multiple structured fields through the existing string-based API,
	// so the dialog normalizes that payload before handing control back to the caller.
	if (canConfirmNote(content, selectedType, factionField, extraField)) {
		val extra = if (selectedType == NOTE_TYPE_NPC) {
			buildNpcExtra(factionField, attitudeField)
		} else {
			extraField
		}
		onConfirm(content, selectedType, extra)
		onDismiss()
	}
}

private fun canConfirmNote(
	content: String,
	selectedType: String,
	factionField: String,
	extraField: String
): Boolean {
	if (content.isNotBlank().not()) return false

	return when (selectedType) {
		NOTE_TYPE_LORE, NOTE_TYPE_LOCATION -> extraField.isNotBlank()
		NOTE_TYPE_NPC -> factionField.isNotBlank()
		else -> true
	}
}

@Composable
private fun NoteDialogFields(
	selectedType: String,
	onTypeChange: (String) -> Unit,
	content: String,
	onContentChange: (String) -> Unit,
	extraField: String,
	onExtraChange: (String) -> Unit,
	factionField: String = "",
	onFactionChange: (String) -> Unit = {},
	attitudeField: String = "",
	onAttitudeChange: (String) -> Unit = {}
) {
	Column {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(4.dp)
		) {
			NOTE_TYPES.forEach { type ->
				FilterChip(
					selected = selectedType == type,
					onClick = { onTypeChange(type) },
					label = { Text(text = localizedNoteType(type), fontSize = 10.sp) }
				)
			}
		}
		OutlinedTextField(
			value = content,
			onValueChange = onContentChange,
			label = { Text(stringResource(R.string.note_content_label)) },
			modifier = Modifier.fillMaxWidth()
		)
		// Only note types with additional structured metadata render extra inputs so the
		// dialog stays compact for general notes while still supporting richer note subtypes.
		when (selectedType) {
			NOTE_TYPE_NPC -> {
				OutlinedTextField(
					value = factionField,
					onValueChange = onFactionChange,
					label = { Text(stringResource(R.string.note_faction_label)) },
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 8.dp)
				)
				OutlinedTextField(
					value = attitudeField,
					onValueChange = onAttitudeChange,
					label = { Text(stringResource(R.string.note_attitude_label)) },
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 8.dp)
				)
			}

			NOTE_TYPE_LORE, NOTE_TYPE_LOCATION -> {
				OutlinedTextField(
					value = extraField,
					onValueChange = onExtraChange,
					label = { Text(localizedExtraFieldLabel(selectedType)) },
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 8.dp)
				)
			}
		}
	}
}

private fun initialNoteType(note: Note?) = when (note) {
	is Note.Lore -> NOTE_TYPE_LORE
	is Note.NPC -> NOTE_TYPE_NPC
	is Note.Location -> NOTE_TYPE_LOCATION
	else -> NOTE_TYPE_GENERAL
}

private fun initialExtraField(note: Note?) = when (note) {
	is Note.Lore -> note.historicalEra
	is Note.NPC -> buildNpcExtra(note.faction, note.attitude)
	is Note.Location -> note.region
	else -> ""
}

@Composable
private fun localizedNoteType(type: String): String = when (type) {
	NOTE_TYPE_GENERAL -> stringResource(R.string.note_type_general)
	NOTE_TYPE_LORE -> stringResource(R.string.note_type_lore)
	NOTE_TYPE_NPC -> stringResource(R.string.note_type_npc)
	NOTE_TYPE_LOCATION -> stringResource(R.string.note_type_location)
	else -> type
}

@Composable
private fun localizedExtraFieldLabel(type: String): String = when (type) {
	NOTE_TYPE_LORE -> stringResource(R.string.note_extra_historical_era_label)
	NOTE_TYPE_LOCATION -> stringResource(R.string.note_extra_region_label)
	else -> ""
}

private fun buildUpdatedNote(
	editingNote: Note,
	selectedType: String,
	content: String,
	extraField: String
): Note = when (selectedType) {
	// Editing can change the note subtype, so we rebuild the sealed model explicitly
	// instead of mutating only the shared text field.
	NOTE_TYPE_LORE -> Note.Lore(
		id = editingNote.id,
		campaignId = editingNote.campaignId,
		content = content,
		createdAt = editingNote.createdAt,
		historicalEra = extraField
	)

	NOTE_TYPE_NPC -> {
		val (faction, attitude) = parseNpcExtra(extraField)
		Note.NPC(
			id = editingNote.id,
			campaignId = editingNote.campaignId,
			content = content,
			createdAt = editingNote.createdAt,
			faction = faction,
			attitude = attitude
		)
	}

	NOTE_TYPE_LOCATION -> Note.Location(
		id = editingNote.id,
		campaignId = editingNote.campaignId,
		content = content,
		createdAt = editingNote.createdAt,
		region = extraField
	)

	else -> Note.General(
		id = editingNote.id,
		campaignId = editingNote.campaignId,
		content = content,
		createdAt = editingNote.createdAt
	)
}
