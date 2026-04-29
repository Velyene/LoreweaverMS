package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.ui.viewmodels.CharacterViewModel

@Composable
internal fun JournalTab(
	character: CharacterEntry,
	viewModel: CharacterViewModel,
	onLookupCondition: (String) -> Unit = {}
) {
	Text(stringResource(R.string.inventory_label), style = MaterialTheme.typography.titleMedium)
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 8.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			if (character.inventory.isEmpty()) {
				Text(stringResource(R.string.empty_label))
			} else {
				character.inventory.forEach { item ->
					Text(stringResource(R.string.inventory_bullet, item))
				}
			}
		}
	}

	Spacer(modifier = Modifier.height(16.dp))
	Text(stringResource(R.string.conditions_label), style = MaterialTheme.typography.titleMedium)
	if (character.activeConditions.isEmpty()) {
		Text(stringResource(R.string.none_label), style = MaterialTheme.typography.bodyMedium)
	} else {
		character.activeConditions.forEach { cond ->
			InputChip(
				selected = true,
				onClick = { onLookupCondition(cond) },
				label = { Text(cond) },
				trailingIcon = {
					Icon(
						Icons.Default.Close,
						null,
						Modifier
							.size(16.dp)
							.clickable {
								viewModel.updateCharacter(
									character.copy(activeConditions = character.activeConditions - cond)
								)
							}
					)
				}
			)
		}
	}

	Spacer(modifier = Modifier.height(16.dp))
	Text(stringResource(R.string.notes_label), style = MaterialTheme.typography.titleMedium)
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 8.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Text(
			character.notes.ifBlank { stringResource(R.string.no_notes_label) },
			modifier = Modifier.padding(16.dp)
		)
	}
}

