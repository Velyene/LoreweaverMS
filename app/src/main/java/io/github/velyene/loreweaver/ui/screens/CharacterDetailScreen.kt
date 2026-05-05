/*
 * FILE: CharacterDetailScreen.kt
 *
 * TABLE OF CONTENTS:
 * 1. Main Screen (CharacterDetailScreen)
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.ui.viewmodels.CharacterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
	characterId: String?,
	onEdit: (String) -> Unit,
	onLookupCondition: (String) -> Unit,
	onDelete: () -> Unit,
	onBack: () -> Unit,
	viewModel: CharacterViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	LaunchedEffect(characterId) {
		characterId?.let { viewModel.selectCharacter(it) }
	}

	val character = uiState.selectedCharacter
	var selectedTab by rememberSaveable { mutableIntStateOf(0) }
	var pendingDeleteCharacter by remember { mutableStateOf<CharacterEntry?>(null) }
	val tabs = listOf(
		stringResource(R.string.tab_combat),
		stringResource(R.string.tab_stats),
		stringResource(R.string.tab_journal)
	)
	val onUpdateStat: (Int, String) -> Unit = { delta, statType ->
		character?.let { current ->
			viewModel.updateCharacter(applyStatDelta(current, delta, statType))
			if (statType == STAT_TYPE_HP) {
				val message = if (delta < 0) "takes ${-delta} damage" else "heals for $delta HP"
				viewModel.logAction("${current.name} $message", statType)
			}
		}
	}

	val haptic = LocalHapticFeedback.current
	var rollResult by remember { mutableStateOf<Pair<String, Int>?>(null) }
	var situationalBonus by rememberSaveable { mutableStateOf("0") }

	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Text(character?.name ?: stringResource(R.string.character_details_title))
				},
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = stringResource(R.string.back_to_characters)
						)
					}
				},
				actions = {
					CharacterDetailActions(
						character = character,
						onEdit = onEdit,
						onDeleteRequest = { pendingDeleteCharacter = it }
					)
				}
			)
		}
	) { padding ->
		CharacterDetailContent(
			character = character,
			state = CharacterDetailState(
				selectedTab = selectedTab,
				onTabSelected = { selectedTab = it },
				tabs = tabs,
				onUpdateStat = onUpdateStat,
				haptic = haptic,
				situationalBonus = situationalBonus,
				onBonusChange = { situationalBonus = it },
				rollResult = rollResult,
				onRollResult = { rollResult = it }
			),
			viewModel = viewModel,
			onLookupCondition = onLookupCondition,
			padding = padding
		)
	}

	pendingDeleteCharacter?.let { characterToDelete ->
		ConfirmationDialog(
			title = stringResource(R.string.confirm_delete_character_title),
			message = stringResource(
				R.string.confirm_delete_character_message,
				characterToDelete.name
			),
			confirmLabel = stringResource(R.string.delete_button),
			onConfirm = {
				viewModel.deleteCharacter(characterToDelete)
				onDelete()
				@Suppress("UNUSED_VALUE")
				pendingDeleteCharacter = null
			},
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				pendingDeleteCharacter = null
			}
		)
	}
}

