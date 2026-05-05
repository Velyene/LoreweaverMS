/*
 * FILE: SessionSummaryComponents.kt
 *
 * TABLE OF CONTENTS:
 * 1. Function: OutcomeHeroCard
 * 2. Function: SummaryStatRow
 * 3. Function: SummaryItemCard
 * 4. Function: SummarySectionCard
 * 5. Function: ParticipantSummaryList
 * 6. Function: PersistentStatusList
 * 7. Function: LogSummaryList
 * 8. Function: ActionButtonColumn
 * 9. Function: resultSubtitle
 */

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.viewmodels.EncounterResult
import io.github.velyene.loreweaver.ui.viewmodels.EncounterRewardSummary
import io.github.velyene.loreweaver.ui.viewmodels.PersistentStatusSummary
import io.github.velyene.loreweaver.ui.viewmodels.SessionParticipantSummary
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
				text = encounterResultLabel(summary.result),
				style = MaterialTheme.typography.headlineMedium,
				fontWeight = FontWeight.Bold,
				color = encounterResultColor(summary.result)
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
internal fun SummaryStatRow(
	summary: SessionSummaryUiModel,
	mode: SessionSummaryMode,
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		SummaryItemCard(
			title = when (mode) {
				SessionSummaryMode.STANDARD -> stringResource(R.string.session_summary_rounds_title)
				SessionSummaryMode.ENCOUNTER_COMPLETION -> stringResource(R.string.encounter_summary_rounds_title)
			},
			subtitle = stringResource(R.string.session_final_round, summary.totalRounds),
			icon = Icons.Default.Flag,
			modifier = Modifier.weight(1f)
		)
		SummaryItemCard(
			title = when (mode) {
				SessionSummaryMode.STANDARD -> stringResource(R.string.session_summary_survivors_count_title)
				SessionSummaryMode.ENCOUNTER_COMPLETION -> stringResource(R.string.encounter_summary_surviving_party_title)
			},
			subtitle = summary.survivingPlayers.size.toString(),
			icon = Icons.Default.Groups,
			modifier = Modifier.weight(1f)
		)
		SummaryItemCard(
			title = when (mode) {
				SessionSummaryMode.STANDARD -> stringResource(R.string.session_summary_saved_title)
				SessionSummaryMode.ENCOUNTER_COMPLETION -> stringResource(R.string.session_summary_defeated_enemies_title)
			},
			subtitle = when (mode) {
				SessionSummaryMode.STANDARD -> stringResource(R.string.session_summary_saved_subtitle)
				SessionSummaryMode.ENCOUNTER_COMPLETION -> summary.defeatedEnemies.size.toString()
			},
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
	modifier: Modifier = Modifier,
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
internal fun SummarySectionCard(
	title: String,
	content: @Composable () -> Unit,
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
internal fun ParticipantSummaryList(items: List<SessionParticipantSummary>) {
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
internal fun PersistentStatusList(items: List<PersistentStatusSummary>) {
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
internal fun LogSummaryList(items: List<String>) {
	if (items.isEmpty()) {
		Text(
			text = stringResource(R.string.session_summary_log_empty_message),
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
		return
	}

	Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
		items.forEach { entry ->
			Text(
				text = stringResource(R.string.combat_log_bullet, entry),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
	}
}

@Composable
internal fun RewardSummaryCard(rewards: EncounterRewardSummary) {
	if (
		rewards.experiencePoints <= 0 &&
		rewards.currencyReward == null &&
		rewards.currencyPerParticipant == null &&
		rewards.itemRewards.isEmpty() &&
		rewards.equipmentRewards.isEmpty() &&
		rewards.storyRewards.isEmpty() &&
		rewards.rewardLog.isEmpty()
	) {
		Text(
			text = stringResource(R.string.none_label),
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
		return
	}

	Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
		if (rewards.experiencePoints > 0) {
			Text(
				text = stringResource(R.string.session_summary_reward_xp, rewards.experiencePoints),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
		if (rewards.experiencePerParticipant > 0 && rewards.participantCount > 0) {
			Text(
				text = stringResource(
					R.string.session_summary_reward_xp_share,
					rewards.participantCount,
					rewards.experiencePerParticipant
				),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
		if (rewards.experienceRoundingSurplus > 0) {
			Text(
				text = stringResource(
					R.string.session_summary_reward_xp_surplus,
					rewards.experienceRoundingSurplus
				),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
		}
		rewards.currencyReward?.let { currency ->
			Text(
				text = stringResource(R.string.session_summary_reward_currency_pool, currency),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
		rewards.currencyPerParticipant?.takeIf { rewards.participantCount > 0 }?.let { currency ->
			Text(
				text = stringResource(
					R.string.session_summary_reward_currency_share,
					rewards.participantCount,
					currency
				),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
		if (rewards.currencyRoundingSurplusCp > 0) {
			Text(
				text = stringResource(
					R.string.session_summary_reward_currency_surplus,
					io.github.velyene.loreweaver.domain.util.formatCurrencyCp(rewards.currencyRoundingSurplusCp)
				),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
		}
		if (rewards.participantRewards.isNotEmpty()) {
			Text(
				text = stringResource(R.string.session_summary_reward_participants_title),
				style = MaterialTheme.typography.labelMedium,
				fontWeight = FontWeight.SemiBold,
				color = MaterialTheme.colorScheme.onSurface
			)
			rewards.participantRewards.forEach { participantReward ->
				Text(
					text = stringResource(
						R.string.session_summary_reward_participant_line,
						participantReward.characterName,
						participantReward.experiencePoints,
						io.github.velyene.loreweaver.domain.util.formatCurrencyCp(participantReward.currencyCp)
					),
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurface
				)
			}
		}
		if (rewards.itemRewards.isNotEmpty()) {
			Text(
				text = stringResource(R.string.session_summary_reward_unassigned_loot_title),
				style = MaterialTheme.typography.labelMedium,
				fontWeight = FontWeight.SemiBold,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
		rewards.itemRewards.forEach { rewardLine ->
			Text(
				text = stringResource(R.string.combat_log_bullet, rewardLine),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
		if (rewards.equipmentRewards.isNotEmpty()) {
			Text(
				text = stringResource(R.string.session_summary_reward_special_items_title),
				style = MaterialTheme.typography.labelMedium,
				fontWeight = FontWeight.SemiBold,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
		rewards.equipmentRewards.forEach { rewardLine ->
			Text(
				text = stringResource(R.string.combat_log_bullet, rewardLine),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
		rewards.storyRewards.forEach { rewardLine ->
			Text(
				text = stringResource(R.string.combat_log_bullet, rewardLine),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
		if (rewards.rewardLog.isNotEmpty()) {
			Text(
				text = stringResource(R.string.session_summary_reward_log_title),
				style = MaterialTheme.typography.labelMedium,
				fontWeight = FontWeight.SemiBold,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
		rewards.rewardLog.forEach { rewardLine ->
			Text(
				text = stringResource(R.string.combat_log_bullet, rewardLine),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
	}
}

@Composable
internal fun ActionButtonColumn(
	summary: SessionSummaryUiModel,
	mode: SessionSummaryMode,
	onSaveEncounter: (String) -> Unit,
	onDone: () -> Unit,
	onOpenAdventureLog: () -> Unit,
	onContinueCampaign: (String) -> Unit,
	onOpenSessionDetail: (String) -> Unit,
	onStartAnotherEncounter: () -> Unit,
	onOpenSessionHistory: () -> Unit,
) {
	if (mode == SessionSummaryMode.ENCOUNTER_COMPLETION) {
		Button(
			onClick = { onSaveEncounter(summary.sessionId) },
			modifier = Modifier
				.fillMaxWidth()
				.height(56.dp),
			colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
		) {
			Text(
				text = stringResource(R.string.encounter_summary_save_button),
				color = MaterialTheme.colorScheme.onPrimary,
				fontWeight = FontWeight.Bold
			)
		}

		Spacer(modifier = Modifier.height(12.dp))

		OutlinedButton(
			onClick = onDone,
			modifier = Modifier
				.fillMaxWidth()
				.height(48.dp)
		) {
			Text(stringResource(R.string.session_summary_done_button))
		}

		Spacer(modifier = Modifier.height(12.dp))

		OutlinedButton(
			onClick = onStartAnotherEncounter,
			modifier = Modifier
				.fillMaxWidth()
				.height(48.dp)
		) {
			Icon(Icons.Default.Update, contentDescription = null)
			Spacer(modifier = Modifier.width(8.dp))
			Text(stringResource(R.string.encounter_summary_start_new_encounter_button))
		}
		return
	}

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
		onClick = { onOpenSessionDetail(summary.sessionId) },
		modifier = Modifier
			.fillMaxWidth()
			.height(48.dp)
	) {
		Icon(Icons.Default.SdStorage, contentDescription = null)
		Spacer(modifier = Modifier.width(8.dp))
		Text(stringResource(R.string.session_detail_open_button))
	}

	Spacer(modifier = Modifier.height(12.dp))

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
		Icon(Icons.Default.History, contentDescription = null)
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
private fun resultSubtitle(result: EncounterResult): String {
	return when (result) {
		EncounterResult.VICTORY -> stringResource(R.string.session_summary_result_victory_subtitle)
		EncounterResult.DEFEAT -> stringResource(R.string.session_summary_result_defeat_subtitle)
		EncounterResult.ENDED_EARLY -> stringResource(R.string.session_summary_result_ended_early_subtitle)
	}
}



