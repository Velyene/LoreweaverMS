package com.example.loreweaver.ui.viewmodels

import com.example.loreweaver.domain.model.Campaign
import com.example.loreweaver.domain.model.Encounter
import com.example.loreweaver.domain.model.Note
import com.example.loreweaver.domain.model.SessionRecord

data class CampaignDetailUiState(
	val selectedCampaign: Campaign? = null,
	val linkedEncounters: List<Encounter> = emptyList(),
	val sessions: List<SessionRecord> = emptyList(),
	val notes: List<Note> = emptyList(),
	val isLoading: Boolean = false,
	val error: String? = null,
	val onRetry: (() -> Unit)? = null
)

