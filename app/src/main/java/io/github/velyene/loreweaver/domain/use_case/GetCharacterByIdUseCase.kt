package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.repository.CharactersRepository
import javax.inject.Inject

class GetCharacterByIdUseCase @Inject constructor(
	private val repository: CharactersRepository
) {
	suspend operator fun invoke(id: String): CharacterEntry? {
		return repository.getCharacterById(id)
	}
}
