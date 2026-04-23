package com.example.loreweaver.ui.screens

import com.example.loreweaver.domain.model.CharacterAction
import com.example.loreweaver.domain.model.CharacterResource

/**
 * Encapsulates character form state to reduce cognitive complexity.
 * This addresses SonarQube S3776 (cognitive complexity) issues.
 */
data class CharacterFormState(
	val name: String = "",
	val type: String = CharacterFormConstants.DEFAULT_CLASS,
	val party: String = CharacterFormConstants.DEFAULT_PARTY,
	val level: String = CharacterFormConstants.DEFAULT_LEVEL,
	val challengeRating: String = CharacterFormConstants.DEFAULT_CHALLENGE_RATING,
	val str: String = CharacterFormConstants.DEFAULT_ATTRIBUTE,
	val dex: String = CharacterFormConstants.DEFAULT_ATTRIBUTE,
	val con: String = CharacterFormConstants.DEFAULT_ATTRIBUTE,
	val intell: String = CharacterFormConstants.DEFAULT_ATTRIBUTE,
	val wis: String = CharacterFormConstants.DEFAULT_ATTRIBUTE,
	val cha: String = CharacterFormConstants.DEFAULT_ATTRIBUTE,
	val hp: String = CharacterFormConstants.DEFAULT_HP,
	val maxHp: String = CharacterFormConstants.DEFAULT_HP,
	val mana: String = CharacterFormConstants.DEFAULT_MANA,
	val maxMana: String = CharacterFormConstants.DEFAULT_MANA,
	val stamina: String = CharacterFormConstants.DEFAULT_STAMINA,
	val maxStamina: String = CharacterFormConstants.DEFAULT_STAMINA,
	val ac: String = CharacterFormConstants.DEFAULT_AC,
	val initiative: String = CharacterFormConstants.DEFAULT_INITIATIVE,
	val speed: String = CharacterFormConstants.DEFAULT_SPEED,
	val hitDieType: String = CharacterFormConstants.DEFAULT_HIT_DIE,
	val selectedProficiencies: Set<String> = emptySet(),
	val selectedSaveProficiencies: Set<String> = emptySet(),
	val inventoryText: String = "",
	val resources: List<CharacterResource> = emptyList(),
	val actions: List<CharacterAction> = emptyList(),
	val status: String = "",
	val notes: String = "",
	val nameError: Boolean = false,
	val hpError: Boolean = false
)
