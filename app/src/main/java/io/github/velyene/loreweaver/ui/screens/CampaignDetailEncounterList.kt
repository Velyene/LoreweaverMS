/*
 * FILE: CampaignDetailEncounterList.kt
 *
 * TABLE OF CONTENTS:
 * 1. Function: EncounterListBody
 * 2. Value: listState
 * 3. Function: EncountersEmptyState
 * 4. Function: EncounterListItem
 */

package io.github.velyene.loreweaver.ui.screens

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterStatus
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal
import io.github.velyene.loreweaver.ui.theme.MutedText

internal fun encounterListItemTag(encounterId: String): String = "encounter_list_item_$encounterId"

@Composable
internal fun EncounterListBody(
	encounters: List<Encounter>,
	onEncounterClick: (String) -> Unit,
	onEditEncounter: (Encounter) -> Unit,
	onDeleteEncounter: (Encounter) -> Unit,
	modifier: Modifier = Modifier
) {
	if (encounters.isEmpty()) {
		EncountersEmptyState()
	} else {
		val listState = rememberLazyListState()

		LazyColumn(
			state = listState,
			modifier = modifier.visibleVerticalScrollbar(listState)
		) {
			items(encounters, key = { it.id }) { encounter ->
				EncounterListItem(
					encounter = encounter,
					onEncounterClick = onEncounterClick,
					onEditEncounter = onEditEncounter,
					onDeleteEncounter = onDeleteEncounter,
				)
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
	onEncounterClick: (String) -> Unit,
	onEditEncounter: (Encounter) -> Unit,
	onDeleteEncounter: (Encounter) -> Unit,
) {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 4.dp)
			.background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
			.padding(12.dp)
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
			modifier = Modifier.fillMaxWidth()
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.weight(1f)
					.testTag(encounterListItemTag(encounter.id))
					.clickable(
						role = Role.Button,
						onClickLabel = stringResource(R.string.open_encounter_action, encounter.name),
						onClick = { onEncounterClick(encounter.id) }
					)
			) {
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
						text = encounterStatusLabel(encounter.status),
						color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
						fontSize = 11.sp
					)
				}
			}
			Row(verticalAlignment = Alignment.CenterVertically) {
				Badge(containerColor = MaterialTheme.colorScheme.surface) {
					Text(
						text = stringResource(R.string.encounter_open_badge),
						color = ArcaneTeal,
						fontSize = 11.sp
					)
				}
				IconButton(onClick = { onEditEncounter(encounter) }) {
					Icon(
						imageVector = Icons.Default.Edit,
						contentDescription = stringResource(R.string.edit_encounter, encounter.name)
					)
				}
				IconButton(onClick = { onDeleteEncounter(encounter) }) {
					Icon(
						imageVector = Icons.Default.Delete,
						contentDescription = stringResource(R.string.delete_encounter, encounter.name)
					)
				}
			}
		}
	}
}

@Composable
private fun encounterStatusLabel(status: EncounterStatus): String {
	return when (status) {
		EncounterStatus.PENDING -> stringResource(R.string.encounter_status_pending)
		EncounterStatus.ACTIVE -> stringResource(R.string.encounter_status_active)
	}
}

