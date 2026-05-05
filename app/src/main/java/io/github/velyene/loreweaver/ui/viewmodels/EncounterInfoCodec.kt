package io.github.velyene.loreweaver.ui.viewmodels

internal data class EncounterInfoFields(
	val locationTerrain: String = "",
	val notesBody: String = "",
)

private const val ENCOUNTER_INFO_MARKER = "[[LOREWEAVER_ENCOUNTER_INFO_V1]]"
private const val ENCOUNTER_INFO_LOCATION_HEADER = "[[LOCATION]]"
private const val ENCOUNTER_INFO_NOTES_HEADER = "[[NOTES]]"
private const val ENCOUNTER_INFO_LOCATION_LABEL = "Location / Terrain:"

internal fun parseEncounterInfo(rawNotes: String): EncounterInfoFields {
	if (rawNotes.isBlank()) return EncounterInfoFields()
	val trimmed = rawNotes.trim()
	if (!trimmed.startsWith(ENCOUNTER_INFO_MARKER)) {
		return EncounterInfoFields(notesBody = rawNotes)
	}

	val body = trimmed.removePrefix(ENCOUNTER_INFO_MARKER).trimStart()
	if (!body.startsWith(ENCOUNTER_INFO_LOCATION_HEADER)) {
		return EncounterInfoFields(notesBody = rawNotes)
	}

	val afterLocationHeader = body.removePrefix(ENCOUNTER_INFO_LOCATION_HEADER).trimStart()
	val parts = afterLocationHeader.split(ENCOUNTER_INFO_NOTES_HEADER, limit = 2)
	if (parts.size != 2) {
		return EncounterInfoFields(notesBody = rawNotes)
	}

	return EncounterInfoFields(
		locationTerrain = parts[0].trim(),
		notesBody = parts[1].trim(),
	)
}

internal fun encodeEncounterInfo(
	locationTerrain: String,
	notesBody: String,
): String {
	val trimmedLocation = locationTerrain.trim()
	val trimmedNotes = notesBody.trim()
	if (trimmedLocation.isBlank()) return trimmedNotes
	return buildString {
		appendLine(ENCOUNTER_INFO_MARKER)
		appendLine(ENCOUNTER_INFO_LOCATION_HEADER)
		appendLine(trimmedLocation)
		appendLine(ENCOUNTER_INFO_NOTES_HEADER)
		append(trimmedNotes)
	}.trimEnd()
}

internal fun encounterInfoDisplayText(rawNotes: String): String {
	val info = parseEncounterInfo(rawNotes)
	return buildString {
		if (info.locationTerrain.isNotBlank()) {
			append(ENCOUNTER_INFO_LOCATION_LABEL)
			append(' ')
			append(info.locationTerrain.trim())
		}
		if (info.notesBody.isNotBlank()) {
			if (isNotBlank()) append("\n\n")
			append(info.notesBody.trim())
		}
	}.trim()
}

