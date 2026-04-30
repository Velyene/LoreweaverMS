package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.MainDispatcherRule
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterViewModelTest {
	@get:Rule
	val mainDispatcherRule = MainDispatcherRule()

	@Test
	fun addCharacter_invokesSuccessCallbackAfterInsertAndUpdatesList() = runTest {
		val repository = FakeCharactersRepository()
		var successCalled = false
		val viewModel = createCharacterViewModel(repository)
		val character = CharacterEntry(name = "Mira", spells = listOf("Magic Missile"))

		viewModel.addCharacter(character) { successCalled = true }
		advanceUntilIdle()

		assertTrue(successCalled)
		assertEquals(listOf(character), viewModel.uiState.value.characters)
	}

	@Test
	fun selectedCharacter_staysSyncedAfterUpdateAndDelete() = runTest {
		val repository = FakeCharactersRepository(
			initialCharacters = listOf(CharacterEntry(id = "char-1", name = "Mira", hp = 10, maxHp = 10))
		)
		val viewModel = createCharacterViewModel(repository)
		advanceUntilIdle()

		viewModel.selectCharacter("char-1")
		advanceUntilIdle()
		assertEquals(10, viewModel.uiState.value.selectedCharacter?.hp)

		viewModel.updateCharacter(CharacterEntry(id = "char-1", name = "Mira", hp = 7, maxHp = 10))
		advanceUntilIdle()
		assertEquals(7, viewModel.uiState.value.selectedCharacter?.hp)

		viewModel.deleteCharacter(CharacterEntry(id = "char-1", name = "Mira"))
		advanceUntilIdle()
		assertNull(viewModel.uiState.value.selectedCharacter)
		assertTrue(viewModel.uiState.value.characters.isEmpty())
	}
}

private fun createCharacterViewModel(repository: FakeCharactersRepository): CharacterViewModel {
	return CharacterViewModel(
		getCharactersUseCase = GetCharactersUseCase(repository),
		getCharacterByIdUseCase = GetCharacterByIdUseCase(repository),
		addCharacterUseCase = AddCharacterUseCase(repository),
		updateCharacterUseCase = UpdateCharacterUseCase(repository),
		deleteCharacterUseCase = DeleteCharacterUseCase(repository),
		insertLogUseCase = InsertLogUseCase(FakeLogsRepository())
	)
}

private class FakeCharactersRepository(
	initialCharacters: List<CharacterEntry> = emptyList()
) : CharactersRepository {
	private val charactersFlow = MutableStateFlow(initialCharacters)

	override fun getAllCharacters(): Flow<List<CharacterEntry>> = charactersFlow

	override suspend fun getCharacterById(id: String): CharacterEntry? {
		return charactersFlow.value.find { it.id == id }
	}

	override suspend fun insertCharacter(character: CharacterEntry) {
		charactersFlow.value = listOf(character) + charactersFlow.value
	}

	override suspend fun updateCharacter(character: CharacterEntry) {
		charactersFlow.value = charactersFlow.value.map {
			if (it.id == character.id) character else it
		}
	}

	override suspend fun deleteCharacter(character: CharacterEntry) {
		charactersFlow.value = charactersFlow.value.filterNot { it.id == character.id }
	}
}

private class FakeLogsRepository : LogsRepository {
	override fun getAllLogs(): Flow<List<LogEntry>> = flowOf(emptyList())
	override suspend fun insertLog(log: LogEntry) = Unit
	override suspend fun clearLogs() = Unit
}

