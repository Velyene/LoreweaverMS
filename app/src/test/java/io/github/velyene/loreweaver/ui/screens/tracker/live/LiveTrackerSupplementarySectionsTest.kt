package io.github.velyene.loreweaver.ui.screens.tracker.live

import org.junit.Assert.assertEquals
import org.junit.Test

class LiveTrackerSupplementarySectionsTest {

	@Test
	fun buildEncounterNotesSections_splitsEncodedEncounterInfoIntoTwoSections() {
		val sections = buildEncounterNotesSections(
			encounterNotes = io.github.velyene.loreweaver.ui.viewmodels.encodeEncounterInfo(
				locationTerrain = "Ashen Ruins",
				notesBody = "Broken stairs, low visibility",
			),
		)

		assertEquals(
			listOf(
				EncounterNotesSection(
					type = EncounterNotesSectionType.LOCATION_TERRAIN,
					content = "Ashen Ruins",
				),
				EncounterNotesSection(
					type = EncounterNotesSectionType.ENCOUNTER_NOTES,
					content = "Broken stairs, low visibility",
				),
			),
			sections,
		)
	}

	@Test
	fun buildEncounterNotesSections_treatsLegacyPlainTextAsEncounterNotesOnly() {
		val sections = buildEncounterNotesSections(
			encounterNotes = "Ancient runes glow in the walls.",
		)

		assertEquals(
			listOf(
				EncounterNotesSection(
					type = EncounterNotesSectionType.ENCOUNTER_NOTES,
					content = "Ancient runes glow in the walls.",
				),
			),
			sections,
		)
	}

	@Test
	fun buildEncounterNotesSections_omitsBlankSections() {
		val sections = buildEncounterNotesSections(encounterNotes = "   ")

		assertEquals(emptyList<EncounterNotesSection>(), sections)
	}

	@Test
	fun buildEncounterLogFeed_includesSanitizedNotesAndNonBlankStatuses() {
		val feed = buildEncounterLogFeed(
			encounterNotes = "  Ancient runes glow.\n\nThe bridge shakes.  ",
			statuses = listOf("Aria takes cover.", "   ", "Goblin is bloodied!"),
		)

		assertEquals(
			listOf(
				"Notes: Ancient runes glow. The bridge shakes.",
				"Aria takes cover.",
				"Goblin is bloodied!",
			),
			feed,
		)
	}

	@Test
	fun buildEncounterLogFeed_omitsBlankNotes() {
		val feed = buildEncounterLogFeed(
			encounterNotes = "   ",
			statuses = listOf("Round 2 begins"),
		)

		assertEquals(listOf("Round 2 begins"), feed)
	}

	@Test
	fun buildEncounterLogFeed_decodesEncodedEncounterInfoBeforeBuildingNotesEntry() {
		val feed = buildEncounterLogFeed(
			encounterNotes = io.github.velyene.loreweaver.ui.viewmodels.encodeEncounterInfo(
				locationTerrain = "Ashen Ruins",
				notesBody = "Broken stairs, low visibility",
			),
			statuses = listOf("Round 2 begins"),
		)

		assertEquals(
			listOf(
				"Notes: Location / Terrain: Ashen Ruins Broken stairs, low visibility",
				"Round 2 begins",
			),
			feed,
		)
	}
}

