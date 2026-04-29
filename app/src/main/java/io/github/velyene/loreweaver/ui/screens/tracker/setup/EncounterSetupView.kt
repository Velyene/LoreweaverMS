/*
 * FILE: EncounterSetupView.kt
 *
 * TABLE OF CONTENTS:
 * 1. Encounter Setup Screen (EncounterSetupView)
 * 2. Difficulty Summary (EncounterDifficultyCard, difficulty color helpers)
 * 3. Notes Input (EncounterNotesSection)
 * 4. Add Enemy Dialog (AddEnemyDialog)
 */

package io.github.velyene.loreweaver.ui.screens.tracker.setup

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.util.CharacterParty
import io.github.velyene.loreweaver.domain.util.DifficultyRating
import io.github.velyene.loreweaver.domain.util.EncounterDifficulty
import io.github.velyene.loreweaver.domain.util.EncounterDifficultyResult
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.DEFAULT_ENEMY_HP
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.DEFAULT_ENEMY_INITIATIVE
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.NOTES_HEIGHT_DP
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.SETUP_BUTTON_HEIGHT_DP
import io.github.velyene.loreweaver.ui.screens.ConfirmationDialog
import io.github.velyene.loreweaver.ui.screens.tracker.components.TrackerModeBadge

@Composable
internal fun EncounterSetupView(
	notes: String,
	combatants: List<CombatantState>,
	availablePartyMembers: List<CharacterEntry>,
	encounterDifficulty: EncounterDifficultyResult?,
	onNotesChange: (String) -> Unit,
	onStart: () -> Unit,
	onAddParty: () -> Unit,
	onAddEnemy: (name: String, hp: Int, initiative: Int) -> Unit,
	onRemoveCombatant: (String) -> Unit
) {
	var showAddEnemyDialog by remember { mutableStateOf(false) }
	var combatantPendingRemoval by remember { mutableStateOf<CombatantState?>(null) }
	val partyCount = availablePartyMembers.count { it.party == CharacterParty.ADVENTURERS }

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		TrackerModeBadge(
			label = stringResource(R.string.combat_setup_badge_label),
			containerColor = MaterialTheme.colorScheme.tertiary,
			contentColor = MaterialTheme.colorScheme.onTertiary
		)
		Spacer(modifier = Modifier.height(16.dp))

		SetupQuickAddRow(
			partyCount = partyCount,
			onAddParty = onAddParty,
			onAddEnemy = { showAddEnemyDialog = true }
		)

		Spacer(modifier = Modifier.height(12.dp))

		if ((encounterDifficulty != null) && (encounterDifficulty.partySize > 0)) {
			// Difficulty is only shown once a valid party is available so the card never
			// presents partial calculations during early setup.
			EncounterDifficultyCard(encounterDifficulty = encounterDifficulty)
		}

		Spacer(modifier = Modifier.height(12.dp))

		SetupRosterSection(
			combatants = combatants,
			onRemoveCombatant = { combatantPendingRemoval = it },
			modifier = Modifier.weight(1f)
		)

		Spacer(modifier = Modifier.height(12.dp))

		EncounterNotesSection(notes = notes, onNotesChange = onNotesChange)

		Spacer(modifier = Modifier.height(16.dp))

		Button(
			onClick = onStart,
			enabled = combatants.isNotEmpty(),
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

@Composable
private fun EncounterDifficultyCard(encounterDifficulty: EncounterDifficultyResult) {
	val ratingLabel = EncounterDifficulty.formatDifficultyRating(encounterDifficulty.rating).first
	val difficultySummary = stringResource(
		R.string.encounter_difficulty_accessibility_desc,
		ratingLabel,
		encounterDifficulty.adjustedXp
	)

	Card(
		modifier = Modifier
			.fillMaxWidth()
			.semantics {
				contentDescription = difficultySummary
			},
		colors = CardDefaults.cardColors(
			containerColor = difficultyContainerColor(encounterDifficulty.rating)
		)
	) {
		Column(modifier = Modifier.padding(12.dp)) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					stringResource(R.string.encounter_difficulty_label),
					style = MaterialTheme.typography.labelMedium,
					fontWeight = FontWeight.Bold,
					modifier = Modifier.semantics { heading() }
				)
				Text(
					ratingLabel,
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold,
					color = difficultyTextColor(encounterDifficulty.rating)
				)
			}
			Spacer(modifier = Modifier.height(4.dp))
			Text(
				EncounterDifficulty.getDifficultyDescription(encounterDifficulty.rating),
				style = MaterialTheme.typography.bodySmall,
				fontSize = 11.sp,
				lineHeight = 14.sp
			)
			Spacer(modifier = Modifier.height(8.dp))
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				Column {
					Text(
						stringResource(
							R.string.party_level_label,
							encounterDifficulty.averagePartyLevel
						),
						fontSize = 10.sp
					)
					Text(
						stringResource(R.string.party_size_label, encounterDifficulty.partySize),
						fontSize = 10.sp
					)
				}
				Column(horizontalAlignment = Alignment.End) {
					Text(
						stringResource(
							R.string.monsters_count_label,
							encounterDifficulty.monsterCount
						),
						fontSize = 10.sp
					)
					Text(
						stringResource(R.string.adjusted_xp_label, encounterDifficulty.adjustedXp),
						fontSize = 10.sp
					)
				}
			}
		}
	}
}

@Composable
private fun difficultyContainerColor(rating: DifficultyRating): Color {
	return when (rating) {
		DifficultyRating.TRIVIAL -> MaterialTheme.colorScheme.surfaceVariant
		DifficultyRating.EASY -> MaterialTheme.colorScheme.primaryContainer
		DifficultyRating.MEDIUM -> MaterialTheme.colorScheme.tertiaryContainer
		DifficultyRating.HARD -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
		DifficultyRating.DEADLY -> MaterialTheme.colorScheme.errorContainer
		DifficultyRating.BEYOND_DEADLY -> MaterialTheme.colorScheme.error
	}
}

@Composable
private fun difficultyTextColor(rating: DifficultyRating): Color {
	return when (rating) {
		DifficultyRating.TRIVIAL, DifficultyRating.EASY -> MaterialTheme.colorScheme.onPrimaryContainer
		DifficultyRating.MEDIUM -> MaterialTheme.colorScheme.onTertiaryContainer
		DifficultyRating.HARD, DifficultyRating.DEADLY -> MaterialTheme.colorScheme.onErrorContainer
		DifficultyRating.BEYOND_DEADLY -> MaterialTheme.colorScheme.onError
	}
}

@Composable
private fun EncounterNotesSection(
	notes: String,
	onNotesChange: (String) -> Unit
) {
	val notesFieldDescription = stringResource(R.string.encounter_notes_title)

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.height(NOTES_HEIGHT_DP.dp)
			.border(1.dp, MaterialTheme.colorScheme.outline, androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
			.padding(12.dp)
	) {
		Text(
			stringResource(R.string.encounter_notes_title),
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			style = MaterialTheme.typography.labelSmall,
			modifier = Modifier.semantics { heading() }
		)
		BasicTextField(
			value = notes,
			onValueChange = onNotesChange,
			textStyle = TextStyle(
				color = MaterialTheme.colorScheme.onSurface,
				fontSize = 14.sp
			),
			modifier = Modifier
				.fillMaxSize()
				.semantics {
					contentDescription = notesFieldDescription
				}
		)
	}
}

@Composable
private fun AddEnemyDialog(
	onConfirm: (name: String, hp: Int, initiative: Int) -> Unit,
	onDismiss: () -> Unit
) {
	var name by remember { mutableStateOf("") }
	var hp by remember { mutableStateOf(DEFAULT_ENEMY_HP.toString()) }
	var initiative by remember { mutableStateOf(DEFAULT_ENEMY_INITIATIVE.toString()) }

	AlertDialog(
		onDismissRequest = onDismiss,
		title = {
			Text(
				stringResource(R.string.add_enemy_dialog_title),
				modifier = Modifier.semantics { heading() }
			)
		},
		text = {
			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				OutlinedTextField(
					value = name,
					onValueChange = { name = it },
					label = { Text(stringResource(R.string.name_label)) },
					modifier = Modifier.fillMaxWidth()
				)
				Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
					OutlinedTextField(
						value = hp,
						onValueChange = { hp = it.filter { c -> c.isDigit() } },
						label = { Text(stringResource(R.string.hp_label)) },
						keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
						modifier = Modifier.weight(1f)
					)
					OutlinedTextField(
						value = initiative,
						onValueChange = { initiative = it.filter { c -> c.isDigit() } },
						label = { Text(stringResource(R.string.initiative_label)) },
						keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
						modifier = Modifier.weight(1f)
					)
				}
			}
		},
		confirmButton = {
			Button(
				onClick = {
					if (name.isNotBlank()) {
						onConfirm(
							name.trim(),
							hp.toIntOrNull() ?: DEFAULT_ENEMY_HP,
							initiative.toIntOrNull() ?: DEFAULT_ENEMY_INITIATIVE
						)
					}
				}
			) { Text(stringResource(R.string.add_button)) }
		},
		dismissButton = {
			TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel_button)) }
		}
	)
}


