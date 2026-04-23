package com.example.loreweaver.ui.viewmodels

internal fun formatCampaignError(prefix: String, exception: Exception): String {
	val detail = exception.localizedMessage ?: exception.message ?: "Unknown error"
	return "$prefix: $detail"
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

