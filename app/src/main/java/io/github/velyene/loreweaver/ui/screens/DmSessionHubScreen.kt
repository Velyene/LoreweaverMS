/*
 * FILE: DmSessionHubScreen.kt
 *
 * TABLE OF CONTENTS:
 * 1. DM Session Hub Route
 * 2. Hub Content
 * 3. Encounter Snapshot and Status Cards
 * 4. Action Grid and Quick Tips
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Fort
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.theme.AntiqueGold
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal
import io.github.velyene.loreweaver.ui.theme.MutedText
import io.github.velyene.loreweaver.ui.viewmodels.CampaignListUiState
import io.github.velyene.loreweaver.ui.viewmodels.CampaignListViewModel
import java.text.SimpleDateFormat
import java.util.Locale

internal data class DmSessionHubActions(
	val onBack: () -> Unit,
	val onNewEncounter: () -> Unit,
	val onResumeActiveEncounter: () -> Unit,
	val onOpenSavedEncounters: () -> Unit,
	val onManageCharacters: () -> Unit,
	val onEnemyLibrary: () -> Unit,
	val onSessionLogNotes: () -> Unit,
	val onCampaigns: () -> Unit,
	val onAboutHelp: () -> Unit,
)

@Composable
fun DmSessionHubScreen(
	onBack: () -> Unit,
	onNewEncounter: () -> Unit,
	onResumeActiveEncounter: () -> Unit,
	onOpenSavedEncounters: () -> Unit,
	onManageCharacters: () -> Unit,
	onEnemyLibrary: () -> Unit,
	onSessionLogNotes: () -> Unit,
	onCampaigns: () -> Unit,
	onAboutHelp: () -> Unit,
	viewModel: CampaignListViewModel = hiltViewModel(),
) {
	val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
	val snackbarHostState = remember { SnackbarHostState() }
	val retryActionLabel = stringResource(R.string.retry_action)
	val lifecycleOwner = LocalLifecycleOwner.current

	DisposableEffect(lifecycleOwner, viewModel) {
		val observer = LifecycleEventObserver { _, event ->
			if (event == Lifecycle.Event.ON_RESUME) {
				viewModel.refreshActiveEncounter()
			}
		}
		lifecycleOwner.lifecycle.addObserver(observer)
		onDispose {
			lifecycleOwner.lifecycle.removeObserver(observer)
		}
	}

	ErrorSnackbarHandler(
		error = uiState.error,
		onRetry = uiState.onRetry,
		snackbarHostState = snackbarHostState,
		onClear = viewModel::clearError,
		retryActionLabel = retryActionLabel,
	)

	DmSessionHubContent(
		uiState = uiState,
		actions = DmSessionHubActions(
			onBack = onBack,
			onNewEncounter = onNewEncounter,
			onResumeActiveEncounter = onResumeActiveEncounter,
			onOpenSavedEncounters = onOpenSavedEncounters,
			onManageCharacters = onManageCharacters,
			onEnemyLibrary = onEnemyLibrary,
			onSessionLogNotes = onSessionLogNotes,
			onCampaigns = onCampaigns,
			onAboutHelp = onAboutHelp,
		),
		snackbarHostState = snackbarHostState,
	)
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun DmSessionHubContent(
	uiState: CampaignListUiState,
	actions: DmSessionHubActions,
	snackbarHostState: SnackbarHostState,
) {
	val scrollState = rememberScrollState()
	val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }
	val latestSavedEncounter = remember(uiState.sessions) {
		uiState.sessions
			.asSequence()
			.filterNot { it.isCompleted }
			.maxByOrNull { it.date }
	}

	Scaffold(
		snackbarHost = { SnackbarHost(snackbarHostState) },
		topBar = {
			TopAppBar(
				title = { Text(stringResource(R.string.dm_session_hub_title)) },
				navigationIcon = {
						IconButton(onClick = actions.onBack) {
						Icon(
							imageVector = Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = stringResource(R.string.back_button),
						)
					}
				},
			)
		},
	) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
				.padding(horizontal = 16.dp)
				.verticalScroll(scrollState),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Spacer(modifier = Modifier.height(4.dp))
			Text(
				text = stringResource(R.string.app_name),
				style = MaterialTheme.typography.headlineSmall,
				fontWeight = FontWeight.Bold,
			)
			Text(
				text = stringResource(R.string.home_brand_subtitle),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
			Text(
				text = stringResource(R.string.dm_session_hub_subtitle),
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
			ActiveEncounterSnapshotCard(
				uiState = uiState,
				onResumeEncounter = actions.onResumeActiveEncounter,
			)
			HomeSectionHeader(stringResource(R.string.quick_actions))
			DmHubActionGrid(
				onNewEncounter = actions.onNewEncounter,
				onOpenSavedEncounters = actions.onOpenSavedEncounters,
				onManageCharacters = actions.onManageCharacters,
				onEnemyLibrary = actions.onEnemyLibrary,
				onSessionLogNotes = actions.onSessionLogNotes,
				onCampaigns = actions.onCampaigns,
				onAboutHelp = actions.onAboutHelp,
			)
			HomeSectionHeader(stringResource(R.string.dm_recent_encounter_title))
			RecentEncounterSummaryCard(
				recentSession = latestSavedEncounter,
				dateLabel = latestSavedEncounter?.let { session ->
					stringResource(R.string.dm_recent_encounter_saved_at, dateFormat.format(session.date))
				},
			)
			HomeSectionHeader(stringResource(R.string.dm_quick_tips_title))
			QuickTipsCard()
			Spacer(modifier = Modifier.height(16.dp))
		}
	}
}

@Composable
private fun ActiveEncounterSnapshotCard(
	uiState: CampaignListUiState,
	onResumeEncounter: () -> Unit,
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Text(
				text = stringResource(R.string.dm_active_snapshot_title),
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold,
				color = ArcaneTeal,
			)
			if (uiState.hasActiveEncounter) {
				Text(
					text = uiState.activeEncounterName ?: stringResource(R.string.dm_operator_no_active_encounter),
					style = MaterialTheme.typography.headlineSmall,
					fontWeight = FontWeight.Bold,
				)
				FlowRow(
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					verticalArrangement = Arrangement.spacedBy(8.dp),
				) {
					SnapshotBadge(stringResource(R.string.round_counter, uiState.activeEncounterRound ?: 1))
					SnapshotBadge(
						stringResource(
							R.string.dm_active_snapshot_roster_count,
							uiState.activeEncounterCombatantCount,
						),
					)
					uiState.activeEncounterTurnName?.let { turnName ->
						SnapshotBadge(
							stringResource(R.string.dm_active_snapshot_turn_chip, turnName),
						)
					}
				}
				OperatorStatusLine(
					label = stringResource(R.string.dm_operator_question_changed),
					value = uiState.activeEncounterLatestChange ?: stringResource(R.string.dm_operator_changed_none),
				)
				OperatorStatusLine(
					label = stringResource(R.string.dm_operator_question_attention),
					value = stringResource(
						R.string.dm_operator_attention_active,
						uiState.activeEncounterRound ?: 1,
					),
				)
				OperatorStatusLine(
					label = stringResource(R.string.dm_operator_question_next),
					value = uiState.activeEncounterNextStep ?: stringResource(R.string.dm_operator_next_none),
				)
				Button(
					onClick = onResumeEncounter,
					modifier = Modifier.fillMaxWidth(),
					colors = ButtonDefaults.buttonColors(containerColor = ArcaneTeal),
				) {
					Text(
						text = stringResource(R.string.dm_active_snapshot_resume_button),
						color = MaterialTheme.colorScheme.onPrimary,
					)
				}
			} else {
				OperatorStatusLine(
					label = stringResource(R.string.dm_operator_question_encounter),
					value = stringResource(R.string.dm_operator_no_active_encounter),
				)
				OperatorStatusLine(
					label = stringResource(R.string.dm_operator_question_turn),
					value = stringResource(R.string.dm_operator_turn_none),
				)
				OperatorStatusLine(
					label = stringResource(R.string.dm_operator_question_next),
					value = stringResource(R.string.dm_operator_next_none),
				)
			}
		}
	}
}

@Composable
private fun SnapshotBadge(label: String) {
	Badge(containerColor = MaterialTheme.colorScheme.surface) {
		Text(text = label, modifier = Modifier.padding(horizontal = 4.dp))
	}
}

@Composable
private fun OperatorStatusLine(label: String, value: String) {
	Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
		Text(
			text = label,
			style = MaterialTheme.typography.labelMedium,
			fontWeight = FontWeight.SemiBold,
			color = AntiqueGold,
		)
		Text(
			text = value,
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurface,
		)
	}
}

@Composable
private fun DmHubActionGrid(
	onNewEncounter: () -> Unit,
	onOpenSavedEncounters: () -> Unit,
	onManageCharacters: () -> Unit,
	onEnemyLibrary: () -> Unit,
	onSessionLogNotes: () -> Unit,
	onCampaigns: () -> Unit,
	onAboutHelp: () -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(12.dp),
		) {
			HomeActionCard(
				title = stringResource(R.string.dm_action_new_encounter),
				icon = Icons.Default.Add,
				isPrimary = true,
				modifier = Modifier.weight(1f),
				onClick = onNewEncounter,
			)
			HomeActionCard(
				title = stringResource(R.string.dm_action_load_encounter),
				icon = Icons.Default.PlayArrow,
				isPrimary = false,
				modifier = Modifier.weight(1f),
				onClick = onOpenSavedEncounters,
			)
		}
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(12.dp),
		) {
			HomeActionCard(
				title = stringResource(R.string.dm_action_manage_characters),
				icon = Icons.Default.Group,
				isPrimary = false,
				modifier = Modifier.weight(1f),
				onClick = onManageCharacters,
			)
			HomeActionCard(
				title = stringResource(R.string.dm_action_enemy_library),
				icon = Icons.Default.Fort,
				isPrimary = false,
				modifier = Modifier.weight(1f),
				onClick = onEnemyLibrary,
			)
		}
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(12.dp),
		) {
			HomeActionCard(
				title = stringResource(R.string.dm_action_session_log_notes),
				icon = Icons.Default.AutoStories,
				isPrimary = false,
				modifier = Modifier.weight(1f),
				onClick = onSessionLogNotes,
			)
			HomeActionCard(
				title = stringResource(R.string.browse_campaigns),
				icon = Icons.Default.History,
				isPrimary = false,
				modifier = Modifier.weight(1f),
				onClick = onCampaigns,
			)
		}
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(12.dp),
		) {
			HomeActionCard(
				title = stringResource(R.string.dm_action_about_help),
				icon = Icons.Default.AutoStories,
				isPrimary = false,
				modifier = Modifier.weight(1f),
				onClick = onAboutHelp,
			)
			Spacer(modifier = Modifier.weight(1f))
		}
		Text(
			text = stringResource(R.string.dm_session_hub_hint),
			style = MaterialTheme.typography.bodySmall,
			color = MutedText,
		)
	}
}

@Composable
private fun RecentEncounterSummaryCard(
	recentSession: io.github.velyene.loreweaver.domain.model.SessionRecord?,
	dateLabel: String?,
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp),
		) {
			if (recentSession == null) {
				Text(
					text = stringResource(R.string.dm_recent_encounter_empty),
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			} else {
				Text(
					text = recentSession.title,
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.SemiBold,
				)
				dateLabel?.let { label ->
					Text(
						text = label,
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
				}
				recentSession.snapshot?.let { snapshot ->
					Text(
						text = stringResource(R.string.dm_saved_encounter_summary, snapshot.currentRound, snapshot.combatants.size),
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
				}
			}
		}
	}
}

@Composable
private fun QuickTipsCard() {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp),
		) {
			Text(text = stringResource(R.string.dm_quick_tip_start_fast), style = MaterialTheme.typography.bodySmall)
			Text(text = stringResource(R.string.dm_quick_tip_resume_fast), style = MaterialTheme.typography.bodySmall)
			Text(text = stringResource(R.string.dm_quick_tip_setup_location), style = MaterialTheme.typography.bodySmall)
		}
	}
}

