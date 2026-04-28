package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.viewmodels.CampaignListUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CampaignListContent(
	uiState: CampaignListUiState,
	snackbarHostState: SnackbarHostState,
	onBack: () -> Unit,
	onCampaignClick: (String) -> Unit,
	onShowCreateDialog: () -> Unit
) {
	val listState = rememberLazyListState()

	Scaffold(
		snackbarHost = { SnackbarHost(snackbarHostState) },
		topBar = {
			TopAppBar(
				title = { Text(text = stringResource(R.string.campaigns_title)) },
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							imageVector = Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = stringResource(R.string.back_button)
						)
					}
				}
			)
		},
		floatingActionButton = {
			ExtendedFloatingActionButton(
				onClick = onShowCreateDialog,
				icon = {
					Icon(
						imageVector = Icons.Default.Add,
						contentDescription = stringResource(R.string.create_campaign_desc)
					)
				},
				text = { Text(text = stringResource(R.string.new_campaign_button)) }
			)
		}
	) { padding ->
		when {
			uiState.isLoading && uiState.campaigns.isEmpty() -> {
				CenteredLoadingState(modifier = Modifier.padding(padding))
			}

			uiState.campaigns.isEmpty() -> {
				CenteredEmptyState(
					message = stringResource(R.string.campaigns_empty_message),
					modifier = Modifier.padding(padding),
				)
			}

			else -> {
				LazyColumn(
					state = listState,
					modifier = Modifier
						.fillMaxSize()
						.padding(padding)
						.visibleVerticalScrollbar(listState)
				) {
					items(uiState.campaigns, key = { it.id }) { campaign ->
						CampaignListItem(
							campaign = campaign,
							onCampaignClick = onCampaignClick,
						)
					}
				}
			}
		}
	}
}
