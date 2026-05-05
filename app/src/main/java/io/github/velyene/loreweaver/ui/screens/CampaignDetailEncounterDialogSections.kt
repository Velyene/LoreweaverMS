/*
 * FILE: CampaignDetailEncounterDialogSections.kt
 *
 * TABLE OF CONTENTS:
 * 1. Encounter Dialog Shared Models
 * 2. Encounter Form Sections
 * 3. Monster Selection Sections
 * 4. Reward Template Sections
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.focusable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.util.MonsterReferenceCatalog
import io.github.velyene.loreweaver.domain.util.MonsterReferenceEntry
import androidx.compose.foundation.layout.Arrangement as LayoutArrangement

@Composable
internal fun EncounterMonsterPickerSection(
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
						.padding(top = encounterPickerTopPadding),
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
internal fun MonsterEncounterFilterSection(
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
internal fun SelectedEncounterMonsterSummarySection(
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
					label = {
						Text(
							stringResource(
								R.string.encounter_selected_monsters_more,
								remainingCount
							)
						)
					}
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

