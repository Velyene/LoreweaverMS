/*
 * FILE: CampaignListScreen.kt
 *
 * TABLE OF CONTENTS:
 * 1. Main Screen (CampaignListScreen)
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.viewmodels.CampaignEditorViewModel
import io.github.velyene.loreweaver.ui.viewmodels.CampaignListViewModel

@Composable
fun CampaignListScreen(
	onCampaignClick: (String) -> Unit,
	onBack: () -> Unit,
	viewModel: CampaignListViewModel = hiltViewModel(),
	editorViewModel: CampaignEditorViewModel = hiltViewModel(),
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val editorUiState by editorViewModel.uiState.collectAsStateWithLifecycle()
	var showCreateDialog by remember { mutableStateOf(false) }
	val snackbarHostState = remember { SnackbarHostState() }
	val retryActionLabel = stringResource(R.string.retry_action)

	LaunchedEffect(uiState.error) {
		uiState.error?.let {
			val result = snackbarHostState.showSnackbar(
				message = it,
				actionLabel = if (uiState.onRetry != null) retryActionLabel else null,
			)
			if (result == SnackbarResult.ActionPerformed) {
				uiState.onRetry?.invoke()
			}
			viewModel.clearError()
		}
	}

	LaunchedEffect(editorUiState.message) {
		editorUiState.message?.let {
			snackbarHostState.showSnackbar(it)
			editorViewModel.clearMessage()
		}
	}

	CampaignListContent(
		uiState = uiState,
		snackbarHostState = snackbarHostState,
		onBack = onBack,
		onCampaignClick = onCampaignClick,
		onShowCreateDialog = { showCreateDialog = true },
	)

	if (showCreateDialog) {
		CreateCampaignDialog(
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				showCreateDialog = false
			},
			onCreate = { name, desc ->
				editorViewModel.addCampaign(name, desc)
				@Suppress("UNUSED_VALUE")
				showCreateDialog = false
			}
		)
	}
}
