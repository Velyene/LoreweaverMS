/*
 * FILE: LiveTrackerFooterSections.kt
 *
 * TABLE OF CONTENTS:
 * 1. Combat log section
 * 2. Encounter footer actions
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.ACTION_BUTTON_HEIGHT_DP
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.LOG_HEIGHT_DP
import io.github.velyene.loreweaver.ui.screens.visibleVerticalScrollbar
import io.github.velyene.loreweaver.ui.viewmodels.CombatTurnStep

@Composable
internal fun CombatLogSection(statuses: List<String>) {
	val listState = rememberLazyListState()

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.height(LOG_HEIGHT_DP.dp)
			.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
			.padding(8.dp)
	) {
		Text(
			text = stringResource(R.string.session_combat_log_title),
			color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
			style = MaterialTheme.typography.labelSmall,
			modifier = Modifier.semantics { heading() }
		)
		LazyColumn(
			state = listState,
			modifier = Modifier.visibleVerticalScrollbar(listState)
		) {
			itemsIndexed(statuses.reversed(), key = { index, status -> "$index-$status" }) { _, status ->
				Text(
					text = stringResource(R.string.combat_log_bullet, status),
					color = if (status.contains("!")) MaterialTheme.colorScheme.primary
					else MaterialTheme.colorScheme.onSurface,
					fontSize = 11.sp,
					modifier = Modifier.padding(vertical = 1.dp)
				)
			}
		}
	}
}

@Composable
internal fun EncounterActionButtons(
	turnStep: CombatTurnStep,
	onNextTurn: () -> Unit,
	onEnd: () -> Unit
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Button(
			onClick = onNextTurn,
			modifier = Modifier
				.weight(1f)
				.height(ACTION_BUTTON_HEIGHT_DP.dp),
			colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
		) {
			Text(
				text = if (turnStep == CombatTurnStep.READY_TO_END) {
					stringResource(R.string.end_turn_button)
				} else {
					stringResource(R.string.encounter_skip_turn_button)
				},
				color = MaterialTheme.colorScheme.onPrimary
			)
		}
		Button(
			onClick = onEnd,
			modifier = Modifier
				.weight(1f)
				.height(ACTION_BUTTON_HEIGHT_DP.dp),
			colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
		) {
			Text(
				text = stringResource(R.string.end_encounter_button),
				color = MaterialTheme.colorScheme.onSecondary
			)
		}
	}
}

