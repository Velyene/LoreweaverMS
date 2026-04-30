/*
 * FILE: SessionSummaryViewModel.kt
 *
 * TABLE OF CONTENTS:
 * 1. Session summary UI models
 * 2. ViewModel setup and loading flow
 * 3. Summary mapping helpers
 */

package io.github.velyene.loreweaver.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.use_case.GetCampaignByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCharactersUseCase
import io.github.velyene.loreweaver.domain.use_case.GetEncounterByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetRecentSessionUseCase
import io.github.velyene.loreweaver.domain.util.CharacterParty
import io.github.velyene.loreweaver.ui.util.AppText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SessionSummaryUiState(
	val isLoading: Boolean = true,
	val summary: SessionSummaryUiModel? = null,
	val error: String? = null,
	val onRetry: (() -> Unit)? = null
)

data class SessionSummaryUiModel(
	val encounterId: String?,
	val encounterName: String,
	val campaignId: String?,
	val campaignTitle: String?,
	val result: EncounterResult,
	val totalRounds: Int,
	val survivingPlayers: List<SessionParticipantSummary>,
	val defeatedEnemies: List<SessionParticipantSummary>,
	val persistentStatuses: List<PersistentStatusSummary>,
	val notesSummary: String,
	val logSummary: List<String>
)

enum class EncounterResult {
	VICTORY,
	DEFEAT,
	ENDED_EARLY
}

data class SessionParticipantSummary(
	val name: String,
	val hpLabel: String,
	val initiative: Int
)

data class PersistentStatusSummary(
	val participantName: String,
	val conditions: List<String>
)

@HiltViewModel
class SessionSummaryViewModel @Inject constructor(
	private val getRecentSessionUseCase: GetRecentSessionUseCase,
	private val getEncounterByIdUseCase: GetEncounterByIdUseCase,
	private val getCampaignByIdUseCase: GetCampaignByIdUseCase,
	private val getCharactersUseCase: GetCharactersUseCase,
	private val appText: AppText
) : ViewModel() {
	private val _uiState = MutableStateFlow(SessionSummaryUiState())
	val uiState: StateFlow<SessionSummaryUiState> = _uiState.asStateFlow()

	init {
		refreshSummary()
	}

	fun refreshSummary() {
		_uiState.update { it.beginLoading() }
		viewModelScope.launch {
			try {
				val session = getRecentSessionUseCase()
				if (session?.snapshot == null) {
					_uiState.update {
						it.withError(
							message = appText.getString(R.string.session_summary_error_no_recent)
						)
					}
					return@launch
				}

				val encounter = session.encounterId?.let { getEncounterByIdUseCase(it) }
				val campaign = encounter?.campaignId?.let { getCampaignByIdUseCase(it) }
				val characters = getCharactersUseCase().first()
				val summary = buildSessionSummary(
					appText = appText,
					session = session,
					encounter = encounter,
					campaign = campaign,
					characters = characters
				)
				_uiState.update { it.withSummary(summary) }
			} catch (cancellationException: CancellationException) {
				throw cancellationException
			} catch (e: Exception) {
				_uiState.update {
					it.withError(
						message = appText.getString(R.string.session_summary_error_load),
						onRetry = { refreshSummary() }
					)
				}
			}
		}
	}
}

internal fun buildSessionSummary(
	appText: AppText,
	session: SessionRecord,
	encounter: Encounter?,
	campaign: Campaign?,
	characters: List<CharacterEntry>
): SessionSummaryUiModel {
	val snapshot = requireNotNull(session.snapshot)
	val charactersById = characters.associateBy(CharacterEntry::id)
	val players = snapshot.combatants.filter { combatant ->
		charactersById[combatant.characterId]?.party == CharacterParty.ADVENTURERS
	}
	val enemies = snapshot.combatants.filter { combatant ->
		charactersById[combatant.characterId]?.party != CharacterParty.ADVENTURERS
	}
	val survivingPlayers = players.filter { it.currentHp > 0 }
	val defeatedEnemies = enemies.filter { it.currentHp == 0 }
	val activeEnemies = enemies.filter { it.currentHp > 0 }
	val result = when {
		survivingPlayers.isNotEmpty() && activeEnemies.isEmpty() -> EncounterResult.VICTORY
		players.isNotEmpty() && survivingPlayers.isEmpty() && activeEnemies.isNotEmpty() -> EncounterResult.DEFEAT
		else -> EncounterResult.ENDED_EARLY
	}

	return SessionSummaryUiModel(
		encounterId = session.encounterId,
		encounterName = encounter?.name ?: session.title,
		campaignId = encounter?.campaignId,
		campaignTitle = campaign?.title,
		result = result,
		totalRounds = snapshot.currentRound,
		survivingPlayers = survivingPlayers.map { combatant -> toParticipantSummary(appText, combatant) },
		defeatedEnemies = defeatedEnemies.map { combatant -> toParticipantSummary(appText, combatant) },
		persistentStatuses = players.mapNotNull { combatant ->
			val persistentConditions = charactersById[combatant.characterId]
				?.activeConditions
				?.toList()
				.orEmpty()
				.sorted()
			if (persistentConditions.isEmpty()) {
				null
			} else {
				PersistentStatusSummary(
					participantName = combatant.name,
					conditions = persistentConditions
				)
			}
		},
		notesSummary = encounter?.notes.orEmpty(),
		logSummary = session.log.takeLast(MAX_SUMMARY_LOG_ENTRIES)
	)
}

private const val MAX_SUMMARY_LOG_ENTRIES = 8

private fun toParticipantSummary(appText: AppText, combatant: CombatantState): SessionParticipantSummary {
	return SessionParticipantSummary(
		name = combatant.name,
		hpLabel = appText.getString(
			R.string.combatant_hp_label,
			combatant.currentHp,
			combatant.maxHp
		),
		initiative = combatant.initiative
	)
}

