package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.viewmodels.SessionSummaryUiModel

@Composable
internal fun ActionButtonColumn(
	summary: SessionSummaryUiModel,
	onDone: () -> Unit,
	onOpenAdventureLog: () -> Unit,
	onContinueCampaign: (String) -> Unit,
	onStartAnotherEncounter: () -> Unit,
	onOpenSessionHistory: () -> Unit
) {
	Button(
		onClick = onDone,
		modifier = Modifier
			.fillMaxWidth()
			.height(56.dp),
		colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
	) {
		Text(
			text = stringResource(R.string.session_summary_done_button),
			color = MaterialTheme.colorScheme.onPrimary,
			fontWeight = FontWeight.Bold
		)
	}

	Spacer(modifier = Modifier.height(12.dp))

	if (summary.campaignId != null) {
		OutlinedButton(
			onClick = { onContinueCampaign(summary.campaignId) },
			modifier = Modifier
				.fillMaxWidth()
				.height(48.dp)
		) {
			Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null)
			Spacer(modifier = Modifier.width(8.dp))
			Text(stringResource(R.string.session_summary_continue_campaign_button))
		}

		Spacer(modifier = Modifier.height(12.dp))
	}

	OutlinedButton(
		onClick = onStartAnotherEncounter,
		modifier = Modifier
			.fillMaxWidth()
			.height(48.dp)
	) {
		Icon(Icons.Default.Update, contentDescription = null)
		Spacer(modifier = Modifier.width(8.dp))
		Text(stringResource(R.string.session_summary_start_another_encounter_button))
	}

	Spacer(modifier = Modifier.height(12.dp))

	OutlinedButton(
		onClick = onOpenSessionHistory,
		modifier = Modifier
			.fillMaxWidth()
			.height(48.dp)
	) {
		Icon(Icons.Default.Star, contentDescription = null)
		Spacer(modifier = Modifier.width(8.dp))
		Text(stringResource(R.string.session_summary_view_history_button))
	}

	Spacer(modifier = Modifier.height(12.dp))

	OutlinedButton(
		onClick = onOpenAdventureLog,
		modifier = Modifier
			.fillMaxWidth()
			.height(48.dp)
	) {
		Text(stringResource(R.string.session_summary_open_adventure_log))
	}
}

