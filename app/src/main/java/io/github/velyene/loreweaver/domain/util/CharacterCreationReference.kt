@file:Suppress("kotlin:S1192")

/*
 * FILE: CharacterCreationReference.kt
 *
 * TABLE OF CONTENTS:
 * 1. Character creation data models
 * 2. Background, language, and feat reference datasets
 * 3. Text sections for guided character creation
 * 4. Background, language, feat, and table datasets
 * 5. Aggregate CharacterCreationReference accessors
 */

package io.github.velyene.loreweaver.domain.util

/**
 * Data class for character creation steps.
 */
data class CharacterCreationStep(
	val number: Int,
	val title: String,
	val content: String,
	val example: String? = null
)

/**
 * Data class for an ability score summary.
 */
data class AbilityScoreSummary(
	val ability: String,
	val measures: String,
	val importantFor: String,
	val racialIncreases: String
)

/**
 * Data class for a structured background summary.
 */
data class BackgroundReference(
	val name: String,
	val abilityScores: List<String>,
	val feat: String,
	val skillProficiencies: List<String>,
	val toolProficiency: String,
	val equipmentOptions: List<String>
)

/**
 * Data class for a structured language reference entry.
 */
data class LanguageReference(
	val name: String,
	val group: String,
	val roll: String? = null
)

/**
 * Data class for a structured feat reference entry.
 */
data class FeatReference(
	val name: String,
	val category: String,
	val prerequisite: String? = null,
	val benefits: List<String>,
	val repeatable: Boolean = false
)

/**
 * Data class for chapter text sections in character creation.
 */
data class CharacterCreationTextSection(
	val title: String,
	val body: String
)

/**
 * Data class for racial traits and feature summaries.
 */
data class RacialTraitReference(
	val name: String,
	val description: String
)

/**
 * Data class for a playable subrace summary.
 */
data class SubraceReference(
	val name: String,
	val overview: String,
	val abilityScoreIncrease: String? = null,
	val traits: List<RacialTraitReference> = emptyList()
)

/**
 * Data class for a race summary in the character creation chapter.
 */
data class RaceReference(
	val name: String,
	val overview: String,
	val personality: String,
	val society: String,
	val adventurers: String,
	val names: String,
	val abilityScoreIncrease: String,
	val age: String,
	val size: String,
	val speed: String,
	val languages: String,
	val traits: List<RacialTraitReference>,
	val subraces: List<SubraceReference> = emptyList(),
	val notes: List<String> = emptyList()
)

/**
 * Utility object for character creation reference data.
 */
object CharacterCreationReference {
	val RACE_CHAPTER_SECTIONS = CharacterCreationReferenceSpeciesData.raceChapterSections

	val RACES = CharacterCreationReferenceSpeciesData.races

	val INTRODUCTION = CharacterCreationReferenceNarrativeData.introduction
	val CHARACTER_CREATION_SECTIONS = CharacterCreationReferenceNarrativeData.characterCreationSections
	val STEPS = CharacterCreationReferenceNarrativeData.steps
	val ABILITY_SCORES = CharacterCreationReferenceNarrativeData.abilityScores

	val BACKGROUNDS = CharacterCreationReferenceCatalogData.backgrounds
	val STANDARD_LANGUAGES = CharacterCreationReferenceCatalogData.standardLanguages
	val RARE_LANGUAGES = CharacterCreationReferenceCatalogData.rareLanguages
	val FEATS = CharacterCreationReferenceCatalogData.feats
	val LANGUAGE_NOTES = CharacterCreationReferenceCatalogData.languageNotes
	val TRINKETS = CharacterCreationReferenceCatalogData.trinkets

	val ABILITY_MODIFIERS = CharacterCreationReferenceTablesData.abilityModifiers
	val CHARACTER_CREATION_TABLES = CharacterCreationReferenceTablesData.characterCreationTables

	val ALIGNMENT_SUMMARIES = CharacterCreationReferenceNarrativeData.alignmentSummaries
	val BEYOND_FIRST_LEVEL = CharacterCreationReferenceNarrativeData.beyondFirstLevel
}
