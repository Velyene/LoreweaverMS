/*
 * FILE: CombatInteractionSupport.kt
 *
 * TABLE OF CONTENTS:
 * 1. Combatant HP and turn-selection support
 * 2. Action-resolution support
 * 3. Condition and temp-HP support
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.use_case.InsertLogUseCase
import io.github.velyene.loreweaver.domain.use_case.UpdateCharacterUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex

internal fun applyCombatantHpChange(
	uiState: MutableStateFlow<CombatUiState>,
	characterId: String,
	delta: Int
) {
	val (updatedState, hpChange) = uiState.value.applyHpDelta(characterId, delta) ?: return
	uiState.value = updatedState
	appendHpStatus(uiState, hpChange = hpChange, delta = delta)
	appendDefeatRecoveryStatus(uiState, hpChange)
}

internal fun handleActionSelection(
	uiState: MutableStateFlow<CombatUiState>,
	action: String
) {
	val state = uiState.value
	val current = state.currentCombatant() ?: return
	val pendingAction = resolvePendingAction(state.availableCharacters, current.characterId, action)
	val targetCandidates = targetCandidates(state, pendingAction)

	uiState.update {
		it.copy(
			pendingAction = pendingAction,
			selectedTargetId = null,
			turnStep = if (targetCandidates.isEmpty()) {
				CombatTurnStep.APPLY_RESULT
			} else {
				CombatTurnStep.SELECT_TARGET
			}
		)
	}
}

internal fun handleTargetSelection(
	uiState: MutableStateFlow<CombatUiState>,
	targetId: String
) {
	val state = uiState.value
	val pendingAction = state.pendingAction ?: return
	val validTarget = targetCandidates(state, pendingAction)
		.any { it.characterId == targetId }
	if (!validTarget) return

	uiState.update {
		it.copy(
			selectedTargetId = targetId,
			turnStep = CombatTurnStep.APPLY_RESULT
		)
	}
}

internal fun clearPendingTurnSelection(uiState: MutableStateFlow<CombatUiState>) {
	uiState.update { it.clearPendingTurnState() }
}

internal fun applyPendingActionResult(
	uiState: MutableStateFlow<CombatUiState>,
	scope: CoroutineScope,
	insertLogUseCase: InsertLogUseCase,
	resultType: ActionResolutionType,
	amount: Int? = null
) {
	val state = uiState.value
	val actor = state.currentCombatant() ?: return
	val pendingAction = state.pendingAction ?: return
	val target = state.resolveSelectedTarget() ?: return

	when (resultType) {
		ActionResolutionType.MISS -> {
			appendAndPersistStatus(
				uiState = uiState,
				scope = scope,
				insertLogUseCase = insertLogUseCase,
				message = buildActionResultStatusMessage(
					actorName = actor.name,
					actionName = pendingAction.name,
					targetName = target.name,
					resultType = resultType
				)
			)
		}

		ActionResolutionType.DAMAGE -> {
			val damage = amount?.coerceAtLeast(0) ?: return
			val (updatedState, hpChange) = uiState.value.applyHpDelta(target.characterId, -damage) ?: return
			uiState.value = updatedState
			appendAndPersistStatus(
				uiState = uiState,
				scope = scope,
				insertLogUseCase = insertLogUseCase,
				message = buildActionResultStatusMessage(
					actorName = actor.name,
					actionName = pendingAction.name,
					targetName = target.name,
					resultType = resultType,
					amount = damage
				)
			)
			appendDefeatRecoveryStatus(uiState, hpChange)
		}

		ActionResolutionType.HEAL -> {
			val healing = amount?.coerceAtLeast(0) ?: return
			val (updatedState, hpChange) = uiState.value.applyHpDelta(target.characterId, healing) ?: return
			uiState.value = updatedState
			appendAndPersistStatus(
				uiState = uiState,
				scope = scope,
				insertLogUseCase = insertLogUseCase,
				message = buildActionResultStatusMessage(
					actorName = actor.name,
					actionName = pendingAction.name,
					targetName = target.name,
					resultType = resultType,
					amount = healing
				)
			)
			appendDefeatRecoveryStatus(uiState, hpChange)
		}
	}

	uiState.update {
		it.copy(
			turnStep = CombatTurnStep.READY_TO_END,
			pendingAction = pendingAction,
			selectedTargetId = target.characterId
		)
	}
}

internal fun advanceCombatTurn(
	uiState: MutableStateFlow<CombatUiState>,
	combatTextProvider: CombatTextProvider
) {
	uiState.update { it.clearPendingTurnState().advanceTurn(combatTextProvider) }
}

internal fun addCombatantCondition(
	uiState: MutableStateFlow<CombatUiState>,
	scope: CoroutineScope,
	mutex: Mutex,
	characterId: String,
	conditionName: String,
	duration: Int? = null,
	persistsAcrossEncounters: Boolean = false,
	getCharacterById: suspend (String) -> CharacterEntry?,
	updateCharacterUseCase: UpdateCharacterUseCase
) {
	val currentRound = uiState.value.currentRound
	if (persistsAcrossEncounters) {
		persistCharacterCondition(
			scope = scope,
			mutex = mutex,
			characterId = characterId,
			conditionName = conditionName,
			shouldAddCondition = true,
			getCharacterById = getCharacterById,
			availableCharacters = { uiState.value.availableCharacters },
			updateCharacterUseCase = updateCharacterUseCase
		)
	} else {
		uiState.update { state ->
			state.withUpdatedCombatant(characterId) { combatant ->
				combatant.copy(
					conditions = combatant.conditions + buildEncounterCondition(
						conditionName = conditionName,
						duration = duration,
						currentRound = currentRound
					)
				)
			}
		}
	}

	appendConditionStatus(
		uiState = uiState,
		characterId = characterId,
		conditionName = conditionName,
		duration = duration,
		persistsAcrossEncounters = persistsAcrossEncounters,
		wasConditionAdded = true
	)
}

internal fun removeCombatantCondition(
	uiState: MutableStateFlow<CombatUiState>,
	scope: CoroutineScope,
	mutex: Mutex,
	characterId: String,
	conditionName: String,
	getCharacterById: suspend (String) -> CharacterEntry?,
	updateCharacterUseCase: UpdateCharacterUseCase
) {
	uiState.update { state ->
		state.withUpdatedCombatant(characterId) { combatant ->
			combatant.copy(conditions = combatant.conditions.filter { it.name != conditionName })
		}
	}
	persistCharacterCondition(
		scope = scope,
		mutex = mutex,
		characterId = characterId,
		conditionName = conditionName,
		shouldAddCondition = false,
		getCharacterById = getCharacterById,
		availableCharacters = { uiState.value.availableCharacters },
		updateCharacterUseCase = updateCharacterUseCase
	)
	appendConditionStatus(
		uiState = uiState,
		characterId = characterId,
		conditionName = conditionName,
		wasConditionAdded = false
	)
}

internal fun updateCombatantTempHp(
	uiState: MutableStateFlow<CombatUiState>,
	characterId: String,
	tempHp: Int
) {
	uiState.update { it.withUpdatedTempHp(characterId, tempHp) }
}



