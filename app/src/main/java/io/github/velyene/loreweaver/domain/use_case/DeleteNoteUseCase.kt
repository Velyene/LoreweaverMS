package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.Note
import io.github.velyene.loreweaver.domain.repository.NotesRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
	private val repository: NotesRepository
) {
	suspend operator fun invoke(note: Note) {
		repository.deleteNote(note)
	}
}
