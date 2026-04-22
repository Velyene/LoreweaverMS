/*
 * FILE: EncounterDifficulty.kt
 *
 * TABLE OF CONTENTS:
 * 1. DifficultyRating enum and EncounterDifficultyResult model
 * 2. EncounterDifficulty singleton — XP thresholds, multiplier tables, and calculation logic
 */

package com.example.loreweaver.domain.util

import com.example.loreweaver.domain.model.CharacterEntry
import com.example.loreweaver.domain.model.CombatantState

/**
 * Represents the standard fifth-edition encounter difficulty categories.
 */
enum class DifficultyRating {
	TRIVIAL,  // Below Easy threshold
	EASY,
	MEDIUM,
	HARD,
	DEADLY,
	BEYOND_DEADLY  // Above Deadly threshold
}

/**
 * Data class to hold calculated encounter difficulty information.
 */
data class EncounterDifficultyResult(
	val rating: DifficultyRating,
	val adjustedXp: Int,
	val partyThresholds: Map<DifficultyRating, Int>,
	val partySize: Int,
	val averagePartyLevel: Int,
	val monsterCount: Int,
	val totalMonsterXp: Int,
	val multiplier: Double
)

/**
 * Utility object for calculating fifth-edition encounter difficulty.
 */
object EncounterDifficulty {

	/**
	 * XP thresholds by character level and difficulty band.
	 */
	private val XP_THRESHOLDS = mapOf(
		1 to mapOf(
			DifficultyRating.EASY to 25,
			DifficultyRating.MEDIUM to 50,
			DifficultyRating.HARD to 75,
			DifficultyRating.DEADLY to 100
		),
		2 to mapOf(
			DifficultyRating.EASY to 50,
			DifficultyRating.MEDIUM to 100,
			DifficultyRating.HARD to 150,
			DifficultyRating.DEADLY to 200
		),
		3 to mapOf(
			DifficultyRating.EASY to 75,
			DifficultyRating.MEDIUM to 150,
			DifficultyRating.HARD to 225,
			DifficultyRating.DEADLY to 400
		),
		4 to mapOf(
			DifficultyRating.EASY to 125,
			DifficultyRating.MEDIUM to 250,
			DifficultyRating.HARD to 375,
			DifficultyRating.DEADLY to 500
		),
		5 to mapOf(
			DifficultyRating.EASY to 250,
			DifficultyRating.MEDIUM to 500,
			DifficultyRating.HARD to 750,
			DifficultyRating.DEADLY to 1100
		),
		6 to mapOf(
			DifficultyRating.EASY to 300,
			DifficultyRating.MEDIUM to 600,
			DifficultyRating.HARD to 900,
			DifficultyRating.DEADLY to 1400
		),
		7 to mapOf(
			DifficultyRating.EASY to 350,
			DifficultyRating.MEDIUM to 750,
			DifficultyRating.HARD to 1100,
			DifficultyRating.DEADLY to 1700
		),
		8 to mapOf(
			DifficultyRating.EASY to 450,
			DifficultyRating.MEDIUM to 900,
			DifficultyRating.HARD to 1400,
			DifficultyRating.DEADLY to 2100
		),
		9 to mapOf(
			DifficultyRating.EASY to 550,
			DifficultyRating.MEDIUM to 1100,
			DifficultyRating.HARD to 1600,
			DifficultyRating.DEADLY to 2400
		),
		10 to mapOf(
			DifficultyRating.EASY to 600,
			DifficultyRating.MEDIUM to 1200,
			DifficultyRating.HARD to 1900,
			DifficultyRating.DEADLY to 2800
		),
		11 to mapOf(
			DifficultyRating.EASY to 800,
			DifficultyRating.MEDIUM to 1600,
			DifficultyRating.HARD to 2400,
			DifficultyRating.DEADLY to 3600
		),
		12 to mapOf(
			DifficultyRating.EASY to 1000,
			DifficultyRating.MEDIUM to 2000,
			DifficultyRating.HARD to 3000,
			DifficultyRating.DEADLY to 4500
		),
		13 to mapOf(
			DifficultyRating.EASY to 1100,
			DifficultyRating.MEDIUM to 2200,
			DifficultyRating.HARD to 3400,
			DifficultyRating.DEADLY to 5100
		),
		14 to mapOf(
			DifficultyRating.EASY to 1250,
			DifficultyRating.MEDIUM to 2500,
			DifficultyRating.HARD to 3800,
			DifficultyRating.DEADLY to 5700
		),
		15 to mapOf(
			DifficultyRating.EASY to 1400,
			DifficultyRating.MEDIUM to 2800,
			DifficultyRating.HARD to 4300,
			DifficultyRating.DEADLY to 6400
		),
		16 to mapOf(
			DifficultyRating.EASY to 1600,
			DifficultyRating.MEDIUM to 3200,
			DifficultyRating.HARD to 4800,
			DifficultyRating.DEADLY to 7200
		),
		17 to mapOf(
			DifficultyRating.EASY to 2000,
			DifficultyRating.MEDIUM to 3900,
			DifficultyRating.HARD to 5900,
			DifficultyRating.DEADLY to 8800
		),
		18 to mapOf(
			DifficultyRating.EASY to 2100,
			DifficultyRating.MEDIUM to 4200,
			DifficultyRating.HARD to 6300,
			DifficultyRating.DEADLY to 9500
		),
		19 to mapOf(
			DifficultyRating.EASY to 2400,
			DifficultyRating.MEDIUM to 4900,
			DifficultyRating.HARD to 7300,
			DifficultyRating.DEADLY to 10900
		),
		20 to mapOf(
			DifficultyRating.EASY to 2800,
			DifficultyRating.MEDIUM to 5700,
			DifficultyRating.HARD to 8500,
			DifficultyRating.DEADLY to 12700
		)
	)

	/**
	 * XP values by Challenge Rating.
	 */
	private val CR_TO_XP = mapOf(
		0.0 to 0,      // CR 0
		0.125 to 25,   // CR 1/8
		0.25 to 50,    // CR 1/4
		0.5 to 100,    // CR 1/2
		1.0 to 200,
		2.0 to 450,
		3.0 to 700,
		4.0 to 1100,
		5.0 to 1800,
		6.0 to 2300,
		7.0 to 2900,
		8.0 to 3900,
		9.0 to 5000,
		10.0 to 5900,
		11.0 to 7200,
		12.0 to 8400,
		13.0 to 10000,
		14.0 to 11500,
		15.0 to 13000,
		16.0 to 15000,
		17.0 to 18000,
		18.0 to 20000,
		19.0 to 22000,
		20.0 to 25000,
		21.0 to 33000,
		22.0 to 41000,
		23.0 to 50000,
		24.0 to 62000,
		25.0 to 75000,
		26.0 to 90000,
		27.0 to 105000,
		28.0 to 120000,
		29.0 to 135000,
		30.0 to 155000
	)

	/**
	 * Encounter multipliers based on the number of monsters, with party-size adjustments.
	 */
	private fun getMultiplier(monsterCount: Int, partySize: Int): Double {
		// Adjust multiplier table based on party size
		val useHigherMultiplier = partySize < 3
		val useLowerMultiplier = partySize > 5

		return when (monsterCount) {
			0 -> 1.0
			1 -> if (useHigherMultiplier) 1.5 else if (useLowerMultiplier) 0.5 else 1.0
			2 -> if (useHigherMultiplier) 2.0 else if (useLowerMultiplier) 1.0 else 1.5
			in 3..6 -> if (useHigherMultiplier) 2.5 else if (useLowerMultiplier) 1.5 else 2.0
			in 7..10 -> if (useHigherMultiplier) 3.0 else if (useLowerMultiplier) 2.0 else 2.5
			in 11..14 -> if (useHigherMultiplier) 4.0 else if (useLowerMultiplier) 2.5 else 3.0
			else -> if (useHigherMultiplier) 5.0 else if (useLowerMultiplier) 3.0 else 4.0
		}
	}

	/**
	 * Gets the XP value for a given Challenge Rating.
	 */
	fun getXpForCr(cr: Double): Int {
		return CR_TO_XP[cr] ?: 0
	}

	/**
	 * Calculates encounter difficulty based on party composition and enemies.
	 *
	 * @param partyMembers List of player characters (party == "Adventurers")
	 * @param enemies List of enemy combatants (should have CR information)
	 * @return EncounterDifficultyResult with full difficulty breakdown
	 */
	fun calculateDifficulty(
		partyMembers: List<CharacterEntry>,
		enemies: List<CombatantState>,
		enemyCRMap: Map<String, Double> = emptyMap()
	): EncounterDifficultyResult? {
		// Need at least one party member to calculate difficulty
		if (partyMembers.isEmpty()) return null

		val partySize = partyMembers.size
		val averageLevel = partyMembers.map { it.level }.average().toInt().coerceIn(1, 20)

		// Calculate party XP thresholds
		val partyThresholds = calculatePartyThresholds(partyMembers)

		// Calculate total monster XP
		val totalMonsterXp = enemies.sumOf { enemy ->
			val cr = enemyCRMap[enemy.characterId] ?: 0.0
			getXpForCr(cr)
		}

		// Apply multiplier based on number of monsters
		val monsterCount = enemies.size
		val multiplier = getMultiplier(monsterCount, partySize)
		val adjustedXp = (totalMonsterXp * multiplier).toInt()

		// Determine difficulty rating
		val rating = when {
			adjustedXp == 0 -> DifficultyRating.TRIVIAL
			adjustedXp < (partyThresholds[DifficultyRating.EASY] ?: 0) -> DifficultyRating.TRIVIAL
			adjustedXp < (partyThresholds[DifficultyRating.MEDIUM] ?: 0) -> DifficultyRating.EASY
			adjustedXp < (partyThresholds[DifficultyRating.HARD] ?: 0) -> DifficultyRating.MEDIUM
			adjustedXp < (partyThresholds[DifficultyRating.DEADLY] ?: 0) -> DifficultyRating.HARD
			adjustedXp < ((partyThresholds[DifficultyRating.DEADLY]
				?: 0) * 1.5) -> DifficultyRating.DEADLY

			else -> DifficultyRating.BEYOND_DEADLY
		}

		return EncounterDifficultyResult(
			rating = rating,
			adjustedXp = adjustedXp,
			partyThresholds = partyThresholds,
			partySize = partySize,
			averagePartyLevel = averageLevel,
			monsterCount = monsterCount,
			totalMonsterXp = totalMonsterXp,
			multiplier = multiplier
		)
	}

	/**
	 * Calculates XP thresholds for the entire party.
	 */
	private fun calculatePartyThresholds(partyMembers: List<CharacterEntry>): Map<DifficultyRating, Int> {
		val thresholds = mutableMapOf(
			DifficultyRating.EASY to 0,
			DifficultyRating.MEDIUM to 0,
			DifficultyRating.HARD to 0,
			DifficultyRating.DEADLY to 0
		)

		partyMembers.forEach { character ->
			val level = character.level.coerceIn(1, 20)
			val characterThresholds = XP_THRESHOLDS[level] ?: return@forEach

			characterThresholds.forEach { (rating, xp) ->
				thresholds[rating] = (thresholds[rating] ?: 0) + xp
			}
		}

		return thresholds
	}

	/**
	 * Get daily XP budget for a character at a given level.
	 */
	fun getDailyXpBudget(level: Int): Int {
		val budgetTable = mapOf(
			1 to 300, 2 to 600, 3 to 1200, 4 to 1700, 5 to 3500,
			6 to 4000, 7 to 5000, 8 to 6000, 9 to 7500, 10 to 9000,
			11 to 10500, 12 to 11500, 13 to 13500, 14 to 15000, 15 to 18000,
			16 to 20000, 17 to 25000, 18 to 27000, 19 to 30000, 20 to 40000
		)
		return budgetTable[level.coerceIn(1, 20)] ?: 300
	}

	/**
	 * Format difficulty rating as a human-readable string with color suggestion.
	 */
	fun formatDifficultyRating(rating: DifficultyRating): Pair<String, String> {
		return when (rating) {
			DifficultyRating.TRIVIAL -> "Trivial" to "Easy"
			DifficultyRating.EASY -> "Easy" to "Easy"
			DifficultyRating.MEDIUM -> "Medium" to "Medium"
			DifficultyRating.HARD -> "Hard" to "Hard"
			DifficultyRating.DEADLY -> "Deadly" to "Deadly"
			DifficultyRating.BEYOND_DEADLY -> "Beyond Deadly" to "Deadly"
		}
	}

	/**
	 * Get description for difficulty rating.
	 */
	fun getDifficultyDescription(rating: DifficultyRating): String {
		return when (rating) {
			DifficultyRating.TRIVIAL -> "Little risk. The party should handle it comfortably."
			DifficultyRating.EASY -> "Low pressure. The party is favored to win without much strain."
			DifficultyRating.MEDIUM -> "Manageable, but likely to create a few tense turns."
			DifficultyRating.HARD -> "A serious fight that can punish weak positioning or bad luck."
			DifficultyRating.DEADLY -> "High risk. Winning may cost major resources or fallen allies."
			DifficultyRating.BEYOND_DEADLY -> "Extreme danger. A full-party defeat is a real possibility."
		}
	}
}
