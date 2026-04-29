/*
 * FILE: LiveTrackerView.kt
 *
 * TABLE OF CONTENTS:
 * 1. Live Tracker Screen (LiveTrackerView)
 * 2. Shared Encounter Dashboard Sections
 * 3. Active Participant Panel and Turn Loop
 * 4. Secondary Party / Enemy Panels
 * 5. Combat Log and Footer Actions
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.util.CharacterParty
import io.github.velyene.loreweaver.ui.screens.AddConditionDialog
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.ACTION_BUTTON_HEIGHT_DP
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.LOG_HEIGHT_DP
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.QUICK_HP_BUTTON_HEIGHT_DP
import io.github.velyene.loreweaver.ui.screens.tracker.components.TrackerModeBadge
import io.github.velyene.loreweaver.ui.screens.visibleVerticalScrollbar
import io.github.velyene.loreweaver.ui.theme.AntiqueGold
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal
import io.github.velyene.loreweaver.ui.theme.DangerRed
import io.github.velyene.loreweaver.ui.viewmodels.ActionResolutionType
import io.github.velyene.loreweaver.ui.viewmodels.CombatTurnStep
import io.github.velyene.loreweaver.ui.viewmodels.PendingTurnAction

private data class LiveParticipantUiModel(
	val combatant: CombatantState,
	val typeLabel: String,
	val isPlayer: Boolean,
	val isEliminated: Boolean,
	val notes: String,
	val persistentConditions: Set<String>,
	val actionLabels: List<String>,
	val resourceLines: List<String>
)

internal data class LiveTrackerViewState(
	val encounterName: String,
	val encounterNotes: String,
	val round: Int,
	val combatants: List<CombatantState>,
	val availableCharacters: List<CharacterEntry>,
	val turnIndex: Int,
	val statuses: List<String>,
	val turnStep: CombatTurnStep,
	val pendingAction: PendingTurnAction?,
	val selectedTargetId: String?
)

internal data class LiveTrackerCallbacks(
	val onSelectAction: (String) -> Unit,
	val onSelectTarget: (String) -> Unit,
	val onApplyActionResult: (ActionResolutionType, Int?) -> Unit,
	val onClearPendingTurn: () -> Unit,
	val onNextTurn: () -> Unit,
	val onHpChange: (characterId: String, delta: Int) -> Unit,
	val onAddCondition: (characterId: String, condition: String, duration: Int?, persistsAcrossEncounters: Boolean) -> Unit,
	val onRemoveCondition: (characterId: String, conditionName: String) -> Unit,
	val onEnd: () -> Unit
)

private data class LiveTrackerUiState(
	val participants: List<LiveParticipantUiModel>,
	val currentParticipant: LiveParticipantUiModel?,
	val selectedTarget: LiveParticipantUiModel?,
	val targetableParticipants: List<LiveParticipantUiModel>,
	val selectableTargetIds: Set<String>,
	val secondaryPartyMembers: List<LiveParticipantUiModel>,
	val enemies: List<LiveParticipantUiModel>
)

private data class CurrentParticipantPanelState(
	val encounterName: String,
	val participant: LiveParticipantUiModel?,
	val pendingAction: PendingTurnAction?,
	val selectedTarget: LiveParticipantUiModel?,
	val turnStep: CombatTurnStep,
	val targetableParticipants: List<LiveParticipantUiModel>
)

@Composable
internal fun LiveTrackerView(
	state: LiveTrackerViewState,
	callbacks: LiveTrackerCallbacks
) {
	val trackerState = rememberLiveTrackerUiState(
		combatants = state.combatants,
		availableCharacters = state.availableCharacters,
		turnIndex = state.turnIndex,
		pendingAction = state.pendingAction,
		selectedTargetId = state.selectedTargetId
	)
	val contentListState = rememberLazyListState()

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

		TurnTrackerStrip(
			round = state.round,
			participants = trackerState.participants,
			turnIndex = state.turnIndex,
			modifier = Modifier.fillMaxWidth()
		)

		Spacer(modifier = Modifier.height(12.dp))
		LiveTrackerContentList(
			encounterName = state.encounterName,
			encounterNotes = state.encounterNotes,
			statuses = state.statuses,
			turnStep = state.turnStep,
			pendingAction = state.pendingAction,
			selectedTargetId = state.selectedTargetId,
			trackerState = trackerState,
			callbacks = callbacks,
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f)
				.visibleVerticalScrollbar(contentListState),
			contentListState = contentListState
		)

		Spacer(modifier = Modifier.height(12.dp))

		EncounterActionButtons(
			turnStep = state.turnStep,
			onNextTurn = callbacks.onNextTurn,
			onEnd = callbacks.onEnd
		)
	}
}

@Composable
private fun rememberLiveTrackerUiState(
	combatants: List<CombatantState>,
	availableCharacters: List<CharacterEntry>,
	turnIndex: Int,
	pendingAction: PendingTurnAction?,
	selectedTargetId: String?
): LiveTrackerUiState {
	val participants = remember(combatants, availableCharacters) {
		buildLiveParticipants(combatants = combatants, availableCharacters = availableCharacters)
	}
	return remember(participants, turnIndex, pendingAction, selectedTargetId) {
		val currentParticipant = participants.getOrNull(turnIndex)
		val targetableParticipants = participants.filter { participant ->
			when {
				currentParticipant == null -> false
				pendingAction == null -> false
				pendingAction.allowsSelfTarget -> !participant.isEliminated || participant.combatant.characterId == currentParticipant.combatant.characterId
				else -> participant.combatant.characterId != currentParticipant.combatant.characterId && !participant.isEliminated
			}
		}
		val selectedTarget = participants.firstOrNull { it.combatant.characterId == selectedTargetId }
			?: if (pendingAction?.allowsSelfTarget == true) currentParticipant else null
		val partyMembers = participants.filter(LiveParticipantUiModel::isPlayer)
		LiveTrackerUiState(
			participants = participants,
			currentParticipant = currentParticipant,
			selectedTarget = selectedTarget,
			targetableParticipants = targetableParticipants,
			selectableTargetIds = targetableParticipants.map { it.combatant.characterId }.toSet(),
			secondaryPartyMembers = partyMembers.filterNot {
				it.combatant.characterId == currentParticipant?.combatant?.characterId
			},
			enemies = participants.filterNot(LiveParticipantUiModel::isPlayer)
		)
	}
}

@Composable
private fun LiveTrackerContentList(
	encounterName: String,
	encounterNotes: String,
	statuses: List<String>,
	turnStep: CombatTurnStep,
	pendingAction: PendingTurnAction?,
	selectedTargetId: String?,
	trackerState: LiveTrackerUiState,
	callbacks: LiveTrackerCallbacks,
	modifier: Modifier = Modifier,
	contentListState: androidx.compose.foundation.lazy.LazyListState
) {
	LazyColumn(
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

private fun buildLiveParticipants(
	combatants: List<CombatantState>,
	availableCharacters: List<CharacterEntry>
): List<LiveParticipantUiModel> {
	val charactersById = availableCharacters.associateBy(CharacterEntry::id)
	return combatants.map { combatant ->
		val character = charactersById[combatant.characterId]
		val isPlayer = character?.party == CharacterParty.ADVENTURERS
		val customActionLabels = character
			?.actions
			?.map { it.name.trim() }
			?.filter(String::isNotBlank)
			.orEmpty()
		LiveParticipantUiModel(
			combatant = combatant,
			typeLabel = character?.type?.takeIf(String::isNotBlank) ?: if (isPlayer) {
				CharacterParty.ADVENTURERS
			} else {
				stringLiteralEnemy
			},
			isPlayer = isPlayer,
			isEliminated = combatant.currentHp <= 0,
			notes = character?.notes.orEmpty(),
			persistentConditions = character?.activeConditions.orEmpty(),
			actionLabels = if (customActionLabels.isNotEmpty()) customActionLabels else defaultActionLabels,
			resourceLines = buildResourceLines(character, combatant)
		)
	}
}

private const val stringLiteralEnemy = "Enemy"
private val defaultActionLabels = listOf("Strike", "Cast", "Sneak", "Dodge")

private fun buildResourceLines(
	character: CharacterEntry?,
	combatant: CombatantState
): List<String> {
	return buildList {
		if (combatant.tempHp > 0) {
			add("Temp HP ${combatant.tempHp}")
		}
		if (character != null) {
			if (character.maxMana > 0) {
				add("Mana ${character.mana}/${character.maxMana}")
			}
			if (character.maxStamina > 0) {
				add("Stamina ${character.stamina}/${character.maxStamina}")
			}
			character.resources.take(3).forEach { resource ->
				add("${resource.name} ${resource.current}/${resource.max}")
			}
		}
	}
}

@Composable
private fun TurnTrackerStrip(
	round: Int,
	participants: List<LiveParticipantUiModel>,
	turnIndex: Int,
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
						else -> stringResource(R.string.encounter_turn_label_upcoming)
					},
					isCurrent = index == 0
				)
			}
		}
	}
}

private fun upcomingTurnWindow(
	participants: List<LiveParticipantUiModel>,
	turnIndex: Int,
	windowSize: Int = 6
): List<LiveParticipantUiModel> {
	if (participants.isEmpty()) return emptyList()
	return List(minOf(windowSize, participants.size)) { offset ->
		participants[(turnIndex + offset).floorMod(participants.size)]
	}
}

private fun Int.floorMod(divisor: Int): Int = ((this % divisor) + divisor) % divisor

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
				text = if (participant.isPlayer) stringResource(R.string.encounter_turn_chip_player) else stringResource(R.string.encounter_turn_chip_enemy),
				style = MaterialTheme.typography.bodySmall,
				color = contentColor.copy(alpha = 0.8f)
			)
		}
	}
}

@Composable
@Suppress("UNUSED_VALUE")
private fun CurrentParticipantPanel(
	state: CurrentParticipantPanelState,
	callbacks: LiveTrackerCallbacks
) {
	val participant = state.participant
	if (participant == null) {
		Card(
			modifier = Modifier.fillMaxWidth(),
			colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
		) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					text = state.encounterName.ifBlank { stringResource(R.string.combat_tracker_live_title) },
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold
				)
				Spacer(modifier = Modifier.height(4.dp))
				Text(
					text = stringResource(R.string.encounter_active_panel_empty_message),
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
		}
		return
	}

	var showAddConditionDialog by remember(participant.combatant.characterId) { mutableStateOf(false) }

	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
		border = BorderStroke(2.dp, AntiqueGold)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			Text(
				text = stringResource(R.string.encounter_active_panel_title),
				style = MaterialTheme.typography.labelSmall,
				color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
				modifier = Modifier.semantics { heading() }
			)
			Spacer(modifier = Modifier.height(6.dp))
			Text(
				text = participant.combatant.name,
				style = MaterialTheme.typography.headlineSmall,
				fontWeight = FontWeight.Bold,
				color = MaterialTheme.colorScheme.onPrimaryContainer
			)
			Text(
				text = participant.typeLabel,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
			)

			Spacer(modifier = Modifier.height(12.dp))
			SummaryStatRow(
				primaryLabel = stringResource(
					R.string.encounter_active_hp_summary,
					participant.combatant.currentHp,
					participant.combatant.maxHp
				),
				secondaryLabel = stringResource(
					R.string.combatant_initiative_summary,
					participant.combatant.initiative
				)
			)

			if (participant.resourceLines.isNotEmpty()) {
				Spacer(modifier = Modifier.height(10.dp))
				ResourceLinesSection(resourceLines = participant.resourceLines)
			}

			Spacer(modifier = Modifier.height(12.dp))
			TurnLoopSection(
				turnStep = state.turnStep,
				participant = participant,
				pendingAction = state.pendingAction,
				selectedTarget = state.selectedTarget,
				targetableParticipants = state.targetableParticipants,
				onSelectAction = callbacks.onSelectAction,
				onSelectTarget = callbacks.onSelectTarget,
				onApplyActionResult = callbacks.onApplyActionResult,
				onClearPendingTurn = callbacks.onClearPendingTurn
			)

			Spacer(modifier = Modifier.height(12.dp))
			Text(
				text = stringResource(R.string.conditions_label),
				style = MaterialTheme.typography.labelSmall,
				color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
			)
			Spacer(modifier = Modifier.height(4.dp))
			CombatantConditionsRow(
				combatant = participant.combatant,
				persistentConditions = participant.persistentConditions,
				onRemoveCondition = callbacks.onRemoveCondition,
				onAddConditionClick = { showAddConditionDialog = true }
			)

			Spacer(modifier = Modifier.height(12.dp))
			QuickHpControls(
				characterId = participant.combatant.characterId,
				onHpChange = callbacks.onHpChange
			)

			Spacer(modifier = Modifier.height(12.dp))
			Text(
				text = stringResource(R.string.notes_label),
				style = MaterialTheme.typography.labelSmall,
				color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
			)
			Spacer(modifier = Modifier.height(4.dp))
			Text(
				text = participant.notes.ifBlank { stringResource(R.string.encounter_participant_notes_empty_message) },
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onPrimaryContainer
			)
		}
	}

	if (showAddConditionDialog) {
		AddConditionDialog(
			onConfirm = { condition, duration, persistsAcrossEncounters ->
				callbacks.onAddCondition(
					participant.combatant.characterId,
					condition,
					duration,
					persistsAcrossEncounters
				)
				showAddConditionDialog = false
			},
			onDismiss = { showAddConditionDialog = false }
		)
	}
}

@Composable
private fun TurnLoopSection(
	turnStep: CombatTurnStep,
	participant: LiveParticipantUiModel,
	pendingAction: PendingTurnAction?,
	selectedTarget: LiveParticipantUiModel?,
	targetableParticipants: List<LiveParticipantUiModel>,
	onSelectAction: (String) -> Unit,
	onSelectTarget: (String) -> Unit,
	onApplyActionResult: (ActionResolutionType, Int?) -> Unit,
	onClearPendingTurn: () -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.18f), RoundedCornerShape(10.dp))
			.padding(12.dp)
	) {
		Text(
			text = stringResource(R.string.encounter_turn_flow_title),
			style = MaterialTheme.typography.labelSmall,
			color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
			modifier = Modifier.semantics { heading() }
		)
		Spacer(modifier = Modifier.height(6.dp))
		Text(
			text = turnStepSummary(turnStep),
			style = MaterialTheme.typography.bodyMedium,
			fontWeight = FontWeight.Medium,
			color = MaterialTheme.colorScheme.onPrimaryContainer
		)
		Spacer(modifier = Modifier.height(10.dp))

		when (turnStep) {
			CombatTurnStep.SELECT_ACTION -> {
				Text(
					text = stringResource(R.string.encounter_available_actions_title),
					style = MaterialTheme.typography.labelSmall,
					color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
				)
				Spacer(modifier = Modifier.height(6.dp))
				ParticipantActionChips(
					actionLabels = participant.actionLabels,
					onAction = onSelectAction
				)
			}

			CombatTurnStep.SELECT_TARGET -> {
				Text(
					text = pendingAction?.let {
						stringResource(R.string.encounter_target_prompt_with_action, it.name)
					} ?: stringResource(R.string.encounter_target_prompt),
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onPrimaryContainer
				)
				Spacer(modifier = Modifier.height(8.dp))
				TargetSelectorChips(
					targets = targetableParticipants,
					selectedTargetId = selectedTarget?.combatant?.characterId,
					onSelectTarget = onSelectTarget
				)
			}

			CombatTurnStep.APPLY_RESULT -> {
				ActionResultControls(
					selectedTarget = selectedTarget,
					onApplyActionResult = onApplyActionResult
				)
			}

			CombatTurnStep.READY_TO_END -> {
				Text(
					text = selectedTarget?.let {
						stringResource(
							R.string.encounter_turn_ready_summary,
							pendingAction?.name.orEmpty(),
							it.combatant.name
						)
					} ?: stringResource(R.string.encounter_turn_ready_no_target),
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onPrimaryContainer
				)
			}
		}

		if (pendingAction != null) {
			Spacer(modifier = Modifier.height(8.dp))
			TextButton(onClick = onClearPendingTurn) {
				Text(text = stringResource(R.string.encounter_clear_turn_selection_button))
			}
		}
	}
}

@Composable
private fun ActionResultControls(
	selectedTarget: LiveParticipantUiModel?,
	onApplyActionResult: (ActionResolutionType, Int?) -> Unit
) {
	var amountInput by rememberSaveable(selectedTarget?.combatant?.characterId) { mutableStateOf("") }

	Text(
		text = selectedTarget?.let {
			stringResource(R.string.encounter_apply_result_prompt, it.combatant.name)
		} ?: stringResource(R.string.encounter_apply_result_prompt_no_target),
		style = MaterialTheme.typography.bodyMedium,
		color = MaterialTheme.colorScheme.onPrimaryContainer
	)
	Spacer(modifier = Modifier.height(8.dp))
	OutlinedTextField(
		value = amountInput,
		onValueChange = { amountInput = it.filter(Char::isDigit) },
		label = { Text(stringResource(R.string.encounter_result_amount_label)) },
		keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
		modifier = Modifier.fillMaxWidth()
	)
	Spacer(modifier = Modifier.height(8.dp))
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		OutlinedButton(
			onClick = { onApplyActionResult(ActionResolutionType.MISS, null) },
			modifier = Modifier.weight(1f)
		) {
			Text(text = stringResource(R.string.encounter_result_miss_button))
		}
		Button(
			onClick = { onApplyActionResult(ActionResolutionType.DAMAGE, amountInput.toIntOrNull()) },
			modifier = Modifier.weight(1f)
		) {
			Text(text = stringResource(R.string.encounter_result_damage_button))
		}
		Button(
			onClick = { onApplyActionResult(ActionResolutionType.HEAL, amountInput.toIntOrNull()) },
			modifier = Modifier.weight(1f),
			colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
		) {
			Text(
				text = stringResource(R.string.encounter_result_heal_button),
				color = MaterialTheme.colorScheme.onSecondary
			)
		}
	}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TargetSelectorChips(
	targets: List<LiveParticipantUiModel>,
	selectedTargetId: String?,
	onSelectTarget: (String) -> Unit
) {
	FlowRow(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		targets.forEach { target ->
			TargetChip(
				name = target.combatant.name,
				isSelected = target.combatant.characterId == selectedTargetId,
				onClick = { onSelectTarget(target.combatant.characterId) }
			)
		}
	}
}

@Composable
private fun TargetChip(name: String, isSelected: Boolean, onClick: () -> Unit) {
	val containerColor = if (isSelected) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant
	val contentColor = if (isSelected) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
	Box(
		modifier = Modifier
			.background(containerColor, RoundedCornerShape(20.dp))
			.clickable(onClick = onClick)
			.padding(horizontal = 14.dp, vertical = 8.dp)
	) {
		Text(
			text = name,
			color = contentColor,
			fontSize = 12.sp,
			fontWeight = FontWeight.Medium
		)
	}
}

@Composable
private fun SummaryStatRow(primaryLabel: String, secondaryLabel: String) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		MiniSummaryCard(label = primaryLabel, modifier = Modifier.weight(1f))
		MiniSummaryCard(label = secondaryLabel, modifier = Modifier.weight(1f))
	}
}

@Composable
private fun MiniSummaryCard(label: String, modifier: Modifier = Modifier) {
	Box(
		modifier = modifier
			.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
			.padding(horizontal = 12.dp, vertical = 10.dp),
		contentAlignment = Alignment.Center
	) {
		Text(
			text = label,
			style = MaterialTheme.typography.bodyMedium,
			fontWeight = FontWeight.Medium,
			color = MaterialTheme.colorScheme.onPrimaryContainer
		)
	}
}

@Composable
private fun ResourceLinesSection(resourceLines: List<String>) {
	Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
		resourceLines.forEach { line ->
			Text(
				text = line,
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onPrimaryContainer
			)
		}
	}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ParticipantActionChips(
	actionLabels: List<String>,
	onAction: (String) -> Unit
) {
	FlowRow(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		actionLabels.forEach { actionLabel ->
			ActionChip(label = actionLabel, onClick = { onAction(actionLabel) })
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
				text = stringResource(R.string.encounter_notes_title),
				style = MaterialTheme.typography.labelSmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.semantics { heading() }
			)
			Spacer(modifier = Modifier.height(4.dp))
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
	CompactParticipantSection(
		title = stringResource(R.string.encounter_secondary_party_title),
		emptyMessage = stringResource(R.string.encounter_secondary_party_empty_message),
		participants = partyMembers,
		selectedTargetId = selectedTargetId,
		selectableTargetIds = selectableTargetIds,
		statusFor = { participant ->
			if (participant.isEliminated) {
				stringResource(R.string.encounter_status_downed)
			} else {
				stringResource(R.string.encounter_status_ready)
			}
		},
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
	CompactParticipantSection(
		title = stringResource(R.string.encounter_enemy_panel_title),
		emptyMessage = stringResource(R.string.encounter_enemy_panel_empty_message),
		participants = enemies,
		selectedTargetId = selectedTargetId,
		selectableTargetIds = selectableTargetIds,
		statusFor = { participant ->
			if (participant.isEliminated) {
				stringResource(R.string.encounter_status_defeated)
			} else {
				stringResource(R.string.encounter_status_alive)
			}
		},
		onSelectTarget = onSelectTarget
	)
}

@Composable
private fun CompactParticipantSection(
	title: String,
	emptyMessage: String,
	participants: List<LiveParticipantUiModel>,
	selectedTargetId: String?,
	selectableTargetIds: Set<String>,
	statusFor: @Composable (LiveParticipantUiModel) -> String,
	onSelectTarget: (String) -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
			.padding(12.dp)
	) {
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
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
			)
			return@Column
		}

		Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
			participants.forEach { participant ->
				CompactParticipantCard(
					participant = participant,
					statusLabel = statusFor(participant),
					isSelectedTarget = participant.combatant.characterId == selectedTargetId,
					isSelectableTarget = selectableTargetIds.contains(participant.combatant.characterId),
					onClick = {
						if (selectableTargetIds.contains(participant.combatant.characterId)) {
							onSelectTarget(participant.combatant.characterId)
						}
					}
				)
			}
		}
	}
}

@Composable
private fun CompactParticipantCard(
	participant: LiveParticipantUiModel,
	statusLabel: String,
	isSelectedTarget: Boolean,
	isSelectableTarget: Boolean,
	onClick: () -> Unit
) {
	val accentColor = if (participant.isPlayer) ArcaneTeal else DangerRed
	val containerColor = when {
		isSelectedTarget -> MaterialTheme.colorScheme.tertiaryContainer
		participant.isPlayer -> ArcaneTeal.copy(alpha = 0.12f)
		else -> DangerRed.copy(alpha = 0.12f)
	}
	val contentColor = if (isSelectedTarget) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.then(
				if (isSelectableTarget) Modifier.clickable(onClick = onClick) else Modifier
			),
		colors = CardDefaults.cardColors(containerColor = containerColor),
		border = BorderStroke(
			width = if (isSelectedTarget) 2.dp else 1.dp,
			color = if (isSelectedTarget) AntiqueGold else accentColor.copy(alpha = 0.55f)
		)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 12.dp, vertical = 10.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = participant.combatant.name,
					style = MaterialTheme.typography.titleSmall,
					fontWeight = FontWeight.SemiBold,
					color = contentColor
				)
				Text(
					text = participant.typeLabel,
					style = MaterialTheme.typography.bodySmall,
					color = contentColor.copy(alpha = 0.8f)
				)
				Text(
					text = stringResource(
						R.string.encounter_active_hp_summary,
						participant.combatant.currentHp,
						participant.combatant.maxHp
					),
					style = MaterialTheme.typography.bodySmall,
					color = contentColor.copy(alpha = 0.8f)
				)
			}
			Text(
				text = statusLabel,
				style = MaterialTheme.typography.labelMedium,
				fontWeight = FontWeight.Bold,
				color = if (participant.isEliminated) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
			)
		}
	}
}

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
		listOf(-5, -1, 1, 5).forEach { delta ->
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
private fun EncounterActionButtons(
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

@Composable
private fun ActionChip(label: String, onClick: () -> Unit) {
	Box(
		modifier = Modifier
			.background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(20.dp))
			.clickable(onClick = onClick)
			.semantics { contentDescription = label }
			.padding(horizontal = 14.dp, vertical = 8.dp)
	) {
		Text(
			text = label,
			color = MaterialTheme.colorScheme.onSecondaryContainer,
			fontSize = 12.sp,
			fontWeight = FontWeight.Medium
		)
	}
}

@Composable
private fun turnStepSummary(turnStep: CombatTurnStep): String {
	return when (turnStep) {
		CombatTurnStep.SELECT_ACTION -> stringResource(R.string.encounter_turn_step_select_action)
		CombatTurnStep.SELECT_TARGET -> stringResource(R.string.encounter_turn_step_select_target)
		CombatTurnStep.APPLY_RESULT -> stringResource(R.string.encounter_turn_step_apply_result)
		CombatTurnStep.READY_TO_END -> stringResource(R.string.encounter_turn_step_ready_to_end)
	}
}
