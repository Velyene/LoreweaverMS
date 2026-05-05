/*
 * FILE: SessionDetailViewModelTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Class: SessionDetailViewModelTest
 * 2. Value: mainDispatcherRule
 * 3. Function: loadSession_whenSessionMissing_setsErrorAndRetry
 * 4. Value: repository
 * 5. Value: viewModel
 * 6. Function: loadSession_whenLinkedRecordsExist_buildsSummaryAndCampaignMetadata
 * 7. Value: campaign
 * 8. Value: encounter
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.MainDispatcherRule
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterSnapshot
import io.github.velyene.loreweaver.domain.model.SessionRecord
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SessionDetailViewModelTest {

	@get:Rule
	val mainDispatcherRule = MainDispatcherRule()

	@Test
	fun loadSession_whenSessionMissing_setsErrorAndRetry() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createSessionDetailViewModel(repository)

			viewModel.loadSession("missing-session")
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertFalse(isLoading)
				assertEquals(SESSION_NOT_FOUND_MESSAGE, error)
				assertNotNull(onRetry)
				assertNull(session)
			}
		}
	}

	@Test
	fun loadSession_whenLinkedRecordsExist_buildsSummaryAndCampaignMetadata() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val campaign = Campaign(id = "campaign-1", title = "Stormreach")
			val encounter = Encounter(id = "encounter-1", campaignId = campaign.id, name = "Bridge Ambush")
			val hero = CharacterEntry(id = "hero-1", name = "Hero", party = "Adventurers")
			val goblin = CharacterEntry(id = "goblin-1", name = "Goblin", party = "Monsters")
			val session = SessionRecord(
				id = "session-1",
				encounterId = encounter.id,
				title = "Bridge Ambush Recap",
				date = 1234L,
				log = listOf("Goblin takes 3 damage"),
				snapshot = EncounterSnapshot(
					combatants = listOf(
						CombatantState("hero-1", "Hero", 15, 12, 12),
						CombatantState("goblin-1", "Goblin", 10, 0, 7),
					),
					currentTurnIndex = 0,
					currentRound = 3,
				),
				isCompleted = true,
				encounterResult = "VICTORY",
			)
			repository.setCampaigns(listOf(campaign))
			repository.insertEncounter(encounter)
			repository.setCharacters(listOf(hero, goblin))
			repository.insertSessionRecord(session)
			val viewModel = createSessionDetailViewModel(repository)

			viewModel.loadSession(session.id)
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertFalse(isLoading)
				assertEquals(session, this.session)
				assertEquals(encounter.name, encounterName)
				assertEquals(campaign.id, campaignId)
				assertEquals(campaign.title, campaignTitle)
				assertNotNull(summary)
				assertEquals(EncounterResult.VICTORY, summary?.result)
				assertEquals(3, summary?.totalRounds)
				assertEquals(listOf("Goblin takes 3 damage"), summary?.logSummary)
			}
		}
	}

	@Test
	fun loadSession_whenEncounterLinkMissing_fallsBackToSessionTitleWithoutCampaign() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val session = SessionRecord(
				id = "session-detached",
				encounterId = "missing-encounter",
				title = "Detached Session",
				date = 5678L,
				log = listOf("Party retreats"),
				snapshot = EncounterSnapshot(
					combatants = emptyList(),
					currentTurnIndex = 0,
					currentRound = 2,
				),
			)
			repository.insertSessionRecord(session)
			val viewModel = createSessionDetailViewModel(repository)

			viewModel.loadSession(session.id)
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertFalse(isLoading)
				assertEquals(session, this.session)
				assertEquals(session.title, encounterName)
				assertNull(campaignId)
				assertNull(campaignTitle)
				assertNotNull(summary)
				assertEquals(session.title, summary?.encounterName)
			}
		}
	}

	@Test
	fun loadSession_whenSecondLookupFails_clearsPreviouslyLoadedSession() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val session = SessionRecord(
				id = "session-1",
				title = "Bridge Ambush Recap",
				log = listOf("Goblin takes 3 damage"),
			)
			repository.insertSessionRecord(session)
			val viewModel = createSessionDetailViewModel(repository)

			viewModel.loadSession(session.id)
			advanceUntilIdle()
			assertEquals(session, viewModel.uiState.value.session)

			viewModel.loadSession("missing-session")
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertFalse(isLoading)
				assertNull(this.session)
				assertNull(summary)
				assertEquals(SESSION_NOT_FOUND_MESSAGE, error)
			}
		}
	}
}

