/*
 * FILE: CombatTrackerRoute.kt
 *
 * TABLE OF CONTENTS:
 * 1. Combat Tracker Route (CombatTrackerScreen)
 * 2. Route Chrome and Sheets
 * 3. Screen Model (TrackerScreenModel)
 * 4. Content Switcher (TrackerContent)
 * 5. Party Combatant Builder (buildPartyCombatants)
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.util.CharacterParty
import io.github.velyene.loreweaver.domain.util.EncounterDifficultyResult
import io.github.velyene.loreweaver.domain.util.MonsterReferenceCatalog
import io.github.velyene.loreweaver.ui.screens.tracker.live.LiveTrackerCallbacks
import io.github.velyene.loreweaver.ui.screens.tracker.live.LiveTrackerView
import io.github.velyene.loreweaver.ui.screens.tracker.live.LiveTrackerViewState
import io.github.velyene.loreweaver.ui.screens.tracker.setup.EncounterSetupCallbacks
import io.github.velyene.loreweaver.ui.screens.tracker.setup.EncounterSetupPartyActions
import io.github.velyene.loreweaver.ui.screens.tracker.setup.EncounterSetupView
import io.github.velyene.loreweaver.ui.screens.tracker.setup.EncounterSetupViewState
import io.github.velyene.loreweaver.ui.util.asString
import io.github.velyene.loreweaver.ui.viewmodels.ActionResolutionType
import io.github.velyene.loreweaver.ui.viewmodels.CombatTurnStep
import io.github.velyene.loreweaver.ui.viewmodels.CombatViewModel
import io.github.velyene.loreweaver.ui.viewmodels.EncounterLifecycle
import io.github.velyene.loreweaver.ui.viewmodels.EncounterResult
import io.github.velyene.loreweaver.ui.viewmodels.EnemyLibraryEncounterSetupDraft
import io.github.velyene.loreweaver.ui.viewmodels.InitiativeMode
import io.github.velyene.loreweaver.ui.viewmodels.PendingTurnAction
import io.github.velyene.loreweaver.ui.viewmodels.asEncounterSession
import io.github.velyene.loreweaver.ui.viewmodels.asEncounterSetupState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CombatTrackerScreen(
	encounterId: String? = null,
	startFresh: Boolean = false,
	stagedLibraryDraft: EnemyLibraryEncounterSetupDraft = EnemyLibraryEncounterSetupDraft(),
	onBack: () -> Unit,
	onExitHome: () -> Unit = onBack,
	onEndEncounter: (String) -> Unit,
	viewModel: CombatViewModel = hiltViewModel(),
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val snackbarHostState = remember { SnackbarHostState() }
	var showEncounterMenu by remember { mutableStateOf(false) }
	var showSaveOptionsSheet by remember { mutableStateOf(false) }
	var showEndEncounterSheet by remember { mutableStateOf(false) }
	val encounterSetupState = remember(uiState) { uiState.asEncounterSetupState() }
	val encounterSession = remember(uiState) { uiState.asEncounterSession() }
	// The screen model snapshots the view-model state into a UI-focused contract so the
	// route can keep setup/live rendering logic simple and stable across recompositions.
	val screenModel = remember(uiState, encounterId, startFresh, stagedLibraryDraft, viewModel, onEndEncounter, onExitHome) {
		TrackerScreenModel(
			isCombatActive = uiState.isCombatActive,
			encounterId = encounterId,
			startFresh = startFresh,
			encounterName = encounterSession.encounterName,
			encounterLifecycle = uiState.encounterLifecycle,
			initiativeMode = encounterSetupState.initiativeMode,
			locationTerrain = encounterSetupState.locationTerrain,
			notes = encounterSetupState.notes,
			combatants = uiState.combatants,
			availableCharacters = uiState.availableCharacters,
			encounterDifficulty = uiState.encounterDifficulty,
			importedLibraryEnemyCount = stagedLibraryDraft.totalEnemyCount,
			importedTemporaryEnemyCount = stagedLibraryDraft.temporaryEnemyCount,
			generationSettings = uiState.generationSettings,
			generationDetails = uiState.generationDetails,
			round = encounterSession.roundNumber,
			turnIndex = encounterSession.currentTurnIndex,
			statuses = encounterSession.logEntries,
			canGoToPreviousTurn = uiState.canGoToPreviousTurn,
			turnStep = uiState.turnStep,
			pendingAction = uiState.pendingAction,
			selectedTargetId = uiState.selectedTargetId,
			onEncounterNameChange = viewModel::updateEncounterName,
			onLocationTerrainChange = viewModel::updateLocationTerrain,
			onNotesChange = viewModel::updateNotes,
			onInitiativeModeChange = viewModel::updateInitiativeMode,
			onStartEncounter = viewModel::startEncounter,
			onAddEntireParty = {
				val missingPartyMembers = uiState.availableCharacters
					.filter { it.party == CharacterParty.ADVENTURERS }
					.filterNot { character ->
						uiState.combatants.any { it.characterId == character.id }
					}
				if (missingPartyMembers.isNotEmpty()) {
					viewModel.addParty(buildPartyCombatants(missingPartyMembers))
				}
			},
			onClearPartyMembers = {
				val adventurerIds = uiState.availableCharacters
					.filter { it.party == CharacterParty.ADVENTURERS }
					.map(CharacterEntry::id)
					.toSet()
				uiState.combatants
					.filter { adventurerIds.contains(it.characterId) }
					.forEach { combatant -> viewModel.removeCombatant(combatant.characterId) }
			},
			onTogglePartyMember = { character ->
				val alreadyAdded = uiState.combatants.any { it.characterId == character.id }
				if (alreadyAdded) {
					viewModel.removeCombatant(character.id)
				} else {
					viewModel.addParty(listOf(buildPartyCombatant(character)))
				}
			},
			onAddEnemies = viewModel::addEnemies,
			onUpdateGenerationSettings = viewModel::updateGenerationSettings,
			onGenerateEncounter = viewModel::generateEncounter,
			onUpdateCombatantInitiative = viewModel::updateCombatantInitiative,
			onMoveCombatantUp = viewModel::moveCombatantUp,
			onMoveCombatantDown = viewModel::moveCombatantDown,
			onRemoveCombatant = viewModel::removeCombatant,
			onSelectAction = viewModel::selectAction,
			onSelectTarget = viewModel::selectTarget,
			onApplyActionResult = viewModel::applyActionResult,
			onClearPendingTurn = viewModel::clearPendingTurn,
			onPreviousTurn = viewModel::previousTurn,
			onNextTurn = viewModel::nextTurn,
			onAdvanceRound = viewModel::advanceRound,
			onHpChange = viewModel::updateCombatantHp,
			onSetHp = viewModel::setCombatantHp,
			onMarkDefeated = viewModel::markCombatantDefeated,
			onAddParticipantNote = viewModel::addParticipantNote,
			onDuplicateEnemy = viewModel::duplicateEnemy,
			onAddCondition = viewModel::addCondition,
			onRemoveCondition = viewModel::removeCondition,
			onSaveAndExit = { viewModel.saveAndPauseEncounter(onBack) },
			onExitHome = onExitHome,
			onEndEncounter = {
				showEncounterMenu = false
				showEndEncounterSheet = true
			}
		)
	}
	val retryActionLabel = stringResource(R.string.retry_action)
	val defaultLiveTitle = stringResource(R.string.combat_tracker_live_title)
	val setupTitle = stringResource(R.string.combat_tracker_setup_title)
	val errorMessage = uiState.error?.asString()

	LaunchedEffect(encounterId, startFresh) {
		viewModel.loadEncounter(
			encounterId = encounterId,
			startFresh = startFresh,
			stagedEnemies = stagedLibraryDraft.stagedEnemies,
		)
	}

	LaunchedEffect(errorMessage) {
		errorMessage?.let { errorMsg ->
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
		snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
		topBar = {
			CombatTrackerTopBar(
				uiState = uiState,
				defaultLiveTitle = defaultLiveTitle,
				setupTitle = setupTitle,
				onBack = onBack,
				showEncounterMenu = showEncounterMenu,
				onOpenSaveOptions = { showSaveOptionsSheet = true },
				onOpenEncounterMenu = { showEncounterMenu = true },
				onDismissEncounterMenu = { showEncounterMenu = false },
				onEndEncounter = screenModel.onEndEncounter,
				onExitHome = screenModel.onExitHome,
			)
		},
		content = { padding: PaddingValues ->
			TrackerScaffoldContent(
				padding = padding,
				model = screenModel,
			)
		},
	)

	if (showSaveOptionsSheet) {
		SaveEncounterOptionsSheet(
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				showSaveOptionsSheet = false
			},
			onSaveAndExit = {
				@Suppress("UNUSED_VALUE")
				showSaveOptionsSheet = false
				screenModel.onSaveAndExit()
			},
			onResumeLater = {
				@Suppress("UNUSED_VALUE")
				showSaveOptionsSheet = false
				viewModel.saveAndPauseEncounter(onExitHome)
			},
			onReturnHome = {
				@Suppress("UNUSED_VALUE")
				showSaveOptionsSheet = false
				onExitHome()
			},
		)
	}

	if (showEndEncounterSheet) {
		EndEncounterResultSheet(
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				showEndEncounterSheet = false
			},
			onChooseResult = { result ->
				@Suppress("UNUSED_VALUE")
				showEndEncounterSheet = false
				viewModel.endEncounterToSummary(result = result, onComplete = onEndEncounter)
			},
		)
	}
}

internal const val SAVE_ENCOUNTER_OPTIONS_SHEET_TAG = "save_encounter_options_sheet"
internal const val END_ENCOUNTER_RESULT_SHEET_TAG = "end_encounter_result_sheet"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CombatTrackerTopBar(
	uiState: io.github.velyene.loreweaver.ui.viewmodels.CombatUiState,
	defaultLiveTitle: String,
	setupTitle: String,
	onBack: () -> Unit,
	showEncounterMenu: Boolean,
	onOpenSaveOptions: () -> Unit,
	onOpenEncounterMenu: () -> Unit,
	onDismissEncounterMenu: () -> Unit,
	onEndEncounter: () -> Unit,
	onExitHome: () -> Unit,
) {
	TopAppBar(
		title = {
			CombatTrackerTopBarTitle(
				uiState = uiState,
				defaultLiveTitle = defaultLiveTitle,
				setupTitle = setupTitle,
			)
		},
		navigationIcon = {
			IconButton(onClick = onBack) {
				Icon(
					Icons.AutoMirrored.Filled.ArrowBack,
					contentDescription = stringResource(R.string.back_button),
				)
			}
		},
		actions = {
			if (uiState.isCombatActive) {
				IconButton(onClick = onOpenSaveOptions) {
					Icon(
						Icons.Default.Save,
						contentDescription = stringResource(R.string.encounter_save_exit_button),
					)
				}
				Box {
					IconButton(onClick = onOpenEncounterMenu) {
						Icon(
							Icons.Default.MoreVert,
							contentDescription = stringResource(R.string.encounter_options_button),
						)
					}
					DropdownMenu(
						expanded = showEncounterMenu,
						onDismissRequest = onDismissEncounterMenu,
					) {
						CombatTrackerMenuItem(
							label = stringResource(R.string.encounter_save_exit_button),
							onClick = {
								onDismissEncounterMenu()
								onOpenSaveOptions()
							},
						)
						CombatTrackerMenuItem(
							label = stringResource(R.string.end_encounter_button),
							onClick = {
								onDismissEncounterMenu()
								onEndEncounter()
							},
						)
						CombatTrackerMenuItem(
							label = stringResource(R.string.encounter_exit_home_button),
							onClick = {
								onDismissEncounterMenu()
								onExitHome()
							},
						)
					}
				}
			}
		},
	)
}

@Composable
private fun CombatTrackerTopBarTitle(
	uiState: io.github.velyene.loreweaver.ui.viewmodels.CombatUiState,
	defaultLiveTitle: String,
	setupTitle: String,
) {
	Column {
		if (uiState.isCombatActive) {
			Text(uiState.currentEncounterName.ifBlank { defaultLiveTitle })
			Text(
				text = stringResource(
					R.string.encounter_top_bar_active_subtitle,
					uiState.currentRound,
					formatEncounterLifecycleLabel(uiState.encounterLifecycle),
				),
				style = MaterialTheme.typography.labelSmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		} else {
			Text(setupTitle)
			Text(
				text = formatEncounterLifecycleLabel(uiState.encounterLifecycle),
				style = MaterialTheme.typography.labelSmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		}
	}
}

@Composable
private fun CombatTrackerMenuItem(
	label: String,
	onClick: () -> Unit,
) {
	DropdownMenuItem(
		text = { Text(label) },
		onClick = onClick,
	)
}

@Composable
private fun TrackerScaffoldContent(
	padding: PaddingValues,
	model: TrackerScreenModel,
) {
	Box(
		modifier = Modifier
			.padding(padding)
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.background),
	) {
		TrackerContent(model = model)
	}
}

@Composable
private fun TrackerSheetActionButton(
	label: String,
	onClick: () -> Unit,
) {
	TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
		Text(label)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SaveEncounterOptionsSheet(
	onDismiss: () -> Unit,
	onSaveAndExit: () -> Unit,
	onResumeLater: () -> Unit,
	onReturnHome: () -> Unit,
) {
	ModalBottomSheet(
		onDismissRequest = onDismiss,
		modifier = Modifier.testTag(SAVE_ENCOUNTER_OPTIONS_SHEET_TAG),
	) {
		Column(
			modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
		) {
			Text(
				text = stringResource(R.string.encounter_save_options_title),
				style = MaterialTheme.typography.titleMedium,
			)
			Text(
				text = stringResource(R.string.encounter_save_options_supporting_text),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
			)
			HorizontalDivider()
			TrackerSheetActionButton(
				label = stringResource(R.string.encounter_save_exit_button),
				onClick = onSaveAndExit,
			)
			TrackerSheetActionButton(
				label = stringResource(R.string.encounter_resume_later_button),
				onClick = onResumeLater,
			)
			TrackerSheetActionButton(
				label = stringResource(R.string.encounter_return_home_button),
				onClick = onReturnHome,
			)
			TrackerSheetActionButton(
				label = stringResource(R.string.cancel_button),
				onClick = onDismiss,
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EndEncounterResultSheet(
	onDismiss: () -> Unit,
	onChooseResult: (EncounterResult) -> Unit,
) {
	ModalBottomSheet(
		onDismissRequest = onDismiss,
		modifier = Modifier.testTag(END_ENCOUNTER_RESULT_SHEET_TAG),
	) {
		Column(
			modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
		) {
			Text(
				text = stringResource(R.string.end_encounter_result_title),
				style = MaterialTheme.typography.titleMedium,
			)
			Text(
				text = stringResource(R.string.end_encounter_result_supporting_text),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
			)
			HorizontalDivider()
			TrackerSheetActionButton(
				label = stringResource(R.string.session_summary_result_victory),
				onClick = { onChooseResult(EncounterResult.VICTORY) },
			)
			TrackerSheetActionButton(
				label = stringResource(R.string.session_summary_result_defeat),
				onClick = { onChooseResult(EncounterResult.DEFEAT) },
			)
			TrackerSheetActionButton(
				label = stringResource(R.string.end_encounter_result_manual_end_button),
				onClick = { onChooseResult(EncounterResult.ENDED_EARLY) },
			)
			TrackerSheetActionButton(
				label = stringResource(R.string.cancel_button),
				onClick = onDismiss,
			)
		}
	}
}

private data class TrackerScreenModel(
	val isCombatActive: Boolean,
	val encounterId: String?,
	val startFresh: Boolean,
	val encounterName: String,
	val encounterLifecycle: EncounterLifecycle,
	val initiativeMode: InitiativeMode,
	val locationTerrain: String,
	val notes: String,
	val combatants: List<CombatantState>,
	val availableCharacters: List<CharacterEntry>,
	val encounterDifficulty: EncounterDifficultyResult?,
	val importedLibraryEnemyCount: Int,
	val importedTemporaryEnemyCount: Int,
	val generationSettings: io.github.velyene.loreweaver.domain.model.EncounterGenerationSettings,
	val generationDetails: io.github.velyene.loreweaver.domain.model.EncounterGenerationDetails?,
	val round: Int,
	val turnIndex: Int,
	val statuses: List<String>,
	val canGoToPreviousTurn: Boolean,
	val turnStep: CombatTurnStep,
	val pendingAction: PendingTurnAction?,
	val selectedTargetId: String?,
	val onEncounterNameChange: (String) -> Unit,
	val onLocationTerrainChange: (String) -> Unit,
	val onNotesChange: (String) -> Unit,
	val onInitiativeModeChange: (InitiativeMode) -> Unit,
	val onStartEncounter: (String?) -> Unit,
	val onAddEntireParty: () -> Unit,
	val onClearPartyMembers: () -> Unit,
	val onTogglePartyMember: (CharacterEntry) -> Unit,
	val onAddEnemies: (String, Int, Int, Int) -> Unit,
	val onUpdateGenerationSettings: (io.github.velyene.loreweaver.domain.model.EncounterGenerationSettings) -> Unit,
	val onGenerateEncounter: () -> Unit,
	val onUpdateCombatantInitiative: (String, Int) -> Unit,
	val onMoveCombatantUp: (String) -> Unit,
	val onMoveCombatantDown: (String) -> Unit,
	val onRemoveCombatant: (String) -> Unit,
	val onSelectAction: (String) -> Unit,
	val onSelectTarget: (String) -> Unit,
	val onApplyActionResult: (ActionResolutionType, Int?) -> Unit,
	val onClearPendingTurn: () -> Unit,
	val onPreviousTurn: () -> Unit,
	val onNextTurn: () -> Unit,
	val onAdvanceRound: () -> Unit,
	val onHpChange: (String, Int) -> Unit,
	val onSetHp: (String, Int) -> Unit,
	val onMarkDefeated: (String) -> Unit,
	val onAddParticipantNote: (String, String) -> Unit,
	val onDuplicateEnemy: (String) -> Unit,
	val onAddCondition: (String, String, Int?, Boolean) -> Unit,
	val onRemoveCondition: (String, String) -> Unit,
	val onSaveAndExit: () -> Unit,
	val onExitHome: () -> Unit,
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
			state = EncounterSetupViewState(
				encounterName = model.encounterName,
				startFresh = model.startFresh,
				encounterId = model.encounterId,
				encounterLifecycle = model.encounterLifecycle,
				initiativeMode = model.initiativeMode,
				locationTerrain = model.locationTerrain,
				notes = model.notes,
				combatants = model.combatants,
				availablePartyMembers = model.availableCharacters,
				encounterDifficulty = model.encounterDifficulty,
				importedLibraryEnemyCount = model.importedLibraryEnemyCount,
				importedTemporaryEnemyCount = model.importedTemporaryEnemyCount,
				generationSettings = model.generationSettings,
				generationDetails = model.generationDetails,
				monsterTypeOptions = MonsterReferenceCatalog.CREATURE_TYPE_OPTIONS,
				monsterGroupOptions = MonsterReferenceCatalog.ALL.mapNotNull { it.group }.distinct().sorted()
			),
			callbacks = EncounterSetupCallbacks(
				onEncounterNameChange = model.onEncounterNameChange,
				onLocationTerrainChange = model.onLocationTerrainChange,
				onNotesChange = model.onNotesChange,
				onInitiativeModeChange = model.onInitiativeModeChange,
				onStart = { model.onStartEncounter(model.encounterId) },
				partyActions = EncounterSetupPartyActions(
					onAddEntireParty = model.onAddEntireParty,
					onClearPartyMembers = model.onClearPartyMembers,
					onTogglePartyMember = model.onTogglePartyMember
				),
				onAddEnemies = model.onAddEnemies,
				onUpdateGenerationSettings = model.onUpdateGenerationSettings,
				onGenerateEncounter = model.onGenerateEncounter,
				onUpdateCombatantInitiative = model.onUpdateCombatantInitiative,
				onMoveCombatantUp = model.onMoveCombatantUp,
				onMoveCombatantDown = model.onMoveCombatantDown,
				onRemoveCombatant = model.onRemoveCombatant
			)
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
			canGoToPreviousTurn = model.canGoToPreviousTurn,
			turnStep = model.turnStep,
			pendingAction = model.pendingAction,
			selectedTargetId = model.selectedTargetId
		),
		callbacks = LiveTrackerCallbacks(
			onSelectAction = model.onSelectAction,
			onSelectTarget = model.onSelectTarget,
			onApplyActionResult = model.onApplyActionResult,
			onClearPendingTurn = model.onClearPendingTurn,
				onPreviousTurn = model.onPreviousTurn,
			onNextTurn = model.onNextTurn,
			onAdvanceRound = model.onAdvanceRound,
			onHpChange = model.onHpChange,
			onSetHp = model.onSetHp,
			onMarkDefeated = model.onMarkDefeated,
			onAddParticipantNote = model.onAddParticipantNote,
			onDuplicateEnemy = model.onDuplicateEnemy,
			onRemoveCombatant = model.onRemoveCombatant,
			onAddCondition = model.onAddCondition,
			onRemoveCondition = model.onRemoveCondition,
			onEnd = model.onEndEncounter
		)
	)
}


private fun buildPartyCombatants(characters: List<CharacterEntry>): List<CombatantState> {
	return characters.map(::buildPartyCombatant)
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
