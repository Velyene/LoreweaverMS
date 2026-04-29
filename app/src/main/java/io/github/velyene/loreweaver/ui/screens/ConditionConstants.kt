package io.github.velyene.loreweaver.ui.screens

import androidx.compose.ui.graphics.Color
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver
import io.github.velyene.loreweaver.domain.util.SrdSpellIndexReference
import io.github.velyene.loreweaver.ui.theme.MutedText
import io.github.velyene.loreweaver.ui.theme.PrimaryText
import io.github.velyene.loreweaver.ui.viewmodels.ReferenceCategory

/**
 * Standard fifth-edition conditions and common status effects.
 */
object ConditionConstants {
	enum class StatusPolicyBucket {
		OFFICIAL_CONDITION,
		BUILT_IN_STATUS,
		CUSTOM_EFFECT,
		CUSTOM_HOMEBREW
	}

	enum class StatusCategory {
		DAMAGE_OVER_TIME,
		SENSORY,
		CONTROL,
		MOBILITY,
		PERSISTENT,
		BUFF,
		DEBUFF,
		DEFENSIVE
	}

	data class StatusMetadata(
		val label: String,
		val category: StatusCategory,
		val color: Color,
		val borderColor: Color,
		val persistsAcrossEncounters: Boolean = false,
		val iconGlyph: String? = null
	)

	data class StatusLookupTarget(
		val category: ReferenceCategory,
		val query: String = "",
		val detailCategory: String? = null,
		val detailSlug: String? = null
	)

	private val allMetadata = listOf(
		StatusMetadata(
			label = "Poisoned",
			category = StatusCategory.DAMAGE_OVER_TIME,
			color = Color(0xFF6BCB3D),
			borderColor = Color(0xFF8CE85A),
			iconGlyph = "☠"
		),
		StatusMetadata(
			label = "Burning",
			category = StatusCategory.DAMAGE_OVER_TIME,
			color = Color(0xFFFF7A1A),
			borderColor = Color(0xFFFFA45C),
			iconGlyph = "✦"
		),
		StatusMetadata(
			label = "Bleeding",
			category = StatusCategory.DAMAGE_OVER_TIME,
			color = Color(0xFFC93A3A),
			borderColor = Color(0xFFE06060),
			iconGlyph = "✹"
		),
		StatusMetadata(
			label = "Blinded",
			category = StatusCategory.SENSORY,
			color = Color(0xFF9A92B1),
			borderColor = Color(0xFFB7B0CC),
			iconGlyph = "◌"
		),
		StatusMetadata(
			label = "Deafened",
			category = StatusCategory.SENSORY,
			color = Color(0xFF6E7C91),
			borderColor = Color(0xFF90A0B7),
			iconGlyph = "◔"
		),
		StatusMetadata(
			label = "Frightened",
			category = StatusCategory.CONTROL,
			color = Color(0xFF6C4A8B),
			borderColor = Color(0xFF8D68AF),
			iconGlyph = "!"
		),
		StatusMetadata(
			label = "Charmed",
			category = StatusCategory.CONTROL,
			color = Color(0xFFC86DD7),
			borderColor = Color(0xFFE29AF0),
			iconGlyph = "♥"
		),
		StatusMetadata(
			label = "Stunned",
			category = StatusCategory.CONTROL,
			color = Color(0xFFE2B93B),
			borderColor = Color(0xFFF2D36F),
			iconGlyph = "⚡"
		),
		StatusMetadata(
			label = "Restrained",
			category = StatusCategory.MOBILITY,
			color = Color(0xFF4F6E73),
			borderColor = Color(0xFF6E959B),
			iconGlyph = "⛓"
		),
		StatusMetadata(
			label = "Grappled",
			category = StatusCategory.MOBILITY,
			color = Color(0xFF7A5C3A),
			borderColor = Color(0xFFA17A4E),
			iconGlyph = "↯"
		),
		StatusMetadata(
			label = "Prone",
			category = StatusCategory.MOBILITY,
			color = Color(0xFF8A6B4E),
			borderColor = Color(0xFFB08A63),
			iconGlyph = "↓"
		),
		StatusMetadata(
			label = "Paralyzed",
			category = StatusCategory.MOBILITY,
			color = Color(0xFF708090),
			borderColor = Color(0xFF93A6B8),
			iconGlyph = "⟂"
		),
		StatusMetadata(
			label = "Petrified",
			category = StatusCategory.MOBILITY,
			color = Color(0xFF7C7F86),
			borderColor = Color(0xFFA0A4AC),
			persistsAcrossEncounters = true,
			iconGlyph = "◼"
		),
		StatusMetadata(
			label = "Exhaustion",
			category = StatusCategory.PERSISTENT,
			color = Color(0xFF4B4E91),
			borderColor = Color(0xFF7175C2),
			persistsAcrossEncounters = true,
			iconGlyph = "☾"
		),
		StatusMetadata(
			label = "Cursed",
			category = StatusCategory.PERSISTENT,
			color = Color(0xFF5A3B6E),
			borderColor = Color(0xFF82549F),
			persistsAcrossEncounters = true,
			iconGlyph = "✶"
		),
		StatusMetadata(
			label = "Diseased",
			category = StatusCategory.PERSISTENT,
			color = Color(0xFF76853A),
			borderColor = Color(0xFF98AC4B),
			persistsAcrossEncounters = true,
			iconGlyph = "☣"
		),
		StatusMetadata(
			label = "Marked",
			category = StatusCategory.PERSISTENT,
			color = Color(0xFFB28A2E),
			borderColor = Color(0xFFD4B24C),
			persistsAcrossEncounters = true,
			iconGlyph = "✧"
		),
		StatusMetadata(
			label = "Blessed",
			category = StatusCategory.BUFF,
			color = Color(0xFFD4B24C),
			borderColor = Color(0xFFE8CB73),
			iconGlyph = "✦"
		),
		StatusMetadata(
			label = "Invisible",
			category = StatusCategory.BUFF,
			color = Color(0xFF4AA3A1),
			borderColor = Color(0xFF79C9C7),
			iconGlyph = "◔"
		),
		StatusMetadata(
			label = "Shielded",
			category = StatusCategory.DEFENSIVE,
			color = Color(0xFF3F7CAC),
			borderColor = Color(0xFF68A6D8),
			iconGlyph = "⬡"
		),
		StatusMetadata(
			label = "Unconscious",
			category = StatusCategory.CONTROL,
			color = Color(0xFF606D78),
			borderColor = Color(0xFF8B98A4),
			iconGlyph = "☾"
		),
		StatusMetadata(
			label = "Incapacitated",
			category = StatusCategory.CONTROL,
			color = Color(0xFF7A6E88),
			borderColor = Color(0xFFA094B0),
			iconGlyph = "⊘"
		),
		StatusMetadata(
			label = "Hidden",
			category = StatusCategory.BUFF,
			color = Color(0xFF4F6E73),
			borderColor = Color(0xFF79A0A6),
			iconGlyph = "◐"
		),
		StatusMetadata(
			label = "Inspired",
			category = StatusCategory.BUFF,
			color = Color(0xFF2DA8B0),
			borderColor = Color(0xFF59E5EF),
			iconGlyph = "✧"
		),
		StatusMetadata(
			label = "Hasted",
			category = StatusCategory.BUFF,
			color = Color(0xFF00C2C7),
			borderColor = Color(0xFF59E5EF),
			iconGlyph = "➤"
		),
		StatusMetadata(
			label = "Hexed",
			category = StatusCategory.DEBUFF,
			color = Color(0xFF7A2E57),
			borderColor = Color(0xFF9D4671),
			iconGlyph = "✶"
		),
		StatusMetadata(
			label = "Dodging",
			category = StatusCategory.DEFENSIVE,
			color = Color(0xFF3F7CAC),
			borderColor = Color(0xFF68A6D8),
			iconGlyph = "⬒"
		),
		StatusMetadata(
			label = "Raging",
			category = StatusCategory.BUFF,
			color = Color(0xFFC93A3A),
			borderColor = Color(0xFFE06060),
			iconGlyph = "✹"
		),
		StatusMetadata(
			label = "Concentrating",
			category = StatusCategory.DEFENSIVE,
			color = Color(0xFF4B4E91),
			borderColor = Color(0xFF7175C2),
			iconGlyph = "◎"
		),
		StatusMetadata(
			label = "Slowed",
			category = StatusCategory.DEBUFF,
			color = Color(0xFF6E7C91),
			borderColor = Color(0xFF90A0B7),
			iconGlyph = "⏷"
		)
	)

	private val metadataByLabel = allMetadata.associateBy { it.label.lowercase() }
	val OFFICIAL_CONDITIONS = listOf(
		"Blinded",
		"Charmed",
		"Deafened",
		"Exhaustion",
		"Frightened",
		"Grappled",
		"Incapacitated",
		"Invisible",
		"Paralyzed",
		"Petrified",
		"Poisoned",
		"Prone",
		"Restrained",
		"Stunned",
		"Unconscious"
	)

	val BUILT_IN_STATUS_LABELS = listOf(
		"Blessed",
		"Concentrating",
		"Dodging",
		"Hasted",
		"Hexed",
		"Inspired",
		"Marked",
		"Raging"
	)

	val CUSTOM_EFFECT_LABELS = listOf(
		"Burning",
		"Cursed",
		"Diseased",
		"Hidden",
		"Shielded",
		"Slowed"
	)

	val CUSTOM_HOMEBREW_ONLY_LABELS = listOf(
		"Bleeding"
	)

	/**
	 * Legacy aliases kept so older call sites still compile while the picker/UI shifts to the new
	 * status-policy terminology.
	 */
	val STANDARD_CONDITIONS = OFFICIAL_CONDITIONS
	val COMMON_CONDITIONS = BUILT_IN_STATUS_LABELS + CUSTOM_EFFECT_LABELS + CUSTOM_HOMEBREW_ONLY_LABELS

	/**
	 * Combined list of all built-in status labels for selection.
	 */
	val ALL_STATUS_LABELS = (OFFICIAL_CONDITIONS + COMMON_CONDITIONS).sorted()
	val ALL_CONDITIONS = ALL_STATUS_LABELS

	fun metadataFor(label: String): StatusMetadata {
		return metadataByLabel[label.lowercase()] ?: StatusMetadata(
			label = label,
			category = StatusCategory.DEBUFF,
			color = MutedText,
			borderColor = PrimaryText.copy(alpha = 0.55f),
			persistsAcrossEncounters = false,
			iconGlyph = "•"
		)
	}

	fun defaultPersistsAcrossEncounters(label: String): Boolean =
		metadataFor(label).persistsAcrossEncounters

	fun bucketFor(label: String): StatusPolicyBucket {
		val canonicalLabel = metadataFor(label).label
		return when (canonicalLabel) {
			in OFFICIAL_CONDITIONS -> StatusPolicyBucket.OFFICIAL_CONDITION
			in BUILT_IN_STATUS_LABELS -> StatusPolicyBucket.BUILT_IN_STATUS
			in CUSTOM_EFFECT_LABELS -> StatusPolicyBucket.CUSTOM_EFFECT
			else -> StatusPolicyBucket.CUSTOM_HOMEBREW
		}
	}

	fun isOfficialCondition(label: String): Boolean =
		bucketFor(label) == StatusPolicyBucket.OFFICIAL_CONDITION

	fun shouldAllowOfficialConditionLookup(label: String): Boolean = isOfficialCondition(label)

	fun referenceTargetFor(label: String): StatusLookupTarget? {
		val canonicalLabel = metadataFor(label).label
		return when (canonicalLabel) {
			in OFFICIAL_CONDITIONS -> officialConditionTarget(canonicalLabel)
			"Blessed" -> spellTarget("Bless")
			"Concentrating" -> glossaryTarget(
				term = "Concentration",
				detailCategory = ReferenceDetailResolver.CATEGORY_GLOSSARY
			)
			"Dodging" -> glossaryTarget(
				term = "Dodge",
				detailCategory = ReferenceDetailResolver.CATEGORY_ACTIONS
			)
			"Hexed" -> spellTarget("Hex")
			"Hasted" -> spellTarget("Haste")
			"Burning" -> glossaryTarget(
				term = "Burning",
				detailCategory = ReferenceDetailResolver.CATEGORY_HAZARDS
			)
			"Cursed" -> spellSearchTarget("Curse")
			"Diseased" -> StatusLookupTarget(category = ReferenceCategory.DISEASES)
			"Hidden" -> glossaryTarget(
				term = "Hide",
				detailCategory = ReferenceDetailResolver.CATEGORY_ACTIONS,
				query = canonicalLabel
			)
			"Shielded" -> spellSearchTarget("Shield")
			"Slowed" -> spellTarget("Slow")
			else -> null
		}
	}

	fun persistentConditions(): List<String> =
		ALL_STATUS_LABELS.filter(::defaultPersistsAcrossEncounters)

	private fun officialConditionTarget(label: String): StatusLookupTarget {
		val slug = ReferenceDetailResolver.slugFor(label)
		val hasDetail = ReferenceDetailResolver.resolve(
			ReferenceDetailResolver.CATEGORY_CONDITIONS,
			slug
		) != null
		return StatusLookupTarget(
			category = ReferenceCategory.CORE_RULES,
			query = label,
			detailCategory = if (hasDetail) ReferenceDetailResolver.CATEGORY_CONDITIONS else null,
			detailSlug = if (hasDetail) slug else null
		)
	}

	private fun glossaryTarget(
		term: String,
		detailCategory: String,
		query: String = term
	): StatusLookupTarget {
		val slug = ReferenceDetailResolver.slugFor(term)
		val hasDetail = ReferenceDetailResolver.resolve(detailCategory, slug) != null
		return StatusLookupTarget(
			category = ReferenceCategory.CORE_RULES,
			query = query,
			detailCategory = if (hasDetail) detailCategory else null,
			detailSlug = if (hasDetail) slug else null
		)
	}

	private fun spellTarget(spellName: String): StatusLookupTarget? {
		val canonicalSpellName = SrdSpellIndexReference.canonicalNameFor(spellName)
			?: return spellSearchTarget(spellName)
		val slug = ReferenceDetailResolver.slugFor(canonicalSpellName)
		val hasDetail = ReferenceDetailResolver.resolve(
			ReferenceDetailResolver.CATEGORY_SPELLS,
			slug
		) != null
		return StatusLookupTarget(
			category = ReferenceCategory.SPELLCASTING,
			query = canonicalSpellName,
			detailCategory = if (hasDetail) ReferenceDetailResolver.CATEGORY_SPELLS else null,
			detailSlug = if (hasDetail) slug else null
		)
	}

	private fun spellSearchTarget(query: String): StatusLookupTarget = StatusLookupTarget(
		category = ReferenceCategory.SPELLCASTING,
		query = query
	)
}
