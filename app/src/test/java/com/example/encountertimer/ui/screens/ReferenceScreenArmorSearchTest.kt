package com.example.encountertimer.ui.screens

import com.example.encountertimer.domain.util.EquipmentReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceScreenArmorSearchTest {

	@Test
	fun armorSearch_matchesStructuredArmorFields() {
		val leatherArmor = EquipmentReference.ARMOR.first { it.name == "Leather Armor" }
		val chainMail = EquipmentReference.ARMOR.first { it.name == "Chain Mail" }
		val shield = EquipmentReference.ARMOR.first { it.name == "Shield" }

		assertTrue(leatherArmor.matchesSearchQuery("Light"))
		assertTrue(leatherArmor.matchesSearchQuery("11 + Dex modifier"))
		assertTrue(chainMail.matchesSearchQuery("Str 13"))
		assertTrue(chainMail.matchesSearchQuery("Disadvantage"))
		assertTrue(shield.matchesSearchQuery("Utilize action"))
	}

	@Test
	fun filterEquipmentArmor_returnsExpectedMatchesForQuery() {
		assertEquals(listOf("Chain Mail"), filterEquipmentArmor("Str 13").map { it.name })
		assertEquals(listOf("Shield"), filterEquipmentArmor("Utilize action").map { it.name })
		assertEquals(
			EquipmentReference.ARMOR.filter { it.categoryDonDoff.startsWith("Light") }
				.map { it.name },
			filterEquipmentArmor("Light").map { it.name }
		)
		assertEquals(
			listOf(
				"Padded Armor",
				"Scale Mail",
				"Half Plate Armor",
				"Ring Mail",
				"Chain Mail",
				"Splint Armor",
				"Plate Armor"
			),
			filterEquipmentArmor("Disadvantage").map { it.name }
		)
		assertTrue(filterEquipmentArmor("Nonexistent Armor Term").isEmpty())
	}

	@Test
	fun visibleEquipmentArmor_respectsEquipmentSubsectionWhenQueryBlank() {
		assertEquals(
			EquipmentReference.ARMOR.map { it.name },
			visibleEquipmentArmor("", CharacterCreationSubsection.EQUIPMENT).map { it.name }
		)
		assertTrue(visibleEquipmentArmor("", CharacterCreationSubsection.CLASSES).isEmpty())
	}

	@Test
	fun visibleEquipmentArmor_usesSearchAcrossSubsections() {
		assertEquals(
			listOf("Shield"),
			visibleEquipmentArmor(
				"Utilize action",
				CharacterCreationSubsection.SPECIES_ORIGIN
			).map { it.name }
		)
	}
}

