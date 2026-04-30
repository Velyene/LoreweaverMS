package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.util.HysteriaDuration
import io.github.velyene.loreweaver.domain.util.HysteriaReference

internal fun hysteriaResultFor(duration: HysteriaDuration, roll: Int): String? {
	return when (duration) {
		HysteriaDuration.SHORT_TERM -> HysteriaReference.getShortTermEffect(roll)?.effect
		HysteriaDuration.LONG_TERM -> HysteriaReference.getLongTermEffect(roll)?.effect
		HysteriaDuration.INDEFINITE -> HysteriaReference.getIndefiniteFlaw(roll)
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

