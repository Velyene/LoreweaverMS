package com.example.loreweaver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loreweaver.R
import com.example.loreweaver.domain.model.Encounter
import com.example.loreweaver.ui.theme.ArcaneTeal
import com.example.loreweaver.ui.theme.MutedText

@Composable
internal fun EncounterListBody(
	encounters: List<Encounter>,
	onEncounterClick: (String) -> Unit,
	modifier: Modifier = Modifier
) {
	if (encounters.isEmpty()) {
		EncountersEmptyState()
	} else {
		LazyColumn(modifier = modifier) {
			items(encounters, key = { it.id }) { encounter ->
				EncounterListItem(encounter = encounter, onEncounterClick = onEncounterClick)
			}
		}
	}
}

@Composable
private fun EncountersEmptyState() {
	Text(
		text = stringResource(R.string.encounters_empty_message),
		color = MutedText,
		fontSize = 14.sp,
		modifier = Modifier.padding(vertical = 8.dp)
	)
}

@Composable
private fun EncounterListItem(
	encounter: Encounter,
	onEncounterClick: (String) -> Unit
) {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 4.dp)
			.background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
			.clickable(
				role = Role.Button,
				onClickLabel = encounter.name,
				onClick = { onEncounterClick(encounter.id) }
			)
			.padding(12.dp)
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
			modifier = Modifier.fillMaxWidth()
		) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				Icon(
					imageVector = Icons.Default.PlayArrow,
					contentDescription = null,
					tint = ArcaneTeal,
					modifier = Modifier.size(20.dp)
				)
				Spacer(modifier = Modifier.width(12.dp))
				Column {
					Text(
						text = encounter.name,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						fontSize = 14.sp
					)
					Text(
						text = encounter.status.name,
						color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
						fontSize = 11.sp
					)
				}
			}
			Badge(containerColor = MaterialTheme.colorScheme.surface) {
				Text(
					text = stringResource(R.string.encounter_open_badge),
					color = ArcaneTeal,
					fontSize = 11.sp
				)
			}
		}
	}
}
