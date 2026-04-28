/*
 * FILE: ReferenceScreen.kt
 *
 * Fifth-edition reference screen with tabs and search.
 *
 * TABLE OF CONTENTS:
 * 1. Screen entry point and category/list state setup
 * 2. Remembered derived content state
 * 3. Category content routing
 * 4. Monster availability placeholder helpers
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
import io.github.velyene.loreweaver.domain.util.MonsterReferenceEntry
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

	uiState.selectedReferenceDetail?.let { detail ->
		GenericReferenceDetailView(
			detail = detail,
			onBack = viewModel::clearReferenceDetail
		)
		return
	}

	Scaffold(
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
	val madnessListState = rememberLazyListState()
	val monstersListState = rememberLazyListState()
	val coreRulesListState = rememberLazyListState()
	val characterCreationListState = rememberLazyListState()

	return remember(
		trapsListState,
		poisonsListState,
		diseasesListState,
		spellcastingListState,
		objectsListState,
		madnessListState,
		monstersListState,
		coreRulesListState,
		characterCreationListState
	) {
		mapOf(
			ReferenceCategory.TRAPS to trapsListState,
			ReferenceCategory.POISONS to poisonsListState,
			ReferenceCategory.DISEASES to diseasesListState,
			ReferenceCategory.SPELLCASTING to spellcastingListState,
			ReferenceCategory.OBJECTS to objectsListState,
			ReferenceCategory.MADNESS to madnessListState,
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
		uiState.filteredDiseases
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
		ReferenceCategory.MADNESS -> MadnessContent(
			selectedDuration = uiState.selectedMadnessDuration,
			lastRoll = uiState.madnessLastRoll,
			lastResult = uiState.madnessLastResult,
			listState = categoryListState,
			onDurationSelected = viewModel::selectMadnessDuration,
			onRoll = viewModel::rollMadness
		)

		ReferenceCategory.MONSTERS -> MonstersContent(searchQuery = uiState.appliedSearchQuery)
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

// Madness content extracted to ReferenceScreenMadness.kt.
// Spellcasting content extracted to ReferenceScreenSpellcasting.kt.

@Composable
private fun MonstersContent(searchQuery: String) {
	ReferenceCenteredMessage(
		message = if (searchQuery.isBlank()) {
			stringResource(R.string.reference_monster_unavailable)
		} else {
			stringResource(R.string.reference_monster_search_unavailable)
		}
	)
}


internal fun filterMonsterEntries(): List<MonsterReferenceEntry> {
	return emptyList()
}
