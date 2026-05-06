/*
 * FILE: SessionSummaryViewModelTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Class: SessionSummaryViewModelTest
 * 2. Value: mainDispatcherRule
 * 3. Function: buildSessionSummary_derivesVictoryAndPersistentStatuses
 * 4. Value: session
 * 5. Value: encounter
 * 6. Value: campaign
 * 7. Value: characters
 * 8. Value: summary
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.MainDispatcherRule
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterRewardSummary
import io.github.velyene.loreweaver.domain.model.EncounterSnapshot
import io.github.velyene.loreweaver.domain.model.SessionRecord
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SessionSummaryViewModelTest {

	@get:Rule
	val mainDispatcherRule = MainDispatcherRule()

	@Test
	fun buildSessionSummary_derivesVictoryAndPersistentStatuses() {
		val session = SessionRecord(
			encounterId = "encounter-1",
			title = "Bridge Clash",
			log = listOf("Round 1 begins", "Kael used Strike on Goblin for 8 damage."),
			isCompleted = true,
			encounterResult = "VICTORY",
			snapshot = EncounterSnapshot(
				combatants = listOf(
					combatant(id = "hero-1", name = "Kael", hp = 14, maxHp = 22, initiative = 16),
					combatant(id = "hero-2", name = "Mira", hp = 9, maxHp = 18, initiative = 12),
					combatant(id = "enemy-1", name = "Goblin", hp = 0, maxHp = 7, initiative = 15)
				),
				currentTurnIndex = 0,
				currentRound = 3
			)
		)
		val encounter = Encounter(
			id = "encounter-1",
			campaignId = "campaign-1",
			name = "Bridge Clash",
			notes = "The bridge is slick with rain."
		)
		val campaign = Campaign(id = "campaign-1", title = "Stormroad")
		val characters = listOf(
			CharacterEntry(id = "hero-1", name = "Kael", party = "Adventurers", activeConditions = setOf("Poisoned")),
			CharacterEntry(id = "hero-2", name = "Mira", party = "Adventurers"),
			CharacterEntry(id = "enemy-1", name = "Goblin", party = "Monsters", challengeRating = 0.25)
		)

		val summary = buildSessionSummary(
			session = session,
			encounter = encounter,
			campaign = campaign,
			characters = characters
		)

		assertEquals(session.id, summary.sessionId)
		assertEquals(EncounterResult.VICTORY, summary.result)
		assertEquals(3, summary.totalRounds)
		assertEquals(2, summary.survivingPlayers.size)
		assertEquals(1, summary.defeatedEnemies.size)
		assertEquals("Stormroad", summary.campaignTitle)
		assertEquals("The bridge is slick with rain.", summary.notesSummary)
		assertEquals(1, summary.persistentStatuses.size)
		assertTrue(summary.persistentStatuses.single().conditions.contains("Poisoned"))
		assertEquals(50, summary.rewards.experiencePoints)
		assertEquals(25, summary.rewards.experiencePerParticipant)
		assertEquals("1 gp, 2 sp, 5 cp", summary.rewards.currencyReward)
		assertEquals("6 sp, 3 cp", summary.rewards.currencyPerParticipant)
		assertEquals(1, summary.rewards.currencyRoundingSurplusCp)
		assertTrue(summary.rewards.itemRewards.any { it.contains("Goblin") })
		assertTrue(summary.rewards.rewardLog.any { it.contains("Bridge Clash marked as a victory") })
		assertEquals(2, summary.logSummary.size)
	}

	@Test
	fun buildSessionSummary_derivesEndedEarlyWhenEnemiesStillRemain() {
		val session = SessionRecord(
			encounterId = "encounter-2",
			title = "Ambush",
			isCompleted = true,
			encounterResult = "ENDED_EARLY",
			snapshot = EncounterSnapshot(
				combatants = listOf(
					combatant(id = "hero-1", name = "Kael", hp = 3, maxHp = 22, initiative = 16),
					combatant(id = "enemy-1", name = "Goblin", hp = 4, maxHp = 7, initiative = 15)
				),
				currentTurnIndex = 0,
				currentRound = 2
			)
		)
		val summary = buildSessionSummary(
			session = session,
			encounter = null,
			campaign = null,
			characters = listOf(
				CharacterEntry(id = "hero-1", name = "Kael", party = "Adventurers")
			)
		)

		assertEquals(EncounterResult.ENDED_EARLY, summary.result)
		assertEquals("Ambush", summary.encounterName)
		assertEquals(1, summary.survivingPlayers.size)
		assertEquals(0, summary.defeatedEnemies.size)
	}

	@Test
	fun buildSessionSummary_prefersStoredEncounterResultWhenPresent() {
		val session = SessionRecord(
			encounterId = "encounter-3",
			title = "Last Stand",
			isCompleted = true,
			encounterResult = "DEFEAT",
			snapshot = EncounterSnapshot(
				combatants = listOf(
					combatant(id = "hero-1", name = "Kael", hp = 6, maxHp = 22, initiative = 16),
					combatant(id = "enemy-1", name = "Goblin", hp = 0, maxHp = 7, initiative = 15)
				),
				currentTurnIndex = 0,
				currentRound = 5
			)
		)

		val summary = buildSessionSummary(
			session = session,
			encounter = null,
			campaign = null,
			characters = listOf(CharacterEntry(id = "hero-1", name = "Kael", party = "Adventurers"))
		)

		assertEquals(EncounterResult.DEFEAT, summary.result)
	}

	@Test
	fun buildSessionSummary_decodesEncounterInfoNotesForDisplay() {
		val session = SessionRecord(
			encounterId = "encounter-encoded",
			title = "Ashen Ruins",
			isCompleted = true,
			snapshot = EncounterSnapshot(
				combatants = listOf(
					combatant(id = "hero-1", name = "Kael", hp = 6, maxHp = 22, initiative = 16),
				),
				currentTurnIndex = 0,
				currentRound = 2,
			),
		)
		val encounter = Encounter(
			id = "encounter-encoded",
			name = "Ashen Ruins",
			notes = encodeEncounterInfo(
				locationTerrain = "Collapsed bridge, low visibility",
				notesBody = "Falling ash obscures the far stairs.",
			),
		)

		val summary = buildSessionSummary(
			session = session,
			encounter = encounter,
			campaign = null,
			characters = listOf(CharacterEntry(id = "hero-1", name = "Kael", party = "Adventurers")),
		)

		assertEquals(
			"Location / Terrain: Collapsed bridge, low visibility\n\nFalling ash obscures the far stairs.",
			summary.notesSummary,
		)
	}

	@Test
	fun buildSessionSummary_prefersPersistedStructuredRewardsWhenPresent() {
		val persistedRewards = EncounterRewardSummary(
			experiencePoints = 999,
			experiencePerParticipant = 333,
			participantCount = 3,
			currencyReward = "42 gp",
			currencyPerParticipant = "14 gp",
			itemRewards = listOf("Moonlit Relic"),
			equipmentRewards = listOf("Runed Shield"),
			skillPoints = 3,
			storyRewards = listOf("Bridge toll abolished"),
			rewardLog = listOf("Kael: 333 XP and 14 gp.")
		)
		val session = SessionRecord(
			encounterId = "encounter-4",
			title = "Bridge Clash",
			isCompleted = true,
			encounterResult = "VICTORY",
			rewards = persistedRewards,
			snapshot = EncounterSnapshot(
				combatants = listOf(
					combatant(id = "hero-1", name = "Kael", hp = 14, maxHp = 22, initiative = 16),
					combatant(id = "enemy-1", name = "Goblin", hp = 0, maxHp = 7, initiative = 15)
				),
				currentTurnIndex = 0,
				currentRound = 3
			)
		)

		val summary = buildSessionSummary(
			session = session,
			encounter = null,
			campaign = null,
			characters = listOf(
				CharacterEntry(id = "hero-1", name = "Kael", party = "Adventurers"),
				CharacterEntry(id = "enemy-1", name = "Goblin", party = "Monsters", challengeRating = 0.25)
			)
		)

		assertEquals(999, summary.rewards.experiencePoints)
		assertEquals(333, summary.rewards.experiencePerParticipant)
		assertEquals(3, summary.rewards.participantCount)
		assertEquals("42 gp", summary.rewards.currencyReward)
		assertEquals("14 gp", summary.rewards.currencyPerParticipant)
		assertEquals(listOf("Moonlit Relic"), summary.rewards.itemRewards)
		assertEquals(listOf("Runed Shield"), summary.rewards.equipmentRewards)
		assertEquals(3, summary.rewards.skillPoints)
		assertEquals(listOf("Bridge toll abolished"), summary.rewards.storyRewards)
		assertEquals(listOf("Kael: 333 XP and 14 gp."), summary.rewards.rewardLog)
	}

	@Test
	fun refreshSummary_withExplicitSessionId_prefersRequestedCompletedSessionOverRecent() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			repository.setAllSessions(
				listOf(
					SessionRecord(
						id = "session-target",
						title = "Target Session",
						isCompleted = true,
						snapshot = EncounterSnapshot(
							combatants = emptyList(),
							currentTurnIndex = 0,
							currentRound = 4,
						),
					),
					SessionRecord(
						id = "session-recent",
						title = "Recent Session",
						isCompleted = true,
						snapshot = EncounterSnapshot(
							combatants = emptyList(),
							currentTurnIndex = 0,
							currentRound = 1,
						),
					),
				)
			)
			repository.setRecentSessionForTest(
				SessionRecord(
					id = "session-recent",
					title = "Recent Session",
					isCompleted = true,
					snapshot = EncounterSnapshot(
						combatants = emptyList(),
						currentTurnIndex = 0,
						currentRound = 1,
					),
				)
			)
			val viewModel = createSessionSummaryViewModel(repository)

			viewModel.refreshSummary(sessionId = "session-target")
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertFalse(isLoading)
				assertEquals("session-target", summary?.sessionId)
				assertEquals("Target Session", summary?.encounterName)
			}
		}
	}

	@Test
	fun refreshSummary_whenRecentSessionLookupFails_exposesRetryAndRecoversAfterRetry() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			repository.recentSessionFailure = IllegalStateException("boom")
			val viewModel = createSessionSummaryViewModel(repository)

			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertFalse(isLoading)
				assertNull(summary)
				assertNull(error)
				assertNotNull(onRetry)
			}

			repository.recentSessionFailure = null
			repository.setRecentSessionForTest(
				SessionRecord(
					id = "session-1",
					title = "Recovered Session",
					isCompleted = true,
					snapshot = EncounterSnapshot(
						combatants = emptyList(),
						currentTurnIndex = 0,
						currentRound = 2,
					),
				)
			)

			viewModel.uiState.value.onRetry?.invoke()
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertFalse(isLoading)
				assertNotNull(summary)
				assertEquals("session-1", summary?.sessionId)
				assertNull(onRetry)
			}
		}
	}

	private fun combatant(
		id: String,
		name: String,
		hp: Int,
		maxHp: Int,
		initiative: Int
	): CombatantState {
		return CombatantState(
			characterId = id,
			name = name,
			initiative = initiative,
			currentHp = hp,
			maxHp = maxHp
		)
	}
}

