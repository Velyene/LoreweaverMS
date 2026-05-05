/*
 * FILE: CampaignEncounterEditDeleteUiTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Encounter Edit Dialog Tests
 * 2. Encounter Delete Confirmation Tests
 * 3. Test Data Helpers
 */

package io.github.velyene.loreweaver.ui.screens

import android.content.Context
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterStatus
import io.github.velyene.loreweaver.ui.theme.LoreweaverTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CampaignEncounterEditDeleteUiTest {

	@get:Rule
	val composeRule = createComposeRule()

	private val context: Context = ApplicationProvider.getApplicationContext()

	private fun testEncounter() = Encounter(
		id = "encounter-1",
		campaignId = "campaign-1",
		name = "Bridge Ambush",
	)

	@Test
	fun encounterListBody_showsLocalizedStatusAndSpecificOpenAccessibilityLabel() {
		val encounter = testEncounter().copy(status = EncounterStatus.PENDING)

		composeRule.setContent {
			LoreweaverTheme {
				EncounterListBody(
					encounters = listOf(encounter),
					onEncounterClick = {},
					onEditEncounter = {},
					onDeleteEncounter = {},
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.encounter_status_pending)).assertIsDisplayed()
		composeRule.onAllNodesWithText(encounter.status.name).assertCountEquals(0)

		composeRule
			.onNodeWithText(encounter.name)
			.assert(
				SemanticsMatcher("has specific open-encounter click label") { node ->
					node.config.getOrNull(SemanticsActions.OnClick)?.label ==
						context.getString(R.string.open_encounter_action, encounter.name)
				}
			)
	}

	@Test
	fun encounterListBody_editAndDeleteButtonsInvokeSpecificCallbacksWithoutOpeningEncounter() {
		val encounter = testEncounter()
		var openedEncounterId: String? = null
		var editedEncounterId: String? = null
		var deletedEncounterId: String? = null

		composeRule.setContent {
			LoreweaverTheme {
				EncounterListBody(
					encounters = listOf(encounter),
					onEncounterClick = { openedEncounterId = it },
					onEditEncounter = { editedEncounterId = it.id },
					onDeleteEncounter = { deletedEncounterId = it.id },
				)
			}
		}

		composeRule.onNodeWithContentDescription(context.getString(R.string.edit_encounter, encounter.name)).performClick()
		assertEquals(encounter.id, editedEncounterId)
		assertNull(openedEncounterId)
		assertNull(deletedEncounterId)

		composeRule.onNodeWithContentDescription(context.getString(R.string.delete_encounter, encounter.name)).performClick()
		assertEquals(encounter.id, deletedEncounterId)
		assertNull(openedEncounterId)
	}

	@Test
	fun linkedEncounterList_editFlowPrepopulatesAndSavesTrimmedName() {
		val encounter = testEncounter()
		var updatedEncounterId: String? = null
		var updatedName: String? = null

		composeRule.setContent {
			LoreweaverTheme {
				LinkedEncounterList(
					encounters = listOf(encounter),
					onEncounterClick = {},
					onAddEncounter = {},
					onAddEncounterWithMonsters = { _, _ -> },
					onUpdateEncounter = { editedEncounter, name ->
						updatedEncounterId = editedEncounter.id
						updatedName = name
					},
					onDeleteEncounter = {},
				)
			}
		}

		composeRule.onNodeWithContentDescription(context.getString(R.string.edit_encounter, encounter.name)).performClick()
		composeRule.onNodeWithText(context.getString(R.string.edit_encounter_title)).assertIsDisplayed()
		composeRule.onNodeWithTag(EDIT_ENCOUNTER_NAME_FIELD_TAG).assertIsDisplayed()

		composeRule.onNodeWithTag(EDIT_ENCOUNTER_NAME_FIELD_TAG).performTextClearance()
		composeRule.onNodeWithText(context.getString(R.string.save_button)).assertIsNotEnabled()
		composeRule.onNodeWithTag(EDIT_ENCOUNTER_NAME_FIELD_TAG).performTextInput("  Bridge Ambush Revised  ")
		composeRule.onNodeWithText(context.getString(R.string.save_button)).performClick()

		assertEquals(encounter.id, updatedEncounterId)
		assertEquals("Bridge Ambush Revised", updatedName)
	}

	@Test
	fun linkedEncounterList_deleteFlowConfirmsSpecificEncounterBeforeDeleting() {
		val encounter = testEncounter()
		var deletedEncounterId: String? = null

		composeRule.setContent {
			LoreweaverTheme {
				LinkedEncounterList(
					encounters = listOf(encounter),
					onEncounterClick = {},
					onAddEncounter = {},
					onAddEncounterWithMonsters = { _, _ -> },
					onUpdateEncounter = { _, _ -> },
					onDeleteEncounter = { deletedEncounterId = it.id },
				)
			}
		}

		composeRule.onNodeWithContentDescription(context.getString(R.string.delete_encounter, encounter.name)).performClick()
		composeRule.onNodeWithText(context.getString(R.string.confirm_delete_encounter_title)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.confirm_delete_encounter_message, encounter.name)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.delete_button)).performClick()

		assertEquals(encounter.id, deletedEncounterId)
	}
}
