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
	var showAddDialog by remember { mutableStateOf(false) }
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
}
