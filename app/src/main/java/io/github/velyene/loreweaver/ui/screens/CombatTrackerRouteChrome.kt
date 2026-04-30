/*
 * FILE: CombatTrackerRouteChrome.kt
 *
 * TABLE OF CONTENTS:
 * 1. Combat tracker top bar
 * 2. Encounter overflow menu support
 * 3. Lifecycle label formatting
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.viewmodels.EncounterLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CombatTrackerTopBar(
	uiState: TrackerTopBarState,
	showEncounterMenu: Boolean,
	onBack: () -> Unit,
	onSaveAndExit: () -> Unit,
	onEndEncounter: () -> Unit,
	onToggleEncounterMenu: (Boolean) -> Unit
) {
	val defaultLiveTitle = stringResource(R.string.combat_tracker_live_title)
	val setupTitle = stringResource(R.string.combat_tracker_setup_title)

	TopAppBar(
		title = {
			if (uiState.isCombatActive) {
				Column {
					Text(uiState.encounterName.ifBlank { defaultLiveTitle })
					Text(
						text = stringResource(
							R.string.encounter_top_bar_active_subtitle,
							uiState.currentRound,
							formatEncounterLifecycleLabel(uiState.encounterLifecycle)
						),
						style = MaterialTheme.typography.labelSmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
			} else {
				Column {
					Text(setupTitle)
					Text(
						text = formatEncounterLifecycleLabel(uiState.encounterLifecycle),
						style = MaterialTheme.typography.labelSmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
			}
		},
		navigationIcon = {
			IconButton(onClick = onBack) {
				Icon(
					Icons.AutoMirrored.Filled.ArrowBack,
					contentDescription = stringResource(R.string.back_button)
				)
			}
		},
		actions = {
			if (uiState.isCombatActive) {
				IconButton(onClick = onSaveAndExit) {
					Icon(
						Icons.Default.Save,
						contentDescription = stringResource(R.string.encounter_save_exit_button)
					)
				}
				CombatTrackerTopBarMenu(
					showEncounterMenu = showEncounterMenu,
					onSaveAndExit = onSaveAndExit,
					onEndEncounter = onEndEncounter,
					onToggleEncounterMenu = onToggleEncounterMenu
				)
			}
		}
	)
}

@Composable
private fun CombatTrackerTopBarMenu(
	showEncounterMenu: Boolean,
	onSaveAndExit: () -> Unit,
	onEndEncounter: () -> Unit,
	onToggleEncounterMenu: (Boolean) -> Unit
) {
	androidx.compose.foundation.layout.Box {
		IconButton(onClick = { onToggleEncounterMenu(true) }) {
			Icon(
				Icons.Default.MoreVert,
				contentDescription = stringResource(R.string.encounter_options_button)
			)
		}
		DropdownMenu(
			expanded = showEncounterMenu,
			onDismissRequest = { onToggleEncounterMenu(false) }
		) {
			DropdownMenuItem(
				text = { Text(stringResource(R.string.encounter_save_exit_button)) },
				onClick = {
					onToggleEncounterMenu(false)
					onSaveAndExit()
				}
			)
			DropdownMenuItem(
				text = { Text(stringResource(R.string.end_encounter_button)) },
				onClick = {
					onToggleEncounterMenu(false)
					onEndEncounter()
				}
			)
		}
	}
}

@Composable
private fun formatEncounterLifecycleLabel(lifecycle: EncounterLifecycle): String {
	return when (lifecycle) {
		EncounterLifecycle.DRAFT -> stringResource(R.string.encounter_lifecycle_draft)
		EncounterLifecycle.ACTIVE -> stringResource(R.string.encounter_lifecycle_active)
		EncounterLifecycle.PAUSED -> stringResource(R.string.encounter_lifecycle_paused)
		EncounterLifecycle.COMPLETED -> stringResource(R.string.encounter_lifecycle_completed)
		EncounterLifecycle.ARCHIVED -> stringResource(R.string.encounter_lifecycle_archived)
	}
}

internal data class TrackerTopBarState(
	val isCombatActive: Boolean,
	val encounterName: String,
	val encounterLifecycle: EncounterLifecycle,
	val currentRound: Int
)

