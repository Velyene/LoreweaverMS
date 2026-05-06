/*
 * FILE: HomeScreenUiTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Class: HomeScreenUiTest
 * 2. Value: composeRule
 * 3. Value: context
 * 4. Function: setHomeContent
 * 5. Function: homeScreenContent_withLatestCompletedSession_rendersShortcutAndInvokesCallback
 * 6. Value: session
 * 7. Value: openedSessionId
 * 8. Function: homeScreenContent_withoutLatestCompletedSession_hidesShortcutSection
 */

package io.github.velyene.loreweaver.ui.screens

import android.content.Context
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.ui.theme.LoreweaverTheme
import io.github.velyene.loreweaver.ui.viewmodels.CampaignListUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenUiTest {

	@get:Rule
	val composeRule = createComposeRule()

	private val context: Context = ApplicationProvider.getApplicationContext()

	private fun setHomeContent(
		uiState: CampaignListUiState,
		onNewEncounter: () -> Unit = {},
		onResumeEncounter: () -> Unit = {},
		onLatestSessionClick: (String) -> Unit = {},
		onCampaigns: () -> Unit = {},
		onCampaignClick: (String) -> Unit = {},
		onRulesReference: () -> Unit = {},
	) {
		composeRule.setContent {
			LoreweaverTheme {
				HomeScreenContent(
					uiState = uiState,
					padding = androidx.compose.foundation.layout.PaddingValues(),
					onNewEncounter = onNewEncounter,
					onResumeEncounter = onResumeEncounter,
					onLatestSessionClick = onLatestSessionClick,
					onCampaigns = onCampaigns,
					onCampaignClick = onCampaignClick,
					onRulesReference = onRulesReference,
				)
			}
		}
	}

	@Test
	fun homeScreenContent_withLatestCompletedSession_rendersShortcutAndInvokesCallback() {
		val session = SessionRecord(
			id = "session-1",
			title = "Bridge Ambush Recap",
			date = 1_700_000_000_000L,
			isCompleted = true,
		)
		var openedSessionId: String? = null

		setHomeContent(
			uiState = CampaignListUiState(latestCompletedSession = session),
			onLatestSessionClick = { openedSessionId = it },
		)

		composeRule.onNodeWithText(context.getString(R.string.home_latest_session_title).uppercase()).assertIsDisplayed()
		composeRule.onNodeWithText(session.title).assertIsDisplayed()
		composeRule.onNodeWithText(session.title).performClick()

		assertEquals(session.id, openedSessionId)
	}

	@Test
	fun homeScreenContent_withLatestCompletedSession_usesSpecificOpenSessionAccessibilityLabel() {
		val session = SessionRecord(
			id = "session-1",
			title = "Bridge Ambush Recap",
			date = 1_700_000_000_000L,
			isCompleted = true,
		)

		setHomeContent(uiState = CampaignListUiState(latestCompletedSession = session))

		composeRule
			.onNodeWithText(session.title)
			.assert(
				SemanticsMatcher("has specific open-session click label") { node ->
					node.config.getOrNull(SemanticsActions.OnClick)?.label ==
						context.getString(R.string.open_session_action, session.title)
				}
			)
	}

	@Test
	fun homeScreenContent_withoutLatestCompletedSession_hidesShortcutSection() {
		setHomeContent(uiState = CampaignListUiState())

		composeRule
			.onAllNodesWithText(context.getString(R.string.home_latest_session_title).uppercase())
			.assertCountEquals(0)
	}

	@Test
	fun homeScreenContent_quickActionsInvokeCallbacks() {
		var newEncounterClicks = 0
		var campaignsClicks = 0
		var referenceClicks = 0

		setHomeContent(
			uiState = CampaignListUiState(
				hasActiveEncounter = true,
				activeEncounterName = "Bridge Clash",
			),
			onNewEncounter = { newEncounterClicks++ },
			onCampaigns = { campaignsClicks++ },
			onRulesReference = { referenceClicks++ },
		)

		composeRule.onNodeWithText(context.getString(R.string.open_session_hub)).performClick()
		composeRule.onNodeWithText(context.getString(R.string.browse_campaigns)).performClick()
		composeRule.onNodeWithText(context.getString(R.string.home_rules_reference)).performClick()

		assertEquals(1, newEncounterClicks)
		assertTrue(campaignsClicks >= 1)
		assertEquals(1, referenceClicks)
	}

	@Test
	fun homeScreenContent_withoutActiveEncounter_showsBrowseCampaignsOnlyOnce() {
		setHomeContent(uiState = CampaignListUiState())

		composeRule
			.onAllNodesWithText(context.getString(R.string.browse_campaigns))
			.assertCountEquals(1)
	}

	@Test
	fun homeScreenContent_withRecentCampaigns_opensCampaignDetail() {
		val campaign = Campaign(id = "campaign-1", title = "Stormreach")
		var openedCampaignId: String? = null

		setHomeContent(
			uiState = CampaignListUiState(campaigns = listOf(campaign)),
			onCampaignClick = { openedCampaignId = it },
		)

		composeRule.onNodeWithText(campaign.title).assertIsDisplayed()
		composeRule.onNodeWithText(campaign.title).performClick()

		assertEquals(campaign.id, openedCampaignId)
	}

	@Test
	fun homeScreenContent_withRecentCampaigns_usesSpecificOpenCampaignAccessibilityLabel() {
		val campaign = Campaign(id = "campaign-1", title = "Stormreach")

		setHomeContent(uiState = CampaignListUiState(campaigns = listOf(campaign)))

		composeRule
			.onNodeWithText(campaign.title)
			.assert(
				SemanticsMatcher("has specific open-campaign click label") { node ->
					node.config.getOrNull(SemanticsActions.OnClick)?.label ==
						context.getString(R.string.open_campaign_action, campaign.title)
				}
			)
	}

	@Test
	fun homeScreenContent_withActiveEncounter_showsResumeCardAndInvokesCallback() {
		var resumeClicks = 0

		setHomeContent(
			uiState = CampaignListUiState(
				hasActiveEncounter = true,
				activeEncounterName = "Clocktower Siege",
				activeEncounterRound = 4,
			),
			onResumeEncounter = { resumeClicks++ },
		)

		composeRule.onNodeWithText(context.getString(R.string.home_active_encounter_title)).assertIsDisplayed()
		composeRule.onNodeWithText("Clocktower Siege").assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.resume_encounter)).performClick()

		assertEquals(1, resumeClicks)
	}

	@Test
	fun homeScreenContent_whenFullyEmpty_showsFirstRunHintAndNoCampaignsMessage() {
		setHomeContent(uiState = CampaignListUiState())

		composeRule.onNodeWithText(context.getString(R.string.home_first_run_title)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.home_first_run_message)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.no_campaigns)).assertIsDisplayed()
	}
}


