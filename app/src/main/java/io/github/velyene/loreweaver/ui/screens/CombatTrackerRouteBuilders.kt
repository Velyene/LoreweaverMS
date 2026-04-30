/*
 * FILE: CombatTrackerRouteBuilders.kt
 *
 * TABLE OF CONTENTS:
 * 1. Party combatant builder
 */

package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import kotlin.random.Random

internal fun buildPartyCombatant(character: CharacterEntry): CombatantState {
	// Party members receive a light initiative roll here so the setup flow can seed the
	// tracker immediately without a separate round of manual initiative entry.
	return CombatantState(
		character.id,
		character.name,
		character.initiative + Random.nextInt(
			CombatTrackerConstants.INITIATIVE_ROLL_MIN,
			CombatTrackerConstants.INITIATIVE_ROLL_MAX
		),
		character.hp,
		character.maxHp
	)
}

