package com.example.encountertimer.domain.use_case

import com.example.encountertimer.domain.model.CharacterEntry
import com.example.encountertimer.domain.repository.CharactersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
	private val repository: CharactersRepository
) {
	operator fun invoke(): Flow<List<CharacterEntry>> {
		return repository.getAllCharacters()
	}
}
