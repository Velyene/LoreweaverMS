/*
 * FILE: LiveTrackerSupplementarySections.kt
 *
 * TABLE OF CONTENTS:
 * 1. Encounter Notes Models
 * 2. Encounter Notes UI
 * 3. Combat Log UI
 * 4. Encounter Notes and Log Builders
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.viewmodels.encounterInfoDisplayText
import io.github.velyene.loreweaver.ui.viewmodels.parseEncounterInfo

private const val ENCOUNTER_NOTES_PREVIEW_LIMIT = 140

internal enum class EncounterNotesSectionType {
	LOCATION_TERRAIN,
	ENCOUNTER_NOTES,
}

internal data class EncounterNotesSection(
	val type: EncounterNotesSectionType,
	val content: String,
)

@Composable
private fun EncounterNotesSectionCard(section: EncounterNotesSection) {
	data class SectionColors(
		val containerColor: androidx.compose.ui.graphics.Color,
		val titleColor: androidx.compose.ui.graphics.Color,
		val contentColor: androidx.compose.ui.graphics.Color,
		val borderColor: androidx.compose.ui.graphics.Color,
	)
	val sectionColors = when (section.type) {
		EncounterNotesSectionType.LOCATION_TERRAIN -> SectionColors(
			containerColor = MaterialTheme.colorScheme.secondaryContainer,
			titleColor = MaterialTheme.colorScheme.onSecondaryContainer,
			contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
			borderColor = MaterialTheme.colorScheme.secondary,
		)
		EncounterNotesSectionType.ENCOUNTER_NOTES -> SectionColors(
			containerColor = MaterialTheme.colorScheme.tertiaryContainer,
			titleColor = MaterialTheme.colorScheme.onTertiaryContainer,
			contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
			borderColor = MaterialTheme.colorScheme.tertiary,
		)
	}

	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = sectionColors.containerColor),
		border = BorderStroke(1.dp, sectionColors.borderColor.copy(alpha = 0.6f)),
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(4.dp),
		) {
			Text(
				text = stringResource(
					when (section.type) {
						EncounterNotesSectionType.LOCATION_TERRAIN -> R.string.encounter_location_terrain_label
						EncounterNotesSectionType.ENCOUNTER_NOTES -> R.string.encounter_notes_section_title
					}
				),
				style = MaterialTheme.typography.labelMedium,
				color = sectionColors.titleColor,
			)
			Text(
				text = section.content,
				style = MaterialTheme.typography.bodyMedium,
				color = sectionColors.contentColor,
			)
		}
	}
}

internal fun buildEncounterLogFeed(
	encounterNotes: String,
	statuses: List<String>,
	notePrefix: String = "Notes:"
): List<String> {
	val noteEntry = encounterInfoDisplayText(encounterNotes).sanitizedEncounterNotes().takeIf(String::isNotBlank)?.let { notes ->
		"$notePrefix ${notes.take(ENCOUNTER_NOTES_PREVIEW_LIMIT)}"
	}
	val statusEntries = statuses.map(String::trim).filter(String::isNotBlank)
	return listOfNotNull(noteEntry) + statusEntries
}

internal fun buildEncounterNotesSections(encounterNotes: String): List<EncounterNotesSection> {
	val encounterInfo = parseEncounterInfo(encounterNotes)
	return buildList {
		encounterInfo.locationTerrain
			.trim()
			.takeIf(String::isNotBlank)
			?.let { add(EncounterNotesSection(EncounterNotesSectionType.LOCATION_TERRAIN, it)) }
		encounterInfo.notesBody
			.trim()
			.takeIf(String::isNotBlank)
			?.let { add(EncounterNotesSection(EncounterNotesSectionType.ENCOUNTER_NOTES, it)) }
	}
}

private fun String.sanitizedEncounterNotes(): String =
	trim().replace("\n", " ").replace(Regex("\\s+"), " ")

