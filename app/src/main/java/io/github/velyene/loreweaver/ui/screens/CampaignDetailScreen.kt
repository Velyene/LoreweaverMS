package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.theme.ObsidianBlack
import io.github.velyene.loreweaver.ui.viewmodels.CampaignDetailViewModel
import io.github.velyene.loreweaver.ui.viewmodels.CampaignEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignDetailScreen(
	campaignId: String,
	onBack: () -> Unit,
	onEncounterClick: (String) -> Unit,
	viewModel: CampaignDetailViewModel = hiltViewModel(),
	editorViewModel: CampaignEditorViewModel = hiltViewModel(),
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val editorUiState by editorViewModel.uiState.collectAsStateWithLifecycle()
	val snackbarHostState = remember { SnackbarHostState() }

	LaunchedEffect(campaignId) {
		viewModel.selectCampaign(campaignId)
	}

	LaunchedEffect(uiState.error) {
		uiState.error?.let {
			snackbarHostState.showSnackbar(it)
			viewModel.clearError()
		}
	}

	LaunchedEffect(editorUiState.message) {
		editorUiState.message?.let {
			snackbarHostState.showSnackbar(it)
			editorViewModel.clearMessage()
		}
	}

	val campaign = uiState.selectedCampaign
	var selectedTab by rememberSaveable { mutableIntStateOf(0) }

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
				notes = uiState.notes,
				sessions = uiState.sessions,
				encounters = uiState.linkedEncounters,
			),
			actions = CampaignDetailActions(
				onEncounterClick = onEncounterClick,
				onAddNote = { content, type, extra ->
					editorViewModel.addNote(
						campaign?.id.orEmpty(),
						content,
						type,
						extra,
					)
				},
				onDeleteNote = editorViewModel::deleteNote,
				onUpdateNote = editorViewModel::updateNote,
				onAddEncounter = { name ->
					campaign?.let { editorViewModel.addEncounter(it.id, name) }
				},
				onAddEncounterWithMonsters = { name, monsters ->
					campaign?.let {
						editorViewModel.addEncounterWithMonsters(it.id, name, monsters)
					}
				},
			),
			onSelectedTabChange = { selectedTab = it },
			modifier = Modifier
				.padding(padding)
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.background),
		)
	}
}
