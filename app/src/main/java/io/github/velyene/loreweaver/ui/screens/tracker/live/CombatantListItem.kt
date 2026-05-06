package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.ui.screens.AddConditionDialog

@Composable
internal fun CombatantListItem(
	combatant: CombatantState,
	persistentConditions: Set<String>,
	isActive: Boolean,
	onHpChange: (characterId: String, delta: Int) -> Unit,
	onAddCondition: (characterId: String, condition: String, duration: Int?, persistsAcrossEncounters: Boolean) -> Unit,
	onRemoveCondition: (characterId: String, conditionName: String) -> Unit
) {
	var showAddConditionDialog by remember { mutableStateOf(false) }
	fun dismissConditionDialog() {
		showAddConditionDialog = false
	}

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 4.dp)
			.background(combatantContainerColor(isActive), RoundedCornerShape(6.dp))
			.padding(horizontal = 8.dp, vertical = 8.dp)
	) {
		CombatantHeaderRow(
			combatant = combatant,
			isActive = isActive,
			onHpChange = onHpChange
		)

		if (combatant.conditions.isNotEmpty() || persistentConditions.isNotEmpty() || isActive) {
			Spacer(modifier = Modifier.height(4.dp))
			CombatantConditionsRow(
				combatant = combatant,
				persistentConditions = persistentConditions,
				onRemoveCondition = { characterId, conditionName, _ -> onRemoveCondition(characterId, conditionName) },
				onAddConditionClick = { showAddConditionDialog = true }
			)
		}
	}

	if (showAddConditionDialog) {
		AddConditionDialog(
			onConfirm = { condition, duration, persistsAcrossEncounters ->
				onAddCondition(combatant.characterId, condition, duration, persistsAcrossEncounters)
				dismissConditionDialog()
			},
			onDismiss = ::dismissConditionDialog
		)
	}
}

@Composable
private fun combatantContainerColor(isActive: Boolean): Color {
	return if (isActive) MaterialTheme.colorScheme.primaryContainer
	else MaterialTheme.colorScheme.surfaceVariant
}

