package com.example.encountertimer.domain.model

import java.util.UUID

data class LogEntry(
	val id: String = UUID.randomUUID().toString(),
	val timestamp: Long = System.currentTimeMillis(),
	val message: String,
	val type: String = "Roll"
)
