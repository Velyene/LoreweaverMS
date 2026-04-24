package io.github.velyene.loreweaver.domain.util


data class MonsterReferenceEntry(
	val name: String,
	val subtitle: String,
	val body: String,
	val group: String? = null,
	val tables: List<ReferenceTable> = emptyList()
)
