package io.github.velyene.loreweaver.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.velyene.loreweaver.domain.use_case.GetAllSessionsUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCampaignsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CampaignListViewModel @Inject constructor(
	private val getCampaignsUseCase: GetCampaignsUseCase,
	private val getAllSessionsUseCase: GetAllSessionsUseCase
) : ViewModel() {
	private val _uiState = MutableStateFlow(CampaignListUiState())
	val uiState: StateFlow<CampaignListUiState> = _uiState.asStateFlow()

	init {
		loadCampaigns()
		loadAllSessions()
	}

	fun clearError() {
		_uiState.update(CampaignListUiState::clearErrorState)
	}

	private fun loadCampaigns() {
		viewModelScope.launch {
			_uiState.update(CampaignListUiState::beginLoading)
			try {
				getCampaignsUseCase().collect { campaigns ->
					_uiState.update { it.copy(campaigns = campaigns, isLoading = false) }
				}
			} catch (e: CancellationException) {
				throw e
			} catch (e: Exception) {
				reportError(formatCampaignError("Failed to load campaigns", e), ::loadCampaigns)
			}
		}
	}

	private fun loadAllSessions() {
		viewModelScope.launch {
			try {
				getAllSessionsUseCase().collect { sessions ->
					_uiState.update { it.copy(sessions = sessions) }
				}
			} catch (e: CancellationException) {
				throw e
			} catch (e: Exception) {
				reportError(formatCampaignError("Failed to load sessions", e), ::loadAllSessions)
			}
		}
	}

	private fun reportError(message: String, onRetry: (() -> Unit)? = null) {
		_uiState.update { it.withError(message, onRetry) }
	}
}

