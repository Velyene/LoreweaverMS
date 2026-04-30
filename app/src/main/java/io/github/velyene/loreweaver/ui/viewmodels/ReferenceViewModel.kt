/*
 * FILE: ReferenceViewModel.kt
 *
 * TABLE OF CONTENTS:
 * 1. ViewModel setup and public state
 * 2. Search/navigation delegation
 * 3. Detail, favorites, hysteria, and spellcasting input actions
 */

package io.github.velyene.loreweaver.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.velyene.loreweaver.domain.repository.ReferencePreferencesRepository
import io.github.velyene.loreweaver.domain.util.DiseaseTemplate
import io.github.velyene.loreweaver.domain.util.HysteriaDuration
import io.github.velyene.loreweaver.domain.util.PoisonTemplate
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver
import io.github.velyene.loreweaver.domain.util.SpellcasterType
import io.github.velyene.loreweaver.domain.util.TrapTemplate
import io.github.velyene.loreweaver.ui.util.AppText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class ReferenceViewModel @Inject constructor(
	private val referencePreferencesRepository: ReferencePreferencesRepository,
	private val appText: AppText
) : ViewModel() {
	private val _uiState = MutableStateFlow(ReferenceUiState())
	val uiState: StateFlow<ReferenceUiState> = _uiState.asStateFlow()
	private val searchController = ReferenceSearchController(
		uiState = _uiState,
		scope = viewModelScope
	)
	private val preferenceSideEffects = ReferencePreferenceSideEffects(
		referencePreferencesRepository = referencePreferencesRepository,
		appText = appText,
		uiState = _uiState,
		scope = viewModelScope,
		reapplyAppliedSearch = searchController::reapplyAppliedSearch
	)

	init {
		preferenceSideEffects.observeFavorites()
	}

	fun clearError() {
		_uiState.update { it.clearErrorState() }
	}

	/**
	 * Change selected category
	 */
	fun selectCategory(category: ReferenceCategory) {
		searchController.selectCategory(category)
	}

	fun initializeFromNavigation(
		category: ReferenceCategory?,
		query: String,
		detailCategory: String?,
		detailSlug: String?
	) {
		searchController.initializeFromNavigation(
			category = category,
			query = query,
			detailCategory = detailCategory,
			detailSlug = detailSlug
		)
	}

	/**
	 * Update search query
	 */
	fun search(query: String) {
		searchController.search(query)
	}

	/**
	 * Select trap for detail view
	 */
	fun selectTrap(trap: TrapTemplate?) {
		_uiState.update { it.copy(selectedTrap = trap) }
	}

	/**
	 * Select poison for detail view
	 */
	fun selectPoison(poison: PoisonTemplate?) {
		_uiState.update { it.copy(selectedPoison = poison) }
	}

	/**
	 * Select disease for detail view
	 */
	fun selectDisease(disease: DiseaseTemplate?) {
		_uiState.update { it.copy(selectedDisease = disease) }
	}

	fun openReferenceDetail(category: String, slug: String) {
		_uiState.update {
			it.copy(selectedReferenceDetail = ReferenceDetailResolver.resolve(category, slug))
		}
	}

	fun clearReferenceDetail() {
		_uiState.update { it.copy(selectedReferenceDetail = null) }
	}

	fun toggleTrapFavorite(trapName: String) {
		preferenceSideEffects.toggleTrapFavorite(trapName)
	}

	fun togglePoisonFavorite(poisonName: String) {
		preferenceSideEffects.togglePoisonFavorite(poisonName)
	}

	fun toggleDiseaseFavorite(diseaseName: String) {
		preferenceSideEffects.toggleDiseaseFavorite(diseaseName)
	}

	fun toggleFavoritesOnly() {
		searchController.cancelPendingSearch()
		_uiState.update {
			it.copy(
				showFavoritesOnly = !it.showFavoritesOnly,
				selectedReferenceDetail = null
			)
		}
		searchController.reapplyAppliedSearch()
	}

	fun updateMonsterCreatureTypeFilter(creatureType: String?) {
		searchController.cancelPendingSearch()
		_uiState.update {
			it.copy(selectedMonsterCreatureType = creatureType, selectedReferenceDetail = null)
		}
		searchController.reapplyAppliedSearch()
	}

	fun updateMonsterGroupFilter(group: String?) {
		searchController.cancelPendingSearch()
		_uiState.update {
			it.copy(selectedMonsterGroup = group, selectedReferenceDetail = null)
		}
		searchController.reapplyAppliedSearch()
	}

	fun updateMonsterChallengeRatingFilter(challengeRating: String?) {
		searchController.cancelPendingSearch()
		_uiState.update {
			it.copy(selectedMonsterChallengeRating = challengeRating, selectedReferenceDetail = null)
		}
		searchController.reapplyAppliedSearch()
	}

	/**
	 * Select hysteria duration type
	 */
	fun selectHysteriaDuration(duration: HysteriaDuration) {
		_uiState.update { it.copy(selectedHysteriaDuration = duration).clearHysteriaResult() }
	}

	fun updateSpellcastingAbilityModifierInput(value: String) {
		updateSpellcastingInput {
			it.copy(
				spellcastingAbilityModifierInput = normalizeSignedIntegerInput(
					value
				)
			)
		}
	}

	fun updateSelectedSpellcasterType(type: SpellcasterType) {
		updateSpellcastingInput { it.copy(selectedSpellcasterType = type) }
	}

	fun updateSpellcastingProficiencyBonusInput(value: String) {
		updateSpellcastingInput {
			it.copy(
				spellcastingProficiencyBonusInput = normalizePositiveIntegerInput(
					value
				)
			)
		}
	}

	fun updateSpellcastingBonusInput(value: String) {
		updateSpellcastingInput { it.copy(spellcastingBonusInput = normalizeSignedIntegerInput(value)) }
	}

	fun updateSpellcastingCasterLevelInput(value: String) {
		updateSpellcastingInput {
			it.copy(
				spellcastingCasterLevelInput = normalizePositiveIntegerInput(
					value
				)
			)
		}
	}

	fun rollHysteria() {
		val roll = Random.nextInt(1, 101)
		val result =
			hysteriaResultFor(duration = _uiState.value.selectedHysteriaDuration, roll = roll)
		_uiState.update { it.copy(hysteriaLastRoll = roll, hysteriaLastResult = result) }
	}

	/**
	 * Clear search
	 */
	fun clearSearch() {
		searchController.clearSearch()
	}

	private fun updateSpellcastingInput(update: (ReferenceUiState) -> ReferenceUiState) {
		_uiState.update(update)
	}


}
