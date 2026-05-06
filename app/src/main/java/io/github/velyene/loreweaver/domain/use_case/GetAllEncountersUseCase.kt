package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.repository.EncountersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllEncountersUseCase @Inject constructor(
	private val repository: EncountersRepository
) {
	operator fun invoke(): Flow<List<Encounter>> {
		return repository.getAllEncounters()
	}
}

