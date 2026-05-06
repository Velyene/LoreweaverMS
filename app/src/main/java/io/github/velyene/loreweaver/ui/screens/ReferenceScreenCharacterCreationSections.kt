/*
 * FILE: ReferenceScreenCharacterCreationSections.kt
 *
 * TABLE OF CONTENTS:
 * 1. LazyListScope renderer: foundation items
 * 2. LazyListScope renderer: guidance items
 * 3. LazyListScope renderer: tail items
 * 4. Detail navigation helper: openFeatReferenceDetail
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.res.stringResource
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.util.CharacterCreationReference
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver

internal fun LazyListScope.renderCharacterCreationFoundationItems(
	state: CharacterCreationContentState,
	onOpenDetail: (String, String) -> Unit
) {
	if (state.showIntroduction && state.effectiveSubsection.showsIntroduction()) {
		item {
			ReferenceSectionHeader(stringResource(R.string.reference_character_creation_introduction))
			InfoCard(
				stringResource(R.string.reference_character_creation_creating_adventurer),
				CharacterCreationReference.INTRODUCTION
			)
		}
	}

	if (state.visibleRaceChapterSections.isNotEmpty()) {
		item { ReferenceSectionHeader(stringResource(R.string.reference_character_creation_species_guidance)) }
		items(state.visibleRaceChapterSections, key = { it.title }) { section ->
			CharacterCreationTextSectionCard(section)
		}
	}

	if (state.visibleSteps.isNotEmpty()) {
		item { ReferenceSectionHeader(stringResource(R.string.reference_character_creation_step_by_step)) }
		items(state.visibleSteps, key = { it.number }) { step ->
			CharacterCreationStepCard(step)
		}
	}

	if (state.visibleBackgrounds.isNotEmpty() && state.effectiveSubsection.showsBackgrounds()) {
		item { ReferenceSectionHeader(stringResource(R.string.reference_character_creation_backgrounds)) }
		items(state.visibleBackgrounds, key = { it.name }) { background ->
			BackgroundReferenceCard(background)
		}
	}

	if (state.visibleFeats.isNotEmpty() && state.effectiveSubsection.showsFeats()) {
		item { ReferenceSectionHeader(stringResource(R.string.reference_character_creation_feats)) }
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
		item { ReferenceSectionHeader(stringResource(R.string.reference_character_creation_languages)) }
		items(state.visibleLanguages, key = { "${it.group}-${it.name}" }) { language ->
			LanguageReferenceCard(language)
		}
	}

	if (state.filteredAbilities.isNotEmpty() && state.effectiveSubsection.showsAbilitySummaries()) {
		item { ReferenceSectionHeader(stringResource(R.string.reference_character_creation_ability_scores_summary)) }
		items(state.filteredAbilities, key = { it.ability }) { ability ->
			AbilityScoreSummaryCard(ability)
		}
	}

	if (state.showAbilityModifiers && state.effectiveSubsection.showsAbilityModifiers()) {
		item {
			ReferenceSectionHeader(stringResource(R.string.reference_character_creation_ability_modifiers))
			AbilityModifiersCard()
		}
	}
}

internal fun LazyListScope.renderCharacterCreationGuidanceItems(state: CharacterCreationContentState) {
	if (state.visibleCharacterCreationSections.isNotEmpty()) {
		item { ReferenceSectionHeader(stringResource(R.string.reference_character_creation_core_guidance)) }
		items(state.visibleCharacterCreationSections, key = { it.title }) { section ->
			CharacterCreationTextSectionCard(section)
		}
	}

	if (state.visibleCharacterCreationTables.isNotEmpty()) {
		item { ReferenceSectionHeader(stringResource(R.string.reference_character_creation_tables)) }
		items(state.visibleCharacterCreationTables, key = { it.title }) { table ->
			ReferenceTableCard(table)
		}
	}

	if (state.languageNotes.isNotEmpty() && state.effectiveSubsection.showsLanguageNotes()) {
		item {
			ReferenceSectionHeader(stringResource(R.string.reference_character_creation_language_notes))
			BulletListCard(items = state.languageNotes)
		}
	}

	if (state.alignmentSummaries.isNotEmpty() && state.effectiveSubsection.showsAlignmentSummaries()) {
		item {
			ReferenceSectionHeader(stringResource(R.string.reference_character_creation_alignment_quick_reference))
			BulletListCard(items = state.alignmentSummaries)
		}
	}

	if (state.filteredRaces.isNotEmpty() && state.effectiveSubsection.showsPlayableSpecies()) {
		item { ReferenceSectionHeader(stringResource(R.string.reference_character_creation_playable_species)) }
		items(state.filteredRaces, key = { it.name }) { race ->
			RaceReferenceCard(race)
		}
	}

	if (state.effectiveSubsection.showsPlayableClasses() && state.normalizedQuery.isBlank()) {
		item {
			ReferenceSectionHeader(stringResource(R.string.reference_character_creation_classes))
			InfoCard(
				title = stringResource(R.string.reference_class_unavailable_title),
				body = stringResource(R.string.reference_class_unavailable_body)
			)
		}
	}
}

internal fun LazyListScope.renderCharacterCreationTailItems(state: CharacterCreationContentState) {
	if (state.showBeyondFirstLevel) {
		item {
			ReferenceSectionHeader(stringResource(R.string.reference_character_creation_beyond_first_level))
			InfoCard(
				stringResource(R.string.reference_character_creation_leveling_up),
				CharacterCreationReference.BEYOND_FIRST_LEVEL
			)
		}
	}

	if (state.trinkets.isNotEmpty() && state.effectiveSubsection.showsTrinkets()) {
		item {
			ReferenceSectionHeader(stringResource(R.string.reference_character_creation_trinkets))
			BulletListCard(items = state.trinkets)
		}
	}
}

private fun openFeatReferenceDetail(
	onOpenDetail: (String, String) -> Unit,
	name: String
) {
	onOpenDetail(ReferenceDetailResolver.CATEGORY_FEATS, ReferenceDetailResolver.slugFor(name))
}

