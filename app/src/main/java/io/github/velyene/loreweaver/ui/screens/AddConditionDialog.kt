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
import androidx.compose.material3.MaterialTheme
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
 * Configuration for [AddConditionDialog].
 */
data class AddConditionDialogConfig(
	val participantName: String? = null,
	val titleOverride: String? = null,
	val supportingTextOverride: String? = null,
	val availableConditions: Set<String>? = null,
	val initialHasDuration: Boolean = true,
	val initialPersistsAcrossEncounters: Boolean = false,
	val showDurationControls: Boolean = true,
	val showPersistenceToggle: Boolean = true,
	val sheetTag: String = ADD_CONDITION_SHEET_TAG,
)

/**
 * Dialog for adding a condition to a combatant.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddConditionDialog(
	config: AddConditionDialogConfig = AddConditionDialogConfig(),
	summaryContent: (@Composable () -> Unit)? = null,
	onConfirm: (conditionName: String, duration: Int?, persistsAcrossEncounters: Boolean) -> Unit,
	onDismiss: () -> Unit
) {
	var selectedCondition by remember { mutableStateOf("") }
	var duration by remember { mutableStateOf("") }
	var hasDuration by remember(config.initialHasDuration) { mutableStateOf(config.initialHasDuration) }
	var persistsAcrossEncounters by remember(config.initialPersistsAcrossEncounters) {
		mutableStateOf(config.initialPersistsAcrossEncounters)
	}
	val allowedConditions = rememberAllowedConditions(config.availableConditions)
	val dialogTitle = rememberConditionDialogTitle(config)

	val onSelectCondition: (String) -> Unit = { condition ->
		selectedCondition = condition
		persistsAcrossEncounters = resolveDefaultPersistence(config, condition)
	}
	val onHasDurationChange: (Boolean) -> Unit = { hasDuration = it }
	val onPersistsChange: (Boolean) -> Unit = { persistsAcrossEncounters = it }
	val onDurationChange: (String) -> Unit = { duration = it }

	ModalBottomSheet(
		onDismissRequest = onDismiss,
		modifier = Modifier.testTag(config.sheetTag),
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 20.dp, vertical = 8.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Text(text = dialogTitle, modifier = Modifier.semantics { heading() })
			if (!config.supportingTextOverride.isNullOrBlank()) {
				Text(
					text = config.supportingTextOverride,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			}
			HorizontalDivider()
			AddConditionScrollableContent(
				config = config,
				summaryContent = summaryContent,
				state = AddConditionScrollState(
					selectedCondition = selectedCondition,
					hasDuration = hasDuration,
					persistsAcrossEncounters = persistsAcrossEncounters,
					duration = duration,
				),
				allowedConditions = allowedConditions,
				onSelectCondition = onSelectCondition,
				onHasDurationChange = onHasDurationChange,
				onPersistsChange = onPersistsChange,
				onDurationChange = onDurationChange,
			)
			AddConditionActionButtons(
				selectedCondition = selectedCondition,
				hasDuration = hasDuration,
				duration = duration,
				persistsAcrossEncounters = persistsAcrossEncounters,
				onConfirm = onConfirm,
				onDismiss = onDismiss,
			)
		}
	}
}

@Composable
private fun rememberConditionDialogTitle(config: AddConditionDialogConfig): String {
	val applyStatusTitle = config.participantName
		?.takeIf(String::isNotBlank)
		?.let { stringResource(R.string.encounter_apply_status_dialog_title, it) }
	return config.titleOverride
		?: applyStatusTitle
		?: stringResource(R.string.add_condition_dialog_title)
}

private fun resolveDefaultPersistence(config: AddConditionDialogConfig, condition: String): Boolean {
	return if (config.showPersistenceToggle) {
		ConditionConstants.defaultPersistsAcrossEncounters(condition)
	} else {
		config.initialPersistsAcrossEncounters
	}
}

@Composable
private fun rememberAllowedConditions(availableConditions: Set<String>?): Set<String>? {
	return remember(availableConditions) {
		availableConditions?.map(::canonicalStatusLabel)?.toSet()
	}
}

private data class AddConditionScrollState(
	val selectedCondition: String,
	val hasDuration: Boolean,
	val persistsAcrossEncounters: Boolean,
	val duration: String,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddConditionScrollableContent(
	config: AddConditionDialogConfig,
	summaryContent: (@Composable () -> Unit)?,
	state: AddConditionScrollState,
	allowedConditions: Set<String>?,
	onSelectCondition: (String) -> Unit,
	onHasDurationChange: (Boolean) -> Unit,
	onPersistsChange: (Boolean) -> Unit,
	onDurationChange: (String) -> Unit,
) {
	val selectedCondition = state.selectedCondition
	val selectedMetadata = remember(selectedCondition) {
		selectedCondition.takeIf(String::isNotBlank)?.let(ConditionConstants::metadataFor)
	}
	val officialLabel = stringResource(R.string.condition_picker_official_conditions)
	val builtInLabel = stringResource(R.string.condition_picker_built_in_statuses)
	val customLabel = stringResource(R.string.condition_picker_custom_effects)
	val homebrewLabel = stringResource(R.string.condition_picker_homebrew_effects)

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
				color = metadata.borderColor,
			)
		}
		OutlinedTextField(
			value = selectedCondition,
			onValueChange = {},
			readOnly = true,
			label = { Text(stringResource(R.string.add_condition_dialog_condition_label)) },
			modifier = Modifier.fillMaxWidth(),
		)
		ConditionChipSection(
			title = officialLabel,
			conditions = filterAllowedConditions(ConditionConstants.OFFICIAL_CONDITIONS, allowedConditions),
			selectedCondition = selectedCondition,
			onSelectCondition = onSelectCondition,
		)
		ConditionChipSection(
			title = builtInLabel,
			conditions = filterAllowedConditions(ConditionConstants.BUILT_IN_STATUS_LABELS, allowedConditions),
			selectedCondition = selectedCondition,
			onSelectCondition = onSelectCondition,
		)
		ConditionChipSection(
			title = customLabel,
			conditions = filterAllowedConditions(ConditionConstants.CUSTOM_EFFECT_LABELS, allowedConditions),
			selectedCondition = selectedCondition,
			onSelectCondition = onSelectCondition,
		)
		ConditionChipSection(
			title = homebrewLabel,
			conditions = filterAllowedConditions(ConditionConstants.CUSTOM_HOMEBREW_ONLY_LABELS, allowedConditions),
			selectedCondition = selectedCondition,
			onSelectCondition = onSelectCondition,
		)
		if (config.showDurationControls) {
			LabeledCheckboxRow(
				checked = state.hasDuration,
				onCheckedChange = onHasDurationChange,
				label = stringResource(R.string.add_condition_dialog_has_duration_label),
			)
		}
		if (config.showPersistenceToggle) {
			LabeledCheckboxRow(
				checked = state.persistsAcrossEncounters,
				onCheckedChange = onPersistsChange,
				label = stringResource(R.string.add_condition_dialog_persistent_label),
			)
		}
		if (config.showDurationControls && state.hasDuration) {
			OutlinedTextField(
				value = state.duration,
				onValueChange = { if (it.all(Char::isDigit)) onDurationChange(it) },
				label = { Text(stringResource(R.string.add_condition_dialog_duration_label)) },
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
				modifier = Modifier.fillMaxWidth(),
				placeholder = { Text(stringResource(R.string.add_condition_dialog_duration_placeholder)) },
			)
		}
	}
}

@Composable
private fun LabeledCheckboxRow(
	checked: Boolean,
	onCheckedChange: (Boolean) -> Unit,
	label: String,
) {
	Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
		Checkbox(checked = checked, onCheckedChange = onCheckedChange)
		Text(text = label)
	}
}

@Composable
private fun AddConditionActionButtons(
	selectedCondition: String,
	hasDuration: Boolean,
	duration: String,
	persistsAcrossEncounters: Boolean,
	onConfirm: (String, Int?, Boolean) -> Unit,
	onDismiss: () -> Unit,
) {
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
					onConfirm(selectedCondition, resolveDurationForConfirm(hasDuration, duration), persistsAcrossEncounters)
				}
			},
			enabled = selectedCondition.isNotBlank(),
			modifier = Modifier.weight(1f),
		) {
			Text(stringResource(R.string.add_button))
		}
	}
}

private fun resolveDurationForConfirm(hasDuration: Boolean, duration: String): Int? {
	return if (hasDuration && duration.isNotBlank()) duration.toIntOrNull() else null
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
