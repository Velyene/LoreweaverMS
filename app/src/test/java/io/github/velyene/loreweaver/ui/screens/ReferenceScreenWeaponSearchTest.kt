package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.EquipmentReference
import org.junit.Test

class ReferenceScreenWeaponSearchTest {

	@Test
	fun weaponSearch_matchesStructuredWeaponFields() {
		val dagger = EquipmentReference.WEAPONS.first { it.name == "Dagger" }
		val lance = EquipmentReference.WEAPONS.first { it.name == "Lance" }
		val musket = EquipmentReference.WEAPONS.first { it.name == "Musket" }

		assertMatchesAll(dagger::matchesQuery, "Finesse", "Nick")
		assertMatchesAll(lance::matchesQuery, "unless mounted")
		assertMatchesAll(musket::matchesQuery, "500 GP", "Bullet")
	}

	@Test
	fun filterEquipmentWeapons_returnsExpectedMatchesForQuery() {
		assertQueryResults(listOf("Blowgun"), "Needle", ::filterEquipmentWeapons) { it.name }
		assertQueryResults(listOf("Musket"), "500 GP", ::filterEquipmentWeapons) { it.name }
		assertQueryResults(listOf("Lance"), "unless mounted", ::filterEquipmentWeapons) { it.name }
		assertQueryResults(
			listOf("Glaive", "Greatsword"),
			"Graze",
			::filterEquipmentWeapons
		) { it.name }
		assertQueryResults(
			EquipmentReference.WEAPONS.filter { it.category == "Martial Ranged" }.map { it.name },
			"Martial Ranged",
			::filterEquipmentWeapons
		) { it.name }
		assertNoQueryResults("Nonexistent Weapon Term", ::filterEquipmentWeapons)
	}

	@Test
	fun visibleEquipmentWeapons_respectsEquipmentSubsectionWhenQueryBlank() {
		assertVisibleWhenBlankInSubsection(
			EquipmentReference.WEAPONS.map { it.name },
			CharacterCreationSubsection.EQUIPMENT,
			::visibleEquipmentWeapons
		) { it.name }
		assertHiddenWhenBlankInSubsection(CharacterCreationSubsection.CLASSES, ::visibleEquipmentWeapons)
	}

	@Test
	fun visibleEquipmentWeapons_usesSearchAcrossSubsections() {
		assertVisibleForSearch(
			listOf("Blowgun"),
			"Needle",
			CharacterCreationSubsection.SPECIES_ORIGIN,
			::visibleEquipmentWeapons
		) { it.name }
	}
}
