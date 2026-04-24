package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.EquipmentReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceScreenAmmunitionSearchTest {
	private companion object {
		const val ARROWS = "Arrows"
		const val NEEDLES = "Needles"
		const val QUIVER = "Quiver"
	}

	@Test
	fun ammunitionSearch_matchesStructuredFields() {
		val arrows = EquipmentReference.AMMUNITION.first { it.type == ARROWS }
		val firearmBullets = EquipmentReference.AMMUNITION.first { it.type == "Bullets, Firearm" }
		val needles = EquipmentReference.AMMUNITION.first { it.type == NEEDLES }

		assertTrue(arrows.matchesSearchQuery(QUIVER))
		assertTrue(arrows.matchesSearchQuery("20"))
		assertTrue(firearmBullets.matchesSearchQuery("Pouch"))
		assertTrue(firearmBullets.matchesSearchQuery("3 GP"))
		assertTrue(needles.matchesSearchQuery("50"))
	}

	@Test
	fun filterEquipmentAmmunition_returnsExpectedMatchesForQuery() {
		assertEquals(listOf(ARROWS), filterEquipmentAmmunition(QUIVER).map { it.type })
		assertEquals(
			listOf("Bullets, Firearm", "Bullets, Sling", "Needles"),
			filterEquipmentAmmunition("Pouch").map { it.type })
		assertEquals(listOf(NEEDLES), filterEquipmentAmmunition("50").map { it.type })
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
			listOf(ARROWS),
			visibleEquipmentAmmunition(
				QUIVER,
				CharacterCreationSubsection.SPECIES_ORIGIN
			).map { it.type }
		)
	}
}
