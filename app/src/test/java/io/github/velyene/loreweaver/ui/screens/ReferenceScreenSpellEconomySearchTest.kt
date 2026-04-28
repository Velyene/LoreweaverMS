package io.github.velyene.loreweaver.ui.screens

import org.junit.Test

class ReferenceScreenSpellEconomySearchTest {

	@Test
	fun filterEquipmentTables_matchesMagicItemReferenceTables() {
		assertQueryResults(listOf("Magic Item Categories"), "Immovable Rod", ::filterEquipmentTables) { it.title }
		assertQueryResults(listOf("Potion Miscibility"), "4d10 Force damage", ::filterEquipmentTables) { it.title }
		assertQueryResults(listOf("Magic Item Rarities and Values"), "Priceless", ::filterEquipmentTables) { it.title }
		assertQueryResults(listOf("Magic Item Tools"), "nonmagical base item", ::filterEquipmentTables) { it.title }
		assertQueryResults(listOf("Magic Item Crafting Time and Cost"), "250 days", ::filterEquipmentTables) { it.title }
		assertQueryResults(listOf("Sentient Itemâ€™s Communication"), "telepathically", ::filterEquipmentTables) { it.title }
		assertQueryResults(listOf("Sentient Itemâ€™s Senses"), "Darkvision out to 120 feet", ::filterEquipmentTables) { it.title }
		assertQueryResults(listOf("Sentient Itemâ€™s Special Purpose"), "Soulmate Seeker", ::filterEquipmentTables) { it.title }
		assertNoQueryResults("Nonexistent Spell Economy Term", ::filterEquipmentTables)
	}

	@Test
	fun filterAdventuringGear_matchesGenericMagicItemRows() {
		assertQueryResults(listOf("Spell Scroll"), "stored magic", ::filterAdventuringGear) { it.name }
		assertQueryResults(listOf("Potion of Healing"), "Potion Miscibility table", ::filterAdventuringGear) { it.name }
	}
}
