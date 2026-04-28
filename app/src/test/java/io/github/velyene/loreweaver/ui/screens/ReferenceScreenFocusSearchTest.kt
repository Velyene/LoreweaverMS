package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.EquipmentReference
import org.junit.Test

class ReferenceScreenFocusSearchTest {
	private companion object {
		const val HOLY_SYMBOL_RESULTS = "Shield"
		const val HOLY_SYMBOL_QUERY = "Holy Symbol"
		val HOLY_SYMBOL_FOCUSES = listOf("Amulet", "Emblem", "Reliquary")
	}

	@Test
	fun focusSearch_matchesStructuredFields() {
		val crystal = EquipmentReference.FOCUSES.first { it.name == "Crystal" }
		val woodenStaff = EquipmentReference.FOCUSES.first { it.name == "Wooden Staff" }
		val emblem = EquipmentReference.FOCUSES.first { it.name == "Emblem" }

		assertMatchesAll(crystal::matchesSearchQuery, "Arcane Focus", "Wizard")
		assertMatchesAll(woodenStaff::matchesSearchQuery, "Quarterstaff")
		assertMatchesAll(emblem::matchesSearchQuery, HOLY_SYMBOL_RESULTS, "Cleric or Paladin")
	}

	@Test
	fun filterEquipmentFocuses_returnsExpectedMatchesForQuery() {
		assertQueryResults(
			listOf("Crystal", "Orb", "Rod", "Staff", "Wand"),
			"Arcane Focus",
			::filterEquipmentFocuses
		) { it.name }
		assertQueryResults(
			listOf("Sprig of mistletoe", "Wooden Staff", "Yew wand"),
			"Druid or Ranger",
			::filterEquipmentFocuses
		) { it.name }
		assertQueryResults(
			HOLY_SYMBOL_FOCUSES,
			HOLY_SYMBOL_RESULTS,
			::filterEquipmentFocuses
		) { it.name }
		assertQueryResults(
			HOLY_SYMBOL_FOCUSES,
			HOLY_SYMBOL_QUERY,
			::filterEquipmentFocuses
		) { it.name }
		assertNoQueryResults("Nonexistent Focus Term", ::filterEquipmentFocuses)
	}

	@Test
	fun visibleEquipmentFocuses_respectsEquipmentSubsectionWhenQueryBlank() {
		assertVisibleWhenBlankInSubsection(
			EquipmentReference.FOCUSES.map { it.name },
			CharacterCreationSubsection.EQUIPMENT,
			::visibleEquipmentFocuses
		) { it.name }
		assertHiddenWhenBlankInSubsection(CharacterCreationSubsection.CLASSES, ::visibleEquipmentFocuses)
	}

	@Test
	fun visibleEquipmentFocuses_usesSearchAcrossSubsections() {
		assertVisibleForSearch(
			HOLY_SYMBOL_FOCUSES,
			HOLY_SYMBOL_RESULTS,
			CharacterCreationSubsection.SPECIES_ORIGIN,
			::visibleEquipmentFocuses
		) { it.name }
	}
}
