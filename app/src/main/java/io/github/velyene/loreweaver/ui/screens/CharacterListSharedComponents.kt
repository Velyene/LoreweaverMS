/*
 * FILE: CharacterListSharedComponents.kt
 *
 * TABLE OF CONTENTS:
 * 1. Accessibility text helpers
 * 2. Initiative and dying-state UI components
 * 3. Character count and section-header composables
 */

package io.github.velyene.loreweaver.ui.screens

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.util.CharacterParty

internal fun buildDyingAccessibilitySuffix(
	isDying: Boolean,
	dyingAccessibilityLabel: String
): String {
	return if (isDying) ". $dyingAccessibilityLabel" else ""
}

@Composable
internal fun InitiativeCircle(initiative: Int, isActiveTurn: Boolean, highlight: Boolean) {
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
internal fun InitiativeHeadline(name: String, isActiveTurn: Boolean) {
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
internal fun InitiativeSupportingContent(
	character: CharacterEntry,
	haptic: HapticFeedback,
	lastRoll: Int?,
	onUpdateHP: (Int) -> Unit,
	onRoll: () -> Unit
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
internal fun InitiativeTrailingContent(character: CharacterEntry) {
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
internal fun D20RollButton(lastRoll: Int?, onRoll: () -> Unit, small: Boolean = false) {
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

