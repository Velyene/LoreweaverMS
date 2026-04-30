/*
 * FILE: CharacterListScreen.kt
 *
 * TABLE OF CONTENTS:
 * 1. Character list route entry and screen state wiring
 * 2. Initiative list item composable
 * 3. Character list item composables
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
