/*
 * FILE: CombatTurnSupport.kt
 *
 * TABLE OF CONTENTS:
 * 1. Action and target-selection support
 * 2. Condition and status-message support
 * 3. HP and action-result status support
 * 4. CharacterEntry combat support extensions
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Condition
import io.github.velyene.loreweaver.domain.model.DurationType
import io.github.velyene.loreweaver.domain.util.CharacterParty

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

internal fun buildEncounterCondition(
	conditionName: String,
	duration: Int?,
	currentRound: Int
): Condition {
	return Condition(
		name = conditionName,
		duration = duration,
		durationType = if (duration != null) DurationType.ROUNDS else DurationType.ENCOUNTER,
		addedOnRound = currentRound
	)
}

internal fun buildConditionStatusMessage(
	combatantName: String,
	conditionName: String,
	duration: Int? = null,
	persistsAcrossEncounters: Boolean = false,
	wasConditionAdded: Boolean
): String {
	if (!wasConditionAdded) {
		return "$combatantName is no longer $conditionName"
	}
	val durationText = if (duration != null) " ($duration rounds)" else ""
	val persistenceText = if (persistsAcrossEncounters) " (persistent)" else durationText
	return "$combatantName is now $conditionName$persistenceText"
}

internal fun buildActionResultStatusMessage(
	actorName: String,
	actionName: String,
	targetName: String,
	resultType: ActionResolutionType,
	amount: Int? = null
): String {
	return when (resultType) {
		ActionResolutionType.MISS -> "$actorName used $actionName on $targetName, but missed."
		ActionResolutionType.DAMAGE -> "$actorName used $actionName on $targetName for ${amount ?: 0} damage."
		ActionResolutionType.HEAL -> "$actorName used $actionName on $targetName, restoring ${amount ?: 0} HP."
	}
}

internal fun buildHpStatusMessage(hpChange: HpChangeResult, delta: Int): String? {
	if (delta == 0) return null
	return if (delta < 0) {
		"${hpChange.combatant.name} takes ${-delta} damage (${hpChange.newHp}/${hpChange.combatant.maxHp} HP)"
	} else {
		"${hpChange.combatant.name} recovers $delta HP (${hpChange.newHp}/${hpChange.combatant.maxHp} HP)"
	}
}

internal fun buildDefeatRecoveryStatusMessage(hpChange: HpChangeResult): String? {
	return when {
		hpChange.newHp == 0 && hpChange.oldHp > 0 -> "${hpChange.combatant.name} has been defeated"
		hpChange.oldHp == 0 && hpChange.newHp > 0 -> "${hpChange.combatant.name} is back in the fight"
		else -> null
	}
}

internal fun defaultActionIsAttack(actionName: String): Boolean {
	return when (actionName.lowercase()) {
		"dodge", "help", "heal", "hide" -> false
		else -> true
	}
}

internal fun CharacterEntry.isAdventurer(): Boolean = party == CharacterParty.ADVENTURERS

internal fun CharacterEntry.withPersistedCondition(
	conditionName: String,
	shouldAddCondition: Boolean
): CharacterEntry {
	return if (shouldAddCondition) {
		copy(activeConditions = activeConditions + conditionName)
	} else {
		copy(activeConditions = activeConditions - conditionName)
	}
}

