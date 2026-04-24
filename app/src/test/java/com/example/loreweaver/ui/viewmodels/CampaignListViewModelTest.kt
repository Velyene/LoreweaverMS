package com.example.loreweaver.ui.viewmodels

import com.example.loreweaver.MainDispatcherRule
import com.example.loreweaver.domain.model.Campaign
import com.example.loreweaver.domain.model.SessionRecord
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
				assertFalse(isLoading)
			}
		}
	}
}

