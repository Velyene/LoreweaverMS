package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.model.Condition
import io.github.velyene.loreweaver.domain.model.DurationType

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

