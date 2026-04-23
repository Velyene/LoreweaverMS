package com.example.loreweaver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loreweaver.R
import com.example.loreweaver.domain.model.Note
import com.example.loreweaver.ui.theme.PanelSurface

@Composable
internal fun NoteListItem(
	note: Note,
	onEdit: () -> Unit,
	onDelete: () -> Unit
) {
	val (label, detail) = noteMetadata(note)
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 4.dp)
			.background(PanelSurface, RoundedCornerShape(8.dp))
			.padding(12.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		Column(modifier = Modifier.weight(1f)) {
			Text(
				text = label,
				color = MaterialTheme.colorScheme.secondary,
				fontSize = 10.sp,
				fontWeight = FontWeight.Bold
			)
			Text(
				text = note.content,
				color = MaterialTheme.colorScheme.onSurface,
				fontSize = 14.sp
			)
			detail?.let {
				Text(
					text = it,
					color = MaterialTheme.colorScheme.tertiary,
					fontSize = 11.sp,
					modifier = Modifier.padding(top = 4.dp)
				)
			}
		}
		IconButton(onClick = onEdit) {
			Icon(
				imageVector = Icons.Default.Edit,
				contentDescription = stringResource(R.string.edit_note_desc),
				tint = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.size(18.dp)
			)
		}
		IconButton(onClick = onDelete) {
			Icon(
				imageVector = Icons.Default.Delete,
				contentDescription = stringResource(R.string.delete_note_desc),
				tint = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.size(18.dp)
			)
		}
	}
}

@Composable
private fun noteMetadata(note: Note): Pair<String, String?> = when (note) {
	is Note.General -> stringResource(R.string.note_label_general) to null
	is Note.Lore -> {
		stringResource(R.string.note_label_lore) to note.historicalEra
			.takeIf { it.isNotBlank() }
			?.let { stringResource(R.string.note_era_detail, it) }
	}

	is Note.NPC -> stringResource(R.string.note_label_npc) to stringResource(
		R.string.note_npc_detail,
		note.faction,
		note.attitude
	)

	is Note.Location -> {
		stringResource(R.string.note_label_location) to note.region
			.takeIf { it.isNotBlank() }
			?.let { stringResource(R.string.note_region_detail, it) }
	}
}
