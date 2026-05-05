package io.github.velyene.loreweaver.domain.util

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.EncounterDifficultyTarget
import io.github.velyene.loreweaver.domain.model.EncounterRewardTemplate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EncounterRewardsTest {

	@Test
	fun calculateEncounterRewards_roundsXpSharesUpAndRecordsSurplus() {
		val rewards = calculateEncounterRewards(
			encounterName = "Bridge Clash",
			encounterResult = "VICTORY",
			combatants = listOf(
				combatant("hero-1", "Kael", 18, 24),
				combatant("hero-2", "Mira", 14, 20),
				combatant("hero-3", "Tarin", 10, 18),
				combatant("hero-4", "Vera", 9, 17),
				combatant("enemy-1", "Bandit Captain", 0, 30),
				combatant("enemy-2", "Bandit Captain", 0, 30),
				combatant("enemy-3", "Ogre", 0, 59)
			),
			characters = listOf(
				character("hero-1", "Kael", CharacterParty.ADVENTURERS, level = 3),
				character("hero-2", "Mira", CharacterParty.ADVENTURERS, level = 3),
				character("hero-3", "Tarin", CharacterParty.ADVENTURERS, level = 3),
				character("hero-4", "Vera", CharacterParty.ADVENTURERS, level = 3),
				character("enemy-1", "Bandit Captain", CharacterParty.MONSTERS, challengeRating = 1.0),
				character("enemy-2", "Bandit Captain", CharacterParty.MONSTERS, challengeRating = 1.0),
				character("enemy-3", "Ogre", CharacterParty.MONSTERS, challengeRating = 2.0)
			),
			totalRounds = 4,
			rewardTemplate = EncounterRewardTemplate(
				difficultyTarget = EncounterDifficultyTarget.CUSTOM,
				customTargetBudgetXp = 850,
				currencyRateCpPerXp = 10.0
			)
		)

		assertEquals(850, rewards.experiencePoints)
		assertEquals(213, rewards.experiencePerParticipant)
		assertEquals(2, rewards.experienceRoundingSurplus)
		assertEquals(4, rewards.participantCount)
		assertEquals(8500, rewards.totalCurrencyCp)
		assertEquals(2125, rewards.currencyPerParticipantCp)
		assertEquals("85 gp", rewards.currencyReward)
		assertEquals("21 gp, 2 sp, 5 cp", rewards.currencyPerParticipant)
		assertTrue(rewards.itemRewards.any { it.contains("Bandit Captain") || it.contains("Ogre") })
		assertTrue(rewards.rewardLog.any { it.contains("XP rounding surplus recorded: 2 XP") })
		assertTrue(rewards.rewardLog.any { it.contains("Currency pool: 85 gp") })
	}

	@Test
	fun calculateEncounterRewards_usesPreloadedCurrencyLootAndSpecialItems() {
		val rewards = calculateEncounterRewards(
			encounterName = "Crypt Sweep",
			encounterResult = "VICTORY",
			combatants = listOf(
				combatant("hero-1", "Kael", 12, 24),
				combatant("hero-2", "Mira", 8, 18),
				combatant("enemy-1", "Ghoul", 0, 22)
			),
			characters = listOf(
				character("hero-1", "Kael", CharacterParty.ADVENTURERS, level = 2),
				character("hero-2", "Mira", CharacterParty.ADVENTURERS, level = 2),
				character("enemy-1", "Ghoul", CharacterParty.MONSTERS, challengeRating = 1.0)
			),
			totalRounds = 2,
			rewardTemplate = EncounterRewardTemplate(
				preloadedCurrencyCp = 10100,
				preloadedLoot = listOf("Gem Pouch", "Bone Map"),
				specialItemRewards = listOf("Moonblade Fragment")
			)
		)

		assertEquals(200, rewards.experiencePoints)
		assertEquals(100, rewards.experiencePerParticipant)
		assertEquals("101 gp", rewards.currencyReward)
		assertEquals("50 gp, 5 sp", rewards.currencyPerParticipant)
		assertEquals(listOf("Gem Pouch", "Bone Map"), rewards.itemRewards)
		assertEquals(listOf("Moonblade Fragment"), rewards.equipmentRewards)
		assertTrue(rewards.rewardLog.any { it.contains("Currency pool came from DM-preloaded rewards") })
		assertTrue(rewards.rewardLog.any { it.contains("Special DM-assigned items recorded: Moonblade Fragment") })
	}

	private fun character(
		id: String,
		name: String,
		party: String,
		level: Int = 1,
		challengeRating: Double = 0.0
	): CharacterEntry {
		return CharacterEntry(
			id = id,
			name = name,
			party = party,
			level = level,
			challengeRating = challengeRating,
			hp = 10,
			maxHp = 10,
			ac = 12
		)
	}

	private fun combatant(id: String, name: String, hp: Int, maxHp: Int): CombatantState {
		return CombatantState(
			characterId = id,
			name = name,
			initiative = 10,
			currentHp = hp,
			maxHp = maxHp
		)
	}
}

