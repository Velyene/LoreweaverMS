/*
 * FILE: SessionSummarySections.kt
 *
 * TABLE OF CONTENTS:
 * 1. Shared section card container
 * 2. Participant and status lists
 * 3. Combat log list
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.viewmodels.PersistentStatusSummary
import io.github.velyene.loreweaver.ui.viewmodels.SessionParticipantSummary

@Composable
internal fun SummarySectionCard(
	title: String,
	content: @Composable () -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
			.padding(16.dp)
	) {
		Text(
			text = title,
			style = MaterialTheme.typography.labelLarge,
			fontWeight = FontWeight.Bold,
			color = MaterialTheme.colorScheme.onSurface,
			modifier = Modifier.semantics { heading() }
		)
		Spacer(modifier = Modifier.height(8.dp))
		Box(modifier = Modifier.fillMaxWidth()) {
			content()
		}
	}
}

@Composable
internal fun ParticipantSummaryList(items: List<SessionParticipantSummary>) {
	if (items.isEmpty()) {
		Text(
			text = stringResource(R.string.none_label),
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
		return
	}

	Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
		items.forEach { participant ->
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Column(modifier = Modifier.weight(1f)) {
					Text(
						text = participant.name,
						style = MaterialTheme.typography.bodyLarge,
						fontWeight = FontWeight.Medium,
						color = MaterialTheme.colorScheme.onSurface
					)
					Text(
						text = participant.hpLabel,
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
				Text(
					text = stringResource(R.string.combatant_initiative_summary, participant.initiative),
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
		}
	}
}

@Composable
internal fun PersistentStatusList(items: List<PersistentStatusSummary>) {
	if (items.isEmpty()) {
		Text(
			text = stringResource(R.string.none_label),
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
		return
	}

	Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
		items.forEach { item ->
			Column(modifier = Modifier.fillMaxWidth()) {
				Text(
					text = item.participantName,
					style = MaterialTheme.typography.bodyLarge,
					fontWeight = FontWeight.Medium,
					color = MaterialTheme.colorScheme.onSurface
				)
				Spacer(modifier = Modifier.height(6.dp))
				StatusChipFlowRow(
					statuses = persistentStatusChipModels(item.conditions)
				)
			}
		}
	}
}

@Composable
internal fun LogSummaryList(items: List<String>) {
	if (items.isEmpty()) {
		Text(
			text = stringResource(R.string.session_summary_log_empty_message),
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
		return
	}

	Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
		items.reversed().forEach { entry ->
			Text(
				text = stringResource(R.string.combat_log_bullet, entry),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
	}
}

