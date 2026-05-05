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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.LOG_HEIGHT_DP
import io.github.velyene.loreweaver.ui.screens.visibleVerticalScrollbar
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
internal fun EncounterNotesCard(encounterNotes: String) {
	var isExpanded by rememberSaveable { mutableStateOf(false) }
	val sections = buildEncounterNotesSections(encounterNotes)
	val displayNotes = encounterInfoDisplayText(encounterNotes)
	val notesPreview = displayNotes.sanitizedEncounterNotes().take(ENCOUNTER_NOTES_PREVIEW_LIMIT)

	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = stringResource(R.string.encounter_notes_title),
					style = MaterialTheme.typography.labelSmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					modifier = Modifier.semantics { heading() }
				)
				TextButton(onClick = { isExpanded = !isExpanded }) {
					Text(
						text = stringResource(
							if (isExpanded) R.string.encounter_notes_collapse_action else R.string.encounter_notes_expand_action
						)
					)
				}
			}
			Spacer(modifier = Modifier.height(4.dp))
			if (isExpanded) {
				if (sections.isEmpty()) {
					Text(
						text = stringResource(R.string.encounter_notes_collapsed_empty),
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurface,
					)
				} else {
					Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
						sections.forEach { section ->
							EncounterNotesSectionCard(section = section)
						}
					}
				}
			} else {
				Text(
					text = notesPreview.ifBlank {
						stringResource(R.string.encounter_notes_collapsed_empty)
					},
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurface
				)
			}
		}
	}
}

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

@Composable
internal fun CombatLogSection(
	encounterNotes: String,
	statuses: List<String>
) {
	val listState = rememberLazyListState()
	val feedItems = buildEncounterLogFeed(
		encounterNotes = encounterNotes,
		statuses = statuses,
		notePrefix = "Notes:"
	)

	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(modifier = Modifier.padding(12.dp)) {
			Text(
				text = stringResource(R.string.session_combat_log_title),
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				style = MaterialTheme.typography.labelSmall,
				modifier = Modifier.semantics { heading() }
			)
			Spacer(modifier = Modifier.height(6.dp))
			Text(
				text = stringResource(R.string.encounter_log_feed_supporting_text),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Spacer(modifier = Modifier.height(8.dp))
			Column(modifier = Modifier.height(LOG_HEIGHT_DP.dp)) {
				LazyColumn(
					state = listState,
					modifier = Modifier.visibleVerticalScrollbar(listState)
				) {
					if (feedItems.isEmpty()) {
						item {
							Text(
								text = stringResource(R.string.combat_log_collapsed_empty),
								color = MaterialTheme.colorScheme.onSurfaceVariant,
								style = MaterialTheme.typography.bodySmall
							)
						}
					} else {
						itemsIndexed(feedItems.reversed(), key = { index, status -> "$index-$status" }) { _, status ->
							Text(
								text = stringResource(R.string.combat_log_bullet, status),
								color = if (status.contains("!")) MaterialTheme.colorScheme.primary
								else MaterialTheme.colorScheme.onSurface,
								fontSize = 11.sp,
								modifier = Modifier.padding(vertical = 1.dp)
							)
						}
					}
				}
			}
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

