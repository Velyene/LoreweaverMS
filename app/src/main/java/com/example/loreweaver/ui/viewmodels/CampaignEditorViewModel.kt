package com.example.loreweaver.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loreweaver.domain.model.Note
import com.example.loreweaver.domain.model.RemoteItem
import com.example.loreweaver.domain.use_case.AddCampaignUseCase
import com.example.loreweaver.domain.use_case.AddEncounterUseCase
import com.example.loreweaver.domain.use_case.AddMonstersToEncounterUseCase
import com.example.loreweaver.domain.use_case.AddNoteUseCase
import com.example.loreweaver.domain.use_case.DeleteNoteUseCase
import com.example.loreweaver.domain.use_case.UpdateNoteUseCase
import com.example.loreweaver.ui.util.NOTE_TYPE_LOCATION
import com.example.loreweaver.ui.util.NOTE_TYPE_LORE
import com.example.loreweaver.ui.util.NOTE_TYPE_NPC
import com.example.loreweaver.ui.util.parseNpcExtra
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CampaignEditorViewModel @Inject constructor(
	private val addCampaignUseCase: AddCampaignUseCase,
	private val addEncounterUseCase: AddEncounterUseCase,
	private val addMonstersToEncounterUseCase: AddMonstersToEncounterUseCase,
	private val addNoteUseCase: AddNoteUseCase,
	private val updateNoteUseCase: UpdateNoteUseCase,
	private val deleteNoteUseCase: DeleteNoteUseCase
) : ViewModel() {
	private val _uiState = MutableStateFlow(CampaignEditorUiState())
	val uiState: StateFlow<CampaignEditorUiState> = _uiState.asStateFlow()

	fun clearMessage() {
		_uiState.update { it.copy(message = null) }
	}

	fun addCampaign(name: String, description: String) {
		launchMutation("Failed to add campaign") {
			addCampaignUseCase(name, description)
		}
	}

	fun addEncounter(campaignId: String, name: String) {
		launchMutation("Failed to add encounter") {
			addEncounterUseCase(campaignId, name)
		}
	}

	fun addEncounterWithMonsters(
		campaignId: String,
		name: String,
		selectedMonsters: List<RemoteItem>
	) {
		viewModelScope.launch {
			try {
				val encounterId = addEncounterUseCase(campaignId, name)
				if (selectedMonsters.isNotEmpty()) {
					addMonstersToEncounterUseCase(encounterId, selectedMonsters)
					_uiState.update {
						it.copy(message = "Encounter created with ${selectedMonsters.size} monsters!")
					}
				}
			} catch (e: Exception) {
				_uiState.update {
					it.copy(message = "Failed to create encounter: ${e.message}")
				}
			}
		}
	}

	fun addNote(campaignId: String, content: String, type: String, extra: String = "") {
		launchMutation("Failed to add note") {
			val note = createNote(campaignId, content, type, extra)
			addNoteUseCase(note)
		}
	}

	fun updateNote(note: Note) {
		launchMutation("Failed to update note") {
			updateNoteUseCase(note)
		}
	}

	fun deleteNote(note: Note) {
		launchMutation("Failed to delete note") {
			deleteNoteUseCase(note)
		}
	}

	private fun launchMutation(
		errorPrefix: String,
		action: suspend () -> Unit
	) {
		viewModelScope.launch {
			try {
				action()
			} catch (e: Exception) {
				_uiState.update { it.copy(message = formatError(errorPrefix, e)) }
			}
		}
	}

	private fun formatError(prefix: String, exception: Exception): String {
		val detail = exception.localizedMessage ?: exception.message ?: "Unknown error"
		return "$prefix: $detail"
	}

	private fun createNote(campaignId: String, content: String, type: String, extra: String): Note {
		return when (type) {
			NOTE_TYPE_LORE -> Note.Lore(
				campaignId = campaignId,
				content = content,
				historicalEra = extra
			)

			NOTE_TYPE_NPC -> {
				val (faction, attitude) = parseNpcExtra(extra)
				Note.NPC(
					campaignId = campaignId,
					content = content,
					faction = faction,
					attitude = attitude
				)
			}

			NOTE_TYPE_LOCATION -> Note.Location(
				campaignId = campaignId,
				content = content,
				region = extra
			)

			else -> Note.General(campaignId = campaignId, content = content)
		}
	}
}

