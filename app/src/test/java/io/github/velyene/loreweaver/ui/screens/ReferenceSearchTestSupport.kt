package io.github.velyene.loreweaver.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

internal fun assertMatchesAll(matchesQuery: (String) -> Boolean, vararg queries: String) {
	queries.forEach { query ->
		assertTrue("Expected query to match: $query", matchesQuery(query))
	}
}

internal fun <T> assertQueryResults(
	expected: List<String>,
	query: String,
	search: (String) -> List<T>,
	label: (T) -> String
) {
	assertEquals(expected, search(query).map(label))
}

internal fun <T> assertNoQueryResults(query: String, search: (String) -> List<T>) {
	assertTrue(search(query).isEmpty())
}

internal fun <T> assertContainsLabels(
	items: List<T>,
	label: (T) -> String,
	vararg expected: String
) {
	val labels = items.map(label)
	expected.forEach { expectedLabel ->
		assertTrue("Expected results to contain: $expectedLabel", expectedLabel in labels)
	}
}

internal fun <T> assertAllMatch(
	items: List<T>,
	message: String,
	predicate: (T) -> Boolean
) {
	assertTrue(message, items.all(predicate))
}

internal fun <T> assertVisibleWhenBlankInSubsection(
	expected: List<String>,
	subsection: CharacterCreationSubsection,
	visible: (String, CharacterCreationSubsection) -> List<T>,
	label: (T) -> String
) {
	assertQueryResults(expected, "", { query -> visible(query, subsection) }, label)
}

internal fun <T> assertHiddenWhenBlankInSubsection(
	subsection: CharacterCreationSubsection,
	visible: (String, CharacterCreationSubsection) -> List<T>
) {
	assertNoQueryResults("") { query -> visible(query, subsection) }
}

internal fun <T> assertVisibleForSearch(
	expected: List<String>,
	query: String,
	subsection: CharacterCreationSubsection,
	visible: (String, CharacterCreationSubsection) -> List<T>,
	label: (T) -> String
) {
	assertQueryResults(expected, query, { normalizedQuery -> visible(normalizedQuery, subsection) }, label)
}

internal fun <T> assertSearchOnlyHiddenWhenBlank(visible: (String) -> List<T>) {
	assertNoQueryResults("", visible)
}

internal fun <T> assertSearchOnlyVisible(
	expected: List<String>,
	query: String,
	visible: (String) -> List<T>,
	label: (T) -> String
) {
	assertQueryResults(expected, query, visible, label)
}

