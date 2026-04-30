package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R

/**
 * Dialog for adding a condition to a combatant.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddConditionDialog(
	onConfirm: (conditionName: String, duration: Int?, persistsAcrossEncounters: Boolean) -> Unit,
	onDismiss: () -> Unit
) {
	var selectedCondition by remember { mutableStateOf("") }
	var duration by remember { mutableStateOf("") }
	var hasDuration by remember { mutableStateOf(true) }
	var persistsAcrossEncounters by remember { mutableStateOf(false) }
	var expanded by remember { mutableStateOf(false) }
	val selectedConditionLabel = ConditionConstants.ALL_CONDITIONS
		.firstOrNull { it.name == selectedCondition }
		?.let { stringResource(it.labelRes) }
		.orEmpty()

	AlertDialog(
		onDismissRequest = onDismiss,
		title = {
			Text(
				text = stringResource(R.string.add_condition_dialog_title),
				modifier = Modifier.semantics { heading() }
			)
		},
		text = {
			Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
				ExposedDropdownMenuBox(
					expanded = expanded,
					onExpandedChange = { expanded = !expanded },
					modifier = Modifier.fillMaxWidth()
				) {
					OutlinedTextField(
						value = selectedConditionLabel,
						onValueChange = {},
						readOnly = true,
						label = { Text(stringResource(R.string.add_condition_dialog_condition_label)) },
						trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
						modifier = Modifier
							.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
							.fillMaxWidth()
					)
					ExposedDropdownMenu(
						expanded = expanded,
						onDismissRequest = { expanded = false }
					) {
						ConditionConstants.ALL_STATUS_LABELS.forEach { condition ->
							DropdownMenuItem(
								text = { Text(stringResource(condition.labelRes)) },
								onClick = {
									selectedCondition = condition.name
									selectedCondition = condition
									persistsAcrossEncounters = ConditionConstants.defaultPersistsAcrossEncounters(condition)
									expanded = false
								}
							)
						}
					}
				}

				Row(
					modifier = Modifier.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically
				) {
					Checkbox(
						checked = hasDuration,
						onCheckedChange = { hasDuration = it }
					)
					Text(stringResource(R.string.add_condition_dialog_has_duration_label))
				}

				if (hasDuration) {
					OutlinedTextField(
						value = duration,
						onValueChange = { if (it.all { c -> c.isDigit() }) duration = it },
						label = { Text(stringResource(R.string.add_condition_dialog_duration_label)) },
						keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
						modifier = Modifier.fillMaxWidth(),
						placeholder = { Text(stringResource(R.string.add_condition_dialog_duration_placeholder)) }
					)
				}
			}
		},
		confirmButton = {
			Button(
				onClick = {
					if (selectedCondition.isNotBlank()) {
						val dur = if (hasDuration && duration.isNotBlank())
							duration.toIntOrNull()
						else
							null
						onConfirm(selectedCondition, dur, persistsAcrossEncounters)
					}
				},
				enabled = selectedCondition.isNotBlank()
			) {
				Text(stringResource(R.string.add_button))
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel_button)) }
		}
	)
}
