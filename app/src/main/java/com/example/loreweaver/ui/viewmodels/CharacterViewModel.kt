package com.example.loreweaver.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loreweaver.domain.model.CharacterEntry
import com.example.loreweaver.domain.model.LogEntry
import com.example.loreweaver.domain.use_case.AddCharacterUseCase
import com.example.loreweaver.domain.use_case.DeleteCharacterUseCase
import com.example.loreweaver.domain.use_case.GetCharacterByIdUseCase
import com.example.loreweaver.domain.use_case.GetCharactersUseCase
import com.example.loreweaver.domain.use_case.InsertLogUseCase
import com.example.loreweaver.domain.use_case.UpdateCharacterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
				reportError(formatError("Failed to load characters", e))
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
				reportError(formatError("Character not found", e))
			}
		}
	}

	fun addCharacter(character: CharacterEntry) {
		launchActionWithError("Failed to add character") { addCharacterUseCase(character) }
	}

	fun updateCharacter(character: CharacterEntry) {
		launchActionWithError("Failed to update character") { updateCharacterUseCase(character) }
	}

	fun deleteCharacter(character: CharacterEntry) {
		launchActionWithError("Failed to delete character") { deleteCharacterUseCase(character) }
	}

	private fun beginLoading() {
		_uiState.update { it.copy(isLoading = true, error = null) }
	}

	private fun reportError(message: String) {
		_uiState.update { it.copy(isLoading = false, error = message) }
	}

	private fun formatError(prefix: String, exception: Exception): String {
		val detail = exception.localizedMessage ?: exception.message ?: "Unknown error"
		return "$prefix: $detail"
	}

	private fun launchActionWithError(errorPrefix: String, action: suspend () -> Unit) {
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
