package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.Note
import io.github.velyene.loreweaver.domain.repository.NotesRepository
import javax.inject.Inject


class AddNoteUseCase @Inject constructor(
	private val repository: NotesRepository
) {
	suspend operator fun invoke(note: Note) {
		validateNote(note)
		repository.insertNote(note)
	}
}

internal fun validateNote(note: Note) {
	require(note.content.isNotBlank()) { ValidationMessages.NOTE_CONTENT_EMPTY_MESSAGE }
	when (note) {
		is Note.General -> Unit
		is Note.Lore -> require(note.historicalEra.isNotBlank()) {
			ValidationMessages.NOTE_LORE_HISTORICAL_ERA_EMPTY_MESSAGE
		}

		is Note.NPC -> require(note.faction.isNotBlank()) { ValidationMessages.NOTE_NPC_FACTION_EMPTY_MESSAGE }
		is Note.Location -> require(note.region.isNotBlank()) { ValidationMessages.NOTE_LOCATION_REGION_EMPTY_MESSAGE }
	}
}
