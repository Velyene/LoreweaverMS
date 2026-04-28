package io.github.velyene.loreweaver.ui.screens

import org.junit.Test

class ReferenceScreenSpellcastingSearchTest {

	@Test
	fun filterSpellcastingRules_matchesNewDetailedRules() {
		assertQueryResults(listOf("Gaining Spells"), "prepared in your mind", ::filterSpellcastingRules) { it.first }
		assertQueryResults(listOf("Casting in Armor"), "hampered by the armor", ::filterSpellcastingRules) { it.first }
		assertQueryResults(listOf("Casting without Slots"), "takes 10 minutes longer", ::filterSpellcastingRules) { it.first }
		assertNoQueryResults("Nonexistent Spellcasting Rule Term", ::filterSpellcastingRules)
	}

	@Test
	fun filterSpellcastingTables_matchesPreparationTableRows() {
		assertQueryResults(listOf("Spell Preparation by Class"), "Finish a Long Rest", ::filterSpellcastingTables) { it.title }
		assertQueryResults(listOf("Spell Preparation by Class"), "Warlock", ::filterSpellcastingTables) { it.title }
		assertQueryResults(listOf("Spell Preparation by Class"), "change when you", ::filterSpellcastingTables) { it.title }
		assertNoQueryResults("Nonexistent Spell Preparation Term", ::filterSpellcastingTables)
	}
}
