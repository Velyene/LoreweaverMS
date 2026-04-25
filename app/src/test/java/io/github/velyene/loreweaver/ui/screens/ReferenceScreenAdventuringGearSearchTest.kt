package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.EquipmentReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceScreenAdventuringGearSearchTest {
	private companion object {
		const val BACKPACK = "Backpack"
		const val SADDLEBAG = "saddlebag"
	}

	@Test
	fun adventuringGearSearch_matchesStructuredFields() {
		val backpack = EquipmentReference.ADVENTURING_GEAR.first { it.name == BACKPACK }
		val healerKit = EquipmentReference.ADVENTURING_GEAR.first { it.name == "Healerâ€™s Kit" }
		val spellScroll = EquipmentReference.ADVENTURING_GEAR.first { it.name == "Spell Scroll" }

		assertTrue(backpack.matchesSearchQuery("2 GP"))
		assertTrue(backpack.matchesSearchQuery(SADDLEBAG))
		assertTrue(healerKit.matchesSearchQuery("stabilize an Unconscious creature"))
		assertTrue(spellScroll.matchesSearchQuery("magic item"))
		assertTrue(spellScroll.matchesSearchQuery("written language"))
	}

	@Test
	fun filterAdventuringGear_returnsExpectedMatchesForQuery() {
		assertEquals(listOf(BACKPACK), filterAdventuringGear(SADDLEBAG).map { it.name })
		assertEquals(
			listOf("Healerâ€™s Kit"),
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
			listOf(BACKPACK),
			visibleAdventuringGear(
				SADDLEBAG
			).map { it.name }
		)
	}

	@Test
	fun visibleAdventuringGear_usesSearchAcrossSubsections() {
		assertEquals(
			listOf(BACKPACK),
			visibleAdventuringGear(SADDLEBAG).map { it.name }
		)
	}
}
