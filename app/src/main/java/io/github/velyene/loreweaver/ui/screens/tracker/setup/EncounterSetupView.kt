/*
 * FILE: EncounterSetupView.kt
 *
 * TABLE OF CONTENTS:
 * 1. Encounter setup view entry point
 * 2. Derived setup-state and orchestration
 * 3. Dialog and removal handling
 */

package io.github.velyene.loreweaver.ui.screens.tracker.setup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.util.CharacterParty
import io.github.velyene.loreweaver.domain.util.EncounterDifficultyResult
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.SETUP_BUTTON_HEIGHT_DP
import io.github.velyene.loreweaver.ui.screens.ConfirmationDialog
import io.github.velyene.loreweaver.ui.screens.tracker.components.TrackerModeBadge
import io.github.velyene.loreweaver.ui.screens.visibleVerticalScrollbar

@Composable
internal fun EncounterSetupView(
	notes: String,
	combatants: List<CombatantState>,
	availablePartyMembers: List<CharacterEntry>,
	encounterDifficulty: EncounterDifficultyResult?,
	onNotesChange: (String) -> Unit,
	onStart: () -> Unit,
	onTogglePartyMember: (CharacterEntry) -> Unit,
	onAddEnemy: (name: String, hp: Int, initiative: Int) -> Unit,
	onRemoveCombatant: (String) -> Unit
) {
	var showAddEnemyDialog by remember { mutableStateOf(false) }
	var combatantPendingRemoval by remember { mutableStateOf<CombatantState?>(null) }
	val scrollState = rememberScrollState()
	val savedPartyMembers = remember(availablePartyMembers) {
		availablePartyMembers
			.filter { it.party == CharacterParty.ADVENTURERS }
			.sortedBy { it.name.lowercase() }
	}
	val savedPartyMemberIds = remember(savedPartyMembers) {
		savedPartyMembers.map(CharacterEntry::id).toSet()
	}
	val selectedPartyIds = remember(combatants, savedPartyMemberIds) {
		combatants.map(CombatantState::characterId).filter(savedPartyMemberIds::contains).toSet()
	}
	val enemies = remember(combatants, savedPartyMemberIds) {
		combatants.filterNot { savedPartyMemberIds.contains(it.characterId) }
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(scrollState)
			.visibleVerticalScrollbar(scrollState)
			.padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		TrackerModeBadge(
			label = stringResource(R.string.combat_setup_badge_label),
			containerColor = MaterialTheme.colorScheme.tertiary,
			contentColor = MaterialTheme.colorScheme.onTertiary
		)
		Spacer(modifier = Modifier.height(16.dp))

		Text(
			text = stringResource(R.string.encounter_setup_shared_manager_summary),
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			modifier = Modifier.fillMaxWidth()
		)

		Spacer(modifier = Modifier.height(12.dp))

		PartyMembersSection(
			savedPartyMembers = savedPartyMembers,
			selectedPartyIds = selectedPartyIds,
			onTogglePartyMember = onTogglePartyMember
		)

		Spacer(modifier = Modifier.height(12.dp))

		EnemiesSection(
			enemies = enemies,
			onAddEnemy = { showAddEnemyDialog = true },
			onRemoveEnemy = { combatantPendingRemoval = it }
		)

		Spacer(modifier = Modifier.height(12.dp))

		if ((encounterDifficulty != null) && (encounterDifficulty.partySize > 0)) {
			// Difficulty is only shown once a valid party is available so the card never
			// presents partial calculations during early setup.
			EncounterDifficultyCard(encounterDifficulty = encounterDifficulty)
			Spacer(modifier = Modifier.height(12.dp))
		}

		Spacer(modifier = Modifier.height(12.dp))

		EncounterNotesSection(notes = notes, onNotesChange = onNotesChange)

		Spacer(modifier = Modifier.height(16.dp))

		Button(
			onClick = onStart,
			enabled = selectedPartyIds.isNotEmpty(),
			modifier = Modifier
				.fillMaxWidth()
				.height(SETUP_BUTTON_HEIGHT_DP.dp),
			colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
		) {
			Text(
				stringResource(R.string.start_encounter_button),
				color = MaterialTheme.colorScheme.onPrimary,
				fontWeight = FontWeight.Bold
			)
		}
	}

	if (showAddEnemyDialog) {
		// Add-enemy dialog state stays local to setup so the route does not need to manage
		// transient form values that only exist while encounter preparation is in progress.
		AddEnemyDialog(
			onConfirm = { name, hp, initiative ->
				onAddEnemy(name, hp, initiative)
				@Suppress("UNUSED_VALUE")
				showAddEnemyDialog = false
			},
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				showAddEnemyDialog = false
			}
		)
	}

	combatantPendingRemoval?.let { combatant ->
		ConfirmationDialog(
			title = stringResource(R.string.remove_combatant_confirm_title),
			message = stringResource(R.string.remove_combatant_confirm_message, combatant.name),
			confirmLabel = stringResource(R.string.remove_button),
			onConfirm = {
				onRemoveCombatant(combatant.characterId)
				@Suppress("UNUSED_VALUE")
				combatantPendingRemoval = null
			},
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				combatantPendingRemoval = null
			}
		)
	}
}



