/*
 * FILE: CharacterCreationReferenceTablesData.kt
 *
 * TABLE OF CONTENTS:
 * 1. Character Creation Tables Dataset
 */

package io.github.velyene.loreweaver.domain.util

internal object CharacterCreationReferenceTablesData {
	val abilityModifiers = mapOf(
		"1" to "−5",
		"2–3" to "−4",
		"4–5" to "−3",
		"6–7" to "−2",
		"8–9" to "−1",
		"10–11" to "+0",
		"12–13" to "+1",
		"14–15" to "+2",
		"16–17" to "+3",
		"18–19" to "+4",
		"20–21" to "+5",
		"22–23" to "+6",
		"24–25" to "+7",
		"26–27" to "+8",
		"28–29" to "+9",
		"30" to "+10"
	)

	val characterCreationTables = listOf(
		ReferenceTable(
			title = "Class Overview",
			columns = listOf("Class", "Likes", "Primary Ability", "Complexity"),
			rows = listOf(
				listOf("Barbarian", "Battle", "Strength", "Average"),
				listOf("Bard", "Performing", "Charisma", "High"),
				listOf("Cleric", "Gods", "Wisdom", "Average"),
				listOf("Druid", "Nature", "Wisdom", "High"),
				listOf("Fighter", "Weapons", "Strength or Dexterity", "Low"),
				listOf("Monk", "Unarmed combat", "Dexterity and Wisdom", "High"),
				listOf("Paladin", "Defense", "Strength and Charisma", "Average"),
				listOf("Ranger", "Survival", "Dexterity and Wisdom", "Average"),
				listOf("Rogue", "Stealth", "Dexterity", "Low"),
				listOf("Sorcerer", "Power", "Charisma", "High"),
				listOf("Warlock", "Occult lore", "Charisma", "High"),
				listOf("Wizard", "Spellbooks", "Intelligence", "Average")
			)
		),
		ReferenceTable(
			title = "Ability Scores and Backgrounds",
			columns = listOf("Ability", "Background"),
			rows = CharacterCreationReferenceCatalogData.backgrounds.flatMap { background ->
				background.abilityScores.map { ability ->
					listOf(ability, background.name)
				}
			}
		),
		ReferenceTable(
			title = "Standard Languages",
			columns = listOf("Roll", "Language"),
			rows = CharacterCreationReferenceCatalogData.standardLanguages.map { language ->
				listOf(language.roll.orEmpty(), language.name)
			}
		),
		ReferenceTable(
			title = "Rare Languages",
			columns = listOf("Language A", "Language B"),
			rows = CharacterCreationReferenceCatalogData.rareLanguages.chunked(2).map { pair ->
				listOf(pair.first().name, pair.getOrNull(1)?.name.orEmpty())
			}
		),
		ReferenceTable(
			title = "Ability Score Point Costs",
			columns = listOf("Score", "Cost"),
			rows = listOf(
				listOf("8", "0"),
				listOf("9", "1"),
				listOf("10", "2"),
				listOf("11", "3"),
				listOf("12", "4"),
				listOf("13", "5"),
				listOf("14", "7"),
				listOf("15", "9")
			)
		),
		ReferenceTable(
			title = "Standard Array by Class",
			columns = listOf("Class", "Str", "Dex", "Con", "Int", "Wis", "Cha"),
			rows = listOf(
				listOf("Barbarian", "15", "13", "14", "10", "12", "8"),
				listOf("Bard", "8", "14", "12", "13", "10", "15"),
				listOf("Cleric", "14", "8", "13", "10", "15", "12"),
				listOf("Druid", "8", "12", "14", "13", "15", "10"),
				listOf("Fighter", "15", "14", "13", "8", "10", "12"),
				listOf("Monk", "12", "15", "13", "10", "14", "8"),
				listOf("Paladin", "15", "10", "13", "8", "12", "14"),
				listOf("Ranger", "12", "15", "13", "8", "14", "10"),
				listOf("Rogue", "12", "15", "13", "14", "10", "8"),
				listOf("Sorcerer", "10", "13", "14", "8", "12", "15"),
				listOf("Warlock", "8", "14", "13", "12", "10", "15"),
				listOf("Wizard", "8", "12", "13", "15", "14", "10")
			)
		),
		ReferenceTable(
			title = "Ability Scores and Modifiers (Creation Range)",
			columns = listOf("Score", "Modifier"),
			rows = listOf(
				listOf("3", "−4"),
				listOf("4–5", "−3"),
				listOf("6–7", "−2"),
				listOf("8–9", "−1"),
				listOf("10–11", "+0"),
				listOf("12–13", "+1"),
				listOf("14–15", "+2"),
				listOf("16–17", "+3"),
				listOf("18–19", "+4"),
				listOf("20", "+5")
			)
		),
		ReferenceTable(
			title = "Level 1 Hit Points by Class",
			columns = listOf("Class", "Hit Point Maximum"),
			rows = listOf(
				listOf("Barbarian", "12 + Constitution modifier"),
				listOf("Fighter, Paladin, or Ranger", "10 + Constitution modifier"),
				listOf("Bard, Cleric, Druid, Monk, Rogue, Warlock", "8 + Constitution modifier"),
				listOf("Sorcerer or Wizard", "6 + Constitution modifier")
			)
		),
		ReferenceTable(
			title = "Character Advancement",
			columns = listOf("Level", "Experience Points", "Proficiency Bonus"),
			rows = listOf(
				listOf("1", "0", "+2"),
				listOf("2", "300", "+2"),
				listOf("3", "900", "+2"),
				listOf("4", "2,700", "+2"),
				listOf("5", "6,500", "+3"),
				listOf("6", "14,000", "+3"),
				listOf("7", "23,000", "+3"),
				listOf("8", "34,000", "+3"),
				listOf("9", "48,000", "+4"),
				listOf("10", "64,000", "+4"),
				listOf("11", "85,000", "+4"),
				listOf("12", "100,000", "+4"),
				listOf("13", "120,000", "+5"),
				listOf("14", "140,000", "+5"),
				listOf("15", "165,000", "+5"),
				listOf("16", "195,000", "+5"),
				listOf("17", "225,000", "+6"),
				listOf("18", "265,000", "+6"),
				listOf("19", "305,000", "+6"),
				listOf("20", "355,000", "+6")
			)
		),
		ReferenceTable(
			title = "Fixed Hit Points by Class",
			columns = listOf("Class", "Hit Points per Level"),
			rows = listOf(
				listOf("Barbarian", "7 + Constitution modifier"),
				listOf("Fighter, Paladin, or Ranger", "6 + Constitution modifier"),
				listOf("Bard, Cleric, Druid, Monk, Rogue, Warlock", "5 + Constitution modifier"),
				listOf("Sorcerer or Wizard", "4 + Constitution modifier")
			)
		),
		ReferenceTable(
			title = "Starting Equipment at Higher Levels",
			columns = listOf("Starting Level", "Equipment and Money", "Magic Items"),
			rows = listOf(
				listOf("2–4", "Normal starting equipment", "1 Common"),
				listOf(
					"5–10",
					"500 GP plus 1d10 × 25 GP plus normal starting equipment",
					"1 Common, 1 Uncommon"
				),
				listOf(
					"11–16",
					"5,000 GP plus 1d10 × 250 GP plus normal starting equipment",
					"2 Common, 3 Uncommon, 1 Rare"
				),
				listOf(
					"17–20",
					"20,000 GP plus 1d10 × 250 GP plus normal starting equipment",
					"2 Common, 4 Uncommon, 3 Rare, 1 Very Rare"
				)
			)
		),
		ReferenceTable(
			title = "Multiclass Spellcaster: Spell Slots per Spell Level",
			columns = listOf("Level", "1", "2", "3", "4", "5", "6", "7", "8", "9"),
			rows = listOf(
				listOf("1", "2", "—", "—", "—", "—", "—", "—", "—", "—"),
				listOf("2", "3", "—", "—", "—", "—", "—", "—", "—", "—"),
				listOf("3", "4", "2", "—", "—", "—", "—", "—", "—", "—"),
				listOf("4", "4", "3", "—", "—", "—", "—", "—", "—", "—"),
				listOf("5", "4", "3", "2", "—", "—", "—", "—", "—", "—"),
				listOf("6", "4", "3", "3", "—", "—", "—", "—", "—", "—"),
				listOf("7", "4", "3", "3", "1", "—", "—", "—", "—", "—"),
				listOf("8", "4", "3", "3", "2", "—", "—", "—", "—", "—"),
				listOf("9", "4", "3", "3", "3", "1", "—", "—", "—", "—"),
				listOf("10", "4", "3", "3", "3", "2", "—", "—", "—", "—"),
				listOf("11", "4", "3", "3", "3", "2", "1", "—", "—", "—"),
				listOf("12", "4", "3", "3", "3", "2", "1", "—", "—", "—"),
				listOf("13", "4", "3", "3", "3", "2", "1", "1", "—", "—"),
				listOf("14", "4", "3", "3", "3", "2", "1", "1", "—", "—"),
				listOf("15", "4", "3", "3", "3", "2", "1", "1", "1", "—"),
				listOf("16", "4", "3", "3", "3", "2", "1", "1", "1", "—"),
				listOf("17", "4", "3", "3", "3", "2", "1", "1", "1", "1"),
				listOf("18", "4", "3", "3", "3", "3", "1", "1", "1", "1"),
				listOf("19", "4", "3", "3", "3", "3", "2", "1", "1", "1"),
				listOf("20", "4", "3", "3", "3", "3", "2", "2", "1", "1")
			)
		)
	)
}
