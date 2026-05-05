/*
 * FILE: CampaignDetailEncounterMonsterSelection.kt
 *
 * TABLE OF CONTENTS:
 * 1. Encounter Monster Filter Models
 * 2. Encounter Monster Selection Helpers
 * 3. Monster Card Formatting Helpers
 */

package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.model.RemoteItem
import io.github.velyene.loreweaver.domain.util.MonsterReferenceCatalog
import io.github.velyene.loreweaver.domain.util.MonsterReferenceEntry
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver
import io.github.velyene.loreweaver.domain.util.parseMonsterChallengeRatingValue

internal fun filteredEncounterMonsterEntries(
	query: String,
	animalsOnly: Boolean = false,
	creatureType: String? = null,
	challengeRating: String? = null,
	selectedMonsterCounts: Map<String, Int> = emptyMap(),
	selectedOnly: Boolean = false,
	sortMode: EncounterMonsterSortMode = EncounterMonsterSortMode.NAME
): List<MonsterReferenceEntry> {
	val filteredMonsters = MonsterReferenceCatalog.filter(
		query = query,
		group = encounterMonsterGroupFilter(animalsOnly),
		creatureType = creatureType,
		challengeRating = challengeRating
	)
	val visibleMonsters = filterSelectedEncounterMonsterEntries(
		monsters = filteredMonsters,
		selectedMonsterCounts = selectedMonsterCounts,
		selectedOnly = selectedOnly
	)
	return sortEncounterMonsterEntries(visibleMonsters, sortMode)
}

private fun encounterMonsterGroupFilter(animalsOnly: Boolean): String? {
	return MonsterReferenceCatalog.ANIMAL_GROUP.takeIf { animalsOnly }
}

private fun filterSelectedEncounterMonsterEntries(
	monsters: List<MonsterReferenceEntry>,
	selectedMonsterCounts: Map<String, Int>,
	selectedOnly: Boolean
): List<MonsterReferenceEntry> {
	if (!selectedOnly) return monsters
	return monsters.filter { monster -> encounterMonsterKey(monster) in selectedMonsterCounts }
}

private fun sortEncounterMonsterEntries(
	monsters: List<MonsterReferenceEntry>,
	sortMode: EncounterMonsterSortMode
): List<MonsterReferenceEntry> {
	return when (sortMode) {
		EncounterMonsterSortMode.NAME -> monsters.sortedBy(MonsterReferenceEntry::name)
		EncounterMonsterSortMode.CHALLENGE_RATING -> monsters.sortedWith(encounterMonsterChallengeRatingComparator())
	}
}

private fun encounterMonsterChallengeRatingComparator(): Comparator<MonsterReferenceEntry> {
	return compareByDescending<MonsterReferenceEntry> { parseMonsterChallengeRatingValue(it.challengeRating) }
		.thenBy(MonsterReferenceEntry::name)
}

internal fun selectedEncounterMonsterCount(
	selectedMonsterCounts: Map<String, Int>,
	monsterKey: String
): Int {
	return selectedMonsterCounts[monsterKey] ?: 0
}

internal fun encounterMonsterKey(monster: MonsterReferenceEntry): String {
	return ReferenceDetailResolver.slugFor(monster.name)
}

internal fun updateEncounterMonsterSelection(
	selectedMonsterCounts: Map<String, Int>,
	monsterKey: String,
	delta: Int
): Map<String, Int> {
	val current = selectedMonsterCounts[monsterKey] ?: 0
	val updated = (current + delta).coerceAtLeast(0)
	return selectedMonsterCounts.toMutableMap().apply {
		if (updated == 0) remove(monsterKey) else this[monsterKey] = updated
	}.toMap()
}

internal fun removeEncounterMonsterSelection(
	selectedMonsterCounts: Map<String, Int>,
	monsterKey: String
): Map<String, Int> {
	return selectedMonsterCounts.toMutableMap().apply {
		remove(monsterKey)
	}.toMap()
}

internal fun decrementEncounterMonsterSelection(
	selectedMonsterCounts: Map<String, Int>,
	monsterKey: String
): Map<String, Int> {
	return updateEncounterMonsterSelection(
		selectedMonsterCounts = selectedMonsterCounts,
		monsterKey = monsterKey,
		delta = -1
	)
}

internal fun buildEncounterMonsterRemoteItems(selectedMonsterCounts: Map<String, Int>): List<RemoteItem> {
	return selectedMonsterCounts.entries
		.sortedBy { it.key }
		.flatMap { (monsterKey, count) ->
			val monster = MonsterReferenceCatalog.findEntry(monsterKey) ?: return@flatMap emptyList()
			List(count) { monster.toEncounterRemoteItem() }
		}
}

private fun MonsterReferenceEntry.toEncounterRemoteItem(): RemoteItem {
	return RemoteItem(
		id = encounterMonsterKey(this),
		name = name,
		category = ENCOUNTER_REMOTE_ITEM_CATEGORY,
		detail = creatureType.takeIf { it.isNotBlank() } ?: subtitle,
		fullDescription = body
	)
}

internal fun selectedEncounterMonsterSummaries(selectedMonsterCounts: Map<String, Int>): List<EncounterSelectedMonsterSummary> {
	return selectedMonsterCounts.entries
		.mapNotNull { (monsterKey, count) ->
			val monster = MonsterReferenceCatalog.findEntry(monsterKey) ?: return@mapNotNull null
			EncounterSelectedMonsterSummary(
				key = monsterKey,
				name = monster.name,
				count = count,
				challengeRating = monster.challengeRating
			)
		}
		.sortedBy(EncounterSelectedMonsterSummary::name)
}

internal fun compactSelectedEncounterMonsterSummary(
	summaries: List<EncounterSelectedMonsterSummary>,
	visibleItemCount: Int = 3
): Pair<List<EncounterSelectedMonsterSummary>, Int> {
	val visible = summaries.take(visibleItemCount)
	return visible to (summaries.size - visible.size).coerceAtLeast(0)
}

internal fun selectedEncounterMonsterChipLabel(summary: EncounterSelectedMonsterSummary): String {
	val crSuffix = summary.challengeRating.takeIf { it.isNotBlank() }
		?.let { " • $ENCOUNTER_CR_LABEL_PREFIX$it" }
		.orEmpty()
	return "${summary.name} ×${summary.count}$crSuffix"
}

internal fun encounterPickerFocusTarget(hasSelectedMonsters: Boolean): EncounterPickerFocusTarget {
	return if (hasSelectedMonsters) {
		EncounterPickerFocusTarget.SELECTED_SUMMARY
	} else {
		EncounterPickerFocusTarget.PICKER_HEADER
	}
}

internal fun hasActiveEncounterMonsterFilters(
	query: String,
	animalsOnly: Boolean,
	creatureType: String?,
	challengeRating: String?,
	selectedOnly: Boolean,
	sortMode: EncounterMonsterSortMode
): Boolean {
	return query.isNotBlank() ||
		animalsOnly ||
		!creatureType.isNullOrBlank() ||
		!challengeRating.isNullOrBlank() ||
		selectedOnly ||
		sortMode != EncounterMonsterSortMode.NAME
}
