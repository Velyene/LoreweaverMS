/*
 * FILE: ConditionConstants.kt
 *
 * TABLE OF CONTENTS:
 * 1. Condition option model
 * 2. Shared condition/status metadata
 * 3. Lookup and routing support
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver
import io.github.velyene.loreweaver.ui.viewmodels.ReferenceCategory

data class ConditionOption(
	val name: String,
	@field:StringRes val labelRes: Int
)

object ConditionConstants {
	enum class StatusCategory {
		BUFF,
		DEBUFF,
		DAMAGE_OVER_TIME,
		PERSISTENT,
		UTILITY
	}

	enum class StatusPolicyBucket {
		OFFICIAL_CONDITION,
		BUILT_IN_STATUS,
		CUSTOM_EFFECT,
		CUSTOM_HOMEBREW
	}

	data class StatusMetadata(
		val label: String,
		val category: StatusCategory,
		val color: Color,
		val borderColor: Color,
		val iconGlyph: String,
		val persistsAcrossEncounters: Boolean = false
	)

	data class ReferenceTarget(
		val category: ReferenceCategory,
		val query: String = "",
		val detailCategory: String? = null,
		val detailSlug: String? = null
	)

	private val officialConditionOptions = listOf(
		ConditionOption("Blinded", R.string.cond_blinded),
		ConditionOption("Charmed", R.string.cond_charmed),
		ConditionOption("Deafened", R.string.cond_deafened),
		ConditionOption("Frightened", R.string.cond_frightened),
		ConditionOption("Grappled", R.string.cond_grappled),
		ConditionOption("Incapacitated", R.string.cond_incapacitated),
		ConditionOption("Invisible", R.string.cond_invisible),
		ConditionOption("Paralyzed", R.string.cond_paralyzed),
		ConditionOption("Petrified", R.string.cond_petrified),
		ConditionOption("Poisoned", R.string.cond_poisoned),
		ConditionOption("Prone", R.string.cond_prone),
		ConditionOption("Restrained", R.string.cond_restrained),
		ConditionOption("Stunned", R.string.cond_stunned),
		ConditionOption("Unconscious", R.string.cond_unconscious)
	)

	private val builtInStatusOptions = listOf(
		ConditionOption("Blessed", R.string.cond_blessed),
		ConditionOption("Concentrating", R.string.cond_concentrating),
		ConditionOption("Cursed", R.string.cond_cursed),
		ConditionOption("Dodging", R.string.cond_dodging),
		ConditionOption("Hasted", R.string.cond_hasted),
		ConditionOption("Hidden", R.string.cond_hidden),
		ConditionOption("Inspired", R.string.cond_inspired),
		ConditionOption("Marked", R.string.cond_marked),
		ConditionOption("Slowed", R.string.cond_slowed)
	)

	private val customEffectOptions = listOf(
		ConditionOption("Burning", R.string.cond_burning),
		ConditionOption("Hexed", R.string.cond_hexed),
		ConditionOption("Raging", R.string.cond_raging)
	)

	private val customHomebrewOptions = listOf(
		ConditionOption("Bleeding", R.string.cond_bleeding)
	)

	val OFFICIAL_CONDITIONS: List<ConditionOption> = officialConditionOptions
	val BUILT_IN_STATUS_LABELS: List<ConditionOption> = builtInStatusOptions
	val CUSTOM_EFFECT_LABELS: List<ConditionOption> = customEffectOptions
	val CUSTOM_HOMEBREW_ONLY_LABELS: List<ConditionOption> = customHomebrewOptions

	val STANDARD_CONDITIONS: List<ConditionOption> = OFFICIAL_CONDITIONS
	val COMMON_CONDITIONS: List<ConditionOption> = BUILT_IN_STATUS_LABELS + CUSTOM_EFFECT_LABELS + CUSTOM_HOMEBREW_ONLY_LABELS
	val ALL_CONDITIONS: List<ConditionOption> =
		(STANDARD_CONDITIONS + COMMON_CONDITIONS).sortedBy(ConditionOption::name)
	val ALL_STATUS_LABELS: List<ConditionOption> = ALL_CONDITIONS

	private val officialConditionNames =
		OFFICIAL_CONDITIONS.map(ConditionOption::name).toSet() + setOf("Exhaustion")
	private val builtInStatusNames = BUILT_IN_STATUS_LABELS.map(ConditionOption::name).toSet()
	private val customEffectNames = CUSTOM_EFFECT_LABELS.map(ConditionOption::name).toSet()
	private val customHomebrewNames = CUSTOM_HOMEBREW_ONLY_LABELS.map(ConditionOption::name).toSet()

	private val canonicalLabels = ALL_CONDITIONS.associateBy { it.name.lowercase() }

	private val metadataByLabel = mapOf(
		"blinded" to statusMetadata("Blinded", StatusCategory.DEBUFF, 0xFF8E8E93, "◌"),
		"charmed" to statusMetadata("Charmed", StatusCategory.DEBUFF, 0xFFE8A0BF, "♡"),
		"deafened" to statusMetadata("Deafened", StatusCategory.DEBUFF, 0xFF9AA0A6, "◔"),
		"diseased" to statusMetadata("Diseased", StatusCategory.DEBUFF, 0xFF7DA16F, "☣"),
		"exhaustion" to statusMetadata("Exhaustion", StatusCategory.PERSISTENT, 0xFFB39DDB, "✧", persistsAcrossEncounters = true),
		"frightened" to statusMetadata("Frightened", StatusCategory.DEBUFF, 0xFF6D6875, "⚑"),
		"grappled" to statusMetadata("Grappled", StatusCategory.DEBUFF, 0xFFB08968, "⛓"),
		"incapacitated" to statusMetadata("Incapacitated", StatusCategory.DEBUFF, 0xFF607D8B, "⏸"),
		"invisible" to statusMetadata("Invisible", StatusCategory.BUFF, 0xFF80CBC4, "◌"),
		"paralyzed" to statusMetadata("Paralyzed", StatusCategory.DEBUFF, 0xFF9575CD, "⚡"),
		"petrified" to statusMetadata("Petrified", StatusCategory.DEBUFF, 0xFF9E9E9E, "⬢"),
		"poisoned" to statusMetadata("Poisoned", StatusCategory.DAMAGE_OVER_TIME, 0xFF6BCB3D, "☠"),
		"prone" to statusMetadata("Prone", StatusCategory.DEBUFF, 0xFFBC8F6F, "↧"),
		"restrained" to statusMetadata("Restrained", StatusCategory.DEBUFF, 0xFFB07D62, "⛓"),
		"stunned" to statusMetadata("Stunned", StatusCategory.DEBUFF, 0xFFFFC857, "✦"),
		"unconscious" to statusMetadata("Unconscious", StatusCategory.DEBUFF, 0xFF5C6BC0, "☾"),
		"bleeding" to statusMetadata("Bleeding", StatusCategory.DAMAGE_OVER_TIME, 0xFFE57373, "✹"),
		"blessed" to statusMetadata("Blessed", StatusCategory.BUFF, 0xFFFFD54F, "✦"),
		"burning" to statusMetadata("Burning", StatusCategory.DAMAGE_OVER_TIME, 0xFFFF7043, "🔥"),
		"concentrating" to statusMetadata("Concentrating", StatusCategory.UTILITY, 0xFF4DB6AC, "◎"),
		"cursed" to statusMetadata("Cursed", StatusCategory.DEBUFF, 0xFFBA68C8, "✢", persistsAcrossEncounters = true),
		"dodging" to statusMetadata("Dodging", StatusCategory.UTILITY, 0xFF64B5F6, "⬒"),
		"hasted" to statusMetadata("Hasted", StatusCategory.BUFF, 0xFF4FC3F7, "➜"),
		"hexed" to statusMetadata("Hexed", StatusCategory.DEBUFF, 0xFFAB47BC, "✣", persistsAcrossEncounters = true),
		"hidden" to statusMetadata("Hidden", StatusCategory.UTILITY, 0xFF90A4AE, "◔"),
		"inspired" to statusMetadata("Inspired", StatusCategory.BUFF, 0xFF81C784, "✪"),
		"marked" to statusMetadata("Marked", StatusCategory.DEBUFF, 0xFFFFB74D, "⌖"),
		"raging" to statusMetadata("Raging", StatusCategory.BUFF, 0xFFE53935, "⚔"),
		"slowed" to statusMetadata("Slowed", StatusCategory.DEBUFF, 0xFF7986CB, "↘")
	)

	fun metadataFor(label: String): StatusMetadata {
		val normalized = label.trim()
		if (normalized.isBlank()) {
			return fallbackMetadata(label = "")
		}
		val canonicalLabel = canonicalStatusLabel(normalized)
		return metadataByLabel[canonicalLabel.lowercase()]
			?: fallbackMetadata(label = canonicalLabel)
	}

	fun defaultPersistsAcrossEncounters(label: String): Boolean =
		metadataFor(label).persistsAcrossEncounters

	fun bucketFor(label: String): StatusPolicyBucket {
		val canonical = canonicalStatusLabel(label)
		return when (canonical) {
			in officialConditionNames -> StatusPolicyBucket.OFFICIAL_CONDITION
			in builtInStatusNames -> StatusPolicyBucket.BUILT_IN_STATUS
			in customEffectNames -> StatusPolicyBucket.CUSTOM_EFFECT
			else -> StatusPolicyBucket.CUSTOM_HOMEBREW
		}
	}

	fun shouldAllowOfficialConditionLookup(label: String): Boolean =
		bucketFor(label) == StatusPolicyBucket.OFFICIAL_CONDITION

	fun referenceTargetFor(label: String): ReferenceTarget? {
		return when (canonicalStatusLabel(label)) {
			in officialConditionNames -> ReferenceTarget(
				category = ReferenceCategory.CORE_RULES,
				query = canonicalStatusLabel(label),
				detailCategory = ReferenceDetailResolver.CATEGORY_CONDITIONS,
				detailSlug = slugify(canonicalStatusLabel(label))
			)
			"Blessed" -> ReferenceTarget(
				category = ReferenceCategory.SPELLCASTING,
				query = "Bless",
				detailCategory = ReferenceDetailResolver.CATEGORY_SPELLS,
				detailSlug = slugify("Bless")
			)
			"Dodging" -> ReferenceTarget(
				category = ReferenceCategory.CORE_RULES,
				query = "Dodge",
				detailCategory = ReferenceDetailResolver.CATEGORY_ACTIONS,
				detailSlug = slugify("Dodge")
			)
			"Burning" -> ReferenceTarget(
				category = ReferenceCategory.CORE_RULES,
				query = "Burning",
				detailCategory = ReferenceDetailResolver.CATEGORY_HAZARDS,
				detailSlug = slugify("Burning")
			)
			"Diseased" -> ReferenceTarget(
				category = ReferenceCategory.DISEASES,
				query = "Diseased"
			)
			else -> null
		}
	}

	private fun canonicalStatusLabel(label: String): String {
		val normalized = label.trim()
		return canonicalLabels[normalized.lowercase()]?.name ?: normalized.replaceFirstChar(Char::titlecase)
	}

	private fun statusMetadata(
		label: String,
		category: StatusCategory,
		color: Long,
		iconGlyph: String,
		persistsAcrossEncounters: Boolean = false
	): StatusMetadata {
		val baseColor = Color(color)
		return StatusMetadata(
			label = label,
			category = category,
			color = baseColor,
			borderColor = baseColor.copy(alpha = 0.85f),
			iconGlyph = iconGlyph,
			persistsAcrossEncounters = persistsAcrossEncounters
		)
	}

	private fun fallbackMetadata(label: String): StatusMetadata {
		return StatusMetadata(
			label = label,
			category = StatusCategory.DEBUFF,
			color = Color(0xFFB0BEC5),
			borderColor = Color(0xFF90A4AE),
			iconGlyph = "•"
		)
	}

	private fun slugify(label: String): String =
		label.trim().lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')
}
