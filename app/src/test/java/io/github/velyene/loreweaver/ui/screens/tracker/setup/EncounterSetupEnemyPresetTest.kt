package io.github.velyene.loreweaver.ui.screens.tracker.setup

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EncounterSetupEnemyPresetTest {

	@Test
	fun defaultEncounterEnemyPresets_exposesExpectedMvpPresetGroups() {
		val presets = defaultEncounterEnemyPresets()

		assertEquals(
			listOf("Goblin Ambush", "Skeleton Patrol", "Cultist Cell", "Boss + Minions"),
			presets.map(EncounterEnemyPreset::label)
		)
		assertTrue(presets.all { it.entries.isNotEmpty() })
	}

	@Test
	fun applyEncounterEnemyPreset_invokesEnemyInsertionForEachPresetEntry() {
		val applied = mutableListOf<String>()
		val preset = EncounterEnemyPreset(
			label = "Mixed Wave",
			summary = "Test preset",
			entries = listOf(
				EncounterEnemyPresetEntry(name = "Goblin", hp = 7, initiative = 14, quantity = 3),
				EncounterEnemyPresetEntry(name = "Boss", hp = 45, initiative = 16, quantity = 1)
			)
		)

		applyEncounterEnemyPreset(preset) { name, hp, initiative, quantity ->
			applied += "$name|$hp|$initiative|$quantity"
		}

		assertEquals(listOf("Goblin|7|14|3", "Boss|45|16|1"), applied)
	}
}

