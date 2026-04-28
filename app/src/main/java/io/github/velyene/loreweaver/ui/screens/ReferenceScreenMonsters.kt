package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.util.MONSTER_CARD_SUMMARY_STAT_LABELS
import io.github.velyene.loreweaver.domain.util.MonsterReferenceCatalog
import io.github.velyene.loreweaver.domain.util.MonsterReferenceEntry
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver

private val monsterListContentPadding = PaddingValues(16.dp)
private val monsterListItemSpacing = 8.dp
private val monsterFilterSectionSpacing = 10.dp
private val monsterShortcutChipSpacing = 8.dp
private val monsterCardContentPadding = 16.dp
private val monsterCardSectionSpacing = 6.dp
private val monsterCardPreviewSpacerHeight = 2.dp
private val monsterBadgePadding = 4.dp

@Composable
internal fun MonstersContent(
	monsters: List<MonsterReferenceEntry>,
	selectedGroup: String?,
	selectedCreatureType: String?,
	selectedChallengeRating: String?,
	listState: LazyListState,
	onGroupSelected: (String?) -> Unit,
	onCreatureTypeSelected: (String?) -> Unit,
	onChallengeRatingSelected: (String?) -> Unit,
	onOpenDetail: (String, String) -> Unit
) {
	LazyColumn(
		state = listState,
		modifier = Modifier
			.fillMaxSize()
			.visibleVerticalScrollbar(listState),
		contentPadding = monsterListContentPadding,
		verticalArrangement = Arrangement.spacedBy(monsterListItemSpacing)
	) {
		item(key = MONSTER_FILTERS_ITEM_KEY) {
			MonsterFilterSection(
				selectedGroup = selectedGroup,
				selectedCreatureType = selectedCreatureType,
				selectedChallengeRating = selectedChallengeRating,
				onGroupSelected = onGroupSelected,
				onCreatureTypeSelected = onCreatureTypeSelected,
				onChallengeRatingSelected = onChallengeRatingSelected
			)
		}
		monsterListItems(monsters = monsters, onOpenDetail = onOpenDetail)
	}
}

private fun LazyListScope.monsterListItems(
	monsters: List<MonsterReferenceEntry>,
	onOpenDetail: (String, String) -> Unit
) {
	if (monsters.isEmpty()) {
		item(key = MONSTER_NO_RESULTS_ITEM_KEY) { ReferenceNoResultsState() }
		return
	}

	items(monsters, key = { it.name }) { monster ->
		MonsterCard(monster = monster) {
			onOpenDetail(ReferenceDetailResolver.CATEGORY_MONSTERS, ReferenceDetailResolver.slugFor(monster.name))
		}
	}
}

@Composable
private fun MonsterFilterSection(
	selectedGroup: String?,
	selectedCreatureType: String?,
	selectedChallengeRating: String?,
	onGroupSelected: (String?) -> Unit,
	onCreatureTypeSelected: (String?) -> Unit,
	onChallengeRatingSelected: (String?) -> Unit
) {
	Column(verticalArrangement = Arrangement.spacedBy(monsterFilterSectionSpacing)) {
		MonsterShortcutRow(
			selectedGroup = selectedGroup,
			onGroupSelected = onGroupSelected
		)
		MonsterSingleSelectFilterRow(
			label = stringResource(R.string.reference_monster_filter_cr),
			selectedOption = selectedChallengeRating,
			options = MonsterReferenceCatalog.CHALLENGE_RATING_OPTIONS,
			onOptionSelected = onChallengeRatingSelected
		)
		MonsterSingleSelectFilterRow(
			label = stringResource(R.string.reference_monster_filter_type),
			selectedOption = selectedCreatureType,
			options = MonsterReferenceCatalog.CREATURE_TYPE_OPTIONS,
			onOptionSelected = onCreatureTypeSelected
		)
	}
}

@Composable
private fun MonsterShortcutRow(
	selectedGroup: String?,
	onGroupSelected: (String?) -> Unit
) {
	MonsterFilterSectionBlock(label = stringResource(R.string.reference_monster_filter_shortcuts)) {
		LazyRow(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(monsterShortcutChipSpacing)
		) {
			item(key = MONSTER_SHORTCUT_KEY_PREFIX + MonsterReferenceCatalog.ANIMAL_GROUP) {
				AnimalShortcutFilterChip(
					selectedGroup = selectedGroup,
					onGroupSelected = onGroupSelected
				)
			}
		}
	}
}

@Composable
private fun AnimalShortcutFilterChip(
	selectedGroup: String?,
	onGroupSelected: (String?) -> Unit
) {
	FilterChip(
		selected = selectedGroup == MonsterReferenceCatalog.ANIMAL_GROUP,
		onClick = { onGroupSelected(nextMonsterGroupSelection(selectedGroup)) },
		label = { Text(stringResource(R.string.reference_monster_filter_animals)) }
	)
}

private fun nextMonsterGroupSelection(selectedGroup: String?): String? {
	return if (selectedGroup == MonsterReferenceCatalog.ANIMAL_GROUP) null else MonsterReferenceCatalog.ANIMAL_GROUP
}

@Composable
private fun MonsterCard(monster: MonsterReferenceEntry, onClick: () -> Unit) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(role = Role.Button, onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(monsterCardContentPadding),
			verticalArrangement = Arrangement.spacedBy(monsterCardSectionSpacing)
		) {
			MonsterCardHeader(monster)
			MonsterCardDetails(monster)
		}
	}
}

@Composable
private fun MonsterCardHeader(monster: MonsterReferenceEntry) {
	Text(
		text = monster.name,
		style = MaterialTheme.typography.titleMedium,
		fontWeight = FontWeight.Bold
	)
	FlowRow(
		horizontalArrangement = Arrangement.spacedBy(monsterShortcutChipSpacing),
		verticalArrangement = Arrangement.spacedBy(monsterCardSectionSpacing)
	) {
		monster.group?.let { group -> MonsterGroupBadge(group) }
		Text(
			text = monster.subtitle,
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.secondary
		)
	}
}

@Composable
private fun MonsterCardDetails(monster: MonsterReferenceEntry) {
	monster.monsterCardFilterSummaryLine().takeIf { it.isNotBlank() }?.let { summary ->
		Text(
			text = summary,
			style = MaterialTheme.typography.labelMedium,
			color = MaterialTheme.colorScheme.primary
		)
	}
	monster.monsterCardSummaryLine(MONSTER_CARD_SUMMARY_STAT_LABELS).takeIf { it.isNotBlank() }?.let { summary ->
		Text(
			text = summary,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
	}
	monster.monsterCardPreviewLine()?.let { preview ->
		Spacer(modifier = Modifier.height(monsterCardPreviewSpacerHeight))
		Text(
			text = preview,
			style = MaterialTheme.typography.bodyMedium,
			maxLines = 2,
			color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
		)
	}
}

@Composable
private fun MonsterGroupBadge(group: String) {
	val isAnimalGroup = group.equals(MonsterReferenceCatalog.ANIMAL_GROUP, ignoreCase = true)
	val (containerColor, contentColor) = monsterGroupBadgeColors(isAnimalGroup)
	Badge(
		containerColor = containerColor,
		contentColor = contentColor
	) {
		Text(group.uppercase(), modifier = Modifier.padding(monsterBadgePadding))
	}
}

@Composable
private fun monsterGroupBadgeColors(isAnimalGroup: Boolean) = if (isAnimalGroup) {
	MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
} else {
	MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
}

internal fun filterMonsterEntries(query: String = ""): List<MonsterReferenceEntry> =
	MonsterReferenceCatalog.filter(query)

