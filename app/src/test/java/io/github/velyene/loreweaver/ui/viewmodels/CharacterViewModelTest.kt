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
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterViewModelTest {

	@get:Rule
	val mainDispatcherRule = MainDispatcherRule()

	@Test
	fun updateCharacter_updatesSelectedCharacterImmediately() {
		runTest {
			val initialCharacter = CharacterEntry(id = "hero-1", name = "Aria", party = "Adventurers", hp = 12, maxHp = 12)
			val repository = TestCharactersRepository(listOf(initialCharacter))
			val viewModel = createViewModel(repository)

			viewModel.selectCharacter(initialCharacter.id)
			advanceUntilIdle()

			val updatedCharacter = initialCharacter.copy(activeConditions = setOf("Cursed"))
			viewModel.updateCharacter(updatedCharacter)
			advanceUntilIdle()

			assertEquals(setOf("Cursed"), viewModel.uiState.value.selectedCharacter?.activeConditions)
		}
	}

	@Test
	fun deleteCharacter_clearsSelectedCharacterImmediately() {
		runTest {
			val initialCharacter = CharacterEntry(id = "hero-1", name = "Aria", party = "Adventurers", hp = 12, maxHp = 12)
			val repository = TestCharactersRepository(listOf(initialCharacter))
			val viewModel = createViewModel(repository)

			viewModel.selectCharacter(initialCharacter.id)
			advanceUntilIdle()
			viewModel.deleteCharacter(initialCharacter)
			advanceUntilIdle()

			assertNull(viewModel.uiState.value.selectedCharacter)
		}
	}

	private fun createViewModel(repository: TestCharactersRepository): CharacterViewModel {
		return CharacterViewModel(
			getCharactersUseCase = GetCharactersUseCase(repository),
			getCharacterByIdUseCase = GetCharacterByIdUseCase(repository),
			addCharacterUseCase = AddCharacterUseCase(repository),
			updateCharacterUseCase = UpdateCharacterUseCase(repository),
			deleteCharacterUseCase = DeleteCharacterUseCase(repository),
			insertLogUseCase = InsertLogUseCase(TestLogsRepository()),
		)
	}
}

private class TestCharactersRepository(initialCharacters: List<CharacterEntry>) : CharactersRepository {
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

private class TestLogsRepository : LogsRepository {
	private val logsFlow = MutableStateFlow<List<LogEntry>>(emptyList())

	override fun getAllLogs(): Flow<List<LogEntry>> = logsFlow

	override suspend fun insertLog(log: LogEntry) {
		logsFlow.value += log
	}

	override suspend fun clearLogs() {
		logsFlow.value = emptyList()
	}
}

