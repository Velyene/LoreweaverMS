package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterListSearchTest {
	private val character = CharacterEntry(
		name = "Mira",
		type = "Wizard",
		species = "Elf",
		background = "Sage"
	)

	@Test
	fun matchesCharacterSearch_matchesNameTypeAndIdentityFields() {
		assertTrue(matchesCharacterSearch(character, "Mira"))
		assertTrue(matchesCharacterSearch(character, "Wizard"))
		assertTrue(matchesCharacterSearch(character, "Elf"))
		assertTrue(matchesCharacterSearch(character, "Sage"))
		assertTrue(matchesCharacterSearch(character, "Elf • Sage"))
	}

	@Test
	fun matchesCharacterSearch_treatsBlankQueryAsMatch() {
		assertTrue(matchesCharacterSearch(character, ""))
		assertTrue(matchesCharacterSearch(character, "   "))
	}

	@Test
	fun matchesCharacterSearch_returnsFalseForUnrelatedQuery() {
		assertFalse(matchesCharacterSearch(character, "Barbarian"))
	}
}

