package io.github.velyene.loreweaver.ui.screens

import org.junit.Test

class ReferenceScreenMagicItemSearchTest {

	@Test
	fun filterEquipmentMagicItems_matchesNamesBodiesAndEmbeddedTables() {
		assertQueryResults(
			listOf("Adamantine Armor"),
			"Critical Hit against you becomes a normal hit",
			::filterEquipmentMagicItems
		) { it.name }
		assertQueryResults(
			listOf("Amulet of the Planes"),
			"DC 15 Intelligence (Arcana) check",
			::filterEquipmentMagicItems
		) { it.name }
		assertQueryResults(
			listOf("Apparatus of the Crab"),
			"giant lobster-like vehicle",
			::filterEquipmentMagicItems
		) { it.name }
		assertQueryResults(
			listOf("Bag of Beans"),
			"cloud giantâ€™s castle",
			::filterEquipmentMagicItems
		) { it.name }
		assertQueryResults(
			listOf("Crystal Ball of Telepathy"),
			"cast Suggestion through that sensor",
			::filterEquipmentMagicItems
		) { it.name }
		assertQueryResults(
			listOf("Dragon Orb"),
			"chromatic dragons within 40 miles",
			::filterEquipmentMagicItems
		) { it.name }
		assertQueryResults(
			listOf("Ioun Stone"),
			"Your Proficiency Bonus increases by 1",
			::filterEquipmentMagicItems
		) { it.name }
		assertQueryResults(
			listOf("Portable Hole"),
			"extradimensional hole 10 feet deep",
			::filterEquipmentMagicItems
		) { it.name }
		assertQueryResults(
			listOf("Spell Scroll"),
			"mystical cipher",
			::filterEquipmentMagicItems
		) { it.name }
		assertQueryResults(
			listOf("Wand of Wonder"),
			"oversized butterflies",
			::filterEquipmentMagicItems
		) { it.name }
		assertNoQueryResults("Nonexistent Magic Item Term", ::filterEquipmentMagicItems)
	}

	@Test
	fun visibleEquipmentMagicItems_isSearchOnlyWithinEquipmentSubsection() {
		assertSearchOnlyHiddenWhenBlank(::visibleEquipmentMagicItems)
		assertSearchOnlyVisible(
			listOf("Adamantine Armor"),
			"Critical Hit against you becomes a normal hit",
			::visibleEquipmentMagicItems
		) { it.name }
		assertSearchOnlyVisible(
			listOf("Dragon Orb"),
			"chromatic dragons within 40 miles",
			::visibleEquipmentMagicItems
		) { it.name }
		assertSearchOnlyVisible(
			listOf("Portable Hole"),
			"extradimensional hole 10 feet deep",
			::visibleEquipmentMagicItems
		) { it.name }
	}

	@Test
	fun visibleEquipmentMagicItems_usesSearchAcrossSubsections() {
		assertSearchOnlyVisible(
			listOf("Amulet of the Planes"),
			"DC 15 Intelligence (Arcana) check",
			::visibleEquipmentMagicItems
		) { it.name }
		assertSearchOnlyVisible(
			listOf("Ioun Stone"),
			"Your Proficiency Bonus increases by 1",
			::visibleEquipmentMagicItems
		) { it.name }
		assertSearchOnlyVisible(listOf("Spell Scroll"), "mystical cipher", ::visibleEquipmentMagicItems) { it.name }
	}
}
