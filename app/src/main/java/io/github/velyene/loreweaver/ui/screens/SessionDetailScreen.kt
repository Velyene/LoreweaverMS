/*
 * FILE: SessionDetailScreen.kt
 *
 * TABLE OF CONTENTS:
 * 1. Function: SessionDetailScreen
 * 2. Value: uiState
 * 3. Function: SessionDetailScreenRoute
 * 4. Value: dateFormat
 * 5. Value: scrollState
 * 6. Function: SessionDetailContent
 * 7. Function: SessionDetailSection
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.ui.util.asString
import io.github.velyene.loreweaver.ui.viewmodels.EncounterResult
import io.github.velyene.loreweaver.ui.viewmodels.SessionDetailUiState
import io.github.velyene.loreweaver.ui.viewmodels.SessionDetailViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(
	sessionId: String,
	onBack: () -> Unit,
	onOpenCampaign: (String) -> Unit = {},
	viewModel: SessionDetailViewModel = hiltViewModel(),
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	SessionDetailScreenRoute(
		sessionId = sessionId,
		uiState = uiState,
		onBack = onBack,
		onLoadSession = viewModel::loadSession,
		onOpenCampaign = onOpenCampaign,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SessionDetailScreenRoute(
	sessionId: String,
	uiState: SessionDetailUiState,
	onBack: () -> Unit,
	onLoadSession: (String) -> Unit,
	onOpenCampaign: (String) -> Unit,
) {
	val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }
	val scrollState = rememberScrollState()
	val errorMessage = uiState.error?.asString()

	LaunchedEffect(sessionId) {
		onLoadSession(sessionId)
	}

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(stringResource(R.string.session_detail_title)) },
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							imageVector = Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = stringResource(R.string.back_button),
						)
					}
				},
			)
		},
	) { padding ->
		when {
			uiState.isLoading -> {
				CenteredLoadingState(
					modifier = Modifier
						.fillMaxSize()
						.padding(padding),
				)
			}

			uiState.session == null -> {
				CenteredEmptyState(
					message = errorMessage ?: stringResource(R.string.session_detail_not_found_message),
					actionLabel = uiState.onRetry?.let { stringResource(R.string.retry_action) },
					onAction = uiState.onRetry,
					modifier = Modifier
						.fillMaxSize()
						.padding(padding),
				)
			}

			else -> {
				SessionDetailContent(
					session = uiState.session,
					encounterName = uiState.encounterName ?: uiState.session.title,
					campaignId = uiState.campaignId,
					campaignTitle = uiState.campaignTitle,
					result = uiState.summary?.result,
					rewards = uiState.summary?.rewards,
					notesSummary = uiState.summary?.notesSummary.orEmpty(),
					onOpenCampaign = onOpenCampaign,
					dateFormat = dateFormat,
					modifier = Modifier
						.fillMaxSize()
						.padding(padding)
						.verticalScroll(scrollState)
						.visibleVerticalScrollbar(scrollState),
				)
			}
		}
	}
}

@Composable
private fun SessionDetailContent(
	session: SessionRecord,
	encounterName: String,
	campaignId: String?,
	campaignTitle: String?,
	result: EncounterResult?,
	rewards: io.github.velyene.loreweaver.ui.viewmodels.EncounterRewardSummary?,
	notesSummary: String,
	onOpenCampaign: (String) -> Unit,
	dateFormat: SimpleDateFormat,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier.padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		Badge(containerColor = MaterialTheme.colorScheme.primary) {
			Text(
				text = stringResource(R.string.session_detail_badge),
				color = MaterialTheme.colorScheme.onPrimary,
				modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
				fontWeight = FontWeight.Bold,
			)
		}

		Spacer(modifier = Modifier.height(20.dp))

		Text(
			text = encounterName,
			style = MaterialTheme.typography.headlineSmall,
			fontWeight = FontWeight.Bold,
			color = MaterialTheme.colorScheme.onBackground,
			modifier = Modifier.semantics { heading() },
		)

		campaignTitle?.let {
			Spacer(modifier = Modifier.height(6.dp))
			Text(
				text = it,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		}

		Spacer(modifier = Modifier.height(8.dp))
		Text(
			text = stringResource(R.string.session_detail_saved_at_label, dateFormat.format(Date(session.date))),
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)

		campaignId?.let {
			Spacer(modifier = Modifier.height(16.dp))
			Button(
				onClick = { onOpenCampaign(it) },
				modifier = Modifier.fillMaxWidth(),
			) {
				Text(stringResource(R.string.session_detail_open_campaign_button))
			}
		}

		result?.let {
			Spacer(modifier = Modifier.height(16.dp))
			Text(
				text = encounterResultLabel(it),
				style = MaterialTheme.typography.titleLarge,
				fontWeight = FontWeight.Bold,
				color = encounterResultColor(it),
			)
		}

		rewards?.takeIf {
			it.experiencePoints > 0 ||
				it.experiencePerParticipant > 0 ||
				it.currencyReward != null ||
				it.currencyPerParticipant != null ||
				it.itemRewards.isNotEmpty() ||
				it.equipmentRewards.isNotEmpty() ||
				it.rewardLog.isNotEmpty()
		}?.let {
			Spacer(modifier = Modifier.height(16.dp))
			SessionDetailSection(
				title = stringResource(R.string.session_summary_rewards_title),
			) {
				RewardSummaryCard(rewards = it)
			}
		}

		if (notesSummary.isNotBlank()) {
			Spacer(modifier = Modifier.height(16.dp))
			SessionDetailSection(
				title = stringResource(R.string.encounter_notes_title),
			) {
				Text(
					text = notesSummary,
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurface,
				)
			}
		}

		Spacer(modifier = Modifier.height(16.dp))
		SessionDetailSection(
			title = stringResource(R.string.session_end_state_title),
		) {
			session.snapshot?.let { snapshot ->
				Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp)) {
					Text(
						text = stringResource(R.string.session_final_round, snapshot.currentRound),
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurface,
					)
					snapshot.combatants.forEach { combatant ->
						Text(
							text = stringResource(
								R.string.session_combatant_hp_summary,
								combatant.name,
								combatant.currentHp,
								combatant.maxHp,
							),
							style = MaterialTheme.typography.bodyMedium,
							color = MaterialTheme.colorScheme.onSurface,
						)
					}
				}
			} ?: Text(
				text = stringResource(R.string.session_detail_snapshot_unavailable),
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		}

		Spacer(modifier = Modifier.height(16.dp))
		SessionDetailSection(
			title = stringResource(R.string.session_combat_log_title),
		) {
			if (session.log.isEmpty()) {
				Text(
					text = stringResource(R.string.session_summary_log_empty_message),
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			} else {
				Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp)) {
					session.log.forEach { entry ->
						Text(
							text = stringResource(R.string.combat_log_bullet, entry),
							style = MaterialTheme.typography.bodyMedium,
							color = MaterialTheme.colorScheme.onSurface,
						)
					}
				}
			}
		}
	}
}

@Composable
private fun SessionDetailSection(
	title: String,
	content: @Composable () -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
			.padding(16.dp),
	) {
		Text(
			text = title,
			style = MaterialTheme.typography.labelLarge,
			fontWeight = FontWeight.Bold,
			color = MaterialTheme.colorScheme.onSurface,
			modifier = Modifier.semantics { heading() },
		)
		Spacer(modifier = Modifier.height(8.dp))
		content()
	}
}


