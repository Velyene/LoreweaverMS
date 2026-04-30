/*
 * FILE: ReferenceScreenNavigation.kt
 *
 * TABLE OF CONTENTS:
 * 1. Character creation subsection enum
 * 2. Character creation section-matching helpers
 * 3. Character creation visibility helpers
 */

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

internal fun CharacterCreationSubsection.matches(section: CharacterCreationTextSection): Boolean =
	when (this) {
		CharacterCreationSubsection.ALL -> true
		CharacterCreationSubsection.OVERVIEW -> section.title in setOf(
			"Choose a Character Sheet",
			"Step 1: Choose Class",
			"Step 5: Character Creation Details"
		)

		CharacterCreationSubsection.SPECIES_ORIGIN -> section.title in setOf(
			"Step 2: Character Origin",
			"Choose Languages"
		) || section.title.contains("Species", ignoreCase = true)

		CharacterCreationSubsection.ABILITIES -> section.title == "Step 3: Ability Scores"
		CharacterCreationSubsection.CLASSES -> section.title == "Step 1: Choose Class"
		CharacterCreationSubsection.EQUIPMENT -> false
		CharacterCreationSubsection.ADVANCEMENT -> section.title in setOf(
			"Level Advancement",
			"Gaining a Level",
			"Tiers of Play",
			"Starting at Higher Levels",
			"Multiclassing",
			"Spellcasting Across Classes"
		)

		CharacterCreationSubsection.FLAVOR -> section.title in setOf(
			"Imagine the Past and Present",
			"Step 4: Alignment"
		)
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
	CharacterCreationSubsection.SPECIES_ORIGIN -> feat.category == "Origin"
	CharacterCreationSubsection.ABILITIES -> feat.category == "General"
	CharacterCreationSubsection.CLASSES -> feat.category == "Fighting Style"
	CharacterCreationSubsection.EQUIPMENT -> false
	CharacterCreationSubsection.ADVANCEMENT -> feat.category == "Epic Boon"
	CharacterCreationSubsection.FLAVOR -> false
}

internal fun CharacterCreationSubsection.matches(table: ReferenceTable): Boolean = when (this) {
	CharacterCreationSubsection.ALL -> true
	CharacterCreationSubsection.OVERVIEW -> table.title == "Class Overview"
	CharacterCreationSubsection.SPECIES_ORIGIN -> table.title in setOf(
		"Ability Scores and Backgrounds",
		"Standard Languages",
		"Rare Languages"
	)

	CharacterCreationSubsection.ABILITIES -> table.title in setOf(
		"Ability Score Point Costs",
		"Standard Array by Class",
		"Ability Scores and Modifiers (Creation Range)"
	)

	CharacterCreationSubsection.CLASSES -> table.title in setOf(
		"Class Overview",
		"Classes Summary",
		"Level 1 Hit Points by Class"
	)

	CharacterCreationSubsection.EQUIPMENT -> false
	CharacterCreationSubsection.ADVANCEMENT -> table.title in setOf(
		"Character Advancement",
		"Fixed Hit Points by Class",
		"Starting Equipment at Higher Levels",
		"Multiclass Spellcaster: Spell Slots per Spell Level"
	)

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

