package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState

internal fun resolvePendingAction(
	availableCharacters: List<CharacterEntry>,
	actorId: String,
	actionName: String
): PendingTurnAction {
	val actor = availableCharacters.firstOrNull { it.id == actorId }
	val customAction = actor?.actions?.firstOrNull { it.name.equals(actionName, ignoreCase = true) }
	val isAttack = customAction?.isAttack ?: defaultActionIsAttack(actionName)
	return PendingTurnAction(
		name = customAction?.name ?: actionName,
		isAttack = isAttack,
		allowsSelfTarget = !isAttack
	)
}

internal fun targetCandidates(
	state: CombatUiState,
	pendingAction: PendingTurnAction
): List<CombatantState> {
	val currentCombatantId = state.currentCombatant()?.characterId
	return state.combatants.filter { combatant ->
		val isSelf = combatant.characterId == currentCombatantId
		val isEligibleTarget = pendingAction.allowsSelfTarget || !isSelf
		isEligibleTarget && (combatant.currentHp > 0 || isSelf)
	}
}

internal fun CombatUiState.withSelectedAction(pendingAction: PendingTurnAction): CombatUiState {
	val availableTargets = targetCandidates(this, pendingAction)
	return copy(
		pendingAction = pendingAction,
		selectedTargetId = null,
		turnStep = if (availableTargets.isEmpty()) {
			CombatTurnStep.APPLY_RESULT
		} else {
			CombatTurnStep.SELECT_TARGET
		}
	)
}

internal fun CombatUiState.withSelectedTargetOrNull(targetId: String): CombatUiState? {
	val activePendingAction = pendingAction ?: return null
	val validTarget = targetCandidates(this, activePendingAction)
		.any { it.characterId == targetId }
	if (!validTarget) return null

	return copy(
		selectedTargetId = targetId,
		turnStep = CombatTurnStep.APPLY_RESULT
	)
}

internal fun CombatUiState.applyHpDeltaToState(
	characterId: String,
	delta: Int
): Pair<CombatUiState, HpChangeResult>? {
	val combatant = combatants.find { it.characterId == characterId } ?: return null
	val newHp = (combatant.currentHp + delta).coerceIn(0, combatant.maxHp)
	return copy(
		combatants = combatants.map { currentCombatant ->
			if (currentCombatant.characterId == characterId) {
				currentCombatant.copy(currentHp = newHp)
			} else {
				currentCombatant
			}
		}
	) to HpChangeResult(
		combatant = combatant,
		oldHp = combatant.currentHp,
		newHp = newHp
	)
}

internal fun hpStatusMessage(hpChange: HpChangeResult, delta: Int): String? {
	if (delta == 0) return null
	return if (delta < 0) {
		"${hpChange.combatant.name} takes ${-delta} damage (${hpChange.newHp}/${hpChange.combatant.maxHp} HP)"
	} else {
		"${hpChange.combatant.name} recovers $delta HP (${hpChange.newHp}/${hpChange.combatant.maxHp} HP)"
	}
}

internal fun defeatRecoveryStatusMessage(hpChange: HpChangeResult): String? {
	return when {
		hpChange.newHp == 0 && hpChange.oldHp > 0 -> "${hpChange.combatant.name} has been defeated"
		hpChange.oldHp == 0 && hpChange.newHp > 0 -> "${hpChange.combatant.name} is back in the fight"
		else -> null
	}
}

