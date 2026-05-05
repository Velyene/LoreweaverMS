/*
 * FILE: RewardReviewScreen.kt
 *
 * TABLE OF CONTENTS:
 * 1. Reward Review Route
 * 2. Reward Review Content
 * 3. Reward Summary Cards and Filters
 * 4. Reward Editing Helpers
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.RewardItemDisposition
import io.github.velyene.loreweaver.domain.model.RewardReviewItem
import io.github.velyene.loreweaver.domain.util.formatCurrencyCp
import io.github.velyene.loreweaver.ui.viewmodels.RewardReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardReviewScreen(
	onBack: () -> Unit,
	onApplied: () -> Unit,
	viewModel: RewardReviewViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	var manualItemName by remember { mutableStateOf("") }
	val rewardReview = uiState.rewardReview
	val rewards = uiState.rewards
	val scrollState = rememberScrollState()

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(stringResource(R.string.reward_review_title)) },
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_button))
					}
				}
			)
		}
	) { padding ->
		if (uiState.isLoading) {
			CenteredLoadingState(modifier = Modifier.fillMaxSize().padding(padding))
			return@Scaffold
		}
		if (rewardReview == null || rewards == null) {
			CenteredEmptyState(
				message = stringResource(R.string.reward_review_empty_message),
				modifier = Modifier.fillMaxSize().padding(padding)
			)
			return@Scaffold
		}

		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
				.padding(16.dp)
				.verticalScroll(scrollState),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			Text(
				text = stringResource(R.string.reward_review_heading, uiState.encounterName),
				style = MaterialTheme.typography.headlineSmall
			)
			RewardSummaryCard(
				label = stringResource(R.string.reward_review_participants_label),
				content = uiState.participants.joinToString(separator = "\n") { it.name }
			)
			RewardSummaryCard(
				label = stringResource(R.string.reward_review_xp_label),
				content = stringResource(
					R.string.reward_review_xp_summary,
					rewards.experiencePoints,
					rewards.experiencePerParticipant
				)
			)
			RewardSummaryCard(
				label = stringResource(R.string.reward_review_currency_label),
				content = stringResource(
					R.string.reward_review_currency_summary,
					formatCurrencyCp(rewardReview.currencyPoolCp),
					formatCurrencyCp(if (rewardReview.useSharedCurrency) 0 else rewards.currencyPerParticipantCp)
				)
			)
			Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
				Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
					Text(stringResource(R.string.reward_review_currency_options_label), style = MaterialTheme.typography.titleMedium)
					OutlinedTextField(
						value = rewardReview.currencyPoolCp.toString(),
						onValueChange = viewModel::updateCurrencyPool,
						modifier = Modifier.fillMaxWidth(),
						label = { Text(stringResource(R.string.reward_review_currency_pool_cp_label)) }
					)
					Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
						Switch(
							checked = rewardReview.useSharedCurrency,
							onCheckedChange = viewModel::setSharedCurrency,
							enabled = uiState.campaign != null
						)
						Text(stringResource(R.string.reward_review_shared_currency_toggle))
					}
				}
			}
			Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
				Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
					Text(stringResource(R.string.reward_review_manual_special_item_label), style = MaterialTheme.typography.titleMedium)
					OutlinedTextField(
						value = manualItemName,
						onValueChange = { manualItemName = it },
						modifier = Modifier.fillMaxWidth(),
						label = { Text(stringResource(R.string.reward_review_manual_special_item_input_label)) }
					)
					TextButton(onClick = {
						viewModel.addSpecialItem(manualItemName)
						manualItemName = ""
					}) {
						Text(stringResource(R.string.reward_review_add_special_item_button))
					}
				}
			}
			Text(stringResource(R.string.reward_review_loot_label), style = MaterialTheme.typography.titleMedium)
			rewardReview.items.forEach { item ->
				RewardItemAssignmentCard(
					item = item,
					participants = uiState.participants,
					onAssignToCharacter = viewModel::assignItemToCharacter,
					onSendToPartyStash = viewModel::sendItemToPartyStash,
					onMarkUnclaimed = viewModel::markItemUnclaimed
				)
			}
			Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
				Button(onClick = { viewModel.sendAllLootToPartyStash() }, modifier = Modifier.weight(1f)) {
					Text(stringResource(R.string.reward_review_send_all_to_party_stash))
				}
				Button(onClick = { viewModel.applyRewards(onApplied) }, modifier = Modifier.weight(1f)) {
					Text(stringResource(R.string.reward_review_apply_button))
				}
			}
			Spacer(modifier = Modifier.height(24.dp))
		}
	}
}

@Composable
private fun RewardSummaryCard(label: String, content: String) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
			Text(label, style = MaterialTheme.typography.titleMedium)
			Text(content)
		}
	}
}

@Composable
private fun RewardItemAssignmentCard(
	item: RewardReviewItem,
	participants: List<CharacterEntry>,
	onAssignToCharacter: (String, String) -> Unit,
	onSendToPartyStash: (String) -> Unit,
	onMarkUnclaimed: (String) -> Unit
) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
			Text(
				text = if (item.item.quantity > 1) "${item.item.name} ×${item.item.quantity}" else item.item.name,
				style = MaterialTheme.typography.titleMedium
			)
			Text(text = dispositionLabel(item, participants))
			Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
				FilterChip(
					selected = item.disposition == RewardItemDisposition.PARTY_STASH,
					onClick = { onSendToPartyStash(item.item.id) },
					label = { Text(stringResource(R.string.reward_review_party_stash_action)) }
				)
				FilterChip(
					selected = item.disposition == RewardItemDisposition.UNCLAIMED,
					onClick = { onMarkUnclaimed(item.item.id) },
					label = { Text(stringResource(R.string.reward_review_unclaimed_action)) }
				)
			}
			participants.forEach { participant ->
				FilterChip(
					selected = item.disposition == RewardItemDisposition.CHARACTER && item.assignedCharacterId == participant.id,
					onClick = { onAssignToCharacter(item.item.id, participant.id) },
					label = { Text(participant.name) }
				)
			}
		}
	}
}

@Composable
private fun dispositionLabel(item: RewardReviewItem, participants: List<CharacterEntry>): String {
	return when (item.disposition) {
		RewardItemDisposition.CHARACTER -> stringResource(
			R.string.reward_review_assigned_to_character,
			participants.firstOrNull { it.id == item.assignedCharacterId }?.name.orEmpty()
		)
		RewardItemDisposition.PARTY_STASH -> stringResource(R.string.reward_review_party_stash_action)
		RewardItemDisposition.UNCLAIMED -> stringResource(R.string.reward_review_unclaimed_action)
	}
}

