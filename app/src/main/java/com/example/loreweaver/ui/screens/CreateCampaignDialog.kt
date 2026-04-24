package com.example.loreweaver.ui.screens

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.loreweaver.R

@Composable
internal fun CreateCampaignDialog(
	onDismiss: () -> Unit,
	onCreate: (name: String, desc: String) -> Unit
) {
	var name by remember { mutableStateOf("") }
	var desc by remember { mutableStateOf("") }

	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text(text = stringResource(R.string.create_campaign_title)) },
		text = {
			Column {
				OutlinedTextField(
					value = name,
					onValueChange = { name = it },
					label = { Text(text = stringResource(R.string.campaign_name_label)) }
				)
				Spacer(modifier = Modifier.height(8.dp))
				OutlinedTextField(
					value = desc,
					onValueChange = { desc = it },
					label = { Text(text = stringResource(R.string.description_label)) }
				)
			}
		},
		confirmButton = {
			Button(
				onClick = { if (name.isNotEmpty()) onCreate(name, desc) },
				enabled = name.isNotEmpty()
			) { Text(text = stringResource(R.string.create_button)) }
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(text = stringResource(R.string.cancel_button))
			}
		}
	)
}
