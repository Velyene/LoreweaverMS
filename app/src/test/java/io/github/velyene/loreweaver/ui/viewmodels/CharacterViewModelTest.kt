package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.MainDispatcherRule
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.use_case.ValidationMessages.CHARACTER_NAME_EMPTY_MESSAGE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterViewModelTest {

	@get:Rule
	val mainDispatcherRule = MainDispatcherRule()

	@Test
	fun init_loadsCharactersIntoUiState() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val characters = listOf(
				CharacterEntry(id = "character-1", name = "Aria"),
				CharacterEntry(id = "character-2", name = "Bram")
			)
			repository.setCharacters(characters)
			val viewModel = createCharacterViewModel(repository)
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals(characters, this.characters)
				assertFalse(isLoading)
			}
		}
	}

	@Test
	fun addCharacter_setsLocalizedValidationMessage_whenNameIsBlank() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCharacterViewModel(repository)

			viewModel.addCharacter(CharacterEntry(name = "\t"))
			advanceUntilIdle()

			assertEquals(
				expectedErrorMessage(R.string.character_error_add, CHARACTER_NAME_EMPTY_MESSAGE),
				viewModel.uiState.value.error
			)
		}
	}
}

