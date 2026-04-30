/*
 * FILE: CombatModels.kt
 *
 * TABLE OF CONTENTS:
 * 1. CombatUiState model
 * 2. Turn/lifecycle/action enums
 * 3. PendingTurnAction and HP support models
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.util.EncounterDifficultyResult

data class CombatUiState(
	val isCombatActive: Boolean = false,
	val currentEncounterId: String? = null,
	val currentEncounterName: String = "",
	val encounterLifecycle: EncounterLifecycle = EncounterLifecycle.DRAFT,
	val combatants: List<CombatantState> = emptyList(),
	val availableCharacters: List<CharacterEntry> = emptyList(),
	val currentTurnIndex: Int = 0,
	val currentRound: Int = 1,
	val encounterNotes: String = "",
	val activeStatuses: List<String> = emptyList(),
	val turnStep: CombatTurnStep = CombatTurnStep.SELECT_ACTION,
	val pendingAction: PendingTurnAction? = null,
	val selectedTargetId: String? = null,
	val encounterDifficulty: EncounterDifficultyResult? = null,
	val isLoading: Boolean = false,
	val error: String? = null,
	val onRetry: (() -> Unit)? = null
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
	val allowsSelfTarget: Boolean
)

internal data class HpChangeResult(
	val combatant: CombatantState,
	val oldHp: Int,
	val newHp: Int
)

