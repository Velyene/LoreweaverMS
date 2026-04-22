package com.example.encountertimer.ui.screens

import com.example.encountertimer.domain.util.CharacterCreationReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceScreenLanguageSearchTest {

	@Test
	fun languageSearch_matchesStructuredLanguageFields() {
		val common = CharacterCreationReference.STANDARD_LANGUAGES.first { it.name == "Common" }
		val dwarvish = CharacterCreationReference.STANDARD_LANGUAGES.first { it.name == "Dwarvish" }
		val primordial = CharacterCreationReference.RARE_LANGUAGES.first { it.name == "Primordial" }

		assertTrue(common.matchesSearchQuery("Common"))
		assertTrue(dwarvish.matchesSearchQuery("3–4"))
		assertTrue(dwarvish.matchesSearchQuery("Standard"))
		assertTrue(primordial.matchesSearchQuery("Primordial"))
		assertTrue(primordial.matchesSearchQuery("Rare"))
	}

	@Test
	fun filterCharacterCreationLanguages_returnsExpectedMatchesForQuery() {
		assertEquals(listOf("Dwarvish"), filterCharacterCreationLanguages("3–4").map { it.name })
		assertEquals(
			listOf("Common Sign Language"),
			filterCharacterCreationLanguages("sign").map { it.name })
		assertEquals(
			listOf("Primordial"),
			filterCharacterCreationLanguages("Primordial").map { it.name })
		assertEquals(
			CharacterCreationReference.RARE_LANGUAGES.map { it.name },
			filterCharacterCreationLanguages("Rare").map { it.name })
		assertTrue(filterCharacterCreationLanguages("Nonexistent Language Term").isEmpty())
	}

	@Test
	fun visibleCharacterCreationLanguages_respectsSubsectionWhenQueryBlank() {
		assertEquals(
			(CharacterCreationReference.STANDARD_LANGUAGES + CharacterCreationReference.RARE_LANGUAGES).map { it.name },
			visibleCharacterCreationLanguages(
				"",
				CharacterCreationSubsection.SPECIES_ORIGIN
			).map { it.name }
		)
		assertTrue(
			visibleCharacterCreationLanguages(
				"",
				CharacterCreationSubsection.ABILITIES
			).isEmpty()
		)
	}

	@Test
	fun visibleCharacterCreationLanguages_usesAllSubsectionForActiveSearch() {
		assertEquals(
			listOf("Primordial"),
			visibleCharacterCreationLanguages(
				"Primordial",
				CharacterCreationSubsection.ABILITIES
			).map { it.name }
		)
	}
}

