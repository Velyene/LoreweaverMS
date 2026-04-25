package io.github.velyene.loreweaver.domain.util

data class ReferenceTable(
	val title: String,
	val columns: List<String>,
	val rows: List<List<String>>
)
