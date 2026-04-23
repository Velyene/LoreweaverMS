package com.example.loreweaver.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "adventure_logs")
data class LogEntryEntity(
	@PrimaryKey
	val id: String = UUID.randomUUID().toString(),
	val timestamp: Long = System.currentTimeMillis(),
	val message: String,
	val type: String = "Roll"
)
