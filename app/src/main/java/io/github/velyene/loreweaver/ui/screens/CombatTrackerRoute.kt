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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.util.CharacterParty
import io.github.velyene.loreweaver.domain.util.EncounterDifficultyResult
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.INITIATIVE_ROLL_MAX
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.INITIATIVE_ROLL_MIN
import io.github.velyene.loreweaver.ui.screens.tracker.live.LiveTrackerCallbacks
import io.github.velyene.loreweaver.ui.screens.tracker.live.LiveTrackerView
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
	// The screen model snapshots the view-model state into a UI-focused contract so the
	// route can keep setup/live rendering logic simple and stable across recompositions.
	val screenModel = remember(uiState, encounterId, viewModel, onEndEncounter) {
		TrackerScreenModel(
			isCombatActive = uiState.isCombatActive,
			encounterId = encounterId,
			notes = uiState.encounterNotes,
			combatants = uiState.combatants,
			availableCharacters = uiState.availableCharacters,
			encounterDifficulty = uiState.encounterDifficulty,
			round = uiState.currentRound,
			turnIndex = uiState.currentTurnIndex,
			statuses = uiState.activeStatuses,
			persistentConditionsByCharacterId = uiState.availableCharacters.associate { character ->
				character.id to character.persistentConditions
			},
			onNotesChange = viewModel::updateNotes,
			onStartEncounter = viewModel::startEncounter,
			onAddParty = viewModel::addParty,
			onAddEnemy = viewModel::addEnemy,
			onRemoveCombatant = viewModel::removeCombatant,
			onAction = viewModel::performAction,
			onNextTurn = viewModel::nextTurn,
			onHpChange = viewModel::updateCombatantHp,
			onAddCondition = { characterId, condition, duration, persistsAcrossEncounters ->
				viewModel.addCondition(characterId, condition, duration, persistsAcrossEncounters)
			},
			onRemoveCondition = { characterId, conditionName, removePersistentCondition ->
				viewModel.removeCondition(characterId, conditionName, removePersistentCondition)
			},
			onEndEncounter = { viewModel.saveAndEndEncounter(onEndEncounter) }
		)
	}
	val topBarTitle = if (uiState.isCombatActive) {
		stringResource(R.string.combat_tracker_live_title)
	} else {
		stringResource(R.string.combat_tracker_setup_title)
	}
	val retryActionLabel = stringResource(R.string.retry_action)

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
				title = { Text(topBarTitle) },
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = stringResource(R.string.back_button)
						)
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
	val notes: String,
	val combatants: List<CombatantState>,
	val availableCharacters: List<CharacterEntry>,
	val encounterDifficulty: EncounterDifficultyResult?,
	val round: Int,
	val turnIndex: Int,
	val statuses: List<String>,
	val persistentConditionsByCharacterId: Map<String, Set<String>>,
	val onNotesChange: (String) -> Unit,
	val onStartEncounter: (String?) -> Unit,
	val onAddParty: (List<CombatantState>) -> Unit,
	val onAddEnemy: (String, Int, Int) -> Unit,
	val onRemoveCombatant: (String) -> Unit,
	val onAction: (String) -> Unit,
	val onNextTurn: () -> Unit,
	val onHpChange: (String, Int) -> Unit,
	val onAddCondition: (String, String, Int?, Boolean) -> Unit,
	val onRemoveCondition: (String, String, Boolean) -> Unit,
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
			onAddParty = { model.onAddParty(buildPartyCombatants(model.availableCharacters)) },
			onAddEnemy = model.onAddEnemy,
			onRemoveCombatant = model.onRemoveCombatant
		)
		return
	}

	LiveTrackerView(
		round = model.round,
		combatants = model.combatants,
		persistentConditionsByCharacterId = model.persistentConditionsByCharacterId,
		turnIndex = model.turnIndex,
		statuses = model.statuses,
		callbacks = LiveTrackerCallbacks(
			onAction = model.onAction,
			onNextTurn = model.onNextTurn,
			onHpChange = model.onHpChange,
			onAddCondition = model.onAddCondition,
			onRemoveCondition = model.onRemoveCondition,
			onEnd = model.onEndEncounter
		)
	)
}

private fun buildPartyCombatants(availableCharacters: List<CharacterEntry>): List<CombatantState> {
	// Party members receive a light initiative roll here so the setup flow can seed the
	// tracker immediately without a separate round of manual initiative entry.
	return availableCharacters
		.asSequence()
		.filter { it.party == CharacterParty.ADVENTURERS }
		.map { character ->
			CombatantState(
				character.id,
				character.name,
				character.initiative + Random.nextInt(INITIATIVE_ROLL_MIN, INITIATIVE_ROLL_MAX),
				character.hp,
				character.maxHp
			)
		}
		.toList()
}
