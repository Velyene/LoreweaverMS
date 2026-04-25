package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal
import io.github.velyene.loreweaver.ui.theme.MutedText
import io.github.velyene.loreweaver.ui.theme.PanelSurface

@Composable
internal fun NotesQuickActions(onAddNoteClick: () -> Unit) {
	SectionHeader(stringResource(R.string.notes_quick_actions_title))
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 8.dp),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Button(
			onClick = onAddNoteClick,
			colors = ButtonDefaults.buttonColors(containerColor = PanelSurface),
			border = BorderStroke(1.dp, ArcaneTeal)
		) {
			Icon(
				imageVector = Icons.Default.Add,
				contentDescription = null,
				tint = ArcaneTeal,
				modifier = Modifier.size(16.dp)
			)
			Spacer(modifier = Modifier.size(8.dp))
			Text(
				text = stringResource(R.string.new_note_button),
				color = ArcaneTeal,
				fontSize = 12.sp
			)
		}
	}
}

@Composable
internal fun NotesEmptyState() {
	Text(
		text = stringResource(R.string.campaign_notes_empty_message),
		color = MutedText,
		fontSize = 14.sp
	)
}
