package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.util.MadnessDuration
import io.github.velyene.loreweaver.domain.util.MadnessReference

internal fun madnessResultFor(duration: MadnessDuration, roll: Int): String? {
	return when (duration) {
		MadnessDuration.SHORT_TERM -> MadnessReference.getShortTermEffect(roll)?.effect
		MadnessDuration.LONG_TERM -> MadnessReference.getLongTermEffect(roll)?.effect
		MadnessDuration.INDEFINITE -> MadnessReference.getIndefiniteFlaw(roll)
	}
}

internal fun normalizeSignedIntegerInput(value: String): String {
	if (value.isEmpty()) return ""
	if (value == "-") return value

	val filtered = buildString {
		value.forEachIndexed { index, character ->
			if (character.isDigit() || (character == '-' && index == 0)) append(character)
		}
	}

	return filtered.take(4)
}

internal fun normalizePositiveIntegerInput(value: String): String {
	return value.filter { it.isDigit() }.take(2)
}

