/*
 * FILE: CharacterDetailScreenUiTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Character Detail Overview Tests
 * 2. Journal and Persistent Status Tests
 * 3. Combat and Tab Interaction Tests
 * 4. Test Repository and Content Helpers
 */

package io.github.velyene.loreweaver.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterAction
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.LogEntry
import io.github.velyene.loreweaver.domain.repository.CharactersRepository
import io.github.velyene.loreweaver.domain.repository.LogsRepository
import io.github.velyene.loreweaver.domain.use_case.AddCharacterUseCase
import io.github.velyene.loreweaver.domain.use_case.DeleteCharacterUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCharacterByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCharactersUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertLogUseCase
import io.github.velyene.loreweaver.domain.use_case.UpdateCharacterUseCase
import io.github.velyene.loreweaver.ui.theme.LoreweaverTheme
import io.github.velyene.loreweaver.ui.viewmodels.CharacterViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CharacterDetailScreenUiTest {

	@get:Rule
	val composeRule = createComposeRule()

	private val context: Context = ApplicationProvider.getApplicationContext()

	@Test
	fun characterDetailContent_switchingTabsShowsCombatStatsAndJournalSurfaces() {
		val character = sampleCharacter()
		val viewModel = createViewModel(character)

		composeRule.setContent {
			LoreweaverTheme {
				val haptic = LocalHapticFeedback.current
				var selectedTab by remember { mutableIntStateOf(0) }
				var situationalBonus by remember { mutableStateOf("0") }
				var rollResult by remember { mutableStateOf<Pair<String, Int>?>(null) }
				CharacterDetailContent(
					character = character,
					state = CharacterDetailState(
						selectedTab = selectedTab,
						onTabSelected = { selectedTab = it },
						tabs = listOf(
							context.getString(R.string.tab_combat),
							context.getString(R.string.tab_stats),
							context.getString(R.string.tab_journal)
						),
						onUpdateStat = { _, _ -> },
						haptic = haptic,
						situationalBonus = situationalBonus,
						onBonusChange = { situationalBonus = it },
						rollResult = rollResult,
						onRollResult = { rollResult = it }
					),
					viewModel = viewModel,
					onLookupCondition = {},
					padding = PaddingValues()
				)
			}
		}

		composeRule.onNodeWithText(
			context.getString(R.string.stat_value_with_max, context.getString(R.string.hp_label), character.hp, character.maxHp)
		).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.actions_attacks_label)).performScrollTo().assertIsDisplayed()

		composeRule.onNodeWithText(context.getString(R.string.tab_stats)).performClick()
		composeRule.onNodeWithText(context.getString(R.string.situational_bonus_label)).performScrollTo().assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.skills_label)).performScrollTo().assertIsDisplayed()

		composeRule.onNodeWithText(context.getString(R.string.tab_journal)).performClick()
		composeRule.onNodeWithText(context.getString(R.string.inventory_label)).performScrollTo().assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.character_status_overview_title)).performScrollTo().assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.notes_label)).performScrollTo().assertIsDisplayed()
	}

	@Test
	fun characterDetailContent_statsRollShowsResultAndClearRemovesIt() {
		val character = sampleCharacter()
		val viewModel = createViewModel(character)
		val strengthCheckLabel = context.getString(R.string.attribute_check, "STR")

		composeRule.setContent {
			LoreweaverTheme {
				val haptic = LocalHapticFeedback.current
				var situationalBonus by remember { mutableStateOf("0") }
				var rollResult by remember { mutableStateOf<Pair<String, Int>?>(null) }
				CharacterDetailContent(
					character = character,
					state = CharacterDetailState(
						selectedTab = 1,
						onTabSelected = {},
						tabs = listOf(
							context.getString(R.string.tab_combat),
							context.getString(R.string.tab_stats),
							context.getString(R.string.tab_journal)
						),
						onUpdateStat = { _, _ -> },
						haptic = haptic,
						situationalBonus = situationalBonus,
						onBonusChange = { situationalBonus = it },
						rollResult = rollResult,
						onRollResult = { rollResult = it }
					),
					viewModel = viewModel,
					onLookupCondition = {},
					padding = PaddingValues()
				)
			}
		}

		composeRule.onNodeWithText("STR").performClick()
		composeRule.onNodeWithText(strengthCheckLabel).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.clear_button)).performClick()
		composeRule.onAllNodesWithText(strengthCheckLabel).assertCountEquals(0)
	}

	@Test
	fun characterDetailActions_deleteFlowShowsSpecificConfirmationAndInvokesConfirm() {
		val character = sampleCharacter()
		var editedId: String? = null
		var deletedId: String? = null

		composeRule.setContent {
			LoreweaverTheme {
				var pendingDelete by remember { mutableStateOf<CharacterEntry?>(null) }
				CharacterDetailActions(
					character = character,
					onEdit = { editedId = it },
					onDeleteRequest = { pendingDelete = it }
				)
				pendingDelete?.let { characterToDelete ->
					ConfirmationDialog(
						title = context.getString(R.string.confirm_delete_character_title),
						message = context.getString(
							R.string.confirm_delete_character_message,
							characterToDelete.name
						),
						confirmLabel = context.getString(R.string.delete_button),
						onConfirm = {
							deletedId = characterToDelete.id
							pendingDelete = null
						},
						onDismiss = { pendingDelete = null }
					)
				}
			}
		}

		composeRule.onNodeWithTag(CHARACTER_DETAIL_EDIT_ACTION_TAG).performClick()
		composeRule.waitForIdle()
		assertEquals(character.id, editedId)

		composeRule.onNodeWithTag(CHARACTER_DETAIL_DELETE_ACTION_TAG).performClick()
		composeRule.waitForIdle()
		composeRule.onNodeWithText(context.getString(R.string.confirm_delete_character_title)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.confirm_delete_character_message, character.name)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.delete_button)).performClick()

		assertEquals(character.id, deletedId)
	}

	@Test
	fun characterDetailScreen_journalTabCanAddPersistentStatus() {
		val character = sampleCharacter()
		val viewModel = createViewModel(character)
		val persistentCondition = ConditionConstants.persistentConditions().first()

		composeRule.setContent {
			LoreweaverTheme {
				CharacterDetailScreen(
					characterId = character.id,
					onEdit = {},
					onLookupCondition = {},
					onDelete = {},
					onBack = {},
					viewModel = viewModel,
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.tab_journal)).performClick()
		composeRule.onNodeWithText(context.getString(R.string.character_status_manage_persistent_button)).performScrollTo().performClick()
		composeRule.onNodeWithTag(PERSISTENT_STATUS_PICKER_SHEET_TAG).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.character_status_manage_persistent_title)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.character_status_manage_persistent_supporting_text)).assertIsDisplayed()
		composeRule.onNodeWithTag(PERSISTENT_STATUS_MANAGER_CURRENT_EFFECTS_TAG).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.character_status_current_persistent_effects_title)).assertIsDisplayed()
		composeRule.onNodeWithText(persistentCondition).performScrollTo().performClick()
		composeRule.onNodeWithText(context.getString(R.string.add_button)).performClick()
		composeRule.onNodeWithText("✶ $persistentCondition (Persistent)").assertIsDisplayed()
	}

	@Test
	fun characterDetailScreen_journalTabRemovesPersistentStatusFromManagerSheet() {
		val character = sampleCharacter().copy(activeConditions = setOf("cursed"))
		val viewModel = createViewModel(character)

		composeRule.setContent {
			LoreweaverTheme {
				CharacterDetailScreen(
					characterId = character.id,
					onEdit = {},
					onLookupCondition = {},
					onDelete = {},
					onBack = {},
					viewModel = viewModel,
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.tab_journal)).performClick()
		composeRule.onNodeWithText(context.getString(R.string.character_status_manage_persistent_button)).performScrollTo().performClick()
		composeRule.onNodeWithTag(PERSISTENT_STATUS_MANAGER_CURRENT_EFFECTS_TAG).assertIsDisplayed()
		composeRule.onNodeWithTag(persistentStatusManagerRemoveButtonTag("Cursed")).performClick()
		composeRule.onAllNodesWithTag(statusChipTag("Cursed")).assertCountEquals(0)
	}

	private fun sampleCharacter(): CharacterEntry {
		return CharacterEntry(
			id = "hero-1",
			name = "Aria",
			type = "Fighter",
			hp = 18,
			maxHp = 24,
			tempHp = 5,
			ac = 16,
			speed = 30,
			initiative = 2,
			notes = "Keeps a coded field journal.",
			level = 3,
			strength = 16,
			dexterity = 14,
			constitution = 14,
			wisdom = 12,
			proficiencies = setOf("Athletics", "Perception"),
			inventory = listOf("Rope", "Lantern"),
			activeConditions = setOf("Blessed"),
			actions = listOf(
				CharacterAction(
					name = "Longsword",
					attackBonus = 5,
					damageDice = "1d8+3"
				)
			)
		)
	}

	private fun createViewModel(vararg characters: CharacterEntry): CharacterViewModel {
		val characterRepository = FakeCharacterTestRepository(characters.toList())
		val logRepository = FakeLogTestRepository()
		return CharacterViewModel(
			getCharactersUseCase = GetCharactersUseCase(characterRepository),
			getCharacterByIdUseCase = GetCharacterByIdUseCase(characterRepository),
			addCharacterUseCase = AddCharacterUseCase(characterRepository),
			updateCharacterUseCase = UpdateCharacterUseCase(characterRepository),
			deleteCharacterUseCase = DeleteCharacterUseCase(characterRepository),
			insertLogUseCase = InsertLogUseCase(logRepository)
		)
	}
}

private class FakeCharacterTestRepository(initialCharacters: List<CharacterEntry>) : CharactersRepository {
	private val charactersFlow = MutableStateFlow(initialCharacters)

	override fun getAllCharacters(): Flow<List<CharacterEntry>> = charactersFlow

	override suspend fun getCharacterById(id: String): CharacterEntry? =
		charactersFlow.value.firstOrNull { it.id == id }

	override suspend fun insertCharacter(character: CharacterEntry) {
		charactersFlow.value += character
	}

	override suspend fun updateCharacter(character: CharacterEntry) {
		charactersFlow.value = charactersFlow.value.map { existing ->
			if (existing.id == character.id) character else existing
		}
	}

	override suspend fun deleteCharacter(character: CharacterEntry) {
		charactersFlow.value = charactersFlow.value.filterNot { it.id == character.id }
	}
}

private class FakeLogTestRepository : LogsRepository {
	private val logsFlow = MutableStateFlow<List<LogEntry>>(emptyList())

	override fun getAllLogs(): Flow<List<LogEntry>> = logsFlow

	override suspend fun insertLog(log: LogEntry) {
		logsFlow.value += log
	}

	override suspend fun clearLogs() {
		logsFlow.value = emptyList()
	}
}
