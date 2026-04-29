/*
 * FILE: SessionSummaryScreen.kt
 *
 * TABLE OF CONTENTS:
 * 1. Session summary screen entry point
 * 2. Summary cards and stat badges
 * 3. Primary and secondary action rows
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.viewmodels.EncounterResult
import io.github.velyene.loreweaver.ui.viewmodels.PersistentStatusSummary
import io.github.velyene.loreweaver.ui.viewmodels.SessionParticipantSummary
import io.github.velyene.loreweaver.ui.viewmodels.SessionSummaryUiModel
import io.github.velyene.loreweaver.ui.viewmodels.SessionSummaryViewModel

@Composable
fun SessionSummaryScreen(
	onDone: () -> Unit,
	onOpenAdventureLog: () -> Unit = {},
	onContinueCampaign: (String) -> Unit = {},
	onStartAnotherEncounter: () -> Unit = {},
	onOpenSessionHistory: () -> Unit = {},
	viewModel: SessionSummaryViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val scrollState = rememberScrollState()

	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.background)
			.verticalScroll(scrollState)
			.visibleVerticalScrollbar(scrollState)
	) {
		val summary = uiState.summary
		when {
			uiState.isLoading -> {
				CenteredLoadingState(modifier = Modifier.fillMaxSize())
			}

			summary != null -> {
				SessionSummaryContent(
					summary = summary,
					onDone = onDone,
					onOpenAdventureLog = onOpenAdventureLog,
					onContinueCampaign = onContinueCampaign,
					onStartAnotherEncounter = onStartAnotherEncounter,
					onOpenSessionHistory = onOpenSessionHistory
				)
			}

			else -> {
				CenteredEmptyState(
					message = uiState.error ?: stringResource(R.string.session_summary_empty_message),
					modifier = Modifier.fillMaxSize()
				)
			}
		}
	}
}

@Composable
private fun SessionSummaryContent(
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

@Composable
private fun OutcomeHeroCard(summary: SessionSummaryUiModel) {
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
private fun SummaryStatRow(summary: SessionSummaryUiModel) {
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
	icon: androidx.compose.ui.graphics.vector.ImageVector,
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
private fun SummarySectionCard(
	title: String,
	content: @Composable () -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
			.padding(16.dp)
	) {
		Text(
			text = title,
			style = MaterialTheme.typography.labelLarge,
			fontWeight = FontWeight.Bold,
			color = MaterialTheme.colorScheme.onSurface,
			modifier = Modifier.semantics { heading() }
		)
		Spacer(modifier = Modifier.height(8.dp))
		Box(modifier = Modifier.fillMaxWidth()) {
			content()
		}
	}
}

@Composable
private fun ParticipantSummaryList(items: List<SessionParticipantSummary>) {
	if (items.isEmpty()) {
		Text(
			text = stringResource(R.string.none_label),
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
		return
	}

	Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
		items.forEach { participant ->
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Column(modifier = Modifier.weight(1f)) {
					Text(
						text = participant.name,
						style = MaterialTheme.typography.bodyLarge,
						fontWeight = FontWeight.Medium,
						color = MaterialTheme.colorScheme.onSurface
					)
					Text(
						text = participant.hpLabel,
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
				Text(
					text = stringResource(R.string.combatant_initiative_summary, participant.initiative),
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
		}
	}
}

@Composable
private fun PersistentStatusList(items: List<PersistentStatusSummary>) {
	if (items.isEmpty()) {
		Text(
			text = stringResource(R.string.none_label),
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
		return
	}

	Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
		items.forEach { item ->
			Column(modifier = Modifier.fillMaxWidth()) {
				Text(
					text = item.participantName,
					style = MaterialTheme.typography.bodyLarge,
					fontWeight = FontWeight.Medium,
					color = MaterialTheme.colorScheme.onSurface
				)
				Spacer(modifier = Modifier.height(6.dp))
				StatusChipFlowRow(
					statuses = persistentStatusChipModels(item.conditions)
				)
			}
		}
	}
}

@Composable
private fun LogSummaryList(items: List<String>) {
	if (items.isEmpty()) {
		Text(
			text = stringResource(R.string.session_summary_log_empty_message),
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
		return
	}

	Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
		items.reversed().forEach { entry ->
			Text(
				text = stringResource(R.string.combat_log_bullet, entry),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
	}
}

@Composable
private fun ActionButtonColumn(
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
private fun resultColor(result: EncounterResult) = when (result) {
	EncounterResult.VICTORY -> MaterialTheme.colorScheme.primary
	EncounterResult.DEFEAT -> MaterialTheme.colorScheme.error
	EncounterResult.ENDED_EARLY -> MaterialTheme.colorScheme.tertiary
}
