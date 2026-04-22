package com.example.encountertimer.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "session_records")
data class SessionEntity(
	@PrimaryKey
	val id: String = UUID.randomUUID().toString(),
	val encounterId: String?,
	val title: String,
	val date: Long,
	val logJson: String, // Stored as JSON string
	val snapshotJson: String?, // Stored as JSON string
	val reuseFlag: Boolean = false
)
