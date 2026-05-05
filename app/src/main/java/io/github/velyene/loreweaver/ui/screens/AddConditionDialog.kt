/*
 * FILE: AddConditionDialog.kt
 *
 * TABLE OF CONTENTS:
 * 1. Add Condition Sheet Tags
 * 2. Add Condition Dialog
 * 3. Condition Selection Sections
 * 4. Allowed Condition Filtering
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R

const val ADD_CONDITION_SHEET_TAG = "add_condition_sheet"

/**
 * Dialog for adding a condition to a combatant.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddConditionDialog(
	participantName: String? = null,
	titleOverride: String? = null,
	supportingTextOverride: String? = null,
	availableConditions: Set<String>? = null,
	summaryContent: (@Composable () -> Unit)? = null,
	initialHasDuration: Boolean = true,
	initialPersistsAcrossEncounters: Boolean = false,
	showDurationControls: Boolean = true,
	showPersistenceToggle: Boolean = true,
	sheetTag: String = ADD_CONDITION_SHEET_TAG,
	onConfirm: (conditionName: String, duration: Int?, persistsAcrossEncounters: Boolean) -> Unit,
	onDismiss: () -> Unit
) {
	var selectedCondition by remember { mutableStateOf("") }
	var duration by remember { mutableStateOf("") }
	var hasDuration by remember(initialHasDuration) { mutableStateOf(initialHasDuration) }
	var persistsAcrossEncounters by remember(initialPersistsAcrossEncounters) {
		mutableStateOf(initialPersistsAcrossEncounters)
	}
	val selectedMetadata = remember(selectedCondition) {
		selectedCondition.takeIf(String::isNotBlank)?.let(ConditionConstants::metadataFor)
	}
	val officialConditionsLabel = stringResource(R.string.condition_picker_official_conditions)
	val builtInStatusesLabel = stringResource(R.string.condition_picker_built_in_statuses)
	val customEffectsLabel = stringResource(R.string.condition_picker_custom_effects)
	val homebrewEffectsLabel = stringResource(R.string.condition_picker_homebrew_effects)
	val allowedConditions = remember(availableConditions) {
		availableConditions
			?.map(::canonicalStatusLabel)
			?.toSet()
	}
	val dialogTitle = titleOverride ?: participantName
		?.takeIf(String::isNotBlank)
		?.let { stringResource(R.string.encounter_apply_status_dialog_title, it) }
		?: stringResource(R.string.add_condition_dialog_title)
	val supportingText = supportingTextOverride

	ModalBottomSheet(
		onDismissRequest = onDismiss,
		modifier = Modifier.testTag(sheetTag),
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 20.dp, vertical = 8.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Text(
				text = dialogTitle,
				modifier = Modifier.semantics { heading() },
			)
			supportingText?.takeIf(String::isNotBlank)?.let { text ->
				Text(
					text = text,
					color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
				)
			}
			HorizontalDivider()
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.heightIn(max = 520.dp)
					.verticalScroll(rememberScrollState()),
				verticalArrangement = Arrangement.spacedBy(12.dp),
			) {
				summaryContent?.invoke()
				selectedMetadata?.let { metadata ->
					Text(
						text = "${metadata.iconGlyph.orEmpty()} ${metadata.category.name.replace('_', ' ')}",
						color = metadata.borderColor
					)
				}
				OutlinedTextField(
					value = selectedCondition,
					onValueChange = {},
					readOnly = true,
					label = { Text(stringResource(R.string.condition_label)) },
					modifier = Modifier.fillMaxWidth(),
				)

				ConditionChipSection(
					title = officialConditionsLabel,
					conditions = filterAllowedConditions(ConditionConstants.OFFICIAL_CONDITIONS, allowedConditions),
					selectedCondition = selectedCondition,
					onSelectCondition = { condition ->
						selectedCondition = condition
						persistsAcrossEncounters = if (showPersistenceToggle) {
							ConditionConstants.defaultPersistsAcrossEncounters(condition)
						} else {
							initialPersistsAcrossEncounters
						}
					}
				)
				ConditionChipSection(
					title = builtInStatusesLabel,
					conditions = filterAllowedConditions(ConditionConstants.BUILT_IN_STATUS_LABELS, allowedConditions),
					selectedCondition = selectedCondition,
					onSelectCondition = { condition ->
						selectedCondition = condition
						persistsAcrossEncounters = if (showPersistenceToggle) {
							ConditionConstants.defaultPersistsAcrossEncounters(condition)
						} else {
							initialPersistsAcrossEncounters
						}
					}
				)
				ConditionChipSection(
					title = customEffectsLabel,
					conditions = filterAllowedConditions(ConditionConstants.CUSTOM_EFFECT_LABELS, allowedConditions),
					selectedCondition = selectedCondition,
					onSelectCondition = { condition ->
						selectedCondition = condition
						persistsAcrossEncounters = if (showPersistenceToggle) {
							ConditionConstants.defaultPersistsAcrossEncounters(condition)
						} else {
							initialPersistsAcrossEncounters
						}
					}
				)
				ConditionChipSection(
					title = homebrewEffectsLabel,
					conditions = filterAllowedConditions(ConditionConstants.CUSTOM_HOMEBREW_ONLY_LABELS, allowedConditions),
					selectedCondition = selectedCondition,
					onSelectCondition = { condition ->
						selectedCondition = condition
						persistsAcrossEncounters = if (showPersistenceToggle) {
							ConditionConstants.defaultPersistsAcrossEncounters(condition)
						} else {
							initialPersistsAcrossEncounters
						}
					}
				)

				if (showDurationControls) {
					Row(
						modifier = Modifier.fillMaxWidth(),
						verticalAlignment = Alignment.CenterVertically
					) {
						Checkbox(
							checked = hasDuration,
							onCheckedChange = { hasDuration = it }
						)
						Text(stringResource(R.string.condition_has_duration_label))
					}
				}

				if (showPersistenceToggle) {
					Row(
						modifier = Modifier.fillMaxWidth(),
						verticalAlignment = Alignment.CenterVertically
					) {
						Checkbox(
							checked = persistsAcrossEncounters,
							onCheckedChange = { persistsAcrossEncounters = it }
						)
						Text(stringResource(R.string.condition_persistent_label))
					}
				}

				if (showDurationControls && hasDuration) {
					OutlinedTextField(
						value = duration,
						onValueChange = { if (it.all { c -> c.isDigit() }) duration = it },
						label = { Text(stringResource(R.string.condition_duration_rounds_label)) },
						keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
						modifier = Modifier.fillMaxWidth(),
						placeholder = { Text(stringResource(R.string.condition_duration_placeholder)) }
					)
				}
			}
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(8.dp),
			) {
				TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
					Text(stringResource(R.string.cancel_button))
				}
				Button(
					onClick = {
						if (selectedCondition.isNotBlank()) {
							val dur = if (hasDuration && duration.isNotBlank()) duration.toIntOrNull() else null
							onConfirm(selectedCondition, dur, persistsAcrossEncounters)
						}
					},
					enabled = selectedCondition.isNotBlank(),
					modifier = Modifier.weight(1f),
				) {
					Text(stringResource(R.string.add_button))
				}
			}
		}
	}
}

/**
 * 1. Add Condition Sheet Tags
 */

private fun filterAllowedConditions(
	conditions: List<String>,
	allowedConditions: Set<String>?
): List<String> {
	return allowedConditions?.let { allowed ->
		conditions.filter { condition -> canonicalStatusLabel(condition) in allowed }
	} ?: conditions
}

/**
 * 3. Condition Selection Sections
 */

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ConditionChipSection(
	title: String,
	conditions: List<String>,
	selectedCondition: String,
	onSelectCondition: (String) -> Unit,
) {
	if (conditions.isEmpty()) return

	Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
		Text(
			text = title,
		)
		FlowRow(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp),
		) {
			conditions.forEach { condition ->
				val metadata = ConditionConstants.metadataFor(condition)
				FilterChip(
					selected = selectedCondition == condition,
					onClick = { onSelectCondition(condition) },
					label = {
						Text(
							text = buildString {
								metadata.iconGlyph?.takeIf(String::isNotBlank)?.let {
									append(it)
									append(' ')
								}
								append(condition)
							},
						)
					},
					border = BorderStroke(1.dp, metadata.borderColor.copy(alpha = 0.8f)),
					colors = FilterChipDefaults.filterChipColors(
						selectedContainerColor = metadata.color.copy(alpha = 0.28f),
						selectedLabelColor = metadata.borderColor,
						containerColor = metadata.color.copy(alpha = 0.16f),
						labelColor = metadata.borderColor.copy(alpha = 0.9f),
					)
				)
			}
		}
	}
}
