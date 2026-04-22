package com.example.encountertimer.domain.use_case

import com.example.encountertimer.domain.model.CombatantState
import com.example.encountertimer.domain.model.RemoteItem
import com.example.encountertimer.domain.repository.EncountersRepository
import javax.inject.Inject
import kotlin.random.Random

/**
 * Use case for adding selected monster items to an encounter.
 * External API-backed monster imports were removed, so selected entries use local default stats.
 */
class AddMonstersToEncounterUseCase @Inject constructor(
	private val repository: EncountersRepository
) {
	/**
	 * Adds selected monsters to an encounter.
	 * @param encounterId The ID of the encounter to add monsters to
	 * @param selectedMonsters List of monsters selected from the library
	 */
	suspend operator fun invoke(
		encounterId: String,
		selectedMonsters: List<RemoteItem>
	) {
		if (selectedMonsters.isEmpty()) return

		// Convert RemoteItems to CombatantStates with local default data.
		val combatants = selectedMonsters.map { monster ->
			CombatantState(
				characterId = java.util.UUID.randomUUID().toString(),
				name = monster.name,
				initiative = Random.nextInt(1, 21),
				currentHp = 50,
				maxHp = 50,
				conditions = emptyList()
			)
		}

		// Add all combatants to the encounter
		repository.addCombatantsToEncounter(encounterId, combatants)
	}
}

