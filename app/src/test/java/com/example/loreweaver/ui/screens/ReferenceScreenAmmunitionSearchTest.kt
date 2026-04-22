package com.example.loreweaver.ui.screens

import com.example.loreweaver.domain.util.EquipmentReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceScreenAmmunitionSearchTest {

	@Test
	fun ammunitionSearch_matchesStructuredFields() {
		val arrows = EquipmentReference.AMMUNITION.first { it.type == "Arrows" }
		val firearmBullets = EquipmentReference.AMMUNITION.first { it.type == "Bullets, Firearm" }
		val needles = EquipmentReference.AMMUNITION.first { it.type == "Needles" }

		assertTrue(arrows.matchesSearchQuery("Quiver"))
		assertTrue(arrows.matchesSearchQuery("20"))
		assertTrue(firearmBullets.matchesSearchQuery("Pouch"))
		assertTrue(firearmBullets.matchesSearchQuery("3 GP"))
		assertTrue(needles.matchesSearchQuery("50"))
	}

	@Test
	fun filterEquipmentAmmunition_returnsExpectedMatchesForQuery() {
		assertEquals(listOf("Arrows"), filterEquipmentAmmunition("Quiver").map { it.type })
		assertEquals(
			listOf("Bullets, Firearm", "Bullets, Sling", "Needles"),
			filterEquipmentAmmunition("Pouch").map { it.type })
		assertEquals(listOf("Needles"), filterEquipmentAmmunition("50").map { it.type })
		assertTrue(filterEquipmentAmmunition("Nonexistent Ammunition Term").isEmpty())
	}

	@Test
	fun visibleEquipmentAmmunition_respectsEquipmentSubsectionWhenQueryBlank() {
		assertEquals(
			EquipmentReference.AMMUNITION.map { it.type },
			visibleEquipmentAmmunition("", CharacterCreationSubsection.EQUIPMENT).map { it.type }
		)
		assertTrue(visibleEquipmentAmmunition("", CharacterCreationSubsection.CLASSES).isEmpty())
	}

	@Test
	fun visibleEquipmentAmmunition_usesSearchAcrossSubsections() {
		assertEquals(
			listOf("Arrows"),
			visibleEquipmentAmmunition(
				"Quiver",
				CharacterCreationSubsection.SPECIES_ORIGIN
			).map { it.type }
		)
	}
}
