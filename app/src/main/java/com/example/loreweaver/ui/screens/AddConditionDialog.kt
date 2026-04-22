package com.example.loreweaver.ui.screens

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Dialog for adding a condition to a combatant.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddConditionDialog(
	onConfirm: (conditionName: String, duration: Int?) -> Unit,
	onDismiss: () -> Unit
) {
	var selectedCondition by remember { mutableStateOf("") }
	var duration by remember { mutableStateOf("") }
	var hasDuration by remember { mutableStateOf(true) }
	var expanded by remember { mutableStateOf(false) }

	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text("Add Condition") },
		text = {
			Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
				// Condition selector
				ExposedDropdownMenuBox(
					expanded = expanded,
					onExpandedChange = { expanded = !expanded },
					modifier = Modifier.fillMaxWidth()
				) {
					OutlinedTextField(
						value = selectedCondition,
						onValueChange = {},
						readOnly = true,
						label = { Text("Condition") },
						trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
						modifier = Modifier
							.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
							.fillMaxWidth()
					)
					ExposedDropdownMenu(
						expanded = expanded,
						onDismissRequest = { expanded = false }
					) {
						ConditionConstants.ALL_CONDITIONS.forEach { condition ->
							DropdownMenuItem(
								text = { Text(condition) },
								onClick = {
									selectedCondition = condition
									expanded = false
								}
							)
						}
					}
				}

				// Duration toggle
				Row(
					modifier = Modifier.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically
				) {
					Checkbox(
						checked = hasDuration,
						onCheckedChange = { hasDuration = it }
					)
					Text("Has duration (in rounds)")
				}

				// Duration input
				if (hasDuration) {
					OutlinedTextField(
						value = duration,
						onValueChange = { if (it.all { c -> c.isDigit() }) duration = it },
						label = { Text("Duration (rounds)") },
						keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
						modifier = Modifier.fillMaxWidth(),
						placeholder = { Text("e.g., 3") }
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
						onConfirm(selectedCondition, dur)
					}
				},
				enabled = selectedCondition.isNotBlank()
			) {
				Text("Add")
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) { Text("Cancel") }
		}
	)
}
