/*
 * FILE: ReferenceViewModel.kt
 *
 * TABLE OF CONTENTS:
 * 1. ViewModel setup and public state
 * 2. Navigation and debounced search orchestration
 * 3. Detail, favorites, madness, and spellcasting input actions
 * 4. Favorites observation and small coroutine helpers
 */

package io.github.velyene.loreweaver.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.velyene.loreweaver.domain.repository.ReferencePreferencesRepository
import io.github.velyene.loreweaver.domain.util.MadnessDuration
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver
import io.github.velyene.loreweaver.domain.util.SpellcasterType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ReferenceViewModel @Inject constructor(
	private val referencePreferencesRepository: ReferencePreferencesRepository
) : ViewModel() {
	private companion object {
		val SEARCH_DEBOUNCE_MS = 250.milliseconds
	}

	private val _uiState = MutableStateFlow(ReferenceUiState())
	val uiState: StateFlow<ReferenceUiState> = _uiState.asStateFlow()
	private var lastNavigationInitializationKey: String? = null
	private var searchJob: Job? = null

	init {
		observeFavorites()
	}

	fun selectCategory(category: ReferenceCategory) {
		cancelPendingSearch()
		_uiState.update { it.forCategory(category) }
		applySearchImmediately("")
	}

	fun initializeFromNavigation(
		category: ReferenceCategory?,
		query: String,
		detailCategory: String?,
		detailSlug: String?
	) {
		val normalizedQuery = query.trim()
		val normalizedDetailCategory = detailCategory?.takeIf { it.isNotBlank() }
		val normalizedDetailSlug = detailSlug?.takeIf { it.isNotBlank() }
		val key = listOf(
			category?.name.orEmpty(),
			normalizedQuery,
			normalizedDetailCategory.orEmpty(),
			normalizedDetailSlug.orEmpty()
		).joinToString("|")

		if (lastNavigationInitializationKey == key) return
		lastNavigationInitializationKey = key

		_uiState.update {
			it.forNavigation(category = category ?: it.selectedCategory, query = normalizedQuery)
				.copy(
					selectedReferenceDetail = normalizedDetailCategory?.let { resolvedCategory ->
						normalizedDetailSlug?.let { resolvedSlug ->
							ReferenceDetailResolver.resolve(resolvedCategory, resolvedSlug)
						}
					}
				)
		}
		cancelPendingSearch()
		applySearchImmediately(normalizedQuery)
	}

	fun search(query: String) {
		cancelPendingSearch()
		_uiState.update {
			it.copy(
				searchQuery = query,
				selectedReferenceDetail = null,
				isSearchPending = query.isNotBlank() && query != it.appliedSearchQuery
			)
		}

		if (query.isBlank()) {
			applySearchImmediately(query)
			return
		}

		searchJob = viewModelScope.launch {
			delay(SEARCH_DEBOUNCE_MS)
			applySearchImmediately(query)
		}
	}

	private fun applySearchImmediately(query: String) {
		_uiState.update {
			it.copy(appliedSearchQuery = query, isSearchPending = false).withFilteredContent(query)
		}
	}

	fun selectTrap(trap: io.github.velyene.loreweaver.domain.util.TrapTemplate?) {
		_uiState.update { it.copy(selectedTrap = trap) }
	}

	fun selectPoison(poison: io.github.velyene.loreweaver.domain.util.PoisonTemplate?) {
		_uiState.update { it.copy(selectedPoison = poison) }
	}

	fun selectDisease(disease: io.github.velyene.loreweaver.domain.util.DiseaseTemplate?) {
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
		launchPreferenceToggle { referencePreferencesRepository.toggleTrapFavorite(trapName) }
	}

	fun togglePoisonFavorite(poisonName: String) {
		launchPreferenceToggle { referencePreferencesRepository.togglePoisonFavorite(poisonName) }
	}

	fun toggleDiseaseFavorite(diseaseName: String) {
		launchPreferenceToggle { referencePreferencesRepository.toggleDiseaseFavorite(diseaseName) }
	}

	fun toggleFavoritesOnly() {
		cancelPendingSearch()
		_uiState.update {
			it.copy(
				showFavoritesOnly = !it.showFavoritesOnly,
				selectedReferenceDetail = null
			)
		}
		applySearchImmediately(_uiState.value.appliedSearchQuery)
	}

	fun updateMonsterCreatureTypeFilter(creatureType: String?) {
		cancelPendingSearch()
		_uiState.update {
			it.copy(selectedMonsterCreatureType = creatureType, selectedReferenceDetail = null)
		}
		applySearchImmediately(_uiState.value.appliedSearchQuery)
	}

	fun updateMonsterGroupFilter(group: String?) {
		cancelPendingSearch()
		_uiState.update {
			it.copy(selectedMonsterGroup = group, selectedReferenceDetail = null)
		}
		applySearchImmediately(_uiState.value.appliedSearchQuery)
	}

	fun updateMonsterChallengeRatingFilter(challengeRating: String?) {
		cancelPendingSearch()
		_uiState.update {
			it.copy(selectedMonsterChallengeRating = challengeRating, selectedReferenceDetail = null)
		}
		applySearchImmediately(_uiState.value.appliedSearchQuery)
	}

	fun selectMadnessDuration(duration: MadnessDuration) {
		_uiState.update { it.copy(selectedMadnessDuration = duration).clearMadnessResult() }
	}

	fun updateSpellcastingAbilityModifierInput(value: String) {
		updateSpellcastingInput {
			it.copy(spellcastingAbilityModifierInput = normalizeSignedIntegerInput(value))
		}
	}

	fun updateSelectedSpellcasterType(type: SpellcasterType) {
		updateSpellcastingInput { it.copy(selectedSpellcasterType = type) }
	}

	fun updateSpellcastingProficiencyBonusInput(value: String) {
		updateSpellcastingInput {
			it.copy(spellcastingProficiencyBonusInput = normalizePositiveIntegerInput(value))
		}
	}

	fun updateSpellcastingBonusInput(value: String) {
		updateSpellcastingInput { it.copy(spellcastingBonusInput = normalizeSignedIntegerInput(value)) }
	}

	fun updateSpellcastingCasterLevelInput(value: String) {
		updateSpellcastingInput {
			it.copy(spellcastingCasterLevelInput = normalizePositiveIntegerInput(value))
		}
	}

	fun rollMadness() {
		val roll = Random.nextInt(1, 101)
		val result = madnessResultFor(duration = _uiState.value.selectedMadnessDuration, roll = roll)
		_uiState.update { it.copy(madnessLastRoll = roll, madnessLastResult = result) }
	}

	fun clearSearch() {
		search("")
	}

	private fun observeFavorites() {
		viewModelScope.launch {
			combine(
				referencePreferencesRepository.favoriteTrapNames,
				referencePreferencesRepository.favoritePoisonNames,
				referencePreferencesRepository.favoriteDiseaseNames
			) { trapFavorites, poisonFavorites, diseaseFavorites ->
				Triple(trapFavorites, poisonFavorites, diseaseFavorites)
			}.collect { (trapFavorites, poisonFavorites, diseaseFavorites) ->
				_uiState.update {
					it.copy(
						favoriteTrapNames = trapFavorites,
						favoritePoisonNames = poisonFavorites,
						favoriteDiseaseNames = diseaseFavorites
					)
				}
				applySearchImmediately(_uiState.value.appliedSearchQuery)
			}
		}
	}

	private fun cancelPendingSearch() {
		searchJob?.cancel()
		searchJob = null
	}

	private fun launchPreferenceToggle(action: suspend () -> Unit) {
		viewModelScope.launch { action() }
	}

	private fun updateSpellcastingInput(update: (ReferenceUiState) -> ReferenceUiState) {
		_uiState.update(update)
	}
}
