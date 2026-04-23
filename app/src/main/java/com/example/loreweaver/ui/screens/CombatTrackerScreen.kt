/*
 * FILE: CombatTrackerScreen.kt
 *
 * TABLE OF CONTENTS:
 * 1. Main Tracker Screen (CombatTrackerScreen)
 *    a. Combat Lifecycle Management
 *    b. State Transitions (Setup vs. Live)
 * 2. Setup Mode (EncounterSetupView)
 *    a. Party & Environment Selection
 *    b. Difficulty & Enemy Count Config
 *    c. AddEnemyDialog
 * 3. Live Mode (LiveTrackerView) - Refactored for reduced complexity
 *    a. CombatantHpList - Combatant roster display
 *    b. CombatantListItem - Individual combatant row
 *    c. CurrentCombatantControls - Active combatant controls
 *    d. ActionChipsRow - Combat action buttons
 *    e. QuickHpControls - HP adjustment buttons
 *    f. CombatLogSection - Event log display
 *    g. EncounterActionButtons - Turn/encounter controls
 * 4. UI Components
 *    a. TrackerDisplayBox - Generic display container
 *    b. ActionChip - Individual action button
 */

package com.example.loreweaver.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loreweaver.R
import com.example.loreweaver.domain.model.CharacterEntry
import com.example.loreweaver.domain.model.CombatantState
import com.example.loreweaver.domain.model.Condition
import com.example.loreweaver.domain.util.CharacterParty
import com.example.loreweaver.domain.util.DifficultyRating
import com.example.loreweaver.domain.util.EncounterDifficulty
import com.example.loreweaver.domain.util.EncounterDifficultyResult
import com.example.loreweaver.ui.screens.CombatTrackerConstants.ACTION_BUTTON_HEIGHT_DP
import com.example.loreweaver.ui.screens.CombatTrackerConstants.DEFAULT_ENEMY_HP
import com.example.loreweaver.ui.screens.CombatTrackerConstants.DEFAULT_ENEMY_INITIATIVE
import com.example.loreweaver.ui.screens.CombatTrackerConstants.HP_QUICK_ADJUSTMENTS
import com.example.loreweaver.ui.screens.CombatTrackerConstants.INITIATIVE_ROLL_MAX
import com.example.loreweaver.ui.screens.CombatTrackerConstants.INITIATIVE_ROLL_MIN
import com.example.loreweaver.ui.screens.CombatTrackerConstants.LOG_HEIGHT_DP
import com.example.loreweaver.ui.screens.CombatTrackerConstants.NOTES_HEIGHT_DP
import com.example.loreweaver.ui.screens.CombatTrackerConstants.QUICK_HP_BUTTON_HEIGHT_DP
import com.example.loreweaver.ui.screens.CombatTrackerConstants.SETUP_BUTTON_HEIGHT_DP
import com.example.loreweaver.ui.viewmodels.CombatViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CombatTrackerScreen(
	encounterId: String? = null,
	onBack: () -> Unit,
	onEndEncounter: () -> Unit,
	viewModel: CombatViewModel = hiltViewModel(),
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	val snackbarHostState = remember { SnackbarHostState() }
	val topBarTitle = if (uiState.isCombatActive) {
		stringResource(R.string.combat_tracker_live_title)
	} else {
		stringResource(R.string.combat_tracker_setup_title)
	}
	val retryActionLabel = stringResource(R.string.retry_action)

	LaunchedEffect(encounterId) {
		viewModel.loadEncounter(encounterId)
	}

	LaunchedEffect(uiState.error) {
		uiState.error?.let { errorMsg ->
			val result = snackbarHostState.showSnackbar(
				message = errorMsg,
				actionLabel = if (uiState.onRetry != null) retryActionLabel else null,
				duration = SnackbarDuration.Long
			)
			if (result == SnackbarResult.ActionPerformed) {
				uiState.onRetry?.invoke()
			}
			viewModel.clearError()
		}
	}

	Scaffold(
		snackbarHost = { SnackbarHost(snackbarHostState) },
		topBar = {
			TopAppBar(
				title = { Text(topBarTitle) },
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = stringResource(R.string.back_button)
						)
					}
				}
			)
		}
	) { padding ->
		val trackerContentState = remember(
			uiState.isCombatActive,
			encounterId,
			uiState.encounterNotes,
			uiState.combatants,
			uiState.availableCharacters,
			uiState.encounterDifficulty,
			uiState.currentRound,
			uiState.currentTurnIndex,
			uiState.activeStatuses
		) {
			TrackerContentState(
				isCombatActive = uiState.isCombatActive,
				encounterId = encounterId,
				notes = uiState.encounterNotes,
				combatants = uiState.combatants,
				availableCharacters = uiState.availableCharacters,
				encounterDifficulty = uiState.encounterDifficulty,
				round = uiState.currentRound,
				turnIndex = uiState.currentTurnIndex,
				statuses = uiState.activeStatuses
			)
		}
		val trackerContentCallbacks = remember(viewModel, onEndEncounter) {
			TrackerContentCallbacks().apply {
				onNotesChange = viewModel::updateNotes
				onStartEncounter = { id -> viewModel.startEncounter(id) }
				onAddParty = viewModel::addParty
				onAddEnemy = viewModel::addEnemy
				onRemoveCombatant = viewModel::removeCombatant
				onAction = viewModel::performAction
				onNextTurn = viewModel::nextTurn
				onHpChange = viewModel::updateCombatantHp
				onAddCondition = viewModel::addCondition
				onRemoveCondition = viewModel::removeCondition
				this.onEndEncounter = { viewModel.saveAndEndEncounter(onEndEncounter) }
			}
		}
		Box(
			modifier = Modifier
				.padding(padding)
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.background)
		) {
			TrackerContent(
				state = trackerContentState,
				callbacks = trackerContentCallbacks
			)
		}
	}
}

private data class TrackerContentState(
	val isCombatActive: Boolean,
	val encounterId: String?,
	val notes: String,
	val combatants: List<CombatantState>,
	val availableCharacters: List<CharacterEntry>,
	val encounterDifficulty: EncounterDifficultyResult?,
	val round: Int,
	val turnIndex: Int,
	val statuses: List<String>
)

private class TrackerContentCallbacks {
	lateinit var onNotesChange: (String) -> Unit
	lateinit var onStartEncounter: (String?) -> Unit
	lateinit var onAddParty: (List<CombatantState>) -> Unit
	lateinit var onAddEnemy: (String, Int, Int) -> Unit
	lateinit var onRemoveCombatant: (String) -> Unit
	lateinit var onAction: (String) -> Unit
	lateinit var onNextTurn: () -> Unit
	lateinit var onHpChange: (String, Int) -> Unit
	lateinit var onAddCondition: (String, String, Int?) -> Unit
	lateinit var onRemoveCondition: (String, String) -> Unit
	lateinit var onEndEncounter: () -> Unit
}

@Composable
private fun TrackerContent(
	state: TrackerContentState,
	callbacks: TrackerContentCallbacks
) {
	if (!state.isCombatActive) {
		EncounterSetupView(
			notes = state.notes,
			combatants = state.combatants,
			availablePartyMembers = state.availableCharacters,
			encounterDifficulty = state.encounterDifficulty,
			onNotesChange = callbacks.onNotesChange,
			onStart = { callbacks.onStartEncounter(state.encounterId) },
			onAddParty = { callbacks.onAddParty(buildPartyCombatants(state.availableCharacters)) },
			onAddEnemy = callbacks.onAddEnemy,
			onRemoveCombatant = callbacks.onRemoveCombatant
		)
		return
	}

	LiveTrackerView(
		round = state.round,
		combatants = state.combatants,
		turnIndex = state.turnIndex,
		statuses = state.statuses,
		onAction = callbacks.onAction,
		onNextTurn = callbacks.onNextTurn,
		onHpChange = callbacks.onHpChange,
		onAddCondition = callbacks.onAddCondition,
		onRemoveCondition = callbacks.onRemoveCondition,
		onEnd = callbacks.onEndEncounter
	)
}

private fun buildPartyCombatants(availableCharacters: List<CharacterEntry>): List<CombatantState> {
	return availableCharacters
		.asSequence()
		.filter { it.party == CharacterParty.ADVENTURERS }
		.map { character ->
			CombatantState(
				character.id,
				character.name,
				character.initiative + Random.nextInt(INITIATIVE_ROLL_MIN, INITIATIVE_ROLL_MAX),
				character.hp,
				character.maxHp
			)
		}
		.toList()
}

@Composable
fun EncounterSetupView(
	notes: String,
	combatants: List<CombatantState>,
	availablePartyMembers: List<CharacterEntry>,
	encounterDifficulty: EncounterDifficultyResult?,
	onNotesChange: (String) -> Unit,
	onStart: () -> Unit,
	onAddParty: () -> Unit,
	onAddEnemy: (name: String, hp: Int, initiative: Int) -> Unit,
	onRemoveCombatant: (String) -> Unit
) {
	var showAddEnemyDialog by remember { mutableStateOf(value = false) }
	var combatantPendingRemoval by remember { mutableStateOf<CombatantState?>(null) }
	val partyCount = availablePartyMembers.count { it.party == CharacterParty.ADVENTURERS }

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		TrackerModeBadge(
			label = stringResource(R.string.combat_setup_badge_label),
			containerColor = MaterialTheme.colorScheme.tertiary,
			contentColor = MaterialTheme.colorScheme.onTertiary
		)
		Spacer(modifier = Modifier.height(16.dp))

		SetupQuickAddRow(
			partyCount = partyCount,
			onAddParty = onAddParty,
			onAddEnemy = { showAddEnemyDialog = true }
		)

		Spacer(modifier = Modifier.height(12.dp))

		if ((encounterDifficulty != null) && (encounterDifficulty.partySize > 0)) {
			EncounterDifficultyCard(encounterDifficulty = encounterDifficulty)
		}

		Spacer(modifier = Modifier.height(12.dp))

		SetupRosterSection(
			combatants = combatants,
			onRemoveCombatant = { combatantPendingRemoval = it },
			modifier = Modifier.weight(1f)
		)

		Spacer(modifier = Modifier.height(12.dp))

		EncounterNotesSection(notes = notes, onNotesChange = onNotesChange)

		Spacer(modifier = Modifier.height(16.dp))

		Button(
			onClick = onStart,
			enabled = combatants.isNotEmpty(),
			modifier = Modifier
				.fillMaxWidth()
				.height(SETUP_BUTTON_HEIGHT_DP.dp),
			colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
		) {
			Text(
				stringResource(R.string.start_encounter_button),
				color = MaterialTheme.colorScheme.onPrimary,
				fontWeight = FontWeight.Bold
			)
		}
	}

	if (showAddEnemyDialog) {
		AddEnemyDialog(
			onConfirm = { name, hp, initiative ->
				onAddEnemy(name, hp, initiative)
				@Suppress("UNUSED_VALUE")
				showAddEnemyDialog = false
			},
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				showAddEnemyDialog = false
			}
		)
	}

	combatantPendingRemoval?.let { combatant ->
		ConfirmationDialog(
			title = stringResource(R.string.remove_combatant_confirm_title),
			message = stringResource(R.string.remove_combatant_confirm_message, combatant.name),
			confirmLabel = stringResource(R.string.remove_button),
			onConfirm = {
				onRemoveCombatant(combatant.characterId)
				@Suppress("UNUSED_VALUE")
				combatantPendingRemoval = null
			},
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				combatantPendingRemoval = null
			}
		)
	}
}

@Composable
private fun TrackerModeBadge(
	label: String,
	containerColor: Color,
	contentColor: Color
) {
	Badge(containerColor = containerColor) {
		Text(
			label,
			color = contentColor,
			modifier = Modifier.padding(4.dp)
		)
	}
}

@Composable
private fun SetupQuickAddRow(
	partyCount: Int,
	onAddParty: () -> Unit,
	onAddEnemy: () -> Unit
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Button(
			onClick = onAddParty,
			modifier = Modifier.weight(1f),
			colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
		) {
			Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(16.dp))
			Spacer(modifier = Modifier.size(6.dp))
			Text(
				if (partyCount > 0) {
					stringResource(R.string.add_party_button_with_count, partyCount)
				} else {
					stringResource(R.string.add_party_button)
				},
				fontSize = 12.sp
			)
		}
		Button(
			onClick = onAddEnemy,
			modifier = Modifier.weight(1f),
			colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
		) {
			Icon(
				Icons.Default.PersonAdd,
				contentDescription = null,
				modifier = Modifier.size(16.dp)
			)
			Spacer(modifier = Modifier.size(6.dp))
			Text(stringResource(R.string.add_enemy_button), fontSize = 12.sp)
		}
	}
}

@Composable
private fun EncounterDifficultyCard(encounterDifficulty: EncounterDifficultyResult) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(
			containerColor = difficultyContainerColor(encounterDifficulty.rating)
		)
	) {
		Column(modifier = Modifier.padding(12.dp)) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					stringResource(R.string.encounter_difficulty_label),
					style = MaterialTheme.typography.labelMedium,
					fontWeight = FontWeight.Bold
				)
				Text(
					EncounterDifficulty.formatDifficultyRating(encounterDifficulty.rating).first,
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold,
					color = difficultyTextColor(encounterDifficulty.rating)
				)
			}
			Spacer(modifier = Modifier.height(4.dp))
			Text(
				EncounterDifficulty.getDifficultyDescription(encounterDifficulty.rating),
				style = MaterialTheme.typography.bodySmall,
				fontSize = 11.sp,
				lineHeight = 14.sp
			)
			Spacer(modifier = Modifier.height(8.dp))
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				Column {
					Text(
						stringResource(
							R.string.party_level_label,
							encounterDifficulty.averagePartyLevel
						),
						fontSize = 10.sp
					)
					Text(
						stringResource(R.string.party_size_label, encounterDifficulty.partySize),
						fontSize = 10.sp
					)
				}
				Column(horizontalAlignment = Alignment.End) {
					Text(
						stringResource(
							R.string.monsters_count_label,
							encounterDifficulty.monsterCount
						),
						fontSize = 10.sp
					)
					Text(
						stringResource(R.string.adjusted_xp_label, encounterDifficulty.adjustedXp),
						fontSize = 10.sp
					)
				}
			}
		}
	}
}

@Composable
private fun difficultyContainerColor(rating: DifficultyRating): Color {
	return when (rating) {
		DifficultyRating.TRIVIAL -> MaterialTheme.colorScheme.surfaceVariant
		DifficultyRating.EASY -> MaterialTheme.colorScheme.primaryContainer
		DifficultyRating.MEDIUM -> MaterialTheme.colorScheme.tertiaryContainer
		DifficultyRating.HARD -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
		DifficultyRating.DEADLY -> MaterialTheme.colorScheme.errorContainer
		DifficultyRating.BEYOND_DEADLY -> MaterialTheme.colorScheme.error
	}
}

@Composable
private fun difficultyTextColor(rating: DifficultyRating): Color {
	return when (rating) {
		DifficultyRating.TRIVIAL, DifficultyRating.EASY -> MaterialTheme.colorScheme.onPrimaryContainer
		DifficultyRating.MEDIUM -> MaterialTheme.colorScheme.onTertiaryContainer
		DifficultyRating.HARD, DifficultyRating.DEADLY -> MaterialTheme.colorScheme.onErrorContainer
		DifficultyRating.BEYOND_DEADLY -> MaterialTheme.colorScheme.onError
	}
}

@Composable
private fun SetupRosterSection(
	combatants: List<CombatantState>,
	onRemoveCombatant: (CombatantState) -> Unit,
	modifier: Modifier = Modifier
) {
	Column(
		modifier = modifier
			.fillMaxWidth()
			.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
			.padding(12.dp)
	) {
		Text(
			stringResource(R.string.encounter_roster_title, combatants.size),
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			style = MaterialTheme.typography.labelSmall,
			modifier = Modifier.padding(bottom = 8.dp)
		)
		if (combatants.isEmpty()) {
			Text(
				stringResource(R.string.encounter_roster_empty_message),
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
				fontSize = 13.sp,
				lineHeight = 20.sp
			)
		} else {
			LazyColumn {
				items(combatants, key = { it.characterId }) { combatant ->
					SetupCombatantRow(combatant = combatant, onRemoveCombatant = onRemoveCombatant)
				}
			}
		}
	}
}

@Composable
private fun SetupCombatantRow(
	combatant: CombatantState,
	onRemoveCombatant: (CombatantState) -> Unit
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 4.dp)
			.background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
			.padding(horizontal = 12.dp, vertical = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		Column(modifier = Modifier.weight(1f)) {
			Text(
				combatant.name,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				fontSize = 14.sp,
				fontWeight = FontWeight.Medium
			)
			Text(
				stringResource(
					R.string.combatant_setup_summary,
					combatant.maxHp,
					combatant.initiative
				),
				color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
				fontSize = 11.sp
			)
		}
		IconButton(
			onClick = { onRemoveCombatant(combatant) },
			modifier = Modifier.size(32.dp)
		) {
			Icon(
				Icons.Default.Delete,
				contentDescription = stringResource(R.string.remove_combatant_desc, combatant.name),
				tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
				modifier = Modifier.size(18.dp)
			)
		}
	}
}

@Composable
private fun EncounterNotesSection(
	notes: String,
	onNotesChange: (String) -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.height(NOTES_HEIGHT_DP.dp)
			.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
			.padding(12.dp)
	) {
		Text(
			stringResource(R.string.encounter_notes_title),
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			style = MaterialTheme.typography.labelSmall
		)
		androidx.compose.foundation.text.BasicTextField(
			value = notes,
			onValueChange = onNotesChange,
			textStyle = androidx.compose.ui.text.TextStyle(
				color = MaterialTheme.colorScheme.onSurface,
				fontSize = 14.sp
			),
			modifier = Modifier.fillMaxSize()
		)
	}
}

@Composable
private fun AddEnemyDialog(
	onConfirm: (name: String, hp: Int, initiative: Int) -> Unit,
	onDismiss: () -> Unit
) {
	var name by remember { mutableStateOf("") }
	var hp by remember { mutableStateOf(DEFAULT_ENEMY_HP.toString()) }
	var initiative by remember { mutableStateOf(DEFAULT_ENEMY_INITIATIVE.toString()) }

	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text(stringResource(R.string.add_enemy_dialog_title)) },
		text = {
			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				OutlinedTextField(
					value = name,
					onValueChange = { name = it },
					label = { Text(stringResource(R.string.name_label)) },
					modifier = Modifier.fillMaxWidth()
				)
				Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
					OutlinedTextField(
						value = hp,
						onValueChange = { hp = it.filter { c -> c.isDigit() } },
						label = { Text(stringResource(R.string.hp_label)) },
						keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
						modifier = Modifier.weight(1f)
					)
					OutlinedTextField(
						value = initiative,
						onValueChange = { initiative = it.filter { c -> c.isDigit() } },
						label = { Text(stringResource(R.string.initiative_label)) },
						keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
						modifier = Modifier.weight(1f)
					)
				}
			}
		},
		confirmButton = {
			Button(
				onClick = {
					if (name.isNotBlank()) {
						onConfirm(
							name.trim(),
							hp.toIntOrNull() ?: DEFAULT_ENEMY_HP,
							initiative.toIntOrNull() ?: DEFAULT_ENEMY_INITIATIVE
						)
					}
				}
			) { Text(stringResource(R.string.add_button)) }
		},
		dismissButton = {
			TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel_button)) }
		}
	)
}

@Composable
fun LiveTrackerView(
	round: Int,
	combatants: List<CombatantState>,
	turnIndex: Int,
	statuses: List<String>,
	onAction: (String) -> Unit,
	onNextTurn: () -> Unit,
	onHpChange: (characterId: String, delta: Int) -> Unit,
	onAddCondition: (characterId: String, condition: String, duration: Int?) -> Unit,
	onRemoveCondition: (characterId: String, conditionName: String) -> Unit,
	onEnd: () -> Unit
) {
	val current = combatants.getOrNull(turnIndex)

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		TrackerModeBadge(
			label = stringResource(R.string.combat_tracker_badge_label),
			containerColor = MaterialTheme.colorScheme.primary,
			contentColor = MaterialTheme.colorScheme.onPrimary
		)
		Spacer(modifier = Modifier.height(16.dp))

		// Round counter
		TrackerDisplayBox(
			label = stringResource(R.string.round_counter, round),
			modifier = Modifier.fillMaxWidth()
		)
		Spacer(modifier = Modifier.height(8.dp))

		// Combatant HP list
		CombatantHpList(
			combatants = combatants,
			turnIndex = turnIndex,
			onHpChange = onHpChange,
			onAddCondition = onAddCondition,
			onRemoveCondition = onRemoveCondition,
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f)
		)

		Spacer(modifier = Modifier.height(8.dp))

		// Action controls for current combatant
		current?.let {
			CurrentCombatantControls(
				combatant = it,
				onAction = onAction,
				onHpChange = onHpChange
			)
		}

		// Combat log
		CombatLogSection(statuses = statuses)

		Spacer(modifier = Modifier.height(8.dp))

		// Bottom action buttons
		EncounterActionButtons(
			onNextTurn = onNextTurn,
			onEnd = onEnd
		)
	}
}

/**
 * Displays the list of all combatants with their HP and initiative.
 * Highlights the active combatant and provides HP adjustment controls.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CombatantHpList(
	combatants: List<CombatantState>,
	turnIndex: Int,
	onHpChange: (characterId: String, delta: Int) -> Unit,
	onAddCondition: (characterId: String, condition: String, duration: Int?) -> Unit,
	onRemoveCondition: (characterId: String, conditionName: String) -> Unit,
	modifier: Modifier = Modifier
) {
	LazyColumn(
		modifier = modifier
			.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
			.padding(8.dp)
	) {
		itemsIndexed(
			combatants,
			key = { _, combatant -> combatant.characterId }) { index, combatant ->
			CombatantListItem(
				combatant = combatant,
				isActive = index == turnIndex,
				onHpChange = onHpChange,
				onAddCondition = onAddCondition,
				onRemoveCondition = onRemoveCondition
			)
		}
	}
}

/**
 * Individual combatant row showing name, initiative, HP, conditions, and controls.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CombatantListItem(
	combatant: CombatantState,
	isActive: Boolean,
	onHpChange: (characterId: String, delta: Int) -> Unit,
	onAddCondition: (characterId: String, condition: String, duration: Int?) -> Unit,
	onRemoveCondition: (characterId: String, conditionName: String) -> Unit
) {
	var showAddConditionDialog by remember { mutableStateOf(false) }
	fun dismissConditionDialog() {
		showAddConditionDialog = false
	}

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 4.dp)
			.background(combatantContainerColor(isActive), RoundedCornerShape(6.dp))
			.padding(horizontal = 8.dp, vertical = 8.dp)
	) {
		CombatantHeaderRow(
			combatant = combatant,
			isActive = isActive,
			onHpChange = onHpChange
		)

		if (combatant.conditions.isNotEmpty() || isActive) {
			Spacer(modifier = Modifier.height(4.dp))
			CombatantConditionsRow(
				combatant = combatant,
				onRemoveCondition = onRemoveCondition,
				onAddConditionClick = { showAddConditionDialog = true }
			)
		}
	}

	// Add condition dialog
	if (showAddConditionDialog) {
		AddConditionDialog(
			onConfirm = { condition, duration ->
				onAddCondition(combatant.characterId, condition, duration)
				dismissConditionDialog()
			},
			onDismiss = ::dismissConditionDialog
		)
	}
}

@Composable
private fun combatantContainerColor(isActive: Boolean): Color {
	return if (isActive) MaterialTheme.colorScheme.primaryContainer
	else MaterialTheme.colorScheme.surfaceVariant
}

@Composable
private fun combatantNameColor(isActive: Boolean): Color {
	return if (isActive) MaterialTheme.colorScheme.onPrimaryContainer
	else MaterialTheme.colorScheme.onSurfaceVariant
}

private fun combatantNameWeight(isActive: Boolean): FontWeight {
	return if (isActive) FontWeight.Bold else FontWeight.Normal
}

private fun formatCombatantHpLabel(combatant: CombatantState): String {
	return "HP: ${combatant.currentHp}/${combatant.maxHp}${if (combatant.tempHp > 0) " +${combatant.tempHp}" else ""}"
}

@Composable
private fun CombatantHeaderRow(
	combatant: CombatantState,
	isActive: Boolean,
	onHpChange: (characterId: String, delta: Int) -> Unit
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically
	) {
		Column(modifier = Modifier.weight(1f)) {
			Text(
				combatant.name,
				color = combatantNameColor(isActive),
				fontWeight = combatantNameWeight(isActive),
				fontSize = 13.sp
			)
			Text(
				stringResource(R.string.combatant_initiative_summary, combatant.initiative),
				color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
				fontSize = 10.sp
			)
		}

		Text(
			formatCombatantHpLabel(combatant),
			fontSize = 12.sp,
			color = if (combatant.currentHp == 0) MaterialTheme.colorScheme.error
			else MaterialTheme.colorScheme.onSurfaceVariant,
			modifier = Modifier.padding(horizontal = 4.dp)
		)
		IconButton(
			onClick = { onHpChange(combatant.characterId, -1) },
			modifier = Modifier.size(28.dp)
		) {
			Icon(
				Icons.Default.Remove,
				contentDescription = stringResource(R.string.damage_combatant_desc, combatant.name),
				modifier = Modifier.size(14.dp)
			)
		}
		IconButton(
			onClick = { onHpChange(combatant.characterId, 1) },
			modifier = Modifier.size(28.dp)
		) {
			Icon(
				Icons.Default.Add,
				contentDescription = stringResource(R.string.heal_combatant_desc, combatant.name),
				modifier = Modifier.size(14.dp)
			)
		}
	}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CombatantConditionsRow(
	combatant: CombatantState,
	onRemoveCondition: (String, String) -> Unit,
	onAddConditionClick: () -> Unit
) {
	FlowRow(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(4.dp),
		verticalArrangement = Arrangement.spacedBy(4.dp)
	) {
		combatant.conditions.forEach { condition ->
			ConditionChip(
				condition = condition,
				onRemove = { onRemoveCondition(combatant.characterId, condition.name) }
			)
		}

		AssistChip(
			onClick = onAddConditionClick,
			label = { Text("+", fontSize = 12.sp) },
			leadingIcon = {
				Icon(
					Icons.Default.Add,
					contentDescription = stringResource(R.string.add_condition_desc),
					modifier = Modifier.size(14.dp)
				)
			},
			colors = AssistChipDefaults.assistChipColors(
				containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
			),
			border = BorderStroke(
				width = 1.dp,
				color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
			)
		)
	}
}

/**
 * Chip displaying a single condition with duration and remove button.
 */
@Composable
private fun ConditionChip(
	condition: Condition,
	onRemove: () -> Unit
) {
	FilterChip(
		selected = true,
		onClick = {},
		label = {
			Text(
				"${condition.name}${conditionDurationText(condition)}",
				fontSize = 11.sp
			)
		},
		trailingIcon = {
			IconButton(
				onClick = onRemove,
				modifier = Modifier.size(16.dp)
			) {
				Icon(
					Icons.Default.Close,
					contentDescription = stringResource(R.string.remove_condition_desc),
					modifier = Modifier.size(12.dp)
				)
			}
		},
		colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
			selectedContainerColor = getConditionColor(condition.name)
		)
	)
}

private fun conditionDurationText(condition: Condition): String {
	return condition.duration?.let { " ($it)" } ?: ""
}

/**
 * Returns a color for the condition chip based on severity/type.
 */
@Composable
private fun getConditionColor(conditionName: String): Color {
	return when (conditionName.lowercase()) {
		"blinded", "deafened", "frightened", "grappled", "prone", "restrained" ->
			MaterialTheme.colorScheme.errorContainer

		"charmed", "stunned", "paralyzed", "petrified", "unconscious", "incapacitated" ->
			MaterialTheme.colorScheme.error.copy(alpha = 0.3f)

		"poisoned", "burning", "bleeding" ->
			Color(0xFF8B0000).copy(alpha = 0.3f)  // Dark red
		"blessed", "inspired", "hasted" ->
			Color(0xFF4CAF50).copy(alpha = 0.3f)  // Green
		"invisible", "hidden" ->
			MaterialTheme.colorScheme.secondaryContainer

		else ->
			MaterialTheme.colorScheme.tertiaryContainer
	}
}

/**
 * Action controls for the currently active combatant.
 * Includes action chips and quick HP adjustment buttons.
 */
@Composable
private fun CurrentCombatantControls(
	combatant: CombatantState,
	onAction: (String) -> Unit,
	onHpChange: (characterId: String, delta: Int) -> Unit
) {
	Text(
		"${combatant.name}'s Turn",
		color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
		style = MaterialTheme.typography.labelSmall
	)
	Spacer(modifier = Modifier.height(4.dp))

	// Action chips
	ActionChipsRow(onAction = onAction)

	Spacer(modifier = Modifier.height(4.dp))

	// Quick HP adjustments
	QuickHpControls(
		characterId = combatant.characterId,
		onHpChange = onHpChange
	)
}

/**
 * Row of action chips (Strike, Cast, Sneak, Dodge).
 */
@Composable
private fun ActionChipsRow(onAction: (String) -> Unit) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		ActionChip(label = "Strike", onClick = { onAction("Strike") })
		ActionChip(label = "Cast", onClick = { onAction("Cast") })
		ActionChip(label = "Sneak", onClick = { onAction("Sneak") })
		ActionChip(label = "Dodge", onClick = { onAction("Dodge") })
	}
}

/**
 * Quick HP adjustment buttons (-5, -1, +1, +5).
 */
@Composable
private fun QuickHpControls(
	characterId: String,
	onHpChange: (characterId: String, delta: Int) -> Unit
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(4.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		Text("HP:", fontSize = 12.sp, modifier = Modifier.padding(end = 4.dp))
		HP_QUICK_ADJUSTMENTS.forEach { delta ->
			OutlinedButton(
				onClick = { onHpChange(characterId, delta) },
				modifier = Modifier
					.weight(1f)
					.height(QUICK_HP_BUTTON_HEIGHT_DP.dp),
				contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
			) {
				Text(if (delta > 0) "+$delta" else delta.toString(), fontSize = 12.sp)
			}
		}
	}
}

/**
 * Combat log section displaying recent actions and events.
 */
@Composable
private fun CombatLogSection(statuses: List<String>) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.height(LOG_HEIGHT_DP.dp)
			.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
			.padding(8.dp)
	) {
		Text(
			stringResource(R.string.session_combat_log_title),
			color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
			style = MaterialTheme.typography.labelSmall
		)
		LazyColumn {
			itemsIndexed(
				statuses.reversed(),
				key = { index, status -> "$index-$status" }) { _, status ->
				Text(
					stringResource(R.string.combat_log_bullet, status),
					color = if (status.contains("!")) MaterialTheme.colorScheme.primary
					else MaterialTheme.colorScheme.onSurface,
					fontSize = 11.sp,
					modifier = Modifier.padding(vertical = 1.dp)
				)
			}
		}
	}
}

/**
 * Bottom action buttons for encounter control (End Turn, End Encounter).
 */
@Composable
private fun EncounterActionButtons(
	onNextTurn: () -> Unit,
	onEnd: () -> Unit
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Button(
			onClick = onNextTurn,
			modifier = Modifier
				.weight(1f)
				.height(ACTION_BUTTON_HEIGHT_DP.dp),
			colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
		) {
			Text(
				stringResource(R.string.end_turn_button),
				color = MaterialTheme.colorScheme.onPrimary
			)
		}
		Button(
			onClick = onEnd,
			modifier = Modifier
				.weight(1f)
				.height(ACTION_BUTTON_HEIGHT_DP.dp),
			colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
		) {
			Text(
				stringResource(R.string.end_encounter_button),
				color = MaterialTheme.colorScheme.onSecondary
			)
		}
	}
}


@Composable
fun TrackerDisplayBox(label: String, modifier: Modifier = Modifier) {
	Box(
		modifier = modifier
			.background(
				MaterialTheme.colorScheme.surfaceVariant,
				RoundedCornerShape(8.dp)
			)
			.padding(16.dp), contentAlignment = Alignment.Center
	) {
		Text(
			label,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			fontWeight = FontWeight.Bold
		)
	}
}

@Composable
fun ActionChip(label: String, onClick: () -> Unit) {
	Box(
		modifier = Modifier
			.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(20.dp))
			.clickable { onClick() }
			.padding(horizontal = 16.dp, vertical = 8.dp)
	) {
		Text(
			label,
			color = MaterialTheme.colorScheme.onPrimaryContainer,
			fontSize = 12.sp,
			fontWeight = FontWeight.Medium
		)
	}
}
