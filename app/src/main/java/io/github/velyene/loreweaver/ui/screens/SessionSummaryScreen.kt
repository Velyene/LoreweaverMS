/*
 * FILE: SessionSummaryScreen.kt
 *
 * TABLE OF CONTENTS:
 * 1. Session summary screen entry point
 * 2. Screen state rendering
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.viewmodels.SessionSummaryViewModel

@Composable
fun SessionSummaryScreen(
	onDone: () -> Unit,
	onOpenAdventureLog: () -> Unit = {},
	onContinueCampaign: (String) -> Unit = {},
	onStartAnotherEncounter: () -> Unit = {},
	onOpenSessionHistory: () -> Unit = {},
	viewModel: SessionSummaryViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val scrollState = rememberScrollState()

	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.background)
			.verticalScroll(scrollState)
			.visibleVerticalScrollbar(scrollState)
	) {
		val summary = uiState.summary
		when {
			uiState.isLoading -> {
				CenteredLoadingState(modifier = Modifier.fillMaxSize())
			}

			summary != null -> {
				SessionSummaryContent(
					summary = summary,
					onDone = onDone,
					onOpenAdventureLog = onOpenAdventureLog,
					onContinueCampaign = onContinueCampaign,
					onStartAnotherEncounter = onStartAnotherEncounter,
					onOpenSessionHistory = onOpenSessionHistory
				)
			}

			else -> {
				CenteredEmptyState(
					message = uiState.error ?: stringResource(R.string.session_summary_empty_message),
					actionLabel = uiState.onRetry?.let { stringResource(R.string.retry_action) },
					onAction = uiState.onRetry,
					modifier = Modifier.fillMaxSize()
				)
			}
		}
	}
}

