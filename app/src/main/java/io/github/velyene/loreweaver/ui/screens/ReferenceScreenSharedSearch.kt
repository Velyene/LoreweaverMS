package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.ReferenceTable

internal fun matchesQuery(query: String, vararg values: String): Boolean {
	return query.isBlank() || values.any { it.contains(query, ignoreCase = true) }
}

internal fun Enum<*>.toDisplayLabel(): String = name
	.lowercase()
	.replace('_', ' ')
	.split(' ')
	.joinToString(" ") { token -> token.replaceFirstChar { it.uppercase() } }

internal fun ReferenceTable.matchesQuery(query: String): Boolean {
	return matchesQuery(query, title, *columns.toTypedArray()) ||
		rows.any { row -> row.any { it.contains(query, ignoreCase = true) } }
}

internal inline fun <T> Iterable<T>.filterByQuery(
	query: String,
	crossinline label: (T) -> String,
	crossinline description: (T) -> String
): List<T> {
	return filter { entry -> matchesQuery(query, label(entry), description(entry)) }
}

internal fun List<String>.filterByQuery(query: String): List<String> {
	return if (query.isBlank()) this else filter { it.contains(query, ignoreCase = true) }
}

