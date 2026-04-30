package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.ui.theme.AntiqueGold
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal
import io.github.velyene.loreweaver.ui.viewmodels.CharacterViewModel

@Composable
internal fun CharacterDetailQuickStatsBar(
	character: CharacterEntry,
	viewModel: CharacterViewModel
) {
	Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
		CharacterQuickStatCell(
			label = stringResource(R.string.ac_label),
			value = "${character.effectiveAc}"
		)
		CharacterQuickStatCell(
			label = stringResource(R.string.initiative_label),
			value = "${if (character.initiative >= 0) "+" else ""}${character.initiative}"
		)
		CharacterQuickStatCell(
			label = stringResource(R.string.passive_perc_label),
			value = "${character.passivePerception}"
		)
		CharacterQuickStatCell(
			label = stringResource(R.string.speed_label),
			value = stringResource(R.string.speed_feet_value, character.effectiveSpeed),
			valueColor = if (character.effectiveSpeed < character.speed) {
				MaterialTheme.colorScheme.error
			} else {
				Color.Unspecified
			}
		)
		CharacterDetailInspirationToggle(
			hasInspiration = character.hasInspiration,
			onToggle = {
				viewModel.updateCharacter(character.copy(hasInspiration = !character.hasInspiration))
			}
		)
	}
}

@Composable
internal fun CharacterDetailInspirationToggle(
	hasInspiration: Boolean,
	onToggle: () -> Unit
) {
	val inspirationStateDescription = stringResource(
		if (hasInspiration) R.string.inspiration_state_on else R.string.inspiration_state_off
	)
	Column(horizontalAlignment = Alignment.CenterHorizontally) {
		Text(
			stringResource(R.string.insp_label),
			fontWeight = FontWeight.Bold,
			color = AntiqueGold,
			style = MaterialTheme.typography.labelSmall
		)
		Icon(
			imageVector = if (hasInspiration) Icons.Default.Star else Icons.Default.StarBorder,
			contentDescription = stringResource(R.string.inspiration_desc),
			tint = if (hasInspiration) ArcaneTeal else MaterialTheme.colorScheme.outline,
			modifier = Modifier
				.size(24.dp)
				.semantics { stateDescription = inspirationStateDescription }
				.clickable(onClick = onToggle)
		)
	}
}

@Composable
private fun CharacterQuickStatCell(
	label: String,
	value: String,
	valueColor: Color = Color.Unspecified
) {
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

