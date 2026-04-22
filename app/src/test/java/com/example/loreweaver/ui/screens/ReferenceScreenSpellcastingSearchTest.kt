package com.example.loreweaver.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceScreenSpellcastingSearchTest {

	@Test
	fun filterSpellcastingRules_matchesNewDetailedRules() {
		assertEquals(
			listOf("Gaining Spells"),
			filterSpellcastingRules("prepared in your mind").map { it.first }
		)
		assertEquals(
			listOf("Casting in Armor"),
			filterSpellcastingRules("hampered by the armor").map { it.first }
		)
		assertEquals(
			listOf("Casting without Slots"),
			filterSpellcastingRules("takes 10 minutes longer").map { it.first }
		)
		assertTrue(filterSpellcastingRules("Nonexistent Spellcasting Rule Term").isEmpty())
	}

	@Test
	fun filterSpellcastingTables_matchesPreparationTableRows() {
		assertEquals(
			listOf("Spell Preparation by Class"),
			filterSpellcastingTables("Finish a Long Rest").map { it.title }
		)
		assertEquals(
			listOf("Spell Preparation by Class"),
			filterSpellcastingTables("Warlock").map { it.title }
		)
		assertEquals(
			listOf("Spell Preparation by Class"),
			filterSpellcastingTables("change when you").map { it.title }
		)
		assertTrue(filterSpellcastingTables("Nonexistent Spell Preparation Term").isEmpty())
	}
}
