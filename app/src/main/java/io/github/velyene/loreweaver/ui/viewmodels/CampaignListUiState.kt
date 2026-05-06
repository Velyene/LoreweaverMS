package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.ui.util.UiText

data class CampaignListUiState(
	val campaigns: List<Campaign> = emptyList(),
	val sessions: List<SessionRecord> = emptyList(),
	val sessionHistoryIsLoading: Boolean = false,
	val sessionHistoryOnRetry: (() -> Unit)? = null,
	val latestCompletedSession: SessionRecord? = null,
	val hasActiveEncounter: Boolean = false,
	val activeEncounterName: String? = null,
	val activeEncounterRound: Int? = null,
	val activeEncounterCombatantCount: Int = 0,
	val activeEncounterTurnName: String? = null,
	val activeEncounterLatestChange: String? = null,
	val activeEncounterNextStep: String? = null,
	val isLoading: Boolean = false,
	val error: UiText? = null,
	val onRetry: (() -> Unit)? = null
)

