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
import io.github.velyene.loreweaver.ui.util.UiText
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
	val error: UiText? = null
)

@HiltViewModel
class CharacterViewModel @Inject constructor(
	private val getCharactersUseCase: GetCharactersUseCase,
	private val getCharacterByIdUseCase: GetCharacterByIdUseCase,
	private val addCharacterUseCase: AddCharacterUseCase,
	private val updateCharacterUseCase: UpdateCharacterUseCase,
	private val deleteCharacterUseCase: DeleteCharacterUseCase,
	private val insertLogUseCase: InsertLogUseCase
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
			} catch (e: Exception) {
				reportError(formatError(UiText.StringResource(R.string.error_load_characters), e))
			}
		}
	}

	fun selectCharacter(id: String) {
		viewModelScope.launch {
			beginLoading()
			try {
				val character = getCharacterByIdUseCase(id)
				_uiState.update { it.copy(selectedCharacter = character, isLoading = false) }
			} catch (e: Exception) {
				reportError(formatError(UiText.StringResource(R.string.error_character_not_found), e))
			}
		}
	}

	fun addCharacter(character: CharacterEntry) {
		launchActionWithError(UiText.StringResource(R.string.error_add_character)) { addCharacterUseCase(character) }
	}

	fun updateCharacter(character: CharacterEntry) {
		_uiState.update { state ->
			if (state.selectedCharacter?.id == character.id) {
				state.copy(selectedCharacter = character)
			} else {
				state
			}
		}
		launchActionWithError(UiText.StringResource(R.string.error_update_character)) { updateCharacterUseCase(character) }
	}

	fun deleteCharacter(character: CharacterEntry) {
		_uiState.update { state ->
			if (state.selectedCharacter?.id == character.id) {
				state.copy(selectedCharacter = null)
			} else {
				state
			}
		}
		launchActionWithError(UiText.StringResource(R.string.error_delete_character)) { deleteCharacterUseCase(character) }
	}

	private fun beginLoading() {
		_uiState.update { it.copy(isLoading = true, error = null) }
	}

	private fun reportError(message: UiText) {
		_uiState.update { it.copy(isLoading = false, error = message) }
	}

	private fun formatError(prefix: UiText, exception: Exception): UiText {
		return UiText.StringResource(R.string.error_with_detail, listOf(prefix, exceptionDetail(exception)))
	}

	private fun launchActionWithError(errorPrefix: UiText, action: suspend () -> Unit) {
		viewModelScope.launch {
			try {
				action()
			} catch (e: Exception) {
				reportError(formatError(errorPrefix, e))
			}
		}
	}

	fun clearError() {
		_uiState.update { it.copy(error = null) }
	}

	fun logAction(message: String, type: String = DEFAULT_LOG_TYPE) {
		viewModelScope.launch {
			insertLogUseCase(LogEntry(message = message, type = type))
		}
	}
}
