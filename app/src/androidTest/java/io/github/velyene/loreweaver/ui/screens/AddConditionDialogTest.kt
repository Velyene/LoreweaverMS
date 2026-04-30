package io.github.velyene.loreweaver.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.github.velyene.loreweaver.ui.theme.LoreweaverTheme
import org.junit.Rule
import org.junit.Test

class AddConditionDialogTest {
	@get:Rule
	val composeRule = createComposeRule()

	@Test
	fun addConditionDialog_usesEncounterSpecificCopy() {
		composeRule.setContent {
			LoreweaverTheme(darkTheme = true) {
				AddConditionDialog(onConfirm = { _, _, _ -> }, onDismiss = {})
			}
		}

		composeRule.onNodeWithText("Add Encounter Condition").assertIsDisplayed()
		composeRule.onNodeWithText("Encounter Condition").assertIsDisplayed()
		composeRule.onNodeWithText("Has round duration").assertIsDisplayed()
		composeRule.onNodeWithText("Keep as a persistent condition after this encounter").assertIsDisplayed()
		composeRule.onNodeWithText("Duration (rounds)").assertIsDisplayed()
	}

	@Test
	fun addConditionDialog_showsEncounterEndHintWhenDurationDisabled() {
		composeRule.setContent {
			LoreweaverTheme(darkTheme = true) {
				AddConditionDialog(onConfirm = { _, _, _ -> }, onDismiss = {})
			}
		}

		composeRule.onAllNodes(isToggleable())[0].performClick()
		composeRule.onNodeWithText("Leave this unchecked to keep the effect until the encounter ends.").assertIsDisplayed()
	}
}


