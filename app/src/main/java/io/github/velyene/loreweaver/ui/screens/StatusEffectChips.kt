/*
 * FILE: StatusEffectChips.kt
 *
 * TABLE OF CONTENTS:
 * 1. Class: StatusChipModel
 * 2. Value: name
 * 3. Value: durationText
 * 4. Value: isPersistent
 * 5. Value: isInteractive
 * 6. Function: statusChipModel
 * 7. Value: canonicalName
 * 8. Function: persistentStatusChipModels
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.theme.PrimaryText

data class StatusChipModel(
	val name: String,
	val durationText: String = "",
	val isPersistent: Boolean = false,
	val isInteractive: Boolean = ConditionConstants.referenceTargetFor(name) != null
)

internal fun statusChipModel(
	name: String,
	durationText: String = "",
	isPersistent: Boolean = ConditionConstants.defaultPersistsAcrossEncounters(name),
	isInteractive: Boolean = ConditionConstants.referenceTargetFor(name) != null
): StatusChipModel {
	val canonicalName = canonicalStatusLabel(name)
	return StatusChipModel(
		name = canonicalName,
		durationText = durationText,
		isPersistent = isPersistent,
		isInteractive = isInteractive
	)
}

internal fun persistentStatusChipModels(labels: Iterable<String>): List<StatusChipModel> {
	return normalizedStatusLabels(labels).map { label ->
		statusChipModel(name = label, isPersistent = true)
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

internal fun canonicalStatusLabel(label: String): String =
	ConditionConstants.metadataFor(label.trim()).label

internal fun statusChipTag(label: String): String =
	"status_chip_${statusChipTagKey(label)}"

internal fun statusChipRemoveButtonTag(label: String): String =
	"status_chip_remove_${statusChipTagKey(label)}"

private fun statusChipTagKey(label: String): String = canonicalStatusLabel(label)
	.lowercase()
	.replace(Regex("[^a-z0-9]+"), "_")
	.trim('_')

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatusChipFlowRow(
	statuses: List<StatusChipModel>,
	modifier: Modifier = Modifier,
	onStatusClick: ((StatusChipModel) -> Unit)? = null,
	onStatusRemove: ((StatusChipModel) -> Unit)? = null,
	trailingContent: (@Composable () -> Unit)? = null
) {
	FlowRow(
		modifier = modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		statuses.forEach { status ->
			val chipClick = if (status.isInteractive) {
				onStatusClick?.let { click -> { click(status) } }
			} else {
				null
			}
			StatusEffectChip(
				status = status,
				onClick = chipClick,
				onRemove = onStatusRemove?.let { remove -> { remove(status) } }
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
	onRemove: (() -> Unit)? = null
) {
	val visual = statusChipVisual(status)
	val isClickable = onClick != null && status.isInteractive
	val persistentSuffix = stringResource(R.string.condition_persistent_chip_suffix)
	val referenceAvailableDescription = stringResource(R.string.status_chip_reference_available)
	val statusOnlyDescription = stringResource(R.string.status_chip_status_only)
	val chipLabel = statusChipDisplayLabel(status, persistentSuffix)
	val announcement = statusChipAnnouncement(
		status = status,
		persistentSuffix = persistentSuffix,
		referenceAvailableDescription = referenceAvailableDescription,
		statusOnlyDescription = statusOnlyDescription
	)
	val stateDescription = if (isClickable) {
		referenceAvailableDescription
	} else {
		statusOnlyDescription
	}
	val removeConditionDescription = buildString {
		append(stringResource(R.string.remove_condition_desc))
		append(' ')
		append(status.name)
	}

	FilterChip(
		selected = true,
		onClick = onClick ?: {},
		modifier = modifier
			.testTag(statusChipTag(status.name))
			.semantics {
				contentDescription = announcement
				this.stateDescription = stateDescription
			},
		label = {
			Text(
				text = chipLabel,
				fontSize = 11.sp,
				color = if (isClickable) PrimaryText else PrimaryText.copy(alpha = 0.72f)
			)
		},
		trailingIcon = onRemove?.let {
			{
				IconButton(
					onClick = it,
					modifier = Modifier
						.size(16.dp)
						.testTag(statusChipRemoveButtonTag(status.name))
				) {
					Icon(
						imageVector = Icons.Default.Close,
						contentDescription = removeConditionDescription,
						modifier = Modifier.size(12.dp)
					)
				}
			}
		},
		border = BorderStroke(
			width = 1.dp,
			color = if (isClickable) visual.borderColor else visual.borderColor.copy(alpha = 0.6f)
		),
		colors = FilterChipDefaults.filterChipColors(
			selectedContainerColor = if (isClickable) {
				visual.backgroundColor
			} else {
				visual.backgroundColor.copy(alpha = visual.backgroundColor.alpha * 0.72f)
			},
			selectedLabelColor = if (isClickable) PrimaryText else PrimaryText.copy(alpha = 0.72f),
			selectedTrailingIconColor = PrimaryText
		)
	)
}

@Composable
private fun statusChipLabel(status: StatusChipModel): String {
	return statusChipDisplayLabel(
		status = status,
		persistentSuffix = stringResource(R.string.condition_persistent_chip_suffix)
	)
}

private fun statusChipDisplayLabel(status: StatusChipModel, persistentSuffix: String): String {
	val metadata = ConditionConstants.metadataFor(status.name)
	return buildString {
		metadata.iconGlyph?.takeIf(String::isNotBlank)?.let {
			append(it)
			append(' ')
		}
		append(status.name)
		if (status.isPersistent) {
			append(' ')
			append(persistentSuffix)
		} else if (status.durationText.isNotBlank()) {
			append(status.durationText)
		}
	}
}

internal fun statusChipAnnouncement(
	status: StatusChipModel,
	persistentSuffix: String,
	referenceAvailableDescription: String,
	statusOnlyDescription: String
): String {
	val chipLabel = statusChipDisplayLabel(status, persistentSuffix)
	val suffix = if (status.isInteractive) {
		referenceAvailableDescription
	} else {
		statusOnlyDescription
	}
	return "$chipLabel, $suffix"
}

private data class StatusChipVisual(
	val backgroundColor: Color,
	val borderColor: Color
)

private fun statusChipVisual(status: StatusChipModel): StatusChipVisual {
	val metadata = ConditionConstants.metadataFor(status.name)
	val borderColor = if (status.isPersistent && !metadata.persistsAcrossEncounters) {
		metadata.borderColor.copy(alpha = 0.9f)
	} else {
		metadata.borderColor
	}
	return StatusChipVisual(
		backgroundColor = metadata.color.copy(
			alpha = if (metadata.persistsAcrossEncounters || status.isPersistent) 0.24f else 0.18f
		),
		borderColor = borderColor
	)
}
