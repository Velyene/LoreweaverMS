package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R

internal data class EncounterCreationDialogState(
	val showAddDialog: Boolean,
	val encounterName: String
)

internal data class EncounterCreationDialogActions(
	val onEncounterNameChange: (String) -> Unit,
	val onCreateWithoutMonsters: () -> Unit,
	val onDismiss: () -> Unit
)

@Composable
internal fun EncounterCreationDialogs(
	state: EncounterCreationDialogState,
	actions: EncounterCreationDialogActions
) {
	if (state.showAddDialog) {
		NewEncounterDialog(
			encounterName = state.encounterName,
			onEncounterNameChange = actions.onEncounterNameChange,
			onCreateWithoutMonsters = actions.onCreateWithoutMonsters,
			onDismiss = actions.onDismiss
		)
	}
}

@Composable
private fun NewEncounterDialog(
	encounterName: String,
	onEncounterNameChange: (String) -> Unit,
	onCreateWithoutMonsters: () -> Unit,
	onDismiss: () -> Unit
) {
	val canCreateEncounter = encounterName.isNotBlank()
	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text(text = stringResource(R.string.new_encounter_title)) },
		text = {
			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				OutlinedTextField(
					value = encounterName,
					onValueChange = onEncounterNameChange,
					label = { Text(text = stringResource(R.string.encounter_name_label)) },
					modifier = Modifier.fillMaxWidth()
				)
				Text(
					text = stringResource(R.string.monster_import_removed_message),
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					fontSize = 12.sp
				)
			}
		},
		confirmButton = {
			Button(onClick = onCreateWithoutMonsters, enabled = canCreateEncounter) {
				Text(text = stringResource(R.string.create_without_monsters_button))
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(text = stringResource(R.string.cancel_button))
			}
		}
	)
}
