package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.use_case.GetCampaignByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCharactersUseCase
import io.github.velyene.loreweaver.domain.use_case.GetEncounterByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetSessionByIdUseCase
import io.github.velyene.loreweaver.ui.util.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal val SESSION_NOT_FOUND_MESSAGE = UiText.StringResource(io.github.velyene.loreweaver.R.string.session_detail_not_found_message)

data class SessionDetailUiState(
	val isLoading: Boolean = true,
	val session: SessionRecord? = null,
	val encounterName: String? = null,
	val campaignId: String? = null,
	val campaignTitle: String? = null,
	val summary: SessionSummaryUiModel? = null,
	val error: UiText? = null,
	val onRetry: (() -> Unit)? = null,
)

@HiltViewModel
class SessionDetailViewModel @Inject constructor(
	private val getSessionByIdUseCase: GetSessionByIdUseCase,
	private val getEncounterByIdUseCase: GetEncounterByIdUseCase,
	private val getCampaignByIdUseCase: GetCampaignByIdUseCase,
	private val getCharactersUseCase: GetCharactersUseCase,
) : ViewModel() {
	private val _uiState = MutableStateFlow(SessionDetailUiState())
	val uiState: StateFlow<SessionDetailUiState> = _uiState.asStateFlow()

	fun loadSession(sessionId: String) {
		_uiState.update {
			it.copy(
				isLoading = true,
				session = null,
				encounterName = null,
				campaignId = null,
				campaignTitle = null,
				summary = null,
				error = null,
				onRetry = null,
			)
		}
		viewModelScope.launch {
			try {
				val session = getSessionByIdUseCase(sessionId)
				if (session == null) {
					_uiState.update {
						it.copy(
							isLoading = false,
							session = null,
							encounterName = null,
							campaignId = null,
							campaignTitle = null,
							summary = null,
							error = SESSION_NOT_FOUND_MESSAGE,
							onRetry = { loadSession(sessionId) },
						)
					}
					return@launch
				}

				val encounter = session.encounterId?.let { getEncounterByIdUseCase(it) }
				val campaign = encounter?.campaignId?.let { getCampaignByIdUseCase(it) }
				val characters = getCharactersUseCase().first()
				val summary = session.snapshot?.let {
					buildSessionSummary(
						session = session,
						encounter = encounter,
						campaign = campaign,
						characters = characters,
					)
				}

				_uiState.update {
					it.copy(
						isLoading = false,
						session = session,
						encounterName = encounter?.name ?: session.title,
						campaignId = campaign?.id,
						campaignTitle = campaign?.title,
						summary = summary,
						error = null,
						onRetry = null,
					)
				}
			} catch (exception: Exception) {
				_uiState.update {
					it.copy(
						isLoading = false,
						session = null,
						encounterName = null,
						campaignId = null,
						campaignTitle = null,
						summary = null,
						error = exception.localizedMessage
							?.takeIf(String::isNotBlank)
							?.let(UiText::DynamicString)
							?: UiText.StringResource(R.string.error_load_session_detail),
						onRetry = { loadSession(sessionId) },
					)
				}
			}
		}
	}
}

