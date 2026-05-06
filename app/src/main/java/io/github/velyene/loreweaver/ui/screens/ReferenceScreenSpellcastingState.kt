package io.github.velyene.loreweaver.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.velyene.loreweaver.domain.util.AreaOfEffect
import io.github.velyene.loreweaver.domain.util.CastingTime
import io.github.velyene.loreweaver.domain.util.ReferenceTable
import io.github.velyene.loreweaver.domain.util.SchoolOfMagic
import io.github.velyene.loreweaver.domain.util.SpellComponent
import io.github.velyene.loreweaver.domain.util.SpellSlotTable
import io.github.velyene.loreweaver.domain.util.SpellcasterType
import io.github.velyene.loreweaver.domain.util.SpellcastingReference
import io.github.velyene.loreweaver.domain.util.SrdSpellIndexReference
import io.github.velyene.loreweaver.ui.viewmodels.ReferenceUiState

internal data class SpellcastingContentState(
	val selectedSpellcasterType: SpellcasterType,
	val abilityModifier: Int,
	val proficiencyBonus: Int,
	val bonus: Int,
	val casterLevel: Int,
	val saveDc: Int,
	val attackBonus: Int,
	val selectedSpellSlots: SpellSlotTable?,
	val rules: List<Pair<String, String>>,
	val tables: List<ReferenceTable>,
	val schools: List<SchoolOfMagic>,
	val components: List<SpellComponent>,
	val castingTimes: List<CastingTime>,
	val areas: List<AreaOfEffect>,
	val tips: List<String>,
	val concentrationBreakers: List<String>,
	val spellSlots: List<SpellSlotTable>,
	val spellNames: List<String>,
	val magicTheoryOverview: String?
) {
	val hasResults: Boolean
		get() = rules.isNotEmpty() ||
			tables.isNotEmpty() ||
			schools.isNotEmpty() ||
			components.isNotEmpty() ||
			castingTimes.isNotEmpty() ||
			areas.isNotEmpty() ||
			tips.isNotEmpty() ||
			concentrationBreakers.isNotEmpty() ||
			spellSlots.isNotEmpty() ||
			spellNames.isNotEmpty() ||
			magicTheoryOverview != null
}

@Composable
internal fun rememberSpellcastingContentState(uiState: ReferenceUiState): SpellcastingContentState {
	val normalizedQuery = uiState.searchQuery.trim()
	return remember(
		normalizedQuery,
		uiState.selectedSpellcasterType,
		uiState.spellcastingAbilityModifierInput,
		uiState.spellcastingProficiencyBonusInput,
		uiState.spellcastingBonusInput,
		uiState.spellcastingCasterLevelInput
	) {
		val selectedSpellcasterType = uiState.selectedSpellcasterType
		val abilityModifier = uiState.spellcastingAbilityModifierInput.toIntOrNull() ?: 0
		val proficiencyBonus = uiState.spellcastingProficiencyBonusInput.toIntOrNull() ?: 0
		val bonus = uiState.spellcastingBonusInput.toIntOrNull() ?: 0
		val casterLevel = (uiState.spellcastingCasterLevelInput.toIntOrNull() ?: 1).coerceIn(1, 20)
		val saveDc = SpellcastingReference.calculateSpellSaveDC(
			spellcastingMod = abilityModifier,
			proficiencyBonus = proficiencyBonus,
			bonuses = bonus
		)
		val attackBonus = SpellcastingReference.calculateSpellAttackBonus(
			spellcastingMod = abilityModifier,
			proficiencyBonus = proficiencyBonus,
			bonuses = bonus
		)

		SpellcastingContentState(
			selectedSpellcasterType = selectedSpellcasterType,
			abilityModifier = abilityModifier,
			proficiencyBonus = proficiencyBonus,
			bonus = bonus,
			casterLevel = casterLevel,
			saveDc = saveDc,
			attackBonus = attackBonus,
			selectedSpellSlots = SpellcastingReference.getSpellSlotsForCaster(
				selectedSpellcasterType,
				casterLevel
			),
			rules = filterSpellcastingRules(normalizedQuery),
			tables = filterSpellcastingTables(normalizedQuery),
			schools = SchoolOfMagic.entries.filterByQuery(
				normalizedQuery,
				{ school -> school.toDisplayLabel() },
				SpellcastingReference::getSchoolDescription
			),
			components = SpellComponent.entries.filterByQuery(
				normalizedQuery,
				{ component -> component.toDisplayLabel() },
				SpellcastingReference::getComponentDescription
			),
			castingTimes = CastingTime.entries.filterByQuery(
				normalizedQuery,
				{ castingTime -> castingTime.toDisplayLabel() },
				SpellcastingReference::getCastingTimeDescription
			),
			areas = AreaOfEffect.entries.filterByQuery(
				normalizedQuery,
				{ area -> area.toDisplayLabel() },
				SpellcastingReference::getAreaOfEffectRules
			),
			tips = SpellcastingReference.getSpellcastingTips().filterByQuery(normalizedQuery),
			concentrationBreakers = SpellcastingReference.getConcentrationBreakers()
				.filterByQuery(normalizedQuery),
			spellSlots = SpellcastingReference.getSpellSlotTableForCaster(selectedSpellcasterType)
				.filterSpellSlotsByQuery(normalizedQuery),
			spellNames = SrdSpellIndexReference.search(normalizedQuery),
			magicTheoryOverview = SpellcastingReference.getMagicTheoryOverview().takeIf {
				matchesQuery(
					normalizedQuery,
					it,
					"magic theory",
					"spellcasting theory",
					"magic in play"
				)
			}
		)
	}
}

