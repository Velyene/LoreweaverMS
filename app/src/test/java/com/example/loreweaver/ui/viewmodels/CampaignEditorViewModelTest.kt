package com.example.loreweaver.ui.viewmodels

import com.example.loreweaver.MainDispatcherRule
import com.example.loreweaver.domain.model.Note
import com.example.loreweaver.ui.util.NOTE_TYPE_LORE
import com.example.loreweaver.ui.util.NOTE_TYPE_NPC
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CampaignEditorViewModelTest {

	private companion object {
		const val CAMPAIGN_ID = "campaign-1"
	}

	@get:Rule
	val mainDispatcherRule = MainDispatcherRule()

	@Test
	fun addNote_createsLoreNoteWithHistoricalEra() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignEditorViewModel(repository)

			viewModel.addNote(
				campaignId = CAMPAIGN_ID,
				content = "The empire fell in ash.",
				type = NOTE_TYPE_LORE,
				extra = "Age of Ruin"
			)
			advanceUntilIdle()

			val insertedNote = repository.insertedNotes.single()
			assertTrue(insertedNote is Note.Lore)
			insertedNote as Note.Lore
			assertEquals(CAMPAIGN_ID, insertedNote.campaignId)
			assertEquals("The empire fell in ash.", insertedNote.content)
			assertEquals("Age of Ruin", insertedNote.historicalEra)
		}
	}

	@Test
	fun addNote_createsNpcNoteAndDefaultsMissingAttitudeToNeutral() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignEditorViewModel(repository)

			viewModel.addNote(
				campaignId = CAMPAIGN_ID,
				content = "Suspicious quartermaster",
				type = NOTE_TYPE_NPC,
				extra = "Iron Circle"
			)
			advanceUntilIdle()

			val insertedNote = repository.insertedNotes.single()
			assertTrue(insertedNote is Note.NPC)
			insertedNote as Note.NPC
			assertEquals("Iron Circle", insertedNote.faction)
			assertEquals("Neutral", insertedNote.attitude)
		}
	}
}

