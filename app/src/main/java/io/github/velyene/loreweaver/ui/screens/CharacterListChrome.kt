/*
 * FILE: CharacterListChrome.kt
 *
 * TABLE OF CONTENTS:
 * 1. Shared screen state models
 * 2. Scaffold content and filtering helpers
 * 3. Top app bar and party-filter chrome
 * 4. Empty state and grouped list sections
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.ui.theme.AntiqueGold
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal
import io.github.velyene.loreweaver.ui.theme.MutedText

internal data class SearchState(
	val isActive: Boolean,
	val query: String,
	val onQueryChange: (String) -> Unit,
	val onToggle: () -> Unit,
	val selectedPartyFilter: String?,
	val onPartyFilterChange: (String?) -> Unit
)

internal data class CombatState(
	val showInitiativeOrder: Boolean,
	val onToggle: () -> Unit,
	val sortByInitiative: Boolean,
	val onToggleSort: () -> Unit,
	val currentTurnIndex: Int,
	val onNextTurn: () -> Unit,
	val roundCount: Int,
	val charactersSize: Int
)

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

@Composable
internal fun CharacterListScaffoldContent(
	padding: PaddingValues,
	viewState: CharacterListViewState,
	onPartyFilterChange: (String?) -> Unit,
	onCharacterClick: (String) -> Unit,
	onUpdateHP: (CharacterEntry, Int) -> Unit,
	onDelete: (CharacterEntry) -> Unit
) {
	val listState = rememberLazyListState()

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
			LazyColumn(
				state = listState,
				modifier = Modifier
					.weight(1f)
					.visibleVerticalScrollbar(listState)
			) {
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

internal fun matchesFilter(
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

internal fun computeNextTurnState(currentIndex: Int, roundCount: Int, size: Int): Pair<Int, Int> {
	if (size == 0) return currentIndex to roundCount
	val next = (currentIndex + 1) % size
	return next to (if (next == 0) roundCount + 1 else roundCount)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CharacterListTopBar(
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
			label = { Text(stringResource(R.string.search_characters_label)) },
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
		androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(16.dp))
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

