/*
 * FILE: LiveTrackerView.kt
 *
 * TABLE OF CONTENTS:
 * 1. Live Tracker Screen (LiveTrackerView)
 * 2. Combatant List (CombatantHpList)
 * 3. Current Turn Controls (CurrentCombatantControls, ActionChipsRow, QuickHpControls)
 * 4. Combat Log (CombatLogSection)
 * 5. Encounter Actions (EncounterActionButtons)
 * 6. Shared UI Elements (TrackerDisplayBox, ActionChip)
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.ACTION_BUTTON_HEIGHT_DP
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.HP_QUICK_ADJUSTMENTS
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.LOG_HEIGHT_DP
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.QUICK_HP_BUTTON_HEIGHT_DP
import io.github.velyene.loreweaver.ui.screens.tracker.components.TrackerModeBadge
import io.github.velyene.loreweaver.ui.screens.visibleVerticalScrollbar

@Composable
internal fun LiveTrackerView(
	round: Int,
	combatants: List<CombatantState>,
	turnIndex: Int,
	statuses: List<String>,
	onAction: (String) -> Unit,
	onNextTurn: () -> Unit,
	onHpChange: (characterId: String, delta: Int) -> Unit,
	onAddCondition: (characterId: String, condition: String, duration: Int?) -> Unit,
	onRemoveCondition: (characterId: String, conditionName: String) -> Unit,
	onEnd: () -> Unit
) {
	val current = combatants.getOrNull(turnIndex)

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		TrackerModeBadge(
			label = stringResource(R.string.combat_tracker_badge_label),
			containerColor = MaterialTheme.colorScheme.primary,
			contentColor = MaterialTheme.colorScheme.onPrimary
		)
		Spacer(modifier = Modifier.height(16.dp))

		TrackerDisplayBox(
			label = stringResource(R.string.round_counter, round),
			modifier = Modifier.fillMaxWidth()
		)
		Spacer(modifier = Modifier.height(8.dp))

		CombatantHpList(
			combatants = combatants,
			turnIndex = turnIndex,
			onHpChange = onHpChange,
			onAddCondition = onAddCondition,
			onRemoveCondition = onRemoveCondition,
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f)
		)

		Spacer(modifier = Modifier.height(8.dp))

		// The current combatant section is only shown when the turn index resolves to a live
		// combatant, which prevents stale controls from showing during edge-case transitions.
		current?.let {
			CurrentCombatantControls(
				combatant = it,
				onAction = onAction,
				onHpChange = onHpChange
			)
		}

		CombatLogSection(statuses = statuses)

		Spacer(modifier = Modifier.height(8.dp))

		EncounterActionButtons(
			onNextTurn = onNextTurn,
			onEnd = onEnd
		)
	}
}

@Composable
private fun CombatantHpList(
	combatants: List<CombatantState>,
	turnIndex: Int,
	onHpChange: (characterId: String, delta: Int) -> Unit,
	onAddCondition: (characterId: String, condition: String, duration: Int?) -> Unit,
	onRemoveCondition: (characterId: String, conditionName: String) -> Unit,
	modifier: Modifier = Modifier
) {
	val listState = rememberLazyListState()

	LazyColumn(
		state = listState,
		modifier = modifier
			.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
			.padding(8.dp)
			.visibleVerticalScrollbar(listState)
	) {
		itemsIndexed(combatants, key = { _, combatant -> combatant.characterId }) { index, combatant ->
			CombatantListItem(
				combatant = combatant,
				isActive = index == turnIndex,
				onHpChange = onHpChange,
				onAddCondition = onAddCondition,
				onRemoveCondition = onRemoveCondition
			)
		}
	}
}

@Composable
private fun CurrentCombatantControls(
	combatant: CombatantState,
	onAction: (String) -> Unit,
	onHpChange: (characterId: String, delta: Int) -> Unit
) {
	Text(
		text = stringResource(R.string.combatant_turn, combatant.name),
		color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
		style = MaterialTheme.typography.labelSmall,
		modifier = Modifier.semantics { heading() }
	)
	Spacer(modifier = Modifier.height(4.dp))

	ActionChipsRow(onAction = onAction)

	Spacer(modifier = Modifier.height(4.dp))

	QuickHpControls(
		characterId = combatant.characterId,
		onHpChange = onHpChange
	)
}

@Composable
private fun ActionChipsRow(onAction: (String) -> Unit) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		combatActionChipSpecs.forEach { action ->
			val actionLabel = stringResource(action.labelRes)
			ActionChip(
				label = actionLabel,
				onClick = { onAction(actionLabel) }
			)
		}
	}
}

private val combatActionChipSpecs = listOf(
	CombatActionChipSpec(labelRes = R.string.combat_action_strike),
	CombatActionChipSpec(labelRes = R.string.combat_action_cast),
	CombatActionChipSpec(labelRes = R.string.combat_action_sneak),
	CombatActionChipSpec(labelRes = R.string.combat_action_dodge)
)

private data class CombatActionChipSpec(
	val labelRes: Int
)

@Composable
private fun QuickHpControls(
	characterId: String,
	onHpChange: (characterId: String, delta: Int) -> Unit
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(4.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = stringResource(R.string.hp_label),
			fontSize = 12.sp,
			modifier = Modifier.padding(end = 4.dp)
		)
		HP_QUICK_ADJUSTMENTS.forEach { delta ->
			OutlinedButton(
				onClick = { onHpChange(characterId, delta) },
				modifier = Modifier
					.weight(1f)
					.height(QUICK_HP_BUTTON_HEIGHT_DP.dp),
				contentPadding = PaddingValues(0.dp)
			) {
				Text(if (delta > 0) "+$delta" else delta.toString(), fontSize = 12.sp)
			}
		}
	}
}

@Composable
private fun CombatLogSection(statuses: List<String>) {
	val listState = rememberLazyListState()

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.height(LOG_HEIGHT_DP.dp)
			.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
			.padding(8.dp)
	) {
		Text(
			stringResource(R.string.session_combat_log_title),
			color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
			style = MaterialTheme.typography.labelSmall,
			modifier = Modifier.semantics { heading() }
		)
		// Newest statuses are shown first so a long encounter log keeps the most recent
		// action announcements within the default viewport.
		LazyColumn(
			state = listState,
			modifier = Modifier.visibleVerticalScrollbar(listState)
		) {
			itemsIndexed(statuses.reversed(), key = { index, status -> "$index-$status" }) { _, status ->
				Text(
					stringResource(R.string.combat_log_bullet, status),
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
private fun EncounterActionButtons(
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
				stringResource(R.string.end_turn_button),
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
				stringResource(R.string.end_encounter_button),
				color = MaterialTheme.colorScheme.onSecondary
			)
		}
	}
}

@Composable
private fun TrackerDisplayBox(label: String, modifier: Modifier = Modifier) {
	Box(
		modifier = modifier
			.background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
			.semantics {
				contentDescription = label
			}
			.padding(16.dp),
		contentAlignment = Alignment.Center
	) {
		Text(
			label,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			fontWeight = FontWeight.Bold
		)
	}
}

@Composable
private fun ActionChip(label: String, onClick: () -> Unit) {
	Box(
		modifier = Modifier
			.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(20.dp))
			.clickable(
				role = Role.Button,
				onClickLabel = label,
				onClick = onClick,
			)
			.padding(horizontal = 16.dp, vertical = 8.dp)
	) {
		Text(
			label,
			color = MaterialTheme.colorScheme.onPrimaryContainer,
			fontSize = 12.sp,
			fontWeight = FontWeight.Medium
		)
	}
}

