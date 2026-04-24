package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.CharacterCreationReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceScreenLanguageSearchTest {
	private companion object {
		const val PRIMORDIAL = "Primordial"
	}

	@Test
	fun languageSearch_matchesStructuredLanguageFields() {
		val common = CharacterCreationReference.STANDARD_LANGUAGES.first { it.name == "Common" }
		val dwarvish = CharacterCreationReference.STANDARD_LANGUAGES.first { it.name == "Dwarvish" }
		val primordial = CharacterCreationReference.RARE_LANGUAGES.first { it.name == PRIMORDIAL }

		assertTrue(common.matchesSearchQuery("Common"))
		assertTrue(dwarvish.matchesSearchQuery("3â€“4"))
		assertTrue(dwarvish.matchesSearchQuery("Standard"))
		assertTrue(primordial.matchesSearchQuery(PRIMORDIAL))
		assertTrue(primordial.matchesSearchQuery("Rare"))
	}

	@Test
	fun filterCharacterCreationLanguages_returnsExpectedMatchesForQuery() {
		assertEquals(listOf("Dwarvish"), filterCharacterCreationLanguages("3â€“4").map { it.name })
		assertEquals(
			listOf("Common Sign Language"),
			filterCharacterCreationLanguages("sign").map { it.name })
		assertEquals(
			listOf(PRIMORDIAL),
			filterCharacterCreationLanguages(PRIMORDIAL).map { it.name })
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
			listOf(PRIMORDIAL),
			visibleCharacterCreationLanguages(
				PRIMORDIAL,
				CharacterCreationSubsection.ABILITIES
			).map { it.name }
		)
	}
}
