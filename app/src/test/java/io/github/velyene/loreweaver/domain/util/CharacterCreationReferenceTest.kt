/*
 * FILE: CharacterCreationReferenceTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Creation flow, section, and table coverage
 * 2. Species guidance and SRD species validation
 * 3. Background, language, and feat datasets
 */

package io.github.velyene.loreweaver.domain.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterCreationReferenceTest {

	@Test
	fun steps_matchUpdatedCreationFlow() {
		val steps = CharacterCreationReference.STEPS

		assertEquals("Choose a Class", steps.first().title)
		assertEquals("Determine Origin", steps[1].title)
		assertEquals("Choose an Alignment", steps[3].title)
		assertEquals("Fill in Details", steps.last().title)
	}

	@Test
	fun characterCreationTables_includeLanguagesAdvancementAndMulticlassing() {
		val titles = CharacterCreationReference.CHARACTER_CREATION_TABLES.map { it.title }

		assertTrue(titles.contains("Standard Languages"))
		assertTrue(titles.contains("Character Advancement"))
		assertTrue(titles.contains("Multiclass Spellcaster: Spell Slots per Spell Level"))
	}

	@Test
	fun characterCreationSections_coverAdvancementAndMulticlassingGuidance() {
		val titles = CharacterCreationReference.CHARACTER_CREATION_SECTIONS.map { it.title }

		assertTrue(titles.contains("Level Advancement"))
		assertTrue(titles.contains("Gaining a Level"))
		assertTrue(titles.contains("Multiclassing"))
		assertTrue(titles.contains("Spellcasting Across Classes"))
	}

	@Test
	fun speciesGuidance_titles_useSpeciesTerminology() {
		val titles = CharacterCreationReference.RACE_CHAPTER_SECTIONS.map { it.title }

		assertTrue(titles.contains("Chapter 2: Species"))
		assertTrue(titles.contains("Choosing a Species"))
		assertTrue(titles.contains("Species Traits"))
		assertTrue(titles.contains("Less Common Species"))
	}

	@Test
	fun speciesEntries_matchAllowedSrdSpeciesExactly() {
		val species = CharacterCreationReference.RACES.associateBy { it.name }

		assertEquals(
			setOf(
				"Dragonborn",
				"Dwarf",
				"Elf",
				"Gnome",
				"Goliath",
				"Halfling",
				"Human",
				"Orc",
				"Tiefling"
			),
			species.keys
		)
		assertEquals(9, species.size)
		assertFalse(species.containsKey("Half-Elf"))
		assertFalse(species.containsKey("Half-Orc"))

		val goliath = species.getValue("Goliath")
		assertEquals("Strength +2, Constitution +1", goliath.abilityScoreIncrease)
		assertEquals("Common and Giant.", goliath.languages)
		assertTrue(goliath.traits.any { it.name == "Stone's Endurance" })

		val orc = species.getValue("Orc")
		assertEquals("Strength +2, Constitution +1", orc.abilityScoreIncrease)
		assertEquals("Common and Orc.", orc.languages)
		assertTrue(orc.traits.any { it.name == "Relentless Endurance" })
	}

	@Test
	fun speciesGuidanceAndAbilitySummaries_doNotMentionRemovedHalfSpecies() {
		val chapterText =
			CharacterCreationReference.RACE_CHAPTER_SECTIONS.joinToString("\n") { "${it.title}\n${it.body}" }
		val abilitySummaryText =
			CharacterCreationReference.ABILITY_SCORES.joinToString("\n") { it.racialIncreases }

		assertFalse(chapterText.contains("half-elf", ignoreCase = true))
		assertFalse(chapterText.contains("half-orc", ignoreCase = true))
		assertTrue(chapterText.contains("goliaths", ignoreCase = true))
		assertTrue(chapterText.contains("orcs", ignoreCase = true))

		assertFalse(abilitySummaryText.contains("Half-elf", ignoreCase = true))
		assertFalse(abilitySummaryText.contains("Half-orc", ignoreCase = true))
		assertTrue(abilitySummaryText.contains("Goliath", ignoreCase = true))
		assertTrue(abilitySummaryText.contains("Orc", ignoreCase = true))
	}

	@Test
	fun trinketList_containsHundredEntries() {
		assertEquals(100, CharacterCreationReference.TRINKETS.size)
	}

	@Test
	fun backgrounds_includeFourStructuredSrdEntriesWithExpectedFields() {
		val backgrounds = CharacterCreationReference.BACKGROUNDS.associateBy { it.name }

		assertEquals(setOf("Acolyte", "Criminal", "Sage", "Soldier"), backgrounds.keys)
		assertEquals(4, backgrounds.size)

		val acolyte = backgrounds.getValue("Acolyte")
		assertEquals(listOf("Intelligence", "Wisdom", "Charisma"), acolyte.abilityScores)
		assertEquals("Magic Initiate", acolyte.feat)
		assertEquals(listOf("Insight", "Religion"), acolyte.skillProficiencies)
		assertEquals("Calligrapher's Supplies", acolyte.toolProficiency)
		assertTrue(acolyte.equipmentOptions.first().contains("Holy Symbol"))

		val criminal = backgrounds.getValue("Criminal")
		assertEquals(listOf("Dexterity", "Constitution", "Intelligence"), criminal.abilityScores)
		assertEquals("Alert", criminal.feat)
		assertEquals(listOf("Sleight of Hand", "Stealth"), criminal.skillProficiencies)
		assertEquals("Thieves' Tools", criminal.toolProficiency)
		assertTrue(criminal.equipmentOptions.first().contains("Crowbar"))

		val sage = backgrounds.getValue("Sage")
		assertEquals(listOf("Constitution", "Intelligence", "Wisdom"), sage.abilityScores)
		assertEquals("Magic Initiate", sage.feat)
		assertEquals(listOf("Arcana", "History"), sage.skillProficiencies)
		assertEquals("Calligrapher's Supplies", sage.toolProficiency)
		assertTrue(sage.equipmentOptions.first().contains("Bottle of Ink"))

		val soldier = backgrounds.getValue("Soldier")
		assertEquals(listOf("Strength", "Dexterity", "Constitution"), soldier.abilityScores)
		assertEquals("Savage Attacker", soldier.feat)
		assertEquals(listOf("Athletics", "Intimidation"), soldier.skillProficiencies)
		assertEquals("Gaming Set", soldier.toolProficiency)
		assertTrue(soldier.equipmentOptions.first().contains("Healer's Kit"))

		backgrounds.values.forEach { background ->
			assertEquals(2, background.equipmentOptions.size)
			assertEquals("B: 50 GP.", background.equipmentOptions.last())
		}
	}

	@Test
	fun abilityScoresAndBackgroundsTable_isDerivedFromStructuredBackgrounds() {
		val table = CharacterCreationReference.CHARACTER_CREATION_TABLES.first {
			it.title == "Ability Scores and Backgrounds"
		}
		val expectedRows = CharacterCreationReference.BACKGROUNDS.flatMap { background ->
			background.abilityScores.map { ability -> listOf(ability, background.name) }
		}

		assertEquals(expectedRows, table.rows)
		assertEquals(
			CharacterCreationReference.BACKGROUNDS.sumOf { it.abilityScores.size },
			table.rows.size
		)
		assertTrue(table.rows.contains(listOf("Dexterity", "Criminal")))
		assertTrue(table.rows.contains(listOf("Wisdom", "Sage")))
	}

	@Test
	fun languages_includeStructuredStandardAndRareEntries() {
		assertEquals(10, CharacterCreationReference.STANDARD_LANGUAGES.size)
		assertEquals(9, CharacterCreationReference.RARE_LANGUAGES.size)

		val common = CharacterCreationReference.STANDARD_LANGUAGES.first()
		assertEquals("Common", common.name)
		assertEquals("Standard", common.group)
		assertEquals("â€”", common.roll)

		val commonSign =
			CharacterCreationReference.STANDARD_LANGUAGES.first { it.name == "Common Sign Language" }
		assertEquals("1", commonSign.roll)

		val primordial = CharacterCreationReference.RARE_LANGUAGES.first { it.name == "Primordial" }
		assertEquals("Rare", primordial.group)
		assertEquals(null, primordial.roll)

		assertEquals(
			19,
			CharacterCreationReference.STANDARD_LANGUAGES.size + CharacterCreationReference.RARE_LANGUAGES.size
		)
	}

	@Test
	fun languageTables_areDerivedFromStructuredLanguageLists() {
		val standardTable = CharacterCreationReference.CHARACTER_CREATION_TABLES.first {
			it.title == "Standard Languages"
		}
		val rareTable = CharacterCreationReference.CHARACTER_CREATION_TABLES.first {
			it.title == "Rare Languages"
		}

		val expectedStandardRows = CharacterCreationReference.STANDARD_LANGUAGES.map { language ->
			listOf(language.roll.orEmpty(), language.name)
		}
		val expectedRareRows = CharacterCreationReference.RARE_LANGUAGES.chunked(2).map { pair ->
			listOf(pair.first().name, pair.getOrNull(1)?.name.orEmpty())
		}

		assertEquals(expectedStandardRows, standardTable.rows)
		assertEquals(expectedRareRows, rareTable.rows)
		assertTrue(standardTable.rows.contains(listOf("3â€“4", "Dwarvish")))
		assertTrue(rareTable.rows.contains(listOf("Infernal", "")))
	}

	@Test
	fun feats_includeStructuredEntriesAcrossCategories() {
		val feats = CharacterCreationReference.FEATS.associateBy { it.name }
		val expectedFeatNames = listOf(
			"Alert",
			"Magic Initiate",
			"Savage Attacker",
			"Skilled",
			"Ability Score Improvement",
			"Grappler",
			"Archery",
			"Defense",
			"Great Weapon Fighting",
			"Two-Weapon Fighting",
			"Boon of Combat Prowess",
			"Boon of Dimensional Travel",
			"Boon of Fate",
			"Boon of Irresistible Offense",
			"Boon of Spell Recall",
			"Boon of the Night Spirit",
			"Boon of Truesight"
		)

		assertEquals(17, feats.size)
		assertEquals(expectedFeatNames, CharacterCreationReference.FEATS.map { it.name })
		assertEquals(expectedFeatNames.toSet(), feats.keys)
		assertEquals("Origin", feats.getValue("Alert").category)
		assertEquals("General", feats.getValue("Grappler").category)
		assertEquals("Fighting Style", feats.getValue("Archery").category)
		assertEquals("Epic Boon", feats.getValue("Boon of Truesight").category)
		assertEquals("Level 4+", feats.getValue("Ability Score Improvement").prerequisite)
		assertTrue(feats.getValue("Magic Initiate").repeatable)
		assertTrue(feats.getValue("Skilled").repeatable)
		assertTrue(feats.getValue("Boon of Spell Recall").benefits.any { it.contains("level 1â€“4 spell slot") })
		assertTrue(feats.getValue("Boon of the Night Spirit").benefits.any { it.contains("Invisible condition") })
	}
}
