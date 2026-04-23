/*
 * FILE: CoreRulesReference.kt
 *
 * TABLE OF CONTENTS:
 * 1. Reference data models (CoreRuleSection, CoreGlossaryEntry)
 * 2. CoreRulesReference singleton — section and glossary datasets
 */

package com.example.loreweaver.domain.util

/**
 * Summary card for a Core Rules topic.
 */
data class CoreRuleSection(
	val title: String,
	val summary: String,
	val bullets: List<String> = emptyList(),
	val keywords: List<String> = emptyList()
)

/**
 * Compact glossary entry used by the Core Rules reference UI.
 */
data class CoreGlossaryEntry(
	val term: String,
	val tag: String? = null,
	val summary: String,
	val bullets: List<String> = emptyList(),
	val seeAlso: List<String> = emptyList(),
	val keywords: List<String> = emptyList()
) {
	val title: String
		get() = if (tag.isNullOrBlank()) term else "$term [$tag]"

	fun matchesQuery(query: String): Boolean {
		if (query.isBlank()) return true

		return sequenceOf(title, summary)
			.plus(bullets.asSequence())
			.plus(seeAlso.asSequence())
			.plus(keywords.asSequence())
			.any { it.contains(query, ignoreCase = true) }
	}
}

/**
 * Utility object for Core Rules reference content.
 */
object CoreRulesReference {
	// -----------------------------------------------------------------------
	// Cross-reference term constants — extracted to satisfy SonarQube S1192
	// -----------------------------------------------------------------------
	private const val ABILITY_CHECK = "Ability Check"
	private const val AREA_OF_EFFECT = "Area of Effect"
	private const val ARMOR_CLASS = "Armor Class"
	private const val ATTACK_ACTION = "Attack [Action]"
	private const val ATTACK_ROLL = "Attack Roll"
	private const val ATTITUDE_ATTITUDE = "Attitude [Attitude]"
	private const val BREAKING_OBJECTS = "Breaking Objects"
	private const val CHALLENGE_RATING = "Challenge Rating"
	private const val D20_TEST = "D20 Test"
	private const val DAMAGE_TYPES = "Damage Types"
	private const val DEATH_SAVING_THROW = "Death Saving Throw"
	private const val DIFFICULTY_CLASS = "Difficulty Class"
	private const val EXHAUSTION_CONDITION = "Exhaustion [Condition]"
	private const val FRIENDLY_ATTITUDE = "Friendly [Attitude]"
	private const val HIT_POINTS = "Hit Points"
	private const val INFLUENCE_ACTION = "Influence [Action]"
	private const val LONG_REST = "Long Rest"
	private const val MAGIC_ACTION = "Magic [Action]"
	private const val OPPORTUNITY_ATTACKS = "Opportunity Attacks"
	private const val SAVING_THROW = "Saving Throw"
	private const val SHORT_REST = "Short Rest"
	private const val STAT_BLOCK = "Stat Block"
	private const val TEMPORARY_HIT_POINTS = "Temporary Hit Points"
	private const val UNARMED_STRIKE = "Unarmed Strike"
	private const val UNCONSCIOUS_CONDITION = "Unconscious [Condition]"
	private const val WEAPON_ATTACK = "Weapon Attack"

	// -----------------------------------------------------------------------
	// Section and glossary content
	// -----------------------------------------------------------------------
	const val INTRODUCTION =
		"This tab condenses the system loop used most often at the table: scene framing, d20 resolution, " +
			"movement, combat timing, and recovery. It is meant to be a fast rules digest rather than a full " +
			"rulebook replacement."

	const val GLOSSARY_INTRODUCTION =
		"This glossary is organized for fast table lookups. Bracket tags group related rule families, search " +
			"checks both terms and cross-references, and the entries stay concise so they work as a quick reminder " +
			"rather than a full rulebook transcript."

	val GLOSSARY_CONVENTIONS = listOf(
		"Bracket tags identify shared rule families such as actions, attitudes, conditions, hazards, and area shapes.",
		"The word \"you\" always means the creature or object currently affected by the rule being read.",
		"See also notes point toward nearby terms that are commonly used together at the table.",
		"Only current shorthand and current rules terms are listed here; older terminology is intentionally omitted."
	)

	val SECTIONS = listOf(
		CoreRuleSection(
			title = "General Principles",
			summary =
				"Specific features beat broad defaults, and fractions produced by multiplication or division are " +
					"rounded down unless a rule explicitly says otherwise.",
			bullets = listOf(
				"If a feature changes a baseline rule, follow the feature.",
				"Round down after multiplying or dividing, even on a one-half result.",
				"Treat unusual wording as intentional whenever it conflicts with a general rule."
			),
			keywords = listOf("exceptions supersede general rules", "round down")
		),
		CoreRuleSection(
			title = "Rhythm of Play",
			summary =
				"Play usually loops through the Game Master describing the scene, the players declaring actions, and " +
					"the Game Master narrating the outcome. Combat keeps the same pattern but formalizes it with turns " +
					"and rounds.",
			bullets = listOf(
				"The Game Master establishes the location, threats, and opportunities.",
				"Players explain what their characters attempt.",
				"The Game Master resolves the outcome, sometimes with a die roll when failure would matter."
			),
			keywords = listOf("scene", "players describe", "gm narrates")
		),
		CoreRuleSection(
			title = "D20 Tests",
			summary =
				"Ability checks, saving throws, and attack rolls all follow the same structure: roll a d20, add the " +
					"right modifiers, and compare the total to a target number.",
			bullets = listOf(
				"Roll one d20, or two and keep one when advantage or disadvantage applies.",
				"Add the relevant ability modifier.",
				"Add proficiency if the creature is trained in the skill, save, weapon, spell, or tool involved.",
				"Meet or beat the DC for checks and saves, or the Armor Class for attacks."
			),
			keywords = listOf("ability check", "saving throw", "attack roll", "dc", "armor class")
		),
		CoreRuleSection(
			title = "Ability Checks",
			summary =
				"Checks resolve uncertain tasks outside direct attacks, such as lifting gates, sneaking, " +
					"recalling lore, or reading a room.",
			bullets = listOf(
				"Strength handles force and athletics.",
				"Dexterity covers agility, balance, stealth, and precise motion.",
				"Constitution measures endurance and pushing through strain.",
				"Intelligence supports reasoning, memory, and study.",
				"Wisdom handles awareness, intuition, and perception.",
				"Charisma covers influence, deception, presence, and performance."
			),
			keywords = listOf("typical difficulty classes", "study", "search", "influence")
		),
		CoreRuleSection(
			title = "Saving Throws",
			summary =
				"Saving throws represent resisting or avoiding threats that target the body, senses, or mind. You can " +
					"also choose to fail a save voluntarily.",
			bullets = listOf(
				"Strength resists direct physical force.",
				"Dexterity avoids blasts, traps, and sudden danger.",
				"Constitution endures poison, disease, and other bodily hazards.",
				"Intelligence can pierce falsehoods such as illusions.",
				"Wisdom resists fear, charm, and other mental pressure.",
				"Charisma protects identity, will, and planar integrity."
			),
			keywords = listOf("save", "saving throw proficiency")
		),
		CoreRuleSection(
			title = "Attack Rolls and Armor Class",
			summary =
				"Weapon attacks usually use Strength in melee and Dexterity at range, while spell attacks use the " +
					"casting ability defined by the attacker. Base Armor Class starts at 10 plus Dexterity modifier " +
					"unless armor, a feature, or magic supplies another formula.",
			bullets = listOf(
				"A natural 20 on an attack is an automatic hit and a critical hit.",
				"A natural 1 on an attack always misses.",
				"Only one base Armor Class calculation can apply at a time."
			),
			keywords = listOf("critical hit", "natural 20", "natural 1", "base ac")
		),
		CoreRuleSection(
			title = "Advantage, Disadvantage, and Heroic Inspiration",
			summary =
				"Advantage means roll two d20s and keep the higher; disadvantage keeps the lower. They never stack, " +
					"and if both apply, they cancel. Heroic Inspiration lets you reroll one die after you see it.",
			bullets = listOf(
				"Even if several effects grant advantage, you still roll only two d20s.",
				"Even if several effects impose disadvantage, you still roll only two d20s.",
				"When a reroll applies to a roll with advantage or disadvantage, reroll only one of the dice.",
				"A character can hold only one instance of Heroic Inspiration at a time."
			),
			keywords = listOf("reroll", "inspiration")
		),
		CoreRuleSection(
			title = "Proficiency",
			summary =
				"Proficiency shows trained competence. Add the proficiency bonus once when a rule says the creature is " +
					"proficient in the relevant skill, save, tool, weapon, or spell attack.",
			bullets = listOf(
				"The same proficiency bonus cannot be added to the same roll more than once.",
				"A doubled or halved proficiency bonus is still modified only one time each way.",
				"Tool proficiency can combine with the related skill proficiency, granting advantage on the check when both apply."
			),
			keywords = listOf("expertise", "tool proficiency", "skill proficiency")
		),
		CoreRuleSection(
			title = "Actions, Bonus Actions, and Reactions",
			summary =
				"Creatures normally take one action on their turn, possibly one bonus action if a feature grants it, " +
					"and at most one reaction before the start of their next turn.",
			bullets = listOf(
				"You can do only one action at a time.",
				"A bonus action exists only when a feature, spell, or rule grants one.",
				"A reaction happens immediately after its trigger unless the rule says otherwise.",
				"If a reaction interrupts another turn, that turn resumes right after the reaction resolves."
			),
			keywords = listOf("one thing at a time", "bonus action", "reaction")
		),
		CoreRuleSection(
			title = "Social Interaction",
			summary =
				"Conversations can be resolved through roleplay alone or by checks when uncertainty matters. Nonplayer " +
					"creatures usually begin from a friendly, indifferent, or hostile attitude.",
			bullets = listOf(
				"Roleplay can improve or worsen an attitude before any roll happens.",
				"Use Influence when the outcome hinges on persuasion, deception, intimidation, performance, or animal handling.",
				"Let the character with the strongest relevant proficiency lead important exchanges."
			),
			keywords = listOf("friendly", "indifferent", "hostile", "roleplaying")
		),
		CoreRuleSection(
			title = "Exploration Basics",
			summary =
				"Exploration covers visibility, hidden dangers, object interaction, and environmental " +
					"obstacles while time matters less than in combat.",
			bullets = listOf(
				"Lightly obscured areas hinder sight-based Perception checks.",
				"Heavily obscured areas make sight fail as though the observer were blinded.",
				"Searching for something hidden works only when the character looks in the correct area.",
				"Breaking a fragile nonmagical object usually takes an action."
			),
			keywords = listOf("vision", "light", "darkness", "hiding", "objects", "hazards")
		),
		CoreRuleSection(
			title = "Travel and Marching Order",
			summary =
				"Long-distance movement uses travel pace rather than combat speed. Marching order decides who triggers " +
					"hazards first, who spots danger, and who starts closest when a fight erupts.",
			bullets = listOf(
				"Fast pace trades awareness for distance.",
				"Normal pace keeps steady movement but still hampers stealth.",
				"Slow pace sacrifices distance for better perception and survival awareness.",
				"Mounts can briefly double overland distance for about an hour before needing rest."
			),
			keywords = listOf("pace", "marching order", "vehicles")
		),
		CoreRuleSection(
			title = "Combat Sequence",
			summary =
				"Combat begins with positions, then initiative, then repeated turns in descending order until one side " +
					"is defeated, flees, or the fight otherwise ends.",
			bullets = listOf(
				"A round represents about six seconds.",
				"Initiative is a Dexterity check.",
				"A surprised combatant has disadvantage on initiative.",
				"Ties are sorted by the Game Master for monsters, by the players for their own characters, or by the " +
					"Game Master when both sides tie."
			),
			keywords = listOf("initiative", "surprise", "rounds", "turn order")
		),
		CoreRuleSection(
			title = "Movement and Position",
			summary =
				"On your turn, you can move up to your speed, split that movement around your action, and combine " +
					"walking with climbing, swimming, crawling, or jumping.",
			bullets = listOf(
				"Difficult terrain costs 1 extra foot for each foot moved.",
				"You can drop prone for free on your turn unless your speed is 0.",
				"You can move through allies, tiny creatures, incapacitated creatures, or creatures that are much " +
					"larger or smaller than you, but their spaces may count as difficult terrain.",
				"You cannot willingly end movement in another creature’s space."
			),
			keywords = listOf("difficult terrain", "prone", "creature size", "grid")
		),
		CoreRuleSection(
			title = "Making Attacks",
			summary =
				"Attacks follow a short structure: choose a target, determine modifiers such as cover and advantage, " +
					"then resolve the attack and its damage or special effect.",
			bullets = listOf(
				"Ranged attacks use a normal range and sometimes a longer range with disadvantage.",
				"Ranged attacks made while an enemy is adjacent are usually at disadvantage.",
				"Melee attacks usually reach 5 feet unless a feature or stat block extends that reach.",
				"Leaving a hostile creature’s reach can provoke an opportunity attack unless something prevents " +
					"it."
			),
			keywords = listOf(
				"reach",
				"opportunity attack",
				"cover",
				"ranged attacks in close combat"
			)
		),
		CoreRuleSection(
			title = "Special Combat Cases",
			summary =
				"Mounts, underwater fights, unseen targets, and unusual positioning all modify ordinary " +
					"attack and movement rules.",
			bullets = listOf(
				"If you attack a target you cannot see, the attack usually has disadvantage.",
				"If a creature cannot see you, your attacks against it usually have advantage.",
				"Controlled mounts act on your initiative and are limited to Dash, Disengage, and Dodge.",
				"Without a swim speed, many underwater weapon attacks are impaired, and fire damage is resisted underwater."
			),
			keywords = listOf("mounted combat", "underwater combat", "unseen attackers")
		),
		CoreRuleSection(
			title = "Monster Stat Blocks",
			summary =
				"A monster stat block is the creature's compact play reference, grouping identity, defenses, " +
					"movement, senses, challenge, and active abilities in one place.",
			bullets = listOf(
				"Name, size, creature type, tags, and alignment establish the monster's identity and default " +
					"roleplaying baseline.",
				"Armor Class, Hit Points, Speed, and Initiative summarize the creature's combat readiness at a " +
					"glance.",
				"Ability scores, saves, senses, languages, challenge rating, and optional details such as " +
					"skills, resistances, immunities, and gear appear when relevant.",
				"Traits, actions, bonus actions, reactions, and legendary actions list what the monster can do in play."
			),
			keywords = listOf(
				"monster",
				"stat block overview",
				"armor class",
				"hit points",
				"speed",
				"initiative",
				"senses",
				"languages",
				"cr"
			)
		),
		CoreRuleSection(
			title = "Running a Monster",
			summary =
				"To match a monster's listed threat, lean on its strongest limited features early, use " +
					"Multiattack whenever a bigger option is unavailable, and spend its extra action economy often.",
			bullets = listOf(
				"Use high-impact limited abilities such as breath weapons or once-per-day spells as quickly " +
					"and as often as the monster can.",
				"If a monster has Multiattack, default to it on turns when the creature is not using a stronger " +
					"special option.",
				"Bonus actions, reactions, and legendary actions are part of the monster's expected output, so " +
					"use them regularly when triggers appear."
			),
			keywords = listOf(
				"recharge",
				"multiattack",
				"legendary action",
				"bonus action",
				"reaction"
			)
		),
		CoreRuleSection(
			title = "Monster Attack and Usage Notation",
			summary =
				"Monster entries use short notation for attack rolls, save-based effects, spellcasting, " +
					"damage, and abilities with limited uses.",
			bullets = listOf(
				"Attack entries state whether the attack is melee or ranged, then list the attack bonus, " +
					"reach or range, and the result on a hit.",
				"Some attacks or effects also specify outcomes on a miss or regardless of whether they hit.",
				"Save-based effects name the save, give the DC, and explain the failed-save and " +
					"successful-save results; 'half damage only' means other riders are ignored on a " +
					"success.",
				"Spellcasting notes the monster's casting ability, save DC, attack bonus, and any component or usage exceptions.",
				"Limited-use features usually recharge by X/Day, by a Recharge roll, or after a Short or Long Rest."
			),
			keywords = listOf(
				"hit or miss",
				"saving throw effect",
				"damage notation",
				"spellcasting",
				"limited usage",
				"x/day"
			)
		),
		CoreRuleSection(
			title = "Damage, Healing, and Dying",
			summary =
				"Damage reduces hit points, critical hits roll the attack’s damage dice twice, and " +
					"creatures at 0 hit points either die outright or fall unconscious and begin death " +
					"saves.",
			bullets = listOf(
				"Half damage from a successful save is rounded down.",
				"Apply bonuses, penalties, and multipliers before resistance, then apply vulnerability last.",
				"Resistance and vulnerability do not stack with themselves.",
				"A stable creature at 0 hit points stops making death saves and recovers 1 hit point after 1d4 hours if left alone."
			),
			keywords = listOf(
				"critical hits",
				"resistance",
				"vulnerability",
				"immunity",
				"death saving throws",
				"massive damage"
			)
		),
		CoreRuleSection(
			title = TEMPORARY_HIT_POINTS,
			summary =
				"Temporary hit points are a separate buffer that is lost before real hit points, do not " +
					"stack together, and do not count as healing.",
			bullets = listOf(
				"Choose whether to keep your current temporary hit points or replace them with a new amount.",
				"They disappear after a long rest if they have not already been spent.",
				"Receiving temporary hit points while at 0 hit points does not restore consciousness."
			),
			keywords = listOf("temp hp", "temporary hp")
		)
	)

	val ABILITY_DESCRIPTIONS_TABLE = ReferenceTable(
		title = "Ability Descriptions",
		columns = listOf("Ability", "Measures"),
		rows = listOf(
			listOf("Strength", "Physical might"),
			listOf("Dexterity", "Agility, reflexes, and balance"),
			listOf("Constitution", "Health and stamina"),
			listOf("Intelligence", "Reasoning and memory"),
			listOf("Wisdom", "Perceptiveness and mental fortitude"),
			listOf("Charisma", "Confidence, poise, and charm")
		)
	)

	val ABILITY_SCORE_BENCHMARKS_TABLE = ReferenceTable(
		title = "Ability Score Benchmarks",
		columns = listOf("Score", "Meaning"),
		rows = listOf(
			listOf("1", "Lowest normal value; effects explain what happens at 0."),
			listOf("2–9", "Weak capability"),
			listOf("10–11", "Human average"),
			listOf("12–19", "Strong capability"),
			listOf("20", "Highest normal adventurer score unless a feature says otherwise"),
			listOf("21–29", "Extraordinary capability"),
			listOf("30", "Highest possible score")
		)
	)

	val ABILITY_MODIFIERS_TABLE = ReferenceTable(
		title = "Ability Modifiers",
		columns = listOf("Score", "Modifier"),
		rows = listOf(
			listOf("1", "−5"),
			listOf("2–3", "−4"),
			listOf("4–5", "−3"),
			listOf("6–7", "−2"),
			listOf("8–9", "−1"),
			listOf("10–11", "+0"),
			listOf("12–13", "+1"),
			listOf("14–15", "+2"),
			listOf("16–17", "+3"),
			listOf("18–19", "+4"),
			listOf("20–21", "+5"),
			listOf("22–23", "+6"),
			listOf("24–25", "+7"),
			listOf("26–27", "+8"),
			listOf("28–29", "+9"),
			listOf("30", "+10")
		)
	)

	val ATTACK_ROLL_ABILITIES_TABLE = ReferenceTable(
		title = "Attack Roll Abilities",
		columns = listOf("Ability", "Attack Type"),
		rows = listOf(
			listOf("Strength", "Melee weapon attacks and unarmed strikes"),
			listOf("Dexterity", "Ranged weapon attacks"),
			listOf("Varies", "Spell attacks use the attacker’s spellcasting ability")
		)
	)

	val TYPICAL_DIFFICULTY_CLASSES_TABLE = ReferenceTable(
		title = "Typical Difficulty Classes",
		columns = listOf("Task Difficulty", "DC"),
		rows = listOf(
			listOf("Very easy", "5"),
			listOf("Easy", "10"),
			listOf("Medium", "15"),
			listOf("Hard", "20"),
			listOf("Very hard", "25"),
			listOf("Nearly impossible", "30")
		)
	)

	val PROFICIENCY_BONUS_TABLE = ReferenceTable(
		title = "Proficiency Bonus",
		columns = listOf("Level or CR", "Bonus"),
		rows = listOf(
			listOf("Up to 4", "+2"),
			listOf("5–8", "+3"),
			listOf("9–12", "+4"),
			listOf("13–16", "+5"),
			listOf("17–20", "+6"),
			listOf("21–24", "+7"),
			listOf("25–28", "+8"),
			listOf("29–30", "+9")
		)
	)

	val SKILLS_TABLE = ReferenceTable(
		title = "Skills",
		columns = listOf("Skill", "Ability", "Example Uses"),
		rows = listOf(
			listOf("Acrobatics", "Dexterity", "Keep footing or perform stunts"),
			listOf("Animal Handling", "Wisdom", "Calm, guide, or train animals"),
			listOf("Arcana", "Intelligence", "Recall magical and planar lore"),
			listOf("Athletics", "Strength", "Climb, swim, jump, or break obstacles"),
			listOf("Deception", "Charisma", "Lie convincingly or sell a disguise"),
			listOf("History", "Intelligence", "Recall historical people and events"),
			listOf("Insight", "Wisdom", "Read motives and moods"),
			listOf("Intimidation", "Charisma", "Coerce through threat or force of presence"),
			listOf("Investigation", "Intelligence", "Deduce clues or search records"),
			listOf("Medicine", "Wisdom", "Diagnose illness or treat the injured"),
			listOf("Nature", "Intelligence", "Recall lore about terrain, plants, and weather"),
			listOf("Perception", "Wisdom", "Notice hidden or subtle details"),
			listOf("Performance", "Charisma", "Act, sing, tell stories, or dance"),
			listOf("Persuasion", "Charisma", "Win someone over honestly and gracefully"),
			listOf("Religion", "Intelligence", "Recall divine traditions and symbols"),
			listOf("Sleight of Hand", "Dexterity", "Palm, conceal, or filch small objects"),
			listOf("Stealth", "Dexterity", "Hide and move quietly"),
			listOf("Survival", "Wisdom", "Track, forage, or navigate hazards")
		)
	)

	val ACTIONS_TABLE = ReferenceTable(
		title = "Common Actions",
		columns = listOf("Action", "Summary"),
		rows = listOf(
			listOf("Attack", "Make a weapon attack or unarmed strike"),
			listOf("Dash", "Gain extra movement equal to your speed for the turn"),
			listOf("Disengage", "Your movement avoids opportunity attacks for the turn"),
			listOf(
				"Dodge",
				"Attack rolls against you have disadvantage, and Dexterity saves have advantage until your next turn"
			),
			listOf("Help", "Aid another creature’s check or attack, or administer first aid"),
			listOf("Hide", "Make a Dexterity (Stealth) check"),
			listOf("Influence", "Try to change a creature’s attitude with a social check"),
			listOf("Magic", "Cast a spell or use a magical feature or item"),
			listOf("Ready", "Set a trigger and prepare an action"),
			listOf("Search", "Use Wisdom to find clues, threats, or wounds"),
			listOf("Study", "Use Intelligence to recall or analyze information"),
			listOf("Utilize", "Use a nonmagical object")
		)
	)

	val TRAVEL_PACE_TABLE = ReferenceTable(
		title = "Travel Pace",
		columns = listOf("Pace", "Per Minute", "Per Hour", "Per Day", "Game Effect"),
		rows = listOf(
			listOf(
				"Fast",
				"400 feet",
				"4 miles",
				"30 miles",
				"Disadvantage on Wisdom (Perception or Survival) and Dexterity (Stealth) checks"
			),
			listOf(
				"Normal",
				"300 feet",
				"3 miles",
				"24 miles",
				"Disadvantage on Dexterity (Stealth) checks"
			),
			listOf(
				"Slow",
				"200 feet",
				"2 miles",
				"18 miles",
				"Advantage on Wisdom (Perception or Survival) checks"
			)
		)
	)

	val CREATURE_SIZE_AND_SPACE_TABLE = ReferenceTable(
		title = "Creature Size and Space",
		columns = listOf("Size", "Space (Feet)", "Space (Squares)"),
		rows = listOf(
			listOf("Tiny", "2½ by 2½ feet", "4 per square"),
			listOf("Small", "5 by 5 feet", "1 square"),
			listOf("Medium", "5 by 5 feet", "1 square"),
			listOf("Large", "10 by 10 feet", "4 squares (2 by 2)"),
			listOf("Huge", "15 by 15 feet", "9 squares (3 by 3)"),
			listOf("Gargantuan", "20 by 20 feet", "16 squares (4 by 4)")
		)
	)

	val MONSTER_CREATURE_TYPES_TABLE = ReferenceTable(
		title = "Monster Creature Types",
		columns = listOf("Type", "Summary"),
		rows = listOf(
			listOf("Aberration", "Utterly alien creature"),
			listOf("Beast", "Natural non-Humanoid creature, including most giant animals"),
			listOf("Celestial", "Magical being tied to the Upper Planes"),
			listOf("Construct", "Magically created creature"),
			listOf("Dragon", "Ancient scaled being"),
			listOf("Elemental", "Creature from the Elemental Planes"),
			listOf("Fey", "Being tied to the Feywild or to primal nature"),
			listOf("Fiend", "Creature of the Lower Planes"),
			listOf("Giant", "Towering creature with a humanlike shape"),
			listOf("Humanoid", "Person defined by role or profession across many species"),
			listOf("Monstrosity", "Unnatural creature with a strange origin"),
			listOf("Ooze", "Gelatinous creature"),
			listOf("Plant", "Sentient vegetation or fungal monster"),
			listOf("Undead", "Spirit or reanimated dead")
		)
	)

	val MONSTER_HIT_DICE_BY_SIZE_TABLE = ReferenceTable(
		title = "Monster Hit Dice by Size",
		columns = listOf("Monster Size", "Hit Die", "Average HP per Die"),
		rows = listOf(
			listOf("Tiny", "d4", "2.5"),
			listOf("Small", "d6", "3.5"),
			listOf("Medium", "d8", "4.5"),
			listOf("Large", "d10", "5.5"),
			listOf("Huge", "d12", "6.5"),
			listOf("Gargantuan", "d20", "10.5")
		)
	)

	val EXPERIENCE_POINTS_BY_CHALLENGE_RATING_TABLE = ReferenceTable(
		title = "Experience Points by Challenge Rating",
		columns = listOf("CR", "XP"),
		rows = listOf(
			listOf("0", "0 or 10"),
			listOf("1/8", "25"),
			listOf("1/4", "50"),
			listOf("1/2", "100"),
			listOf("1", "200"),
			listOf("2", "450"),
			listOf("3", "700"),
			listOf("4", "1,100"),
			listOf("5", "1,800"),
			listOf("6", "2,300"),
			listOf("7", "2,900"),
			listOf("8", "3,900"),
			listOf("9", "5,000"),
			listOf("10", "5,900"),
			listOf("11", "7,200"),
			listOf("12", "8,400"),
			listOf("13", "10,000"),
			listOf("14", "11,500"),
			listOf("15", "13,000"),
			listOf("16", "15,000"),
			listOf("17", "18,000"),
			listOf("18", "20,000"),
			listOf("19", "22,000"),
			listOf("20", "25,000"),
			listOf("21", "33,000"),
			listOf("22", "41,000"),
			listOf("23", "50,000"),
			listOf("24", "62,000"),
			listOf("25", "75,000"),
			listOf("26", "90,000"),
			listOf("27", "105,000"),
			listOf("28", "120,000"),
			listOf("29", "135,000"),
			listOf("30", "155,000")
		)
	)

	val COVER_TABLE = ReferenceTable(
		title = "Cover",
		columns = listOf("Degree", "Benefit", "Typical Source"),
		rows = listOf(
			listOf(
				"Half",
				"+2 AC and Dexterity saves",
				"A creature or obstacle that hides about half the target"
			),
			listOf(
				"Three-quarters",
				"+5 AC and Dexterity saves",
				"A sturdy obstacle that hides most of the target"
			),
			listOf(
				"Total",
				"Cannot be targeted directly",
				"An obstacle that completely blocks the target"
			)
		)
	)

	val GLOSSARY_ABBREVIATIONS_TABLE = ReferenceTable(
		title = "Rules Glossary Abbreviations",
		columns = listOf("Abbreviation", "Meaning"),
		rows = listOf(
			listOf("AC", ARMOR_CLASS),
			listOf("C", "Concentration"),
			listOf("CE", "Chaotic Evil"),
			listOf("CG", "Chaotic Good"),
			listOf("Cha.", "Charisma"),
			listOf("CN", "Chaotic Neutral"),
			listOf("Con.", "Constitution"),
			listOf("CP", "Copper Piece(s)"),
			listOf("CR", CHALLENGE_RATING),
			listOf("DC", DIFFICULTY_CLASS),
			listOf("Dex.", "Dexterity"),
			listOf("EP", "Electrum Piece(s)"),
			listOf("GM", "Game Master"),
			listOf("GP", "Gold Piece(s)"),
			listOf("HP", "Hit Point(s)"),
			listOf("Int.", "Intelligence"),
			listOf("LE", "Lawful Evil"),
			listOf("LG", "Lawful Good"),
			listOf("LN", "Lawful Neutral"),
			listOf("M", "Material component"),
			listOf("N", "Neutral"),
			listOf("NE", "Neutral Evil"),
			listOf("NG", "Neutral Good"),
			listOf("NPC", "Nonplayer character"),
			listOf("PB", "Proficiency Bonus"),
			listOf("PP", "Platinum Piece(s)"),
			listOf("R", "Ritual"),
			listOf("S", "Somatic component"),
			listOf("SP", "Silver Piece(s)"),
			listOf("Str.", "Strength"),
			listOf("V", "Verbal component"),
			listOf("Wis.", "Wisdom"),
			listOf("XP", "Experience Point(s)")
		)
	)

	val GLOSSARY_ENTRIES = listOf(
		CoreGlossaryEntry(
			term = ABILITY_CHECK,
			summary = "A d20 test used to overcome uncertainty with an ability or one of its linked skills.",
			bullets = listOf(
				"Add the relevant ability modifier.",
				"Add proficiency when the creature is trained in the skill or tool involved.",
				"Compare the total to a DC."
			),
			seeAlso = listOf(D20_TEST, DIFFICULTY_CLASS, "Proficiency"),
			keywords = listOf("skill check")
		),
		CoreGlossaryEntry(
			term = "Ability Score and Modifier",
			summary = "Each of the six abilities has a score and a modifier; the modifier is what most rules add to d20 tests.",
			seeAlso = listOf(ABILITY_CHECK, SAVING_THROW, ATTACK_ROLL)
		),
		CoreGlossaryEntry(
			term = "Action",
			summary =
				"A creature normally gets one action on its turn and chooses it from basic actions or from " +
					"granted features.",
			bullets = listOf(
				"Common choices include Attack, Dash, Dodge, Help, Hide, Influence, Magic, Ready, " +
					"Search, Study, and Utilize.",
				"An action is separate from a bonus action or reaction."
			),
			seeAlso = listOf("Bonus Action", "Reaction", ATTACK_ACTION),
			keywords = listOf("turn action")
		),
		CoreGlossaryEntry(
			term = "Advantage",
			summary = "Roll two d20s and use the higher result.",
			bullets = listOf(
				"Multiple sources still give only one extra die.",
				"Advantage and disadvantage on the same roll cancel each other."
			),
			seeAlso = listOf("Disadvantage", "Heroic Inspiration")
		),
		CoreGlossaryEntry(
			term = AREA_OF_EFFECT,
			tag = AREA_OF_EFFECT,
			summary =
				"An effect can fill a defined shape from a chosen origin point, provided total cover does " +
					"not block the line to that spot.",
			bullets = listOf(
				"Common shapes are cone, cube, cylinder, emanation, line, and sphere.",
				"If the chosen origin lies behind a blocking obstacle, the effect begins on the near side instead."
			),
			seeAlso = listOf("Cover", "Target"),
			keywords = listOf("cone", "cube", "cylinder", "emanation", "line", "sphere")
		),
		CoreGlossaryEntry(
			term = "Alignment",
			summary =
				"A monster's listed alignment is a default roleplaying guide rather than an unbreakable " +
					"rule, and you can change it to fit the story.",
			bullets = listOf(
				"Neutral often signals that the individual creature could lean in different moral or " +
					"ethical directions."
			),
			seeAlso = listOf("Monster", STAT_BLOCK)
		),
		CoreGlossaryEntry(
			term = ARMOR_CLASS,
			summary = "Armor Class is the target number an attack roll must meet or exceed to hit.",
			bullets = listOf(
				"Base AC starts at 10 plus Dexterity modifier unless another formula replaces it.",
				"Only one base AC calculation applies at a time."
			),
			seeAlso = listOf(ATTACK_ROLL, "Cover")
		),
		CoreGlossaryEntry(
			term = "Attack",
			tag = "Action",
			summary = "Take one weapon attack or one unarmed strike as your action.",
			bullets = listOf(
				"You may equip or unequip one weapon before or after the attack made as part of this action.",
				"If a feature grants extra attacks, you can move between them."
			),
			seeAlso = listOf(ATTACK_ROLL, UNARMED_STRIKE, WEAPON_ATTACK)
		),
		CoreGlossaryEntry(
			term = ATTACK_ROLL,
			summary = "A d20 test made to hit with a weapon, an unarmed strike, or a spell attack.",
			seeAlso = listOf(ARMOR_CLASS, "Critical Hit", WEAPON_ATTACK)
		),
		CoreGlossaryEntry(
			term = "Attitude",
			tag = "Attitude",
			summary = "A creature's starting social stance is usually friendly, hostile, or indifferent.",
			bullets = listOf(
				"Attitude shapes whether influence checks gain advantage, suffer disadvantage, or proceed normally.",
				"Roleplay can change attitude before any roll happens."
			),
			seeAlso = listOf(
				FRIENDLY_ATTITUDE,
				"Hostile [Attitude]",
				"Indifferent [Attitude]",
				INFLUENCE_ACTION
			)
		),
		CoreGlossaryEntry(
			term = "Blinded",
			tag = "Condition",
			summary =
				"You cannot see, sight-based ability checks fail, attacks against you gain advantage, and " +
					"your attacks suffer disadvantage.",
			seeAlso = listOf("Darkness", "Heavily Obscured", "Invisible [Condition]")
		),
		CoreGlossaryEntry(
			term = "Bonus Action",
			summary = "A special extra action available only when a rule explicitly grants one.",
			bullets = listOf("You can take at most one bonus action on a turn."),
			seeAlso = listOf("Action", "Reaction")
		),
		CoreGlossaryEntry(
			term = BREAKING_OBJECTS,
			summary = "Objects use AC, hit points, and sometimes a damage threshold when attacks or spells try to destroy them.",
			bullets = listOf(
				"Very fragile objects may break automatically when the GM allows it.",
				"Objects ignore poison and psychic damage unless a rule says otherwise."
			),
			seeAlso = listOf("Damage Threshold", "Object")
		),
		CoreGlossaryEntry(
			term = "Burning",
			tag = "Hazard",
			summary = "A burning creature or object takes ongoing fire damage until the flames are put out.",
			bullets = listOf(
				"The damage is 1d4 fire at the start of each turn.",
				"Going prone to smother flames, dousing, submerging, or cutting off air can end it."
			),
			seeAlso = listOf("Hazard")
		),
		CoreGlossaryEntry(
			term = "Cantrip",
			summary = "A level 0 spell that is cast without spending a spell slot.",
			seeAlso = listOf("Spell", MAGIC_ACTION)
		),
		CoreGlossaryEntry(
			term = CHALLENGE_RATING,
			summary = "Challenge Rating estimates how threatening a monster is to a typical four-character party.",
			bullets = listOf("Actual danger still changes with terrain, numbers, and circumstances."),
			seeAlso = listOf(STAT_BLOCK, "Experience Points")
		),
		CoreGlossaryEntry(
			term = "Charmed",
			tag = "Condition",
			summary =
				"You cannot attack the charmer or target the charmer with damaging effects, and the " +
					"charmer has an edge in social interaction with you.",
			seeAlso = listOf(FRIENDLY_ATTITUDE, INFLUENCE_ACTION)
		),
		CoreGlossaryEntry(
			term = "Concentration",
			summary = "Some spells and effects stay active only while their creator keeps concentration.",
			bullets = listOf(
				"Starting another concentration effect ends the earlier one.",
				"Taking damage can force a Constitution save to maintain it.",
				"Being incapacitated or dying ends it immediately."
			),
			seeAlso = listOf(MAGIC_ACTION, SAVING_THROW, "Spell")
		),
		CoreGlossaryEntry(
			term = "Condition",
			tag = "Condition",
			summary = "A condition is a temporary game state with defined effects and its own ending rules.",
			bullets = listOf(
				"A condition usually does not stack with itself.",
				"Exhaustion is the main exception because it tracks levels."
			),
			seeAlso = listOf(
				EXHAUSTION_CONDITION,
				"Blinded [Condition]",
				UNCONSCIOUS_CONDITION
			)
		),
		CoreGlossaryEntry(
			term = "Cover",
			summary =
				"Cover protects a target behind an obstacle by raising AC, helping Dexterity saves, or " +
					"blocking direct targeting entirely.",
			bullets = listOf(
				"Only the best available degree of cover applies.",
				"Total cover blocks direct targeting."
			),
			seeAlso = listOf(ARMOR_CLASS, AREA_OF_EFFECT)
		),
		CoreGlossaryEntry(
			term = "Creature Type",
			summary =
				"A monster's creature type identifies what kind of being it is so spells, items, and " +
					"features can interact with it correctly.",
			bullets = listOf(
				"Tags in parentheses add extra categorization but usually do not carry rules by themselves.",
				"The common types include Aberration, Beast, Celestial, Construct, Dragon, Elemental, " +
					"Fey, Fiend, Giant, Humanoid, Monstrosity, Ooze, Plant, and Undead."
			),
			seeAlso = listOf("Monster", STAT_BLOCK),
			keywords = listOf("descriptive tags")
		),
		CoreGlossaryEntry(
			term = D20_TEST,
			summary = "Ability checks, attack rolls, and saving throws are all d20 tests.",
			seeAlso = listOf(ABILITY_CHECK, ATTACK_ROLL, SAVING_THROW)
		),
		CoreGlossaryEntry(
			term = "Damage Threshold",
			summary =
				"A creature or object with a damage threshold ignores any single hit or effect that does " +
					"not meet that minimum.",
			seeAlso = listOf(BREAKING_OBJECTS, "Damage")
		),
		CoreGlossaryEntry(
			term = DAMAGE_TYPES,
			summary = "Damage is labeled by type so resistance, vulnerability, and immunity can interact with it.",
			bullets = listOf(
				"Common types include fire, cold, lightning, force, bludgeoning, piercing, slashing, " +
					"poison, psychic, radiant, necrotic, acid, and thunder."
			),
			seeAlso = listOf("Resistance", "Vulnerability", "Immunity")
		),
		CoreGlossaryEntry(
			term = "Dash",
			tag = "Action",
			summary = "Dash grants extra movement equal to the speed you choose to use that turn.",
			seeAlso = listOf("Speed", "Movement")
		),
		CoreGlossaryEntry(
			term = DEATH_SAVING_THROW,
			summary = "A player character at 0 hit points usually makes a death save at the start of each turn.",
			seeAlso = listOf(HIT_POINTS, "Stable", UNCONSCIOUS_CONDITION),
			keywords = listOf("death save")
		),
		CoreGlossaryEntry(
			term = "Difficult Terrain",
			summary = "Each foot moved through difficult terrain costs one extra foot of movement.",
			bullets = listOf("The cost does not stack with itself; a space is either difficult terrain or it is not."),
			seeAlso = listOf("Speed", "Climbing", "Swimming")
		),
		CoreGlossaryEntry(
			term = DIFFICULTY_CLASS,
			summary = "A DC is the target number used for an ability check or a saving throw.",
			seeAlso = listOf(ABILITY_CHECK, SAVING_THROW)
		),
		CoreGlossaryEntry(
			term = "Disadvantage",
			summary = "Roll two d20s and use the lower result.",
			bullets = listOf(
				"Multiple sources still leave you with only two dice.",
				"Advantage and disadvantage on the same roll cancel."
			),
			seeAlso = listOf("Advantage")
		),
		CoreGlossaryEntry(
			term = "Dodge",
			tag = "Action",
			summary =
				"Until your next turn, attacks against you are hindered and Dexterity saves improve so " +
					"long as you can act and still move.",
			seeAlso = listOf("Action", "Incapacitated [Condition]")
		),
		CoreGlossaryEntry(
			term = "Exhaustion",
			tag = "Condition",
			summary =
				"Exhaustion builds in levels, weakening d20 tests and reducing speed until it becomes " +
					"lethal at level six.",
			bullets = listOf(
				"Each level applies a larger penalty to tests and movement.",
				"A long rest usually removes one level."
			),
			seeAlso = listOf(LONG_REST, "Hazard")
		),
		CoreGlossaryEntry(
			term = "Experience Points",
			summary =
				"A monster's XP value is based on its challenge rating and is awarded for defeating or " +
					"otherwise neutralizing that creature.",
			bullets = listOf(
				"Summoned monsters are still worth the XP listed in their own stat blocks unless a rule " +
					"says otherwise."
			),
			seeAlso = listOf(CHALLENGE_RATING, "Monster"),
			keywords = listOf("xp by challenge rating")
		),
		CoreGlossaryEntry(
			term = "Falling",
			tag = "Hazard",
			summary =
				"A fall usually deals 1d6 bludgeoning damage for every 10 feet, up to 20d6, and often " +
					"leaves the creature prone.",
			seeAlso = listOf("Prone [Condition]", "Flying")
		),
		CoreGlossaryEntry(
			term = "Friendly",
			tag = "Attitude",
			summary = "A friendly creature is inclined to help and is easier to influence.",
			seeAlso = listOf(ATTITUDE_ATTITUDE, INFLUENCE_ACTION)
		),
		CoreGlossaryEntry(
			term = "Gear",
			summary =
				"A monster's Gear entry lists retrievable equipment it can use proficiently, while " +
					"supernatural or highly specialized items mentioned elsewhere usually stop working for " +
					"others.",
			bullets = listOf(
				"A monster that needs ammunition for ranged attacks is assumed to carry enough " +
					"ammunition to make those attacks."
			),
			seeAlso = listOf("Monster", "Weapon")
		),
		CoreGlossaryEntry(
			term = "Grappled",
			tag = "Condition",
			summary = "A grapple sets your speed to 0 and makes it harder for you to attack targets other than the grappler.",
			bullets = listOf("The grappler can drag you at extra movement cost unless size differences make it easy."),
			seeAlso = listOf("Grappling", "Speed")
		),
		CoreGlossaryEntry(
			term = "Grappling",
			summary = "A grapple usually starts with an unarmed strike and applies the grappled condition on a failed save.",
			bullets = listOf(
				"A free hand or a listed body part is required.",
				"The target can escape with Athletics or Acrobatics against the escape DC."
			),
			seeAlso = listOf("Grappled [Condition]", UNARMED_STRIKE)
		),
		CoreGlossaryEntry(
			term = "Hazard",
			tag = "Hazard",
			summary = "A hazard is an environmental threat such as fire, starvation, dehydration, falling, or suffocation.",
			seeAlso = listOf("Burning [Hazard]", "Falling [Hazard]", "Suffocation [Hazard]")
		),
		CoreGlossaryEntry(
			term = "Help",
			tag = "Action",
			summary = "Help grants an ally an opening on a nearby attack or advantage on a relevant ability check.",
			seeAlso = listOf(ABILITY_CHECK, ATTACK_ROLL)
		),
		CoreGlossaryEntry(
			term = "Heroic Inspiration",
			summary = "Heroic Inspiration lets a player character reroll one die right after rolling it.",
			bullets = listOf(
				"If you already have it, a new grant is wasted unless you pass it to another player " +
					"character who lacks it."
			),
			seeAlso = listOf("Advantage")
		),
		CoreGlossaryEntry(
			term = "Hide",
			tag = "Action",
			summary = "Hide is a Dexterity (Stealth) check made while unseen or heavily protected from view.",
			bullets = listOf(
				"A successful hide leaves you effectively invisible until you give yourself away.",
				"Loud noise, attacking, or verbal spellcasting ends it."
			),
			seeAlso = listOf("Invisible [Condition]", "Heavily Obscured")
		),
		CoreGlossaryEntry(
			term = HIT_POINTS,
			summary = "Hit points measure how much damage a creature or object can take before it is destroyed or knocked down.",
			seeAlso = listOf("Healing", TEMPORARY_HIT_POINTS, DEATH_SAVING_THROW)
		),
		CoreGlossaryEntry(
			term = "Hostile",
			tag = "Attitude",
			summary = "A hostile creature resists your requests and is harder to influence.",
			seeAlso = listOf(ATTITUDE_ATTITUDE, INFLUENCE_ACTION)
		),
		CoreGlossaryEntry(
			term = "Immunity",
			summary = "Immunity means a damage type or condition does not affect you at all.",
			seeAlso = listOf("Resistance", "Vulnerability", "Condition")
		),
		CoreGlossaryEntry(
			term = "Incapacitated",
			tag = "Condition",
			summary = "You cannot take actions, bonus actions, or reactions; you also lose concentration and cannot speak.",
			seeAlso = listOf("Concentration", "Reaction")
		),
		CoreGlossaryEntry(
			term = "Indifferent",
			tag = "Attitude",
			summary = "An indifferent creature is neither inclined to help nor eager to hinder you.",
			seeAlso = listOf(ATTITUDE_ATTITUDE, INFLUENCE_ACTION)
		),
		CoreGlossaryEntry(
			term = "Influence",
			tag = "Action",
			summary =
				"Influence is the action for persuading, deceiving, threatening, amusing, or coaxing a " +
					"creature into cooperation.",
			bullets = listOf(
				"Willing creatures may comply without a roll.",
				"Hesitant creatures usually call for a Charisma or Wisdom check against a DC set by attitude and context."
			),
			seeAlso = listOf(ATTITUDE_ATTITUDE, FRIENDLY_ATTITUDE, "Hostile [Attitude]")
		),
		CoreGlossaryEntry(
			term = "Initiative",
			summary = "Initiative decides turn order in combat and is usually a Dexterity check.",
			bullets = listOf(
				"Monster stat blocks often show both the Initiative modifier and a fixed Initiative " +
					"score you can use instead of rolling."
			),
			seeAlso = listOf("Surprise", "Combat Sequence")
		),
		CoreGlossaryEntry(
			term = "Invisible",
			tag = "Condition",
			summary =
				"An invisible creature is concealed from effects that require sight, attacks with " +
					"advantage, and is attacked with disadvantage unless seen another way.",
			seeAlso = listOf("Hide [Action]", "Blindsight", "Truesight")
		),
		CoreGlossaryEntry(
			term = "Legendary Action",
			summary =
				"A legendary action is an action a monster can take immediately after another creature's " +
					"turn, spending from a limited pool that refreshes at the start of the monster's turn.",
			bullets = listOf("A monster cannot take legendary actions while incapacitated or otherwise unable to act."),
			seeAlso = listOf("Reaction", "Monster"),
			keywords = listOf("legendary actions")
		),
		CoreGlossaryEntry(
			term = "Limited Usage",
			summary =
				"Some monster features can be used only a fixed number of times per day, on a recharge " +
					"roll, or once before a short or long rest.",
			bullets = listOf(
				"X/Day restores after a Long Rest.",
				"Recharge X-Y returns when the listed d6 result appears at the start of the monster's " +
					"turn, or when it finishes a rest.",
				"Recharge after a Short or Long Rest restores only after one of those rests is completed."
			),
			seeAlso = listOf("Monster", SHORT_REST, LONG_REST),
			keywords = listOf("x/day", "recharge 5-6")
		),
		CoreGlossaryEntry(
			term = LONG_REST,
			summary =
				"A long rest is at least eight hours of extended downtime that restores hit points, spent " +
					"Hit Dice, and one level of exhaustion.",
			bullets = listOf(
				"You need at least 1 hit point to begin one.",
				"Major interruptions pause or end the rest unless it is resumed."
			),
			seeAlso = listOf(SHORT_REST, EXHAUSTION_CONDITION)
		),
		CoreGlossaryEntry(
			term = "Magic",
			tag = "Action",
			summary =
				"Magic is the action used to cast an action-time spell or activate a magical feature or " +
					"item that says so.",
			bullets = listOf(
				"Longer casting times require repeated Magic actions and ongoing concentration until the " +
					"casting finishes."
			),
			seeAlso = listOf("Spell", "Concentration", "Ritual")
		),
		CoreGlossaryEntry(
			term = "Monster",
			summary =
				"A monster is any creature run from a stat block, including beasts, foes, NPC opponents, " +
					"and unusual beings summoned or encountered in play.",
			bullets = listOf(
				"A monster stat block usually groups identity, defenses, movement, senses, challenge " +
					"rating, traits, and actions."
			),
			seeAlso = listOf(STAT_BLOCK, CHALLENGE_RATING, "Creature Type")
		),
		CoreGlossaryEntry(
			term = "Multiattack",
			summary =
				"Multiattack lets a creature make more than one listed attack, or combine listed attacks " +
					"and abilities, as part of the Attack action.",
			bullets = listOf(
				"When a monster has Multiattack, it usually relies on it during turns when a stronger " +
					"special option is unavailable."
			),
			seeAlso = listOf(ATTACK_ACTION, "Monster")
		),
		CoreGlossaryEntry(
			term = "Nonplayer Character",
			summary = "An NPC is a named, personality-driven creature run by the Game Master.",
			seeAlso = listOf("Monster")
		),
		CoreGlossaryEntry(
			term = "Object",
			summary =
				"An object is a distinct nonliving thing, while larger structures are usually treated as " +
					"multiple objects.",
			seeAlso = listOf(BREAKING_OBJECTS, "Size")
		),
		CoreGlossaryEntry(
			term = OPPORTUNITY_ATTACKS,
			summary = "Leaving a creature's reach with normal movement usually provokes a reactionary melee attack.",
			bullets = listOf("Teleportation and forced movement do not trigger it unless a rule says otherwise."),
			seeAlso = listOf("Reaction", "Reach", "Teleportation")
		),
		CoreGlossaryEntry(
			term = "Paralyzed",
			tag = "Condition",
			summary =
				"Paralysis incapacitates you, fixes your speed at 0, causes automatic Strength and " +
					"Dexterity save failures, and makes nearby hits into critical hits.",
			seeAlso = listOf("Incapacitated [Condition]", "Critical Hit")
		),
		CoreGlossaryEntry(
			term = "Passive Perception",
			summary = "Passive Perception is a creature's always-on awareness score, usually 10 plus its Perception bonus.",
			seeAlso = listOf("Search [Action]", "Wisdom (Perception)")
		),
		CoreGlossaryEntry(
			term = "Prone",
			tag = "Condition",
			summary = "A prone creature crawls or spends movement to stand, attacks poorly, and is easier to hit from nearby.",
			seeAlso = listOf("Speed", "Crawling")
		),
		CoreGlossaryEntry(
			term = "Proficiency",
			summary =
				"Proficiency lets you add your proficiency bonus to a d20 test that uses the trained " +
					"skill, save, weapon, spell, or tool.",
			bullets = listOf("The same proficiency bonus is not added to the same roll more than once."),
			seeAlso = listOf(ABILITY_CHECK, SAVING_THROW, WEAPON_ATTACK)
		),
		CoreGlossaryEntry(
			term = "Reaction",
			summary =
				"A reaction is a special response to a trigger and you can take only one before the start " +
					"of your next turn.",
			seeAlso = listOf(OPPORTUNITY_ATTACKS, "Ready [Action]")
		),
		CoreGlossaryEntry(
			term = "Ready",
			tag = "Action",
			summary = "Ready sets a trigger so you can use your reaction for a chosen action or movement later in the round.",
			bullets = listOf(
				"Readied spells are cast in advance, held with concentration, and released when the " +
					"trigger happens."
			),
			seeAlso = listOf("Reaction", "Concentration", MAGIC_ACTION)
		),
		CoreGlossaryEntry(
			term = "Resistance",
			summary =
				"Resistance halves damage of a listed type against you, rounding down, and each source " +
					"applies only once per hit.",
			seeAlso = listOf(DAMAGE_TYPES, "Vulnerability", "Immunity")
		),
		CoreGlossaryEntry(
			term = "Ritual",
			summary = "A prepared spell with the ritual tag can be cast more slowly without using a spell slot.",
			bullets = listOf("Ritual casting adds ten minutes and cannot heighten the spell with a higher-level slot."),
			seeAlso = listOf("Spell", MAGIC_ACTION)
		),
		CoreGlossaryEntry(
			term = "Round Down",
			summary = "Fractions from multiplication or division are rounded down unless a rule explicitly says otherwise.",
			seeAlso = listOf("Damage", "Resistance")
		),
		CoreGlossaryEntry(
			term = SAVING_THROW,
			summary =
				"A saving throw resists or avoids a rule-defined threat, and a creature may choose to fail " +
					"one voluntarily.",
			seeAlso = listOf(D20_TEST, DIFFICULTY_CLASS)
		),
		CoreGlossaryEntry(
			term = "Search",
			tag = "Action",
			summary = "Search uses Wisdom to notice something that is present but not obvious.",
			bullets = listOf(
				"Depending on the target, Insight, Medicine, Perception, or Survival may be the most " +
					"fitting skill."
			),
			seeAlso = listOf("Passive Perception", "Study [Action]")
		),
		CoreGlossaryEntry(
			term = SHORT_REST,
			summary = "A short rest is one hour of light downtime that allows Hit Dice spending and other short-rest recharges.",
			seeAlso = listOf(LONG_REST, HIT_POINTS)
		),
		CoreGlossaryEntry(
			term = "Speed",
			summary =
				"Speed is the distance a creature can move on its turn, and special speeds replace or mix " +
					"with normal movement as needed.",
			bullets = listOf(
				"When you switch between speeds, subtract distance already moved from the new speed to " +
					"see what remains."
			),
			seeAlso = listOf("Difficult Terrain", "Dash [Action]")
		),
		CoreGlossaryEntry(
			term = "Spell",
			summary = "A spell is a magical effect with defined components, range, duration, and casting time.",
			seeAlso = listOf("Spell Attack", "Spellcasting Focus", "Concentration")
		),
		CoreGlossaryEntry(
			term = "Spell Attack",
			summary = "A spell attack is an attack roll created by a spell or another magical effect.",
			seeAlso = listOf(ATTACK_ROLL, "Spell")
		),
		CoreGlossaryEntry(
			term = "Spellcasting Focus",
			summary = "A permitted focus can replace many non-costly, non-consumed material spell components.",
			seeAlso = listOf("Spell", MAGIC_ACTION)
		),
		CoreGlossaryEntry(
			term = "Stable",
			summary = "A stable creature has 0 hit points but no longer makes death saving throws.",
			seeAlso = listOf(DEATH_SAVING_THROW, UNCONSCIOUS_CONDITION)
		),
		CoreGlossaryEntry(
			term = STAT_BLOCK,
			summary =
				"A stat block is a monster's compact rules summary, gathering identity, defenses, " +
					"movement, senses, traits, actions, and challenge rating.",
			bullets = listOf(
				"Common entries include Armor Class, Hit Points, Speed, Initiative, ability scores, " +
					"saving throws, senses, and languages.",
				"Some stat blocks also list skills, resistances, vulnerabilities, immunities, gear, " +
					"bonus actions, reactions, or legendary actions."
			),
			seeAlso = listOf(CHALLENGE_RATING, "Monster", "Gear")
		),
		CoreGlossaryEntry(
			term = "Study",
			tag = "Action",
			summary = "Study uses Intelligence to recall knowledge or analyze clues, books, symbols, traps, or similar details.",
			bullets = listOf("Arcana, History, Investigation, Nature, and Religion are common skills for this action."),
			seeAlso = listOf("Search [Action]", ABILITY_CHECK)
		),
		CoreGlossaryEntry(
			term = "Suffocation",
			tag = "Hazard",
			summary = "When a creature runs out of breath or is choking, it gains exhaustion until it can breathe again.",
			seeAlso = listOf(EXHAUSTION_CONDITION, "Hazard")
		),
		CoreGlossaryEntry(
			term = "Target",
			summary = "A target is the creature or object chosen for an attack, save-forcing effect, spell, or similar rule.",
			seeAlso = listOf(ATTACK_ROLL, SAVING_THROW, "Spell")
		),
		CoreGlossaryEntry(
			term = "Telepathy",
			summary =
				"Telepathy is a magical ability that lets a creature communicate mentally with another " +
					"creature within a listed range.",
			seeAlso = listOf("Languages", "Monster"),
			keywords = listOf("mental communication")
		),
		CoreGlossaryEntry(
			term = "Teleportation",
			summary =
				"Teleportation moves you instantly without crossing intervening space, spending " +
					"movement, or provoking opportunity attacks.",
			bullets = listOf(
				"Worn and carried gear travels with you.",
				"If the destination is blocked, you appear in the nearest open space you choose."
			),
			seeAlso = listOf(OPPORTUNITY_ATTACKS, "Target")
		),
		CoreGlossaryEntry(
			term = TEMPORARY_HIT_POINTS,
			summary = "Temporary hit points absorb damage before real hit points, never stack together, and are not healing.",
			seeAlso = listOf(HIT_POINTS, "Healing")
		),
		CoreGlossaryEntry(
			term = UNARMED_STRIKE,
			summary =
				"An unarmed strike is a melee attack made with the body and can deal damage, start a " +
					"grapple, or shove a nearby target.",
			bullets = listOf("The grapple and shove options use a save DC based on Strength and proficiency."),
			seeAlso = listOf(ATTACK_ACTION, "Grappling")
		),
		CoreGlossaryEntry(
			term = "Unconscious",
			tag = "Condition",
			summary =
				"You become inert, prone, unaware, unable to move, and easy to hit; nearby hits are " +
					"automatic critical hits.",
			seeAlso = listOf(DEATH_SAVING_THROW, "Stable", "Prone [Condition]")
		),
		CoreGlossaryEntry(
			term = "Utilize",
			tag = "Action",
			summary =
				"Utilize is the action for using a nonmagical object when the object's rule says doing " +
					"so takes an action.",
			seeAlso = listOf("Action", BREAKING_OBJECTS)
		),
		CoreGlossaryEntry(
			term = "Vulnerability",
			summary = "Vulnerability doubles damage of a listed type against you, and each source applies only once per hit.",
			seeAlso = listOf("Resistance", "Immunity", DAMAGE_TYPES)
		),
		CoreGlossaryEntry(
			term = WEAPON_ATTACK,
			summary = "A weapon attack is an attack roll made with a weapon.",
			seeAlso = listOf(ATTACK_ROLL, "Weapon")
		),
		CoreGlossaryEntry(
			term = "Weapon",
			summary = "A weapon is an item in the simple or martial weapon categories.",
			seeAlso = listOf(WEAPON_ATTACK, ATTACK_ACTION)
		)
	)

	val GLOSSARY_TABLES = listOf(GLOSSARY_ABBREVIATIONS_TABLE)

	val ALL_TABLES = listOf(
		ABILITY_DESCRIPTIONS_TABLE,
		ABILITY_SCORE_BENCHMARKS_TABLE,
		ABILITY_MODIFIERS_TABLE,
		ATTACK_ROLL_ABILITIES_TABLE,
		TYPICAL_DIFFICULTY_CLASSES_TABLE,
		PROFICIENCY_BONUS_TABLE,
		SKILLS_TABLE,
		ACTIONS_TABLE,
		TRAVEL_PACE_TABLE,
		CREATURE_SIZE_AND_SPACE_TABLE,
		MONSTER_CREATURE_TYPES_TABLE,
		MONSTER_HIT_DICE_BY_SIZE_TABLE,
		EXPERIENCE_POINTS_BY_CHALLENGE_RATING_TABLE,
		COVER_TABLE
	)
}
