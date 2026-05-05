package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.viewmodels.EncounterResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SessionReviewFormattingTest {
	@Test
	fun encounterResultLabelRes_returnsExpectedResourceForEachOutcome() {
		assertEquals(R.string.session_summary_result_victory, encounterResultLabelRes(EncounterResult.VICTORY))
		assertEquals(R.string.session_summary_result_defeat, encounterResultLabelRes(EncounterResult.DEFEAT))
		assertEquals(R.string.session_summary_result_ended_early, encounterResultLabelRes(EncounterResult.ENDED_EARLY))
	}

	@Test
	fun parseEncounterResultOrNull_returnsMatchingEnumOrNull() {
		assertEquals(EncounterResult.VICTORY, parseEncounterResultOrNull("VICTORY"))
		assertEquals(EncounterResult.DEFEAT, parseEncounterResultOrNull("DEFEAT"))
		assertEquals(EncounterResult.ENDED_EARLY, parseEncounterResultOrNull("ENDED_EARLY"))
		assertNull(parseEncounterResultOrNull("UNKNOWN"))
	}
}

