/*
 * FILE: ReferenceScreenCharacterCreation.kt
 *
 * TABLE OF CONTENTS:
 * 1. Character creation content entry point and remembered state
 * 2. Foundation, guidance, and tail lazy-list renderers
 * 3. Shared filter-chip and text-section components
 * 4. Character creation reference cards
 * 5. Search and matching helpers
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.util.AbilityScoreSummary
import io.github.velyene.loreweaver.domain.util.BackgroundReference
import io.github.velyene.loreweaver.domain.util.CharacterCreationReference
import io.github.velyene.loreweaver.domain.util.CharacterCreationStep
import io.github.velyene.loreweaver.domain.util.CharacterCreationTextSection
import io.github.velyene.loreweaver.domain.util.FeatReference
import io.github.velyene.loreweaver.domain.util.LanguageReference
import io.github.velyene.loreweaver.domain.util.RaceReference
import io.github.velyene.loreweaver.domain.util.RacialTraitReference
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver
import io.github.velyene.loreweaver.domain.util.ReferenceTable
import io.github.velyene.loreweaver.domain.util.SubraceReference
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal

@Composable
@Suppress("kotlin:S3776")
internal fun CharacterCreationContent(
	searchQuery: String,
	listState: LazyListState,
	onOpenDetail: (String, String) -> Unit
) {
	var selectedSubsectionName by rememberSaveable { mutableStateOf(CharacterCreationSubsection.ALL.name) }
	val selectedSubsection = remember(selectedSubsectionName) {
		CharacterCreationSubsection.valueOf(selectedSubsectionName)
	}
	val state = rememberCharacterCreationContentState(
		searchQuery = searchQuery,
		selectedSubsection = selectedSubsection
	)

	if (!state.hasResults) {
		ReferenceNoResultsState()
		return
	}

	Column(modifier = Modifier.fillMaxSize()) {
		ReferenceFilterChipRow(
			options = CharacterCreationSubsection.entries,
			selectedOption = state.effectiveSubsection,
			onOptionSelected = { selectedSubsectionName = it.name },
			label = CharacterCreationSubsection::label
		)

		LazyColumn(
			state = listState,
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f)
				.visibleVerticalScrollbar(listState),
			contentPadding = PaddingValues(16.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			renderCharacterCreationFoundationItems(state, onOpenDetail)
			renderCharacterCreationGuidanceItems(state)
			renderCharacterCreationEquipmentItems(
				sectionData = CharacterCreationEquipmentSectionData(
					showEquipment = state.effectiveSubsection.showsEquipment(),
					equipmentChapterSections = state.equipmentChapterSections,
					visibleWeapons = state.visibleWeapons,
					visibleArmor = state.visibleArmor,
					visibleGear = state.visibleGear,
					visibleMagicItems = state.visibleMagicItems,
					visibleAmmunition = state.visibleAmmunition,
					visibleFocuses = state.visibleFocuses,
					visibleMounts = state.visibleMounts,
					visibleTackAndDrawn = state.visibleTackAndDrawn,
					visibleLargeVehicles = state.visibleLargeVehicles,
					weaponPropertySections = state.weaponPropertySections,
					weaponMasterySections = state.weaponMasterySections,
					visibleTools = state.visibleTools,
					equipmentTables = state.equipmentTables,
					adventuringGearDetailSections = state.adventuringGearDetailSections,
					onOpenDetail = onOpenDetail
				)
			)
			renderCharacterCreationTailItems(state)
		}
	}
}

private data class CharacterCreationContentState(
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
private fun rememberCharacterCreationContentState(
	searchQuery: String,
	selectedSubsection: CharacterCreationSubsection
): CharacterCreationContentState {
	val normalizedQuery = searchQuery.trim()
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
		"Introduction",
		"Creating an Adventurer",
		CharacterCreationReference.INTRODUCTION
	)
	val showAbilityModifiers =
		normalizedQuery.isBlank() || CharacterCreationReference.ABILITY_MODIFIERS.any { (score, modifier) ->
			matchesQuery(normalizedQuery, "Ability Modifiers", score, modifier)
		}
	val showBeyondFirstLevel = matchesQuery(
		normalizedQuery,
		"Beyond 1st Level",
		"Leveling Up",
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

private fun LazyListScope.renderCharacterCreationFoundationItems(
	state: CharacterCreationContentState,
	onOpenDetail: (String, String) -> Unit
) {
	if (state.showIntroduction && state.effectiveSubsection.showsIntroduction()) {
		item {
			ReferenceSectionHeader("Introduction")
			InfoCard("Creating an Adventurer", CharacterCreationReference.INTRODUCTION)
		}
	}

	if (state.visibleRaceChapterSections.isNotEmpty()) {
		item { ReferenceSectionHeader("Species Guidance") }
		items(state.visibleRaceChapterSections, key = { it.title }) { section ->
			CharacterCreationTextSectionCard(section)
		}
	}

	if (state.visibleSteps.isNotEmpty()) {
		item { ReferenceSectionHeader("Step-by-step Creation") }
		items(state.visibleSteps, key = { it.number }) { step ->
			CharacterCreationStepCard(step)
		}
	}

	if (state.visibleBackgrounds.isNotEmpty() && state.effectiveSubsection.showsBackgrounds()) {
		item { ReferenceSectionHeader("Backgrounds") }
		items(state.visibleBackgrounds, key = { it.name }) { background ->
			BackgroundReferenceCard(background)
		}
	}

	if (state.visibleFeats.isNotEmpty() && state.effectiveSubsection.showsFeats()) {
		item { ReferenceSectionHeader("Feats") }
		items(state.visibleFeats, key = { it.name }) { feat ->
			FeatReferenceCard(
				feat = feat,
				onClick = {
					openFeatReferenceDetail(
						onOpenDetail = onOpenDetail,
						name = feat.name
					)
				}
			)
		}
	}

	if (state.visibleLanguages.isNotEmpty() && state.effectiveSubsection.showsLanguageNotes()) {
		item { ReferenceSectionHeader("Languages") }
		items(state.visibleLanguages, key = { "${it.group}-${it.name}" }) { language ->
			LanguageReferenceCard(language)
		}
	}

	if (state.filteredAbilities.isNotEmpty() && state.effectiveSubsection.showsAbilitySummaries()) {
		item { ReferenceSectionHeader("Ability Scores Summary") }
		items(state.filteredAbilities, key = { it.ability }) { ability ->
			AbilityScoreSummaryCard(ability)
		}
	}

	if (state.showAbilityModifiers && state.effectiveSubsection.showsAbilityModifiers()) {
		item {
			ReferenceSectionHeader("Ability Modifiers")
			AbilityModifiersCard()
		}
	}
}

private fun LazyListScope.renderCharacterCreationGuidanceItems(state: CharacterCreationContentState) {
	if (state.visibleCharacterCreationSections.isNotEmpty()) {
		item { ReferenceSectionHeader("Core Character Creation Guidance") }
		items(state.visibleCharacterCreationSections, key = { it.title }) { section ->
			CharacterCreationTextSectionCard(section)
		}
	}

	if (state.visibleCharacterCreationTables.isNotEmpty()) {
		item { ReferenceSectionHeader("Character Creation Tables") }
		items(state.visibleCharacterCreationTables, key = { it.title }) { table ->
			ReferenceTableCard(table)
		}
	}

	if (state.languageNotes.isNotEmpty() && state.effectiveSubsection.showsLanguageNotes()) {
		item {
			ReferenceSectionHeader("Language Notes")
			BulletListCard(items = state.languageNotes)
		}
	}

	if (state.alignmentSummaries.isNotEmpty() && state.effectiveSubsection.showsAlignmentSummaries()) {
		item {
			ReferenceSectionHeader("Alignment Quick Reference")
			BulletListCard(items = state.alignmentSummaries)
		}
	}

	if (state.filteredRaces.isNotEmpty() && state.effectiveSubsection.showsPlayableSpecies()) {
		item { ReferenceSectionHeader("Playable Species") }
		items(state.filteredRaces, key = { it.name }) { race ->
			RaceReferenceCard(race)
		}
	}

	if (state.effectiveSubsection.showsPlayableClasses() && state.normalizedQuery.isBlank()) {
		item {
			ReferenceSectionHeader("Classes")
			InfoCard(
				title = stringResource(R.string.reference_class_unavailable_title),
				body = stringResource(R.string.reference_class_unavailable_body)
			)
		}
	}
}

private fun LazyListScope.renderCharacterCreationTailItems(state: CharacterCreationContentState) {
	if (state.showBeyondFirstLevel) {
		item {
			ReferenceSectionHeader("Beyond 1st Level")
			InfoCard("Leveling Up", CharacterCreationReference.BEYOND_FIRST_LEVEL)
		}
	}

	if (state.trinkets.isNotEmpty() && state.effectiveSubsection.showsTrinkets()) {
		item {
			ReferenceSectionHeader("Trinkets")
			BulletListCard(items = state.trinkets)
		}
	}
}

@Composable
private fun <T> ReferenceFilterChipRow(
	options: List<T>,
	selectedOption: T,
	onOptionSelected: (T) -> Unit,
	label: (T) -> String
) {
	LazyRow(
		modifier = Modifier.fillMaxWidth(),
		contentPadding = PaddingValues(horizontal = 16.dp),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		items(options, key = { label(it) }) { option ->
			FilterChip(
				selected = option == selectedOption,
				onClick = { onOptionSelected(option) },
				label = {
					Text(
						text = label(option),
						fontWeight = if (option == selectedOption) FontWeight.Bold else FontWeight.Normal
					)
				}
			)
		}
	}
}

@Composable
private fun AbilityModifiersCard() {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(4.dp)
		) {
			CharacterCreationReference.ABILITY_MODIFIERS.forEach { (score, modifier) ->
				DetailRow("Score $score", "Modifier $modifier")
			}
		}
	}
}

@Composable
internal fun CharacterCreationTextSectionCard(section: CharacterCreationTextSection) {
	InfoCard(title = section.title, body = section.body)
}

@Composable
private fun CharacterCreationStepCard(step: CharacterCreationStep) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = "Step ${step.number}: ${step.title}",
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			Text(text = step.content, style = MaterialTheme.typography.bodyMedium)

			step.example?.let { example ->
				Spacer(modifier = Modifier.height(4.dp))
				Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
					Column(modifier = Modifier.padding(12.dp)) {
						Text(
							"Example",
							style = MaterialTheme.typography.labelMedium,
							color = ArcaneTeal
						)
						Text(example, style = MaterialTheme.typography.bodySmall)
					}
				}
			}
		}
	}
}

@Composable
private fun BackgroundReferenceCard(background: BackgroundReference) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = background.name,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			StackedDetailRow("Ability Scores", background.abilityScores.joinToString())
			StackedDetailRow("Feat", background.feat)
			StackedDetailRow("Skill Proficiencies", background.skillProficiencies.joinToString())
			StackedDetailRow("Tool Proficiency", background.toolProficiency)

			if (background.equipmentOptions.isNotEmpty()) {
				CharacterCreationCardSectionHeader("Equipment Options")
				BulletListCard(items = background.equipmentOptions)
			}
		}
	}
}

@Composable
private fun FeatReferenceCard(feat: FeatReference, onClick: () -> Unit) {
	Card(
		modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = feat.name,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			StackedDetailRow("Category", feat.category)
			feat.prerequisite?.let { prerequisite ->
				StackedDetailRow("Prerequisite", prerequisite)
			}
			CharacterCreationCardSectionHeader("Benefits")
			BulletListCard(items = feat.benefits)
			if (feat.repeatable) {
				StackedDetailRow("Repeatable", "Yes")
			}
		}
	}
}

@Composable
private fun LanguageReferenceCard(language: LanguageReference) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp)
		) {
			Text(
				text = language.name,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			StackedDetailRow("Group", language.group)
			language.roll?.let { roll ->
				StackedDetailRow("Roll", roll)
			}
		}
	}
}

@Composable
private fun AbilityScoreSummaryCard(ability: AbilityScoreSummary) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(4.dp)
		) {
			Text(
				ability.ability,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			DetailRow("Measures", ability.measures)
			DetailRow("Important For", ability.importantFor)
			DetailRow("Species Increases", ability.racialIncreases)
		}
	}
}

@Composable
private fun RaceReferenceCard(race: RaceReference) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(10.dp)
		) {
			Text(
				text = race.name,
				style = MaterialTheme.typography.headlineSmall,
				fontWeight = FontWeight.Bold
			)
			Text(text = race.overview, style = MaterialTheme.typography.bodyMedium)

			CharacterCreationCardSectionHeader("Roleplaying")
			Text(text = race.personality, style = MaterialTheme.typography.bodyMedium)

			CharacterCreationCardSectionHeader("Culture & Society")
			Text(text = race.society, style = MaterialTheme.typography.bodyMedium)

			CharacterCreationCardSectionHeader("Adventurers")
			Text(text = race.adventurers, style = MaterialTheme.typography.bodyMedium)

			CharacterCreationCardSectionHeader("Names")
			Text(text = race.names, style = MaterialTheme.typography.bodyMedium)

			RaceReferenceStatsCard(race)

			if (race.traits.isNotEmpty()) {
				CharacterCreationCardSectionHeader("Core Traits")
				race.traits.forEach { trait ->
					TraitReferenceRow(trait)
				}
			}

			if (race.subraces.isNotEmpty()) {
				CharacterCreationCardSectionHeader("Species Variants")
				race.subraces.forEach { subrace ->
					SubraceReferenceCard(subrace)
				}
			}

			if (race.notes.isNotEmpty()) {
				CharacterCreationCardSectionHeader("Notes")
				BulletListCard(items = race.notes)
			}
		}
	}
}

@Composable
internal fun CharacterCreationCardSectionHeader(title: String) {
	Text(
		text = title,
		style = MaterialTheme.typography.titleSmall,
		fontWeight = FontWeight.Bold,
		color = ArcaneTeal
	)
}

@Composable
private fun RaceReferenceStatsCard(race: RaceReference) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(4.dp)
		) {
			DetailRow("Ability Score Increase", race.abilityScoreIncrease)
			DetailRow("Age", race.age)
			DetailRow("Size", race.size)
			DetailRow("Speed", race.speed)
			DetailRow("Languages", race.languages)
		}
	}
}

@Composable
private fun TraitReferenceRow(trait: RacialTraitReference) {
	Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
		Text(
			text = trait.name,
			style = MaterialTheme.typography.titleSmall,
			fontWeight = FontWeight.Bold
		)
		Text(
			text = trait.description,
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
		)
	}
}

@Composable
private fun SubraceReferenceCard(subrace: SubraceReference) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = subrace.name,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			Text(text = subrace.overview, style = MaterialTheme.typography.bodyMedium)
			subrace.abilityScoreIncrease?.let { abilityIncrease ->
				DetailRow("Ability Score Increase", abilityIncrease)
			}
			subrace.traits.forEach { trait ->
				TraitReferenceRow(trait)
			}
		}
	}
}

private fun CharacterCreationTextSection.matchesQuery(query: String): Boolean {
	return matchesQuery(query, title, body)
}

private fun CharacterCreationStep.matchesQuery(query: String): Boolean {
	return matchesQuery(query, number.toString(), title, content, example.orEmpty())
}

private fun AbilityScoreSummary.matchesQuery(query: String): Boolean {
	return matchesQuery(query, ability, measures, importantFor, racialIncreases)
}

internal fun filterCharacterCreationBackgrounds(query: String): List<BackgroundReference> {
	return CharacterCreationReference.BACKGROUNDS.filter { it.matchesSearchQuery(query) }
}

internal fun filterCharacterCreationFeats(query: String): List<FeatReference> {
	return CharacterCreationReference.FEATS.filter { it.matchesSearchQuery(query) }
}

internal fun filterCharacterCreationLanguages(query: String): List<LanguageReference> {
	return (CharacterCreationReference.STANDARD_LANGUAGES + CharacterCreationReference.RARE_LANGUAGES)
		.filter { it.matchesSearchQuery(query) }
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

internal fun BackgroundReference.matchesSearchQuery(query: String): Boolean {
	return matchesQuery(
		query,
		name,
		feat,
		toolProficiency,
		*backgroundSearchAliases().toTypedArray(),
		*abilityScores.toTypedArray(),
		*skillProficiencies.toTypedArray(),
		*equipmentOptions.toTypedArray()
	)
}

private fun BackgroundReference.backgroundSearchAliases(): List<String> = when (name) {
	"Acolyte" -> listOf("Magic Initiate", "Cleric")
	"Sage" -> listOf("Magic Initiate", "Wizard")
	else -> emptyList()
}

internal fun FeatReference.matchesSearchQuery(query: String): Boolean {
	return matchesQuery(
		query,
		name,
		category,
		prerequisite.orEmpty(),
		*benefits.toTypedArray(),
		if (repeatable) "Repeatable" else ""
	)
}

internal fun LanguageReference.matchesSearchQuery(query: String): Boolean {
	return matchesQuery(query, name, group, roll.orEmpty())
}

private fun RaceReference.matchesQuery(query: String): Boolean {
	return matchesQuery(
		query,
		name,
		overview,
		personality,
		society,
		adventurers,
		names,
		abilityScoreIncrease,
		age,
		size,
		speed,
		languages
	) || traits.any { it.matchesQuery(query) } ||
		subraces.any { it.matchesQuery(query) } ||
		notes.any { it.contains(query, ignoreCase = true) }
}

private fun SubraceReference.matchesQuery(query: String): Boolean {
	return matchesQuery(query, name, overview, abilityScoreIncrease.orEmpty()) ||
		traits.any { it.matchesQuery(query) }
}

private fun RacialTraitReference.matchesQuery(query: String): Boolean {
	return matchesQuery(query, name, description)
}

private fun openFeatReferenceDetail(
	onOpenDetail: (String, String) -> Unit,
	name: String
) {
	onOpenDetail(ReferenceDetailResolver.CATEGORY_FEATS, ReferenceDetailResolver.slugFor(name))
}
