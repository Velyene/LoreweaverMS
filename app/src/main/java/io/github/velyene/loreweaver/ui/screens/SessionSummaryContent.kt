/*
 * FILE: SessionSummaryContent.kt
 *
 * TABLE OF CONTENTS:
 * 1. Session summary content layout
 * 2. Section composition
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.viewmodels.SessionSummaryUiModel

@Composable
internal fun SessionSummaryContent(
	summary: SessionSummaryUiModel,
	onDone: () -> Unit,
	onOpenAdventureLog: () -> Unit,
	onContinueCampaign: (String) -> Unit,
	onStartAnotherEncounter: () -> Unit,
	onOpenSessionHistory: () -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Badge(containerColor = MaterialTheme.colorScheme.primary) {
			Text(
				stringResource(R.string.session_summary_badge),
				color = MaterialTheme.colorScheme.onPrimary,
				modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
				fontWeight = FontWeight.Bold
			)
		}

		Spacer(modifier = Modifier.height(24.dp))

		Text(
			text = summary.encounterName,
			style = MaterialTheme.typography.headlineSmall,
			fontWeight = FontWeight.Bold,
			color = MaterialTheme.colorScheme.onBackground,
			textAlign = TextAlign.Center
		)
		if (summary.campaignTitle != null) {
			Spacer(modifier = Modifier.height(4.dp))
			Text(
				text = summary.campaignTitle,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				textAlign = TextAlign.Center
			)
		}

		Spacer(modifier = Modifier.height(20.dp))

		OutcomeHeroCard(summary = summary)

		Spacer(modifier = Modifier.height(16.dp))

		SummaryStatRow(summary = summary)

		Spacer(modifier = Modifier.height(16.dp))

		SummarySectionCard(
			title = stringResource(R.string.session_summary_surviving_players_title)
		) {
			ParticipantSummaryList(items = summary.survivingPlayers)
		}

		Spacer(modifier = Modifier.height(12.dp))

		SummarySectionCard(
			title = stringResource(R.string.session_summary_defeated_enemies_title)
		) {
			ParticipantSummaryList(items = summary.defeatedEnemies)
		}

		Spacer(modifier = Modifier.height(12.dp))

		SummarySectionCard(
			title = stringResource(R.string.session_summary_persistent_statuses_title)
		) {
			PersistentStatusList(items = summary.persistentStatuses)
		}

		if (summary.notesSummary.isNotBlank()) {
			Spacer(modifier = Modifier.height(12.dp))
			SummarySectionCard(
				title = stringResource(R.string.encounter_notes_title)
			) {
				Text(
					text = summary.notesSummary,
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurface
				)
			}
		}

		Spacer(modifier = Modifier.height(12.dp))

		SummarySectionCard(
			title = stringResource(R.string.session_combat_log_title)
		) {
			LogSummaryList(items = summary.logSummary)
		}

		Spacer(modifier = Modifier.height(24.dp))

		ActionButtonColumn(
			summary = summary,
			onDone = onDone,
			onOpenAdventureLog = onOpenAdventureLog,
			onContinueCampaign = onContinueCampaign,
			onStartAnotherEncounter = onStartAnotherEncounter,
			onOpenSessionHistory = onOpenSessionHistory
		)
	}
}


