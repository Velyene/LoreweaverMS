package com.example.encountertimer.domain.use_case

import com.example.encountertimer.domain.model.CharacterEntry
import com.example.encountertimer.domain.repository.CharactersRepository
import javax.inject.Inject

class DeleteCharacterUseCase @Inject constructor(
	private val repository: CharactersRepository
) {
	suspend operator fun invoke(character: CharacterEntry) {
		repository.deleteCharacter(character)
	}
}
