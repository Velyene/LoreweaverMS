package io.github.velyene.loreweaver.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.velyene.loreweaver.domain.model.LogEntry
import io.github.velyene.loreweaver.domain.use_case.ClearLogsUseCase
import io.github.velyene.loreweaver.domain.use_case.GetAllLogsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdventureLogViewModel @Inject constructor(
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

