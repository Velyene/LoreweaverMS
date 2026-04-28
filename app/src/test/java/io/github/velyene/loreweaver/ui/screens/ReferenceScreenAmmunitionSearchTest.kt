package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.EquipmentReference
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

		assertMatchesAll(arrows::matchesSearchQuery, QUIVER, "20")
		assertMatchesAll(firearmBullets::matchesSearchQuery, "Pouch", "3 GP")
		assertMatchesAll(needles::matchesSearchQuery, "50")
	}

	@Test
	fun filterEquipmentAmmunition_returnsExpectedMatchesForQuery() {
		assertQueryResults(listOf(ARROWS), QUIVER, ::filterEquipmentAmmunition) { it.type }
		assertQueryResults(
			listOf("Bullets, Firearm", "Bullets, Sling", "Needles"),
			"Pouch",
			::filterEquipmentAmmunition
		) { it.type }
		assertQueryResults(listOf(NEEDLES), "50", ::filterEquipmentAmmunition) { it.type }
		assertNoQueryResults("Nonexistent Ammunition Term", ::filterEquipmentAmmunition)
	}

	@Test
	fun visibleEquipmentAmmunition_respectsEquipmentSubsectionWhenQueryBlank() {
		assertVisibleWhenBlankInSubsection(
			EquipmentReference.AMMUNITION.map { it.type },
			CharacterCreationSubsection.EQUIPMENT,
			::visibleEquipmentAmmunition
		) { it.type }
		assertHiddenWhenBlankInSubsection(CharacterCreationSubsection.CLASSES, ::visibleEquipmentAmmunition)
	}

	@Test
	fun visibleEquipmentAmmunition_usesSearchAcrossSubsections() {
		assertVisibleForSearch(
			listOf(ARROWS),
			QUIVER,
			CharacterCreationSubsection.SPECIES_ORIGIN,
			::visibleEquipmentAmmunition
		) { it.type }
	}
}
