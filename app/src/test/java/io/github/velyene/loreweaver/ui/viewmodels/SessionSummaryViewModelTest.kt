package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterSnapshot
import io.github.velyene.loreweaver.domain.model.SessionRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionSummaryViewModelTest {

	@Test
	fun buildSessionSummary_derivesVictoryAndPersistentStatuses() {
		val session = SessionRecord(
			encounterId = "encounter-1",
			title = "Bridge Clash",
			log = listOf("Round 1 begins", "Kael used Strike on Goblin for 8 damage."),
			snapshot = EncounterSnapshot(
				combatants = listOf(
					combatant(id = "hero-1", name = "Kael", hp = 14, maxHp = 22, initiative = 16),
					combatant(id = "hero-2", name = "Mira", hp = 9, maxHp = 18, initiative = 12),
					combatant(id = "enemy-1", name = "Goblin", hp = 0, maxHp = 7, initiative = 15)
				),
				currentTurnIndex = 0,
				currentRound = 3
			)
		)
		val encounter = Encounter(
			id = "encounter-1",
			campaignId = "campaign-1",
			name = "Bridge Clash",
			notes = "The bridge is slick with rain."
		)
		val campaign = Campaign(id = "campaign-1", title = "Stormroad")
		val characters = listOf(
			CharacterEntry(id = "hero-1", name = "Kael", party = "Adventurers", activeConditions = setOf("Poisoned")),
			CharacterEntry(id = "hero-2", name = "Mira", party = "Adventurers"),
			CharacterEntry(id = "enemy-1", name = "Goblin", party = "Monsters")
		)

		val summary = buildSessionSummary(
			session = session,
			encounter = encounter,
			campaign = campaign,
			characters = characters
		)

		assertEquals(EncounterResult.VICTORY, summary.result)
		assertEquals(3, summary.totalRounds)
		assertEquals(2, summary.survivingPlayers.size)
		assertEquals(1, summary.defeatedEnemies.size)
		assertEquals("Stormroad", summary.campaignTitle)
		assertEquals("The bridge is slick with rain.", summary.notesSummary)
		assertEquals(1, summary.persistentStatuses.size)
		assertTrue(summary.persistentStatuses.single().conditions.contains("Poisoned"))
		assertEquals(2, summary.logSummary.size)
	}

	@Test
	fun buildSessionSummary_derivesEndedEarlyWhenEnemiesStillRemain() {
		val session = SessionRecord(
			encounterId = "encounter-2",
			title = "Ambush",
			snapshot = EncounterSnapshot(
				combatants = listOf(
					combatant(id = "hero-1", name = "Kael", hp = 3, maxHp = 22, initiative = 16),
					combatant(id = "enemy-1", name = "Goblin", hp = 4, maxHp = 7, initiative = 15)
				),
				currentTurnIndex = 0,
				currentRound = 2
			)
		)
		val summary = buildSessionSummary(
			session = session,
			encounter = null,
			campaign = null,
			characters = listOf(
				CharacterEntry(id = "hero-1", name = "Kael", party = "Adventurers")
			)
		)

		assertEquals(EncounterResult.ENDED_EARLY, summary.result)
		assertEquals("Ambush", summary.encounterName)
		assertEquals(1, summary.survivingPlayers.size)
		assertEquals(0, summary.defeatedEnemies.size)
	}

	private fun combatant(
		id: String,
		name: String,
		hp: Int,
		maxHp: Int,
		initiative: Int
	): CombatantState {
		return CombatantState(
			characterId = id,
			name = name,
			initiative = initiative,
			currentHp = hp,
			maxHp = maxHp
		)
	}
}

