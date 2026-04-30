package io.github.velyene.loreweaver.ui.viewmodels

import androidx.annotation.StringRes
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.util.AppText

internal fun formatEncounterAddedWithMonstersMessage(
	appText: AppText,
	monsterCount: Int
): String {
	return appText.getQuantityString(
		R.plurals.encounter_created_with_monsters_message,
		monsterCount,
		monsterCount
	)
}

internal fun exceptionDetail(appText: AppText, exception: Exception): String {
	return exception.localizedMessage ?: exception.message ?: appText.getString(R.string.unknown_error)
}

internal fun formatCampaignError(
	appText: AppText,
	@StringRes prefixResId: Int,
	exception: Exception
): String {
	return appText.getString(
		R.string.message_with_detail,
		appText.getString(prefixResId),
		exceptionDetail(appText, exception)
	)
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

internal fun CampaignListUiState.clearErrorState(): CampaignListUiState = copy(error = null, onRetry = null)

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

internal fun CampaignDetailUiState.clearErrorState(): CampaignDetailUiState = copy(error = null, onRetry = null)

internal fun CharacterUiState.beginLoading(): CharacterUiState = copy(isLoading = true, error = null)

internal fun CharacterUiState.withError(message: String): CharacterUiState = copy(
	isLoading = false,
	error = message
)

internal fun CharacterUiState.clearErrorState(): CharacterUiState = copy(error = null)

internal fun CombatUiState.beginLoading(): CombatUiState = copy(
	isLoading = true,
	error = null,
	onRetry = null
)

internal fun CombatUiState.withError(
	message: String?,
	onRetry: (() -> Unit)? = null
): CombatUiState = copy(
	isLoading = false,
	error = message,
	onRetry = onRetry
)

internal fun CombatUiState.clearErrorState(): CombatUiState = copy(error = null, onRetry = null)

internal fun SessionSummaryUiState.beginLoading(): SessionSummaryUiState = copy(
	isLoading = true,
	summary = null,
	error = null,
	onRetry = null
)

internal fun SessionSummaryUiState.withError(
	message: String,
	onRetry: (() -> Unit)? = null
): SessionSummaryUiState = copy(
	isLoading = false,
	summary = null,
	error = message,
	onRetry = onRetry
)

internal fun SessionSummaryUiState.withSummary(summary: SessionSummaryUiModel): SessionSummaryUiState = copy(
	isLoading = false,
	summary = summary,
	error = null,
	onRetry = null
)

