package io.github.velyene.loreweaver.ui.viewmodels

internal const val CAMPAIGN_EDITOR_ADD_CAMPAIGN_ERROR_PREFIX = "Failed to add campaign"
internal const val CAMPAIGN_EDITOR_ADD_ENCOUNTER_ERROR_PREFIX = "Failed to add encounter"
internal const val CAMPAIGN_EDITOR_ADD_NOTE_ERROR_PREFIX = "Failed to add note"
internal const val CAMPAIGN_EDITOR_UPDATE_NOTE_ERROR_PREFIX = "Failed to update note"
internal const val CAMPAIGN_EDITOR_DELETE_NOTE_ERROR_PREFIX = "Failed to delete note"
internal const val UNKNOWN_ERROR_FALLBACK = "Unknown error"

internal fun formatEncounterAddedWithMonstersMessage(monsterCount: Int): String {
	return "Encounter created with $monsterCount monsters!"
}

internal fun exceptionDetail(exception: Exception): String {
	return exception.localizedMessage ?: exception.message ?: UNKNOWN_ERROR_FALLBACK
}

internal fun formatCampaignError(prefix: String, exception: Exception): String {
	return "$prefix: ${exceptionDetail(exception)}"
}

internal fun CampaignListUiState.beginLoading(): CampaignListUiState =
	copy(isLoading = true, error = null, onRetry = null)

internal fun CampaignListUiState.withError(
	message: String,
	onRetry: (() -> Unit)? = null
): CampaignListUiState = copy(
	isLoading = false,
	error = message,
	onRetry = onRetry
)

internal fun CampaignListUiState.clearErrorState(): CampaignListUiState = copy(error = null)

internal fun CampaignDetailUiState.beginLoading(): CampaignDetailUiState =
	copy(isLoading = true, error = null, onRetry = null)

internal fun CampaignDetailUiState.withError(
	message: String,
	onRetry: (() -> Unit)? = null
): CampaignDetailUiState = copy(
	isLoading = false,
	error = message,
	onRetry = onRetry
)

internal fun CampaignDetailUiState.clearErrorState(): CampaignDetailUiState = copy(error = null)
