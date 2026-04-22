package com.example.encountertimer.ui.screens

import com.example.encountertimer.domain.util.EquipmentReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceScreenAdventuringGearSearchTest {

	@Test
	fun adventuringGearSearch_matchesStructuredFields() {
		val backpack = EquipmentReference.ADVENTURING_GEAR.first { it.name == "Backpack" }
		val healerKit = EquipmentReference.ADVENTURING_GEAR.first { it.name == "Healer’s Kit" }
		val spellScroll = EquipmentReference.ADVENTURING_GEAR.first { it.name == "Spell Scroll" }

		assertTrue(backpack.matchesSearchQuery("2 GP"))
		assertTrue(backpack.matchesSearchQuery("saddlebag"))
		assertTrue(healerKit.matchesSearchQuery("stabilize an Unconscious creature"))
		assertTrue(spellScroll.matchesSearchQuery("magic item"))
		assertTrue(spellScroll.matchesSearchQuery("written language"))
	}

	@Test
	fun filterAdventuringGear_returnsExpectedMatchesForQuery() {
		assertEquals(listOf("Backpack"), filterAdventuringGear("saddlebag").map { it.name })
		assertEquals(
			listOf("Healer’s Kit"),
			filterAdventuringGear("stabilize an Unconscious creature").map { it.name })
		assertEquals(
			listOf("Ammunition"),
			filterAdventuringGear("Ammunition property").map { it.name })
		assertEquals(
			listOf("Potion of Healing", "Spell Scroll"),
			filterAdventuringGear("magic item").map { it.name })
		assertTrue(filterAdventuringGear("Nonexistent Gear Term").isEmpty())
	}

	@Test
	fun visibleAdventuringGear_isSearchFocusedWithinEquipmentSubsection() {
		assertTrue(visibleAdventuringGear("").isEmpty())
		assertEquals(
			listOf("Backpack"),
			visibleAdventuringGear(
				"saddlebag"
			).map { it.name }
		)
	}

	@Test
	fun visibleAdventuringGear_usesSearchAcrossSubsections() {
		assertEquals(
			listOf("Backpack"),
			visibleAdventuringGear("saddlebag").map { it.name }
		)
	}
}

