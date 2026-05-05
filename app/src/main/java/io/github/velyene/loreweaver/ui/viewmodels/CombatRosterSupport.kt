/*
 * FILE: CombatRosterSupport.kt
 *
 * TABLE OF CONTENTS:
 * 1. Encounter Difficulty Calculation
 * 2. Enemy Combatant Builders
 * 3. Combatant State Mutation Helpers
 * 4. Enemy Naming Support
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.util.EncounterDifficulty
import io.github.velyene.loreweaver.domain.util.EncounterDifficultyResult
import io.github.velyene.loreweaver.domain.util.CharacterParty
import java.util.UUID

internal fun calculateEncounterDifficulty(state: CombatUiState): EncounterDifficultyResult? {
	val generatedEnemiesById = state.generationDetails
		?.finalEnemies
		?.associate { generatedEnemy ->
			generatedEnemy.combatantId to CharacterEntry(
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
		}
		.orEmpty()
	val charactersById = state.availableCharacters.associateBy(CharacterEntry::id) + generatedEnemiesById
	val selectedCharacters = state.combatants.mapNotNull { combatant ->
		charactersById[combatant.characterId]
	}
	val partyMembers = selectedCharacters.filter { it.isAdventurer() }
	val enemies = state.combatants.filter { combatant ->
		charactersById[combatant.characterId]?.isAdventurer() != true
	}
	val enemyCRMap = enemies.associate { combatant ->
		combatant.characterId to (charactersById[combatant.characterId]?.challengeRating ?: 0.0)
	}
	return EncounterDifficulty.calculateDifficulty(
		partyMembers = partyMembers,
		enemies = enemies,
		enemyCRMap = enemyCRMap
	)
}

internal fun buildEnemyCombatants(
	name: String,
	hp: Int,
	initiative: Int,
	quantity: Int,
	idFactory: () -> String = { UUID.randomUUID().toString() }
): List<CombatantState> {
	val trimmedName = name.trim()
	if (trimmedName.isBlank()) return emptyList()

	val sanitizedQuantity = quantity.coerceAtLeast(1)
	return List(sanitizedQuantity) { index ->
		CombatantState(
			characterId = idFactory(),
			name = formatEnemyName(trimmedName, index, sanitizedQuantity),
			initiative = initiative,
			currentHp = hp,
			maxHp = hp
		)
	}
}

internal fun CombatUiState.withAppendedCombatants(
	newCombatants: List<CombatantState>,
	distinctByCharacterId: Boolean = false
): CombatUiState {
	if (newCombatants.isEmpty()) return this
	val updatedCombatants = if (distinctByCharacterId) {
		(combatants + newCombatants).distinctBy(CombatantState::characterId)
	} else {
		combatants + newCombatants
	}
	return copy(
		combatants = updatedCombatants,
		currentTurnIndex = normalizedTurnIndex(updatedCombatants, currentTurnIndex)
	)
}

internal fun CombatUiState.withUpdatedCombatantInitiative(
	characterId: String,
	initiative: Int
): CombatUiState = copy(
	combatants = combatants.map { combatant ->
		if (combatant.characterId == characterId) {
			combatant.copy(initiative = initiative)
		} else {
			combatant
		}
	}
)

internal fun CombatUiState.withCombatantUpdated(
	characterId: String,
	transform: (CombatantState) -> CombatantState
): CombatUiState = copy(
	combatants = combatants.map { combatant ->
		if (combatant.characterId == characterId) transform(combatant) else combatant
	}
)

internal fun CombatUiState.withRemovedCombatant(characterId: String): CombatUiState {
	val updatedCombatants = combatants.filter { combatant -> combatant.characterId != characterId }
	val removedCurrentCombatant = currentCombatant()?.characterId == characterId
	val shouldClearPendingTurn =
		updatedCombatants.isEmpty() || removedCurrentCombatant || selectedTargetId == characterId
	val requestedIndex = if (removedCurrentCombatant && currentTurnIndex > 0) {
		currentTurnIndex - 1
	} else {
		currentTurnIndex
	}
	val baseState = if (shouldClearPendingTurn) {
		clearPendingTurnState()
	} else {
		this
	}
	return baseState.copy(
		combatants = updatedCombatants,
		currentTurnIndex = normalizedTurnIndex(updatedCombatants, requestedIndex)
	)
}

internal fun CombatUiState.withUpdatedTempHp(characterId: String, tempHp: Int): CombatUiState = copy(
	combatants = combatants.map { combatant ->
		if (combatant.characterId == characterId) {
			combatant.copy(tempHp = tempHp.coerceAtLeast(0))
		} else {
			combatant
		}
	}
)

internal fun CombatUiState.combatantById(characterId: String): CombatantState? =
	combatants.find { it.characterId == characterId }

internal fun CombatUiState.reorderedCombatant(
	characterId: String,
	moveBy: Int
): CombatUiState {
	val visibleOrder = sortCombatantsForInitiative(combatants)
	val currentIndex = visibleOrder.indexOfFirst { it.characterId == characterId }
	if (currentIndex == -1) return this

	val targetIndex = currentIndex + moveBy
	if (targetIndex !in visibleOrder.indices) return this

	val currentCombatant = visibleOrder[currentIndex]
	val targetCombatant = visibleOrder[targetIndex]
	val updatedCombatantsById = combatants
		.map { combatant ->
			when (combatant.characterId) {
				currentCombatant.characterId -> combatant.copy(initiative = targetCombatant.initiative)
				targetCombatant.characterId -> combatant.copy(initiative = currentCombatant.initiative)
				else -> combatant
			}
		}
		.associateBy(CombatantState::characterId)

	val reorderedVisibleOrder = visibleOrder.toMutableList().apply {
		this[currentIndex] = visibleOrder[targetIndex]
		this[targetIndex] = visibleOrder[currentIndex]
	}.map { combatant -> updatedCombatantsById.getValue(combatant.characterId) }

	return copy(
		combatants = reorderedVisibleOrder,
		currentTurnIndex = normalizedTurnIndex(reorderedVisibleOrder, currentTurnIndex)
	)
}

private fun formatEnemyName(baseName: String, index: Int, quantity: Int): String {
	return if (quantity == 1) baseName else "$baseName ${index + 1}"
}
