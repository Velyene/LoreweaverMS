package com.example.loreweaver.ui.screens

import com.example.loreweaver.domain.util.CharacterParty

/**
 * Constants for CharacterFormScreen to avoid magic numbers and strings.
 */
object CharacterFormConstants {
	const val DEFAULT_CLASS = "Fighter"

	// Default attribute values
	const val DEFAULT_ATTRIBUTE = "10"
	const val DEFAULT_HP = "10"
	const val DEFAULT_MANA = "0"
	const val DEFAULT_STAMINA = "10"
	const val DEFAULT_AC = "10"
	const val DEFAULT_INITIATIVE = "0"
	const val DEFAULT_SPEED = "30"
	const val DEFAULT_HIT_DIE = "8"
	const val DEFAULT_LEVEL = "1"
	const val DEFAULT_CHALLENGE_RATING = "0"

	// Attribute bounds
	const val MIN_ATTRIBUTE = 1
	const val MAX_ATTRIBUTE = 30

	// Party constants
	const val ADVENTURERS_PARTY = CharacterParty.ADVENTURERS
	const val DEFAULT_PARTY = ADVENTURERS_PARTY

	// Skill proficiencies
	val SKILL_LIST = listOf(
		"Acrobatics", "Animal Handling", "Arcana", "Athletics", "Deception",
		"History", "Insight", "Intimidation", "Investigation", "Medicine", "Nature",
		"Perception", "Performance", "Persuasion", "Religion", "Sleight of Hand",
		"Stealth", "Survival"
	)

	// Save proficiency labels
	val ABILITY_SCORE_ABBREVIATIONS = listOf("STR", "DEX", "CON", "INT", "WIS", "CHA")
}
