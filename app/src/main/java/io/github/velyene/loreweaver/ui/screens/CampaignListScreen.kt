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
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.ui.viewmodels.CampaignEditorViewModel
import io.github.velyene.loreweaver.ui.viewmodels.CampaignListViewModel
import io.github.velyene.loreweaver.ui.util.asString

@Composable
fun CampaignListScreen(
	onCampaignClick: (String) -> Unit,
	onBack: () -> Unit,
	viewModel: CampaignListViewModel = hiltViewModel(),
	editorViewModel: CampaignEditorViewModel = hiltViewModel(),
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val editorUiState by editorViewModel.uiState.collectAsStateWithLifecycle()

	CampaignListRoute(
		uiState = uiState,
		editorUiState = editorUiState,
		onBack = onBack,
		onCampaignClick = onCampaignClick,
		onClearError = viewModel::clearError,
		onClearMessage = editorViewModel::clearMessage,
		onClearUpdatedCampaignId = editorViewModel::clearUpdatedCampaignId,
		onClearDeletedCampaignId = editorViewModel::clearDeletedCampaignId,
		onAddCampaign = editorViewModel::addCampaign,
		onUpdateCampaign = editorViewModel::updateCampaign,
		onDeleteCampaign = editorViewModel::deleteCampaign,
	)
}

@Composable
internal fun CampaignListRoute(
	uiState: io.github.velyene.loreweaver.ui.viewmodels.CampaignListUiState,
	editorUiState: io.github.velyene.loreweaver.ui.viewmodels.CampaignEditorUiState,
	onBack: () -> Unit,
	onCampaignClick: (String) -> Unit,
	onClearError: () -> Unit,
	onClearMessage: () -> Unit,
	onClearUpdatedCampaignId: () -> Unit,
	onClearDeletedCampaignId: () -> Unit,
	onAddCampaign: (String, String) -> Unit,
	onUpdateCampaign: (Campaign, String, String) -> Unit,
	onDeleteCampaign: (Campaign) -> Unit,
) {
	var showCreateDialog by remember { mutableStateOf(false) }
	var campaignBeingEdited by remember { mutableStateOf<Campaign?>(null) }
	var campaignPendingDelete by remember { mutableStateOf<Campaign?>(null) }
	var pendingSnackbarMessage by remember { mutableStateOf<String?>(null) }
	val snackbarHostState = remember { SnackbarHostState() }
	val retryActionLabel = stringResource(R.string.retry_action)
	val campaignUpdatedMessage = stringResource(R.string.campaign_updated_message)
	val campaignDeletedMessage = stringResource(R.string.campaign_deleted_message)
	val errorMessage = uiState.error?.asString()
	val editorMessage = editorUiState.message?.asString()

	LaunchedEffect(errorMessage) {
		if (uiState.campaigns.isEmpty() && uiState.onRetry != null) {
			return@LaunchedEffect
		}
		errorMessage?.let {
			val result = snackbarHostState.showSnackbar(
				message = it,
				actionLabel = if (uiState.onRetry != null) retryActionLabel else null,
			)
			if (result == SnackbarResult.ActionPerformed) {
				uiState.onRetry?.invoke()
			}
			onClearError()
		}
	}

	LaunchedEffect(editorMessage) {
		editorMessage?.let {
			snackbarHostState.showSnackbar(it)
			onClearMessage()
		}
	}

	LaunchedEffect(pendingSnackbarMessage) {
		pendingSnackbarMessage?.let { message ->
			snackbarHostState.showSnackbar(message)
			pendingSnackbarMessage = null
		}
	}

	LaunchedEffect(editorUiState.updatedCampaignId) {
		editorUiState.updatedCampaignId?.let { updatedCampaignId ->
			if (campaignBeingEdited?.id == updatedCampaignId) {
				@Suppress("UNUSED_VALUE")
				campaignBeingEdited = null
				pendingSnackbarMessage = campaignUpdatedMessage
				onClearUpdatedCampaignId()
				return@let
			}
			onClearUpdatedCampaignId()
		}
	}

	LaunchedEffect(editorUiState.deletedCampaignId) {
		editorUiState.deletedCampaignId?.let { deletedCampaignId ->
			if (campaignPendingDelete?.id == deletedCampaignId) {
				@Suppress("UNUSED_VALUE")
				campaignPendingDelete = null
				pendingSnackbarMessage = campaignDeletedMessage
				onClearDeletedCampaignId()
				return@let
			}
			onClearDeletedCampaignId()
		}
	}

	CampaignListContent(
		uiState = uiState,
		snackbarHostState = snackbarHostState,
		onBack = onBack,
		onCampaignClick = onCampaignClick,
		onEditCampaign = { campaignBeingEdited = it },
		onDeleteCampaign = { campaignPendingDelete = it },
		onShowCreateDialog = { showCreateDialog = true },
	)

	if (showCreateDialog) {
		CreateCampaignDialog(
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				showCreateDialog = false
			},
			onConfirm = { name, desc ->
				onAddCampaign(name, desc)
				@Suppress("UNUSED_VALUE")
				showCreateDialog = false
			}
		)
	}

	campaignBeingEdited?.let { campaign ->
		CreateCampaignDialog(
			title = stringResource(R.string.edit_campaign_title),
			confirmLabel = stringResource(R.string.save_button),
			initialName = campaign.title,
			initialDescription = campaign.description,
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				campaignBeingEdited = null
			},
			onConfirm = { name, desc ->
				onUpdateCampaign(campaign, name, desc)
			}
		)
	}

	campaignPendingDelete?.let { campaign ->
		ConfirmationDialog(
			title = stringResource(R.string.confirm_delete_campaign_title),
			message = stringResource(R.string.confirm_delete_campaign_message, campaign.title),
			onConfirm = { onDeleteCampaign(campaign) },
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				campaignPendingDelete = null
			},
			confirmLabel = stringResource(R.string.delete_button),
		)
	}
}
