/*
 * FILE: CombatTrackerRoute.kt
 *
 * TABLE OF CONTENTS:
 * 1. Combat Tracker Route (CombatTrackerScreen)
 * 2. Route loading and snackbar effects
 * 3. Scaffold shell and routed content host
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import io.github.velyene.loreweaver.ui.viewmodels.CombatViewModel

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
	val screenModel = rememberTrackerScreenModel(uiState, encounterId, viewModel, onEndEncounter)
	val retryActionLabel = stringResource(R.string.retry_action)

	LaunchedEffect(encounterId) {
		// Reload when navigation targets a different encounter, but avoid restarting the load on
		// ordinary recompositions of the same route instance.
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
			// Clear the consumed error after the snackbar finishes so restored compositions do not keep
			// replaying the same message.
			viewModel.clearError()
		}
	}

	Scaffold(
		snackbarHost = { SnackbarHost(snackbarHostState) },
		topBar = {
			CombatTrackerTopBar(
				uiState = TrackerTopBarState(
					isCombatActive = uiState.isCombatActive,
					encounterName = uiState.currentEncounterName,
					encounterLifecycle = uiState.encounterLifecycle,
					currentRound = uiState.currentRound
				),
				showEncounterMenu = showEncounterMenu,
				onBack = onBack,
				onSaveAndExit = { viewModel.saveAndPauseEncounter(onEndEncounter) },
				onToggleEncounterMenu = { showEncounterMenu = it }
			)
		}
	) { padding ->
		Box(
			modifier = Modifier
				.padding(padding)
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.background)
		) {
			TrackerContent(model = screenModel)
		}
	}
}
