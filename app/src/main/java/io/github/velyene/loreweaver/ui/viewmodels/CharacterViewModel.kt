package io.github.velyene.loreweaver.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.LogEntry
import io.github.velyene.loreweaver.domain.use_case.AddCharacterUseCase
import io.github.velyene.loreweaver.domain.use_case.DeleteCharacterUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCharacterByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCharactersUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertLogUseCase
import io.github.velyene.loreweaver.domain.use_case.UpdateCharacterUseCase
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

	private var selectedCharacterId: String? = null

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
					_uiState.update { currentState ->
						currentState.copy(
							characters = characters,
							selectedCharacter = selectedCharacterId?.let { id ->
								characters.find { it.id == id }
							},
							isLoading = false
						)
					}
				}
			} catch (e: Exception) {
				reportError(formatError("Failed to load characters", e))
			}
		}
	}

	fun selectCharacter(id: String) {
		selectedCharacterId = id
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

	fun addCharacter(character: CharacterEntry, onSuccess: () -> Unit = {}) {
		launchActionWithError(
			errorPrefix = "Failed to add character",
			action = { addCharacterUseCase(character) },
			onSuccess = onSuccess
		)
	}

	fun updateCharacter(character: CharacterEntry, onSuccess: () -> Unit = {}) {
		launchActionWithError(
			errorPrefix = "Failed to update character",
			action = { updateCharacterUseCase(character) },
			onSuccess = onSuccess
		)
	}

	fun deleteCharacter(character: CharacterEntry, onSuccess: () -> Unit = {}) {
		launchActionWithError(
			errorPrefix = "Failed to delete character",
			action = { deleteCharacterUseCase(character) },
			onSuccess = {
				if (selectedCharacterId == character.id) {
					selectedCharacterId = null
					_uiState.update { it.copy(selectedCharacter = null) }
				}
				onSuccess()
			}
		)
	}

	private fun beginLoading() {
		_uiState.update { it.copy(isLoading = true, error = null) }
	}

	private fun reportError(message: String) {
		_uiState.update { it.copy(isLoading = false, error = message) }
	}

	private fun formatError(prefix: String, exception: Exception): String {
		return "$prefix: ${exceptionDetail(exception)}"
	}

	private fun launchActionWithError(
		errorPrefix: String,
		action: suspend () -> Unit,
		onSuccess: () -> Unit = {}
	) {
		viewModelScope.launch {
			try {
				action()
				onSuccess()
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
