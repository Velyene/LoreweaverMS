package com.example.encountertimer.ui.screens

import com.example.encountertimer.domain.util.EquipmentReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceScreenTransportSearchTest {

	@Test
	fun transportSearch_matchesStructuredFields() {
		val warhorse = EquipmentReference.MOUNTS.first { it.item == "Warhorse" }
		val saddle = EquipmentReference.TACK_AND_DRAWN_ITEMS.first { it.item == "Saddle, Military" }
		val airship = EquipmentReference.LARGE_VEHICLES.first { it.ship == "Airship" }

		assertTrue(warhorse.matchesSearchQuery("540 lb."))
		assertTrue(warhorse.matchesSearchQuery("400 GP"))
		assertTrue(saddle.matchesSearchQuery("30 lb."))
		assertTrue(saddle.matchesSearchQuery("20 GP"))
		assertTrue(airship.matchesSearchQuery("8 mph"))
		assertTrue(airship.matchesSearchQuery("40,000 GP"))
	}

	@Test
	fun filterEquipmentTransport_returnsExpectedMatchesForQuery() {
		assertEquals(listOf("Warhorse"), filterEquipmentMounts("400 GP").map { it.item })
		assertEquals(
			listOf("Saddle, Military"),
			filterEquipmentTackAndDrawn("30 lb.").map { it.item })
		assertEquals(listOf("Airship"), filterEquipmentLargeVehicles("8 mph").map { it.ship })
		assertEquals(
			listOf("Galley", "Warship"),
			filterEquipmentLargeVehicles("500").map { it.ship })
		assertTrue(filterEquipmentMounts("Nonexistent Mount Term").isEmpty())
		assertTrue(filterEquipmentTackAndDrawn("Nonexistent Tack Term").isEmpty())
		assertTrue(filterEquipmentLargeVehicles("Nonexistent Vehicle Term").isEmpty())
	}

	@Test
	fun visibleEquipmentTransport_respectsEquipmentSubsectionWhenQueryBlank() {
		assertEquals(
			EquipmentReference.MOUNTS.map { it.item },
			visibleEquipmentMounts("", CharacterCreationSubsection.EQUIPMENT).map { it.item }
		)
		assertEquals(
			EquipmentReference.TACK_AND_DRAWN_ITEMS.map { it.item },
			visibleEquipmentTackAndDrawn("", CharacterCreationSubsection.EQUIPMENT).map { it.item }
		)
		assertEquals(
			EquipmentReference.LARGE_VEHICLES.map { it.ship },
			visibleEquipmentLargeVehicles("", CharacterCreationSubsection.EQUIPMENT).map { it.ship }
		)
		assertTrue(visibleEquipmentMounts("", CharacterCreationSubsection.CLASSES).isEmpty())
		assertTrue(visibleEquipmentTackAndDrawn("", CharacterCreationSubsection.CLASSES).isEmpty())
		assertTrue(visibleEquipmentLargeVehicles("", CharacterCreationSubsection.CLASSES).isEmpty())
	}

	@Test
	fun visibleEquipmentTransport_usesSearchAcrossSubsections() {
		assertEquals(
			listOf("Warhorse"),
			visibleEquipmentMounts(
				"400 GP",
				CharacterCreationSubsection.SPECIES_ORIGIN
			).map { it.item }
		)
		assertEquals(
			listOf("Saddle, Military"),
			visibleEquipmentTackAndDrawn(
				"30 lb.",
				CharacterCreationSubsection.SPECIES_ORIGIN
			).map { it.item }
		)
		assertEquals(
			listOf("Airship"),
			visibleEquipmentLargeVehicles(
				"8 mph",
				CharacterCreationSubsection.SPECIES_ORIGIN
			).map { it.ship }
		)
	}

	@Test
	fun filterEquipmentTables_keepsSupplementalEquipmentTablesSearchable() {
		assertEquals(
			listOf("Lifestyle Expenses"),
			filterEquipmentTables("Aristocratic").map { it.title }
		)
		assertEquals(
			listOf("Food, Drink, and Lodging"),
			filterEquipmentTables("Inn Stay").map { it.title }
		)
		assertEquals(
			listOf("Hirelings"),
			filterEquipmentTables("Untrained").map { it.title }
		)
	}
}

