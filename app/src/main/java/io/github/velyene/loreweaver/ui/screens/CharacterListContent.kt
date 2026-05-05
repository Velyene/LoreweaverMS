/*
 * FILE: CharacterListContent.kt
 *
 * TABLE OF CONTENTS:
 * 1. Function: CharacterListScaffoldContent
 * 2. Function: EmptyCharactersMessage
 * 3. Function: initiativeOrderItems
 * 4. Function: groupedCharacterItems
 * 5. Function: PartyHeader
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
internal fun CharacterListScaffoldContent(
	padding: PaddingValues,
	viewState: CharacterListViewState,
	onPartyFilterChange: (String?) -> Unit,
	onCharacterClick: (String) -> Unit,
	onUpdateHP: (CharacterEntry, Int) -> Unit,
	onDelete: (CharacterEntry) -> Unit,
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

private fun LazyListScope.initiativeOrderItems(
	characters: List<CharacterEntry>,
	sortByInitiative: Boolean,
	currentTurnIndex: Int,
	onCharacterClick: (String) -> Unit,
	onUpdateHP: (CharacterEntry, Int) -> Unit,
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
			onUpdateHP = { delta: Int -> onUpdateHP(character, delta) },
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
	onDelete: (CharacterEntry) -> Unit,
) {
	groupedCharacters.forEach { (party, partyMembers) ->
		item(key = "header_$party") { PartyHeader(party) }
		items(partyMembers, key = { it.id }) { character ->
			CharacterItem(
				character = character,
				onClick = { onCharacterClick(character.id) },
				onUpdateHP = { delta: Int -> onUpdateHP(character, delta) },
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

