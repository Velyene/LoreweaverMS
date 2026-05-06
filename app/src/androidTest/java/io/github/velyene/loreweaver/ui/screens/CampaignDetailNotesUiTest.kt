package io.github.velyene.loreweaver.ui.screens

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.Note
import io.github.velyene.loreweaver.ui.theme.LoreweaverTheme
import io.github.velyene.loreweaver.ui.util.NOTE_TYPE_LORE
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CampaignDetailNotesUiTest {

	@get:Rule
	val composeRule = createComposeRule()

	private val context: Context = ApplicationProvider.getApplicationContext()
	private val campaign = Campaign(id = "campaign-1", title = "Stormroad")

	@Test
	fun loreAndNotesSection_addLoreNoteRequiresExtraFieldAndUsesCanonicalType() {
		var notes by mutableStateOf<List<Note>>(emptyList())
		var recordedType: String? = null
		var recordedExtra: String? = null

		composeRule.setContent {
			LoreweaverTheme {
				LoreAndNotesSection(
					campaign = campaign,
					notes = notes,
					onAddNote = { content, type, extra ->
						recordedType = type
						recordedExtra = extra
						notes = notes + Note.Lore(
							campaignId = "campaign-1",
							content = content,
							historicalEra = extra
						)
					},
					onDeleteNote = {},
					onUpdateNote = {}
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.new_note_button)).performClick()
		composeRule.onNodeWithText(context.getString(R.string.add_note_title)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.note_type_lore)).performClick()
		composeRule.onNodeWithTag(NOTE_DIALOG_CONTENT_FIELD_TAG).performTextInput("The empire fell in ash.")
		composeRule.onNodeWithText(context.getString(R.string.add_button)).assertIsNotEnabled()
		composeRule.onNodeWithTag(NOTE_DIALOG_EXTRA_FIELD_TAG).performTextInput("Age of Ash")
		composeRule.onNodeWithText(context.getString(R.string.add_button)).performClick()

		assertEquals(NOTE_TYPE_LORE, recordedType)
		assertEquals("Age of Ash", recordedExtra)
		composeRule.onNodeWithText("The empire fell in ash.").assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.note_era_detail, "Age of Ash")).assertIsDisplayed()
	}

	@Test
	fun loreAndNotesSection_editNpcPrepopulatesFieldsAndDeleteFlowRemovesNote() {
		val existingNote = Note.NPC(
			id = "note-npc-1",
			campaignId = "campaign-1",
			content = "Quartermaster",
			faction = "Harbor Guard",
			attitude = "Wary"
		)
		var notes by mutableStateOf<List<Note>>(listOf(existingNote))
		var updatedNote: Note? = null
		var deletedNote: Note? = null

		composeRule.setContent {
			LoreweaverTheme {
				LoreAndNotesSection(
					campaign = campaign,
					notes = notes,
					onAddNote = { _, _, _ -> },
					onDeleteNote = { note ->
						deletedNote = note
						notes = notes.filterNot { it.id == note.id }
					},
					onUpdateNote = { note ->
						updatedNote = note
						notes = notes.map { existing -> if (existing.id == note.id) note else existing }
					}
				)
			}
		}

		composeRule.onNodeWithContentDescription(context.getString(R.string.edit_note_desc)).performClick()
		composeRule.onNodeWithText(context.getString(R.string.edit_note_title)).assertIsDisplayed()
		composeRule.onNodeWithTag(NOTE_DIALOG_CONTENT_FIELD_TAG).assertIsDisplayed()
		composeRule.onNodeWithTag(NOTE_DIALOG_FACTION_FIELD_TAG).assertIsDisplayed()
		composeRule.onNodeWithTag(NOTE_DIALOG_ATTITUDE_FIELD_TAG).assertIsDisplayed()

		composeRule.onNodeWithTag(NOTE_DIALOG_ATTITUDE_FIELD_TAG).performTextClearance()
		composeRule.onNodeWithTag(NOTE_DIALOG_ATTITUDE_FIELD_TAG).performTextInput("Friendly")
		composeRule.onNodeWithText(context.getString(R.string.save_button)).performClick()

		assertTrue(updatedNote is Note.NPC)
		assertEquals("Friendly", (updatedNote as Note.NPC).attitude)
		composeRule.onNodeWithText(context.getString(R.string.note_npc_detail, "Harbor Guard", "Friendly")).assertIsDisplayed()

		composeRule.onNodeWithContentDescription(context.getString(R.string.delete_note_desc)).performClick()
		composeRule.onNodeWithText(context.getString(R.string.confirm_delete_note_title)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.confirm_delete_note_message)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.delete_button)).performClick()

		assertEquals(existingNote.id, deletedNote?.id)
		composeRule.onNodeWithText(context.getString(R.string.campaign_notes_empty_message)).assertIsDisplayed()
	}
}

