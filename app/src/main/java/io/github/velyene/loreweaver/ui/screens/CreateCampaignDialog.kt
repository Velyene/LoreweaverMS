package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R

internal const val CREATE_CAMPAIGN_NAME_FIELD_TAG = "create_campaign_name_field"
internal const val CREATE_CAMPAIGN_DESCRIPTION_FIELD_TAG = "create_campaign_description_field"

@Composable
internal fun CreateCampaignDialog(
	title: String = stringResource(R.string.create_campaign_title),
	confirmLabel: String = stringResource(R.string.create_button),
	initialName: String = "",
	initialDescription: String = "",
	onDismiss: () -> Unit,
	onConfirm: (name: String, desc: String) -> Unit
) {
	var name by remember(initialName) { mutableStateOf(initialName) }
	var desc by remember(initialDescription) { mutableStateOf(initialDescription) }
	val trimmedName = name.trim()

	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text(text = title) },
		text = {
			Column {
				OutlinedTextField(
					value = name,
					onValueChange = { name = it },
					label = { Text(text = stringResource(R.string.campaign_name_label)) },
					modifier = Modifier.testTag(CREATE_CAMPAIGN_NAME_FIELD_TAG)
				)
				Spacer(modifier = Modifier.height(8.dp))
				OutlinedTextField(
					value = desc,
					onValueChange = { desc = it },
					label = { Text(text = stringResource(R.string.description_label)) },
					modifier = Modifier.testTag(CREATE_CAMPAIGN_DESCRIPTION_FIELD_TAG)
				)
			}
		},
		confirmButton = {
			Button(
				onClick = { if (trimmedName.isNotEmpty()) onConfirm(trimmedName, desc) },
				enabled = trimmedName.isNotEmpty()
			) { Text(text = confirmLabel) }
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(text = stringResource(R.string.cancel_button))
			}
		}
	)
}
