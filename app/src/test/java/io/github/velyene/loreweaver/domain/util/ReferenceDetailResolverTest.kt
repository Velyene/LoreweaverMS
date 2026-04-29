package io.github.velyene.loreweaver.domain.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceDetailResolverTest {

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
