package com.example.encountertimer.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceScreenSpellEconomySearchTest {

	@Test
	fun filterEquipmentTables_matchesMagicItemReferenceTables() {
		assertEquals(
			listOf("Magic Item Categories"),
			filterEquipmentTables("Immovable Rod").map { it.title }
		)
		assertEquals(
			listOf("Potion Miscibility"),
			filterEquipmentTables("4d10 Force damage").map { it.title }
		)
		assertEquals(
			listOf("Magic Item Rarities and Values"),
			filterEquipmentTables("Priceless").map { it.title }
		)
		assertEquals(
			listOf("Magic Item Tools"),
			filterEquipmentTables("nonmagical base item").map { it.title }
		)
		assertEquals(
			listOf("Magic Item Crafting Time and Cost"),
			filterEquipmentTables("250 days").map { it.title }
		)
		assertEquals(
			listOf("Sentient Item’s Communication"),
			filterEquipmentTables("telepathically").map { it.title }
		)
		assertEquals(
			listOf("Sentient Item’s Senses"),
			filterEquipmentTables("Darkvision out to 120 feet").map { it.title }
		)
		assertEquals(
			listOf("Sentient Item’s Special Purpose"),
			filterEquipmentTables("Soulmate Seeker").map { it.title }
		)
		assertTrue(filterEquipmentTables("Nonexistent Spell Economy Term").isEmpty())
	}

	@Test
	fun filterAdventuringGear_matchesGenericMagicItemRows() {
		assertEquals(
			listOf("Spell Scroll"),
			filterAdventuringGear("stored magic").map { it.name }
		)
		assertEquals(
			listOf("Potion of Healing"),
			filterAdventuringGear("Potion Miscibility table").map { it.name }
		)
	}
}

