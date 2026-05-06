/*
 * FILE: CombatViewModelTestSupport.kt
 *
 * TABLE OF CONTENTS:
 * 1. Shared test constants and factory helpers
 * 2. Shared CombatTextProvider test helpers
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.repository.CampaignRepository
import io.github.velyene.loreweaver.domain.use_case.GetActiveEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCharactersUseCase
import io.github.velyene.loreweaver.domain.use_case.GetEncounterByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetSessionsForEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertLogUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertSessionRecordUseCase
import io.github.velyene.loreweaver.domain.use_case.SetActiveEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.UpdateCharacterUseCase

internal const val HERO_ID = "hero-1"
internal const val HERO_NAME = "Hero"
internal const val GOBLIN_ID = "goblin-1"
internal const val GOBLIN_NAME = "Goblin"
internal const val MONSTER_ID = "monster-1"

internal fun createCombatViewModel(
	repository: CampaignRepository,
	combatTextProvider: CombatTextProvider = testCombatTextProvider
): CombatViewModel {
	return CombatViewModel(
		getCharactersUseCase = GetCharactersUseCase(repository),
		getActiveEncounterUseCase = GetActiveEncounterUseCase(repository),
		getEncounterByIdUseCase = GetEncounterByIdUseCase(repository),
		getSessionsForEncounterUseCase = GetSessionsForEncounterUseCase(repository),
		insertEncounterUseCase = InsertEncounterUseCase(repository),
		setActiveEncounterUseCase = SetActiveEncounterUseCase(repository),
		insertLogUseCase = InsertLogUseCase(repository),
		insertSessionRecordUseCase = InsertSessionRecordUseCase(repository),
		updateCharacterUseCase = UpdateCharacterUseCase(repository),
		combatTextProvider = combatTextProvider
	)
}

internal val testCombatTextProvider: CombatTextProvider = object : CombatTextProvider {
	override fun roundBeginsStatus(round: Int): String = "Round $round begins"

	override fun quickEncounterName(timestampMillis: Long): String {
		return "Quick Encounter $timestampMillis"
	}

	override fun encounterSessionTitle(timestampMillis: Long): String {
		return "Encounter Session - $timestampMillis"
	}
}

internal fun combatant(id: String, name: String, initiative: Int, hp: Int): CombatantState {
	return CombatantState(
		characterId = id,
		name = name,
		initiative = initiative,
		currentHp = hp,
		maxHp = hp
	)
}


