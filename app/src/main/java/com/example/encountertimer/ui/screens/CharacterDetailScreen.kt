/*
 * FILE: CharacterDetailScreen.kt
 *
 * TABLE OF CONTENTS:
 * 1. Main Screen (CharacterDetailScreen)
 *    a. State & Storage Initialization
 *    b. Update Handlers (HP, Mana, Stamina)
 *    c. Tab Navigation (Combat, Stats, Journal)
 * 2. Tab Views
 *    a. CombatTab
 *    b. StatsTab
 *    c. JournalTab
 * 3. Combat Sub-sections
 *    a. QuickStatsBar
 *    b. HitDiceAndRestingSection
 *    c. SpellSlotsSection
 *    d. ActionsSection
 *    e. DiceTraySection
 * 4. Stats Sub-sections
 *    a. AttributeGrid
 *    b. SkillChips
 * 5. Pure Logic Helpers
 *    a. applyStatDelta
 *    b. rollDicePool / rollWithAdvDis / evaluateDicePart / parseAndRoll
 * 6. UI Components (StatDisplayRow, DeathSaveRow)
 */

package com.example.encountertimer.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.encountertimer.R
import com.example.encountertimer.domain.model.CharacterEntry
import com.example.encountertimer.domain.util.CharacterParty
import com.example.encountertimer.ui.theme.AntiqueGold
import com.example.encountertimer.ui.theme.ArcaneTeal
import com.example.encountertimer.ui.theme.MutedText
import com.example.encountertimer.ui.viewmodels.CharacterViewModel
import kotlin.random.Random

// -----------------------------------------------------------------------------
// -----------------------------------------------------------------------------
// -----------------------------------------------------------------------------

private fun applyStatDelta(
	character: CharacterEntry,
	delta: Int,
	statType: String
): CharacterEntry =
	when (statType) {
		"HP" -> if (delta < 0) character.applyDamage(-delta) else character.applyHealing(delta)
		"Mana" -> character.copy(mana = (character.mana + delta).coerceIn(0, character.maxMana))
		"Stamina" -> character.copy(
			stamina = (character.stamina + delta).coerceIn(
				0,
				character.maxStamina
			)
		)

		"TempHP" -> character.copy(tempHp = (character.tempHp + delta).coerceAtLeast(0))
		else -> character
	}

// -----------------------------------------------------------------------------
// -----------------------------------------------------------------------------
// -----------------------------------------------------------------------------

private fun rollDicePool(num: Int, sides: Int): Int =
	(1..num).sumOf { Random.nextInt(1, sides + 1) }

private fun rollWithAdvDis(num: Int, sides: Int, advantage: Boolean): Int {
	val roll1 = rollDicePool(num, sides)
	val roll2 = rollDicePool(num, sides)
	return if (advantage) maxOf(roll1, roll2) else minOf(roll1, roll2)
}

private fun evaluateDicePart(
	cleanPart: String,
	isFirstPart: Boolean,
	isAdv: Boolean,
	isDis: Boolean
): Int {
	if (!cleanPart.contains("d")) return cleanPart.toIntOrNull() ?: 0
	val (numStr, sidesStr) = cleanPart.split("d", limit = 2)
	val num = numStr.ifEmpty { "1" }.toIntOrNull() ?: 1
	val sides = sidesStr.toIntOrNull() ?: 6
	return if (isFirstPart && (isAdv || isDis)) {
		rollWithAdvDis(num, sides, isAdv)
	} else {
		rollDicePool(num, sides)
	}
}

fun parseAndRoll(diceExpr: String): Int = try {
	val cleanExpr = diceExpr.lowercase().trim()
	val isAdv = cleanExpr.startsWith("adv")
	val isDis = cleanExpr.startsWith("dis")
	val expr = cleanExpr.removePrefix("adv").removePrefix("dis").replace(" ", "")

	expr.split("(?=[+-])".toRegex())
		.mapIndexed { index, part ->
			val negative = part.startsWith("-")
			val cleanPart = part.removePrefix("+").removePrefix("-")
			val value = evaluateDicePart(cleanPart, index == 0, isAdv, isDis)
			if (negative) -value else value
		}
		.sum()
} catch (_: Exception) {
	0
}

// -----------------------------------------------------------------------------
// 1. Main Screen
// -----------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CharacterDetailScreen(
	characterId: String?,
	onEdit: (String) -> Unit,
	onLookupCondition: (String) -> Unit,
	onDelete: () -> Unit,
	onBack: () -> Unit,
	viewModel: CharacterViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	LaunchedEffect(characterId) {
		characterId?.let { viewModel.selectCharacter(it) }
	}

	val character = uiState.selectedCharacter
	var selectedTab by rememberSaveable { mutableIntStateOf(0) }
	var pendingDeleteCharacter by remember { mutableStateOf<CharacterEntry?>(null) }
	val tabs = listOf(
		stringResource(R.string.tab_combat),
		stringResource(R.string.tab_stats),
		stringResource(R.string.tab_journal)
	)
	// Extracted: stat mutation + optional HP log in one clean lambda
	val onUpdateStat: (Int, String) -> Unit = { delta, statType ->
		character?.let { current ->
			viewModel.updateCharacter(applyStatDelta(current, delta, statType))
			if (statType == "HP") {
				val msg = if (delta < 0) "takes ${-delta} damage" else "heals for $delta HP"
				viewModel.logAction("${current.name} $msg", statType)
			}
		}
	}

	val haptic = LocalHapticFeedback.current
	var rollResult by remember { mutableStateOf<Pair<String, Int>?>(null) }
	var situationalBonus by rememberSaveable { mutableStateOf("0") }

	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Text(character?.name ?: stringResource(R.string.character_details_title))
				},
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = stringResource(R.string.back_to_characters)
						)
					}
				},
				actions = {
					CharacterDetailActions(
						character = character,
						onEdit = onEdit,
						onDeleteRequest = { pendingDeleteCharacter = it }
					)
				}
			)
		}
	) { padding ->
		CharacterDetailContent(
			character = character,
			state = CharacterDetailState(
				selectedTab = selectedTab,
				onTabSelected = { selectedTab = it },
				tabs = tabs,
				onUpdateStat = onUpdateStat,
				haptic = haptic,
				situationalBonus = situationalBonus,
				onBonusChange = { situationalBonus = it },
				rollResult = rollResult,
				onRollResult = { rollResult = it }
			),
			viewModel = viewModel,
			onLookupCondition = onLookupCondition,
			padding = padding
		)
	}

	pendingDeleteCharacter?.let { characterToDelete ->
		ConfirmationDialog(
			title = stringResource(R.string.confirm_delete_character_title),
			message = stringResource(
				R.string.confirm_delete_character_message,
				characterToDelete.name
			),
			confirmLabel = stringResource(R.string.delete_button),
			onConfirm = {
				viewModel.deleteCharacter(characterToDelete)
				onDelete()
				@Suppress("UNUSED_VALUE")
				pendingDeleteCharacter = null
			},
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				pendingDeleteCharacter = null
			}
		)
	}
}

// -----------------------------------------------------------------------------
// State bundle for CharacterDetailContent
// -----------------------------------------------------------------------------

private data class CharacterDetailState(
	val selectedTab: Int,
	val onTabSelected: (Int) -> Unit,
	val tabs: List<String>,
	val onUpdateStat: (Int, String) -> Unit,
	val haptic: HapticFeedback,
	val situationalBonus: String,
	val onBonusChange: (String) -> Unit,
	val rollResult: Pair<String, Int>?,
	val onRollResult: (Pair<String, Int>?) -> Unit
)

@Composable
private fun CharacterDetailActions(
	character: CharacterEntry?,
	onEdit: (String) -> Unit,
	onDeleteRequest: (CharacterEntry) -> Unit
) {
	if (character != null) {
		IconButton(onClick = { onEdit(character.id) }) {
			Icon(
				Icons.Default.Edit,
				contentDescription = stringResource(R.string.edit_character, character.name)
			)
		}
		IconButton(onClick = { onDeleteRequest(character) }) {
			Icon(
				Icons.Default.Delete,
				contentDescription = stringResource(R.string.delete_character, character.name)
			)
		}
	}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CharacterDetailContent(
	character: CharacterEntry?,
	state: CharacterDetailState,
	viewModel: CharacterViewModel,
	onLookupCondition: (String) -> Unit,
	padding: androidx.compose.foundation.layout.PaddingValues
) {
	if (character == null) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding),
			contentAlignment = Alignment.Center
		) {
			Text(stringResource(R.string.character_not_found))
		}
	} else {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
		) {
			PrimaryTabRow(selectedTabIndex = state.selectedTab) {
				state.tabs.forEachIndexed { index, title ->
					Tab(
						selected = state.selectedTab == index,
						onClick = { state.onTabSelected(index) },
						text = { Text(title) }
					)
				}
			}
			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(16.dp)
					.verticalScroll(rememberScrollState())
			) {
				when (state.selectedTab) {
					0 -> CombatTab(
						character,
						viewModel,
						state.onUpdateStat,
						state.haptic,
						state.situationalBonus,
						state.onRollResult
					)

					1 -> StatsTab(
						character,
						state.situationalBonus,
						state.onBonusChange,
						state.onRollResult
					)

					2 -> JournalTab(character, viewModel, onLookupCondition)
				}
				if (state.rollResult != null) {
					RollResultCard(
						result = state.rollResult,
						onClear = { state.onRollResult(null) })
				}
				Spacer(modifier = Modifier.height(80.dp))
			}
		}
	}
}

// -----------------------------------------------------------------------------
// Roll Result Card (extracted from inline if-block)
// -----------------------------------------------------------------------------

@Composable
private fun RollResultCard(result: Pair<String, Int>, onClear: () -> Unit) {
	val label = result.first
	val value = result.second
	val contentDesc = stringResource(R.string.roll_result_desc, label, value)
	Spacer(modifier = Modifier.height(16.dp))
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.semantics {
				liveRegion = LiveRegionMode.Polite
				contentDescription = contentDesc
			},
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(label, style = MaterialTheme.typography.labelLarge, color = MutedText)
			Text(
				"$value",
				style = MaterialTheme.typography.displayMedium,
				fontWeight = FontWeight.Bold,
				color = ArcaneTeal
			)
			Button(onClick = onClear) { Text(stringResource(R.string.clear_button)) }
		}
	}
}

// -----------------------------------------------------------------------------
// -----------------------------------------------------------------------------
// -----------------------------------------------------------------------------

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
	QuickStatsBar(character, viewModel)
	Spacer(modifier = Modifier.height(16.dp))

	StatDisplayRow(
		label = stringResource(R.string.hp_label),
		value = character.hp,
		max = character.maxHp,
		onUpdate = {
			haptic.performHapticFeedback(HapticFeedbackType.LongPress); onUpdateStat(
			it,
			"HP"
		)
		}
	)

	if (character.tempHp > 0 || character.hp > 0) {
		StatDisplayRow(
			label = stringResource(R.string.temp_hp_label),
			value = character.tempHp,
			max = character.maxHp,
			onUpdate = { onUpdateStat(it, "TempHP") }
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
						deathSaveSuccesses = (character.deathSaveSuccesses + delta).coerceIn(
							0,
							3
						)
					)
				} else {
					character.copy(
						deathSaveFailures = (character.deathSaveFailures + delta).coerceIn(
							0,
							3
						)
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

// -----------------------------------------------------------------------------
// 3a. QuickStatsBar
// -----------------------------------------------------------------------------

@Composable
private fun QuickStatsBar(character: CharacterEntry, viewModel: CharacterViewModel) {
	Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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
			valueColor = if (character.effectiveSpeed < character.speed) MaterialTheme.colorScheme.error else Color.Unspecified
		)
// -----------------------------------------------------------------------------
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

// -----------------------------------------------------------------------------
// 3b. HitDiceAndRestingSection
// -----------------------------------------------------------------------------

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

// -----------------------------------------------------------------------------
// 3c. SpellSlotsSection
// -----------------------------------------------------------------------------

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

// -----------------------------------------------------------------------------
// 3d. ActionsSection
// -----------------------------------------------------------------------------

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
	action: com.example.encountertimer.domain.model.CharacterAction,
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

// -----------------------------------------------------------------------------
// 3e. DiceTraySection
// -----------------------------------------------------------------------------

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

// -----------------------------------------------------------------------------
// -----------------------------------------------------------------------------
// -----------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatsTab(
	character: CharacterEntry,
	situationalBonus: String,
	onBonusChange: (String) -> Unit,
	onRollResult: (Pair<String, Int>?) -> Unit
) {
	Row(verticalAlignment = Alignment.CenterVertically) {
		Text(
			stringResource(R.string.situational_bonus_label),
			style = MaterialTheme.typography.titleSmall
		)
		OutlinedTextField(
			value = situationalBonus,
			onValueChange = { input ->
				val valid =
					input.isEmpty() || input == "-" || input.all { c -> c.isDigit() || c == '-' }
				if (valid) onBonusChange(input)
			},
			modifier = Modifier.width(80.dp),
			singleLine = true,
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
		)
	}
	Spacer(modifier = Modifier.height(16.dp))
	AttributeGrid(character, situationalBonus, onRollResult)
	Spacer(modifier = Modifier.height(16.dp))
	Text(stringResource(R.string.skills_label), style = MaterialTheme.typography.titleMedium)
	SkillChips(character, situationalBonus, onRollResult)
}

// -----------------------------------------------------------------------------
// 4a. AttributeGrid
// -----------------------------------------------------------------------------

@Composable
private fun AttributeGrid(
	character: CharacterEntry,
	situationalBonus: String,
	onRollResult: (Pair<String, Int>?) -> Unit
) {
	val attributes = listOf(
		Triple("STR", character.strength, character.getModifier(character.strength)),
		Triple("DEX", character.dexterity, character.getModifier(character.dexterity)),
		Triple("CON", character.constitution, character.getModifier(character.constitution)),
		Triple("INT", character.intelligence, character.getModifier(character.intelligence)),
		Triple("WIS", character.wisdom, character.getModifier(character.wisdom)),
		Triple("CHA", character.charisma, character.getModifier(character.charisma))
	)
	attributes.chunked(3).forEach { rowAttrs ->
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			rowAttrs.forEach { (name, score, mod) ->
				AttributeCard(name, score, mod, situationalBonus, onRollResult, Modifier.weight(1f))
			}
		}
		Spacer(modifier = Modifier.height(8.dp))
	}
}

@Composable
private fun AttributeCard(
	name: String,
	score: Int,
	mod: Int,
	situationalBonus: String,
	onRollResult: (Pair<String, Int>?) -> Unit,
	modifier: Modifier = Modifier
) {
	val label = stringResource(R.string.attribute_check, name)
	Card(
		modifier = modifier.clickable {
			val bonus = situationalBonus.toIntOrNull() ?: 0
			onRollResult(label to (Random.nextInt(1, 21) + mod + bonus))
		},
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
	) {
		Column(
			modifier = Modifier.padding(8.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(name, style = MaterialTheme.typography.labelLarge)
			Text("$score", style = MaterialTheme.typography.titleLarge)
			Text(if (mod >= 0) "+$mod" else "$mod", style = MaterialTheme.typography.bodySmall)
		}
	}
}

// -----------------------------------------------------------------------------
// 4b. SkillChips
// -----------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SkillChips(
	character: CharacterEntry,
	situationalBonus: String,
	onRollResult: (Pair<String, Int>?) -> Unit
) {
	val skills = listOf(
		"Acrobatics" to character.dexterity, "Animal Handling" to character.wisdom,
		"Arcana" to character.intelligence, "Athletics" to character.strength,
		"Deception" to character.charisma, "History" to character.intelligence,
		"Insight" to character.wisdom, "Intimidation" to character.charisma,
		"Investigation" to character.intelligence, "Medicine" to character.wisdom,
		"Nature" to character.intelligence, "Perception" to character.wisdom,
		"Performance" to character.charisma, "Persuasion" to character.charisma,
		"Religion" to character.intelligence, "Sleight of Hand" to character.dexterity,
		"Stealth" to character.dexterity, "Survival" to character.wisdom
	)
	FlowRow(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		skills.forEach { (skill, baseScore) ->
			FilterChip(
				selected = character.proficiencies.contains(skill),
				onClick = {
					val sit = situationalBonus.toIntOrNull() ?: 0
					val bonus = character.getSkillBonus(skill, baseScore)
					onRollResult(skill to (Random.nextInt(1, 21) + bonus + sit))
				},
				label = { Text(skill) }
			)
		}
	}
}

// -----------------------------------------------------------------------------
// 2c. JournalTab
// -----------------------------------------------------------------------------

@Composable
fun JournalTab(
	character: CharacterEntry,
	viewModel: CharacterViewModel,
	onLookupCondition: (String) -> Unit = {}
) {
	Text(stringResource(R.string.inventory_label), style = MaterialTheme.typography.titleMedium)
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 8.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			if (character.inventory.isEmpty()) {
				Text(stringResource(R.string.empty_label))
			} else {
				character.inventory.forEach { item ->
					Text(stringResource(R.string.inventory_bullet, item))
				}
			}
		}
	}

	Spacer(modifier = Modifier.height(16.dp))
	Text(stringResource(R.string.conditions_label), style = MaterialTheme.typography.titleMedium)
	if (character.activeConditions.isEmpty()) {
		Text(stringResource(R.string.none_label), style = MaterialTheme.typography.bodyMedium)
	} else {
		character.activeConditions.forEach { cond ->
			InputChip(
				selected = true,
				onClick = { onLookupCondition(cond) },
				label = { Text(cond) },
				trailingIcon = {
					Icon(
						Icons.Default.Close, null, Modifier
							.size(16.dp)
							.clickable {
								viewModel.updateCharacter(character.copy(activeConditions = character.activeConditions - cond))
							}
					)
				}
			)
		}
	}

	Spacer(modifier = Modifier.height(16.dp))
	Text(stringResource(R.string.notes_label), style = MaterialTheme.typography.titleMedium)
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 8.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Text(
			character.notes.ifBlank { stringResource(R.string.no_notes_label) },
			modifier = Modifier.padding(16.dp)
		)
	}
}

// -----------------------------------------------------------------------------
// 6. UI Components
// -----------------------------------------------------------------------------

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
			strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
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

