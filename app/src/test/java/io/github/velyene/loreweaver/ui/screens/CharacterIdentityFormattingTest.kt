package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import org.junit.Assert.assertEquals
import org.junit.Test

class CharacterIdentityFormattingTest {
	@Test
	fun formatCharacterIdentity_joinsSpeciesAndBackgroundWhenBothPresent() {
		assertEquals("Elf • Sage", formatCharacterIdentity("Elf", "Sage"))
		assertEquals("Elf • Sage", formatCharacterIdentity(" Elf ", " Sage "))
	}

	@Test
	fun formatCharacterIdentity_omitsBlankParts() {
		assertEquals("Elf", formatCharacterIdentity("Elf", ""))
		assertEquals("Soldier", formatCharacterIdentity("  ", "Soldier"))
		assertEquals("", formatCharacterIdentity("", "  "))
	}

	@Test
	fun formattedIdentity_extensionUsesCharacterFields() {
		val character = CharacterEntry(species = "Tiefling", background = "Charlatan")

		assertEquals("Tiefling • Charlatan", character.formattedIdentity())
	}
}


