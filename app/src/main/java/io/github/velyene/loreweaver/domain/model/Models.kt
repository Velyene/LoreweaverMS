/*
 * FILE: Models.kt
 *
 * TABLE OF CONTENTS:
 * 1. Core campaign, encounter, session, and note models
 * 2. Snapshot and combat-state helper models
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
	val settings: Map<String, String> = emptyMap(), // Campaign Settings
	val inventoryState: CampaignInventoryState = CampaignInventoryState()
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
	val activeTrackers: List<TrackerEntity> = emptyList(),
	val activeLog: List<String> = emptyList(),
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
	val staminaCost: Int = 0,
	val resourceName: String? = null,
	val resourceCost: Int = 0,
	val spellSlotLevel: Int? = null,
	val manaCost: Int = 0,
	val itemName: String? = null
)

