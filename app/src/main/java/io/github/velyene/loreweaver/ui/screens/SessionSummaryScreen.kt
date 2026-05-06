/*
 * FILE: SessionSummaryScreen.kt
 *
 * TABLE OF CONTENTS:
 * 1. Function: SessionSummaryScreen
 * 2. Value: uiState
 * 3. Function: SessionSummaryScreenRoute
 * 4. Value: summary
 * 5. Function: SessionSummaryContent
 * 6. Value: scrollState
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import io.github.velyene.loreweaver.ui.viewmodels.SessionSummaryUiModel
import io.github.velyene.loreweaver.ui.viewmodels.SessionSummaryUiState
import io.github.velyene.loreweaver.ui.viewmodels.SessionSummaryViewModel

enum class SessionSummaryMode {
	STANDARD,
	ENCOUNTER_COMPLETION,
}

@Composable
fun SessionSummaryScreen(
	sessionId: String? = null,
	summaryMode: SessionSummaryMode = SessionSummaryMode.STANDARD,
	onSaveEncounter: (String) -> Unit = {},
	onDone: () -> Unit,
	onOpenAdventureLog: () -> Unit = {},
	onContinueCampaign: (String) -> Unit = {},
	onOpenSessionDetail: (String) -> Unit = {},
	onStartAnotherEncounter: () -> Unit = {},
	onOpenSessionHistory: () -> Unit = {},
	viewModel: SessionSummaryViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	LaunchedEffect(sessionId) {
		viewModel.refreshSummary(sessionId = sessionId)
	}

	SessionSummaryScreenRoute(
		uiState = uiState,
		summaryMode = summaryMode,
		onSaveEncounter = onSaveEncounter,
		onDone = onDone,
		onOpenAdventureLog = onOpenAdventureLog,
		onContinueCampaign = onContinueCampaign,
		onOpenSessionDetail = onOpenSessionDetail,
		onStartAnotherEncounter = onStartAnotherEncounter,
		onOpenSessionHistory = onOpenSessionHistory,
	)
}


@Composable
internal fun SessionSummaryScreenRoute(
	uiState: SessionSummaryUiState,
	summaryMode: SessionSummaryMode = SessionSummaryMode.STANDARD,
	onSaveEncounter: (String) -> Unit = {},
	onDone: () -> Unit,
	onOpenAdventureLog: () -> Unit,
	onContinueCampaign: (String) -> Unit,
	onOpenSessionDetail: (String) -> Unit,
	onStartAnotherEncounter: () -> Unit,
	onOpenSessionHistory: () -> Unit,
) {
	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.background)
	) {
		val summary = uiState.summary
		when {
			uiState.isLoading -> {
				CenteredLoadingState(modifier = Modifier.fillMaxSize())
			}

			summary != null -> {
				SessionSummaryContent(
					summary = summary,
					mode = summaryMode,
					onSaveEncounter = onSaveEncounter,
					onDone = onDone,
					onOpenAdventureLog = onOpenAdventureLog,
					onContinueCampaign = onContinueCampaign,
					onOpenSessionDetail = onOpenSessionDetail,
					onStartAnotherEncounter = onStartAnotherEncounter,
					onOpenSessionHistory = onOpenSessionHistory
				)
			}

			else -> {
				CenteredEmptyState(
					message = uiState.error
						?: if (uiState.onRetry != null) {
							stringResource(R.string.session_summary_load_failed_message)
						} else {
							stringResource(R.string.session_summary_empty_message)
						},
					actionLabel = uiState.onRetry?.let { stringResource(R.string.retry_action) },
					onAction = uiState.onRetry,
					modifier = Modifier.fillMaxSize()
				)
			}
		}
	}
}

@Composable
internal fun SessionSummaryContent(
	summary: SessionSummaryUiModel,
	mode: SessionSummaryMode = SessionSummaryMode.STANDARD,
	onSaveEncounter: (String) -> Unit = {},
	onDone: () -> Unit,
	onOpenAdventureLog: () -> Unit,
	onContinueCampaign: (String) -> Unit,
	onOpenSessionDetail: (String) -> Unit,
	onStartAnotherEncounter: () -> Unit,
	onOpenSessionHistory: () -> Unit
) {
	val scrollState = rememberScrollState()

	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(scrollState)
			.visibleVerticalScrollbar(scrollState)
			.fillMaxWidth()
			.padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Badge(containerColor = MaterialTheme.colorScheme.primary) {
			Text(
				text = when (mode) {
					SessionSummaryMode.STANDARD -> stringResource(R.string.session_summary_badge)
					SessionSummaryMode.ENCOUNTER_COMPLETION -> stringResource(R.string.encounter_summary_badge)
				},
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
			textAlign = TextAlign.Center,
			modifier = Modifier.semantics { heading() }
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
			title = when (mode) {
				SessionSummaryMode.STANDARD -> stringResource(R.string.session_summary_surviving_players_title)
				SessionSummaryMode.ENCOUNTER_COMPLETION -> stringResource(R.string.encounter_summary_surviving_party_title)
			}
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

		if (
			summary.rewards.experiencePoints > 0 ||
			summary.rewards.experiencePerParticipant > 0 ||
			summary.rewards.currencyReward != null ||
			summary.rewards.currencyPerParticipant != null ||
			summary.rewards.itemRewards.isNotEmpty() ||
			summary.rewards.equipmentRewards.isNotEmpty() ||
			summary.rewards.storyRewards.isNotEmpty() ||
			summary.rewards.rewardLog.isNotEmpty()
		) {
			Spacer(modifier = Modifier.height(12.dp))
			SummarySectionCard(
				title = stringResource(R.string.session_summary_rewards_title)
			) {
				RewardSummaryCard(rewards = summary.rewards)
			}
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
			title = when (mode) {
				SessionSummaryMode.STANDARD -> stringResource(R.string.session_combat_log_title)
				SessionSummaryMode.ENCOUNTER_COMPLETION -> stringResource(R.string.encounter_summary_log_title)
			}
		) {
			LogSummaryList(items = summary.logSummary)
		}

		Spacer(modifier = Modifier.height(24.dp))

		ActionButtonColumn(
			summary = summary,
			mode = mode,
			onSaveEncounter = onSaveEncounter,
			onDone = onDone,
			onOpenAdventureLog = onOpenAdventureLog,
			onContinueCampaign = onContinueCampaign,
			onOpenSessionDetail = onOpenSessionDetail,
			onStartAnotherEncounter = onStartAnotherEncounter,
			onOpenSessionHistory = onOpenSessionHistory
		)
	}
}

