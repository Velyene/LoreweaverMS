/*
 * FILE: CharacterDetailAttributesAndSkills.kt
 *
 * TABLE OF CONTENTS:
 * 1. Stats tab content entry point
 * 2. Attribute grid and cards
 * 3. Skill chips
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import kotlin.random.Random

@Composable
internal fun CharacterDetailAttributesAndSkills(
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

