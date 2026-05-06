/*
 * FILE: LiveTrackerStateDerivation.kt
 *
 * TABLE OF CONTENTS:
 * 1. Derived live-tracker UI state
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.ui.viewmodels.PendingTurnAction

@Composable
internal fun rememberLiveTrackerUiState(
	combatants: List<CombatantState>,
	availableCharacters: List<CharacterEntry>,
	turnIndex: Int,
	pendingAction: PendingTurnAction?,
	selectedTargetId: String?
): LiveTrackerUiState {
	val labels = rememberLiveTrackerLabels()
	val participants = rememberLiveParticipants(
		combatants = combatants,
		availableCharacters = availableCharacters,
		labels = labels
	)
	return remember(participants, turnIndex, pendingAction, selectedTargetId) {
		val currentParticipant = participants.getOrNull(turnIndex)
		val targetableParticipants = participants.filter { participant ->
			when {
				currentParticipant == null -> false
				pendingAction == null -> false
				pendingAction.allowsSelfTarget -> !participant.isEliminated || participant.combatant.characterId == currentParticipant.combatant.characterId
				else -> participant.combatant.characterId != currentParticipant.combatant.characterId && !participant.isEliminated
			}
		}
		val selectedTarget = participants.firstOrNull { it.combatant.characterId == selectedTargetId }
			?: if (pendingAction?.allowsSelfTarget == true) currentParticipant else null
		val partyMembers = participants.filter(LiveParticipantUiModel::isPlayer)
		LiveTrackerUiState(
			participants = participants,
			currentParticipant = currentParticipant,
			selectedTarget = selectedTarget,
			targetableParticipants = targetableParticipants,
			selectableTargetIds = targetableParticipants.map { it.combatant.characterId }.toSet(),
			secondaryPartyMembers = partyMembers.filterNot {
				it.combatant.characterId == currentParticipant?.combatant?.characterId
			},
			enemies = participants.filterNot(LiveParticipantUiModel::isPlayer)
		)
	}
}
