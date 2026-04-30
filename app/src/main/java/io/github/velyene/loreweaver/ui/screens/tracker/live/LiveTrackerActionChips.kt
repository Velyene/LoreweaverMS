/*
 * FILE: LiveTrackerActionChips.kt
 *
 * TABLE OF CONTENTS:
 * 1. Participant action chips
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ParticipantActionChips(
	actionLabels: List<String>,
	onAction: (String) -> Unit
) {
	FlowRow(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		actionLabels.forEach { actionLabel ->
			ActionChip(
				label = actionLabel,
				onClick = { onAction(actionLabel) }
			)
		}
	}
}

@Composable
private fun ActionChip(label: String, onClick: () -> Unit) {
	Box(
		modifier = Modifier
			.background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(20.dp))
			.clickable(onClick = onClick)
			.semantics { contentDescription = label }
			.padding(horizontal = 14.dp, vertical = 8.dp)
	) {
		Text(
			text = label,
			color = MaterialTheme.colorScheme.onSecondaryContainer,
			fontSize = 12.sp,
			fontWeight = FontWeight.Medium
		)
	}
}

