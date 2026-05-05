/*
 * FILE: LiveTrackerTurnOrder.kt
 *
 * TABLE OF CONTENTS:
 * 1. Turn Order Header and Controls
 * 2. Current Turn and Queue Views
 * 3. Turn Order Helpers
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.theme.AntiqueGold
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal
import io.github.velyene.loreweaver.ui.theme.DangerRed

internal enum class TurnOrderEmphasis {
	CURRENT,
	NEXT,
	ON_DECK,
	LATER
}

@Composable
internal fun TurnTrackerStrip(
	round: Int,
	participants: List<LiveParticipantUiModel>,
	turnIndex: Int,
	focusedCombatantId: String?,
	onFocusCombatant: (String) -> Unit,
	modifier: Modifier = Modifier
) {
	Column(
		modifier = modifier
			.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
			.padding(12.dp)
	) {
		Text(
			text = stringResource(R.string.encounter_turn_tracker_title),
			style = MaterialTheme.typography.labelSmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			modifier = Modifier.semantics { heading() }
		)
		Spacer(modifier = Modifier.height(6.dp))
		Text(
			text = stringResource(R.string.round_counter, round),
			style = MaterialTheme.typography.bodyMedium,
			fontWeight = FontWeight.Bold,
			color = MaterialTheme.colorScheme.onSurface
		)
		if (participants.isNotEmpty()) {
			Spacer(modifier = Modifier.height(4.dp))
			Text(
				text = stringResource(
					R.string.encounter_turn_timeline_summary,
					round,
					upcomingTurnWindow(participants, turnIndex)
						.joinToString(separator = " -> ") { participant -> participant.combatant.name }
				),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
		}
		Spacer(modifier = Modifier.height(8.dp))

		if (participants.isEmpty()) {
			Text(
				text = stringResource(R.string.encounter_turn_tracker_empty_message),
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
						0 -> stringResource(R.string.encounter_turn_label_current)
						1 -> stringResource(R.string.encounter_turn_label_next)
						2 -> stringResource(R.string.encounter_turn_label_on_deck)
						3 -> stringResource(R.string.encounter_turn_label_upcoming)
						else -> stringResource(R.string.encounter_turn_label_later)
					},
					emphasis = when (index) {
						0 -> TurnOrderEmphasis.CURRENT
						1 -> TurnOrderEmphasis.NEXT
						2 -> TurnOrderEmphasis.ON_DECK
						else -> TurnOrderEmphasis.LATER
					},
					isFocused = participant.combatant.characterId == focusedCombatantId,
					onClick = { onFocusCombatant(participant.combatant.characterId) }
				)
			}
		}
	}
}

internal fun upcomingTurnWindow(
	participants: List<LiveParticipantUiModel>,
	turnIndex: Int,
	windowSize: Int = 6
): List<LiveParticipantUiModel> {
	if (participants.isEmpty()) return emptyList()
	return List(minOf(windowSize, participants.size)) { offset ->
		participants[(turnIndex + offset).floorMod(participants.size)]
	}
}

internal fun Int.floorMod(divisor: Int): Int = ((this % divisor) + divisor) % divisor

@Composable
internal fun TurnOrderChip(
	participant: LiveParticipantUiModel,
	turnLabel: String,
	emphasis: TurnOrderEmphasis,
	isFocused: Boolean,
	onClick: () -> Unit
) {
	val accentColor = if (participant.isPlayer) ArcaneTeal else DangerRed
	val containerColor = when (emphasis) {
		TurnOrderEmphasis.CURRENT -> MaterialTheme.colorScheme.primaryContainer
		TurnOrderEmphasis.NEXT -> MaterialTheme.colorScheme.tertiaryContainer
		TurnOrderEmphasis.ON_DECK -> MaterialTheme.colorScheme.secondaryContainer
		TurnOrderEmphasis.LATER -> if (participant.isPlayer) ArcaneTeal.copy(alpha = 0.14f) else DangerRed.copy(alpha = 0.14f)
	}
	val contentColor = when (emphasis) {
		TurnOrderEmphasis.CURRENT -> MaterialTheme.colorScheme.onPrimaryContainer
		TurnOrderEmphasis.NEXT -> MaterialTheme.colorScheme.onTertiaryContainer
		TurnOrderEmphasis.ON_DECK -> MaterialTheme.colorScheme.onSecondaryContainer
		TurnOrderEmphasis.LATER -> MaterialTheme.colorScheme.onSurfaceVariant
	}
	val roleLabel = if (participant.isPlayer) stringResource(R.string.encounter_turn_chip_player) else stringResource(R.string.encounter_turn_chip_enemy)
	val statusLabel = when {
		participant.isEliminated && participant.isPlayer -> stringResource(R.string.encounter_status_downed)
		participant.isEliminated -> stringResource(R.string.encounter_status_defeated)
		else -> stringResource(
			R.string.encounter_active_hp_summary,
			participant.combatant.currentHp,
			participant.combatant.maxHp
		)
	}
	val contentAlpha = if (participant.isEliminated && emphasis != TurnOrderEmphasis.CURRENT) 0.7f else 1f

	Card(
		modifier = Modifier.clickable(onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = containerColor),
		border = BorderStroke(
			width = when {
				emphasis == TurnOrderEmphasis.CURRENT || isFocused -> 2.dp
				emphasis == TurnOrderEmphasis.NEXT -> 1.5.dp
				else -> 1.dp
			},
			color = when {
				emphasis == TurnOrderEmphasis.CURRENT -> AntiqueGold
				isFocused -> AntiqueGold.copy(alpha = 0.82f)
				emphasis == TurnOrderEmphasis.NEXT -> MaterialTheme.colorScheme.tertiary
				emphasis == TurnOrderEmphasis.ON_DECK -> MaterialTheme.colorScheme.secondary
				else -> accentColor.copy(alpha = 0.6f)
			}
		)
	) {
		Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
			Text(
				text = turnLabel,
				style = MaterialTheme.typography.labelSmall,
				color = contentColor.copy(alpha = 0.8f * contentAlpha)
			)
			Text(
				text = participant.combatant.name,
				style = MaterialTheme.typography.titleSmall,
				fontWeight = FontWeight.Bold,
				color = contentColor.copy(alpha = contentAlpha)
			)
			Text(
				text = "$roleLabel • ${stringResource(R.string.combatant_initiative_summary, participant.combatant.initiative)}",
				style = MaterialTheme.typography.bodySmall,
				color = contentColor.copy(alpha = 0.8f * contentAlpha)
			)
			Text(
				text = statusLabel,
				style = MaterialTheme.typography.bodySmall,
				color = contentColor.copy(alpha = 0.8f * contentAlpha)
			)
			if (!isFocused && emphasis != TurnOrderEmphasis.CURRENT) {
				Text(
					text = stringResource(R.string.encounter_turn_chip_focus_hint),
					style = MaterialTheme.typography.labelSmall,
					color = contentColor.copy(alpha = 0.72f * contentAlpha)
				)
			}
		}
	}
}
