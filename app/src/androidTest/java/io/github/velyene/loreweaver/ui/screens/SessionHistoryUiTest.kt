/*
 * FILE: SessionHistoryUiTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Session History Route Empty and Search States
 * 2. Session History Retry and Outcome Rendering
 */

package io.github.velyene.loreweaver.ui.screens

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.ui.theme.LoreweaverTheme
import io.github.velyene.loreweaver.ui.viewmodels.CampaignListUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SessionHistoryUiTest {

	@get:Rule
	val composeRule = createComposeRule()

	private val context: Context = ApplicationProvider.getApplicationContext()

	@Test
	fun sessionHistoryScreenRoute_withoutSessions_showsEmptyHistoryMessage() {
		composeRule.setContent {
			LoreweaverTheme {
				SessionHistoryScreenRoute(
					uiState = CampaignListUiState(),
					onBack = {},
					onSessionClick = {},
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.session_history_empty_message)).assertIsDisplayed()
	}

	@Test
	fun sessionHistoryScreenRoute_withSearchMiss_showsSearchSpecificEmptyMessage() {
		composeRule.setContent {
			LoreweaverTheme {
				SessionHistoryScreenRoute(
					uiState = CampaignListUiState(
						sessions = listOf(
							SessionRecord(id = "session-1", title = "Bridge Ambush"),
						),
					),
					onBack = {},
					onSessionClick = {},
				)
			}
		}

		composeRule
			.onNodeWithText(context.getString(R.string.search_sessions_label))
			.performTextInput("Clocktower")

		composeRule
			.onNodeWithText(context.getString(R.string.no_search_results, "Clocktower"))
			.assertIsDisplayed()
	}

	@Test
	fun sessionHistoryScreenRoute_clearSearchRestoresSessionResults() {
		composeRule.setContent {
			LoreweaverTheme {
				SessionHistoryScreenRoute(
					uiState = CampaignListUiState(
						sessions = listOf(
							SessionRecord(id = "session-1", title = "Bridge Ambush"),
						),
					),
					onBack = {},
					onSessionClick = {},
				)
			}
		}

		composeRule
			.onNodeWithText(context.getString(R.string.search_sessions_label))
			.performTextInput("Clocktower")

		composeRule
			.onNodeWithText(context.getString(R.string.no_search_results, "Clocktower"))
			.assertIsDisplayed()

		composeRule
			.onNodeWithContentDescription(context.getString(R.string.clear_button))
			.assertIsDisplayed()
			.performClick()

		composeRule
			.onAllNodesWithText(context.getString(R.string.no_search_results, "Clocktower"))
			.assertCountEquals(0)

		composeRule.onNodeWithText("Bridge Ambush").assertIsDisplayed()
	}

	@Test
	fun sessionHistoryScreenRoute_retryActionInvokesCallbackWhenProvided() {
		var retryCalls = 0

		composeRule.setContent {
			LoreweaverTheme {
				SessionHistoryScreenRoute(
					uiState = CampaignListUiState(
						sessionHistoryOnRetry = { retryCalls++ },
					),
					onBack = {},
					onSessionClick = {},
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.session_history_load_failed_message)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.retry_action)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.retry_action)).performClick()

		assertEquals(1, retryCalls)
	}

	@Test
	fun sessionHistoryScreenRoute_withEncounterResultShowsOutcomeLabel() {
		composeRule.setContent {
			LoreweaverTheme {
				SessionHistoryScreenRoute(
					uiState = CampaignListUiState(
						sessions = listOf(
							SessionRecord(
								id = "session-1",
								title = "Bridge Ambush",
								encounterResult = "VICTORY",
							),
						),
					),
					onBack = {},
					onSessionClick = {},
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.session_summary_result_victory)).assertIsDisplayed()
	}
}

