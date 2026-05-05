/*
 * FILE: CampaignScreenStateReactionTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Class: CampaignScreenStateReactionTest
 * 2. Value: composeRule
 * 3. Value: context
 * 4. Function: campaignListRoute_closesEditDialogAndShowsUpdatedSnackbarWhenUpdateEventArrives
 * 5. Value: campaign
 * 6. Value: editorUiState
 * 7. Value: clearUpdatedCalls
 * 8. Function: campaignListRoute_withBlockingLoadErrorShowsInlineRetryAndDoesNotClearErrorImmediately
 */

package io.github.velyene.loreweaver.ui.screens

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.ui.theme.LoreweaverTheme
import io.github.velyene.loreweaver.ui.util.UiText
import io.github.velyene.loreweaver.ui.viewmodels.CampaignDetailUiState
import io.github.velyene.loreweaver.ui.viewmodels.CampaignEditorUiState
import io.github.velyene.loreweaver.ui.viewmodels.CampaignListUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CampaignScreenStateReactionTest {

	@get:Rule
	val composeRule = createComposeRule()

	private val context: Context = ApplicationProvider.getApplicationContext()

	@Test
	fun campaignListRoute_closesEditDialogAndShowsUpdatedSnackbarWhenUpdateEventArrives() {
		val campaign = Campaign(id = "campaign-1", title = "Harbor Watch", description = "Before")
		var editorUiState by mutableStateOf(CampaignEditorUiState())
		var clearUpdatedCalls = 0

		composeRule.setContent {
			LoreweaverTheme {
				CampaignListRoute(
					uiState = CampaignListUiState(campaigns = listOf(campaign)),
					editorUiState = editorUiState,
					onBack = {},
					onCampaignClick = {},
					onClearError = {},
					onClearMessage = {},
					onClearUpdatedCampaignId = {
						clearUpdatedCalls++
						editorUiState = editorUiState.copy(updatedCampaignId = null)
					},
					onClearDeletedCampaignId = {},
					onAddCampaign = { _, _ -> },
					onUpdateCampaign = { _, _, _ -> },
					onDeleteCampaign = {},
				)
			}
		}

		composeRule.onNodeWithContentDescription(context.getString(R.string.edit_campaign, campaign.title)).performClick()
		composeRule.onNodeWithText(context.getString(R.string.edit_campaign_title)).assertIsDisplayed()

		composeRule.runOnIdle {
			editorUiState = editorUiState.copy(updatedCampaignId = campaign.id)
		}
		composeRule.waitUntil(timeoutMillis = 5_000) { clearUpdatedCalls == 1 }

		composeRule.onNodeWithText(context.getString(R.string.campaign_updated_message)).assertIsDisplayed()
		composeRule.onAllNodesWithText(context.getString(R.string.edit_campaign_title)).assertCountEquals(0)
		assertEquals(1, clearUpdatedCalls)
	}

	@Test
	fun campaignListRoute_withBlockingLoadErrorShowsInlineRetryAndDoesNotClearErrorImmediately() {
		var retryCalls = 0
		var clearErrorCalls = 0

		composeRule.setContent {
			LoreweaverTheme {
				CampaignListRoute(
					uiState = CampaignListUiState(
						error = UiText.DynamicString("Failed to load campaigns: boom"),
						onRetry = { retryCalls++ },
					),
					editorUiState = CampaignEditorUiState(),
					onBack = {},
					onCampaignClick = {},
					onClearError = { clearErrorCalls++ },
					onClearMessage = {},
					onClearUpdatedCampaignId = {},
					onClearDeletedCampaignId = {},
					onAddCampaign = { _, _ -> },
					onUpdateCampaign = { _, _, _ -> },
					onDeleteCampaign = {},
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.campaigns_load_failed_message)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.retry_action)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.retry_action)).performClick()

		assertEquals(1, retryCalls)
		assertEquals(0, clearErrorCalls)
	}

	@Test
	fun campaignDetailRoute_showsDeletedFeedbackBeforeNavigatingBack() {
		composeRule.mainClock.autoAdvance = false
		val campaign = Campaign(id = "campaign-1", title = "Harbor Watch", description = "Before")
		var backCalls = 0
		var clearDeletedCalls = 0

		composeRule.setContent {
			LoreweaverTheme {
				CampaignDetailRoute(
					campaignId = campaign.id,
					uiState = CampaignDetailUiState(selectedCampaign = campaign),
					editorUiState = CampaignEditorUiState(deletedCampaignId = campaign.id),
					onBack = { backCalls++ },
					onSessionClick = {},
					onEncounterClick = {},
					onSelectCampaign = {},
					onClearError = {},
					onClearMessage = {},
					onClearUpdatedCampaignId = {},
					onClearDeletedCampaignId = { clearDeletedCalls++ },
					onClearUpdatedEncounterId = {},
					onClearDeletedEncounterId = {},
					onAddNote = { _, _, _, _ -> },
					onDeleteNote = {},
					onUpdateNote = {},
					onAddEncounter = { _, _ -> },
					onAddEncounterWithMonsters = { _, _, _ -> },
					onUpdateCampaign = { _, _, _ -> },
					onDeleteCampaign = {},
					onUpdateEncounter = { _, _ -> },
					onDeleteEncounter = {},
					deleteFeedbackDurationMillis = 1_000L,
				)
			}
		}

		composeRule.mainClock.advanceTimeByFrame()
		composeRule.waitForIdle()

		composeRule.onNodeWithText(context.getString(R.string.campaign_deleted_message)).assertIsDisplayed()
		assertEquals(0, backCalls)
		assertEquals(1, clearDeletedCalls)

		composeRule.mainClock.advanceTimeBy(1_000L)
		composeRule.waitForIdle()

		assertEquals(1, backCalls)
 	}

	@Test
	fun campaignDetailRoute_withBlockingLoadErrorShowsInlineRetryAndDoesNotClearErrorImmediately() {
		var retryCalls = 0
		var clearErrorCalls = 0

		composeRule.setContent {
			LoreweaverTheme {
				CampaignDetailRoute(
					campaignId = "campaign-1",
					uiState = CampaignDetailUiState(
						error = UiText.DynamicString("Critical error: boom"),
						onRetry = { retryCalls++ },
					),
					editorUiState = CampaignEditorUiState(),
					onBack = {},
					onSessionClick = {},
					onEncounterClick = {},
					onSelectCampaign = {},
					onClearError = { clearErrorCalls++ },
					onClearMessage = {},
					onClearUpdatedCampaignId = {},
					onClearDeletedCampaignId = {},
					onClearUpdatedEncounterId = {},
					onClearDeletedEncounterId = {},
					onAddNote = { _, _, _, _ -> },
					onDeleteNote = {},
					onUpdateNote = {},
					onAddEncounter = { _, _ -> },
					onAddEncounterWithMonsters = { _, _, _ -> },
					onUpdateCampaign = { _, _, _ -> },
					onDeleteCampaign = {},
					onUpdateEncounter = { _, _ -> },
					onDeleteEncounter = {},
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.campaign_load_failed_message)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.retry_action)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.retry_action)).performClick()

		assertEquals(1, retryCalls)
		assertEquals(0, clearErrorCalls)
	}

	@Test
	fun campaignDetailRoute_withMissingCampaignShowsNotFoundStateWithoutRetry() {
		var clearErrorCalls = 0

		composeRule.setContent {
			LoreweaverTheme {
				CampaignDetailRoute(
					campaignId = "missing",
					uiState = CampaignDetailUiState(
						error = UiText.DynamicString(context.getString(R.string.campaign_not_found_message)),
					),
					editorUiState = CampaignEditorUiState(),
					onBack = {},
					onSessionClick = {},
					onEncounterClick = {},
					onSelectCampaign = {},
					onClearError = { clearErrorCalls++ },
					onClearMessage = {},
					onClearUpdatedCampaignId = {},
					onClearDeletedCampaignId = {},
					onClearUpdatedEncounterId = {},
					onClearDeletedEncounterId = {},
					onAddNote = { _, _, _, _ -> },
					onDeleteNote = {},
					onUpdateNote = {},
					onAddEncounter = { _, _ -> },
					onAddEncounterWithMonsters = { _, _, _ -> },
					onUpdateCampaign = { _, _, _ -> },
					onDeleteCampaign = {},
					onUpdateEncounter = { _, _ -> },
					onDeleteEncounter = {},
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.campaign_not_found_message)).assertIsDisplayed()
		composeRule.onAllNodesWithText(context.getString(R.string.retry_action)).assertCountEquals(0)
		assertEquals(0, clearErrorCalls)
	}

	@Test
	fun campaignDetailRoute_showsEncounterUpdatedFeedbackAndClearsEvent() {
		val campaign = Campaign(id = "campaign-1", title = "Harbor Watch", description = "Before")
		var clearUpdatedEncounterCalls = 0

		composeRule.setContent {
			LoreweaverTheme {
				CampaignDetailRoute(
					campaignId = campaign.id,
					uiState = CampaignDetailUiState(selectedCampaign = campaign),
					editorUiState = CampaignEditorUiState(updatedEncounterId = "encounter-1"),
					onBack = {},
					onSessionClick = {},
					onEncounterClick = {},
					onSelectCampaign = {},
					onClearError = {},
					onClearMessage = {},
					onClearUpdatedCampaignId = {},
					onClearDeletedCampaignId = {},
					onClearUpdatedEncounterId = { clearUpdatedEncounterCalls++ },
					onClearDeletedEncounterId = {},
					onAddNote = { _, _, _, _ -> },
					onDeleteNote = {},
					onUpdateNote = {},
					onAddEncounter = { _, _ -> },
					onAddEncounterWithMonsters = { _, _, _ -> },
					onUpdateCampaign = { _, _, _ -> },
					onDeleteCampaign = {},
					onUpdateEncounter = { _, _ -> },
					onDeleteEncounter = {},
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.encounter_updated_message)).assertIsDisplayed()
		assertEquals(1, clearUpdatedEncounterCalls)
	}

	@Test
	fun campaignDetailRoute_showsEncounterDeletedFeedbackAndClearsEvent() {
		val campaign = Campaign(id = "campaign-1", title = "Harbor Watch", description = "Before")
		var clearDeletedEncounterCalls = 0

		composeRule.setContent {
			LoreweaverTheme {
				CampaignDetailRoute(
					campaignId = campaign.id,
					uiState = CampaignDetailUiState(selectedCampaign = campaign),
					editorUiState = CampaignEditorUiState(deletedEncounterId = "encounter-1"),
					onBack = {},
					onSessionClick = {},
					onEncounterClick = {},
					onSelectCampaign = {},
					onClearError = {},
					onClearMessage = {},
					onClearUpdatedCampaignId = {},
					onClearDeletedCampaignId = {},
					onClearUpdatedEncounterId = {},
					onClearDeletedEncounterId = { clearDeletedEncounterCalls++ },
					onAddNote = { _, _, _, _ -> },
					onDeleteNote = {},
					onUpdateNote = {},
					onAddEncounter = { _, _ -> },
					onAddEncounterWithMonsters = { _, _, _ -> },
					onUpdateCampaign = { _, _, _ -> },
					onDeleteCampaign = {},
					onUpdateEncounter = { _, _ -> },
					onDeleteEncounter = {},
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.encounter_deleted_message)).assertIsDisplayed()
		assertEquals(1, clearDeletedEncounterCalls)
	}
}

