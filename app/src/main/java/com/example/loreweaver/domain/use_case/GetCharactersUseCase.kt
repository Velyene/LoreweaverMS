package com.example.loreweaver.domain.use_case

import com.example.loreweaver.domain.model.CharacterEntry
import com.example.loreweaver.domain.repository.CharactersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
	private val repository: CharactersRepository
) {
	operator fun invoke(): Flow<List<CharacterEntry>> {
		return repository.getAllCharacters()
	}
}
