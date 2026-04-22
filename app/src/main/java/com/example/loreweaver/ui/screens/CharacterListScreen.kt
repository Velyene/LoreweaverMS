/*
 * FILE: CharacterListScreen.kt
 *
 * TABLE OF CONTENTS:
 * 1. Main List Screen (CharacterListScreen)
 *    a. Search & Filtering Logic
 *    b. Combat Mode State (Initiative, Rounds)
 *    c. Active Combat UI (Next Turn, Round Counter)
 * 2. State Bundles (SearchState, CombatState)
 * 3. Top Bar (CharacterListTopBar)
 * 4. Party Filter Tabs (PartyFilterTabs)
 * 5. Empty State (EmptyCharactersMessage)
 * 6. List Sections (InitiativeOrderList, GroupedCharacterList)
 * 7. UI Components (InitiativeItem, CharacterItem)
 * 8. Initiative Item Sub-sections
 */

package com.example.loreweaver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.loreweaver.R
import com.example.loreweaver.domain.model.CharacterEntry
import com.example.loreweaver.domain.util.CharacterParty
import com.example.loreweaver.ui.theme.AntiqueGold
import com.example.loreweaver.ui.theme.ArcaneTeal
import com.example.loreweaver.ui.theme.MutedText
import com.example.loreweaver.ui.viewmodels.CharacterViewModel

// -----------------------------------------------------------------------------
// -----------------------------------------------------------------------------
// -----------------------------------------------------------------------------

private val CharacterEntry.partyLabel: String
	get() = if (party == CharacterParty.ADVENTURERS) CharacterParty.ADVENTURERS else CharacterParty.MONSTERS

// -----------------------------------------------------------------------------
// 2. State Bundles
// -----------------------------------------------------------------------------

private data class SearchState(
	val isActive: Boolean,
	val query: String,
	val onQueryChange: (String) -> Unit,
	val onToggle: () -> Unit,
	val selectedPartyFilter: String?,
	val onPartyFilterChange: (String?) -> Unit
)

private data class CombatState(
	val showInitiativeOrder: Boolean,
	val onToggle: () -> Unit,
	val sortByInitiative: Boolean,
	val onToggleSort: () -> Unit,
	val currentTurnIndex: Int,
	val onNextTurn: () -> Unit,
	val roundCount: Int,
	val charactersSize: Int
)

// -----------------------------------------------------------------------------
// View-state bundle for Scaffold body
// -----------------------------------------------------------------------------

private data class CharacterListViewState(
	val showInitiativeOrder: Boolean,
	val allParties: List<String>,
	val selectedPartyFilter: String?,
	val filteredCharacters: List<CharacterEntry>,
	val searchQuery: String,
	val groupedCharacters: Map<String, List<CharacterEntry>>,
	val sortByInitiative: Boolean,
	val currentTurnIndex: Int
)

// -----------------------------------------------------------------------------
// 1. Main Screen
// -----------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
	onCharacterClick: (String) -> Unit,
	onAddCharacter: () -> Unit,
	onBack: () -> Unit,
	viewModel: CharacterViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val characters = uiState.characters

	var searchQuery by rememberSaveable { mutableStateOf("") }
	var selectedPartyFilter by rememberSaveable { mutableStateOf<String?>(null) }
	var isSearchActive by rememberSaveable { mutableStateOf(false) }
	var showInitiativeOrder by rememberSaveable { mutableStateOf(false) }
	var sortByInitiative by rememberSaveable { mutableStateOf(true) }
	var currentTurnIndex by rememberSaveable { mutableIntStateOf(0) }
	var roundCount by rememberSaveable { mutableIntStateOf(1) }

	val filteredCharacters = remember(characters, searchQuery, selectedPartyFilter) {
		characters.filter { matchesFilter(it, searchQuery, selectedPartyFilter) }
	}
	val groupedCharacters = remember(filteredCharacters) {
		filteredCharacters.groupBy { it.partyLabel }
	}
	val allParties = remember(characters) {
		characters.map { it.partyLabel }.distinct().sorted()
	}
	val viewState = remember(
		showInitiativeOrder,
		allParties,
		selectedPartyFilter,
		filteredCharacters,
		searchQuery,
		groupedCharacters,
		sortByInitiative,
		currentTurnIndex
	) {
		CharacterListViewState(
			showInitiativeOrder = showInitiativeOrder,
			allParties = allParties,
			selectedPartyFilter = selectedPartyFilter,
			filteredCharacters = filteredCharacters,
			searchQuery = searchQuery,
			groupedCharacters = groupedCharacters,
			sortByInitiative = sortByInitiative,
			currentTurnIndex = currentTurnIndex
		)
	}

	val searchState = SearchState(
		isActive = isSearchActive,
		query = searchQuery,
		onQueryChange = { searchQuery = it },
		onToggle = {
// -----------------------------------------------------------------------------
			@Suppress("UNUSED_VALUE")
			isSearchActive = !isSearchActive
			@Suppress("UNUSED_VALUE")
			searchQuery = ""
		},
		selectedPartyFilter = selectedPartyFilter,
		onPartyFilterChange = { selectedPartyFilter = it }
	)
	val combatState = CombatState(
		showInitiativeOrder = showInitiativeOrder,
		onToggle = {
			@Suppress("UNUSED_VALUE")
			showInitiativeOrder = !showInitiativeOrder
		},
		sortByInitiative = sortByInitiative,
		onToggleSort = {
			@Suppress("UNUSED_VALUE")
			sortByInitiative = !sortByInitiative
		},
		currentTurnIndex = currentTurnIndex,
		onNextTurn = {
			val (newIndex, newRound) = computeNextTurnState(
				currentTurnIndex,
				roundCount,
				characters.size
			)
			@Suppress("UNUSED_VALUE")
			currentTurnIndex = newIndex
			@Suppress("UNUSED_VALUE")
			roundCount = newRound
		},
		roundCount = roundCount,
		charactersSize = characters.size
	)

	Scaffold(
		topBar = {
			CharacterListTopBar(
				searchState = searchState,
				combatState = combatState,
				onBack = onBack
			)
		},
		floatingActionButton = {
			ExtendedFloatingActionButton(
				onClick = onAddCharacter,
				icon = { Icon(Icons.Default.Add, contentDescription = null) },
				text = { Text(stringResource(R.string.add_character)) }
			)
		}
	) { padding ->
		CharacterListScaffoldContent(
			padding = padding,
			viewState = viewState,
			onPartyFilterChange = { selectedPartyFilter = it },
			onCharacterClick = onCharacterClick,
			onUpdateHP = { char, delta ->
				viewModel.updateCharacter(char.copy(hp = (char.hp + delta).coerceIn(0, char.maxHp)))
			},
			onDelete = { viewModel.deleteCharacter(it) }
		)
	}
}

@Composable
private fun CharacterListScaffoldContent(
	padding: PaddingValues,
	viewState: CharacterListViewState,
	onPartyFilterChange: (String?) -> Unit,
	onCharacterClick: (String) -> Unit,
	onUpdateHP: (CharacterEntry, Int) -> Unit,
	onDelete: (CharacterEntry) -> Unit
) {
	Column(
		modifier = Modifier
			.padding(padding)
			.fillMaxSize()
	) {
		if (!viewState.showInitiativeOrder && viewState.allParties.isNotEmpty()) {
			PartyFilterTabs(
				allParties = viewState.allParties,
				selectedPartyFilter = viewState.selectedPartyFilter,
				onPartyFilterChange = onPartyFilterChange
			)
		}
		if (viewState.filteredCharacters.isEmpty()) {
			EmptyCharactersMessage(searchQuery = viewState.searchQuery)
		} else {
			LazyColumn(modifier = Modifier.weight(1f)) {
				if (viewState.showInitiativeOrder) {
					initiativeOrderItems(
						characters = viewState.filteredCharacters,
						sortByInitiative = viewState.sortByInitiative,
						currentTurnIndex = viewState.currentTurnIndex,
						onCharacterClick = onCharacterClick,
						onUpdateHP = onUpdateHP
					)
				} else {
					groupedCharacterItems(
						groupedCharacters = viewState.groupedCharacters,
						onCharacterClick = onCharacterClick,
						onUpdateHP = onUpdateHP,
						onDelete = onDelete
					)
				}
			}
		}
	}
}

private fun matchesFilter(
	character: CharacterEntry,
	query: String,
	partyFilter: String?
): Boolean {
	val matchesSearch = query.isEmpty() ||
		character.name.contains(query, ignoreCase = true) ||
		character.type.contains(query, ignoreCase = true)
	val matchesParty = partyFilter == null || character.partyLabel == partyFilter
	return matchesSearch && matchesParty
}

/** Returns (newTurnIndex, newRoundCount) without any branching in the caller. */
private fun computeNextTurnState(currentIndex: Int, roundCount: Int, size: Int): Pair<Int, Int> {
	if (size == 0) return currentIndex to roundCount
	val next = (currentIndex + 1) % size
	return next to (if (next == 0) roundCount + 1 else roundCount)
}

// -----------------------------------------------------------------------------
// 3. Top Bar
// -----------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharacterListTopBar(
	searchState: SearchState,
	combatState: CombatState,
	onBack: () -> Unit
) {
	TopAppBar(
		title = { CharacterListTitle(searchState, combatState.showInitiativeOrder) },
		navigationIcon = { CharacterListNavIcon(searchState, onBack) },
		actions = { CharacterListActions(searchState, combatState) }
	)
}

@Composable
private fun CharacterListTitle(searchState: SearchState, showInitiativeOrder: Boolean) {
	if (searchState.isActive) {
		TextField(
			value = searchState.query,
			onValueChange = searchState.onQueryChange,
			placeholder = { Text(stringResource(R.string.search_characters_placeholder)) },
			modifier = Modifier.fillMaxWidth(),
			colors = TextFieldDefaults.colors(
				focusedContainerColor = Color.Transparent,
				unfocusedContainerColor = Color.Transparent
			)
		)
	} else {
		val titleRes =
			if (showInitiativeOrder) R.string.initiative_order_title else R.string.characters_title
		Text(stringResource(titleRes))
	}
}

@Composable
private fun CharacterListNavIcon(searchState: SearchState, onBack: () -> Unit) {
	if (searchState.isActive) {
		IconButton(onClick = searchState.onToggle) {
			Icon(Icons.Default.Close, contentDescription = stringResource(R.string.back_button))
		}
	} else {
		IconButton(onClick = onBack) {
			Icon(
				Icons.AutoMirrored.Filled.ArrowBack,
				contentDescription = stringResource(R.string.back_button)
			)
		}
	}
}

@Composable
private fun CharacterListActions(searchState: SearchState, combatState: CombatState) {
	if (!combatState.showInitiativeOrder) {
		IconButton(onClick = searchState.onToggle) {
			Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search_button))
		}
	}
	if (combatState.showInitiativeOrder) {
		Text(
			stringResource(R.string.round_counter, combatState.roundCount),
			style = MaterialTheme.typography.labelLarge,
			modifier = Modifier.padding(end = 8.dp)
		)
		IconButton(onClick = combatState.onNextTurn) {
			Icon(Icons.Default.PlayArrow, contentDescription = stringResource(R.string.next_turn))
		}
		val sortDesc =
			if (combatState.sortByInitiative) R.string.sort_by_hp else R.string.sort_by_initiative
		IconButton(onClick = combatState.onToggleSort) {
			Icon(Icons.Default.SwapVert, contentDescription = stringResource(sortDesc))
		}
	}
	val toggleIcon =
		if (combatState.showInitiativeOrder) Icons.AutoMirrored.Filled.List else Icons.Default.PlayArrow
	IconButton(onClick = combatState.onToggle) {
		Icon(toggleIcon, contentDescription = stringResource(R.string.toggle_initiative))
	}
}

// -----------------------------------------------------------------------------
// 4. Party Filter Tabs
// -----------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PartyFilterTabs(
	allParties: List<String>,
	selectedPartyFilter: String?,
	onPartyFilterChange: (String?) -> Unit
) {
	PrimaryScrollableTabRow(
		selectedTabIndex = allParties.indexOf(selectedPartyFilter) + 1,
		edgePadding = 16.dp,
		divider = {},
		containerColor = Color.Transparent
	) {
		Tab(selected = selectedPartyFilter == null, onClick = { onPartyFilterChange(null) }) {
			Text(
				stringResource(R.string.all_parties),
				modifier = Modifier.padding(vertical = 12.dp)
			)
		}
		allParties.forEach { party ->
			Tab(selected = selectedPartyFilter == party, onClick = { onPartyFilterChange(party) }) {
				Text(party, modifier = Modifier.padding(vertical = 12.dp))
			}
		}
	}
}

// -----------------------------------------------------------------------------
// 5. Empty State
// -----------------------------------------------------------------------------

@Composable
private fun EmptyCharactersMessage(searchQuery: String) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(32.dp),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Icon(
			imageVector = Icons.Default.Info,
			contentDescription = null,
			modifier = Modifier.size(56.dp),
			tint = ArcaneTeal.copy(alpha = 0.5f)
		)
		Spacer(modifier = Modifier.height(16.dp))
		val message = if (searchQuery.isNotEmpty()) {
			stringResource(R.string.no_search_results, searchQuery)
		} else {
			stringResource(R.string.empty_characters_message)
		}
		Text(
			text = message,
			style = MaterialTheme.typography.titleMedium,
			textAlign = TextAlign.Center,
			color = MutedText
		)
	}
}

// -----------------------------------------------------------------------------
// 6. List Sections
// -----------------------------------------------------------------------------

private fun LazyListScope.initiativeOrderItems(
	characters: List<CharacterEntry>,
	sortByInitiative: Boolean,
	currentTurnIndex: Int,
	onCharacterClick: (String) -> Unit,
	onUpdateHP: (CharacterEntry, Int) -> Unit
) {
	val sorted = if (sortByInitiative) {
		characters.sortedByDescending { it.initiative }
	} else {
		characters.sortedBy { it.hp.toFloat() / it.maxHp }
	}
	itemsIndexed(sorted, key = { index, char -> "init_${char.id}_$index" }) { index, character ->
		val isActive = sortByInitiative && index == currentTurnIndex
		val highlight = isActive || (!sortByInitiative && character.hp < character.maxHp / 4)
		InitiativeItem(
			character = character,
			onClick = { onCharacterClick(character.id) },
			onUpdateHP = { delta -> onUpdateHP(character, delta) },
			highlight = highlight,
			isActiveTurn = isActive
		)
		HorizontalDivider()
	}
}

private fun LazyListScope.groupedCharacterItems(
	groupedCharacters: Map<String, List<CharacterEntry>>,
	onCharacterClick: (String) -> Unit,
	onUpdateHP: (CharacterEntry, Int) -> Unit,
	onDelete: (CharacterEntry) -> Unit
) {
	groupedCharacters.forEach { (party, partyMembers) ->
		item(key = "header_$party") { PartyHeader(party) }
		items(partyMembers, key = { it.id }) { character ->
			CharacterItem(
				character = character,
				onClick = { onCharacterClick(character.id) },
				onUpdateHP = { delta -> onUpdateHP(character, delta) },
				onDelete = { onDelete(character) }
			)
			HorizontalDivider()
		}
	}
}

@Composable
private fun PartyHeader(party: String) {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.background(
				androidx.compose.ui.graphics.Brush.horizontalGradient(
					listOf(AntiqueGold.copy(alpha = 0.15f), AntiqueGold.copy(alpha = 0.05f))
				)
			)
			.padding(horizontal = 16.dp, vertical = 8.dp)
	) {
		Text(
			text = party.uppercase(),
			style = MaterialTheme.typography.labelLarge,
			fontWeight = FontWeight.Bold,
			letterSpacing = 1.5.sp,
			color = AntiqueGold
		)
	}
}

// -----------------------------------------------------------------------------
// 7. InitiativeItem
// -----------------------------------------------------------------------------

@Composable
fun InitiativeItem(
	character: CharacterEntry,
	onClick: () -> Unit,
	onUpdateHP: (Int) -> Unit,
	highlight: Boolean = false,
	isActiveTurn: Boolean = false
) {
	val haptic = LocalHapticFeedback.current
	var lastRoll by remember { mutableStateOf<Int?>(null) }

	val backgroundColor = when {
		isActiveTurn -> MaterialTheme.colorScheme.primaryContainer
		highlight -> MaterialTheme.colorScheme.errorContainer
		else -> Color.Transparent
	}
	val viewDetailsLabel = stringResource(R.string.view_details, character.name)
	val currentTurnStatus = stringResource(R.string.current_turn)
	val dyingAccessibilityLabel = stringResource(R.string.dying_accessibility_label)
	val initiativeContentDescription = stringResource(
		R.string.initiative_accessibility_desc,
		character.name,
		character.initiative,
		character.hp,
		character.maxHp,
		buildDyingAccessibilitySuffix(character.hp == 0, dyingAccessibilityLabel)
	)

	Surface(
		color = backgroundColor,
		modifier = Modifier
			.clickable(onClickLabel = viewDetailsLabel, role = Role.Button, onClick = onClick)
			.semantics(mergeDescendants = true) {
				stateDescription = if (isActiveTurn) currentTurnStatus else ""
				contentDescription = initiativeContentDescription
			}
	) {
		ListItem(
			leadingContent = { InitiativeCircle(character.initiative, isActiveTurn, highlight) },
			headlineContent = { InitiativeHeadline(character.name, isActiveTurn) },
			supportingContent = {
				InitiativeSupportingContent(
					character = character,
					haptic = haptic,
					lastRoll = lastRoll,
					onUpdateHP = onUpdateHP,
					onRoll = { lastRoll = (1..20).random() }
				)
			},
			trailingContent = { InitiativeTrailingContent(character) },
			colors = ListItemDefaults.colors(containerColor = Color.Transparent)
		)
	}
}

private fun buildDyingAccessibilitySuffix(
	isDying: Boolean,
	dyingAccessibilityLabel: String
): String {
	return if (isDying) ". $dyingAccessibilityLabel" else ""
}

// -----------------------------------------------------------------------------
// 8. Initiative Item Sub-sections
// -----------------------------------------------------------------------------

@Composable
private fun InitiativeCircle(initiative: Int, isActiveTurn: Boolean, highlight: Boolean) {
	val color = when {
		isActiveTurn -> MaterialTheme.colorScheme.primary
		highlight -> MaterialTheme.colorScheme.error
		else -> MaterialTheme.colorScheme.outline
	}
	Surface(shape = CircleShape, color = color, modifier = Modifier.size(40.dp)) {
		Box(contentAlignment = Alignment.Center) {
			Text(
				text = initiative.toString(),
				color = if (isActiveTurn || highlight) MaterialTheme.colorScheme.onPrimary else Color.Unspecified,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.clearAndSetSemantics { }
			)
		}
	}
}

@Composable
private fun InitiativeHeadline(name: String, isActiveTurn: Boolean) {
	Row(verticalAlignment = Alignment.CenterVertically) {
		Text(
			name,
			fontWeight = if (isActiveTurn) FontWeight.ExtraBold else FontWeight.Bold,
			color = if (isActiveTurn) MaterialTheme.colorScheme.primary else Color.Unspecified,
			modifier = Modifier.semantics { heading() }
		)
		if (isActiveTurn) {
			Badge(
				modifier = Modifier.padding(start = 8.dp),
				containerColor = MaterialTheme.colorScheme.primary
			) { Text(stringResource(R.string.active_badge)) }
		}
	}
}

@Composable
private fun InitiativeSupportingContent(
	character: CharacterEntry,
	haptic: androidx.compose.ui.hapticfeedback.HapticFeedback,
	lastRoll: Int?,
	onUpdateHP: (Int) -> Unit,
	onRoll: () -> Unit
) {
	Column {
		Text(character.type)
		Row(
			modifier = Modifier.padding(top = 4.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			IconButton(
				onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); onUpdateHP(-1) },
				modifier = Modifier.size(32.dp)
			) {
				Icon(
					Icons.Default.Remove,
					contentDescription = stringResource(R.string.decrease_hp_desc, character.name),
					tint = MaterialTheme.colorScheme.error,
					modifier = Modifier.size(16.dp)
				)
			}
			IconButton(
				onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); onUpdateHP(1) },
				modifier = Modifier.size(32.dp)
			) {
				Icon(
					Icons.Default.Add,
					contentDescription = stringResource(R.string.increase_hp_desc, character.name),
					tint = MaterialTheme.colorScheme.primary,
					modifier = Modifier.size(16.dp)
				)
			}
			Spacer(modifier = Modifier.width(4.dp))
			D20RollButton(lastRoll = lastRoll, onRoll = onRoll, small = true)
		}
	}
}

@Composable
private fun InitiativeTrailingContent(character: CharacterEntry) {
	Column(horizontalAlignment = Alignment.End) {
		Text(
			text = stringResource(R.string.initiative_hp_summary, character.hp, character.maxHp),
			color = if (character.hp < character.maxHp / 4) MaterialTheme.colorScheme.error else Color.Unspecified,
			fontWeight = if (character.hp < character.maxHp / 4) FontWeight.Bold else FontWeight.Normal,
			modifier = Modifier.clearAndSetSemantics { }
		)
		if (character.hp == 0 && character.party == CharacterParty.ADVENTURERS) {
			Text(
				stringResource(R.string.dying_label),
				color = MaterialTheme.colorScheme.error,
				fontWeight = FontWeight.Bold,
				style = MaterialTheme.typography.labelSmall,
				modifier = Modifier.clearAndSetSemantics { }
			)
		}
	}
}

// -----------------------------------------------------------------------------
// D20 Roll Button (shared)
// -----------------------------------------------------------------------------

@Composable
private fun D20RollButton(lastRoll: Int?, onRoll: () -> Unit, small: Boolean = false) {
	val label = if (lastRoll == null) stringResource(R.string.d20_roll)
	else stringResource(R.string.d20_roll_result, lastRoll)
	FilledTonalButton(
		onClick = onRoll,
		modifier = Modifier.height(if (small) 28.dp else 32.dp),
		contentPadding = PaddingValues(horizontal = if (small) 4.dp else 8.dp)
	) {
		Text(
			label,
			style = if (small) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodySmall
		)
	}
}

// -----------------------------------------------------------------------------
// CharacterItem
// -----------------------------------------------------------------------------

@Composable
fun CharacterItem(
	character: CharacterEntry,
	onClick: () -> Unit,
	onUpdateHP: (Int) -> Unit,
	onDelete: () -> Unit
) {
	val haptic = LocalHapticFeedback.current
	var lastRoll by remember { mutableStateOf<Int?>(null) }

	val editDetailsLabel = stringResource(R.string.edit_details, character.name)
	val deleteActionLabel = stringResource(R.string.delete_character_desc, character.name)
	val dyingAccessibilityLabel = stringResource(R.string.dying_accessibility_label)
	val characterContentDescription = stringResource(
		R.string.character_accessibility_desc,
		character.name,
		character.type,
		character.hp,
		character.maxHp,
		character.ac,
		buildDyingAccessibilitySuffix(character.hp == 0, dyingAccessibilityLabel)
	)

	ListItem(
		headlineContent = {
			Text(
				character.name,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.semantics { heading() })
		},
		supportingContent = {
			CharacterItemSupporting(
				character,
				haptic,
				lastRoll,
				onUpdateHP,
				onRoll = { lastRoll = (1..20).random() })
		},
		trailingContent = { CharacterItemTrailing(character) },
		modifier = Modifier
			.clickable(onClickLabel = editDetailsLabel, role = Role.Button) { onClick() }
			.semantics(mergeDescendants = true) {
				contentDescription = characterContentDescription
				customActions = listOf(
					CustomAccessibilityAction(deleteActionLabel) { onDelete(); true }
				)
			}
	)
}

@Composable
private fun CharacterItemSupporting(
	character: CharacterEntry,
	haptic: androidx.compose.ui.hapticfeedback.HapticFeedback,
	lastRoll: Int?,
	onUpdateHP: (Int) -> Unit,
	onRoll: () -> Unit
) {
	Column {
		Text(
			text = stringResource(
				R.string.character_list_item_summary,
				character.type,
				character.hp,
				character.maxHp,
				character.ac
			),
			modifier = Modifier.clearAndSetSemantics { }
		)
		Row(
			modifier = Modifier.padding(top = 4.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			OutlinedButton(
				onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); onUpdateHP(-5) },
				modifier = Modifier
					.height(32.dp)
					.padding(end = 4.dp),
				contentPadding = PaddingValues(horizontal = 8.dp)
			) {
				Text(
					stringResource(R.string.hp_minus_five),
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.error
				)
			}
			OutlinedButton(
				onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); onUpdateHP(5) },
				modifier = Modifier.height(32.dp),
				contentPadding = PaddingValues(horizontal = 8.dp)
			) {
				Text(
					stringResource(R.string.hp_plus_five),
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.primary
				)
			}
			Spacer(modifier = Modifier.width(8.dp))
			D20RollButton(
				lastRoll = lastRoll,
				onRoll = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); onRoll() })
		}
	}
}

@Composable
private fun CharacterItemTrailing(character: CharacterEntry) {
	Column(horizontalAlignment = Alignment.End) {
		Text(character.notes)
		if (character.hp == 0 && character.party == CharacterParty.ADVENTURERS) {
			Text(
				stringResource(R.string.dying_label),
				color = MaterialTheme.colorScheme.error,
				fontWeight = FontWeight.ExtraBold,
				style = MaterialTheme.typography.labelSmall,
				modifier = Modifier.clearAndSetSemantics { }
			)
		}
	}
}
