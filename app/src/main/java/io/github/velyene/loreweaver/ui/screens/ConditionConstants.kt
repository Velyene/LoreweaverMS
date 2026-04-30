package io.github.velyene.loreweaver.ui.screens

import androidx.compose.ui.graphics.Color

/**
 * Standard fifth-edition conditions plus app-specific status metadata used for chip rendering.
 */
object ConditionConstants {
	enum class StatusCategory {
		CONTROL,
		MOBILITY,
		SENSORY,
		DEBUFF,
		BUFF,
		PERSISTENT,
		CUSTOM,
	}

	data class StatusMetadata(
		val label: String,
		val category: StatusCategory,
		val color: Color,
		val borderColor: Color,
		val persistsAcrossEncounters: Boolean = false,
		val isSrdCondition: Boolean = false,
	)

	private val srdStatusMetadata = listOf(
		StatusMetadata("Blinded", StatusCategory.SENSORY, Color(0xFF5E4A86), Color(0xFF8E74C7), isSrdCondition = true),
		StatusMetadata("Charmed", StatusCategory.CONTROL, Color(0xFF7A4E7D), Color(0xFFBA7CC0), isSrdCondition = true),
		StatusMetadata("Deafened", StatusCategory.SENSORY, Color(0xFF4D5B7A), Color(0xFF7F94BF), isSrdCondition = true),
		StatusMetadata("Frightened", StatusCategory.CONTROL, Color(0xFF7B3F3F), Color(0xFFD07F7F), isSrdCondition = true),
		StatusMetadata("Grappled", StatusCategory.MOBILITY, Color(0xFF6C4E2E), Color(0xFFB68452), isSrdCondition = true),
		StatusMetadata("Incapacitated", StatusCategory.CONTROL, Color(0xFF485066), Color(0xFF7D88AE), isSrdCondition = true),
		StatusMetadata("Invisible", StatusCategory.BUFF, Color(0xFF3F6E73), Color(0xFF68B6BE), isSrdCondition = true),
		StatusMetadata("Paralyzed", StatusCategory.MOBILITY, Color(0xFF59616E), Color(0xFF95A3B8), isSrdCondition = true),
		StatusMetadata("Petrified", StatusCategory.PERSISTENT, Color(0xFF64656C), Color(0xFFA5A7B0), persistsAcrossEncounters = true, isSrdCondition = true),
		StatusMetadata("Poisoned", StatusCategory.DEBUFF, Color(0xFF4A6B3D), Color(0xFF7FB666), isSrdCondition = true),
		StatusMetadata("Prone", StatusCategory.MOBILITY, Color(0xFF7A5B3F), Color(0xFFBC9162), isSrdCondition = true),
		StatusMetadata("Restrained", StatusCategory.MOBILITY, Color(0xFF6C4F58), Color(0xFFB37C8B), isSrdCondition = true),
		StatusMetadata("Stunned", StatusCategory.CONTROL, Color(0xFF57507F), Color(0xFF9387D1), isSrdCondition = true),
		StatusMetadata("Unconscious", StatusCategory.CONTROL, Color(0xFF3F4350), Color(0xFF767D94), isSrdCondition = true),
	)

	private val commonStatusMetadata = listOf(
		StatusMetadata("Bleeding", StatusCategory.DEBUFF, Color(0xFF7B3A3A), Color(0xFFD17C7C)),
		StatusMetadata("Blessed", StatusCategory.BUFF, Color(0xFF58633B), Color(0xFFADC66D), persistsAcrossEncounters = true),
		StatusMetadata("Burning", StatusCategory.DEBUFF, Color(0xFF8A4F2F), Color(0xFFE19A63)),
		StatusMetadata("Concentrating", StatusCategory.BUFF, Color(0xFF305E72), Color(0xFF69B8D7)),
		StatusMetadata("Cursed", StatusCategory.PERSISTENT, Color(0xFF5E4369), Color(0xFFB587CA), persistsAcrossEncounters = true),
		StatusMetadata("Dodging", StatusCategory.BUFF, Color(0xFF355F4A), Color(0xFF6DBD94)),
		StatusMetadata("Hasted", StatusCategory.BUFF, Color(0xFF2F6661), Color(0xFF63BDB5)),
		StatusMetadata("Hexed", StatusCategory.DEBUFF, Color(0xFF69455F), Color(0xFFC58AB4)),
		StatusMetadata("Hidden", StatusCategory.BUFF, Color(0xFF4A5C4A), Color(0xFF89A889)),
		StatusMetadata("Inspired", StatusCategory.BUFF, Color(0xFF6B5A2E), Color(0xFFD1B46A), persistsAcrossEncounters = true),
		StatusMetadata("Marked", StatusCategory.DEBUFF, Color(0xFF7B5536), Color(0xFFD09C6B)),
		StatusMetadata("Raging", StatusCategory.BUFF, Color(0xFF7B443F), Color(0xFFD68D84)),
		StatusMetadata("Slowed", StatusCategory.DEBUFF, Color(0xFF4A5874), Color(0xFF8CA1D4)),
	)

	private val metadataByKey = (srdStatusMetadata + commonStatusMetadata).associateBy { it.label.lowercase() }

	val STANDARD_CONDITIONS: List<String> = srdStatusMetadata.map(StatusMetadata::label)
	val COMMON_CONDITIONS: List<String> = commonStatusMetadata.map(StatusMetadata::label)
	val ALL_CONDITIONS: List<String> = (STANDARD_CONDITIONS + COMMON_CONDITIONS).sorted()

	fun metadataFor(label: String): StatusMetadata {
		val trimmedLabel = label.trim()
		return metadataByKey[trimmedLabel.lowercase()] ?: StatusMetadata(
			label = trimmedLabel.ifBlank { "Custom Effect" },
			category = StatusCategory.CUSTOM,
			color = Color(0xFF3E5560),
			borderColor = Color(0xFF88AAB9),
		)
	}

	fun canonicalLabel(label: String): String = metadataFor(label).label

	fun isSrdCondition(label: String): Boolean = metadataFor(label).isSrdCondition

	fun defaultPersistsAcrossEncounters(label: String): Boolean {
		return metadataFor(label).persistsAcrossEncounters
	}
}
