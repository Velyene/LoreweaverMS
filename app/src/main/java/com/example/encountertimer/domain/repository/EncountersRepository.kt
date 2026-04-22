package com.example.encountertimer.domain.repository

import com.example.encountertimer.domain.model.CombatantState
import com.example.encountertimer.domain.model.Encounter
import kotlinx.coroutines.flow.Flow

interface EncountersRepository {
	fun getEncountersForCampaign(campaignId: String): Flow<List<Encounter>>
	suspend fun getEncounterById(encounterId: String): Encounter?
	suspend fun insertEncounter(encounter: Encounter)
	suspend fun addCombatantsToEncounter(encounterId: String, combatants: List<CombatantState>)
	suspend fun getActiveEncounter(): Encounter?
	suspend fun setActiveEncounter(encounterId: String)
}

