package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.model.CharacterAction
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CharacterResource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterActionHubSupportTest {
	private val character = CharacterEntry(
		name = "Mira",
		type = "Wizard",
		actions = listOf(
			CharacterAction(
				name = "Fire Bolt",
				attackBonus = 6,
				damageDice = "1d10",
				isAttack = true,
				notes = "Reliable ranged cantrip."
			)
		),
		resources = listOf(CharacterResource(name = "Arcane Ward", current = 3, max = 5)),
		spellSlots = mapOf(1 to (2 to 4)),
		spells = listOf("Magic Missile", "Shield"),
		inventory = listOf("Potion of Healing", "Spell Scroll")
	)

	@Test
	fun buildActionHubEntries_actionsSectionIncludesActionsAndResources() {
		val entries = buildActionHubEntries(character, ActionHubSection.ACTIONS)

		assertEquals(2, entries.size)
		assertEquals("Fire Bolt", entries.first().title)
		assertEquals("Attack", entries.first().typeLabel)
		assertEquals("Arcane Ward", entries.last().title)
		assertEquals("Resource", entries.last().typeLabel)
	}

	@Test
	fun buildActionHubEntries_spellsSectionBuildsSlotEntry() {
		val entries = buildActionHubEntries(character, ActionHubSection.SPELLS)

		assertEquals(3, entries.size)
		assertEquals("Level 1 Slot", entries.first().title)
		assertEquals("Spell Slot", entries.first().typeLabel)
		assertEquals("1 level 1 slot", entries.first().costLabel)
		assertEquals("Magic Missile", entries[1].title)
		assertEquals("Spell", entries[1].typeLabel)
	}

	@Test
	fun buildActionHubEntries_itemsSectionInfersBasicItemMetadata() {
		val entries = buildActionHubEntries(character, ActionHubSection.ITEMS)

		assertEquals(2, entries.size)
		assertEquals("Potion of Healing", entries.first().title)
		assertEquals("Consumable", entries.first().typeLabel)
		assertTrue(entries.first().useNotes.contains("healing", ignoreCase = true))
		assertEquals("Spell Scroll", entries.last().title)
		assertEquals("Magic Item", entries.last().typeLabel)
	}
}

