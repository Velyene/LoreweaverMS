/*
 * FILE: CoreRulesReferenceTablesData.kt
 *
 * TABLE OF CONTENTS:
 * 1. Core Rules Reference Tables Dataset
 */

package io.github.velyene.loreweaver.domain.util

internal object CoreRulesReferenceTablesData {
	val abilityDescriptionsTable = ReferenceTable(
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

	val abilityScoreBenchmarksTable = ReferenceTable(
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

	val abilityModifiersTable = ReferenceTable(
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

	val attackRollAbilitiesTable = ReferenceTable(
		title = "Attack Roll Abilities",
		columns = listOf("Ability", "Attack Type"),
		rows = listOf(
			listOf("Strength", "Melee weapon attacks and unarmed strikes"),
			listOf("Dexterity", "Ranged weapon attacks"),
			listOf("Varies", "Spell attacks use the attacker’s spellcasting ability")
		)
	)

	val typicalDifficultyClassesTable = ReferenceTable(
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

	val proficiencyBonusTable = ReferenceTable(
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

	val skillsTable = ReferenceTable(
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

	val actionsTable = ReferenceTable(
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

	val travelPaceTable = ReferenceTable(
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

	val creatureSizeAndSpaceTable = ReferenceTable(
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

	val monsterCreatureTypesTable = ReferenceTable(
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

	val monsterHitDiceBySizeTable = ReferenceTable(
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

	val experiencePointsByChallengeRatingTable = ReferenceTable(
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

	val coverTable = ReferenceTable(
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

	val glossaryAbbreviationsTable = ReferenceTable(
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

	val allTables = listOf(
		abilityDescriptionsTable,
		abilityScoreBenchmarksTable,
		abilityModifiersTable,
		attackRollAbilitiesTable,
		typicalDifficultyClassesTable,
		proficiencyBonusTable,
		skillsTable,
		actionsTable,
		travelPaceTable,
		creatureSizeAndSpaceTable,
		monsterCreatureTypesTable,
		monsterHitDiceBySizeTable,
		experiencePointsByChallengeRatingTable,
		coverTable
	)
}
