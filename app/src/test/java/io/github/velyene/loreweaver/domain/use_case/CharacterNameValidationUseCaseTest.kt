package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.repository.CharactersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Test

class CharacterNameValidationUseCaseTest {

	@Test
	fun addCharacterUseCase_rejectsBlankName_whenNameIsBlank() = runBlocking {
		val repository = FakeCharactersRepository()
		val useCase = AddCharacterUseCase(repository)

		try {
			useCase(CharacterEntry(name = "   "))
			fail("Expected IllegalArgumentException for blank character name")
		} catch (exception: IllegalArgumentException) {
			assertEquals(ValidationMessages.CHARACTER_NAME_EMPTY_MESSAGE, exception.message)
		}
	}

	@Test
	fun updateCharacterUseCase_rejectsBlankName_whenNameIsBlank() = runBlocking {
		val repository = FakeCharactersRepository()
		val useCase = UpdateCharacterUseCase(repository)

		try {
			useCase(CharacterEntry(name = "\t"))
			fail("Expected IllegalArgumentException for blank character name")
		} catch (exception: IllegalArgumentException) {
			assertEquals(ValidationMessages.CHARACTER_NAME_EMPTY_MESSAGE, exception.message)
		}
	}

	@Test
	fun updateCharacterUseCase_updatesCharacter_whenNameIsNotBlank() = runBlocking {
		val repository = FakeCharactersRepository()
		val useCase = UpdateCharacterUseCase(repository)
		val character = CharacterEntry(name = "Meris")

		useCase(character)

		assertSame(character, repository.updatedCharacter)
	}
}

private class FakeCharactersRepository : CharactersRepository {
	var updatedCharacter: CharacterEntry? = null

	override fun getAllCharacters(): Flow<List<CharacterEntry>> = emptyFlow()

	override suspend fun getCharacterById(id: String): CharacterEntry? = null

	override suspend fun insertCharacter(character: CharacterEntry) = Unit

	override suspend fun updateCharacter(character: CharacterEntry) {
		updatedCharacter = character
	}

	override suspend fun deleteCharacter(character: CharacterEntry) = Unit
}
