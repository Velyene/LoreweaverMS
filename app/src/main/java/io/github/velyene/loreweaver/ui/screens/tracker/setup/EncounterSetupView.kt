/*
 * FILE: EncounterSetupView.kt
 *
 * TABLE OF CONTENTS:
 * 1. Encounter Setup Screen (EncounterSetupView)
 * 2. Difficulty Summary (EncounterDifficultyCard, difficulty color helpers)
 * 3. Notes Input (EncounterNotesSection)
 * 4. Add Enemy Dialog (AddEnemyDialog)
 */

package io.github.velyene.loreweaver.ui.screens.tracker.setup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.util.CharacterParty
import io.github.velyene.loreweaver.domain.util.EncounterDifficultyResult
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.SETUP_BUTTON_HEIGHT_DP
import io.github.velyene.loreweaver.ui.screens.ConfirmationDialog
import io.github.velyene.loreweaver.ui.screens.tracker.components.TrackerModeBadge
import io.github.velyene.loreweaver.ui.screens.visibleVerticalScrollbar
import io.github.velyene.loreweaver.ui.viewmodels.EncounterLifecycle
import io.github.velyene.loreweaver.ui.viewmodels.InitiativeMode
import io.github.velyene.loreweaver.ui.viewmodels.sortCombatantsForInitiative

internal const val ENCOUNTER_SETUP_ROOT_TAG = "encounter_setup_root"
internal const val ENCOUNTER_SETUP_START_BUTTON_TAG = "encounter_setup_start_button"
internal const val ENCOUNTER_SETUP_LIST_TAG = "encounter_setup_list"
internal const val ENCOUNTER_SETUP_NAME_FIELD_TAG = "encounter_setup_name_field"
internal const val ENCOUNTER_SETUP_LOCATION_FIELD_TAG = "encounter_setup_location_field"

internal data class EncounterSetupPartyActions(
	val onAddEntireParty: () -> Unit,
	val onClearPartyMembers: () -> Unit,
	val onTogglePartyMember: (CharacterEntry) -> Unit
)

internal data class EncounterSetupViewState(
	val encounterName: String = "",
	val startFresh: Boolean = false,
	val encounterId: String? = null,
	val encounterLifecycle: EncounterLifecycle = EncounterLifecycle.DRAFT,
	val initiativeMode: InitiativeMode = InitiativeMode.MANUAL,
	val locationTerrain: String = "",
	val notes: String,
	val combatants: List<CombatantState>,
	val availablePartyMembers: List<CharacterEntry>,
	val encounterDifficulty: EncounterDifficultyResult?,
	val importedLibraryEnemyCount: Int = 0,
	val importedTemporaryEnemyCount: Int = 0,
	val generationSettings: io.github.velyene.loreweaver.domain.model.EncounterGenerationSettings = io.github.velyene.loreweaver.domain.model.EncounterGenerationSettings(),
	val generationDetails: io.github.velyene.loreweaver.domain.model.EncounterGenerationDetails? = null,
	val monsterTypeOptions: List<String> = emptyList(),
	val monsterGroupOptions: List<String> = emptyList()
)

internal data class EncounterSetupCallbacks(
	val onEncounterNameChange: (String) -> Unit = {},
	val onLocationTerrainChange: (String) -> Unit = {},
	val onNotesChange: (String) -> Unit,
	val onInitiativeModeChange: (InitiativeMode) -> Unit = {},
	val onStart: () -> Unit,
	val partyActions: EncounterSetupPartyActions,
	val onAddEnemies: (name: String, hp: Int, initiative: Int, quantity: Int) -> Unit,
	val onUpdateGenerationSettings: (io.github.velyene.loreweaver.domain.model.EncounterGenerationSettings) -> Unit = {},
	val onGenerateEncounter: () -> Unit = {},
	val onUpdateCombatantInitiative: (characterId: String, initiative: Int) -> Unit,
	val onMoveCombatantUp: (characterId: String) -> Unit,
	val onMoveCombatantDown: (characterId: String) -> Unit,
	val onRemoveCombatant: (characterId: String) -> Unit
)

internal data class EncounterEnemyPresetEntry(
	val name: String,
	val hp: Int,
	val initiative: Int,
	val quantity: Int
)

internal data class EncounterEnemyPreset(
	val label: String,
	val summary: String,
	val entries: List<EncounterEnemyPresetEntry>
)

private data class EncounterSetupDerivedUiState(
	val savedPartyMembers: List<CharacterEntry>,
	val savedPartyMemberIds: Set<String>,
	val selectedPartyIds: Set<String>,
	val selectedPartyMembers: List<CharacterEntry>,
	val enemies: List<CombatantState>,
	val rosterByInitiative: List<CombatantState>,
	val canStartEncounter: Boolean,
	val encounterDisplayName: String,
	val importedLibrarySummary: String?,
	val latestChange: String,
	val attentionItems: List<String>,
	val nextStep: String,
	val initiativeSummary: String,
)

private enum class EncounterSetupMode {
	QUICK,
	CAMPAIGN,
	LOAD_SAVED,
}

internal fun defaultEncounterEnemyPresets(): List<EncounterEnemyPreset> {
	return listOf(
		EncounterEnemyPreset(
			label = "Goblin Ambush",
			summary = "4 Goblins at fast initiative for a quick skirmish.",
			entries = listOf(
				EncounterEnemyPresetEntry(name = "Goblin", hp = 7, initiative = 14, quantity = 4)
			)
		),
		EncounterEnemyPreset(
			label = "Skeleton Patrol",
			summary = "6 Skeletons for an attrition-heavy encounter wave.",
			entries = listOf(
				EncounterEnemyPresetEntry(name = "Skeleton", hp = 13, initiative = 12, quantity = 6)
			)
		),
		EncounterEnemyPreset(
			label = "Cultist Cell",
			summary = "4 Cultists backed by 1 Acolyte support caster.",
			entries = listOf(
				EncounterEnemyPresetEntry(name = "Cultist", hp = 9, initiative = 11, quantity = 4),
				EncounterEnemyPresetEntry(name = "Acolyte", hp = 9, initiative = 10, quantity = 1)
			)
		),
		EncounterEnemyPreset(
			label = "Boss + Minions",
			summary = "1 Boss frontliner with 3 guards for a showcase fight.",
			entries = listOf(
				EncounterEnemyPresetEntry(name = "Boss", hp = 45, initiative = 16, quantity = 1),
				EncounterEnemyPresetEntry(name = "Guard", hp = 11, initiative = 12, quantity = 3)
			)
		)
	)
}

internal fun applyEncounterEnemyPreset(
	preset: EncounterEnemyPreset,
	onAddEnemies: (name: String, hp: Int, initiative: Int, quantity: Int) -> Unit
) {
	preset.entries.forEach { entry ->
		onAddEnemies(entry.name, entry.hp, entry.initiative, entry.quantity)
	}
}

private fun defaultEncounterSetupMode(state: EncounterSetupViewState): EncounterSetupMode {
	return when {
		state.encounterId != null || !state.startFresh -> EncounterSetupMode.LOAD_SAVED
		state.encounterLifecycle != EncounterLifecycle.DRAFT -> EncounterSetupMode.CAMPAIGN
		else -> EncounterSetupMode.QUICK
	}
}

@Suppress("LongParameterList")
@Composable
internal fun EncounterSetupView(
	state: EncounterSetupViewState,
	callbacks: EncounterSetupCallbacks
) {
	var showAddEnemyDialog by remember { mutableStateOf(false) }
	var combatantPendingRemoval by remember { mutableStateOf<CombatantState?>(null) }
	var encounterSetupMode by remember(
		state.encounterId,
		state.startFresh,
		state.encounterLifecycle,
	) {
		mutableStateOf(defaultEncounterSetupMode(state))
	}
	val enemyPresets = remember { defaultEncounterEnemyPresets() }
	val listState = rememberLazyListState()
	val derivedState = rememberEncounterSetupDerivedUiState(
		state = state,
		encounterSetupMode = encounterSetupMode,
	)

	Box(
		modifier = Modifier
			.fillMaxSize()
			.testTag(ENCOUNTER_SETUP_ROOT_TAG)
	) {
		EncounterSetupStepsList(
			state = state,
			callbacks = callbacks,
			derivedState = derivedState,
			enemyPresets = enemyPresets,
			listState = listState,
			onAddEnemy = { showAddEnemyDialog = true },
			onRequestRemoveCombatant = { combatantPendingRemoval = it },
		)
	}

	EncounterSetupDialogs(
		showAddEnemyDialog = showAddEnemyDialog,
		combatantPendingRemoval = combatantPendingRemoval,
		onAddEnemies = callbacks.onAddEnemies,
		onRemoveCombatant = callbacks.onRemoveCombatant,
		onDismissAddEnemyDialog = {
			@Suppress("UNUSED_VALUE")
			showAddEnemyDialog = false
		},
		onDismissCombatantRemoval = {
			@Suppress("UNUSED_VALUE")
			combatantPendingRemoval = null
		},
	)
}

@Composable
private fun rememberEncounterSetupDerivedUiState(
	state: EncounterSetupViewState,
	encounterSetupMode: EncounterSetupMode,
): EncounterSetupDerivedUiState {
	val savedPartyMembers = remember(state.availablePartyMembers) {
		state.availablePartyMembers
			.filter { it.party == CharacterParty.ADVENTURERS }
			.sortedBy { it.name.lowercase() }
	}
	val savedPartyMemberIds = remember(savedPartyMembers) {
		savedPartyMembers.map(CharacterEntry::id).toSet()
	}
	val selectedPartyIds = remember(state.combatants, savedPartyMemberIds) {
		state.combatants.map(CombatantState::characterId).filter(savedPartyMemberIds::contains).toSet()
	}
	val selectedPartyMembers = remember(savedPartyMembers, selectedPartyIds) {
		savedPartyMembers.filter { it.id in selectedPartyIds }
	}
	val enemies = remember(state.combatants, savedPartyMemberIds) {
		state.combatants.filterNot { savedPartyMemberIds.contains(it.characterId) }
	}
	val rosterByInitiative = remember(state.combatants) { sortCombatantsForInitiative(state.combatants) }
	val canStartEncounter = selectedPartyIds.isNotEmpty()
	val encounterDisplayName = state.encounterName.ifBlank {
		stringResource(
			when (encounterSetupMode) {
				EncounterSetupMode.QUICK -> R.string.encounter_setup_mode_quick
				EncounterSetupMode.CAMPAIGN -> R.string.encounter_setup_mode_campaign
				EncounterSetupMode.LOAD_SAVED -> R.string.encounter_setup_mode_load_saved
			},
		)
	}
	val notesStatus = if (state.notes.isBlank()) {
		stringResource(R.string.encounter_setup_notes_status_pending)
	} else {
		stringResource(R.string.encounter_setup_notes_status_ready)
	}
	val importedLibrarySummary = when {
		state.importedLibraryEnemyCount <= 0 -> null
		state.importedTemporaryEnemyCount > 0 -> stringResource(
			R.string.encounter_setup_imported_staging_summary_with_temp,
			state.importedLibraryEnemyCount,
			state.importedTemporaryEnemyCount,
		)
		else -> stringResource(
			R.string.encounter_setup_imported_staging_summary,
			state.importedLibraryEnemyCount,
		)
	}
	val latestChange = state.generationDetails?.logLines?.lastOrNull()
		?: importedLibrarySummary
		?: stringResource(
			R.string.encounter_setup_operator_changed_summary,
			selectedPartyIds.size,
			enemies.size,
			notesStatus,
		)
	val attentionItems = buildList {
		if (selectedPartyIds.isEmpty()) add(stringResource(R.string.encounter_setup_attention_add_party))
		if (enemies.isEmpty()) add(stringResource(R.string.encounter_setup_attention_add_enemies))
		if (rosterByInitiative.isEmpty()) add(stringResource(R.string.encounter_setup_attention_set_initiative))
		if (isEmpty()) add(stringResource(R.string.encounter_setup_attention_ready))
	}
	val nextStep = if (canStartEncounter) {
		stringResource(R.string.encounter_setup_operator_next_ready)
	} else {
		stringResource(R.string.encounter_setup_operator_next_pending)
	}
	val initiativeSummary = stringResource(
		R.string.encounter_setup_initiative_mode_summary,
		stringResource(state.initiativeMode.labelRes()),
	)
	return EncounterSetupDerivedUiState(
		savedPartyMembers = savedPartyMembers,
		savedPartyMemberIds = savedPartyMemberIds,
		selectedPartyIds = selectedPartyIds,
		selectedPartyMembers = selectedPartyMembers,
		enemies = enemies,
		rosterByInitiative = rosterByInitiative,
		canStartEncounter = canStartEncounter,
		encounterDisplayName = encounterDisplayName,
		importedLibrarySummary = importedLibrarySummary,
		latestChange = latestChange,
		attentionItems = attentionItems,
		nextStep = nextStep,
		initiativeSummary = initiativeSummary,
	)
}

@Composable
private fun EncounterSetupStepsList(
	state: EncounterSetupViewState,
	callbacks: EncounterSetupCallbacks,
	derivedState: EncounterSetupDerivedUiState,
	enemyPresets: List<EncounterEnemyPreset>,
	listState: androidx.compose.foundation.lazy.LazyListState,
	onAddEnemy: () -> Unit,
	onRequestRemoveCombatant: (CombatantState) -> Unit,
) {
	LazyColumn(
		state = listState,
		modifier = Modifier
			.fillMaxSize()
			.testTag(ENCOUNTER_SETUP_LIST_TAG)
			.visibleVerticalScrollbar(listState),
		contentPadding = PaddingValues(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 32.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		item(key = "setup-badge") {
			Column(horizontalAlignment = Alignment.CenterHorizontally) {
				TrackerModeBadge(
					label = stringResource(R.string.combat_setup_badge_label),
					containerColor = MaterialTheme.colorScheme.tertiary,
					contentColor = MaterialTheme.colorScheme.onTertiary,
				)
				Spacer(modifier = Modifier.height(4.dp))
			}
		}

		item(key = "setup-summary") {
			Text(
				text = stringResource(R.string.encounter_setup_shared_manager_summary),
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.fillMaxWidth(),
			)
		}

		item(key = "setup-brief") {
			EncounterSetupOperatorBriefCard(
				encounterName = derivedState.encounterDisplayName,
				latestChange = derivedState.latestChange,
				attentionItems = derivedState.attentionItems,
				nextStep = derivedState.nextStep,
			)
		}

		item(key = "setup-encounter-info") {
			EncounterSetupStepCard(
				stepNumber = 1,
				title = stringResource(R.string.encounter_setup_info_step_title),
				supportingText = stringResource(R.string.encounter_setup_info_step_supporting_text),
			) {
				Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
					EncounterNameSection(
						encounterName = state.encounterName,
						onEncounterNameChange = callbacks.onEncounterNameChange,
					)
					LocationTerrainSection(
						locationTerrain = state.locationTerrain,
						onLocationTerrainChange = callbacks.onLocationTerrainChange,
					)
					EncounterNotesSection(notes = state.notes, onNotesChange = callbacks.onNotesChange)
				}
			}
		}

		item(key = "setup-party") {
			EncounterSetupStepCard(
				stepNumber = 2,
				title = stringResource(R.string.encounter_party_members_title),
				supportingText = stringResource(R.string.encounter_setup_party_step_supporting_text),
			) {
				PartyMembersSection(
					savedPartyMembers = derivedState.savedPartyMembers,
					selectedPartyIds = derivedState.selectedPartyIds,
					onTogglePartyMember = callbacks.partyActions.onTogglePartyMember,
				)
			}
		}

		item(key = "setup-enemies") {
			EncounterSetupStepCard(
				stepNumber = 3,
				title = stringResource(R.string.encounter_setup_enemies_step_title),
				supportingText = stringResource(R.string.encounter_setup_enemies_step_supporting_text),
			) {
				Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
					if (derivedState.importedLibrarySummary != null) {
						EnemyLibraryImportCard(
							summary = derivedState.importedLibrarySummary,
							temporaryEnemyCount = state.importedTemporaryEnemyCount,
						)
					}
					EnemyPresetSection(
						presets = enemyPresets,
						onApplyPreset = { preset -> applyEncounterEnemyPreset(preset, callbacks.onAddEnemies) },
					)
					EnemiesSection(
						enemies = derivedState.enemies,
						onAddEnemy = onAddEnemy,
						onRemoveEnemy = onRequestRemoveCombatant,
					)
					RandomEncounterGenerationSection(
						selectedPartyMembers = derivedState.selectedPartyMembers,
						settings = state.generationSettings,
						generationDetails = state.generationDetails,
						monsterTypeOptions = state.monsterTypeOptions,
						monsterGroupOptions = state.monsterGroupOptions,
						onSettingsChange = callbacks.onUpdateGenerationSettings,
						onGenerateEncounter = callbacks.onGenerateEncounter,
						enabled = derivedState.selectedPartyMembers.isNotEmpty(),
					)
				}
			}
		}

		item(key = "setup-roster") {
			EncounterSetupStepCard(
				stepNumber = 4,
				title = stringResource(R.string.encounter_setup_initiative_step_title),
				supportingText = stringResource(R.string.encounter_setup_initiative_step_supporting_text),
			) {
				Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
					InitiativeModeSection(
						initiativeMode = state.initiativeMode,
						onInitiativeModeChange = callbacks.onInitiativeModeChange,
					)
					Text(
						text = derivedState.initiativeSummary,
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
					EncounterRosterSection(
						combatants = derivedState.rosterByInitiative,
						savedPartyMemberIds = derivedState.savedPartyMemberIds,
						onUpdateCombatantInitiative = callbacks.onUpdateCombatantInitiative,
						onMoveCombatantUp = callbacks.onMoveCombatantUp,
						onMoveCombatantDown = callbacks.onMoveCombatantDown,
						onRemoveCombatant = onRequestRemoveCombatant,
					)
				}
			}
		}

		item(key = "setup-launch") {
			EncounterSetupStepCard(
				stepNumber = 5,
				title = stringResource(R.string.encounter_setup_launch_step_title),
				supportingText = stringResource(R.string.encounter_setup_launch_step_supporting_text),
			) {
				Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
					EncounterSetupSnapshotCard(
						totalCombatants = state.combatants.size,
						selectedPartyCount = derivedState.selectedPartyIds.size,
						enemyCount = derivedState.enemies.size,
						initiativeLeader = derivedState.rosterByInitiative.firstOrNull(),
					)
					if ((state.encounterDifficulty != null) && (state.encounterDifficulty.partySize > 0)) {
						EncounterDifficultyCard(encounterDifficulty = state.encounterDifficulty)
					}
					SetupActionBar(
						selectedPartyCount = derivedState.selectedPartyIds.size,
						enemyCount = derivedState.enemies.size,
						canStartEncounter = derivedState.canStartEncounter,
						onAddEnemy = onAddEnemy,
						onStart = callbacks.onStart,
					)
				}
			}
		}
	}
}

@Composable
private fun EncounterSetupDialogs(
	showAddEnemyDialog: Boolean,
	combatantPendingRemoval: CombatantState?,
	onAddEnemies: (name: String, hp: Int, initiative: Int, quantity: Int) -> Unit,
	onRemoveCombatant: (characterId: String) -> Unit,
	onDismissAddEnemyDialog: () -> Unit,
	onDismissCombatantRemoval: () -> Unit,
) {
	if (showAddEnemyDialog) {
		AddEnemyDialog(
			initialName = "",
			initialHp = io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.DEFAULT_ENEMY_HP.toString(),
			initialInitiative = io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.DEFAULT_ENEMY_INITIATIVE.toString(),
			initialQuantity = "1",
			onConfirm = { name, hp, initiative, quantity ->
				onAddEnemies(name, hp, initiative, quantity)
				onDismissAddEnemyDialog()
			},
			onDismiss = onDismissAddEnemyDialog,
		)
	}

	combatantPendingRemoval?.let { combatant ->
		ConfirmationDialog(
			title = stringResource(R.string.remove_combatant_confirm_title),
			message = stringResource(R.string.remove_combatant_confirm_message, combatant.name),
			confirmLabel = stringResource(R.string.remove_button),
			onConfirm = {
				onRemoveCombatant(combatant.characterId)
				onDismissCombatantRemoval()
			},
			onDismiss = onDismissCombatantRemoval,
		)
	}
}

@Composable
private fun EncounterSetupOperatorBriefCard(
	encounterName: String,
	latestChange: String,
	attentionItems: List<String>,
	nextStep: String,
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp),
		) {
			Text(
				text = stringResource(R.string.encounter_setup_operator_brief_title),
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold,
			)
			OperatorBriefLine(
				label = stringResource(R.string.dm_operator_question_encounter),
				value = encounterName,
			)
			OperatorBriefLine(
				label = stringResource(R.string.dm_operator_question_turn),
				value = stringResource(R.string.encounter_setup_operator_turn_pending),
			)
			OperatorBriefLine(
				label = stringResource(R.string.dm_operator_question_changed),
				value = latestChange,
			)
			OperatorBriefLine(
				label = stringResource(R.string.dm_operator_question_attention),
				value = attentionItems.joinToString(separator = "\n") { item -> "• $item" },
			)
			OperatorBriefLine(
				label = stringResource(R.string.dm_operator_question_next),
				value = nextStep,
			)
		}
	}
}

@Composable
private fun EnemyLibraryImportCard(
	summary: String,
	temporaryEnemyCount: Int,
) {
	Card(
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp),
		) {
			Text(
				text = stringResource(R.string.encounter_setup_imported_staging_title),
				style = MaterialTheme.typography.labelLarge,
				fontWeight = FontWeight.SemiBold,
				color = MaterialTheme.colorScheme.onSecondaryContainer,
			)
			Text(
				text = summary,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSecondaryContainer,
			)
			Text(
				text = stringResource(
					if (temporaryEnemyCount > 0) {
						R.string.encounter_setup_imported_staging_supporting_text_with_temp
					} else {
						R.string.encounter_setup_imported_staging_supporting_text
					},
				),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.84f),
			)
		}
	}
}

@Composable
private fun OperatorBriefLine(label: String, value: String) {
	Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
		Text(
			text = label,
			style = MaterialTheme.typography.labelMedium,
			fontWeight = FontWeight.SemiBold,
			color = MaterialTheme.colorScheme.primary,
		)
		Text(
			text = value,
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurface,
		)
	}
}

@Composable
private fun EncounterSetupStepCard(
	stepNumber: Int,
	title: String,
	supportingText: String,
	content: @Composable () -> Unit,
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(10.dp),
		) {
			Text(
				text = stringResource(R.string.encounter_setup_step_format, stepNumber, title),
				style = MaterialTheme.typography.titleSmall,
				fontWeight = FontWeight.SemiBold,
			)
			Text(
				text = supportingText,
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
			content()
		}
	}
}


@Composable
private fun EncounterNameSection(
	encounterName: String,
	onEncounterNameChange: (String) -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
		OutlinedTextField(
			value = encounterName,
			onValueChange = onEncounterNameChange,
			modifier = Modifier
				.fillMaxWidth()
				.testTag(ENCOUNTER_SETUP_NAME_FIELD_TAG),
			label = { Text(text = stringResource(R.string.encounter_name_label)) },
			placeholder = { Text(text = stringResource(R.string.encounter_setup_encounter_name_placeholder)) },
			singleLine = true,
		)
		Text(
			text = stringResource(R.string.encounter_setup_encounter_name_supporting_text),
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
	}
}

@Composable
private fun InitiativeModeSection(
	initiativeMode: InitiativeMode,
	onInitiativeModeChange: (InitiativeMode) -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
		Text(
			text = stringResource(R.string.encounter_setup_initiative_mode_title),
			style = MaterialTheme.typography.labelLarge,
			fontWeight = FontWeight.SemiBold,
		)
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(8.dp),
		) {
			InitiativeMode.entries.forEach { mode ->
				FilterChip(
					selected = initiativeMode == mode,
					onClick = { onInitiativeModeChange(mode) },
					label = { Text(text = stringResource(mode.labelRes())) },
				)
			}
		}
		Text(
			text = stringResource(initiativeMode.descriptionRes()),
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
	}
}

private fun InitiativeMode.labelRes(): Int {
	return when (this) {
		InitiativeMode.MANUAL -> R.string.initiative_mode_manual
		InitiativeMode.AUTO_ROLL -> R.string.initiative_mode_auto_roll
		InitiativeMode.AUTO_SORT -> R.string.initiative_mode_auto_sort
	}
}

private fun InitiativeMode.descriptionRes(): Int {
	return when (this) {
		InitiativeMode.MANUAL -> R.string.initiative_mode_manual_description
		InitiativeMode.AUTO_ROLL -> R.string.initiative_mode_auto_roll_description
		InitiativeMode.AUTO_SORT -> R.string.initiative_mode_auto_sort_description
	}
}

@Composable
private fun SetupActionBar(
	selectedPartyCount: Int,
	enemyCount: Int,
	canStartEncounter: Boolean,
	onAddEnemy: () -> Unit,
	onStart: () -> Unit,
	modifier: Modifier = Modifier
) {
	Surface(
		modifier = modifier.fillMaxWidth(),
		tonalElevation = 4.dp,
		shadowElevation = 10.dp,
		color = MaterialTheme.colorScheme.surface,
		border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
	) {
		Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
			Text(
				text = if (canStartEncounter) {
					stringResource(
						R.string.encounter_setup_action_bar_ready,
						selectedPartyCount,
						enemyCount
					)
				} else {
					stringResource(R.string.encounter_setup_action_bar_missing_party)
				},
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.fillMaxWidth()
			)
			Spacer(modifier = Modifier.height(10.dp))
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(12.dp)
			) {
				OutlinedButton(
					onClick = onAddEnemy,
					modifier = Modifier
						.weight(1f)
						.height(SETUP_BUTTON_HEIGHT_DP.dp)
				) {
					Text(text = stringResource(R.string.add_enemy_dialog_title))
				}
				Button(
					onClick = onStart,
					enabled = canStartEncounter,
					modifier = Modifier
						.weight(1f)
						.height(SETUP_BUTTON_HEIGHT_DP.dp)
						.testTag(ENCOUNTER_SETUP_START_BUTTON_TAG),
					colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
				) {
					Text(
						text = stringResource(R.string.start_encounter_button),
						color = MaterialTheme.colorScheme.onPrimary,
						fontWeight = FontWeight.Bold
					)
				}
			}
		}
	}
}



