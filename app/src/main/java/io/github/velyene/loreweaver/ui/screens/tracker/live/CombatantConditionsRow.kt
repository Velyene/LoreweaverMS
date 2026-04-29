package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
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
import io.github.velyene.loreweaver.domain.model.Condition
import io.github.velyene.loreweaver.ui.screens.StatusChipFlowRow
import io.github.velyene.loreweaver.ui.screens.StatusChipModel
import io.github.velyene.loreweaver.ui.screens.canonicalStatusLabel
import io.github.velyene.loreweaver.ui.screens.persistentStatusChipModels
import io.github.velyene.loreweaver.ui.screens.statusChipModel

@Composable
internal fun CombatantConditionsRow(
	combatant: CombatantState,
	persistentConditions: Set<String> = emptySet(),
	onRemoveCondition: (String, String) -> Unit,
	onAddConditionClick: () -> Unit
) {
	val addConditionDescription = stringResource(R.string.add_condition_desc)
	val conditionsStateDescription = buildString {
		append(stringResource(R.string.conditions_label))
		append(": ")
		append(
			if (combatant.conditions.isEmpty() && persistentConditions.isEmpty()) {
				stringResource(R.string.empty_label)
			} else {
				buildList {
					addAll(combatant.conditions.map { condition ->
						condition.name + conditionDurationText(condition)
					})
					addAll(
						persistentConditions
							.filterNot { persistentName ->
								combatant.conditions.any { it.name == persistentName }
							}
							.map { persistentName ->
								"$persistentName ${stringResource(R.string.condition_persistent_chip_suffix)}"
							}
					)
				}.joinToString()
			}
		)
	}

	// Conditions remain inline with the combatant row so status effect changes stay visible
	// without forcing players to open a secondary detail surface during live combat.
	StatusChipFlowRow(
		statuses = buildCombatantStatusChips(combatant, persistentConditions),
		modifier = Modifier
			.fillMaxWidth()
			.semantics {
				stateDescription = conditionsStateDescription
			},
		onStatusRemove = { status -> onRemoveCondition(combatant.characterId, status.name) },
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

private fun conditionDurationText(condition: Condition): String {
	return condition.duration?.let { " ($it)" } ?: ""
}

private fun buildCombatantStatusChips(
	combatant: CombatantState,
	persistentConditions: Set<String>
): List<StatusChipModel> {
	val activeEncounterConditionLabels = combatant.conditions
		.map { condition -> canonicalStatusLabel(condition.name) }
		.toSet()
	val encounterStatuses = combatant.conditions
		.sortedBy { condition -> canonicalStatusLabel(condition.name).lowercase() }
		.map { condition ->
			statusChipModel(
				name = condition.name,
				durationText = conditionDurationText(condition),
				isPersistent = false
			)
		}
	val persistentStatuses = persistentStatusChipModels(
		persistentConditions.filterNot { persistentName ->
			canonicalStatusLabel(persistentName) in activeEncounterConditionLabels
		}
	)
	return encounterStatuses + persistentStatuses
}

