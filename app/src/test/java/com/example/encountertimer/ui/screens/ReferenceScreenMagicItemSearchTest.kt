package com.example.encountertimer.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceScreenMagicItemSearchTest {

	@Test
	fun filterEquipmentMagicItems_matchesNamesBodiesAndEmbeddedTables() {
		assertEquals(
			listOf("Adamantine Armor"),
			filterEquipmentMagicItems("Critical Hit against you becomes a normal hit").map { it.name }
		)
		assertEquals(
			listOf("Amulet of the Planes"),
			filterEquipmentMagicItems("DC 15 Intelligence (Arcana) check").map { it.name }
		)
		assertEquals(
			listOf("Apparatus of the Crab"),
			filterEquipmentMagicItems("giant lobster-like vehicle").map { it.name }
		)
		assertEquals(
			listOf("Bag of Beans"),
			filterEquipmentMagicItems("cloud giant’s castle").map { it.name }
		)
		assertEquals(
			listOf("Crystal Ball of Telepathy"),
			filterEquipmentMagicItems("cast Suggestion through that sensor").map { it.name }
		)
		assertEquals(
			listOf("Dragon Orb"),
			filterEquipmentMagicItems("chromatic dragons within 40 miles").map { it.name }
		)
		assertEquals(
			listOf("Ioun Stone"),
			filterEquipmentMagicItems("Your Proficiency Bonus increases by 1").map { it.name }
		)
		assertEquals(
			listOf("Portable Hole"),
			filterEquipmentMagicItems("extradimensional hole 10 feet deep").map { it.name }
		)
		assertEquals(
			listOf("Spell Scroll"),
			filterEquipmentMagicItems("mystical cipher").map { it.name }
		)
		assertEquals(
			listOf("Wand of Wonder"),
			filterEquipmentMagicItems("oversized butterflies").map { it.name }
		)
		assertTrue(filterEquipmentMagicItems("Nonexistent Magic Item Term").isEmpty())
	}

	@Test
	fun visibleEquipmentMagicItems_isSearchOnlyWithinEquipmentSubsection() {
		assertTrue(visibleEquipmentMagicItems("").isEmpty())
		assertEquals(
			listOf("Adamantine Armor"),
			visibleEquipmentMagicItems(
				"Critical Hit against you becomes a normal hit"
			).map { it.name }
		)
		assertEquals(
			listOf("Dragon Orb"),
			visibleEquipmentMagicItems(
				"chromatic dragons within 40 miles"
			).map { it.name }
		)
		assertEquals(
			listOf("Portable Hole"),
			visibleEquipmentMagicItems(
				"extradimensional hole 10 feet deep"
			).map { it.name }
		)
	}

	@Test
	fun visibleEquipmentMagicItems_usesSearchAcrossSubsections() {
		assertEquals(
			listOf("Amulet of the Planes"),
			visibleEquipmentMagicItems(
				"DC 15 Intelligence (Arcana) check"
			).map { it.name }
		)
		assertEquals(
			listOf("Ioun Stone"),
			visibleEquipmentMagicItems(
				"Your Proficiency Bonus increases by 1"
			).map { it.name }
		)
		assertEquals(
			listOf("Spell Scroll"),
			visibleEquipmentMagicItems(
				"mystical cipher"
			).map { it.name }
		)
	}
}

