/*
 * FILE: CombatTrackerRoute.kt
 *
 * TABLE OF CONTENTS:
 * 1. Combat Tracker Route (CombatTrackerScreen)
 * 2. Screen Model (TrackerScreenModel)
 * 3. Content Switcher (TrackerContent)
 * 4. Party Combatant Builder (buildPartyCombatants)
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.util.EncounterDifficultyResult
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.INITIATIVE_ROLL_MAX
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.INITIATIVE_ROLL_MIN
import io.github.velyene.loreweaver.ui.screens.tracker.live.LiveTrackerCallbacks
import io.github.velyene.loreweaver.ui.screens.tracker.live.LiveTrackerView
import io.github.velyene.loreweaver.ui.screens.tracker.live.LiveTrackerViewState
import io.github.velyene.loreweaver.ui.viewmodels.ActionResolutionType
import io.github.velyene.loreweaver.ui.viewmodels.EncounterLifecycle
import io.github.velyene.loreweaver.ui.viewmodels.CombatTurnStep
import io.github.velyene.loreweaver.ui.viewmodels.PendingTurnAction
import io.github.velyene.loreweaver.ui.screens.tracker.setup.EncounterSetupView
import io.github.velyene.loreweaver.ui.viewmodels.CombatViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CombatTrackerScreen(
	encounterId: String? = null,
	onBack: () -> Unit,
	onEndEncounter: () -> Unit,
	viewModel: CombatViewModel = hiltViewModel(),
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val snackbarHostState = remember { SnackbarHostState() }
	var showEncounterMenu by remember { mutableStateOf(false) }
	// The screen model snapshots the view-model state into a UI-focused contract so the
	// route can keep setup/live rendering logic simple and stable across recompositions.
	val screenModel = remember(uiState, encounterId, viewModel, onEndEncounter) {
		TrackerScreenModel(
			isCombatActive = uiState.isCombatActive,
			encounterId = encounterId,
			encounterName = uiState.currentEncounterName,
			encounterLifecycle = uiState.encounterLifecycle,
			notes = uiState.encounterNotes,
			combatants = uiState.combatants,
			availableCharacters = uiState.availableCharacters,
			encounterDifficulty = uiState.encounterDifficulty,
			round = uiState.currentRound,
			turnIndex = uiState.currentTurnIndex,
			statuses = uiState.activeStatuses,
			turnStep = uiState.turnStep,
			pendingAction = uiState.pendingAction,
			selectedTargetId = uiState.selectedTargetId,
			onNotesChange = viewModel::updateNotes,
			onStartEncounter = viewModel::startEncounter,
			onTogglePartyMember = { character ->
				val alreadyAdded = uiState.combatants.any { it.characterId == character.id }
				if (alreadyAdded) {
					viewModel.removeCombatant(character.id)
				} else {
					viewModel.addParty(listOf(buildPartyCombatant(character)))
				}
			},
			onAddEnemy = viewModel::addEnemy,
			onRemoveCombatant = viewModel::removeCombatant,
			onSelectAction = viewModel::selectAction,
			onSelectTarget = viewModel::selectTarget,
			onApplyActionResult = viewModel::applyActionResult,
			onClearPendingTurn = viewModel::clearPendingTurn,
			onNextTurn = viewModel::nextTurn,
			onHpChange = viewModel::updateCombatantHp,
			onAddCondition = viewModel::addCondition,
			onRemoveCondition = viewModel::removeCondition,
			onEndEncounter = { viewModel.saveAndPauseEncounter(onEndEncounter) }
		)
	}
	val retryActionLabel = stringResource(R.string.retry_action)
	val defaultLiveTitle = stringResource(R.string.combat_tracker_live_title)
	val setupTitle = stringResource(R.string.combat_tracker_setup_title)

	LaunchedEffect(encounterId) {
		viewModel.loadEncounter(encounterId)
	}

	LaunchedEffect(uiState.error) {
		uiState.error?.let { errorMsg ->
			val result = snackbarHostState.showSnackbar(
				message = errorMsg,
				actionLabel = if (uiState.onRetry != null) retryActionLabel else null,
				duration = SnackbarDuration.Long
			)
			if (result == SnackbarResult.ActionPerformed) {
				uiState.onRetry?.invoke()
			}
			viewModel.clearError()
		}
	}

	Scaffold(
		snackbarHost = { SnackbarHost(snackbarHostState) },
		topBar = {
			TopAppBar(
					title = {
						if (uiState.isCombatActive) {
							Column {
								Text(uiState.currentEncounterName.ifBlank { defaultLiveTitle })
								Text(
									text = stringResource(
										R.string.encounter_top_bar_active_subtitle,
										uiState.currentRound,
										formatEncounterLifecycleLabel(uiState.encounterLifecycle)
									),
									style = MaterialTheme.typography.labelSmall,
									color = MaterialTheme.colorScheme.onSurfaceVariant
								)
							}
						} else {
							Column {
								Text(setupTitle)
								Text(
									text = formatEncounterLifecycleLabel(uiState.encounterLifecycle),
									style = MaterialTheme.typography.labelSmall,
									color = MaterialTheme.colorScheme.onSurfaceVariant
								)
							}
						}
					},
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = stringResource(R.string.back_button)
						)
					}
					},
					actions = {
						if (uiState.isCombatActive) {
										IconButton(onClick = { viewModel.saveAndPauseEncounter(onEndEncounter) }) {
								Icon(
									Icons.Default.Save,
									contentDescription = stringResource(R.string.encounter_save_exit_button)
								)
							}
							Box {
								IconButton(onClick = { showEncounterMenu = true }) {
									Icon(
										Icons.Default.MoreVert,
										contentDescription = stringResource(R.string.encounter_options_button)
									)
								}
								DropdownMenu(
									expanded = showEncounterMenu,
									onDismissRequest = { showEncounterMenu = false }
								) {
									DropdownMenuItem(
										text = { Text(stringResource(R.string.encounter_save_exit_button)) },
										onClick = {
											showEncounterMenu = false
														viewModel.saveAndPauseEncounter(onEndEncounter)
										}
									)
									DropdownMenuItem(
										text = { Text(stringResource(R.string.end_encounter_button)) },
										onClick = {
											showEncounterMenu = false
														viewModel.saveAndPauseEncounter(onEndEncounter)
										}
									)
								}
							}
						}
				}
			)
		}
	) { padding ->
		Box(
			modifier = Modifier
				.padding(padding)
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.background)
		) {
			TrackerContent(
				model = screenModel
			)
		}
	}
}

private data class TrackerScreenModel(
	val isCombatActive: Boolean,
	val encounterId: String?,
	val encounterName: String,
	val encounterLifecycle: EncounterLifecycle,
	val notes: String,
	val combatants: List<CombatantState>,
	val availableCharacters: List<CharacterEntry>,
	val encounterDifficulty: EncounterDifficultyResult?,
	val round: Int,
	val turnIndex: Int,
	val statuses: List<String>,
	val turnStep: CombatTurnStep,
	val pendingAction: PendingTurnAction?,
	val selectedTargetId: String?,
	val onNotesChange: (String) -> Unit,
	val onStartEncounter: (String?) -> Unit,
	val onTogglePartyMember: (CharacterEntry) -> Unit,
	val onAddEnemy: (String, Int, Int) -> Unit,
	val onRemoveCombatant: (String) -> Unit,
	val onSelectAction: (String) -> Unit,
	val onSelectTarget: (String) -> Unit,
	val onApplyActionResult: (ActionResolutionType, Int?) -> Unit,
	val onClearPendingTurn: () -> Unit,
	val onNextTurn: () -> Unit,
	val onHpChange: (String, Int) -> Unit,
	val onAddCondition: (String, String, Int?, Boolean) -> Unit,
	val onRemoveCondition: (String, String) -> Unit,
	val onEndEncounter: () -> Unit
)

@Composable
private fun TrackerContent(
	model: TrackerScreenModel
) {
	// Setup and live tracking intentionally split here so each branch can evolve as an
	// independent screen while the route continues to own shared state wiring.
	if (!model.isCombatActive) {
		EncounterSetupView(
			notes = model.notes,
			combatants = model.combatants,
			availablePartyMembers = model.availableCharacters,
			encounterDifficulty = model.encounterDifficulty,
			onNotesChange = model.onNotesChange,
			onStart = { model.onStartEncounter(model.encounterId) },
			onTogglePartyMember = model.onTogglePartyMember,
			onAddEnemy = model.onAddEnemy,
			onRemoveCombatant = model.onRemoveCombatant
		)
		return
	}

	LiveTrackerView(
		state = LiveTrackerViewState(
			encounterName = model.encounterName,
			encounterNotes = model.notes,
			round = model.round,
			combatants = model.combatants,
			availableCharacters = model.availableCharacters,
			turnIndex = model.turnIndex,
			statuses = model.statuses,
			turnStep = model.turnStep,
			pendingAction = model.pendingAction,
			selectedTargetId = model.selectedTargetId
		),
		callbacks = LiveTrackerCallbacks(
			onSelectAction = model.onSelectAction,
			onSelectTarget = model.onSelectTarget,
			onApplyActionResult = model.onApplyActionResult,
			onClearPendingTurn = model.onClearPendingTurn,
			onNextTurn = model.onNextTurn,
			onHpChange = model.onHpChange,
			onAddCondition = model.onAddCondition,
			onRemoveCondition = model.onRemoveCondition,
			onEnd = model.onEndEncounter
		)
	)
}

private fun buildPartyCombatant(character: CharacterEntry): CombatantState {
	// Party members receive a light initiative roll here so the setup flow can seed the
	// tracker immediately without a separate round of manual initiative entry.
	return CombatantState(
		character.id,
		character.name,
		character.initiative + Random.nextInt(INITIATIVE_ROLL_MIN, INITIATIVE_ROLL_MAX),
		character.hp,
		character.maxHp
	)
}

@Composable
private fun formatEncounterLifecycleLabel(lifecycle: EncounterLifecycle): String {
	return when (lifecycle) {
		EncounterLifecycle.DRAFT -> stringResource(R.string.encounter_lifecycle_draft)
		EncounterLifecycle.ACTIVE -> stringResource(R.string.encounter_lifecycle_active)
		EncounterLifecycle.PAUSED -> stringResource(R.string.encounter_lifecycle_paused)
		EncounterLifecycle.COMPLETED -> stringResource(R.string.encounter_lifecycle_completed)
		EncounterLifecycle.ARCHIVED -> stringResource(R.string.encounter_lifecycle_archived)
	}
}
