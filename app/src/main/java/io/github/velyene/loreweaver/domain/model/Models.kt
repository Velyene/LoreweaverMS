/*
 * FILE: Models.kt
 *
 * TABLE OF CONTENTS:
 * 1. Class: Campaign
 * 2. Value: id
 * 3. Value: title
 * 4. Value: description
 * 5. Value: characterIds
 * 6. Value: noteIds
 * 7. Value: encounterIds
 * 8. Value: settings
 */

/*
 * FILE: Models.kt
 */

package io.github.velyene.loreweaver.domain.model

import java.util.UUID

// ==========================================================
// 1. Core Data Entities (ER Diagram Alignment)
// ==========================================================

/**
 * Groups characters, notes, and encounters into a cohesive adventure.
 */
data class Campaign(
	val id: String = UUID.randomUUID().toString(),
	val title: String,
	val description: String = "",
	val characterIds: List<String> = emptyList(),
	val noteIds: List<String> = emptyList(),
	val encounterIds: List<String> = emptyList(),
	val inventoryState: CampaignInventoryState = CampaignInventoryState(),
	val settings: Map<String, String> = emptyMap() // Campaign Settings
)

enum class InventoryItemType {
	GENERAL,
	EQUIPMENT,
	CONSUMABLE,
	QUEST,
	SPECIAL
}

enum class InventoryItemSource {
	SRD,
	CUSTOM,
	DM_CREATED
}

data class InventoryItem(
	val id: String = UUID.randomUUID().toString(),
	val name: String,
	val itemType: InventoryItemType = InventoryItemType.GENERAL,
	val quantity: Int = 1,
	val valueCp: Int = 0,
	val weight: Double? = null,
	val description: String = "",
	val source: InventoryItemSource = InventoryItemSource.DM_CREATED,
	val assignedOwnerId: String? = null,
	val stackable: Boolean = true,
	val consumable: Boolean = false,
	val specialItem: Boolean = false
)

data class CharacterInventoryState(
	val personalInventory: List<InventoryItem> = emptyList(),
	val equippedItems: List<InventoryItem> = emptyList(),
	val currencyCp: Int = 0,
	val carryingNotes: String = ""
)

data class CampaignInventoryState(
	val partyStash: List<InventoryItem> = emptyList(),
	val unclaimedLoot: List<InventoryItem> = emptyList(),
	val encounterRewardPool: List<InventoryItem> = emptyList(),
	val sharedCurrencyCp: Int = 0
)

/**
 * A prepared or active encounter.
 */
data class Encounter(
	val id: String = UUID.randomUUID().toString(),
	val campaignId: String? = null, // FK to Campaign
	val name: String,
	val notes: String = "",
	val status: EncounterStatus = EncounterStatus.PENDING,
	val currentRound: Int = 1,
	val currentTurnIndex: Int = 0,
	val participants: List<CombatantState> = emptyList(),
	val activeLog: List<String> = emptyList(),
	val activeTrackers: List<TrackerEntity> = emptyList(),
	val rewardTemplate: EncounterRewardTemplate = EncounterRewardTemplate(),
	val generationSettings: EncounterGenerationSettings = EncounterGenerationSettings(),
	val generationDetails: EncounterGenerationDetails? = null
)

enum class EncounterStatus { PENDING, ACTIVE }

/**
 * Represents the state of a specific mechanic (HP, Spell Slots, etc.)
 */
data class TrackerEntity(
	val id: String = UUID.randomUUID().toString(),
	val encounterId: String? = null, // FK to Encounter
	val title: String,
	val counterValue: Int,
	val maxValue: Int = 100,
	val duration: Int? = null, // Optional duration in rounds
	val conditionEffects: List<String> = emptyList()
)

/**
 * A persistent record of a combat session (Tracker State in ERD).
 */
data class SessionRecord(
	val id: String = UUID.randomUUID().toString(),
	val encounterId: String? = null, // FK to Encounter
	val title: String,
	val date: Long = System.currentTimeMillis(),
	val log: List<String> = emptyList(),
	val snapshot: EncounterSnapshot? = null,
	val reuseFlag: Boolean = false, // Tracker Reuse
	val isCompleted: Boolean = false,
	val encounterResult: String? = null,
	val rewards: EncounterRewardSummary? = null,
	val rewardReview: RewardReviewState? = null
)

enum class EncounterDifficultyTarget {
	LOW,
	MODERATE,
	HIGH,
	CUSTOM
}

enum class EncounterGenerationSourceFilter {
	SRD_ONLY,
	CUSTOM_ONLY,
	BOTH
}

data class EncounterGenerationSettings(
	val difficultyTarget: EncounterDifficultyTarget = EncounterDifficultyTarget.MODERATE,
	val customTargetXp: Int? = null,
	val minimumTargetPercent: Int = 100,
	val maximumTargetPercent: Int = 105,
	val allowSingleHighCrEnemy: Boolean = false,
	val maximumEnemyCr: Double? = null,
	val allowDuplicateEnemies: Boolean = true,
	val maximumEnemyQuantity: Int? = null,
	val creatureTypeFilter: String? = null,
	val groupFilter: String? = null,
	val sourceFilter: EncounterGenerationSourceFilter = EncounterGenerationSourceFilter.SRD_ONLY
)

data class EncounterGeneratedEnemy(
	val combatantId: String,
	val name: String,
	val challengeRating: Double,
	val challengeRatingLabel: String,
	val xpValue: Int,
	val hp: Int,
	val initiative: Int,
	val creatureType: String,
	val sourceLabel: String = "SRD 5.2",
	val quantityGroupKey: String = ""
)

data class EncounterGenerationAttempt(
	val attemptNumber: Int,
	val selectedEnemies: List<String> = emptyList(),
	val totalEnemyXp: Int,
	val accepted: Boolean,
	val resultMessage: String
)

data class EncounterPartyPowerEntry(
	val characterId: String,
	val characterName: String,
	val level: Int,
	val budgetXp: Int
)

data class EncounterGenerationDetails(
	val participantLevels: List<Int> = emptyList(),
	val participantCount: Int = 0,
	val targetDifficulty: EncounterDifficultyTarget = EncounterDifficultyTarget.MODERATE,
	val partyPower: Int = 0,
	val minimumTargetXp: Int = 0,
	val maximumTargetXp: Int = 0,
	val appliedMaximumEnemyCr: Double = 0.0,
	val requiresDmReview: Boolean = false,
	val finalTotalEnemyXp: Int = 0,
	val finalVariancePercent: Double = 0.0,
	val partyPowerEntries: List<EncounterPartyPowerEntry> = emptyList(),
	val attempts: List<EncounterGenerationAttempt> = emptyList(),
	val finalEnemies: List<EncounterGeneratedEnemy> = emptyList(),
	val logLines: List<String> = emptyList()
)

data class EncounterRewardTemplate(
	val difficultyTarget: EncounterDifficultyTarget = EncounterDifficultyTarget.MODERATE,
	val customTargetBudgetXp: Int? = null,
	val preloadedCurrencyCp: Int = 0,
	val preloadedLoot: List<String> = emptyList(),
	val specialItemRewards: List<String> = emptyList(),
	val currencyRateCpPerXp: Double = 5.0,
	val economyMultiplier: Double = 1.0
)

data class ParticipantRewardShare(
	val characterId: String,
	val characterName: String,
	val experiencePoints: Int = 0,
	val currencyCp: Int = 0
)

enum class RewardItemDisposition {
	CHARACTER,
	PARTY_STASH,
	UNCLAIMED
}

data class RewardReviewItem(
	val item: InventoryItem,
	val disposition: RewardItemDisposition = RewardItemDisposition.UNCLAIMED,
	val assignedCharacterId: String? = null
)

data class RewardReviewState(
	val currencyPoolCp: Int = 0,
	val useSharedCurrency: Boolean = false,
	val items: List<RewardReviewItem> = emptyList(),
	val applied: Boolean = false,
	val appliedLog: List<String> = emptyList(),
	val lastUpdated: Long = System.currentTimeMillis()
)

data class EncounterRewardSummary(
	val experiencePoints: Int = 0,
	val experiencePerParticipant: Int = 0,
	val experienceRoundingSurplus: Int = 0,
	val participantCount: Int = 0,
	val participantRewards: List<ParticipantRewardShare> = emptyList(),
	val currencyReward: String? = null,
	val currencyPerParticipant: String? = null,
	val totalCurrencyCp: Int = 0,
	val currencyPerParticipantCp: Int = 0,
	val currencyRoundingSurplusCp: Int = 0,
	val itemRewards: List<String> = emptyList(),
	val equipmentRewards: List<String> = emptyList(),
	val skillPoints: Int = 0,
	val storyRewards: List<String> = emptyList(),
	val rewardLog: List<String> = emptyList()
)

/**
 * Creative content or lore snippets (Linked to Campaign or Encounter).
 */
sealed class Note(
	open val id: String,
	open val campaignId: String?,
	open val content: String,
	open val createdAt: Long
) {
	data class General(
		override val id: String = UUID.randomUUID().toString(),
		override val campaignId: String?,
		override val content: String,
		override val createdAt: Long = System.currentTimeMillis()
	) : Note(id, campaignId, content, createdAt)

	data class Lore(
		override val id: String = UUID.randomUUID().toString(),
		override val campaignId: String?,
		override val content: String,
		override val createdAt: Long = System.currentTimeMillis(),
		val historicalEra: String = ""
	) : Note(id, campaignId, content, createdAt)

	data class NPC(
		override val id: String = UUID.randomUUID().toString(),
		override val campaignId: String?,
		override val content: String,
		override val createdAt: Long = System.currentTimeMillis(),
		val faction: String = "",
		val attitude: String = "Neutral"
	) : Note(id, campaignId, content, createdAt)

	data class Location(
		override val id: String = UUID.randomUUID().toString(),
		override val campaignId: String?,
		override val content: String,
		override val createdAt: Long = System.currentTimeMillis(),
		val region: String = ""
	) : Note(id, campaignId, content, createdAt)
}


// ==========================================================
// 2. Helper & Snapshot Models
// ==========================================================

data class EncounterSnapshot(
	val combatants: List<CombatantState>,
	val currentTurnIndex: Int,
	val currentRound: Int
)

/**
 * Represents a condition/status effect on a combatant.
 */
data class Condition(
	val name: String,
	val duration: Int? = null,  // null = indefinite, Int = rounds remaining
	val durationType: DurationType = DurationType.ROUNDS,
	val addedOnRound: Int = 0
)

enum class DurationType {
	ROUNDS,        // "3 rounds remaining"
	ENCOUNTER      // "until end of encounter"
}

data class CombatantState(
	val characterId: String,
	val name: String,
	val initiative: Int,
	val currentHp: Int,
	val maxHp: Int,
	val conditions: List<Condition> = emptyList(),
	val tempHp: Int = 0  // Temporary hit points
)

data class CharacterAction(
	val name: String,
	val attackBonus: Int = 0,
	val damageDice: String = "",
	val isAttack: Boolean = true,
	val notes: String = "",
	val manaCost: Int = 0,
	val staminaCost: Int = 0,
	val spellSlotLevel: Int? = null,
	val resourceName: String? = null,
	val resourceCost: Int = 0,
	val itemName: String? = null
)

