package io.github.velyene.loreweaver.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
	tableName = "session_records",
	indices = [
		Index(value = ["date"]),
		Index(value = ["encounterId", "date"])
	]
)
data class SessionEntity(
	@PrimaryKey
	val id: String = UUID.randomUUID().toString(),
	val encounterId: String?,
	val title: String,
	val date: Long,
	val logJson: String, // Stored as JSON string
	val snapshotJson: String?, // Stored as JSON string
	val reuseFlag: Boolean = false,
	val isCompleted: Boolean = false,
	val encounterResult: String? = null,
	val rewardsJson: String? = null,
	val rewardReviewJson: String? = null
)
