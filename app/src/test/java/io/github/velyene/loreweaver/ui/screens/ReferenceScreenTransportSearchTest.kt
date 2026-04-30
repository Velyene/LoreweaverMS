package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.EquipmentReference
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

		assertMatchesAll(warhorse::matchesSearchQuery, "540 lb.", WARHORSE_QUERY)
		assertMatchesAll(saddle::matchesSearchQuery, SADDLE_QUERY, "20 GP")
		assertMatchesAll(airship::matchesSearchQuery, AIRSHIP_QUERY, "40,000 GP")
	}

	@Test
	fun filterEquipmentTransport_returnsExpectedMatchesForQuery() {
		assertQueryResults(listOf(WARHORSE), WARHORSE_QUERY, ::filterEquipmentMounts) { it.item }
		assertQueryResults(
			listOf(SADDLE_MILITARY),
			SADDLE_QUERY,
			::filterEquipmentTackAndDrawn
		) { it.item }
		assertQueryResults(listOf(AIRSHIP), AIRSHIP_QUERY, ::filterEquipmentLargeVehicles) { it.ship }
		assertQueryResults(
			listOf("Galley", "Warship"),
			"500",
			::filterEquipmentLargeVehicles
		) { it.ship }
		assertNoQueryResults("Nonexistent Mount Term", ::filterEquipmentMounts)
		assertNoQueryResults("Nonexistent Tack Term", ::filterEquipmentTackAndDrawn)
		assertNoQueryResults("Nonexistent Vehicle Term", ::filterEquipmentLargeVehicles)
	}

	@Test
	fun visibleEquipmentTransport_respectsEquipmentSubsectionWhenQueryBlank() {
		assertVisibleWhenBlankInSubsection(
			EquipmentReference.MOUNTS.map { it.item },
			CharacterCreationSubsection.EQUIPMENT,
			::visibleEquipmentMounts
		) { it.item }
		assertVisibleWhenBlankInSubsection(
			EquipmentReference.TACK_AND_DRAWN_ITEMS.map { it.item },
			CharacterCreationSubsection.EQUIPMENT,
			::visibleEquipmentTackAndDrawn
		) { it.item }
		assertVisibleWhenBlankInSubsection(
			EquipmentReference.LARGE_VEHICLES.map { it.ship },
			CharacterCreationSubsection.EQUIPMENT,
			::visibleEquipmentLargeVehicles
		) { it.ship }
		assertHiddenWhenBlankInSubsection(CharacterCreationSubsection.CLASSES, ::visibleEquipmentMounts)
		assertHiddenWhenBlankInSubsection(CharacterCreationSubsection.CLASSES, ::visibleEquipmentTackAndDrawn)
		assertHiddenWhenBlankInSubsection(CharacterCreationSubsection.CLASSES, ::visibleEquipmentLargeVehicles)
	}

	@Test
	fun visibleEquipmentTransport_usesSearchAcrossSubsections() {
		assertVisibleForSearch(
			listOf(WARHORSE),
			WARHORSE_QUERY,
			CharacterCreationSubsection.SPECIES_ORIGIN,
			::visibleEquipmentMounts
		) { it.item }
		assertVisibleForSearch(
			listOf(SADDLE_MILITARY),
			SADDLE_QUERY,
			CharacterCreationSubsection.SPECIES_ORIGIN,
			::visibleEquipmentTackAndDrawn
		) { it.item }
		assertVisibleForSearch(
			listOf(AIRSHIP),
			AIRSHIP_QUERY,
			CharacterCreationSubsection.SPECIES_ORIGIN,
			::visibleEquipmentLargeVehicles
		) { it.ship }
	}

	@Test
	fun filterEquipmentTables_keepsSupplementalEquipmentTablesSearchable() {
		assertQueryResults(
			listOf("Lifestyle Expenses"),
			"Aristocratic",
			::filterEquipmentTables
		) { it.title }
		assertQueryResults(
			listOf("Food, Drink, and Lodging"),
			"Inn Stay",
			::filterEquipmentTables
		) { it.title }
		assertQueryResults(
			listOf("Hirelings"),
			"Untrained",
			::filterEquipmentTables
		) { it.title }
	}
}
