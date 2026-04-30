/*
 * FILE: EncounterSetupEnemies.kt
 *
 * TABLE OF CONTENTS:
 * 1. Enemies section
 * 2. Enemy combatant cards
 */

package io.github.velyene.loreweaver.ui.screens.tracker.setup

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CombatantState

@Composable
internal fun EnemiesSection(
	enemies: List<CombatantState>,
	onAddEnemy: () -> Unit,
	onRemoveEnemy: (CombatantState) -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.border(1.dp, MaterialTheme.colorScheme.outline, androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
			.padding(12.dp)
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = stringResource(R.string.encounter_enemies_title),
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				style = MaterialTheme.typography.labelSmall,
				modifier = Modifier.semantics { heading() }
			)
			TextButton(onClick = onAddEnemy) {
				Text(text = stringResource(R.string.add_enemy_button))
			}
		}
		Text(
			text = stringResource(R.string.encounter_enemies_supporting_text),
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
		Spacer(modifier = Modifier.height(8.dp))

		if (enemies.isEmpty()) {
			Text(
				text = stringResource(R.string.encounter_enemies_empty_message),
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
				fontSize = 13.sp,
				lineHeight = 20.sp
			)
			return@Column
		}

		enemies.forEach { enemy ->
			EnemyCombatantCard(
				combatant = enemy,
				onRemoveEnemy = { onRemoveEnemy(enemy) }
			)
			Spacer(modifier = Modifier.height(8.dp))
		}
	}
}

@Composable
private fun EnemyCombatantCard(
	combatant: CombatantState,
	onRemoveEnemy: () -> Unit
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 12.dp, vertical = 8.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = combatant.name,
					style = MaterialTheme.typography.titleSmall,
					fontWeight = FontWeight.Medium,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
				Text(
					text = stringResource(
						R.string.combatant_setup_summary,
						combatant.maxHp,
						combatant.initiative
					),
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
				)
			}
			TextButton(onClick = onRemoveEnemy) {
				Text(text = stringResource(R.string.remove_button))
			}
		}
	}
}

