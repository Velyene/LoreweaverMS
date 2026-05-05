/*
 * FILE: SessionDetailUiTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Class: SessionDetailUiTest
 * 2. Value: composeRule
 * 3. Value: context
 * 4. Value: dateFormat
 * 5. Function: sessionExpandableItem_openDetailButtonInvokesCallback
 * 6. Value: session
 * 7. Value: openedSessionId
 * 8. Function: sessionExpandableItem_withEmptyLogShowsFallbackMessageWhenExpanded
 */

package io.github.velyene.loreweaver.ui.screens

import android.content.Context
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.EncounterSnapshot
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.ui.theme.LoreweaverTheme
import io.github.velyene.loreweaver.ui.util.UiText
import io.github.velyene.loreweaver.ui.viewmodels.EncounterResult
import io.github.velyene.loreweaver.ui.viewmodels.SessionDetailUiState
import io.github.velyene.loreweaver.ui.viewmodels.SessionParticipantSummary
import io.github.velyene.loreweaver.ui.viewmodels.SessionSummaryUiModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class SessionDetailUiTest {

	@get:Rule
	val composeRule = createComposeRule()

	private val context: Context = ApplicationProvider.getApplicationContext()
	private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

	@Test
	fun sessionExpandableItem_openDetailButtonInvokesCallback() {
		val session = SessionRecord(
			id = "session-1",
			title = "Bridge Ambush",
			log = listOf("Goblin takes 3 damage"),
			snapshot = EncounterSnapshot(
				combatants = listOf(
					CombatantState("hero-1", "Hero", 15, 12, 12),
				),
				currentTurnIndex = 0,
				currentRound = 2,
			),
		)
		var openedSessionId: String? = null

		composeRule.setContent {
			LoreweaverTheme {
				SessionExpandableItem(
					session = session,
					dateFormat = dateFormat,
					onOpenSessionDetail = { openedSessionId = it },
				)
			}
		}

		composeRule.onNodeWithText(session.title).performClick()
		composeRule.onNodeWithText(context.getString(R.string.session_detail_open_button)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.session_detail_open_button)).performClick()

		assertEquals(session.id, openedSessionId)
	}

	@Test
	fun sessionExpandableItem_withEmptyLogShowsFallbackMessageWhenExpanded() {
		val session = SessionRecord(
			id = "session-empty-log",
			title = "Quiet Aftermath",
			log = emptyList(),
		)

		composeRule.setContent {
			LoreweaverTheme {
				SessionExpandableItem(
					session = session,
					dateFormat = dateFormat,
				)
			}
		}

		composeRule.onNodeWithText(session.title).performClick()
		composeRule.onNodeWithText(context.getString(R.string.session_summary_log_empty_message)).assertIsDisplayed()
	}

	@Test
	fun sessionExpandableItem_withoutSnapshotShowsUnavailableFallbackWhenExpanded() {
		val session = SessionRecord(
			id = "session-no-snapshot",
			title = "Quiet Aftermath",
			log = listOf("The party regroups."),
			snapshot = null,
		)

		composeRule.setContent {
			LoreweaverTheme {
				SessionExpandableItem(
					session = session,
					dateFormat = dateFormat,
				)
			}
		}

		composeRule.onNodeWithText(session.title).performClick()
		composeRule.onNodeWithText(context.getString(R.string.session_end_state_title)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.session_detail_snapshot_unavailable)).assertIsDisplayed()
	}

	@Test
	fun sessionExpandableItem_sectionLabelsExposeHeadingSemanticsWhenExpanded() {
		val session = SessionRecord(
			id = "session-headings",
			title = "Bridge Ambush",
			log = listOf("Hero takes cover"),
			snapshot = EncounterSnapshot(
				combatants = listOf(
					CombatantState("hero-1", "Hero", 15, 12, 12),
				),
				currentTurnIndex = 0,
				currentRound = 2,
			),
		)

		composeRule.setContent {
			LoreweaverTheme {
				SessionExpandableItem(
					session = session,
					dateFormat = dateFormat,
				)
			}
		}

		composeRule.onNodeWithText(session.title).performClick()

		composeRule
			.onNodeWithText(context.getString(R.string.session_combat_log_title), useUnmergedTree = true)
			.assertIsDisplayed()
			.assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))

		composeRule
			.onNodeWithText(context.getString(R.string.session_end_state_title), useUnmergedTree = true)
			.assertIsDisplayed()
			.assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
	}

	@Test
	fun sessionHistoryList_clickingSessionInvokesCallback() {
		val session = SessionRecord(
			id = "session-campaign",
			title = "Bridge Ambush Recap",
			log = listOf("Hero takes cover"),
		)
		var openedSessionId: String? = null

		composeRule.setContent {
			LoreweaverTheme {
				SessionHistoryList(
					sessions = listOf(session),
					onSessionClick = { openedSessionId = it },
				)
			}
		}

		composeRule.onNodeWithText(session.title).performClick()

		assertEquals(session.id, openedSessionId)
	}

	@Test
	fun sessionHistoryList_usesSpecificOpenSessionAccessibilityLabel() {
		val session = SessionRecord(
			id = "session-campaign",
			title = "Bridge Ambush Recap",
			log = listOf("Hero takes cover"),
		)

		composeRule.setContent {
			LoreweaverTheme {
				SessionHistoryList(
					sessions = listOf(session),
					onSessionClick = {},
				)
			}
		}

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
	fun sessionDetailScreenRoute_rendersSessionAndOpensCampaign() {
		val session = SessionRecord(
			id = "session-1",
			title = "Bridge Ambush Recap",
			date = 1234L,
			log = listOf("Goblin takes 3 damage"),
			snapshot = EncounterSnapshot(
				combatants = listOf(
					CombatantState("hero-1", "Hero", 15, 12, 12),
				),
				currentTurnIndex = 0,
				currentRound = 3,
			),
		)
		var loadedSessionId: String? = null
		var openedCampaignId: String? = null

		composeRule.setContent {
			LoreweaverTheme {
				SessionDetailScreenRoute(
					sessionId = session.id,
					uiState = SessionDetailUiState(
						isLoading = false,
						session = session,
						encounterName = "Bridge Ambush",
						campaignId = "campaign-1",
						campaignTitle = "Stormreach",
						summary = SessionSummaryUiModel(
							sessionId = session.id,
							encounterId = "encounter-1",
							encounterName = "Bridge Ambush",
							campaignId = "campaign-1",
							campaignTitle = "Stormreach",
							result = EncounterResult.VICTORY,
							totalRounds = 3,
							survivingPlayers = listOf(
								SessionParticipantSummary("Hero", "12/12 HP", 15),
							),
							defeatedEnemies = emptyList(),
							persistentStatuses = emptyList(),
							notesSummary = "Fog covers the bridge.",
							logSummary = session.log,
						),
					),
					onBack = {},
					onLoadSession = { loadedSessionId = it },
					onOpenCampaign = { openedCampaignId = it },
				)
			}
		}

		composeRule
			.onNodeWithText("Bridge Ambush", useUnmergedTree = true)
			.assertIsDisplayed()
			.assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
		composeRule.onNodeWithText(context.getString(R.string.session_detail_badge)).assertIsDisplayed()
		composeRule.onNodeWithText("Bridge Ambush").assertIsDisplayed()
		composeRule.onNodeWithText("Stormreach").assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.session_detail_open_campaign_button)).performClick()

		assertEquals(session.id, loadedSessionId)
		assertEquals("campaign-1", openedCampaignId)
	}

	@Test
	fun sessionDetailScreenRoute_retryActionInvokesCallbackWhenProvided() {
		var loadedSessionId: String? = null
		var retryCount = 0

		composeRule.setContent {
			LoreweaverTheme {
				SessionDetailScreenRoute(
					sessionId = "missing-session",
					uiState = SessionDetailUiState(
						isLoading = false,
						error = UiText.DynamicString(context.getString(R.string.session_detail_not_found_message)),
						onRetry = { retryCount++ },
					),
					onBack = {},
					onLoadSession = { loadedSessionId = it },
					onOpenCampaign = {},
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.retry_action)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.retry_action)).performClick()

		assertEquals("missing-session", loadedSessionId)
		assertEquals(1, retryCount)
	}
}

