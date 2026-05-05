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
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.util.CharacterParty
import io.github.velyene.loreweaver.ui.viewmodels.CharacterViewModel

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

internal data class CharacterListViewState(
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
internal fun PartyFilterTabs(
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

