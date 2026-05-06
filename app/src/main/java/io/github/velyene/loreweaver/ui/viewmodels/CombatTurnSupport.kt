/*
 * FILE: CombatTurnSupport.kt
 *
 * TABLE OF CONTENTS:
 * 1. HP and action-result status support
 *
 * Note: resolvePendingAction, targetCandidates → CombatTurnResolutionSupport.kt
 *       buildEncounterCondition, buildConditionStatusMessage → CombatConditionSupport.kt
 *       defaultActionIsAttack, isAdventurer, withPersistedCondition → CombatStateSupport.kt
 */

package io.github.velyene.loreweaver.ui.viewmodels


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

