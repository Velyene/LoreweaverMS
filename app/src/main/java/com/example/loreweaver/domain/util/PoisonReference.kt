/*
 * FILE: PoisonReference.kt
 *
 * TABLE OF CONTENTS:
 * 1. PoisonType enum and PoisonTemplate model
 * 2. PoisonReference singleton — poison dataset and search utilities
 */

package com.example.loreweaver.domain.util

// ---------------------------------------------------------------------------
// Private string constants (extracted to satisfy SonarQube S1192)
// ---------------------------------------------------------------------------
private const val DAMAGE_HALF = "Half damage"
private const val PRICE_200_GP = "200 gp"
private const val DURATION_1_HOUR = "1 hour"

/**
 * Poison delivery methods.
 */
enum class PoisonType {
	CONTACT,   // Smeared on objects, affects on touch
	INGESTED,  // Must be swallowed
	INHALED,   // Powders or gases (5-foot cube)
	INJURY     // Applied to weapons, delivered through wounds
}

/**
 * Rules summary for a poison or venom entry.
 */
data class PoisonTemplate(
	val name: String,
	val type: PoisonType,
	val saveDC: Int,
	val damageOnFail: String,
	val damageType: String = "Poison",
	val damageOnSuccess: String = DAMAGE_HALF,
	val duration: String = "",
	val additionalEffect: String = "",
	val pricePerDose: String,
	val description: String
)

/**
 * Utility object for poison reference data.
 */
object PoisonReference {
	/**
	 * Canonical Gameplay Toolbox poison summaries for quick table reference.
	 */
	val TEMPLATES = listOf(
		PoisonTemplate(
			name = "Assassin's Blood",
			type = PoisonType.INGESTED,
			saveDC = 10,
			damageOnFail = "1d12",
			damageOnSuccess = DAMAGE_HALF,
			duration = "24 hours",
			additionalEffect = "Poisoned for 24 hours on failed save.",
			pricePerDose = "150 gp",
			description =
				"An ingested poison that deals immediate poison damage and can leave the victim poisoned for a " +
					"full day."
		),
		PoisonTemplate(
			name = "Burnt Othur Fumes",
			type = PoisonType.INHALED,
			saveDC = 13,
			damageOnFail = "3d6 initial, then 1d6/turn",
			damageOnSuccess = "0",
			duration = "Until 3 successful saves",
			additionalEffect =
				"Repeat the Constitution save at the start of each turn; each failed repeat deals 1d6 Poison " +
					"damage.",
			pricePerDose = "500 gp",
			description =
				"A poisonous inhaled cloud that keeps dealing damage until the victim strings together three " +
					"successful saves."
		),
		PoisonTemplate(
			name = "Crawler Mucus",
			type = PoisonType.CONTACT,
			saveDC = 13,
			damageOnFail = "0",
			damageOnSuccess = "0",
			duration = DURATION_1_HOUR,
			additionalEffect =
				"Poisoned for 1 minute and Paralyzed while poisoned this way; repeat the save at the end of each " +
					"turn.",
			pricePerDose = PRICE_200_GP,
			description =
				"A sticky contact poison that locks the victim's body in place instead of dealing direct damage."
		),
		PoisonTemplate(
			name = "Essence of Ether",
			type = PoisonType.INHALED,
			saveDC = 15,
			damageOnFail = "0",
			damageOnSuccess = "0",
			duration = "8 hours",
			additionalEffect =
				"Poisoned and Unconscious for 8 hours. The creature wakes if it takes damage or is shaken awake.",
			pricePerDose = "300 gp",
			description =
				"A heavy inhaled sedative that drops the victim unconscious for hours rather than injuring them " +
					"outright."
		),
		PoisonTemplate(
			name = "Malice",
			type = PoisonType.INHALED,
			saveDC = 15,
			damageOnFail = "0",
			damageOnSuccess = "0",
			duration = DURATION_1_HOUR,
			additionalEffect = "Poisoned and Blinded for 1 hour.",
			pricePerDose = "250 gp",
			description = "A blinding agent that turns a successful strike or inhalation into a sensory handicap."
		),
		PoisonTemplate(
			name = "Midnight Tears",
			type = PoisonType.INGESTED,
			saveDC = 17,
			damageOnFail = "9d6",
			damageOnSuccess = DAMAGE_HALF,
			duration = "Until midnight",
			additionalEffect =
				"No effect until midnight. Any effect that ends the Poisoned condition neutralizes it before then.",
			pricePerDose = "1,500 gp",
			description =
				"A delayed ingested poison that lies dormant until midnight before delivering its full burst of " +
					"harm."
		),
		PoisonTemplate(
			name = "Oil of Taggit",
			type = PoisonType.CONTACT,
			saveDC = 13,
			damageOnFail = "0",
			damageOnSuccess = "0",
			duration = "24 hours",
			additionalEffect = "Poisoned and Unconscious for 24 hours. The creature wakes if it takes damage.",
			pricePerDose = "400 gp",
			description =
				"A contact sedative that overwhelms the victim and keeps them unconscious until they are harmed " +
					"or the poison ends."
		),
		PoisonTemplate(
			name = "Pale Tincture",
			type = PoisonType.INGESTED,
			saveDC = 16,
			damageOnFail = "1d6 initially, then 1d6 per failed daily save",
			damageOnSuccess = "0",
			duration = "Until 7 successful saves",
			additionalEffect = "Poisoned. Repeat the save every 24 hours; the poison's damage can't be healed while it lasts.",
			pricePerDose = "250 gp",
			description = "A slow-acting wasting agent that keeps its victim weak and blocks normal recovery."
		),
		PoisonTemplate(
			name = "Purple Worm Poison",
			type = PoisonType.INJURY,
			saveDC = 21,
			damageOnFail = "10d6",
			damageOnSuccess = DAMAGE_HALF,
			duration = "Instant",
			additionalEffect = "A devastating injury poison commonly associated with purple worms.",
			pricePerDose = "2,000 gp",
			description = "One of the deadliest venoms in common reference lists, prized for sheer destructive force."
		),
		PoisonTemplate(
			name = "Serpent Venom",
			type = PoisonType.INJURY,
			saveDC = 11,
			damageOnFail = "3d6",
			damageOnSuccess = DAMAGE_HALF,
			duration = "Instant",
			additionalEffect = "A straightforward injury poison associated with venomous snakes.",
			pricePerDose = PRICE_200_GP,
			description = "A practical injury poison distilled from a venomous serpent and often sold in smaller markets."
		),
		PoisonTemplate(
			name = "Spider's Sting",
			type = PoisonType.INJURY,
			saveDC = 13,
			damageOnFail = "0",
			damageOnSuccess = "0",
			duration = DURATION_1_HOUR,
			additionalEffect =
				"Poisoned for 1 hour. If the save fails by 5 or more, the creature is also Unconscious until " +
					"damaged or shaken awake.",
			pricePerDose = PRICE_200_GP,
			description =
				"An injury poison that leaves the victim sickened and can knock them out completely on a very " +
					"poor save."
		),
		PoisonTemplate(
			name = "Torpor",
			type = PoisonType.INGESTED,
			saveDC = 15,
			damageOnFail = "0",
			damageOnSuccess = "0",
			duration = "4d6 hours",
			additionalEffect = "Poisoned for 4d6 hours, and the creature's Speed is halved while poisoned this way.",
			pricePerDose = "600 gp",
			description = "An ingested agent that leaves the target barely able to function for hours at a time."
		),
		PoisonTemplate(
			name = "Truth Serum",
			type = PoisonType.INGESTED,
			saveDC = 11,
			damageOnFail = "0",
			damageOnSuccess = "0",
			duration = DURATION_1_HOUR,
			additionalEffect = "Poisoned. The creature can't knowingly communicate a lie.",
			pricePerDose = "150 gp",
			description = "A coercive draught that makes deliberate lies difficult while the effect lasts."
		),
		PoisonTemplate(
			name = "Wyvern Poison",
			type = PoisonType.INJURY,
			saveDC = 14,
			damageOnFail = "7d6",
			damageOnSuccess = DAMAGE_HALF,
			duration = "Instant",
			additionalEffect = "A potent injury poison associated with wyverns.",
			pricePerDose = "1,200 gp",
			description = "A potent monster venom that delivers a heavy burst of poison damage through a wound."
		)
	)

	fun matchesSearchQuery(poison: PoisonTemplate, query: String): Boolean {
		if (query.isBlank()) return true

		return sequenceOf(
			poison.name,
			poison.description,
			poison.additionalEffect,
			poison.type.name,
			poison.pricePerDose,
			poison.damageOnFail,
			poison.damageOnSuccess,
			poison.duration
		).any { it.contains(query, ignoreCase = true) }
	}


	/**
	 * Get poison template by name.
	 */
	fun getTemplateByName(name: String): PoisonTemplate? {
		return TEMPLATES.find { it.name.equals(name, ignoreCase = true) }
	}


	/**
	 * Get description of poison type.
	 */
	fun getTypeDescription(type: PoisonType): String {
		return when (type) {
			PoisonType.CONTACT -> "Can be smeared on objects and takes effect on contact with exposed skin."
			PoisonType.INGESTED -> "Must be swallowed (entire dose). Can be in food or liquid."
			PoisonType.INHALED -> "Powders or gases that fill a 5-foot cube. Holding your breath is ineffective."
			PoisonType.INJURY -> "Applied to weapons/ammunition. Delivered through wounds."
		}
	}
}
