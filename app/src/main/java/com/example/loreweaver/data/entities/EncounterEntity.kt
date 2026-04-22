package com.example.loreweaver.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
	tableName = "encounters",
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
data class EncounterEntity(
	@PrimaryKey val id: String = UUID.randomUUID().toString(),
	val campaignId: String,
	val name: String,
	val notes: String = "",
	val isActive: Boolean = false,
	val currentRound: Int = 1,
	val currentTurnIndex: Int = 0,
	val createdAt: Long = System.currentTimeMillis()
)
