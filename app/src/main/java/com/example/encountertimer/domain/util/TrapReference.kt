/*
 * FILE: TrapReference.kt
 *
 * TABLE OF CONTENTS:
 * 1. TrapDanger and TrapType enums, TrapTemplate model
 * 2. TrapReference singleton — trap dataset and search utilities
 */

package com.example.encountertimer.domain.util

/**
 * Broad danger bands for encounter-building and hazard notes.
 */
enum class TrapDanger {
	SETBACK,    // Unlikely to kill or seriously harm
	DANGEROUS,  // Likely to seriously injure
	DEADLY      // Likely to kill
}

/**
 * Trap activation types.
 */
enum class TrapTrigger {
	PRESSURE_PLATE,
	TRIP_WIRE,
	TOUCH,
	PROXIMITY,
	PASSWORD,
	LOCK
}

/**
 * Compact trap summary used by the in-app rules reference.
 */
data class TrapTemplate(
	val name: String,
	val type: String, // "Mechanical" or "Magic"
	val danger: TrapDanger,
	val trigger: TrapTrigger,
	val detectionDC: Int,
	val disarmDC: Int,
	val saveDC: Int,
	val attackBonus: Int,
	val damage: String,
	val damageType: String,
	val effect: String,
	val description: String,
	val disarmMethod: String = "Thieves' tools"
)

/**
 * Utility object for trap reference data.
 */
object TrapReference {
	/**
	 * Get save DC based on trap danger level.
	 */
	fun getSaveDC(danger: TrapDanger): IntRange {
		return when (danger) {
			TrapDanger.SETBACK -> 10..11
			TrapDanger.DANGEROUS -> 12..15
			TrapDanger.DEADLY -> 16..20
		}
	}

	/**
	 * Get attack bonus based on trap danger level.
	 */
	fun getAttackBonus(danger: TrapDanger): IntRange {
		return when (danger) {
			TrapDanger.SETBACK -> 3..5
			TrapDanger.DANGEROUS -> 6..8
			TrapDanger.DEADLY -> 9..12
		}
	}

	/**
	 * Get damage dice based on character level and danger.
	 */
	fun getDamage(characterLevel: Int, danger: TrapDanger): String {
		return when {
			characterLevel <= 4 -> when (danger) {
				TrapDanger.SETBACK -> "1d10"
				TrapDanger.DANGEROUS -> "2d10"
				TrapDanger.DEADLY -> "4d10"
			}

			characterLevel <= 10 -> when (danger) {
				TrapDanger.SETBACK -> "2d10"
				TrapDanger.DANGEROUS -> "4d10"
				TrapDanger.DEADLY -> "10d10"
			}

			characterLevel <= 16 -> when (danger) {
				TrapDanger.SETBACK -> "4d10"
				TrapDanger.DANGEROUS -> "10d10"
				TrapDanger.DEADLY -> "18d10"
			}

			else -> when (danger) {
				TrapDanger.SETBACK -> "10d10"
				TrapDanger.DANGEROUS -> "18d10"
				TrapDanger.DEADLY -> "24d10"
			}
		}
	}

	/**
	 * Canonical Gameplay Toolbox trap write-ups that summarize common fantasy hazards.
	 */
	val TEMPLATES = listOf(
		TrapTemplate(
			name = "Collapsing Roof",
			type = "Mechanical",
			danger = TrapDanger.DANGEROUS,
			trigger = TrapTrigger.TRIP_WIRE,
			detectionDC = 11,
			disarmDC = 0,
			saveDC = 13,
			attackBonus = 0,
			damage = "2d10",
			damageType = "Bludgeoning",
			effect =
				"Each creature beneath the unstable ceiling makes a Dexterity save, taking half damage on a " +
					"success. Rubble makes the area Difficult Terrain.",
			description = "A trip wire collapses an unstable section of ceiling onto the area below.",
			disarmMethod = "Cut or avoid the trip wire once spotted"
		),
		TrapTemplate(
			name = "Falling Net",
			type = "Mechanical",
			danger = TrapDanger.SETBACK,
			trigger = TrapTrigger.TRIP_WIRE,
			detectionDC = 11,
			disarmDC = 0,
			saveDC = 10,
			attackBonus = 0,
			damage = "0",
			damageType = "None",
			effect =
				"The target is Restrained until freed with a DC 10 Strength (Athletics) check or until the net is " +
					"destroyed.",
			description = "A weighted net drops from above when a hidden trip wire is crossed.",
			disarmMethod = "Cut or avoid the trip wire once spotted"
		),
		TrapTemplate(
			name = "Fire-Casting Statue",
			type = "Magic",
			danger = TrapDanger.DANGEROUS,
			trigger = TrapTrigger.PRESSURE_PLATE,
			detectionDC = 10,
			disarmDC = 15,
			saveDC = 15,
			attackBonus = 0,
			damage = "2d10",
			damageType = "Fire",
			effect =
				"When the linked plate is triggered, the statue exhales a 15-foot Cone of magical flame; a " +
					"Dexterity save halves the damage.",
			description =
				"A nearby statue releases magical flame when a creature steps onto the pressure plate, then resets " +
					"at the start of the next turn.",
			disarmMethod = "Deface the fire glyph, or wedge an Iron Spike under the pressure plate"
		),
		TrapTemplate(
			name = "Hidden Pit",
			type = "Mechanical",
			danger = TrapDanger.SETBACK,
			trigger = TrapTrigger.PRESSURE_PLATE,
			detectionDC = 15,
			disarmDC = 0,
			saveDC = 0,
			attackBonus = 0,
			damage = "1d6",
			damageType = "Bludgeoning",
			effect = "A creature that moves onto the disguised lid falls 10 feet into the pit when it swings open.",
			description = "A hidden trapdoor lid drops a creature into a 10-foot pit and remains open afterward.",
			disarmMethod = "Wedge the lid shut with an Iron Spike or secure it with similar magic"
		),
		TrapTemplate(
			name = "Spiked Pit",
			type = "Mechanical",
			danger = TrapDanger.DANGEROUS,
			trigger = TrapTrigger.PRESSURE_PLATE,
			detectionDC = 15,
			disarmDC = 0,
			saveDC = 0,
			attackBonus = 0,
			damage = "1d6 + 2d8",
			damageType = "Bludgeoning + Piercing",
			effect =
				"A creature that falls through the lid takes bludgeoning damage from the fall and piercing damage " +
					"from the spikes below.",
			description = "A concealed pit drops the victim onto sharpened spikes at the bottom.",
			disarmMethod = "Wedge the lid shut with an Iron Spike or similar object"
		),
		TrapTemplate(
			name = "Poisoned Darts",
			type = "Mechanical",
			danger = TrapDanger.DANGEROUS,
			trigger = TrapTrigger.PRESSURE_PLATE,
			detectionDC = 15,
			disarmDC = 0,
			saveDC = 13,
			attackBonus = 0,
			damage = "1d3 darts × 1d6",
			damageType = "Poison",
			effect =
				"Each creature in the darts' path makes a Dexterity save or is struck by 1d3 darts, taking 1d6 " +
					"Poison damage per dart.",
			description =
				"Hidden wall tubes fire poisoned darts when the pressure plate is triggered and can reset up to " +
					"three times.",
			disarmMethod = "Plug the dart holes, or wedge an Iron Spike under the pressure plate"
		),
		TrapTemplate(
			name = "Poisoned Needle",
			type = "Mechanical",
			danger = TrapDanger.SETBACK,
			trigger = TrapTrigger.LOCK,
			detectionDC = 15,
			disarmDC = 15,
			saveDC = 11,
			attackBonus = 0,
			damage = "1d10",
			damageType = "Poison",
			effect =
				"Opening the lock improperly forces a Constitution save; failure deals poison damage and leaves " +
					"the target Poisoned for 1 hour.",
			description = "A tiny poison-coated needle springs out of a trapped lock when it is handled incorrectly.",
			disarmMethod = "Detect the needle, then remove it with a DC 15 Dexterity (Sleight of Hand) check"
		),
		TrapTemplate(
			name = "Rolling Stone",
			type = "Mechanical",
			danger = TrapDanger.DEADLY,
			trigger = TrapTrigger.PRESSURE_PLATE,
			detectionDC = 15,
			disarmDC = 15,
			saveDC = 15,
			attackBonus = 0,
			damage = "10d10",
			damageType = "Bludgeoning",
			effect =
				"The stone rolls Initiative with a +8 bonus, moves 60 feet on its turn, and forces a Dexterity " +
					"save or 10d10 bludgeoning damage plus Prone.",
			description = "A massive stone is released and barrels down the corridor until it hits a barrier.",
			disarmMethod =
				"Wedge an Iron Spike under the pressure plate, or slow the stone with a DC 20 Strength " +
					"(Athletics) check"
		)
	)

	fun matchesSearchQuery(trap: TrapTemplate, query: String): Boolean {
		if (query.isBlank()) return true

		return sequenceOf(
			trap.name,
			trap.type,
			trap.description,
			trap.effect,
			trap.disarmMethod,
			trap.damage,
			trap.damageType,
			trap.trigger.name
		).any { it.contains(query, ignoreCase = true) }
	}


	/**
	 * Get trap template by name.
	 */
	fun getTemplateByName(name: String): TrapTemplate? {
		return TEMPLATES.find { it.name.equals(name, ignoreCase = true) }
	}
}
