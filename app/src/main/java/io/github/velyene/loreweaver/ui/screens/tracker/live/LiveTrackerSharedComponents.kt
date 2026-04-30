/*
 * FILE: LiveTrackerSharedComponents.kt
 *
 * TABLE OF CONTENTS:
 * 1. Resource chips and quick HP controls
 * 2. Summary stat row support
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.QUICK_HP_BUTTON_HEIGHT_DP

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ResourceLinesSection(resourceLines: List<String>) {
	FlowRow(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(6.dp),
		verticalArrangement = Arrangement.spacedBy(6.dp)
	) {
		resourceLines.forEach { line ->
			Box(
				modifier = Modifier
					.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
					.padding(horizontal = 10.dp, vertical = 6.dp)
			) {
				Text(
					text = line,
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onPrimaryContainer
				)
			}
		}
	}
}

@Composable
internal fun QuickHpControls(
	characterId: String,
	onHpChange: (characterId: String, delta: Int) -> Unit
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(4.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = stringResource(R.string.hp_label),
			fontSize = 12.sp,
			modifier = Modifier.padding(end = 4.dp)
		)
		listOf(-5, -1, 1, 5).forEach { delta ->
			OutlinedButton(
				onClick = { onHpChange(characterId, delta) },
				modifier = Modifier
					.weight(1f)
					.height(QUICK_HP_BUTTON_HEIGHT_DP.dp),
				contentPadding = PaddingValues(0.dp)
			) {
				Text(if (delta > 0) "+$delta" else delta.toString(), fontSize = 12.sp)
			}
		}
	}
}
@Composable
internal fun SummaryStatRow(primaryLabel: String, secondaryLabel: String) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		SummaryStatCard(
			label = primaryLabel,
			modifier = Modifier.weight(1f)
		)
		SummaryStatCard(
			label = secondaryLabel,
			modifier = Modifier.weight(1f)
		)
	}
}

@Composable
private fun SummaryStatCard(label: String, modifier: Modifier = Modifier) {
	Card(
		modifier = modifier,
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.18f))
	) {
		Text(
			text = label,
			modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
			style = MaterialTheme.typography.bodyMedium,
			fontWeight = FontWeight.Medium,
			color = MaterialTheme.colorScheme.onPrimaryContainer
		)
	}
}


