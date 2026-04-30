package io.github.velyene.loreweaver.domain.util

object MonsterReferenceCatalog {
	const val ANIMAL_GROUP = MONSTER_GROUP_ANIMALS

	private data class IndexedMonsterEntry(
		val entry: MonsterReferenceEntry,
		val searchCorpus: String,
		val normalizedCreatureType: String,
		val normalizedGroup: String,
		val challengeRating: String
	)

	val ALL: List<MonsterReferenceEntry> = run {
		val entries =
			MonsterReferenceDataAnimals.ENTRIES +
				MonsterReferenceDataAtoC.ENTRIES +
				MonsterReferenceDataDtoG.ENTRIES +
				MonsterReferenceDataHtoN.ENTRIES +
				MonsterReferenceDataOtoR.ENTRIES +
				MonsterReferenceDataStoW.ENTRIES +
				MonsterReferenceDataXtoZ.ENTRIES
		entries.sortedBy(MonsterReferenceEntry::name)
	}

	val CREATURE_TYPE_OPTIONS: List<String> = ALL
		.map(MonsterReferenceEntry::creatureType)
		.filter(String::isNotBlank)
		.distinct()
		.sorted()

	val CHALLENGE_RATING_OPTIONS: List<String> = ALL
		.map(MonsterReferenceEntry::challengeRating)
		.filter(String::isNotBlank)
		.distinct()
		.sortedBy(::parseMonsterChallengeRatingValue)

	private val INDEXED_ALL: List<IndexedMonsterEntry> = ALL.map { entry ->
		IndexedMonsterEntry(
			entry = entry,
			searchCorpus = buildMonsterSearchCorpus(entry),
			normalizedCreatureType = entry.creatureType.lowercase(),
			normalizedGroup = entry.group.orEmpty().lowercase(),
			challengeRating = entry.challengeRating
		)
	}

	fun filter(
		query: String,
		creatureType: String? = null,
		challengeRating: String? = null,
		group: String? = null
	): List<MonsterReferenceEntry> {
		val normalizedQuery = query.trim().lowercase()
		val normalizedCreatureType = creatureType?.trim()?.lowercase().orEmpty()
		val normalizedGroup = group?.trim()?.lowercase().orEmpty()

		return INDEXED_ALL.filter { indexedMonster ->
			indexedMonster.matchesGroup(normalizedGroup) &&
				indexedMonster.matchesCreatureType(normalizedCreatureType) &&
				indexedMonster.matchesChallengeRating(challengeRating) &&
				(normalizedQuery.isBlank() || indexedMonster.searchCorpus.contains(normalizedQuery))
		}.map(IndexedMonsterEntry::entry)
	}

	fun findEntry(identifier: String): MonsterReferenceEntry? {
		if (identifier.isBlank()) return null
		return ALL.firstOrNull { entry ->
			entry.name.equals(identifier, ignoreCase = true) || matchesSlug(entry.name, identifier)
		}
	}

	fun resolve(slug: String): ReferenceDetailContent? {
		return findEntry(slug)?.toReferenceDetailContent()
	}

	private fun IndexedMonsterEntry.matchesCreatureType(selectedType: String): Boolean {
		return selectedType.isBlank() || normalizedCreatureType == selectedType
	}

	private fun IndexedMonsterEntry.matchesGroup(selectedGroup: String): Boolean {
		return selectedGroup.isBlank() || normalizedGroup == selectedGroup
	}

	private fun IndexedMonsterEntry.matchesChallengeRating(selectedRating: String?): Boolean {
		return selectedRating.isNullOrBlank() || challengeRating == selectedRating
	}

	private fun buildMonsterSearchCorpus(entry: MonsterReferenceEntry): String {
		return buildString {
			appendLine(entry.name)
			appendLine(entry.subtitle)
			appendLine(entry.body)
			appendLine(entry.group.orEmpty())
			appendLine(entry.creatureType)
			appendLine(entry.challengeRating)
			entry.statRows.forEach { (label, value) ->
				appendLine(label)
				appendLine(value)
			}
			entry.sections.forEach { section ->
				appendLine(section.title)
				section.body?.let(::appendLine)
				section.bullets.forEach(::appendLine)
			}
			entry.tables.forEach { table ->
				appendLine(table.title)
				table.columns.forEach(::appendLine)
				table.rows.forEach { row -> row.forEach(::appendLine) }
			}
		}.lowercase()
	}

	private fun MonsterReferenceEntry.toReferenceDetailContent(): ReferenceDetailContent {
		return ReferenceDetailContent(
			title = name,
			subtitle = group?.let { "$subtitle • $it" } ?: subtitle,
			overview = body.takeIf { it.isNotBlank() && sections.isEmpty() },
			statRows = statRows,
			sections = sections,
			tables = tables
		)
	}

	private fun matchesSlug(name: String, slug: String): Boolean {
		return ReferenceDetailResolver.slugFor(name) == ReferenceDetailResolver.slugFor(slug.replace('-', ' '))
	}
}


