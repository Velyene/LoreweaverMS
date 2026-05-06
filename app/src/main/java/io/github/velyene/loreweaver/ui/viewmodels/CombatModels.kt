/*
 * FILE: CombatModels.kt
 *
 * TABLE OF CONTENTS:
 * 1. Initiative Sorting Helpers
 * 2. Combat UI State Models
 * 3. Encounter and Turn Enums
 * 4. State Mapping Helpers
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.EncounterGenerationDetails
import io.github.velyene.loreweaver.domain.model.EncounterGenerationSettings
import io.github.velyene.loreweaver.domain.util.EncounterDifficultyResult
import io.github.velyene.loreweaver.ui.util.UiText

internal fun sortCombatantsForInitiative(combatants: List<CombatantState>): List<CombatantState> {
	return combatants.sortedByDescending(CombatantState::initiative)
}

data class CombatUiState(
	val isCombatActive: Boolean = false,
	val currentEncounterId: String? = null,
	val currentEncounterName: String = "",
	val encounterLifecycle: EncounterLifecycle = EncounterLifecycle.DRAFT,
	val initiativeMode: InitiativeMode = InitiativeMode.MANUAL,
	val combatants: List<CombatantState> = emptyList(),
	val availableCharacters: List<CharacterEntry> = emptyList(),
	val currentTurnIndex: Int = 0,
	val currentRound: Int = 1,
	val encounterNotes: String = "",
	val activeStatuses: List<String> = emptyList(),
	val canGoToPreviousTurn: Boolean = false,
	val turnStep: CombatTurnStep = CombatTurnStep.SELECT_ACTION,
	val pendingAction: PendingTurnAction? = null,
	val selectedTargetId: String? = null,
	val encounterDifficulty: EncounterDifficultyResult? = null,
	val generationSettings: EncounterGenerationSettings = EncounterGenerationSettings(),
	val generationDetails: EncounterGenerationDetails? = null,
	val isLoading: Boolean = false,
	val error: UiText? = null,
	val onRetry: (() -> Unit)? = null
)

enum class InitiativeMode {
	MANUAL,
	AUTO_ROLL,
	AUTO_SORT
}

enum class EncounterState {
	DRAFT,
	ACTIVE,
	PAUSED,
	COMPLETED,
	ARCHIVED
}

data class EncounterSetupState(
	val encounterName: String = "",
	val selectedPlayers: List<String> = emptyList(),
	val selectedEnemies: List<String> = emptyList(),
	val locationTerrain: String = "",
	val notes: String = "",
	val initiativeMode: InitiativeMode = InitiativeMode.MANUAL
)

data class EncounterParticipant(
	val id: String,
	val name: String,
	val initiative: Int,
	val currentHp: Int,
	val maxHp: Int,
	val isPlayer: Boolean,
	val conditions: List<String> = emptyList()
)

data class EncounterSession(
	val id: String,
	val encounterName: String,
	val roundNumber: Int,
	val currentTurnIndex: Int,
	val participants: List<EncounterParticipant>,
	val logEntries: List<String>,
	val state: EncounterState
)

enum class CombatTurnStep {
	SELECT_ACTION,
	SELECT_TARGET,
	APPLY_RESULT,
	READY_TO_END
}

enum class EncounterLifecycle {
	DRAFT,
	ACTIVE,
	PAUSED,
	COMPLETED,
	ARCHIVED
}

enum class ActionResolutionType {
	MISS,
	DAMAGE,
	HEAL
}

data class PendingTurnAction(
	val name: String,
	val isAttack: Boolean,
	val allowsSelfTarget: Boolean,
	val manaCost: Int = 0,
	val staminaCost: Int = 0,
	val spellSlotLevel: Int? = null,
	val resourceName: String? = null,
	val resourceCost: Int = 0,
	val itemName: String? = null,
	val useSummary: String = ""
)

internal data class HpChangeResult(
	val combatant: CombatantState,
	val oldHp: Int,
	val newHp: Int
)

internal fun CombatUiState.asEncounterSetupState(): EncounterSetupState {
	val encounterInfo = parseEncounterInfo(encounterNotes)
	val availableCharactersById = availableCharacters.associateBy(CharacterEntry::id)
	val selectedPlayers = combatants
		.map(CombatantState::characterId)
		.filter { characterId -> availableCharactersById[characterId]?.isAdventurer() == true }
	val selectedEnemies = combatants
		.map(CombatantState::characterId)
		.filterNot { characterId -> availableCharactersById[characterId]?.isAdventurer() == true }
	return EncounterSetupState(
		encounterName = currentEncounterName,
		selectedPlayers = selectedPlayers,
		selectedEnemies = selectedEnemies,
		locationTerrain = encounterInfo.locationTerrain,
		notes = encounterInfo.notesBody,
		initiativeMode = initiativeMode,
	)
}

internal fun CombatUiState.asEncounterSession(): EncounterSession {
	val availableCharactersById = availableCharacters.associateBy(CharacterEntry::id)
	return EncounterSession(
		id = currentEncounterId.orEmpty(),
		encounterName = currentEncounterName,
		roundNumber = currentRound,
		currentTurnIndex = currentTurnIndex,
		participants = combatants.map { combatant ->
			EncounterParticipant(
				id = combatant.characterId,
				name = combatant.name,
				initiative = combatant.initiative,
				currentHp = combatant.currentHp,
				maxHp = combatant.maxHp,
				isPlayer = availableCharactersById[combatant.characterId]?.isAdventurer() == true,
				conditions = combatant.conditions.map { condition -> condition.name },
			)
		},
		logEntries = activeStatuses,
		state = encounterLifecycle.asEncounterState(),
	)
}

private fun EncounterLifecycle.asEncounterState(): EncounterState {
	return when (this) {
		EncounterLifecycle.DRAFT -> EncounterState.DRAFT
		EncounterLifecycle.ACTIVE -> EncounterState.ACTIVE
		EncounterLifecycle.PAUSED -> EncounterState.PAUSED
		EncounterLifecycle.COMPLETED -> EncounterState.COMPLETED
		EncounterLifecycle.ARCHIVED -> EncounterState.ARCHIVED
	}
}

internal fun resolveEncounterStartCombatants(
	combatants: List<CombatantState>,
	initiativeMode: InitiativeMode,
	rollInitiative: () -> Int = { kotlin.random.Random.nextInt(1, 21) }
): List<CombatantState> {
	return when (initiativeMode) {
		InitiativeMode.MANUAL -> combatants
		InitiativeMode.AUTO_SORT -> sortCombatantsForInitiative(combatants)
		InitiativeMode.AUTO_ROLL -> sortCombatantsForInitiative(
			combatants.map { combatant ->
				combatant.copy(initiative = combatant.initiative + rollInitiative())
			}
		)
	}
}
