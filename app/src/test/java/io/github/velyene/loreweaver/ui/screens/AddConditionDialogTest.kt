package io.github.velyene.loreweaver.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AddConditionDialogTest {

	@Test
	fun canConfirmConditionSelection_requiresPositiveRoundDurationWhenEnabled() {
		assertFalse(
			canConfirmConditionSelection(
				selectedCondition = "Poisoned",
				hasDuration = true,
				durationText = ""
			)
		)
		assertFalse(
			canConfirmConditionSelection(
				selectedCondition = "Poisoned",
				hasDuration = true,
				durationText = "0"
			)
		)
		assertTrue(
			canConfirmConditionSelection(
				selectedCondition = "Poisoned",
				hasDuration = true,
				durationText = "3"
			)
		)
	}

	@Test
	fun canConfirmConditionSelection_allowsEncounterLengthConditionsWithoutRoundDuration() {
		assertTrue(
			canConfirmConditionSelection(
				selectedCondition = "Poisoned",
				hasDuration = false,
				durationText = ""
			)
		)
	}

	@Test
	fun parseConditionDuration_returnsPositiveRoundValueOnly() {
		assertEquals(2, parseConditionDuration(hasDuration = true, durationText = "2"))
		assertNull(parseConditionDuration(hasDuration = true, durationText = "0"))
		assertNull(parseConditionDuration(hasDuration = true, durationText = ""))
		assertNull(parseConditionDuration(hasDuration = false, durationText = "5"))
	}
}

