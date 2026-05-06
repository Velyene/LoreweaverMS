/*
 * FILE: CampaignDetailEncountersSection.kt
 *
 * TABLE OF CONTENTS:
 * 1. Value: EDIT_ENCOUNTER_NAME_FIELD_TAG
 * 2. Function: LinkedEncounterList
 * 3. Value: showAddDialog
 * 4. Value: encounterBeingEdited
 * 5. Value: encounterPendingDelete
 * 6. Value: encounterName
 * 7. Value: monsterSearchQuery
 * 8. Value: showAnimalsOnly
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.RemoteItem

internal const val EDIT_ENCOUNTER_NAME_FIELD_TAG = "edit_encounter_name_field"

@Composable
internal fun LinkedEncounterList(
	encounters: List<Encounter>,
	onEncounterClick: (String) -> Unit,
	onAddEncounter: (String) -> Unit,
	onAddEncounterWithMonsters: (String, List<RemoteItem>) -> Unit,
	onUpdateEncounter: (Encounter, String) -> Unit,
	onDeleteEncounter: (Encounter) -> Unit,
) {
	var showAddDialog by remember { mutableStateOf(false) }
	var encounterBeingEdited by remember { mutableStateOf<Encounter?>(null) }
	var encounterPendingDelete by remember { mutableStateOf<Encounter?>(null) }
	var encounterName by remember { mutableStateOf("") }
	var monsterSearchQuery by remember { mutableStateOf("") }
	var showAnimalsOnly by remember { mutableStateOf(false) }
	var selectedMonsterCreatureType by remember { mutableStateOf<String?>(null) }
	var selectedMonsterChallengeRating by remember { mutableStateOf<String?>(null) }
	var monsterSortMode by remember { mutableStateOf(EncounterMonsterSortMode.NAME) }
	var showSelectedMonstersOnly by remember { mutableStateOf(false) }
	var selectedMonsterCounts by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }

	fun clearMonsterFilters() {
		@Suppress("UNUSED_VALUE")
		monsterSearchQuery = ""
		@Suppress("UNUSED_VALUE")
		showAnimalsOnly = false
		@Suppress("UNUSED_VALUE")
		selectedMonsterCreatureType = null
		@Suppress("UNUSED_VALUE")
		selectedMonsterChallengeRating = null
		@Suppress("UNUSED_VALUE")
		monsterSortMode = EncounterMonsterSortMode.NAME
		@Suppress("UNUSED_VALUE")
		showSelectedMonstersOnly = false
	}

	fun clearMonsterSelection() {
		@Suppress("UNUSED_VALUE")
		selectedMonsterCounts = emptyMap()
	}

	fun resetEncounterDialogState() {
		@Suppress("UNUSED_VALUE")
		showAddDialog = false
		@Suppress("UNUSED_VALUE")
		encounterName = ""
		clearMonsterFilters()
		clearMonsterSelection()
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp)
	) {
		EncounterListHeader(onAddEncounterClick = {
			resetEncounterDialogState()
			@Suppress("UNUSED_VALUE")
			showAddDialog = true
		})
		EncounterListBody(
			encounters = encounters,
			onEncounterClick = onEncounterClick,
			onEditEncounter = { encounterBeingEdited = it },
			onDeleteEncounter = { encounterPendingDelete = it },
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f),
		)
	}

	EncounterCreationDialogs(
		state = EncounterCreationDialogState(
			showAddDialog = showAddDialog,
			encounterName = encounterName,
			monsterSearchQuery = monsterSearchQuery,
			showAnimalsOnly = showAnimalsOnly,
			selectedMonsterCreatureType = selectedMonsterCreatureType,
			selectedMonsterChallengeRating = selectedMonsterChallengeRating,
			monsterSortMode = monsterSortMode,
			showSelectedMonstersOnly = showSelectedMonstersOnly,
			selectedMonsterCounts = selectedMonsterCounts,
		),
		actions = EncounterCreationDialogActions(
			onEncounterNameChange = {
				@Suppress("UNUSED_VALUE")
				encounterName = it
			},
			onMonsterSearchQueryChange = {
				@Suppress("UNUSED_VALUE")
				monsterSearchQuery = it
			},
			onAnimalsOnlyChange = {
				@Suppress("UNUSED_VALUE")
				showAnimalsOnly = it
			},
			onMonsterCreatureTypeChange = {
				@Suppress("UNUSED_VALUE")
				selectedMonsterCreatureType = it
			},
			onMonsterChallengeRatingChange = {
				@Suppress("UNUSED_VALUE")
				selectedMonsterChallengeRating = it
			},
			onMonsterSortModeChange = {
				@Suppress("UNUSED_VALUE")
				monsterSortMode = it
			},
			onSelectedOnlyChange = {
				@Suppress("UNUSED_VALUE")
				showSelectedMonstersOnly = it
			},
			onMonsterCountChange = { monsterKey, delta ->
				@Suppress("UNUSED_VALUE")
				selectedMonsterCounts = updateEncounterMonsterSelection(
					selectedMonsterCounts = selectedMonsterCounts,
					monsterKey = monsterKey,
					delta = delta
				)
			},
			onDecrementSelectedMonster = { monsterKey ->
				@Suppress("UNUSED_VALUE")
				selectedMonsterCounts = decrementEncounterMonsterSelection(
					selectedMonsterCounts = selectedMonsterCounts,
					monsterKey = monsterKey
				)
			},
			onRemoveSelectedMonster = { monsterKey ->
				@Suppress("UNUSED_VALUE")
				selectedMonsterCounts = removeEncounterMonsterSelection(
					selectedMonsterCounts = selectedMonsterCounts,
					monsterKey = monsterKey
				)
			},
			onClearMonsterFilters = {
				clearMonsterFilters()
			},
			onClearMonsterSelection = {
				clearMonsterSelection()
			},
			onCreateEncounter = {
				val selectedMonsters = buildEncounterMonsterRemoteItems(selectedMonsterCounts)
				if (selectedMonsters.isEmpty()) {
					onAddEncounter(encounterName.trim())
				} else {
					onAddEncounterWithMonsters(encounterName.trim(), selectedMonsters)
				}
				resetEncounterDialogState()
			},
			onCreateWithoutMonsters = {
				onAddEncounter(encounterName.trim())
				resetEncounterDialogState()
			},
			onDismiss = {
				resetEncounterDialogState()
			},
		),
	)

	encounterBeingEdited?.let { encounter ->
		EditEncounterDialog(
			encounter = encounter,
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				encounterBeingEdited = null
			},
			onConfirm = { updatedName ->
				onUpdateEncounter(encounter, updatedName)
				@Suppress("UNUSED_VALUE")
				encounterBeingEdited = null
			}
		)
	}

	encounterPendingDelete?.let { encounter ->
		ConfirmationDialog(
			title = stringResource(R.string.confirm_delete_encounter_title),
			message = stringResource(R.string.confirm_delete_encounter_message, encounter.name),
			onConfirm = {
				onDeleteEncounter(encounter)
				@Suppress("UNUSED_VALUE")
				encounterPendingDelete = null
			},
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				encounterPendingDelete = null
			},
			confirmLabel = stringResource(R.string.delete_button),
		)
	}
}

@Composable
private fun EditEncounterDialog(
	encounter: Encounter,
	onDismiss: () -> Unit,
	onConfirm: (String) -> Unit,
) {
	var name by remember(encounter.id) { mutableStateOf(encounter.name) }
	val trimmedName = name.trim()

	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text(text = stringResource(R.string.edit_encounter_title)) },
		text = {
			OutlinedTextField(
				value = name,
				onValueChange = { name = it },
				label = { Text(text = stringResource(R.string.encounter_name_label)) },
				modifier = Modifier
					.fillMaxWidth()
					.testTag(EDIT_ENCOUNTER_NAME_FIELD_TAG)
			)
		},
		confirmButton = {
			Button(
				onClick = { if (trimmedName.isNotEmpty()) onConfirm(trimmedName) },
				enabled = trimmedName.isNotEmpty(),
			) {
				Text(text = stringResource(R.string.save_button))
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(text = stringResource(R.string.cancel_button))
			}
		}
	)
}
