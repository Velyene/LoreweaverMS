package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.MonsterReferenceCatalog
import io.github.velyene.loreweaver.domain.util.MonsterReferenceEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ReferenceScreenMonsterSearchTest {
	private companion object {
		const val ABOLETH = "Aboleth"
		const val ALLOSAURUS = "Allosaurus"
		const val ANCIENT_BLUE_DRAGON = "Ancient Blue Dragon"
		const val ANCIENT_WHITE_DRAGON = "Ancient White Dragon"
		const val BROWN_BEAR = "Brown Bear"
		const val DARKMANTLE = "Darkmantle"
		const val GRIFFON = "Griffon"
		const val NIGHT_HAG = "Night Hag"
		const val NIGHTMARE = "Nightmare"
		const val SWARM_OF_BATS = "Swarm of Bats"
		const val SWARM_OF_RAVENS = "Swarm of Ravens"
		const val TARRASQUE = "Tarrasque"
		const val TYRANNOSAURUS_REX = "Tyrannosaurus Rex"
		const val WILL_O_WISP = "Will-o'-Wisp"
		const val WOLF = "Wolf"
		const val ZOMBIE = "Zombie"

		const val ALLOSAURUS_CLAWS_QUERY =
			"Claws. Melee Attack Roll: +6, reach 5 ft. Hit: 8 (1d8 + 4) Slashing damage."
		const val SWARM_OF_RAVENS_QUERY = "Cacophony (Recharge 6)"
	}

	@Test
	fun filterMonsterEntries_returnsRestoredLocalMonsterCorpus() {
		val results = filterMonsterEntries()

		assertFalse(results.isEmpty())
		assertContainsLabels(
			results,
			MonsterReferenceEntry::name,
			ABOLETH,
			ALLOSAURUS,
			BROWN_BEAR,
			GRIFFON,
			NIGHTMARE,
			"Adult White Dragon",
			ZOMBIE
		)
	}

	@Test
	fun filterMonsterEntries_isStableAcrossRepeatedCalls() {
		assertEquals(filterMonsterEntries(), filterMonsterEntries())
	}

	@Test
	fun filterMonsterEntries_matchesNamesAndEmbeddedMonsterRulesText() {
		assertQueryResults(listOf(ABOLETH), "Consume Memories", ::filterMonsterEntries, MonsterReferenceEntry::name)
		assertQueryResults(listOf(DARKMANTLE), "Magical Darkness fills a 15-foot Emanation", ::filterMonsterEntries, MonsterReferenceEntry::name)
		assertQueryResults(listOf(ANCIENT_BLUE_DRAGON), "88 (16d10) Lightning damage", ::filterMonsterEntries, MonsterReferenceEntry::name)
		assertQueryResults(listOf(NIGHT_HAG), "Nightmare Haunting", ::filterMonsterEntries, MonsterReferenceEntry::name)
		assertQueryResults(listOf(TARRASQUE), "Reflective Carapace", ::filterMonsterEntries, MonsterReferenceEntry::name)
		assertQueryResults(listOf(WILL_O_WISP), "Consume Life", ::filterMonsterEntries, MonsterReferenceEntry::name)
		assertQueryResults(listOf(ALLOSAURUS), ALLOSAURUS_CLAWS_QUERY, ::filterMonsterEntries, MonsterReferenceEntry::name)
	}

	@Test
	fun filterMonsterEntries_matchesAnimalSwarmRulesText() {
		assertQueryResults(listOf(SWARM_OF_RAVENS), SWARM_OF_RAVENS_QUERY, ::filterMonsterEntries, MonsterReferenceEntry::name)
	}

	@Test
	fun monsterCatalog_filtersByCreatureTypeAndChallengeRating() {
		val results = MonsterReferenceCatalog.filter(
			query = "",
			creatureType = "Dragon",
			challengeRating = "20"
		)

		assertFalse(results.isEmpty())
		assertContainsLabels(results, MonsterReferenceEntry::name, ANCIENT_WHITE_DRAGON)
		assertAllMatch(results, "Expected all filtered monsters to be CR 20 dragons") {
			it.creatureType == "Dragon" && it.challengeRating == "20"
		}
	}

	@Test
	fun monsterCatalog_filtersAnimalsByBeastCreatureType() {
		val results = MonsterReferenceCatalog.filter(
			query = "",
			creatureType = "Beast"
		)

		assertFalse(results.isEmpty())
		assertContainsLabels(results, MonsterReferenceEntry::name, ALLOSAURUS, WOLF)
		assertAllMatch(results, "Expected all filtered monsters to be Beasts") { it.creatureType == "Beast" }
	}

	@Test
	fun monsterCatalog_filtersAnimalsByGroupIncludingSwarmAndDinosaurEntries() {
		val results = MonsterReferenceCatalog.filter(
			query = "",
			group = MonsterReferenceCatalog.ANIMAL_GROUP
		)

		assertFalse(results.isEmpty())
		assertContainsLabels(results, MonsterReferenceEntry::name, SWARM_OF_BATS, TYRANNOSAURUS_REX)
		assertAllMatch(results, "Expected all filtered monsters to be in the Animals group") {
			it.group == MonsterReferenceCatalog.ANIMAL_GROUP
		}
	}
}


