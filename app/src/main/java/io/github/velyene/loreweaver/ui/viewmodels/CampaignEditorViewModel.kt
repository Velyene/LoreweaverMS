package io.github.velyene.loreweaver.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.annotation.StringRes
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.velyene.loreweaver.R
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
import io.github.velyene.loreweaver.ui.util.AppText
import io.github.velyene.loreweaver.ui.util.parseNpcExtra
import kotlinx.coroutines.CancellationException
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
	private val deleteNoteUseCase: DeleteNoteUseCase,
	private val appText: AppText
) : ViewModel() {
	private val _uiState = MutableStateFlow(CampaignEditorUiState())
	val uiState: StateFlow<CampaignEditorUiState> = _uiState.asStateFlow()

	fun clearMessage() {
		_uiState.update { it.copy(message = null) }
	}

	fun addCampaign(name: String, description: String) {
		launchMutation(R.string.campaign_error_add_campaign) {
			addCampaignUseCase(name, description)
		}
	}

	fun addEncounter(campaignId: String, name: String) {
		launchMutation(R.string.campaign_error_add_encounter) {
			addEncounterUseCase(campaignId, name)
		}
	}

	fun addEncounterWithMonsters(
		campaignId: String,
		name: String,
		selectedMonsters: List<RemoteItem>
	) {
		val errorPrefixResId = R.string.campaign_error_add_encounter

		viewModelScope.launch {
			try {
				val encounterId = addEncounterUseCase(campaignId, name)
				if (selectedMonsters.isNotEmpty()) {
					addMonstersToEncounterUseCase(encounterId, selectedMonsters)
					_uiState.update {
						it.copy(
							message = formatEncounterAddedWithMonstersMessage(appText, selectedMonsters.size)
						)
					}
				}
			} catch (e: CancellationException) {
				throw e
			} catch (e: Exception) {
				_uiState.update {
					it.copy(message = formatCampaignError(appText, errorPrefixResId, e))
				}
			}
		}
	}

	fun addNote(campaignId: String, content: String, type: String, extra: String = "") {
		launchMutation(R.string.campaign_error_add_note) {
			val note = createNote(campaignId, content, type, extra)
			addNoteUseCase(note)
		}
	}

	fun updateNote(note: Note) {
		launchMutation(R.string.campaign_error_update_note) {
			updateNoteUseCase(note)
		}
	}

	fun deleteNote(note: Note) {
		launchMutation(R.string.campaign_error_delete_note) {
			deleteNoteUseCase(note)
		}
	}

	private fun launchMutation(
		@StringRes errorPrefixResId: Int,
		action: suspend () -> Unit
	) {
		viewModelScope.launch {
			try {
				action()
			} catch (e: CancellationException) {
				throw e
			} catch (e: Exception) {
				_uiState.update {
					it.copy(message = formatCampaignError(appText, errorPrefixResId, e))
				}
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
