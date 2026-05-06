/*
 * FILE: CharacterListItems.kt
 *
 * TABLE OF CONTENTS:
 * 1. Function: InitiativeItem
 * 2. Function: buildDyingAccessibilitySuffix
 * 3. Function: InitiativeCircle
 * 4. Function: InitiativeHeadline
 * 5. Function: InitiativeSupportingContent
 * 6. Function: InitiativeTrailingContent
 * 7. Function: D20RollButton
 * 8. Function: CharacterItem
 * 9. Function: CharacterItemSupporting
 * 10. Function: CharacterItemTrailing
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Badge
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.util.CharacterParty

@Composable
internal fun InitiativeItem(
	character: CharacterEntry,
	onClick: () -> Unit,
	onUpdateHP: (Int) -> Unit,
	highlight: Boolean = false,
	isActiveTurn: Boolean = false,
) {
	val haptic = LocalHapticFeedback.current
	var lastRoll by remember { mutableStateOf<Int?>(null) }

	val backgroundColor = when {
		isActiveTurn -> MaterialTheme.colorScheme.primaryContainer
		highlight -> MaterialTheme.colorScheme.errorContainer
		else -> Color.Transparent
	}
	val viewDetailsLabel = stringResource(R.string.view_details, character.name)
	val currentTurnStatus = stringResource(R.string.current_turn)
	val dyingAccessibilityLabel = stringResource(R.string.dying_accessibility_label)
	val initiativeContentDescription = stringResource(
		R.string.initiative_accessibility_desc,
		character.name,
		character.initiative,
		character.hp,
		character.maxHp,
		buildDyingAccessibilitySuffix(character.hp == 0, dyingAccessibilityLabel)
	)

	Surface(
		color = backgroundColor,
		modifier = Modifier
			.clickable(onClickLabel = viewDetailsLabel, role = Role.Button, onClick = onClick)
			.semantics(mergeDescendants = true) {
				stateDescription = if (isActiveTurn) currentTurnStatus else ""
				contentDescription = initiativeContentDescription
			}
	) {
		ListItem(
			leadingContent = { InitiativeCircle(character.initiative, isActiveTurn, highlight) },
			headlineContent = { InitiativeHeadline(character.name, isActiveTurn) },
			supportingContent = {
				InitiativeSupportingContent(
					character = character,
					haptic = haptic,
					lastRoll = lastRoll,
					onUpdateHP = onUpdateHP,
					onRoll = { lastRoll = (1..20).random() }
				)
			},
			trailingContent = { InitiativeTrailingContent(character) },
			colors = ListItemDefaults.colors(containerColor = Color.Transparent)
		)
	}
}

internal fun buildDyingAccessibilitySuffix(
	isDying: Boolean,
	dyingAccessibilityLabel: String,
): String {
	return if (isDying) ". $dyingAccessibilityLabel" else ""
}

@Composable
private fun InitiativeCircle(initiative: Int, isActiveTurn: Boolean, highlight: Boolean) {
	val color = when {
		isActiveTurn -> MaterialTheme.colorScheme.primary
		highlight -> MaterialTheme.colorScheme.error
		else -> MaterialTheme.colorScheme.outline
	}
	Surface(shape = CircleShape, color = color, modifier = Modifier.size(40.dp)) {
		Box(contentAlignment = Alignment.Center) {
			Text(
				text = initiative.toString(),
				color = if (isActiveTurn || highlight) MaterialTheme.colorScheme.onPrimary else Color.Unspecified,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.clearAndSetSemantics { }
			)
		}
	}
}

@Composable
private fun InitiativeHeadline(name: String, isActiveTurn: Boolean) {
	Row(verticalAlignment = Alignment.CenterVertically) {
		Text(
			name,
			fontWeight = if (isActiveTurn) FontWeight.ExtraBold else FontWeight.Bold,
			color = if (isActiveTurn) MaterialTheme.colorScheme.primary else Color.Unspecified,
			modifier = Modifier.semantics { heading() }
		)
		if (isActiveTurn) {
			Badge(
				modifier = Modifier.padding(start = 8.dp),
				containerColor = MaterialTheme.colorScheme.primary
			) { Text(stringResource(R.string.active_badge)) }
		}
	}
}

@Composable
private fun InitiativeSupportingContent(
	character: CharacterEntry,
	haptic: HapticFeedback,
	lastRoll: Int?,
	onUpdateHP: (Int) -> Unit,
	onRoll: () -> Unit,
) {
	Column {
		Text(character.type)
		Row(
			modifier = Modifier.padding(top = 4.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			IconButton(
				onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); onUpdateHP(-1) },
				modifier = Modifier.size(32.dp)
			) {
				Icon(
					Icons.Default.Remove,
					contentDescription = stringResource(R.string.decrease_hp_desc, character.name),
					tint = MaterialTheme.colorScheme.error,
					modifier = Modifier.size(16.dp)
				)
			}
			IconButton(
				onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); onUpdateHP(1) },
				modifier = Modifier.size(32.dp)
			) {
				Icon(
					Icons.Default.Add,
					contentDescription = stringResource(R.string.increase_hp_desc, character.name),
					tint = MaterialTheme.colorScheme.primary,
					modifier = Modifier.size(16.dp)
				)
			}
			Spacer(modifier = Modifier.width(4.dp))
			D20RollButton(lastRoll = lastRoll, onRoll = onRoll, small = true)
		}
	}
}

@Composable
private fun InitiativeTrailingContent(character: CharacterEntry) {
	Column(horizontalAlignment = Alignment.End) {
		Text(
			text = stringResource(R.string.initiative_hp_summary, character.hp, character.maxHp),
			color = if (character.hp < character.maxHp / 4) MaterialTheme.colorScheme.error else Color.Unspecified,
			fontWeight = if (character.hp < character.maxHp / 4) FontWeight.Bold else FontWeight.Normal,
			modifier = Modifier.clearAndSetSemantics { }
		)
		if (character.hp == 0 && character.party == CharacterParty.ADVENTURERS) {
			Text(
				stringResource(R.string.dying_label),
				color = MaterialTheme.colorScheme.error,
				fontWeight = FontWeight.Bold,
				style = MaterialTheme.typography.labelSmall,
				modifier = Modifier.clearAndSetSemantics { }
			)
		}
	}
}

@Composable
private fun D20RollButton(lastRoll: Int?, onRoll: () -> Unit, small: Boolean = false) {
	val label = if (lastRoll == null) stringResource(R.string.d20_roll)
	else stringResource(R.string.d20_roll_result, lastRoll)
	FilledTonalButton(
		onClick = onRoll,
		modifier = Modifier.height(if (small) 28.dp else 32.dp),
		contentPadding = PaddingValues(horizontal = if (small) 4.dp else 8.dp)
	) {
		Text(
			label,
			style = if (small) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodySmall
		)
	}
}

@Composable
internal fun CharacterItem(
	character: CharacterEntry,
	onClick: () -> Unit,
	onUpdateHP: (Int) -> Unit,
	onDelete: () -> Unit,
) {
	val haptic = LocalHapticFeedback.current
	var lastRoll by remember { mutableStateOf<Int?>(null) }

	val editDetailsLabel = stringResource(R.string.edit_details, character.name)
	val deleteActionLabel = stringResource(R.string.delete_character_desc, character.name)
	val dyingAccessibilityLabel = stringResource(R.string.dying_accessibility_label)
	val characterContentDescription = stringResource(
		R.string.character_accessibility_desc,
		character.name,
		character.type,
		character.hp,
		character.maxHp,
		character.ac,
		buildDyingAccessibilitySuffix(character.hp == 0, dyingAccessibilityLabel)
	)

	ListItem(
		headlineContent = {
			Text(
				character.name,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.semantics { heading() }
			)
		},
		supportingContent = {
			CharacterItemSupporting(
				character = character,
				haptic = haptic,
				lastRoll = lastRoll,
				onUpdateHP = onUpdateHP,
				onRoll = { lastRoll = (1..20).random() }
			)
		},
		trailingContent = { CharacterItemTrailing(character) },
		modifier = Modifier
			.clickable(onClickLabel = editDetailsLabel, role = Role.Button) { onClick() }
			.semantics(mergeDescendants = true) {
				contentDescription = characterContentDescription
				customActions = listOf(
					CustomAccessibilityAction(deleteActionLabel) { onDelete(); true }
				)
			}
	)
}

@Composable
private fun CharacterItemSupporting(
	character: CharacterEntry,
	haptic: HapticFeedback,
	lastRoll: Int?,
	onUpdateHP: (Int) -> Unit,
	onRoll: () -> Unit,
) {
	Column {
		Text(
			text = stringResource(
				R.string.character_list_item_summary,
				character.type,
				character.hp,
				character.maxHp,
				character.ac
			),
			modifier = Modifier.clearAndSetSemantics { }
		)
		Row(
			modifier = Modifier.padding(top = 4.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			OutlinedButton(
				onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); onUpdateHP(-5) },
				modifier = Modifier
					.height(32.dp)
					.padding(end = 4.dp),
				contentPadding = PaddingValues(horizontal = 8.dp)
			) {
				Text(
					stringResource(R.string.hp_minus_five),
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.error
				)
			}
			OutlinedButton(
				onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); onUpdateHP(5) },
				modifier = Modifier.height(32.dp),
				contentPadding = PaddingValues(horizontal = 8.dp)
			) {
				Text(
					stringResource(R.string.hp_plus_five),
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.primary
				)
			}
			Spacer(modifier = Modifier.width(8.dp))
			D20RollButton(
				lastRoll = lastRoll,
				onRoll = {
					haptic.performHapticFeedback(HapticFeedbackType.LongPress)
					onRoll()
				}
			)
		}
	}
}

@Composable
private fun CharacterItemTrailing(character: CharacterEntry) {
	Column(horizontalAlignment = Alignment.End) {
		Text(character.notes)
		if (character.hp == 0 && character.party == CharacterParty.ADVENTURERS) {
			Text(
				stringResource(R.string.dying_label),
				color = MaterialTheme.colorScheme.error,
				fontWeight = FontWeight.ExtraBold,
				style = MaterialTheme.typography.labelSmall,
				modifier = Modifier.clearAndSetSemantics { }
			)
		}
	}
}

