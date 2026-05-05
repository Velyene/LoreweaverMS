/*
 * FILE: SpellcastingReference.kt
 *
 * TABLE OF CONTENTS:
 * 1. Spell classification enums (SchoolOfMagic, SpellComponent, AreaOfEffect, CastingTime)
 * 2. Caster-type models (SpellcasterType, SpellSlotTable, SpellPreparationReferenceEntry)
 * 3. SpellcastingReference singleton â€” slot tables, caster rules, and spell economy data
 */

package io.github.velyene.loreweaver.domain.util

/**
 * Schools of magic.
 */
enum class SchoolOfMagic {
	ABJURATION,      // Protective, barriers
	CONJURATION,     // Transportation, summoning
	DIVINATION,      // Information, secrets
	ENCHANTMENT,     // Mind control, influence
	EVOCATION,       // Energy manipulation, damage
	ILLUSION,        // Deception, sensory tricks
	NECROMANCY,      // Life and death
	TRANSMUTATION    // Change properties
}

/**
 * Spell components.
 */
enum class SpellComponent {
	VERBAL,    // (V) Mystic words
	SOMATIC,   // (S) Gestures
	MATERIAL   // (M) Objects/focus
}

/**
 * Area of effect shapes.
 */
enum class AreaOfEffect {
	CONE,
	CUBE,
	CYLINDER,
	EMANATION,
	LINE,
	SPHERE,
	SELF
}

/**
 * Casting time categories.
 */
enum class CastingTime {
	ACTION,
	BONUS_ACTION,
	REACTION,
	RITUAL,        // +10 minutes
	MINUTES,
	HOURS
}

/**
 * Spellcasting class / progression families supported by the reference calculator.
 */
enum class SpellcasterType {
	BARD,
	CLERIC,
	DRUID,
	SORCERER,
	WIZARD,
	PALADIN,
	RANGER,
	WARLOCK
}

/**
 * Data class for spell slot progression.
 */
data class SpellSlotTable(
	val characterLevel: Int,
	val cantrips: Int,
	val slot1: Int = 0,
	val slot2: Int = 0,
	val slot3: Int = 0,
	val slot4: Int = 0,
	val slot5: Int = 0,
	val slot6: Int = 0,
	val slot7: Int = 0,
	val slot8: Int = 0,
	val slot9: Int = 0
)

data class SpellPreparationReferenceEntry(
	val className: String,
	val changeWhenYou: String,
	val numberOfSpells: String
)

/**
 * Utility object for spellcasting reference.
 */
object SpellcastingReference {
	val SPELL_PREPARATION_BY_CLASS = SpellcastingReferenceData.spellPreparationByClass
	val SPELL_PREPARATION_BY_CLASS_TABLE = SpellcastingReferenceData.spellPreparationByClassTable

	/**
	 * Get school of magic description.
	 */
	fun getSchoolDescription(school: SchoolOfMagic): String {
		return SpellcastingReferenceRules.getSchoolDescription(school)
	}

	/**
	 * Get spell level name.
	 */
	fun getSpellLevelName(level: Int): String {
		return SpellcastingReferenceRules.getSpellLevelName(level)
	}

	/**
	 * Get component description.
	 */
	fun getComponentDescription(component: SpellComponent): String {
		return SpellcastingReferenceRules.getComponentDescription(component)
	}

	/**
	 * Get casting time description.
	 */
	fun getCastingTimeDescription(castingTime: CastingTime): String {
		return SpellcastingReferenceRules.getCastingTimeDescription(castingTime)
	}

	/**
	 * Per-caster spell slot tables accessible via getSpellSlotsForCaster / getSpellSlotTableForCaster.
	 */

	fun getSpellcasterDescription(type: SpellcasterType): String {
		return SpellcastingReferenceRules.getSpellcasterDescription(type)
	}

	fun getSpellSlotsForCaster(type: SpellcasterType, level: Int): SpellSlotTable? {
		return SpellcastingReferenceData.getSpellSlotsForCaster(type, level)
	}

	fun getSpellSlotTableForCaster(type: SpellcasterType): List<SpellSlotTable> {
		return SpellcastingReferenceData.getSpellSlotTableForCaster(type)
	}

	/**
	 * Core spellcasting rules.
	 */
	fun getSpellcastingRules(): Map<String, String> {
		return SpellcastingReferenceRules.getSpellcastingRules()
	}

	fun getSpellcastingTables(): List<ReferenceTable> {
		return SpellcastingReferenceRules.getSpellcastingTables()
	}

	/**
	 * Area of effect rules.
	 */
	fun getAreaOfEffectRules(area: AreaOfEffect): String {
		return SpellcastingReferenceRules.getAreaOfEffectRules(area)
	}

	/**
	 * Concentration breaking conditions.
	 */
	fun getConcentrationBreakers(): List<String> {
		return SpellcastingReferenceRules.getConcentrationBreakers()
	}

	/**
	 * Calculate spell save DC.
	 */
	fun calculateSpellSaveDC(spellcastingMod: Int, proficiencyBonus: Int, bonuses: Int = 0): Int {
		return 8 + spellcastingMod + proficiencyBonus + bonuses
	}

	/**
	 * Calculate spell attack bonus.
	 */
	fun calculateSpellAttackBonus(
		spellcastingMod: Int,
		proficiencyBonus: Int,
		bonuses: Int = 0
	): Int {
		return spellcastingMod + proficiencyBonus + bonuses
	}


	/**
	 * Get spellcasting tips.
	 */
	fun getSpellcastingTips(): List<String> {
		return SpellcastingReferenceRules.getSpellcastingTips()
	}

	/**
	 * Setting-neutral primer on how spellcasting can be framed in a campaign.
	 */
	fun getMagicTheoryOverview(): String {
		return SpellcastingReferenceRules.getMagicTheoryOverview()
	}
}
