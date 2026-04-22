package com.example.encountertimer.ui.screens

import com.example.encountertimer.domain.util.EquipmentReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceScreenToolSearchTest {

	@Test
	fun toolSearch_matchesStructuredToolFields() {
		val calligrapher =
			EquipmentReference.ARTISANS_TOOLS.first { it.name == "Calligrapher's Supplies" }
		val gamingSet = EquipmentReference.OTHER_TOOLS.first { it.name == "Gaming Set" }
		val herbalismKit = EquipmentReference.OTHER_TOOLS.first { it.name == "Herbalism Kit" }

		assertTrue(calligrapher.matchesSearchQuery("Dexterity"))
		assertTrue(calligrapher.matchesSearchQuery("Ink"))
		assertTrue(gamingSet.matchesSearchQuery("dragonchess"))
		assertTrue(gamingSet.matchesSearchQuery("Variants require separate proficiency"))
		assertTrue(herbalismKit.matchesSearchQuery("Antitoxin"))
	}

	@Test
	fun filterEquipmentTools_returnsExpectedMatchesForQuery() {
		assertEquals(
			listOf("Calligrapher's Supplies"),
			filterEquipmentTools("impressive flourishes").map { it.name })
		assertEquals(listOf("Gaming Set"), filterEquipmentTools("dragonchess").map { it.name })
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
			listOf("Gaming Set"),
			visibleEquipmentTools(
				"dragonchess",
				CharacterCreationSubsection.CLASSES
			).map { it.name }
		)
	}
}

