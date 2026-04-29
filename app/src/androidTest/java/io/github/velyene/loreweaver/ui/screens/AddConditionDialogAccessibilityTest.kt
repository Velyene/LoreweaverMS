package io.github.velyene.loreweaver.ui.screens

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddConditionDialogAccessibilityTest {

	@get:Rule
	val composeRule = createComposeRule()

	private val context: Context
		get() = ApplicationProvider.getApplicationContext()

	@Test
	fun addConditionDialog_usesLocalizedResourceLabels() {
		composeRule.setContent {
			MaterialTheme {
				AddConditionDialog(
					onConfirm = { _, _ -> },
					onDismiss = {}
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.add_condition_dialog_title)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.add_condition_dialog_condition_label)).assertExists()
		composeRule.onNodeWithText(context.getString(R.string.add_condition_dialog_has_duration_label)).assertExists()
		composeRule.onNodeWithText(context.getString(R.string.add_condition_dialog_duration_label)).assertExists()
		composeRule.onNodeWithText(context.getString(R.string.add_condition_dialog_duration_placeholder)).assertExists()
	}
}

