package io.github.velyene.loreweaver.ui.screens

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.theme.LoreweaverTheme
import io.github.velyene.loreweaver.ui.viewmodels.CampaignListUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DmSessionHubScreenUiTest {

	@get:Rule
	val composeRule = createComposeRule()

	private val context: Context = ApplicationProvider.getApplicationContext()

	@Test
	fun dmSessionHubContent_showsOperatorAnswersForActiveEncounter() {
		var resumeClicks = 0

		composeRule.setContent {
			LoreweaverTheme {
				DmSessionHubContent(
					uiState = CampaignListUiState(
						hasActiveEncounter = true,
						activeEncounterName = "Goblin Ambush",
						activeEncounterRound = 3,
						activeEncounterCombatantCount = 5,
						activeEncounterTurnName = "Aria",
						activeEncounterLatestChange = "Bren dropped a goblin scout.",
						activeEncounterNextStep = "Resolve Aria's turn and keep initiative moving.",
					),
					actions = DmSessionHubActions(
						onBack = {},
						onNewEncounter = {},
						onResumeActiveEncounter = { resumeClicks++ },
						onOpenSavedEncounters = {},
						onManageCharacters = {},
						onEnemyLibrary = {},
						onSessionLogNotes = {},
						onCampaigns = {},
						onAboutHelp = {},
					),
					snackbarHostState = remember { SnackbarHostState() },
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.dm_operator_question_encounter)).assertIsDisplayed()
		composeRule.onNodeWithText("Goblin Ambush").assertIsDisplayed()
		composeRule.onNodeWithText("Aria").assertIsDisplayed()
		composeRule.onNodeWithText("Bren dropped a goblin scout.").assertIsDisplayed()
		composeRule.onNodeWithText("Resolve Aria's turn and keep initiative moving.").assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.dm_active_snapshot_resume_button)).performClick()

		assertEquals(1, resumeClicks)
	}

	@Test
	fun dmSessionHubContent_mainActionsInvokeExpectedCallbacks() {
		var newEncounterClicks = 0
		var openSavedEncounterClicks = 0
		var manageCharactersClicks = 0
		var enemyLibraryClicks = 0
		var sessionLogClicks = 0
		var campaignsClicks = 0
		var aboutHelpClicks = 0

		composeRule.setContent {
			LoreweaverTheme {
				DmSessionHubContent(
					uiState = CampaignListUiState(hasActiveEncounter = true),
					actions = DmSessionHubActions(
						onBack = {},
						onNewEncounter = { newEncounterClicks++ },
						onResumeActiveEncounter = {},
						onOpenSavedEncounters = { openSavedEncounterClicks++ },
						onManageCharacters = { manageCharactersClicks++ },
						onEnemyLibrary = { enemyLibraryClicks++ },
						onSessionLogNotes = { sessionLogClicks++ },
						onCampaigns = { campaignsClicks++ },
						onAboutHelp = { aboutHelpClicks++ },
					),
					snackbarHostState = remember { SnackbarHostState() },
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.dm_action_new_encounter)).performClick()
		composeRule.onNodeWithText(context.getString(R.string.dm_action_load_encounter)).performClick()
		composeRule.onNodeWithText(context.getString(R.string.dm_action_manage_characters)).performClick()
		composeRule.onNodeWithText(context.getString(R.string.dm_action_enemy_library)).performClick()
		composeRule.onNodeWithText(context.getString(R.string.dm_action_session_log_notes)).performClick()
		composeRule.onNodeWithText(context.getString(R.string.browse_campaigns)).performClick()
		composeRule.onNodeWithText(context.getString(R.string.dm_action_about_help)).performClick()

		assertEquals(1, newEncounterClicks)
		assertEquals(1, openSavedEncounterClicks)
		assertEquals(1, manageCharactersClicks)
		assertEquals(1, enemyLibraryClicks)
		assertEquals(1, sessionLogClicks)
		assertEquals(1, campaignsClicks)
		assertEquals(1, aboutHelpClicks)
	}
}


