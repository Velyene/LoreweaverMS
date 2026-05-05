package io.github.velyene.loreweaver.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.LogEntry
import io.github.velyene.loreweaver.domain.use_case.ClearLogsUseCase
import io.github.velyene.loreweaver.domain.use_case.GetAllLogsUseCase
import io.github.velyene.loreweaver.ui.util.AppText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdventureLogUiState(
	val logs: List<LogEntry> = emptyList(),
	val isLoading: Boolean = true,
	val error: String? = null,
	val onRetry: (() -> Unit)? = null
)

@HiltViewModel
class AdventureLogViewModel @Inject constructor(
	private val getAllLogsUseCase: GetAllLogsUseCase,
	private val clearLogsUseCase: ClearLogsUseCase,
	private val appText: AppText
) : ViewModel() {
	private val _uiState = MutableStateFlow(AdventureLogUiState())
	val uiState: StateFlow<AdventureLogUiState> = _uiState.asStateFlow()

	private var logsJob: Job? = null

	init {
		observeLogs()
	}

	fun clearError(expectedMessage: String? = null) {
		_uiState.update {
			if (expectedMessage == null || it.error == expectedMessage) {
				it.clearErrorState()
			} else {
				it
			}
		}
	}

	private fun observeLogs() {
		logsJob?.cancel()
		_uiState.update { it.beginLoading() }
		logsJob = viewModelScope.launch {
			try {
				getAllLogsUseCase().collect { logs ->
					_uiState.update {
						it.copy(
							logs = logs,
							isLoading = false,
							error = null,
							onRetry = null
						)
					}
				}
			} catch (e: CancellationException) {
				throw e
			} catch (e: Exception) {
				reportError(
					formatCampaignError(appText, R.string.adventure_log_error_load, e),
					::observeLogs
				)
			}
		}
	}

	fun clearLogs() {
		viewModelScope.launch {
			try {
				clearLogsUseCase()
			} catch (e: CancellationException) {
				throw e
			} catch (e: Exception) {
				reportError(formatCampaignError(appText, R.string.adventure_log_error_clear, e))
			}
		}
	}

	private fun reportError(message: String, onRetry: (() -> Unit)? = null) {
		_uiState.update { it.withError(message, onRetry) }
	}

	private fun AdventureLogUiState.beginLoading(): AdventureLogUiState =
		copy(isLoading = true, error = null, onRetry = null)

	private fun AdventureLogUiState.withError(
		message: String,
		onRetry: (() -> Unit)? = null
	): AdventureLogUiState = copy(isLoading = false, error = message, onRetry = onRetry)

	private fun AdventureLogUiState.clearErrorState(): AdventureLogUiState =
		copy(error = null, onRetry = null)
}

