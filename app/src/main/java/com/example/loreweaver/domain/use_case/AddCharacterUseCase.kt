package com.example.loreweaver.domain.use_case

import com.example.loreweaver.domain.model.CharacterEntry
import com.example.loreweaver.domain.repository.CharactersRepository
import javax.inject.Inject

class AddCharacterUseCase @Inject constructor(
	private val repository: CharactersRepository
) {
	suspend operator fun invoke(character: CharacterEntry) {
		if (character.name.isBlank()) {
			throw IllegalArgumentException("Character name cannot be empty")
		}
		repository.insertCharacter(character)
	}
}
