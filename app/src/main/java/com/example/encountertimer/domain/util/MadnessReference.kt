/*
 * FILE: MadnessReference.kt
 *
 * TABLE OF CONTENTS:
 * 1. MadnessDuration enum and MadnessEffect models
 * 2. MadnessReference singleton — short-term, long-term, and indefinite effect tables
 */

package com.example.encountertimer.domain.util

/**
 * Madness duration types.
 */
enum class MadnessDuration {
	SHORT_TERM,   // 1d10 minutes
	LONG_TERM,    // 1d10 × 10 hours
	INDEFINITE    // Until cured
}

/**
 * Data class for madness effect.
 */
data class MadnessEffect(
	val duration: MadnessDuration,
	val effect: String,
	val diceRange: IntRange
)

/**
 * Utility object for madness reference data.
 */
object MadnessReference {

	/**
	 * Short-term madness effects (1d10 minutes).
	 */
	val SHORT_TERM_EFFECTS = listOf(
		MadnessEffect(
			MadnessDuration.SHORT_TERM,
			"The character mentally shuts down and is paralyzed until the episode ends or incoming harm snaps them out of it.",
			1..20
		),
		MadnessEffect(
			MadnessDuration.SHORT_TERM,
			"The character is incapacitated and spends the episode crying out, laughing, or sobbing uncontrollably.",
			21..30
		),
		MadnessEffect(
			MadnessDuration.SHORT_TERM,
			"The character is overwhelmed by fear and tries to get away from the perceived threat each round.",
			31..40
		),
		MadnessEffect(
			MadnessDuration.SHORT_TERM,
			"The character can only babble and cannot manage clear speech or controlled spellcasting.",
			41..50
		),
		MadnessEffect(
			MadnessDuration.SHORT_TERM,
			"The character lashes out at the nearest creature whenever able.",
			51..60
		),
		MadnessEffect(
			MadnessDuration.SHORT_TERM,
			"Disturbing hallucinations cloud the character’s judgment, imposing disadvantage on ability checks.",
			61..70
		),
		MadnessEffect(
			MadnessDuration.SHORT_TERM,
			"The character becomes dangerously suggestible and follows instructions unless the command is plainly suicidal.",
			71..75
		),
		MadnessEffect(
			MadnessDuration.SHORT_TERM,
			"The character fixates on eating something repulsive or clearly inedible.",
			76..80
		),
		MadnessEffect(
			MadnessDuration.SHORT_TERM,
			"The character is stunned and can barely process what is happening around them.",
			81..90
		),
		MadnessEffect(
			MadnessDuration.SHORT_TERM,
			"The character drops unconscious.",
			91..100
		)
	)

	/**
	 * Long-term madness effects (1d10 × 10 hours).
	 */
	val LONG_TERM_EFFECTS = listOf(
		MadnessEffect(
			MadnessDuration.LONG_TERM,
			"The character becomes obsessed with repeating a simple ritual such as counting, cleaning, " +
				"touching objects, or muttering prayers.",
			1..10
		),
		MadnessEffect(
			MadnessDuration.LONG_TERM,
			"Persistent visions or false sensations distract the character, causing disadvantage on ability checks.",
			11..20
		),
		MadnessEffect(
			MadnessDuration.LONG_TERM,
			"The character trusts almost no one and second-guesses every interaction, giving disadvantage " +
				"on Wisdom and Charisma checks.",
			21..30
		),
		MadnessEffect(
			MadnessDuration.LONG_TERM,
			"One person, place, or object becomes unbearable to the character, who reacts with immediate " +
				"revulsion whenever it is near.",
			31..40
		),
		MadnessEffect(
			MadnessDuration.LONG_TERM,
			"The character clings to a powerful delusion, such as believing a magical effect is helping " +
				"or harming them when none is present.",
			41..45
		),
		MadnessEffect(
			MadnessDuration.LONG_TERM,
			"The character becomes dependent on a personal token and suffers disadvantage on attacks, " +
				"checks, and saves while separated from it.",
			46..55
		),
		MadnessEffect(
			MadnessDuration.LONG_TERM,
			"The episode manifests as a temporary sensory collapse: blindness is possible, but deafness is more common.",
			56..65
		),
		MadnessEffect(
			MadnessDuration.LONG_TERM,
			"Shaking hands, tics, or sudden muscle jolts interfere with physical control and impose " +
				"disadvantage on Strength and Dexterity checks and saves.",
			66..75
		),
		MadnessEffect(
			MadnessDuration.LONG_TERM,
			"Chunks of memory go missing, leaving the character unable to place familiar faces or recall " +
				"parts of their recent past.",
			76..85
		),
		MadnessEffect(
			MadnessDuration.LONG_TERM,
			"Whenever the character takes damage, they must steady themselves with a DC 15 Wisdom save or " +
				"lapse into confused behavior for 1 minute.",
			86..90
		),
		MadnessEffect(
			MadnessDuration.LONG_TERM,
			"The character loses the ability to speak coherently.",
			91..95
		),
		MadnessEffect(
			MadnessDuration.LONG_TERM,
			"The character collapses into a deep unresponsive state that ordinary jostling cannot end.",
			96..100
		)
	)

	/**
	 * Indefinite madness effects (character flaws, lasts until cured).
	 */
	val INDEFINITE_FLAWS = listOf(
		"Only intoxication or numbness makes me feel safe." to (1..15),
		"If I find something unattended, I convince myself it belongs with me." to (16..25),
		"I keep remaking myself in someone else’s image, borrowing their style, habits, or identity." to
			(26..30),
		"I twist the truth because plain honesty feels dull and unbearable." to (31..35),
		"My personal objective outweighs every other concern." to (36..45),
		"I struggle to care about the people and events around me." to (46..50),
		"I assume everyone is quietly criticizing or judging me." to (51..55),
		"I believe I am superior to everyone around me in almost every way." to (56..70),
		"I am certain hidden enemies are tracking my movements and waiting for a mistake." to
			(71..80),
		"I trust only a companion no one else can perceive." to (81..85),
		"Serious moments strike me as absurd, and I react with inappropriate humor." to (86..95),
		"Hurting others feels disturbingly easy to justify." to (96..100)
	)

	/**
	 * Get random short-term madness effect.
	 */
	fun getShortTermEffect(roll: Int): MadnessEffect? {
		return SHORT_TERM_EFFECTS.find { roll in it.diceRange }
	}

	/**
	 * Get random long-term madness effect.
	 */
	fun getLongTermEffect(roll: Int): MadnessEffect? {
		return LONG_TERM_EFFECTS.find { roll in it.diceRange }
	}

	/**
	 * Get random indefinite madness flaw.
	 */
	fun getIndefiniteFlaw(roll: Int): String? {
		return INDEFINITE_FLAWS.find { roll in it.second }?.first
	}

	/**
	 * Get madness cure information.
	 */
	fun getCureInfo(): Map<MadnessDuration, String> {
		return mapOf(
			MadnessDuration.SHORT_TERM to "Calm Emotions (suppresses), Lesser Restoration (cures)",
			MadnessDuration.LONG_TERM to "Calm Emotions (suppresses), Lesser Restoration (cures)",
			MadnessDuration.INDEFINITE to "Greater Restoration or more powerful magic required"
		)
	}

	/**
	 * Get madness duration description.
	 */
	fun getDurationDescription(duration: MadnessDuration): String {
		return when (duration) {
			MadnessDuration.SHORT_TERM -> "1d10 minutes"
			MadnessDuration.LONG_TERM -> "1d10 × 10 hours"
			MadnessDuration.INDEFINITE -> "Until cured (permanent character flaw)"
		}
	}

}

