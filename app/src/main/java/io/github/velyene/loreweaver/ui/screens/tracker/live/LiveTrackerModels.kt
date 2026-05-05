/*
 * FILE: LiveTrackerModels.kt
 *
 * TABLE OF CONTENTS:
 * 1. Live Tracker UI Models
 * 2. Live Tracker Labels
 * 3. Participant Mapping Helpers
 * 4. Resource Formatting Helpers
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.util.CharacterParty
import io.github.velyene.loreweaver.ui.viewmodels.ActionResolutionType
import io.github.velyene.loreweaver.ui.viewmodels.CombatTurnStep
import io.github.velyene.loreweaver.ui.viewmodels.PendingTurnAction

internal data class LiveParticipantUiModel(
	val combatant: CombatantState,
	val typeLabel: String,
	val isPlayer: Boolean,
	val isEliminated: Boolean,
	val notes: String,
	val persistentConditions: Set<String>,
	val actionLabels: List<String>,
	val resourceLines: List<String>
)

internal data class LiveTrackerViewState(
	val encounterName: String,
	val encounterNotes: String,
	val round: Int,
	val combatants: List<CombatantState>,
	val availableCharacters: List<CharacterEntry>,
	val turnIndex: Int,
	val statuses: List<String>,
	val canGoToPreviousTurn: Boolean,
	val turnStep: CombatTurnStep,
	val pendingAction: PendingTurnAction?,
	val selectedTargetId: String?
)

internal data class LiveTrackerCallbacks(
	val onSelectAction: (String) -> Unit,
	val onSelectTarget: (String) -> Unit,
	val onApplyActionResult: (ActionResolutionType, Int?) -> Unit,
	val onClearPendingTurn: () -> Unit,
	val onPreviousTurn: () -> Unit,
	val onNextTurn: () -> Unit,
	val onAdvanceRound: () -> Unit,
	val onHpChange: (characterId: String, delta: Int) -> Unit,
	val onSetHp: (characterId: String, hp: Int) -> Unit,
	val onMarkDefeated: (characterId: String) -> Unit,
	val onAddParticipantNote: (characterId: String, note: String) -> Unit,
	val onDuplicateEnemy: (characterId: String) -> Unit,
	val onRemoveCombatant: (characterId: String) -> Unit,
	val onAddCondition: (characterId: String, condition: String, duration: Int?, persistsAcrossEncounters: Boolean) -> Unit,
	val onRemoveCondition: (characterId: String, conditionName: String) -> Unit,
	val onEnd: () -> Unit
)

internal data class CurrentParticipantPanelState(
	val encounterName: String,
	val participant: LiveParticipantUiModel?,
	val nextParticipant: LiveParticipantUiModel?,
	val canGoToPreviousTurn: Boolean,
	val pendingAction: PendingTurnAction?,
	val selectedTarget: LiveParticipantUiModel?,
	val turnStep: CombatTurnStep,
	val targetableParticipants: List<LiveParticipantUiModel>
)

internal data class LiveTrackerContentListState(
	val encounterName: String,
	val encounterNotes: String,
	val statuses: List<String>,
	val canGoToPreviousTurn: Boolean,
	val turnStep: CombatTurnStep,
	val pendingAction: PendingTurnAction?,
	val selectedTargetId: String?,
	val focusedCombatantId: String?,
	val isCompactBattleMode: Boolean,
	val participants: List<LiveParticipantUiModel>,
	val currentParticipant: LiveParticipantUiModel?,
	val nextParticipant: LiveParticipantUiModel?,
	val selectedTarget: LiveParticipantUiModel?,
	val targetableParticipants: List<LiveParticipantUiModel>,
	val selectableTargetIds: Set<String>,
	val rosterParticipants: List<LiveParticipantUiModel>
)

internal data class LiveTrackerLabels(
	val enemyLabel: String,
	val defaultActionLabels: List<String>,
	val tempHpLabel: String,
	val manaLabel: String,
	val staminaLabel: String
)

internal const val LIVE_TRACKER_ROOT_TAG = "live_tracker_root"
internal const val COMPACT_BATTLE_MODE_THRESHOLD = 8

@Composable
internal fun rememberLiveParticipants(
	combatants: List<CombatantState>,
	availableCharacters: List<CharacterEntry>,
	labels: LiveTrackerLabels
): List<LiveParticipantUiModel> {
	return remember(combatants, availableCharacters, labels) {
		buildLiveParticipants(
			combatants = combatants,
			availableCharacters = availableCharacters,
			labels = labels
		)
	}
}

@Composable
internal fun rememberLiveTrackerLabels(): LiveTrackerLabels {
	val enemyLabel = stringResource(R.string.encounter_turn_chip_enemy)
	val strikeLabel = stringResource(R.string.combat_action_strike)
	val castLabel = stringResource(R.string.combat_action_cast)
	val sneakLabel = stringResource(R.string.combat_action_sneak)
	val dodgeLabel = stringResource(R.string.combat_action_dodge)
	val defaultActionLabels = remember(strikeLabel, castLabel, sneakLabel, dodgeLabel) {
		listOf(strikeLabel, castLabel, sneakLabel, dodgeLabel)
	}
	return LiveTrackerLabels(
		enemyLabel = enemyLabel,
		defaultActionLabels = defaultActionLabels,
		tempHpLabel = stringResource(R.string.temp_hp_label),
		manaLabel = stringResource(R.string.mana_label),
		staminaLabel = stringResource(R.string.stamina_label)
	)
}

internal fun buildLiveParticipants(
	combatants: List<CombatantState>,
	availableCharacters: List<CharacterEntry>,
	labels: LiveTrackerLabels
): List<LiveParticipantUiModel> {
	val charactersById = availableCharacters.associateBy(CharacterEntry::id)
	return combatants.map { combatant ->
		val character = charactersById[combatant.characterId]
		val isPlayer = character?.party == CharacterParty.ADVENTURERS
					val customActionLabels = character
						?.actions
						?.map { it.name.trim() }
						?.filter(String::isNotBlank)
						.orEmpty()

																	// Context-sensitive filtering for special actions
																	val filteredActionLabels = customActionLabels.toMutableList()
																	character?.let {
																																							// Remove 'Use Item' if no items
																																							if (filteredActionLabels.contains("Use Item") && it.availableInventoryNames().isEmpty()) {
																			filteredActionLabels.remove("Use Item")
																		}
																		// Remove 'Cast Spell' if no spell slots
																		val hasSpells = it.spellSlots.isNotEmpty() && it.spellSlots.values.any { pair -> pair.second > 0 }
																		if (filteredActionLabels.contains("Cast Spell") && !hasSpells) {
																			filteredActionLabels.remove("Cast Spell")
																		}
																		// Remove 'Use Ability' if no resources
																		if (filteredActionLabels.contains("Use Ability") && (it.resources.isEmpty() || it.resources.all { r -> r.name.isBlank() })) {
																			filteredActionLabels.remove("Use Ability")
																		}
																	}

					LiveParticipantUiModel(
						combatant = combatant,
						typeLabel = character?.type?.takeIf(String::isNotBlank) ?: if (isPlayer) {
							CharacterParty.ADVENTURERS
						} else {
							labels.enemyLabel
						},
						isPlayer = isPlayer,
						isEliminated = combatant.currentHp <= 0,
						notes = character?.notes.orEmpty(),
						persistentConditions = character?.activeConditions.orEmpty(),
						actionLabels = if (filteredActionLabels.isNotEmpty()) filteredActionLabels else labels.defaultActionLabels,
						resourceLines = buildResourceLines(character, combatant, labels)
					)
	}
}

internal fun buildResourceLines(
	character: CharacterEntry?,
	combatant: CombatantState,
	labels: LiveTrackerLabels
): List<String> {
	return buildList {
		if (combatant.tempHp > 0) {
			add("${labels.tempHpLabel} ${combatant.tempHp}")
		}
		if (character != null) {
			if (character.maxMana > 0) {
				add("${labels.manaLabel} ${character.mana}/${character.maxMana}")
			}
			if (character.maxStamina > 0) {
				add("${labels.staminaLabel} ${character.stamina}/${character.maxStamina}")
			}
			character.resources.take(3).forEach { resource ->
				add("${resource.name} ${resource.current}/${resource.max}")
			}
		}
	}
}
