package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.repository.CharactersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
	private val repository: CharactersRepository
) {
	operator fun invoke(): Flow<List<CharacterEntry>> {
		return repository.getAllCharacters()
	}
}
