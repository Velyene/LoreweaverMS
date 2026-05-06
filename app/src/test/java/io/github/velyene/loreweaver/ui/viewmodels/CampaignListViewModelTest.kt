/*
 * FILE: CampaignListViewModelTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Class: CampaignListViewModelTest
 * 2. Value: mainDispatcherRule
 * 3. Function: init_loadsCampaignsAndSessionsIntoUiState
 * 4. Value: repository
 * 5. Value: campaigns
 * 6. Value: sessions
 * 7. Value: viewModel
 * 8. Function: init_withCompletedSessions_exposesMostRecentCompletedSessionForHomeShortcut
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.MainDispatcherRule
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterSnapshot
import io.github.velyene.loreweaver.domain.model.EncounterStatus
import io.github.velyene.loreweaver.domain.model.SessionRecord
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CampaignListViewModelTest {

	@get:Rule
	val mainDispatcherRule = MainDispatcherRule()

	@Test
	fun init_loadsCampaignsAndSessionsIntoUiState() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val campaigns = listOf(
				Campaign(id = "campaign-1", title = "Stormreach"),
				Campaign(id = "campaign-2", title = "Ravenfall")
			)
			val sessions = listOf(
				SessionRecord(title = "Harbor Skirmish"),
				SessionRecord(title = "Castle Breach")
			)
			repository.setCampaigns(campaigns)
			repository.setAllSessions(sessions)

			val viewModel = createCampaignListViewModel(repository)
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals(campaigns, this.campaigns)
				assertEquals(sessions, this.sessions)
				assertNull(latestCompletedSession)
				assertFalse(isLoading)
			}
		}
	}

	@Test
	fun init_withCompletedSessions_exposesMostRecentCompletedSessionForHomeShortcut() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val completedOlder = SessionRecord(
				id = "session-old",
				title = "Harbor Skirmish",
				date = 100L,
				isCompleted = true,
			)
			val incompleteNewer = SessionRecord(
				id = "session-draft",
				title = "Paused Tracker",
				date = 300L,
				isCompleted = false,
			)
			val completedNewest = SessionRecord(
				id = "session-newest",
				title = "Clocktower Siege",
				date = 200L,
				isCompleted = true,
			)
			repository.setAllSessions(listOf(completedOlder, incompleteNewer, completedNewest))

			val viewModel = createCampaignListViewModel(repository)
			advanceUntilIdle()

			assertEquals(completedNewest, viewModel.uiState.value.latestCompletedSession)
		}
	}

	@Test
	fun init_whenAllSessionsLookupFails_exposesSessionHistoryRetryAndRecoversAfterRetry() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			repository.allSessionsFailure = IllegalStateException("boom")
			val viewModel = createCampaignListViewModel(repository)

			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertFalse(sessionHistoryIsLoading)
				assertTrue(sessions.isEmpty())
				assertNull(latestCompletedSession)
				assertTrue(sessionHistoryOnRetry != null)
			}

			val sessions = listOf(
				SessionRecord(id = "session-1", title = "Harbor Skirmish", date = 100L, isCompleted = true),
			)
			repository.allSessionsFailure = null
			repository.setAllSessions(sessions)

			viewModel.uiState.value.sessionHistoryOnRetry?.invoke()
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertFalse(sessionHistoryIsLoading)
				assertEquals(sessions, this.sessions)
				assertEquals(sessions.single(), latestCompletedSession)
				assertNull(sessionHistoryOnRetry)
			}
		}
	}

	@Test
	fun init_withoutCompletedSessions_keepsLatestCompletedSessionCleared() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			repository.setAllSessions(
				listOf(
					SessionRecord(id = "session-1", title = "Draft One", date = 100L, isCompleted = false),
					SessionRecord(id = "session-2", title = "Draft Two", date = 200L, isCompleted = false),
				)
			)

			val viewModel = createCampaignListViewModel(repository)
			advanceUntilIdle()

			assertNull(viewModel.uiState.value.latestCompletedSession)
		}
	}

	@Test
	fun init_withActiveEncounter_populatesResumeState() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			repository.setActiveEncounterForTest(
				Encounter(
					id = "encounter-1",
					campaignId = "campaign-1",
					name = "Bridge Clash",
					status = EncounterStatus.ACTIVE,
					currentRound = 2
				)
			)
			repository.setRecentSessionForTest(
				SessionRecord(
					encounterId = "encounter-1",
					title = "Bridge Clash",
					snapshot = EncounterSnapshot(
						combatants = emptyList(),
						currentTurnIndex = 0,
						currentRound = 4
					)
				)
			)

			val viewModel = createCampaignListViewModel(repository)
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertTrue(hasActiveEncounter)
				assertEquals("Bridge Clash", activeEncounterName)
				assertEquals(2, activeEncounterRound)
			}
		}
	}

	@Test
	fun init_withoutActiveEncounter_keepsResumeStateCleared() {
		runTest {
			val repository = SplitFakeCampaignRepository()

			val viewModel = createCampaignListViewModel(repository)
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertFalse(hasActiveEncounter)
				assertNull(activeEncounterName)
				assertNull(activeEncounterRound)
			}
		}
	}

	@Test
	fun refreshActiveEncounter_clearsResumeStateAfterActiveEncounterDisappears() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			repository.setActiveEncounterForTest(
				Encounter(
					id = "encounter-1",
					campaignId = "campaign-1",
					name = "Bridge Clash",
					status = EncounterStatus.ACTIVE,
					currentRound = 2
				)
			)

			val viewModel = createCampaignListViewModel(repository)
			advanceUntilIdle()
			assertTrue(viewModel.uiState.value.hasActiveEncounter)

			repository.setActiveEncounterForTest(null)
			viewModel.refreshActiveEncounter()
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertFalse(hasActiveEncounter)
				assertNull(activeEncounterName)
				assertNull(activeEncounterRound)
			}
		}
	}

	@Test
	fun refreshActiveEncounter_updatesResumeSummaryWhenActiveEncounterChanges() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			repository.setActiveEncounterForTest(
				Encounter(
					id = "encounter-1",
					campaignId = "campaign-1",
					name = "Bridge Clash",
					status = EncounterStatus.ACTIVE,
					currentRound = 2
				)
			)
			val viewModel = createCampaignListViewModel(repository)
			advanceUntilIdle()

			repository.setActiveEncounterForTest(
				Encounter(
					id = "encounter-1",
					campaignId = "campaign-1",
					name = "Bridge Clash Revised",
					status = EncounterStatus.ACTIVE,
					currentRound = 5
				)
			)
			repository.setRecentSessionForTest(
				SessionRecord(
					encounterId = "encounter-1",
					title = "Bridge Clash Revised",
					snapshot = EncounterSnapshot(
						combatants = emptyList(),
						currentTurnIndex = 0,
						currentRound = 6
					)
				)
			)

			viewModel.refreshActiveEncounter()
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertTrue(hasActiveEncounter)
				assertEquals("Bridge Clash Revised", activeEncounterName)
				assertEquals(5, activeEncounterRound)
			}
		}
	}

	@Test
	fun init_withNewerSessionFromDifferentEncounter_prefersActiveEncounterSession() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			repository.setActiveEncounterForTest(
				Encounter(
					id = "encounter-active",
					campaignId = "campaign-1",
					name = "Clocktower Siege",
					status = EncounterStatus.ACTIVE,
					currentRound = 2
				)
			)
			repository.insertSessionRecord(
				SessionRecord(
					encounterId = "encounter-active",
					title = "Clocktower Siege",
					date = 100L,
					snapshot = EncounterSnapshot(
						combatants = emptyList(),
						currentTurnIndex = 0,
						currentRound = 3
					)
				)
			)
			repository.insertSessionRecord(
				SessionRecord(
					encounterId = "encounter-other",
					title = "Harbor Ambush",
					date = 200L,
					snapshot = EncounterSnapshot(
						combatants = emptyList(),
						currentTurnIndex = 0,
						currentRound = 9
					)
				)
			)

			val viewModel = createCampaignListViewModel(repository)
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertTrue(hasActiveEncounter)
				assertEquals("Clocktower Siege", activeEncounterName)
				assertEquals(2, activeEncounterRound)
			}
		}
	}
}

