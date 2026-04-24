package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal
import io.github.velyene.loreweaver.ui.theme.PanelSurface

@Composable
internal fun EncounterListHeader(onAddEncounterClick: () -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(bottom = 8.dp),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		SectionHeader(stringResource(R.string.encounters_section_title))
		Button(
			onClick = onAddEncounterClick,
			colors = ButtonDefaults.buttonColors(containerColor = PanelSurface),
			border = BorderStroke(1.dp, ArcaneTeal)
		) {
			Icon(
				imageVector = Icons.Default.Add,
				contentDescription = null,
				tint = ArcaneTeal,
				modifier = Modifier.size(16.dp)
			)
			Spacer(modifier = Modifier.width(8.dp))
			Text(
				text = stringResource(R.string.add_encounter_button),
				color = ArcaneTeal,
				fontSize = 12.sp
			)
		}
	}
}
