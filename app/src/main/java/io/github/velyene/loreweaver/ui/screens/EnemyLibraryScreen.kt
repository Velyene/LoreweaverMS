/*
 * FILE: EnemyLibraryScreen.kt
 *
 * TABLE OF CONTENTS:
 * 1. Enemy Library Route
 * 2. Enemy Library Screen Content
 * 3. Monster Detail and Staging UI
 * 4. Filter and Summary Helpers
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.DEFAULT_ENEMY_HP
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.DEFAULT_ENEMY_INITIATIVE
import io.github.velyene.loreweaver.ui.screens.tracker.setup.AddEnemyDialog
import io.github.velyene.loreweaver.ui.viewmodels.EnemyLibraryUiState
import io.github.velyene.loreweaver.ui.viewmodels.EnemyLibraryViewModel
import io.github.velyene.loreweaver.ui.viewmodels.StagedEnemyItem

@Composable
fun EnemyLibraryScreen(
	onBack: () -> Unit,
	onOpenEncounterSetup: (List<StagedEnemyItem>) -> Unit,
	viewModel: EnemyLibraryViewModel = hiltViewModel(),
) {
	val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
	EnemyLibraryContent(
		uiState = uiState,
		onBack = onBack,
		onSearchQueryChange = viewModel::updateSearchQuery,
		onGroupSelected = viewModel::updateGroupFilter,
		onCreatureTypeSelected = viewModel::updateCreatureTypeFilter,
		onChallengeRatingSelected = viewModel::updateChallengeRatingFilter,
		onOpenDetail = viewModel::openMonsterDetail,
		onStageMonster = viewModel::stageMonster,
		onIncrementStagedEnemy = viewModel::incrementStagedEnemy,
		onAddTemporaryEnemy = viewModel::stageTemporaryEnemy,
		onRemoveStagedMonster = viewModel::removeStagedMonster,
		onDecrementStagedMonster = viewModel::decrementStagedMonster,
		onClearStagedEnemies = viewModel::clearStagedEnemies,
		onOpenEncounterSetup = { stagedEnemies ->
			onOpenEncounterSetup(stagedEnemies)
			viewModel.clearStagedEnemies()
		},
		onCloseDetail = viewModel::clearMonsterDetail,
	)
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
@Suppress("LongParameterList", "UNUSED_VALUE")
internal fun EnemyLibraryContent(
	uiState: EnemyLibraryUiState,
	onBack: () -> Unit,
	onSearchQueryChange: (String) -> Unit,
	onGroupSelected: (String?) -> Unit,
	onCreatureTypeSelected: (String?) -> Unit,
	onChallengeRatingSelected: (String?) -> Unit,
	onOpenDetail: (String) -> Unit,
	onStageMonster: (String) -> Unit,
	onIncrementStagedEnemy: (String) -> Unit,
	onAddTemporaryEnemy: (String, Int, Int, Int) -> Unit,
	onRemoveStagedMonster: (String) -> Unit,
	onDecrementStagedMonster: (String) -> Unit,
	onClearStagedEnemies: () -> Unit,
	onOpenEncounterSetup: (List<StagedEnemyItem>) -> Unit,
	onCloseDetail: () -> Unit,
) {
	if (uiState.selectedDetail != null) {
		GenericReferenceDetailView(
			detail = uiState.selectedDetail,
			onBack = onCloseDetail,
		)
		return
	}

	val listState = rememberLazyListState()
	var showAddTemporaryEnemyDialog by remember { mutableStateOf(false) }
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(stringResource(R.string.enemy_library_title)) },
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							imageVector = Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = stringResource(R.string.back_button),
						)
					}
				},
			)
		},
	) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp, vertical = 12.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp),
			) {
				Text(
					text = stringResource(R.string.enemy_library_subtitle),
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
				OutlinedTextField(
					value = uiState.searchQuery,
					onValueChange = onSearchQueryChange,
					modifier = Modifier.fillMaxWidth(),
					label = { Text(stringResource(R.string.enemy_library_search_label)) },
					placeholder = { Text(stringResource(R.string.reference_search_hint)) },
					singleLine = true,
				)
				RecentEnemiesCard(
					recentEnemies = uiState.recentEnemies,
					onStageMonster = onStageMonster,
				)
				StagedEnemiesCard(
					stagedEnemies = uiState.stagedEnemies,
					totalCount = uiState.stagedEnemyTotalCount,
					onAddTemporaryEnemy = { showAddTemporaryEnemyDialog = true },
					onIncrementStagedEnemy = onIncrementStagedEnemy,
					onRemoveStagedMonster = onRemoveStagedMonster,
					onDecrementStagedMonster = onDecrementStagedMonster,
					onClearStagedEnemies = onClearStagedEnemies,
					onOpenEncounterSetup = { onOpenEncounterSetup(uiState.stagedEnemies) },
				)
			}
			MonstersContent(
				monsters = uiState.monsters,
				selectedGroup = uiState.selectedGroup,
				selectedCreatureType = uiState.selectedCreatureType,
				selectedChallengeRating = uiState.selectedChallengeRating,
				listState = listState,
				onGroupSelected = onGroupSelected,
				onCreatureTypeSelected = onCreatureTypeSelected,
				onChallengeRatingSelected = onChallengeRatingSelected,
				onOpenDetail = { _, slug -> onOpenDetail(slug) },
				onStageMonster = onStageMonster,
			)
		}
	}

	if (showAddTemporaryEnemyDialog) {
		AddEnemyDialog(
			initialName = "",
			initialHp = DEFAULT_ENEMY_HP.toString(),
			initialInitiative = DEFAULT_ENEMY_INITIATIVE.toString(),
			initialQuantity = "1",
			titleOverride = stringResource(R.string.enemy_library_temp_enemy_dialog_title),
			helperTextOverride = stringResource(R.string.enemy_library_temp_enemy_dialog_supporting_text),
			onConfirm = { name, hp, initiative, quantity ->
				onAddTemporaryEnemy(name, hp, initiative, quantity)
				showAddTemporaryEnemyDialog = false
			},
			onDismiss = { showAddTemporaryEnemyDialog = false },
		)
	}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecentEnemiesCard(
	recentEnemies: List<io.github.velyene.loreweaver.domain.util.MonsterReferenceEntry>,
	onStageMonster: (String) -> Unit,
) {
	if (recentEnemies.isEmpty()) return
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp),
		) {
			Text(
				text = stringResource(R.string.enemy_library_recent_title),
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.SemiBold,
			)
			FlowRow(
				horizontalArrangement = Arrangement.spacedBy(8.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp),
			) {
				recentEnemies.forEach { enemy ->
					FilterChip(
						selected = false,
						onClick = { onStageMonster(enemy.name) },
						label = { Text(enemy.name) },
					)
				}
			}
		}
	}
}

@Composable
private fun StagedEnemiesCard(
	stagedEnemies: List<StagedEnemyItem>,
	totalCount: Int,
	onAddTemporaryEnemy: () -> Unit,
	onIncrementStagedEnemy: (String) -> Unit,
	onRemoveStagedMonster: (String) -> Unit,
	onDecrementStagedMonster: (String) -> Unit,
	onClearStagedEnemies: () -> Unit,
	onOpenEncounterSetup: () -> Unit,
) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp),
		) {
			Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
				Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
					Text(
						text = stringResource(R.string.enemy_library_staging_title),
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.SemiBold,
					)
					Text(
						text = stringResource(R.string.enemy_library_staging_total, totalCount),
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
				}
				TextButton(onClick = onAddTemporaryEnemy) {
					Text(stringResource(R.string.enemy_library_temporary_enemy_button))
				}
				if (stagedEnemies.isNotEmpty()) {
					TextButton(onClick = onClearStagedEnemies) {
						Text(stringResource(R.string.clear_button))
					}
				}
			}
			if (stagedEnemies.isEmpty()) {
				Text(
					text = stringResource(R.string.enemy_library_staging_empty),
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			} else {
				stagedEnemies.forEach { stagedEnemy ->
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceBetween,
					) {
						Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
							Text(stagedEnemy.name, fontWeight = FontWeight.Medium)
							Text(
								text = stringResource(
									if (stagedEnemy.isTemporary) {
										R.string.enemy_library_staging_item_summary_temporary
									} else {
										R.string.enemy_library_staging_item_summary_catalog
									},
									stagedEnemy.quantity,
									stagedEnemy.hp,
									stagedEnemy.initiative,
									stagedEnemy.challengeRating.ifBlank { stringResource(R.string.none_label) },
								),
								style = MaterialTheme.typography.bodySmall,
								color = MaterialTheme.colorScheme.onSurfaceVariant,
							)
						}
						Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
							TextButton(onClick = { onDecrementStagedMonster(stagedEnemy.key) }) {
								Text("-1")
							}
							TextButton(onClick = { onIncrementStagedEnemy(stagedEnemy.key) }) {
								Text("+1")
							}
							TextButton(onClick = { onRemoveStagedMonster(stagedEnemy.key) }) {
								Text(stringResource(R.string.remove_button))
							}
						}
					}
				}
			}
			Button(
				onClick = onOpenEncounterSetup,
				enabled = stagedEnemies.isNotEmpty(),
				modifier = Modifier.fillMaxWidth(),
			) {
				Text(stringResource(R.string.enemy_library_handoff_button))
			}
		}
	}
}

