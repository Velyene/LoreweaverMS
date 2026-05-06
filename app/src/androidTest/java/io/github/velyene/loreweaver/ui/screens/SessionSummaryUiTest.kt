/*
 * FILE: SessionSummaryUiTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Class: SessionSummaryUiTest
 * 2. Value: composeRule
 * 3. Value: context
 * 4. Function: sessionSummaryContent_openSessionDetailInvokesCallback
 * 5. Value: summary
 * 6. Value: openedSessionId
 * 7. Function: sessionSummaryContent_viewHistoryInvokesCallback
 * 8. Value: historyClicks
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.theme.LoreweaverTheme
import io.github.velyene.loreweaver.ui.viewmodels.EncounterResult
import io.github.velyene.loreweaver.ui.viewmodels.EncounterRewardSummary
import io.github.velyene.loreweaver.ui.viewmodels.SessionSummaryUiModel
import io.github.velyene.loreweaver.ui.viewmodels.SessionSummaryUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SessionSummaryUiTest {

	@get:Rule
	val composeRule = createComposeRule()
	private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

	@Test
	fun sessionSummaryContent_openSessionDetailInvokesCallback() {
		val summary = SessionSummaryUiModel(
			sessionId = "session-1",
			encounterId = "encounter-1",
			encounterName = "Bridge Ambush",
			campaignId = "campaign-1",
			campaignTitle = "Stormreach",
			result = EncounterResult.VICTORY,
			totalRounds = 3,
			survivingPlayers = emptyList(),
			defeatedEnemies = emptyList(),
			persistentStatuses = emptyList(),
			notesSummary = "Fog covers the bridge.",
			logSummary = listOf("Goblin takes 3 damage"),
		)
		var openedSessionId: String? = null

		composeRule.setContent {
			LoreweaverTheme {
				SessionSummaryContent(
					summary = summary,
					onDone = {},
					onOpenAdventureLog = {},
					onContinueCampaign = {},
					onOpenSessionDetail = { openedSessionId = it },
					onStartAnotherEncounter = {},
					onOpenSessionHistory = {},
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.session_summary_badge)).assertIsDisplayed()
		composeRule
			.onNodeWithText(summary.encounterName, useUnmergedTree = true)
			.assertIsDisplayed()
			.assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
		composeRule.onNodeWithText(context.getString(R.string.session_summary_continue_campaign_button)).performScrollTo()
		composeRule.onNodeWithText(context.getString(R.string.session_summary_continue_campaign_button)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.session_detail_open_button)).performScrollTo()
		composeRule.onNodeWithText(context.getString(R.string.session_detail_open_button)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.session_detail_open_button)).performClick()

		assertEquals(summary.sessionId, openedSessionId)
	}

	@Test
	fun sessionSummaryContent_viewHistoryInvokesCallback() {
		val summary = SessionSummaryUiModel(
			sessionId = "session-1",
			encounterId = "encounter-1",
			encounterName = "Bridge Ambush",
			campaignId = "campaign-1",
			campaignTitle = "Stormreach",
			result = EncounterResult.VICTORY,
			totalRounds = 3,
			survivingPlayers = emptyList(),
			defeatedEnemies = emptyList(),
			persistentStatuses = emptyList(),
			notesSummary = "Fog covers the bridge.",
			logSummary = listOf("Goblin takes 3 damage"),
		)
		var historyClicks = 0

		composeRule.setContent {
			LoreweaverTheme {
				SessionSummaryContent(
					summary = summary,
					onDone = {},
					onOpenAdventureLog = {},
					onContinueCampaign = {},
					onOpenSessionDetail = {},
					onStartAnotherEncounter = {},
					onOpenSessionHistory = { historyClicks++ },
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.session_summary_view_history_button)).performScrollTo()
		composeRule.onNodeWithText(context.getString(R.string.session_summary_view_history_button)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.session_summary_view_history_button)).performClick()

		assertEquals(1, historyClicks)
	}

	@Test
	fun sessionSummaryContent_logEntriesPreserveSourceOrder() {
		val firstEntry = "Alpha strikes first"
		val secondEntry = "Bravo answers second"
		val summary = SessionSummaryUiModel(
			sessionId = "session-1",
			encounterId = "encounter-1",
			encounterName = "Bridge Ambush",
			campaignId = "campaign-1",
			campaignTitle = "Stormreach",
			result = EncounterResult.VICTORY,
			totalRounds = 3,
			survivingPlayers = emptyList(),
			defeatedEnemies = emptyList(),
			persistentStatuses = emptyList(),
			notesSummary = "Fog covers the bridge.",
			logSummary = listOf(firstEntry, secondEntry),
		)

		composeRule.setContent {
			LoreweaverTheme {
				SessionSummaryContent(
					summary = summary,
					onDone = {},
					onOpenAdventureLog = {},
					onContinueCampaign = {},
					onOpenSessionDetail = {},
					onStartAnotherEncounter = {},
					onOpenSessionHistory = {},
				)
			}
		}

		val firstBullet = context.getString(R.string.combat_log_bullet, firstEntry)
		val secondBullet = context.getString(R.string.combat_log_bullet, secondEntry)

		composeRule.onNodeWithText(secondBullet).performScrollTo()

		val firstBoundsTop = composeRule.onNodeWithText(firstBullet).fetchSemanticsNode().boundsInRoot.top
		val secondBoundsTop = composeRule.onNodeWithText(secondBullet).fetchSemanticsNode().boundsInRoot.top

		assert(firstBoundsTop < secondBoundsTop) {
			"Expected first log entry to render above second entry, but got tops $firstBoundsTop and $secondBoundsTop"
		}
	}

	@Test
	fun sessionSummaryScreenRoute_retryActionInvokesCallbackWhenProvided() {
		var retryCount = 0

		composeRule.setContent {
			LoreweaverTheme {
				SessionSummaryScreenRoute(
					uiState = SessionSummaryUiState(
						isLoading = false,
						onRetry = { retryCount++ },
					),
					onDone = {},
					onOpenAdventureLog = {},
					onContinueCampaign = {},
					onOpenSessionDetail = {},
					onStartAnotherEncounter = {},
					onOpenSessionHistory = {},
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.session_summary_load_failed_message)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.retry_action)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.retry_action)).performClick()

		assertEquals(1, retryCount)
	}

	@Test
	fun sessionSummaryContent_displaysStructuredRewardsWhenPresent() {
		val summary = SessionSummaryUiModel(
			sessionId = "session-1",
			encounterId = "encounter-1",
			encounterName = "Bridge Ambush",
			campaignId = "campaign-1",
			campaignTitle = "Stormreach",
			result = EncounterResult.VICTORY,
			totalRounds = 3,
			survivingPlayers = emptyList(),
			defeatedEnemies = emptyList(),
			persistentStatuses = emptyList(),
			rewards = EncounterRewardSummary(
				experiencePoints = 50,
				experiencePerParticipant = 25,
				participantCount = 2,
				currencyReward = "2 gp, 5 sp",
				currencyPerParticipant = "1 gp, 2 sp, 5 cp",
				itemRewards = listOf("Loot from Goblin"),
				rewardLog = listOf("Bridge Ambush completed successfully.")
			),
			notesSummary = "Fog covers the bridge.",
			logSummary = listOf("Goblin takes 3 damage"),
		)

		composeRule.setContent {
			LoreweaverTheme {
				SessionSummaryContent(
					summary = summary,
					onDone = {},
					onOpenAdventureLog = {},
					onContinueCampaign = {},
					onOpenSessionDetail = {},
					onStartAnotherEncounter = {},
					onOpenSessionHistory = {},
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.session_summary_rewards_title)).performScrollTo()
		composeRule.onNodeWithText(context.getString(R.string.session_summary_rewards_title)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.session_summary_reward_xp, 50)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.session_summary_reward_xp_share, 2, 25)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.session_summary_reward_currency_pool, "2 gp, 5 sp")).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.session_summary_reward_currency_share, 2, "1 gp, 2 sp, 5 cp")).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.session_summary_reward_log_title)).assertIsDisplayed()
	}

	@Test
	fun sessionSummaryContent_encounterCompletionModeShowsDmEndFlowActions() {
		val summary = SessionSummaryUiModel(
			sessionId = "session-42",
			encounterId = "encounter-1",
			encounterName = "Bridge Ambush",
			campaignId = "campaign-1",
			campaignTitle = "Stormreach",
			result = EncounterResult.VICTORY,
			totalRounds = 3,
			survivingPlayers = emptyList(),
			defeatedEnemies = emptyList(),
			persistentStatuses = emptyList(),
			notesSummary = "Fog covers the bridge.",
			logSummary = listOf("Goblin takes 3 damage"),
		)
		var savedSessionId: String? = null
		var returnHomeClicks = 0
		var newEncounterClicks = 0

		composeRule.setContent {
			LoreweaverTheme {
				SessionSummaryContent(
					summary = summary,
					mode = SessionSummaryMode.ENCOUNTER_COMPLETION,
					onSaveEncounter = { savedSessionId = it },
					onDone = { returnHomeClicks++ },
					onOpenAdventureLog = {},
					onContinueCampaign = {},
					onOpenSessionDetail = {},
					onStartAnotherEncounter = { newEncounterClicks++ },
					onOpenSessionHistory = {},
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.encounter_summary_badge)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_summary_log_title)).performScrollTo()
		composeRule.onNodeWithText(context.getString(R.string.encounter_summary_log_title)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_summary_save_button)).performScrollTo()
		composeRule.onNodeWithText(context.getString(R.string.encounter_summary_save_button)).performClick()
		composeRule.onNodeWithText(context.getString(R.string.session_summary_done_button)).performClick()
		composeRule.onNodeWithText(context.getString(R.string.encounter_summary_start_new_encounter_button)).performClick()

		assertEquals("session-42", savedSessionId)
		assertEquals(1, returnHomeClicks)
		assertEquals(1, newEncounterClicks)
	}
}


