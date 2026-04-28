package io.github.velyene.loreweaver.ui.screens

import org.junit.Test

class ReferenceScreenGlossarySearchTest {

	@Test
	fun filterCoreGlossaryEntries_matchesDistinctRulesText() {
		assertQueryResults(listOf("Heroic Inspiration"), "reroll one die right after", ::filterCoreGlossaryEntries) { it.title }
		assertQueryResults(listOf("Teleportation"), "nearest open space", ::filterCoreGlossaryEntries) { it.title }
		assertQueryResults(listOf("Unarmed Strike"), "save DC based on Strength and proficiency", ::filterCoreGlossaryEntries) { it.title }
		assertQueryResults(listOf("Telepathy"), "communicate mentally with another creature", ::filterCoreGlossaryEntries) { it.title }
		assertQueryResults(listOf("Legendary Action"), "immediately after another creature's turn", ::filterCoreGlossaryEntries) { it.title }
		assertNoQueryResults("Nonexistent Glossary Phrase", ::filterCoreGlossaryEntries)
	}

	@Test
	fun filterCoreGlossaryTables_matchesAbbreviations() {
		assertQueryResults(listOf("Rules Glossary Abbreviations"), "Platinum Piece", ::filterCoreGlossaryTables) { it.title }
		assertQueryResults(listOf("Rules Glossary Abbreviations"), "Cha.", ::filterCoreGlossaryTables) { it.title }
	}
}
