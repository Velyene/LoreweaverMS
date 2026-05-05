package io.github.velyene.loreweaver.ui.screens

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.EncounterSnapshot
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.ui.theme.LoreweaverTheme
import io.github.velyene.loreweaver.ui.viewmodels.CampaignListUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DmSessionHubUiTest {

	@get:Rule
	val composeRule = createComposeRule()

	private val context: Context = ApplicationProvider.getApplicationContext()

	@Test
	fun dmSessionHubContent_showsAboutHelpRecentEncounterAndQuickTips() {
		composeRule.setContent {
			LoreweaverTheme {
				DmSessionHubContent(
					uiState = CampaignListUiState(
						sessions = listOf(
							SessionRecord(
								id = "session-1",
								title = "Ashen Ruins",
								date = 1_700_000_000_000,
								isCompleted = false,
								snapshot = EncounterSnapshot(
									combatants = emptyList(),
									currentTurnIndex = 0,
									currentRound = 2,
								),
							),
						),
					),
					actions = DmSessionHubActions(
						onBack = {},
						onNewEncounter = {},
						onResumeActiveEncounter = {},
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

		composeRule.onNodeWithText(context.getString(R.string.app_name)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.dm_action_about_help)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.dm_recent_encounter_title)).assertIsDisplayed()
		composeRule.onNodeWithText("Ashen Ruins").assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.dm_quick_tips_title)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.dm_quick_tip_start_fast)).assertIsDisplayed()
	}
}

