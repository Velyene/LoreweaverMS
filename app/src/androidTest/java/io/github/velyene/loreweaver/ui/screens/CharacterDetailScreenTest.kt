/*
 * FILE: CharacterDetailScreenTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Class: CharacterDetailScreenTest
 * 2. Test coverage: Journal tab content
 * 3. Test coverage: Combat tab content
 * 4. Test support: CharacterViewModel and fake repositories
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
import io.github.velyene.loreweaver.ui.util.AppText
import io.github.velyene.loreweaver.ui.viewmodels.CharacterViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class CharacterDetailScreenTest {
	@get:Rule
	val composeRule = createComposeRule()

	@Test
	fun journalTab_showsSelectedSpellsAndGroupedConditions() {
		val viewModel = createTestCharacterViewModel()
		val character = CharacterEntry(
			name = "Mira",
			type = "Wizard",
			spells = listOf("Magic Missile", "Shield"),
			inventory = listOf("Potion of Healing"),
			activeConditions = setOf("Blinded"),
			persistentConditions = setOf("Burning"),
			status = "Concentrating on Fly.",
			notes = "Prepared for the tower assault."
		)

		composeRule.setContent {
			LoreweaverTheme(darkTheme = true) {
				JournalTab(
					character = character,
					viewModel = viewModel,
					onLookupCondition = {}
				)
			}
		}

		composeRule.onNodeWithText("Selected Spells").assertIsDisplayed()
		composeRule.onNodeWithText("• Magic Missile").assertIsDisplayed()
		composeRule.onNodeWithText("• Shield").assertIsDisplayed()
		composeRule.onNodeWithText("Encounter Conditions").assertIsDisplayed()
		composeRule.onNodeWithText("Blinded").assertIsDisplayed()
		composeRule.onNodeWithText("Persistent Conditions").assertIsDisplayed()
		composeRule.onNodeWithText("Burning").assertIsDisplayed()
		composeRule.onNodeWithText("Encounter Status Notes").assertIsDisplayed()
		composeRule.onNodeWithText("Concentrating on Fly.").assertIsDisplayed()
	}

	@Test
	fun combatTab_showsActionViewSectionsAndPreparedSpells() {
		val viewModel = createTestCharacterViewModel()
		val character = CharacterEntry(
			name = "Mira",
			type = "Wizard",
			hp = 14,
			maxHp = 14,
			mana = 6,
			maxMana = 6,
			stamina = 2,
			maxStamina = 2,
			level = 3,
			spells = listOf("Magic Missile"),
			spellSlots = mapOf(1 to (2 to 4)),
			inventory = listOf("Potion of Healing"),
			activeConditions = setOf("Blinded"),
			persistentConditions = setOf("Blessed")
		)

		composeRule.setContent {
			LoreweaverTheme(darkTheme = true) {
				CombatTab(
					character = character,
					viewModel = viewModel,
					onUpdateStat = { _, _ -> },
					haptic = LocalHapticFeedback.current,
					situationalBonus = "0",
					onRollResult = {}
				)
			}
		}

		composeRule.onNodeWithText("Action View").assertIsDisplayed()
		composeRule.onNodeWithText("Actions").assertIsDisplayed()
		composeRule.onNodeWithText("Spells").assertIsDisplayed()
		composeRule.onNodeWithText("Items").assertIsDisplayed()
		composeRule.onNodeWithText("Encounter Conditions").assertIsDisplayed()
		composeRule.onNodeWithText("Persistent Conditions").assertIsDisplayed()

		composeRule.onNodeWithText("Spells").performClick()
		composeRule.onNodeWithText("Magic Missile").assertIsDisplayed()
		composeRule.onNodeWithText("Level 1 Slot").assertIsDisplayed()

		composeRule.onNodeWithText("Items").performClick()
		composeRule.onNodeWithText("Potion of Healing").assertIsDisplayed()
	}
}

private fun createTestCharacterViewModel(): CharacterViewModel {
	val charactersRepository = AndroidTestCharactersRepository()
	return CharacterViewModel(
		getCharactersUseCase = GetCharactersUseCase(charactersRepository),
		getCharacterByIdUseCase = GetCharacterByIdUseCase(charactersRepository),
		addCharacterUseCase = AddCharacterUseCase(charactersRepository),
		updateCharacterUseCase = UpdateCharacterUseCase(charactersRepository),
		deleteCharacterUseCase = DeleteCharacterUseCase(charactersRepository),
		insertLogUseCase = InsertLogUseCase(AndroidTestLogsRepository()),
		appText = androidTestAppText
	)
}

private val androidTestAppText: AppText = object : AppText {
	override fun getString(resId: Int, vararg formatArgs: Any): String =
		buildString {
			append("resId=")
			append(resId)
			if (formatArgs.isNotEmpty()) {
				append(" args=")
				append(formatArgs.toList())
			}
		}
}

private class AndroidTestCharactersRepository : CharactersRepository {
	private val characters = MutableStateFlow<List<CharacterEntry>>(emptyList())

	override fun getAllCharacters(): Flow<List<CharacterEntry>> = characters

	override suspend fun getCharacterById(id: String): CharacterEntry? {
		return characters.value.find { it.id == id }
	}

	override suspend fun insertCharacter(character: CharacterEntry) {
		characters.value = listOf(character) + characters.value
	}

	override suspend fun updateCharacter(character: CharacterEntry) {
		characters.value = characters.value.map { if (it.id == character.id) character else it }
	}

	override suspend fun deleteCharacter(character: CharacterEntry) {
		characters.value = characters.value.filterNot { it.id == character.id }
	}
}

private class AndroidTestLogsRepository : LogsRepository {
	override fun getAllLogs(): Flow<List<LogEntry>> = flowOf(emptyList())
	override suspend fun insertLog(log: LogEntry) = Unit
	override suspend fun clearLogs() = Unit
}


