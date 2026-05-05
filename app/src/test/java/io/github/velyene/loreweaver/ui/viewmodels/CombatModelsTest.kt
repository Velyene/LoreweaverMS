package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.model.CombatantState
import org.junit.Assert.assertEquals
import org.junit.Test

class CombatModelsTest {

	@Test
	fun resolveEncounterStartCombatants_autoRollAddsRollsAndSortsDescending() {
		val resolved = resolveEncounterStartCombatants(
			combatants = listOf(
				CombatantState(characterId = "hero-1", name = "Hero", initiative = 2, currentHp = 12, maxHp = 12),
				CombatantState(characterId = "goblin-1", name = "Goblin", initiative = 1, currentHp = 7, maxHp = 7),
			),
			initiativeMode = InitiativeMode.AUTO_ROLL,
			rollInitiative = sequenceOf(5, 15).iterator()::next,
		)

		assertEquals(listOf("goblin-1", "hero-1"), resolved.map { it.characterId })
		assertEquals(listOf(16, 7), resolved.map { it.initiative })
	}
}

