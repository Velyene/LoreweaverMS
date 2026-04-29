/*
 * FILE: CharacterDetailCombatTab.kt
 *
 * TABLE OF CONTENTS:
 * 1. Combat tab entry point
 * 2. Hit dice and resting section
 * 3. Spell slot section
 * 4. Actions section and action cards
 * 5. Dice tray section
 * 6. Dice parsing helpers
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterAction
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.util.CharacterParty
import io.github.velyene.loreweaver.ui.viewmodels.CharacterViewModel
import kotlin.random.Random

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CombatTab(
	character: CharacterEntry,
	viewModel: CharacterViewModel,
	onUpdateStat: (Int, String) -> Unit,
	haptic: HapticFeedback,
	situationalBonus: String,
	onRollResult: (Pair<String, Int>?) -> Unit
) {
	CharacterDetailQuickStatsBar(character, viewModel)
	Spacer(modifier = Modifier.height(16.dp))

	StatDisplayRow(
		label = stringResource(R.string.hp_label),
		value = character.hp,
		max = character.maxHp,
		onUpdate = {
			haptic.performHapticFeedback(HapticFeedbackType.LongPress)
			onUpdateStat(it, STAT_TYPE_HP)
		}
	)

	if (character.tempHp > 0 || character.hp > 0) {
		StatDisplayRow(
			label = stringResource(R.string.temp_hp_label),
			value = character.tempHp,
			max = character.maxHp,
			onUpdate = { onUpdateStat(it, STAT_TYPE_TEMP_HP) }
		)
	}

	if (character.hp == 0 && character.party == CharacterParty.ADVENTURERS) {
		DeathSaveRow(
			successes = character.deathSaveSuccesses,
			failures = character.deathSaveFailures,
			onUpdate = { success, delta ->
				haptic.performHapticFeedback(HapticFeedbackType.LongPress)
				val updated = if (success) {
					character.copy(
						deathSaveSuccesses = (character.deathSaveSuccesses + delta).coerceIn(0, 3)
					)
				} else {
					character.copy(
						deathSaveFailures = (character.deathSaveFailures + delta).coerceIn(0, 3)
					)
				}
				viewModel.updateCharacter(updated)
			}
		)
	}

	Spacer(modifier = Modifier.height(16.dp))
	HitDiceAndRestingSection(character, viewModel, onRollResult)
	Spacer(modifier = Modifier.height(16.dp))
	SpellSlotsSection(character, viewModel)
	ActionsSection(character, viewModel, situationalBonus, onRollResult)
	Spacer(modifier = Modifier.height(16.dp))
	DiceTraySection(situationalBonus, onRollResult)
}

@Composable
private fun HitDiceAndRestingSection(
	character: CharacterEntry,
	viewModel: CharacterViewModel,
	onRollResult: (Pair<String, Int>?) -> Unit
) {
	val hitDieHealLabel = stringResource(R.string.hit_die_heal_label)
	Text(
		stringResource(R.string.survival_resting_label),
		style = MaterialTheme.typography.titleMedium
	)
	Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
		Text(
			stringResource(
				R.string.hit_dice_label,
				character.hitDieType,
				character.hitDiceCurrent,
				character.hitDiceMax
			),
			modifier = Modifier.weight(1f)
		)
		TextButton(
			onClick = {
				if (character.hitDiceCurrent > 0) {
					val roll = Random.nextInt(1, character.hitDieType + 1)
					val conMod = character.getModifier(character.constitution)
					val healing = (roll + conMod).coerceAtLeast(1)
					viewModel.updateCharacter(
						character.copy(
							hp = (character.hp + healing).coerceAtMost(character.maxHp),
							hitDiceCurrent = character.hitDiceCurrent - 1
						)
					)
					onRollResult(hitDieHealLabel to healing)
				}
			},
			enabled = character.hitDiceCurrent > 0
		) {
			Text(stringResource(R.string.roll_hd_button))
		}
	}
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Button(
			onClick = { viewModel.updateCharacter(character.shortRest()) },
			modifier = Modifier.weight(1f),
			colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
		) {
			Text(stringResource(R.string.short_rest_button))
		}
		Button(
			onClick = { viewModel.updateCharacter(character.longRest()) },
			modifier = Modifier.weight(1f),
			colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
		) {
			Text(stringResource(R.string.long_rest_button))
		}
	}
}

@Composable
private fun SpellSlotsSection(character: CharacterEntry, viewModel: CharacterViewModel) {
	if (character.spellSlots.isEmpty()) return
	Text(stringResource(R.string.spell_slots_label), style = MaterialTheme.typography.titleMedium)
	character.spellSlots.toSortedMap().forEach { (level, slots) ->
		SpellSlotRow(level, slots, character, viewModel)
	}
	Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun SpellSlotRow(
	level: Int,
	slots: Pair<Int, Int>,
	character: CharacterEntry,
	viewModel: CharacterViewModel
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier.padding(vertical = 4.dp)
	) {
		Text(
			stringResource(R.string.spell_slot_level, level, slots.first, slots.second),
			modifier = Modifier.weight(1f)
		)
		IconButton(onClick = {
			val updated = character.spellSlots.toMutableMap()
			updated[level] = (slots.first - 1).coerceAtLeast(0) to slots.second
			viewModel.updateCharacter(character.copy(spellSlots = updated))
		}) {
			Icon(
				Icons.Default.Remove,
				contentDescription = stringResource(R.string.use_spell_slot, level)
			)
		}
		IconButton(onClick = {
			val updated = character.spellSlots.toMutableMap()
			updated[level] = (slots.first + 1).coerceAtMost(slots.second) to slots.second
			viewModel.updateCharacter(character.copy(spellSlots = updated))
		}) {
			Icon(
				Icons.Default.Add,
				contentDescription = stringResource(R.string.recover_spell_slot, level)
			)
		}
	}
}

@Composable
private fun ActionsSection(
	character: CharacterEntry,
	viewModel: CharacterViewModel,
	situationalBonus: String,
	onRollResult: (Pair<String, Int>?) -> Unit
) {
	Text(
		stringResource(R.string.actions_attacks_label),
		style = MaterialTheme.typography.titleMedium
	)
	character.actions.forEach { action ->
		ActionCard(action, character, viewModel, situationalBonus, onRollResult)
	}
}

@Composable
private fun ActionCard(
	action: CharacterAction,
	character: CharacterEntry,
	viewModel: CharacterViewModel,
	situationalBonus: String,
	onRollResult: (Pair<String, Int>?) -> Unit
) {
	val bonus = situationalBonus.toIntOrNull() ?: 0
	val attackRollLabel = stringResource(R.string.action_attack_roll_label, action.name)
	val damageRollLabel = stringResource(R.string.action_damage_roll_label, action.name)
	val attackLogType = stringResource(R.string.attack_log_type)
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 4.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
			Column(modifier = Modifier.weight(1f)) {
				Text(action.name, fontWeight = FontWeight.Bold)
				if (action.damageDice.isNotBlank()) {
					Text(
						stringResource(R.string.dmg_label, action.damageDice),
						style = MaterialTheme.typography.bodySmall
					)
				}
			}
			Button(onClick = {
				val total = Random.nextInt(1, 21) + action.attackBonus + bonus
				onRollResult(attackRollLabel to total)
				viewModel.logAction(
					"${character.name} attacked with ${action.name}: $total",
					attackLogType
				)
			}) {
				val sign = if (action.attackBonus >= 0) "+" else ""
				Text(stringResource(R.string.atk_button, sign, action.attackBonus))
			}
			if (action.damageDice.isNotBlank()) {
				Spacer(modifier = Modifier.width(8.dp))
				OutlinedButton(onClick = {
					onRollResult(damageRollLabel to parseAndRoll(action.damageDice))
				}) { Text(stringResource(R.string.dmg_button)) }
			}
		}
	}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DiceTraySection(
	situationalBonus: String,
	onRollResult: (Pair<String, Int>?) -> Unit
) {
	Text(stringResource(R.string.dice_tray_label), style = MaterialTheme.typography.titleMedium)
	FlowRow(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		listOf(4, 6, 8, 10, 12, 20, 100).forEach { sides ->
			val diceLabel = stringResource(R.string.dice_button, sides)
			ElevatedButton(onClick = {
				val bonus = situationalBonus.toIntOrNull() ?: 0
				onRollResult(diceLabel to (Random.nextInt(1, sides + 1) + bonus))
			}) { Text(diceLabel) }
		}
	}
}

private fun rollDicePool(num: Int, sides: Int): Int =
	(1..num).sumOf { Random.nextInt(1, sides + 1) }

private fun rollWithAdvDis(num: Int, sides: Int, advantage: Boolean): Int {
	val a = rollDicePool(num, sides)
	val b = rollDicePool(num, sides)
	return if (advantage) maxOf(a, b) else minOf(a, b)
}

private fun evaluateDicePart(
	part: String,
	advantage: Boolean,
	disadvantage: Boolean
): Int {
	val sign = if (part.startsWith("-")) -1 else 1
	val cleanPart = part.removePrefix("+").removePrefix("-")
	return if (cleanPart.contains("d", ignoreCase = true)) {
		val (n, s) = cleanPart.lowercase().split("d", limit = 2)
		val num = n.toIntOrNull() ?: 1
		val sides = s.toIntOrNull() ?: 0
		if (sides <= 0) 0
		else {
			val rolled = if (advantage || disadvantage) rollWithAdvDis(num, sides, advantage)
			else rollDicePool(num, sides)
			sign * rolled
		}
	} else sign * (cleanPart.toIntOrNull() ?: 0)
}

internal fun parseAndRoll(diceExpr: String): Int = try {
	val cleanExpr = diceExpr.trim().lowercase()
	val advantage = cleanExpr.startsWith("adv")
	val disadvantage = cleanExpr.startsWith("dis")
	val expr = cleanExpr.removePrefix("adv").removePrefix("dis").replace(" ", "")
	Regex("(?=[+-])", RegexOption.MULTILINE)
		.split(expr.replace("-", "+-"))
		.filter { it.isNotBlank() }
		.sumOf { part ->
			val cleanPart = part.removePrefix("+").removePrefix("-")
			if (cleanPart.isBlank()) 0 else evaluateDicePart(part, advantage, disadvantage)
		}
} catch (_: Exception) {
	0
}

