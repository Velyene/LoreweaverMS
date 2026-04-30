/*
 * FILE: CombatViewModelSideEffects.kt
 *
 * TABLE OF CONTENTS:
 * 1. Status/log side effects
 * 2. Persisted-condition side effects
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.LogEntry
import io.github.velyene.loreweaver.domain.use_case.InsertLogUseCase
import io.github.velyene.loreweaver.domain.use_case.UpdateCharacterUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal fun MutableStateFlow<CombatUiState>.combatantById(characterId: String) : io.github.velyene.loreweaver.domain.model.CombatantState? =
	value.combatants.find { it.characterId == characterId }

internal fun MutableStateFlow<CombatUiState>.appendStatus(message: String) {
	update { it.copy(activeStatuses = it.activeStatuses + message) }
}

internal fun appendAndPersistStatus(
	uiState: MutableStateFlow<CombatUiState>,
	scope: CoroutineScope,
	insertLogUseCase: InsertLogUseCase,
	message: String
) {
	uiState.appendStatus(message)
	scope.launch {
		insertLogUseCase(LogEntry(message = message, type = "Combat"))
	}
}

internal fun appendHpStatus(
	uiState: MutableStateFlow<CombatUiState>,
	hpChange: HpChangeResult,
	delta: Int
) {
	buildHpStatusMessage(hpChange, delta)?.let(uiState::appendStatus)
}

internal fun appendDefeatRecoveryStatus(
	uiState: MutableStateFlow<CombatUiState>,
	hpChange: HpChangeResult
) {
	buildDefeatRecoveryStatusMessage(hpChange)?.let(uiState::appendStatus)
}

internal fun appendConditionStatus(
	uiState: MutableStateFlow<CombatUiState>,
	characterId: String,
	conditionName: String,
	duration: Int? = null,
	persistsAcrossEncounters: Boolean = false,
	wasConditionAdded: Boolean
) {
	uiState.combatantById(characterId)?.let { combatant ->
		uiState.appendStatus(
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

internal fun persistCharacterCondition(
	scope: CoroutineScope,
	mutex: Mutex,
	characterId: String,
	conditionName: String,
	shouldAddCondition: Boolean,
	getCharacterById: suspend (String) -> CharacterEntry?,
	availableCharacters: () -> List<CharacterEntry>,
	updateCharacterUseCase: UpdateCharacterUseCase
) {
	scope.launch {
		mutex.withLock {
			val latestCharacter = getCharacterById(characterId)
				?: availableCharacters().firstOrNull { it.id == characterId }
				?: return@withLock
			val updatedCharacter = latestCharacter.withPersistedCondition(conditionName, shouldAddCondition)
			updateCharacterUseCase(updatedCharacter)
		}
	}
}

