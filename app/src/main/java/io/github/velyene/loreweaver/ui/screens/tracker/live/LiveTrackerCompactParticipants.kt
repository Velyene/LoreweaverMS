/*
 * FILE: LiveTrackerCompactParticipants.kt
 *
 * TABLE OF CONTENTS:
 * 1. Compact Party / Enemy Panel Entrypoints
 * 2. Compact Participant Section
 * 3. Compact Participant Card Helpers
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.screens.AddConditionDialog
import io.github.velyene.loreweaver.ui.theme.AntiqueGold
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal
import io.github.velyene.loreweaver.ui.theme.DangerRed
import kotlin.math.abs

private data class CompactParticipantSectionState(
	val title: String,
	val supportingText: String,
	val emptyMessage: String,
	val participants: List<LiveParticipantUiModel>,
	val currentParticipantId: String?,
	val selectedTargetId: String?,
	val focusedCombatantId: String?,
	val isCompactBattleMode: Boolean,
	val selectableTargetIds: Set<String>,
)

private data class CompactParticipantCardState(
	val participant: LiveParticipantUiModel,
	val statusLabel: String,
	val isCurrentParticipant: Boolean,
	val isSelectedTarget: Boolean,
	val isFocused: Boolean,
	val isCompactBattleMode: Boolean,
	val isSelectableTarget: Boolean,
)

private data class CompactParticipantCardUiState(
	val containerColor: Color,
	val contentColor: Color,
	val borderWidth: Dp,
	val borderColor: Color,
	val compactPaddingVertical: Dp,
	val compactPaddingHorizontal: Dp,
	val showInteractionHint: Boolean,
	val showQuickHpControls: Boolean,
)

internal data class BattlefieldRosterPanelState(
	val rosterParticipants: List<LiveParticipantUiModel>,
	val selection: BattlefieldRosterSelectionState,
)

internal data class BattlefieldRosterSelectionState(
	val currentParticipantId: String?,
	val selectedTargetId: String?,
	val focusedCombatantId: String?,
	val isCompactBattleMode: Boolean,
	val selectableTargetIds: Set<String>,
)

internal data class BattlefieldRosterPanelCallbacks(
	val onSelectTarget: (String) -> Unit,
	val onFocusCombatant: (String) -> Unit,
	val onHpChange: (String, Int) -> Unit,
	val onSetHp: (String, Int) -> Unit,
	val onMarkDefeated: (String) -> Unit,
	val onAddParticipantNote: (String, String) -> Unit,
	val onDuplicateEnemy: (String) -> Unit,
	val onRemoveCombatant: (String) -> Unit,
	val onAddCondition: (String, String, Int?, Boolean) -> Unit,
	val onRemoveCondition: (String, String) -> Unit,
)

private data class CompactParticipantCardCallbacks(
	val onHpChange: (String, Int) -> Unit,
	val onSetHp: ((String, Int) -> Unit)?,
	val onMarkDefeated: ((String) -> Unit)?,
	val onAddParticipantNote: ((String, String) -> Unit)?,
	val onDuplicateEnemy: ((String) -> Unit)?,
	val onRemoveCombatant: ((String) -> Unit)?,
	val onAddCondition: ((String, String, Int?, Boolean) -> Unit)?,
	val onRemoveCondition: ((String, String) -> Unit)?,
	val onClick: () -> Unit,
)

private class CompactParticipantDialogState {
	var showAddConditionDialog by mutableStateOf(false)
	var showManualHpDialog by mutableStateOf(false)
	var showNoteDialog by mutableStateOf(false)
	var showDamageHealDialog by mutableStateOf(false)
	var showParticipantDetailDialog by mutableStateOf(false)
}

@Composable
private fun rememberCompactParticipantDialogState(combatantId: String): CompactParticipantDialogState {
	return remember(combatantId) { CompactParticipantDialogState() }
}

@Composable
internal fun BattlefieldRosterPanel(
	state: BattlefieldRosterPanelState,
	callbacks: BattlefieldRosterPanelCallbacks,
) {
	var collapsedEnemyGroups by remember(state.rosterParticipants) { mutableStateOf(setOf<String>()) }
	val partyParticipants = remember(state.rosterParticipants) {
		state.rosterParticipants.filter(LiveParticipantUiModel::isPlayer)
	}
	val enemyGroups = remember(state.rosterParticipants) {
		state.rosterParticipants
			.filterNot(LiveParticipantUiModel::isPlayer)
			.groupBy { participant -> participant.combatant.name.enemyGroupKey() }
			.toSortedMap(String.CASE_INSENSITIVE_ORDER)
	}

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
			.padding(12.dp)
	) {
		Text(
			text = stringResource(R.string.encounter_battlefield_roster_title),
			style = MaterialTheme.typography.labelSmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			modifier = Modifier.semantics { heading() },
		)
		Spacer(modifier = Modifier.height(4.dp))
		Text(
			text = stringResource(R.string.encounter_battlefield_roster_supporting_text),
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
		Spacer(modifier = Modifier.height(8.dp))

		if (state.rosterParticipants.isEmpty()) {
			Text(
				text = stringResource(R.string.encounter_battlefield_roster_empty_message),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
			)
			return@Column
		}

		Column(verticalArrangement = Arrangement.spacedBy(if (state.selection.isCompactBattleMode) 6.dp else 8.dp)) {
			partyParticipants.forEach { participant ->
				BattlefieldParticipantCard(
					participant = participant,
					rosterState = state,
					callbacks = callbacks,
				)
			}

			enemyGroups.forEach { (groupKey, participants) ->
				val forceExpanded = participants.any { participant ->
					participant.combatant.characterId == state.selection.selectedTargetId ||
					state.selection.selectableTargetIds.contains(participant.combatant.characterId)
				}
				val isCollapsible = participants.size > 1
				val isCollapsed = isCollapsible && !forceExpanded && collapsedEnemyGroups.contains(groupKey)
				if (isCollapsible) {
					EnemyGroupHeader(
						groupName = groupKey,
						participantCount = participants.size,
						aliveCount = participants.count { !it.isEliminated },
						isCollapsed = isCollapsed,
						onToggleCollapsed = {
							collapsedEnemyGroups = if (groupKey in collapsedEnemyGroups) {
								collapsedEnemyGroups - groupKey
							} else {
								collapsedEnemyGroups + groupKey
							}
						},
					)
				}
				if (!isCollapsed) {
					participants.forEach { participant ->
						BattlefieldParticipantCard(
							participant = participant,
							rosterState = state,
							callbacks = callbacks,
						)
					}
				}
			}
		}
	}
}

@Composable
internal fun SecondaryPartyPanel(
	partyMembers: List<LiveParticipantUiModel>,
	currentParticipantId: String?,
	selectedTargetId: String?,
	focusedCombatantId: String?,
	isCompactBattleMode: Boolean,
	selectableTargetIds: Set<String>,
	onSelectTarget: (String) -> Unit,
	onFocusCombatant: (String) -> Unit,
	onHpChange: (String, Int) -> Unit,
) {
	CompactParticipantSection(
		state = CompactParticipantSectionState(
			title = stringResource(R.string.encounter_secondary_party_title),
			supportingText = stringResource(R.string.encounter_secondary_party_supporting_text),
			emptyMessage = stringResource(R.string.encounter_secondary_party_empty_message),
			participants = partyMembers,
			currentParticipantId = currentParticipantId,
			selectedTargetId = selectedTargetId,
			focusedCombatantId = focusedCombatantId,
			isCompactBattleMode = isCompactBattleMode,
			selectableTargetIds = selectableTargetIds,
		),
		statusFor = { participant ->
			when {
				participant.combatant.characterId == currentParticipantId -> stringResource(R.string.encounter_status_active)
				participant.isEliminated -> stringResource(R.string.encounter_status_downed)
				else -> stringResource(R.string.encounter_status_ready)
			}
		},
		onSelectTarget = onSelectTarget,
		onFocusCombatant = onFocusCombatant,
		onHpChange = onHpChange,
	)
}

@Composable
internal fun EnemyPanel(
	enemies: List<LiveParticipantUiModel>,
	currentParticipantId: String?,
	selectedTargetId: String?,
	focusedCombatantId: String?,
	isCompactBattleMode: Boolean,
	selectableTargetIds: Set<String>,
	onSelectTarget: (String) -> Unit,
	onFocusCombatant: (String) -> Unit,
	onHpChange: (String, Int) -> Unit,
) {
	CompactParticipantSection(
		state = CompactParticipantSectionState(
			title = stringResource(R.string.encounter_enemy_panel_title),
			supportingText = stringResource(R.string.encounter_enemy_panel_supporting_text),
			emptyMessage = stringResource(R.string.encounter_enemy_panel_empty_message),
			participants = enemies,
			currentParticipantId = currentParticipantId,
			selectedTargetId = selectedTargetId,
			focusedCombatantId = focusedCombatantId,
			isCompactBattleMode = isCompactBattleMode,
			selectableTargetIds = selectableTargetIds,
		),
		statusFor = { participant ->
			when {
				participant.combatant.characterId == currentParticipantId -> stringResource(R.string.encounter_status_active)
				participant.isEliminated -> stringResource(R.string.encounter_status_defeated)
				else -> stringResource(R.string.encounter_status_alive)
			}
		},
		onSelectTarget = onSelectTarget,
		onFocusCombatant = onFocusCombatant,
		onHpChange = onHpChange,
	)
}

@Composable
private fun CompactParticipantSection(
	state: CompactParticipantSectionState,
	statusFor: @Composable (LiveParticipantUiModel) -> String,
	onSelectTarget: (String) -> Unit,
	onFocusCombatant: (String) -> Unit,
	onHpChange: (String, Int) -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
			.padding(12.dp)
	) {
		Text(
			text = state.title,
			style = MaterialTheme.typography.labelSmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			modifier = Modifier.semantics { heading() },
		)
		Spacer(modifier = Modifier.height(4.dp))
		Text(
			text = state.supportingText,
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
		Spacer(modifier = Modifier.height(8.dp))

		if (state.participants.isEmpty()) {
			Text(
				text = state.emptyMessage,
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
			)
			return@Column
		}

		CompactParticipantSectionItems(
			state = state,
			statusFor = statusFor,
			onSelectTarget = onSelectTarget,
			onFocusCombatant = onFocusCombatant,
			onHpChange = onHpChange,
		)
	}
}

@Composable
private fun CompactParticipantSectionItems(
	state: CompactParticipantSectionState,
	statusFor: @Composable (LiveParticipantUiModel) -> String,
	onSelectTarget: (String) -> Unit,
	onFocusCombatant: (String) -> Unit,
	onHpChange: (String, Int) -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(if (state.isCompactBattleMode) 6.dp else 8.dp)) {
		state.participants.forEach { participant ->
			key(participant.combatant.characterId) {
				CompactParticipantCard(
					state = sectionParticipantCardState(
						participant = participant,
						sectionState = state,
						statusLabel = statusFor(participant),
					),
					callbacks = sectionParticipantCardCallbacks(
						participant = participant,
						sectionState = state,
						onSelectTarget = onSelectTarget,
						onFocusCombatant = onFocusCombatant,
						onHpChange = onHpChange,
					),
				)
			}
		}
	}
}

@Composable
private fun CompactParticipantCard(
	state: CompactParticipantCardState,
	callbacks: CompactParticipantCardCallbacks,
) {
	val uiState = compactParticipantCardUiState(state)
	val interactionLabel = compactParticipantInteractionLabel(state)
	val compactSummary = stringResource(
		R.string.combatant_hp_label,
		state.participant.combatant.currentHp,
		state.participant.combatant.maxHp,
	)
	val showDmControls = (callbacks.onSetHp != null || callbacks.onMarkDefeated != null || callbacks.onAddParticipantNote != null) &&
		(state.isFocused || state.isCurrentParticipant || state.isSelectedTarget || state.isSelectableTarget)
	val dialogState = rememberCompactParticipantDialogState(state.participant.combatant.characterId)

	CompactParticipantCardBody(
		state = state,
		callbacks = callbacks,
		uiState = uiState,
		interactionLabel = interactionLabel,
		compactSummary = compactSummary,
		showDmControls = showDmControls,
		dialogState = dialogState,
	)

	CompactParticipantCardDialogHosts(
		state = state,
		callbacks = callbacks,
		dialogState = dialogState,
	)
}

@Composable
private fun CompactParticipantCardBody(
	state: CompactParticipantCardState,
	callbacks: CompactParticipantCardCallbacks,
	uiState: CompactParticipantCardUiState,
	interactionLabel: String,
	compactSummary: String,
	showDmControls: Boolean,
	dialogState: CompactParticipantDialogState,
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = callbacks.onClick),
		colors = CardDefaults.cardColors(containerColor = uiState.containerColor),
		border = BorderStroke(width = uiState.borderWidth, color = uiState.borderColor),
	) {
		Column(
			modifier = Modifier.padding(
				horizontal = uiState.compactPaddingHorizontal,
				vertical = uiState.compactPaddingVertical,
			),
		) {
			CompactParticipantCardHeader(
				state = state,
				uiState = uiState,
				compactSummary = compactSummary,
				interactionLabel = interactionLabel,
			)
			if (uiState.showQuickHpControls) {
				Spacer(modifier = Modifier.height(if (state.isCompactBattleMode) 6.dp else 8.dp))
				CompactQuickHpControls(
					characterId = state.participant.combatant.characterId,
					onHpChange = callbacks.onHpChange,
					isCompactBattleMode = state.isCompactBattleMode,
				)
			}
			if (showDmControls && callbacks.onAddCondition != null && callbacks.onRemoveCondition != null) {
				Spacer(modifier = Modifier.height(if (state.isCompactBattleMode) 6.dp else 8.dp))
				CombatantConditionsRow(
					combatant = state.participant.combatant,
					persistentConditions = state.participant.persistentConditions,
					onRemoveCondition = callbacks.onRemoveCondition,
					onAddConditionClick = { dialogState.showAddConditionDialog = true },
				)
			}
			if (showDmControls) {
				Spacer(modifier = Modifier.height(if (state.isCompactBattleMode) 6.dp else 8.dp))
				CompactParticipantDmActions(
					participant = state.participant,
					onOpenDetails = { dialogState.showParticipantDetailDialog = true },
					onOpenDamageHeal = { dialogState.showDamageHealDialog = true },
					onOpenApplyStatus = if (callbacks.onAddCondition != null) {
						{ dialogState.showAddConditionDialog = true }
					} else {
						null
					},
					onSetHp = callbacks.onSetHp?.let { _ -> { dialogState.showManualHpDialog = true } },
					onMarkDefeated = callbacks.onMarkDefeated,
					onAddParticipantNote = callbacks.onAddParticipantNote?.let { { dialogState.showNoteDialog = true } },
					onDuplicateEnemy = callbacks.onDuplicateEnemy,
					onRemoveCombatant = callbacks.onRemoveCombatant,
				)
			}
		}
	}
}

@Composable
private fun CompactParticipantCardDialogHosts(
	state: CompactParticipantCardState,
	callbacks: CompactParticipantCardCallbacks,
	dialogState: CompactParticipantDialogState,
) {
	if (dialogState.showAddConditionDialog && callbacks.onAddCondition != null) {
		AddConditionDialog(
			participantName = state.participant.combatant.name,
			onConfirm = { condition: String, duration: Int?, persistsAcrossEncounters: Boolean ->
				callbacks.onAddCondition(
					state.participant.combatant.characterId,
					condition,
					duration,
					persistsAcrossEncounters,
				)
				@Suppress("UNUSED_VALUE")
				dialogState.showAddConditionDialog = false
			},
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				dialogState.showAddConditionDialog = false
			},
		)
	}

	if (dialogState.showDamageHealDialog) {
		HpAdjustmentDialog(
			participantName = state.participant.combatant.name,
			onConfirm = { delta, note ->
				callbacks.onHpChange(state.participant.combatant.characterId, delta)
				if (note.isNotBlank() && callbacks.onAddParticipantNote != null) {
					callbacks.onAddParticipantNote(
						state.participant.combatant.characterId,
						buildHpAdjustmentNote(delta = delta, note = note),
					)
				}
				@Suppress("UNUSED_VALUE")
				dialogState.showDamageHealDialog = false
			},
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				dialogState.showDamageHealDialog = false
			},
		)
	}

	if (dialogState.showManualHpDialog && callbacks.onSetHp != null) {
		ManualHpDialog(
			participantName = state.participant.combatant.name,
			currentHp = state.participant.combatant.currentHp,
			maxHp = state.participant.combatant.maxHp,
			onConfirm = { hp ->
				callbacks.onSetHp(state.participant.combatant.characterId, hp)
				@Suppress("UNUSED_VALUE")
				dialogState.showManualHpDialog = false
			},
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				dialogState.showManualHpDialog = false
			},
		)
	}

	if (dialogState.showNoteDialog && callbacks.onAddParticipantNote != null) {
		ParticipantNoteDialog(
			participantName = state.participant.combatant.name,
			onConfirm = { note ->
				callbacks.onAddParticipantNote(state.participant.combatant.characterId, note)
				@Suppress("UNUSED_VALUE")
				dialogState.showNoteDialog = false
			},
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				dialogState.showNoteDialog = false
			},
		)
	}

	if (dialogState.showParticipantDetailDialog) {
		ParticipantDetailDialog(
			participant = state.participant,
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				dialogState.showParticipantDetailDialog = false
			},
			actions = ParticipantDetailDialogActions(
				onOpenDamageHeal = {
					@Suppress("UNUSED_VALUE")
					dialogState.showParticipantDetailDialog = false
					@Suppress("UNUSED_VALUE")
					dialogState.showDamageHealDialog = true
				},
				onOpenSetHp = {
					@Suppress("UNUSED_VALUE")
					dialogState.showParticipantDetailDialog = false
					@Suppress("UNUSED_VALUE")
					dialogState.showManualHpDialog = true
				},
				onOpenApplyStatus = {
					@Suppress("UNUSED_VALUE")
					dialogState.showParticipantDetailDialog = false
					@Suppress("UNUSED_VALUE")
					dialogState.showAddConditionDialog = true
				},
				onOpenEditNotes = {
					@Suppress("UNUSED_VALUE")
					dialogState.showParticipantDetailDialog = false
					@Suppress("UNUSED_VALUE")
					dialogState.showNoteDialog = true
				},
				onMarkDefeated = {
					callbacks.onMarkDefeated?.invoke(state.participant.combatant.characterId)
					@Suppress("UNUSED_VALUE")
					dialogState.showParticipantDetailDialog = false
				},
				onRestoreFullHp = {
					callbacks.onSetHp?.invoke(state.participant.combatant.characterId, state.participant.combatant.maxHp)
					@Suppress("UNUSED_VALUE")
					dialogState.showParticipantDetailDialog = false
				},
				onRemoveCondition = { characterId, conditionName ->
					callbacks.onRemoveCondition?.invoke(characterId, conditionName) ?: Unit
				},
				onDuplicateEnemy = callbacks.onDuplicateEnemy?.let { callback ->
					{
						callback(state.participant.combatant.characterId)
						@Suppress("UNUSED_VALUE")
						dialogState.showParticipantDetailDialog = false
					}
				},
				onRemoveCombatant = callbacks.onRemoveCombatant?.let { callback ->
					{
						callback(state.participant.combatant.characterId)
						@Suppress("UNUSED_VALUE")
						dialogState.showParticipantDetailDialog = false
					}
				},
			),
		)
	}
}

@Composable
private fun BattlefieldParticipantCard(
	participant: LiveParticipantUiModel,
	rosterState: BattlefieldRosterPanelState,
	callbacks: BattlefieldRosterPanelCallbacks,
) {
	CompactParticipantCard(
		state = battlefieldParticipantCardState(participant, rosterState),
		callbacks = battlefieldParticipantCardCallbacks(participant, rosterState, callbacks),
	)
}

private fun sectionParticipantCardState(
	participant: LiveParticipantUiModel,
	sectionState: CompactParticipantSectionState,
	statusLabel: String,
): CompactParticipantCardState {
	return CompactParticipantCardState(
		participant = participant,
		statusLabel = statusLabel,
		isCurrentParticipant = participant.combatant.characterId == sectionState.currentParticipantId,
		isSelectedTarget = participant.combatant.characterId == sectionState.selectedTargetId,
		isFocused = participant.combatant.characterId == sectionState.focusedCombatantId,
		isCompactBattleMode = sectionState.isCompactBattleMode,
		isSelectableTarget = sectionState.selectableTargetIds.contains(participant.combatant.characterId),
	)
}

private fun sectionParticipantCardCallbacks(
	participant: LiveParticipantUiModel,
	sectionState: CompactParticipantSectionState,
	onSelectTarget: (String) -> Unit,
	onFocusCombatant: (String) -> Unit,
	onHpChange: (String, Int) -> Unit,
): CompactParticipantCardCallbacks {
	return CompactParticipantCardCallbacks(
		onHpChange = onHpChange,
		onSetHp = null,
		onMarkDefeated = null,
		onAddParticipantNote = null,
		onDuplicateEnemy = null,
		onRemoveCombatant = null,
		onAddCondition = null,
		onRemoveCondition = null,
		onClick = focusAndMaybeSelectClick(
			participantId = participant.combatant.characterId,
			selectableTargetIds = sectionState.selectableTargetIds,
			onFocusCombatant = onFocusCombatant,
			onSelectTarget = onSelectTarget,
		),
	)
}

@Composable
private fun battlefieldParticipantCardState(
	participant: LiveParticipantUiModel,
	rosterState: BattlefieldRosterPanelState,
): CompactParticipantCardState {
	return CompactParticipantCardState(
		participant = participant,
		statusLabel = when {
			participant.combatant.characterId == rosterState.selection.currentParticipantId -> stringResource(R.string.encounter_status_active)
			participant.isEliminated && participant.isPlayer -> stringResource(R.string.encounter_status_downed)
			participant.isEliminated -> stringResource(R.string.encounter_status_defeated)
			participant.isPlayer -> stringResource(R.string.encounter_status_ready)
			else -> stringResource(R.string.encounter_status_alive)
		},
		isCurrentParticipant = participant.combatant.characterId == rosterState.selection.currentParticipantId,
		isSelectedTarget = participant.combatant.characterId == rosterState.selection.selectedTargetId,
		isFocused = participant.combatant.characterId == rosterState.selection.focusedCombatantId,
		isCompactBattleMode = rosterState.selection.isCompactBattleMode,
		isSelectableTarget = rosterState.selection.selectableTargetIds.contains(participant.combatant.characterId),
	)
}

private fun battlefieldParticipantCardCallbacks(
	participant: LiveParticipantUiModel,
	rosterState: BattlefieldRosterPanelState,
	callbacks: BattlefieldRosterPanelCallbacks,
): CompactParticipantCardCallbacks {
	return CompactParticipantCardCallbacks(
		onHpChange = callbacks.onHpChange,
		onSetHp = callbacks.onSetHp,
		onMarkDefeated = callbacks.onMarkDefeated,
		onAddParticipantNote = callbacks.onAddParticipantNote,
		onDuplicateEnemy = if (participant.isPlayer) null else callbacks.onDuplicateEnemy,
		onRemoveCombatant = if (participant.isPlayer) null else callbacks.onRemoveCombatant,
		onAddCondition = callbacks.onAddCondition,
		onRemoveCondition = callbacks.onRemoveCondition,
		onClick = focusAndMaybeSelectClick(
			participantId = participant.combatant.characterId,
			selectableTargetIds = rosterState.selection.selectableTargetIds,
			onFocusCombatant = callbacks.onFocusCombatant,
			onSelectTarget = callbacks.onSelectTarget,
		),
	)
}

private fun focusAndMaybeSelectClick(
	participantId: String,
	selectableTargetIds: Set<String>,
	onFocusCombatant: (String) -> Unit,
	onSelectTarget: (String) -> Unit,
): () -> Unit {
	return {
		onFocusCombatant(participantId)
		if (selectableTargetIds.contains(participantId)) {
			onSelectTarget(participantId)
		}
	}
}

@Composable
private fun EnemyGroupHeader(
	groupName: String,
	participantCount: Int,
	aliveCount: Int,
	isCollapsed: Boolean,
	onToggleCollapsed: () -> Unit,
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween,
	) {
		Column(modifier = Modifier.weight(1f)) {
			Text(
				text = groupName,
				style = MaterialTheme.typography.labelMedium,
				fontWeight = FontWeight.SemiBold,
				color = MaterialTheme.colorScheme.onSurface,
			)
			Text(
				text = stringResource(R.string.encounter_enemy_group_summary, participantCount, aliveCount),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		}
		TextButton(onClick = onToggleCollapsed) {
			Text(
				text = stringResource(
					if (isCollapsed) R.string.encounter_enemy_group_expand_action else R.string.encounter_enemy_group_collapse_action,
				),
			)
		}
	}
}

@Composable
private fun CompactParticipantDmActions(
	participant: LiveParticipantUiModel,
	onOpenDetails: () -> Unit,
	onOpenDamageHeal: () -> Unit,
	onOpenApplyStatus: (() -> Unit)?,
	onSetHp: (() -> Unit)?,
	onMarkDefeated: ((String) -> Unit)?,
	onAddParticipantNote: (() -> Unit)?,
	onDuplicateEnemy: ((String) -> Unit)?,
	onRemoveCombatant: ((String) -> Unit)?,
) {
	Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
		CompactParticipantActionGroup(title = stringResource(R.string.encounter_compact_controls_hp_title)) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(6.dp),
			) {
				OutlinedButton(onClick = onOpenDamageHeal, modifier = Modifier.weight(1f)) {
					Text(text = stringResource(R.string.encounter_damage_heal_button), fontSize = 11.sp)
				}
				onSetHp?.let {
					OutlinedButton(onClick = it, modifier = Modifier.weight(1f)) {
						Text(text = stringResource(R.string.encounter_manual_hp_button), fontSize = 11.sp)
					}
				}
				onMarkDefeated?.let {
					OutlinedButton(
						onClick = { it(participant.combatant.characterId) },
						modifier = Modifier.weight(1f),
					) {
						Text(text = stringResource(R.string.encounter_mark_defeated_button), fontSize = 11.sp)
					}
				}
			}
		}
		CompactParticipantActionGroup(title = stringResource(R.string.encounter_compact_controls_status_notes_title)) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(6.dp),
			) {
				OutlinedButton(onClick = onOpenDetails, modifier = Modifier.weight(1f)) {
					Text(text = stringResource(R.string.encounter_participant_details_button), fontSize = 11.sp)
				}
				onOpenApplyStatus?.let {
					OutlinedButton(onClick = it, modifier = Modifier.weight(1f)) {
						Text(text = stringResource(R.string.encounter_apply_status_button), fontSize = 11.sp)
					}
				}
				onAddParticipantNote?.let {
					OutlinedButton(onClick = it, modifier = Modifier.weight(1f)) {
						Text(text = stringResource(R.string.encounter_add_note_button), fontSize = 11.sp)
					}
				}
			}
		}
		if (!participant.isPlayer && (onDuplicateEnemy != null || onRemoveCombatant != null)) {
			CompactParticipantActionGroup(title = stringResource(R.string.encounter_compact_controls_enemy_admin_title)) {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(6.dp),
				) {
					onDuplicateEnemy?.let {
						OutlinedButton(
							onClick = { it(participant.combatant.characterId) },
							modifier = Modifier.weight(1f),
						) {
							Text(text = stringResource(R.string.encounter_duplicate_enemy_button), fontSize = 11.sp)
						}
					}
					onRemoveCombatant?.let {
						OutlinedButton(
							onClick = { it(participant.combatant.characterId) },
							modifier = Modifier.weight(1f),
						) {
							Text(text = stringResource(R.string.remove_button), fontSize = 11.sp)
						}
					}
				}
			}
		}
	}
}

@Composable
private fun CompactParticipantActionGroup(
	title: String,
	content: @Composable () -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
		Text(
			text = title,
			style = MaterialTheme.typography.labelSmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			fontWeight = FontWeight.SemiBold,
		)
		content()
	}
}

@Composable
private fun CompactParticipantCardHeader(
	state: CompactParticipantCardState,
	uiState: CompactParticipantCardUiState,
	compactSummary: String,
	interactionLabel: String,
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween,
	) {
		Column(modifier = Modifier.weight(1f)) {
			Text(
				text = state.participant.combatant.name,
				style = if (state.isCompactBattleMode) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.titleSmall,
				fontWeight = FontWeight.SemiBold,
				color = uiState.contentColor,
			)
			if (state.isCompactBattleMode) {
				Text(
					text = "${state.participant.typeLabel} • $compactSummary",
					style = MaterialTheme.typography.labelSmall,
					color = uiState.contentColor.copy(alpha = 0.8f),
				)
			} else {
				Text(
					text = state.participant.typeLabel,
					style = MaterialTheme.typography.bodySmall,
					color = uiState.contentColor.copy(alpha = 0.8f),
				)
				Text(
					text = stringResource(
						R.string.encounter_active_hp_summary,
						state.participant.combatant.currentHp,
						state.participant.combatant.maxHp,
					),
					style = MaterialTheme.typography.bodySmall,
					color = uiState.contentColor.copy(alpha = 0.8f),
				)
			}
		}
		Column(horizontalAlignment = Alignment.End) {
			Text(
				text = state.statusLabel,
				style = if (state.isCompactBattleMode) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium,
				fontWeight = FontWeight.Bold,
				color = if (state.participant.isEliminated) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
			)
			if (uiState.showInteractionHint) {
				Text(
					text = interactionLabel,
					style = MaterialTheme.typography.labelSmall,
					color = uiState.contentColor.copy(alpha = 0.82f),
				)
			}
		}
	}
}

private fun buildHpAdjustmentNote(delta: Int, note: String): String {
	val trimmedNote = note.trim()
	if (trimmedNote.isBlank()) return ""
	val actionLabel = if (delta < 0) "Damage" else "Healing"
	return "$actionLabel note (${abs(delta)}): $trimmedNote"
}

@Composable
private fun compactParticipantCardUiState(state: CompactParticipantCardState): CompactParticipantCardUiState {
	val accentColor = if (state.participant.isPlayer) ArcaneTeal else DangerRed
	return CompactParticipantCardUiState(
		containerColor = when {
			state.isCurrentParticipant -> MaterialTheme.colorScheme.primaryContainer
			state.isSelectedTarget -> MaterialTheme.colorScheme.tertiaryContainer
			state.isFocused -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.72f)
			state.participant.isPlayer -> ArcaneTeal.copy(alpha = 0.12f)
			else -> DangerRed.copy(alpha = 0.12f)
		},
		contentColor = when {
			state.isCurrentParticipant -> MaterialTheme.colorScheme.onPrimaryContainer
			state.isSelectedTarget -> MaterialTheme.colorScheme.onTertiaryContainer
			state.isFocused -> MaterialTheme.colorScheme.onSecondaryContainer
			else -> MaterialTheme.colorScheme.onSurfaceVariant
		},
		borderWidth = when {
			state.isSelectedTarget || state.isCurrentParticipant -> 2.dp
			state.isFocused -> 1.5.dp
			else -> 1.dp
		},
		borderColor = when {
			state.isSelectedTarget -> AntiqueGold
			state.isCurrentParticipant -> MaterialTheme.colorScheme.primary
			state.isFocused -> AntiqueGold.copy(alpha = 0.78f)
			else -> accentColor.copy(alpha = 0.55f)
		},
		compactPaddingVertical = if (state.isCompactBattleMode) 8.dp else 10.dp,
		compactPaddingHorizontal = if (state.isCompactBattleMode) 10.dp else 12.dp,
		showInteractionHint = !state.isCompactBattleMode || state.isFocused || state.isSelectedTarget || state.isSelectableTarget,
		showQuickHpControls = !state.isCompactBattleMode || state.isCurrentParticipant || state.isSelectedTarget || state.isFocused || state.isSelectableTarget,
	)
}

@Composable
private fun compactParticipantInteractionLabel(state: CompactParticipantCardState): String {
	return when {
		state.isSelectedTarget -> stringResource(R.string.encounter_interaction_targeted)
		state.isSelectableTarget -> stringResource(R.string.encounter_interaction_tap_to_target)
		state.isFocused -> stringResource(R.string.encounter_interaction_focused)
		else -> stringResource(R.string.encounter_interaction_tap_to_focus)
	}
}

@Composable
private fun CompactQuickHpControls(
	characterId: String,
	onHpChange: (String, Int) -> Unit,
	isCompactBattleMode: Boolean,
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(if (isCompactBattleMode) 4.dp else 6.dp),
	) {
		listOf(-5, -1, 1, 5).forEach { delta ->
			OutlinedButton(
				onClick = { onHpChange(characterId, delta) },
				modifier = Modifier
					.weight(1f)
					.height(if (isCompactBattleMode) 32.dp else 36.dp),
				contentPadding = PaddingValues(0.dp),
			) {
				Text(
					text = if (delta > 0) "+$delta" else delta.toString(),
					fontSize = if (isCompactBattleMode) 11.sp else 12.sp,
					fontWeight = FontWeight.Medium,
				)
			}
		}
	}
}

private fun String.enemyGroupKey(): String = trim().replace(Regex("""\s+\d+$"""), "")

