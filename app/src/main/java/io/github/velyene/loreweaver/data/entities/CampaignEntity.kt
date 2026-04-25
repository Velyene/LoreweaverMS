package io.github.velyene.loreweaver.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "campaigns")
data class CampaignEntity(
	@PrimaryKey val id: String = UUID.randomUUID().toString(),
	val name: String,
	val description: String = "",
	val createdAt: Long = System.currentTimeMillis()
)
