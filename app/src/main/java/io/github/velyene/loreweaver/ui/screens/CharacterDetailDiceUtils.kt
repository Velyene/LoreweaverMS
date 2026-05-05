package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import kotlin.random.Random

internal const val STAT_TYPE_HP = "HP"
internal const val STAT_TYPE_MANA = "Mana"
internal const val STAT_TYPE_STAMINA = "Stamina"
internal const val STAT_TYPE_TEMP_HP = "TempHP"

internal fun applyStatDelta(
	character: CharacterEntry,
	delta: Int,
	statType: String
): CharacterEntry =
	when (statType) {
		STAT_TYPE_HP -> if (delta < 0) character.applyDamage(-delta) else character.applyHealing(delta)
		STAT_TYPE_MANA -> character.copy(mana = (character.mana + delta).coerceIn(0, character.maxMana))
		STAT_TYPE_STAMINA -> character.copy(
			stamina = (character.stamina + delta).coerceIn(
				0,
				character.maxStamina
			)
		)

		STAT_TYPE_TEMP_HP -> character.copy(tempHp = (character.tempHp + delta).coerceAtLeast(0))
		else -> character
	}

private fun rollDicePool(num: Int, sides: Int): Int =
	(1..num).sumOf { Random.nextInt(1, sides + 1) }

private fun rollWithAdvDis(num: Int, sides: Int, advantage: Boolean): Int {
	val roll1 = rollDicePool(num, sides)
	val roll2 = rollDicePool(num, sides)
	return if (advantage) maxOf(roll1, roll2) else minOf(roll1, roll2)
}

private fun evaluateDicePart(
	cleanPart: String,
	isFirstPart: Boolean,
	isAdv: Boolean,
	isDis: Boolean
): Int {
	if (!cleanPart.contains("d")) return cleanPart.toIntOrNull() ?: 0
	val (numStr, sidesStr) = cleanPart.split("d", limit = 2)
	val num = numStr.ifEmpty { "1" }.toIntOrNull() ?: 1
	val sides = sidesStr.toIntOrNull() ?: 6
	return if (isFirstPart && (isAdv || isDis)) {
		rollWithAdvDis(num, sides, isAdv)
	} else {
		rollDicePool(num, sides)
	}
}

fun parseAndRoll(diceExpr: String): Int = try {
	val cleanExpr = diceExpr.lowercase().trim()
	val isAdv = cleanExpr.startsWith("adv")
	val isDis = cleanExpr.startsWith("dis")
	val expr = cleanExpr.removePrefix("adv").removePrefix("dis").replace(" ", "")

	expr.split("(?=[+-])".toRegex())
		.mapIndexed { index, part ->
			val negative = part.startsWith("-")
			val cleanPart = part.removePrefix("+").removePrefix("-")
			val value = evaluateDicePart(cleanPart, index == 0, isAdv, isDis)
			if (negative) -value else value
		}
		.sum()
} catch (_: Exception) {
	0
}

