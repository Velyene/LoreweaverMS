/*
 * FILE: CampaignDetailEncounterDialogs.kt
 *
 * TABLE OF CONTENTS:
 * 1. Constants
 * 2. Dialog state models
 * 3. EncounterCreationDialogs
 * 4. NewEncounterDialog
 * 5. NewEncounterDialogContent
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.util.MonsterReferenceEntry
import androidx.compose.foundation.layout.Arrangement as LayoutArrangement

internal const val ENCOUNTER_REMOTE_ITEM_CATEGORY = "monster"
internal const val ENCOUNTER_CR_LABEL_PREFIX = "CR "
internal const val ENCOUNTER_MONSTER_PICKER_ANIMATION_LABEL = "encounterMonsterPickerList"
internal const val NEW_ENCOUNTER_NAME_FIELD_TAG = "new_encounter_name_field"
internal const val ENCOUNTER_ANIMALS_ONLY_FILTER_KEY = "animals-only"
internal const val ENCOUNTER_SELECTED_ONLY_FILTER_KEY = "selected-only"
internal const val ENCOUNTER_SELECTED_SUMMARY_OVERFLOW_KEY = "remaining"

internal val encounterDialogSectionSpacing = 12.dp
internal val encounterDismissButtonSpacing = 8.dp
internal val encounterFilterSectionSpacing = 10.dp
internal val encounterChipSpacing = 8.dp
internal val encounterChipGroupSpacing = 6.dp
internal val encounterPickerMinHeight = 180.dp
internal val encounterPickerMaxHeight = 320.dp
internal val encounterPickerHeaderBottomPadding = 8.dp
internal val encounterPickerTopPadding = 24.dp
internal val encounterSectionCardPadding = 12.dp
internal val encounterSelectedCountHorizontalPadding = 8.dp
internal val encounterSelectedCountVerticalPadding = 12.dp
internal val encounterHelperTextSize = 12.sp

internal enum class EncounterMonsterSortMode {
	NAME,
	CHALLENGE_RATING
}

internal enum class EncounterPickerFocusTarget {
	SELECTED_SUMMARY,
	PICKER_HEADER
}

internal data class EncounterSelectedMonsterSummary(
	val key: String,
	val name: String,
	val count: Int,
	val challengeRating: String
)

internal data class EncounterCreationDialogState(
	val showAddDialog: Boolean,
	val encounterName: String,
	val monsterSearchQuery: String = "",
	val showAnimalsOnly: Boolean = false,
	val selectedMonsterCreatureType: String? = null,
	val selectedMonsterChallengeRating: String? = null,
	val monsterSortMode: EncounterMonsterSortMode = EncounterMonsterSortMode.NAME,
	val showSelectedMonstersOnly: Boolean = false,
	val selectedMonsterCounts: Map<String, Int> = emptyMap()
)

internal data class EncounterCreationDialogActions(
	val onEncounterNameChange: (String) -> Unit,
	val onMonsterSearchQueryChange: (String) -> Unit,
	val onAnimalsOnlyChange: (Boolean) -> Unit,
	val onMonsterCreatureTypeChange: (String?) -> Unit,
	val onMonsterChallengeRatingChange: (String?) -> Unit,
	val onMonsterSortModeChange: (EncounterMonsterSortMode) -> Unit,
	val onSelectedOnlyChange: (Boolean) -> Unit,
	val onMonsterCountChange: (String, Int) -> Unit,
	val onDecrementSelectedMonster: (String) -> Unit,
	val onRemoveSelectedMonster: (String) -> Unit,
	val onClearMonsterFilters: () -> Unit,
	val onClearMonsterSelection: () -> Unit,
	val onCreateEncounter: () -> Unit,
	val onCreateWithoutMonsters: () -> Unit,
	val onDismiss: () -> Unit
)

internal data class EncounterMonsterFilterSectionState(
	val showAnimalsOnly: Boolean,
	val selectedCreatureType: String?,
	val selectedChallengeRating: String?,
	val selectedSortMode: EncounterMonsterSortMode,
	val showSelectedMonstersOnly: Boolean,
	val hasActiveFilters: Boolean
)

internal data class EncounterMonsterFilterSectionActions(
	val onAnimalsOnlyChange: (Boolean) -> Unit,
	val onCreatureTypeSelected: (String?) -> Unit,
	val onChallengeRatingSelected: (String?) -> Unit,
	val onSortModeSelected: (EncounterMonsterSortMode) -> Unit,
	val onSelectedOnlyChange: (Boolean) -> Unit,
	val onClearFilters: () -> Unit
)

private data class NewEncounterDialogContentState(
	val filterSectionState: EncounterMonsterFilterSectionState,
	val filterSectionActions: EncounterMonsterFilterSectionActions,
	val selectedSummaryFocusRequester: FocusRequester,
	val pickerHeaderFocusRequester: FocusRequester,
	val selectedCountText: String,
	val visibleSelectedSummaries: List<EncounterSelectedMonsterSummary>,
	val remainingSelectedSummaryCount: Int,
	val selectedMonsterTotal: Int,
	val filteredMonsters: List<MonsterReferenceEntry>,
	val monsterListState: androidx.compose.foundation.lazy.LazyListState,
)

@Composable
internal fun EncounterCreationDialogs(
	state: EncounterCreationDialogState,
	actions: EncounterCreationDialogActions
) {
	if (state.showAddDialog) {
		NewEncounterDialog(
			state = state,
			actions = actions
		)
	}
}

@Composable
private fun NewEncounterDialog(
	state: EncounterCreationDialogState,
	actions: EncounterCreationDialogActions
) {
	val monsterListState = rememberLazyListState()
	val selectedSummaryFocusRequester = remember { FocusRequester() }
	val pickerHeaderFocusRequester = remember { FocusRequester() }
	val filteredMonsters = filteredEncounterMonsterEntries(
		query = state.monsterSearchQuery,
		animalsOnly = state.showAnimalsOnly,
		creatureType = state.selectedMonsterCreatureType,
		challengeRating = state.selectedMonsterChallengeRating,
		selectedMonsterCounts = state.selectedMonsterCounts,
		selectedOnly = state.showSelectedMonstersOnly,
		sortMode = state.monsterSortMode
	)
	val selectedMonsterSummary = selectedEncounterMonsterSummaries(state.selectedMonsterCounts)
	val selectedMonsterTotal = state.selectedMonsterCounts.values.sum()
	val hasSelectedMonsters = selectedMonsterTotal > 0
	val canCreateEncounter = state.encounterName.isNotBlank()
	val hasActiveFilters = hasActiveEncounterMonsterFilters(
		query = state.monsterSearchQuery,
		animalsOnly = state.showAnimalsOnly,
		creatureType = state.selectedMonsterCreatureType,
		challengeRating = state.selectedMonsterChallengeRating,
		selectedOnly = state.showSelectedMonstersOnly,
		sortMode = state.monsterSortMode
	)
	val createButtonText = if (selectedMonsterTotal > 0) {
		stringResource(R.string.create_encounter_with_monsters_button, selectedMonsterTotal)
	} else {
		stringResource(R.string.create_button)
	}
	val selectedCountText = pluralStringResource(
		R.plurals.encounter_selected_monsters_quantity,
		selectedMonsterTotal,
		selectedMonsterTotal
	)
	val (visibleSelectedSummaries, remainingSelectedSummaryCount) =
		compactSelectedEncounterMonsterSummary(selectedMonsterSummary)
	val filterSectionState = EncounterMonsterFilterSectionState(
		showAnimalsOnly = state.showAnimalsOnly,
		selectedCreatureType = state.selectedMonsterCreatureType,
		selectedChallengeRating = state.selectedMonsterChallengeRating,
		selectedSortMode = state.monsterSortMode,
		showSelectedMonstersOnly = state.showSelectedMonstersOnly,
		hasActiveFilters = hasActiveFilters
	)
	val filterSectionActions = EncounterMonsterFilterSectionActions(
		onAnimalsOnlyChange = actions.onAnimalsOnlyChange,
		onCreatureTypeSelected = actions.onMonsterCreatureTypeChange,
		onChallengeRatingSelected = actions.onMonsterChallengeRatingChange,
		onSortModeSelected = actions.onMonsterSortModeChange,
		onSelectedOnlyChange = actions.onSelectedOnlyChange,
		onClearFilters = actions.onClearMonsterFilters
	)
	val contentState = NewEncounterDialogContentState(
		filterSectionState = filterSectionState,
		filterSectionActions = filterSectionActions,
		selectedSummaryFocusRequester = selectedSummaryFocusRequester,
		pickerHeaderFocusRequester = pickerHeaderFocusRequester,
		selectedCountText = selectedCountText,
		visibleSelectedSummaries = visibleSelectedSummaries,
		remainingSelectedSummaryCount = remainingSelectedSummaryCount,
		selectedMonsterTotal = selectedMonsterTotal,
		filteredMonsters = filteredMonsters,
		monsterListState = monsterListState,
	)

	LaunchedEffect(state.showSelectedMonstersOnly) {
		if (state.showSelectedMonstersOnly) {
			if (filteredMonsters.isNotEmpty()) {
				monsterListState.scrollToItem(0)
			}
			when (encounterPickerFocusTarget(hasSelectedMonsters = hasSelectedMonsters)) {
				EncounterPickerFocusTarget.SELECTED_SUMMARY -> selectedSummaryFocusRequester.requestFocus()
				EncounterPickerFocusTarget.PICKER_HEADER -> pickerHeaderFocusRequester.requestFocus()
			}
		}
	}

	AlertDialog(
		onDismissRequest = actions.onDismiss,
		title = { Text(text = stringResource(R.string.new_encounter_title)) },
		text = {
			NewEncounterDialogContent(
				state = state,
				actions = actions,
				contentState = contentState
			)
		},
		confirmButton = {
			Button(onClick = actions.onCreateEncounter, enabled = canCreateEncounter) {
				Text(text = createButtonText)
			}
		},
		dismissButton = {
			Row(horizontalArrangement = LayoutArrangement.spacedBy(encounterDismissButtonSpacing)) {
				TextButton(onClick = actions.onCreateWithoutMonsters, enabled = canCreateEncounter) {
					Text(text = stringResource(R.string.create_without_monsters_button))
				}
				TextButton(onClick = actions.onDismiss) {
					Text(text = stringResource(R.string.cancel_button))
				}
			}
		}
	)
}

@Composable
private fun NewEncounterDialogContent(
	state: EncounterCreationDialogState,
	actions: EncounterCreationDialogActions,
	contentState: NewEncounterDialogContentState,
) {
	Column(verticalArrangement = LayoutArrangement.spacedBy(encounterDialogSectionSpacing)) {
		OutlinedTextField(
			value = state.encounterName,
			onValueChange = actions.onEncounterNameChange,
			label = { Text(text = stringResource(R.string.encounter_name_label)) },
			modifier = Modifier
				.fillMaxWidth()
				.testTag(NEW_ENCOUNTER_NAME_FIELD_TAG)
		)
		OutlinedTextField(
			value = state.monsterSearchQuery,
			onValueChange = actions.onMonsterSearchQueryChange,
			label = { Text(text = stringResource(R.string.reference_search_hint)) },
			modifier = Modifier.fillMaxWidth(),
			singleLine = true
		)
		MonsterEncounterFilterSection(
			state = contentState.filterSectionState,
			actions = contentState.filterSectionActions
		)
		SelectedEncounterMonsterSummarySection(
			modifier = Modifier.focusRequester(contentState.selectedSummaryFocusRequester),
			selectedCountText = contentState.selectedCountText,
			summaries = contentState.visibleSelectedSummaries,
			remainingCount = contentState.remainingSelectedSummaryCount,
			onDecrementSummary = actions.onDecrementSelectedMonster,
			onRemoveSummary = actions.onRemoveSelectedMonster,
			onClearSelection = actions.onClearMonsterSelection,
			hasSelection = contentState.selectedMonsterTotal > 0
		)
		EncounterMonsterPickerSection(
			filteredMonsters = contentState.filteredMonsters,
			selectedMonsterCounts = state.selectedMonsterCounts,
			selectedMonsterTotal = contentState.selectedMonsterTotal,
			monsterListState = contentState.monsterListState,
			pickerHeaderFocusRequester = contentState.pickerHeaderFocusRequester,
			onMonsterCountChange = actions.onMonsterCountChange
		)
		Text(
			text = stringResource(R.string.encounter_monster_picker_helper),
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			fontSize = encounterHelperTextSize
		)
	}
}
