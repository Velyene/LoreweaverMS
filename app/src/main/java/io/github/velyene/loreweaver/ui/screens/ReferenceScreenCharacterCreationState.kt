/*
 * FILE: ReferenceScreenCharacterCreationState.kt
 *
 * TABLE OF CONTENTS:
 * 1. Character Creation Section State Models
 * 2. Section Visibility Builders
 * 3. Search and Filtering Helpers
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.util.AbilityScoreSummary
import io.github.velyene.loreweaver.domain.util.BackgroundReference
import io.github.velyene.loreweaver.domain.util.CharacterCreationReference
import io.github.velyene.loreweaver.domain.util.CharacterCreationStep
import io.github.velyene.loreweaver.domain.util.CharacterCreationTextSection
import io.github.velyene.loreweaver.domain.util.FeatReference
import io.github.velyene.loreweaver.domain.util.LanguageReference
import io.github.velyene.loreweaver.domain.util.RaceReference
import io.github.velyene.loreweaver.domain.util.ReferenceTable

internal data class CharacterCreationContentState(
	val normalizedQuery: String,
	val effectiveSubsection: CharacterCreationSubsection,
	val showIntroduction: Boolean,
	val showAbilityModifiers: Boolean,
	val showBeyondFirstLevel: Boolean,
	val visibleCharacterCreationSections: List<CharacterCreationTextSection>,
	val visibleCharacterCreationTables: List<ReferenceTable>,
	val languageNotes: List<String>,
	val alignmentSummaries: List<String>,
	val trinkets: List<String>,
	val visibleRaceChapterSections: List<CharacterCreationTextSection>,
	val equipmentChapterSections: List<CharacterCreationTextSection>,
	val weaponPropertySections: List<CharacterCreationTextSection>,
	val weaponMasterySections: List<CharacterCreationTextSection>,
	val adventuringGearDetailSections: List<CharacterCreationTextSection>,
	val equipmentTables: List<ReferenceTable>,
	val visibleSteps: List<CharacterCreationStep>,
	val visibleBackgrounds: List<BackgroundReference>,
	val visibleFeats: List<FeatReference>,
	val visibleLanguages: List<LanguageReference>,
	val filteredAbilities: List<AbilityScoreSummary>,
	val filteredRaces: List<RaceReference>,
	val visibleTools: List<io.github.velyene.loreweaver.domain.util.ToolReferenceEntry>,
	val visibleWeapons: List<io.github.velyene.loreweaver.domain.util.WeaponReferenceEntry>,
	val visibleArmor: List<io.github.velyene.loreweaver.domain.util.ArmorReferenceEntry>,
	val visibleGear: List<io.github.velyene.loreweaver.domain.util.AdventuringGearEntry>,
	val visibleMagicItems: List<io.github.velyene.loreweaver.domain.util.MagicItemReferenceEntry>,
	val visibleAmmunition: List<io.github.velyene.loreweaver.domain.util.AmmunitionReferenceEntry>,
	val visibleFocuses: List<io.github.velyene.loreweaver.domain.util.FocusReferenceEntry>,
	val visibleMounts: List<io.github.velyene.loreweaver.domain.util.MountReferenceEntry>,
	val visibleTackAndDrawn: List<io.github.velyene.loreweaver.domain.util.TackDrawnReferenceEntry>,
	val visibleLargeVehicles: List<io.github.velyene.loreweaver.domain.util.LargeVehicleReferenceEntry>,
	val hasResults: Boolean
)

@Composable
@Suppress("kotlin:S3776")
internal fun rememberCharacterCreationContentState(
	searchQuery: String,
	selectedSubsection: CharacterCreationSubsection
): CharacterCreationContentState {
	val normalizedQuery = searchQuery.trim()
	val introductionTitle = stringResource(R.string.reference_character_creation_introduction)
	val introductionCardTitle = stringResource(R.string.reference_character_creation_creating_adventurer)
	val abilityModifiersTitle = stringResource(R.string.reference_character_creation_ability_modifiers)
	val beyondFirstLevelTitle = stringResource(R.string.reference_character_creation_beyond_first_level)
	val levelingUpTitle = stringResource(R.string.reference_character_creation_leveling_up)
	val effectiveSubsection =
		if (normalizedQuery.isBlank()) selectedSubsection else CharacterCreationSubsection.ALL
	val characterCreationSections = remember(normalizedQuery) {
		CharacterCreationReference.CHARACTER_CREATION_SECTIONS.filter { it.matchesQuery(normalizedQuery) }
	}
	val characterCreationTables = remember(normalizedQuery) {
		CharacterCreationReference.CHARACTER_CREATION_TABLES.filter { it.matchesQuery(normalizedQuery) }
	}
	val languageNotes = remember(normalizedQuery) {
		CharacterCreationReference.LANGUAGE_NOTES.filterByQuery(normalizedQuery)
	}
	val alignmentSummaries = remember(normalizedQuery) {
		CharacterCreationReference.ALIGNMENT_SUMMARIES.filterByQuery(normalizedQuery)
	}
	val trinkets = remember(normalizedQuery) {
		CharacterCreationReference.TRINKETS.filterByQuery(normalizedQuery)
	}
	val raceChapterSections = remember(normalizedQuery) {
		CharacterCreationReference.RACE_CHAPTER_SECTIONS.filter { it.matchesQuery(normalizedQuery) }
	}
	val equipmentChapterSections = remember(normalizedQuery) {
		io.github.velyene.loreweaver.domain.util.EquipmentReference.CHAPTER_SECTIONS.filter {
			it.matchesQuery(normalizedQuery)
		}
	}
	val weaponPropertySections = remember(normalizedQuery) {
		io.github.velyene.loreweaver.domain.util.EquipmentReference.WEAPON_PROPERTY_SECTIONS.filter {
			it.matchesQuery(normalizedQuery)
		}
	}
	val weaponMasterySections = remember(normalizedQuery) {
		io.github.velyene.loreweaver.domain.util.EquipmentReference.WEAPON_MASTERY_SECTIONS.filter {
			it.matchesQuery(normalizedQuery)
		}
	}
	val adventuringGearDetailSections = remember(normalizedQuery) {
		io.github.velyene.loreweaver.domain.util.EquipmentReference.ADVENTURING_GEAR_DETAIL_SECTIONS.filter {
			it.matchesQuery(normalizedQuery)
		}
	}
	val equipmentTables = remember(normalizedQuery) {
		filterEquipmentTables(normalizedQuery)
	}
	val filteredSteps = remember(normalizedQuery) {
		CharacterCreationReference.STEPS.filter { it.matchesQuery(normalizedQuery) }
	}
	val filteredAbilities = remember(normalizedQuery) {
		CharacterCreationReference.ABILITY_SCORES.filter { it.matchesQuery(normalizedQuery) }
	}
	val filteredRaces = remember(normalizedQuery) {
		CharacterCreationReference.RACES.filter { it.matchesQuery(normalizedQuery) }
	}
	val showIntroduction = matchesQuery(
		normalizedQuery,
		introductionTitle,
		introductionCardTitle,
		CharacterCreationReference.INTRODUCTION
	)
	val showAbilityModifiers =
		normalizedQuery.isBlank() || CharacterCreationReference.ABILITY_MODIFIERS.any { (score, modifier) ->
			matchesQuery(normalizedQuery, abilityModifiersTitle, score, modifier)
		}
	val showBeyondFirstLevel = matchesQuery(
		normalizedQuery,
		beyondFirstLevelTitle,
		levelingUpTitle,
		CharacterCreationReference.BEYOND_FIRST_LEVEL
	) && effectiveSubsection.showsBeyondFirstLevel()
	val visibleCharacterCreationSections = remember(characterCreationSections, effectiveSubsection) {
		characterCreationSections.filter { effectiveSubsection.matches(it) }
	}
	val visibleCharacterCreationTables = remember(characterCreationTables, effectiveSubsection) {
		characterCreationTables.filter { effectiveSubsection.matches(it) }
	}
	val visibleRaceChapterSections = remember(raceChapterSections, effectiveSubsection) {
		raceChapterSections.filter { effectiveSubsection.matches(it) }
	}
	val visibleSteps = remember(filteredSteps, effectiveSubsection) {
		filteredSteps.filter { effectiveSubsection.matches(it) }
	}
	val visibleBackgrounds = remember(normalizedQuery, effectiveSubsection) {
		visibleCharacterCreationBackgrounds(normalizedQuery, effectiveSubsection)
	}
	val visibleFeats = remember(normalizedQuery, effectiveSubsection) {
		visibleCharacterCreationFeats(normalizedQuery, effectiveSubsection)
	}
	val visibleLanguages = remember(normalizedQuery, effectiveSubsection) {
		visibleCharacterCreationLanguages(normalizedQuery, effectiveSubsection)
	}
	val visibleTools = remember(normalizedQuery, effectiveSubsection) {
		visibleEquipmentTools(normalizedQuery, effectiveSubsection)
	}
	val visibleWeapons = remember(normalizedQuery, effectiveSubsection) {
		visibleEquipmentWeapons(normalizedQuery, effectiveSubsection)
	}
	val visibleArmor = remember(normalizedQuery, effectiveSubsection) {
		visibleEquipmentArmor(normalizedQuery, effectiveSubsection)
	}
	val visibleGear = remember(normalizedQuery, effectiveSubsection) {
		visibleAdventuringGear(normalizedQuery)
	}
	val visibleMagicItems = remember(normalizedQuery, effectiveSubsection) {
		visibleEquipmentMagicItems(normalizedQuery)
	}
	val visibleAmmunition = remember(normalizedQuery, effectiveSubsection) {
		visibleEquipmentAmmunition(normalizedQuery, effectiveSubsection)
	}
	val visibleFocuses = remember(normalizedQuery, effectiveSubsection) {
		visibleEquipmentFocuses(normalizedQuery, effectiveSubsection)
	}
	val visibleMounts = remember(normalizedQuery, effectiveSubsection) {
		visibleEquipmentMounts(normalizedQuery, effectiveSubsection)
	}
	val visibleTackAndDrawn = remember(normalizedQuery, effectiveSubsection) {
		visibleEquipmentTackAndDrawn(normalizedQuery, effectiveSubsection)
	}
	val visibleLargeVehicles = remember(normalizedQuery, effectiveSubsection) {
		visibleEquipmentLargeVehicles(normalizedQuery, effectiveSubsection)
	}
	val hasResults = (showIntroduction && effectiveSubsection.showsIntroduction()) ||
		visibleCharacterCreationSections.isNotEmpty() ||
		visibleCharacterCreationTables.isNotEmpty() ||
		(visibleFeats.isNotEmpty() && effectiveSubsection.showsFeats()) ||
		(visibleLanguages.isNotEmpty() && effectiveSubsection.showsLanguageNotes()) ||
		(languageNotes.isNotEmpty() && effectiveSubsection.showsLanguageNotes()) ||
		(alignmentSummaries.isNotEmpty() && effectiveSubsection.showsAlignmentSummaries()) ||
		(trinkets.isNotEmpty() && effectiveSubsection.showsTrinkets()) ||
		visibleRaceChapterSections.isNotEmpty() ||
		equipmentChapterSections.isNotEmpty() ||
		weaponPropertySections.isNotEmpty() ||
		weaponMasterySections.isNotEmpty() ||
		(visibleWeapons.isNotEmpty() && effectiveSubsection.showsEquipment()) ||
		(visibleArmor.isNotEmpty() && effectiveSubsection.showsEquipment()) ||
		(visibleGear.isNotEmpty() && effectiveSubsection.showsEquipment()) ||
		(visibleMagicItems.isNotEmpty() && effectiveSubsection.showsEquipment()) ||
		(visibleAmmunition.isNotEmpty() && effectiveSubsection.showsEquipment()) ||
		(visibleFocuses.isNotEmpty() && effectiveSubsection.showsEquipment()) ||
		(visibleMounts.isNotEmpty() && effectiveSubsection.showsEquipment()) ||
		(visibleTackAndDrawn.isNotEmpty() && effectiveSubsection.showsEquipment()) ||
		(visibleLargeVehicles.isNotEmpty() && effectiveSubsection.showsEquipment()) ||
		adventuringGearDetailSections.isNotEmpty() ||
		(visibleTools.isNotEmpty() && effectiveSubsection.showsEquipment()) ||
		equipmentTables.isNotEmpty() ||
		visibleSteps.isNotEmpty() ||
		(visibleBackgrounds.isNotEmpty() && effectiveSubsection.showsBackgrounds()) ||
		(filteredAbilities.isNotEmpty() && effectiveSubsection.showsAbilitySummaries()) ||
		(showAbilityModifiers && effectiveSubsection.showsAbilityModifiers()) ||
		(filteredRaces.isNotEmpty() && effectiveSubsection.showsPlayableSpecies()) ||
		showBeyondFirstLevel
	return CharacterCreationContentState(
		normalizedQuery = normalizedQuery,
		effectiveSubsection = effectiveSubsection,
		showIntroduction = showIntroduction,
		showAbilityModifiers = showAbilityModifiers,
		showBeyondFirstLevel = showBeyondFirstLevel,
		visibleCharacterCreationSections = visibleCharacterCreationSections,
		visibleCharacterCreationTables = visibleCharacterCreationTables,
		languageNotes = languageNotes,
		alignmentSummaries = alignmentSummaries,
		trinkets = trinkets,
		visibleRaceChapterSections = visibleRaceChapterSections,
		equipmentChapterSections = equipmentChapterSections,
		weaponPropertySections = weaponPropertySections,
		weaponMasterySections = weaponMasterySections,
		adventuringGearDetailSections = adventuringGearDetailSections,
		equipmentTables = equipmentTables,
		visibleSteps = visibleSteps,
		visibleBackgrounds = visibleBackgrounds,
		visibleFeats = visibleFeats,
		visibleLanguages = visibleLanguages,
		filteredAbilities = filteredAbilities,
		filteredRaces = filteredRaces,
		visibleTools = visibleTools,
		visibleWeapons = visibleWeapons,
		visibleArmor = visibleArmor,
		visibleGear = visibleGear,
		visibleMagicItems = visibleMagicItems,
		visibleAmmunition = visibleAmmunition,
		visibleFocuses = visibleFocuses,
		visibleMounts = visibleMounts,
		visibleTackAndDrawn = visibleTackAndDrawn,
		visibleLargeVehicles = visibleLargeVehicles,
		hasResults = hasResults
	)
}

internal fun filterCharacterCreationBackgrounds(query: String): List<BackgroundReference> {
	return CharacterCreationReference.BACKGROUNDS.filter { it.matchesQuery(query) }
}

internal fun filterCharacterCreationFeats(query: String): List<FeatReference> {
	return CharacterCreationReference.FEATS.filter { it.matchesQuery(query) }
}

internal fun filterCharacterCreationLanguages(query: String): List<LanguageReference> {
	return (CharacterCreationReference.STANDARD_LANGUAGES + CharacterCreationReference.RARE_LANGUAGES)
		.filter { it.matchesQuery(query) }
}

internal fun visibleCharacterCreationBackgrounds(
	searchQuery: String,
	selectedSubsection: CharacterCreationSubsection
): List<BackgroundReference> {
	val normalizedQuery = searchQuery.trim()
	val effectiveSubsection =
		if (normalizedQuery.isBlank()) selectedSubsection else CharacterCreationSubsection.ALL
	return filterCharacterCreationBackgrounds(normalizedQuery).filter { effectiveSubsection.matches() }
}

internal fun visibleCharacterCreationFeats(
	searchQuery: String,
	selectedSubsection: CharacterCreationSubsection
): List<FeatReference> {
	val normalizedQuery = searchQuery.trim()
	val effectiveSubsection =
		if (normalizedQuery.isBlank()) selectedSubsection else CharacterCreationSubsection.ALL
	return filterCharacterCreationFeats(normalizedQuery).filter { effectiveSubsection.matches(it) }
}

internal fun visibleCharacterCreationLanguages(
	searchQuery: String,
	selectedSubsection: CharacterCreationSubsection
): List<LanguageReference> {
	val normalizedQuery = searchQuery.trim()
	if (normalizedQuery.isBlank() && !selectedSubsection.showsLanguageNotes()) {
		return emptyList()
	}
	return filterCharacterCreationLanguages(normalizedQuery)
}

