package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.EncounterDifficultyTarget
import io.github.velyene.loreweaver.domain.model.EncounterGeneratedEnemy
import io.github.velyene.loreweaver.domain.model.EncounterGenerationDetails
import io.github.velyene.loreweaver.domain.util.CharacterParty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CombatPersistenceSupportTest {

	@Test
	fun buildEncounterSession_persistsStructuredRewardsForVictories() {
		val state = CombatUiState(
			currentEncounterName = "Bridge Clash",
			currentRound = 3,
			currentTurnIndex = 0,
			combatants = listOf(
				combatant(id = "hero-1", name = "Kael", initiative = 16, hp = 14, maxHp = 22),
				combatant(id = "enemy-1", name = "Goblin", initiative = 12, hp = 0, maxHp = 7)
			),
			availableCharacters = listOf(
				CharacterEntry(id = "hero-1", name = "Kael", party = CharacterParty.ADVENTURERS),
				CharacterEntry(id = "enemy-1", name = "Goblin", party = CharacterParty.MONSTERS, challengeRating = 0.25)
			),
			activeStatuses = listOf("Goblin has been defeated")
		)

		val session = buildEncounterSession(
			encounterId = "encounter-1",
			state = state,
			isCompleted = true,
			encounterResult = "VICTORY",
			combatTextProvider = testCombatTextProvider(),
			timestampMillis = 1234L
		)

		assertNotNull(session.rewards)
		assertEquals(50, session.rewards?.experiencePoints)
		assertEquals(50, session.rewards?.experiencePerParticipant)
		assertEquals("2 gp, 5 sp", session.rewards?.currencyReward)
		assertEquals("2 gp, 5 sp", session.rewards?.currencyPerParticipant)
		assertTrue(session.rewards?.itemRewards?.single()?.contains("Goblin") == true)
		assertTrue(session.rewards?.rewardLog?.any { it.contains("Bridge Clash marked as a victory") } == true)
	}

	@Test
	fun buildEncounterRewardSummary_returnsEmptyRewardsWhenEncounterWasNotWon() {
		val rewards = buildEncounterRewardSummary(
			state = CombatUiState(),
			encounterResult = "DEFEAT"
		)

		assertEquals(0, rewards.experiencePoints)
		assertEquals(null, rewards.currencyReward)
		assertTrue(rewards.itemRewards.isEmpty())
		assertTrue(rewards.rewardLog.isEmpty())
	}

	@Test
	fun buildEncounterRewardSummary_usesGeneratedEnemyMetadataWhenRosterHasNoPersistedMonsterEntry() {
		val rewards = buildEncounterRewardSummary(
			state = CombatUiState(
				currentEncounterName = "Generated Clash",
				combatants = listOf(
					combatant(id = "hero-1", name = "Kael", initiative = 16, hp = 14, maxHp = 22),
					combatant(id = "generated-enemy-1", name = "Ghoul", initiative = 12, hp = 0, maxHp = 22)
				),
				availableCharacters = listOf(
					CharacterEntry(id = "hero-1", name = "Kael", party = CharacterParty.ADVENTURERS)
				),
				generationDetails = EncounterGenerationDetails(
					targetDifficulty = EncounterDifficultyTarget.MODERATE,
					finalEnemies = listOf(
						EncounterGeneratedEnemy(
							combatantId = "generated-enemy-1",
							name = "Ghoul",
							challengeRating = 1.0,
							challengeRatingLabel = "1",
							xpValue = 200,
							hp = 22,
							initiative = 2,
							creatureType = "Undead"
						)
					)
				)
			),
			encounterResult = "VICTORY"
		)

		assertEquals(200, rewards.experiencePoints)
		assertEquals(200, rewards.experiencePerParticipant)
	}

	private fun combatant(
		id: String,
		name: String,
		initiative: Int,
		hp: Int,
		maxHp: Int
	): CombatantState {
		return CombatantState(
			characterId = id,
			name = name,
			initiative = initiative,
			currentHp = hp,
			maxHp = maxHp
		)
	}

	private fun testCombatTextProvider(): CombatTextProvider {
		return object : CombatTextProvider {
			override fun roundBeginsStatus(round: Int): String = "Round $round begins"
			override fun quickEncounterName(timestampMillis: Long): String = "Quick Encounter $timestampMillis"
			override fun encounterSessionTitle(timestampMillis: Long): String = "Encounter Session - $timestampMillis"
		}
	}
}

