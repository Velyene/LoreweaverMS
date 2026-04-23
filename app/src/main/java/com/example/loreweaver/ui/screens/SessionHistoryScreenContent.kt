package com.example.loreweaver.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.loreweaver.R
import com.example.loreweaver.domain.model.SessionRecord
import java.text.SimpleDateFormat
import java.util.Date

@Composable
private fun SessionExpandableItem(
	session: SessionRecord,
	dateFormat: SimpleDateFormat
) {
	var expanded by rememberSaveable(session.id) { mutableStateOf(false) }
	val toggleActionLabel = if (expanded) {
		stringResource(R.string.session_collapse_action, session.title)
	} else {
		stringResource(R.string.session_expand_action, session.title)
	}
	val expandedStateDescription = if (expanded) {
		stringResource(R.string.accessibility_state_expanded)
	} else {
		stringResource(R.string.accessibility_state_collapsed)
	}

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.semantics { stateDescription = expandedStateDescription }
			.clickable(
				role = Role.Button,
				onClickLabel = toggleActionLabel
			) { expanded = !expanded }
			.padding(16.dp)
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Column {
				Text(
					session.title,
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold,
					modifier = Modifier.semantics { heading() }
				)
				Text(
					dateFormat.format(Date(session.date)),
					style = MaterialTheme.typography.bodySmall
				)
			}
			Icon(
				imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
				contentDescription = null
			)
		}

		AnimatedVisibility(visible = expanded) {
			Column(modifier = Modifier.padding(top = 16.dp)) {
				Text(
					stringResource(R.string.session_combat_log_title),
					style = MaterialTheme.typography.labelLarge,
					color = MaterialTheme.colorScheme.primary
				)
				Spacer(modifier = Modifier.height(8.dp))
				session.log.forEach { entry ->
					Text(
						stringResource(R.string.combat_log_bullet, entry),
						style = MaterialTheme.typography.bodyMedium
					)
				}

				session.snapshot?.let { snap ->
					Spacer(modifier = Modifier.height(16.dp))
					Text(
						stringResource(R.string.session_end_state_title),
						style = MaterialTheme.typography.labelLarge,
						color = MaterialTheme.colorScheme.secondary
					)
					Text(stringResource(R.string.session_final_round, snap.currentRound))
					snap.combatants.forEach { combatant ->
						Text(
							stringResource(
								R.string.session_combatant_hp_summary,
								combatant.name,
								combatant.currentHp,
								combatant.maxHp
							)
						)
					}
				}
			}
		}
	}
}
