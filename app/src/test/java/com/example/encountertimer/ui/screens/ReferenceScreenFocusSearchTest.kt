package com.example.encountertimer.ui.screens

import com.example.encountertimer.domain.util.EquipmentReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceScreenFocusSearchTest {

	@Test
	fun focusSearch_matchesStructuredFields() {
		val crystal = EquipmentReference.FOCUSES.first { it.name == "Crystal" }
		val woodenStaff = EquipmentReference.FOCUSES.first { it.name == "Wooden Staff" }
		val emblem = EquipmentReference.FOCUSES.first { it.name == "Emblem" }

		assertTrue(crystal.matchesSearchQuery("Arcane Focus"))
		assertTrue(crystal.matchesSearchQuery("Wizard"))
		assertTrue(woodenStaff.matchesSearchQuery("Quarterstaff"))
		assertTrue(emblem.matchesSearchQuery("Shield"))
		assertTrue(emblem.matchesSearchQuery("Cleric or Paladin"))
	}

	@Test
	fun filterEquipmentFocuses_returnsExpectedMatchesForQuery() {
		assertEquals(
			listOf("Crystal", "Orb", "Rod", "Staff", "Wand"),
			filterEquipmentFocuses("Arcane Focus").map { it.name })
		assertEquals(
			listOf("Sprig of mistletoe", "Wooden Staff", "Yew wand"),
			filterEquipmentFocuses("Druid or Ranger").map { it.name })
		assertEquals(
			listOf("Amulet", "Emblem", "Reliquary"),
			filterEquipmentFocuses("Shield").map { it.name })
		assertEquals(
			listOf("Amulet", "Emblem", "Reliquary"),
			filterEquipmentFocuses("Holy Symbol").map { it.name })
		assertTrue(filterEquipmentFocuses("Nonexistent Focus Term").isEmpty())
	}

	@Test
	fun visibleEquipmentFocuses_respectsEquipmentSubsectionWhenQueryBlank() {
		assertEquals(
			EquipmentReference.FOCUSES.map { it.name },
			visibleEquipmentFocuses("", CharacterCreationSubsection.EQUIPMENT).map { it.name }
		)
		assertTrue(visibleEquipmentFocuses("", CharacterCreationSubsection.CLASSES).isEmpty())
	}

	@Test
	fun visibleEquipmentFocuses_usesSearchAcrossSubsections() {
		assertEquals(
			listOf("Amulet", "Emblem", "Reliquary"),
			visibleEquipmentFocuses(
				"Shield",
				CharacterCreationSubsection.SPECIES_ORIGIN
			).map { it.name }
		)
	}
}


