package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.EquipmentReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceScreenArmorSearchTest {
	private companion object {
		const val CHAIN_MAIL = "Chain Mail"
		const val SHIELD = "Shield"
		const val LIGHT = "Light"
		const val UTILIZE_ACTION = "Utilize action"
	}

	@Test
	fun armorSearch_matchesStructuredArmorFields() {
		val leatherArmor = EquipmentReference.ARMOR.first { it.name == "Leather Armor" }
		val chainMail = EquipmentReference.ARMOR.first { it.name == CHAIN_MAIL }
		val shield = EquipmentReference.ARMOR.first { it.name == SHIELD }

		assertTrue(leatherArmor.matchesSearchQuery(LIGHT))
		assertTrue(leatherArmor.matchesSearchQuery("11 + Dex modifier"))
		assertTrue(chainMail.matchesSearchQuery("Str 13"))
		assertTrue(chainMail.matchesSearchQuery("Disadvantage"))
		assertTrue(shield.matchesSearchQuery(UTILIZE_ACTION))
	}

	@Test
	fun filterEquipmentArmor_returnsExpectedMatchesForQuery() {
		assertEquals(listOf(CHAIN_MAIL), filterEquipmentArmor("Str 13").map { it.name })
		assertEquals(listOf(SHIELD), filterEquipmentArmor(UTILIZE_ACTION).map { it.name })
		assertEquals(
			EquipmentReference.ARMOR.filter { it.categoryDonDoff.startsWith(LIGHT) }
				.map { it.name },
			filterEquipmentArmor(LIGHT).map { it.name }
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
			listOf(SHIELD),
			visibleEquipmentArmor(
				UTILIZE_ACTION,
				CharacterCreationSubsection.SPECIES_ORIGIN
			).map { it.name }
		)
	}
}
