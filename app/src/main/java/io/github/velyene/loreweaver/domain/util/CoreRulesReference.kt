/*
 * FILE: CoreRulesReference.kt
 *
 * TABLE OF CONTENTS:
 * 1. CoreRulesReference facade
 */

package io.github.velyene.loreweaver.domain.util

/**
 * Utility object for Core Rules reference content.
 *
 * The datasets now live in smaller sibling files so the facade stays stable for callers while
 * the reference corpus is split into maintainable sections.
 */
object CoreRulesReference {
	val INTRODUCTION = CoreRulesReferenceSectionsData.introduction
	val GLOSSARY_INTRODUCTION = CoreRulesReferenceGlossaryData.glossaryIntroduction
	val GLOSSARY_CONVENTIONS = CoreRulesReferenceGlossaryData.glossaryConventions
	val SECTIONS = CoreRulesReferenceSectionsData.sections

	val ABILITY_DESCRIPTIONS_TABLE = CoreRulesReferenceTablesData.abilityDescriptionsTable
	val ABILITY_SCORE_BENCHMARKS_TABLE = CoreRulesReferenceTablesData.abilityScoreBenchmarksTable
	val ABILITY_MODIFIERS_TABLE = CoreRulesReferenceTablesData.abilityModifiersTable
	val ATTACK_ROLL_ABILITIES_TABLE = CoreRulesReferenceTablesData.attackRollAbilitiesTable
	val TYPICAL_DIFFICULTY_CLASSES_TABLE = CoreRulesReferenceTablesData.typicalDifficultyClassesTable
	val PROFICIENCY_BONUS_TABLE = CoreRulesReferenceTablesData.proficiencyBonusTable
	val SKILLS_TABLE = CoreRulesReferenceTablesData.skillsTable
	val ACTIONS_TABLE = CoreRulesReferenceTablesData.actionsTable
	val TRAVEL_PACE_TABLE = CoreRulesReferenceTablesData.travelPaceTable
	val CREATURE_SIZE_AND_SPACE_TABLE = CoreRulesReferenceTablesData.creatureSizeAndSpaceTable
	val MONSTER_CREATURE_TYPES_TABLE = CoreRulesReferenceTablesData.monsterCreatureTypesTable
	val MONSTER_HIT_DICE_BY_SIZE_TABLE = CoreRulesReferenceTablesData.monsterHitDiceBySizeTable
	val EXPERIENCE_POINTS_BY_CHALLENGE_RATING_TABLE = CoreRulesReferenceTablesData.experiencePointsByChallengeRatingTable
	val COVER_TABLE = CoreRulesReferenceTablesData.coverTable
	val GLOSSARY_ABBREVIATIONS_TABLE = CoreRulesReferenceTablesData.glossaryAbbreviationsTable

	val GLOSSARY_ENTRIES = CoreRulesReferenceGlossaryData.glossaryEntries
	val GLOSSARY_TABLES = CoreRulesReferenceGlossaryData.glossaryTables
	val ALL_TABLES = CoreRulesReferenceTablesData.allTables
}
