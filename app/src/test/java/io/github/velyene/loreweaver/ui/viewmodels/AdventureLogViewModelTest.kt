package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.MainDispatcherRule
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.LogEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdventureLogViewModelTest {

	@get:Rule
	val mainDispatcherRule = MainDispatcherRule()

	@Test
	fun init_loadsLogsIntoUiState() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val logs = listOf(
				LogEntry(id = "log-1", message = "Initiative rolled", type = "Combat"),
				LogEntry(id = "log-2", message = "Trap triggered", type = "Exploration")
			)
			repository.setLogs(logs)

			val viewModel = createAdventureLogViewModel(repository)
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals(logs, this.logs)
				assertFalse(isLoading)
				assertNull(error)
				assertNull(onRetry)
			}
		}
	}

	@Test
	fun clearLogs_setsLocalizedError_whenClearFails() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			repository.setLogs(listOf(LogEntry(id = "log-1", message = "Camp set", type = "Travel")))
			repository.setClearLogsException(IllegalStateException("Disk full"))
			val viewModel = createAdventureLogViewModel(repository)
			advanceUntilIdle()

			viewModel.clearLogs()
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals(
					expectedErrorMessage(R.string.adventure_log_error_clear, "Disk full"),
					error
				)
				assertNull(onRetry)
				assertEquals(1, logs.size)
			}
		}
	}
}

