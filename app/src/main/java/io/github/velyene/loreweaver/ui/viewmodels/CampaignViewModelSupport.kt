package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.util.UiText

internal val CAMPAIGN_EDITOR_ADD_CAMPAIGN_ERROR_PREFIX = UiText.StringResource(R.string.error_add_campaign)
internal val CAMPAIGN_EDITOR_UPDATE_CAMPAIGN_ERROR_PREFIX = UiText.StringResource(R.string.error_update_campaign)
internal val CAMPAIGN_EDITOR_DELETE_CAMPAIGN_ERROR_PREFIX = UiText.StringResource(R.string.error_delete_campaign)
internal val CAMPAIGN_EDITOR_ADD_ENCOUNTER_ERROR_PREFIX = UiText.StringResource(R.string.error_add_encounter)
internal val CAMPAIGN_EDITOR_UPDATE_ENCOUNTER_ERROR_PREFIX = UiText.StringResource(R.string.error_update_encounter)
internal val CAMPAIGN_EDITOR_DELETE_ENCOUNTER_ERROR_PREFIX = UiText.StringResource(R.string.error_delete_encounter)
internal val CAMPAIGN_EDITOR_ADD_NOTE_ERROR_PREFIX = UiText.StringResource(R.string.error_add_note)
internal val CAMPAIGN_EDITOR_UPDATE_NOTE_ERROR_PREFIX = UiText.StringResource(R.string.error_update_note)
internal val CAMPAIGN_EDITOR_DELETE_NOTE_ERROR_PREFIX = UiText.StringResource(R.string.error_delete_note)
internal val UNKNOWN_ERROR_FALLBACK = UiText.StringResource(R.string.unknown_error_message)

internal fun formatEncounterAddedWithMonstersMessage(monsterCount: Int): UiText {
	return UiText.StringResource(
		R.string.encounter_created_with_monsters_message,
		listOf(monsterCount)
	)
}

internal fun exceptionDetail(exception: Exception): UiText {
	return exception.localizedMessage
		?.takeIf(String::isNotBlank)
		?.let(UiText::DynamicString)
		?: exception.message
			?.takeIf(String::isNotBlank)
			?.let(UiText::DynamicString)
		?: UNKNOWN_ERROR_FALLBACK
}

internal fun formatCampaignError(prefix: UiText, exception: Exception): UiText {
	return UiText.StringResource(
		R.string.error_with_detail,
		listOf(prefix, exceptionDetail(exception))
	)
}

internal fun CampaignListUiState.beginLoading(): CampaignListUiState =
	copy(isLoading = true, error = null, onRetry = null)

internal fun CampaignListUiState.withError(
	message: UiText,
	onRetry: (() -> Unit)? = null
): CampaignListUiState = copy(
	isLoading = false,
	error = message,
	onRetry = onRetry
)

internal fun CampaignListUiState.clearErrorState(): CampaignListUiState = copy(error = null, onRetry = null)

internal fun CampaignDetailUiState.beginLoading(): CampaignDetailUiState =
	copy(isLoading = true, error = null, onRetry = null)

internal fun CampaignDetailUiState.withError(
	message: UiText,
	onRetry: (() -> Unit)? = null
): CampaignDetailUiState = copy(
	isLoading = false,
	error = message,
	onRetry = onRetry
)

internal fun CampaignDetailUiState.clearErrorState(): CampaignDetailUiState = copy(error = null, onRetry = null)
