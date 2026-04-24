package com.example.loreweaver.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loreweaver.R
import com.example.loreweaver.ui.viewmodels.CampaignListViewModel

@Composable
fun HomeScreen(
	onNewEncounter: () -> Unit,
	onCampaigns: () -> Unit,
	onResumeEncounter: () -> Unit,
	onCampaignClick: (String) -> Unit,
	onRulesReference: () -> Unit,
) {
	val viewModel: CampaignListViewModel = hiltViewModel()
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val snackbarHostState = remember { SnackbarHostState() }
	val retryActionLabel = stringResource(R.string.retry_action)

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
			onCampaigns = onCampaigns,
			onCampaignClick = onCampaignClick,
			onRulesReference = onRulesReference,
		)
	}
}

@Composable
private fun ErrorSnackbarHandler(
	error: String?,
	onRetry: (() -> Unit)?,
	snackbarHostState: SnackbarHostState,
	onClear: () -> Unit,
	retryActionLabel: String,
) {
	LaunchedEffect(error) {
		error ?: return@LaunchedEffect

		val result = snackbarHostState.showSnackbar(
			message = error,
			actionLabel = if (onRetry != null) retryActionLabel else null,
			duration = SnackbarDuration.Long,
		)

		if (result == SnackbarResult.ActionPerformed) {
			onRetry?.invoke()
		}

		onClear()
	}
}
