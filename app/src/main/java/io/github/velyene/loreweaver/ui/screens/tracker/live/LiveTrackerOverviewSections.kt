/*
 * FILE: LiveTrackerOverviewSections.kt
 *
 * TABLE OF CONTENTS:
 * 1. Content list and notes card
 * 2. Secondary party and enemy roster panels
 * 3. Turn tracker strip and turn chips
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.LazyColumn
import androidx.compose.foundation.layout.LazyRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
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
import io.github.velyene.loreweaver.ui.theme.AntiqueGold
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal
import io.github.velyene.loreweaver.ui.theme.DangerRed

@Composable
internal fun LiveTrackerContentList(
	encounterName: String,
	encounterNotes: String,
	statuses: List<String>,
	turnStep: io.github.velyene.loreweaver.ui.viewmodels.CombatTurnStep,
	pendingAction: io.github.velyene.loreweaver.ui.viewmodels.PendingTurnAction?,
	selectedTargetId: String?,
	trackerState: LiveTrackerUiState,
	callbacks: LiveTrackerCallbacks,
	modifier: Modifier = Modifier,
	contentListState: androidx.compose.foundation.lazy.LazyListState
) {
	androidx.compose.foundation.lazy.LazyColumn(
		state = contentListState,
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		item {
			CurrentParticipantPanel(
				state = CurrentParticipantPanelState(
					encounterName = encounterName,
					participant = trackerState.currentParticipant,
					pendingAction = pendingAction,
					selectedTarget = trackerState.selectedTarget,
					turnStep = turnStep,
					targetableParticipants = trackerState.targetableParticipants
				),
				callbacks = callbacks
			)
		}

		if (encounterNotes.isNotBlank()) {
			item {
				EncounterNotesCard(encounterNotes = encounterNotes)
			}
		}

		item {
			SecondaryPartyPanel(
				partyMembers = trackerState.secondaryPartyMembers,
				selectedTargetId = selectedTargetId,
				selectableTargetIds = trackerState.selectableTargetIds,
				onSelectTarget = callbacks.onSelectTarget
			)
		}

		item {
			EnemyPanel(
				enemies = trackerState.enemies,
				selectedTargetId = selectedTargetId,
				selectableTargetIds = trackerState.selectableTargetIds,
				onSelectTarget = callbacks.onSelectTarget
			)
		}

		item {
			CombatLogSection(statuses = statuses)
		}
	}
}

@Composable
private fun EncounterNotesCard(encounterNotes: String) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			Text(
				text = androidx.compose.ui.res.stringResource(R.string.encounter_notes_title),
				style = MaterialTheme.typography.labelSmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.semantics { heading() }
			)
			Spacer(modifier = Modifier.height(6.dp))
			Text(
				text = encounterNotes,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
	}
}

@Composable
private fun SecondaryPartyPanel(
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
private fun EnemyPanel(
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

@Composable
internal fun TurnTrackerStrip(
	round: Int,
	participants: List<LiveParticipantUiModel>,
	turnIndex: Int,
	modifier: Modifier = Modifier
) {
	Column(
		modifier = modifier
			.border(1.dp, MaterialTheme.colorScheme.outline, androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
			.padding(12.dp)
	) {
		Text(
			text = androidx.compose.ui.res.stringResource(R.string.encounter_turn_tracker_title),
			style = MaterialTheme.typography.labelSmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			modifier = Modifier.semantics { heading() }
		)
		Spacer(modifier = Modifier.height(6.dp))
		Text(
			text = androidx.compose.ui.res.stringResource(R.string.round_counter, round),
			style = MaterialTheme.typography.bodyMedium,
			fontWeight = FontWeight.Bold,
			color = MaterialTheme.colorScheme.onSurface
		)
		Spacer(modifier = Modifier.height(8.dp))

		if (participants.isEmpty()) {
			Text(
				text = androidx.compose.ui.res.stringResource(R.string.encounter_turn_tracker_empty_message),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
			)
			return@Column
		}

		LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
			itemsIndexed(upcomingTurnWindow(participants, turnIndex), key = { _, participant -> participant.combatant.characterId }) { index, participant ->
				TurnOrderChip(
					participant = participant,
					turnLabel = when (index) {
						0 -> androidx.compose.ui.res.stringResource(R.string.encounter_turn_label_current)
						1 -> androidx.compose.ui.res.stringResource(R.string.encounter_turn_label_next)
						else -> androidx.compose.ui.res.stringResource(R.string.encounter_turn_label_upcoming)
					},
					isCurrent = index == 0
				)
			}
		}
	}
}

@Composable
private fun TurnOrderChip(
	participant: LiveParticipantUiModel,
	turnLabel: String,
	isCurrent: Boolean
) {
	val accentColor = if (participant.isPlayer) ArcaneTeal else DangerRed
	val containerColor = when {
		isCurrent -> MaterialTheme.colorScheme.primaryContainer
		participant.isPlayer -> ArcaneTeal.copy(alpha = 0.14f)
		else -> DangerRed.copy(alpha = 0.14f)
	}
	val contentColor = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

	Card(
		colors = CardDefaults.cardColors(containerColor = containerColor),
		border = BorderStroke(
			width = if (isCurrent) 2.dp else 1.dp,
			color = if (isCurrent) AntiqueGold else accentColor.copy(alpha = 0.6f)
		)
	) {
		Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
			Text(
				text = turnLabel,
				style = MaterialTheme.typography.labelSmall,
				color = contentColor.copy(alpha = 0.8f)
			)
			Text(
				text = participant.combatant.name,
				style = MaterialTheme.typography.titleSmall,
				fontWeight = FontWeight.Bold,
				color = contentColor
			)
			Text(
				text = if (participant.isPlayer) androidx.compose.ui.res.stringResource(R.string.encounter_turn_chip_player) else androidx.compose.ui.res.stringResource(R.string.encounter_turn_chip_enemy),
				style = MaterialTheme.typography.bodySmall,
				color = contentColor.copy(alpha = 0.8f)
			)
		}
	}
}

