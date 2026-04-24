/*
 * FILE: DiseaseReference.kt
 * Fifth-edition disease reference data and utilities.
 */

package io.github.velyene.loreweaver.domain.util

/**
 * Compact disease entry for reference use.
 */
data class DiseaseTemplate(
	val name: String,
	val transmission: String,
	val saveDC: Int,
	val incubationTime: String,
	val symptoms: String,
	val effects: String,
	val progression: String,
	val cure: String,
	val description: String
)

/**
 * Utility object for disease reference data.
 */
object DiseaseReference {

	/**
	 * Original disease summaries for quick rules lookup.
	 */
	val TEMPLATES = listOf(
		DiseaseTemplate(
			name = "Cackle Fever",
			transmission = "Airborne (within 10 ft of infected creature during mad laughter)",
			saveDC = 13,
			incubationTime = "1d4 hours",
			symptoms = "Fever, disorientation, fits of mad laughter",
			effects =
				"The victim gains a level of exhaustion that lingers until the illness ends. Stressful moments " +
					"can trigger a Constitution save; on a failure, the target takes psychic damage and collapses " +
					"into uncontrollable laughter for up to a minute.",
			progression =
				"At the end of each long rest, the victim makes a Constitution save. Success lowers the disease " +
					"DC, while repeated failures can push the illness toward lasting mental fallout.",
			cure = "Recovery happens once the disease DC is reduced to 0. Restorative magic can also end it early.",
			description = "A feverish laughing sickness that spreads between nearby humanoids and wears them down over time."
		),
		DiseaseTemplate(
			name = "Sewer Plague",
			transmission = "Bite from carrier creature or contact with contaminated filth/offal",
			saveDC = 11,
			incubationTime = "1d4 days",
			symptoms = "Fatigue and cramps",
			effects =
				"The target gains exhaustion, recovers less from Hit Dice, and gets no hit point recovery from a " +
					"long rest while the sickness lingers.",
			progression =
				"Each long rest ends with a Constitution save. Failure adds exhaustion, while success helps the " +
					"victim work it off.",
			cure =
				"The disease fades once the creature is no longer carrying exhaustion from it, and restorative " +
					"magic can speed that process.",
			description = "A grime-borne infection common in filthy tunnels, refuse piles, and stagnant marshes."
		),
		DiseaseTemplate(
			name = "Sight Rot",
			transmission = "Drinking water tainted by sight rot",
			saveDC = 15,
			incubationTime = "1 day",
			symptoms = "Bleeding from eyes, blurry vision",
			effects =
				"The victimâ€™s sight deteriorates, imposing a growing penalty on attacks and sight-based ability " +
					"checks. If the penalty reaches its limit, the creature goes blind.",
			progression =
				"After each long rest, the penalty worsens by 1 until the disease is treated or the victim is fully " +
					"blinded.",
			cure = "Repeated doses of an herbal salve or restorative magic can clear the infection.",
			description = "A painful eye infection that gradually turns blurred vision into full blindness."
		)
	)

}
