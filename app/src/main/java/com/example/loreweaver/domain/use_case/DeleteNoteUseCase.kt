package com.example.loreweaver.domain.use_case

import com.example.loreweaver.domain.model.Note
import com.example.loreweaver.domain.repository.NotesRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
	private val repository: NotesRepository
) {
	suspend operator fun invoke(note: Note) {
		repository.deleteNote(note)
	}
}
