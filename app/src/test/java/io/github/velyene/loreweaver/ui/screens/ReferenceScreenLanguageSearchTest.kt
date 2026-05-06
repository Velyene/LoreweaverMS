package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.CharacterCreationReference
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

		assertMatchesAll(common::matchesQuery, "Common")
		assertMatchesAll(dwarvish::matchesQuery, "3–4", "Standard")
		assertMatchesAll(primordial::matchesQuery, PRIMORDIAL, "Rare")
	}

	@Test
	fun filterCharacterCreationLanguages_returnsExpectedMatchesForQuery() {
		assertQueryResults(listOf("Dwarvish"), "3–4", ::filterCharacterCreationLanguages) { it.name }
		assertQueryResults(
			listOf("Common Sign Language"),
			"sign",
			::filterCharacterCreationLanguages
		) { it.name }
		assertQueryResults(
			listOf(PRIMORDIAL),
			PRIMORDIAL,
			::filterCharacterCreationLanguages
		) { it.name }
		assertQueryResults(
			CharacterCreationReference.RARE_LANGUAGES.map { it.name },
			"Rare",
			::filterCharacterCreationLanguages
		) { it.name }
		assertNoQueryResults("Nonexistent Language Term", ::filterCharacterCreationLanguages)
	}

	@Test
	fun visibleCharacterCreationLanguages_respectsSubsectionWhenQueryBlank() {
		assertVisibleWhenBlankInSubsection(
			(CharacterCreationReference.STANDARD_LANGUAGES + CharacterCreationReference.RARE_LANGUAGES).map { it.name },
			CharacterCreationSubsection.SPECIES_ORIGIN,
			::visibleCharacterCreationLanguages
		) { it.name }
		assertHiddenWhenBlankInSubsection(CharacterCreationSubsection.ABILITIES, ::visibleCharacterCreationLanguages)
	}

	@Test
	fun visibleCharacterCreationLanguages_usesAllSubsectionForActiveSearch() {
		assertVisibleForSearch(
			listOf(PRIMORDIAL),
			PRIMORDIAL,
			CharacterCreationSubsection.ABILITIES,
			::visibleCharacterCreationLanguages
		) { it.name }
	}
}
