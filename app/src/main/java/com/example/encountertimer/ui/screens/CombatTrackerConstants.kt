package com.example.encountertimer.ui.screens

/**
 * Constants for CombatTrackerScreen to avoid magic numbers.
 * Addresses SonarQube S109 (magic numbers) issues.
 */
object CombatTrackerConstants {
	// Default enemy values
	const val DEFAULT_ENEMY_HP = 10
	const val DEFAULT_ENEMY_INITIATIVE = 10

	// Random initiative range
	const val INITIATIVE_ROLL_MIN = 1
	const val INITIATIVE_ROLL_MAX = 21

	// HP adjustment quick values
	const val HP_QUICK_DAMAGE_SMALL = -1
	const val HP_QUICK_DAMAGE_LARGE = -5
	const val HP_QUICK_HEAL_SMALL = 1
	const val HP_QUICK_HEAL_LARGE = 5

	val HP_QUICK_ADJUSTMENTS = listOf(

		HP_QUICK_DAMAGE_LARGE,
		HP_QUICK_DAMAGE_SMALL,
		HP_QUICK_HEAL_SMALL,
		HP_QUICK_HEAL_LARGE

	)

	// Layout constants
	const val NOTES_HEIGHT_DP = 100
	const val LOG_HEIGHT_DP = 80
	const val SETUP_BUTTON_HEIGHT_DP = 56
	const val ACTION_BUTTON_HEIGHT_DP = 48
	const val QUICK_HP_BUTTON_HEIGHT_DP = 36
}

