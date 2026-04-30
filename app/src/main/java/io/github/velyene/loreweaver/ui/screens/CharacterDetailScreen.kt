/*
 * FILE: CharacterDetailScreen.kt
 *
 * TABLE OF CONTENTS:
 * 1. Character detail screen entry point
 *    a. State and storage initialization
 *    b. Update handlers for HP, mana, and stamina
 *    c. Tab navigation and content routing
 * 2. Roll result presentation
 * 3. Shared UI components
 *    a. StatDisplayRow
 *    b. DeathSaveRow
 * 4. Pure update support
 *    a. applyStatDelta
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
// 1. Character detail screen entry point
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
			isLoading = uiState.isLoading,
			error = uiState.error,
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
				viewModel.deleteCharacter(characterToDelete, onSuccess = onDelete)
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
	isLoading: Boolean,
	error: String?,
	state: CharacterDetailState,
	viewModel: CharacterViewModel,
	onLookupCondition: (String) -> Unit,
	padding: androidx.compose.foundation.layout.PaddingValues
) {
	if (isLoading && character == null) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding),
			contentAlignment = Alignment.Center
		) {
			Text(stringResource(R.string.loading_label), color = MutedText)
		}
		return
	}

	if (character == null) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding),
			contentAlignment = Alignment.Center
		) {
			Text(error ?: stringResource(R.string.character_not_found))
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
			CharacterIdentityBanner(character)
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

					2 -> JournalTab(
						character = character,
						viewModel = viewModel,
						onLookupCondition = onLookupCondition
					)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CombatTab(
	character: CharacterEntry,
	viewModel: CharacterViewModel,
	onUpdateStat: (Int, String) -> Unit,
	haptic: HapticFeedback,
	situationalBonus: String,
	onRollResult: (Pair<String, Int>?) -> Unit
) {
	var selectedSectionName by rememberSaveable { mutableStateOf(ActionHubSection.ACTIONS.name) }
	var selectedEntryId by rememberSaveable { mutableStateOf<String?>(null) }
	val selectedSection = remember(selectedSectionName) { ActionHubSection.valueOf(selectedSectionName) }
	val actionHubEntries = remember(character, selectedSection) {
		buildActionHubEntries(character, selectedSection)
	}
	val selectedEntry = actionHubEntries.firstOrNull { it.id == selectedEntryId }
		?: actionHubEntries.firstOrNull()

	LaunchedEffect(actionHubEntries, selectedEntry?.id) {
		selectedEntryId = selectedEntry?.id
	}

	ActionViewHeaderCard(character)
	Spacer(modifier = Modifier.height(16.dp))
	QuickStatsBar(character, viewModel)
	Spacer(modifier = Modifier.height(16.dp))

	StatDisplayRow(
		label = stringResource(R.string.hp_label),
		value = character.hp,
		max = character.maxHp,
		onUpdate = {
			haptic.performHapticFeedback(HapticFeedbackType.LongPress); onUpdateStat(
			it,
			STAT_TYPE_HP
		)
		}
	)

	if (character.tempHp > 0 || character.hp > 0) {
		StatDisplayRow(
			label = stringResource(R.string.temp_hp_label),
			value = character.tempHp,
			max = character.maxHp,
			onUpdate = { onUpdateStat(it, STAT_TYPE_TEMP_HP) }
		)
	}

	if (character.hp == 0 && character.party == CharacterParty.ADVENTURERS) {
		DeathSaveRow(
			successes = character.deathSaveSuccesses,
			failures = character.deathSaveFailures,
			onUpdate = { success, delta ->
				haptic.performHapticFeedback(HapticFeedbackType.LongPress)
				val updated = if (success) {
					character.copy(
						deathSaveSuccesses = (character.deathSaveSuccesses + delta).coerceIn(
							0,
							3
						)
					)
				} else {
					character.copy(
						deathSaveFailures = (character.deathSaveFailures + delta).coerceIn(
							0,
							3
						)
					)
				}
				viewModel.updateCharacter(updated)
			}
		)
	}

	Spacer(modifier = Modifier.height(16.dp))
	HitDiceAndRestingSection(character, viewModel, onRollResult)
	Spacer(modifier = Modifier.height(16.dp))
	ActionHubSectionCard(
		character = character,
		viewModel = viewModel,
		selectedSection = selectedSection,
		onSectionSelected = { selectedSectionName = it.name },
		entries = actionHubEntries,
		selectedEntryId = selectedEntry?.id,
		onEntrySelected = { selectedEntryId = it },
		situationalBonus = situationalBonus,
		onRollResult = onRollResult
	)
	Spacer(modifier = Modifier.height(16.dp))
	ActionDetailPanel(entry = selectedEntry)
	Spacer(modifier = Modifier.height(16.dp))
	DiceTraySection(situationalBonus, onRollResult)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActionViewHeaderCard(character: CharacterEntry) {
	val identitySummary = character.formattedIdentity()
	val conditionSections = remember(character.activeConditions, character.persistentConditions) {
		buildCharacterConditionSections(character)
	}
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
		border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.25f)),
		shape = RoundedCornerShape(20.dp)
	) {
		Column(
			modifier = Modifier.padding(18.dp),
			verticalArrangement = Arrangement.spacedBy(14.dp)
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.Top
			) {
				Column(modifier = Modifier.weight(1f)) {
					Text(
						text = character.name,
						style = MaterialTheme.typography.headlineMedium,
						fontWeight = FontWeight.Bold
					)
					Text(
						text = character.type,
						style = MaterialTheme.typography.titleSmall,
						color = AntiqueGold
					)
					if (identitySummary.isNotBlank()) {
						Text(
							text = identitySummary,
							style = MaterialTheme.typography.bodySmall,
							color = MutedText
						)
					}
				}
				Surface(
					shape = RoundedCornerShape(999.dp),
					color = ArcaneTeal.copy(alpha = 0.14f),
					border = BorderStroke(1.dp, ArcaneTeal.copy(alpha = 0.45f))
				) {
					Text(
						text = stringResource(R.string.action_view_level_label, character.level),
						style = MaterialTheme.typography.labelLarge,
						fontWeight = FontWeight.Bold,
						color = ArcaneTeal,
						modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
					)
				}
			}

			ActionViewResourceBar(
				label = stringResource(R.string.hp_label),
				current = character.hp,
				max = character.maxHp,
				barColor = MaterialTheme.colorScheme.error
			)

			if (character.maxMana > 0) {
				ActionViewResourceBar(
					label = stringResource(R.string.mana_label),
					current = character.mana,
					max = character.maxMana,
					barColor = MaterialTheme.colorScheme.primary
				)
			}

			if (character.maxStamina > 0) {
				ActionViewResourceBar(
					label = stringResource(R.string.stamina_label),
					current = character.stamina,
					max = character.maxStamina,
					barColor = MaterialTheme.colorScheme.secondary
				)
			}

			character.resources.filter { it.max > 0 }.take(2).forEach { resource ->
				ActionViewResourceBar(
					label = resource.name,
					current = resource.current,
					max = resource.max,
					barColor = MaterialTheme.colorScheme.tertiary
				)
			}

			Text(
				text = stringResource(R.string.conditions_label),
				style = MaterialTheme.typography.labelMedium,
				fontWeight = FontWeight.Bold,
				color = AntiqueGold
			)
			if (conditionSections.encounter.isEmpty() && conditionSections.persistent.isEmpty()) {
				Text(
					text = stringResource(R.string.action_view_no_conditions),
					style = MaterialTheme.typography.bodyMedium,
					color = MutedText
				)
			} else {
				CharacterStatusChipGroup(
					title = stringResource(R.string.encounter_conditions_title),
					statuses = conditionSections.encounter
				)
				CharacterStatusChipGroup(
					title = stringResource(R.string.persistent_conditions_title),
					statuses = conditionSections.persistent
				)
			}
		}
	}
}

@Composable
private fun CharacterStatusChipGroup(title: String, statuses: List<StatusChipModel>) {
	if (statuses.isEmpty()) return

	Text(
		text = title,
		style = MaterialTheme.typography.labelMedium,
		fontWeight = FontWeight.Bold,
		color = AntiqueGold
	)
	StatusChipFlowRow(statuses = statuses)
}

@Composable
private fun ActionViewResourceBar(
	label: String,
	current: Int,
	max: Int,
	barColor: Color,
) {
	Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(label, style = MaterialTheme.typography.labelSmall, color = AntiqueGold)
			Text(
				text = stringResource(R.string.action_view_resource_value, current, max),
				style = MaterialTheme.typography.labelMedium,
				color = MutedText
			)
		}
		LinearProgressIndicator(
			progress = { if (max > 0) current.toFloat() / max.toFloat() else 0f },
			modifier = Modifier
				.fillMaxWidth()
				.height(10.dp),
			color = barColor,
			trackColor = barColor.copy(alpha = 0.2f)
		)
	}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActionHubSectionCard(
	character: CharacterEntry,
	viewModel: CharacterViewModel,
	selectedSection: ActionHubSection,
	onSectionSelected: (ActionHubSection) -> Unit,
	entries: List<ActionHubEntry>,
	selectedEntryId: String?,
	onEntrySelected: (String) -> Unit,
	situationalBonus: String,
	onRollResult: (Pair<String, Int>?) -> Unit,
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
		shape = RoundedCornerShape(20.dp)
	) {
		Column(
			modifier = Modifier.padding(18.dp),
			verticalArrangement = Arrangement.spacedBy(14.dp)
		) {
			Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
				Text(
					text = stringResource(R.string.action_view_title),
					style = MaterialTheme.typography.headlineSmall,
					fontWeight = FontWeight.Bold
				)
				Text(
					text = character.type,
					style = MaterialTheme.typography.bodySmall,
					color = MutedText
				)
			}
			FlowRow(
				horizontalArrangement = Arrangement.spacedBy(8.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				ActionHubSection.entries.forEach { section ->
					val isSelected = section == selectedSection
					FilterChip(
						selected = isSelected,
						onClick = { onSectionSelected(section) },
						leadingIcon = {
							Icon(
								imageVector = when (section) {
									ActionHubSection.ACTIONS -> Icons.Default.FlashOn
									ActionHubSection.SPELLS -> Icons.Default.AutoAwesome
									ActionHubSection.ITEMS -> Icons.Default.Inventory2
								},
								contentDescription = null,
								modifier = Modifier.size(18.dp)
							)
						},
						border = BorderStroke(
							1.dp,
							if (isSelected) ArcaneTeal else MaterialTheme.colorScheme.outlineVariant
						),
						colors = FilterChipDefaults.filterChipColors(
							selectedContainerColor = ArcaneTeal.copy(alpha = 0.16f),
							selectedLabelColor = MaterialTheme.colorScheme.onSurface,
							selectedLeadingIconColor = ArcaneTeal
						),
						label = {
							Text(
								stringResource(
									when (section) {
										ActionHubSection.ACTIONS -> R.string.action_view_tab_actions
										ActionHubSection.SPELLS -> R.string.action_view_tab_spells
										ActionHubSection.ITEMS -> R.string.action_view_tab_items
									}
								)
							)
						}
					)
				}
			}

			if (entries.isEmpty()) {
				Text(
					text = stringResource(
						when (selectedSection) {
							ActionHubSection.ACTIONS -> R.string.action_view_empty_actions
							ActionHubSection.SPELLS -> R.string.action_view_empty_spells
							ActionHubSection.ITEMS -> R.string.action_view_empty_items
						}
					),
					style = MaterialTheme.typography.bodyMedium,
					color = MutedText
				)
			} else {
				entries.forEach { entry ->
					ActionHubEntryCard(
						entry = entry,
						isSelected = entry.id == selectedEntryId,
						onSelect = { onEntrySelected(entry.id) },
						character = character,
						viewModel = viewModel,
						situationalBonus = situationalBonus,
						onRollResult = onRollResult,
					)
				}
			}
		}
	}
}

@Composable
private fun ActionHubEntryCard(
	entry: ActionHubEntry,
	isSelected: Boolean,
	onSelect: () -> Unit,
	character: CharacterEntry,
	viewModel: CharacterViewModel,
	situationalBonus: String,
	onRollResult: (Pair<String, Int>?) -> Unit,
) {
	val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
	else MaterialTheme.colorScheme.surface
	val borderColor = if (isSelected) ArcaneTeal else MaterialTheme.colorScheme.outlineVariant
	val featuredTitleColor = if (isSelected) ArcaneTeal else MaterialTheme.colorScheme.onSurface
	val action = entry.actionIndex?.let(character.actions::getOrNull)
	val spellSlots = entry.spellLevel?.let(character.spellSlots::get)

	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onSelect),
		colors = CardDefaults.cardColors(containerColor = containerColor),
		border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
		shape = RoundedCornerShape(16.dp)
	) {
		Row(
			modifier = Modifier.padding(14.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Surface(
				shape = RoundedCornerShape(12.dp),
				color = if (isSelected) ArcaneTeal.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant,
				modifier = Modifier.size(width = 10.dp, height = 88.dp)
			) {}
			Column(
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(10.dp)
			) {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.Top
				) {
					Column(
						modifier = Modifier.weight(1f),
						verticalArrangement = Arrangement.spacedBy(4.dp)
					) {
						Text(
							entry.title,
							style = if (isSelected) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleSmall,
							fontWeight = FontWeight.Bold,
							color = featuredTitleColor
						)
						Text(
							entry.summary,
							style = MaterialTheme.typography.labelMedium,
							color = MutedText
						)
					}
					if (isSelected) {
						Icon(
							Icons.Default.RadioButtonChecked,
							contentDescription = null,
							tint = ArcaneTeal,
							modifier = Modifier.size(20.dp)
						)
					}
				}

				FlowRow(
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					verticalArrangement = Arrangement.spacedBy(8.dp)
				) {
					EntryMetaBadge(label = entry.typeLabel, highlighted = isSelected)
					EntryMetaBadge(label = entry.costLabel)
					if (entry.useNotes.isNotBlank()) {
						EntryMetaBadge(
							label = stringResource(R.string.action_view_notes_badge),
							highlighted = isSelected
						)
					}
				}

				action?.let { resolvedAction ->
					ActionHubActionButtons(
						action = resolvedAction,
						character = character,
						viewModel = viewModel,
						situationalBonus = situationalBonus,
						onRollResult = onRollResult,
					)
				}

				spellSlots?.let { slots ->
					SpellSlotControls(
						level = entry.spellLevel,
						slots = slots,
						character = character,
						viewModel = viewModel,
					)
				}
			}
		}
	}
}

@Composable
private fun EntryMetaBadge(label: String, highlighted: Boolean = false) {
	val icon = when {
		label.contains("Attack", ignoreCase = true) || label.contains("Action", ignoreCase = true) -> Icons.Default.FlashOn
		label.contains("Spell", ignoreCase = true) || label.contains("Magic", ignoreCase = true) -> Icons.Default.AutoAwesome
		label.contains("Item", ignoreCase = true) || label.contains("Consumable", ignoreCase = true) -> Icons.Default.Inventory2
		label.contains("Resource", ignoreCase = true) -> Icons.Default.Star
		else -> null
	}
	Surface(
		shape = RoundedCornerShape(999.dp),
		color = if (highlighted) ArcaneTeal.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surfaceVariant,
		border = BorderStroke(
			1.dp,
			if (highlighted) ArcaneTeal.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant
		)
	) {
		Row(
			modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
			horizontalArrangement = Arrangement.spacedBy(6.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			icon?.let {
				Icon(
					imageVector = it,
					contentDescription = null,
					modifier = Modifier.size(14.dp),
					tint = if (highlighted) ArcaneTeal else MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
			Text(
				text = label,
				style = MaterialTheme.typography.labelMedium,
				color = if (highlighted) ArcaneTeal else MaterialTheme.colorScheme.onSurfaceVariant
			)
		}
	}
}

@Composable
private fun ActionHubActionButtons(
	action: io.github.velyene.loreweaver.domain.model.CharacterAction,
	character: CharacterEntry,
	viewModel: CharacterViewModel,
	situationalBonus: String,
	onRollResult: (Pair<String, Int>?) -> Unit,
) {
	val bonus = situationalBonus.toIntOrNull() ?: 0
	val attackRollLabel = stringResource(R.string.action_attack_roll_label, action.name)
	val damageRollLabel = stringResource(R.string.action_damage_roll_label, action.name)
	val attackLogType = stringResource(R.string.attack_log_type)
	Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
		Button(onClick = {
			val total = Random.nextInt(1, 21) + action.attackBonus + bonus
			onRollResult(attackRollLabel to total)
			viewModel.logAction(
				"${character.name} attacked with ${action.name}: $total",
				attackLogType
			)
		}) {
			val sign = if (action.attackBonus >= 0) "+" else ""
			Text(stringResource(R.string.atk_button, sign, action.attackBonus))
		}
		if (action.damageDice.isNotBlank()) {
			OutlinedButton(onClick = {
				onRollResult(damageRollLabel to parseAndRoll(action.damageDice))
			}) {
				Text(stringResource(R.string.dmg_button))
			}
		}
	}
}

@Composable
private fun SpellSlotControls(
	level: Int,
	slots: Pair<Int, Int>,
	character: CharacterEntry,
	viewModel: CharacterViewModel,
) {
	Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = stringResource(R.string.action_view_spell_level_label, level),
				style = MaterialTheme.typography.labelLarge,
				fontWeight = FontWeight.Bold,
				color = ArcaneTeal
			)
			Text(
				text = stringResource(R.string.action_view_resource_value, slots.first, slots.second),
				style = MaterialTheme.typography.bodySmall,
				color = MutedText
			)
		}
		Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
			repeat(slots.second) { index ->
				Surface(
					shape = CircleShape,
					color = if (index < slots.first) ArcaneTeal else ArcaneTeal.copy(alpha = 0.16f),
					modifier = Modifier.size(14.dp)
				) {}
			}
		}
		Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
			Button(onClick = {
				val updated = character.spellSlots.toMutableMap()
				updated[level] = (slots.first - 1).coerceAtLeast(0) to slots.second
				viewModel.updateCharacter(character.copy(spellSlots = updated))
			}) {
				Text(stringResource(R.string.action_view_use_slot_button))
			}
			OutlinedButton(onClick = {
				val updated = character.spellSlots.toMutableMap()
				updated[level] = (slots.first + 1).coerceAtMost(slots.second) to slots.second
				viewModel.updateCharacter(character.copy(spellSlots = updated))
			}) {
				Text(stringResource(R.string.action_view_recover_slot_button))
			}
		}
	}
}

@Composable
private fun ActionDetailPanel(entry: ActionHubEntry?) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
		border = BorderStroke(1.dp, ArcaneTeal.copy(alpha = 0.22f)),
		shape = RoundedCornerShape(20.dp)
	) {
		Column(
			modifier = Modifier.padding(18.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
				Text(
					text = stringResource(R.string.action_view_detail_panel_title),
					style = MaterialTheme.typography.headlineSmall,
					fontWeight = FontWeight.Bold
				)
				Text(
					text = stringResource(R.string.description_label),
					style = MaterialTheme.typography.labelSmall,
					color = MutedText
				)
			}
			Crossfade(targetState = entry, animationSpec = tween(durationMillis = 220), label = "actionDetailPanel") { selectedEntry ->
				if (selectedEntry == null) {
					Text(
						text = stringResource(R.string.action_view_detail_empty),
						style = MaterialTheme.typography.bodyMedium,
						color = MutedText
					)
				} else {
					Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
						Row(
							modifier = Modifier.fillMaxWidth(),
							horizontalArrangement = Arrangement.SpaceBetween,
							verticalAlignment = Alignment.CenterVertically
						) {
							Column(
								modifier = Modifier.weight(1f),
								verticalArrangement = Arrangement.spacedBy(4.dp)
							) {
								Text(
									selectedEntry.title,
									style = MaterialTheme.typography.titleLarge,
									fontWeight = FontWeight.Bold,
									color = AntiqueGold
								)
								Text(
									selectedEntry.summary,
									style = MaterialTheme.typography.bodySmall,
									color = MutedText
								)
							}
							EntryMetaBadge(label = selectedEntry.typeLabel, highlighted = true)
						}
						IdentityDetailRow(stringResource(R.string.description_label), selectedEntry.description)
						FlowRow(
							horizontalArrangement = Arrangement.spacedBy(8.dp),
							verticalArrangement = Arrangement.spacedBy(8.dp)
						) {
							EntryMetaBadge(label = selectedEntry.typeLabel, highlighted = true)
							EntryMetaBadge(label = selectedEntry.costLabel)
						}
						IdentityDetailRow(stringResource(R.string.action_view_use_notes_label), selectedEntry.useNotes)
					}
				}
			}
		}
	}
}

// -----------------------------------------------------------------------------
// 3a. QuickStatsBar
// -----------------------------------------------------------------------------

@Composable
private fun QuickStatsBar(character: CharacterEntry, viewModel: CharacterViewModel) {
	Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
		StatCell(label = stringResource(R.string.ac_label), value = "${character.effectiveAc}")
		StatCell(
			label = stringResource(R.string.initiative_label),
			value = "${if (character.initiative >= 0) "+" else ""}${character.initiative}"
		)
		StatCell(
			label = stringResource(R.string.passive_perc_label),
			value = "${character.passivePerception}"
		)
		StatCell(
			label = stringResource(R.string.speed_label),
			value = stringResource(R.string.speed_feet_value, character.effectiveSpeed),
			valueColor = if (character.effectiveSpeed < character.speed) MaterialTheme.colorScheme.error else Color.Unspecified
		)
// -----------------------------------------------------------------------------
		Column(horizontalAlignment = Alignment.CenterHorizontally) {
			Text(
				stringResource(R.string.insp_label),
				fontWeight = FontWeight.Bold,
				color = AntiqueGold,
				style = MaterialTheme.typography.labelSmall
			)
			Icon(
				imageVector = if (character.hasInspiration) Icons.Default.Star else Icons.Default.StarBorder,
				contentDescription = stringResource(R.string.inspiration_desc),
				tint = if (character.hasInspiration) ArcaneTeal else MaterialTheme.colorScheme.outline,
				modifier = Modifier
					.size(24.dp)
					.clickable {
						viewModel.updateCharacter(character.copy(hasInspiration = !character.hasInspiration))
					}
			)
		}
	}
}

@Composable
private fun StatCell(label: String, value: String, valueColor: Color = Color.Unspecified) {
	Column(horizontalAlignment = Alignment.CenterHorizontally) {
		Text(
			label,
			fontWeight = FontWeight.Bold,
			color = AntiqueGold,
			style = MaterialTheme.typography.labelSmall
		)
		Text(value, fontSize = 20.sp, color = valueColor)
	}
}

// -----------------------------------------------------------------------------
// 3b. HitDiceAndRestingSection
// -----------------------------------------------------------------------------

@Composable
private fun HitDiceAndRestingSection(
	character: CharacterEntry,
	viewModel: CharacterViewModel,
	onRollResult: (Pair<String, Int>?) -> Unit
) {
	val hitDieHealLabel = stringResource(R.string.hit_die_heal_label)
	Text(
		stringResource(R.string.survival_resting_label),
		style = MaterialTheme.typography.titleMedium
	)
	Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
		Text(
			stringResource(
				R.string.hit_dice_label,
				character.hitDieType,
				character.hitDiceCurrent,
				character.hitDiceMax
			),
			modifier = Modifier.weight(1f)
		)
		TextButton(
			onClick = {
				if (character.hitDiceCurrent > 0) {
					val roll = Random.nextInt(1, character.hitDieType + 1)
					val conMod = character.getModifier(character.constitution)
					val healing = (roll + conMod).coerceAtLeast(1)
					viewModel.updateCharacter(
						character.copy(
							hp = (character.hp + healing).coerceAtMost(character.maxHp),
							hitDiceCurrent = character.hitDiceCurrent - 1
						)
					)
					onRollResult(hitDieHealLabel to healing)
				}
			},
			enabled = character.hitDiceCurrent > 0
		) {
			Text(stringResource(R.string.roll_hd_button))
		}
	}
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Button(
			onClick = { viewModel.updateCharacter(character.shortRest()) },
			modifier = Modifier.weight(1f),
			colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
		) {
			Text(stringResource(R.string.short_rest_button))
		}
		Button(
			onClick = { viewModel.updateCharacter(character.longRest()) },
			modifier = Modifier.weight(1f),
			colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
		) {
			Text(stringResource(R.string.long_rest_button))
		}
	}
}

// -----------------------------------------------------------------------------
// 3c. SpellSlotsSection
// -----------------------------------------------------------------------------

@Composable
private fun SpellSlotsSection(character: CharacterEntry, viewModel: CharacterViewModel) {
	if (character.spellSlots.isEmpty()) return
	Text(stringResource(R.string.spell_slots_label), style = MaterialTheme.typography.titleMedium)
	character.spellSlots.toSortedMap().forEach { (level, slots) ->
		SpellSlotRow(level, slots, character, viewModel)
	}
	Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun SpellSlotRow(
	level: Int,
	slots: Pair<Int, Int>,
	character: CharacterEntry,
	viewModel: CharacterViewModel
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier.padding(vertical = 4.dp)
	) {
		Text(
			stringResource(R.string.spell_slot_level, level, slots.first, slots.second),
			modifier = Modifier.weight(1f)
		)
		IconButton(onClick = {
			val updated = character.spellSlots.toMutableMap()
			updated[level] = (slots.first - 1).coerceAtLeast(0) to slots.second
			viewModel.updateCharacter(character.copy(spellSlots = updated))
		}) {
			Icon(
				Icons.Default.Remove,
				contentDescription = stringResource(R.string.use_spell_slot, level)
			)
		}
		IconButton(onClick = {
			val updated = character.spellSlots.toMutableMap()
			updated[level] = (slots.first + 1).coerceAtMost(slots.second) to slots.second
			viewModel.updateCharacter(character.copy(spellSlots = updated))
		}) {
			Icon(
				Icons.Default.Add,
				contentDescription = stringResource(R.string.recover_spell_slot, level)
			)
		}
	}
}

// -----------------------------------------------------------------------------
// 3d. ActionsSection
// -----------------------------------------------------------------------------

@Composable
private fun ActionsSection(
	character: CharacterEntry,
	viewModel: CharacterViewModel,
	situationalBonus: String,
	onRollResult: (Pair<String, Int>?) -> Unit
) {
	Text(
		stringResource(R.string.actions_attacks_label),
		style = MaterialTheme.typography.titleMedium
	)
	character.actions.forEach { action ->
		ActionCard(action, character, viewModel, situationalBonus, onRollResult)
	}
}

@Composable
private fun ActionCard(
	action: io.github.velyene.loreweaver.domain.model.CharacterAction,
	character: CharacterEntry,
	viewModel: CharacterViewModel,
	situationalBonus: String,
	onRollResult: (Pair<String, Int>?) -> Unit
) {
	val bonus = situationalBonus.toIntOrNull() ?: 0
	val attackRollLabel = stringResource(R.string.action_attack_roll_label, action.name)
	val damageRollLabel = stringResource(R.string.action_damage_roll_label, action.name)
	val attackLogType = stringResource(R.string.attack_log_type)
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 4.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
			Column(modifier = Modifier.weight(1f)) {
				Text(action.name, fontWeight = FontWeight.Bold)
				if (action.damageDice.isNotBlank()) {
					Text(
						stringResource(R.string.dmg_label, action.damageDice),
						style = MaterialTheme.typography.bodySmall
					)
				}
			}
			Button(onClick = {
				val total = Random.nextInt(1, 21) + action.attackBonus + bonus
				onRollResult(attackRollLabel to total)
				viewModel.logAction(
					"${character.name} attacked with ${action.name}: $total",
					attackLogType
				)
			}) {
				val sign = if (action.attackBonus >= 0) "+" else ""
				Text(stringResource(R.string.atk_button, sign, action.attackBonus))
			}
			if (action.damageDice.isNotBlank()) {
				Spacer(modifier = Modifier.width(8.dp))
				OutlinedButton(onClick = {
					onRollResult(damageRollLabel to parseAndRoll(action.damageDice))
				}) { Text(stringResource(R.string.dmg_button)) }
			}
		}
	}
}

// -----------------------------------------------------------------------------
// 3e. DiceTraySection
// -----------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DiceTraySection(
	situationalBonus: String,
	onRollResult: (Pair<String, Int>?) -> Unit
) {
	Text(stringResource(R.string.dice_tray_label), style = MaterialTheme.typography.titleMedium)
	FlowRow(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		listOf(4, 6, 8, 10, 12, 20, 100).forEach { sides ->
			val diceLabel = stringResource(R.string.dice_button, sides)
			ElevatedButton(onClick = {
				val bonus = situationalBonus.toIntOrNull() ?: 0
				onRollResult(diceLabel to (Random.nextInt(1, sides + 1) + bonus))
			}) { Text(diceLabel) }
		}
	}
}

// -----------------------------------------------------------------------------
// -----------------------------------------------------------------------------
// -----------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatsTab(
	character: CharacterEntry,
	situationalBonus: String,
	onBonusChange: (String) -> Unit,
	onRollResult: (Pair<String, Int>?) -> Unit
) {
	Row(verticalAlignment = Alignment.CenterVertically) {
		Text(
			stringResource(R.string.situational_bonus_label),
			style = MaterialTheme.typography.titleSmall
		)
		OutlinedTextField(
			value = situationalBonus,
			onValueChange = { input ->
				val valid =
					input.isEmpty() || input == "-" || input.all { c -> c.isDigit() || c == '-' }
				if (valid) onBonusChange(input)
			},
			modifier = Modifier.width(80.dp),
			singleLine = true,
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
		)
	}
	Spacer(modifier = Modifier.height(16.dp))
	AttributeGrid(character, situationalBonus, onRollResult)
	Spacer(modifier = Modifier.height(16.dp))
	Text(stringResource(R.string.skills_label), style = MaterialTheme.typography.titleMedium)
	SkillChips(character, situationalBonus, onRollResult)
}

// -----------------------------------------------------------------------------
// 4a. AttributeGrid
// -----------------------------------------------------------------------------

@Composable
private fun AttributeGrid(
	character: CharacterEntry,
	situationalBonus: String,
	onRollResult: (Pair<String, Int>?) -> Unit
) {
	val attributes = listOf(
		Triple("STR", character.strength, character.getModifier(character.strength)),
		Triple("DEX", character.dexterity, character.getModifier(character.dexterity)),
		Triple("CON", character.constitution, character.getModifier(character.constitution)),
		Triple("INT", character.intelligence, character.getModifier(character.intelligence)),
		Triple("WIS", character.wisdom, character.getModifier(character.wisdom)),
		Triple("CHA", character.charisma, character.getModifier(character.charisma))
	)
	attributes.chunked(3).forEach { rowAttrs ->
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			rowAttrs.forEach { (name, score, mod) ->
				AttributeCard(name, score, mod, situationalBonus, onRollResult, Modifier.weight(1f))
			}
		}
		Spacer(modifier = Modifier.height(8.dp))
	}
}

@Composable
private fun AttributeCard(
	name: String,
	score: Int,
	mod: Int,
	situationalBonus: String,
	onRollResult: (Pair<String, Int>?) -> Unit,
	modifier: Modifier = Modifier
) {
	val label = stringResource(R.string.attribute_check, name)
	Card(
		modifier = modifier.clickable {
			val bonus = situationalBonus.toIntOrNull() ?: 0
			onRollResult(label to (Random.nextInt(1, 21) + mod + bonus))
		},
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
	) {
		Column(
			modifier = Modifier.padding(8.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(name, style = MaterialTheme.typography.labelLarge)
			Text("$score", style = MaterialTheme.typography.titleLarge)
			Text(if (mod >= 0) "+$mod" else "$mod", style = MaterialTheme.typography.bodySmall)
		}
	}
}

// -----------------------------------------------------------------------------
// 4b. SkillChips
// -----------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SkillChips(
	character: CharacterEntry,
	situationalBonus: String,
	onRollResult: (Pair<String, Int>?) -> Unit
) {
	val skills = listOf(
		"Acrobatics" to character.dexterity, "Animal Handling" to character.wisdom,
		"Arcana" to character.intelligence, "Athletics" to character.strength,
		"Deception" to character.charisma, "History" to character.intelligence,
		"Insight" to character.wisdom, "Intimidation" to character.charisma,
		"Investigation" to character.intelligence, "Medicine" to character.wisdom,
		"Nature" to character.intelligence, "Perception" to character.wisdom,
		"Performance" to character.charisma, "Persuasion" to character.charisma,
		"Religion" to character.intelligence, "Sleight of Hand" to character.dexterity,
		"Stealth" to character.dexterity, "Survival" to character.wisdom
	)
	FlowRow(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		skills.forEach { (skill, baseScore) ->
			FilterChip(
				selected = character.proficiencies.contains(skill),
				onClick = {
					val sit = situationalBonus.toIntOrNull() ?: 0
					val bonus = character.getSkillBonus(skill, baseScore)
					onRollResult(skill to (Random.nextInt(1, 21) + bonus + sit))
				},
				label = { Text(skill) }
			)
		}
	}
}

// -----------------------------------------------------------------------------
// 2c. JournalTab
// -----------------------------------------------------------------------------

@Composable
fun JournalTab(
	character: CharacterEntry,
	viewModel: CharacterViewModel,
	onLookupCondition: (String) -> Unit = {}
) {
	val conditionSections = remember(character.activeConditions, character.persistentConditions) {
		buildCharacterConditionSections(character)
	}

	Text(stringResource(R.string.character_build_selected_spells_title), style = MaterialTheme.typography.titleMedium)
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 8.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			if (character.spells.isEmpty()) {
				Text(stringResource(R.string.none_label))
			} else {
				character.spells.forEach { spell ->
					Text(stringResource(R.string.inventory_bullet, spell))
				}
			}
		}
	}

	Spacer(modifier = Modifier.height(16.dp))
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
	if (conditionSections.encounter.isEmpty() && conditionSections.persistent.isEmpty()) {
		Text(stringResource(R.string.none_label), style = MaterialTheme.typography.bodyMedium)
	} else {
		ConditionGroupSection(
			title = stringResource(R.string.encounter_conditions_title),
			conditions = conditionSections.encounter,
			isPersistent = false,
			character = character,
			viewModel = viewModel,
			onLookupCondition = onLookupCondition,
		)
		ConditionGroupSection(
			title = stringResource(R.string.persistent_conditions_title),
			conditions = conditionSections.persistent,
			isPersistent = true,
			character = character,
			viewModel = viewModel,
			onLookupCondition = onLookupCondition,
		)
	}

	if (character.status.isNotBlank()) {
		Spacer(modifier = Modifier.height(16.dp))
		Text(stringResource(R.string.encounter_status_notes_label), style = MaterialTheme.typography.titleMedium)
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.padding(vertical = 8.dp),
			colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
		) {
			Text(
				text = character.status,
				modifier = Modifier.padding(16.dp)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ConditionGroupSection(
	title: String,
	conditions: List<StatusChipModel>,
	isPersistent: Boolean,
	character: CharacterEntry,
	viewModel: CharacterViewModel,
	onLookupCondition: (String) -> Unit,
) {
	if (conditions.isEmpty()) return

	Text(
		text = title,
		style = MaterialTheme.typography.labelLarge,
		fontWeight = FontWeight.Bold,
		color = AntiqueGold
	)
	StatusChipFlowRow(
		statuses = conditions,
		onStatusClick = { status -> onLookupCondition(status.name) },
		onStatusRemove = { status ->
			viewModel.updateCharacter(
				if (isPersistent) {
					character.copy(persistentConditions = character.persistentConditions - status.name)
				} else {
					character.copy(activeConditions = character.activeConditions - status.name)
				}
			)
		}
	)
	Spacer(modifier = Modifier.height(12.dp))
}

private data class CharacterConditionSections(
	val encounter: List<StatusChipModel>,
	val persistent: List<StatusChipModel>,
)

private fun buildCharacterConditionSections(character: CharacterEntry): CharacterConditionSections {
	return CharacterConditionSections(
		encounter = buildStatusChipModels(character.activeConditions, isPersistent = false),
		persistent = buildStatusChipModels(character.persistentConditions, isPersistent = true),
	)
}

@Composable
private fun CharacterIdentityBanner(character: CharacterEntry) {
	val identitySummary = character.formattedIdentity()

	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 12.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp)
		) {
			Text(
				text = character.type,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			if (identitySummary.isNotBlank()) {
				Text(
					text = identitySummary,
					style = MaterialTheme.typography.bodyMedium,
					color = MutedText
				)
			}
			IdentityDetailRow(
				label = stringResource(R.string.species_label),
				value = character.species
			)
			IdentityDetailRow(
				label = stringResource(R.string.background_label),
				value = character.background
			)
		}
	}
}

@Composable
private fun IdentityDetailRow(label: String, value: String) {
	Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
		Text(
			text = label,
			style = MaterialTheme.typography.labelMedium,
			color = AntiqueGold
		)
		Text(
			text = value.ifBlank { stringResource(R.string.none_label) },
			style = MaterialTheme.typography.bodyMedium
		)
	}
}

// -----------------------------------------------------------------------------
// 3. Shared UI components
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
