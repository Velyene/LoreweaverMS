/*
 * FILE: AdventureLogScreen.kt
 *
 * TABLE OF CONTENTS:
 * 1. Adventure log screen shell and snackbar handling
 * 2. Loading, empty, and populated log states
 * 3. Clear-log confirmation flow
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.theme.AntiqueGold
import io.github.velyene.loreweaver.ui.theme.MutedText
import io.github.velyene.loreweaver.ui.viewmodels.AdventureLogViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdventureLogScreen(
	onBack: () -> Unit,
	viewModel: AdventureLogViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
	var showClearConfirmation by remember { mutableStateOf(false) }
	val listState = rememberLazyListState()
	val snackbarHostState = remember { SnackbarHostState() }
	val retryActionLabel = stringResource(R.string.retry_action)

	LaunchedEffect(uiState.error) {
		val error = uiState.error ?: return@LaunchedEffect
		val result = snackbarHostState.showSnackbar(
			message = error,
			actionLabel = if (uiState.onRetry != null) retryActionLabel else null,
			duration = SnackbarDuration.Long,
		)
		if (result == SnackbarResult.ActionPerformed) {
			uiState.onRetry?.invoke()
		}
		viewModel.clearError()
	}

	Scaffold(
		snackbarHost = { SnackbarHost(snackbarHostState) },
		topBar = {
			TopAppBar(
				title = { Text(stringResource(R.string.adventure_log_title)) },
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = stringResource(R.string.back_button)
						)
					}
				},
				actions = {
					IconButton(onClick = { showClearConfirmation = true }) {
						Icon(
							Icons.Default.Delete,
							contentDescription = stringResource(R.string.clear_log_desc)
						)
					}
				}
			)
		}
	) { padding ->
		if (uiState.isLoading && uiState.logs.isEmpty()) {
			CenteredLoadingState(
				modifier = Modifier
					.fillMaxSize()
					.padding(padding)
			)
		} else if (uiState.logs.isEmpty()) {
			CenteredEmptyState(
				message = stringResource(R.string.adventure_log_empty_message),
				modifier = Modifier
					.fillMaxSize()
					.padding(padding)
			)
		} else {
			LazyColumn(
				state = listState,
				modifier = Modifier
					.fillMaxSize()
					.padding(padding)
					.visibleVerticalScrollbar(listState)
			) {
				items(uiState.logs, key = { it.id }) { entry ->
					ListItem(
						overlineContent = {
							Text(
								dateFormat.format(Date(entry.timestamp)),
								color = AntiqueGold
							)
						},
						headlineContent = { Text(entry.message, fontWeight = FontWeight.Medium) },
						supportingContent = { Text(entry.type, color = MutedText) }
					)
					HorizontalDivider()
				}
			}
		}
	}

	if (showClearConfirmation) {
		ConfirmationDialog(
			title = stringResource(R.string.clear_log_confirm_title),
			message = stringResource(R.string.clear_log_confirm_message),
			onConfirm = {
				viewModel.clearLogs()
				@Suppress("UNUSED_VALUE")
				showClearConfirmation = false
			},
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				showClearConfirmation = false
			}
		)
	}
}

