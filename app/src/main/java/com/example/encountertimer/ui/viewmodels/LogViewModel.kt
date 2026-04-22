package com.example.encountertimer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.encountertimer.domain.model.LogEntry
import com.example.encountertimer.domain.use_case.ClearLogsUseCase
import com.example.encountertimer.domain.use_case.GetAllLogsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(
	getAllLogsUseCase: GetAllLogsUseCase,
	private val clearLogsUseCase: ClearLogsUseCase
) : ViewModel() {
	val logs: Flow<List<LogEntry>> = getAllLogsUseCase()

	fun clearLogs() {
		viewModelScope.launch {
			clearLogsUseCase()
		}
	}
}
