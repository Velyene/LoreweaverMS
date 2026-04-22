/*
 * FILE: ReferenceScreenSpellcasting.kt
 *
 * TABLE OF CONTENTS:
 * 1. Spellcasting content entry point and derived state
 * 2. Overview, rules, glossary, and progression sections
 * 3. Spellcasting calculators and presentation helpers
 * 4. Search, filtering, and share-text helpers
 */

package com.example.loreweaver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loreweaver.R
import com.example.loreweaver.domain.util.AreaOfEffect
import com.example.loreweaver.domain.util.CastingTime
import com.example.loreweaver.domain.util.ReferenceTable
import com.example.loreweaver.domain.util.SchoolOfMagic
import com.example.loreweaver.domain.util.SpellComponent
import com.example.loreweaver.domain.util.SpellSlotTable
import com.example.loreweaver.domain.util.SpellcasterType
import com.example.loreweaver.domain.util.SpellcastingReference
import com.example.loreweaver.domain.util.SrdSpellIndexReference
import com.example.loreweaver.ui.viewmodels.ReferenceUiState

@Composable
internal fun SpellcastingContent(
	uiState: ReferenceUiState,
	listState: LazyListState,
	onSpellcasterTypeChange: (SpellcasterType) -> Unit,
	onAbilityModifierChange: (String) -> Unit,
	onProficiencyBonusChange: (String) -> Unit,
	onSpellBonusChange: (String) -> Unit,
	onCasterLevelChange: (String) -> Unit,
	onOpenSpellDetail: (String) -> Unit
) {
	val context = LocalContext.current
	val shareChooserTitle = stringResource(R.string.reference_share_chooser_title)
	val spellcastingState = rememberSpellcastingContentState(uiState)

	if (!spellcastingState.hasResults) {
		ReferenceNoResultsState()
		return
	}

	LazyColumn(
		state = listState,
		modifier = Modifier.fillMaxSize(),
		contentPadding = PaddingValues(16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		item {
			SpellcastingOverviewSection(
				uiState = uiState,
				state = spellcastingState,
				shareChooserTitle = shareChooserTitle,
				context = context,
				onSpellcasterTypeChange = onSpellcasterTypeChange,
				onAbilityModifierChange = onAbilityModifierChange,
				onProficiencyBonusChange = onProficiencyBonusChange,
				onSpellBonusChange = onSpellBonusChange,
				onCasterLevelChange = onCasterLevelChange
			)
		}

		item { SpellcastingRulesSection(state = spellcastingState) }
		item { SpellcastingGlossarySection(state = spellcastingState) }
		item {
			SpellcastingProgressionSection(
				state = spellcastingState,
				onOpenSpellDetail = onOpenSpellDetail
			)
		}
	}
}

private data class SpellcastingContentState(
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
private fun rememberSpellcastingContentState(uiState: ReferenceUiState): SpellcastingContentState {
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

@Composable
private fun SpellcastingOverviewSection(
	uiState: ReferenceUiState,
	state: SpellcastingContentState,
	shareChooserTitle: String,
	context: android.content.Context,
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
private fun SpellcastingRulesSection(state: SpellcastingContentState) {
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
private fun SpellcastingGlossarySection(state: SpellcastingContentState) {
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
private fun SpellcastingProgressionSection(
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
			ReferenceSectionHeader("Verified SRD Spell Names")
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
private fun SpellNameListCard(
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
private fun <T> SpellcastingInfoGroup(
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
private fun SpellcasterTypeTabs(
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

internal fun filterSpellcastingRules(query: String): List<Pair<String, String>> {
	return SpellcastingReference.getSpellcastingRules()
		.entries
		.filter { matchesQuery(query, it.key, it.value) }
		.map { it.toPair() }
}

internal fun filterSpellcastingTables(query: String): List<ReferenceTable> {
	return SpellcastingReference.getSpellcastingTables().filter { it.matchesQuery(query) }
}

internal inline fun <T> Iterable<T>.filterByQuery(
	query: String,
	crossinline label: (T) -> String,
	crossinline description: (T) -> String
): List<T> {
	return filter { entry -> matchesQuery(query, label(entry), description(entry)) }
}

internal fun List<String>.filterByQuery(query: String): List<String> {
	return if (query.isBlank()) this else filter { it.contains(query, ignoreCase = true) }
}

private fun List<SpellSlotTable>.filterSpellSlotsByQuery(query: String): List<SpellSlotTable> {
	return filter { table ->
		query.isBlank() ||
			"level ${table.characterLevel}".contains(query, ignoreCase = true) ||
			table.characterLevel.toString() == query
	}
}

@Composable
private fun FormulaCard(title: String, rows: List<Pair<String, String>>) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp)
		) {
			Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
			rows.forEach { (label, value) ->
				DetailRow(label, value)
			}
		}
	}
}

@Composable
private fun SpellcastingCalculatorCard(
	abilityModifierInput: String,
	proficiencyBonusInput: String,
	spellBonusInput: String,
	saveDc: Int,
	attackBonus: Int,
	onAbilityModifierChange: (String) -> Unit,
	onProficiencyBonusChange: (String) -> Unit,
	onSpellBonusChange: (String) -> Unit
) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			Text(
				text = stringResource(R.string.spellcasting_calculator_title),
				style = MaterialTheme.typography.titleSmall,
				fontWeight = FontWeight.Bold
			)
			Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
				OutlinedTextField(
					value = abilityModifierInput,
					onValueChange = onAbilityModifierChange,
					label = { Text(stringResource(R.string.spellcasting_ability_modifier_label)) },
					modifier = Modifier.weight(1f),
					keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
					singleLine = true
				)
				OutlinedTextField(
					value = proficiencyBonusInput,
					onValueChange = onProficiencyBonusChange,
					label = { Text(stringResource(R.string.spellcasting_proficiency_bonus_label)) },
					modifier = Modifier.weight(1f),
					keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
					singleLine = true
				)
			}
			OutlinedTextField(
				value = spellBonusInput,
				onValueChange = onSpellBonusChange,
				label = { Text(stringResource(R.string.spellcasting_misc_bonus_label)) },
				modifier = Modifier.fillMaxWidth(),
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
				singleLine = true
			)
			Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
				ResultBadge(
					label = stringResource(R.string.spellcasting_formula_save_dc),
					value = saveDc.toString(),
					modifier = Modifier.weight(1f)
				)
				ResultBadge(
					label = stringResource(R.string.spellcasting_formula_attack_bonus),
					value = formatSignedNumber(attackBonus),
					modifier = Modifier.weight(1f)
				)
			}
		}
	}
}

@Composable
private fun SpellSlotCalculatorCard(
	spellcasterType: SpellcasterType,
	casterLevelInput: String,
	selectedLevel: Int,
	slotTable: SpellSlotTable?,
	onCasterLevelChange: (String) -> Unit
) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			Text(
				text = stringResource(R.string.spellcasting_slot_calculator_title),
				style = MaterialTheme.typography.titleSmall,
				fontWeight = FontWeight.Bold
			)
			OutlinedTextField(
				value = casterLevelInput,
				onValueChange = onCasterLevelChange,
				label = { Text(stringResource(R.string.spellcasting_caster_level_label)) },
				modifier = Modifier.fillMaxWidth(),
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
				singleLine = true,
				supportingText = {
					Text(
						stringResource(
							R.string.spellcasting_selected_progression_summary,
							spellcasterType.toDisplayLabel(),
							selectedLevel
						)
					)
				}
			)
			slotTable?.let { SpellSlotCard(slotTable = it) }
		}
	}
}

@Composable
private fun ResultBadge(
	label: String,
	value: String,
	modifier: Modifier = Modifier
) {
	Column(
		modifier = modifier
			.background(
				color = MaterialTheme.colorScheme.primaryContainer,
				shape = MaterialTheme.shapes.medium
			)
			.padding(12.dp),
		verticalArrangement = Arrangement.spacedBy(4.dp)
	) {
		Text(
			label,
			style = MaterialTheme.typography.labelSmall,
			color = MaterialTheme.colorScheme.onPrimaryContainer
		)
		Text(
			value,
			style = MaterialTheme.typography.titleMedium,
			fontWeight = FontWeight.Bold
		)
	}
}

@Composable
private fun SpellSlotCard(slotTable: SpellSlotTable) {
	val slots = listOf(
		slotTable.slot1,
		slotTable.slot2,
		slotTable.slot3,
		slotTable.slot4,
		slotTable.slot5,
		slotTable.slot6,
		slotTable.slot7,
		slotTable.slot8,
		slotTable.slot9
	)
		.mapIndexedNotNull { index, value ->
			if (value > 0) "${SpellcastingReference.getSpellLevelName(index + 1)} ×$value" else null
		}

	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp)
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = stringResource(
						R.string.spellcasting_level_row,
						slotTable.characterLevel
					),
					style = MaterialTheme.typography.titleSmall,
					fontWeight = FontWeight.Bold
				)
				Text(
					text = stringResource(R.string.spellcasting_cantrips_row, slotTable.cantrips),
					fontSize = 12.sp,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
			if (slots.isNotEmpty()) {
				Text(
					text = slots.joinToString("  •  "),
					style = MaterialTheme.typography.bodyMedium
				)
			}
		}
	}
}

private fun formatSignedNumber(value: Int): String = if (value >= 0) "+$value" else value.toString()

private fun buildSpellcastingShareText(
	spellcasterType: SpellcasterType,
	abilityModifier: Int,
	proficiencyBonus: Int,
	bonus: Int,
	saveDc: Int,
	attackBonus: Int,
	slotTable: SpellSlotTable?
): String = buildString {
	appendLine("Spellcasting Reference")
	appendLine("Spellcaster: ${spellcasterType.toDisplayLabel()}")
	appendLine(SpellcastingReference.getSpellcasterDescription(spellcasterType))
	appendLine()
	appendLine("Ability Mod: ${formatSignedNumber(abilityModifier)}")
	appendLine("Proficiency Bonus: ${formatSignedNumber(proficiencyBonus)}")
	appendLine("Misc Bonus: ${formatSignedNumber(bonus)}")
	appendLine("Spell Save DC: $saveDc")
	appendLine("Spell Attack Bonus: ${formatSignedNumber(attackBonus)}")
	appendLine()
	if (slotTable != null) {
		appendLine("Spell Slots at Level ${slotTable.characterLevel}")
		appendLine("Cantrips: ${slotTable.cantrips}")
		listOf(
			slotTable.slot1,
			slotTable.slot2,
			slotTable.slot3,
			slotTable.slot4,
			slotTable.slot5,
			slotTable.slot6,
			slotTable.slot7,
			slotTable.slot8,
			slotTable.slot9
		)
			.mapIndexedNotNull { index, value ->
				if (value > 0) "${SpellcastingReference.getSpellLevelName(index + 1)} ×$value" else null
			}
			.forEach { appendLine(it) }
	}
}.trim()
