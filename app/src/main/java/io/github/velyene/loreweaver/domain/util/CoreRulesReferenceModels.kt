package io.github.velyene.loreweaver.domain.util

/**
 * Summary card for a Core Rules topic.
 */
data class CoreRuleSection(
	val title: String,
	val summary: String,
	val bullets: List<String> = emptyList(),
	val keywords: List<String> = emptyList()
)

/**
 * Compact glossary entry used by the Core Rules reference UI.
 */
data class CoreGlossaryEntry(
	val term: String,
	val tag: String? = null,
	val summary: String,
	val bullets: List<String> = emptyList(),
	val seeAlso: List<String> = emptyList(),
	val keywords: List<String> = emptyList()
) {
	val title: String
		get() = if (tag.isNullOrBlank()) term else "$term [$tag]"

	fun matchesQuery(query: String): Boolean {
		if (query.isBlank()) return true

		return sequenceOf(title, summary)
			.plus(bullets.asSequence())
			.plus(seeAlso.asSequence())
			.plus(keywords.asSequence())
			.any { it.contains(query, ignoreCase = true) }
	}
}

internal const val ABILITY_CHECK = "Ability Check"
internal const val AREA_OF_EFFECT = "Area of Effect"
internal const val ARMOR_CLASS = "Armor Class"
internal const val ATTACK_ACTION = "Attack [Action]"
internal const val ATTACK_ROLL = "Attack Roll"
internal const val ATTITUDE_ATTITUDE = "Attitude [Attitude]"
internal const val BREAKING_OBJECTS = "Breaking Objects"
internal const val CHALLENGE_RATING = "Challenge Rating"
internal const val D20_TEST = "D20 Test"
internal const val DAMAGE_TYPES = "Damage Types"
internal const val DEATH_SAVING_THROW = "Death Saving Throw"
internal const val DIFFICULTY_CLASS = "Difficulty Class"
internal const val EXHAUSTION_CONDITION = "Exhaustion [Condition]"
internal const val FRIENDLY_ATTITUDE = "Friendly [Attitude]"
internal const val HIT_POINTS = "Hit Points"
internal const val INFLUENCE_ACTION = "Influence [Action]"
internal const val LONG_REST = "Long Rest"
internal const val MAGIC_ACTION = "Magic [Action]"
internal const val OPPORTUNITY_ATTACKS = "Opportunity Attacks"
internal const val SAVING_THROW = "Saving Throw"
internal const val SHORT_REST = "Short Rest"
internal const val STAT_BLOCK = "Stat Block"
internal const val TEMPORARY_HIT_POINTS = "Temporary Hit Points"
internal const val UNARMED_STRIKE = "Unarmed Strike"
internal const val UNCONSCIOUS_CONDITION = "Unconscious [Condition]"
internal const val WEAPON_ATTACK = "Weapon Attack"

