package com.example.loreweaver.ui.viewmodels

import com.example.loreweaver.domain.model.Campaign
import com.example.loreweaver.domain.model.SessionRecord

data class CampaignListUiState(
	val campaigns: List<Campaign> = emptyList(),
	val sessions: List<SessionRecord> = emptyList(),
	val isLoading: Boolean = false,
	val error: String? = null,
	val onRetry: (() -> Unit)? = null
)

