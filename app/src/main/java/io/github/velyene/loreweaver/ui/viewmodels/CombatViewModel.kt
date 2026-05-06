/*
 * FILE: CombatViewModel.kt
 *
 * TABLE OF CONTENTS:
 * 1. UI State Definition (CombatUiState)
 * 2. Combat View Model (CombatViewModel)
 *    a. Data Observation & Loading
 *       - loadEncounter (main entry point)
 *       - loadActiveEncounter (active encounter flow)
 *       - loadSpecificEncounter (ID-based flow)
 *       - handleActiveEncounterSuccess
 *       - handleActiveEncounterError
 *       - restoreCombatFromSnapshot
 *    b. Encounter Control (Start, Action, Next Turn)
 *    c. Persistence (Save & Pause)
 */

package io.github.velyene.loreweaver.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Condition
import io.github.velyene.loreweaver.domain.model.DurationType
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterGenerationSettings
import io.github.velyene.loreweaver.domain.model.EncounterStatus
import io.github.velyene.loreweaver.domain.model.LogEntry
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.use_case.GetActiveEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCharactersUseCase
import io.github.velyene.loreweaver.domain.use_case.GetEncounterByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetSessionsForEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertLogUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertSessionRecordUseCase
import io.github.velyene.loreweaver.domain.use_case.SetActiveEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.UpdateCharacterUseCase
import io.github.velyene.loreweaver.domain.util.generateRandomEncounter
import io.github.velyene.loreweaver.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class CombatViewModel @Inject constructor(
	private val getCharactersUseCase: GetCharactersUseCase,
	private val getActiveEncounterUseCase: GetActiveEncounterUseCase,
	private val getEncounterByIdUseCase: GetEncounterByIdUseCase,
	private val getSessionsForEncounterUseCase: GetSessionsForEncounterUseCase,
	private val insertEncounterUseCase: InsertEncounterUseCase,
	private val setActiveEncounterUseCase: SetActiveEncounterUseCase,
	private val insertLogUseCase: InsertLogUseCase,
	private val insertSessionRecordUseCase: InsertSessionRecordUseCase,
	private val updateCharacterUseCase: UpdateCharacterUseCase,
	private val combatTextProvider: CombatTextProvider
) : ViewModel() {
	private data class TurnHistoryEntry(
		val currentRound: Int,
		val currentTurnIndex: Int,
		val combatants: List<CombatantState>,
		val activeStatuses: List<String>,
		val turnStep: CombatTurnStep,
		val pendingAction: PendingTurnAction?,
		val selectedTargetId: String?,
		val availableCharacters: List<CharacterEntry>,
		val encounterNotes: String,
		val generationDetails: io.github.velyene.loreweaver.domain.model.EncounterGenerationDetails?
	)

	private val _uiState = MutableStateFlow(CombatUiState())
	val uiState: StateFlow<CombatUiState> = _uiState.asStateFlow()
	private val turnHistory = ArrayDeque<TurnHistoryEntry>()
	private var currentTurnAnchor: TurnHistoryEntry? = null

	init {
		observeCharacters()
	}

	private fun observeCharacters() {
		viewModelScope.launch {
			getCharactersUseCase().collect { characters ->
				_uiState.update { it.copy(availableCharacters = characters) }
				updateEncounterDifficulty()
			}
		}
	}

	private suspend fun syncAvailableCharactersFromRepository() {
		_uiState.update { it.copy(availableCharacters = getCharactersUseCase().first()) }
	}

	/**
	 * Recalculates encounter difficulty based on current combatants and party.
	 */
	private fun updateEncounterDifficulty() {
		_uiState.update { it.copy(encounterDifficulty = calculateEncounterDifficulty(it)) }
	}

	fun loadEncounter(
		encounterId: String? = null,
		startFresh: Boolean = false,
		stagedEnemies: List<StagedEnemyItem> = emptyList(),
	) {
		beginLoading()
		clearTurnHistory()
		viewModelScope.launch {
			if (startFresh) {
				_uiState.update { it.clearLoadedEncounterState() }
				if (stagedEnemies.isNotEmpty()) {
					applyStagedEnemyDrafts(stagedEnemies)
				}
			} else if (encounterId == null) {
				loadActiveEncounter()
			} else {
				loadSpecificEncounter(encounterId)
			}
		}
	}

	private fun applyStagedEnemyDrafts(stagedEnemies: List<StagedEnemyItem>) {
		_uiState.update { state ->
			state.withAppendedCombatants(
				stagedEnemies.flatMap { stagedEnemy ->
					buildEnemyCombatants(
						name = stagedEnemy.name,
						hp = stagedEnemy.hp,
						initiative = stagedEnemy.initiative,
						quantity = stagedEnemy.quantity,
					)
				},
			).copy(generationDetails = null)
		}
		updateEncounterDifficulty()
	}

	/**
	 * Loads the currently active encounter from the repository.
	 * Handles Resource states and restores session snapshot if available.
	 */
	private suspend fun loadActiveEncounter() {
		when (val result = getActiveEncounterUseCase()) {
			is Resource.Success -> {
				handleActiveEncounterSuccess(result.data)
			}

			is Resource.Error -> {
				handleActiveEncounterError(result.message)
			}

			is Resource.Loading -> {
				// Loading state already handled at the start of loadEncounter
			}
		}
	}

	/**
	 * Handles successful retrieval of active encounter.
	 * Restores combat state from snapshot if available.
	 */
	private suspend fun handleActiveEncounterSuccess(
		data: Pair<Encounter, SessionRecord?>
	) {
		val (encounter, lastSession) = data

		if (encounter.status != EncounterStatus.ACTIVE) {
			_uiState.update { it.clearLoadedEncounterState() }
			return
		}

		showEncounter(encounter, lastSession)
	}

	/**
	 * Restores combat state from a session snapshot.
	 */
	private suspend fun restoreCombatFromSnapshot(encounter: Encounter, session: SessionRecord) {
		val snapshot = session.snapshot ?: return
		syncAvailableCharactersFromRepository()
		updateEncounterPresentation(
			encounter = encounter,
			combatants = snapshot.combatants,
			encounterLifecycle = EncounterLifecycle.ACTIVE,
			requestedTurnIndex = snapshot.currentTurnIndex,
			currentRound = snapshot.currentRound,
			activeStatuses = session.log,
			isCombatActive = true
		)
		updateEncounterDifficulty()
		refreshTurnAnchorFromCurrentState()
	}

	/**
	 * Handles errors when loading active encounter.
	 */
	private fun handleActiveEncounterError(message: String?) {
		if (message == "No active encounter found") {
			_uiState.update { it.clearLoadedEncounterState() }
			return
		}
		reportError(message = message, onRetry = retryLoadEncounter(null))
	}

	/**
	 * Loads a specific encounter by ID.
	 * Restores session snapshot if available.
	 */
	private suspend fun loadSpecificEncounter(encounterId: String) {
		try {
			val encounter = getEncounterByIdUseCase(encounterId)
				?: run {
					reportError("Encounter not found")
					return
				}

			val sessions = getSessionsForEncounterUseCase(encounter.id).first()
			val lastSession = sessions.firstOrNull()
			showEncounter(encounter, lastSession)
		} catch (e: Exception) {
			reportError(
				message = formatError("Failed to load encounter", e),
				onRetry = retryLoadEncounter(encounterId)
			)
		}
	}

	private fun beginLoading() {
		_uiState.update { it.copy(isLoading = true, error = null, onRetry = null) }
	}

	private fun retryLoadEncounter(encounterId: String?, startFresh: Boolean = false): () -> Unit =
		{ loadEncounter(encounterId = encounterId, startFresh = startFresh) }

	private fun formatError(prefix: String, exception: Exception): String {
		return "$prefix: ${exceptionDetail(exception)}"
	}

	private fun reportError(message: String?, onRetry: (() -> Unit)? = null) {
		_uiState.update {
			it.copy(
				isLoading = false,
				error = message?.let { msg -> io.github.velyene.loreweaver.ui.util.UiText.DynamicString(msg) },
				onRetry = onRetry
			)
		}
	}

	private fun updateEncounterPresentation(
		encounter: Encounter,
		combatants: List<CombatantState>,
		encounterLifecycle: EncounterLifecycle,
		requestedTurnIndex: Int,
		currentRound: Int,
		activeStatuses: List<String>,
		isCombatActive: Boolean,
		resetTransientTurnState: Boolean = true
	) {
		_uiState.update { state ->
			state.withEncounterPresentation(
				encounter = encounter,
				combatants = combatants,
				encounterLifecycle = encounterLifecycle,
				requestedTurnIndex = requestedTurnIndex,
				currentRound = currentRound,
				activeStatuses = activeStatuses,
				isCombatActive = isCombatActive,
				resetTransientTurnState = resetTransientTurnState
			)
		}
	}

	private suspend fun showEncounter(encounter: Encounter, lastSession: SessionRecord?) {
		if (encounter.status == EncounterStatus.ACTIVE) {
			showActiveEncounter(encounter, lastSession)
			return
		}

		showSetupEncounter(encounter, lastSession)
	}

	private suspend fun showActiveEncounter(encounter: Encounter, lastSession: SessionRecord?) {
		if (lastSession?.snapshot != null) {
			restoreCombatFromSnapshot(encounter, lastSession)
			return
		}
		syncAvailableCharactersFromRepository()
		updateEncounterPresentation(
			encounter = encounter,
			combatants = encounter.participants,
			encounterLifecycle = EncounterLifecycle.ACTIVE,
			requestedTurnIndex = encounter.currentTurnIndex,
			currentRound = encounter.currentRound,
			activeStatuses = encounter.activeLog.ifEmpty { lastSession?.log ?: emptyList() },
			isCombatActive = true
		)
		updateEncounterDifficulty()
		refreshTurnAnchorFromCurrentState()
	}

	private fun showSetupEncounter(encounter: Encounter, lastSession: SessionRecord?) {
		val encounterCombatants = lastSession?.snapshot?.combatants ?: encounter.participants
		updateEncounterPresentation(
			encounter = encounter,
			combatants = encounterCombatants,
			encounterLifecycle = when {
				lastSession?.isCompleted == true -> EncounterLifecycle.COMPLETED
				lastSession != null -> EncounterLifecycle.PAUSED
				else -> EncounterLifecycle.DRAFT
			},
			requestedTurnIndex = lastSession?.snapshot?.currentTurnIndex ?: encounter.currentTurnIndex,
			currentRound = encounter.currentRound,
			activeStatuses = lastSession?.log ?: emptyList(),
			isCombatActive = false
		)
		updateEncounterDifficulty()
		clearTurnHistory()
	}

	fun clearError() {
		_uiState.update { it.copy(error = null) }
	}

	fun updateNotes(newNotes: String) {
		val encounterInfo = parseEncounterInfo(_uiState.value.encounterNotes)
		_uiState.update {
			it.copy(
				encounterNotes = encodeEncounterInfo(
					locationTerrain = encounterInfo.locationTerrain,
					notesBody = newNotes,
				)
			)
		}
		persistActiveEncounterCheckpoint()
	}

	fun updateEncounterName(newName: String) {
		_uiState.update { it.copy(currentEncounterName = newName) }
		persistActiveEncounterCheckpoint()
	}

	fun updateLocationTerrain(newLocationTerrain: String) {
		val encounterInfo = parseEncounterInfo(_uiState.value.encounterNotes)
		_uiState.update {
			it.copy(
				encounterNotes = encodeEncounterInfo(
					locationTerrain = newLocationTerrain,
					notesBody = encounterInfo.notesBody,
				)
			)
		}
		persistActiveEncounterCheckpoint()
	}

	fun updateInitiativeMode(initiativeMode: InitiativeMode) {
		_uiState.update { it.copy(initiativeMode = initiativeMode) }
		persistActiveEncounterCheckpoint()
	}

	fun startEncounter(encounterId: String? = null) {
		val state = _uiState.value
		if (state.combatants.isEmpty()) return

		val existingEncounterId = encounterId ?: state.currentEncounterId
		val id = encounterId ?: state.currentEncounterId ?: UUID.randomUUID().toString()
		viewModelScope.launch {
			try {
				clearTurnHistory()
				syncAvailableCharactersFromRepository()
				val existingEncounter = existingEncounterId?.let { getEncounterByIdUseCase(it) }
				val sortedCombatants = resolveEncounterStartCombatants(
					combatants = state.combatants,
					initiativeMode = state.initiativeMode,
				)
				val encounter = buildStartedEncounter(
					encounterId = id,
					existingEncounter = existingEncounter,
					state = state,
					sortedCombatants = sortedCombatants,
					combatTextProvider = combatTextProvider
				)
				insertEncounterUseCase(encounter)
				setActiveEncounterUseCase(id)
				updateEncounterPresentation(
					encounter = encounter,
					combatants = sortedCombatants,
					encounterLifecycle = EncounterLifecycle.ACTIVE,
					requestedTurnIndex = encounter.currentTurnIndex,
					currentRound = encounter.currentRound,
					activeStatuses = state.activeStatuses,
					isCombatActive = true,
					resetTransientTurnState = false
				)
				updateEncounterDifficulty()
				refreshTurnAnchorFromCurrentState()
				runEnemyTurnIfNeeded()
			} catch (e: Exception) {
				reportError(formatError("Failed to start encounter", e))
			}
		}
	}

	fun addParty(partyCombatants: List<CombatantState>) {
		_uiState.update {
			it.withAppendedCombatants(partyCombatants, distinctByCharacterId = true).copy(generationDetails = null)
		}
		updateEncounterDifficulty()
		persistActiveEncounterCheckpoint()
	}

	fun addEnemy(name: String, hp: Int, initiative: Int) {
		addEnemies(name = name, hp = hp, initiative = initiative, quantity = 1)
	}

	fun addEnemies(name: String, hp: Int, initiative: Int, quantity: Int) {
		_uiState.update {
			it.withAppendedCombatants(buildEnemyCombatants(name, hp, initiative, quantity)).copy(generationDetails = null)
		}
		updateEncounterDifficulty()
		persistActiveEncounterCheckpoint()
	}

	fun updateGenerationSettings(settings: EncounterGenerationSettings) {
		_uiState.update { it.copy(generationSettings = settings) }
		persistActiveEncounterCheckpoint()
	}

	fun generateEncounter() {
		val state = _uiState.value
		val partyIds = state.availableCharacters.filter { it.party == io.github.velyene.loreweaver.domain.util.CharacterParty.ADVENTURERS }
			.map(CharacterEntry::id)
			.toSet()
		val partyMembers = state.combatants
			.mapNotNull { combatant ->
				state.availableCharacters.firstOrNull { character ->
					character.id == combatant.characterId && character.id in partyIds
				}
			}
		val generationDetails = generateRandomEncounter(
			partyMembers = partyMembers,
			settings = state.generationSettings,
			random = Random(
				(listOf(
					state.currentEncounterName,
					partyMembers.joinToString("|") { "${it.id}:${it.level}" },
					state.generationSettings.toString()
				).joinToString("#").hashCode()).toLong()
			),
			idFactory = { UUID.randomUUID().toString() }
		)
		val existingPartyCombatants = state.combatants.filter { it.characterId in partyIds }
		val generatedCombatants = generationDetails.finalEnemies.map { generatedEnemy ->
			CombatantState(
				characterId = generatedEnemy.combatantId,
				name = generatedEnemy.name,
				initiative = generatedEnemy.initiative,
				currentHp = generatedEnemy.hp,
				maxHp = generatedEnemy.hp
			)
		}
		_uiState.update {
			it.copy(
				combatants = existingPartyCombatants + generatedCombatants,
				generationDetails = generationDetails,
				currentTurnIndex = normalizedTurnIndex(existingPartyCombatants + generatedCombatants, it.currentTurnIndex)
			)
		}
		updateEncounterDifficulty()
		appendStatus(
			if (generationDetails.finalEnemies.isEmpty()) {
				"Encounter generation found no valid SRD match."
			} else if (generationDetails.requiresDmReview) {
				"Random encounter generated as the closest match. DM review suggested."
			} else {
				"Random encounter generated at ${generationDetails.finalTotalEnemyXp} XP."
			}
		)
		persistActiveEncounterCheckpoint()
	}

	fun updateCombatantInitiative(characterId: String, initiative: Int) {
		_uiState.update { it.withUpdatedCombatantInitiative(characterId, initiative) }
		persistActiveEncounterCheckpoint()
	}

	fun moveCombatantUp(characterId: String) {
		_uiState.update { it.reorderedCombatant(characterId, moveBy = -1) }
		persistActiveEncounterCheckpoint()
	}

	fun moveCombatantDown(characterId: String) {
		_uiState.update { it.reorderedCombatant(characterId, moveBy = 1) }
		persistActiveEncounterCheckpoint()
	}

	fun removeCombatant(characterId: String) {
		_uiState.update { it.withRemovedCombatant(characterId).copy(generationDetails = null) }
		updateEncounterDifficulty()
		persistOrCompleteEncounterAfterStateChange()
	}

	fun updateCombatantHp(characterId: String, delta: Int) {
		val hpChange = applyHpDelta(characterId, delta) ?: return
		appendHpStatus(hpChange = hpChange, delta = delta)
		appendDefeatRecoveryStatus(hpChange)
		persistOrCompleteEncounterAfterStateChange()
	}

	fun setCombatantHp(characterId: String, newHp: Int) {
		val combatant = combatantById(characterId) ?: return
		val clampedHp = newHp.coerceIn(0, combatant.maxHp)
		if (combatant.currentHp == clampedHp && combatant.tempHp == 0) return

		_uiState.update { state ->
			state.copy(
				combatants = state.combatants.map { currentCombatant ->
					if (currentCombatant.characterId == characterId) {
						currentCombatant.copy(currentHp = clampedHp, tempHp = 0)
					} else {
						currentCombatant
					}
				}
			)
		}
		appendStatus("${combatant.name} HP set to $clampedHp/${combatant.maxHp}.")
		appendDefeatRecoveryStatus(
			HpChangeResult(
				combatant = combatant,
				oldHp = combatant.currentHp,
				newHp = clampedHp,
			)
		)
		persistOrCompleteEncounterAfterStateChange()
	}

	fun markCombatantDefeated(characterId: String) {
		val combatant = combatantById(characterId) ?: return
		if (combatant.currentHp == 0 && combatant.tempHp == 0) return

		_uiState.update { state ->
			state.copy(
				combatants = state.combatants.map { currentCombatant ->
					if (currentCombatant.characterId == characterId) {
						currentCombatant.copy(currentHp = 0, tempHp = 0)
					} else {
						currentCombatant
					}
				}
			)
		}
		appendStatus("${combatant.name} marked as defeated.")
		persistOrCompleteEncounterAfterStateChange()
	}

	fun addParticipantNote(characterId: String, note: String) {
		val trimmedNote = note.trim()
		if (trimmedNote.isBlank()) return
		val combatant = combatantById(characterId) ?: return
		appendAndPersistStatus("Note — ${combatant.name}: $trimmedNote")
		persistCharacterNote(characterId, trimmedNote)
		persistActiveEncounterCheckpoint()
	}

	fun duplicateEnemy(characterId: String) {
		val state = _uiState.value
		val combatant = state.combatants.firstOrNull { it.characterId == characterId } ?: return
		val sourceCharacter = state.availableCharacters.firstOrNull { it.id == characterId }
		if (sourceCharacter?.isAdventurer() == true) return

		val duplicatedCombatant = buildDuplicatedEnemyCombatant(combatant, state.combatants)
		_uiState.update {
			it.withAppendedCombatants(listOf(duplicatedCombatant)).copy(generationDetails = null)
		}
		appendStatus("${duplicatedCombatant.name} joins the encounter.")
		updateEncounterDifficulty()
		persistOrCompleteEncounterAfterStateChange()
	}

	fun advanceRound() {
		val state = _uiState.value
		if (state.combatants.isEmpty()) return
		pushCurrentTurnAnchor()
		val nextRound = state.currentRound + 1
		val firstLivingIndex = state.combatants.indexOfFirst { it.currentHp > 0 }.coerceAtLeast(0)
		_uiState.update {
			it.clearPendingTurnState().copy(
				currentRound = nextRound,
				currentTurnIndex = firstLivingIndex,
				activeStatuses = it.activeStatuses + "DM advanced to round $nextRound." + combatTextProvider.roundBeginsStatus(nextRound),
			)
		}
		persistOrCompleteEncounterAfterStateChange()
		refreshTurnAnchorFromCurrentState()
		runEnemyTurnIfNeeded()
	}

	fun selectAction(action: String) {
		val state = _uiState.value
		val current = state.currentCombatant() ?: return
		val pendingAction = resolvePendingAction(current.characterId, action)
		val actorCharacter = state.availableCharacters.firstOrNull { it.id == current.characterId }
		val affordabilityIssue = actorCharacter.pendingActionAffordabilityIssue(pendingAction)
		if (affordabilityIssue != null) {
			appendStatus("${current.name} can't use ${pendingAction.name}: $affordabilityIssue")
			return
		}
		val targetCandidates = targetCandidates(state, pendingAction)

		_uiState.update {
			it.copy(
				pendingAction = pendingAction,
				selectedTargetId = null,
				turnStep = if (targetCandidates.isEmpty()) {
					CombatTurnStep.APPLY_RESULT
				} else {
					CombatTurnStep.SELECT_TARGET
				}
			)
		}
		persistActiveEncounterCheckpoint()
	}

	fun selectTarget(targetId: String) {
		val state = _uiState.value
		val pendingAction = state.pendingAction ?: return
		val validTarget = targetCandidates(state, pendingAction)
			.any { it.characterId == targetId }
		if (!validTarget) return

		_uiState.update {
			it.copy(
				selectedTargetId = targetId,
				turnStep = CombatTurnStep.APPLY_RESULT
			)
		}
		persistActiveEncounterCheckpoint()
	}

	fun clearPendingTurn() {
		_uiState.update { it.clearPendingTurnState() }
		persistActiveEncounterCheckpoint()
	}

	fun applyActionResult(resultType: ActionResolutionType, amount: Int? = null) {
		val state = _uiState.value
		val actor = state.currentCombatant() ?: return
		val pendingAction = state.pendingAction ?: return
		val target = state.resolveSelectedTarget() ?: return
		when (val spendOutcome = spendPendingActionCosts(actor.characterId, pendingAction)) {
			is ActionSpendOutcome.Blocked -> {
				appendStatus("${actor.name} can't use ${pendingAction.name}: ${spendOutcome.reason}")
				_uiState.update { it.clearPendingTurnState() }
				return
			}
			is ActionSpendOutcome.Spent -> {
				if (spendOutcome.summary.isNotBlank()) {
					appendStatus("${actor.name} spends ${spendOutcome.summary}.")
				}
			}
			ActionSpendOutcome.NotApplicable -> Unit
		}

		when (resultType) {
			ActionResolutionType.MISS -> {
				appendAndPersistStatus(
					"${actor.name} used ${pendingAction.name} on ${target.name}, but missed."
				)
			}

			ActionResolutionType.DAMAGE -> {
				val damage = amount?.coerceAtLeast(0) ?: return
				val hpChange = applyHpDelta(target.characterId, -damage) ?: return
				appendAndPersistStatus(
					"${actor.name} used ${pendingAction.name} on ${target.name} for $damage damage."
				)
				appendDefeatRecoveryStatus(hpChange)
			}

			ActionResolutionType.HEAL -> {
				val healing = amount?.coerceAtLeast(0) ?: return
				val hpChange = applyHpDelta(target.characterId, healing) ?: return
				appendAndPersistStatus(
					"${actor.name} used ${pendingAction.name} on ${target.name}, restoring $healing HP."
				)
				appendDefeatRecoveryStatus(hpChange)
			}
		}

		_uiState.update {
			it.copy(
				turnStep = CombatTurnStep.READY_TO_END,
				pendingAction = pendingAction,
				selectedTargetId = target.characterId
			)
		}
		persistOrCompleteEncounterAfterStateChange()
	}

	fun nextTurn() {
		pushCurrentTurnAnchor()
		_uiState.update { it.clearPendingTurnState().advanceTurn() }
		persistOrCompleteEncounterAfterStateChange()
		refreshTurnAnchorFromCurrentState()
		runEnemyTurnIfNeeded()
	}

	fun previousTurn() {
		val historyEntry = turnHistory.removeLastOrNull() ?: return
		viewModelScope.launch {
			restoreTurnHistoryEntry(historyEntry)
		}
	}

	/**
	 * Adds a condition to a combatant.
	 */
	fun addCondition(
		characterId: String,
		conditionName: String,
		duration: Int? = null,
		persistsAcrossEncounters: Boolean = false
	) {
		val currentRound = _uiState.value.currentRound
		if (persistsAcrossEncounters) {
			updatePersistedCharacterCondition(characterId, conditionName, shouldAddCondition = true)
		} else {
			updateCombatant(characterId) { combatant ->
				combatant.copy(
					conditions = combatant.conditions + buildEncounterCondition(
						conditionName = conditionName,
						duration = duration,
						currentRound = currentRound
					)
				)
			}
		}

		appendConditionStatus(
			characterId = characterId,
			conditionName = conditionName,
			duration = duration,
			persistsAcrossEncounters = persistsAcrossEncounters,
			wasConditionAdded = true
		)
		persistActiveEncounterCheckpoint()
	}

	/**
	 * Removes a condition from a combatant.
	 */
	fun removeCondition(characterId: String, conditionName: String) {
		updateCombatant(characterId) { combatant ->
			combatant.copy(conditions = combatant.conditions.filter { it.name != conditionName })
		}
		updatePersistedCharacterCondition(characterId, conditionName, shouldAddCondition = false)
		appendConditionStatus(
			characterId = characterId,
			conditionName = conditionName,
			wasConditionAdded = false
		)
		persistActiveEncounterCheckpoint()
	}

	private fun updatePersistedCharacterCondition(
		characterId: String,
		conditionName: String,
		shouldAddCondition: Boolean
	) {
		val character = _uiState.value.availableCharacters.firstOrNull { it.id == characterId } ?: return
		viewModelScope.launch {
			val updatedCharacter = character.withPersistedCondition(conditionName, shouldAddCondition)
			updateCharacterUseCase(updatedCharacter)
		}
	}

	private fun buildEncounterCondition(
		conditionName: String,
		duration: Int?,
		currentRound: Int
	): Condition {
		return Condition(
			name = conditionName,
			duration = duration,
			durationType = if (duration != null) DurationType.ROUNDS else DurationType.ENCOUNTER,
			addedOnRound = currentRound
		)
	}

	private fun appendConditionStatus(
		characterId: String,
		conditionName: String,
		duration: Int? = null,
		persistsAcrossEncounters: Boolean = false,
		wasConditionAdded: Boolean
	) {
		combatantById(characterId)?.let { combatant ->
			appendStatus(
				buildConditionStatusMessage(
					combatantName = combatant.name,
					conditionName = conditionName,
					duration = duration,
					persistsAcrossEncounters = persistsAcrossEncounters,
					wasConditionAdded = wasConditionAdded
				)
			)
		}
	}

	private fun buildConditionStatusMessage(
		combatantName: String,
		conditionName: String,
		duration: Int? = null,
		persistsAcrossEncounters: Boolean = false,
		wasConditionAdded: Boolean
	): String {
		if (!wasConditionAdded) {
			return "$combatantName is no longer $conditionName"
		}
		val durationText = if (duration != null) " ($duration rounds)" else ""
		val persistenceText = if (persistsAcrossEncounters) " (persistent)" else durationText
		return "$combatantName is now $conditionName$persistenceText"
	}

	private fun updateCombatant(
		characterId: String,
		transform: (CombatantState) -> CombatantState
	) {
		_uiState.update { state ->
			state.copy(
				combatants = state.combatants.map { combatant ->
					if (combatant.characterId == characterId) transform(combatant) else combatant
				}
			)
		}
	}

	private fun combatantById(characterId: String): CombatantState? =
		_uiState.value.combatants.find { it.characterId == characterId }

	private fun persistCharacterNote(characterId: String, note: String) {
		val character = _uiState.value.availableCharacters.firstOrNull { it.id == characterId } ?: return
		viewModelScope.launch {
			updateCharacterUseCase(
				character.copy(
					notes = character.notes.appendEncounterNote(note)
				)
			)
		}
	}

	private fun snapshotTurnState(state: CombatUiState): TurnHistoryEntry {
		return TurnHistoryEntry(
			currentRound = state.currentRound,
			currentTurnIndex = state.currentTurnIndex,
			combatants = state.combatants.map { combatant ->
				combatant.copy(conditions = combatant.conditions.map { condition -> condition.copy() })
			},
			activeStatuses = state.activeStatuses.toList(),
			turnStep = state.turnStep,
			pendingAction = state.pendingAction,
			selectedTargetId = state.selectedTargetId,
			availableCharacters = state.availableCharacters.map { character ->
				character.copy(
					inventory = character.inventory.toList(),
					inventoryState = character.inventoryState.copy(
						personalInventory = character.inventoryState.personalInventory.toList(),
						equippedItems = character.inventoryState.equippedItems.toList(),
					),
					resources = character.resources.map { resource -> resource.copy() },
					spellSlots = character.spellSlots.toMap(),
					activeConditions = character.activeConditions.toSet(),
					actions = character.actions.map { action -> action.copy() },
				)
			},
			encounterNotes = state.encounterNotes,
			generationDetails = state.generationDetails,
		)
	}

	private fun clearTurnHistory() {
		turnHistory.clear()
		currentTurnAnchor = null
		_uiState.update { it.copy(canGoToPreviousTurn = false) }
	}

	private fun refreshTurnAnchorFromCurrentState() {
		val currentState = _uiState.value
		currentTurnAnchor = currentState
			.takeIf { it.isCombatActive && it.combatants.isNotEmpty() }
			?.let(::snapshotTurnState)
		_uiState.update { it.copy(canGoToPreviousTurn = turnHistory.isNotEmpty()) }
	}

	private fun pushCurrentTurnAnchor() {
		val anchor = currentTurnAnchor ?: return
		if (turnHistory.size >= MAX_TURN_HISTORY) {
			turnHistory.removeFirst()
		}
		turnHistory.addLast(anchor)
		_uiState.update { it.copy(canGoToPreviousTurn = true) }
	}

	private suspend fun restoreTurnHistoryEntry(entry: TurnHistoryEntry) {
		val previousCharacters = _uiState.value.availableCharacters
		persistRestoredCharacters(previousCharacters, entry.availableCharacters)
		syncAvailableCharactersFromRepository()
		val restoredCharacters = _uiState.value.availableCharacters
		_uiState.update {
			it.copy(
				currentRound = entry.currentRound,
				currentTurnIndex = entry.currentTurnIndex,
				combatants = entry.combatants,
				activeStatuses = entry.activeStatuses,
				turnStep = entry.turnStep,
				pendingAction = entry.pendingAction,
				selectedTargetId = entry.selectedTargetId,
				availableCharacters = restoredCharacters,
				encounterNotes = entry.encounterNotes,
				generationDetails = entry.generationDetails,
				canGoToPreviousTurn = turnHistory.isNotEmpty(),
			)
		}
		currentTurnAnchor = snapshotTurnState(_uiState.value)
		updateEncounterDifficulty()
		persistActiveEncounterCheckpoint()
	}

	private suspend fun persistRestoredCharacters(
		previousCharacters: List<CharacterEntry>,
		restoredCharacters: List<CharacterEntry>
	) {
		val previousCharacterIds = previousCharacters.map(CharacterEntry::id).toSet()
		val changedCharacters = restoredCharacters.filter { restoredCharacter ->
			restoredCharacter.id in previousCharacterIds
		}
		if (changedCharacters.isEmpty()) return
		changedCharacters.forEach { updatedCharacter ->
			updateCharacterUseCase(updatedCharacter)
		}
	}

	private fun appendStatus(message: String) {
		_uiState.update { it.copy(activeStatuses = it.activeStatuses + message) }
	}

	private fun appendAndPersistStatus(message: String) {
		appendStatus(message)
		viewModelScope.launch {
			insertLogUseCase(LogEntry(message = message, type = "Combat"))
		}
	}

	private fun appendHpStatus(hpChange: HpChangeResult, delta: Int) {
		if (delta == 0) return

		val message = if (delta < 0) {
			"${hpChange.combatant.name} takes ${-delta} damage (${hpChange.newHp}/${hpChange.combatant.maxHp} HP)"
		} else {
			"${hpChange.combatant.name} recovers $delta HP (${hpChange.newHp}/${hpChange.combatant.maxHp} HP)"
		}
		appendStatus(message)
	}

	private fun appendDefeatRecoveryStatus(hpChange: HpChangeResult) {
		when {
			hpChange.newHp == 0 && hpChange.oldHp > 0 -> {
				appendStatus("${hpChange.combatant.name} has been defeated")
			}

			hpChange.oldHp == 0 && hpChange.newHp > 0 -> {
				appendStatus("${hpChange.combatant.name} is back in the fight")
			}
		}
	}

	private fun applyHpDelta(characterId: String, delta: Int): HpChangeResult? {
		val combatant = _uiState.value.combatants.find { it.characterId == characterId } ?: return null
		val damage = (-delta).coerceAtLeast(0)
		val absorbedByTempHp = damage.coerceAtMost(combatant.tempHp)
		val remainingDamage = damage - absorbedByTempHp
		val newTempHp = if (delta < 0) {
			(combatant.tempHp - absorbedByTempHp).coerceAtLeast(0)
		} else {
			combatant.tempHp
		}
		val newHp = if (delta < 0) {
			(combatant.currentHp - remainingDamage).coerceAtLeast(0)
		} else {
			(combatant.currentHp + delta).coerceAtMost(combatant.maxHp)
		}
		_uiState.update { state ->
			state.copy(
				combatants = state.combatants.map { currentCombatant ->
					if (currentCombatant.characterId == characterId) {
						currentCombatant.copy(currentHp = newHp, tempHp = newTempHp)
					} else {
						currentCombatant
					}
				}
			)
		}
		return HpChangeResult(
			combatant = combatant,
			oldHp = combatant.currentHp,
			newHp = newHp
		)
	}

	private fun buildDuplicatedEnemyCombatant(
		source: CombatantState,
		existingCombatants: List<CombatantState>
	): CombatantState {
		val baseName = source.name.enemyGroupKey()
		val nextIndex = existingCombatants
			.filter { it.name.enemyGroupKey().equals(baseName, ignoreCase = true) }
			.mapNotNull { combatant ->
				Regex("""^(.*?)(?:\s+(\d+))?$""")
					.matchEntire(combatant.name.trim())
					?.groupValues
					?.getOrNull(2)
					?.toIntOrNull()
			}
			.maxOrNull()
			?.plus(1)
			?: if (existingCombatants.any { it.name.equals(baseName, ignoreCase = true) }) 2 else 1
		val duplicateName = if (nextIndex <= 1) baseName else "$baseName $nextIndex"
		return CombatantState(
			characterId = UUID.randomUUID().toString(),
			name = duplicateName,
			initiative = source.initiative,
			currentHp = source.maxHp,
			maxHp = source.maxHp,
		)
	}

	private fun String.appendEncounterNote(note: String): String {
		return if (isBlank()) note else "$this\n$note"
	}

	private fun String.enemyGroupKey(): String = trim().replace(Regex("""\s+\d+$"""), "")

	private fun CombatUiState.advanceTurn(): CombatUiState {
		if (combatants.isEmpty()) return this
		val activeCombatant = currentCombatant() ?: return this
		val (postTurnCombatants, endOfTurnMessages) = processEndOfTurnEffects(
			combatants = combatants,
			actingCombatantId = activeCombatant.characterId
		)

		val nextLivingIndex = findNextLivingCombatantIndex(
			combatants = postTurnCombatants,
			startingAfterIndex = currentTurnIndex
		)
		if (nextLivingIndex != null) {
			return copy(
				currentTurnIndex = nextLivingIndex,
				combatants = postTurnCombatants,
				activeStatuses = activeStatuses + "${activeCombatant.name} ended turn." + endOfTurnMessages
			)
		}

		val firstLivingIndex = postTurnCombatants.indexOfFirst { it.currentHp > 0 }
		return copy(
			currentTurnIndex = firstLivingIndex.coerceAtLeast(0),
			currentRound = currentRound + 1,
			combatants = postTurnCombatants,
			activeStatuses = activeStatuses + "${activeCombatant.name} ended turn." + endOfTurnMessages + combatTextProvider.roundBeginsStatus(currentRound + 1)
		)
	}

	private fun processEndOfTurnEffects(
		combatants: List<CombatantState>,
		actingCombatantId: String
	): Pair<List<CombatantState>, List<String>> {
		val endOfTurnMessages = mutableListOf<String>()
		val updatedCombatants = combatants.map { combatant ->
			if (combatant.characterId != actingCombatantId) return@map combatant

			val updatedConditions = combatant.conditions.mapNotNull { condition ->
				when {
					condition.duration == null -> condition
					condition.duration > 1 -> condition.copy(duration = condition.duration - 1)
					else -> {
						endOfTurnMessages += "${combatant.name}'s ${condition.name} condition has expired"
						null
					}
				}
			}
			combatant.copy(conditions = updatedConditions)
		}
		return updatedCombatants to endOfTurnMessages
	}

	private fun findNextLivingCombatantIndex(
		combatants: List<CombatantState>,
		startingAfterIndex: Int
	): Int? {
		if (combatants.isEmpty()) return null
		for (index in (startingAfterIndex + 1) until combatants.size) {
			if (combatants[index].currentHp > 0) return index
		}
		return null
	}

	private fun CombatUiState.currentCombatant(): CombatantState? =
		combatants.getOrNull(currentTurnIndex)

	private fun CombatUiState.resolveSelectedTarget(): CombatantState? {
		val activePendingAction = pendingAction ?: return null
		val resolvedTargetId = selectedTargetId ?: if (activePendingAction.allowsSelfTarget) {
			currentCombatant()?.characterId
		} else {
			null
		}
		return combatants.firstOrNull { it.characterId == resolvedTargetId }
	}

	private fun CombatUiState.clearPendingTurnState(): CombatUiState = copy(
		turnStep = CombatTurnStep.SELECT_ACTION,
		pendingAction = null,
		selectedTargetId = null
	)

	private fun resolvePendingAction(actorId: String, actionName: String): PendingTurnAction {
		val actor = _uiState.value.availableCharacters.firstOrNull { it.id == actorId }
		val customAction = actor?.actions?.firstOrNull { it.name.equals(actionName, ignoreCase = true) }
		val isAttack = customAction?.isAttack ?: defaultActionIsAttack(actionName)
		val resolvedSpellSlotLevel = customAction?.spellSlotLevel
			?: actor.resolveImplicitSpellSlotLevel(actionName)
		val resolvedResourceName = customAction?.resourceName
			?: actor.resolveImplicitAbilityResourceName(actionName)
		val resolvedResourceCost = if (customAction?.resourceCost ?: 0 > 0) {
			customAction?.resourceCost ?: 0
		} else if (resolvedResourceName != null) {
			1
		} else {
			0
		}
		val resolvedStaminaCost = when {
			(customAction?.staminaCost ?: 0) > 0 -> customAction?.staminaCost ?: 0
			actionName.equals("Use Ability", ignoreCase = true) && resolvedResourceName == null -> 1
			else -> 0
		}
		val resolvedManaCost = when {
			(customAction?.manaCost ?: 0) > 0 -> customAction?.manaCost ?: 0
			actionName.equals("Cast Spell", ignoreCase = true) && resolvedSpellSlotLevel == null && ((actor?.maxMana ?: 0) > 0) -> 1
			else -> 0
		}
		val resolvedItemName = customAction?.itemName ?: actor.resolveImplicitConsumableItemName(actionName)
		return PendingTurnAction(
			name = customAction?.name ?: actionName,
			isAttack = isAttack,
			allowsSelfTarget = !isAttack,
			manaCost = resolvedManaCost,
			staminaCost = resolvedStaminaCost,
			spellSlotLevel = resolvedSpellSlotLevel,
			resourceName = resolvedResourceName,
			resourceCost = resolvedResourceCost,
			itemName = resolvedItemName,
			useSummary = buildActionUseSummary(
				manaCost = resolvedManaCost,
				staminaCost = resolvedStaminaCost,
				spellSlotLevel = resolvedSpellSlotLevel,
				resourceName = resolvedResourceName,
				resourceCost = resolvedResourceCost,
				itemName = resolvedItemName
			)
		)
	}

	private fun targetCandidates(
		state: CombatUiState,
		pendingAction: PendingTurnAction
	): List<CombatantState> {
		val currentCombatantId = state.currentCombatant()?.characterId
		return state.combatants.filter { combatant ->
			val isSelf = combatant.characterId == currentCombatantId
			val isEligibleTarget = pendingAction.allowsSelfTarget || !isSelf
			isEligibleTarget && (combatant.currentHp > 0 || isSelf)
		}
	}

	private fun normalizedTurnIndex(combatants: List<CombatantState>, requestedIndex: Int): Int {
		return if (combatants.isEmpty()) 0 else requestedIndex.coerceIn(0, combatants.lastIndex)
	}

	private fun defaultActionIsAttack(actionName: String): Boolean {
		return when (actionName.lowercase()) {
			"dodge", "help", "heal", "hide", "use item", "cast spell", "use ability" -> false
			else -> true
		}
	}


	private fun CharacterEntry.withPersistedCondition(
		conditionName: String,
		shouldAddCondition: Boolean
	): CharacterEntry {
		return if (shouldAddCondition) {
			copy(activeConditions = activeConditions + conditionName)
		} else {
			copy(activeConditions = activeConditions - conditionName)
		}
	}

	/**
	 * Updates temp HP for a combatant.
	 */
	fun updateTempHp(characterId: String, tempHp: Int) {
		_uiState.update { it.withUpdatedTempHp(characterId, tempHp) }
		persistActiveEncounterCheckpoint()
	}

	fun endEncounter(
		result: EncounterResult? = null,
		onComplete: () -> Unit = {}
	) {
		viewModelScope.launch {
			completeEncounter(result = result)
			onComplete()
		}
	}

	fun endEncounterToSummary(
		result: EncounterResult? = null,
		onComplete: (String) -> Unit,
	) {
		viewModelScope.launch {
			val sessionId = completeEncounter(result = result)
			onComplete(sessionId)
		}
	}

	fun saveAndPauseEncounter(onComplete: () -> Unit) {
		val state = _uiState.value
		val encounterId = state.currentEncounterId ?: UUID.randomUUID().toString()

		viewModelScope.launch {
			clearTurnHistory()
			_uiState.update { it.copy(encounterLifecycle = EncounterLifecycle.PAUSED) }
			val existingEncounter = state.currentEncounterId?.let { getEncounterByIdUseCase(it) }
			insertEncounterUseCase(
				buildPendingEncounter(
					encounterId = encounterId,
					existingEncounter = existingEncounter,
					state = state,
					combatTextProvider = combatTextProvider
				)
			)
			insertSessionRecordUseCase(
				buildEncounterSession(
					encounterId = encounterId,
					state = state,
					isCompleted = false,
					encounterResult = null,
					rewardTemplate = existingEncounter?.rewardTemplate ?: io.github.velyene.loreweaver.domain.model.EncounterRewardTemplate(),
					combatTextProvider = combatTextProvider
				)
			)
			onComplete()
		}
	}

	private fun persistOrCompleteEncounterAfterStateChange() {
		if (!_uiState.value.isCombatActive) return
		persistActiveEncounterCheckpoint()
	}

	private fun persistActiveEncounterCheckpoint() {
		val state = _uiState.value
		val encounterId = state.currentEncounterId ?: return
		if (!state.isCombatActive || state.encounterLifecycle != EncounterLifecycle.ACTIVE) return

		viewModelScope.launch {
			val existingEncounter = getEncounterByIdUseCase(encounterId)
			insertEncounterUseCase(
				buildActiveEncounterCheckpoint(
					encounterId = encounterId,
					existingEncounter = existingEncounter,
					state = _uiState.value,
					combatTextProvider = combatTextProvider
				)
			)
		}
	}

	private suspend fun completeEncounter(result: EncounterResult?): String {
		val state = _uiState.value
		val encounterId = state.currentEncounterId ?: UUID.randomUUID().toString()
		val existingEncounter = state.currentEncounterId?.let { getEncounterByIdUseCase(it) }
		val encounterResult = result?.name ?: deriveEncounterResultFromState(state)
		val session = buildEncounterSession(
			encounterId = encounterId,
			state = state,
			isCompleted = true,
			encounterResult = encounterResult,
			rewardTemplate = existingEncounter?.rewardTemplate ?: io.github.velyene.loreweaver.domain.model.EncounterRewardTemplate(),
			combatTextProvider = combatTextProvider
		)

		insertEncounterUseCase(
			buildPendingEncounter(
				encounterId = encounterId,
				existingEncounter = existingEncounter,
				state = state,
				combatTextProvider = combatTextProvider
			)
		)
		insertSessionRecordUseCase(session)
		setActiveEncounterUseCase("")
		_uiState.update {
			it.copy(
				isCombatActive = false,
				encounterLifecycle = EncounterLifecycle.COMPLETED,
				canGoToPreviousTurn = false,
				turnStep = CombatTurnStep.SELECT_ACTION,
				pendingAction = null,
				selectedTargetId = null
			)
		}
		clearTurnHistory()
		return session.id
	}

	private fun CombatUiState.withEncounterPresentation(
		encounter: Encounter,
		combatants: List<CombatantState>,
		encounterLifecycle: EncounterLifecycle,
		requestedTurnIndex: Int,
		currentRound: Int,
		activeStatuses: List<String>,
		isCombatActive: Boolean,
		resetTransientTurnState: Boolean
	): CombatUiState {
		val presentedState = copy(
			currentEncounterId = encounter.id,
			currentEncounterName = encounter.name,
			encounterLifecycle = encounterLifecycle,
			combatants = combatants,
			currentTurnIndex = normalizedTurnIndex(combatants, requestedTurnIndex),
			currentRound = currentRound.coerceAtLeast(1),
			encounterNotes = encounter.notes,
			activeStatuses = activeStatuses,
			generationSettings = encounter.generationSettings,
			generationDetails = encounter.generationDetails,
			isCombatActive = isCombatActive,
			isLoading = false
		)
		return if (resetTransientTurnState) {
			presentedState.clearPendingTurnState()
		} else {
			presentedState
		}
	}

	private fun runEnemyTurnIfNeeded() {
		val state = _uiState.value
		if (!state.isCombatActive) return
		val actor = state.currentCombatant() ?: return
		if (actor.currentHp <= 0) return
		if (isPlayerControlled(actor.characterId, state.availableCharacters)) return

		val decision = decideEnemyTurn(state, actor) ?: return
		when (val spendOutcome = spendPendingActionCosts(actor.characterId, decision.pendingAction)) {
			is ActionSpendOutcome.Blocked -> {
				appendStatus("${actor.name} can't use ${decision.pendingAction.name}: ${spendOutcome.reason}")
				if (_uiState.value.isCombatActive) {
					nextTurn()
				}
				return
			}
			is ActionSpendOutcome.Spent -> {
				if (spendOutcome.summary.isNotBlank()) {
					appendStatus("${actor.name} spends ${spendOutcome.summary}.")
				}
			}
			ActionSpendOutcome.NotApplicable -> Unit
		}
		if (decision.appliedCondition != null) {
			applyEnemySupportCondition(
				targetId = decision.targetId,
				conditionName = decision.appliedCondition,
				duration = decision.conditionDurationRounds
			)
			appendAndPersistStatus(
				"${actor.name} used ${decision.pendingAction.name} on ${decision.targetName}, applying ${decision.appliedCondition}."
			)
			persistOrCompleteEncounterAfterStateChange()
			if (_uiState.value.isCombatActive) {
				nextTurn()
			}
			return
		}
		when (decision.resultType) {
			ActionResolutionType.MISS -> {
				appendAndPersistStatus(
					"${actor.name} used ${decision.pendingAction.name} on ${decision.targetName}, but missed."
				)
			}
			ActionResolutionType.DAMAGE -> {
				val hpChange = applyHpDelta(decision.targetId, -(decision.amount ?: DEFAULT_ENEMY_ACTION_AMOUNT)) ?: return
				appendAndPersistStatus(
					"${actor.name} used ${decision.pendingAction.name} on ${decision.targetName} for ${decision.amount ?: DEFAULT_ENEMY_ACTION_AMOUNT} damage."
				)
				appendDefeatRecoveryStatus(hpChange)
			}
			ActionResolutionType.HEAL -> {
				val hpChange = applyHpDelta(decision.targetId, decision.amount ?: DEFAULT_ENEMY_ACTION_AMOUNT) ?: return
				appendAndPersistStatus(
					"${actor.name} used ${decision.pendingAction.name} on ${decision.targetName}, restoring ${decision.amount ?: DEFAULT_ENEMY_ACTION_AMOUNT} HP."
				)
				appendDefeatRecoveryStatus(hpChange)
			}
		}

		persistOrCompleteEncounterAfterStateChange()
		if (_uiState.value.isCombatActive) {
			nextTurn()
		}
	}

	private fun decideEnemyTurn(
		state: CombatUiState,
		actor: CombatantState
	): EnemyTurnDecision? {
		val charactersById = state.availableCharacters.associateBy(CharacterEntry::id)
		val actorCharacter = charactersById[actor.characterId]
		val livingEnemies = state.combatants
			.filter { combatant ->
				!isPlayerControlled(combatant.characterId, state.availableCharacters) && combatant.currentHp > 0
			}
			.sortedWith(compareBy<CombatantState> { it.currentHp + it.tempHp }.thenByDescending { it.initiative })
		val livingPlayers = state.combatants
			.filter { combatant ->
				isPlayerControlled(combatant.characterId, state.availableCharacters) && combatant.currentHp > 0
			}
			.sortedWith(compareBy<CombatantState> { it.currentHp + it.tempHp }.thenByDescending { it.initiative })
		if (livingPlayers.isEmpty()) return null
		val resolvedActions = actorCharacter
			?.actions
			?.map { resolvePendingAction(actor.characterId, it.name) }
			?.filterNot { action -> actionAffordabilityIssue(actorCharacter, action) != null }
			.orEmpty()

		val healingAction = resolvedActions.firstOrNull { action ->
			isHealingAction(action.name)
		}
		val healingTarget = livingEnemies.firstOrNull { ally ->
			ally.maxHp > 0 && ally.currentHp.toFloat() / ally.maxHp <= ENEMY_HEAL_THRESHOLD
		} ?: livingEnemies.firstOrNull { it.currentHp < it.maxHp }
		if (healingAction != null && healingTarget != null) {
			return EnemyTurnDecision(
				pendingAction = healingAction,
				targetId = healingTarget.characterId,
				targetName = healingTarget.name,
				resultType = ActionResolutionType.HEAL,
				amount = actorCharacter
					?.actions
					?.firstOrNull { it.name.equals(healingAction.name, ignoreCase = true) }
					?.damageDice
					?.toAverageActionAmount() ?: DEFAULT_ENEMY_ACTION_AMOUNT
			)
		}

		val buffAction = resolvedActions.firstOrNull { action ->
			!action.isAttack && supportConditionName(action.name, beneficial = true) != null
		}
		val buffConditionName = buffAction?.let { supportConditionName(it.name, beneficial = true) }
		if (state.currentRound <= 2 && buffAction != null && buffConditionName != null) {
			val buffTarget = livingEnemies
				.filterNot { combatant -> combatant.hasCondition(buffConditionName) }
				.maxByOrNull { it.currentHp + it.tempHp }
			if (buffTarget != null) {
				return EnemyTurnDecision(
					pendingAction = buffAction,
					targetId = buffTarget.characterId,
					targetName = buffTarget.name,
					resultType = ActionResolutionType.HEAL,
					amount = 0,
					appliedCondition = buffConditionName,
					conditionDurationRounds = DEFAULT_SUPPORT_CONDITION_DURATION
				)
			}
		}

		val debuffAction = resolvedActions.firstOrNull { action ->
			!action.isAttack && supportConditionName(action.name, beneficial = false) != null
		}
		val debuffConditionName = debuffAction?.let { supportConditionName(it.name, beneficial = false) }
		if (debuffAction != null && debuffConditionName != null) {
			val debuffTarget = livingPlayers
				.filterNot { combatant -> combatant.hasCondition(debuffConditionName) }
				.maxByOrNull { it.currentHp + it.tempHp }
			if (debuffTarget != null) {
				return EnemyTurnDecision(
					pendingAction = debuffAction,
					targetId = debuffTarget.characterId,
					targetName = debuffTarget.name,
					resultType = ActionResolutionType.DAMAGE,
					amount = 0,
					appliedCondition = debuffConditionName,
					conditionDurationRounds = DEFAULT_SUPPORT_CONDITION_DURATION
				)
			}
		}

		val attackAction = resolvedActions
			.filter { it.isAttack }
			.maxByOrNull { action ->
				actorCharacter
					?.actions
					?.firstOrNull { it.name.equals(action.name, ignoreCase = true) }
					?.damageDice
					?.toAverageActionAmount() ?: DEFAULT_ENEMY_ACTION_AMOUNT
			}
		val target = livingPlayers.first()
		val attackMisses = ((state.currentRound + actor.initiative + target.initiative) % ENEMY_MISS_DIVISOR) == 0
		return EnemyTurnDecision(
			pendingAction = attackAction ?: resolvePendingAction(actor.characterId, DEFAULT_ENEMY_ACTION_NAME),
			targetId = target.characterId,
			targetName = target.name,
			resultType = if (attackMisses) ActionResolutionType.MISS else ActionResolutionType.DAMAGE,
			amount = actorCharacter
				?.actions
				?.firstOrNull { it.name.equals(attackAction?.name, ignoreCase = true) }
				?.damageDice
				?.toAverageActionAmount() ?: DEFAULT_ENEMY_ACTION_AMOUNT
		)
	}

	private fun spendPendingActionCosts(
		actorId: String,
		pendingAction: PendingTurnAction
	): ActionSpendOutcome {
		val actorCharacter = _uiState.value.availableCharacters.firstOrNull { it.id == actorId }
			?: return ActionSpendOutcome.NotApplicable
		val affordabilityIssue = actionAffordabilityIssue(actorCharacter, pendingAction)
		if (affordabilityIssue != null) {
			return ActionSpendOutcome.Blocked(affordabilityIssue)
		}

		val updatedCharacter = actorCharacter.spendActionCosts(
			manaCost = pendingAction.manaCost,
			staminaCost = pendingAction.staminaCost,
			spellSlotLevel = pendingAction.spellSlotLevel,
			resourceName = pendingAction.resourceName,
			resourceCost = pendingAction.resourceCost,
			itemName = pendingAction.itemName
		)
		replaceAvailableCharacter(updatedCharacter)
		viewModelScope.launch {
			updateCharacterUseCase(updatedCharacter)
		}
		return ActionSpendOutcome.Spent(pendingAction.useSummary)
	}

	private fun replaceAvailableCharacter(updatedCharacter: CharacterEntry) {
		_uiState.update { state ->
			state.copy(
				availableCharacters = state.availableCharacters.map { character ->
					if (character.id == updatedCharacter.id) updatedCharacter else character
				}
			)
		}
	}

	private fun actionAffordabilityIssue(
		actorCharacter: CharacterEntry,
		pendingAction: PendingTurnAction
	): String? {
		val currentSpellSlots = pendingAction.spellSlotLevel?.let { level ->
			actorCharacter.spellSlots[level]?.first ?: 0
		}
		val currentResource = pendingAction.resourceName?.let { resourceName ->
			actorCharacter.resources.firstOrNull { it.name.equals(resourceName, ignoreCase = true) }
		}
		return when {
			pendingAction.spellSlotLevel != null && (currentSpellSlots ?: 0) <= 0 ->
				"no level ${pendingAction.spellSlotLevel} spell slot is available"
			pendingAction.manaCost > actorCharacter.mana -> "not enough mana"
			pendingAction.staminaCost > actorCharacter.stamina -> "not enough stamina"
			pendingAction.resourceName != null && (currentResource == null || currentResource.current < pendingAction.resourceCost.coerceAtLeast(0)) ->
				"${pendingAction.resourceName} is depleted"
			pendingAction.itemName != null && actorCharacter.availableInventoryNames().none { it.equals(pendingAction.itemName, ignoreCase = true) } ->
				"${pendingAction.itemName} is not available"
			else -> null
		}
	}

	private fun CharacterEntry?.pendingActionAffordabilityIssue(pendingAction: PendingTurnAction): String? {
		return this?.let { actionAffordabilityIssue(it, pendingAction) }
	}

	private fun CharacterEntry?.resolveImplicitSpellSlotLevel(actionName: String): Int? {
		if (this == null || !actionName.equals("Cast Spell", ignoreCase = true)) return null
		return spellSlots.entries
			.filter { it.value.first > 0 }
			.minByOrNull { it.key }
			?.key
	}

	private fun CharacterEntry?.resolveImplicitAbilityResourceName(actionName: String): String? {
		if (this == null || !actionName.equals("Use Ability", ignoreCase = true)) return null
		return resources.firstOrNull { it.name.isNotBlank() && it.current > 0 }?.name
	}

	private fun CharacterEntry?.resolveImplicitConsumableItemName(actionName: String): String? {
		if (this == null || !actionName.equals("Use Item", ignoreCase = true)) return null
		return availableInventoryNames().firstOrNull { item ->
			item.contains("potion", ignoreCase = true) ||
				item.contains("scroll", ignoreCase = true) ||
				item.contains("elixir", ignoreCase = true) ||
				item.contains("bomb", ignoreCase = true)
		}
	}

	private fun buildActionUseSummary(
		manaCost: Int,
		staminaCost: Int,
		spellSlotLevel: Int?,
		resourceName: String?,
		resourceCost: Int,
		itemName: String?
	): String {
		return buildList {
			if (manaCost > 0) add("$manaCost mana")
			if (staminaCost > 0) add("$staminaCost stamina")
			if (spellSlotLevel != null) add("1 level $spellSlotLevel spell slot")
			if (!resourceName.isNullOrBlank() && resourceCost > 0) add("$resourceCost $resourceName")
			if (!itemName.isNullOrBlank()) add(itemName)
		}.joinToString(" • ")
	}

	private fun isPlayerControlled(
		characterId: String,
		availableCharacters: List<CharacterEntry>
	): Boolean {
		return availableCharacters.firstOrNull { it.id == characterId }?.party == io.github.velyene.loreweaver.domain.util.CharacterParty.ADVENTURERS
	}

	private fun applyEnemySupportCondition(
		targetId: String,
		conditionName: String,
		duration: Int?
	) {
		val currentRound = _uiState.value.currentRound
		updateCombatant(targetId) { combatant ->
			if (combatant.hasCondition(conditionName)) {
				combatant
			} else {
				combatant.copy(
					conditions = combatant.conditions + buildEncounterCondition(
						conditionName = conditionName,
						duration = duration,
						currentRound = currentRound
					)
				)
			}
		}
	}

	private fun isHealingAction(actionName: String): Boolean {
		return actionName.contains("heal", ignoreCase = true) ||
			actionName.contains("mend", ignoreCase = true) ||
			actionName.contains("restore", ignoreCase = true) ||
			actionName.contains("cure", ignoreCase = true)
	}

	private fun supportConditionName(actionName: String, beneficial: Boolean): String? {
		val normalizedName = actionName.lowercase()
		return when {
			beneficial && ("bless" in normalizedName || "inspire" in normalizedName) -> "Blessed"
			beneficial && "shield" in normalizedName -> "Shielded"
			beneficial && ("haste" in normalizedName || "rally" in normalizedName) -> "Empowered"
			!beneficial && ("poison" in normalizedName || "venom" in normalizedName) -> "Poisoned"
			!beneficial && ("hex" in normalizedName || "curse" in normalizedName || "bane" in normalizedName) -> "Cursed"
			!beneficial && ("slow" in normalizedName || "web" in normalizedName) -> "Restrained"
			!beneficial && ("fear" in normalizedName || "terror" in normalizedName) -> "Frightened"
			else -> null
		}
	}

	private fun CombatantState.hasCondition(conditionName: String): Boolean {
		return conditions.any { it.name.equals(conditionName, ignoreCase = true) }
	}

	private fun String.toAverageActionAmount(): Int? {
		val trimmed = trim()
		if (trimmed.isBlank()) return null
		trimmed.toIntOrNull()?.let { return it.coerceAtLeast(1) }

		val match = Regex("""^(\\d+)d(\\d+)([+-]\\d+)?$""").matchEntire(trimmed) ?: return null
		val diceCount = match.groupValues[1].toIntOrNull() ?: return null
		val diceSize = match.groupValues[2].toIntOrNull() ?: return null
		val modifier = match.groupValues[3].takeIf(String::isNotBlank)?.toIntOrNull() ?: 0
		val averageRoll = diceCount * ((diceSize + 1) / 2.0)
		return (averageRoll + modifier).toInt().coerceAtLeast(1)
	}

	private data class EnemyTurnDecision(
		val pendingAction: PendingTurnAction,
		val targetId: String,
		val targetName: String,
		val resultType: ActionResolutionType,
		val amount: Int?,
		val appliedCondition: String? = null,
		val conditionDurationRounds: Int? = null
	)

	private sealed interface ActionSpendOutcome {
		data object NotApplicable : ActionSpendOutcome
		data class Blocked(val reason: String) : ActionSpendOutcome
		data class Spent(val summary: String) : ActionSpendOutcome
	}

	private companion object {
		const val MAX_TURN_HISTORY = 25
		const val DEFAULT_ENEMY_ACTION_NAME = "Strike"
		const val DEFAULT_ENEMY_ACTION_AMOUNT = 4
		const val DEFAULT_SUPPORT_CONDITION_DURATION = 2
		const val ENEMY_MISS_DIVISOR = 5
		const val ENEMY_HEAL_THRESHOLD = 0.4f
	}
}
