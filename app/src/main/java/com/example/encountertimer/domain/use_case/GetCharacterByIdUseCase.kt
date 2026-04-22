package com.example.encountertimer.domain.use_case

import com.example.encountertimer.domain.model.CharacterEntry
import com.example.encountertimer.domain.repository.CharactersRepository
import javax.inject.Inject

class GetCharacterByIdUseCase @Inject constructor(
	private val repository: CharactersRepository
) {
	suspend operator fun invoke(id: String): CharacterEntry? {
		return repository.getCharacterById(id)
	}
}
