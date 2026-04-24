/*
 * FILE: ObjectStats.kt
 *
 * TABLE OF CONTENTS:
 * 1. ObjectSize enum and ObjectStatBlock model
 * 2. ObjectStats singleton â€” AC, HP, and substance threshold tables
 */

package io.github.velyene.loreweaver.domain.util

/**
 * Object size categories for HP calculation.
 */
enum class ObjectSize {
	TINY,     // Bottle, lock
	SMALL,    // Chest, lute
	MEDIUM,   // Barrel, chandelier
	LARGE,    // Cart, 10x10 window
	HUGE      // Large sections and massive structures
}

/**
 * Object resilience categories.
 */
enum class ObjectResilience {
	FRAGILE,   // Easy to break
	RESILIENT  // Tough to break
}

/**
 * Data class for object statistics.
 */
data class ObjectStats(
	val substance: String,
	val ac: Int,
	val size: ObjectSize,
	val resilience: ObjectResilience,
	val hitPoints: String,
	val damageThreshold: Int = 0,
	val immunities: List<String> = listOf("Poison", "Psychic"),
	val vulnerabilities: List<String> = emptyList(),
	val resistances: List<String> = emptyList()
)

/**
 * Utility object for quick object durability estimates.
 */
object ObjectStatsReference {

	private const val HP_LARGE_RESILIENT = "27 (5d10)"

	/**
	 * Armor Class by substance.
	 */
	private val SUBSTANCE_AC = mapOf(
		"Cloth" to 11,
		"Paper" to 11,
		"Rope" to 11,
		"Crystal" to 13,
		"Glass" to 13,
		"Ice" to 13,
		"Wood" to 15,
		"Bone" to 15,
		"Stone" to 17,
		"Iron" to 19,
		"Steel" to 19,
		"Mithral" to 21,
		"Adamantine" to 23
	)

	/**
	 * Hit points by size and resilience.
	 */
	private val SIZE_HP = mapOf(
		ObjectSize.TINY to mapOf(
			ObjectResilience.FRAGILE to "2 (1d4)",
			ObjectResilience.RESILIENT to "5 (2d4)"
		),
		ObjectSize.SMALL to mapOf(
			ObjectResilience.FRAGILE to "3 (1d6)",
			ObjectResilience.RESILIENT to "10 (3d6)"
		),
		ObjectSize.MEDIUM to mapOf(
			ObjectResilience.FRAGILE to "4 (1d8)",
			ObjectResilience.RESILIENT to "18 (4d8)"
		),
		ObjectSize.LARGE to mapOf(
			ObjectResilience.FRAGILE to "5 (1d10)",
			ObjectResilience.RESILIENT to "27 (5d10)"
		)
	)

	/**
	 * Get AC for a substance.
	 */
	fun getAC(substance: String): Int {
		return SUBSTANCE_AC.entries.find {
			substance.contains(it.key, ignoreCase = true)
		}?.value ?: 15 // Default to wood/bone
	}

	/**
	 * Get HP for size and resilience.
	 */
	fun getHP(size: ObjectSize, resilience: ObjectResilience): String {
		return SIZE_HP[size]?.get(resilience) ?: "1 (1d4)"
	}

	/**
	 * Common object examples.
	 */
	val COMMON_OBJECTS = listOf(
		ObjectStats("Rope (10 ft)", 11, ObjectSize.SMALL, ObjectResilience.FRAGILE, "2 (1d4)"),
		ObjectStats("Cloth (sheet)", 11, ObjectSize.MEDIUM, ObjectResilience.FRAGILE, "4 (1d8)"),
		ObjectStats("Glass Window", 13, ObjectSize.LARGE, ObjectResilience.FRAGILE, "5 (1d10)"),
		ObjectStats("Wooden Door", 15, ObjectSize.LARGE, ObjectResilience.RESILIENT, HP_LARGE_RESILIENT),
		ObjectStats("Treasure Chest", 15, ObjectSize.SMALL, ObjectResilience.RESILIENT, "10 (3d6)"),
		ObjectStats(
			"Stone Wall (section)",
			17,
			ObjectSize.HUGE,
			ObjectResilience.RESILIENT,
			"Varies",
			damageThreshold = 10
		),
		ObjectStats(
			"Iron Door",
			19,
			ObjectSize.LARGE,
			ObjectResilience.RESILIENT,
			HP_LARGE_RESILIENT,
			damageThreshold = 10
		),
		ObjectStats("Iron Bars", 19, ObjectSize.MEDIUM, ObjectResilience.RESILIENT, "18 (4d8)"),
		ObjectStats(
			"Adamantine Door",
			23,
			ObjectSize.LARGE,
			ObjectResilience.RESILIENT,
			HP_LARGE_RESILIENT,
			damageThreshold = 20
		)
	)

	/**
	 * Get damage type effectiveness guidance.
	 */
	fun getDamageTypeGuidance(): Map<String, String> {
		return mapOf(
			"Bludgeoning" to "Best for crushing or breaking rigid objects, but poor at slicing softer material.",
			"Slashing" to "Best for cutting rope, cloth, leather, and similar flexible material.",
			"Piercing" to "Useful for punching through thin surfaces, but less helpful when broad structural damage is needed.",
			"Fire" to "Excellent against flammable material and much less impressive against heavy stone or metal.",
			"Lightning" to "Works well on fragile material and can still arc through metal fittings or mechanisms.",
			"Acid" to "A steady answer for corrosion and slow material breakdown.",
			"Force" to "A dependable damage type when you do not want to care about material differences.",
			"Thunder" to "Particularly dramatic against brittle surfaces such as glass or crystal."
		)
	}

	/**
	 * Huge/Gargantuan object guidance.
	 */
	fun getLargeObjectGuidance(): String {
		return buildString {
			appendLine("Handling Massive Objects:")
			appendLine()
			appendLine(
				"â€¢ For extremely large targets, it is often easier to break the object into sections than to " +
					"treat it as one pool of hit points."
			)
			appendLine(
				"â€¢ Each section can have its own AC, hit points, and threshold if the scene needs that level " +
					"of detail."
			)
			appendLine(
				"â€¢ Ruining one critical section can be enough to topple or disable the whole structure."
			)
			appendLine()
			appendLine("Loose Adjudication Option:")
			appendLine(
				"â€¢ If tracking damage would slow the scene down, decide narratively how much punishment the " +
					"object can take."
			)
			append(
				"â€¢ Fragile oversized objects may fail quickly, while reinforced structures might require siege " +
					"tools, repeated attacks, or magic."
			)
		}
	}

	/**
	 * Damage threshold guidance.
	 */
	fun getDamageThresholdInfo(): String {
		return buildString {
			appendLine("Damage Thresholds:")
			appendLine()
			appendLine("â€¢ Some sturdy objects ignore small hits entirely.")
			appendLine(
				"â€¢ If a single attack or effect does not meet the threshold, treat the impact as cosmetic " +
					"rather than structural."
			)
			appendLine("â€¢ Once an attack clears the threshold, apply damage normally.")
			append(
				"â€¢ Thresholds around 5 to 10 fit sturdy gear and doors, while truly massive structures can " +
					"justify higher numbers."
			)
		}
	}
}
