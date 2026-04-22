package com.example.loreweaver.domain.use_case

import com.example.loreweaver.domain.model.CharacterEntry
import com.example.loreweaver.domain.repository.CharactersRepository
import javax.inject.Inject

class GetCharacterByIdUseCase @Inject constructor(
	private val repository: CharactersRepository
) {
	suspend operator fun invoke(id: String): CharacterEntry? {
		return repository.getCharacterById(id)
	}
}
