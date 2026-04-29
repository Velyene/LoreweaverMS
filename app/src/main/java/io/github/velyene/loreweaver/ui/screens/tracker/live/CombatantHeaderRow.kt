package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CombatantState

@Composable
internal fun CombatantHeaderRow(
	combatant: CombatantState,
	isActive: Boolean,
	onHpChange: (characterId: String, delta: Int) -> Unit
) {
	val initiativeSummary = stringResource(R.string.combatant_initiative_summary, combatant.initiative)
	val hpSummary = formatCombatantHpLabel(combatant)

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.semantics {
				stateDescription = "$initiativeSummary, $hpSummary"
			},
		verticalAlignment = Alignment.CenterVertically
	) {
		Column(modifier = Modifier.weight(1f)) {
			Text(
				combatant.name,
				color = combatantNameColor(isActive),
				fontWeight = combatantNameWeight(isActive),
				fontSize = 13.sp
			)
			Text(
				initiativeSummary,
				color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
				fontSize = 10.sp
			)
		}

		Text(
			hpSummary,
			fontSize = 12.sp,
			color = if (combatant.currentHp == 0) MaterialTheme.colorScheme.error
			else MaterialTheme.colorScheme.onSurfaceVariant,
			modifier = Modifier.padding(horizontal = 4.dp)
		)
		IconButton(
			onClick = { onHpChange(combatant.characterId, -1) },
			modifier = Modifier.size(28.dp)
		) {
			Icon(
				Icons.Default.Remove,
				contentDescription = stringResource(R.string.damage_combatant_desc, combatant.name),
				modifier = Modifier.size(14.dp)
			)
		}
		IconButton(
			onClick = { onHpChange(combatant.characterId, 1) },
			modifier = Modifier.size(28.dp)
		) {
			Icon(
				Icons.Default.Add,
				contentDescription = stringResource(R.string.heal_combatant_desc, combatant.name),
				modifier = Modifier.size(14.dp)
			)
		}
	}
}

@Composable
private fun combatantNameColor(isActive: Boolean): Color {
	return if (isActive) MaterialTheme.colorScheme.onPrimaryContainer
	else MaterialTheme.colorScheme.onSurfaceVariant
}

private fun combatantNameWeight(isActive: Boolean): FontWeight {
	return if (isActive) FontWeight.Bold else FontWeight.Normal
}

@Composable
private fun formatCombatantHpLabel(combatant: CombatantState): String {
	return if (combatant.tempHp > 0) {
		stringResource(
			R.string.combatant_hp_label_with_temp,
			combatant.currentHp,
			combatant.maxHp,
			combatant.tempHp
		)
	} else {
		stringResource(
			R.string.combatant_hp_label,
			combatant.currentHp,
			combatant.maxHp
		)
	}
}

