package com.example.loreweaver.ui.screens

import com.example.loreweaver.domain.util.CharacterCreationReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceScreenBackgroundSearchTest {

	@Test
	fun backgroundSearch_matchesStructuredBackgroundFields() {
		val acolyte = CharacterCreationReference.BACKGROUNDS.first { it.name == "Acolyte" }
		val criminal = CharacterCreationReference.BACKGROUNDS.first { it.name == "Criminal" }
		val sage = CharacterCreationReference.BACKGROUNDS.first { it.name == "Sage" }
		val soldier = CharacterCreationReference.BACKGROUNDS.first { it.name == "Soldier" }

		assertTrue(acolyte.matchesSearchQuery("Acolyte"))
		assertTrue(acolyte.matchesSearchQuery("Religion"))
		assertTrue(criminal.matchesSearchQuery("Crowbar"))
		assertTrue(criminal.matchesSearchQuery("Thieves' Tools"))
		assertTrue(sage.matchesSearchQuery("Magic Initiate"))
		assertTrue(soldier.matchesSearchQuery("Gaming Set"))
	}

	@Test
	fun filterCharacterCreationBackgrounds_returnsExpectedMatchesForQuery() {
		assertEquals(
			listOf("Criminal"),
			filterCharacterCreationBackgrounds("Crowbar").map { it.name })
		assertEquals(
			listOf("Acolyte"),
			filterCharacterCreationBackgrounds("Religion").map { it.name })
		assertEquals(listOf("Sage"), filterCharacterCreationBackgrounds("History").map { it.name })
		assertTrue(filterCharacterCreationBackgrounds("Nonexistent Background Term").isEmpty())
	}

	@Test
	fun visibleCharacterCreationBackgrounds_respectsSubsectionWhenQueryBlank() {
		assertEquals(
			CharacterCreationReference.BACKGROUNDS.map { it.name },
			visibleCharacterCreationBackgrounds(
				"",
				CharacterCreationSubsection.SPECIES_ORIGIN
			).map { it.name }
		)
		assertTrue(
			visibleCharacterCreationBackgrounds(
				"",
				CharacterCreationSubsection.ABILITIES
			).isEmpty()
		)
	}

	@Test
	fun visibleCharacterCreationBackgrounds_usesAllSubsectionForActiveSearch() {
		assertEquals(
			listOf("Criminal"),
			visibleCharacterCreationBackgrounds(
				"Crowbar",
				CharacterCreationSubsection.ABILITIES
			).map { it.name }
		)
	}
}
