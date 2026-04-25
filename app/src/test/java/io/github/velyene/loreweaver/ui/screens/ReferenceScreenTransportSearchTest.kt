package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.EquipmentReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceScreenTransportSearchTest {
	private companion object {
		const val WARHORSE = "Warhorse"
		const val SADDLE_MILITARY = "Saddle, Military"
		const val AIRSHIP = "Airship"
		const val WARHORSE_QUERY = "400 GP"
		const val SADDLE_QUERY = "30 lb."
		const val AIRSHIP_QUERY = "8 mph"
	}

	@Test
	fun transportSearch_matchesStructuredFields() {
		val warhorse = EquipmentReference.MOUNTS.first { it.item == WARHORSE }
		val saddle = EquipmentReference.TACK_AND_DRAWN_ITEMS.first { it.item == SADDLE_MILITARY }
		val airship = EquipmentReference.LARGE_VEHICLES.first { it.ship == AIRSHIP }

		assertTrue(warhorse.matchesSearchQuery("540 lb."))
		assertTrue(warhorse.matchesSearchQuery(WARHORSE_QUERY))
		assertTrue(saddle.matchesSearchQuery(SADDLE_QUERY))
		assertTrue(saddle.matchesSearchQuery("20 GP"))
		assertTrue(airship.matchesSearchQuery(AIRSHIP_QUERY))
		assertTrue(airship.matchesSearchQuery("40,000 GP"))
	}

	@Test
	fun filterEquipmentTransport_returnsExpectedMatchesForQuery() {
		assertEquals(listOf(WARHORSE), filterEquipmentMounts(WARHORSE_QUERY).map { it.item })
		assertEquals(
			listOf(SADDLE_MILITARY),
			filterEquipmentTackAndDrawn(SADDLE_QUERY).map { it.item })
		assertEquals(listOf(AIRSHIP), filterEquipmentLargeVehicles(AIRSHIP_QUERY).map { it.ship })
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
			listOf(WARHORSE),
			visibleEquipmentMounts(
				WARHORSE_QUERY,
				CharacterCreationSubsection.SPECIES_ORIGIN
			).map { it.item }
		)
		assertEquals(
			listOf(SADDLE_MILITARY),
			visibleEquipmentTackAndDrawn(
				SADDLE_QUERY,
				CharacterCreationSubsection.SPECIES_ORIGIN
			).map { it.item }
		)
		assertEquals(
			listOf(AIRSHIP),
			visibleEquipmentLargeVehicles(
				AIRSHIP_QUERY,
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
