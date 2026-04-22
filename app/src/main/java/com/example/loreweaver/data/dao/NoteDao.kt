package com.example.loreweaver.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.loreweaver.data.entities.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
	@Query("SELECT * FROM notes WHERE campaignId = :campaignId ORDER BY createdAt DESC")
	fun getNotesForCampaign(campaignId: String): Flow<List<NoteEntity>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertNote(note: NoteEntity): Long

	@Update
	suspend fun updateNote(note: NoteEntity): Int

	@Delete
	suspend fun deleteNote(note: NoteEntity): Int
}
