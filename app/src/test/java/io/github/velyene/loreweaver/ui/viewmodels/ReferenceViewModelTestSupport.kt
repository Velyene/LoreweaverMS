package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.repository.ReferencePreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

internal fun createViewModel(
	repository: TestReferencePreferencesRepository = TestReferencePreferencesRepository()
): ReferenceViewModel = ReferenceViewModel(repository)

internal fun <T> assertFilteredLabels(
	expected: List<String>,
	actual: List<T>,
	label: (T) -> String
) {
	assertEquals(expected, actual.map(label))
}

internal fun <T> assertContainsFilteredLabels(
	actual: List<T>,
	label: (T) -> String,
	vararg expected: String
) {
	val labels = actual.map(label)
	expected.forEach { expectedLabel ->
		assertTrue("Expected filtered results to contain: $expectedLabel", expectedLabel in labels)
	}
}

internal fun <T> assertAllFiltered(
	actual: List<T>,
	message: String,
	predicate: (T) -> Boolean
) {
	assertTrue(message, actual.all(predicate))
}

internal class TestReferencePreferencesRepository : ReferencePreferencesRepository {
	private val _favoriteTrapNames = MutableStateFlow(emptySet<String>())
	private val _favoritePoisonNames = MutableStateFlow(emptySet<String>())
	private val _favoriteDiseaseNames = MutableStateFlow(emptySet<String>())

	override val favoriteTrapNames: StateFlow<Set<String>> = _favoriteTrapNames
	override val favoritePoisonNames: StateFlow<Set<String>> = _favoritePoisonNames
	override val favoriteDiseaseNames: StateFlow<Set<String>> = _favoriteDiseaseNames

	override suspend fun toggleTrapFavorite(name: String) {
		_favoriteTrapNames.value = _favoriteTrapNames.value.toggle(name)
	}

	override suspend fun togglePoisonFavorite(name: String) {
		_favoritePoisonNames.value = _favoritePoisonNames.value.toggle(name)
	}

	override suspend fun toggleDiseaseFavorite(name: String) {
		_favoriteDiseaseNames.value = _favoriteDiseaseNames.value.toggle(name)
	}

	fun setTrapFavorites(names: Set<String>) {
		_favoriteTrapNames.value = names
	}

	private fun Set<String>.toggle(name: String): Set<String> {
		return if (name in this) this - name else this + name
	}
}

