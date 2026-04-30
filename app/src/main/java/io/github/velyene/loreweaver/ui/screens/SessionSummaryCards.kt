package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.viewmodels.EncounterResult
import io.github.velyene.loreweaver.ui.viewmodels.SessionSummaryUiModel

@Composable
internal fun OutcomeHeroCard(summary: SessionSummaryUiModel) {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
			.padding(20.dp),
		contentAlignment = Alignment.Center
	) {
		Column(horizontalAlignment = Alignment.CenterHorizontally) {
			Text(
				text = resultLabel(summary.result),
				style = MaterialTheme.typography.headlineMedium,
				fontWeight = FontWeight.Bold,
				color = resultColor(summary.result)
			)
			Spacer(modifier = Modifier.height(6.dp))
			Text(
				text = resultSubtitle(summary.result),
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				textAlign = TextAlign.Center
			)
		}
	}
}

@Composable
internal fun SummaryStatRow(summary: SessionSummaryUiModel) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		SummaryItemCard(
			title = stringResource(R.string.session_summary_rounds_title),
			subtitle = stringResource(R.string.session_final_round, summary.totalRounds),
			icon = Icons.Default.Flag,
			modifier = Modifier.weight(1f)
		)
		SummaryItemCard(
			title = stringResource(R.string.session_summary_survivors_count_title),
			subtitle = summary.survivingPlayers.size.toString(),
			icon = Icons.Default.Groups,
			modifier = Modifier.weight(1f)
		)
		SummaryItemCard(
			title = stringResource(R.string.session_summary_saved_title),
			subtitle = stringResource(R.string.session_summary_saved_subtitle),
			icon = Icons.Default.SdStorage,
			modifier = Modifier.weight(1f)
		)
	}
}

@Composable
private fun SummaryItemCard(
	title: String,
	subtitle: String,
	icon: ImageVector,
	modifier: Modifier = Modifier
) {
	Column(
		modifier = modifier
			.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
			.padding(12.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
		Spacer(modifier = Modifier.height(8.dp))
		Text(
			text = title,
			style = MaterialTheme.typography.labelMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			textAlign = TextAlign.Center
		)
		Spacer(modifier = Modifier.height(4.dp))
		Text(
			text = subtitle,
			style = MaterialTheme.typography.titleSmall,
			fontWeight = FontWeight.Bold,
			color = MaterialTheme.colorScheme.onSurface,
			textAlign = TextAlign.Center
		)
	}
}

@Composable
private fun resultLabel(result: EncounterResult): String {
	return when (result) {
		EncounterResult.VICTORY -> stringResource(R.string.session_summary_result_victory)
		EncounterResult.DEFEAT -> stringResource(R.string.session_summary_result_defeat)
		EncounterResult.ENDED_EARLY -> stringResource(R.string.session_summary_result_ended_early)
	}
}

@Composable
private fun resultSubtitle(result: EncounterResult): String {
	return when (result) {
		EncounterResult.VICTORY -> stringResource(R.string.session_summary_result_victory_subtitle)
		EncounterResult.DEFEAT -> stringResource(R.string.session_summary_result_defeat_subtitle)
		EncounterResult.ENDED_EARLY -> stringResource(R.string.session_summary_result_ended_early_subtitle)
	}
}

@Composable
private fun resultColor(result: EncounterResult): Color = when (result) {
	EncounterResult.VICTORY -> MaterialTheme.colorScheme.primary
	EncounterResult.DEFEAT -> MaterialTheme.colorScheme.error
	EncounterResult.ENDED_EARLY -> MaterialTheme.colorScheme.tertiary
}

