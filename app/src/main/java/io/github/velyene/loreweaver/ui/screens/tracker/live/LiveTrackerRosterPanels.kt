/*
 * FILE: LiveTrackerRosterPanels.kt
 *
 * TABLE OF CONTENTS:
 * 1. Secondary party and enemy roster panels
 * 2. Shared participant roster card helpers
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R

@Composable
internal fun SecondaryPartyPanel(
	partyMembers: List<LiveParticipantUiModel>,
	selectedTargetId: String?,
	selectableTargetIds: Set<String>,
	onSelectTarget: (String) -> Unit
) {
	ParticipantRosterPanel(
		title = androidx.compose.ui.res.stringResource(R.string.encounter_secondary_party_title),
		emptyMessage = androidx.compose.ui.res.stringResource(R.string.encounter_secondary_party_empty_message),
		participants = partyMembers,
		selectedTargetId = selectedTargetId,
		selectableTargetIds = selectableTargetIds,
		onSelectTarget = onSelectTarget
	)
}

@Composable
internal fun EnemyPanel(
	enemies: List<LiveParticipantUiModel>,
	selectedTargetId: String?,
	selectableTargetIds: Set<String>,
	onSelectTarget: (String) -> Unit
) {
	ParticipantRosterPanel(
		title = androidx.compose.ui.res.stringResource(R.string.encounter_enemy_panel_title),
		emptyMessage = androidx.compose.ui.res.stringResource(R.string.encounter_enemy_panel_empty_message),
		participants = enemies,
		selectedTargetId = selectedTargetId,
		selectableTargetIds = selectableTargetIds,
		onSelectTarget = onSelectTarget
	)
}

@Composable
private fun ParticipantRosterPanel(
	title: String,
	emptyMessage: String,
	participants: List<LiveParticipantUiModel>,
	selectedTargetId: String?,
	selectableTargetIds: Set<String>,
	onSelectTarget: (String) -> Unit
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			Text(
				text = title,
				style = MaterialTheme.typography.labelSmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.semantics { heading() }
			)
			Spacer(modifier = Modifier.height(8.dp))
			if (participants.isEmpty()) {
				Text(
					text = emptyMessage,
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			} else {
				Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
					participants.forEach { participant ->
						ParticipantRosterCard(
							participant = participant,
							isSelectedTarget = participant.combatant.characterId == selectedTargetId,
							isSelectableTarget = participant.combatant.characterId in selectableTargetIds,
							onSelectTarget = onSelectTarget
						)
					}
				}
			}
		}
	}
}

@Composable
private fun ParticipantRosterCard(
	participant: LiveParticipantUiModel,
	isSelectedTarget: Boolean,
	isSelectableTarget: Boolean,
	onSelectTarget: (String) -> Unit
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(
			containerColor = if (isSelectedTarget) {
				MaterialTheme.colorScheme.tertiaryContainer
			} else {
				MaterialTheme.colorScheme.surface
			}
		)
	) {
		Column(modifier = Modifier.padding(12.dp)) {
			Text(
				text = participant.combatant.name,
				style = MaterialTheme.typography.titleSmall,
				fontWeight = FontWeight.Bold
			)
			Text(
				text = participant.typeLabel,
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Spacer(modifier = Modifier.height(4.dp))
			Text(
				text = androidx.compose.ui.res.stringResource(
					R.string.encounter_active_hp_summary,
					participant.combatant.currentHp,
					participant.combatant.maxHp
				),
				style = MaterialTheme.typography.bodySmall
			)
			if (participant.resourceLines.isNotEmpty()) {
				Spacer(modifier = Modifier.height(4.dp))
				ResourceLinesSection(resourceLines = participant.resourceLines)
			}
			if (isSelectableTarget) {
				Spacer(modifier = Modifier.height(8.dp))
				OutlinedButton(
					onClick = { onSelectTarget(participant.combatant.characterId) }
				) {
					Text(
						text = if (isSelectedTarget) {
							androidx.compose.ui.res.stringResource(R.string.encounter_party_member_selected)
						} else {
							androidx.compose.ui.res.stringResource(R.string.encounter_target_prompt)
						}
					)
				}
			}
		}
	}
}

