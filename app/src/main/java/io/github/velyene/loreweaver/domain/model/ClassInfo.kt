/*
 * FILE: ClassInfo.kt
 * Class definitions, attribute recommendations, and helper calculations.
 */

package io.github.velyene.loreweaver.domain.model

import kotlin.math.floor
import kotlin.random.Random

data class ClassInfo(
	val displayName: String,
	val hitDie: Int,
	val primaryStats: List<String>,       // recommended primary attributes
	val defaultSaveProficiencies: Set<String>,
	val defaultSpellSlotsL1: Map<Int, Int> // spell level -> max slots at character level 1
)

internal const val ATTRIBUTE_STRENGTH = "STR"
internal const val ATTRIBUTE_DEXTERITY = "DEX"
internal const val ATTRIBUTE_CONSTITUTION = "CON"
internal const val ATTRIBUTE_INTELLIGENCE = "INT"
internal const val ATTRIBUTE_WISDOM = "WIS"
internal const val ATTRIBUTE_CHARISMA = "CHA"

internal const val CLASS_FIGHTER = "Fighter"
internal const val CLASS_WIZARD = "Wizard"

private val NO_FIRST_LEVEL_SPELL_SLOTS = emptyMap<Int, Int>()
private val FIRST_LEVEL_FULL_CASTER_SLOTS = mapOf(1 to 2)
private val FIRST_LEVEL_WARLOCK_SLOTS = mapOf(1 to 1)

val ALL_CLASSES: List<ClassInfo> = listOf(
	ClassInfo("Barbarian", 12, listOf(ATTRIBUTE_STRENGTH, ATTRIBUTE_CONSTITUTION), setOf(ATTRIBUTE_STRENGTH, ATTRIBUTE_CONSTITUTION), emptyMap()),
	ClassInfo("Bard", 8, listOf(ATTRIBUTE_CHARISMA, ATTRIBUTE_DEXTERITY), setOf(ATTRIBUTE_DEXTERITY, ATTRIBUTE_CHARISMA), FIRST_LEVEL_FULL_CASTER_SLOTS),
	ClassInfo("Cleric", 8, listOf(ATTRIBUTE_WISDOM, ATTRIBUTE_CONSTITUTION), setOf(ATTRIBUTE_WISDOM, ATTRIBUTE_CHARISMA), FIRST_LEVEL_FULL_CASTER_SLOTS),
	ClassInfo("Druid", 8, listOf(ATTRIBUTE_WISDOM, ATTRIBUTE_CONSTITUTION), setOf(ATTRIBUTE_INTELLIGENCE, ATTRIBUTE_WISDOM), FIRST_LEVEL_FULL_CASTER_SLOTS),
	ClassInfo(DEFAULT_ALLOWED_CLASS_NAME, 10, listOf(ATTRIBUTE_STRENGTH, ATTRIBUTE_DEXTERITY), setOf(ATTRIBUTE_STRENGTH, ATTRIBUTE_CONSTITUTION), NO_FIRST_LEVEL_SPELL_SLOTS),
	ClassInfo("Monk", 8, listOf(ATTRIBUTE_DEXTERITY, ATTRIBUTE_WISDOM), setOf(ATTRIBUTE_STRENGTH, ATTRIBUTE_DEXTERITY), NO_FIRST_LEVEL_SPELL_SLOTS),
	ClassInfo("Paladin", 10, listOf(ATTRIBUTE_STRENGTH, ATTRIBUTE_CHARISMA), setOf(ATTRIBUTE_WISDOM, ATTRIBUTE_CHARISMA), FIRST_LEVEL_FULL_CASTER_SLOTS),
	ClassInfo("Ranger", 10, listOf(ATTRIBUTE_DEXTERITY, ATTRIBUTE_WISDOM), setOf(ATTRIBUTE_STRENGTH, ATTRIBUTE_DEXTERITY), FIRST_LEVEL_FULL_CASTER_SLOTS),
	ClassInfo("Rogue", 8, listOf(ATTRIBUTE_DEXTERITY, ATTRIBUTE_INTELLIGENCE), setOf(ATTRIBUTE_DEXTERITY, ATTRIBUTE_INTELLIGENCE), NO_FIRST_LEVEL_SPELL_SLOTS),
	ClassInfo("Sorcerer", 6, listOf(ATTRIBUTE_CHARISMA, ATTRIBUTE_CONSTITUTION), setOf(ATTRIBUTE_CONSTITUTION, ATTRIBUTE_CHARISMA), FIRST_LEVEL_FULL_CASTER_SLOTS),
	ClassInfo("Warlock", 8, listOf(ATTRIBUTE_CHARISMA, ATTRIBUTE_CONSTITUTION), setOf(ATTRIBUTE_WISDOM, ATTRIBUTE_CHARISMA), FIRST_LEVEL_WARLOCK_SLOTS),
	ClassInfo(CLASS_WIZARD, 6, listOf(ATTRIBUTE_INTELLIGENCE, ATTRIBUTE_CONSTITUTION), setOf(ATTRIBUTE_INTELLIGENCE, ATTRIBUTE_WISDOM), FIRST_LEVEL_FULL_CASTER_SLOTS),
)

private const val DEFAULT_ALLOWED_CLASS_NAME = CLASS_FIGHTER

private val LEGACY_CLASS_ALIASES = mapOf(
	"warrior" to DEFAULT_ALLOWED_CLASS_NAME,
	"mage" to CLASS_WIZARD,
	"enemy" to DEFAULT_ALLOWED_CLASS_NAME
)

private fun findAllowedClassInfo(className: String): ClassInfo? =
	ALL_CLASSES.firstOrNull { it.displayName.equals(className, ignoreCase = true) }

fun normalizeClassName(className: String): String {
	val trimmedName = className.trim()
	if (trimmedName.isBlank()) return DEFAULT_ALLOWED_CLASS_NAME

	findAllowedClassInfo(trimmedName)?.let { return it.displayName }

	val aliasedName =
		LEGACY_CLASS_ALIASES[trimmedName.lowercase()] ?: return DEFAULT_ALLOWED_CLASS_NAME
	return findAllowedClassInfo(aliasedName)?.displayName ?: DEFAULT_ALLOWED_CLASS_NAME
}

/** Quick lookup - returns null for unknown class names */
fun classInfoFor(className: String): ClassInfo? =
	findAllowedClassInfo(normalizeClassName(className))

/**
 * Roll 4d6, drop the lowest die - a standard fifth-edition attribute generation method.
 */
fun roll4d6DropLowest(): Int {
	val rolls = List(4) { Random.nextInt(1, 7) }.sorted()
	return rolls.drop(1).sum() // drop the lowest
}

/**
 * Calculate max HP for a character from their class, level, and CON score.
 * Level 1: full hit die + CON modifier.
 * Each subsequent level: average hit die (die/2 + 1) + CON modifier.
 */
fun calcMaxHp(hitDie: Int, level: Int, conScore: Int): Int {
	val conMod = floor((conScore - 10) / 2.0).toInt()
	val level1 = hitDie + conMod
	val higherLevels = (level - 1) * ((hitDie / 2 + 1) + conMod)
	return (level1 + higherLevels).coerceAtLeast(level)
}

/**
 * Calculate a simple mana pool: total number of max spell slots x 2.
 * Returns 0 for non-casters.
 */
fun calcMaxMana(classInfo: ClassInfo?, level: Int): Int {
	if (classInfo == null || classInfo.defaultSpellSlotsL1.isEmpty()) return 0
	// Scale slots linearly with level (capped at a simple multiplier for now)
	val levelMultiplier = when {
		level >= 17 -> 5
		level >= 11 -> 4
		level >= 5 -> 3
		level >= 3 -> 2
		else -> 1
	}
	return classInfo.defaultSpellSlotsL1.values.sum() * levelMultiplier * 2
}
