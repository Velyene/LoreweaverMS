package io.github.velyene.loreweaver.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.velyene.loreweaver.domain.model.Note
import io.github.velyene.loreweaver.domain.model.RemoteItem
import io.github.velyene.loreweaver.domain.use_case.AddCampaignUseCase
import io.github.velyene.loreweaver.domain.use_case.AddEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.AddMonstersToEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.AddNoteUseCase
import io.github.velyene.loreweaver.domain.use_case.DeleteNoteUseCase
import io.github.velyene.loreweaver.domain.use_case.UpdateNoteUseCase
import io.github.velyene.loreweaver.ui.util.NOTE_TYPE_LOCATION
import io.github.velyene.loreweaver.ui.util.NOTE_TYPE_LORE
import io.github.velyene.loreweaver.ui.util.NOTE_TYPE_NPC
import io.github.velyene.loreweaver.ui.util.parseNpcExtra
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
		launchMutation(CAMPAIGN_EDITOR_ADD_CAMPAIGN_ERROR_PREFIX) {
			addCampaignUseCase(name, description)
		}
	}

	fun addEncounter(campaignId: String, name: String) {
		launchMutation(CAMPAIGN_EDITOR_ADD_ENCOUNTER_ERROR_PREFIX) {
			addEncounterUseCase(campaignId, name)
		}
	}

	fun addEncounterWithMonsters(
		campaignId: String,
		name: String,
		selectedMonsters: List<RemoteItem>
	) {
		val errorPrefix = CAMPAIGN_EDITOR_ADD_ENCOUNTER_ERROR_PREFIX

		viewModelScope.launch {
			try {
				val encounterId = addEncounterUseCase(campaignId, name)
				if (selectedMonsters.isNotEmpty()) {
					addMonstersToEncounterUseCase(encounterId, selectedMonsters)
					_uiState.update {
						it.copy(
							message = formatEncounterAddedWithMonstersMessage(selectedMonsters.size)
						)
					}
				}
			} catch (e: Exception) {
				_uiState.update { it.copy(message = formatCampaignError(errorPrefix, e)) }
			}
		}
	}

	fun addNote(campaignId: String, content: String, type: String, extra: String = "") {
		launchMutation(CAMPAIGN_EDITOR_ADD_NOTE_ERROR_PREFIX) {
			val note = createNote(campaignId, content, type, extra)
			addNoteUseCase(note)
		}
	}

	fun updateNote(note: Note) {
		launchMutation(CAMPAIGN_EDITOR_UPDATE_NOTE_ERROR_PREFIX) {
			updateNoteUseCase(note)
		}
	}

	fun deleteNote(note: Note) {
		launchMutation(CAMPAIGN_EDITOR_DELETE_NOTE_ERROR_PREFIX) {
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
				_uiState.update { it.copy(message = formatCampaignError(errorPrefix, e)) }
			}
		}
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
