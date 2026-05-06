/*
 * FILE: ReferenceScreenSpellcastingCards.kt
 *
 * TABLE OF CONTENTS:
 * 1. Spellcasting Summary Cards
 * 2. Spell Slot and Rule Cards
 * 3. Shared Spellcasting Detail Helpers
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.util.SpellSlotTable
import io.github.velyene.loreweaver.domain.util.SpellcasterType
import io.github.velyene.loreweaver.domain.util.SpellcastingReference

@Composable
internal fun FormulaCard(title: String, rows: List<Pair<String, String>>) {
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
internal fun SpellcastingCalculatorCard(
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
internal fun SpellSlotCalculatorCard(
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
internal fun ResultBadge(
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
internal fun SpellSlotCard(slotTable: SpellSlotTable) {
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
