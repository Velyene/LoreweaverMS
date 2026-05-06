/*
 * FILE: InventoryModels.kt
 *
 * TABLE OF CONTENTS:
 * 1. Inventory Item Types and Sources
 * 2. Inventory Item Model
 * 3. Character Inventory State
 * 4. Campaign Inventory State
 */

package io.github.velyene.loreweaver.domain.model

import java.util.UUID

// ==========================================================
// 1. Inventory Item Types and Sources
// ==========================================================

enum class InventoryItemType {
	GENERAL,
	EQUIPMENT,
	CONSUMABLE,
	SPECIAL,
	QUEST
}

enum class InventoryItemSource {
	MANUAL,
	DM_CREATED,
	REWARD,
	PURCHASED,
	FOUND
}

// ==========================================================
// 2. Inventory Item Model
// ==========================================================

data class InventoryItem(
	val id: String = UUID.randomUUID().toString(),
	val name: String = "",
	val itemType: InventoryItemType = InventoryItemType.GENERAL,
	val source: InventoryItemSource = InventoryItemSource.MANUAL,
	val quantity: Int = 1,
	val stackable: Boolean = true,
	val consumable: Boolean = false,
	val specialItem: Boolean = false,
	val assignedOwnerId: String? = null,
	val notes: String = ""
)

// ==========================================================
// 3. Character Inventory State
// ==========================================================

data class CharacterInventoryState(
	val personalInventory: List<InventoryItem> = emptyList(),
	val equippedItems: List<InventoryItem> = emptyList(),
	val currencyCp: Int = 0,
	val carryingNotes: String = ""
)

// ==========================================================
// 4. Campaign Inventory State
// ==========================================================

data class CampaignInventoryState(
	val sharedCurrencyCp: Int = 0,
	val partyStash: List<InventoryItem> = emptyList(),
	val unclaimedLoot: List<InventoryItem> = emptyList(),
	val encounterRewardPool: List<InventoryItem> = emptyList()
)
