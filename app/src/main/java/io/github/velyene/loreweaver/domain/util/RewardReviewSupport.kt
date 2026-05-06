/*
 * FILE: RewardReviewSupport.kt
 *
 * TABLE OF CONTENTS:
 * 1. Reward Review Model Builders
 * 2. Reward Review Item Helpers
 */

package io.github.velyene.loreweaver.domain.util

import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.CampaignInventoryState
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.EncounterRewardSummary
import io.github.velyene.loreweaver.domain.model.InventoryItem
import io.github.velyene.loreweaver.domain.model.InventoryItemSource
import io.github.velyene.loreweaver.domain.model.InventoryItemType
import io.github.velyene.loreweaver.domain.model.RewardItemDisposition
import io.github.velyene.loreweaver.domain.model.RewardReviewItem
import io.github.velyene.loreweaver.domain.model.RewardReviewState
import io.github.velyene.loreweaver.domain.model.SessionRecord
import kotlin.math.ceil

private val quantitySuffixRegex = Regex("""^(.*?)(?:\s*[×x]\s*(\d+))?$""")

data class RewardApplicationResult(
	val updatedSession: SessionRecord,
	val updatedCharacters: List<CharacterEntry>,
	val updatedCampaign: Campaign?
)

fun buildInitialRewardReview(rewards: EncounterRewardSummary?): RewardReviewState? {
	if (rewards == null) return null
	val rewardItems = buildList {
		rewards.itemRewards.forEach { add(parseRewardItem(it, specialItem = false)) }
		rewards.equipmentRewards.forEach { add(parseRewardItem(it, specialItem = true)) }
	}
	if (rewards.totalCurrencyCp <= 0 && rewardItems.isEmpty()) return null
	return RewardReviewState(
		currencyPoolCp = rewards.totalCurrencyCp,
		items = rewardItems.map { item -> RewardReviewItem(item = item) }
	)
}

fun applyRewardReview(
	session: SessionRecord,
	review: RewardReviewState,
	characters: List<CharacterEntry>,
	campaign: Campaign?
): RewardApplicationResult {
	val rewards = session.rewards ?: EncounterRewardSummary()
	val charactersById = characters.associateBy(CharacterEntry::id).toMutableMap()
	val participantIds = rewards.participantRewards.map { it.characterId }
	val participantCount = participantIds.size
	val sharedCurrency = review.useSharedCurrency
	val currencyPerParticipant = if (!sharedCurrency && review.currencyPoolCp > 0 && participantCount > 0) {
		ceil(review.currencyPoolCp.toDouble() / participantCount).toInt()
	} else {
		0
	}
	val appliedLog = mutableListOf<String>()

	rewards.participantRewards.forEach { rewardShare ->
		val character = charactersById[rewardShare.characterId] ?: return@forEach
		val currencyGain = if (sharedCurrency) 0 else currencyPerParticipant
		charactersById[rewardShare.characterId] = character.copy(
			experiencePoints = character.experiencePoints + rewardShare.experiencePoints,
			inventoryState = character.inventoryState.copy(
				currencyCp = character.inventoryState.currencyCp + currencyGain
			)
		)
		appliedLog += buildString {
			append("${rewardShare.characterName} gains ${rewardShare.experiencePoints} XP")
			if (currencyGain > 0) {
				append(" and ${formatCurrencyCp(currencyGain)}")
			}
			append('.')
		}
	}

	var updatedCampaign = campaign
	if (sharedCurrency && review.currencyPoolCp > 0 && updatedCampaign != null) {
		updatedCampaign = updatedCampaign.copy(
			inventoryState = updatedCampaign.inventoryState.copy(
				sharedCurrencyCp = updatedCampaign.inventoryState.sharedCurrencyCp + review.currencyPoolCp
			)
		)
		appliedLog += "Shared currency stored for the campaign: ${formatCurrencyCp(review.currencyPoolCp)}."
	}

	var partyStash = updatedCampaign?.inventoryState?.partyStash.orEmpty()
	var unclaimedLoot = updatedCampaign?.inventoryState?.unclaimedLoot.orEmpty()
	var encounterRewardPool = updatedCampaign?.inventoryState?.encounterRewardPool.orEmpty()

	review.items.forEach { reviewedItem ->
		encounterRewardPool = encounterRewardPool.removeMatchingItem(reviewedItem.item.id)
		when (reviewedItem.disposition) {
			RewardItemDisposition.CHARACTER -> {
				val ownerId = reviewedItem.assignedCharacterId ?: return@forEach
				val character = charactersById[ownerId] ?: return@forEach
				val updatedPersonalInventory = mergeInventoryItem(
					character.personalInventoryItems(),
					reviewedItem.item.copy(assignedOwnerId = ownerId)
				)
				charactersById[ownerId] = character.copy(
					inventory = updatedPersonalInventory.map(InventoryItem::name),
					inventoryState = character.inventoryState.copy(personalInventory = updatedPersonalInventory)
				)
				appliedLog += "Assigned ${reviewedItem.item.quantityLabel()} to ${character.name}."
			}
			RewardItemDisposition.PARTY_STASH -> {
				partyStash = mergeInventoryItem(partyStash, reviewedItem.item)
				appliedLog += "Sent ${reviewedItem.item.quantityLabel()} to the party stash."
			}
			RewardItemDisposition.UNCLAIMED -> {
				unclaimedLoot = mergeInventoryItem(unclaimedLoot, reviewedItem.item)
				appliedLog += "Left ${reviewedItem.item.quantityLabel()} unclaimed."
			}
		}
	}

	if (updatedCampaign != null) {
		updatedCampaign = updatedCampaign.copy(
			inventoryState = CampaignInventoryState(
				partyStash = partyStash,
				unclaimedLoot = unclaimedLoot,
				encounterRewardPool = encounterRewardPool,
				sharedCurrencyCp = updatedCampaign.inventoryState.sharedCurrencyCp
			)
		)
	}

	val updatedRewards = rewards.copy(rewardLog = rewards.rewardLog + appliedLog)
	val updatedReview = review.copy(applied = true, appliedLog = appliedLog)
	return RewardApplicationResult(
		updatedSession = session.copy(rewards = updatedRewards, rewardReview = updatedReview),
		updatedCharacters = characters.map { character -> charactersById[character.id] ?: character },
		updatedCampaign = updatedCampaign
	)
}

fun mergeInventoryItem(items: List<InventoryItem>, item: InventoryItem): List<InventoryItem> {
	val existingIndex = items.indexOfFirst { existing ->
		existing.stackable && item.stackable &&
			existing.name.equals(item.name, ignoreCase = true) &&
			existing.itemType == item.itemType &&
			existing.specialItem == item.specialItem
	}
	if (existingIndex < 0) return items + item
	return items.toMutableList().also { updatedItems ->
		val existing = updatedItems[existingIndex]
		updatedItems[existingIndex] = existing.copy(quantity = existing.quantity + item.quantity)
	}
}

private fun List<InventoryItem>.removeMatchingItem(itemId: String): List<InventoryItem> {
	return filterNot { it.id == itemId }
}

private fun parseRewardItem(rawLabel: String, specialItem: Boolean): InventoryItem {
	val match = quantitySuffixRegex.matchEntire(rawLabel.trim())
	val name = match?.groupValues?.getOrNull(1)?.trim().orEmpty().ifBlank { rawLabel.trim() }
	val quantity = match?.groupValues?.getOrNull(2)?.toIntOrNull() ?: 1
	val normalizedName = name.lowercase()
	val itemType = when {
		specialItem -> InventoryItemType.SPECIAL
		listOf("potion", "elixir", "scroll").any(normalizedName::contains) -> InventoryItemType.CONSUMABLE
		listOf("bow", "blade", "sword", "shield", "armor", "mail", "dagger", "mace", "spear").any(normalizedName::contains) -> InventoryItemType.EQUIPMENT
		listOf("key", "relic", "map", "seal", "token").any(normalizedName::contains) -> InventoryItemType.QUEST
		else -> InventoryItemType.GENERAL
	}
	return InventoryItem(
		name = name,
		itemType = itemType,
		quantity = quantity,
		source = InventoryItemSource.DM_CREATED,
		stackable = itemType != InventoryItemType.SPECIAL,
		consumable = itemType == InventoryItemType.CONSUMABLE,
		specialItem = specialItem
	)
}

private fun InventoryItem.quantityLabel(): String {
	return if (quantity > 1) "$name ×$quantity" else name
}
