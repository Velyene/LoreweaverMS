/*
 * FILE: SavedEncounterPickerScreen.kt
 *
 * TABLE OF CONTENTS:
 * 1. Saved Encounter Picker Route
 * 2. Picker Content
 * 3. Encounter Cards
 * 4. Filter and Date Helpers
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.EncounterStatus
import io.github.velyene.loreweaver.ui.viewmodels.SavedEncounterPickerItem
import io.github.velyene.loreweaver.ui.viewmodels.SavedEncounterPickerUiState
import io.github.velyene.loreweaver.ui.viewmodels.SavedEncounterPickerViewModel
import io.github.velyene.loreweaver.ui.viewmodels.SavedEncounterStatusFilter
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SavedEncounterPickerScreen(
	onBack: () -> Unit,
	onOpenEncounter: (String) -> Unit,
	viewModel: SavedEncounterPickerViewModel = hiltViewModel(),
) {
	val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
	SavedEncounterPickerContent(
		uiState = uiState,
		onBack = onBack,
		onSearchQueryChange = viewModel::updateSearchQuery,
		onStatusFilterChange = viewModel::updateStatusFilter,
		onOpenEncounter = onOpenEncounter,
	)
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun SavedEncounterPickerContent(
	uiState: SavedEncounterPickerUiState,
	onBack: () -> Unit,
	onSearchQueryChange: (String) -> Unit,
	onStatusFilterChange: (SavedEncounterStatusFilter) -> Unit,
	onOpenEncounter: (String) -> Unit,
) {
	val listState = rememberLazyListState()
	val dateFormat = androidx.compose.runtime.remember {
		SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
	}

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(stringResource(R.string.dm_saved_encounters_title)) },
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							imageVector = Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = stringResource(R.string.back_button),
						)
					}
				},
			)
		},
	) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
				.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Text(
				text = stringResource(R.string.dm_saved_encounters_subtitle),
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
			OutlinedTextField(
				value = uiState.searchQuery,
				onValueChange = onSearchQueryChange,
				modifier = Modifier.fillMaxWidth(),
				label = { Text(stringResource(R.string.search_sessions_label)) },
				placeholder = { Text(stringResource(R.string.dm_saved_encounters_search_placeholder)) },
				singleLine = true,
			)
			FlowRow(
				horizontalArrangement = Arrangement.spacedBy(8.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp),
			) {
				SavedEncounterStatusFilter.entries.forEach { statusFilter ->
					FilterChip(
						selected = uiState.selectedStatusFilter == statusFilter,
						onClick = { onStatusFilterChange(statusFilter) },
						label = {
							Text(
								text = stringResource(
									when (statusFilter) {
										SavedEncounterStatusFilter.ALL -> R.string.dm_saved_encounters_filter_all
										SavedEncounterStatusFilter.PENDING -> R.string.dm_saved_encounters_filter_pending
										SavedEncounterStatusFilter.ACTIVE -> R.string.dm_saved_encounters_filter_active
									}
								)
							)
						},
					)
				}
			}
			when {
				uiState.isLoading -> CenteredLoadingState(modifier = Modifier.fillMaxSize())
				uiState.encounters.isEmpty() -> CenteredEmptyState(
					message = stringResource(R.string.dm_saved_encounters_empty_message),
					modifier = Modifier.fillMaxSize(),
				)
				else -> LazyColumn(
					state = listState,
					modifier = Modifier
						.fillMaxSize()
						.visibleVerticalScrollbar(listState),
					verticalArrangement = Arrangement.spacedBy(8.dp),
				) {
					items(uiState.encounters, key = SavedEncounterPickerItem::id) { encounter ->
						SavedEncounterPickerCard(
							encounter = encounter,
							dateFormat = dateFormat,
							onOpenEncounter = onOpenEncounter,
						)
					}
				}
			}
		}
	}
}

@Composable
private fun SavedEncounterPickerCard(
	encounter: SavedEncounterPickerItem,
	dateFormat: SimpleDateFormat,
	onOpenEncounter: (String) -> Unit,
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(
				role = Role.Button,
				onClickLabel = stringResource(R.string.open_encounter_action, encounter.name),
			) { onOpenEncounter(encounter.id) },
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(10.dp),
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				modifier = Modifier.fillMaxWidth(),
			) {
				Icon(
					imageVector = Icons.Default.PlayArrow,
					contentDescription = null,
					tint = MaterialTheme.colorScheme.primary,
				)
				Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
					Text(
						text = encounter.name,
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.SemiBold,
					)
					encounter.campaignTitle?.takeIf { it.isNotBlank() }?.let { title ->
						Text(
							text = title,
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.secondary,
						)
					}
					Text(
						text = stringResource(
							R.string.dm_saved_encounter_summary,
							encounter.round,
							encounter.combatantCount,
						),
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
				}
				Badge(containerColor = MaterialTheme.colorScheme.surface) {
					Text(
						text = stringResource(
							if (encounter.status == EncounterStatus.ACTIVE) {
								R.string.encounter_status_active
							} else {
								R.string.encounter_status_pending
							},
						),
						modifier = Modifier.padding(horizontal = 4.dp),
					)
				}
			}
			encounter.lastSavedAt?.let { lastSavedAt ->
				Text(
					text = stringResource(
						R.string.dm_saved_encounter_last_saved,
						dateFormat.format(java.util.Date(lastSavedAt)),
					),
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			}
			if (encounter.notesPreview.isNotBlank()) {
				Text(
					text = stringResource(
						R.string.dm_saved_encounter_notes_preview,
						encounter.notesPreview.replace('\n', ' ').take(120),
					),
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurface,
				)
			}
			Button(onClick = { onOpenEncounter(encounter.id) }, modifier = Modifier.fillMaxWidth()) {
				Text(stringResource(R.string.dm_saved_encounter_review_button))
			}
		}
	}
}
