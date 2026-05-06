/*
 * FILE: CampaignDetailScreen.kt
 *
 * TABLE OF CONTENTS:
 * 1. Function: CampaignDetailScreen
 * 2. Value: uiState
 * 3. Value: editorUiState
 * 4. Value: CAMPAIGN_DELETE_FEEDBACK_DURATION_MILLIS
 * 5. Function: CampaignDetailRoute
 * 6. Value: snackbarHostState
 * 7. Value: retryActionLabel
 * 8. Value: campaignUpdatedMessage
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.Note
import io.github.velyene.loreweaver.domain.model.RemoteItem
import io.github.velyene.loreweaver.ui.theme.ObsidianBlack
import io.github.velyene.loreweaver.ui.util.asString
import io.github.velyene.loreweaver.ui.viewmodels.CampaignDetailViewModel
import io.github.velyene.loreweaver.ui.viewmodels.CampaignEditorViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignDetailScreen(
	campaignId: String,
	onBack: () -> Unit,
	onSessionClick: (String) -> Unit,
	onEncounterClick: (String) -> Unit,
	viewModel: CampaignDetailViewModel = hiltViewModel(),
	editorViewModel: CampaignEditorViewModel = hiltViewModel(),
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val editorUiState by editorViewModel.uiState.collectAsStateWithLifecycle()

	CampaignDetailRoute(
		campaignId = campaignId,
		uiState = uiState,
		editorUiState = editorUiState,
		onBack = onBack,
		onSessionClick = onSessionClick,
		onEncounterClick = onEncounterClick,
		onSelectCampaign = viewModel::selectCampaign,
		onClearError = viewModel::clearError,
		onClearMessage = editorViewModel::clearMessage,
		onClearUpdatedCampaignId = editorViewModel::clearUpdatedCampaignId,
		onClearDeletedCampaignId = editorViewModel::clearDeletedCampaignId,
						onClearUpdatedEncounterId = editorViewModel::clearUpdatedEncounterId,
						onClearDeletedEncounterId = editorViewModel::clearDeletedEncounterId,
		onAddNote = editorViewModel::addNote,
		onDeleteNote = editorViewModel::deleteNote,
		onUpdateNote = editorViewModel::updateNote,
		onAddEncounter = editorViewModel::addEncounter,
		onAddEncounterWithMonsters = editorViewModel::addEncounterWithMonsters,
		onUpdateCampaign = editorViewModel::updateCampaign,
		onDeleteCampaign = editorViewModel::deleteCampaign,
						onUpdateEncounter = editorViewModel::updateEncounter,
						onDeleteEncounter = editorViewModel::deleteEncounter,
	)
}

private const val CAMPAIGN_DELETE_FEEDBACK_DURATION_MILLIS = 1_000L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CampaignDetailRoute(
	campaignId: String,
	uiState: io.github.velyene.loreweaver.ui.viewmodels.CampaignDetailUiState,
	editorUiState: io.github.velyene.loreweaver.ui.viewmodels.CampaignEditorUiState,
	onBack: () -> Unit,
	onSessionClick: (String) -> Unit,
	onEncounterClick: (String) -> Unit,
	onSelectCampaign: (String) -> Unit,
	onClearError: () -> Unit,
	onClearMessage: () -> Unit,
	onClearUpdatedCampaignId: () -> Unit,
	onClearDeletedCampaignId: () -> Unit,
	onClearUpdatedEncounterId: () -> Unit,
	onClearDeletedEncounterId: () -> Unit,
	onAddNote: (String, String, String, String) -> Unit,
	onDeleteNote: (Note) -> Unit,
	onUpdateNote: (Note) -> Unit,
	onAddEncounter: (String, String) -> Unit,
	onAddEncounterWithMonsters: (String, String, List<RemoteItem>) -> Unit,
	onUpdateCampaign: (Campaign, String, String) -> Unit,
	onDeleteCampaign: (Campaign) -> Unit,
	onUpdateEncounter: (io.github.velyene.loreweaver.domain.model.Encounter, String) -> Unit,
	onDeleteEncounter: (io.github.velyene.loreweaver.domain.model.Encounter) -> Unit,
	deleteFeedbackDurationMillis: Long = CAMPAIGN_DELETE_FEEDBACK_DURATION_MILLIS,
) {
	val snackbarHostState = remember { SnackbarHostState() }
	val retryActionLabel = stringResource(R.string.retry_action)
	val campaignUpdatedMessage = stringResource(R.string.campaign_updated_message)
	val campaignDeletedMessage = stringResource(R.string.campaign_deleted_message)
	val encounterUpdatedMessage = stringResource(R.string.encounter_updated_message)
	val encounterDeletedMessage = stringResource(R.string.encounter_deleted_message)
	var showEditDialog by remember { mutableStateOf(false) }
	var showDeleteDialog by remember { mutableStateOf(false) }
	var showDeletedFeedback by remember { mutableStateOf(false) }
	val errorMessage = uiState.error?.asString()
	val editorMessage = editorUiState.message?.asString()

	LaunchedEffect(campaignId) {
		onSelectCampaign(campaignId)
	}

	LaunchedEffect(errorMessage) {
		if (uiState.selectedCampaign == null) {
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

	LaunchedEffect(editorUiState.updatedCampaignId) {
		editorUiState.updatedCampaignId?.let { updatedCampaignId ->
			if (updatedCampaignId == campaignId) {
				@Suppress("UNUSED_VALUE")
				showEditDialog = false
				onClearUpdatedCampaignId()
				snackbarHostState.showSnackbar(campaignUpdatedMessage)
				onSelectCampaign(campaignId)
			} else {
				onClearUpdatedCampaignId()
			}
		}
	}

	LaunchedEffect(editorUiState.deletedCampaignId) {
		editorUiState.deletedCampaignId?.let { deletedCampaignId ->
			if (deletedCampaignId == campaignId) {
				showDeletedFeedback = true
				showDeleteDialog = false
				onClearDeletedCampaignId()
				delay(deleteFeedbackDurationMillis)
				onBack()
			} else {
				onClearDeletedCampaignId()
			}
		}
	}

	LaunchedEffect(editorUiState.updatedEncounterId) {
		editorUiState.updatedEncounterId?.let {
			onClearUpdatedEncounterId()
			snackbarHostState.showSnackbar(encounterUpdatedMessage)
		}
	}

	LaunchedEffect(editorUiState.deletedEncounterId) {
		editorUiState.deletedEncounterId?.let {
			onClearDeletedEncounterId()
			snackbarHostState.showSnackbar(encounterDeletedMessage)
		}
	}

	val campaign = uiState.selectedCampaign
	var selectedTab by rememberSaveable { mutableIntStateOf(0) }

	if (showDeletedFeedback) {
		Scaffold(
			snackbarHost = { SnackbarHost(snackbarHostState) },
		) { padding ->
			CenteredEmptyState(
				message = campaignDeletedMessage,
				modifier = Modifier
					.fillMaxSize()
					.padding(padding)
					.background(MaterialTheme.colorScheme.background),
			)
		}
		return
	}

	if (campaign == null && uiState.isLoading) {
		CenteredLoadingState(
			modifier = Modifier
				.fillMaxSize()
				.background(ObsidianBlack),
		)
		return
	}

	Scaffold(
		snackbarHost = { SnackbarHost(snackbarHostState) },
		topBar = {
			TopAppBar(
				title = {
					Text(text = campaign?.title ?: stringResource(R.string.campaign_loading_title))
				},
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							imageVector = Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = stringResource(R.string.back_button),
						)
					}
				},
				actions = {
					campaign?.let { selectedCampaign ->
						IconButton(onClick = { showEditDialog = true }) {
							Icon(
								imageVector = Icons.Default.Edit,
								contentDescription = stringResource(R.string.edit_campaign, selectedCampaign.title),
							)
						}
						IconButton(onClick = { showDeleteDialog = true }) {
							Icon(
								imageVector = Icons.Default.Delete,
								contentDescription = stringResource(R.string.delete_campaign, selectedCampaign.title),
							)
						}
					}
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.background,
					titleContentColor = MaterialTheme.colorScheme.onBackground,
					navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
				),
			)
		},
	) { padding ->
		CampaignDetailContent(
			state = CampaignDetailContentState(
				campaign = campaign,
				selectedTab = selectedTab,
				isLoading = uiState.isLoading,
				onRetry = uiState.onRetry,
				notes = uiState.notes,
				sessions = uiState.sessions,
				encounters = uiState.linkedEncounters,
			),
			actions = CampaignDetailActions(
				onEncounterClick = onEncounterClick,
				onSessionClick = onSessionClick,
				onAddNote = { content, type, extra ->
					onAddNote(
						campaign?.id.orEmpty(),
						content,
						type,
						extra,
					)
				},
				onDeleteNote = onDeleteNote,
				onUpdateNote = onUpdateNote,
				onAddEncounter = { name ->
					campaign?.let { onAddEncounter(it.id, name) }
				},
				onAddEncounterWithMonsters = { name, monsters ->
					campaign?.let {
						onAddEncounterWithMonsters(it.id, name, monsters)
					}
				},
				onUpdateEncounter = onUpdateEncounter,
				onDeleteEncounter = onDeleteEncounter,
			),
			onSelectedTabChange = { selectedTab = it },
			modifier = Modifier
				.padding(padding)
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.background),
		)
	}

	campaign?.takeIf { showEditDialog }?.let { selectedCampaign ->
		CreateCampaignDialog(
			title = stringResource(R.string.edit_campaign_title),
			confirmLabel = stringResource(R.string.save_button),
			initialName = selectedCampaign.title,
			initialDescription = selectedCampaign.description,
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				showEditDialog = false
			},
			onConfirm = { name, desc ->
					onUpdateCampaign(selectedCampaign, name, desc)
			}
		)
	}

	campaign?.takeIf { showDeleteDialog }?.let { selectedCampaign ->
		ConfirmationDialog(
			title = stringResource(R.string.confirm_delete_campaign_title),
			message = stringResource(R.string.confirm_delete_campaign_message, selectedCampaign.title),
			onConfirm = { onDeleteCampaign(selectedCampaign) },
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				showDeleteDialog = false
			},
			confirmLabel = stringResource(R.string.delete_button),
		)
	}
}
