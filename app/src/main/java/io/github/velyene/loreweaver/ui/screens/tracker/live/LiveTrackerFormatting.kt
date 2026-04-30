/*
 * FILE: LiveTrackerFormatting.kt
 *
 * TABLE OF CONTENTS:
 * 1. Participant mapping helpers
 * 2. Resource formatting helpers
 * 3. Turn window helpers
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.util.CharacterParty

private const val stringLiteralEnemy = "Enemy"
private val defaultActionLabels = listOf("Strike", "Cast", "Sneak", "Dodge")

internal fun buildLiveParticipants(
	combatants: List<CombatantState>,
	availableCharacters: List<CharacterEntry>
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
		LiveParticipantUiModel(
			combatant = combatant,
			typeLabel = character?.type?.takeIf(String::isNotBlank) ?: if (isPlayer) {
				CharacterParty.ADVENTURERS
			} else {
				stringLiteralEnemy
			},
			isPlayer = isPlayer,
			isEliminated = combatant.currentHp <= 0,
			notes = character?.notes.orEmpty(),
			persistentConditions = character?.activeConditions.orEmpty(),
			actionLabels = if (customActionLabels.isNotEmpty()) customActionLabels else defaultActionLabels,
			resourceLines = buildResourceLines(character, combatant)
		)
	}
}

private fun buildResourceLines(
	character: CharacterEntry?,
	combatant: CombatantState
): List<String> {
	return buildList {
		if (combatant.tempHp > 0) {
			add("Temp HP ${combatant.tempHp}")
		}
		if (character != null) {
			if (character.maxMana > 0) {
				add("Mana ${character.mana}/${character.maxMana}")
			}
			if (character.maxStamina > 0) {
				add("Stamina ${character.stamina}/${character.maxStamina}")
			}
			character.resources.take(3).forEach { resource ->
				add("${resource.name} ${resource.current}/${resource.max}")
			}
		}
	}
}

internal fun upcomingTurnWindow(
	participants: List<LiveParticipantUiModel>,
	turnIndex: Int,
	windowSize: Int = 6
): List<LiveParticipantUiModel> {
	if (participants.isEmpty()) return emptyList()
	return List(minOf(windowSize, participants.size)) { offset ->
		participants[(turnIndex + offset).floorMod(participants.size)]
	}
}

private fun Int.floorMod(divisor: Int): Int = ((this % divisor) + divisor) % divisor

