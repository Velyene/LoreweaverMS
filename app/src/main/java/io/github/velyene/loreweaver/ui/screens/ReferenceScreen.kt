/*
 * FILE: ReferenceScreen.kt
 *
 * Fifth-edition reference screen with tabs and search.
 *
 * TABLE OF CONTENTS:
 * 1. Screen entry point and category/list state setup
 * 2. Remembered derived content state
 * 3. Category content routing
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.util.DiseaseTemplate
import io.github.velyene.loreweaver.domain.util.PoisonTemplate
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver
import io.github.velyene.loreweaver.domain.util.TrapTemplate
import io.github.velyene.loreweaver.ui.viewmodels.ReferenceCategory
import io.github.velyene.loreweaver.ui.viewmodels.ReferenceUiState
import io.github.velyene.loreweaver.ui.viewmodels.ReferenceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferenceScreen(
	onBack: () -> Unit,
	initialCategory: ReferenceCategory? = null,
	initialQuery: String = "",
	initialDetailCategory: String? = null,
	initialDetailSlug: String? = null,
	viewModel: ReferenceViewModel = hiltViewModel()
) {
	LaunchedEffect(initialCategory, initialQuery, initialDetailCategory, initialDetailSlug) {
		viewModel.initializeFromNavigation(
			category = initialCategory,
			query = initialQuery,
			detailCategory = initialDetailCategory,
			detailSlug = initialDetailSlug
		)
	}

	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val contentState = rememberReferenceContentState(uiState)
	val categoryListStates = rememberReferenceCategoryListStates()
	val snackbarHostState = remember { SnackbarHostState() }
	val retryActionLabel = stringResource(R.string.retry_action)

	LaunchedEffect(uiState.error) {
		uiState.error?.let { errorMessage ->
			val result = snackbarHostState.showSnackbar(
				message = errorMessage,
				actionLabel = if (uiState.onRetry != null) retryActionLabel else null,
				duration = SnackbarDuration.Long
			)
			if (result == SnackbarResult.ActionPerformed) {
				uiState.onRetry?.invoke()
			}
			viewModel.clearError()
		}
	}

	uiState.selectedReferenceDetail?.let { detail ->
		// Detail views temporarily take over the screen instead of nesting inside the tab scaffold so
		// deep links and in-app detail opens share one consistent back path.
		GenericReferenceDetailView(
			detail = detail,
			onBack = viewModel::clearReferenceDetail
		)
		return
	}

	Scaffold(
		snackbarHost = { SnackbarHost(snackbarHostState) },
		topBar = {
			TopAppBar(
				title = { Text(stringResource(R.string.reference_screen_title)) },
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							Icons.AutoMirrored.Filled.ArrowBack,
							stringResource(R.string.back_button)
						)
					}
				}
			)
		}
	) { padding ->
		Column(
			modifier = Modifier
				.padding(padding)
				.fillMaxSize()
		) {
			CategoryTabs(
				selectedCategory = uiState.selectedCategory,
				favoriteCounts = contentState.favoriteCounts,
				onCategorySelected = { viewModel.selectCategory(it) }
			)

			SearchBar(
				query = uiState.searchQuery,
				onQueryChange = { viewModel.search(it) },
				onClear = { viewModel.clearSearch() }
			)

			ReferenceSearchStatusRow(
				searchQuery = uiState.searchQuery,
				appliedSearchQuery = uiState.appliedSearchQuery,
				isSearchPending = uiState.isSearchPending,
				visibleResultsCount = contentState.selectedVisibleResultsCount
			)

			if (uiState.selectedCategory.supportsFavoritesFilter()) {
				FavoritesFilterRow(
					showFavoritesOnly = uiState.showFavoritesOnly,
					favoritesCount = contentState.selectedFavoritesCount,
					onToggleFavoritesOnly = viewModel::toggleFavoritesOnly
				)
			}

			Box(
				modifier = Modifier
					.fillMaxWidth()
					.weight(1f)
			) {
				ReferenceCategoryContent(
					uiState = uiState,
					contentState = contentState,
					categoryListStates = categoryListStates,
					viewModel = viewModel
				)
			}
		}
	}
}

@Composable
private fun rememberReferenceCategoryListStates(): Map<ReferenceCategory, LazyListState> {
	val trapsListState = rememberLazyListState()
	val poisonsListState = rememberLazyListState()
	val diseasesListState = rememberLazyListState()
	val spellcastingListState = rememberLazyListState()
	val objectsListState = rememberLazyListState()
	val hysteriaListState = rememberLazyListState()
	val monstersListState = rememberLazyListState()
	val coreRulesListState = rememberLazyListState()
	val characterCreationListState = rememberLazyListState()

	return remember(
		trapsListState,
		poisonsListState,
		diseasesListState,
		spellcastingListState,
		objectsListState,
		hysteriaListState,
		monstersListState,
		coreRulesListState,
		characterCreationListState
	) {
		// Keep a dedicated list state per category so switching tabs preserves each section's scroll
		// position instead of snapping every category back to the top.
		mapOf(
			ReferenceCategory.TRAPS to trapsListState,
			ReferenceCategory.POISONS to poisonsListState,
			ReferenceCategory.DISEASES to diseasesListState,
			ReferenceCategory.SPELLCASTING to spellcastingListState,
			ReferenceCategory.OBJECTS to objectsListState,
			ReferenceCategory.HYSTERIA to hysteriaListState,
			ReferenceCategory.MONSTERS to monstersListState,
			ReferenceCategory.CORE_RULES to coreRulesListState,
			ReferenceCategory.CHARACTER_CREATION to characterCreationListState
		)
	}
}

private data class ReferenceContentState(
	val favoriteCounts: Map<ReferenceCategory, Int>,
	val selectedFavoritesCount: Int,
	val sortedTraps: List<TrapTemplate>,
	val sortedPoisons: List<PoisonTemplate>,
	val sortedDiseases: List<DiseaseTemplate>,
	val selectedVisibleResultsCount: Int?
)

@Composable
private fun rememberReferenceContentState(uiState: ReferenceUiState): ReferenceContentState {
	return remember(
		uiState.selectedCategory,
		uiState.favoriteTrapNames,
		uiState.favoritePoisonNames,
		uiState.favoriteDiseaseNames,
		uiState.filteredTraps,
		uiState.filteredPoisons,
		uiState.filteredDiseases,
		uiState.filteredMonsters
	) {
		val favoriteCounts = mapOf(
			ReferenceCategory.TRAPS to uiState.favoriteTrapNames.size,
			ReferenceCategory.POISONS to uiState.favoritePoisonNames.size,
			ReferenceCategory.DISEASES to uiState.favoriteDiseaseNames.size
		)

		ReferenceContentState(
			favoriteCounts = favoriteCounts,
			selectedFavoritesCount = favoriteCounts[uiState.selectedCategory] ?: 0,
			sortedTraps = uiState.filteredTraps.sortedByDescending { it.name in uiState.favoriteTrapNames },
			sortedPoisons = uiState.filteredPoisons.sortedByDescending { it.name in uiState.favoritePoisonNames },
			sortedDiseases = uiState.filteredDiseases.sortedByDescending { it.name in uiState.favoriteDiseaseNames },
			selectedVisibleResultsCount = when (uiState.selectedCategory) {
				ReferenceCategory.TRAPS -> uiState.filteredTraps.size
				ReferenceCategory.POISONS -> uiState.filteredPoisons.size
				ReferenceCategory.DISEASES -> uiState.filteredDiseases.size
				ReferenceCategory.MONSTERS -> uiState.filteredMonsters.size
				else -> null
			}
		)
	}
}

@Composable
private fun ReferenceCategoryContent(
	uiState: ReferenceUiState,
	contentState: ReferenceContentState,
	categoryListStates: Map<ReferenceCategory, LazyListState>,
	viewModel: ReferenceViewModel
) {
	val categoryListState = categoryListStates.getValue(uiState.selectedCategory)
	when (uiState.selectedCategory) {
		ReferenceCategory.TRAPS -> TrapsContent(
			traps = contentState.sortedTraps,
			selectedTrap = uiState.selectedTrap,
			showFavoritesOnly = uiState.showFavoritesOnly,
			searchQuery = uiState.appliedSearchQuery,
			listState = categoryListState,
			favoriteTrapNames = uiState.favoriteTrapNames,
			onTrapSelected = viewModel::selectTrap,
			onToggleFavorite = viewModel::toggleTrapFavorite
		)
		ReferenceCategory.POISONS -> PoisonsContent(
			poisons = contentState.sortedPoisons,
			selectedPoison = uiState.selectedPoison,
			showFavoritesOnly = uiState.showFavoritesOnly,
			searchQuery = uiState.appliedSearchQuery,
			listState = categoryListState,
			favoritePoisonNames = uiState.favoritePoisonNames,
			onPoisonSelected = viewModel::selectPoison,
			onToggleFavorite = viewModel::togglePoisonFavorite
		)

		ReferenceCategory.DISEASES -> DiseasesContent(
			diseases = contentState.sortedDiseases,
			selectedDisease = uiState.selectedDisease,
			showFavoritesOnly = uiState.showFavoritesOnly,
			searchQuery = uiState.appliedSearchQuery,
			listState = categoryListState,
			favoriteDiseaseNames = uiState.favoriteDiseaseNames,
			onDiseaseSelected = viewModel::selectDisease,
			onToggleFavorite = viewModel::toggleDiseaseFavorite
		)

		ReferenceCategory.SPELLCASTING -> SpellcastingContent(
			uiState = uiState.copy(searchQuery = uiState.appliedSearchQuery),
			listState = categoryListState,
			onSpellcasterTypeChange = viewModel::updateSelectedSpellcasterType,
			onAbilityModifierChange = viewModel::updateSpellcastingAbilityModifierInput,
			onProficiencyBonusChange = viewModel::updateSpellcastingProficiencyBonusInput,
			onSpellBonusChange = viewModel::updateSpellcastingBonusInput,
			onCasterLevelChange = viewModel::updateSpellcastingCasterLevelInput,
			onOpenSpellDetail = { spellName ->
				viewModel.openReferenceDetail(
					ReferenceDetailResolver.CATEGORY_SPELLS,
					ReferenceDetailResolver.slugFor(spellName)
				)
			}
		)

		ReferenceCategory.OBJECTS -> ObjectsContent(listState = categoryListState)
		ReferenceCategory.HYSTERIA -> HysteriaContent(
			selectedDuration = uiState.selectedHysteriaDuration,
			lastRoll = uiState.hysteriaLastRoll,
			lastResult = uiState.hysteriaLastResult,
			listState = categoryListState,
			onDurationSelected = viewModel::selectHysteriaDuration,
			onRoll = viewModel::rollHysteria
		)

		ReferenceCategory.MONSTERS -> MonstersContent(
			monsters = uiState.filteredMonsters,
			selectedGroup = uiState.selectedMonsterGroup,
			selectedCreatureType = uiState.selectedMonsterCreatureType,
			selectedChallengeRating = uiState.selectedMonsterChallengeRating,
			listState = categoryListState,
			onGroupSelected = viewModel::updateMonsterGroupFilter,
			onCreatureTypeSelected = viewModel::updateMonsterCreatureTypeFilter,
			onChallengeRatingSelected = viewModel::updateMonsterChallengeRatingFilter,
			onOpenDetail = viewModel::openReferenceDetail
		)
		ReferenceCategory.CORE_RULES -> CoreRulesContent(
			searchQuery = uiState.appliedSearchQuery,
			listState = categoryListState
		)

		ReferenceCategory.CHARACTER_CREATION -> CharacterCreationContent(
			searchQuery = uiState.appliedSearchQuery,
			listState = categoryListState,
			onOpenDetail = viewModel::openReferenceDetail
		)
	}
}

// Trap, poison, and disease content extracted to ReferenceScreenHazards.kt.

// Objects content extracted to ReferenceScreenObjects.kt.

// Hysteria content extracted to ReferenceScreenHysteria.kt.
// Spellcasting content extracted to ReferenceScreenSpellcasting.kt.

