package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.EquipmentReference
import org.junit.Test

class ReferenceScreenAdventuringGearSearchTest {
	private companion object {
		const val BACKPACK = "Backpack"
		const val SADDLEBAG = "saddlebag"
	}

	@Test
	fun adventuringGearSearch_matchesStructuredFields() {
		val backpack = EquipmentReference.ADVENTURING_GEAR.first { it.name == BACKPACK }
		val healerKit = EquipmentReference.ADVENTURING_GEAR.first { it.name == "Healer’s Kit" }
		val spellScroll = EquipmentReference.ADVENTURING_GEAR.first { it.name == "Spell Scroll" }

		assertMatchesAll(backpack::matchesSearchQuery, "2 GP", SADDLEBAG)
		assertMatchesAll(healerKit::matchesSearchQuery, "stabilize an Unconscious creature")
		assertMatchesAll(spellScroll::matchesSearchQuery, "magic item", "written language")
	}

	@Test
	fun filterAdventuringGear_returnsExpectedMatchesForQuery() {
		assertQueryResults(listOf(BACKPACK), SADDLEBAG, ::filterAdventuringGear) { it.name }
		assertQueryResults(
			listOf("Healer’s Kit"),
			"stabilize an Unconscious creature",
			::filterAdventuringGear
		) { it.name }
		assertQueryResults(
			listOf("Ammunition"),
			"Ammunition property",
			::filterAdventuringGear
		) { it.name }
		assertQueryResults(
			listOf("Potion of Healing", "Spell Scroll"),
			"magic item",
			::filterAdventuringGear
		) { it.name }
		assertNoQueryResults("Nonexistent Gear Term", ::filterAdventuringGear)
	}

	@Test
	fun visibleAdventuringGear_isSearchFocusedWithinEquipmentSubsection() {
		assertSearchOnlyHiddenWhenBlank(::visibleAdventuringGear)
		assertSearchOnlyVisible(listOf(BACKPACK), SADDLEBAG, ::visibleAdventuringGear) { it.name }
	}

	@Test
	fun visibleAdventuringGear_usesSearchAcrossSubsections() {
		assertSearchOnlyVisible(listOf(BACKPACK), SADDLEBAG, ::visibleAdventuringGear) { it.name }
	}
}
