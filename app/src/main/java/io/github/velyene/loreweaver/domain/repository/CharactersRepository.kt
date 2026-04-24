package io.github.velyene.loreweaver.domain.repository

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import kotlinx.coroutines.flow.Flow

interface CharactersRepository {
	fun getAllCharacters(): Flow<List<CharacterEntry>>
	suspend fun getCharacterById(id: String): CharacterEntry?
	suspend fun insertCharacter(character: CharacterEntry)
	suspend fun updateCharacter(character: CharacterEntry)
	suspend fun deleteCharacter(character: CharacterEntry)
}
