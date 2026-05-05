/*
 * FILE: EncounterGeneration.kt
 *
 * TABLE OF CONTENTS:
 * 1. Encounter Generation Models
 * 2. Encounter Budget and Selection Logic
 * 3. Monster Pool Filtering
 * 4. Encounter Generation Helpers
 */

package io.github.velyene.loreweaver.domain.util

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.EncounterDifficultyTarget
import io.github.velyene.loreweaver.domain.model.EncounterGeneratedEnemy
import io.github.velyene.loreweaver.domain.model.EncounterGenerationAttempt
import io.github.velyene.loreweaver.domain.model.EncounterGenerationDetails
import io.github.velyene.loreweaver.domain.model.EncounterGenerationSettings
import io.github.velyene.loreweaver.domain.model.EncounterPartyPowerEntry
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

private const val DEFAULT_MAX_ATTEMPTS = 40
private const val DEFAULT_SOURCE_LABEL = "SRD 5.2"

fun calculateEncounterPartyPowerEntries(
	partyMembers: List<CharacterEntry>,
	difficultyTarget: EncounterDifficultyTarget,
	customTargetXp: Int? = null
): List<EncounterPartyPowerEntry> {
	if (partyMembers.isEmpty()) return emptyList()
	if (difficultyTarget == EncounterDifficultyTarget.CUSTOM) {
		val total = customTargetXp?.coerceAtLeast(0) ?: 0
		val share = if (partyMembers.isNotEmpty()) total / partyMembers.size else 0
		return partyMembers.map { member ->
			EncounterPartyPowerEntry(
				characterId = member.id,
				characterName = member.name,
				level = member.level,
				budgetXp = share
			)
		}
	}
	return partyMembers.map { member ->
		EncounterPartyPowerEntry(
			characterId = member.id,
			characterName = member.name,
			level = member.level,
			budgetXp = EncounterDifficulty.getXpBudgetForLevel(member.level, difficultyTarget)
		)
	}
}

fun generateRandomEncounter(
	partyMembers: List<CharacterEntry>,
	settings: EncounterGenerationSettings,
	random: Random = Random.Default,
	maxAttempts: Int = DEFAULT_MAX_ATTEMPTS,
	idFactory: () -> String
): EncounterGenerationDetails {
	val participantLevels = partyMembers.map(CharacterEntry::level)
	val partyPowerEntries = calculateEncounterPartyPowerEntries(
		partyMembers = partyMembers,
		difficultyTarget = settings.difficultyTarget,
		customTargetXp = settings.customTargetXp
	)
	val partyPower = if (settings.difficultyTarget == EncounterDifficultyTarget.CUSTOM) {
		settings.customTargetXp?.coerceAtLeast(0) ?: 0
	} else {
		partyPowerEntries.sumOf(EncounterPartyPowerEntry::budgetXp)
	}
	val minimumTargetXp = (partyPower * (settings.minimumTargetPercent / 100.0)).roundToInt()
	val maximumTargetXp = (partyPower * (settings.maximumTargetPercent / 100.0)).roundToInt()
	val averagePartyLevel = if (partyMembers.isEmpty()) 0.0 else partyMembers.map { it.level }.average()
	val appliedMaximumEnemyCr = settings.maximumEnemyCr ?: when {
		settings.allowSingleHighCrEnemy -> Double.MAX_VALUE
		else -> averagePartyLevel
	}
	val candidates = buildEncounterGenerationCandidates(settings, appliedMaximumEnemyCr)
	if (partyMembers.isEmpty() || partyPower <= 0 || candidates.isEmpty()) {
		return buildFallbackGenerationDetails(
			participantLevels = participantLevels,
			settings = settings,
			partyPower = partyPower,
			minimumTargetXp = minimumTargetXp,
			maximumTargetXp = maximumTargetXp,
			appliedMaximumEnemyCr = appliedMaximumEnemyCr,
			reason = if (partyMembers.isEmpty()) {
				"No participating party members."
			} else if (partyPower <= 0) {
				"Party power was 0 XP."
			} else {
				"No monsters matched the current filters."
			}
		)
	}

	var closestAttempt: GeneratedEncounterSelection? = null
	val attempts = mutableListOf<EncounterGenerationAttempt>()
	repeat(maxAttempts.coerceAtLeast(1)) { index ->
		val selection = generateEncounterAttempt(
			candidates = candidates,
			minimumTargetXp = minimumTargetXp,
			maximumTargetXp = maximumTargetXp,
			allowDuplicateEnemies = settings.allowDuplicateEnemies,
			maximumEnemyQuantity = settings.maximumEnemyQuantity ?: (partyMembers.size * 2).coerceAtLeast(3),
			random = random,
			idFactory = idFactory
		)
		val accepted = selection.totalEnemyXp in minimumTargetXp..maximumTargetXp
		attempts += EncounterGenerationAttempt(
			attemptNumber = index + 1,
			selectedEnemies = selection.generatedEnemies.groupedSummary(),
			totalEnemyXp = selection.totalEnemyXp,
			accepted = accepted,
			resultMessage = when {
				accepted -> "Accepted"
				selection.totalEnemyXp < minimumTargetXp -> "Rejected: below target"
				else -> "Rejected: above target"
			}
		)
		if (closestAttempt == null || selection.distanceFromRange(minimumTargetXp, maximumTargetXp) < closestAttempt.distanceFromRange(minimumTargetXp, maximumTargetXp)) {
			closestAttempt = selection
		}
		if (accepted) {
			return buildGenerationDetails(
				participantLevels = participantLevels,
				partyPowerEntries = partyPowerEntries,
				settings = settings,
				partyPower = partyPower,
				minimumTargetXp = minimumTargetXp,
				maximumTargetXp = maximumTargetXp,
				appliedMaximumEnemyCr = appliedMaximumEnemyCr,
				attempts = attempts,
				finalSelection = selection,
				requiresDmReview = false
			)
		}
	}

	return buildGenerationDetails(
		participantLevels = participantLevels,
		partyPowerEntries = partyPowerEntries,
		settings = settings,
		partyPower = partyPower,
		minimumTargetXp = minimumTargetXp,
		maximumTargetXp = maximumTargetXp,
		appliedMaximumEnemyCr = appliedMaximumEnemyCr,
		attempts = attempts,
		finalSelection = closestAttempt ?: GeneratedEncounterSelection(),
		requiresDmReview = true
	)
}

private fun buildEncounterGenerationCandidates(
	settings: EncounterGenerationSettings,
	appliedMaximumEnemyCr: Double
): List<EncounterGenerationCandidate> {
	return MonsterReferenceCatalog.ALL
		.filter { monster ->
			settings.sourceFilter != io.github.velyene.loreweaver.domain.model.EncounterGenerationSourceFilter.CUSTOM_ONLY &&
			(settings.creatureTypeFilter.isNullOrBlank() || monster.creatureType.equals(settings.creatureTypeFilter, ignoreCase = true)) &&
			(settings.groupFilter.isNullOrBlank() || monster.group.equals(settings.groupFilter, ignoreCase = true)) &&
			parseMonsterChallengeRatingValue(monster.challengeRating) <= appliedMaximumEnemyCr
		}
		.mapNotNull { monster ->
			val crValue = parseMonsterChallengeRatingValue(monster.challengeRating)
			val xpValue = EncounterDifficulty.getXpForCr(crValue)
			if (crValue == Double.MAX_VALUE || xpValue <= 0) {
				null
			} else {
				EncounterGenerationCandidate(
					entry = monster,
					challengeRatingValue = crValue,
					xpValue = xpValue,
					hp = monster.statRows.firstOrNull { it.first == MONSTER_STAT_HP }?.second?.leadingIntOr(defaultValue = 10) ?: 10,
					initiative = monster.statRows.firstOrNull { it.first == MONSTER_STAT_INITIATIVE }?.second?.leadingSignedIntOr(defaultValue = 0) ?: 0
				)
			}
		}
}

private fun generateEncounterAttempt(
	candidates: List<EncounterGenerationCandidate>,
	minimumTargetXp: Int,
	maximumTargetXp: Int,
	allowDuplicateEnemies: Boolean,
	maximumEnemyQuantity: Int,
	random: Random,
	idFactory: () -> String
): GeneratedEncounterSelection {
	val selected = mutableListOf<EncounterGeneratedEnemy>()
	val counts = mutableMapOf<String, Int>()
	var totalEnemyXp = 0
	val hardCapEnemyCount = maximumEnemyQuantity.coerceAtLeast(1) * 2

	while (selected.size < hardCapEnemyCount) {
		val remainingMin = minimumTargetXp - totalEnemyXp
		val available = candidates.filter { candidate ->
			val count = counts[candidate.entry.name].orEmptyCount()
			(allowDuplicateEnemies || count == 0) && count < maximumEnemyQuantity
		}
		if (available.isEmpty()) break
		if (totalEnemyXp in minimumTargetXp..maximumTargetXp) break

		val preferred = available.filter { candidate ->
			candidate.xpValue + totalEnemyXp <= maximumTargetXp || totalEnemyXp < minimumTargetXp
		}.ifEmpty { available }
		val ranked = preferred.sortedBy { candidate ->
			abs(((remainingMin.takeIf { it > 0 } ?: (maximumTargetXp - totalEnemyXp))) - candidate.xpValue)
		}
		val pick = ranked.take(6).random(random)
		val nextCount = counts[pick.entry.name].orEmptyCount() + 1
		counts[pick.entry.name] = nextCount
		selected += EncounterGeneratedEnemy(
			combatantId = idFactory(),
			name = pick.entry.name,
			challengeRating = pick.challengeRatingValue,
			challengeRatingLabel = pick.entry.challengeRating,
			xpValue = pick.xpValue,
			hp = pick.hp,
			initiative = pick.initiative,
			creatureType = pick.entry.creatureType,
			sourceLabel = DEFAULT_SOURCE_LABEL,
			quantityGroupKey = pick.entry.name
		)
		totalEnemyXp += pick.xpValue
		if (totalEnemyXp >= minimumTargetXp && totalEnemyXp <= maximumTargetXp) break
		if (totalEnemyXp > maximumTargetXp && selected.size > 1) break
	}

	return GeneratedEncounterSelection(
		generatedEnemies = selected,
		totalEnemyXp = totalEnemyXp
	)
}

private fun buildGenerationDetails(
	participantLevels: List<Int>,
	partyPowerEntries: List<EncounterPartyPowerEntry>,
	settings: EncounterGenerationSettings,
	partyPower: Int,
	minimumTargetXp: Int,
	maximumTargetXp: Int,
	appliedMaximumEnemyCr: Double,
	attempts: List<EncounterGenerationAttempt>,
	finalSelection: GeneratedEncounterSelection,
	requiresDmReview: Boolean
): EncounterGenerationDetails {
	val variancePercent = if (partyPower > 0) {
		((finalSelection.totalEnemyXp - partyPower).toDouble() / partyPower.toDouble()) * 100.0
	} else {
		0.0
	}
	return EncounterGenerationDetails(
		participantLevels = participantLevels,
		participantCount = participantLevels.size,
		targetDifficulty = settings.difficultyTarget,
		partyPower = partyPower,
		minimumTargetXp = minimumTargetXp,
		maximumTargetXp = maximumTargetXp,
		appliedMaximumEnemyCr = appliedMaximumEnemyCr,
		requiresDmReview = requiresDmReview,
		finalTotalEnemyXp = finalSelection.totalEnemyXp,
		finalVariancePercent = variancePercent,
		partyPowerEntries = partyPowerEntries,
		attempts = attempts.takeLast(12),
		finalEnemies = finalSelection.generatedEnemies,
		logLines = buildGenerationLogLines(
			participantLevels = participantLevels,
			partyPowerEntries = partyPowerEntries,
			settings = settings,
			partyPower = partyPower,
			minimumTargetXp = minimumTargetXp,
			maximumTargetXp = maximumTargetXp,
			appliedMaximumEnemyCr = appliedMaximumEnemyCr,
			attempts = attempts.takeLast(12),
			finalSelection = finalSelection,
			requiresDmReview = requiresDmReview,
			variancePercent = variancePercent
		)
	)
}

private fun buildFallbackGenerationDetails(
	participantLevels: List<Int>,
	settings: EncounterGenerationSettings,
	partyPower: Int,
	minimumTargetXp: Int,
	maximumTargetXp: Int,
	appliedMaximumEnemyCr: Double,
	reason: String
): EncounterGenerationDetails {
	return EncounterGenerationDetails(
		participantLevels = participantLevels,
		participantCount = participantLevels.size,
		targetDifficulty = settings.difficultyTarget,
		partyPower = partyPower,
		minimumTargetXp = minimumTargetXp,
		maximumTargetXp = maximumTargetXp,
		appliedMaximumEnemyCr = appliedMaximumEnemyCr,
		requiresDmReview = true,
		logLines = listOf(
			"Party levels: ${participantLevels.joinToString().ifBlank { "None" }}",
			"Target difficulty: ${settings.difficultyTarget.name.lowercase().replaceFirstChar(Char::titlecase)}",
			"Party power: $partyPower XP",
			"Source: ${settings.sourceFilter.formatSourceFilterLabel()}",
			reason
		)
	)
}

private fun buildGenerationLogLines(
	participantLevels: List<Int>,
	partyPowerEntries: List<EncounterPartyPowerEntry>,
	settings: EncounterGenerationSettings,
	partyPower: Int,
	minimumTargetXp: Int,
	maximumTargetXp: Int,
	appliedMaximumEnemyCr: Double,
	attempts: List<EncounterGenerationAttempt>,
	finalSelection: GeneratedEncounterSelection,
	requiresDmReview: Boolean,
	variancePercent: Double
): List<String> {
	return buildList {
		add("Party levels: ${participantLevels.joinToString()}")
		add("Target: ${settings.difficultyTarget.name.lowercase().replaceFirstChar(Char::titlecase)}")
		partyPowerEntries.forEach { entry ->
			add("${entry.characterName} Lv ${entry.level}: ${entry.budgetXp} XP")
		}
		add("Party power: $partyPower XP")
		add("Range: $minimumTargetXp-$maximumTargetXp XP")
		add("Max enemy CR: ${appliedMaximumEnemyCr.formatCrLabel()}")
		add("Source: ${settings.sourceFilter.formatSourceFilterLabel()}")
		add("Duplicates: ${if (settings.allowDuplicateEnemies) "Allowed" else "Off"}")
		settings.creatureTypeFilter?.takeIf { it.isNotBlank() }?.let { add("Type filter: $it") }
		settings.groupFilter?.takeIf { it.isNotBlank() }?.let { add("Group filter: $it") }
		attempts.forEach { attempt ->
			add("Attempt ${attempt.attemptNumber}: ${attempt.selectedEnemies.joinToString()} = ${attempt.totalEnemyXp} XP • ${attempt.resultMessage}")
		}
		add("Final: ${finalSelection.generatedEnemies.groupedSummary().joinToString()} = ${finalSelection.totalEnemyXp} XP")
		add("Variance: ${variancePercent.formatPercentLabel()}")
		add(if (requiresDmReview) "Status: Closest match. Review suggested." else "Status: Ready for DM review.")
	}
}

private data class EncounterGenerationCandidate(
	val entry: MonsterReferenceEntry,
	val challengeRatingValue: Double,
	val xpValue: Int,
	val hp: Int,
	val initiative: Int
)

private data class GeneratedEncounterSelection(
	val generatedEnemies: List<EncounterGeneratedEnemy> = emptyList(),
	val totalEnemyXp: Int = 0
) {
	fun distanceFromRange(minimumTargetXp: Int, maximumTargetXp: Int): Int {
		return when {
			totalEnemyXp < minimumTargetXp -> minimumTargetXp - totalEnemyXp
			totalEnemyXp > maximumTargetXp -> totalEnemyXp - maximumTargetXp
			else -> 0
		}
	}
}

private fun List<EncounterGeneratedEnemy>.groupedSummary(): List<String> {
	return groupBy(EncounterGeneratedEnemy::name)
		.entries
		.sortedBy { it.key }
		.map { (name, enemies) ->
			val xp = enemies.sumOf(EncounterGeneratedEnemy::xpValue)
			"CR ${enemies.first().challengeRatingLabel} × ${enemies.size} ($name, $xp XP)"
		}
}

private fun String.leadingIntOr(defaultValue: Int): Int {
	return Regex("\\d+").find(this)?.value?.toIntOrNull() ?: defaultValue
}

private fun String.leadingSignedIntOr(defaultValue: Int): Int {
	return Regex("[+-]?\\d+").find(this)?.value?.toIntOrNull() ?: defaultValue
}

private fun Int?.orEmptyCount(): Int = this ?: 0

private fun Double.formatCrLabel(): String {
	return when (this) {
		Double.MAX_VALUE -> "Auto"
		0.125 -> "1/8"
		0.25 -> "1/4"
		0.5 -> "1/2"
		else -> if (this % 1.0 == 0.0) toInt().toString() else toString()
	}
}

private fun Double.formatPercentLabel(): String {
	val rounded = (this * 10.0).roundToInt() / 10.0
	return if (rounded >= 0) "+$rounded%" else "$rounded%"
}

private fun io.github.velyene.loreweaver.domain.model.EncounterGenerationSourceFilter.formatSourceFilterLabel(): String {
	return when (this) {
		io.github.velyene.loreweaver.domain.model.EncounterGenerationSourceFilter.SRD_ONLY -> "SRD Only"
		io.github.velyene.loreweaver.domain.model.EncounterGenerationSourceFilter.CUSTOM_ONLY -> "Custom Only"
		io.github.velyene.loreweaver.domain.model.EncounterGenerationSourceFilter.BOTH -> "Both"
	}
}
