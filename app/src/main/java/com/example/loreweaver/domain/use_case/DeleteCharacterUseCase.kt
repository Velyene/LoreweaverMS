package com.example.loreweaver.domain.use_case

import com.example.loreweaver.domain.model.CharacterEntry
import com.example.loreweaver.domain.repository.CharactersRepository
import javax.inject.Inject

class DeleteCharacterUseCase @Inject constructor(
	private val repository: CharactersRepository
) {
	suspend operator fun invoke(character: CharacterEntry) {
		repository.deleteCharacter(character)
	}
}
