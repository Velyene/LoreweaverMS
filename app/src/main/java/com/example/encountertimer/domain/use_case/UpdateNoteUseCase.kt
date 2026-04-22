package com.example.encountertimer.domain.use_case

import com.example.encountertimer.domain.model.Note
import com.example.encountertimer.domain.repository.NotesRepository
import javax.inject.Inject

class UpdateNoteUseCase @Inject constructor(
	private val repository: NotesRepository
) {
	suspend operator fun invoke(note: Note) {
		repository.updateNote(note)
	}
}
