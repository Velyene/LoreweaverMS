package com.example.loreweaver.ui.screens

/**
 * Standard fifth-edition conditions and common status effects.
 */
object ConditionConstants {
	/**
	 * The 14 standard conditions.
	 */
	val STANDARD_CONDITIONS = listOf(
		"Blinded",
		"Charmed",
		"Deafened",
		"Frightened",
		"Grappled",
		"Incapacitated",
		"Invisible",
		"Paralyzed",
		"Petrified",
		"Poisoned",
		"Prone",
		"Restrained",
		"Stunned",
		"Unconscious"
	)

	/**
	 * Common additional status effects used by the app.
	 */
	val COMMON_CONDITIONS = listOf(
		"Bleeding",
		"Blessed",
		"Burning",
		"Concentrating",
		"Cursed",
		"Dodging",
		"Hasted",
		"Hexed",
		"Hidden",
		"Inspired",
		"Marked",
		"Raging",
		"Slowed"
	)

	/**
	 * Combined list of all conditions for selection.
	 */
	val ALL_CONDITIONS = (STANDARD_CONDITIONS + COMMON_CONDITIONS).sorted()
}
