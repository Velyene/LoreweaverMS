/*
 * FILE: CombatPersistenceSupport.kt
 *
 * TABLE OF CONTENTS:
 * 1. Encounter Name Resolution
 * 2. Encounter Persistence Builders
 * 3. Session Snapshot Builders
 * 4. Reward Summary Support
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterGenerationDetails
import io.github.velyene.loreweaver.domain.model.EncounterRewardSummary
import io.github.velyene.loreweaver.domain.model.EncounterRewardTemplate
import io.github.velyene.loreweaver.domain.model.EncounterSnapshot
import io.github.velyene.loreweaver.domain.model.EncounterStatus
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.util.CharacterParty
import io.github.velyene.loreweaver.domain.util.buildInitialRewardReview
import io.github.velyene.loreweaver.domain.util.calculateEncounterRewards

internal fun resolveEncounterName(
	existingEncounter: Encounter?,
	currentEncounterName: String,
	combatTextProvider: CombatTextProvider,
	timestampMillis: Long = System.currentTimeMillis()
): String {
	return existingEncounter?.name ?: currentEncounterName.ifBlank {
		combatTextProvider.quickEncounterName(timestampMillis)
	}
}

internal fun buildStartedEncounter(
	encounterId: String,
	existingEncounter: Encounter?,
	state: CombatUiState,
	sortedCombatants: List<io.github.velyene.loreweaver.domain.model.CombatantState>,
	combatTextProvider: CombatTextProvider
): Encounter {
	return Encounter(
		id = encounterId,
		campaignId = existingEncounter?.campaignId,
		name = resolveEncounterName(existingEncounter, state.currentEncounterName, combatTextProvider),
		notes = state.encounterNotes,
		status = EncounterStatus.ACTIVE,
		currentRound = state.currentRound.coerceAtLeast(1),
		currentTurnIndex = state.currentTurnIndex.coerceIn(0, sortedCombatants.lastIndex),
		participants = sortedCombatants,
		activeLog = state.activeStatuses,
		rewardTemplate = existingEncounter?.rewardTemplate ?: EncounterRewardTemplate(),
		generationSettings = state.generationSettings,
		generationDetails = state.generationDetails
	)
}

internal fun buildActiveEncounterCheckpoint(
	encounterId: String,
	existingEncounter: Encounter?,
	state: CombatUiState,
	combatTextProvider: CombatTextProvider
): Encounter {
	return Encounter(
		id = encounterId,
		campaignId = existingEncounter?.campaignId,
		name = resolveEncounterName(existingEncounter, state.currentEncounterName, combatTextProvider),
		notes = state.encounterNotes,
		status = EncounterStatus.ACTIVE,
		currentRound = state.currentRound.coerceAtLeast(1),
		currentTurnIndex = normalizedTurnIndex(state.combatants, state.currentTurnIndex),
		participants = state.combatants,
		activeLog = state.activeStatuses,
		rewardTemplate = existingEncounter?.rewardTemplate ?: EncounterRewardTemplate(),
		generationSettings = state.generationSettings,
		generationDetails = state.generationDetails
	)
}

internal fun buildEncounterSession(
	encounterId: String,
	state: CombatUiState,
	isCompleted: Boolean,
	encounterResult: String?,
	rewardTemplate: EncounterRewardTemplate = EncounterRewardTemplate(),
	combatTextProvider: CombatTextProvider,
	timestampMillis: Long = System.currentTimeMillis()
): SessionRecord {
	val rewards = if (isCompleted) {
		calculateEncounterRewards(
			encounterName = state.currentEncounterName.ifBlank {
				combatTextProvider.encounterSessionTitle(timestampMillis)
			},
			encounterResult = encounterResult,
			combatants = state.combatants,
			characters = state.availableCharacters + syntheticGeneratedEnemyCharacters(state.generationDetails),
			totalRounds = state.currentRound,
			rewardTemplate = rewardTemplate,
			encounterDifficulty = state.encounterDifficulty
		)
	} else {
		null
	}
	return SessionRecord(
		encounterId = encounterId,
		title = state.currentEncounterName.ifBlank {
			combatTextProvider.encounterSessionTitle(timestampMillis)
		},
		log = state.activeStatuses,
		snapshot = EncounterSnapshot(
			combatants = state.combatants,
			currentTurnIndex = state.currentTurnIndex,
			currentRound = state.currentRound
		),
		isCompleted = isCompleted,
		encounterResult = encounterResult,
		rewards = rewards,
		rewardReview = buildInitialRewardReview(rewards)
	)
}

internal fun buildPendingEncounter(
	encounterId: String,
	existingEncounter: Encounter?,
	state: CombatUiState,
	combatTextProvider: CombatTextProvider
): Encounter {
	return Encounter(
		id = encounterId,
		campaignId = existingEncounter?.campaignId,
		name = resolveEncounterName(existingEncounter, state.currentEncounterName, combatTextProvider),
		notes = state.encounterNotes,
		status = EncounterStatus.PENDING,
		currentRound = state.currentRound,
		currentTurnIndex = state.currentTurnIndex,
		participants = state.combatants,
		activeLog = state.activeStatuses,
		rewardTemplate = existingEncounter?.rewardTemplate ?: EncounterRewardTemplate(),
		generationSettings = state.generationSettings,
		generationDetails = state.generationDetails
	)
}

internal fun deriveEncounterResultFromState(state: CombatUiState): String {
	val charactersById = state.availableCharacters.associateBy(CharacterEntry::id)
	val players = state.combatants.filter { combatant ->
		charactersById[combatant.characterId]?.party == CharacterParty.ADVENTURERS
	}
	val enemies = state.combatants.filter { combatant ->
		charactersById[combatant.characterId]?.party != CharacterParty.ADVENTURERS
	}
	val survivingPlayers = players.filter { it.currentHp > 0 }
	val activeEnemies = enemies.filter { it.currentHp > 0 }
	return when {
		survivingPlayers.isNotEmpty() && activeEnemies.isEmpty() -> "VICTORY"
		players.isNotEmpty() && survivingPlayers.isEmpty() && activeEnemies.isNotEmpty() -> "DEFEAT"
		else -> "ENDED_EARLY"
	}
}

internal fun buildEncounterRewardSummary(
	state: CombatUiState,
	encounterResult: String?,
	rewardTemplate: EncounterRewardTemplate = EncounterRewardTemplate()
): EncounterRewardSummary {
	return calculateEncounterRewards(
		encounterName = state.currentEncounterName,
		encounterResult = encounterResult,
		combatants = state.combatants,
		characters = state.availableCharacters + syntheticGeneratedEnemyCharacters(state.generationDetails),
		totalRounds = state.currentRound,
		rewardTemplate = rewardTemplate,
		encounterDifficulty = state.encounterDifficulty
	)
}

private fun syntheticGeneratedEnemyCharacters(generationDetails: EncounterGenerationDetails?): List<CharacterEntry> {
	return generationDetails?.finalEnemies?.map { generatedEnemy ->
		CharacterEntry(
			id = generatedEnemy.combatantId,
			name = generatedEnemy.name,
			party = CharacterParty.MONSTERS,
			hp = generatedEnemy.hp,
			maxHp = generatedEnemy.hp,
			initiative = generatedEnemy.initiative,
			type = generatedEnemy.creatureType,
			challengeRating = generatedEnemy.challengeRating,
			ac = 10
		)
	}.orEmpty()
}
