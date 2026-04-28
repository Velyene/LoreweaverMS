package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.RemoteItem

@Composable
internal fun LinkedEncounterList(
	encounters: List<Encounter>,
	onEncounterClick: (String) -> Unit,
	onAddEncounter: (String) -> Unit,
	onAddEncounterWithMonsters: (String, List<RemoteItem>) -> Unit,
) {
	@Suppress("UNUSED_PARAMETER")
	fun keepApiStable(unused: (String, List<RemoteItem>) -> Unit) = Unit

	keepApiStable(onAddEncounterWithMonsters)
	var showAddDialog by remember { mutableStateOf(false) }
	var encounterName by remember { mutableStateOf("") }

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp)
	) {
		EncounterListHeader(onAddEncounterClick = {
			encounterName = ""
			showAddDialog = true
		})
		EncounterListBody(
			encounters = encounters,
			onEncounterClick = onEncounterClick,
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f),
		)
	}

	EncounterCreationDialogs(
		state = EncounterCreationDialogState(
			showAddDialog = showAddDialog,
			encounterName = encounterName,
		),
		actions = EncounterCreationDialogActions(
			onEncounterNameChange = {
				@Suppress("UNUSED_VALUE")
				encounterName = it
			},
			onCreateWithoutMonsters = {
				onAddEncounter(encounterName.trim())
				@Suppress("UNUSED_VALUE")
				showAddDialog = false
				@Suppress("UNUSED_VALUE")
				encounterName = ""
			},
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				showAddDialog = false
			},
		),
	)
}
