package io.github.velyene.loreweaver.ui.viewmodels

import androidx.annotation.StringRes
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.util.AppText
import io.github.velyene.loreweaver.ui.util.UiText

// ---------------------------------------------------------------------------
// Campaign editor error-prefix constants (UiText string resources)
// ---------------------------------------------------------------------------

internal val CAMPAIGN_EDITOR_ADD_CAMPAIGN_ERROR_PREFIX: UiText =
	UiText.StringResource(R.string.error_add_campaign)

internal val CAMPAIGN_EDITOR_UPDATE_CAMPAIGN_ERROR_PREFIX: UiText =
	UiText.StringResource(R.string.error_update_campaign)

internal val CAMPAIGN_EDITOR_DELETE_CAMPAIGN_ERROR_PREFIX: UiText =
	UiText.StringResource(R.string.error_delete_campaign)

internal val CAMPAIGN_EDITOR_ADD_ENCOUNTER_ERROR_PREFIX: UiText =
	UiText.StringResource(R.string.error_add_encounter)

internal val CAMPAIGN_EDITOR_UPDATE_ENCOUNTER_ERROR_PREFIX: UiText =
	UiText.StringResource(R.string.error_update_encounter)

internal val CAMPAIGN_EDITOR_DELETE_ENCOUNTER_ERROR_PREFIX: UiText =
	UiText.StringResource(R.string.error_delete_encounter)

internal val CAMPAIGN_EDITOR_ADD_NOTE_ERROR_PREFIX: UiText =
	UiText.StringResource(R.string.error_add_note)

internal val CAMPAIGN_EDITOR_UPDATE_NOTE_ERROR_PREFIX: UiText =
	UiText.StringResource(R.string.error_update_note)

internal val CAMPAIGN_EDITOR_DELETE_NOTE_ERROR_PREFIX: UiText =
	UiText.StringResource(R.string.error_delete_note)

// ---------------------------------------------------------------------------
// Error helpers (UiText-based)
// ---------------------------------------------------------------------------

internal fun formatCampaignError(prefix: UiText, exception: Exception): UiText {
	val detail = exception.localizedMessage ?: exception.message ?: ""
	return if (detail.isBlank()) prefix
	else UiText.StringResource(
		R.string.error_with_detail,
		listOf(prefix, UiText.DynamicString(detail))
	)
}

internal fun formatCampaignError(
	appText: AppText,
	@StringRes prefixResId: Int,
	exception: Exception
): String {
	val prefix = appText.getString(prefixResId)
	val detail = exceptionDetail(exception)
	return if (detail.isBlank()) prefix else appText.getString(R.string.error_with_detail, prefix, detail)
}

internal fun exceptionDetail(exception: Exception): String =
	exception.localizedMessage?.takeIf { it.isNotBlank() }
		?: exception.message?.takeIf { it.isNotBlank() }
		?: exception.javaClass.simpleName

internal fun formatEncounterAddedWithMonstersMessage(monsterCount: Int): UiText =
	UiText.StringResource(
		R.string.encounter_created_with_monsters_message_count,
		listOf(monsterCount)
	)

// ---------------------------------------------------------------------------
// UiState helpers
// ---------------------------------------------------------------------------

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

internal fun CampaignDetailUiState.withError(
	message: String,
	onRetry: (() -> Unit)? = null
): CampaignDetailUiState = withError(UiText.DynamicString(message), onRetry)

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
	error = message?.let(UiText::DynamicString),
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
