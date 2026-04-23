/*
 * FILE: ClassInfo.kt
 * Class definitions, attribute recommendations, and helper calculations.
 */

package com.example.loreweaver.domain.model

import kotlin.math.floor
import kotlin.random.Random

data class ClassInfo(
	val displayName: String,
	val hitDie: Int,
	val primaryStats: List<String>,       // recommended primary attributes
	val defaultSaveProficiencies: Set<String>,
	val defaultSpellSlotsL1: Map<Int, Int> // spell level -> max slots at character level 1
)

val ALL_CLASSES: List<ClassInfo> = listOf(
	ClassInfo("Barbarian", 12, listOf("STR", "CON"), setOf("STR", "CON"), emptyMap()),
	ClassInfo("Bard", 8, listOf("CHA", "DEX"), setOf("DEX", "CHA"), mapOf(1 to 2)),
	ClassInfo("Cleric", 8, listOf("WIS", "CON"), setOf("WIS", "CHA"), mapOf(1 to 2)),
	ClassInfo("Druid", 8, listOf("WIS", "CON"), setOf("INT", "WIS"), mapOf(1 to 2)),
	ClassInfo("Fighter", 10, listOf("STR", "DEX"), setOf("STR", "CON"), emptyMap()),
	ClassInfo("Monk", 8, listOf("DEX", "WIS"), setOf("STR", "DEX"), emptyMap()),
	ClassInfo("Paladin", 10, listOf("STR", "CHA"), setOf("WIS", "CHA"), mapOf(1 to 2)),
	ClassInfo("Ranger", 10, listOf("DEX", "WIS"), setOf("STR", "DEX"), mapOf(1 to 2)),
	ClassInfo("Rogue", 8, listOf("DEX", "INT"), setOf("DEX", "INT"), emptyMap()),
	ClassInfo("Sorcerer", 6, listOf("CHA", "CON"), setOf("CON", "CHA"), mapOf(1 to 2)),
	ClassInfo("Warlock", 8, listOf("CHA", "CON"), setOf("WIS", "CHA"), mapOf(1 to 1)),
	ClassInfo("Wizard", 6, listOf("INT", "CON"), setOf("INT", "WIS"), mapOf(1 to 2)),
)

private const val DEFAULT_ALLOWED_CLASS_NAME = "Fighter"

private val LEGACY_CLASS_ALIASES = mapOf(
	"warrior" to "Fighter",
	"mage" to "Wizard",
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

/** Quick lookup — returns null for unknown class names */
fun classInfoFor(className: String): ClassInfo? =
	findAllowedClassInfo(normalizeClassName(className))

/**
 * Roll 4d6, drop the lowest die — a standard fifth-edition attribute generation method.
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
 * Calculate a simple mana pool: total number of max spell slots × 2.
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
