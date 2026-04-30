/*
 * FILE: CombatantConditionsRow.kt
 *
 * TABLE OF CONTENTS:
 * 1. Combatant conditions row composable
 * 2. Condition chip rendering support
 * 3. Condition duration formatting
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.ui.screens.StatusChipFlowRow
import io.github.velyene.loreweaver.ui.screens.StatusChipModel
import io.github.velyene.loreweaver.ui.screens.canonicalStatusLabel
import io.github.velyene.loreweaver.ui.screens.statusChipDisplayText
import io.github.velyene.loreweaver.ui.screens.statusChipModel

@Composable
internal fun CombatantStatusRow(
	combatant: CombatantState,
	persistentConditions: Set<String> = emptySet(),
	onRemoveCondition: (String, String, Boolean) -> Unit,
	onAddConditionClick: () -> Unit
) {
	val statuses = buildCombatantStatusChips(combatant, persistentConditions)
	val addConditionDescription = stringResource(R.string.add_condition_desc)
	val persistentSuffix = stringResource(R.string.condition_persistent_chip_suffix)
	val statuses = buildCombatantStatusChips(combatant, persistentConditions)
	val conditionsStateValue = if (statuses.isEmpty()) {
		stringResource(R.string.empty_label)
	} else {
		statuses.joinToString { status ->
			statusChipStateText(status, persistentSuffix)
		}
	}
	val conditionsStateDescription = stringResource(
		R.string.conditions_state_description,
		conditionsStateValue
	)

	StatusChipFlowRow(
		statuses = statuses,
		modifier = Modifier
			.semantics {
				stateDescription = conditionsStateDescription
			},
		onStatusRemove = { status ->
			onRemoveCondition(combatant.characterId, status.name, status.isPersistent)
		},
		trailingContent = {
			AssistChip(
				onClick = onAddConditionClick,
				label = { Text("+", fontSize = 12.sp) },
				modifier = Modifier.semantics {
					contentDescription = addConditionDescription
				},
				leadingIcon = {
					Icon(
						Icons.Default.Add,
						contentDescription = null,
						modifier = Modifier.size(14.dp)
					)
				},
				colors = AssistChipDefaults.assistChipColors(
					containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
				),
				border = BorderStroke(
					width = 1.dp,
					color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
				)
			)
		}
	)
}

private fun conditionDurationText(condition: io.github.velyene.loreweaver.domain.model.Condition): String {
	return condition.duration?.let { " ($it)" } ?: ""
}

private fun statusChipStateText(status: StatusChipModel, persistentSuffix: String): String {
	return buildString {
		append(status.name)
		if (status.isPersistent) {
			append(' ')
			append(persistentSuffix)
		} else {
			append(status.durationText)
		}
	}
}

private fun buildCombatantStatusChips(
	combatant: CombatantState,
	persistentConditions: Set<String>
): List<StatusChipModel> {
	val encounterStatusesByLabel = combatant.conditions
		.associate { condition ->
			canonicalStatusLabel(condition.name) to statusChipModel(
				name = condition.name,
				durationText = conditionDurationText(condition),
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


