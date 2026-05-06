/*
 * FILE: CombatEncounterSupport.kt
 *
 * TABLE OF CONTENTS:
 * 1. Encounter presentation models
 * 2. Encounter/session persistence builders
 *
 * Note: calculateEncounterDifficulty moved to CombatRosterSupport.kt
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterSnapshot
import io.github.velyene.loreweaver.domain.model.EncounterStatus
import io.github.velyene.loreweaver.domain.model.SessionRecord
import java.util.UUID

internal data class EncounterPresentation(
	val encounter: Encounter,
	val combatants: List<CombatantState>,
	val encounterLifecycle: EncounterLifecycle,
	val requestedTurnIndex: Int,
	val currentRound: Int,
	val activeStatuses: List<String>,
	val isCombatActive: Boolean
)


internal fun buildActiveEncounterPresentation(
	encounter: Encounter,
	lastSession: SessionRecord?
): EncounterPresentation {
	val snapshot = lastSession?.snapshot
	return if (snapshot != null) {
		EncounterPresentation(
			encounter = encounter,
			combatants = snapshot.combatants,
			encounterLifecycle = EncounterLifecycle.ACTIVE,
			requestedTurnIndex = snapshot.currentTurnIndex,
			currentRound = snapshot.currentRound,
			activeStatuses = lastSession.log,
			isCombatActive = true
		)
	} else {
		EncounterPresentation(
			encounter = encounter,
			combatants = encounter.participants,
			encounterLifecycle = EncounterLifecycle.ACTIVE,
			requestedTurnIndex = encounter.currentTurnIndex,
			currentRound = encounter.currentRound,
			activeStatuses = lastSession?.log.orEmpty(),
			isCombatActive = true
		)
	}
}

internal fun buildSetupEncounterPresentation(
	encounter: Encounter,
	lastSession: SessionRecord?
): EncounterPresentation {
	val snapshot = lastSession?.snapshot
	return EncounterPresentation(
		encounter = encounter,
		combatants = snapshot?.combatants ?: encounter.participants,
		encounterLifecycle = if (lastSession != null) EncounterLifecycle.PAUSED else EncounterLifecycle.DRAFT,
		requestedTurnIndex = snapshot?.currentTurnIndex ?: encounter.currentTurnIndex,
		currentRound = encounter.currentRound,
		activeStatuses = lastSession?.log.orEmpty(),
		isCombatActive = false
	)
}

internal fun buildStartedEncounter(
	encounterId: String,
	state: CombatUiState,
	existingEncounter: Encounter?
): Encounter {
	return Encounter(
		id = encounterId,
		campaignId = existingEncounter?.campaignId,
		name = resolveEncounterName(existingEncounter, state.currentEncounterName),
		notes = state.encounterNotes,
		status = EncounterStatus.ACTIVE,
		currentRound = state.currentRound,
		currentTurnIndex = state.currentTurnIndex,
		participants = state.combatants.sortedByDescending { combatant -> combatant.initiative }
	)
}

internal fun buildPausedEncounter(
	encounterId: String,
	state: CombatUiState,
	existingEncounter: Encounter?
): Encounter {
	return Encounter(
		id = encounterId,
		campaignId = existingEncounter?.campaignId,
		name = resolveEncounterName(existingEncounter, state.currentEncounterName),
		notes = state.encounterNotes,
		status = EncounterStatus.PENDING,
		currentRound = state.currentRound,
		currentTurnIndex = state.currentTurnIndex,
		participants = state.combatants
	)
}

internal fun buildPausedSessionRecord(encounterId: String, state: CombatUiState): SessionRecord {
	return SessionRecord(
		encounterId = encounterId,
		title = state.currentEncounterName.ifBlank {
			"Encounter Session - ${System.currentTimeMillis()}"
		},
		log = state.activeStatuses,
		snapshot = EncounterSnapshot(
			combatants = state.combatants,
			currentTurnIndex = state.currentTurnIndex,
			currentRound = state.currentRound
		)
	)
}

internal fun resolveEncounterId(explicitEncounterId: String?, currentEncounterId: String?): String {
	return explicitEncounterId ?: currentEncounterId ?: UUID.randomUUID().toString()
}

internal fun resolveEncounterName(existingEncounter: Encounter?, currentEncounterName: String): String {
	return existingEncounter?.name ?: currentEncounterName.ifBlank {
		"Quick Encounter ${System.currentTimeMillis()}"
	}
}

