package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.MainDispatcherRule
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.Note
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
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
	fun selectCampaign_whenCampaignMissing_setsErrorAndRetry() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignDetailViewModel(repository)

			viewModel.selectCampaign("missing")
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertFalse(isLoading)
				assertEquals(expectedString(R.string.campaign_not_found_message), error)
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
}


