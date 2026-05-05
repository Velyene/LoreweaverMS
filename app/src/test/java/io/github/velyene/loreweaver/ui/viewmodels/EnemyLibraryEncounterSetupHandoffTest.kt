package io.github.velyene.loreweaver.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EnemyLibraryEncounterSetupHandoffTest {

	@Test
	fun consume_savedStateDraft_roundTripsAndClearsSavedStateEntry() {
		val savedStateHandle = SavedStateHandle()

		stashEnemyLibraryEncounterSetupDraft(
			savedStateHandle = savedStateHandle,
			listOf(
				StagedEnemyItem(
					key = "goblin",
					catalogKey = "goblin",
					name = "Goblin",
					hp = 7,
					initiative = 2,
					quantity = 2,
				),
				StagedEnemyItem(
					key = "temp:clockwork-hound:18:3",
					name = "Clockwork Hound",
					hp = 18,
					initiative = 3,
					quantity = 1,
					isTemporary = true,
				),
			),
		)

		val firstConsume = consumeEnemyLibraryEncounterSetupDraft(savedStateHandle)
		val secondConsume = consumeEnemyLibraryEncounterSetupDraft(savedStateHandle)

		assertEquals(3, firstConsume.totalEnemyCount)
		assertEquals(1, firstConsume.temporaryEnemyCount)
		assertEquals(listOf("Goblin", "Clockwork Hound"), firstConsume.stagedEnemies.map(StagedEnemyItem::name))
		assertTrue(secondConsume.stagedEnemies.isEmpty())
		assertEquals(0, secondConsume.totalEnemyCount)
		assertEquals(0, secondConsume.temporaryEnemyCount)
	}

	@Test
	fun consume_invalidSavedStateDraft_returnsEmptyDraft() {
		val savedStateHandle = SavedStateHandle(
			mapOf(ENEMY_LIBRARY_SETUP_DRAFT_KEY to "not-json"),
		)

		val consumedDraft = consumeEnemyLibraryEncounterSetupDraft(savedStateHandle)

		assertTrue(consumedDraft.stagedEnemies.isEmpty())
		assertEquals(0, consumedDraft.totalEnemyCount)
		assertEquals(0, consumedDraft.temporaryEnemyCount)
	}
}

