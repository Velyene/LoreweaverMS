/*
 * FILE: CharacterListScreen.kt
 *
 * TABLE OF CONTENTS:
 * 1. Character list route entry and screen state wiring
 */
package io.github.velyene.loreweaver.ui.screens
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.util.CharacterParty
import io.github.velyene.loreweaver.ui.viewmodels.CharacterViewModel
internal val CharacterEntry.partyLabel: String
	get() = if (party == CharacterParty.ADVENTURERS) CharacterParty.ADVENTURERS else CharacterParty.MONSTERS
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
