package io.github.velyene.loreweaver.domain.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SpellcastingReferenceTest {

	@Test
	fun spellcasterTypes_matchAllowedBaseSpellcastingClassesExactly() {
		assertEquals(
			listOf(
				SpellcasterType.BARD,
				SpellcasterType.CLERIC,
				SpellcasterType.DRUID,
				SpellcasterType.SORCERER,
				SpellcasterType.WIZARD,
				SpellcasterType.PALADIN,
				SpellcasterType.RANGER,
				SpellcasterType.WARLOCK
			),
			SpellcasterType.entries.toList()
		)
	}

	@Test
	fun getSpellcastingTables_includeSpellPreparationByClass() {
		val tables = SpellcastingReference.getSpellcastingTables()

		assertEquals(listOf("Spell Preparation by Class"), tables.map { it.title })
		assertTrue(tables.single().rows.contains(listOf("Wizard", "Finish a Long Rest", "Any")))
	}

	@Test
	fun getSpellcastingRules_includesExpandedPreparationAndArmorRules() {
		val rules = SpellcastingReference.getSpellcastingRules()

		assertTrue(rules.containsKey("Preparing Spells"))
		assertTrue(
			rules.getValue("Preparing Spells")
				.contains("Most spellcasting monsters donâ€™t change their lists of prepared spells")
		)
		assertTrue(rules.containsKey("Casting in Armor"))
		assertTrue(
			rules.getValue("Casting in Armor")
				.contains("too hampered by the armor for spellcasting")
		)
	}

	@Test
	fun getSpellcastingRules_includesOneSpellSlotPerTurnRule() {
		val rules = SpellcastingReference.getSpellcastingRules()

		assertTrue(rules.containsKey("One Spell with a Spell Slot per Turn"))
		assertTrue(
			rules.getValue("One Spell with a Spell Slot per Turn")
				.contains("expend only one spell slot")
		)
	}

	@Test
	fun getSpellcastingRules_includesDetailedSlotlessCasting() {
		val rules = SpellcastingReference.getSpellcastingRules()

		assertTrue(rules.containsKey("Casting without Slots"))
		assertTrue(
			rules.getValue("Casting without Slots")
				.contains("Spell Scrolls and some other magic items")
		)
		assertTrue(rules.getValue("Casting without Slots").contains("takes 10 minutes longer"))
	}

	@Test
	fun getAreaOfEffectRules_supportsEmanation() {
		assertTrue(
			SpellcastingReference.getAreaOfEffectRules(AreaOfEffect.EMANATION)
				.contains("moves with that source")
		)
	}

	@Test
	fun getSchoolDescription_matchesSrdSummaryStyle() {
		assertEquals(
			"Reveals information.",
			SpellcastingReference.getSchoolDescription(SchoolOfMagic.DIVINATION)
		)
	}
}
