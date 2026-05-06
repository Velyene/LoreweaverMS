/*
 * FILE: CharacterDetailContent.kt
 *
 * TABLE OF CONTENTS:
 * 1. Character Detail Content State
 * 2. Character Detail Content Layout
 * 3. Character Detail Action Bar
 * 4. Tab and Scroll Helpers
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal
import io.github.velyene.loreweaver.ui.theme.MutedText
import io.github.velyene.loreweaver.ui.viewmodels.CharacterViewModel

internal const val CHARACTER_DETAIL_EDIT_ACTION_TAG = "character_detail_edit_action"
internal const val CHARACTER_DETAIL_DELETE_ACTION_TAG = "character_detail_delete_action"

internal data class CharacterDetailState(
	val selectedTab: Int,
	val onTabSelected: (Int) -> Unit,
	val tabs: List<String>,
	val onUpdateStat: (Int, String) -> Unit,
	val haptic: HapticFeedback,
	val situationalBonus: String,
	val onBonusChange: (String) -> Unit,
	val rollResult: Pair<String, Int>?,
	val onRollResult: (Pair<String, Int>?) -> Unit
)

@Composable
internal fun CharacterDetailActions(
	character: CharacterEntry?,
	onEdit: (String) -> Unit,
	onDeleteRequest: (CharacterEntry) -> Unit
) {
	if (character != null) {
		IconButton(
			onClick = { onEdit(character.id) },
			modifier = Modifier.testTag(CHARACTER_DETAIL_EDIT_ACTION_TAG),
		) {
			Icon(
				Icons.Default.Edit,
				contentDescription = stringResource(R.string.edit_character, character.name)
			)
		}
		IconButton(
			onClick = { onDeleteRequest(character) },
			modifier = Modifier.testTag(CHARACTER_DETAIL_DELETE_ACTION_TAG),
		) {
			Icon(
				Icons.Default.Delete,
				contentDescription = stringResource(R.string.delete_character, character.name)
			)
		}
	}
}

@Composable
internal fun CharacterDetailContent(
	character: CharacterEntry?,
	state: CharacterDetailState,
	viewModel: CharacterViewModel,
	onLookupCondition: (String) -> Unit,
	padding: PaddingValues
) {
	if (character == null) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding),
			contentAlignment = Alignment.Center
		) {
			Text(stringResource(R.string.character_not_found))
		}
	} else {
		val scrollState = rememberScrollState()

		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
		) {
			PrimaryTabRow(selectedTabIndex = state.selectedTab) {
				state.tabs.forEachIndexed { index, title ->
					Tab(
						selected = state.selectedTab == index,
						onClick = { state.onTabSelected(index) },
						text = { Text(title) }
					)
				}
			}
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.weight(1f)
					.padding(16.dp)
					.verticalScroll(scrollState)
					.visibleVerticalScrollbar(scrollState)
			) {
				when (state.selectedTab) {
					0 -> CombatTab(
						character = character,
						viewModel = viewModel,
						onUpdateStat = state.onUpdateStat,
						haptic = state.haptic,
						situationalBonus = state.situationalBonus,
						onRollResult = state.onRollResult
					)

					1 -> StatsTab(
						character = character,
						situationalBonus = state.situationalBonus,
						onBonusChange = state.onBonusChange,
						onRollResult = state.onRollResult
					)

					2 -> JournalTab(
						character = character,
						viewModel = viewModel,
						onLookupCondition = onLookupCondition
					)
				}
				state.rollResult?.let { result ->
					RollResultCard(
						result = result,
						onClear = { state.onRollResult(null) }
					)
				}
				Spacer(modifier = Modifier.height(80.dp))
			}
		}
	}
}

@Composable
private fun RollResultCard(result: Pair<String, Int>, onClear: () -> Unit) {
	val label = result.first
	val value = result.second
	val contentDesc = stringResource(R.string.roll_result_desc, label, value)
	Spacer(modifier = Modifier.height(16.dp))
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.semantics {
				liveRegion = LiveRegionMode.Polite
				contentDescription = contentDesc
			},
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(label, style = MaterialTheme.typography.labelLarge, color = MutedText)
			Text(
				"$value",
				style = MaterialTheme.typography.displayMedium,
				fontWeight = FontWeight.Bold,
				color = ArcaneTeal
			)
			Button(onClick = onClear) {
				Text(stringResource(R.string.clear_button))
			}
		}
	}
}
