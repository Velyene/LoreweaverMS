/*
 * FILE: StatusEffectChips.kt
 *
 * TABLE OF CONTENTS:
 * 1. Status chip models and builders
 * 2. Status chip row composables
 * 3. Display and accessibility support
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R

data class StatusChipModel(
	val name: String,
	val durationText: String = "",
	val isPersistent: Boolean = false,
	val isSrdCondition: Boolean = ConditionConstants.isSrdCondition(name),
)

internal fun statusChipModel(
	name: String,
	durationText: String = "",
	isPersistent: Boolean = ConditionConstants.defaultPersistsAcrossEncounters(name),
): StatusChipModel {
	val canonicalName = canonicalStatusLabel(name)
	return StatusChipModel(
		name = canonicalName,
		durationText = durationText,
		isPersistent = isPersistent,
		isSrdCondition = ConditionConstants.isSrdCondition(canonicalName),
	)
}

internal fun buildStatusChipModels(
	labels: Iterable<String>,
	isPersistent: Boolean,
): List<StatusChipModel> {
	return normalizedStatusLabels(labels).map { label ->
		statusChipModel(name = label, isPersistent = isPersistent)
	}
}

internal fun normalizedStatusLabels(labels: Iterable<String>): List<String> {
	return labels
		.asSequence()
		.map(String::trim)
		.filter(String::isNotBlank)
		.map(::canonicalStatusLabel)
		.distinctBy(String::lowercase)
		.sortedBy(String::lowercase)
		.toList()
}

internal fun canonicalStatusLabel(label: String): String = ConditionConstants.canonicalLabel(label)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatusChipFlowRow(
	statuses: List<StatusChipModel>,
	modifier: Modifier = Modifier,
	onStatusClick: ((StatusChipModel) -> Unit)? = null,
	onStatusRemove: ((StatusChipModel) -> Unit)? = null,
	trailingContent: (@Composable () -> Unit)? = null,
) {
	FlowRow(
		modifier = modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp),
	) {
		statuses.forEach { status ->
			StatusEffectChip(
				status = status,
				onClick = onStatusClick?.let { click -> { click(status) } },
				onRemove = onStatusRemove?.let { remove -> { remove(status) } },
			)
		}
		trailingContent?.invoke()
	}
}

@Composable
fun StatusEffectChip(
	status: StatusChipModel,
	modifier: Modifier = Modifier,
	onClick: (() -> Unit)? = null,
	onRemove: (() -> Unit)? = null,
) {
	val visual = statusChipVisual(status)
	if (onRemove != null) {
		InputChip(
			selected = true,
			onClick = onClick ?: {},
			label = { Text(statusChipDisplayText(status = status, persistentSuffix = stringResource(R.string.condition_persistent_chip_suffix))) },
			trailingIcon = {
				Icon(
					imageVector = Icons.Default.Close,
					contentDescription = null,
					modifier = Modifier
						.size(16.dp)
						.clickable(onClick = onRemove),
				)
			},
			modifier = modifier,
			colors = InputChipDefaults.inputChipColors(
				selectedContainerColor = visual.backgroundColor,
				selectedLabelColor = visual.labelColor,
				selectedTrailingIconColor = visual.labelColor,
			),
			border = BorderStroke(1.dp, visual.borderColor),
		)
	} else {
		FilterChip(
			selected = true,
			onClick = onClick ?: {},
			label = { Text(statusChipDisplayText(status = status, persistentSuffix = stringResource(R.string.condition_persistent_chip_suffix))) },
			modifier = modifier,
			colors = FilterChipDefaults.filterChipColors(
				selectedContainerColor = visual.backgroundColor,
				selectedLabelColor = visual.labelColor,
			),
			border = BorderStroke(1.dp, visual.borderColor),
		)
	}
}

internal fun statusChipDisplayText(
	status: StatusChipModel,
	persistentSuffix: String = "Persistent"
): String {
	return buildString {
		metadata.iconGlyph.takeIf(String::isNotBlank)?.let {
			append(it)
			append(' ')
		}
		append(status.name)
		append(status.durationText)
		if (status.isPersistent) {
			append(" • ")
			append(persistentSuffix)
		}
	}
}

private data class StatusChipVisual(
	val backgroundColor: Color,
	val borderColor: Color,
	val labelColor: Color,
)

@Composable
private fun statusChipVisual(status: StatusChipModel): StatusChipVisual {
	val metadata = ConditionConstants.metadataFor(status.name)
	val baseBackground = if (status.isSrdCondition) {
		metadata.color.copy(alpha = if (status.isPersistent) 0.34f else 0.24f)
	} else {
		MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (status.isPersistent) 0.95f else 0.8f)
	}
	val baseBorder = if (status.isSrdCondition) {
		metadata.borderColor.copy(alpha = if (status.isPersistent) 1f else 0.88f)
	} else {
		if (status.isPersistent) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
	}
	return StatusChipVisual(
		backgroundColor = baseBackground,
		borderColor = baseBorder,
		labelColor = MaterialTheme.colorScheme.onSurface,
	)
}


