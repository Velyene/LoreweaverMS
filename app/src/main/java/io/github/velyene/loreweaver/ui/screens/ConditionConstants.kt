package io.github.velyene.loreweaver.ui.screens

import androidx.annotation.StringRes
import io.github.velyene.loreweaver.R

data class ConditionOption(
	val name: String,
	@field:StringRes val labelRes: Int
)

/**
 * Standard fifth-edition conditions and common status effects.
 */
object ConditionConstants {
	/**
	 * Canonical condition keys stay in English because the combat model and rule helpers compare
	 * against stable names, while the dialog renders a localized label from the resource ID.
	 */
	val STANDARD_CONDITIONS = listOf(
		ConditionOption("Blinded", R.string.cond_blinded),
		ConditionOption("Charmed", R.string.cond_charmed),
		ConditionOption("Deafened", R.string.cond_deafened),
		ConditionOption("Frightened", R.string.cond_frightened),
		ConditionOption("Grappled", R.string.cond_grappled),
		ConditionOption("Incapacitated", R.string.cond_incapacitated),
		ConditionOption("Invisible", R.string.cond_invisible),
		ConditionOption("Paralyzed", R.string.cond_paralyzed),
		ConditionOption("Petrified", R.string.cond_petrified),
		ConditionOption("Poisoned", R.string.cond_poisoned),
		ConditionOption("Prone", R.string.cond_prone),
		ConditionOption("Restrained", R.string.cond_restrained),
		ConditionOption("Stunned", R.string.cond_stunned),
		ConditionOption("Unconscious", R.string.cond_unconscious)
	)

	/**
	 * Common additional status effects used by the app.
	 */
	val COMMON_CONDITIONS = listOf(
		ConditionOption("Bleeding", R.string.cond_bleeding),
		ConditionOption("Blessed", R.string.cond_blessed),
		ConditionOption("Burning", R.string.cond_burning),
		ConditionOption("Concentrating", R.string.cond_concentrating),
		ConditionOption("Cursed", R.string.cond_cursed),
		ConditionOption("Dodging", R.string.cond_dodging),
		ConditionOption("Hasted", R.string.cond_hasted),
		ConditionOption("Hexed", R.string.cond_hexed),
		ConditionOption("Hidden", R.string.cond_hidden),
		ConditionOption("Inspired", R.string.cond_inspired),
		ConditionOption("Marked", R.string.cond_marked),
		ConditionOption("Raging", R.string.cond_raging),
		ConditionOption("Slowed", R.string.cond_slowed)
	)

	/**
	 * Combined list of all conditions for selection.
	 */
	val ALL_CONDITIONS = (STANDARD_CONDITIONS + COMMON_CONDITIONS).sortedBy(ConditionOption::name)
}
