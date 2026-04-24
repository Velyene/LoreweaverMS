package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.SessionRecord

data class CampaignListUiState(
	val campaigns: List<Campaign> = emptyList(),
	val sessions: List<SessionRecord> = emptyList(),
	val isLoading: Boolean = false,
	val error: String? = null,
	val onRetry: (() -> Unit)? = null
)

