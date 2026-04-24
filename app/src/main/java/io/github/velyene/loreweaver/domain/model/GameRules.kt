/*
 * FILE: GameRules.kt
 *
 * TABLE OF CONTENTS:
 * 1. Ability scores, skills, and difficulty classes
 * 2. D20 roll models and AbilityScoreRules helper
 * 3. CharacterEntry ability/skill/saving-throw extension functions
 * 4. Travel, terrain, and forced-march rules
 * 5. Vision, light level, and environmental rules
 * 6. Resting and downtime mechanics
 * 7. Combat: actions, cover, damage types, and creature size
 * 8. Combat rules, death saves, grapple, and conditions
 */

package io.github.velyene.loreweaver.domain.model

/**
 * The six core abilities in fifth-edition play.
 */
enum class Ability(val displayName: String, val abbreviation: String) {
	STRENGTH("Strength", "STR"),
	DEXTERITY("Dexterity", "DEX"),
	CONSTITUTION("Constitution", "CON"),
	INTELLIGENCE("Intelligence", "INT"),
	WISDOM("Wisdom", "WIS"),
	CHARISMA("Charisma", "CHA");

	companion object {
		fun fromAbbreviation(abbr: String): Ability? {
			return entries.find { it.abbreviation.equals(abbr, ignoreCase = true) }
		}
	}
}

/**
 * All standard skills with their associated ability scores.
 */
enum class Skill(val displayName: String, val ability: Ability) {
	// Strength
	ATHLETICS("Athletics", Ability.STRENGTH),

	// Dexterity
	ACROBATICS("Acrobatics", Ability.DEXTERITY),
	SLEIGHT_OF_HAND("Sleight of Hand", Ability.DEXTERITY),
	STEALTH("Stealth", Ability.DEXTERITY),

	// Intelligence
	ARCANA("Arcana", Ability.INTELLIGENCE),
	HISTORY("History", Ability.INTELLIGENCE),
	INVESTIGATION("Investigation", Ability.INTELLIGENCE),
	NATURE("Nature", Ability.INTELLIGENCE),
	RELIGION("Religion", Ability.INTELLIGENCE),

	// Wisdom
	ANIMAL_HANDLING("Animal Handling", Ability.WISDOM),
	INSIGHT("Insight", Ability.WISDOM),
	MEDICINE("Medicine", Ability.WISDOM),
	PERCEPTION("Perception", Ability.WISDOM),
	SURVIVAL("Survival", Ability.WISDOM),

	// Charisma
	DECEPTION("Deception", Ability.CHARISMA),
	INTIMIDATION("Intimidation", Ability.CHARISMA),
	PERFORMANCE("Performance", Ability.CHARISMA),
	PERSUASION("Persuasion", Ability.CHARISMA);

	companion object {
		fun fromDisplayName(name: String): Skill? {
			return entries.find { it.displayName.equals(name, ignoreCase = true) }
		}
	}
}

/**
 * Difficulty Classes for ability checks.
 */
enum class DifficultyClass(val dc: Int, val description: String) {
	VERY_EASY(5, "Very easy"),
	EASY(10, "Easy"),
	MEDIUM(15, "Medium"),
	HARD(20, "Hard"),
	VERY_HARD(25, "Very hard"),
	NEARLY_IMPOSSIBLE(30, "Nearly impossible")
}

/**
 * Represents whether a roll has advantage, disadvantage, or neither.
 */
enum class RollModifier {
	NORMAL,
	ADVANTAGE,
	DISADVANTAGE;

	/**
	 * Applies advantage/disadvantage logic to two d20 rolls.
	 * Returns the higher roll for ADVANTAGE, lower for DISADVANTAGE, first roll for NORMAL.
	 */
	fun applyToRolls(roll1: Int, roll2: Int): Int {
		return when (this) {
			ADVANTAGE -> maxOf(roll1, roll2)
			DISADVANTAGE -> minOf(roll1, roll2)
			NORMAL -> roll1
		}
	}

	companion object {
		/**
		 * Combines multiple advantage/disadvantage effects.
		 * If you have both advantage AND disadvantage, they cancel out to NORMAL.
		 */
		fun combine(hasAdvantage: Boolean, hasDisadvantage: Boolean): RollModifier {
			return when {
				hasAdvantage && hasDisadvantage -> NORMAL
				hasAdvantage -> ADVANTAGE
				hasDisadvantage -> DISADVANTAGE
				else -> NORMAL
			}
		}
	}
}

/**
 * Result of a d20 roll with all modifiers applied.
 */
data class D20RollResult(
	val naturalRoll: Int,           // The actual d20 result (1-20)
	val secondRoll: Int? = null,    // If advantage/disadvantage was used
	val modifier: Int,              // All bonuses/penalties combined
	val total: Int,                 // Final result
	val rollModifier: RollModifier = RollModifier.NORMAL,
	val isCriticalSuccess: Boolean = false,  // Natural 20
	val isCriticalFailure: Boolean = false   // Natural 1
) {
	companion object {
		/**
		 * Simulates a d20 roll with advantage/disadvantage.
		 */
		fun roll(
			modifier: Int,
			rollModifier: RollModifier = RollModifier.NORMAL
		): D20RollResult {
			val roll1 = (1..20).random()
			val roll2 = if (rollModifier != RollModifier.NORMAL) (1..20).random() else null

			val effectiveRoll = if (roll2 != null) {
				rollModifier.applyToRolls(roll1, roll2)
			} else {
				roll1
			}

			return D20RollResult(
				naturalRoll = roll1,
				secondRoll = roll2,
				modifier = modifier,
				total = effectiveRoll + modifier,
				rollModifier = rollModifier,
				isCriticalSuccess = effectiveRoll == 20,
				isCriticalFailure = effectiveRoll == 1
			)
		}
	}
}

/**
 * Core fifth-edition ability score calculations and game mechanics.
 */
object AbilityScoreRules {

	/**
	 * Standard fifth-edition ability score to modifier conversion table.
	 * Covers scores 1-30 (modifier -5 to +10).
	 */
	fun getModifier(abilityScore: Int): Int {
		return when (abilityScore) {
			1 -> -5
			in 2..3 -> -4
			in 4..5 -> -3
			in 6..7 -> -2
			in 8..9 -> -1
			in 10..11 -> 0
			in 12..13 -> 1
			in 14..15 -> 2
			in 16..17 -> 3
			in 18..19 -> 4
			in 20..21 -> 5
			in 22..23 -> 6
			in 24..25 -> 7
			in 26..27 -> 8
			in 28..29 -> 9
			30 -> 10
			else -> if (abilityScore > 30) 10 else -5 // Clamp to valid range
		}
	}

	/**
	 * Calculates proficiency bonus based on character level.
	 * Level 1-4: +2, 5-8: +3, 9-12: +4, 13-16: +5, 17-20: +6
	 */
	fun getProficiencyBonus(level: Int): Int {
		return when (level) {
			in 1..4 -> 2
			in 5..8 -> 3
			in 9..12 -> 4
			in 13..16 -> 5
			in 17..20 -> 6
			else -> if (level > 20) 6 else 2 // Clamp to valid range
		}
	}

	/**
	 * Calculates passive check score.
	 * Passive = 10 + all modifiers that normally apply to the check.
	 * Add 5 for advantage, subtract 5 for disadvantage.
	 */
	fun getPassiveScore(
		modifier: Int,
		hasAdvantage: Boolean = false,
		hasDisadvantage: Boolean = false
	): Int {
		var score = 10 + modifier
		if (hasAdvantage && !hasDisadvantage) score += 5
		if (hasDisadvantage && !hasAdvantage) score -= 5
		return score
	}
}

/**
 * Extension functions for CharacterEntry to apply fifth-edition rules.
 */

/**
 * Gets the modifier for a specific ability.
 */
fun CharacterEntry.getAbilityModifier(ability: Ability): Int {
	val score = when (ability) {
		Ability.STRENGTH -> strength
		Ability.DEXTERITY -> dexterity
		Ability.CONSTITUTION -> constitution
		Ability.INTELLIGENCE -> intelligence
		Ability.WISDOM -> wisdom
		Ability.CHARISMA -> charisma
	}
	return AbilityScoreRules.getModifier(score)
}

/**
 * Gets the ability score for a specific ability.
 */
fun CharacterEntry.getAbilityScore(ability: Ability): Int {
	return when (ability) {
		Ability.STRENGTH -> strength
		Ability.DEXTERITY -> dexterity
		Ability.CONSTITUTION -> constitution
		Ability.INTELLIGENCE -> intelligence
		Ability.WISDOM -> wisdom
		Ability.CHARISMA -> charisma
	}
}

/**
 * Calculates the total modifier for a skill check.
 * Includes ability modifier + proficiency bonus (if proficient).
 */
fun CharacterEntry.getSkillModifier(skill: Skill): Int {
	val abilityMod = getAbilityModifier(skill.ability)
	val profBonus = if (proficiencies.contains(skill.displayName)) {
		getProficiencyBonus()
	} else {
		0
	}
	return abilityMod + profBonus
}

/**
 * Calculates the total modifier for a saving throw.
 * Includes ability modifier + proficiency bonus (if proficient).
 */
fun CharacterEntry.getSavingThrowModifier(ability: Ability): Int {
	val abilityMod = getAbilityModifier(ability)
	val profBonus = if (saveProficiencies.contains(ability.abbreviation)) {
		getProficiencyBonus()
	} else {
		0
	}
	return abilityMod + profBonus
}

/**
 * Calculates passive perception (10 + Wisdom modifier + proficiency if applicable).
 */
fun CharacterEntry.getPassivePerception(
	hasAdvantage: Boolean = false,
	hasDisadvantage: Boolean = false
): Int {
	val perceptionMod = getSkillModifier(Skill.PERCEPTION)
	return AbilityScoreRules.getPassiveScore(perceptionMod, hasAdvantage, hasDisadvantage)
}

/**
 * Calculates passive investigation (10 + Intelligence modifier + proficiency if applicable).
 */
fun CharacterEntry.getPassiveInvestigation(
	hasAdvantage: Boolean = false,
	hasDisadvantage: Boolean = false
): Int {
	val investigationMod = getSkillModifier(Skill.INVESTIGATION)
	return AbilityScoreRules.getPassiveScore(investigationMod, hasAdvantage, hasDisadvantage)
}

/**
 * Calculates passive insight (10 + Wisdom modifier + proficiency if applicable).
 */
fun CharacterEntry.getPassiveInsight(
	hasAdvantage: Boolean = false,
	hasDisadvantage: Boolean = false
): Int {
	val insightMod = getSkillModifier(Skill.INSIGHT)
	return AbilityScoreRules.getPassiveScore(insightMod, hasAdvantage, hasDisadvantage)
}

/**
 * Performs an ability check.
 */
fun CharacterEntry.makeAbilityCheck(
	ability: Ability,
	rollModifier: RollModifier = RollModifier.NORMAL
): D20RollResult {
	val modifier = getAbilityModifier(ability)
	return D20RollResult.roll(modifier, rollModifier)
}

/**
 * Performs a skill check.
 */
fun CharacterEntry.makeSkillCheck(
	skill: Skill,
	rollModifier: RollModifier = RollModifier.NORMAL
): D20RollResult {
	val modifier = getSkillModifier(skill)
	return D20RollResult.roll(modifier, rollModifier)
}

/**
 * Performs a saving throw.
 */
fun CharacterEntry.makeSavingThrow(
	ability: Ability,
	rollModifier: RollModifier = RollModifier.NORMAL
): D20RollResult {
	val modifier = getSavingThrowModifier(ability)
	return D20RollResult.roll(modifier, rollModifier)
}

/**
 * Checks if a character succeeds on a check against a DC.
 */
fun D20RollResult.succeeds(dc: Int): Boolean {
	return total >= dc
}

/**
 * Gets a human-readable description of the roll result.
 */
fun D20RollResult.getDescription(): String {
	val rollText = if (secondRoll != null) {
		"$naturalRoll, $secondRoll (${rollModifier.name.lowercase()})"
	} else {
		"$naturalRoll"
	}

	val critText = when {
		isCriticalSuccess -> " CRITICAL SUCCESS!"
		isCriticalFailure -> " CRITICAL FAILURE!"
		else -> ""
	}

	return "Roll: $rollText + $modifier = $total$critText"
}

/**
 * Travel pace for overland movement.
 */
enum class TravelPace(
	val displayName: String,
	val feetPerMinute: Int,
	val milesPerHour: Int,
	val milesPerDay: Int,
	val perceptionPenalty: Int,
	val allowsStealth: Boolean
) {
	FAST("Fast", 400, 4, 30, -5, false),
	NORMAL("Normal", 300, 3, 24, 0, false),
	SLOW("Slow", 200, 2, 18, 0, true);

	/**
	 * Calculate distance traveled in a given number of hours.
	 */
	fun distanceInHours(hours: Int): Int = milesPerHour * hours

	/**
	 * Calculate distance traveled in a given number of minutes.
	 */
	fun distanceInMinutes(minutes: Int): Int = (feetPerMinute * minutes) / 5280 // Convert to miles
}

/**
 * Types of terrain that affect movement.
 */
enum class TerrainType(val displayName: String, val isDifficult: Boolean) {
	ROAD("Road", false),
	PLAINS("Open Plains", false),
	FOREST("Forest", true),
	SWAMP("Swamp", true),
	MOUNTAINS("Mountains", true),
	DESERT("Desert", false),
	TUNDRA("Tundra", true),
	DUNGEON_CORRIDOR("Dungeon Corridor", false),
	RUBBLE("Rubble", true),
	ICE("Ice", true);

	/**
	 * Calculate effective distance traveled accounting for difficult terrain.
	 */
	fun adjustDistance(baseDistance: Int): Int {
		return if (isDifficult) baseDistance / 2 else baseDistance
	}
}

/**
 * Light levels that affect vision.
 */
enum class LightLevel(
	val displayName: String,
	val description: String,
	val effectOnPerception: String
) {
	BRIGHT(
		"Bright Light",
		"Normal vision for most creatures. Provided by daylight, torches, lanterns, and fires.",
		"No penalty"
	),
	DIM(
		"Dim Light",
		"Creates a lightly obscured area. Twilight, dawn, torchlight boundaries.",
		"Disadvantage on Perception checks relying on sight"
	),
	DARKNESS(
		"Darkness",
		"Heavily obscured area. Blocks vision entirely for creatures without darkvision.",
		"Effectively blinded"
	);

	fun isObscured(): Boolean = this != BRIGHT
}

/**
 * Special vision types.
 */
enum class VisionType(
	val displayName: String,
	val range: Int, // in feet, 0 = unlimited
	val description: String
) {
	NORMAL("Normal Vision", 0, "Relies on light to see"),
	DARKVISION("Darkvision", 60, "See in darkness as if it were dim light within range"),
	BLINDSIGHT("Blindsight", 30, "Perceive surroundings without relying on sight"),
	TRUESIGHT(
		"Truesight",
		60,
		"See in magical darkness, see invisible creatures, detect illusions"
	);

	fun canSeeInDarkness(): Boolean = this != NORMAL
}

/**
 * Environmental hazards and their effects.
 */
object EnvironmentalRules {

	/**
	 * Calculate falling damage (1d6 per 10 feet, max 20d6).
	 */
	fun calculateFallingDamage(feetFallen: Int): Int {
		val dice = minOf(feetFallen / 10, 20)
		return (1..dice).sumOf { (1..6).random() }
	}

	/**
	 * Get the maximum falling damage possible.
	 */
	fun maxFallingDamage(): Int = 120 // 20d6 max

	/**
	 * Calculate how long a creature can hold its breath (minutes).
	 */
	fun breathHoldDuration(constitutionModifier: Int): Int {
		return maxOf(1 + constitutionModifier, 1) // Minimum 1 minute (30 seconds rounds up)
	}

	/**
	 * Calculate suffocation rounds before dropping to 0 HP.
	 */
	fun suffocationRounds(constitutionModifier: Int): Int {
		return maxOf(constitutionModifier, 1)
	}

	/**
	 * Calculate long jump distance (feet).
	 */
	fun longJumpDistance(strengthScore: Int, hasRunningStart: Boolean): Int {
		return if (hasRunningStart) strengthScore else strengthScore / 2
	}

	/**
	 * Calculate high jump height (feet).
	 */
	fun highJumpHeight(strengthModifier: Int, hasRunningStart: Boolean): Int {
		val baseHeight = maxOf(3 + strengthModifier, 0)
		return if (hasRunningStart) baseHeight else baseHeight / 2
	}

	/**
	 * Calculate reach during a high jump (feet above the ground).
	 */
	fun highJumpReach(jumpHeight: Int, characterHeight: Int): Int {
		return jumpHeight + (characterHeight * 3 / 2)
	}
}

/**
 * Forced march exhaustion DC calculation.
 */
object ForcedMarchRules {
	/**
	 * Calculate DC for Constitution save to avoid exhaustion.
	 * @param hoursOfTravel Total hours traveled (beyond 8 triggers checks)
	 */
	fun getForcedMarchDC(hoursOfTravel: Int): Int {
		return if (hoursOfTravel <= 8) 0 else 10 + (hoursOfTravel - 8)
	}
}

/**
 * Rest types and their benefits.
 */
sealed class RestType(
	open val displayName: String,
	open val minimumDuration: String,
	open val description: String
) {
	data object ShortRest : RestType(
		"Short Rest",
		"1 hour",
		"Spend Hit Dice to recover HP. Some class features recharge."
	)

	data object LongRest : RestType(
		"Long Rest",
		"8 hours (6 sleeping, 2 light activity)",
		"Recover all HP and half of max Hit Dice. Most features recharge."
	)
}

/**
 * Rest benefits and restrictions.
 */
object RestingRules {
	/**
	 * Calculate Hit Dice recovered on a long rest.
	 */
	fun hitDiceRecoveredOnLongRest(maxHitDice: Int): Int {
		return maxOf(maxHitDice / 2, 1)
	}

	/**
	 * Check if a long rest would be interrupted.
	 * @param hoursOfStrenuousActivity Hours spent fighting, casting, etc.
	 */
	fun isLongRestInterrupted(hoursOfStrenuousActivity: Int): Boolean {
		return hoursOfStrenuousActivity >= 1
	}

	/**
	 * Check if a character can benefit from another long rest.
	 * @param hoursSinceLastLongRest Hours since the last long rest ended
	 */
	fun canTakeLongRest(hoursSinceLastLongRest: Int): Boolean {
		return hoursSinceLastLongRest >= 24
	}
}

/**
 * Downtime activity types.
 */
enum class DowntimeActivity(
	val displayName: String,
	val minimumDays: Int,
	val costPerDay: Int, // in gp
	val description: String
) {
	CRAFTING(
		"Crafting",
		1,
		0,
		"Create nonmagical items. Progress: 5gp of value per day."
	),
	PRACTICING_PROFESSION(
		"Practicing a Profession",
		1,
		0,
		"Work to maintain modest lifestyle without paying 1gp/day."
	),
	RECUPERATING(
		"Recuperating",
		3,
		0,
		"Recover from injury, disease, or poison. DC 15 CON save after 3 days."
	),
	RESEARCHING(
		"Researching",
		1,
		1,
		"Gain insight into mysteries. DM determines duration and requirements."
	),
	TRAINING(
		"Training",
		250,
		1,
		"Learn a new language or tool proficiency."
	);

	fun totalCost(days: Int): Int = costPerDay * days
}

/**
 * Food and water requirements.
 */
object SurvivalRules {
	/**
	 * Food requirement per day (pounds).
	 */
	const val FOOD_PER_DAY = 1

	/**
	 * Water requirement per day (gallons).
	 */
	fun waterPerDay(isHotWeather: Boolean): Int = if (isHotWeather) 2 else 1

	/**
	 * Days a character can go without food before exhaustion.
	 */
	fun daysWithoutFood(constitutionModifier: Int): Int {
		return maxOf(3 + constitutionModifier, 1)
	}

	/**
	 * DC for Constitution save when drinking half water requirement.
	 */
	const val HALF_WATER_DC = 15
}

// ========================================
// COMBAT RULES
// ========================================

/**
 * Combat actions available to creatures.
 */
enum class CombatAction(
	val displayName: String,
	val description: String,
	val requiresTarget: Boolean = false
) {
	ATTACK("Attack", "Make one melee or ranged attack", true),
	CAST_SPELL("Cast a Spell", "Cast a spell with casting time of 1 action", false),
	DASH("Dash", "Gain extra movement equal to your speed", false),
	DISENGAGE("Disengage", "Your movement doesn't provoke opportunity attacks", false),
	DODGE(
		"Dodge",
		"Attack rolls against you have disadvantage; you have advantage on DEX saves",
		false
	),
	HELP("Help", "Give an ally advantage on their next ability check or attack roll", true),
	HIDE("Hide", "Make a Stealth check to hide", false),
	READY("Ready", "Choose a trigger and prepare an action or movement", false),
	SEARCH("Search", "Make a Perception or Investigation check to find something", false),
	USE_OBJECT("Use an Object", "Interact with an object that requires your action", false),
	GRAPPLE("Grapple", "Make a special melee attack to grab a creature", true),
	SHOVE("Shove", "Make a special melee attack to push or knock prone", true)
}

/**
 * Cover types and their bonuses.
 */
enum class CoverType(
	val displayName: String,
	val acBonus: Int,
	val dexSaveBonus: Int,
	val description: String
) {
	NONE("No Cover", 0, 0, "No obstacle between you and the attack"),
	HALF("Half Cover", 2, 2, "Obstacle blocks at least half of your body"),
	THREE_QUARTERS(
		"Three-Quarters Cover",
		5,
		5,
		"Obstacle blocks about three-quarters of your body"
	),
	TOTAL("Total Cover", 0, 0, "Completely concealed - can't be directly targeted");

	fun canBeDirectlyTargeted(): Boolean = this != TOTAL
}

/**
 * Common damage types used by the rules reference.
 */
enum class DamageType(val displayName: String, val description: String) {
	ACID("Acid", "Corrosive spray, dissolving enzymes"),
	BLUDGEONING("Bludgeoning", "Blunt force - hammers, falling, constriction"),
	COLD("Cold", "Infernal chill, frigid blasts"),
	FIRE("Fire", "Dragon breath, conjured flames"),
	FORCE("Force", "Pure magical energy - magic missile, spiritual weapon"),
	LIGHTNING("Lightning", "Lightning bolts, electrical attacks"),
	NECROTIC("Necrotic", "Withers matter and soul - undead, chill touch"),
	PIERCING("Piercing", "Puncturing attacks - spears, bites"),
	POISON("Poison", "Venomous stings, toxic gas"),
	PSYCHIC("Psychic", "Mental abilities, psionic blasts"),
	RADIANT("Radiant", "Sears flesh like fire - divine smites, flame strike"),
	SLASHING("Slashing", "Swords, axes, claws"),
	THUNDER("Thunder", "Concussive sound bursts")
}

/**
 * Creature size categories.
 */
enum class CreatureSize(
	val displayName: String,
	val spaceInFeet: Int,
	val description: String
) {
	TINY("Tiny", 2, "2Â½ by 2Â½ ft."),
	SMALL("Small", 5, "5 by 5 ft."),
	MEDIUM("Medium", 5, "5 by 5 ft."),
	LARGE("Large", 10, "10 by 10 ft."),
	HUGE("Huge", 15, "15 by 15 ft."),
	GARGANTUAN("Gargantuan", 20, "20 by 20 ft. or larger");

	/**
	 * Check if this creature is at least two sizes larger/smaller than another.
	 */
	fun isTwoSizesLargerThan(other: CreatureSize): Boolean {
		return this.ordinal >= other.ordinal + 2
	}

	fun isTwoSizesSmallerThan(other: CreatureSize): Boolean {
		return this.ordinal <= other.ordinal - 2
	}
}

/**
 * Combat rules and calculations.
 */
object CombatRules {

	/**
	 * Standard reach for melee attacks (feet).
	 */
	const val STANDARD_REACH = 5

	/**
	 * Calculate initiative modifier (Dexterity modifier).
	 */
	fun getInitiativeModifier(dexterityScore: Int): Int {
		return AbilityScoreRules.getModifier(dexterityScore)
	}

	/**
	 * Roll initiative.
	 */
	fun rollInitiative(dexterityModifier: Int): Int {
		return (1..20).random() + dexterityModifier
	}

	/**
	 * Calculate attack roll.
	 */
	fun makeAttackRoll(
		abilityModifier: Int,
		proficiencyBonus: Int,
		isProficient: Boolean = true,
		rollModifier: RollModifier = RollModifier.NORMAL
	): D20RollResult {
		val totalModifier = abilityModifier + (if (isProficient) proficiencyBonus else 0)
		return D20RollResult.roll(totalModifier, rollModifier)
	}

	/**
	 * Check if an attack hits based on AC.
	 */
	fun attackHits(attackRoll: Int, targetAC: Int): Boolean {
		return attackRoll >= targetAC
	}

	/**
	 * Calculate melee attack modifier (Strength or DEX for finesse).
	 */
	fun getMeleeAttackModifier(strengthScore: Int, dexterityScore: Int, isFinesse: Boolean): Int {
		return if (isFinesse) {
			maxOf(
				AbilityScoreRules.getModifier(strengthScore),
				AbilityScoreRules.getModifier(dexterityScore)
			)
		} else {
			AbilityScoreRules.getModifier(strengthScore)
		}
	}

	/**
	 * Calculate ranged attack modifier (Dexterity).
	 */
	fun getRangedAttackModifier(dexterityScore: Int): Int {
		return AbilityScoreRules.getModifier(dexterityScore)
	}

	/**
	 * Check if target is within range.
	 */
	fun isWithinRange(distanceInFeet: Int, normalRange: Int, longRange: Int? = null): RollModifier {
		return when {
			distanceInFeet <= normalRange -> RollModifier.NORMAL
			longRange != null && distanceInFeet <= longRange -> RollModifier.DISADVANTAGE
			else -> RollModifier.NORMAL // Beyond range = automatic miss (handled separately)
		}
	}

	/**
	 * Calculate critical hit damage (roll damage dice twice).
	 */
	fun calculateCriticalDamage(baseDamageDice: Int, diceType: Int, modifier: Int): Int {
		val roll1 = (1..diceType).random()
		val roll2 = (1..diceType).random()
		return (roll1 + roll2) * baseDamageDice + modifier
	}

	/**
	 * Apply damage resistance (halve damage).
	 */
	fun applyResistance(damage: Int): Int = damage / 2

	/**
	 * Apply damage vulnerability (double damage).
	 */
	fun applyVulnerability(damage: Int): Int = damage * 2

	/**
	 * Calculate opportunity attack range.
	 */
	fun canMakeOpportunityAttack(
		targetLeftReach: Boolean,
		targetIsVisible: Boolean,
		targetUsedDisengage: Boolean
	): Boolean {
		return targetLeftReach && targetIsVisible && !targetUsedDisengage
	}
}

/**
 * Death saving throw rules.
 */
object DeathSavingThrowRules {
	const val SUCCESS_THRESHOLD = 10
	const val SUCCESSES_TO_STABILIZE = 3
	const val FAILURES_TO_DIE = 3

	/**
	 * Make a death saving throw.
	 */
	fun makeDeathSave(): Int = (1..20).random()

	/**
	 * Check if death save succeeded.
	 */
	fun isSuccess(roll: Int): Boolean = roll >= SUCCESS_THRESHOLD

	/**
	 * Count failures from roll (natural 1 = 2 failures).
	 */
	fun getFailureCount(roll: Int): Int = if (roll == 1) 2 else 1

	/**
	 * Check if natural 20 (regain 1 HP).
	 */
	fun regainsHitPoint(roll: Int): Boolean = roll == 20

	/**
	 * Check if damage causes instant death.
	 */
	fun isInstantDeath(damage: Int, maxHitPoints: Int): Boolean {
		return damage >= maxHitPoints
	}

	/**
	 * Stabilize DC for Medicine check.
	 */
	const val STABILIZE_DC = 10
}

/**
 * Grappling rules.
 */
object GrappleRules {
	/**
	 * Check if a creature can be grappled.
	 */
	fun canGrapple(grapplerSize: CreatureSize, targetSize: CreatureSize): Boolean {
		// Target must be no more than one size larger
		return targetSize.ordinal <= grapplerSize.ordinal + 1
	}

	/**
	 * Grapple is a contested check: STR (Athletics) vs STR (Athletics) or DEX (Acrobatics).
	 */
	fun attemptGrapple(
		grapplerAthletics: Int,
		targetAthletics: Int,
		targetAcrobatics: Int
	): Boolean {
		val targetDefense = maxOf(targetAthletics, targetAcrobatics)
		return grapplerAthletics > targetDefense
	}

	/**
	 * Grappled creatures move at half speed when dragged.
	 */
	fun getDragSpeed(
		grapplerSpeed: Int,
		targetSize: CreatureSize,
		grapplerSize: CreatureSize
	): Int {
		// Normal half speed, unless target is 2+ sizes smaller
		return if (targetSize.isTwoSizesSmallerThan(grapplerSize)) {
			grapplerSpeed
		} else {
			grapplerSpeed / 2
		}
	}
}

/**
 * Mounted combat rules.
 */
object MountedCombatRules {
	/**
	 * Cost to mount/dismount (half movement).
	 */
	fun getMountDismountCost(speed: Int): Int = speed / 2

	/**
	 * DC to avoid falling when mount moves against will.
	 */
	const val FALL_DC = 10

	/**
	 * Check if creature can serve as mount.
	 */
	fun canServeAsMount(mountSize: CreatureSize, riderSize: CreatureSize): Boolean {
		// Mount must be at least one size larger
		return mountSize.ordinal >= riderSize.ordinal + 1
	}
}

/**
 * Underwater combat rules.
 */
object UnderwaterCombatRules {
	/**
	 * Melee weapons that work underwater without disadvantage.
	 */
	val UNDERWATER_MELEE_WEAPONS = setOf(
		"dagger", "javelin", "shortsword", "spear", "trident"
	)

	/**
	 * Ranged weapons that work underwater.
	 */
	val UNDERWATER_RANGED_WEAPONS = setOf(
		"crossbow", "net", "javelin", "spear", "trident", "dart"
	)

	/**
	 * Check if melee attack has disadvantage underwater.
	 */
	fun hasMeleeDisadvantage(weaponName: String, hasSwimSpeed: Boolean): Boolean {
		return !hasSwimSpeed && !UNDERWATER_MELEE_WEAPONS.contains(weaponName.lowercase())
	}

	/**
	 * Check if ranged attack has disadvantage or auto-misses underwater.
	 */
	fun getRangedPenalty(
		weaponName: String,
		distanceInFeet: Int,
		normalRange: Int
	): RollModifier? {
		val weapon = weaponName.lowercase()

		// Auto-miss beyond normal range
		if (distanceInFeet > normalRange) return null

		// Disadvantage unless specific weapons
		return if (UNDERWATER_RANGED_WEAPONS.contains(weapon)) {
			RollModifier.NORMAL
		} else {
			RollModifier.DISADVANTAGE
		}
	}
}
