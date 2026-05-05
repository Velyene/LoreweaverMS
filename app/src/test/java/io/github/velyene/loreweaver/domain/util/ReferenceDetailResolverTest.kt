/*
 * FILE: ReferenceDetailResolverTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Class: ReferenceDetailResolverTest
 * 2. Value: ALLOSAURUS
 * 3. Value: SWARM_OF_BATS
 * 4. Function: resolve_returnsFeatDetailsForSrdFeatSlug
 * 5. Value: detail
 * 6. Function: resolve_returnsWeaponDetailsForSrdEquipmentSlug
 * 7. Function: resolve_returnsSafeSpellIndexEntryForCanonicalSpellSlug
 * 8. Function: resolve_returnsMonsterDetailsForRestoredMonsterCorpus
 */

package io.github.velyene.loreweaver.domain.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceDetailResolverTest {
	private companion object {
		const val ALLOSAURUS = "Allosaurus"
		const val SWARM_OF_BATS = "Swarm of Bats"
	}

	@Test
	fun resolve_returnsFeatDetailsForSrdFeatSlug() {
		val detail = ReferenceDetailResolver.resolve(
			ReferenceDetailResolver.CATEGORY_FEATS,
			ReferenceDetailResolver.slugFor("Magic Initiate")
		)

		assertNotNull(detail)
		assertEquals("Magic Initiate", detail?.title)
		assertTrue(detail?.subtitle?.contains("Origin") == true)
		assertTrue(detail?.sections?.singleOrNull()?.bullets?.any { it.contains("Two Cantrips") } == true)
	}

	@Test
	fun resolve_returnsWeaponDetailsForSrdEquipmentSlug() {
		val detail = ReferenceDetailResolver.resolve(
			ReferenceDetailResolver.CATEGORY_WEAPONS,
			ReferenceDetailResolver.slugFor("Musket")
		)

		assertNotNull(detail)
		assertEquals("Musket", detail?.title)
		assertTrue(detail?.statRows?.contains("Cost" to "500 GP") == true)
		assertTrue(detail?.sections?.singleOrNull()?.bullets?.any { it.contains("Bullet") } == true)
	}

	@Test
	fun resolve_returnsSafeSpellIndexEntryForCanonicalSpellSlug() {
		val detail = ReferenceDetailResolver.resolve(
			ReferenceDetailResolver.CATEGORY_SPELLS,
			ReferenceDetailResolver.slugFor("Acid Arrow")
		)

		assertNotNull(detail)
		assertEquals("Acid Arrow", detail?.title)
		assertEquals(ReferenceDetailResolver.CATEGORY_SPELLS, detail?.subtitle)
		assertTrue(
			detail?.overview.orEmpty().contains("verified SRD spell-name index", ignoreCase = true)
		)
	}

	@Test
	fun resolve_returnsMonsterDetailsForRestoredMonsterCorpus() {
		val detail = ReferenceDetailResolver.resolve(
			ReferenceDetailResolver.CATEGORY_MONSTERS,
			ReferenceDetailResolver.slugFor("Aboleth")
		)

		assertNotNull(detail)
		assertEquals("Aboleth", detail?.title)
		assertTrue(detail?.statRows?.contains("CR" to "10 (XP 5,900, or 7,200 in lair; PB +4)") == true)
		assertTrue(detail?.sections?.any { it.title == "Actions" && it.body.orEmpty().contains("Dominate Mind", ignoreCase = true) } == true)
	}

	@Test
	fun resolve_returnsAnimalMonsterDetailsForAllosaurus() {
		val detail = ReferenceDetailResolver.resolve(
			ReferenceDetailResolver.CATEGORY_MONSTERS,
			ReferenceDetailResolver.slugFor(ALLOSAURUS)
		)

		assertNotNull(detail)
		assertEquals(ALLOSAURUS, detail?.title)
		assertTrue(detail?.subtitle?.contains(MonsterReferenceCatalog.ANIMAL_GROUP) == true)
		assertTrue(detail?.statRows?.contains(MONSTER_STAT_AC to "13") == true)
		assertTrue(detail?.sections?.any { it.title == MONSTER_SECTION_ACTIONS && it.body.orEmpty().contains("Claws", ignoreCase = true) } == true)
	}

	@Test
	fun resolve_returnsAnimalMonsterDetailsForSwarmOfBats() {
		val detail = ReferenceDetailResolver.resolve(
			ReferenceDetailResolver.CATEGORY_MONSTERS,
			ReferenceDetailResolver.slugFor(SWARM_OF_BATS)
		)

		assertNotNull(detail)
		assertEquals(SWARM_OF_BATS, detail?.title)
		assertTrue(detail?.subtitle?.contains(MonsterReferenceCatalog.ANIMAL_GROUP) == true)
		assertTrue(detail?.sections?.any { it.title == MONSTER_SECTION_TRAITS && it.body.orEmpty().contains("Swarm", ignoreCase = true) } == true)
		assertTrue(detail?.sections?.any { it.title == MONSTER_SECTION_ACTIONS && it.body.orEmpty().contains("Bites", ignoreCase = true) } == true)
	}

	@Test
	fun resolve_returnsMonsterDetailsForExpandedHNMonsterSlice() {
		val detail = ReferenceDetailResolver.resolve(
			ReferenceDetailResolver.CATEGORY_MONSTERS,
			ReferenceDetailResolver.slugFor("Kraken")
		)

		assertNotNull(detail)
		assertEquals("Kraken", detail?.title)
		assertTrue(detail?.statRows?.contains("AC" to "18") == true)
		assertTrue(detail?.sections?.any { it.title == "Actions" && it.body.orEmpty().contains("Lightning Strike", ignoreCase = true) } == true)
	}

	@Test
	fun resolve_returnsMonsterDetailsForExpandedOWMonsterSlice() {
		val detail = ReferenceDetailResolver.resolve(
			ReferenceDetailResolver.CATEGORY_MONSTERS,
			ReferenceDetailResolver.slugFor("Adult White Dragon")
		)

		assertNotNull(detail)
		assertEquals("Adult White Dragon", detail?.title)
		assertTrue(detail?.statRows?.contains("CR" to "13 (XP 10,000, or 11,500 in lair; PB +5)") == true)
		assertTrue(detail?.sections?.any { it.title == "Legendary Actions" && it.body.orEmpty().contains("Freezing Burst", ignoreCase = true) } == true)
	}

	@Test
	fun resolve_returnsMonsterDetailsForFinalXZMonsterSlice() {
		val detail = ReferenceDetailResolver.resolve(
			ReferenceDetailResolver.CATEGORY_MONSTERS,
			ReferenceDetailResolver.slugFor("Zombie")
		)

		assertNotNull(detail)
		assertEquals("Zombie", detail?.title)
		assertTrue(detail?.statRows?.contains("AC" to "8") == true)
		assertTrue(detail?.sections?.any { it.title == "Traits" && it.body.orEmpty().contains("Undead Fortitude", ignoreCase = true) } == true)
	}

	@Test
	fun resolve_returnsConditionDetailsFromCoreGlossary() {
		val detail = ReferenceDetailResolver.resolve(
			ReferenceDetailResolver.CATEGORY_CONDITIONS,
			"blinded"
		)

		assertNotNull(detail)
		assertEquals("Blinded", detail?.title)
		assertEquals(ReferenceDetailResolver.CATEGORY_CONDITIONS, detail?.subtitle)
		assertTrue(detail?.overview.orEmpty().contains("cannot see", ignoreCase = true))
	}

	@Test
	fun resolve_returnsActionDetailsFromCoreGlossary() {
		val detail = ReferenceDetailResolver.resolve(
			ReferenceDetailResolver.CATEGORY_ACTIONS,
			ReferenceDetailResolver.slugFor("Dodge")
		)

		assertNotNull(detail)
		assertEquals("Dodge", detail?.title)
		assertEquals(ReferenceDetailResolver.CATEGORY_ACTIONS, detail?.subtitle)
		assertTrue(detail?.overview.orEmpty().contains("attacks against you", ignoreCase = true))
	}

	@Test
	fun resolve_returnsHazardDetailsFromCoreGlossary() {
		val detail = ReferenceDetailResolver.resolve(
			ReferenceDetailResolver.CATEGORY_HAZARDS,
			ReferenceDetailResolver.slugFor("Burning")
		)

		assertNotNull(detail)
		assertEquals("Burning", detail?.title)
		assertEquals(ReferenceDetailResolver.CATEGORY_HAZARDS, detail?.subtitle)
		assertTrue(detail?.overview.orEmpty().contains("ongoing fire damage", ignoreCase = true))
	}

	@Test
	fun resolve_returnsGenericCoreRulesGlossaryDetails() {
		val detail = ReferenceDetailResolver.resolve(
			ReferenceDetailResolver.CATEGORY_GLOSSARY,
			ReferenceDetailResolver.slugFor("Concentration")
		)

		assertNotNull(detail)
		assertEquals("Concentration", detail?.title)
		assertEquals(ReferenceDetailResolver.CATEGORY_GLOSSARY, detail?.subtitle)
		assertTrue(detail?.overview.orEmpty().contains("stay active", ignoreCase = true))
	}

	@Test
	fun resolve_returnsLifestyleTableRowDetails() {
		val detail = ReferenceDetailResolver.resolve(
			ReferenceDetailResolver.CATEGORY_LIFESTYLES,
			ReferenceDetailResolver.slugFor("Modest")
		)

		assertNotNull(detail)
		assertEquals("Modest", detail?.title)
		assertTrue(detail?.statRows?.contains("Daily Cost" to "1 GP") == true)
	}

	@Test
	fun resolve_acceptsSingularCategoryAliasesAndTypographicSlugs() {
		val spellDetail = ReferenceDetailResolver.resolve(
			"Spell",
			ReferenceDetailResolver.slugFor("Heroesâ€™ Feast")
		)
		val conditionDetail = ReferenceDetailResolver.resolve("Condition", "blinded")

		assertNotNull(spellDetail)
		assertEquals("Heroesâ€™ Feast", spellDetail?.title)
		assertNotNull(conditionDetail)
		assertEquals("Blinded", conditionDetail?.title)
	}

	@Test
	fun resolve_acceptsUiSectionAliasForToolsAndHirelingNames() {
		val toolDetail = ReferenceDetailResolver.resolve(
			"Tool Reference",
			ReferenceDetailResolver.slugFor("Alchemist's Supplies")
		)
		val hirelingDetail = ReferenceDetailResolver.resolve(
			"Hireling",
			ReferenceDetailResolver.slugFor("Skilled Hireling")
		)

		assertNotNull(toolDetail)
		assertEquals("Alchemist's Supplies", toolDetail?.title)
		assertNotNull(hirelingDetail)
		assertEquals("Skilled", hirelingDetail?.title)
		assertTrue(hirelingDetail?.statRows?.contains("Pay" to "2 GP per day") == true)
	}

	@Test
	fun resolve_returnsNullForBlankCategoryOrSlug() {
		assertNull(ReferenceDetailResolver.resolve("", "shield"))
		assertNull(ReferenceDetailResolver.resolve(ReferenceDetailResolver.CATEGORY_ARMOR, " "))
	}
}
