package io.github.velyene.loreweaver.domain.util


data class MonsterReferenceEntry(
	val name: String,
	val subtitle: String,
	val body: String = "",
	val group: String? = null,
	val creatureType: String = "",
	val challengeRating: String = "",
	val statRows: List<Pair<String, String>> = emptyList(),
	val sections: List<ReferenceDetailSection> = emptyList(),
	val tables: List<ReferenceTable> = emptyList()
)
