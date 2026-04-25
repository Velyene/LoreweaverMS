package io.github.velyene.loreweaver.ui.screens.tracker.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CombatantState

@Composable
internal fun SetupRosterSection(
	combatants: List<CombatantState>,
	onRemoveCombatant: (CombatantState) -> Unit,
	modifier: Modifier = Modifier
) {
	Column(
		modifier = modifier
			.fillMaxWidth()
			.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
			.padding(12.dp)
	) {
		Text(
			stringResource(R.string.encounter_roster_title, combatants.size),
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			style = MaterialTheme.typography.labelSmall,
			modifier = Modifier
				.padding(bottom = 8.dp)
				.semantics { heading() }
		)
		if (combatants.isEmpty()) {
			Text(
				stringResource(R.string.encounter_roster_empty_message),
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
				fontSize = 13.sp,
				lineHeight = 20.sp
			)
		} else {
			LazyColumn {
				items(combatants, key = { it.characterId }) { combatant ->
					SetupCombatantRow(combatant = combatant, onRemoveCombatant = onRemoveCombatant)
				}
			}
		}
	}
}

@Composable
private fun SetupCombatantRow(
	combatant: CombatantState,
	onRemoveCombatant: (CombatantState) -> Unit
) {
	val combatantSummary = stringResource(
		R.string.combatant_setup_summary,
		combatant.maxHp,
		combatant.initiative
	)

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 4.dp)
			.background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
			.semantics {
				contentDescription = "${combatant.name}. $combatantSummary"
			}
			.padding(horizontal = 12.dp, vertical = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		Column(modifier = Modifier.weight(1f)) {
			Text(
				combatant.name,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				fontSize = 14.sp,
				fontWeight = FontWeight.Medium
			)
			Text(
				combatantSummary,
				color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
				fontSize = 11.sp
			)
		}
		IconButton(
			onClick = { onRemoveCombatant(combatant) },
			modifier = Modifier.size(32.dp)
		) {
			Icon(
				Icons.Default.Delete,
				contentDescription = stringResource(R.string.remove_combatant_desc, combatant.name),
				tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
				modifier = Modifier.size(18.dp)
			)
		}
	}
}

