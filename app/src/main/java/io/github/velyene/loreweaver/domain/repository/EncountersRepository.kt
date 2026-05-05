package io.github.velyene.loreweaver.domain.repository

import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Encounter
import kotlinx.coroutines.flow.Flow

interface EncountersRepository {
	fun getAllEncounters(): Flow<List<Encounter>>
	fun getEncountersForCampaign(campaignId: String): Flow<List<Encounter>>
	suspend fun getEncounterById(encounterId: String): Encounter?
	suspend fun insertEncounter(encounter: Encounter)
	suspend fun updateEncounter(encounter: Encounter)
	suspend fun deleteEncounter(encounter: Encounter)
	suspend fun addCombatantsToEncounter(encounterId: String, combatants: List<CombatantState>)
	suspend fun getActiveEncounter(): Encounter?
	suspend fun setActiveEncounter(encounterId: String)
}
