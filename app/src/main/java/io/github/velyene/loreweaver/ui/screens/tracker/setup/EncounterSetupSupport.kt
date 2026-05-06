/*
 * FILE: EncounterSetupSupport.kt
 *
 * TABLE OF CONTENTS:
 * 1. Function: EncounterDifficultyCard
 * 2. Function: difficultyContainerColor
 * 3. Function: difficultyTextColor
 * 4. Function: EncounterNotesSection
 * 5. Function: AddEnemyDialog
 */

package io.github.velyene.loreweaver.ui.screens.tracker.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R

@Composable
internal fun LocationTerrainSection(
	locationTerrain: String,
	onLocationTerrainChange: (String) -> Unit,
) {
	val locationTerrainLabel = stringResource(R.string.encounter_location_terrain_label)
	Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
		OutlinedTextField(
			value = locationTerrain,
			onValueChange = onLocationTerrainChange,
			modifier = Modifier
				.fillMaxWidth()
				.testTag(ENCOUNTER_SETUP_LOCATION_FIELD_TAG)
				.semantics { contentDescription = locationTerrainLabel }
			,
			label = { Text(locationTerrainLabel) },
			placeholder = { Text(stringResource(R.string.encounter_location_terrain_placeholder)) },
		)
		Text(
			text = stringResource(R.string.encounter_location_terrain_supporting_text),
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
	}
}

