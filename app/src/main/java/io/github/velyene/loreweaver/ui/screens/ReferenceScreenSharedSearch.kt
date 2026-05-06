package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.AbilityScoreSummary
import io.github.velyene.loreweaver.domain.util.AdventuringGearEntry
import io.github.velyene.loreweaver.domain.util.AmmunitionReferenceEntry
import io.github.velyene.loreweaver.domain.util.ArmorReferenceEntry
import io.github.velyene.loreweaver.domain.util.BackgroundReference
import io.github.velyene.loreweaver.domain.util.CharacterCreationStep
import io.github.velyene.loreweaver.domain.util.CharacterCreationTextSection
import io.github.velyene.loreweaver.domain.util.CoreRuleSection
import io.github.velyene.loreweaver.domain.util.FeatReference
import io.github.velyene.loreweaver.domain.util.FocusReferenceEntry
import io.github.velyene.loreweaver.domain.util.LanguageReference
import io.github.velyene.loreweaver.domain.util.LargeVehicleReferenceEntry
import io.github.velyene.loreweaver.domain.util.MagicItemReferenceEntry
import io.github.velyene.loreweaver.domain.util.MountReferenceEntry
import io.github.velyene.loreweaver.domain.util.RaceReference
import io.github.velyene.loreweaver.domain.util.RacialTraitReference
import io.github.velyene.loreweaver.domain.util.ReferenceTable
import io.github.velyene.loreweaver.domain.util.SubraceReference
import io.github.velyene.loreweaver.domain.util.TackDrawnReferenceEntry
import io.github.velyene.loreweaver.domain.util.ToolReferenceEntry
import io.github.velyene.loreweaver.domain.util.WeaponReferenceEntry

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

// Character Creation Extensions
internal fun CharacterCreationTextSection.matchesQuery(query: String): Boolean =
	matchesQuery(query, title, body)

internal fun CharacterCreationStep.matchesQuery(query: String): Boolean =
	matchesQuery(query, number.toString(), title, content, example.orEmpty())

internal fun AbilityScoreSummary.matchesQuery(query: String): Boolean =
	matchesQuery(query, ability, measures, importantFor, racialIncreases)

internal fun BackgroundReference.matchesQuery(query: String): Boolean =
	matchesQuery(
		query,
		name,
		feat,
		toolProficiency,
		*backgroundSearchAliases().toTypedArray(),
		*abilityScores.toTypedArray(),
		*skillProficiencies.toTypedArray(),
		*equipmentOptions.toTypedArray()
	)

private fun BackgroundReference.backgroundSearchAliases(): List<String> = when (name) {
	"Acolyte" -> listOf("Magic Initiate", "Cleric")
	"Sage" -> listOf("Magic Initiate", "Wizard")
	else -> emptyList()
}

internal fun FeatReference.matchesQuery(query: String): Boolean =
	matchesQuery(
		query,
		name,
		category,
		prerequisite.orEmpty(),
		*benefits.toTypedArray(),
		if (repeatable) "Repeatable" else ""
	)

internal fun LanguageReference.matchesQuery(query: String): Boolean =
	matchesQuery(query, name, group, roll.orEmpty())

internal fun RaceReference.matchesQuery(query: String): Boolean =
	matchesQuery(
		query,
		name,
		overview,
		personality,
		society,
		adventurers,
		names,
		abilityScoreIncrease,
		age,
		size,
		speed,
		languages
	) || traits.any { it.matchesQuery(query) } ||
		subraces.any { it.matchesQuery(query) } ||
		notes.any { it.contains(query, ignoreCase = true) }

internal fun SubraceReference.matchesQuery(query: String): Boolean =
	matchesQuery(query, name, overview, abilityScoreIncrease.orEmpty()) ||
		traits.any { it.matchesQuery(query) }

internal fun RacialTraitReference.matchesQuery(query: String): Boolean =
	matchesQuery(query, name, description)

// Core Rules Extensions
internal fun CoreRuleSection.matchesQuery(query: String): Boolean =
	matchesQuery(query, title, summary, *bullets.toTypedArray(), *keywords.toTypedArray())

// Equipment Extensions
internal fun ToolReferenceEntry.matchesQuery(query: String): Boolean =
	matchesQuery(
		query,
		name,
		category.canonicalLabel,
		ability,
		weight,
		cost,
		*utilize.toTypedArray(),
		*craft.toTypedArray(),
		*variants.toTypedArray(),
		*notes.toTypedArray()
	)

internal fun WeaponReferenceEntry.matchesQuery(query: String): Boolean =
	matchesQuery(
		query,
		name,
		category,
		damage,
		mastery,
		weight,
		cost,
		*properties.toTypedArray()
	)

internal fun ArmorReferenceEntry.matchesQuery(query: String): Boolean =
	matchesQuery(
		query,
		name,
		categoryDonDoff,
		armorClass,
		strength,
		stealth,
		weight,
		cost
	)

internal fun AdventuringGearEntry.matchesQuery(query: String): Boolean =
	matchesQuery(query, name, weight, cost, body.orEmpty())

internal fun MagicItemReferenceEntry.matchesQuery(query: String): Boolean {
	val tableFields = tables.flatMap { table ->
		listOf(table.title) + table.columns + table.rows.flatten()
	}
	return matchesQuery(query, name, subtitle, body, *tableFields.toTypedArray())
}

internal fun AmmunitionReferenceEntry.matchesQuery(query: String): Boolean =
	matchesQuery(query, type, amount, storage, weight, cost)

internal fun FocusReferenceEntry.matchesQuery(query: String): Boolean =
	matchesQuery(query, group.canonicalLabel, name, weight, cost, usage, *notes.toTypedArray())

internal fun MountReferenceEntry.matchesQuery(query: String): Boolean =
	matchesQuery(query, item, carryingCapacity, cost)

internal fun TackDrawnReferenceEntry.matchesQuery(query: String): Boolean =
	matchesQuery(query, item, weight, cost)

internal fun LargeVehicleReferenceEntry.matchesQuery(query: String): Boolean =
	matchesQuery(
		query,
		ship,
		speed,
		crew,
		passengers,
		cargoTons,
		ac,
		hp,
		damageThreshold,
		cost
	)
