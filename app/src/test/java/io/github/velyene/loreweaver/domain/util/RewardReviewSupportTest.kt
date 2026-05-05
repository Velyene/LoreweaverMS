package io.github.velyene.loreweaver.domain.util

import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.EncounterRewardSummary
import io.github.velyene.loreweaver.domain.model.InventoryItem
import io.github.velyene.loreweaver.domain.model.InventoryItemType
import io.github.velyene.loreweaver.domain.model.ParticipantRewardShare
import io.github.velyene.loreweaver.domain.model.RewardItemDisposition
import io.github.velyene.loreweaver.domain.model.RewardReviewItem
import io.github.velyene.loreweaver.domain.model.RewardReviewState
import io.github.velyene.loreweaver.domain.model.SessionRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RewardReviewSupportTest {

	@Test
	fun buildInitialRewardReview_parsesLootCurrencyAndSpecialItems() {
		val review = buildInitialRewardReview(
			EncounterRewardSummary(
				totalCurrencyCp = 800,
				itemRewards = listOf("Potion ×2", "Shortbow"),
				equipmentRewards = listOf("Old Silver Key")
			)
		)

		assertEquals(800, review?.currencyPoolCp)
		assertEquals(3, review?.items?.size)
		assertEquals(2, review?.items?.first()?.item?.quantity)
		assertTrue(review?.items?.last()?.item?.specialItem == true)
	}

	@Test
	fun applyRewardReview_assignsRewardsToCharactersAndCampaign() {
		val session = SessionRecord(
			title = "Goblin Ambush",
			rewards = EncounterRewardSummary(
				participantRewards = listOf(
					ParticipantRewardShare("hero-1", "Aria", 225, 0),
					ParticipantRewardShare("hero-2", "Bren", 225, 0)
				),
				totalCurrencyCp = 800
			),
			rewardReview = RewardReviewState(
				currencyPoolCp = 800,
				useSharedCurrency = true,
				items = listOf(
					RewardReviewItem(
						item = InventoryItem(name = "Potion", itemType = InventoryItemType.CONSUMABLE, quantity = 2, consumable = true),
						disposition = RewardItemDisposition.CHARACTER,
						assignedCharacterId = "hero-1"
					),
					RewardReviewItem(
						item = InventoryItem(name = "Old Silver Key", itemType = InventoryItemType.SPECIAL, stackable = false, specialItem = true),
						disposition = RewardItemDisposition.PARTY_STASH
					)
				)
			)
		)
		val characters = listOf(
			CharacterEntry(id = "hero-1", name = "Aria"),
			CharacterEntry(id = "hero-2", name = "Bren")
		)
		val campaign = Campaign(id = "campaign-1", title = "Stormroad")

		val result = applyRewardReview(
			session = session,
			review = requireNotNull(session.rewardReview),
			characters = characters,
			campaign = campaign
		)

		assertEquals(225, result.updatedCharacters.first { it.id == "hero-1" }.experiencePoints)
		assertEquals("Potion", result.updatedCharacters.first { it.id == "hero-1" }.inventoryState.personalInventory.single().name)
		assertEquals(800, result.updatedCampaign?.inventoryState?.sharedCurrencyCp)
		assertEquals("Old Silver Key", result.updatedCampaign?.inventoryState?.partyStash?.single()?.name)
		assertTrue(result.updatedSession.rewardReview?.applied == true)
	}
}

