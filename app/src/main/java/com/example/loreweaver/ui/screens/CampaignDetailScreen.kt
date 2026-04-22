/*
 * FILE: CampaignDetailScreen.kt
 *
 * TABLE OF CONTENTS:
 * 1. Main Screen (CampaignDetailScreen)
 *    a. State & Campaign Loading
 *    b. Header & Tab Navigation (Lore, Sessions, Encounters)
 * 2. Section Views
 *    c. SectionHeader
 *    d. SessionHistoryList
 *    e. LinkedEncounterList
 */

package com.example.loreweaver.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.loreweaver.R
import com.example.loreweaver.domain.model.Encounter
import com.example.loreweaver.domain.model.Note
import com.example.loreweaver.domain.model.SessionRecord
import com.example.loreweaver.ui.theme.AntiqueGold
import com.example.loreweaver.ui.theme.ArcaneTeal
import com.example.loreweaver.ui.theme.MutedText
import com.example.loreweaver.ui.theme.ObsidianBlack
import com.example.loreweaver.ui.theme.PanelSurface
import com.example.loreweaver.ui.util.NOTE_TYPES
import com.example.loreweaver.ui.util.NOTE_TYPE_GENERAL
import com.example.loreweaver.ui.util.NOTE_TYPE_LOCATION
import com.example.loreweaver.ui.util.NOTE_TYPE_LORE
import com.example.loreweaver.ui.util.NOTE_TYPE_NPC
import com.example.loreweaver.ui.util.buildNpcExtra
import com.example.loreweaver.ui.util.parseNpcExtra
import com.example.loreweaver.ui.viewmodels.CampaignViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignDetailScreen(
	campaignId: String,
	onBack: () -> Unit,
	onEncounterClick: (String) -> Unit,
	viewModel: CampaignViewModel = hiltViewModel(),
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val snackbarHostState = remember { SnackbarHostState() }

	LaunchedEffect(campaignId) {
		viewModel.selectCampaign(campaignId)
	}

	LaunchedEffect(uiState.error) {
		uiState.error?.let {
			snackbarHostState.showSnackbar(it)
			viewModel.clearError()
		}
	}

	val campaign = uiState.selectedCampaign
	var selectedTab by rememberSaveable { mutableIntStateOf(0) }

	if ((campaign == null) && uiState.isLoading) {
		CenteredLoadingState(
			modifier = Modifier
				.fillMaxSize()
				.background(ObsidianBlack)
		)
		return
	}

	Scaffold(
		snackbarHost = { SnackbarHost(snackbarHostState) },
		topBar = {
			TopAppBar(
				title = {
					Text(
						campaign?.title ?: stringResource(R.string.campaign_loading_title)
					)
				},
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = stringResource(R.string.back_button)
						)
					}
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.background,
					titleContentColor = MaterialTheme.colorScheme.onBackground,
					navigationIconContentColor = MaterialTheme.colorScheme.onBackground
				)
			)
		}
	) { padding ->
		CampaignDetailContent(
			state = CampaignDetailContentState(
				campaign = campaign,
				selectedTab = selectedTab,
				isLoading = uiState.isLoading,
				notes = uiState.notes,
				sessions = uiState.sessions,
				encounters = uiState.linkedEncounters
			),
			actions = CampaignDetailActions(
				onEncounterClick = onEncounterClick,
				onAddNote = { content, type, extra ->
					viewModel.addNote(
						campaign?.id.orEmpty(),
						content,
						type,
						extra
					)
				},
				onDeleteNote = viewModel::deleteNote,
				onUpdateNote = viewModel::updateNote,
				onAddEncounter = { name -> campaign?.let { viewModel.addEncounter(it.id, name) } },
				onAddEncounterWithMonsters = { name, monsters ->
					campaign?.let { viewModel.addEncounterWithMonsters(it.id, name, monsters) }
				}
			),
			onSelectedTabChange = { selectedTab = it },
			modifier = Modifier
				.padding(padding)
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.background)
		)
	}
}

private data class CampaignDetailContentState(
	val campaign: com.example.loreweaver.domain.model.Campaign?,
	val selectedTab: Int,
	val isLoading: Boolean,
	val notes: List<Note>,
	val sessions: List<SessionRecord>,
	val encounters: List<Encounter>
)

private data class CampaignDetailActions(
	val onEncounterClick: (String) -> Unit,
	val onAddNote: (String, String, String) -> Unit,
	val onDeleteNote: (Note) -> Unit,
	val onUpdateNote: (Note) -> Unit,
	val onAddEncounter: (String) -> Unit,
	val onAddEncounterWithMonsters: (String, List<com.example.loreweaver.domain.model.RemoteItem>) -> Unit
)

@Composable
private fun CampaignDetailContent(
	state: CampaignDetailContentState,
	actions: CampaignDetailActions,
	onSelectedTabChange: (Int) -> Unit,
	modifier: Modifier = Modifier
) {
	Column(modifier = modifier) {
		if (state.isLoading) {
			LinearProgressIndicator(
				modifier = Modifier.fillMaxWidth(),
				color = MaterialTheme.colorScheme.secondary,
				trackColor = MaterialTheme.colorScheme.surfaceVariant
			)
		}

		if (state.campaign == null) {
			if (state.isLoading) {
				CenteredLoadingState()
			} else {
				CampaignNotFoundState()
			}
			return@Column
		}

		CampaignBadgeHeader()
		CampaignDetailTabs(
			selectedTab = state.selectedTab,
			onSelectedTabChange = onSelectedTabChange
		)
		CampaignDetailTabContent(
			state = state,
			actions = actions
		)
	}
}

@Composable
private fun CampaignNotFoundState() {
	CenteredEmptyState(message = stringResource(R.string.campaign_not_found_message))
}

@Composable
private fun CampaignBadgeHeader() {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.padding(16.dp),
		contentAlignment = Alignment.Center
	) {
		Badge(containerColor = MaterialTheme.colorScheme.secondary) {
			Text(
				stringResource(R.string.campaign_badge_label),
				color = MaterialTheme.colorScheme.onSecondary,
				modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
				fontWeight = FontWeight.Bold
			)
		}
	}
}

@Composable
private fun CampaignDetailTabs(
	selectedTab: Int,
	onSelectedTabChange: (Int) -> Unit
) {
	val campaignDetailTabs = listOf(
		stringResource(R.string.campaign_tab_lore_notes),
		stringResource(R.string.campaign_tab_sessions),
		stringResource(R.string.campaign_tab_encounters)
	)

	@Suppress("DEPRECATION")
	TabRow(
		selectedTabIndex = selectedTab,
		containerColor = MaterialTheme.colorScheme.background,
		contentColor = MaterialTheme.colorScheme.secondary,
		indicator = { tabPositions ->
			@Suppress("DEPRECATION")
			TabRowDefaults.SecondaryIndicator(
				Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
				color = MaterialTheme.colorScheme.secondary
			)
		}
	) {
		campaignDetailTabs.forEachIndexed { index, title ->
			Tab(
				selected = selectedTab == index,
				onClick = { onSelectedTabChange(index) },
				text = { Text(title) }
			)
		}
	}
}

@Composable
private fun CampaignDetailTabContent(
	state: CampaignDetailContentState,
	actions: CampaignDetailActions
) {
	when (state.selectedTab) {
		0 -> LoreAndNotesSection(
			notes = state.notes,
			onAddNote = actions.onAddNote,
			onDeleteNote = actions.onDeleteNote,
			onUpdateNote = actions.onUpdateNote
		)

		1 -> SessionHistoryList(state.sessions)
		2 -> LinkedEncounterList(
			encounters = state.encounters,
			onEncounterClick = actions.onEncounterClick,
			onAddEncounter = actions.onAddEncounter,
			onAddEncounterWithMonsters = actions.onAddEncounterWithMonsters
		)
	}
}

private sealed interface NoteDialogMode {
	data object Hidden : NoteDialogMode
	data object Adding : NoteDialogMode
	data class Editing(val note: Note) : NoteDialogMode
}

// S1854/UNUSED_VALUE: MutableState assignments trigger Compose recomposition — not a useless assignment
@Composable
fun LoreAndNotesSection(
	notes: List<Note>,
	onAddNote: (String, String, String) -> Unit,
	onDeleteNote: (Note) -> Unit,
	onUpdateNote: (Note) -> Unit
) {
	var dialogMode by remember { mutableStateOf<NoteDialogMode>(NoteDialogMode.Hidden) }
	var pendingDeleteNote by remember { mutableStateOf<Note?>(null) }

	LazyColumn(modifier = Modifier.padding(16.dp)) {
		item {
			NotesQuickActions(onAddNoteClick = { dialogMode = NoteDialogMode.Adding })
			Spacer(modifier = Modifier.height(16.dp))
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

@Composable
private fun NotesQuickActions(onAddNoteClick: () -> Unit) {
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
			Icon(Icons.Default.Add, null, tint = ArcaneTeal, modifier = Modifier.size(16.dp))
			Spacer(Modifier.width(8.dp))
			Text(stringResource(R.string.new_note_button), color = ArcaneTeal, fontSize = 12.sp)
		}
	}
}

@Composable
private fun NotesEmptyState() {
	Text(stringResource(R.string.campaign_notes_empty_message), color = MutedText, fontSize = 14.sp)
}

@Composable
private fun NoteListItem(
	note: Note,
	onEdit: () -> Unit,
	onDelete: () -> Unit
) {
	val (label, detail) = noteMetadata(note)
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 4.dp)
			.background(PanelSurface, RoundedCornerShape(8.dp))
			.padding(12.dp)
	) {
		Row(verticalAlignment = Alignment.CenterVertically) {
			Column(modifier = Modifier.weight(1f)) {
				Text(
					label,
					color = MaterialTheme.colorScheme.secondary,
					fontSize = 10.sp,
					fontWeight = FontWeight.Bold
				)
				Text(
					note.content,
					color = MaterialTheme.colorScheme.onSurface,
					fontSize = 14.sp
				)
				detail?.let {
					Text(
						it,
						color = MaterialTheme.colorScheme.tertiary,
						fontSize = 11.sp,
						modifier = Modifier.padding(top = 4.dp)
					)
				}
			}
			IconButton(onClick = onEdit) {
				Icon(
					Icons.Default.Edit,
					contentDescription = stringResource(R.string.edit_note_desc),
					tint = MaterialTheme.colorScheme.onSurfaceVariant,
					modifier = Modifier.size(18.dp)
				)
			}
			IconButton(onClick = onDelete) {
				Icon(
					Icons.Default.Delete,
					contentDescription = stringResource(R.string.delete_note_desc),
					tint = MaterialTheme.colorScheme.onSurfaceVariant,
					modifier = Modifier.size(18.dp)
				)
			}
		}
	}
}

@Composable
private fun noteMetadata(note: Note): Pair<String, String?> = when (note) {
	is Note.General -> stringResource(R.string.note_label_general) to null
	is Note.Lore -> stringResource(R.string.note_label_lore) to note.historicalEra
		.takeIf { it.isNotBlank() }
		?.let { stringResource(R.string.note_era_detail, it) }

	is Note.NPC -> stringResource(R.string.note_label_npc) to stringResource(
		R.string.note_npc_detail,
		note.faction,
		note.attitude
	)

	is Note.Location -> stringResource(R.string.note_label_location) to note.region
		.takeIf { it.isNotBlank() }
		?.let { stringResource(R.string.note_region_detail, it) }
}

@Composable
private fun NoteDialogHost(
	dialogMode: NoteDialogMode,
	onAddNote: (String, String, String) -> Unit,
	onUpdateNote: (Note) -> Unit,
	onDismiss: () -> Unit
) {
	when (dialogMode) {
		NoteDialogMode.Hidden -> Unit
		NoteDialogMode.Adding -> NoteEntryDialog(
			editingNote = null,
			onConfirm = onAddNote,
			onDismiss = onDismiss
		)

		is NoteDialogMode.Editing -> NoteEntryDialog(
			editingNote = dialogMode.note,
			onConfirm = { content, type, extra ->
				onUpdateNote(buildUpdatedNote(dialogMode.note, type, content, extra))
			},
			onDismiss = onDismiss
		)
	}
}

@Composable
private fun NoteEntryDialog(
	editingNote: Note?,
	onConfirm: (String, String, String) -> Unit,
	onDismiss: () -> Unit
) {
	var content by remember { mutableStateOf(editingNote?.content ?: "") }
	var selectedType by remember { mutableStateOf(initialNoteType(editingNote)) }
	var extraField by remember { mutableStateOf(initialExtraField(editingNote)) }
	// NPC-specific separate fields (avoids fragile pipe-delimited single string)
	var factionField by remember {
		mutableStateOf((editingNote as? Note.NPC)?.faction ?: "")
	}
	var attitudeField by remember {
		mutableStateOf((editingNote as? Note.NPC)?.attitude ?: "")
	}

	AlertDialog(
		onDismissRequest = onDismiss,
		title = {
			Text(
				if (editingNote != null) stringResource(R.string.edit_note_title)
				else stringResource(R.string.add_note_title)
			)
		},
		text = {
			NoteDialogFields(
				selectedType = selectedType,
				onTypeChange = { selectedType = it },
				content = content,
				onContentChange = { content = it },
				extraField = extraField,
				onExtraChange = { extraField = it },
				factionField = factionField,
				onFactionChange = { factionField = it },
				attitudeField = attitudeField,
				onAttitudeChange = { attitudeField = it }
			)
		},
		confirmButton = {
			Button(
				onClick = {
					handleNoteConfirm(
						content = content,
						selectedType = selectedType,
						factionField = factionField,
						attitudeField = attitudeField,
						extraField = extraField,
						onConfirm = onConfirm,
						onDismiss = onDismiss
					)
				}
			) {
				Text(
					if (editingNote != null) stringResource(R.string.save_button)
					else stringResource(R.string.add_button)
				)
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel_button)) }
		}
	)
}

/**
 * Handles note confirmation logic, reducing cognitive complexity.
 */
private fun handleNoteConfirm(
	content: String,
	selectedType: String,
	factionField: String,
	attitudeField: String,
	extraField: String,
	onConfirm: (String, String, String) -> Unit,
	onDismiss: () -> Unit
) {
	if (content.isNotEmpty()) {
		val extra = if (selectedType == NOTE_TYPE_NPC) {
			buildNpcExtra(factionField, attitudeField)
		} else {
			extraField
		}
		onConfirm(content, selectedType, extra)
		onDismiss()
	}
}

@Composable
private fun NoteDialogFields(
	selectedType: String,
	onTypeChange: (String) -> Unit,
	content: String,
	onContentChange: (String) -> Unit,
	extraField: String,
	onExtraChange: (String) -> Unit,
	factionField: String = "",
	onFactionChange: (String) -> Unit = {},
	attitudeField: String = "",
	onAttitudeChange: (String) -> Unit = {}
) {
	Column {
		Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
			NOTE_TYPES.forEach { type ->
				FilterChip(
					selected = selectedType == type,
					onClick = { onTypeChange(type) },
					label = { Text(localizedNoteType(type), fontSize = 10.sp) }
				)
			}
		}
		OutlinedTextField(
			value = content,
			onValueChange = onContentChange,
			label = { Text(stringResource(R.string.note_content_label)) },
			modifier = Modifier.fillMaxWidth()
		)
		when (selectedType) {
			NOTE_TYPE_NPC -> {
				// Separate fields for NPC — no more fragile "faction|attitude" single field
				OutlinedTextField(
					value = factionField,
					onValueChange = onFactionChange,
					label = { Text(stringResource(R.string.note_faction_label)) },
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 8.dp)
				)
				OutlinedTextField(
					value = attitudeField,
					onValueChange = onAttitudeChange,
					label = { Text(stringResource(R.string.note_attitude_label)) },
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 8.dp)
				)
			}

			NOTE_TYPE_LORE, NOTE_TYPE_LOCATION -> {
				OutlinedTextField(
					value = extraField,
					onValueChange = onExtraChange,
					label = { Text(localizedExtraFieldLabel(selectedType)) },
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 8.dp)
				)
			}
		}
	}
}

private fun initialNoteType(note: Note?) = when (note) {
	is Note.Lore -> NOTE_TYPE_LORE
	is Note.NPC -> NOTE_TYPE_NPC
	is Note.Location -> NOTE_TYPE_LOCATION
	else -> NOTE_TYPE_GENERAL
}

private fun initialExtraField(note: Note?) = when (note) {
	is Note.Lore -> note.historicalEra
	is Note.NPC -> buildNpcExtra(note.faction, note.attitude)
	is Note.Location -> note.region
	else -> ""
}

@Composable
private fun localizedNoteType(type: String): String = when (type) {
	NOTE_TYPE_GENERAL -> stringResource(R.string.note_type_general)
	NOTE_TYPE_LORE -> stringResource(R.string.note_type_lore)
	NOTE_TYPE_NPC -> stringResource(R.string.note_type_npc)
	NOTE_TYPE_LOCATION -> stringResource(R.string.note_type_location)
	else -> type
}

@Composable
private fun localizedExtraFieldLabel(type: String): String = when (type) {
	NOTE_TYPE_LORE -> stringResource(R.string.note_extra_historical_era_label)
	NOTE_TYPE_LOCATION -> stringResource(R.string.note_extra_region_label)
	else -> ""
}

private fun buildUpdatedNote(
	editingNote: Note,
	selectedType: String,
	content: String,
	extraField: String
): Note = when (selectedType) {
	NOTE_TYPE_LORE -> Note.Lore(
		id = editingNote.id, campaignId = editingNote.campaignId,
		content = content, createdAt = editingNote.createdAt,
		historicalEra = extraField
	)

	NOTE_TYPE_NPC -> {
		val (faction, attitude) = parseNpcExtra(extraField)
		Note.NPC(
			id = editingNote.id, campaignId = editingNote.campaignId,
			content = content, createdAt = editingNote.createdAt,
			faction = faction,
			attitude = attitude
		)
	}

	NOTE_TYPE_LOCATION -> Note.Location(
		id = editingNote.id, campaignId = editingNote.campaignId,
		content = content, createdAt = editingNote.createdAt,
		region = extraField
	)

	else -> Note.General(
		id = editingNote.id, campaignId = editingNote.campaignId,
		content = content, createdAt = editingNote.createdAt
	)
}


@Composable
fun SectionHeader(title: String) {
	Text(
		title.uppercase(),
		color = AntiqueGold,
		style = MaterialTheme.typography.labelMedium,
		fontWeight = FontWeight.Bold,
		letterSpacing = 1.5.sp,
		modifier = Modifier.padding(bottom = 8.dp)
	)
}

@Composable
fun SessionHistoryList(sessions: List<SessionRecord>) {
	if (sessions.isEmpty()) {
		CenteredEmptyState(stringResource(R.string.sessions_empty_message))
	} else {
		LazyColumn(modifier = Modifier.padding(16.dp)) {
			items(sessions, key = { it.id }) { session ->
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 4.dp)
						.background(
							MaterialTheme.colorScheme.surfaceVariant,
							RoundedCornerShape(8.dp)
						)
						.padding(12.dp)
				) {
					Row(verticalAlignment = Alignment.CenterVertically) {
						Icon(
							Icons.Default.History,
							null,
							tint = ArcaneTeal,
							modifier = Modifier.size(18.dp)
						)
						Spacer(Modifier.width(12.dp))
						Column {
							Text(
								session.title,
								color = MaterialTheme.colorScheme.onSurfaceVariant,
								fontSize = 14.sp
							)
							Text(
								stringResource(R.string.session_entries_count, session.log.size),
								color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
								fontSize = 11.sp
							)
						}
					}
				}
			}
		}
	}
}

@Composable
fun LinkedEncounterList(
	encounters: List<Encounter>,
	onEncounterClick: (String) -> Unit,
	onAddEncounter: (String) -> Unit,
	onAddEncounterWithMonsters: (String, List<com.example.loreweaver.domain.model.RemoteItem>) -> Unit
) {
	var showAddDialog by remember { mutableStateOf(value = false) }
	var showMonsterSelection by remember { mutableStateOf(value = false) }
	var encounterName by remember { mutableStateOf("") }

	Column(modifier = Modifier.padding(16.dp)) {
		EncounterListHeader(onAddEncounterClick = {
			encounterName = ""
			showAddDialog = true
		})

		if (encounters.isEmpty()) {
			EncountersEmptyState()
		} else {
			LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
				items(encounters, key = { it.id }) { encounter ->
					EncounterListItem(encounter = encounter, onEncounterClick = onEncounterClick)
				}
			}
		}
	}

	EncounterCreationDialogs(
		state = EncounterCreationDialogState(
			showAddDialog = showAddDialog,
			showMonsterSelection = showMonsterSelection,
			encounterName = encounterName
		),
		actions = EncounterCreationDialogActions(
			onEncounterNameChange = {
				@Suppress("UNUSED_VALUE")
				encounterName = it
			},
			onAddEncounter = onAddEncounter,
			onAddEncounterWithMonsters = onAddEncounterWithMonsters,
			onShowAddDialogChange = {
				@Suppress("UNUSED_VALUE")
				showAddDialog = it
			},
			onShowMonsterSelectionChange = {
				@Suppress("UNUSED_VALUE")
				showMonsterSelection = it
			},
			onResetEncounterName = {
				@Suppress("UNUSED_VALUE")
				encounterName = ""
			}
		)
	)
}

private data class EncounterCreationDialogState(
	val showAddDialog: Boolean,
	val showMonsterSelection: Boolean,
	val encounterName: String
)

private data class EncounterCreationDialogActions(
	val onEncounterNameChange: (String) -> Unit,
	val onAddEncounter: (String) -> Unit,
	val onAddEncounterWithMonsters: (String, List<com.example.loreweaver.domain.model.RemoteItem>) -> Unit,
	val onShowAddDialogChange: (Boolean) -> Unit,
	val onShowMonsterSelectionChange: (Boolean) -> Unit,
	val onResetEncounterName: () -> Unit
)

@Composable
private fun EncounterListHeader(onAddEncounterClick: () -> Unit) {
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
			Icon(Icons.Default.Add, null, tint = ArcaneTeal, modifier = Modifier.size(16.dp))
			Spacer(Modifier.width(8.dp))
			Text(
				stringResource(R.string.add_encounter_button),
				color = ArcaneTeal,
				fontSize = 12.sp
			)
		}
	}
}

@Composable
private fun EncountersEmptyState() {
	Text(
		stringResource(R.string.encounters_empty_message),
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
			.clickable { onEncounterClick(encounter.id) }
			.padding(12.dp)
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
			modifier = Modifier.fillMaxWidth()
		) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				Icon(
					Icons.Default.PlayArrow,
					null,
					tint = ArcaneTeal,
					modifier = Modifier.size(20.dp)
				)
				Spacer(Modifier.width(12.dp))
				Column {
					Text(
						encounter.name,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						fontSize = 14.sp
					)
					Text(
						encounter.status.name,
						color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
						fontSize = 11.sp
					)
				}
			}
			Badge(containerColor = MaterialTheme.colorScheme.surface) {
				Text(
					stringResource(R.string.encounter_open_badge),
					color = ArcaneTeal,
					fontSize = 11.sp
				)
			}
		}
	}
}

@Composable
private fun EncounterCreationDialogs(
	state: EncounterCreationDialogState,
	actions: EncounterCreationDialogActions
) {
	if (state.showAddDialog) {
		NewEncounterDialog(
			encounterName = state.encounterName,
			onEncounterNameChange = actions.onEncounterNameChange,
			onCreateWithoutMonsters = {
				actions.onAddEncounter(state.encounterName.trim())
				actions.onShowAddDialogChange(false)
				actions.onResetEncounterName()
			},
			onDismiss = { actions.onShowAddDialogChange(false) }
		)
	}
}

@Composable
private fun NewEncounterDialog(
	encounterName: String,
	onEncounterNameChange: (String) -> Unit,
	onCreateWithoutMonsters: () -> Unit,
	onDismiss: () -> Unit
) {
	val canCreateEncounter = encounterName.isNotBlank()
	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text(stringResource(R.string.new_encounter_title)) },
		text = {
			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				OutlinedTextField(
					value = encounterName,
					onValueChange = onEncounterNameChange,
					label = { Text(stringResource(R.string.encounter_name_label)) },
					modifier = Modifier.fillMaxWidth()
				)
				Text(
					text = stringResource(R.string.monster_import_removed_message),
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					fontSize = 12.sp
				)
			}
		},
		confirmButton = {
			Button(onClick = onCreateWithoutMonsters, enabled = canCreateEncounter) {
				Text(stringResource(R.string.create_without_monsters_button))
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel_button)) }
		}
	)
}
