package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.Note
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.ui.util.UiText

data class CampaignDetailUiState(
	val selectedCampaign: Campaign? = null,
	val linkedEncounters: List<Encounter> = emptyList(),
	val sessions: List<SessionRecord> = emptyList(),
	val notes: List<Note> = emptyList(),
	val isLoading: Boolean = false,
	val error: UiText? = null,
	val onRetry: (() -> Unit)? = null
)

