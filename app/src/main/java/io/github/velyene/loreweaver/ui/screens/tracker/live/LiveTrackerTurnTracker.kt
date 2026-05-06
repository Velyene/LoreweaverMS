/*
 * FILE: LiveTrackerTurnTracker.kt
 *
 * TABLE OF CONTENTS:
 * 1. Turn tracker strip
 * 2. Turn order chips
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
internal fun TurnTrackerStrip(
	round: Int,
	participants: List<LiveParticipantUiModel>,
	turnIndex: Int,
	modifier: Modifier = Modifier,
	focusedCombatantId: String? = null,
	onFocusCombatant: (String) -> Unit = {}
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
					isCurrent = index == 0,
					isFocused = participant.combatant.characterId == focusedCombatantId,
					onFocusCombatant = onFocusCombatant
				)
			}
		}
	}
}

@Composable
private fun TurnOrderChip(
	participant: LiveParticipantUiModel,
	turnLabel: String,
	isCurrent: Boolean,
	isFocused: Boolean,
	onFocusCombatant: (String) -> Unit
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
			width = if (isCurrent || isFocused) 2.dp else 1.dp,
			color = if (isCurrent || isFocused) AntiqueGold else accentColor.copy(alpha = 0.6f)
		),
		modifier = Modifier.clickable { onFocusCombatant(participant.combatant.characterId) }
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
				text = if (participant.isPlayer) {
					androidx.compose.ui.res.stringResource(R.string.encounter_turn_chip_player)
				} else {
					androidx.compose.ui.res.stringResource(R.string.encounter_turn_chip_enemy)
				},
				style = MaterialTheme.typography.bodySmall,
				color = contentColor.copy(alpha = 0.8f)
			)
		}
	}
}

