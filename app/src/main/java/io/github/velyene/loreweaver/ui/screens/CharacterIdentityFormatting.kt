package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.model.CharacterEntry

internal fun formatCharacterIdentity(species: String, background: String): String {
	return listOf(species.trim(), background.trim())
		.filter { it.isNotBlank() }
		.joinToString(" • ")
}

internal fun CharacterEntry.formattedIdentity(): String {
	return formatCharacterIdentity(species = species, background = background)
}

