/*
 * FILE: EncounterSetupNotesDialog.kt
 *
 * TABLE OF CONTENTS:
 * 1. Notes input section
 * 2. Add enemy dialog
 */

package io.github.velyene.loreweaver.ui.screens.tracker.setup

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.DEFAULT_ENEMY_HP
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.DEFAULT_ENEMY_INITIATIVE
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.NOTES_HEIGHT_DP

@Composable
internal fun EncounterNotesSection(
	notes: String,
	onNotesChange: (String) -> Unit
) {
	val notesFieldDescription = stringResource(R.string.encounter_notes_title)

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.height(NOTES_HEIGHT_DP.dp)
			.border(1.dp, MaterialTheme.colorScheme.outline, androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
			.padding(12.dp)
	) {
		Text(
			stringResource(R.string.encounter_notes_title),
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			style = MaterialTheme.typography.labelSmall,
			modifier = Modifier.semantics { heading() }
		)
		BasicTextField(
			value = notes,
			onValueChange = onNotesChange,
			textStyle = TextStyle(
				color = MaterialTheme.colorScheme.onSurface,
				fontSize = 14.sp
			),
			modifier = Modifier
				.fillMaxSize()
				.semantics {
					contentDescription = notesFieldDescription
				}
		)
	}
}

@Composable
internal fun AddEnemyDialog(
	initialName: String = "",
	initialHp: String = DEFAULT_ENEMY_HP.toString(),
	initialInitiative: String = DEFAULT_ENEMY_INITIATIVE.toString(),
	initialQuantity: String = "1",
	titleOverride: String? = null,
	helperTextOverride: String? = null,
	onConfirm: (name: String, hp: Int, initiative: Int, quantity: Int) -> Unit,
	onDismiss: () -> Unit
) {
	var name by remember { mutableStateOf(initialName) }
	var hp by remember { mutableStateOf(initialHp) }
	var initiative by remember { mutableStateOf(initialInitiative) }
	var quantity by remember { mutableStateOf(initialQuantity) }

	AlertDialog(
		onDismissRequest = onDismiss,
		title = {
			Text(
				titleOverride ?: stringResource(R.string.add_enemy_dialog_title),
				modifier = Modifier.semantics { heading() }
			)
		},
		text = {
			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				helperTextOverride?.let { Text(text = it, style = MaterialTheme.typography.bodySmall) }
				OutlinedTextField(
					value = name,
					onValueChange = { name = it },
					label = { Text(stringResource(R.string.name_label)) },
					modifier = Modifier.fillMaxWidth()
				)
				Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
					OutlinedTextField(
						value = hp,
						onValueChange = { hp = it.filter { c -> c.isDigit() } },
						label = { Text(stringResource(R.string.hp_label)) },
						keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
						modifier = Modifier.weight(1f)
					)
					OutlinedTextField(
						value = initiative,
						onValueChange = { initiative = it.filter { c -> c.isDigit() } },
						label = { Text(stringResource(R.string.initiative_label)) },
						keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
						modifier = Modifier.weight(1f)
					)
				}
				OutlinedTextField(
					value = quantity,
					onValueChange = { quantity = it.filter { c -> c.isDigit() } },
					label = { Text(stringResource(R.string.encounter_enemy_quantity_label)) },
					keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
					modifier = Modifier.fillMaxWidth()
				)
			}
		},
		confirmButton = {
			Button(
				onClick = {
					if (name.isNotBlank()) {
						onConfirm(
							name.trim(),
							hp.toIntOrNull() ?: DEFAULT_ENEMY_HP,
							initiative.toIntOrNull() ?: DEFAULT_ENEMY_INITIATIVE,
							quantity.toIntOrNull()?.coerceAtLeast(1) ?: 1
						)
					}
				}
			) { Text(stringResource(R.string.add_button)) }
		},
		dismissButton = {
			TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel_button)) }
		}
	)
}

