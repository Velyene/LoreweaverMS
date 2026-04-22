package com.example.encountertimer.ui.screens

import com.example.encountertimer.domain.util.EquipmentReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceScreenWeaponSearchTest {

	@Test
	fun weaponSearch_matchesStructuredWeaponFields() {
		val dagger = EquipmentReference.WEAPONS.first { it.name == "Dagger" }
		val lance = EquipmentReference.WEAPONS.first { it.name == "Lance" }
		val musket = EquipmentReference.WEAPONS.first { it.name == "Musket" }

		assertTrue(dagger.matchesSearchQuery("Finesse"))
		assertTrue(dagger.matchesSearchQuery("Nick"))
		assertTrue(lance.matchesSearchQuery("unless mounted"))
		assertTrue(musket.matchesSearchQuery("500 GP"))
		assertTrue(musket.matchesSearchQuery("Bullet"))
	}

	@Test
	fun filterEquipmentWeapons_returnsExpectedMatchesForQuery() {
		assertEquals(listOf("Blowgun"), filterEquipmentWeapons("Needle").map { it.name })
		assertEquals(listOf("Musket"), filterEquipmentWeapons("500 GP").map { it.name })
		assertEquals(listOf("Lance"), filterEquipmentWeapons("unless mounted").map { it.name })
		assertEquals(
			listOf("Glaive", "Greatsword"),
			filterEquipmentWeapons("Graze").map { it.name }
		)
		assertEquals(
			EquipmentReference.WEAPONS.filter { it.category == "Martial Ranged" }.map { it.name },
			filterEquipmentWeapons("Martial Ranged").map { it.name }
		)
		assertTrue(filterEquipmentWeapons("Nonexistent Weapon Term").isEmpty())
	}

	@Test
	fun visibleEquipmentWeapons_respectsEquipmentSubsectionWhenQueryBlank() {
		assertEquals(
			EquipmentReference.WEAPONS.map { it.name },
			visibleEquipmentWeapons("", CharacterCreationSubsection.EQUIPMENT).map { it.name }
		)
		assertTrue(visibleEquipmentWeapons("", CharacterCreationSubsection.CLASSES).isEmpty())
	}

	@Test
	fun visibleEquipmentWeapons_usesSearchAcrossSubsections() {
		assertEquals(
			listOf("Blowgun"),
			visibleEquipmentWeapons(
				"Needle",
				CharacterCreationSubsection.SPECIES_ORIGIN
			).map { it.name }
		)
	}
}

