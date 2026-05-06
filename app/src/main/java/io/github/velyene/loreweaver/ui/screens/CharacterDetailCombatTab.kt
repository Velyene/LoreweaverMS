/*
 * FILE: CharacterDetailCombatTab.kt
 *
 * TABLE OF CONTENTS:
 * 1. Combat Tab Layout
 * 2. HP and Resource Controls
 * 3. Actions and Spellcasting Sections
 * 4. Combat Formatting Helpers
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterAction
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.util.CharacterParty
import io.github.velyene.loreweaver.ui.theme.AntiqueGold
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal
import io.github.velyene.loreweaver.ui.viewmodels.CharacterViewModel
import kotlin.random.Random

@Composable
fun CombatTab(
	character: CharacterEntry,
	viewModel: CharacterViewModel,
	onUpdateStat: (Int, String) -> Unit,
	haptic: HapticFeedback,
	situationalBonus: String,
	onRollResult: (Pair<String, Int>?) -> Unit
) {
	QuickStatsBar(character, viewModel)
	Spacer(modifier = Modifier.height(16.dp))

	StatDisplayRow(
		label = stringResource(R.string.hp_label),
		value = character.hp,
		max = character.maxHp,
		onUpdate = { delta ->
			haptic.performHapticFeedback(HapticFeedbackType.LongPress)
			onUpdateStat(delta, STAT_TYPE_HP)
		}
	)

	if (character.tempHp > 0 || character.hp > 0) {
		StatDisplayRow(
			label = stringResource(R.string.temp_hp_label),
			value = character.tempHp,
			max = character.maxHp,
			onUpdate = { delta -> onUpdateStat(delta, STAT_TYPE_TEMP_HP) }
		)
	}

	if (character.hp == 0 && character.party == CharacterParty.ADVENTURERS) {
		DeathSaveRow(
			successes = character.deathSaveSuccesses,
			failures = character.deathSaveFailures,
			onUpdate = { success: Boolean, delta: Int ->
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
private fun QuickStatsBar(character: CharacterEntry, viewModel: CharacterViewModel) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		StatCell(label = stringResource(R.string.ac_label), value = "${character.effectiveAc}")
		StatCell(
			label = stringResource(R.string.initiative_label),
			value = "${if (character.initiative >= 0) "+" else ""}${character.initiative}"
		)
		StatCell(
			label = stringResource(R.string.passive_perc_label),
			value = "${character.passivePerception}"
		)
		StatCell(
			label = stringResource(R.string.speed_label),
			value = stringResource(R.string.speed_feet_value, character.effectiveSpeed),
			valueColor = if (character.effectiveSpeed < character.speed) {
				MaterialTheme.colorScheme.error
			} else {
				Color.Unspecified
			}
		)
		Column(horizontalAlignment = Alignment.CenterHorizontally) {
			Text(
				stringResource(R.string.insp_label),
				fontWeight = FontWeight.Bold,
				color = AntiqueGold,
				style = MaterialTheme.typography.labelSmall
			)
			Icon(
				imageVector = if (character.hasInspiration) Icons.Default.Star else Icons.Default.StarBorder,
				contentDescription = stringResource(R.string.inspiration_desc),
				tint = if (character.hasInspiration) ArcaneTeal else MaterialTheme.colorScheme.outline,
				modifier = Modifier
					.size(24.dp)
					.clickable {
						viewModel.updateCharacter(character.copy(hasInspiration = !character.hasInspiration))
					}
			)
		}
	}
}

@Composable
private fun StatCell(label: String, value: String, valueColor: Color = Color.Unspecified) {
	Column(horizontalAlignment = Alignment.CenterHorizontally) {
		Text(
			label,
			fontWeight = FontWeight.Bold,
			color = AntiqueGold,
			style = MaterialTheme.typography.labelSmall
		)
		Text(value, fontSize = 20.sp, color = valueColor)
	}
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
				}) {
					Text(stringResource(R.string.dmg_button))
				}
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
			}) {
				Text(diceLabel)
			}
		}
	}
}

@Composable
fun StatDisplayRow(label: String, value: Int, max: Int, onUpdate: (Int) -> Unit) {
	Column(modifier = Modifier.padding(vertical = 4.dp)) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(16.dp)
		) {
			Text(
				text = stringResource(R.string.stat_value_with_max, label, value, max),
				fontSize = 18.sp,
				modifier = Modifier.weight(1f)
			)
			Row(verticalAlignment = Alignment.CenterVertically) {
				TextButton(onClick = { onUpdate(-5) }) {
					Text("-5", color = MaterialTheme.colorScheme.error)
				}
				IconButton(onClick = { onUpdate(-1) }) {
					Icon(
						Icons.Default.Remove,
						contentDescription = stringResource(R.string.decrease_stat_desc, label),
						tint = MaterialTheme.colorScheme.error
					)
				}
				IconButton(onClick = { onUpdate(1) }) {
					Icon(
						Icons.Default.Add,
						contentDescription = stringResource(R.string.increase_stat_desc, label),
						tint = MaterialTheme.colorScheme.primary
					)
				}
				TextButton(onClick = { onUpdate(5) }) {
					Text("+5", color = MaterialTheme.colorScheme.primary)
				}
			}
		}
		val barColor = when (label) {
			stringResource(R.string.hp_label) -> MaterialTheme.colorScheme.error
			stringResource(R.string.mana_label) -> MaterialTheme.colorScheme.primary
			stringResource(R.string.stamina_label) -> MaterialTheme.colorScheme.secondary
			else -> MaterialTheme.colorScheme.primary
		}
		LinearProgressIndicator(
			progress = { if (max > 0) value.toFloat() / max.toFloat() else 0f },
			modifier = Modifier
				.fillMaxWidth()
				.height(8.dp),
			strokeCap = StrokeCap.Round,
			color = barColor,
			trackColor = barColor.copy(alpha = 0.2f)
		)
	}
}

@Composable
fun DeathSaveRow(successes: Int, failures: Int, onUpdate: (Boolean, Int) -> Unit) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 8.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			Text(
				stringResource(R.string.death_saves_label),
				fontWeight = FontWeight.Bold,
				color = MaterialTheme.colorScheme.onErrorContainer
			)
			Spacer(modifier = Modifier.height(8.dp))
			DeathSaveCheckboxRow(
				label = stringResource(R.string.successes_label),
				count = successes,
				checkedColor = MaterialTheme.colorScheme.primary,
				onUpdate = { checked -> if (checked) onUpdate(true, 1) else onUpdate(true, -1) }
			)
			DeathSaveCheckboxRow(
				label = stringResource(R.string.failures_label),
				count = failures,
				checkedColor = MaterialTheme.colorScheme.error,
				onUpdate = { checked -> if (checked) onUpdate(false, 1) else onUpdate(false, -1) }
			)
		}
	}
}

@Composable
private fun DeathSaveCheckboxRow(
	label: String,
	count: Int,
	checkedColor: Color,
	onUpdate: (Boolean) -> Unit
) {
	Row(verticalAlignment = Alignment.CenterVertically) {
		Text(label, modifier = Modifier.weight(1f))
		repeat(3) { index ->
			Checkbox(
				checked = index < count,
				onCheckedChange = onUpdate,
				colors = CheckboxDefaults.colors(checkedColor = checkedColor)
			)
		}
	}
}

