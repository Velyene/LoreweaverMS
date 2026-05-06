/*
 * FILE: EncounterExtendedModels.kt
 *
 * TABLE OF CONTENTS:
 * 1. Encounter Difficulty and Reward Planning Models
 * 2. Encounter Reward Summary and Distribution Models
 * 3. Encounter Generation Models
 * 4. Reward Review Models
 */

package io.github.velyene.loreweaver.domain.model

// ==========================================================
// 1. Encounter Difficulty and Reward Planning Models
// ==========================================================

/**
 * Difficulty target used when planning encounter rewards or generation budgets.
 */
enum class EncounterDifficultyTarget {
	LOW,
	MODERATE,
	HIGH,
	CUSTOM
}

/**
 * DM-configurable template controlling how currency and loot are distributed
 * after a successful encounter.
 */
data class EncounterRewardTemplate(
	val difficultyTarget: EncounterDifficultyTarget = EncounterDifficultyTarget.MODERATE,
	val customTargetBudgetXp: Int? = null,
	val preloadedCurrencyCp: Int = 0,
	val preloadedLoot: List<String> = emptyList(),
	val specialItemRewards: List<String> = emptyList(),
	val currencyRateCpPerXp: Double = 5.0,
	val economyMultiplier: Double = 1.0
)

// ==========================================================
// 2. Encounter Reward Summary and Distribution Models
// ==========================================================

data class ParticipantRewardShare(
	val characterId: String,
	val characterName: String,
	val experiencePoints: Int,
	val currencyCp: Int
)

/**
 * Fully calculated reward summary produced after a completed encounter.
 */
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

// ==========================================================
// 3. Encounter Generation Models
// ==========================================================

/**
 * Filter controlling which monster sources are included during generation.
 */
enum class EncounterGenerationSourceFilter {
	SRD_ONLY,
	CUSTOM_ONLY,
	BOTH
}

/**
 * DM-configurable settings used when auto-generating an encounter roster.
 */
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

/**
 * A single enemy entry produced by the encounter generator.
 */
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
	val quantityGroupKey: String = name
)

/**
 * Record of a single generation attempt, kept for diagnostic / DM review.
 */
data class EncounterGenerationAttempt(
	val attemptNumber: Int,
	val selectedEnemies: List<String>,
	val totalEnemyXp: Int,
	val accepted: Boolean,
	val resultMessage: String
)

/**
 * Per-character XP budget entry used during party-power calculations.
 */
data class EncounterPartyPowerEntry(
	val characterId: String,
	val characterName: String,
	val level: Int,
	val budgetXp: Int
)

/**
 * Full details of a completed encounter generation run.
 */
data class EncounterGenerationDetails(
	val participantLevels: List<Int> = emptyList(),
	val participantCount: Int = 0,
	val targetDifficulty: EncounterDifficultyTarget = EncounterDifficultyTarget.MODERATE,
	val partyPower: Int = 0,
	val minimumTargetXp: Int = 0,
	val maximumTargetXp: Int = 0,
	val appliedMaximumEnemyCr: Double = Double.MAX_VALUE,
	val requiresDmReview: Boolean = false,
	val finalTotalEnemyXp: Int = 0,
	val finalVariancePercent: Double = 0.0,
	val partyPowerEntries: List<EncounterPartyPowerEntry> = emptyList(),
	val attempts: List<EncounterGenerationAttempt> = emptyList(),
	val finalEnemies: List<EncounterGeneratedEnemy> = emptyList(),
	val logLines: List<String> = emptyList()
)

// ==========================================================
// 4. Reward Review Models
// ==========================================================

enum class RewardItemDisposition {
	UNCLAIMED,
	CHARACTER,
	PARTY_STASH
}

data class RewardReviewItem(
	val item: InventoryItem,
	val disposition: RewardItemDisposition = RewardItemDisposition.UNCLAIMED,
	val assignedCharacterId: String? = null
)

data class RewardReviewState(
	val currencyPoolCp: Int = 0,
	val items: List<RewardReviewItem> = emptyList(),
	val useSharedCurrency: Boolean = false,
	val lastUpdated: Long = 0L,
	val applied: Boolean = false,
	val appliedLog: List<String> = emptyList()
)
