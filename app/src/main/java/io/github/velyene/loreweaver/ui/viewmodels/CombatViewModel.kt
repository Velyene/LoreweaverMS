/*
 * FILE: CombatViewModel.kt
 *
 * TABLE OF CONTENTS:
 * 1. ViewModel setup and encounter delegation
 * 2. Combatant and turn actions
 * 3. Condition and temp-HP actions
 */

package io.github.velyene.loreweaver.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.use_case.GetActiveEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCharacterByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCharactersUseCase
import io.github.velyene.loreweaver.domain.use_case.GetEncounterByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetSessionsForEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertLogUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertSessionRecordUseCase
import io.github.velyene.loreweaver.domain.use_case.SetActiveEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.UpdateCharacterUseCase
import io.github.velyene.loreweaver.ui.util.AppText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CombatViewModel @Inject constructor(
	private val getCharactersUseCase: GetCharactersUseCase,
	private val getActiveEncounterUseCase: GetActiveEncounterUseCase,
	private val getCharacterByIdUseCase: GetCharacterByIdUseCase,
	private val getEncounterByIdUseCase: GetEncounterByIdUseCase,
	private val getSessionsForEncounterUseCase: GetSessionsForEncounterUseCase,
	private val insertEncounterUseCase: InsertEncounterUseCase,
	private val setActiveEncounterUseCase: SetActiveEncounterUseCase,
	private val insertLogUseCase: InsertLogUseCase,
	private val insertSessionRecordUseCase: InsertSessionRecordUseCase,
	private val updateCharacterUseCase: UpdateCharacterUseCase,
	private val appText: AppText
) : ViewModel() {
	private val _uiState = MutableStateFlow(CombatUiState())
	val uiState: StateFlow<CombatUiState> = _uiState.asStateFlow()
	private val persistedConditionUpdateMutex = Mutex()
	private val encounterController = CombatEncounterController(
		uiState = _uiState,
		scope = viewModelScope,
		getCharactersUseCase = getCharactersUseCase,
		getActiveEncounterUseCase = getActiveEncounterUseCase,
		getEncounterByIdUseCase = getEncounterByIdUseCase,
		getSessionsForEncounterUseCase = getSessionsForEncounterUseCase,
		insertEncounterUseCase = insertEncounterUseCase,
		setActiveEncounterUseCase = setActiveEncounterUseCase,
		insertSessionRecordUseCase = insertSessionRecordUseCase,
		appText = appText
	)

	init {
		encounterController.observeCharacters()
	}

	fun loadEncounter(encounterId: String? = null) {
		encounterController.loadEncounter(encounterId)
	}

	fun clearError() {
		_uiState.update { it.clearErrorState() }
	}

	fun updateNotes(newNotes: String) {
		_uiState.update { it.copy(encounterNotes = newNotes) }
	}

	fun startEncounter(encounterId: String? = null) {
		encounterController.startEncounter(encounterId)
	}

	fun addParty(partyCombatants: List<CombatantState>) {
		_uiState.update { it.withAddedCombatants(partyCombatants) }
		encounterController.recalculateEncounterDifficulty()
	}

	fun addEnemy(name: String, hp: Int, initiative: Int) {
		val enemy = CombatantState(
			characterId = UUID.randomUUID().toString(),
			name = name,
			initiative = initiative,
			currentHp = hp,
			maxHp = hp
		)
		_uiState.update { it.withAddedCombatant(enemy) }
		encounterController.recalculateEncounterDifficulty()
	}

	fun removeCombatant(characterId: String) {
		_uiState.update { it.withRemovedCombatant(characterId) }
		encounterController.recalculateEncounterDifficulty()
	}

	fun updateCombatantHp(characterId: String, delta: Int) {
		applyCombatantHpChange(
			uiState = _uiState,
			characterId = characterId,
			delta = delta
		)
	}

	fun selectAction(action: String) {
		handleActionSelection(uiState = _uiState, action = action)
	}

	fun selectTarget(targetId: String) {
		handleTargetSelection(uiState = _uiState, targetId = targetId)
	}

	fun clearPendingTurn() {
		clearPendingTurnSelection(uiState = _uiState)
	}

	fun applyActionResult(resultType: ActionResolutionType, amount: Int? = null) {
		applyPendingActionResult(
			uiState = _uiState,
			scope = viewModelScope,
			insertLogUseCase = insertLogUseCase,
			resultType = resultType,
			amount = amount
		)
	}

	fun nextTurn() {
		advanceCombatTurn(uiState = _uiState)
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
		addCombatantCondition(
			uiState = _uiState,
			scope = viewModelScope,
			mutex = persistedConditionUpdateMutex,
			characterId = characterId,
			conditionName = conditionName,
			duration = duration,
			persistsAcrossEncounters = persistsAcrossEncounters,
			getCharacterById = { id -> getCharacterByIdUseCase(id) },
			updateCharacterUseCase = updateCharacterUseCase
		)
	}

	/**
	 * Removes a condition from a combatant.
	 */
	fun removeCondition(characterId: String, conditionName: String) {
		removeCombatantCondition(
			uiState = _uiState,
			scope = viewModelScope,
			mutex = persistedConditionUpdateMutex,
			characterId = characterId,
			conditionName = conditionName,
			getCharacterById = { id -> getCharacterByIdUseCase(id) },
			updateCharacterUseCase = updateCharacterUseCase
		)
	}


	/**
	 * Updates temp HP for a combatant.
	 */
	fun updateTempHp(characterId: String, tempHp: Int) {
		updateCombatantTempHp(uiState = _uiState, characterId = characterId, tempHp = tempHp)
	}

	fun saveAndPauseEncounter(onComplete: () -> Unit) {
		encounterController.saveAndPauseEncounter(onComplete)
	}

}
