/*
 * FILE: CampaignListScreen.kt
 * 
 * TABLE OF CONTENTS:
 * 1. Main Screen (CampaignListScreen)
 */

package com.example.encountertimer.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.encountertimer.R
import com.example.encountertimer.ui.theme.ArcaneTeal
import com.example.encountertimer.ui.viewmodels.CampaignViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignListScreen(
	onCampaignClick: (String) -> Unit,
	onBack: () -> Unit,
	viewModel: CampaignViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	var showCreateDialog by remember { mutableStateOf(false) }

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(stringResource(R.string.campaigns_title)) },
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = stringResource(R.string.back_button)
						)
					}
				}
			)
		},
		floatingActionButton = {
			ExtendedFloatingActionButton(
				onClick = { showCreateDialog = true },
				icon = {
					Icon(
						Icons.Default.Add,
						contentDescription = stringResource(R.string.create_campaign_desc)
					)
				},
				text = { Text(stringResource(R.string.new_campaign_button)) }
			)
		}
	) { padding ->
		if (uiState.isLoading && uiState.campaigns.isEmpty()) {
			CenteredLoadingState(modifier = Modifier.padding(padding))
		} else if (uiState.campaigns.isEmpty()) {
			CenteredEmptyState(
				message = stringResource(R.string.campaigns_empty_message),
				modifier = Modifier.padding(padding)
			)
		} else {
			LazyColumn(
				modifier = Modifier
					.fillMaxSize()
					.padding(padding)
			) {
				items(uiState.campaigns, key = { it.id }) { campaign ->
					ListItem(
						headlineContent = { Text(campaign.title, fontWeight = FontWeight.Bold) },
						supportingContent = { Text(campaign.description) },
						leadingContent = {
							Icon(
								Icons.Default.Map,
								contentDescription = stringResource(R.string.campaign_map_desc),
								tint = ArcaneTeal
							)
						},
						trailingContent = {
							Icon(
								Icons.Default.ChevronRight,
								contentDescription = stringResource(R.string.view_campaign_details_desc)
							)
						},
						modifier = Modifier.clickable { onCampaignClick(campaign.id) }
					)
					HorizontalDivider()
				}
			}
		}
	}

	if (showCreateDialog) {
		CreateCampaignDialog(
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				showCreateDialog = false
			},
			onCreate = { name, desc ->
				viewModel.addCampaign(name, desc)
				@Suppress("UNUSED_VALUE")
				showCreateDialog = false
			}
		)
	}
}

@Composable
private fun CreateCampaignDialog(
	onDismiss: () -> Unit,
	onCreate: (name: String, desc: String) -> Unit
) {
	var name by remember { mutableStateOf("") }
	var desc by remember { mutableStateOf("") }
	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text(stringResource(R.string.create_campaign_title)) },
		text = {
			Column {
				OutlinedTextField(
					value = name,
					onValueChange = { name = it },
					label = { Text(stringResource(R.string.campaign_name_label)) })
				Spacer(modifier = Modifier.height(8.dp))
				OutlinedTextField(
					value = desc,
					onValueChange = { desc = it },
					label = { Text(stringResource(R.string.description_label)) })
			}
		},
		confirmButton = {
			Button(
				onClick = { if (name.isNotEmpty()) onCreate(name, desc) },
				enabled = name.isNotEmpty()
			) { Text(stringResource(R.string.create_button)) }
		},
		dismissButton = {
			TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel_button)) }
		}
	)
}
