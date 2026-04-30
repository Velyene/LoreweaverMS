/*
 * FILE: ReferenceSearchController.kt
 *
 * TABLE OF CONTENTS:
 * 1. Search controller
 * 2. Navigation state helpers
 * */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

internal class ReferenceSearchController(
	private val uiState: MutableStateFlow<ReferenceUiState>,
	private val scope: CoroutineScope
) {
	private companion object {
		val SEARCH_DEBOUNCE_MS = 250.milliseconds
	}

	private var lastNavigationInitializationKey: String? = null
	private var searchJob: Job? = null

	fun selectCategory(category: ReferenceCategory) {
		cancelPendingSearch()
		uiState.update { it.forCategory(category) }
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
		val key = navigationInitializationKey(
			category = category,
			query = normalizedQuery,
			detailCategory = normalizedDetailCategory,
			detailSlug = normalizedDetailSlug
		)

		if (lastNavigationInitializationKey == key) return
		lastNavigationInitializationKey = key

		uiState.update {
			it.forNavigation(category = category ?: it.selectedCategory, query = normalizedQuery)
				.copy(
					selectedReferenceDetail = resolveReferenceDetail(
						category = normalizedDetailCategory,
						slug = normalizedDetailSlug
					)
				)
		}
		cancelPendingSearch()
		applySearchImmediately(normalizedQuery)
	}

	fun search(query: String) {
		cancelPendingSearch()
		uiState.update {
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

		searchJob = scope.launch {
			delay(SEARCH_DEBOUNCE_MS)
			applySearchImmediately(query)
		}
	}

	fun clearSearch() {
		search("")
	}

	fun cancelPendingSearch() {
		searchJob?.cancel()
		searchJob = null
	}

	fun reapplyAppliedSearch() {
		applySearchImmediately(uiState.value.appliedSearchQuery)
	}

	private fun applySearchImmediately(query: String) {
		uiState.update {
			it.copy(appliedSearchQuery = query, isSearchPending = false).withFilteredContent(query)
		}
	}
}

internal fun navigationInitializationKey(
	category: ReferenceCategory?,
	query: String,
	detailCategory: String?,
	detailSlug: String?
): String {
	return listOf(
		category?.name.orEmpty(),
		query,
		detailCategory.orEmpty(),
		detailSlug.orEmpty()
	).joinToString("|")
}

private fun resolveReferenceDetail(
	category: String?,
	slug: String?
) = category?.let { resolvedCategory ->
	slug?.let { resolvedSlug ->
		ReferenceDetailResolver.resolve(resolvedCategory, resolvedSlug)
	}
}


