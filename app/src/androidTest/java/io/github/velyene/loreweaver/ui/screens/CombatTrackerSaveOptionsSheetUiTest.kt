package io.github.velyene.loreweaver.ui.screens

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.theme.LoreweaverTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CombatTrackerSaveOptionsSheetUiTest {

	@get:Rule
	val composeRule = createComposeRule()

	private val context: Context = ApplicationProvider.getApplicationContext()

	@Test
	fun saveEncounterOptionsSheet_showsExpectedOptionsAndInvokesCallbacks() {
		var dismissedCount = 0
		var saveAndExitCount = 0
		var resumeLaterCount = 0
		var returnHomeCount = 0

		composeRule.setContent {
			LoreweaverTheme {
				SaveEncounterOptionsSheet(
					onDismiss = { dismissedCount++ },
					onSaveAndExit = { saveAndExitCount++ },
					onResumeLater = { resumeLaterCount++ },
					onReturnHome = { returnHomeCount++ },
				)
			}
		}

		composeRule.onNodeWithTag(SAVE_ENCOUNTER_OPTIONS_SHEET_TAG).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_save_options_title)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_save_options_supporting_text)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_save_exit_button)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_resume_later_button)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_return_home_button)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.cancel_button)).assertIsDisplayed()

		composeRule.onNodeWithText(context.getString(R.string.encounter_save_exit_button)).performClick()
		assertEquals(1, saveAndExitCount)
		assertEquals(0, resumeLaterCount)
		assertEquals(0, returnHomeCount)

		composeRule.setContent {
			LoreweaverTheme {
				SaveEncounterOptionsSheet(
					onDismiss = { dismissedCount++ },
					onSaveAndExit = { saveAndExitCount++ },
					onResumeLater = { resumeLaterCount++ },
					onReturnHome = { returnHomeCount++ },
				)
			}
		}
		composeRule.onNodeWithText(context.getString(R.string.encounter_resume_later_button)).performClick()
		assertEquals(1, resumeLaterCount)

		composeRule.setContent {
			LoreweaverTheme {
				SaveEncounterOptionsSheet(
					onDismiss = { dismissedCount++ },
					onSaveAndExit = { saveAndExitCount++ },
					onResumeLater = { resumeLaterCount++ },
					onReturnHome = { returnHomeCount++ },
				)
			}
		}
		composeRule.onNodeWithText(context.getString(R.string.encounter_return_home_button)).performClick()
		assertEquals(1, returnHomeCount)

		composeRule.setContent {
			LoreweaverTheme {
				SaveEncounterOptionsSheet(
					onDismiss = { dismissedCount++ },
					onSaveAndExit = { saveAndExitCount++ },
					onResumeLater = { resumeLaterCount++ },
					onReturnHome = { returnHomeCount++ },
				)
			}
		}
		composeRule.onNodeWithText(context.getString(R.string.cancel_button)).performClick()
		assertEquals(1, dismissedCount)
	}

	@Test
	fun saveEncounterOptionsSheet_dismissRemovesSheetFromComposition() {
		var visible by mutableStateOf(true)

		composeRule.setContent {
			LoreweaverTheme {
				if (visible) {
					SaveEncounterOptionsSheet(
						onDismiss = { visible = false },
						onSaveAndExit = { visible = false },
						onResumeLater = { visible = false },
						onReturnHome = { visible = false },
					)
				}
			}
		}

		composeRule.onNodeWithTag(SAVE_ENCOUNTER_OPTIONS_SHEET_TAG).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.cancel_button)).performClick()
		composeRule.onAllNodesWithTag(SAVE_ENCOUNTER_OPTIONS_SHEET_TAG).assertCountEquals(0)
	}
}



