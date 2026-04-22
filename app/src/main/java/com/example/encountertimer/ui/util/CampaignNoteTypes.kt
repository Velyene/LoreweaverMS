package com.example.encountertimer.ui.util

internal const val DEFAULT_NOTE_NPC_ATTITUDE = "Neutral"
internal const val NOTE_TYPE_GENERAL = "General"
internal const val NOTE_TYPE_LORE = "Lore"
internal const val NOTE_TYPE_NPC = "NPC"
internal const val NOTE_TYPE_LOCATION = "Location"

internal val NOTE_TYPES = listOf(
	NOTE_TYPE_GENERAL,
	NOTE_TYPE_LORE,
	NOTE_TYPE_NPC,
	NOTE_TYPE_LOCATION
)

internal fun buildNpcExtra(faction: String, attitude: String): String = "$faction|$attitude"

internal fun parseNpcExtra(extra: String): Pair<String, String> {
	val parts = extra.split("|", limit = 2)
	return (parts.getOrNull(0) ?: "") to (parts.getOrNull(1) ?: DEFAULT_NOTE_NPC_ATTITUDE)
}

