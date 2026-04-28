package io.github.velyene.loreweaver.domain.util

object MonsterReferenceCatalog {
	const val ANIMAL_GROUP = MONSTER_GROUP_ANIMALS

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

	fun filter(
		query: String,
		creatureType: String? = null,
		challengeRating: String? = null,
		group: String? = null
	): List<MonsterReferenceEntry> {
		return ALL.filter { monster ->
			monster.matchesGroup(group) &&
				monster.matchesCreatureType(creatureType) &&
				monster.matchesChallengeRating(challengeRating) &&
				(query.isBlank() || monster.matchesSearchQuery(query))
		}
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

	private fun MonsterReferenceEntry.matchesSearchQuery(query: String): Boolean {
		return listOf(name, subtitle, body, group.orEmpty(), creatureType, challengeRating)
			.any { it.contains(query, ignoreCase = true) } ||
			statRows.any { (label, value) ->
				label.contains(query, ignoreCase = true) || value.contains(query, ignoreCase = true)
			} ||
			sections.any { section ->
				section.title.contains(query, ignoreCase = true) ||
					section.body?.contains(query, ignoreCase = true) == true ||
					section.bullets.any { bullet -> bullet.contains(query, ignoreCase = true) }
			} ||
			tables.any { table ->
				table.title.contains(query, ignoreCase = true) ||
					table.columns.any { column -> column.contains(query, ignoreCase = true) } ||
					table.rows.any { row -> row.any { cell -> cell.contains(query, ignoreCase = true) } }
			}
	}

	private fun MonsterReferenceEntry.matchesCreatureType(selectedType: String?): Boolean {
		return selectedType.isNullOrBlank() || creatureType.equals(selectedType, ignoreCase = true)
	}

	private fun MonsterReferenceEntry.matchesGroup(selectedGroup: String?): Boolean {
		return selectedGroup.isNullOrBlank() || group.equals(selectedGroup, ignoreCase = true)
	}

	private fun MonsterReferenceEntry.matchesChallengeRating(selectedRating: String?): Boolean {
		return selectedRating.isNullOrBlank() || challengeRating == selectedRating
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


