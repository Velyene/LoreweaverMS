package io.github.velyene.loreweaver.ui.screens.tracker.live

import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.ui.screens.StatusChipModel
import io.github.velyene.loreweaver.ui.screens.canonicalStatusLabel
import io.github.velyene.loreweaver.ui.screens.statusChipModel

internal fun buildCombatantStatusChips(
	combatant: CombatantState,
	persistentConditions: Set<String>
): List<StatusChipModel> {
	val encounterStatusesByLabel = combatant.conditions
		.associate { condition ->
			canonicalStatusLabel(condition.name) to statusChipModel(
				name = condition.name,
				durationText = condition.duration?.let { " ($it)" } ?: "",
				isPersistent = false
			)
		}
	val mergedStatuses = encounterStatusesByLabel.toMutableMap()
	for (persistentCondition in persistentConditions) {
		val canonicalLabel = canonicalStatusLabel(persistentCondition)
		val encounterStatus = mergedStatuses[canonicalLabel]
		mergedStatuses[canonicalLabel] = if (encounterStatus != null) {
			encounterStatus.copy(isPersistent = true)
		} else {
			statusChipModel(name = persistentCondition, isPersistent = true)
		}
	}
	return mergedStatuses.values.sortedBy { status -> status.name.lowercase() }
}

