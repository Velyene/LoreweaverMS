package io.github.velyene.loreweaver.ui.viewmodels

import org.junit.Assert.assertEquals
import org.junit.Test

class EncounterInfoCodecTest {

	@Test
	fun parseEncounterInfo_treatsLegacyNotesAsPlainNotes() {
		val parsed = parseEncounterInfo("Broken stairs. Low light.")

		assertEquals("", parsed.locationTerrain)
		assertEquals("Broken stairs. Low light.", parsed.notesBody)
	}

	@Test
	fun encodeAndParseEncounterInfo_roundTripsLocationAndNotes() {
		val encoded = encodeEncounterInfo(
			locationTerrain = "Ashen Ruins",
			notesBody = "Broken stairs, narrow approach",
		)

		val parsed = parseEncounterInfo(encoded)
		assertEquals("Ashen Ruins", parsed.locationTerrain)
		assertEquals("Broken stairs, narrow approach", parsed.notesBody)
	}

	@Test
	fun encounterInfoDisplayText_formatsLocationBeforeNotes() {
		val display = encounterInfoDisplayText(
			encodeEncounterInfo(
				locationTerrain = "Ashen Ruins",
				notesBody = "Broken stairs, narrow approach",
			)
		)

		assertEquals(
			"Location / Terrain: Ashen Ruins\n\nBroken stairs, narrow approach",
			display,
		)
	}
}

