/*
 * FILE: ReferenceUiStateTransforms.kt
 *
 * TABLE OF CONTENTS:
 * 1. Error/loading state helpers
 * 2. Category/navigation transforms
 * 3. Reference filtering helpers
 * 4. Query and favorites utility extensions
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.util.DiseaseReference
import io.github.velyene.loreweaver.domain.util.DiseaseTemplate
import io.github.velyene.loreweaver.domain.util.MonsterReferenceCatalog
import io.github.velyene.loreweaver.domain.util.PoisonReference
import io.github.velyene.loreweaver.domain.util.PoisonTemplate
import io.github.velyene.loreweaver.domain.util.TrapReference
import io.github.velyene.loreweaver.domain.util.TrapTemplate

internal fun ReferenceUiState.beginLoading(): ReferenceUiState = copy(
	isLoading = true,
	error = null,
	onRetry = null
)

internal fun ReferenceUiState.withError(
	message: String,
	onRetry: (() -> Unit)? = null
): ReferenceUiState = copy(
	isLoading = false,
	error = message,
	onRetry = onRetry
)

internal fun ReferenceUiState.clearErrorState(): ReferenceUiState = copy(error = null, onRetry = null)

internal fun ReferenceUiState.forCategory(category: ReferenceCategory): ReferenceUiState {
	return forNavigation(category = category, query = "")
}

internal fun ReferenceUiState.forNavigation(
	category: ReferenceCategory,
	query: String
): ReferenceUiState {
	return copy(
		selectedCategory = category,
		searchQuery = query,
		appliedSearchQuery = query,
		isSearchPending = false,
		showFavoritesOnly = category.supportsFavoritesFilter() && showFavoritesOnly
	).clearSelectedDetails()
}

internal fun ReferenceUiState.clearSelectedDetails(): ReferenceUiState {
	return copy(
		selectedTrap = null,
		selectedPoison = null,
		selectedDisease = null,
		selectedReferenceDetail = null
	)
}

internal fun ReferenceUiState.clearHysteriaResult(): ReferenceUiState {
	return copy(
		hysteriaLastRoll = null,
		hysteriaLastResult = null
	)
}

internal fun ReferenceUiState.withFilteredContent(query: String): ReferenceUiState {
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

internal fun matchesTrapQuery(trap: TrapTemplate, query: String): Boolean {
	return TrapReference.matchesSearchQuery(trap, query)
}

internal fun matchesPoisonQuery(poison: PoisonTemplate, query: String): Boolean {
	return PoisonReference.matchesSearchQuery(poison, query)
}

internal fun matchesDiseaseQuery(disease: DiseaseTemplate, query: String): Boolean {
	return disease.name.contains(query, ignoreCase = true) ||
		disease.description.contains(query, ignoreCase = true)
}

internal fun ReferenceCategory.supportsFavoritesFilter(): Boolean {
	return this == ReferenceCategory.TRAPS ||
		this == ReferenceCategory.POISONS ||
		this == ReferenceCategory.DISEASES
}

internal inline fun <T> List<T>.filterByQuery(
	query: String,
	matches: (T, String) -> Boolean
): List<T> {
	return if (query.isBlank()) this else filter { item -> matches(item, query) }
}

internal inline fun <T> List<T>.filterFavoritesOnly(
	showFavoritesOnly: Boolean,
	predicate: (T) -> Boolean
): List<T> {
	return if (showFavoritesOnly) filter(predicate) else this
}

