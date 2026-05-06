package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.ui.util.UiText

data class CampaignEditorUiState(
	val message: UiText? = null,
	val updatedCampaignId: String? = null,
	val deletedCampaignId: String? = null,
	val updatedEncounterId: String? = null,
	val deletedEncounterId: String? = null,
)

