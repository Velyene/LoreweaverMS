package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.EquipmentReference
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

		assertMatchesAll(leatherArmor::matchesSearchQuery, LIGHT, "11 + Dex modifier")
		assertMatchesAll(chainMail::matchesSearchQuery, "Str 13", "Disadvantage")
		assertMatchesAll(shield::matchesSearchQuery, UTILIZE_ACTION)
	}

	@Test
	fun filterEquipmentArmor_returnsExpectedMatchesForQuery() {
		assertQueryResults(listOf(CHAIN_MAIL), "Str 13", ::filterEquipmentArmor) { it.name }
		assertQueryResults(listOf(SHIELD), UTILIZE_ACTION, ::filterEquipmentArmor) { it.name }
		assertQueryResults(
			EquipmentReference.ARMOR.filter { it.categoryDonDoff.startsWith(LIGHT) }
				.map { it.name },
			LIGHT,
			::filterEquipmentArmor
		) { it.name }
		assertQueryResults(
			listOf(
				"Padded Armor",
				"Scale Mail",
				"Half Plate Armor",
				"Ring Mail",
				"Chain Mail",
				"Splint Armor",
				"Plate Armor"
			),
			"Disadvantage",
			::filterEquipmentArmor
		) { it.name }
		assertNoQueryResults("Nonexistent Armor Term", ::filterEquipmentArmor)
	}

	@Test
	fun visibleEquipmentArmor_respectsEquipmentSubsectionWhenQueryBlank() {
		assertVisibleWhenBlankInSubsection(
			EquipmentReference.ARMOR.map { it.name },
			CharacterCreationSubsection.EQUIPMENT,
			::visibleEquipmentArmor
		) { it.name }
		assertHiddenWhenBlankInSubsection(CharacterCreationSubsection.CLASSES, ::visibleEquipmentArmor)
	}

	@Test
	fun visibleEquipmentArmor_usesSearchAcrossSubsections() {
		assertVisibleForSearch(
			listOf(SHIELD),
			UTILIZE_ACTION,
			CharacterCreationSubsection.SPECIES_ORIGIN,
			::visibleEquipmentArmor
		) { it.name }
	}
}
