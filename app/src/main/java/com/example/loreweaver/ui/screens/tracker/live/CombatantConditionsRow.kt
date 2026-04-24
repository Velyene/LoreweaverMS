package com.example.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loreweaver.R
import com.example.loreweaver.domain.model.CombatantState
import com.example.loreweaver.domain.model.Condition

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CombatantConditionsRow(
	combatant: CombatantState,
	onRemoveCondition: (String, String) -> Unit,
	onAddConditionClick: () -> Unit
) {
	val addConditionDescription = stringResource(R.string.add_condition_desc)

	FlowRow(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(4.dp),
		verticalArrangement = Arrangement.spacedBy(4.dp)
	) {
		// Conditions remain inline with the combatant row so status effect changes stay visible
		// without forcing players to open a secondary detail surface during live combat.
		combatant.conditions.forEach { condition ->
			ConditionChip(
				condition = condition,
				onRemove = { onRemoveCondition(combatant.characterId, condition.name) }
			)
		}

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
}

@Composable
private fun ConditionChip(
	condition: Condition,
	onRemove: () -> Unit
) {
	FilterChip(
		selected = true,
		onClick = {},
		label = {
			Text(
				"${condition.name}${conditionDurationText(condition)}",
				fontSize = 11.sp
			)
		},
		trailingIcon = {
			IconButton(
				onClick = onRemove,
				modifier = Modifier.size(16.dp)
			) {
				Icon(
					Icons.Default.Close,
					contentDescription = stringResource(R.string.remove_condition_desc),
					modifier = Modifier.size(12.dp)
				)
			}
		},
		colors = FilterChipDefaults.filterChipColors(
			selectedContainerColor = getConditionColor(condition.name)
		)
	)
}

private fun conditionDurationText(condition: Condition): String {
	return condition.duration?.let { " ($it)" } ?: ""
}

@Composable
private fun getConditionColor(conditionName: String): Color {
	return when (conditionName.lowercase()) {
		"blinded", "deafened", "frightened", "grappled", "prone", "restrained" ->
			MaterialTheme.colorScheme.errorContainer

		"charmed", "stunned", "paralyzed", "petrified", "unconscious", "incapacitated" ->
			MaterialTheme.colorScheme.error.copy(alpha = 0.3f)

		"poisoned", "burning", "bleeding" ->
			Color(0xFF8B0000).copy(alpha = 0.3f)

		"blessed", "inspired", "hasted" ->
			Color(0xFF4CAF50).copy(alpha = 0.3f)

		"invisible", "hidden" ->
			MaterialTheme.colorScheme.secondaryContainer

		else -> MaterialTheme.colorScheme.tertiaryContainer
	}
}

