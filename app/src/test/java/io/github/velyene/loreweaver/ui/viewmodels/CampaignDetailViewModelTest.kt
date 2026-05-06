/*
 * FILE: CampaignDetailViewModelTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Class: CampaignDetailViewModelTest
 * 2. Value: CAMPAIGN_ID
 * 3. Value: mainDispatcherRule
 * 4. Function: selectCampaign_whenCampaignMissing_setsNotFoundErrorWithoutRetry
 * 5. Value: repository
 * 6. Value: viewModel
 * 7. Function: selectCampaign_observesNotesForCampaign
 * 8. Value: campaign
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.MainDispatcherRule
import io.github.velyene.loreweaver.ui.util.UiText
import io.github.velyene.loreweaver.ui.util.campaignNotFoundMessage
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.Note
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
class CampaignDetailViewModelTest {

	private companion object {
		const val CAMPAIGN_ID = "campaign-1"
	}

	@get:Rule
	val mainDispatcherRule = MainDispatcherRule()

	@Test
	fun selectCampaign_whenCampaignMissing_setsNotFoundErrorWithoutRetry() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignDetailViewModel(repository)

			viewModel.selectCampaign("missing")
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertFalse(isLoading)
				assertEquals(UiText.DynamicString(campaignNotFoundMessage(fakeAppText)), error)
				assertNotNull(onRetry)
			}
		}
	}

	@Test
	fun selectCampaign_observesNotesForCampaign() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val campaign = Campaign(id = CAMPAIGN_ID, title = "Stormreach")
			val notes = listOf(
				Note.General(campaignId = campaign.id, content = "Watch the harbor."),
				Note.Location(
					campaignId = campaign.id,
					content = "Ancient vault",
					region = "Cliffside"
				)
			)
			repository.setCampaigns(listOf(campaign))
			repository.setNotes(campaign.id, notes)
			val viewModel = createCampaignDetailViewModel(repository)

			viewModel.selectCampaign(campaign.id)
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals(campaign, selectedCampaign)
				assertEquals(notes, this.notes)
				assertFalse(isLoading)
			}
		}
	}

	@Test
	fun selectCampaign_cancelsPreviousCampaignObserversBeforeSubscribingToNewOnes() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val firstCampaign = Campaign(id = CAMPAIGN_ID, title = "Stormreach")
			val secondCampaign = Campaign(id = "campaign-2", title = "Ravenfall")
			val firstNotes = listOf(Note.General(campaignId = firstCampaign.id, content = "Harbor warning"))
			val secondNotes = listOf(Note.General(campaignId = secondCampaign.id, content = "Castle briefing"))
			repository.setCampaigns(listOf(firstCampaign, secondCampaign))
			repository.setNotes(firstCampaign.id, firstNotes)
			repository.setNotes(secondCampaign.id, secondNotes)
			val viewModel = createCampaignDetailViewModel(repository)

			viewModel.selectCampaign(firstCampaign.id)
			advanceUntilIdle()
			assertEquals(firstNotes, viewModel.uiState.value.notes)

			viewModel.selectCampaign(secondCampaign.id)
			advanceUntilIdle()
			assertEquals(secondCampaign, viewModel.uiState.value.selectedCampaign)
			assertEquals(secondNotes, viewModel.uiState.value.notes)

			repository.setNotes(
				firstCampaign.id,
				listOf(Note.General(campaignId = firstCampaign.id, content = "Stale update"))
			)
			advanceUntilIdle()

			assertEquals(secondCampaign, viewModel.uiState.value.selectedCampaign)
			assertEquals(secondNotes, viewModel.uiState.value.notes)
		}
	}

	@Test
	fun selectCampaign_observesEncounterRenameUpdatesInUiState() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val campaign = Campaign(id = CAMPAIGN_ID, title = "Stormreach")
			val encounter = Encounter(id = "encounter-1", campaignId = campaign.id, name = "Bridge Ambush")
			repository.setCampaigns(listOf(campaign))
			repository.insertEncounter(encounter)
			val viewModel = createCampaignDetailViewModel(repository)

			viewModel.selectCampaign(campaign.id)
			advanceUntilIdle()
			assertEquals("Bridge Ambush", viewModel.uiState.value.linkedEncounters.single().name)

			repository.updateEncounter(encounter.copy(name = "Bridge Ambush Revised"))
			advanceUntilIdle()

			assertEquals("Bridge Ambush Revised", viewModel.uiState.value.linkedEncounters.single().name)
		}
	}

	@Test
	fun selectCampaign_removesDeletedEncounterAndItsSessionsFromUiState() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val campaign = Campaign(id = CAMPAIGN_ID, title = "Stormreach")
			val encounter = Encounter(id = "encounter-1", campaignId = campaign.id, name = "Bridge Ambush")
			val session = SessionRecord(
				encounterId = encounter.id,
				title = "Bridge Ambush Recap",
				date = 1234L,
				log = listOf("Hero takes cover")
			)
			repository.setCampaigns(listOf(campaign))
			repository.insertEncounter(encounter)
			repository.insertSessionRecord(session)
			val viewModel = createCampaignDetailViewModel(repository)

			viewModel.selectCampaign(campaign.id)
			advanceUntilIdle()
			assertEquals(listOf(encounter), viewModel.uiState.value.linkedEncounters)
			assertEquals(listOf(session), viewModel.uiState.value.sessions)

			repository.deleteEncounter(encounter)
			advanceUntilIdle()

			assertTrue(viewModel.uiState.value.linkedEncounters.isEmpty())
			assertTrue(viewModel.uiState.value.sessions.isEmpty())
		}
	}
}


