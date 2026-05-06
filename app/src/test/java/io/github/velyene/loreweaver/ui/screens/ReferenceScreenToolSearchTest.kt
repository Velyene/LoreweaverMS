package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.EquipmentReference
import org.junit.Test

class ReferenceScreenToolSearchTest {
	private companion object {
		const val GAMING_SET = "Gaming Set"
		const val DRAGONCHESS = "dragonchess"
	}

	@Test
	fun toolSearch_matchesStructuredToolFields() {
		val calligrapher =
			EquipmentReference.ARTISANS_TOOLS.first { it.name == "Calligrapher's Supplies" }
		val gamingSet = EquipmentReference.OTHER_TOOLS.first { it.name == GAMING_SET }
		val herbalismKit = EquipmentReference.OTHER_TOOLS.first { it.name == "Herbalism Kit" }

		assertMatchesAll(calligrapher::matchesQuery, "Dexterity", "Ink")
		assertMatchesAll(gamingSet::matchesQuery, DRAGONCHESS, "Variants require separate proficiency")
		assertMatchesAll(herbalismKit::matchesQuery, "Antitoxin")
	}

	@Test
	fun filterEquipmentTools_returnsExpectedMatchesForQuery() {
		assertQueryResults(
			listOf("Calligrapher's Supplies"),
			"impressive flourishes",
			::filterEquipmentTools
		) { it.name }
		assertQueryResults(listOf(GAMING_SET), DRAGONCHESS, ::filterEquipmentTools) { it.name }
		assertQueryResults(listOf("Thieves' Tools"), "Pick a lock", ::filterEquipmentTools) { it.name }
		assertQueryResults(
			EquipmentReference.ARTISANS_TOOLS.map { it.name },
			"Artisan's Tools",
			::filterEquipmentTools
		) { it.name }
		assertNoQueryResults("Nonexistent Tool Term", ::filterEquipmentTools)
	}

	@Test
	fun visibleEquipmentTools_respectsEquipmentSubsectionWhenQueryBlank() {
		assertVisibleWhenBlankInSubsection(
			EquipmentReference.ALL_TOOLS.map { it.name },
			CharacterCreationSubsection.EQUIPMENT,
			::visibleEquipmentTools
		) { it.name }
		assertHiddenWhenBlankInSubsection(CharacterCreationSubsection.SPECIES_ORIGIN, ::visibleEquipmentTools)
	}

	@Test
	fun visibleEquipmentTools_usesSearchAcrossSubsections() {
		assertVisibleForSearch(
			listOf(GAMING_SET),
			DRAGONCHESS,
			CharacterCreationSubsection.CLASSES,
			::visibleEquipmentTools
		) { it.name }
	}
}
