/*
 * FILE: MagicItemsSrdAuditTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Canonical magic-item excerpt scope verification
 * 2. Searchable mechanics and section-shape validation
 * 3. Generic example rows, table scope, and crafting guardrails
 */

package io.github.velyene.loreweaver.domain.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MagicItemsSrdAuditTest {
	private companion object {
		const val POTION_OF_HEALING = "Potion of Healing"
		const val SPELL_SCROLL = "Spell Scroll"
	}

	@Test
	fun equipmentReference_magicItemsAtoZ_matchProvidedExcerptScope() {
		assertEquals(
			listOf(
				"Adamantine Armor",
				"Ammunition, +1, +2, or +3",
				"Ammunition of Slaying",
				"Amulet of Health",
				"Amulet of Proof against Detection and Location",
				"Amulet of the Planes",
				"Animated Shield",
				"Apparatus of the Crab",
				"Armor, +1, +2, or +3",
				"Armor of Invulnerability",
				"Armor of Resistance",
				"Armor of Vulnerability",
				"Arrow-Catching Shield",
				"Bag of Beans",
				"Bag of Devouring",
				"Bag of Holding",
				"Bag of Tricks",
				"Bead of Force",
				"Bead of Nourishment",
				"Belt of Dwarvenkind",
				"Belt of Giant Strength",
				"Berserker Axe",
				"Boots of Elvenkind",
				"Boots of Levitation",
				"Boots of Speed",
				"Boots of Striding and Springing",
				"Boots of the Winterlands",
				"Bowl of Commanding Water Elementals",
				"Bracers of Archery",
				"Bracers of Defense",
				"Brazier of Commanding Fire Elementals",
				"Brooch of Shielding",
				"Broom of Flying",
				"Candle of Invocation",
				"Cape of the Mountebank",
				"Carpet of Flying",
				"Censer of Controlling Air Elementals",
				"Chime of Opening",
				"Circlet of Blasting",
				"Cloak of Arachnida",
				"Cloak of Displacement",
				"Cloak of Elvenkind",
				"Cloak of Invisibility",
				"Cloak of Protection",
				"Cloak of the Bat",
				"Cloak of the Manta Ray",
				"Crystal Ball",
				"Crystal Ball of Mind Reading",
				"Crystal Ball of Telepathy",
				"Crystal Ball of True Seeing",
				"Cube of Force",
				"Cubic Gate",
				"Dagger of Venom",
				"Dancing Sword",
				"Decanter of Endless Water",
				"Deck of Illusions",
				"Defender",
				"Demon Armor",
				"Dimensional Shackles",
				"Dragon Orb",
				"Dragon Scale Mail",
				"Dragon Slayer",
				"Dust of Disappearance",
				"Dust of Dryness",
				"Dust of Sneezing and Choking",
				"Dwarven Plate",
				"Dwarven Thrower",
				"Efficient Quiver",
				"Efreeti Bottle",
				"Elemental Gem",
				"Elixir of Health",
				"Elven Chain",
				"Energy Bow",
				"Eversmoking Bottle",
				"Eyes of Charming",
				"Eyes of Minute Seeing",
				"Eyes of the Eagle",
				"Feather Token",
				"Figurine of Wondrous Power",
				"Flame Tongue",
				"Folding Boat",
				"Frost Brand",
				"Gauntlets of Ogre Power",
				"Gem of Brightness",
				"Gem of Seeing",
				"Giant Slayer",
				"Glamoured Studded Leather",
				"Gloves of Missile Snaring",
				"Gloves of Swimming and Climbing",
				"Gloves of Thievery",
				"Goggles of Night",
				"Hammer of Thunderbolts",
				"Handy Haversack",
				"Hat of Disguise",
				"Hat of Many Spells",
				"Headband of Intellect",
				"Helm of Brilliance",
				"Helm of Comprehending Languages",
				"Helm of Telepathy",
				"Helm of Teleportation",
				"Holy Avenger",
				"Horn of Blasting",
				"Horn of Valhalla",
				"Horseshoes of a Zephyr",
				"Horseshoes of Speed",
				"Immovable Rod",
				"Instant Fortress",
				"Ioun Stone",
				"Iron Bands",
				"Iron Flask",
				"Javelin of Lightning",
				"Lantern of Revealing",
				"Luck Blade",
				"Mace of Disruption",
				"Mace of Smiting",
				"Mace of Terror",
				"Mantle of Spell Resistance",
				"Manual of Bodily Health",
				"Manual of Gainful Exercise",
				"Manual of Golems",
				"Manual of Quickness of Action",
				"Marvelous Pigments",
				"Medallion of Thoughts",
				"Mirror of Life Trapping",
				"Mithral Armor",
				"Mysterious Deck",
				"Necklace of Adaptation",
				"Necklace of Fireballs",
				"Necklace of Prayer Beads",
				"Nine Lives Stealer",
				"Oathbow",
				"Oil of Etherealness",
				"Oil of Sharpness",
				"Oil of Slipperiness",
				"Pearl of Power",
				"Periapt of Health",
				"Periapt of Proof against Poison",
				"Periapt of Wound Closure",
				"Philter of Love",
				"Pipes of Haunting",
				"Pipes of the Sewers",
				"Plate Armor of Etherealness",
				"Portable Hole",
				"Potion of Animal Friendship",
				"Potion of Clairvoyance",
				"Potion of Climbing",
				"Potion of Diminution",
				"Potion of Flying",
				"Potion of Gaseous Form",
				"Potion of Giant Strength",
				"Potion of Growth",
				"Potions of Healing",
				"Potion of Heroism",
				"Potion of Invisibility",
				"Potion of Invulnerability",
				"Potion of Longevity",
				"Potion of Mind Reading",
				"Potion of Poison",
				"Potion of Resistance",
				"Potion of Speed",
				"Potion of Vitality",
				"Potion of Water Breathing",
				"Quarterstaff of the Acrobat",
				"Ring of Animal Influence",
				"Ring of Djinni Summoning",
				"Ring of Elemental Command",
				"Ring of Evasion",
				"Ring of Feather Falling",
				"Ring of Free Action",
				"Ring of Invisibility",
				"Ring of Jumping",
				"Ring of Mind Shielding",
				"Ring of Protection",
				"Ring of Regeneration",
				"Ring of Resistance",
				"Ring of Shooting Stars",
				"Ring of Spell Storing",
				"Ring of Spell Turning",
				"Ring of Swimming",
				"Ring of Telekinesis",
				"Ring of the Ram",
				"Ring of Three Wishes",
				"Ring of Warmth",
				"Ring of Water Walking",
				"Ring of X-ray Vision",
				"Robe of Eyes",
				"Robe of Scintillating Colors",
				"Robe of Stars",
				"Robe of the Archmagi",
				"Robe of Useful Items",
				"Rod of Absorption",
				"Rod of Alertness",
				"Rod of Lordly Might",
				"Rod of Resurrection",
				"Rod of Rulership",
				"Rod of Security",
				"Rope of Climbing",
				"Rope of Entanglement",
				"Scarab of Protection",
				"Scimitar of Speed",
				"Sending Stones",
				"Sentinel Shield",
				"Shield, +1, +2, or +3",
				"Shield of Missile Attraction",
				"Shield of the Cavalier",
				"Slippers of Spider Climbing",
				"Sovereign Glue",
				"Spellguard Shield",
				SPELL_SCROLL,
				"Sphere of Annihilation",
				"Staff of Charming",
				"Staff of Fire",
				"Staff of Frost",
				"Staff of Healing",
				"Staff of Power",
				"Staff of Striking",
				"Staff of Swarming Insects",
				"Staff of the Magi",
				"Staff of the Python",
				"Staff of the Woodlands",
				"Staff of Thunder and Lightning",
				"Staff of Withering",
				"Stone of Controlling Earth Elementals",
				"Stone of Good Luck (Luckstone)",
				"Sun Blade",
				"Sword of Life Stealing",
				"Sword of Sharpness",
				"Sword of Wounding",
				"Talisman of Pure Good",
				"Talisman of the Sphere",
				"Talisman of Ultimate Evil",
				"Thunderous Greatclub",
				"Tome of Clear Thought",
				"Tome of Leadership and Influence",
				"Tome of Understanding",
				"Trident of Fish Command",
				"Universal Solvent",
				"Vicious Weapon",
				"Vorpal Sword",
				"Wand of Binding",
				"Wand of Enemy Detection",
				"Wand of Fear",
				"Wand of Fireballs",
				"Wand of Lightning Bolts",
				"Wand of Magic Detection",
				"Wand of Magic Missiles",
				"Wand of Paralysis",
				"Wand of Polymorph",
				"Wand of Secrets",
				"Wand of the War Mage, +1, +2, or +3",
				"Wand of Web",
				"Wand of Wonder"
			),
			EquipmentReference.MAGIC_ITEMS_A_TO_Z.map { it.name }
		)
	}

	@Test
	fun equipmentReference_magicItemsAtoZ_preserveKeySearchableMechanics() {
		val amuletOfThePlanes =
			EquipmentReference.MAGIC_ITEMS_A_TO_Z.first { it.name == "Amulet of the Planes" }
		assertTrue(amuletOfThePlanes.body.contains("Plane Shift"))
		assertTrue(amuletOfThePlanes.tables.single().rows.any {
			it.last().contains("Astral Plane")
		})

		val bagOfBeans = EquipmentReference.MAGIC_ITEMS_A_TO_Z.first { it.name == "Bag of Beans" }
		assertTrue(bagOfBeans.tables.single().rows.any {
			it.last().contains("cloud giantâ€™s castle")
		})

		val crystalBallOfTelepathy =
			EquipmentReference.MAGIC_ITEMS_A_TO_Z.first { it.name == "Crystal Ball of Telepathy" }
		assertTrue(crystalBallOfTelepathy.body.contains("telepathically"))

		val dragonOrb = EquipmentReference.MAGIC_ITEMS_A_TO_Z.first { it.name == "Dragon Orb" }
		assertTrue(dragonOrb.body.contains("chromatic dragons within 40 miles"))
		assertTrue(dragonOrb.tables.single().rows.any { it.first() == "Scrying (save DC 18)" })

		val hatOfManySpells =
			EquipmentReference.MAGIC_ITEMS_A_TO_Z.first { it.name == "Hat of Many Spells" }
		assertTrue(hatOfManySpells.tables.single().rows.any { it.last().contains("portal") })

		val mysteriousDeck =
			EquipmentReference.MAGIC_ITEMS_A_TO_Z.first { it.name == "Mysterious Deck" }
		assertTrue(mysteriousDeck.tables.single().rows.any {
			it.first() == "Moon" && it.last().contains("Wish")
		})

		val spellScroll = EquipmentReference.MAGIC_ITEMS_A_TO_Z.first { it.name == SPELL_SCROLL }
		assertTrue(spellScroll.tables.single().rows.any { it.first() == "9" && it.last() == "+11" })

		val ringOfShootingStars =
			EquipmentReference.MAGIC_ITEMS_A_TO_Z.first { it.name == "Ring of Shooting Stars" }
		assertTrue(ringOfShootingStars.tables.single().rows.any { it.first() == "1" && it.last() == "4d12" })

		val wandOfWonder =
			EquipmentReference.MAGIC_ITEMS_A_TO_Z.first { it.name == "Wand of Wonder" }
		assertTrue(wandOfWonder.tables.single().rows.any { it.last().contains("Petrified") })
	}

	@Test
	fun equipmentReference_magicItemSections_followExcerptBackedScope() {
		val relatedTitles = EquipmentReference.CHAPTER_SECTIONS.map { it.title }.filter {
			it in setOf(
				"Magic Items",
				"Magic Item Rules",
				"Activating a Magic Item",
				"Crafting Magic Items",
				"Sentient Magic Items"
			)
		}.toSet()

		assertEquals(
			setOf(
				"Magic Items",
				"Magic Item Rules",
				"Activating a Magic Item",
				"Crafting Magic Items",
				"Sentient Magic Items"
			),
			relatedTitles
		)
		assertFalse(EquipmentReference.CHAPTER_SECTIONS.any { it.title == "Brewing Potions of Healing" })
		assertFalse(EquipmentReference.CHAPTER_SECTIONS.any { it.title == "Scribing Spell Scrolls" })
	}

	@Test
	fun equipmentReference_magicItemExamples_useGenericPotionAndScrollRows() {
		val magicItemRows = EquipmentReference.ADVENTURING_GEAR
			.map { it.name }
			.filter { it == POTION_OF_HEALING || it == SPELL_SCROLL }

		assertEquals(
			listOf(POTION_OF_HEALING, SPELL_SCROLL),
			magicItemRows
		)
		assertFalse(magicItemRows.any { it.contains("(") })
		assertFalse(EquipmentReference.ADVENTURING_GEAR.any { it.name == "Spell Scroll (Cantrip)" })
		assertFalse(EquipmentReference.ADVENTURING_GEAR.any { it.name == "Spell Scroll (Level 1)" })
		assertTrue(
			EquipmentReference.ADVENTURING_GEAR_TABLE.rows.contains(
				listOf(
					POTION_OF_HEALING,
					"1/2 lb.",
					"50 GP"
				)
			)
		)
		assertTrue(
			EquipmentReference.ADVENTURING_GEAR_TABLE.rows.contains(
				listOf(
					SPELL_SCROLL,
					"â€”",
					"Varies"
				)
			)
		)
	}

	@Test
	fun equipmentReference_magicItemTables_matchExcerptBackedScope() {
		val tableTitles = EquipmentReference.ALL_TABLES.map { it.title }

		assertTrue(tableTitles.contains("Magic Item Categories"))
		assertTrue(tableTitles.contains("Potion Miscibility"))
		assertTrue(tableTitles.contains("Magic Item Rarities and Values"))
		assertTrue(tableTitles.contains("Magic Item Tools"))
		assertTrue(tableTitles.contains("Magic Item Crafting Time and Cost"))
		assertTrue(tableTitles.contains("Sentient Itemâ€™s Alignment"))
		assertTrue(tableTitles.contains("Sentient Itemâ€™s Communication"))
		assertTrue(tableTitles.contains("Sentient Itemâ€™s Senses"))
		assertTrue(tableTitles.contains("Sentient Itemâ€™s Special Purpose"))
		assertFalse(tableTitles.contains("Spellcasting Services"))
		assertFalse(tableTitles.contains("Spell Scroll Costs"))
	}

	@Test
	fun toolCraftLists_referenceOnlyNonmagicOutputs_whenMagicItemToolsTableExists() {
		val calligrapher =
			EquipmentReference.ARTISANS_TOOLS.first { it.name == "Calligrapher's Supplies" }
		val herbalismKit = EquipmentReference.OTHER_TOOLS.first { it.name == "Herbalism Kit" }

		assertFalse(calligrapher.craft.contains(SPELL_SCROLL))
		assertFalse(herbalismKit.craft.contains("Potion of Healing"))
		assertTrue(
			EquipmentReference.MAGIC_ITEM_TOOLS_TABLE.rows.contains(
				listOf(
					"Scroll",
					"Calligrapherâ€™s Supplies"
				)
			)
		)
		assertTrue(
			EquipmentReference.MAGIC_ITEM_TOOLS_TABLE.rows.contains(
				listOf(
					"Potion",
					"Alchemistâ€™s Supplies or Herbalism Kit"
				)
			)
		)
	}
}
