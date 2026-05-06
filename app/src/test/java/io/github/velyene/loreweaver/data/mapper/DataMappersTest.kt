/*
 * FILE: DataMappersTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Class: DataMappersTest
 * 2. Function: characterEntityToDomain_normalizesLegacyCustomClassNames
 * 3. Function: characterEntryToEntity_normalizesLegacyCustomClassNames
 * 4. Function: characterEntryToEntity_preservesMonsterTypeAndChallengeRating_forNonAdventurers
 * 5. Value: entity
 * 6. Function: sessionEntityToDomain_readsLegacySnapshotConditionsStoredAsStrings
 * 7. Value: session
 * 8. Value: domain
 */

package io.github.velyene.loreweaver.data.mapper

import io.github.velyene.loreweaver.data.entities.CharacterEntity
import io.github.velyene.loreweaver.data.entities.SessionEntity
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Condition
import io.github.velyene.loreweaver.domain.model.DurationType
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterDifficultyTarget
import io.github.velyene.loreweaver.domain.model.EncounterGeneratedEnemy
import io.github.velyene.loreweaver.domain.model.EncounterGenerationAttempt
import io.github.velyene.loreweaver.domain.model.EncounterGenerationDetails
import io.github.velyene.loreweaver.domain.model.EncounterGenerationSettings
import io.github.velyene.loreweaver.domain.model.EncounterRewardSummary
import io.github.velyene.loreweaver.domain.model.EncounterRewardTemplate
import io.github.velyene.loreweaver.domain.model.EncounterStatus
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.CampaignInventoryState
import io.github.velyene.loreweaver.domain.model.EncounterPartyPowerEntry
import io.github.velyene.loreweaver.domain.model.EncounterGenerationSourceFilter
import io.github.velyene.loreweaver.domain.model.CharacterInventoryState
import io.github.velyene.loreweaver.domain.model.InventoryItem
import io.github.velyene.loreweaver.domain.model.InventoryItemType
import io.github.velyene.loreweaver.domain.model.ParticipantRewardShare
import io.github.velyene.loreweaver.domain.model.RewardItemDisposition
import io.github.velyene.loreweaver.domain.model.RewardReviewItem
import io.github.velyene.loreweaver.domain.model.RewardReviewState
import io.github.velyene.loreweaver.domain.util.CharacterParty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test

class DataMappersTest {

	@Test
	fun characterEntityToDomain_normalizesLegacyCustomClassNames() {
		assertEquals(
			"Fighter",
			CharacterEntity(
				name = "Brute",
				type = "Warrior",
				hp = 12,
				maxHp = 12,
				ac = 13
			).toDomain().type
		)
		assertEquals(
			"Wizard",
			CharacterEntity(
				name = "Caster",
				type = "Mage",
				hp = 8,
				maxHp = 8,
				ac = 12
			).toDomain().type
		)
		assertEquals(
			"Fighter",
			CharacterEntity(
				name = "Monster",
				type = "Enemy",
				hp = 20,
				maxHp = 20,
				ac = 11
			).toDomain().type
		)
	}

	@Test
	fun characterEntryToEntity_normalizesLegacyCustomClassNames() {
		assertEquals("Wizard", CharacterEntry(name = "Caster", type = "Mage").toEntity().type)
		assertEquals("Fighter", CharacterEntry(name = "Monster", type = "Enemy").toEntity().type)
	}

	@Test
	fun characterIdentityFields_roundTripSpeciesBackgroundAndSplitConditions() {
		val entity = CharacterEntry(
			name = "Mira",
			type = "Wizard",
			species = "Elf",
			background = "Sage",
			spells = listOf("Magic Missile", "Shield"),
			persistentConditions = setOf("Blessed"),
			activeConditions = setOf("Poisoned")
		).toEntity()

		assertEquals("Elf", entity.species)
		assertEquals("Sage", entity.background)
		assertEquals(listOf("Magic Missile", "Shield"), entity.spells)
		assertEquals(setOf("Blessed"), entity.persistentConditions)
		assertEquals(setOf("Poisoned"), entity.activeConditions)
		assertEquals("Elf", entity.toDomain().species)
		assertEquals("Sage", entity.toDomain().background)
		assertEquals(listOf("Magic Missile", "Shield"), entity.toDomain().spells)
		assertEquals(setOf("Blessed"), entity.toDomain().persistentConditions)
		assertEquals(setOf("Poisoned"), entity.toDomain().activeConditions)
	}

	@Test
	fun encounterRoundTrip_preservesParticipantsAndActiveLog() {
		val encounter = Encounter(
			id = "encounter-1",
			campaignId = "campaign-1",
			name = "Bridge Clash",
			notes = "Fog covers the bridge.",
			status = EncounterStatus.ACTIVE,
			currentRound = 3,
			currentTurnIndex = 1,
			participants = listOf(
				CombatantState(
					characterId = "hero-1",
					name = "Hero",
					initiative = 15,
					currentHp = 12,
					maxHp = 12,
					tempHp = 5
				),
				CombatantState(
					characterId = "goblin-1",
					name = "Goblin",
					initiative = 10,
					currentHp = 4,
					maxHp = 7,
					conditions = listOf(
						Condition(
							name = "Poisoned",
							duration = 2,
							durationType = DurationType.ROUNDS,
							addedOnRound = 1
						)
					)
				)
			),
			activeLog = listOf(
				"Goblin takes 3 damage (4/7 HP)",
				"Goblin is now Poisoned (2 rounds)"
			),
			rewardTemplate = EncounterRewardTemplate(
				difficultyTarget = EncounterDifficultyTarget.HIGH,
				customTargetBudgetXp = 900,
				preloadedCurrencyCp = 1250,
				preloadedLoot = listOf("Silver torc"),
				specialItemRewards = listOf("Storm-forged Spear")
			),
			generationSettings = EncounterGenerationSettings(
				difficultyTarget = EncounterDifficultyTarget.MODERATE,
				maximumEnemyCr = 3.0,
				allowDuplicateEnemies = false,
				maximumEnemyQuantity = 2,
				creatureTypeFilter = "Undead",
				groupFilter = "Animals",
				sourceFilter = EncounterGenerationSourceFilter.SRD_ONLY
			),
			generationDetails = EncounterGenerationDetails(
				participantLevels = listOf(3, 3, 4, 4),
				participantCount = 4,
				targetDifficulty = EncounterDifficultyTarget.MODERATE,
				partyPower = 900,
				minimumTargetXp = 900,
				maximumTargetXp = 945,
				appliedMaximumEnemyCr = 3.0,
				finalTotalEnemyXp = 900,
				partyPowerEntries = listOf(
					EncounterPartyPowerEntry("hero-1", "Hero", 3, 225)
				),
				attempts = listOf(
					EncounterGenerationAttempt(1, listOf("CR 2 × 1"), 450, false, "Rejected: below target")
				),
				finalEnemies = listOf(
					EncounterGeneratedEnemy(
						combatantId = "enemy-1",
						name = "Ghoul",
						challengeRating = 1.0,
						challengeRatingLabel = "1",
						xpValue = 200,
						hp = 22,
						initiative = 2,
						creatureType = "Undead"
					)
				),
				logLines = listOf("Party power: 900 XP")
			)
		)

		val roundTripped = encounter.toEntity().toDomain()

		assertEquals(encounter.id, roundTripped.id)
		assertEquals(encounter.name, roundTripped.name)
		assertEquals(encounter.notes, roundTripped.notes)
		assertEquals(encounter.status, roundTripped.status)
		assertEquals(encounter.currentRound, roundTripped.currentRound)
		assertEquals(encounter.currentTurnIndex, roundTripped.currentTurnIndex)
		assertEquals(encounter.activeLog, roundTripped.activeLog)
		assertEquals(encounter.participants.size, roundTripped.participants.size)
		assertEquals(5, roundTripped.participants.first().tempHp)
		assertEquals(4, roundTripped.participants.last().currentHp)
		assertEquals("Poisoned", roundTripped.participants.last().conditions.single().name)
		assertEquals(2, roundTripped.participants.last().conditions.single().duration)
		assertEquals(encounter.rewardTemplate.difficultyTarget, roundTripped.rewardTemplate.difficultyTarget)
		assertEquals(encounter.rewardTemplate.customTargetBudgetXp, roundTripped.rewardTemplate.customTargetBudgetXp)
		assertEquals(encounter.rewardTemplate.preloadedCurrencyCp, roundTripped.rewardTemplate.preloadedCurrencyCp)
		assertEquals(encounter.rewardTemplate.preloadedLoot, roundTripped.rewardTemplate.preloadedLoot)
		assertEquals(encounter.rewardTemplate.specialItemRewards, roundTripped.rewardTemplate.specialItemRewards)
		assertEquals(encounter.generationSettings.difficultyTarget, roundTripped.generationSettings.difficultyTarget)
		assertEquals(encounter.generationSettings.maximumEnemyCr, roundTripped.generationSettings.maximumEnemyCr)
		assertEquals(encounter.generationSettings.allowDuplicateEnemies, roundTripped.generationSettings.allowDuplicateEnemies)
		assertEquals(encounter.generationSettings.creatureTypeFilter, roundTripped.generationSettings.creatureTypeFilter)
		assertEquals(encounter.generationDetails?.partyPower, roundTripped.generationDetails?.partyPower)
		assertEquals(encounter.generationDetails?.finalTotalEnemyXp, roundTripped.generationDetails?.finalTotalEnemyXp)
		assertEquals(encounter.generationDetails?.logLines, roundTripped.generationDetails?.logLines)
	}

	@Test
	fun sessionRecordRoundTrip_preservesStructuredRewards() {
		val rewards = EncounterRewardSummary(
			experiencePoints = 150,
			experiencePerParticipant = 75,
			experienceRoundingSurplus = 0,
			participantCount = 2,
			participantRewards = listOf(
				ParticipantRewardShare("hero-1", "Kael", 75, 600),
				ParticipantRewardShare("hero-2", "Mira", 75, 600)
			),
			currencyReward = "12 gp",
			currencyPerParticipant = "6 gp",
			totalCurrencyCp = 1200,
			currencyPerParticipantCp = 600,
			currencyRoundingSurplusCp = 0,
			itemRewards = listOf("Goblin Totem"),
			equipmentRewards = listOf("Recovered shield"),
			skillPoints = 2,
			storyRewards = listOf("Village saved"),
			rewardLog = listOf("Kael: 75 XP and 6 gp.")
		)

		val roundTripped = SessionEntity(
			encounterId = "encounter-1",
			title = "Bridge Clash",
			date = 12345L,
			logJson = "[]",
			snapshotJson = null,
			rewardsJson = com.google.gson.Gson().toJson(rewards)
		).toDomain().toEntity().toDomain()

		assertEquals(rewards.experiencePoints, roundTripped.rewards?.experiencePoints)
		assertEquals(rewards.experiencePerParticipant, roundTripped.rewards?.experiencePerParticipant)
		assertEquals(rewards.participantCount, roundTripped.rewards?.participantCount)
		assertEquals(rewards.participantRewards, roundTripped.rewards?.participantRewards)
		assertEquals(rewards.currencyReward, roundTripped.rewards?.currencyReward)
		assertEquals(rewards.currencyPerParticipant, roundTripped.rewards?.currencyPerParticipant)
		assertEquals(rewards.totalCurrencyCp, roundTripped.rewards?.totalCurrencyCp)
		assertEquals(rewards.currencyPerParticipantCp, roundTripped.rewards?.currencyPerParticipantCp)
		assertEquals(rewards.itemRewards, roundTripped.rewards?.itemRewards)
		assertEquals(rewards.equipmentRewards, roundTripped.rewards?.equipmentRewards)
		assertEquals(rewards.skillPoints, roundTripped.rewards?.skillPoints)
		assertEquals(rewards.storyRewards, roundTripped.rewards?.storyRewards)
		assertEquals(rewards.rewardLog, roundTripped.rewards?.rewardLog)
	}

	@Test
	fun characterAndCampaignRoundTrip_preserveStructuredInventoryState() {
		val character = CharacterEntry(
			id = "hero-1",
			name = "Aria",
			inventory = listOf("Rope"),
			inventoryState = CharacterInventoryState(
				personalInventory = listOf(InventoryItem(name = "Potion", itemType = InventoryItemType.CONSUMABLE, consumable = true, quantity = 2)),
				equippedItems = listOf(InventoryItem(name = "Shield", itemType = InventoryItemType.EQUIPMENT, stackable = false)),
				currencyCp = 225,
				carryingNotes = "Pack mule handles rations."
			),
			experiencePoints = 450
		)
		val campaign = Campaign(
			id = "campaign-1",
			title = "Stormroad",
			inventoryState = CampaignInventoryState(
				partyStash = listOf(InventoryItem(name = "Silver Key", itemType = InventoryItemType.SPECIAL, stackable = false, specialItem = true)),
				unclaimedLoot = listOf(InventoryItem(name = "Shortbow", itemType = InventoryItemType.EQUIPMENT, stackable = false)),
				sharedCurrencyCp = 900
			)
		)

		val mappedCharacter = character.toEntity().toDomain()
		val mappedCampaign = campaign.toEntity().toDomain()

		assertEquals(450, mappedCharacter.experiencePoints)
		assertEquals(225, mappedCharacter.inventoryState.currencyCp)
		assertEquals("Pack mule handles rations.", mappedCharacter.inventoryState.carryingNotes)
		assertEquals("Potion", mappedCharacter.inventoryState.personalInventory.single().name)
		assertEquals("Shield", mappedCharacter.inventoryState.equippedItems.single().name)
		assertEquals("Silver Key", mappedCampaign.inventoryState.partyStash.single().name)
		assertEquals(900, mappedCampaign.inventoryState.sharedCurrencyCp)
	}

	@Test
	fun sessionRecordRoundTrip_preservesRewardReviewState() {
		val rewardReview = RewardReviewState(
			currencyPoolCp = 800,
			useSharedCurrency = true,
			items = listOf(
				RewardReviewItem(
					item = InventoryItem(name = "Old Silver Key", itemType = InventoryItemType.SPECIAL, stackable = false, specialItem = true),
					disposition = RewardItemDisposition.PARTY_STASH
				)
			),
			applied = false
		)
		val session = io.github.velyene.loreweaver.domain.model.SessionRecord(
			id = "session-review-1",
			title = "Goblin Ambush",
			rewards = EncounterRewardSummary(participantRewards = listOf(ParticipantRewardShare("hero-1", "Aria", 225, 200))),
			rewardReview = rewardReview
		)

		val roundTripped = session.toEntity().toDomain()

		assertEquals(800, roundTripped.rewardReview?.currencyPoolCp)
		assertEquals(true, roundTripped.rewardReview?.useSharedCurrency)
		assertEquals(RewardItemDisposition.PARTY_STASH, roundTripped.rewardReview?.items?.single()?.disposition)
		assertEquals("Old Silver Key", roundTripped.rewardReview?.items?.single()?.item?.name)
	}
}
