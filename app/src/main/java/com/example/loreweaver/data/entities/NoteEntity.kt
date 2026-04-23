package com.example.loreweaver.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
	tableName = "notes",
	foreignKeys = [
		ForeignKey(
			entity = CampaignEntity::class,
			parentColumns = ["id"],
			childColumns = ["campaignId"],
			onDelete = ForeignKey.CASCADE
		)
	],
	indices = [Index(value = ["campaignId"])]
)
data class NoteEntity(
	@PrimaryKey val id: String = UUID.randomUUID().toString(),
	val campaignId: String,
	val subtype: String = "General",
	val content: String,
	val createdAt: Long = System.currentTimeMillis(),
	val historicalEra: String? = null,
	val faction: String? = null,
	val attitude: String? = null,
	val region: String? = null
)
