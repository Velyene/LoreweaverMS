package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.model.CharacterEntry

internal fun canConfirmConditionSelection(
	selectedCondition: String,
	hasDuration: Boolean,
	durationText: String
): Boolean {
	if (selectedCondition.isBlank()) return false
	return !hasDuration || parseConditionDuration(hasDuration = true, durationText = durationText) != null
}

internal fun parseConditionDuration(hasDuration: Boolean, durationText: String): Int? {
	if (!hasDuration) return null
	return durationText.trim().toIntOrNull()?.takeIf { it > 0 }
}

internal fun matchesCharacterSearch(character: CharacterEntry, query: String): Boolean {
	val normalizedQuery = query.trim()
	if (normalizedQuery.isBlank()) return true
	val searchableFields = listOf(
		character.name,
		character.type,
		character.species,
		character.background,
		listOf(character.species, character.background)
			.filter(String::isNotBlank)
			.joinToString(" • ")
	)
	return searchableFields.any { field -> field.contains(normalizedQuery, ignoreCase = true) }
}

internal val StatusChipModel.isSrdCondition: Boolean
	get() = ConditionConstants.isOfficialCondition(name)

internal fun statusChipDisplayText(status: StatusChipModel): String {
	return io.github.velyene.loreweaver.ui.screens.statusChipDisplayText(
		status = status,
		persistentSuffix = "Persistent",
		includeIconGlyph = false
	)
}

