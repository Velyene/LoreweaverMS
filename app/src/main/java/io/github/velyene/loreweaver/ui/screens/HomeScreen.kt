package io.github.velyene.loreweaver.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.util.UiText
import io.github.velyene.loreweaver.ui.util.asString
import io.github.velyene.loreweaver.ui.viewmodels.CampaignListViewModel

@Composable
fun HomeScreen(
	onNewEncounter: () -> Unit,
	onCampaigns: () -> Unit,
	onResumeEncounter: () -> Unit,
	onLatestSessionClick: (String) -> Unit,
	onCampaignClick: (String) -> Unit,
	onRulesReference: () -> Unit,
) {
	val viewModel: CampaignListViewModel = hiltViewModel()
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val snackbarHostState = remember { SnackbarHostState() }
	val retryActionLabel = stringResource(R.string.retry_action)
	val lifecycleOwner = LocalLifecycleOwner.current

	DisposableEffect(lifecycleOwner, viewModel) {
		val observer = LifecycleEventObserver { _, event ->
			if (event == Lifecycle.Event.ON_RESUME) {
				viewModel.refreshActiveEncounter()
			}
		}
		lifecycleOwner.lifecycle.addObserver(observer)
		onDispose {
			lifecycleOwner.lifecycle.removeObserver(observer)
		}
	}

	ErrorSnackbarHandler(
		error = uiState.error,
		onRetry = uiState.onRetry,
		snackbarHostState = snackbarHostState,
		onClear = viewModel::clearError,
		retryActionLabel = retryActionLabel,
	)

	Scaffold(
		snackbarHost = { SnackbarHost(snackbarHostState) },
		containerColor = MaterialTheme.colorScheme.background,
	) { padding ->
		HomeScreenContent(
			uiState = uiState,
			padding = padding,
			onNewEncounter = onNewEncounter,
			onResumeEncounter = onResumeEncounter,
			onLatestSessionClick = onLatestSessionClick,
			onCampaigns = onCampaigns,
			onCampaignClick = onCampaignClick,
			onRulesReference = onRulesReference,
		)
	}
}

@Composable
internal fun ErrorSnackbarHandler(
	error: UiText?,
	onRetry: (() -> Unit)?,
	snackbarHostState: SnackbarHostState,
	onClear: (String) -> Unit,
	retryActionLabel: String,
) {
	val errorMessage = error?.asString()

	LaunchedEffect(errorMessage) {
		errorMessage ?: return@LaunchedEffect

		val result = snackbarHostState.showSnackbar(
			message = errorMessage,
			actionLabel = if (onRetry != null) retryActionLabel else null,
			duration = SnackbarDuration.Long,
		)

		if (result == SnackbarResult.ActionPerformed) {
			onClear(error)
			onRetry?.invoke()
		} else {
			onClear(error)
		}
	}
}
