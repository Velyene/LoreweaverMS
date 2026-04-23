package com.example.loreweaver.domain.repository

import com.example.loreweaver.domain.model.CharacterEntry
import kotlinx.coroutines.flow.Flow

interface CharactersRepository {
	fun getAllCharacters(): Flow<List<CharacterEntry>>
	suspend fun getCharacterById(id: String): CharacterEntry?
	suspend fun insertCharacter(character: CharacterEntry)
	suspend fun updateCharacter(character: CharacterEntry)
	suspend fun deleteCharacter(character: CharacterEntry)
}
