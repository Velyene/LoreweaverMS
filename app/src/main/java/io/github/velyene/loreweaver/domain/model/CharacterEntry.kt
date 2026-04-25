/*
 * FILE: CharacterEntry.kt
 *
 * TABLE OF CONTENTS:
 * 1. Character Data Model (CharacterEntry)
 * 2. Mechanical Logic (Modifiers, Proficiencies, Rests)
 */

package io.github.velyene.loreweaver.domain.model

import io.github.velyene.loreweaver.domain.util.CharacterParty
import java.util.UUID
import kotlin.math.floor

// ==========================================================
// 1. Character Data Model
// ==========================================================

/**
 * The primary data entity representing a fifth-edition-style character or monster.
 * Stores attributes, resources, inventory, and session-specific state.
 */
data class CharacterEntry(
	val id: String = UUID.randomUUID().toString(),
	var name: String = "",
	var type: String = "",
	var party: String = CharacterParty.ADVENTURERS, // Grouping field for encounter management
	var hp: Int = 0,
	var maxHp: Int = 0,
	var tempHp: Int = 0, // NEW: Temporary Hit Points
	var mana: Int = 0,
	var maxMana: Int = 0,
	var stamina: Int = 0,
	var maxStamina: Int = 0,
	var ac: Int = 10,
	var speed: Int = 30, // NEW: Character Speed in feet
	var initiative: Int = 0,
	var status: String = "",
	var notes: String = "",
	var deathSaveSuccesses: Int = 0,
	var deathSaveFailures: Int = 0,

	// --- Attributes & Progression ---
	var level: Int = 1,
	var challengeRating: Double = 0.0, // CR for monsters (0 for player characters)
	var strength: Int = 10,
	var dexterity: Int = 10,
	var constitution: Int = 10,
	var intelligence: Int = 10,
	var wisdom: Int = 10,
	var charisma: Int = 10,

	// --- Proficiencies & Skills ---
	var proficiencies: Set<String> = emptySet(),
	var saveProficiencies: Set<String> = emptySet(),

	// --- Equipment & Resources ---
	var inventory: List<String> = emptyList(),
	var resources: List<CharacterResource> = emptyList(),
	var spellSlots: Map<Int, Pair<Int, Int>> = emptyMap(), // Level -> (Current, Max)

	// --- Resting & Combat Meta ---
	var hitDieType: Int = 8, // e.g., 8 for d8
	var hitDiceCurrent: Int = 1,
	var hasInspiration: Boolean = false, // NEW: Inspiration toggle
	var activeConditions: Set<String> = emptySet(),
	var actions: List<CharacterAction> = emptyList()
) {
	// ==========================================================
	// 2. Mechanical Logic
	// ==========================================================

	val hitDiceMax: Int get() = level

	/**
	 * Passive Perception is 10 + Wisdom Modifier + Proficiency (if applicable)
	 */
	val passivePerception: Int get() = 10 + getSkillBonus("Perception", wisdom)

	/**
	 * Calculates the effective speed based on conditions.
	 * For example, Prone halves speed and Restrained reduces it to 0.
	 */
	val effectiveSpeed: Int
		get() {
			if (activeConditions.contains("Paralyzed") || activeConditions.contains("Petrified") ||
				activeConditions.contains("Restrained") || activeConditions.contains("Unconscious") || hp == 0
			) return 0
			var base = speed
			if (activeConditions.contains("Prone")) base /= 2
			return base
		}

	/**
	 * Calculates effective AC.
	 * Some conditions might affect this through house rules or specific states.
	 */
	val effectiveAc: Int get() = ac

	/**
	 * Calculates the standard ability modifier: floor((score - 10) / 2)
	 */
	fun getModifier(score: Int): Int = floor((score - 10) / 2.0).toInt()

	/**
	 * Proficiency bonus scales with level (2 at level 1, +1 every 4 levels).
	 */
	fun getProficiencyBonus(): Int = ((level - 1) / 4) + 2

	/**
	 * Applies damage, taking Temporary HP into account first.
	 */
	fun applyDamage(amount: Int): CharacterEntry {
		var remainingDamage = amount
		val newTempHp = (tempHp - remainingDamage).coerceAtLeast(0)
		remainingDamage -= (tempHp - newTempHp)

		val newHp = (hp - remainingDamage).coerceAtLeast(0)
		return this.copy(hp = newHp, tempHp = newTempHp)
	}

	/**
	 * Applies healing, ensuring it doesn't exceed max HP.
	 */
	fun applyHealing(amount: Int): CharacterEntry {
		val newHp = (hp + amount).coerceAtMost(maxHp)
		return this.copy(hp = newHp)
	}

	/**
	 * Calculates total bonus for a skill check, including proficiency.
	 */
	fun getSkillBonus(skillName: String, attributeScore: Int): Int {
		val modifier = getModifier(attributeScore)
		return if (proficiencies.contains(skillName)) modifier + getProficiencyBonus() else modifier
	}

	/**
	 * Calculates total bonus for a saving throw, including proficiency and condition penalties.
	 */
	fun getSaveBonus(attrName: String, attributeScore: Int): Int {
		if ((attrName == "DEX" || attrName == "STR") &&
			(
				activeConditions.contains("Paralyzed") ||
					activeConditions.contains("Unconscious") ||
					activeConditions.contains("Petrified")
				)
		) {
			return -10 // Represents automatic failure or severe penalty
		}
		val modifier = getModifier(attributeScore)
		return if (saveProficiencies.contains(attrName)) modifier + getProficiencyBonus() else modifier
	}

	/**
	 * Automates a 5e Long Rest:
	tw	 * - Restores all HP/Resources/Spell Slots.
	 * - Resets Death Saves.
	 * - Recovers half of max Hit Dice.
	 */
	fun longRest(): CharacterEntry {
		return this.copy(
			hp = maxHp,
			mana = maxMana,
			stamina = maxStamina,
			deathSaveSuccesses = 0,
			deathSaveFailures = 0,
			hitDiceCurrent = (hitDiceCurrent + (hitDiceMax / 2)).coerceAtMost(hitDiceMax),
			resources = resources.map { it.copy(current = it.max) },
			spellSlots = spellSlots.mapValues { it.value.second to it.value.second }
		)
	}

	/**
	 * Automates a 5e Short Rest:
	 * - Warlocks recover all Pact Magic spell slots.
	 * - Monks recover all Ki Points.
	ee	 * - Fighters recover Action Surge, Second Wind, etc.
	 * - Bards recover Bardic Inspiration.
	 * - Warlocks recover Invocations that recharge on short rest.
	 * - HP recovery is done manually via the Hit Dice section.
	 *
	 * Resources with names containing these keywords are restored:
	 * "Ki", "Action Surge", "Second Wind", "Bardic Inspiration",
	 * "Channel Divinity", "Superiority", "Rage", "Wild Shape"
	 */
	fun shortRest(): CharacterEntry {
		val isWarlock = type.equals("Warlock", ignoreCase = true)
		val newSpellSlots = if (isWarlock) {
			spellSlots.mapValues { it.value.second to it.value.second }
		} else {
			spellSlots
		}

		// List of resource name keywords that reset on short rest
		val shortRestKeywords = listOf(
			"Ki", "Action Surge", "Second Wind", "Bardic Inspiration",
			"Channel Divinity", "Superiority", "Rage", "Wild Shape",
			"Arcane Recovery", "Font of Inspiration"
		)

		val newResources = resources.map { res ->
			val resetsOnShortRest = shortRestKeywords.any { keyword ->
				res.name.contains(keyword, ignoreCase = true)
			}
			if (resetsOnShortRest) res.copy(current = res.max) else res
		}

		return this.copy(spellSlots = newSpellSlots, resources = newResources)
	}
}
