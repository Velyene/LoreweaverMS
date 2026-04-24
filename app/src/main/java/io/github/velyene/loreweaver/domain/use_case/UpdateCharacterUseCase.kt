package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.repository.CharactersRepository
import javax.inject.Inject

class UpdateCharacterUseCase @Inject constructor(
	private val repository: CharactersRepository
) {
	suspend operator fun invoke(character: CharacterEntry) {
		require(character.name.isNotBlank()) { ValidationMessages.CHARACTER_NAME_EMPTY_MESSAGE }
		repository.updateCharacter(character)
	}
}
