package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.CharacterCreationStep
import io.github.velyene.loreweaver.domain.util.CharacterCreationTextSection
import io.github.velyene.loreweaver.domain.util.FeatReference
import io.github.velyene.loreweaver.domain.util.ReferenceTable

internal enum class CharacterCreationSubsection(val label: String) {
	ALL("All"),
	OVERVIEW("Overview"),
	SPECIES_ORIGIN("Species & Origin"),
	ABILITIES("Abilities"),
	CLASSES("Classes"),
	EQUIPMENT("Equipment"),
	ADVANCEMENT("Advancement"),
	FLAVOR("Flavor")
}

private const val CHARACTER_CREATION_TITLE_CHOOSE_CHARACTER_SHEET = "Choose a Character Sheet"
private const val CHARACTER_CREATION_TITLE_STEP_1_CHOOSE_CLASS = "Step 1: Choose Class"
private const val CHARACTER_CREATION_TITLE_STEP_2_ORIGIN = "Step 2: Character Origin"
private const val CHARACTER_CREATION_TITLE_STEP_3_ABILITY_SCORES = "Step 3: Ability Scores"
private const val CHARACTER_CREATION_TITLE_STEP_4_ALIGNMENT = "Step 4: Alignment"
private const val CHARACTER_CREATION_TITLE_STEP_5_DETAILS = "Step 5: Character Creation Details"
private const val CHARACTER_CREATION_TITLE_IMAGINE_PAST_PRESENT = "Imagine the Past and Present"
private const val CHARACTER_CREATION_TITLE_CHOOSE_LANGUAGES = "Choose Languages"
private const val CHARACTER_CREATION_TITLE_LEVEL_ADVANCEMENT = "Level Advancement"
private const val CHARACTER_CREATION_TITLE_GAINING_A_LEVEL = "Gaining a Level"
private const val CHARACTER_CREATION_TITLE_TIERS_OF_PLAY = "Tiers of Play"
private const val CHARACTER_CREATION_TITLE_STARTING_AT_HIGHER_LEVELS = "Starting at Higher Levels"
private const val CHARACTER_CREATION_TITLE_MULTICLASSING = "Multiclassing"
private const val CHARACTER_CREATION_TITLE_SPELLCASTING_ACROSS_CLASSES = "Spellcasting Across Classes"

private const val CHARACTER_CREATION_TABLE_CLASS_OVERVIEW = "Class Overview"
private const val CHARACTER_CREATION_TABLE_ABILITY_SCORES_AND_BACKGROUNDS = "Ability Scores and Backgrounds"
private const val CHARACTER_CREATION_TABLE_STANDARD_LANGUAGES = "Standard Languages"
private const val CHARACTER_CREATION_TABLE_RARE_LANGUAGES = "Rare Languages"
private const val CHARACTER_CREATION_TABLE_ABILITY_SCORE_POINT_COSTS = "Ability Score Point Costs"
private const val CHARACTER_CREATION_TABLE_STANDARD_ARRAY_BY_CLASS = "Standard Array by Class"
private const val CHARACTER_CREATION_TABLE_ABILITY_SCORES_AND_MODIFIERS = "Ability Scores and Modifiers (Creation Range)"
private const val CHARACTER_CREATION_TABLE_CLASSES_SUMMARY = "Classes Summary"
private const val CHARACTER_CREATION_TABLE_LEVEL_1_HIT_POINTS = "Level 1 Hit Points by Class"
private const val CHARACTER_CREATION_TABLE_CHARACTER_ADVANCEMENT = "Character Advancement"
private const val CHARACTER_CREATION_TABLE_FIXED_HIT_POINTS = "Fixed Hit Points by Class"
private const val CHARACTER_CREATION_TABLE_STARTING_EQUIPMENT_HIGHER_LEVELS = "Starting Equipment at Higher Levels"
private const val CHARACTER_CREATION_TABLE_MULTICLASS_SPELLCASTER_SLOTS = "Multiclass Spellcaster: Spell Slots per Spell Level"

private const val FEAT_CATEGORY_ORIGIN = "Origin"
private const val FEAT_CATEGORY_GENERAL = "General"
private const val FEAT_CATEGORY_FIGHTING_STYLE = "Fighting Style"
private const val FEAT_CATEGORY_EPIC_BOON = "Epic Boon"

private val OVERVIEW_SECTION_TITLES = setOf(
	CHARACTER_CREATION_TITLE_CHOOSE_CHARACTER_SHEET,
	CHARACTER_CREATION_TITLE_STEP_1_CHOOSE_CLASS,
	CHARACTER_CREATION_TITLE_STEP_5_DETAILS
)

private val SPECIES_ORIGIN_SECTION_TITLES = setOf(
	CHARACTER_CREATION_TITLE_STEP_2_ORIGIN,
	CHARACTER_CREATION_TITLE_CHOOSE_LANGUAGES
)

private val ADVANCEMENT_SECTION_TITLES = setOf(
	CHARACTER_CREATION_TITLE_LEVEL_ADVANCEMENT,
	CHARACTER_CREATION_TITLE_GAINING_A_LEVEL,
	CHARACTER_CREATION_TITLE_TIERS_OF_PLAY,
	CHARACTER_CREATION_TITLE_STARTING_AT_HIGHER_LEVELS,
	CHARACTER_CREATION_TITLE_MULTICLASSING,
	CHARACTER_CREATION_TITLE_SPELLCASTING_ACROSS_CLASSES
)

private val FLAVOR_SECTION_TITLES = setOf(
	CHARACTER_CREATION_TITLE_IMAGINE_PAST_PRESENT,
	CHARACTER_CREATION_TITLE_STEP_4_ALIGNMENT
)

private val SPECIES_ORIGIN_TABLE_TITLES = setOf(
	CHARACTER_CREATION_TABLE_ABILITY_SCORES_AND_BACKGROUNDS,
	CHARACTER_CREATION_TABLE_STANDARD_LANGUAGES,
	CHARACTER_CREATION_TABLE_RARE_LANGUAGES
)

private val ABILITIES_TABLE_TITLES = setOf(
	CHARACTER_CREATION_TABLE_ABILITY_SCORE_POINT_COSTS,
	CHARACTER_CREATION_TABLE_STANDARD_ARRAY_BY_CLASS,
	CHARACTER_CREATION_TABLE_ABILITY_SCORES_AND_MODIFIERS
)

private val CLASSES_TABLE_TITLES = setOf(
	CHARACTER_CREATION_TABLE_CLASS_OVERVIEW,
	CHARACTER_CREATION_TABLE_CLASSES_SUMMARY,
	CHARACTER_CREATION_TABLE_LEVEL_1_HIT_POINTS
)

private val ADVANCEMENT_TABLE_TITLES = setOf(
	CHARACTER_CREATION_TABLE_CHARACTER_ADVANCEMENT,
	CHARACTER_CREATION_TABLE_FIXED_HIT_POINTS,
	CHARACTER_CREATION_TABLE_STARTING_EQUIPMENT_HIGHER_LEVELS,
	CHARACTER_CREATION_TABLE_MULTICLASS_SPELLCASTER_SLOTS
)

internal fun CharacterCreationSubsection.matches(section: CharacterCreationTextSection): Boolean =
	when (this) {
		CharacterCreationSubsection.ALL -> true
		CharacterCreationSubsection.OVERVIEW -> section.title in OVERVIEW_SECTION_TITLES

		CharacterCreationSubsection.SPECIES_ORIGIN ->
			section.title in SPECIES_ORIGIN_SECTION_TITLES || section.title.contains("Species", ignoreCase = true)

		CharacterCreationSubsection.ABILITIES -> section.title == CHARACTER_CREATION_TITLE_STEP_3_ABILITY_SCORES
		CharacterCreationSubsection.CLASSES -> section.title == CHARACTER_CREATION_TITLE_STEP_1_CHOOSE_CLASS
		CharacterCreationSubsection.EQUIPMENT -> false
		CharacterCreationSubsection.ADVANCEMENT -> section.title in ADVANCEMENT_SECTION_TITLES
		CharacterCreationSubsection.FLAVOR -> section.title in FLAVOR_SECTION_TITLES
	}

internal fun CharacterCreationSubsection.matches(step: CharacterCreationStep): Boolean =
	when (this) {
		CharacterCreationSubsection.ALL -> true
		CharacterCreationSubsection.OVERVIEW -> step.number in setOf(1, 5)
		CharacterCreationSubsection.SPECIES_ORIGIN -> step.number == 2
		CharacterCreationSubsection.ABILITIES -> step.number == 3
		CharacterCreationSubsection.CLASSES -> step.number == 1
		CharacterCreationSubsection.EQUIPMENT -> false
		CharacterCreationSubsection.ADVANCEMENT -> false
		CharacterCreationSubsection.FLAVOR -> step.number == 4
	}

internal fun CharacterCreationSubsection.matches(): Boolean =
	when (this) {
		CharacterCreationSubsection.ALL -> true
		CharacterCreationSubsection.OVERVIEW -> false
		CharacterCreationSubsection.SPECIES_ORIGIN -> true
		CharacterCreationSubsection.ABILITIES -> false
		CharacterCreationSubsection.CLASSES -> false
		CharacterCreationSubsection.EQUIPMENT -> false
		CharacterCreationSubsection.ADVANCEMENT -> false
		CharacterCreationSubsection.FLAVOR -> false
	}

internal fun CharacterCreationSubsection.matches(feat: FeatReference): Boolean = when (this) {
	CharacterCreationSubsection.ALL -> true
	CharacterCreationSubsection.OVERVIEW -> false
	CharacterCreationSubsection.SPECIES_ORIGIN -> feat.category == FEAT_CATEGORY_ORIGIN
	CharacterCreationSubsection.ABILITIES -> feat.category == FEAT_CATEGORY_GENERAL
	CharacterCreationSubsection.CLASSES -> feat.category == FEAT_CATEGORY_FIGHTING_STYLE
	CharacterCreationSubsection.EQUIPMENT -> false
	CharacterCreationSubsection.ADVANCEMENT -> feat.category == FEAT_CATEGORY_EPIC_BOON
	CharacterCreationSubsection.FLAVOR -> false
}

internal fun CharacterCreationSubsection.matches(table: ReferenceTable): Boolean = when (this) {
	CharacterCreationSubsection.ALL -> true
	CharacterCreationSubsection.OVERVIEW -> table.title == CHARACTER_CREATION_TABLE_CLASS_OVERVIEW
	CharacterCreationSubsection.SPECIES_ORIGIN -> table.title in SPECIES_ORIGIN_TABLE_TITLES
	CharacterCreationSubsection.ABILITIES -> table.title in ABILITIES_TABLE_TITLES
	CharacterCreationSubsection.CLASSES -> table.title in CLASSES_TABLE_TITLES
	CharacterCreationSubsection.EQUIPMENT -> false
	CharacterCreationSubsection.ADVANCEMENT -> table.title in ADVANCEMENT_TABLE_TITLES
	CharacterCreationSubsection.FLAVOR -> false
}

internal fun CharacterCreationSubsection.showsIntroduction(): Boolean {
	return this == CharacterCreationSubsection.ALL || this == CharacterCreationSubsection.OVERVIEW
}

internal fun CharacterCreationSubsection.showsAbilitySummaries(): Boolean {
	return this == CharacterCreationSubsection.ALL || this == CharacterCreationSubsection.ABILITIES
}

internal fun CharacterCreationSubsection.showsAbilityModifiers(): Boolean {
	return this == CharacterCreationSubsection.ALL || this == CharacterCreationSubsection.ABILITIES
}

internal fun CharacterCreationSubsection.showsLanguageNotes(): Boolean {
	return this == CharacterCreationSubsection.ALL || this == CharacterCreationSubsection.SPECIES_ORIGIN
}

internal fun CharacterCreationSubsection.showsAlignmentSummaries(): Boolean {
	return this == CharacterCreationSubsection.ALL || this == CharacterCreationSubsection.FLAVOR
}

internal fun CharacterCreationSubsection.showsPlayableSpecies(): Boolean {
	return this == CharacterCreationSubsection.ALL || this == CharacterCreationSubsection.SPECIES_ORIGIN
}

internal fun CharacterCreationSubsection.showsBackgrounds(): Boolean {
	return this == CharacterCreationSubsection.ALL || this == CharacterCreationSubsection.SPECIES_ORIGIN
}

internal fun CharacterCreationSubsection.showsFeats(): Boolean {
	return this == CharacterCreationSubsection.ALL || this in setOf(
		CharacterCreationSubsection.SPECIES_ORIGIN,
		CharacterCreationSubsection.ABILITIES,
		CharacterCreationSubsection.CLASSES,
		CharacterCreationSubsection.ADVANCEMENT
	)
}

internal fun CharacterCreationSubsection.showsPlayableClasses(): Boolean {
	return this == CharacterCreationSubsection.ALL || this == CharacterCreationSubsection.CLASSES
}

internal fun CharacterCreationSubsection.showsEquipment(): Boolean {
	return this == CharacterCreationSubsection.ALL || this == CharacterCreationSubsection.EQUIPMENT
}

internal fun CharacterCreationSubsection.showsBeyondFirstLevel(): Boolean {
	return this == CharacterCreationSubsection.ALL || this == CharacterCreationSubsection.ADVANCEMENT
}

internal fun CharacterCreationSubsection.showsTrinkets(): Boolean {
	return this == CharacterCreationSubsection.ALL || this == CharacterCreationSubsection.FLAVOR
}
