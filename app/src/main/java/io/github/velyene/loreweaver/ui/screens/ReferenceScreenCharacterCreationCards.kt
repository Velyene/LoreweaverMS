/*
 * FILE: ReferenceScreenCharacterCreationCards.kt
 *
 * TABLE OF CONTENTS:
 * 1. Character Creation Summary Cards
 * 2. Table and Section Cards
 * 3. Shared Card Helpers
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import io.github.velyene.loreweaver.domain.util.SubraceReference
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal

@Composable
internal fun <T> ReferenceFilterChipRow(
	options: List<T>,
	selectedOption: T,
	onOptionSelected: (T) -> Unit,
	label: @Composable (T) -> String
) {
	LazyRow(
		modifier = Modifier.fillMaxWidth(),
		contentPadding = PaddingValues(horizontal = 16.dp),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		items(options, key = { option -> option.hashCode() }) { option ->
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
internal fun AbilityModifiersCard() {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(4.dp)
		) {
			CharacterCreationReference.ABILITY_MODIFIERS.forEach { (score, modifier) ->
				DetailRow(
					stringResource(R.string.reference_character_creation_score_value, score),
					stringResource(R.string.reference_character_creation_modifier_value, modifier)
				)
			}
		}
	}
}

@Composable
internal fun CharacterCreationTextSectionCard(section: CharacterCreationTextSection) {
	InfoCard(title = section.title, body = section.body)
}

@Composable
internal fun CharacterCreationStepCard(step: CharacterCreationStep) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = stringResource(R.string.reference_character_creation_step_title, step.number, step.title),
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			Text(text = step.content, style = MaterialTheme.typography.bodyMedium)

			step.example?.let { example ->
				Spacer(modifier = Modifier.height(4.dp))
				Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
					Column(modifier = Modifier.padding(12.dp)) {
						Text(
							stringResource(R.string.reference_character_creation_example),
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
internal fun BackgroundReferenceCard(background: BackgroundReference) {
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
			StackedDetailRow(stringResource(R.string.reference_character_creation_ability_scores), background.abilityScores.joinToString())
			StackedDetailRow(stringResource(R.string.reference_character_creation_feat), background.feat)
			StackedDetailRow(stringResource(R.string.reference_character_creation_skill_proficiencies), background.skillProficiencies.joinToString())
			StackedDetailRow(stringResource(R.string.reference_character_creation_tool_proficiency), background.toolProficiency)

			if (background.equipmentOptions.isNotEmpty()) {
				CharacterCreationCardSectionHeader(stringResource(R.string.reference_character_creation_equipment_options))
				BulletListCard(items = background.equipmentOptions)
			}
		}
	}
}

@Composable
internal fun FeatReferenceCard(feat: FeatReference, onClick: () -> Unit) {
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
			StackedDetailRow(stringResource(R.string.reference_character_creation_category), feat.category)
			feat.prerequisite?.let { prerequisite ->
				StackedDetailRow(stringResource(R.string.reference_character_creation_prerequisite), prerequisite)
			}
			CharacterCreationCardSectionHeader(stringResource(R.string.reference_character_creation_benefits))
			BulletListCard(items = feat.benefits)
			if (feat.repeatable) {
				StackedDetailRow(
					stringResource(R.string.reference_character_creation_repeatable),
					stringResource(R.string.reference_character_creation_yes)
				)
			}
		}
	}
}

@Composable
internal fun LanguageReferenceCard(language: LanguageReference) {
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
			StackedDetailRow(stringResource(R.string.reference_character_creation_group), language.group)
			language.roll?.let { roll ->
				StackedDetailRow(stringResource(R.string.reference_character_creation_roll), roll)
			}
		}
	}
}

@Composable
internal fun AbilityScoreSummaryCard(ability: AbilityScoreSummary) {
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
			DetailRow(stringResource(R.string.reference_character_creation_measures), ability.measures)
			DetailRow(stringResource(R.string.reference_character_creation_important_for), ability.importantFor)
			DetailRow(stringResource(R.string.reference_character_creation_species_increases), ability.racialIncreases)
		}
	}
}

@Composable
internal fun RaceReferenceCard(race: RaceReference) {
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

			CharacterCreationCardSectionHeader(stringResource(R.string.reference_character_creation_roleplaying))
			Text(text = race.personality, style = MaterialTheme.typography.bodyMedium)

			CharacterCreationCardSectionHeader(stringResource(R.string.reference_character_creation_culture_society))
			Text(text = race.society, style = MaterialTheme.typography.bodyMedium)

			CharacterCreationCardSectionHeader(stringResource(R.string.reference_character_creation_adventurers))
			Text(text = race.adventurers, style = MaterialTheme.typography.bodyMedium)

			CharacterCreationCardSectionHeader(stringResource(R.string.reference_character_creation_names))
			Text(text = race.names, style = MaterialTheme.typography.bodyMedium)

			RaceReferenceStatsCard(race)

			if (race.traits.isNotEmpty()) {
				CharacterCreationCardSectionHeader(stringResource(R.string.reference_character_creation_core_traits))
				race.traits.forEach { trait ->
					TraitReferenceRow(trait)
				}
			}

			if (race.subraces.isNotEmpty()) {
				CharacterCreationCardSectionHeader(stringResource(R.string.reference_character_creation_species_variants))
				race.subraces.forEach { subrace ->
					SubraceReferenceCard(subrace)
				}
			}

			if (race.notes.isNotEmpty()) {
				CharacterCreationCardSectionHeader(stringResource(R.string.notes_label))
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
internal fun RaceReferenceStatsCard(race: RaceReference) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(4.dp)
		) {
			DetailRow(stringResource(R.string.reference_character_creation_ability_score_increase), race.abilityScoreIncrease)
			DetailRow(stringResource(R.string.reference_character_creation_age), race.age)
			DetailRow(stringResource(R.string.reference_character_creation_size), race.size)
			DetailRow(stringResource(R.string.speed_label), race.speed)
			DetailRow(stringResource(R.string.reference_character_creation_languages), race.languages)
		}
	}
}

@Composable
internal fun TraitReferenceRow(trait: RacialTraitReference) {
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
internal fun SubraceReferenceCard(subrace: SubraceReference) {
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
				DetailRow(stringResource(R.string.reference_character_creation_ability_score_increase), abilityIncrease)
			}
			subrace.traits.forEach { trait ->
				TraitReferenceRow(trait)
			}
		}
	}
}
