/*
 * FILE: ReferenceScreenSpellcastingSections.kt
 *
 * TABLE OF CONTENTS:
 * 1. Spellcasting Section Builders
 * 2. Rule and Table Sections
 * 3. Spellcasting Helper Sections
 */

package io.github.velyene.loreweaver.ui.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.util.SpellcasterType
import io.github.velyene.loreweaver.domain.util.SpellcastingReference
import io.github.velyene.loreweaver.ui.viewmodels.ReferenceUiState

@Composable
internal fun SpellcastingOverviewSection(
	uiState: ReferenceUiState,
	state: SpellcastingContentState,
	shareChooserTitle: String,
	context: Context,
	onSpellcasterTypeChange: (SpellcasterType) -> Unit,
	onAbilityModifierChange: (String) -> Unit,
	onProficiencyBonusChange: (String) -> Unit,
	onSpellBonusChange: (String) -> Unit,
	onCasterLevelChange: (String) -> Unit
) {
	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		ReferenceTitleWithShare(
			title = stringResource(R.string.reference_tab_spellcasting),
			onShare = {
				shareReferenceText(
					context = context,
					chooserTitle = shareChooserTitle,
					text = buildSpellcastingShareText(
						spellcasterType = state.selectedSpellcasterType,
						abilityModifier = state.abilityModifier,
						proficiencyBonus = state.proficiencyBonus,
						bonus = state.bonus,
						saveDc = state.saveDc,
						attackBonus = state.attackBonus,
						slotTable = state.selectedSpellSlots
					)
				)
			}
		)

		SpellcasterTypeTabs(
			selectedType = state.selectedSpellcasterType,
			onTypeSelected = onSpellcasterTypeChange
		)

		InfoCard(
			title = stringResource(
				R.string.spellcasting_profile_title,
				state.selectedSpellcasterType.toDisplayLabel()
			),
			body = SpellcastingReference.getSpellcasterDescription(state.selectedSpellcasterType)
		)

		SpellcastingCalculatorCard(
			abilityModifierInput = uiState.spellcastingAbilityModifierInput,
			proficiencyBonusInput = uiState.spellcastingProficiencyBonusInput,
			spellBonusInput = uiState.spellcastingBonusInput,
			saveDc = state.saveDc,
			attackBonus = state.attackBonus,
			onAbilityModifierChange = onAbilityModifierChange,
			onProficiencyBonusChange = onProficiencyBonusChange,
			onSpellBonusChange = onSpellBonusChange
		)

		SpellSlotCalculatorCard(
			spellcasterType = state.selectedSpellcasterType,
			casterLevelInput = uiState.spellcastingCasterLevelInput,
			selectedLevel = state.casterLevel,
			slotTable = state.selectedSpellSlots,
			onCasterLevelChange = onCasterLevelChange
		)

		FormulaCard(
			title = stringResource(R.string.spellcasting_formula_title),
			rows = listOf(
				stringResource(R.string.spellcasting_formula_save_dc) to state.saveDc.toString(),
				stringResource(R.string.spellcasting_formula_attack_bonus) to formatSignedNumber(
					state.attackBonus
				)
			)
		)
	}
}

@Composable
internal fun SpellcastingRulesSection(state: SpellcastingContentState) {
	if (state.rules.isEmpty() && state.tables.isEmpty() && state.concentrationBreakers.isEmpty()) return

	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		if (state.rules.isNotEmpty() || state.tables.isNotEmpty()) {
			ReferenceSectionHeader(stringResource(R.string.spellcasting_rules_section_title))

			state.rules.forEach { (title, body) ->
				InfoCard(title = title, body = body)
			}

			state.tables.forEach { table ->
				ReferenceTableCard(table)
			}
		}

		if (state.concentrationBreakers.isNotEmpty()) {
			ReferenceSectionHeader(stringResource(R.string.spellcasting_concentration_section_title))
			BulletListCard(items = state.concentrationBreakers)
		}
	}
}

@Composable
internal fun SpellcastingGlossarySection(state: SpellcastingContentState) {
	if (
		state.schools.isEmpty() &&
			state.components.isEmpty() &&
			state.castingTimes.isEmpty() &&
			state.areas.isEmpty()
	) return

	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		SpellcastingInfoGroup(
			title = stringResource(R.string.spellcasting_schools_section_title),
			items = state.schools,
			label = { it.toDisplayLabel() },
			description = SpellcastingReference::getSchoolDescription
		)
		SpellcastingInfoGroup(
			title = stringResource(R.string.spellcasting_components_section_title),
			items = state.components,
			label = { it.toDisplayLabel() },
			description = SpellcastingReference::getComponentDescription
		)
		SpellcastingInfoGroup(
			title = stringResource(R.string.spellcasting_casting_times_section_title),
			items = state.castingTimes,
			label = { it.toDisplayLabel() },
			description = SpellcastingReference::getCastingTimeDescription
		)
		SpellcastingInfoGroup(
			title = stringResource(R.string.spellcasting_areas_section_title),
			items = state.areas,
			label = { it.toDisplayLabel() },
			description = SpellcastingReference::getAreaOfEffectRules
		)
	}
}

@Composable
internal fun SpellcastingProgressionSection(
	state: SpellcastingContentState,
	onOpenSpellDetail: (String) -> Unit
) {
	if (
		state.spellSlots.isEmpty() &&
			state.spellNames.isEmpty() &&
			state.tips.isEmpty() &&
			state.magicTheoryOverview == null
	) {
		return
	}

	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		if (state.spellSlots.isNotEmpty()) {
			ReferenceSectionHeader(stringResource(R.string.spellcasting_slots_section_title))
			state.spellSlots.forEach { slotTable ->
				SpellSlotCard(slotTable)
			}
		}

		if (state.spellNames.isNotEmpty()) {
			ReferenceSectionHeader(stringResource(R.string.reference_spellcasting_verified_srd_spell_names))
			SpellNameListCard(
				spellNames = state.spellNames,
				onOpenSpellDetail = onOpenSpellDetail
			)
		}

		if (state.tips.isNotEmpty()) {
			ReferenceSectionHeader(stringResource(R.string.spellcasting_tips_section_title))
			BulletListCard(items = state.tips)
		}

		state.magicTheoryOverview?.let { magicTheoryOverview ->
			ReferenceSectionHeader(stringResource(R.string.spellcasting_weave_section_title))
			InfoCard(
				title = stringResource(R.string.spellcasting_weave_card_title),
				body = magicTheoryOverview
			)
		}
	}
}

@Composable
internal fun SpellNameListCard(
	spellNames: List<String>,
	onOpenSpellDetail: (String) -> Unit
) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			spellNames.forEach { spell ->
				Text(
					text = spell,
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.primary,
					modifier = Modifier
						.fillMaxWidth()
						.clickable { onOpenSpellDetail(spell) }
						.padding(vertical = 2.dp)
				)
			}
		}
	}
}

@Composable
internal fun <T> SpellcastingInfoGroup(
	title: String,
	items: List<T>,
	label: (T) -> String,
	description: (T) -> String
) {
	if (items.isEmpty()) return

	ReferenceSectionHeader(title)
	items.forEach { item ->
		InfoCard(title = label(item), body = description(item))
	}
}

@Composable
internal fun SpellcasterTypeTabs(
	selectedType: SpellcasterType,
	onTypeSelected: (SpellcasterType) -> Unit
) {
	PrimaryScrollableTabRow(
		selectedTabIndex = selectedType.ordinal,
		containerColor = MaterialTheme.colorScheme.surfaceVariant,
		contentColor = MaterialTheme.colorScheme.onSurface
	) {
		SpellcasterType.entries.forEach { type ->
			Tab(
				selected = type == selectedType,
				onClick = { onTypeSelected(type) },
				text = {
					Text(
						text = type.toDisplayLabel(),
						fontSize = 12.sp,
						fontWeight = if (type == selectedType) FontWeight.Bold else FontWeight.Normal
					)
				}
			)
		}
	}
}

