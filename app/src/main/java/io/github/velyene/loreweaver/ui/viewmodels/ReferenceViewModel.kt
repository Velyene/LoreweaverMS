/*
 * FILE: ReferenceViewModel.kt
 *
 * TABLE OF CONTENTS:
 * 1. Reference models (ReferenceCategory, ReferenceUiState)
 * 2. ViewModel setup and public state
 * 3. Navigation and debounced search orchestration
 * 4. Detail, favorites, madness, and spellcasting input actions
 * 5. Favorites observation and small coroutine helpers
 * 6. ReferenceUiState transformation and filtering helpers
 * 7. Input normalization helpers
 */

package io.github.velyene.loreweaver.ui.viewmodels

import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.velyene.loreweaver.domain.repository.ReferencePreferencesRepository
import io.github.velyene.loreweaver.domain.util.DiseaseReference
import io.github.velyene.loreweaver.domain.util.DiseaseTemplate
import io.github.velyene.loreweaver.domain.util.MadnessDuration
import io.github.velyene.loreweaver.domain.util.MadnessReference
import io.github.velyene.loreweaver.domain.util.MonsterReferenceCatalog
import io.github.velyene.loreweaver.domain.util.MonsterReferenceEntry
import io.github.velyene.loreweaver.domain.util.PoisonReference
import io.github.velyene.loreweaver.domain.util.PoisonTemplate
import io.github.velyene.loreweaver.domain.util.ReferenceDetailContent
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver
import io.github.velyene.loreweaver.domain.util.SpellcasterType
import io.github.velyene.loreweaver.domain.util.TrapReference
import io.github.velyene.loreweaver.domain.util.TrapTemplate
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

/**
 * Reference categories
 */
@Keep
@Serializable
enum class ReferenceCategory {
	TRAPS,
	POISONS,
	DISEASES,
	SPELLCASTING,
	OBJECTS,
	MADNESS,
	MONSTERS,
	CORE_RULES,
	CHARACTER_CREATION
}

/**
 * UI state for reference screen
 */
data class ReferenceUiState(
	val selectedCategory: ReferenceCategory = ReferenceCategory.TRAPS,
	val searchQuery: String = "",
	val appliedSearchQuery: String = "",
	val isSearchPending: Boolean = false,
	val filteredTraps: List<TrapTemplate> = TrapReference.TEMPLATES,
	val filteredPoisons: List<PoisonTemplate> = PoisonReference.TEMPLATES,
	val filteredDiseases: List<DiseaseTemplate> = DiseaseReference.TEMPLATES,
	val filteredMonsters: List<MonsterReferenceEntry> = MonsterReferenceCatalog.ALL,
	val showFavoritesOnly: Boolean = false,
	val selectedMonsterGroup: String? = null,
	val selectedMonsterCreatureType: String? = null,
	val selectedMonsterChallengeRating: String? = null,
	val favoriteTrapNames: Set<String> = emptySet(),
	val favoritePoisonNames: Set<String> = emptySet(),
	val favoriteDiseaseNames: Set<String> = emptySet(),
	val selectedSpellcasterType: SpellcasterType = SpellcasterType.WIZARD,
	val spellcastingAbilityModifierInput: String = "4",
	val spellcastingProficiencyBonusInput: String = "4",
	val spellcastingBonusInput: String = "0",
	val spellcastingCasterLevelInput: String = "5",
	val selectedTrap: TrapTemplate? = null,
	val selectedPoison: PoisonTemplate? = null,
	val selectedDisease: DiseaseTemplate? = null,
	val selectedReferenceDetail: ReferenceDetailContent? = null,
	val selectedMadnessDuration: MadnessDuration = MadnessDuration.SHORT_TERM,
	val madnessLastRoll: Int? = null,
	val madnessLastResult: String? = null
)

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

	/**
	 * Change selected category
	 */
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
		// Navigation state can be re-emitted as the back stack is restored or recomposed.
		// This key prevents those duplicate emissions from resetting the same search/detail state.
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

	/**
	 * Update search query
	 */
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
			// Clearing search should feel immediate and should also restore the full local dataset.
			applySearchImmediately(query)
			return
		}

		searchJob = viewModelScope.launch {
			// Non-blank searches are debounced so rapid typing does not repeatedly rebuild filters.
			delay(SEARCH_DEBOUNCE_MS)
			applySearchImmediately(query)
		}
	}

	/**
	 * Apply search filter to current category
	 */
	private fun applySearchImmediately(query: String) {
		_uiState.update {
			it.copy(appliedSearchQuery = query, isSearchPending = false).withFilteredContent(query)
		}
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

	/**
	 * Select madness duration type
	 */
	fun selectMadnessDuration(duration: MadnessDuration) {
		_uiState.update { it.copy(selectedMadnessDuration = duration).clearMadnessResult() }
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

	fun rollMadness() {
		val roll = Random.nextInt(1, 101)
		val result =
			madnessResultFor(duration = _uiState.value.selectedMadnessDuration, roll = roll)
		_uiState.update { it.copy(madnessLastRoll = roll, madnessLastResult = result) }
	}

	/**
	 * Clear search
	 */
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
				// Reapply the active query so favorites-only mode stays in sync with preference updates.
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

	private fun ReferenceUiState.forCategory(category: ReferenceCategory): ReferenceUiState {
		return forNavigation(category = category, query = "")
	}

	private fun ReferenceUiState.forNavigation(
		category: ReferenceCategory,
		query: String
	): ReferenceUiState {
		return copy(
			selectedCategory = category,
			searchQuery = query,
			appliedSearchQuery = query,
			isSearchPending = false,
			// Favorites-only survives category switches only on sections that actually expose stored
			// favorites; other tabs reset to their normal full-content view.
			showFavoritesOnly = category.supportsFavoritesFilter() && showFavoritesOnly
		).clearSelectedDetails()
	}

	private fun ReferenceUiState.clearSelectedDetails(): ReferenceUiState {
		return copy(
			selectedTrap = null,
			selectedPoison = null,
			selectedDisease = null,
			selectedReferenceDetail = null
		)
	}

	private fun ReferenceUiState.clearMadnessResult(): ReferenceUiState {
		return copy(
			madnessLastRoll = null,
			madnessLastResult = null
		)
	}

	private fun ReferenceUiState.withFilteredContent(query: String): ReferenceUiState {
		// Only the hazard-style categories maintain filtered lists in ViewModel state.
		// The other reference tabs read the query directly and derive their visible content in UI helpers.
		return when (selectedCategory) {
			ReferenceCategory.TRAPS -> copy(
				filteredTraps = TrapReference.TEMPLATES
					.filterByQuery(query, ::matchesTrapQuery)
					.filterFavoritesOnly(showFavoritesOnly) { trap -> trap.name in favoriteTrapNames }
			)

			ReferenceCategory.POISONS -> copy(
				filteredPoisons = PoisonReference.TEMPLATES
					.filterByQuery(query, ::matchesPoisonQuery)
					.filterFavoritesOnly(showFavoritesOnly) { poison -> poison.name in favoritePoisonNames }
			)

			ReferenceCategory.DISEASES -> copy(
				filteredDiseases = DiseaseReference.TEMPLATES
					.filterByQuery(query, ::matchesDiseaseQuery)
					.filterFavoritesOnly(showFavoritesOnly) { disease -> disease.name in favoriteDiseaseNames }
			)

			ReferenceCategory.MONSTERS -> copy(
				filteredMonsters = MonsterReferenceCatalog.filter(
					query = query,
					group = selectedMonsterGroup,
					creatureType = selectedMonsterCreatureType,
					challengeRating = selectedMonsterChallengeRating
				)
			)

			else -> this
		}
	}

	private fun matchesTrapQuery(trap: TrapTemplate, query: String): Boolean {
		return TrapReference.matchesSearchQuery(trap, query)
	}

	private fun matchesPoisonQuery(poison: PoisonTemplate, query: String): Boolean {
		return PoisonReference.matchesSearchQuery(poison, query)
	}

	private fun matchesDiseaseQuery(disease: DiseaseTemplate, query: String): Boolean {
		return disease.name.contains(query, ignoreCase = true) ||
			disease.description.contains(query, ignoreCase = true)
	}

	private fun madnessResultFor(duration: MadnessDuration, roll: Int): String? {
		return when (duration) {
			MadnessDuration.SHORT_TERM -> MadnessReference.getShortTermEffect(roll)?.effect
			MadnessDuration.LONG_TERM -> MadnessReference.getLongTermEffect(roll)?.effect
			MadnessDuration.INDEFINITE -> MadnessReference.getIndefiniteFlaw(roll)
		}
	}

	private fun ReferenceCategory.supportsFavoritesFilter(): Boolean {
		return this == ReferenceCategory.TRAPS ||
			this == ReferenceCategory.POISONS ||
			this == ReferenceCategory.DISEASES
	}

	private inline fun <T> List<T>.filterByQuery(
		query: String,
		matches: (T, String) -> Boolean
	): List<T> {
		return if (query.isBlank()) this else filter { item -> matches(item, query) }
	}

	private inline fun <T> List<T>.filterFavoritesOnly(
		showFavoritesOnly: Boolean,
		predicate: (T) -> Boolean
	): List<T> {
		return if (showFavoritesOnly) filter(predicate) else this
	}

	private fun normalizeSignedIntegerInput(value: String): String {
		if (value.isEmpty()) return ""
		if (value == "-") return value

		val filtered = buildString {
			value.forEachIndexed { index, c ->
				if (c.isDigit() || (c == '-' && index == 0)) append(c)
			}
		}

		return filtered.take(4)
	}

	private fun normalizePositiveIntegerInput(value: String): String {
		return value.filter { it.isDigit() }.take(2)
	}
}
