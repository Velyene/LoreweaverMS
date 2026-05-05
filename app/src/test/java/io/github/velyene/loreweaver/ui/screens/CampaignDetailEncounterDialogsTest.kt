/*
 * FILE: CampaignDetailEncounterDialogsTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Class: CampaignDetailEncounterDialogsTest
 * 2. Function: filteredEncounterMonsterEntries_appliesSearchAndMonsterFilters
 * 3. Value: results
 * 4. Function: filteredEncounterMonsterEntries_sortsByChallengeRatingDescending_thenName
 * 5. Value: currentCr
 * 6. Value: nextCr
 * 7. Function: filteredEncounterMonsterEntries_selectedOnly_limitsResultsToSelectedKeys
 * 8. Function: filteredEncounterMonsterEntries_animalsOnly_limitsResultsToAnimalGroup
 */

package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.MonsterReferenceCatalog
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CampaignDetailEncounterDialogsTest {

	@Test
	fun filteredEncounterMonsterEntries_appliesSearchAndMonsterFilters() {
		val results = filteredEncounterMonsterEntries(
			query = "Dragon",
			animalsOnly = false,
			creatureType = "Dragon",
			challengeRating = "20",
			selectedMonsterCounts = emptyMap(),
			selectedOnly = false,
			sortMode = EncounterMonsterSortMode.NAME
		)

		assertTrue(results.isNotEmpty())
		assertTrue(results.any { it.name == "Ancient White Dragon" })
		assertTrue(results.all { it.creatureType == "Dragon" && it.challengeRating == "20" })
	}

	@Test
	fun filteredEncounterMonsterEntries_sortsByChallengeRatingDescending_thenName() {
		val results = filteredEncounterMonsterEntries(
			query = "Dragon",
			animalsOnly = false,
			selectedMonsterCounts = emptyMap(),
			selectedOnly = false,
			sortMode = EncounterMonsterSortMode.CHALLENGE_RATING
		)

		assertTrue(results.isNotEmpty())
		assertTrue(
			results.zipWithNext().all { (current, next) ->
				val currentCr = MonsterReferenceCatalog.CHALLENGE_RATING_OPTIONS
					.firstOrNull { it == current.challengeRating }
				val nextCr = MonsterReferenceCatalog.CHALLENGE_RATING_OPTIONS
					.firstOrNull { it == next.challengeRating }
				currentCr != null && nextCr != null &&
					io.github.velyene.loreweaver.domain.util.parseMonsterChallengeRatingValue(currentCr) >=
					io.github.velyene.loreweaver.domain.util.parseMonsterChallengeRatingValue(nextCr)
			}
		)
	}

	@Test
	fun filteredEncounterMonsterEntries_selectedOnly_limitsResultsToSelectedKeys() {
		val results = filteredEncounterMonsterEntries(
			query = "",
			animalsOnly = false,
			selectedMonsterCounts = mapOf(
				ReferenceDetailResolver.slugFor("Ancient White Dragon") to 2,
				ReferenceDetailResolver.slugFor("Aboleth") to 1
			),
			selectedOnly = true,
			sortMode = EncounterMonsterSortMode.NAME
		)

		assertEquals(listOf("Aboleth", "Ancient White Dragon"), results.map { it.name })
	}

	@Test
	fun filteredEncounterMonsterEntries_animalsOnly_limitsResultsToAnimalGroup() {
		val results = filteredEncounterMonsterEntries(
			query = "",
			animalsOnly = true,
			selectedMonsterCounts = emptyMap(),
			selectedOnly = false,
			sortMode = EncounterMonsterSortMode.NAME
		)

		assertTrue(results.isNotEmpty())
		assertTrue(results.any { it.name == "Allosaurus" })
		assertTrue(results.any { it.name == "Swarm of Bats" })
		assertTrue(results.any { it.name == "Tyrannosaurus Rex" })
		assertTrue(results.any { it.name == "Wolf" })
		assertTrue(results.all { it.group == MonsterReferenceCatalog.ANIMAL_GROUP })
	}

	@Test
	fun updateEncounterMonsterSelection_tracksQuantitiesAndRemovesZeroCounts() {
		val once = updateEncounterMonsterSelection(emptyMap(), "ancient-white-dragon", 1)
		val twice = updateEncounterMonsterSelection(once, "ancient-white-dragon", 1)
		val cleared = updateEncounterMonsterSelection(twice, "ancient-white-dragon", -2)

		assertEquals(mapOf("ancient-white-dragon" to 1), once)
		assertEquals(mapOf("ancient-white-dragon" to 2), twice)
		assertTrue(cleared.isEmpty())
	}

	@Test
	fun removeEncounterMonsterSelection_removesWholeMonsterEntryForChipAction() {
		val updated = removeEncounterMonsterSelection(
			selectedMonsterCounts = mapOf(
				"ancient-white-dragon" to 2,
				"aboleth" to 1
			),
			monsterKey = "ancient-white-dragon"
		)

		assertEquals(mapOf("aboleth" to 1), updated)
	}

	@Test
	fun decrementEncounterMonsterSelection_reducesCountAndRemovesAtZero() {
		val decremented = decrementEncounterMonsterSelection(
			selectedMonsterCounts = mapOf(
				"ancient-white-dragon" to 2,
				"aboleth" to 1
			),
			monsterKey = "ancient-white-dragon"
		)
		val removed = decrementEncounterMonsterSelection(
			selectedMonsterCounts = mapOf("aboleth" to 1),
			monsterKey = "aboleth"
		)

		assertEquals(mapOf("ancient-white-dragon" to 1, "aboleth" to 1), decremented)
		assertTrue(removed.isEmpty())
	}

	@Test
	fun selectedEncounterMonsterSummaries_andCompactSummary_keepCountsAndOverflow() {
		val summaries = selectedEncounterMonsterSummaries(
			mapOf(
				ReferenceDetailResolver.slugFor("Ancient White Dragon") to 2,
				ReferenceDetailResolver.slugFor("Aboleth") to 1,
				ReferenceDetailResolver.slugFor("Zombie") to 3,
				ReferenceDetailResolver.slugFor("Griffon") to 1
			)
		)
		val (visible, overflow) = compactSelectedEncounterMonsterSummary(summaries)

		assertEquals(listOf("Aboleth", "Ancient White Dragon", "Griffon", "Zombie"), summaries.map { it.name })
		assertEquals(listOf("Aboleth", "Ancient White Dragon", "Griffon"), visible.map { it.name })
		assertEquals(1, overflow)
		assertEquals("Ancient White Dragon ×2 • CR 20", selectedEncounterMonsterChipLabel(summaries[1]))
	}

	@Test
	fun encounterPickerFocusTarget_prefersSummaryWhenSelectionExists() {
		assertEquals(
			EncounterPickerFocusTarget.SELECTED_SUMMARY,
			encounterPickerFocusTarget(hasSelectedMonsters = true)
		)
		assertEquals(
			EncounterPickerFocusTarget.PICKER_HEADER,
			encounterPickerFocusTarget(hasSelectedMonsters = false)
		)
	}

	@Test
	fun hasActiveEncounterMonsterFilters_detectsSearchFiltersAndSortChanges() {
		assertFalse(
			hasActiveEncounterMonsterFilters(
				query = "",
				animalsOnly = false,
				creatureType = null,
				challengeRating = null,
				selectedOnly = false,
				sortMode = EncounterMonsterSortMode.NAME
			)
		)
		assertTrue(
			hasActiveEncounterMonsterFilters(
				query = "dragon",
				animalsOnly = false,
				creatureType = null,
				challengeRating = null,
				selectedOnly = false,
				sortMode = EncounterMonsterSortMode.NAME
			)
		)
		assertTrue(
			hasActiveEncounterMonsterFilters(
				query = "",
				animalsOnly = false,
				creatureType = null,
				challengeRating = null,
				selectedOnly = false,
				sortMode = EncounterMonsterSortMode.CHALLENGE_RATING
			)
		)
		assertTrue(
			hasActiveEncounterMonsterFilters(
				query = "",
				animalsOnly = false,
				creatureType = null,
				challengeRating = null,
				selectedOnly = true,
				sortMode = EncounterMonsterSortMode.NAME
			)
		)
		assertTrue(
			hasActiveEncounterMonsterFilters(
				query = "",
				animalsOnly = true,
				creatureType = null,
				challengeRating = null,
				selectedOnly = false,
				sortMode = EncounterMonsterSortMode.NAME
			)
		)
	}

	@Test
	fun buildEncounterMonsterRemoteItems_repeatsCatalogSelectionsWithStableSlugs() {
		val items = buildEncounterMonsterRemoteItems(
			mapOf(
				ReferenceDetailResolver.slugFor("Ancient White Dragon") to 2,
				ReferenceDetailResolver.slugFor("Aboleth") to 1
			)
		)

		assertEquals(3, items.size)
		assertEquals(2, items.count { it.name == "Ancient White Dragon" })
		assertEquals(1, items.count { it.name == "Aboleth" })
		assertTrue(items.all { it.category == "monster" })
		assertEquals(
			ReferenceDetailResolver.slugFor("Ancient White Dragon"),
			items.first { it.name == "Ancient White Dragon" }.id
		)
	}
}

