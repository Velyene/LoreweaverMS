package io.github.velyene.loreweaver.ui.util

import androidx.annotation.StringRes
import io.github.velyene.loreweaver.R

internal const val DEFAULT_NOTE_NPC_ATTITUDE = "Neutral"
internal const val NOTE_TYPE_GENERAL = "General"
internal const val NOTE_TYPE_LORE = "Lore"
internal const val NOTE_TYPE_NPC = "NPC"
internal const val NOTE_TYPE_LOCATION = "Location"

internal data class CampaignNoteTypeMetadata(
	val canonicalValue: String,
	@param:StringRes val chipLabelResId: Int,
	@param:StringRes val badgeLabelResId: Int,
	@param:StringRes val extraFieldLabelResId: Int? = null
)

internal val NOTE_TYPE_METADATA = listOf(
	CampaignNoteTypeMetadata(
		canonicalValue = NOTE_TYPE_GENERAL,
		chipLabelResId = R.string.note_type_general,
		badgeLabelResId = R.string.note_label_general
	),
	CampaignNoteTypeMetadata(
		canonicalValue = NOTE_TYPE_LORE,
		chipLabelResId = R.string.note_type_lore,
		badgeLabelResId = R.string.note_label_lore,
		extraFieldLabelResId = R.string.note_extra_historical_era_label
	),
	CampaignNoteTypeMetadata(
		canonicalValue = NOTE_TYPE_NPC,
		chipLabelResId = R.string.note_type_npc,
		badgeLabelResId = R.string.note_label_npc
	),
	CampaignNoteTypeMetadata(
		canonicalValue = NOTE_TYPE_LOCATION,
		chipLabelResId = R.string.note_type_location,
		badgeLabelResId = R.string.note_label_location,
		extraFieldLabelResId = R.string.note_extra_region_label
	)
)

internal val NOTE_TYPES = NOTE_TYPE_METADATA.map(CampaignNoteTypeMetadata::canonicalValue)

internal fun noteTypeMetadata(type: String): CampaignNoteTypeMetadata {
	return NOTE_TYPE_METADATA.firstOrNull { it.canonicalValue == type }
		?: NOTE_TYPE_METADATA.first { it.canonicalValue == NOTE_TYPE_GENERAL }
}

internal fun buildNpcExtra(faction: String, attitude: String): String = "$faction|$attitude"

internal fun parseNpcExtra(extra: String): Pair<String, String> {
	val parts = extra.split("|", limit = 2)
	return (parts.getOrNull(0) ?: "") to (
		parts.getOrNull(1)
			?.takeIf { it.isNotBlank() }
			?: DEFAULT_NOTE_NPC_ATTITUDE
		)
}
