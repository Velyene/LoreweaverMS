/*
 * FILE: EncounterSetupSupport.kt
 *
 * TABLE OF CONTENTS:
 * 1. Function: EncounterDifficultyCard
 * 2. Function: difficultyContainerColor
 * 3. Function: difficultyTextColor
 * 4. Function: EncounterNotesSection
 * 5. Function: AddEnemyDialog
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
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
import io.github.velyene.loreweaver.domain.util.DifficultyRating
import io.github.velyene.loreweaver.domain.util.EncounterDifficulty
import io.github.velyene.loreweaver.domain.util.EncounterDifficultyResult
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.DEFAULT_ENEMY_HP
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.DEFAULT_ENEMY_INITIATIVE
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.NOTES_HEIGHT_DP
import io.github.velyene.loreweaver.ui.screens.tracker.setup.ENCOUNTER_SETUP_LOCATION_FIELD_TAG

@Composable
internal fun EncounterDifficultyCard(encounterDifficulty: EncounterDifficultyResult) {
	val ratingLabel = EncounterDifficulty.formatDifficultyRating(encounterDifficulty.rating).first
	val difficultySummary = buildString {
		append(stringResource(R.string.encounter_difficulty_label))
		append(' ')
		append(ratingLabel)
		append(". ")
		append(stringResource(R.string.adjusted_xp_label, encounterDifficulty.adjustedXp))
	}

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
internal fun LocationTerrainSection(
	locationTerrain: String,
	onLocationTerrainChange: (String) -> Unit,
) {
	val locationTerrainLabel = stringResource(R.string.encounter_location_terrain_label)
	Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
		OutlinedTextField(
			value = locationTerrain,
			onValueChange = onLocationTerrainChange,
			modifier = Modifier
				.fillMaxWidth()
				.testTag(ENCOUNTER_SETUP_LOCATION_FIELD_TAG)
				.semantics { contentDescription = locationTerrainLabel }
			,
			label = { Text(locationTerrainLabel) },
			placeholder = { Text(stringResource(R.string.encounter_location_terrain_placeholder)) },
		)
		Text(
			text = stringResource(R.string.encounter_location_terrain_supporting_text),
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
	}
}

@Composable
internal fun EncounterNotesSection(
	notes: String,
	onNotesChange: (String) -> Unit
) {
	val notesFieldDescription = stringResource(R.string.encounter_notes_title)
	var isExpanded by rememberSaveable { mutableStateOf(false) }
	val notesPreview = notes.trim().replace("\n", " ").take(120)

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.border(1.dp, MaterialTheme.colorScheme.outline, androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
			.padding(12.dp)
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				stringResource(R.string.encounter_notes_title),
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				style = MaterialTheme.typography.labelSmall,
				modifier = Modifier.semantics { heading() }
			)
			TextButton(onClick = { isExpanded = !isExpanded }) {
				Text(
					text = stringResource(
						if (isExpanded) R.string.encounter_notes_collapse_action else R.string.encounter_notes_expand_action
					)
				)
			}
		}
		if (isExpanded) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.height(NOTES_HEIGHT_DP.dp)
			) {
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
		} else {
			Text(
				text = if (notesPreview.isBlank()) {
					stringResource(R.string.encounter_notes_collapsed_empty)
				} else {
					stringResource(R.string.encounter_notes_collapsed_preview, notesPreview)
				},
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
		}
	}
}

@Composable
internal fun AddEnemyDialog(
	initialName: String,
	initialHp: String,
	initialInitiative: String,
	initialQuantity: String,
	titleOverride: String? = null,
	helperTextOverride: String? = null,
	onConfirm: (name: String, hp: Int, initiative: Int, quantity: Int) -> Unit,
	onDismiss: () -> Unit
) {
	var name by remember(initialName) { mutableStateOf(initialName) }
	var hp by remember(initialHp) { mutableStateOf(initialHp) }
	var initiative by remember(initialInitiative) { mutableStateOf(initialInitiative) }
	var quantity by remember(initialQuantity) { mutableStateOf(initialQuantity) }

	AlertDialog(
		onDismissRequest = onDismiss,
		title = {
			Text(
				titleOverride ?: stringResource(R.string.add_enemy_dialog_title),
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
				Text(
					text = helperTextOverride ?: stringResource(R.string.encounter_enemy_batch_helper),
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant
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
					OutlinedTextField(
						value = quantity,
						onValueChange = { quantity = it.filter { c -> c.isDigit() } },
						label = { Text(stringResource(R.string.encounter_enemy_quantity_label)) },
						keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
						modifier = Modifier.weight(1f)
					)
				}
			}
		},
		confirmButton = {
			val parsedQuantity = quantity.toIntOrNull()?.coerceAtLeast(1) ?: 1
			Button(
				onClick = {
					if (name.isNotBlank()) {
						onConfirm(
							name.trim(),
							hp.toIntOrNull() ?: DEFAULT_ENEMY_HP,
							initiative.toIntOrNull() ?: DEFAULT_ENEMY_INITIATIVE,
							parsedQuantity
						)
					}
				}
			) {
				Text(
					text = if (parsedQuantity > 1) {
						stringResource(R.string.encounter_add_enemy_batch_button, parsedQuantity)
					} else {
						stringResource(R.string.add_button)
					}
				)
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel_button)) }
		}
	)
}

