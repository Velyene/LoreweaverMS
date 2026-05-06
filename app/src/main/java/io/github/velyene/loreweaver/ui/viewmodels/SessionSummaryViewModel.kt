/*
 * FILE: SessionSummaryViewModel.kt
 *
 * TABLE OF CONTENTS:
 * 1. Class: SessionSummaryUiState
 * 2. Value: isLoading
 * 3. Value: summary
 * 4. Value: error
 * 5. Value: onRetry
 * 6. Class: SessionSummaryUiModel
 * 7. Value: sessionId
 * 8. Value: encounterId
 */

package io.github.velyene.loreweaver.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterGenerationDetails
import io.github.velyene.loreweaver.domain.model.EncounterRewardTemplate
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.use_case.GetCampaignByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCharactersUseCase
import io.github.velyene.loreweaver.domain.use_case.GetEncounterByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetRecentSessionUseCase
import io.github.velyene.loreweaver.domain.use_case.GetSessionByIdUseCase
import io.github.velyene.loreweaver.domain.util.CharacterParty
import io.github.velyene.loreweaver.domain.util.calculateEncounterRewards
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias EncounterRewardSummary = io.github.velyene.loreweaver.domain.model.EncounterRewardSummary

data class SessionSummaryUiState(
	val isLoading: Boolean = true,
	val summary: SessionSummaryUiModel? = null,
	val error: String? = null,
	val onRetry: (() -> Unit)? = null,
)

data class SessionSummaryUiModel(
	val sessionId: String,
	val encounterId: String?,
	val encounterName: String,
	val campaignId: String?,
	val campaignTitle: String?,
	val result: EncounterResult,
	val totalRounds: Int,
	val survivingPlayers: List<SessionParticipantSummary>,
	val defeatedEnemies: List<SessionParticipantSummary>,
	val persistentStatuses: List<PersistentStatusSummary>,
	val rewards: EncounterRewardSummary = EncounterRewardSummary(),
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
	private val getSessionByIdUseCase: GetSessionByIdUseCase,
	private val getEncounterByIdUseCase: GetEncounterByIdUseCase,
	private val getCampaignByIdUseCase: GetCampaignByIdUseCase,
	private val getCharactersUseCase: GetCharactersUseCase
) : ViewModel() {
	private val _uiState = MutableStateFlow(SessionSummaryUiState())
	val uiState: StateFlow<SessionSummaryUiState> = _uiState.asStateFlow()

	init {
		refreshSummary()
	}

	fun refreshSummary(sessionId: String? = null) {
		_uiState.update { it.copy(isLoading = true, error = null, onRetry = null) }
		viewModelScope.launch {
			try {
				val session = if (!sessionId.isNullOrBlank()) {
					getSessionByIdUseCase(sessionId)
				} else {
					getRecentSessionUseCase()
				}
				if (session?.snapshot == null || !session.isCompleted) {
					_uiState.update {
						it.copy(
							isLoading = false,
							summary = null,
							error = null,
							onRetry = null,
						)
					}
					return@launch
				}

				val encounter = session.encounterId?.let { getEncounterByIdUseCase(it) }
				val campaign = encounter?.campaignId?.let { getCampaignByIdUseCase(it) }
				val characters = getCharactersUseCase().first()
				val summary = buildSessionSummary(
					session = session,
					encounter = encounter,
					campaign = campaign,
					characters = characters
				)
				_uiState.update {
					it.copy(
						isLoading = false,
						summary = summary,
						error = null,
						onRetry = null,
					)
				}
			} catch (e: Exception) {
				_uiState.update {
					it.copy(
						isLoading = false,
						summary = null,
						error = null,
						onRetry = { refreshSummary(sessionId = sessionId) },
					)
				}
			}
		}
	}
}

internal fun buildSessionSummary(
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
	val result = session.encounterResult
		?.let(::parseEncounterResult)
		?: when {
			survivingPlayers.isNotEmpty() && activeEnemies.isEmpty() -> EncounterResult.VICTORY
			players.isNotEmpty() && survivingPlayers.isEmpty() && activeEnemies.isNotEmpty() -> EncounterResult.DEFEAT
			else -> EncounterResult.ENDED_EARLY
		}

	return SessionSummaryUiModel(
		sessionId = session.id,
		encounterId = session.encounterId,
		encounterName = encounter?.name ?: session.title,
		campaignId = encounter?.campaignId,
		campaignTitle = campaign?.title,
		result = result,
		totalRounds = snapshot.currentRound,
		survivingPlayers = survivingPlayers.map(::toParticipantSummary),
		defeatedEnemies = defeatedEnemies.map(::toParticipantSummary),
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
		rewards = session.rewards ?: calculateEncounterRewards(
			encounterName = encounter?.name ?: session.title,
			encounterResult = result.name,
			combatants = snapshot.combatants,
			characters = characters + syntheticGeneratedEnemyCharacters(encounter?.generationDetails),
			totalRounds = snapshot.currentRound,
			rewardTemplate = encounter?.rewardTemplate ?: EncounterRewardTemplate()
		),
		notesSummary = encounter?.notes?.let(::encounterInfoDisplayText).orEmpty(),
		logSummary = session.log.takeLast(MAX_SUMMARY_LOG_ENTRIES)
	)
}

private const val MAX_SUMMARY_LOG_ENTRIES = 8

private fun parseEncounterResult(value: String): EncounterResult? {
	return runCatching { EncounterResult.valueOf(value) }.getOrNull()
}

private fun toParticipantSummary(combatant: CombatantState): SessionParticipantSummary {
	return SessionParticipantSummary(
		name = combatant.name,
		hpLabel = "${combatant.currentHp}/${combatant.maxHp} HP",
		initiative = combatant.initiative
	)
}

private fun syntheticGeneratedEnemyCharacters(generationDetails: EncounterGenerationDetails?): List<CharacterEntry> {
	return generationDetails?.finalEnemies?.map { generatedEnemy ->
		CharacterEntry(
			id = generatedEnemy.combatantId,
			name = generatedEnemy.name,
			party = CharacterParty.MONSTERS,
			hp = generatedEnemy.hp,
			maxHp = generatedEnemy.hp,
			initiative = generatedEnemy.initiative,
			type = generatedEnemy.creatureType,
			challengeRating = generatedEnemy.challengeRating,
			ac = 10
		)
	}.orEmpty()
}

