/*
 * FILE: EncounterRewards.kt
 *
 * TABLE OF CONTENTS:
 * 1. Encounter Reward Calculation
 * 2. Reward Pool Distribution
 * 3. Reward Item Helpers
 */

package io.github.velyene.loreweaver.domain.util

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.EncounterDifficultyTarget
import io.github.velyene.loreweaver.domain.model.EncounterRewardSummary
import io.github.velyene.loreweaver.domain.model.EncounterRewardTemplate
import io.github.velyene.loreweaver.domain.model.ParticipantRewardShare
import kotlin.math.ceil
import kotlin.math.roundToInt

private const val SUCCESS_RESULT = "VICTORY"

fun calculateEncounterRewards(
	encounterName: String,
	encounterResult: String?,
	combatants: List<CombatantState>,
	characters: List<CharacterEntry>,
	totalRounds: Int,
	rewardTemplate: EncounterRewardTemplate = EncounterRewardTemplate(),
	encounterDifficulty: EncounterDifficultyResult? = null
): EncounterRewardSummary {
	if (encounterResult != SUCCESS_RESULT) return EncounterRewardSummary()

	val charactersById = characters.associateBy(CharacterEntry::id)
	val participantCombatants = combatants.filter { combatant ->
		charactersById[combatant.characterId]?.party == CharacterParty.ADVENTURERS
	}
	val participantCharacters = participantCombatants.mapNotNull { combatant ->
		charactersById[combatant.characterId]
	}
	val enemyCombatants = combatants.filter { combatant ->
		charactersById[combatant.characterId]?.party != CharacterParty.ADVENTURERS
	}

	val totalEncounterXp = enemyCombatants.sumOf { combatant ->
		EncounterDifficulty.getXpForCr(charactersById[combatant.characterId]?.challengeRating ?: 0.0)
	}
	val participantCount = participantCombatants.size
	val experiencePerParticipant = if (totalEncounterXp > 0 && participantCount > 0) {
		ceil(totalEncounterXp.toDouble() / participantCount).toInt()
	} else {
		0
	}
	val experienceRoundingSurplus =
		(experiencePerParticipant * participantCount - totalEncounterXp).coerceAtLeast(0)

	val totalCurrencyCp = resolveCurrencyPoolCp(
		totalEncounterXp = totalEncounterXp,
		participantCharacters = participantCharacters,
		rewardTemplate = rewardTemplate,
		encounterDifficulty = encounterDifficulty
	)
	val currencyPerParticipantCp = if (totalCurrencyCp > 0 && participantCount > 0) {
		ceil(totalCurrencyCp.toDouble() / participantCount).toInt()
	} else {
		0
	}
	val currencyRoundingSurplusCp =
		(currencyPerParticipantCp * participantCount - totalCurrencyCp).coerceAtLeast(0)
	val participantRewards = participantCombatants.map { combatant ->
		ParticipantRewardShare(
			characterId = combatant.characterId,
			characterName = combatant.name,
			experiencePoints = experiencePerParticipant,
			currencyCp = currencyPerParticipantCp
		)
	}
	val unassignedLoot = rewardTemplate.preloadedLoot.ifEmpty {
		enemyCombatants.distinctBy(CombatantState::name).take(3).map { combatant ->
			"Loot from ${combatant.name}"
		}
	}
	val rewardLog = buildRewardLog(
		encounterName = encounterName,
		totalEncounterXp = totalEncounterXp,
		participantCount = participantCount,
		experiencePerParticipant = experiencePerParticipant,
		experienceRoundingSurplus = experienceRoundingSurplus,
		totalCurrencyCp = totalCurrencyCp,
		currencyPerParticipantCp = currencyPerParticipantCp,
		currencyRoundingSurplusCp = currencyRoundingSurplusCp,
		enemyCount = enemyCombatants.size,
		totalRounds = totalRounds,
		usedPreloadedCurrency = rewardTemplate.preloadedCurrencyCp > 0,
		rewardTemplate = rewardTemplate,
		participantRewards = participantRewards,
		unassignedLoot = unassignedLoot,
		specialItems = rewardTemplate.specialItemRewards,
		encounterDifficulty = encounterDifficulty,
		participantCharacters = participantCharacters
	)

	return EncounterRewardSummary(
		experiencePoints = totalEncounterXp,
		experiencePerParticipant = experiencePerParticipant,
		experienceRoundingSurplus = experienceRoundingSurplus,
		participantCount = participantCount,
		participantRewards = participantRewards,
		currencyReward = totalCurrencyCp.takeIf { it > 0 }?.let(::formatCurrencyCp),
		currencyPerParticipant = currencyPerParticipantCp.takeIf { it > 0 }?.let(::formatCurrencyCp),
		totalCurrencyCp = totalCurrencyCp,
		currencyPerParticipantCp = currencyPerParticipantCp,
		currencyRoundingSurplusCp = currencyRoundingSurplusCp,
		itemRewards = unassignedLoot,
		equipmentRewards = rewardTemplate.specialItemRewards,
		storyRewards = listOf(
			"LoreweaverMS rounds reward shares up.",
			"Encounter resolved in $totalRounds rounds."
		),
		rewardLog = rewardLog
	)
}

fun formatCurrencyCp(totalCopper: Int): String {
	if (totalCopper <= 0) return "0 cp"
	val gold = totalCopper / 100
	val silver = (totalCopper % 100) / 10
	val copper = totalCopper % 10
	return buildList {
		if (gold > 0) add("$gold gp")
		if (silver > 0) add("$silver sp")
		if (copper > 0 || isEmpty()) add("$copper cp")
	}.joinToString(", ")
}

private fun resolveCurrencyPoolCp(
	totalEncounterXp: Int,
	participantCharacters: List<CharacterEntry>,
	rewardTemplate: EncounterRewardTemplate,
	encounterDifficulty: EncounterDifficultyResult?
): Int {
	if (rewardTemplate.preloadedCurrencyCp > 0) return rewardTemplate.preloadedCurrencyCp
	if (totalEncounterXp <= 0) return 0

	val targetBudgetXp = resolveTargetBudgetXp(
		participantCharacters = participantCharacters,
		rewardTemplate = rewardTemplate,
		encounterDifficulty = encounterDifficulty
	)
	val actualEncounterXp = (encounterDifficulty?.adjustedXp ?: totalEncounterXp).coerceAtLeast(0)
	val difficultyModifier = if (targetBudgetXp > 0 && actualEncounterXp > 0) {
		actualEncounterXp.toDouble() / targetBudgetXp
	} else {
		1.0
	}

	return ceil(
		totalEncounterXp * rewardTemplate.currencyRateCpPerXp * difficultyModifier * rewardTemplate.economyMultiplier
	).toInt().coerceAtLeast(0)
}

private fun resolveTargetBudgetXp(
	participantCharacters: List<CharacterEntry>,
	rewardTemplate: EncounterRewardTemplate,
	encounterDifficulty: EncounterDifficultyResult?
): Int {
	if (rewardTemplate.difficultyTarget == EncounterDifficultyTarget.CUSTOM) {
		return rewardTemplate.customTargetBudgetXp?.coerceAtLeast(0) ?: 0
	}

	val thresholds = encounterDifficulty?.partyThresholds
		?: EncounterDifficulty.calculateDifficulty(
			partyMembers = participantCharacters,
			enemies = emptyList(),
			enemyCRMap = emptyMap()
		)?.partyThresholds
		.orEmpty()

	val rating = when (rewardTemplate.difficultyTarget) {
		EncounterDifficultyTarget.LOW -> DifficultyRating.EASY
		EncounterDifficultyTarget.MODERATE -> DifficultyRating.MEDIUM
		EncounterDifficultyTarget.HIGH -> DifficultyRating.HARD
		EncounterDifficultyTarget.CUSTOM -> DifficultyRating.MEDIUM
	}
	return thresholds[rating]?.coerceAtLeast(0) ?: 0
}

private fun buildRewardLog(
	encounterName: String,
	totalEncounterXp: Int,
	participantCount: Int,
	experiencePerParticipant: Int,
	experienceRoundingSurplus: Int,
	totalCurrencyCp: Int,
	currencyPerParticipantCp: Int,
	currencyRoundingSurplusCp: Int,
	enemyCount: Int,
	totalRounds: Int,
	usedPreloadedCurrency: Boolean,
	rewardTemplate: EncounterRewardTemplate,
	participantRewards: List<ParticipantRewardShare>,
	unassignedLoot: List<String>,
	specialItems: List<String>,
	encounterDifficulty: EncounterDifficultyResult?,
	participantCharacters: List<CharacterEntry>
): List<String> {
	val lines = mutableListOf<String>()
	lines += "$encounterName marked as a victory. All hostile roster XP counted."
	lines += "Encounter XP: $totalEncounterXp from $enemyCount enemy combatant${if (enemyCount == 1) "" else "s"}."

	if (participantCount > 0) {
		lines += "$participantCount participant${if (participantCount == 1) "" else "s"} receive $experiencePerParticipant XP each."
		participantRewards.forEach { reward ->
			lines += "${reward.characterName}: ${reward.experiencePoints} XP and ${formatCurrencyCp(reward.currencyCp)}."
		}
	}
	if (experienceRoundingSurplus > 0) {
		lines += "XP rounding surplus recorded: $experienceRoundingSurplus XP."
	}

	if (totalCurrencyCp > 0) {
		lines += "Currency pool: ${formatCurrencyCp(totalCurrencyCp)}."
		if (participantCount > 0) {
			lines += "Currency share per participant: ${formatCurrencyCp(currencyPerParticipantCp)}."
		}
		if (currencyRoundingSurplusCp > 0) {
			lines += "Currency rounding surplus recorded: ${formatCurrencyCp(currencyRoundingSurplusCp)}."
		}
		lines += if (usedPreloadedCurrency) {
			"Currency pool came from DM-preloaded rewards."
		} else {
			val difficultyModifier = resolveDifficultyModifierLabel(
				totalEncounterXp = totalEncounterXp,
				participantCharacters = participantCharacters,
				rewardTemplate = rewardTemplate,
				encounterDifficulty = encounterDifficulty
			)
			"Generated at ${rewardTemplate.currencyRateCpPerXp.trimmedDecimal()} cp/XP, modifier $difficultyModifier, economy ${rewardTemplate.economyMultiplier.trimmedDecimal()}."
		}
	}

	if (unassignedLoot.isNotEmpty()) {
		lines += "Unassigned loot recorded: ${unassignedLoot.joinToString()}."
	}
	if (specialItems.isNotEmpty()) {
		lines += "Special DM-assigned items recorded: ${specialItems.joinToString()}."
	}
	lines += "Encounter duration: $totalRounds rounds."
	return lines
}

private fun resolveDifficultyModifierLabel(
	totalEncounterXp: Int,
	participantCharacters: List<CharacterEntry>,
	rewardTemplate: EncounterRewardTemplate,
	encounterDifficulty: EncounterDifficultyResult?
): String {
	val targetBudgetXp = resolveTargetBudgetXp(
		participantCharacters = participantCharacters,
		rewardTemplate = rewardTemplate,
		encounterDifficulty = encounterDifficulty
	)
	val actualEncounterXp = (encounterDifficulty?.adjustedXp ?: totalEncounterXp).coerceAtLeast(0)
	val difficultyModifier = if (targetBudgetXp > 0 && actualEncounterXp > 0) {
		actualEncounterXp.toDouble() / targetBudgetXp
	} else {
		1.0
	}
	return difficultyModifier.trimmedDecimal()
}

private fun Double.trimmedDecimal(): String {
	val rounded = (this * 100.0).roundToInt() / 100.0
	return if (rounded % 1.0 == 0.0) {
		rounded.toInt().toString()
	} else {
		rounded.toString()
	}
}

