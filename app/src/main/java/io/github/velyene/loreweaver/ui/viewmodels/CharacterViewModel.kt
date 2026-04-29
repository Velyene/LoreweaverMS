package io.github.velyene.loreweaver.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.LogEntry
import io.github.velyene.loreweaver.domain.use_case.AddCharacterUseCase
import io.github.velyene.loreweaver.domain.use_case.DeleteCharacterUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCharacterByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCharactersUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertLogUseCase
import io.github.velyene.loreweaver.domain.use_case.UpdateCharacterUseCase
import io.github.velyene.loreweaver.ui.util.AppText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CharacterUiState(
	val characters: List<CharacterEntry> = emptyList(),
	val selectedCharacter: CharacterEntry? = null,
	val isLoading: Boolean = false,
	val error: String? = null
)

@HiltViewModel
class CharacterViewModel @Inject constructor(
	private val getCharactersUseCase: GetCharactersUseCase,
	private val getCharacterByIdUseCase: GetCharacterByIdUseCase,
	private val addCharacterUseCase: AddCharacterUseCase,
	private val updateCharacterUseCase: UpdateCharacterUseCase,
	private val deleteCharacterUseCase: DeleteCharacterUseCase,
	private val insertLogUseCase: InsertLogUseCase,
	private val appText: AppText
) : ViewModel() {
	private companion object {
		const val DEFAULT_LOG_TYPE = "Roll"
	}

	private val _uiState = MutableStateFlow(CharacterUiState())
	val uiState: StateFlow<CharacterUiState> = _uiState.asStateFlow()

	init {
		loadCharacters()
	}

	private fun loadCharacters() {
		viewModelScope.launch {
			beginLoading()
			try {
				getCharactersUseCase().collect { characters ->
					_uiState.update { it.copy(characters = characters, isLoading = false) }
				}
			} catch (e: CancellationException) {
				throw e
			} catch (e: Exception) {
				reportError(formatCampaignError(appText, R.string.character_error_load, e))
			}
		}
	}

	fun selectCharacter(id: String) {
		viewModelScope.launch {
			beginLoading()
			try {
				val character = getCharacterByIdUseCase(id)
				_uiState.update { it.copy(selectedCharacter = character, isLoading = false) }
			} catch (e: CancellationException) {
				throw e
			} catch (e: Exception) {
				reportError(formatCampaignError(appText, R.string.character_error_not_found, e))
			}
		}
	}

	fun addCharacter(character: CharacterEntry) {
		launchActionWithError(R.string.character_error_add) { addCharacterUseCase(character) }
	}

	fun updateCharacter(character: CharacterEntry) {
		launchActionWithError(R.string.character_error_update) { updateCharacterUseCase(character) }
	}

	fun deleteCharacter(character: CharacterEntry) {
		launchActionWithError(R.string.character_error_delete) { deleteCharacterUseCase(character) }
	}

	private fun beginLoading() {
		_uiState.update { it.beginLoading() }
	}

	private fun reportError(message: String) {
		_uiState.update { it.withError(message) }
	}

	private fun launchActionWithError(
		@androidx.annotation.StringRes errorPrefixResId: Int,
		action: suspend () -> Unit
	) {
		viewModelScope.launch {
			try {
				action()
			} catch (e: CancellationException) {
				throw e
			} catch (e: Exception) {
				reportError(formatCampaignError(appText, errorPrefixResId, e))
			}
		}
	}

	fun clearError() {
		_uiState.update { it.clearErrorState() }
	}

	fun logAction(message: String, type: String = DEFAULT_LOG_TYPE) {
		viewModelScope.launch {
			insertLogUseCase(LogEntry(message = message, type = type))
		}
	}
}
