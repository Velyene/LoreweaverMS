package io.github.velyene.loreweaver.ui.screens.tracker.live

import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Condition
import io.github.velyene.loreweaver.ui.screens.statusChipDisplayText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CombatantConditionsRowTest {

	@Test
	fun buildCombatantStatusChips_marksMergedEncounterAndPersistentConditionAsPersistent() {
		val chips = buildCombatantStatusChips(
			combatant = CombatantState(
				characterId = "hero-1",
				name = "Hero",
				initiative = 14,
				currentHp = 12,
				maxHp = 12,
				conditions = listOf(Condition(name = "Blessed", duration = 3)),
			),
			persistentConditions = setOf("Blessed", "Cursed"),
		)

		assertEquals(listOf("Blessed", "Cursed"), chips.map { it.name })
		assertTrue(chips.first { it.name == "Blessed" }.isPersistent)
		assertEquals(" (3)", chips.first { it.name == "Blessed" }.durationText)
		assertEquals(
			"Blessed (3) • Persistent",
			statusChipDisplayText(chips.first { it.name == "Blessed" })
		)
		assertTrue(chips.first { it.name == "Cursed" }.isPersistent)
	}
}

