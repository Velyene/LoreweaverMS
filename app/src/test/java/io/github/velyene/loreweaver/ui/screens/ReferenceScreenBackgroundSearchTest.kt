package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.CharacterCreationReference
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

		assertMatchesAll(acolyte::matchesSearchQuery, ACOLYTE, "Religion")
		assertMatchesAll(criminal::matchesSearchQuery, CROWBAR, "Thieves' Tools")
		assertMatchesAll(sage::matchesSearchQuery, "Magic Initiate")
		assertMatchesAll(soldier::matchesSearchQuery, "Gaming Set")
	}

	@Test
	fun filterCharacterCreationBackgrounds_returnsExpectedMatchesForQuery() {
		assertQueryResults(
			listOf(CRIMINAL),
			CROWBAR,
			::filterCharacterCreationBackgrounds
		) { it.name }
		assertQueryResults(
			listOf(ACOLYTE),
			"Religion",
			::filterCharacterCreationBackgrounds
		) { it.name }
		assertQueryResults(listOf("Sage"), "History", ::filterCharacterCreationBackgrounds) { it.name }
		assertNoQueryResults("Nonexistent Background Term", ::filterCharacterCreationBackgrounds)
	}

	@Test
	fun visibleCharacterCreationBackgrounds_respectsSubsectionWhenQueryBlank() {
		assertVisibleWhenBlankInSubsection(
			CharacterCreationReference.BACKGROUNDS.map { it.name },
			CharacterCreationSubsection.SPECIES_ORIGIN,
			::visibleCharacterCreationBackgrounds
		) { it.name }
		assertHiddenWhenBlankInSubsection(CharacterCreationSubsection.ABILITIES, ::visibleCharacterCreationBackgrounds)
	}

	@Test
	fun visibleCharacterCreationBackgrounds_usesAllSubsectionForActiveSearch() {
		assertVisibleForSearch(
			listOf(CRIMINAL),
			CROWBAR,
			CharacterCreationSubsection.ABILITIES,
			::visibleCharacterCreationBackgrounds
		) { it.name }
	}
}
