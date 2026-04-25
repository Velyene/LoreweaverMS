package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.CharacterCreationReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceScreenBackgroundSearchTest {
	private companion object {
		const val ACOLYTE = "Acolyte"
		const val CRIMINAL = "Criminal"
		const val CROWBAR = "Crowbar"
	}

	@Test
	fun backgroundSearch_matchesStructuredBackgroundFields() {
		val acolyte = CharacterCreationReference.BACKGROUNDS.first { it.name == ACOLYTE }
		val criminal = CharacterCreationReference.BACKGROUNDS.first { it.name == CRIMINAL }
		val sage = CharacterCreationReference.BACKGROUNDS.first { it.name == "Sage" }
		val soldier = CharacterCreationReference.BACKGROUNDS.first { it.name == "Soldier" }

		assertTrue(acolyte.matchesSearchQuery(ACOLYTE))
		assertTrue(acolyte.matchesSearchQuery("Religion"))
		assertTrue(criminal.matchesSearchQuery(CROWBAR))
		assertTrue(criminal.matchesSearchQuery("Thieves' Tools"))
		assertTrue(sage.matchesSearchQuery("Magic Initiate"))
		assertTrue(soldier.matchesSearchQuery("Gaming Set"))
	}

	@Test
	fun filterCharacterCreationBackgrounds_returnsExpectedMatchesForQuery() {
		assertEquals(
			listOf(CRIMINAL),
			filterCharacterCreationBackgrounds(CROWBAR).map { it.name })
		assertEquals(
			listOf(ACOLYTE),
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
			listOf(CRIMINAL),
			visibleCharacterCreationBackgrounds(
				CROWBAR,
				CharacterCreationSubsection.ABILITIES
			).map { it.name }
		)
	}
}
