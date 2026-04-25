package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.EquipmentReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceScreenFocusSearchTest {
	private companion object {
		const val HOLY_SYMBOL_RESULTS = "Shield"
		const val HOLY_SYMBOL_QUERY = "Holy Symbol"
		val HOLY_SYMBOL_FOCUSES = listOf("Amulet", "Emblem", "Reliquary")
	}

	@Test
	fun focusSearch_matchesStructuredFields() {
		val crystal = EquipmentReference.FOCUSES.first { it.name == "Crystal" }
		val woodenStaff = EquipmentReference.FOCUSES.first { it.name == "Wooden Staff" }
		val emblem = EquipmentReference.FOCUSES.first { it.name == "Emblem" }

		assertTrue(crystal.matchesSearchQuery("Arcane Focus"))
		assertTrue(crystal.matchesSearchQuery("Wizard"))
		assertTrue(woodenStaff.matchesSearchQuery("Quarterstaff"))
		assertTrue(emblem.matchesSearchQuery(HOLY_SYMBOL_RESULTS))
		assertTrue(emblem.matchesSearchQuery("Cleric or Paladin"))
	}

	@Test
	fun filterEquipmentFocuses_returnsExpectedMatchesForQuery() {
		assertEquals(
			listOf("Crystal", "Orb", "Rod", "Staff", "Wand"),
			filterEquipmentFocuses("Arcane Focus").map { it.name })
		assertEquals(
			listOf("Sprig of mistletoe", "Wooden Staff", "Yew wand"),
			filterEquipmentFocuses("Druid or Ranger").map { it.name })
		assertEquals(
			HOLY_SYMBOL_FOCUSES,
			filterEquipmentFocuses(HOLY_SYMBOL_RESULTS).map { it.name })
		assertEquals(
			HOLY_SYMBOL_FOCUSES,
			filterEquipmentFocuses(HOLY_SYMBOL_QUERY).map { it.name })
		assertTrue(filterEquipmentFocuses("Nonexistent Focus Term").isEmpty())
	}

	@Test
	fun visibleEquipmentFocuses_respectsEquipmentSubsectionWhenQueryBlank() {
		assertEquals(
			EquipmentReference.FOCUSES.map { it.name },
			visibleEquipmentFocuses("", CharacterCreationSubsection.EQUIPMENT).map { it.name }
		)
		assertTrue(visibleEquipmentFocuses("", CharacterCreationSubsection.CLASSES).isEmpty())
	}

	@Test
	fun visibleEquipmentFocuses_usesSearchAcrossSubsections() {
		assertEquals(
			HOLY_SYMBOL_FOCUSES,
			visibleEquipmentFocuses(
				HOLY_SYMBOL_RESULTS,
				CharacterCreationSubsection.SPECIES_ORIGIN
			).map { it.name }
		)
	}
}
