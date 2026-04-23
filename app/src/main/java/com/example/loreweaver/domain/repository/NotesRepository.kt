package com.example.loreweaver.domain.repository

import com.example.loreweaver.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
	fun getNotesForCampaign(campaignId: String): Flow<List<Note>>
	suspend fun insertNote(note: Note)
	suspend fun updateNote(note: Note)
	suspend fun deleteNote(note: Note)
}
