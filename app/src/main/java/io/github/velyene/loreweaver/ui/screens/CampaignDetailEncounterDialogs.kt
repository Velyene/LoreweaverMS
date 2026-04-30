/*
 * FILE: CampaignDetailEncounterDialogs.kt
 *
 * TABLE OF CONTENTS:
 * 1. Encounter dialog constants, enums, and state models
 * 2. Encounter creation dialog host and main content
 * 3. Monster filter, picker, and detail-preview composables
 * 4. Selected-monster summary and selection support
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.RemoteItem
import io.github.velyene.loreweaver.domain.util.MonsterReferenceCatalog
import io.github.velyene.loreweaver.domain.util.MonsterReferenceEntry
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver
import io.github.velyene.loreweaver.domain.util.parseMonsterChallengeRatingValue
import androidx.compose.foundation.layout.Arrangement as LayoutArrangement

private const val ENCOUNTER_REMOTE_ITEM_CATEGORY = "monster"
private const val ENCOUNTER_CR_LABEL_PREFIX = "CR "
private const val ENCOUNTER_MONSTER_PICKER_ANIMATION_LABEL = "encounterMonsterPickerList"
private const val ENCOUNTER_ANIMALS_ONLY_FILTER_KEY = "animals-only"
private const val ENCOUNTER_SELECTED_ONLY_FILTER_KEY = "selected-only"
private const val ENCOUNTER_SELECTED_SUMMARY_OVERFLOW_KEY = "remaining"

private val encounterDialogSectionSpacing = 12.dp
private val encounterDismissButtonSpacing = 8.dp
private val encounterFilterSectionSpacing = 10.dp
private val encounterChipSpacing = 8.dp
private val encounterChipGroupSpacing = 6.dp
private val encounterPickerMinHeight = 180.dp
private val encounterPickerMaxHeight = 320.dp
private val encounterPickerHeaderBottomPadding = 8.dp
private val encounterPickerTopPadding = 24.dp
private val encounterSectionCardPadding = 12.dp
private val encounterSelectedCountHorizontalPadding = 8.dp
private val encounterSelectedCountVerticalPadding = 12.dp
private val encounterHelperTextSize = 12.sp
private val encounterDialogContentMinHeight = 320.dp
private const val encounterDialogContentMaxHeightFraction = 0.55f
private val encounterDialogScrollbarPadding = 12.dp

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

private data class EncounterMonsterFilterSectionState(
	val showAnimalsOnly: Boolean,
	val selectedCreatureType: String?,
	val selectedChallengeRating: String?,
	val selectedSortMode: EncounterMonsterSortMode,
	val showSelectedMonstersOnly: Boolean,
	val hasActiveFilters: Boolean
)

private data class EncounterMonsterFilterSectionActions(
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
	val monsterListState: LazyListState,
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
	val scrollState = rememberScrollState()
	val screenHeight = LocalConfiguration.current.screenHeightDp.dp
	BoxWithConstraints(
		modifier = Modifier
			.fillMaxWidth()
			.heightIn(max = (screenHeight * encounterDialogContentMaxHeightFraction).coerceAtLeast(encounterDialogContentMinHeight))
			.visibleVerticalScrollbar(scrollState)
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.verticalScroll(scrollState)
				.padding(end = encounterDialogScrollbarPadding),
			verticalArrangement = LayoutArrangement.spacedBy(encounterDialogSectionSpacing)
		) {
			OutlinedTextField(
				value = state.encounterName,
				onValueChange = actions.onEncounterNameChange,
				label = { Text(text = stringResource(R.string.encounter_name_label)) },
				modifier = Modifier.fillMaxWidth()
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
}

@Composable
private fun EncounterMonsterPickerSection(
	filteredMonsters: List<MonsterReferenceEntry>,
	selectedMonsterCounts: Map<String, Int>,
	selectedMonsterTotal: Int,
	monsterListState: LazyListState,
	pickerHeaderFocusRequester: FocusRequester,
	onMonsterCountChange: (String, Int) -> Unit
) {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.heightIn(min = encounterPickerMinHeight, max = encounterPickerMaxHeight)
	) {
		PickerMonsterCountHeader(
			selectedCount = selectedMonsterTotal,
			filteredCount = filteredMonsters.size,
			modifier = Modifier
				.fillMaxWidth()
				.padding(bottom = encounterPickerHeaderBottomPadding)
				.focusRequester(pickerHeaderFocusRequester)
				.focusable()
				.semantics { liveRegion = LiveRegionMode.Polite }
		)
		if (filteredMonsters.isEmpty()) {
			Text(
				text = stringResource(R.string.encounter_monster_picker_empty),
				modifier = Modifier.padding(top = encounterPickerTopPadding),
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				fontSize = encounterHelperTextSize
			)
		} else {
			Crossfade(
				targetState = filteredMonsters,
				label = ENCOUNTER_MONSTER_PICKER_ANIMATION_LABEL
			) { visibleMonsters ->
				LazyColumn(
					state = monsterListState,
					modifier = Modifier
						.fillMaxHeight()
						.padding(top = encounterPickerTopPadding)
						.visibleVerticalScrollbar(monsterListState),
					verticalArrangement = LayoutArrangement.spacedBy(encounterChipSpacing)
				) {
					visibleMonsters.forEach { monster ->
						item(key = encounterMonsterKey(monster)) {
							val monsterKey = encounterMonsterKey(monster)
							EncounterMonsterPickerRow(
								monster = monster,
								count = selectedEncounterMonsterCount(selectedMonsterCounts, monsterKey),
								onCountChange = { delta -> onMonsterCountChange(monsterKey, delta) }
							)
						}
					}
				}
			}
		}
	}
}

@Composable
private fun MonsterEncounterFilterSection(
	state: EncounterMonsterFilterSectionState,
	actions: EncounterMonsterFilterSectionActions
) {
	Column(verticalArrangement = LayoutArrangement.spacedBy(encounterFilterSectionSpacing)) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = LayoutArrangement.SpaceBetween
		) {
			Text(
				text = stringResource(R.string.encounter_monster_sort_label),
				style = MaterialTheme.typography.labelLarge,
				color = MaterialTheme.colorScheme.secondary
			)
			TextButton(onClick = actions.onClearFilters, enabled = state.hasActiveFilters) {
				Text(text = stringResource(R.string.encounter_clear_filters_button))
			}
		}
		LazyRow(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = LayoutArrangement.spacedBy(encounterChipSpacing)
		) {
			item(key = EncounterMonsterSortMode.NAME.name) {
				FilterChip(
					selected = state.selectedSortMode == EncounterMonsterSortMode.NAME,
					onClick = { actions.onSortModeSelected(EncounterMonsterSortMode.NAME) },
					label = { Text(stringResource(R.string.encounter_monster_sort_name)) }
				)
			}
			item(key = EncounterMonsterSortMode.CHALLENGE_RATING.name) {
				FilterChip(
					selected = state.selectedSortMode == EncounterMonsterSortMode.CHALLENGE_RATING,
					onClick = { actions.onSortModeSelected(EncounterMonsterSortMode.CHALLENGE_RATING) },
					label = { Text(stringResource(R.string.encounter_monster_sort_cr)) }
				)
			}
			item(key = ENCOUNTER_ANIMALS_ONLY_FILTER_KEY) {
				FilterChip(
					selected = state.showAnimalsOnly,
					onClick = { actions.onAnimalsOnlyChange(!state.showAnimalsOnly) },
					label = { Text(stringResource(R.string.encounter_animals_shortcut_button)) }
				)
			}
			item(key = ENCOUNTER_SELECTED_ONLY_FILTER_KEY) {
				FilterChip(
					selected = state.showSelectedMonstersOnly,
					onClick = { actions.onSelectedOnlyChange(!state.showSelectedMonstersOnly) },
					label = { Text(stringResource(R.string.encounter_selected_only_button)) }
				)
			}
		}
		MonsterSingleSelectFilterRow(
			keyPrefix = ENCOUNTER_FILTER_CR_KEY_PREFIX,
			label = stringResource(R.string.reference_monster_filter_cr),
			selectedOption = state.selectedChallengeRating,
			options = MonsterReferenceCatalog.CHALLENGE_RATING_OPTIONS,
			onOptionSelected = actions.onChallengeRatingSelected
		)
		MonsterSingleSelectFilterRow(
			keyPrefix = ENCOUNTER_FILTER_TYPE_KEY_PREFIX,
			label = stringResource(R.string.reference_monster_filter_type),
			selectedOption = state.selectedCreatureType,
			options = MonsterReferenceCatalog.CREATURE_TYPE_OPTIONS,
			onOptionSelected = actions.onCreatureTypeSelected
		)
	}
}

@Composable
private fun PickerMonsterCountHeader(
	selectedCount: Int,
	filteredCount: Int,
	modifier: Modifier = Modifier
) {
	val headerLabel = rememberEncounterPickerCountHeaderLabel(selectedCount, filteredCount)
	Text(
		text = headerLabel,
		modifier = modifier.semantics {
			heading()
			liveRegion = LiveRegionMode.Polite
			contentDescription = headerLabel
		},
		style = MaterialTheme.typography.labelMedium,
		color = MaterialTheme.colorScheme.secondary
	)
}

@Composable
private fun SelectedEncounterMonsterSummarySection(
	modifier: Modifier = Modifier,
	selectedCountText: String,
	summaries: List<EncounterSelectedMonsterSummary>,
	remainingCount: Int,
	onDecrementSummary: (String) -> Unit,
	onRemoveSummary: (String) -> Unit,
	onClearSelection: () -> Unit,
	hasSelection: Boolean
) {
	Card(
		modifier = modifier
			.fillMaxWidth()
			.focusable(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(encounterSectionCardPadding),
			verticalArrangement = LayoutArrangement.spacedBy(encounterChipSpacing)
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = LayoutArrangement.SpaceBetween
			) {
				Text(
					text = selectedCountText,
					style = MaterialTheme.typography.labelLarge,
					color = MaterialTheme.colorScheme.primary
				)
				TextButton(onClick = onClearSelection, enabled = hasSelection) {
					Text(text = stringResource(R.string.encounter_clear_selection_button))
				}
			}
			SelectedEncounterMonsterSummaryContent(
				summaries = summaries,
				remainingCount = remainingCount,
				onDecrementSummary = onDecrementSummary,
				onRemoveSummary = onRemoveSummary
			)
		}
	}
}


@Composable
private fun SelectedEncounterMonsterSummaryContent(
	summaries: List<EncounterSelectedMonsterSummary>,
	remainingCount: Int,
	onDecrementSummary: (String) -> Unit,
	onRemoveSummary: (String) -> Unit
) {
	if (summaries.isEmpty()) {
		Text(
			text = stringResource(R.string.encounter_no_monsters_selected),
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			fontSize = encounterHelperTextSize
		)
		return
	}

	LazyRow(horizontalArrangement = LayoutArrangement.spacedBy(encounterChipSpacing)) {
		summaries.forEach { summary ->
			item(key = summary.key) {
				SelectedEncounterMonsterSummaryChipRow(
					summary = summary,
					onDecrementSummary = onDecrementSummary,
					onRemoveSummary = onRemoveSummary
				)
			}
		}
		if (remainingCount > 0) {
			item(key = ENCOUNTER_SELECTED_SUMMARY_OVERFLOW_KEY) {
				AssistChip(
					onClick = { },
					label = { Text(stringResource(R.string.encounter_selected_monsters_more, remainingCount)) }
				)
			}
		}
	}
}

@Composable
private fun SelectedEncounterMonsterSummaryChipRow(
	summary: EncounterSelectedMonsterSummary,
	onDecrementSummary: (String) -> Unit,
	onRemoveSummary: (String) -> Unit
) {
	val decrementLabel = decrementSelectedEncounterMonsterLabel(summary.name)
	val removeLabel = removeSelectedEncounterMonsterLabel(summary.name)
	Row(horizontalArrangement = LayoutArrangement.spacedBy(encounterChipGroupSpacing)) {
		InputChip(
			selected = true,
			onClick = { onDecrementSummary(summary.key) },
			modifier = Modifier.semantics {
				contentDescription = decrementLabel
			},
			label = {
				Text(
					selectedEncounterMonsterChipLabel(summary),
					maxLines = 1
				)
			}
		)
		AssistChip(
			onClick = { onRemoveSummary(summary.key) },
			modifier = Modifier.semantics {
				contentDescription = removeLabel
			},
			label = { Text(stringResource(R.string.encounter_remove_all_button)) },
			leadingIcon = {
				Icon(
					imageVector = Icons.Default.Close,
					contentDescription = null
				)
			}
		)
	}
}

@Composable
private fun EncounterMonsterPickerRow(
	modifier: Modifier = Modifier,
	monster: MonsterReferenceEntry,
	count: Int,
	onCountChange: (Int) -> Unit
) {
	Card(
		modifier = modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(encounterSectionCardPadding),
			verticalArrangement = LayoutArrangement.spacedBy(encounterChipGroupSpacing)
		) {
			Text(
				text = monster.name,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			Text(
				text = monster.monsterCardSubtitleChallengeRatingLine(),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.secondary
			)
			monster.monsterCardSummaryLine(
				MONSTER_CARD_SUMMARY_STAT_LABELS_NO_CR
			).takeIf { it.isNotBlank() }?.let { summary ->
				Text(
					text = summary,
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = LayoutArrangement.End
			) {
				EncounterMonsterCountControls(
					monsterName = monster.name,
					count = count,
					onCountChange = onCountChange
				)
			}
		}
	}
}

@Composable
private fun EncounterMonsterCountControls(
	monsterName: String,
	count: Int,
	onCountChange: (Int) -> Unit
) {
	val decrementLabel = decrementSelectedEncounterMonsterLabel(monsterName)
	val incrementLabel = stringResource(R.string.encounter_increment_selected_monster, monsterName)
	if (count > 0) {
		IconButton(
			onClick = { onCountChange(-1) },
			modifier = Modifier.semantics {
				contentDescription = decrementLabel
			}
		) {
			Text("-")
		}
		Text(
			text = count.toString(),
			style = MaterialTheme.typography.titleSmall,
			modifier = Modifier.padding(
				horizontal = encounterSelectedCountHorizontalPadding,
				vertical = encounterSelectedCountVerticalPadding
			)
		)
		IconButton(
			onClick = { onCountChange(1) },
			modifier = Modifier.semantics {
				contentDescription = incrementLabel
			}
		) {
			Text("+")
		}
	} else {
		TextButton(onClick = { onCountChange(1) }) {
			Text(text = stringResource(R.string.encounter_add_monster_button))
		}
	}
}

internal fun filteredEncounterMonsterEntries(
	query: String,
	animalsOnly: Boolean = false,
	creatureType: String? = null,
	challengeRating: String? = null,
	selectedMonsterCounts: Map<String, Int> = emptyMap(),
	selectedOnly: Boolean = false,
	sortMode: EncounterMonsterSortMode = EncounterMonsterSortMode.NAME
): List<MonsterReferenceEntry> {
	val filteredMonsters = MonsterReferenceCatalog.filter(
		query = query,
		group = encounterMonsterGroupFilter(animalsOnly),
		creatureType = creatureType,
		challengeRating = challengeRating
	)
	val visibleMonsters = filterSelectedEncounterMonsterEntries(
		monsters = filteredMonsters,
		selectedMonsterCounts = selectedMonsterCounts,
		selectedOnly = selectedOnly
	)
	return sortEncounterMonsterEntries(visibleMonsters, sortMode)
}

private fun encounterMonsterGroupFilter(animalsOnly: Boolean): String? {
	return MonsterReferenceCatalog.ANIMAL_GROUP.takeIf { animalsOnly }
}

private fun filterSelectedEncounterMonsterEntries(
	monsters: List<MonsterReferenceEntry>,
	selectedMonsterCounts: Map<String, Int>,
	selectedOnly: Boolean
): List<MonsterReferenceEntry> {
	if (!selectedOnly) return monsters
	return monsters.filter { monster -> encounterMonsterKey(monster) in selectedMonsterCounts }
}

private fun sortEncounterMonsterEntries(
	monsters: List<MonsterReferenceEntry>,
	sortMode: EncounterMonsterSortMode
): List<MonsterReferenceEntry> {
	return when (sortMode) {
		EncounterMonsterSortMode.NAME -> monsters.sortedBy(MonsterReferenceEntry::name)
		EncounterMonsterSortMode.CHALLENGE_RATING -> monsters.sortedWith(encounterMonsterChallengeRatingComparator())
	}
}

private fun encounterMonsterChallengeRatingComparator(): Comparator<MonsterReferenceEntry> {
	return compareByDescending<MonsterReferenceEntry> { parseMonsterChallengeRatingValue(it.challengeRating) }
		.thenBy(MonsterReferenceEntry::name)
}

private fun selectedEncounterMonsterCount(
	selectedMonsterCounts: Map<String, Int>,
	monsterKey: String
): Int {
	return selectedMonsterCounts[monsterKey] ?: 0
}

internal fun encounterMonsterKey(monster: MonsterReferenceEntry): String {
	return ReferenceDetailResolver.slugFor(monster.name)
}

internal fun updateEncounterMonsterSelection(
	selectedMonsterCounts: Map<String, Int>,
	monsterKey: String,
	delta: Int
): Map<String, Int> {
	val current = selectedMonsterCounts[monsterKey] ?: 0
	val updated = (current + delta).coerceAtLeast(0)
	return selectedMonsterCounts.toMutableMap().apply {
		if (updated == 0) remove(monsterKey) else this[monsterKey] = updated
	}.toMap()
}

internal fun removeEncounterMonsterSelection(
	selectedMonsterCounts: Map<String, Int>,
	monsterKey: String
): Map<String, Int> {
	return selectedMonsterCounts.toMutableMap().apply {
		remove(monsterKey)
	}.toMap()
}

internal fun decrementEncounterMonsterSelection(
	selectedMonsterCounts: Map<String, Int>,
	monsterKey: String
): Map<String, Int> {
	return updateEncounterMonsterSelection(
		selectedMonsterCounts = selectedMonsterCounts,
		monsterKey = monsterKey,
		delta = -1
	)
}

internal fun buildEncounterMonsterRemoteItems(selectedMonsterCounts: Map<String, Int>): List<RemoteItem> {
	return selectedMonsterCounts.entries
		.sortedBy { it.key }
		.flatMap { (monsterKey, count) ->
			val monster = MonsterReferenceCatalog.findEntry(monsterKey) ?: return@flatMap emptyList()
			List(count) { monster.toEncounterRemoteItem() }
		}
}

private fun MonsterReferenceEntry.toEncounterRemoteItem(): RemoteItem {
	return RemoteItem(
		id = encounterMonsterKey(this),
		name = name,
		category = ENCOUNTER_REMOTE_ITEM_CATEGORY,
		detail = creatureType.takeIf { it.isNotBlank() } ?: subtitle,
		fullDescription = body
	)
}

internal fun selectedEncounterMonsterSummaries(selectedMonsterCounts: Map<String, Int>): List<EncounterSelectedMonsterSummary> {
	return selectedMonsterCounts.entries
		.mapNotNull { (monsterKey, count) ->
			val monster = MonsterReferenceCatalog.findEntry(monsterKey) ?: return@mapNotNull null
			EncounterSelectedMonsterSummary(
				key = monsterKey,
				name = monster.name,
				count = count,
				challengeRating = monster.challengeRating
			)
		}
		.sortedBy(EncounterSelectedMonsterSummary::name)
}

internal fun compactSelectedEncounterMonsterSummary(
	summaries: List<EncounterSelectedMonsterSummary>,
	visibleItemCount: Int = 3
): Pair<List<EncounterSelectedMonsterSummary>, Int> {
	val visible = summaries.take(visibleItemCount)
	return visible to (summaries.size - visible.size).coerceAtLeast(0)
}

internal fun selectedEncounterMonsterChipLabel(summary: EncounterSelectedMonsterSummary): String {
	val crSuffix = summary.challengeRating.takeIf { it.isNotBlank() }?.let { " • $ENCOUNTER_CR_LABEL_PREFIX$it" }.orEmpty()
	return "${summary.name} ×${summary.count}$crSuffix"
}

@Composable
private fun rememberEncounterPickerCountHeaderLabel(selectedCount: Int, filteredCount: Int): String {
	val selectedLabel = pluralStringResource(
		R.plurals.encounter_selected_monsters_quantity,
		selectedCount,
		selectedCount
	)
	val matchingLabel = pluralStringResource(
		R.plurals.encounter_matching_monsters_quantity,
		filteredCount,
		filteredCount
	)
	return stringResource(R.string.encounter_picker_count_header, selectedLabel, matchingLabel)
}

@Composable
private fun decrementSelectedEncounterMonsterLabel(monsterName: String): String {
	return stringResource(R.string.encounter_decrement_selected_monster, monsterName)
}

@Composable
private fun removeSelectedEncounterMonsterLabel(monsterName: String): String {
	return stringResource(R.string.encounter_remove_selected_monster, monsterName)
}

internal fun encounterPickerFocusTarget(hasSelectedMonsters: Boolean): EncounterPickerFocusTarget {
	return if (hasSelectedMonsters) {
		EncounterPickerFocusTarget.SELECTED_SUMMARY
	} else {
		EncounterPickerFocusTarget.PICKER_HEADER
	}
}

internal fun hasActiveEncounterMonsterFilters(
	query: String,
	animalsOnly: Boolean,
	creatureType: String?,
	challengeRating: String?,
	selectedOnly: Boolean,
	sortMode: EncounterMonsterSortMode
): Boolean {
	return query.isNotBlank() ||
		animalsOnly ||
		!creatureType.isNullOrBlank() ||
		!challengeRating.isNullOrBlank() ||
		selectedOnly ||
		sortMode != EncounterMonsterSortMode.NAME
}

