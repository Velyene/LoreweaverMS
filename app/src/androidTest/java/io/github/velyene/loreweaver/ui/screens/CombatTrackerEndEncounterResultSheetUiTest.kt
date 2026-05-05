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
import io.github.velyene.loreweaver.ui.viewmodels.EncounterResult
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CombatTrackerEndEncounterResultSheetUiTest {

	@get:Rule
	val composeRule = createComposeRule()

	private val context: Context = ApplicationProvider.getApplicationContext()

	@Test
	fun endEncounterResultSheet_showsExpectedOptionsAndInvokesCallbacks() {
		val chosenResults = mutableListOf<EncounterResult>()
		var dismissCount = 0

		composeRule.setContent {
			LoreweaverTheme {
				EndEncounterResultSheet(
					onDismiss = { dismissCount++ },
					onChooseResult = { chosenResults += it },
				)
			}
		}

		composeRule.onNodeWithTag(END_ENCOUNTER_RESULT_SHEET_TAG).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.end_encounter_result_title)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.end_encounter_result_supporting_text)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.session_summary_result_victory)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.session_summary_result_defeat)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.end_encounter_result_manual_end_button)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.cancel_button)).assertIsDisplayed()

		composeRule.onNodeWithText(context.getString(R.string.session_summary_result_victory)).performClick()
		assertEquals(listOf(EncounterResult.VICTORY), chosenResults)

		composeRule.setContent {
			LoreweaverTheme {
				EndEncounterResultSheet(
					onDismiss = { dismissCount++ },
					onChooseResult = { chosenResults += it },
				)
			}
		}
		composeRule.onNodeWithText(context.getString(R.string.session_summary_result_defeat)).performClick()
		assertEquals(listOf(EncounterResult.VICTORY, EncounterResult.DEFEAT), chosenResults)

		composeRule.setContent {
			LoreweaverTheme {
				EndEncounterResultSheet(
					onDismiss = { dismissCount++ },
					onChooseResult = { chosenResults += it },
				)
			}
		}
		composeRule.onNodeWithText(context.getString(R.string.end_encounter_result_manual_end_button)).performClick()
		assertEquals(
			listOf(EncounterResult.VICTORY, EncounterResult.DEFEAT, EncounterResult.ENDED_EARLY),
			chosenResults
		)

		composeRule.setContent {
			LoreweaverTheme {
				EndEncounterResultSheet(
					onDismiss = { dismissCount++ },
					onChooseResult = { chosenResults += it },
				)
			}
		}
		composeRule.onNodeWithText(context.getString(R.string.cancel_button)).performClick()
		assertEquals(1, dismissCount)
	}

	@Test
	fun endEncounterResultSheet_dismissRemovesSheetFromComposition() {
		var visible by mutableStateOf(true)

		composeRule.setContent {
			LoreweaverTheme {
				if (visible) {
					EndEncounterResultSheet(
						onDismiss = { visible = false },
						onChooseResult = { visible = false },
					)
				}
			}
		}

		composeRule.onNodeWithTag(END_ENCOUNTER_RESULT_SHEET_TAG).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.cancel_button)).performClick()
		composeRule.onAllNodesWithTag(END_ENCOUNTER_RESULT_SHEET_TAG).assertCountEquals(0)
	}
}

