/*
 * FILE: LiveTrackerOverviewSections.kt
 *
 * TABLE OF CONTENTS:
 * 1. Content list and notes card
 * 2. Encounter notes card
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R

@Composable
internal fun LiveTrackerContentList(
	encounterName: String,
	encounterNotes: String,
	statuses: List<String>,
	turnStep: io.github.velyene.loreweaver.ui.viewmodels.CombatTurnStep,
	pendingAction: io.github.velyene.loreweaver.ui.viewmodels.PendingTurnAction?,
	selectedTargetId: String?,
	trackerState: LiveTrackerUiState,
	callbacks: LiveTrackerCallbacks,
	modifier: Modifier = Modifier,
	contentListState: androidx.compose.foundation.lazy.LazyListState
) {
	androidx.compose.foundation.lazy.LazyColumn(
		state = contentListState,
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		item {
			CurrentParticipantPanel(
				state = CurrentParticipantPanelState(
					encounterName = encounterName,
					participant = trackerState.currentParticipant,
					pendingAction = pendingAction,
					selectedTarget = trackerState.selectedTarget,
					turnStep = turnStep,
					targetableParticipants = trackerState.targetableParticipants
				),
				callbacks = callbacks
			)
		}

		if (encounterNotes.isNotBlank()) {
			item {
				EncounterNotesCard(encounterNotes = encounterNotes)
			}
		}

		item {
			SecondaryPartyPanel(
				partyMembers = trackerState.secondaryPartyMembers,
				selectedTargetId = selectedTargetId,
				selectableTargetIds = trackerState.selectableTargetIds,
				onSelectTarget = callbacks.onSelectTarget
			)
		}

		item {
			EnemyPanel(
				enemies = trackerState.enemies,
				selectedTargetId = selectedTargetId,
				selectableTargetIds = trackerState.selectableTargetIds,
				onSelectTarget = callbacks.onSelectTarget
			)
		}

		item {
			CombatLogSection(statuses = statuses)
		}
	}
}

@Composable
private fun EncounterNotesCard(encounterNotes: String) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			Text(
				text = androidx.compose.ui.res.stringResource(R.string.encounter_notes_title),
				style = MaterialTheme.typography.labelSmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.semantics { heading() }
			)
			Spacer(modifier = Modifier.height(6.dp))
			Text(
				text = encounterNotes,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
	}
}


