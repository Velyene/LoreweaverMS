/*
 * FILE: EncounterSetupSections.kt
 *
 * TABLE OF CONTENTS:
 * 1. Function: EnemyPresetSection
 * 2. Function: EnemyPresetCard
 * 3. Function: EncounterSetupSnapshotCard
 * 4. Function: SnapshotMetricCard
 * 5. Function: PartyMembersSection
 * 6. Value: isSelected
 * 7. Function: EncounterRosterSection
 * 8. Function: EncounterRosterCard
 * 9. Function: EnemiesSection
 * 10. Function: EnemyCombatantCard
 * 11. Function: filterSignedNumberInput
 */

package io.github.velyene.loreweaver.ui.screens.tracker.setup

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.res.stringResource
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
import io.github.velyene.loreweaver.domain.model.EncounterDifficultyTarget
import io.github.velyene.loreweaver.domain.model.EncounterGenerationDetails
import io.github.velyene.loreweaver.domain.model.EncounterGenerationSettings
import io.github.velyene.loreweaver.domain.model.EncounterGenerationSourceFilter
import io.github.velyene.loreweaver.domain.util.calculateEncounterPartyPowerEntries

@Composable
private fun EncounterSetupSectionShell(
	title: String,
	supportingText: String,
	trailingContent: (@Composable () -> Unit)? = null,
	content: @Composable () -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.border(1.dp, MaterialTheme.colorScheme.outline, androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
			.padding(12.dp)
	) {
		if (trailingContent == null) {
			Text(
				text = title,
				style = MaterialTheme.typography.labelSmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.semantics { heading() },
			)
		} else {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically,
			) {
				Text(
					text = title,
					style = MaterialTheme.typography.labelSmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					modifier = Modifier.semantics { heading() },
				)
				trailingContent()
			}
		}
		Spacer(modifier = Modifier.height(4.dp))
		Text(
			text = supportingText,
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
		Spacer(modifier = Modifier.height(8.dp))
		content()
	}
}

@Composable
private fun EncounterSetupEmptyState(message: String) {
	Text(
		text = message,
		color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
		fontSize = 13.sp,
		lineHeight = 20.sp,
	)
}

@Composable
internal fun EnemyPresetSection(
	presets: List<EncounterEnemyPreset>,
	onApplyPreset: (EncounterEnemyPreset) -> Unit
) {
	EncounterSetupSectionShell(
		title = stringResource(R.string.encounter_enemy_presets_title),
		supportingText = stringResource(R.string.encounter_enemy_presets_supporting_text),
	) {
		Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
			presets.forEach { preset ->
				androidx.compose.runtime.key(preset.label) {
					EnemyPresetCard(
						preset = preset,
						onApplyPreset = { onApplyPreset(preset) }
					)
				}
			}
		}
	}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EnemyPresetCard(
	preset: EncounterEnemyPreset,
	onApplyPreset: () -> Unit
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(modifier = Modifier.padding(12.dp)) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				Text(
					text = preset.label,
					style = MaterialTheme.typography.titleSmall,
					fontWeight = FontWeight.SemiBold,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					modifier = Modifier.weight(1f)
				)
				TextButton(onClick = onApplyPreset) {
					Text(text = stringResource(R.string.encounter_enemy_preset_apply_button))
				}
			}
			Text(
				text = preset.summary,
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.88f)
			)
			Spacer(modifier = Modifier.height(8.dp))
			FlowRow(
				horizontalArrangement = Arrangement.spacedBy(8.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				preset.entries.forEach { entry ->
					AssistChip(
						onClick = onApplyPreset,
						label = {
							Text(text = "${entry.name} ×${entry.quantity} • HP ${entry.hp} • Init ${entry.initiative}")
						}
					)
				}
			}
		}
	}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun EncounterSetupSnapshotCard(
	totalCombatants: Int,
	selectedPartyCount: Int,
	enemyCount: Int,
	initiativeLeader: CombatantState?
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(modifier = Modifier.padding(12.dp)) {
			Text(
				text = "Encounter Snapshot",
				style = MaterialTheme.typography.labelSmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.semantics { heading() }
			)
			Spacer(modifier = Modifier.height(4.dp))
			Text(
				text = "Track roster pressure before you go live so the demo starts with momentum.",
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Spacer(modifier = Modifier.height(10.dp))
			FlowRow(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(8.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				SnapshotMetricCard(label = "Party In", value = selectedPartyCount.toString())
				SnapshotMetricCard(label = "Enemies", value = enemyCount.toString())
				SnapshotMetricCard(label = "Total Turns", value = totalCombatants.toString())
				SnapshotMetricCard(
					label = "Lead Init",
					value = initiativeLeader?.let { "${it.name} (${it.initiative})" } ?: "Not set"
				)
			}
		}
	}
}

@Composable
private fun SnapshotMetricCard(label: String, value: String) {
	Card(
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
		modifier = Modifier.fillMaxWidth()
	) {
		Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
			Text(
				text = label,
				style = MaterialTheme.typography.labelSmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Spacer(modifier = Modifier.height(2.dp))
			Text(
				text = value,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
	}
}


@Composable
internal fun EncounterRosterSection(
	combatants: List<CombatantState>,
	savedPartyMemberIds: Set<String>,
	onUpdateCombatantInitiative: (String, Int) -> Unit,
	onMoveCombatantUp: (String) -> Unit,
	onMoveCombatantDown: (String) -> Unit,
	onRemoveCombatant: (CombatantState) -> Unit
) {
	EncounterSetupSectionShell(
		title = stringResource(R.string.encounter_roster_title, combatants.size),
		supportingText = "Preview initiative flow and trim the lineup before the first round begins.",
	) {

		if (combatants.isEmpty()) {
			EncounterSetupEmptyState(stringResource(R.string.encounter_roster_empty_message))
		} else {
			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				combatants.forEachIndexed { index, combatant ->
					androidx.compose.runtime.key(combatant.characterId) {
						EncounterRosterCard(
							combatant = combatant,
							isPartyMember = savedPartyMemberIds.contains(combatant.characterId),
							onUpdateInitiative = onUpdateCombatantInitiative,
							canMoveEarlier = index > 0,
							canMoveLater = index < combatants.lastIndex,
							onMoveEarlier = onMoveCombatantUp,
							onMoveLater = onMoveCombatantDown,
							onRemoveCombatant = { onRemoveCombatant(combatant) }
						)
					}
				}
			}
		}
	}
}

@Composable
private fun EncounterRosterCard(
	combatant: CombatantState,
	isPartyMember: Boolean,
	onUpdateInitiative: (String, Int) -> Unit,
	canMoveEarlier: Boolean,
	canMoveLater: Boolean,
	onMoveEarlier: (String) -> Unit,
	onMoveLater: (String) -> Unit,
	onRemoveCombatant: () -> Unit
) {
	var initiativeInput by remember(combatant.characterId, combatant.initiative) {
		mutableStateOf(combatant.initiative.toString())
	}
	val containerColor = if (isPartyMember) {
		MaterialTheme.colorScheme.primaryContainer
	} else {
		MaterialTheme.colorScheme.tertiaryContainer
	}
	val contentColor = if (isPartyMember) {
		MaterialTheme.colorScheme.onPrimaryContainer
	} else {
		MaterialTheme.colorScheme.onTertiaryContainer
	}
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = containerColor)
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 12.dp, vertical = 10.dp),
			verticalArrangement = Arrangement.spacedBy(10.dp)
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				Column(modifier = Modifier.weight(1f)) {
					Text(
						text = combatant.name,
						style = MaterialTheme.typography.titleSmall,
						fontWeight = FontWeight.SemiBold,
						color = contentColor
					)
					Text(
						text = if (isPartyMember) "Party" else "Enemy",
						style = MaterialTheme.typography.labelSmall,
						color = contentColor.copy(alpha = 0.82f)
					)
					Text(
						text = "HP ${combatant.currentHp}/${combatant.maxHp} • Init ${combatant.initiative}",
						style = MaterialTheme.typography.bodySmall,
						color = contentColor.copy(alpha = 0.82f)
					)
				}
				TextButton(onClick = onRemoveCombatant) {
					Text(text = stringResource(R.string.remove_button), color = contentColor)
				}
			}
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				Text(
					text = stringResource(R.string.encounter_order_label),
					style = MaterialTheme.typography.labelMedium,
					color = contentColor
				)
				TextButton(
					onClick = { onMoveEarlier(combatant.characterId) },
					enabled = canMoveEarlier
				) {
					Text(text = stringResource(R.string.encounter_move_earlier_button), color = contentColor)
				}
				TextButton(
					onClick = { onMoveLater(combatant.characterId) },
					enabled = canMoveLater
				) {
					Text(text = stringResource(R.string.encounter_move_later_button), color = contentColor)
				}
			}
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				Text(
					text = stringResource(R.string.initiative_label),
					style = MaterialTheme.typography.labelMedium,
					color = contentColor
				)
				TextButton(onClick = { onUpdateInitiative(combatant.characterId, combatant.initiative - 1) }) {
					Text(text = "-1", color = contentColor)
				}
				OutlinedTextField(
					value = initiativeInput,
					onValueChange = { updatedValue ->
						initiativeInput = filterSignedNumberInput(updatedValue)
						initiativeInput.toIntOrNull()?.let { parsedInitiative ->
							onUpdateInitiative(combatant.characterId, parsedInitiative)
						}
					},
					modifier = Modifier.weight(1f),
					textStyle = TextStyle(color = contentColor),
					keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
					singleLine = true
				)
				TextButton(onClick = { onUpdateInitiative(combatant.characterId, combatant.initiative + 1) }) {
					Text(text = "+1", color = contentColor)
				}
			}
		}
	}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun RandomEncounterGenerationSection(
	selectedPartyMembers: List<CharacterEntry>,
	settings: EncounterGenerationSettings,
	generationDetails: EncounterGenerationDetails?,
	monsterTypeOptions: List<String>,
	monsterGroupOptions: List<String>,
	onSettingsChange: (EncounterGenerationSettings) -> Unit,
	onGenerateEncounter: () -> Unit,
	enabled: Boolean
) {
	val partyPowerEntries = remember(selectedPartyMembers, settings) {
		calculateEncounterPartyPowerEntries(
			partyMembers = selectedPartyMembers,
			difficultyTarget = settings.difficultyTarget,
			customTargetXp = settings.customTargetXp
		)
	}
	val partyPower = remember(partyPowerEntries, settings, selectedPartyMembers) {
		if (settings.difficultyTarget == EncounterDifficultyTarget.CUSTOM) {
			settings.customTargetXp?.coerceAtLeast(0) ?: 0
		} else {
			partyPowerEntries.sumOf { it.budgetXp }
		}
	}
	var customXpInput by remember(settings.customTargetXp) { mutableStateOf(settings.customTargetXp?.toString().orEmpty()) }
	var maxCrInput by remember(settings.maximumEnemyCr) { mutableStateOf(settings.maximumEnemyCr?.let(::formatGenerationCrInput).orEmpty()) }
	var maxQtyInput by remember(settings.maximumEnemyQuantity) { mutableStateOf(settings.maximumEnemyQuantity?.toString().orEmpty()) }
	var creatureTypeFilter by remember(settings.creatureTypeFilter) { mutableStateOf(settings.creatureTypeFilter.orEmpty()) }
	var groupFilter by remember(settings.groupFilter) { mutableStateOf(settings.groupFilter.orEmpty()) }

	EncounterSetupSectionShell(
		title = stringResource(R.string.encounter_generator_title),
		supportingText = stringResource(R.string.encounter_generator_supporting_text),
	) {
		Text(
			text = stringResource(R.string.encounter_generator_party_power_label, partyPower),
			style = MaterialTheme.typography.titleSmall,
			fontWeight = FontWeight.SemiBold,
			color = MaterialTheme.colorScheme.onSurface
		)
		if (partyPowerEntries.isNotEmpty()) {
			Text(
				text = partyPowerEntries.joinToString(" • ") { entry -> "${entry.characterName} Lv ${entry.level}=${entry.budgetXp}" },
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
		}
		Spacer(modifier = Modifier.height(8.dp))
		FlowRow(
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			EncounterDifficultyTarget.entries.forEach { difficultyTarget ->
				FilterChip(
					selected = settings.difficultyTarget == difficultyTarget,
					onClick = {
						onSettingsChange(
							settings.copy(
								difficultyTarget = difficultyTarget,
								customTargetXp = if (difficultyTarget == EncounterDifficultyTarget.CUSTOM) settings.customTargetXp else null
							)
						)
					},
					label = { Text(formatDifficultyTargetLabel(difficultyTarget)) }
				)
			}
		}
		if (settings.difficultyTarget == EncounterDifficultyTarget.CUSTOM) {
			Spacer(modifier = Modifier.height(8.dp))
			OutlinedTextField(
				value = customXpInput,
				onValueChange = { updated ->
					customXpInput = updated.filter(Char::isDigit)
					onSettingsChange(settings.copy(customTargetXp = customXpInput.toIntOrNull()))
				},
				label = { Text(stringResource(R.string.encounter_generator_custom_target_xp_label)) },
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
				modifier = Modifier.fillMaxWidth(),
				singleLine = true
			)
		}
		Spacer(modifier = Modifier.height(8.dp))
		Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
			OutlinedTextField(
				value = maxCrInput,
				onValueChange = { updated ->
					maxCrInput = updated.filter { it.isDigit() || it == '.' }
					onSettingsChange(settings.copy(maximumEnemyCr = maxCrInput.toDoubleOrNull()))
				},
				label = { Text(stringResource(R.string.encounter_generator_max_cr_label)) },
				modifier = Modifier.weight(1f),
				singleLine = true
			)
			OutlinedTextField(
				value = maxQtyInput,
				onValueChange = { updated ->
					maxQtyInput = updated.filter(Char::isDigit)
					onSettingsChange(settings.copy(maximumEnemyQuantity = maxQtyInput.toIntOrNull()))
				},
				label = { Text(stringResource(R.string.encounter_generator_max_quantity_label)) },
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
				modifier = Modifier.weight(1f),
				singleLine = true
			)
		}
		Spacer(modifier = Modifier.height(8.dp))
		OutlinedTextField(
			value = creatureTypeFilter,
			onValueChange = { updated ->
				creatureTypeFilter = updated
				onSettingsChange(settings.copy(creatureTypeFilter = updated.trim().ifBlank { null }))
			},
			label = { Text(stringResource(R.string.encounter_generator_creature_type_label)) },
			modifier = Modifier.fillMaxWidth(),
			singleLine = true,
			supportingText = {
				Text(monsterTypeOptions.take(5).joinToString().ifBlank { stringResource(R.string.none_label) })
			}
		)
		Spacer(modifier = Modifier.height(8.dp))
		OutlinedTextField(
			value = groupFilter,
			onValueChange = { updated ->
				groupFilter = updated
				onSettingsChange(settings.copy(groupFilter = updated.trim().ifBlank { null }))
			},
			label = { Text(stringResource(R.string.encounter_generator_group_filter_label)) },
			modifier = Modifier.fillMaxWidth(),
			singleLine = true,
			supportingText = {
				Text(monsterGroupOptions.take(5).joinToString().ifBlank { stringResource(R.string.none_label) })
			}
		)
		Spacer(modifier = Modifier.height(8.dp))
		Text(
			text = stringResource(R.string.encounter_generator_source_filter_label),
			style = MaterialTheme.typography.labelMedium,
			color = MaterialTheme.colorScheme.onSurface
		)
		Spacer(modifier = Modifier.height(6.dp))
		FlowRow(
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			EncounterGenerationSourceFilter.entries.forEach { sourceFilter ->
				FilterChip(
					selected = settings.sourceFilter == sourceFilter,
					onClick = { onSettingsChange(settings.copy(sourceFilter = sourceFilter)) },
					label = { Text(formatGenerationSourceFilterLabel(sourceFilter)) }
				)
			}
		}
		Spacer(modifier = Modifier.height(8.dp))
		FlowRow(
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			FilterChip(
				selected = settings.allowDuplicateEnemies,
				onClick = { onSettingsChange(settings.copy(allowDuplicateEnemies = !settings.allowDuplicateEnemies)) },
				label = { Text(stringResource(R.string.encounter_generator_duplicates_label)) }
			)
			FilterChip(
				selected = settings.allowSingleHighCrEnemy,
				onClick = { onSettingsChange(settings.copy(allowSingleHighCrEnemy = !settings.allowSingleHighCrEnemy)) },
				label = { Text(stringResource(R.string.encounter_generator_dangerous_single_label)) }
			)
		}
		Spacer(modifier = Modifier.height(10.dp))
		TextButton(onClick = onGenerateEncounter, enabled = enabled) {
			Text(text = stringResource(R.string.encounter_generator_generate_button))
		}
		generationDetails?.let { details ->
			Spacer(modifier = Modifier.height(8.dp))
			Card(
				modifier = Modifier.fillMaxWidth(),
				colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
			) {
				Column(
					modifier = Modifier.padding(12.dp),
					verticalArrangement = Arrangement.spacedBy(6.dp)
				) {
					Text(
						text = stringResource(R.string.encounter_generator_log_title),
						style = MaterialTheme.typography.labelMedium,
						fontWeight = FontWeight.SemiBold,
						color = MaterialTheme.colorScheme.onSurface
					)
					details.logLines.forEach { logLine ->
						Text(
							text = stringResource(R.string.combat_log_bullet, logLine),
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.onSurface
						)
					}
				}
			}
		}
	}
}


private fun filterSignedNumberInput(value: String): String {
	return buildString {
		value.forEachIndexed { index, character ->
			if (character.isDigit() || (character == '-' && index == 0)) {
				append(character)
			}
		}
	}
}

private fun formatDifficultyTargetLabel(target: EncounterDifficultyTarget): String {
	return when (target) {
		EncounterDifficultyTarget.LOW -> "Low"
		EncounterDifficultyTarget.MODERATE -> "Moderate"
		EncounterDifficultyTarget.HIGH -> "High"
		EncounterDifficultyTarget.CUSTOM -> "Custom"
	}
}

private fun formatGenerationCrInput(value: Double): String {
	return if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
}

private fun formatGenerationSourceFilterLabel(sourceFilter: EncounterGenerationSourceFilter): String {
	return when (sourceFilter) {
		EncounterGenerationSourceFilter.SRD_ONLY -> "SRD Only"
		EncounterGenerationSourceFilter.CUSTOM_ONLY -> "Custom Only"
		EncounterGenerationSourceFilter.BOTH -> "Both"
	}
}

