package io.github.velyene.loreweaver.domain.util

internal const val MONSTER_GROUP_ANIMALS = "Animals"

internal const val MONSTER_STAT_AC = "AC"
internal const val MONSTER_STAT_INITIATIVE = "Initiative"
internal const val MONSTER_STAT_HP = "HP"
internal const val MONSTER_STAT_SPEED = "Speed"
internal const val MONSTER_STAT_CR = "CR"

internal const val MONSTER_SECTION_TRAIT = "Trait"
internal const val MONSTER_SECTION_TRAITS = "Traits"
internal const val MONSTER_SECTION_ACTIONS = "Actions"
internal const val MONSTER_SECTION_BONUS_ACTIONS = "Bonus Actions"
internal const val MONSTER_SECTION_REACTIONS = "Reactions"
internal const val MONSTER_SECTION_LEGENDARY_ACTIONS = "Legendary Actions"
internal const val MONSTER_SECTION_ABILITY_SCORES = "Ability Scores"
internal const val MONSTER_SECTION_PROFILE = "Profile"

internal const val MONSTER_PROFILE_LABEL_SKILLS = "Skills"
internal const val MONSTER_PROFILE_LABEL_VULNERABILITIES = "Vulnerabilities"
internal const val MONSTER_PROFILE_LABEL_RESISTANCES = "Resistances"
internal const val MONSTER_PROFILE_LABEL_IMMUNITIES = "Immunities"
internal const val MONSTER_PROFILE_LABEL_GEAR = "Gear"
internal const val MONSTER_PROFILE_LABEL_SENSES = "Senses"
internal const val MONSTER_PROFILE_LABEL_LANGUAGES = "Languages"

internal const val MONSTER_ABILITY_SCORE_PREFIX = "STR "
internal val MONSTER_CARD_SUMMARY_STAT_LABELS = listOf(
	MONSTER_STAT_AC,
	MONSTER_STAT_HP,
	MONSTER_STAT_SPEED,
	MONSTER_STAT_CR
)
internal val MONSTER_CARD_PREVIEW_SECTION_TITLES = setOf(
	MONSTER_SECTION_TRAITS,
	MONSTER_SECTION_ACTIONS,
	MONSTER_SECTION_BONUS_ACTIONS
)
internal val MONSTER_SECTION_TITLES = mapOf(
	MONSTER_SECTION_TRAIT to MONSTER_SECTION_TRAITS,
	MONSTER_SECTION_TRAITS to MONSTER_SECTION_TRAITS,
	MONSTER_SECTION_ACTIONS to MONSTER_SECTION_ACTIONS,
	MONSTER_SECTION_BONUS_ACTIONS to MONSTER_SECTION_BONUS_ACTIONS,
	MONSTER_SECTION_REACTIONS to MONSTER_SECTION_REACTIONS,
	MONSTER_SECTION_LEGENDARY_ACTIONS to MONSTER_SECTION_LEGENDARY_ACTIONS
)
internal val MONSTER_PROFILE_LABELS = setOf(
	MONSTER_PROFILE_LABEL_SKILLS,
	MONSTER_PROFILE_LABEL_VULNERABILITIES,
	MONSTER_PROFILE_LABEL_RESISTANCES,
	MONSTER_PROFILE_LABEL_IMMUNITIES,
	MONSTER_PROFILE_LABEL_GEAR,
	MONSTER_PROFILE_LABEL_SENSES,
	MONSTER_PROFILE_LABEL_LANGUAGES
)

private val monsterSectionRegex =
	Regex("^(${MONSTER_SECTION_TITLES.keys.joinToString("|")}):\\s*(.*)$")

internal fun buildMonsterEntry(
	name: String,
	subtitle: String,
	ac: String,
	initiative: String,
	hp: String,
	speed: String,
	cr: String,
	body: String,
	group: String? = null
): MonsterReferenceEntry {
	val normalizedBody = body.trimIndent()
	return MonsterReferenceEntry(
		name = name,
		subtitle = subtitle,
		body = normalizedBody,
		group = group,
		creatureType = parseMonsterCreatureType(subtitle),
		challengeRating = parseMonsterChallengeRating(cr),
		statRows = listOf(
			MONSTER_STAT_AC to ac,
			MONSTER_STAT_INITIATIVE to initiative,
			MONSTER_STAT_HP to hp,
			MONSTER_STAT_SPEED to speed,
			MONSTER_STAT_CR to cr
		),
		sections = buildMonsterSections(normalizedBody)
	)
}

internal fun parseMonsterChallengeRating(cr: String): String {
	return cr.substringBefore('(').trim().ifBlank { cr.trim() }
}

internal fun parseMonsterChallengeRatingValue(cr: String): Double {
	return when (val label = parseMonsterChallengeRating(cr)) {
		"0" -> 0.0
		"1/8" -> 0.125
		"1/4" -> 0.25
		"1/2" -> 0.5
		else -> label.toDoubleOrNull() ?: Double.MAX_VALUE
	}
}

internal fun parseMonsterCreatureType(subtitle: String): String {
	val descriptor = subtitle.substringBefore(',').substringBefore(';').trim()
	val withoutParentheticals = descriptor.replace(Regex("\\s*\\([^)]*\\)"), "")
	val tokens = withoutParentheticals.split(Regex("\\s+"))
	return tokens.lastOrNull().orEmpty()
}

private fun buildMonsterSections(body: String): List<ReferenceDetailSection> {
	if (body.isBlank()) return emptyList()

	val abilityLines = mutableListOf<String>()
	val profileBullets = mutableListOf<String>()
	val sections = mutableListOf<ReferenceDetailSection>()
	var currentSectionTitle: String? = null
	val currentSectionLines = mutableListOf<String>()

	fun flushCurrentSection() {
		val title = currentSectionTitle ?: return
		sections += ReferenceDetailSection(
			title = title,
			body = currentSectionLines.joinToString("\n").trim().takeIf { it.isNotBlank() }
		)
		currentSectionTitle = null
		currentSectionLines.clear()
	}

	body.lineSequence()
		.map(String::trim)
		.filter(String::isNotBlank)
		.forEach { line ->
			when {
				line.startsWith(MONSTER_ABILITY_SCORE_PREFIX) -> abilityLines += line
				monsterSectionRegex.matches(line) -> {
					flushCurrentSection()
					val match = monsterSectionRegex.matchEntire(line) ?: return@forEach
					currentSectionTitle = MONSTER_SECTION_TITLES.getValue(match.groupValues[1])
					match.groupValues[2].trim().takeIf(String::isNotBlank)?.let(currentSectionLines::add)
				}

				currentSectionTitle != null -> currentSectionLines += line
				line.substringBefore(':') in MONSTER_PROFILE_LABELS -> profileBullets += line
				else -> profileBullets += line
			}
		}

	flushCurrentSection()

	return buildList {
		abilityLines.firstOrNull()?.let { abilityLine ->
			add(
				ReferenceDetailSection(
					title = MONSTER_SECTION_ABILITY_SCORES,
					bullets = abilityLine.split(',').map(String::trim).filter(String::isNotBlank)
				)
			)
		}
		if (profileBullets.isNotEmpty()) {
			add(ReferenceDetailSection(title = MONSTER_SECTION_PROFILE, bullets = profileBullets))
		}
		addAll(sections)
	}
}

