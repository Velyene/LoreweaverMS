package com.example.encountertimer.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.encountertimer.data.entities.CharacterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
	@Query("SELECT * FROM characters ORDER BY createdAt DESC")
	fun getAllCharacters(): Flow<List<CharacterEntity>>

	@Query("SELECT * FROM characters WHERE id = :id")
	suspend fun getCharacterById(id: String): CharacterEntity?

	@Query("SELECT * FROM characters WHERE isPlayerCharacter = 1")
	fun getPlayerCharacters(): Flow<List<CharacterEntity>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertCharacter(character: CharacterEntity): Long

	@Update
	suspend fun updateCharacter(character: CharacterEntity): Int

	@Delete
	suspend fun deleteCharacter(character: CharacterEntity): Int
}
