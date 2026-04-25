package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.EquipmentReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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

		assertTrue(calligrapher.matchesSearchQuery("Dexterity"))
		assertTrue(calligrapher.matchesSearchQuery("Ink"))
		assertTrue(gamingSet.matchesSearchQuery(DRAGONCHESS))
		assertTrue(gamingSet.matchesSearchQuery("Variants require separate proficiency"))
		assertTrue(herbalismKit.matchesSearchQuery("Antitoxin"))
	}

	@Test
	fun filterEquipmentTools_returnsExpectedMatchesForQuery() {
		assertEquals(
			listOf("Calligrapher's Supplies"),
			filterEquipmentTools("impressive flourishes").map { it.name })
		assertEquals(listOf(GAMING_SET), filterEquipmentTools(DRAGONCHESS).map { it.name })
		assertEquals(listOf("Thieves' Tools"), filterEquipmentTools("Pick a lock").map { it.name })
		assertEquals(
			EquipmentReference.ARTISANS_TOOLS.map { it.name },
			filterEquipmentTools("Artisan's Tools").map { it.name }
		)
		assertTrue(filterEquipmentTools("Nonexistent Tool Term").isEmpty())
	}

	@Test
	fun visibleEquipmentTools_respectsEquipmentSubsectionWhenQueryBlank() {
		assertEquals(
			EquipmentReference.ALL_TOOLS.map { it.name },
			visibleEquipmentTools("", CharacterCreationSubsection.EQUIPMENT).map { it.name }
		)
		assertTrue(visibleEquipmentTools("", CharacterCreationSubsection.SPECIES_ORIGIN).isEmpty())
	}

	@Test
	fun visibleEquipmentTools_usesSearchAcrossSubsections() {
		assertEquals(
			listOf(GAMING_SET),
			visibleEquipmentTools(
				DRAGONCHESS,
				CharacterCreationSubsection.CLASSES
			).map { it.name }
		)
	}
}
