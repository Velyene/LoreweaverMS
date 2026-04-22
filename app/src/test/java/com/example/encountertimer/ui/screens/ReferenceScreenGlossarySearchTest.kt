package com.example.encountertimer.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceScreenGlossarySearchTest {

	@Test
	fun filterCoreGlossaryEntries_matchesDistinctRulesText() {
		assertEquals(
			listOf("Heroic Inspiration"),
			filterCoreGlossaryEntries("reroll one die right after").map { it.title }
		)
		assertEquals(
			listOf("Teleportation"),
			filterCoreGlossaryEntries("nearest open space").map { it.title }
		)
		assertEquals(
			listOf("Unarmed Strike"),
			filterCoreGlossaryEntries("save DC based on Strength and proficiency").map { it.title }
		)
		assertEquals(
			listOf("Telepathy"),
			filterCoreGlossaryEntries("communicate mentally with another creature").map { it.title }
		)
		assertEquals(
			listOf("Legendary Action"),
			filterCoreGlossaryEntries("immediately after another creature's turn").map { it.title }
		)
		assertTrue(filterCoreGlossaryEntries("Nonexistent Glossary Phrase").isEmpty())
	}

	@Test
	fun filterCoreGlossaryTables_matchesAbbreviations() {
		assertEquals(
			listOf("Rules Glossary Abbreviations"),
			filterCoreGlossaryTables("Platinum Piece").map { it.title }
		)
		assertEquals(
			listOf("Rules Glossary Abbreviations"),
			filterCoreGlossaryTables("Cha.").map { it.title }
		)
	}
}

