package io.github.velyene.loreweaver.ui.screens

import android.content.Context
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.EncounterStatus
import io.github.velyene.loreweaver.ui.theme.LoreweaverTheme
import io.github.velyene.loreweaver.ui.viewmodels.SavedEncounterPickerItem
import io.github.velyene.loreweaver.ui.viewmodels.SavedEncounterPickerUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SavedEncounterPickerScreenUiTest {

	@get:Rule
	val composeRule = createComposeRule()

	private val context: Context = ApplicationProvider.getApplicationContext()

	@Test
	fun savedEncounterPickerContent_opensEncounterFromList() {
		var openedEncounterId: String? = null
		composeRule.setContent {
			LoreweaverTheme {
				SavedEncounterPickerContent(
					uiState = SavedEncounterPickerUiState(
						isLoading = false,
						encounters = listOf(
							SavedEncounterPickerItem(
								id = "enc-1",
								name = "Clocktower Siege",
								campaignTitle = "Stormreach",
								round = 4,
								combatantCount = 7,
								status = EncounterStatus.PENDING,
								notesPreview = "Bridge lanes are trapped.",
								lastSavedAt = 1_700_000_000_000L,
							),
						),
					),
					onBack = {},
					onSearchQueryChange = {},
					onStatusFilterChange = {},
					onOpenEncounter = { openedEncounterId = it },
				)
			}
		}

		composeRule.onNodeWithText("Clocktower Siege").assertIsDisplayed()
		composeRule.onNodeWithText("Stormreach").assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.dm_saved_encounter_review_button)).performClick()

		assertEquals("enc-1", openedEncounterId)
	}

	@Test
	fun savedEncounterPickerContent_showsSearchAndEmptyState() {
		var searchQuery: String? = null
		composeRule.setContent {
			LoreweaverTheme {
				SavedEncounterPickerContent(
					uiState = SavedEncounterPickerUiState(isLoading = false),
					onBack = {},
					onSearchQueryChange = { searchQuery = it },
					onStatusFilterChange = {},
					onOpenEncounter = {},
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.dm_saved_encounters_empty_message)).assertIsDisplayed()
		composeRule.onNode(hasSetTextAction()).performTextInput("Goblin")
		assertEquals("Goblin", searchQuery)
	}

	@Test
	fun savedEncounterPickerContent_showsStatusFiltersAndNotesPreview() {
		var selectedFilter: String? = null
		composeRule.setContent {
			LoreweaverTheme {
				SavedEncounterPickerContent(
					uiState = SavedEncounterPickerUiState(
						isLoading = false,
						encounters = listOf(
							SavedEncounterPickerItem(
								id = "enc-2",
								name = "Goblin Ambush",
								campaignTitle = "Stormroad",
								round = 2,
								combatantCount = 5,
								status = EncounterStatus.ACTIVE,
								notesPreview = "The goblins will flee toward the ravine.",
							),
						),
					),
					onBack = {},
					onSearchQueryChange = {},
					onStatusFilterChange = { selectedFilter = it.name },
					onOpenEncounter = {},
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.dm_saved_encounters_filter_all)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.dm_saved_encounters_filter_pending)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.dm_saved_encounters_filter_active)).performClick()
		composeRule.onNodeWithText(context.getString(R.string.dm_saved_encounter_notes_preview, "The goblins will flee toward the ravine." )).assertIsDisplayed()

		assertEquals("ACTIVE", selectedFilter)
	}
}




