package io.github.velyene.loreweaver.domain.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CoreRulesReferenceTest {

	@Test
	fun sections_coverCoreResolutionAndCombatTopics() {
		val titles = CoreRulesReference.SECTIONS.map { it.title }

		assertTrue(titles.contains("D20 Tests"))
		assertTrue(titles.contains("Combat Sequence"))
		assertTrue(titles.contains("Monster Stat Blocks"))
		assertTrue(titles.contains("Running a Monster"))
		assertTrue(titles.contains("Damage, Healing, and Dying"))
		assertTrue(titles.contains("Temporary Hit Points"))
	}

	@Test
	fun quickTables_includeFoundationalNumericLookups() {
		val tableTitles = CoreRulesReference.ALL_TABLES.map { it.title }

		assertTrue(tableTitles.contains("Ability Modifiers"))
		assertTrue(tableTitles.contains("Proficiency Bonus"))
		assertTrue(tableTitles.contains("Monster Creature Types"))
		assertTrue(tableTitles.contains("Monster Hit Dice by Size"))
		assertTrue(tableTitles.contains("Experience Points by Challenge Rating"))
		assertTrue(tableTitles.contains("Travel Pace"))
		assertTrue(tableTitles.contains("Cover"))
	}

	@Test
	fun glossary_containsTaggedMechanicalReferenceEntries() {
		val glossaryTitles = CoreRulesReference.GLOSSARY_ENTRIES.map { it.title }

		assertTrue(glossaryTitles.contains("Attack [Action]"))
		assertTrue(glossaryTitles.contains("Blinded [Condition]"))
		assertTrue(glossaryTitles.contains("Friendly [Attitude]"))
		assertTrue(glossaryTitles.contains("Legendary Action"))
		assertTrue(glossaryTitles.contains("Telepathy"))
		assertTrue(glossaryTitles.contains("Suffocation [Hazard]"))
	}

	@Test
	fun glossary_abbreviations_coverCommonShorthand() {
		val rows = CoreRulesReference.GLOSSARY_ABBREVIATIONS_TABLE.rows

		assertTrue(rows.contains(listOf("AC", "Armor Class")))
		assertTrue(rows.contains(listOf("PB", "Proficiency Bonus")))
		assertTrue(rows.contains(listOf("XP", "Experience Point(s)")))
	}

	@Test
	fun proficiencyBonusTable_matchesExpectedBounds() {
		val rows = CoreRulesReference.PROFICIENCY_BONUS_TABLE.rows

		assertEquals(listOf("Up to 4", "+2"), rows.first())
		assertEquals(listOf("29–30", "+9"), rows.last())
	}

	@Test
	fun monsterReferenceTables_matchExpectedRepresentativeRows() {
		assertEquals(
			listOf("Tiny", "d4", "2.5"),
			CoreRulesReference.MONSTER_HIT_DICE_BY_SIZE_TABLE.rows.first()
		)
		assertEquals(
			listOf("30", "155,000"),
			CoreRulesReference.EXPERIENCE_POINTS_BY_CHALLENGE_RATING_TABLE.rows.last()
		)
		assertTrue(
			CoreRulesReference.MONSTER_CREATURE_TYPES_TABLE.rows.contains(
				listOf("Fiend", "Creature of the Lower Planes")
			)
		)
	}

	@Test
	fun introductionAndSections_arePopulated() {
		assertTrue(CoreRulesReference.INTRODUCTION.isNotBlank())
		assertTrue(CoreRulesReference.GLOSSARY_INTRODUCTION.isNotBlank())
		assertFalse(CoreRulesReference.SECTIONS.isEmpty())
		assertFalse(CoreRulesReference.ALL_TABLES.isEmpty())
		assertFalse(CoreRulesReference.GLOSSARY_ENTRIES.isEmpty())
		assertFalse(CoreRulesReference.GLOSSARY_TABLES.isEmpty())
	}
}
