/*
 * FILE: CharacterDetailScreen.kt
 *
 * TABLE OF CONTENTS:
 * 1. Main Screen (CharacterDetailScreen)
 *    a. State & Storage Initialization
 *    b. Update Handlers (HP, Mana, Stamina)
 *    c. Tab Navigation and Content Routing
 * 2. Roll Result Presentation
 * 3. Shared UI Components
 *    a. StatDisplayRow
 *    b. DeathSaveRow
 * 4. Pure Update Helper
 *    a. applyStatDelta
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal
import io.github.velyene.loreweaver.ui.theme.MutedText
import io.github.velyene.loreweaver.ui.viewmodels.CharacterViewModel

// -----------------------------------------------------------------------------
// -----------------------------------------------------------------------------
// -----------------------------------------------------------------------------

internal const val STAT_TYPE_HP = "HP"
internal const val STAT_TYPE_MANA = "Mana"
internal const val STAT_TYPE_STAMINA = "Stamina"
internal const val STAT_TYPE_TEMP_HP = "TempHP"

private fun applyStatDelta(
	character: CharacterEntry,
	delta: Int,
	statType: String
): CharacterEntry =
	when (statType) {
		STAT_TYPE_HP -> if (delta < 0) character.applyDamage(-delta) else character.applyHealing(delta)
		STAT_TYPE_MANA -> character.copy(mana = (character.mana + delta).coerceIn(0, character.maxMana))
		STAT_TYPE_STAMINA -> character.copy(
			stamina = (character.stamina + delta).coerceIn(
				0,
				character.maxStamina
			)
		)

		STAT_TYPE_TEMP_HP -> character.copy(tempHp = (character.tempHp + delta).coerceAtLeast(0))
		else -> character
	}


// -----------------------------------------------------------------------------
// 1. Main Screen
// -----------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
	characterId: String?,
	onEdit: (String) -> Unit,
	onLookupCondition: (String) -> Unit,
	onDelete: () -> Unit,
	onBack: () -> Unit,
	viewModel: CharacterViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	LaunchedEffect(characterId) {
		characterId?.let { viewModel.selectCharacter(it) }
	}

	val character = uiState.selectedCharacter
	var selectedTab by rememberSaveable { mutableIntStateOf(0) }
	var pendingDeleteCharacter by remember { mutableStateOf<CharacterEntry?>(null) }
	val tabs = listOf(
		stringResource(R.string.tab_combat),
		stringResource(R.string.tab_stats),
		stringResource(R.string.tab_journal)
	)
	// Keep stat mutation and optional HP logging together so every tab uses the same rules for
	// applying deltas and the combat log cannot drift from the displayed HP changes.
	val onUpdateStat: (Int, String) -> Unit = { delta, statType ->
		character?.let { current ->
			viewModel.updateCharacter(applyStatDelta(current, delta, statType))
			if (statType == STAT_TYPE_HP) {
				val msg = if (delta < 0) "takes ${-delta} damage" else "heals for $delta HP"
				viewModel.logAction("${current.name} $msg", statType)
			}
		}
	}

	val haptic = LocalHapticFeedback.current
	var rollResult by remember { mutableStateOf<Pair<String, Int>?>(null) }
	var situationalBonus by rememberSaveable { mutableStateOf("0") }

	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Text(character?.name ?: stringResource(R.string.character_details_title))
				},
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = stringResource(R.string.back_to_characters)
						)
					}
				},
				actions = {
					CharacterDetailActions(
						character = character,
						onEdit = onEdit,
						onDeleteRequest = { pendingDeleteCharacter = it }
					)
				}
			)
		}
	) { padding ->
		CharacterDetailContent(
			character = character,
			state = CharacterDetailState(
				selectedTab = selectedTab,
				onTabSelected = { selectedTab = it },
				tabs = tabs,
				onUpdateStat = onUpdateStat,
				haptic = haptic,
				situationalBonus = situationalBonus,
				onBonusChange = { situationalBonus = it },
				rollResult = rollResult,
				onRollResult = { rollResult = it }
			),
			viewModel = viewModel,
			onLookupCondition = onLookupCondition,
			padding = padding
		)
	}

	pendingDeleteCharacter?.let { characterToDelete ->
		ConfirmationDialog(
			title = stringResource(R.string.confirm_delete_character_title),
			message = stringResource(
				R.string.confirm_delete_character_message,
				characterToDelete.name
			),
			confirmLabel = stringResource(R.string.delete_button),
			onConfirm = {
				viewModel.deleteCharacter(characterToDelete)
				onDelete()
				@Suppress("UNUSED_VALUE")
				pendingDeleteCharacter = null
			},
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				pendingDeleteCharacter = null
			}
		)
	}
}

// -----------------------------------------------------------------------------
// State bundle for CharacterDetailContent
// -----------------------------------------------------------------------------

private data class CharacterDetailState(
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
private fun CharacterDetailActions(
	character: CharacterEntry?,
	onEdit: (String) -> Unit,
	onDeleteRequest: (CharacterEntry) -> Unit
) {
	if (character != null) {
		IconButton(onClick = { onEdit(character.id) }) {
			Icon(
				Icons.Default.Edit,
				contentDescription = stringResource(R.string.edit_character, character.name)
			)
		}
		IconButton(onClick = { onDeleteRequest(character) }) {
			Icon(
				Icons.Default.Delete,
				contentDescription = stringResource(R.string.delete_character, character.name)
			)
		}
	}
}

@Composable
private fun CharacterDetailContent(
	character: CharacterEntry?,
	state: CharacterDetailState,
	viewModel: CharacterViewModel,
	onLookupCondition: (String) -> Unit,
	padding: androidx.compose.foundation.layout.PaddingValues
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
						character,
						viewModel,
						state.onUpdateStat,
						state.haptic,
						state.situationalBonus,
						state.onRollResult
					)

					1 -> CharacterDetailAttributesAndSkills(
						character,
						state.situationalBonus,
						state.onBonusChange,
						state.onRollResult
					)

					2 -> JournalTab(character, viewModel, onLookupCondition)
				}
				if (state.rollResult != null) {
					// Roll results live outside the tab switch so checks made in any tab remain visible until
					// dismissed, even if the user immediately changes sections.
					RollResultCard(
						result = state.rollResult,
						onClear = { state.onRollResult(null) })
				}
				Spacer(modifier = Modifier.height(80.dp))
			}
		}
	}
}

// -----------------------------------------------------------------------------
// Roll Result Card (extracted from inline if-block)
// -----------------------------------------------------------------------------

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
			Button(onClick = onClear) { Text(stringResource(R.string.clear_button)) }
		}
	}
}

// -----------------------------------------------------------------------------
// -----------------------------------------------------------------------------
// -----------------------------------------------------------------------------


// -----------------------------------------------------------------------------
// 6. UI Components
// -----------------------------------------------------------------------------

@Composable
fun StatDisplayRow(label: String, value: Int, max: Int, onUpdate: (Int) -> Unit) {
	Column(modifier = Modifier.padding(vertical = 4.dp)) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(16.dp)
		) {
			Text(
				text = stringResource(R.string.stat_value_with_max, label, value, max),
				fontSize = 18.sp,
				modifier = Modifier.weight(1f)
			)
			Row(verticalAlignment = Alignment.CenterVertically) {
				TextButton(onClick = { onUpdate(-5) }) {
					Text("-5", color = MaterialTheme.colorScheme.error)
				}
				IconButton(onClick = { onUpdate(-1) }) {
					Icon(
						Icons.Default.Remove,
						contentDescription = stringResource(R.string.decrease_stat_desc, label),
						tint = MaterialTheme.colorScheme.error
					)
				}
				IconButton(onClick = { onUpdate(1) }) {
					Icon(
						Icons.Default.Add,
						contentDescription = stringResource(R.string.increase_stat_desc, label),
						tint = MaterialTheme.colorScheme.primary
					)
				}
				TextButton(onClick = { onUpdate(5) }) {
					Text("+5", color = MaterialTheme.colorScheme.primary)
				}
			}
		}
		val barColor = when (label) {
			stringResource(R.string.hp_label) -> MaterialTheme.colorScheme.error
			stringResource(R.string.mana_label) -> MaterialTheme.colorScheme.primary
			stringResource(R.string.stamina_label) -> MaterialTheme.colorScheme.secondary
			else -> MaterialTheme.colorScheme.primary
		}
		LinearProgressIndicator(
			progress = { if (max > 0) value.toFloat() / max.toFloat() else 0f },
			modifier = Modifier
				.fillMaxWidth()
				.height(8.dp),
			strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
			color = barColor,
			trackColor = barColor.copy(alpha = 0.2f)
		)
	}
}

@Composable
fun DeathSaveRow(successes: Int, failures: Int, onUpdate: (Boolean, Int) -> Unit) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 8.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			Text(
				stringResource(R.string.death_saves_label),
				fontWeight = FontWeight.Bold,
				color = MaterialTheme.colorScheme.onErrorContainer
			)
			Spacer(modifier = Modifier.height(8.dp))
			DeathSaveCheckboxRow(
				label = stringResource(R.string.successes_label),
				count = successes,
				checkedColor = MaterialTheme.colorScheme.primary,
				onUpdate = { checked -> if (checked) onUpdate(true, 1) else onUpdate(true, -1) }
			)
			DeathSaveCheckboxRow(
				label = stringResource(R.string.failures_label),
				count = failures,
				checkedColor = MaterialTheme.colorScheme.error,
				onUpdate = { checked -> if (checked) onUpdate(false, 1) else onUpdate(false, -1) }
			)
		}
	}
}

@Composable
private fun DeathSaveCheckboxRow(
	label: String,
	count: Int,
	checkedColor: Color,
	onUpdate: (Boolean) -> Unit
) {
	Row(verticalAlignment = Alignment.CenterVertically) {
		Text(label, modifier = Modifier.weight(1f))
		repeat(3) { index ->
			Checkbox(
				checked = index < count,
				onCheckedChange = onUpdate,
				colors = CheckboxDefaults.colors(checkedColor = checkedColor)
			)
		}
	}
}
