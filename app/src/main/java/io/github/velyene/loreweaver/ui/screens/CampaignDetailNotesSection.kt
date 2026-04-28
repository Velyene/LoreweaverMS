package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.Note

@Composable
internal fun LoreAndNotesSection(
	notes: List<Note>,
	onAddNote: (String, String, String) -> Unit,
	onDeleteNote: (Note) -> Unit,
	onUpdateNote: (Note) -> Unit
) {
	// Dialog state is kept local to the section so add/edit/delete flows stay colocated
	// with the note list rendering instead of leaking modal coordination to the screen.
	var dialogMode by remember { mutableStateOf<NoteDialogMode>(NoteDialogMode.Hidden) }
	var pendingDeleteNote by remember { mutableStateOf<Note?>(null) }
	val listState = rememberLazyListState()

	LazyColumn(
		state = listState,
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp)
			.visibleVerticalScrollbar(listState)
	) {
		item {
			NotesQuickActions(onAddNoteClick = { dialogMode = NoteDialogMode.Adding })
			Spacer(modifier = Modifier.size(16.dp))
		}

		item { SectionHeader(stringResource(R.string.notes_list_header)) }

		if (notes.isEmpty()) {
			item { NotesEmptyState() }
		} else {
			items(notes, key = { it.id }) { note ->
				NoteListItem(
					note = note,
					onEdit = { dialogMode = NoteDialogMode.Editing(note) },
					onDelete = { pendingDeleteNote = note }
				)
			}
		}
	}

	NoteDialogHost(
		dialogMode = dialogMode,
		onAddNote = onAddNote,
		onUpdateNote = onUpdateNote,
		onDismiss = {
			@Suppress("UNUSED_VALUE")
			dialogMode = NoteDialogMode.Hidden
		}
	)

	// Deletion uses a second piece of state so edit dialogs and destructive confirmation
	// remain independent even when a user rapidly switches between note actions.
	pendingDeleteNote?.let {
		ConfirmationDialog(
			title = stringResource(R.string.confirm_delete_note_title),
			message = stringResource(R.string.confirm_delete_note_message),
			onConfirm = {
				onDeleteNote(it)
				@Suppress("UNUSED_VALUE")
				pendingDeleteNote = null
			},
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				pendingDeleteNote = null
			}
		)
	}
}
