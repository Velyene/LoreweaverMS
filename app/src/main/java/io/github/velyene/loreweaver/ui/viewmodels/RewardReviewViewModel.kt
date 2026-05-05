/*
 * FILE: RewardReviewViewModel.kt
 *
 * TABLE OF CONTENTS:
 * 1. Reward Review UiState
 * 2. Reward Review Loading and Mutation Flows
 * 3. Reward Application Actions
 */

package io.github.velyene.loreweaver.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.EncounterRewardSummary
import io.github.velyene.loreweaver.domain.model.InventoryItem
import io.github.velyene.loreweaver.domain.model.InventoryItemSource
import io.github.velyene.loreweaver.domain.model.InventoryItemType
import io.github.velyene.loreweaver.domain.model.RewardItemDisposition
import io.github.velyene.loreweaver.domain.model.RewardReviewItem
import io.github.velyene.loreweaver.domain.model.RewardReviewState
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.use_case.GetCampaignByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCharactersUseCase
import io.github.velyene.loreweaver.domain.use_case.GetEncounterByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetRecentSessionUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertSessionRecordUseCase
import io.github.velyene.loreweaver.domain.use_case.UpdateCampaignUseCase
import io.github.velyene.loreweaver.domain.use_case.UpdateCharacterUseCase
import io.github.velyene.loreweaver.domain.util.applyRewardReview
import io.github.velyene.loreweaver.domain.util.buildInitialRewardReview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RewardReviewUiState(
	val isLoading: Boolean = true,
	val session: SessionRecord? = null,
	val encounterName: String = "",
	val campaign: Campaign? = null,
	val participants: List<CharacterEntry> = emptyList(),
	val rewards: EncounterRewardSummary? = null,
	val rewardReview: RewardReviewState? = null,
	val error: String? = null,
	val onRetry: (() -> Unit)? = null
)

@HiltViewModel
class RewardReviewViewModel @Inject constructor(
	private val getRecentSessionUseCase: GetRecentSessionUseCase,
	private val getEncounterByIdUseCase: GetEncounterByIdUseCase,
	private val getCampaignByIdUseCase: GetCampaignByIdUseCase,
	private val getCharactersUseCase: GetCharactersUseCase,
	private val updateCharacterUseCase: UpdateCharacterUseCase,
	private val updateCampaignUseCase: UpdateCampaignUseCase,
	private val insertSessionRecordUseCase: InsertSessionRecordUseCase
) : ViewModel() {
	private val _uiState = MutableStateFlow(RewardReviewUiState())
	val uiState: StateFlow<RewardReviewUiState> = _uiState.asStateFlow()

	init {
		refresh()
	}

	fun refresh() {
		_uiState.update { it.copy(isLoading = true, error = null, onRetry = null) }
		viewModelScope.launch {
			try {
				val session = getRecentSessionUseCase()
				val rewards = session?.rewards
				if (session == null || rewards == null) {
					_uiState.update { it.copy(isLoading = false, session = session, rewards = rewards, rewardReview = null) }
					return@launch
				}
				val encounter = session.encounterId?.let { encounterId ->
					getEncounterByIdUseCase(encounterId)
				}
				val campaign = encounter?.campaignId?.let { campaignId ->
					getCampaignByIdUseCase(campaignId)
				}
				val characters = getCharactersUseCase().first()
				val participants = rewards.participantRewards.mapNotNull { share ->
					characters.firstOrNull { it.id == share.characterId }
				}
				_uiState.update {
					it.copy(
						isLoading = false,
						session = session,
						encounterName = encounter?.name ?: session.title,
						campaign = campaign,
						participants = participants,
						rewards = rewards,
						rewardReview = session.rewardReview ?: buildInitialRewardReview(rewards),
						error = null,
						onRetry = null
					)
				}
			} catch (exception: Exception) {
				_uiState.update {
					it.copy(isLoading = false, error = exception.message, onRetry = ::refresh)
				}
			}
		}
	}

	fun assignItemToCharacter(itemId: String, characterId: String) {
		updateReview { review ->
			review.copy(items = review.items.map { item ->
				if (item.item.id == itemId) {
					item.copy(disposition = RewardItemDisposition.CHARACTER, assignedCharacterId = characterId)
				} else {
					item
				}
			})
		}
	}

	fun sendItemToPartyStash(itemId: String) {
		updateReview { review ->
			review.copy(items = review.items.map { item ->
				if (item.item.id == itemId) {
					item.copy(disposition = RewardItemDisposition.PARTY_STASH, assignedCharacterId = null)
				} else {
					item
				}
			})
		}
	}

	fun markItemUnclaimed(itemId: String) {
		updateReview { review ->
			review.copy(items = review.items.map { item ->
				if (item.item.id == itemId) {
					item.copy(disposition = RewardItemDisposition.UNCLAIMED, assignedCharacterId = null)
				} else {
					item
				}
			})
		}
	}

	fun setSharedCurrency(enabled: Boolean) {
		if (enabled && _uiState.value.campaign == null) return
		updateReview { review -> review.copy(useSharedCurrency = enabled) }
	}

	fun updateCurrencyPool(rawValue: String) {
		updateReview { review -> review.copy(currencyPoolCp = rawValue.toIntOrNull() ?: 0) }
	}

	fun addSpecialItem(name: String) {
		val trimmedName = name.trim()
		if (trimmedName.isBlank()) return
		updateReview { review ->
			review.copy(
				items = review.items + RewardReviewItem(
					item = InventoryItem(
						name = trimmedName,
						itemType = InventoryItemType.SPECIAL,
						source = InventoryItemSource.DM_CREATED,
						stackable = false,
						specialItem = true
					)
				)
			)
		}
	}

	fun sendAllLootToPartyStash() {
		updateReview { review ->
			review.copy(items = review.items.map { it.copy(disposition = RewardItemDisposition.PARTY_STASH, assignedCharacterId = null) })
		}
	}

	fun applyRewards(onApplied: () -> Unit) {
		val state = _uiState.value
		val session = state.session ?: return
		val rewardReview = state.rewardReview ?: return
		viewModelScope.launch {
			try {
				val characters = getCharactersUseCase().first()
				val result = applyRewardReview(
					session = session,
					review = rewardReview,
					characters = characters,
					campaign = state.campaign
				)
				result.updatedCharacters.forEach { character ->
					updateCharacterUseCase(character)
				}
				result.updatedCampaign?.let { campaign ->
					updateCampaignUseCase(campaign)
				}
				insertSessionRecordUseCase(result.updatedSession)
				_uiState.update {
					it.copy(
						session = result.updatedSession,
						campaign = result.updatedCampaign,
						rewards = result.updatedSession.rewards,
						rewardReview = result.updatedSession.rewardReview,
						error = null,
					)
				}
				onApplied()
			} catch (exception: Exception) {
				_uiState.update { it.copy(error = exception.message, onRetry = { applyRewards(onApplied) }) }
			}
		}
	}

	private fun updateReview(transform: (RewardReviewState) -> RewardReviewState) {
		_uiState.update { state ->
			val rewardReview = state.rewardReview ?: return@update state
			state.copy(rewardReview = transform(rewardReview).copy(lastUpdated = System.currentTimeMillis()))
		}
	}
}
