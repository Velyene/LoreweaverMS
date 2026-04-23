package com.example.loreweaver.domain.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EquipmentReferenceTest {

	@Test
	fun equipmentNames_matchAllowedSrdListsExactly() {
		assertEquals(
			listOf(
				"Club",
				"Dagger",
				"Greatclub",
				"Handaxe",
				"Javelin",
				"Light Hammer",
				"Mace",
				"Quarterstaff",
				"Sickle",
				"Spear",
				"Dart",
				"Light Crossbow",
				"Shortbow",
				"Sling",
				"Battleaxe",
				"Flail",
				"Glaive",
				"Greataxe",
				"Greatsword",
				"Halberd",
				"Lance",
				"Longsword",
				"Maul",
				"Morningstar",
				"Pike",
				"Rapier",
				"Scimitar",
				"Shortsword",
				"Trident",
				"War Pick",
				"Warhammer",
				"Whip",
				"Blowgun",
				"Hand Crossbow",
				"Heavy Crossbow",
				"Longbow",
				"Musket",
				"Pistol"
			),
			EquipmentReference.WEAPONS.map { it.name }
		)

		assertEquals(
			listOf(
				"Alchemist's Supplies",
				"Brewer's Supplies",
				"Calligrapher's Supplies",
				"Carpenter's Tools",
				"Cartographer's Tools",
				"Cobbler's Tools",
				"Cook's Utensils",
				"Glassblower's Tools",
				"Jeweler's Tools",
				"Leatherworker's Tools",
				"Mason's Tools",
				"Painter's Supplies",
				"Potter's Tools",
				"Smith's Tools",
				"Tinker's Tools",
				"Weaver's Tools",
				"Woodcarver's Tools"
			),
			EquipmentReference.ARTISANS_TOOLS.map { it.name }
		)

		assertEquals(
			listOf(
				"Disguise Kit",
				"Forgery Kit",
				"Gaming Set",
				"Herbalism Kit",
				"Musical Instrument",
				"Navigator's Tools",
				"Poisoner's Kit",
				"Thieves' Tools"
			),
			EquipmentReference.OTHER_TOOLS.map { it.name }
		)

		assertEquals(
			listOf(
				"Acid",
				"Alchemist’s Fire",
				"Ammunition",
				"Antitoxin",
				"Arcane Focus",
				"Backpack",
				"Ball Bearings",
				"Barrel",
				"Basket",
				"Bedroll",
				"Bell",
				"Blanket",
				"Block and Tackle",
				"Book",
				"Bottle, Glass",
				"Bucket",
				"Burglar’s Pack",
				"Caltrops",
				"Candle",
				"Case, Crossbow Bolt",
				"Case, Map or Scroll",
				"Chain",
				"Chest",
				"Climber’s Kit",
				"Clothes, Fine",
				"Clothes, Traveler’s",
				"Component Pouch",
				"Costume",
				"Crowbar",
				"Diplomat’s Pack",
				"Druidic Focus",
				"Dungeoneer’s Pack",
				"Entertainer’s Pack",
				"Explorer’s Pack",
				"Flask",
				"Grappling Hook",
				"Healer’s Kit",
				"Holy Symbol",
				"Holy Water",
				"Hunting Trap",
				"Ink",
				"Ink Pen",
				"Jug",
				"Ladder",
				"Lamp",
				"Lantern, Bullseye",
				"Lantern, Hooded",
				"Lock",
				"Magnifying Glass",
				"Manacles",
				"Map",
				"Mirror",
				"Net",
				"Oil",
				"Paper",
				"Parchment",
				"Perfume",
				"Poison, Basic",
				"Pole",
				"Pot, Iron",
				"Potion of Healing",
				"Pouch",
				"Priest’s Pack",
				"Quiver",
				"Ram, Portable",
				"Rations",
				"Robe",
				"Rope",
				"Sack",
				"Scholar’s Pack",
				"Shovel",
				"Signal Whistle",
				"Spell Scroll",
				"Spikes, Iron",
				"Spyglass",
				"String",
				"Tent",
				"Tinderbox",
				"Torch",
				"Vial",
				"Waterskin"
			),
			EquipmentReference.ADVENTURING_GEAR.map { it.name }
		)

		assertEquals(
			listOf("Arrows", "Bolts", "Bullets, Firearm", "Bullets, Sling", "Needles"),
			EquipmentReference.AMMUNITION.map { it.type }
		)

		assertEquals(
			listOf(
				"Crystal",
				"Orb",
				"Rod",
				"Staff",
				"Wand",
				"Sprig of mistletoe",
				"Wooden Staff",
				"Yew wand",
				"Amulet",
				"Emblem",
				"Reliquary"
			),
			EquipmentReference.FOCUSES.map { it.name }
		)

		assertEquals(
			listOf("Camel", "Elephant", "Horse", "Mastiff", "Mule", "Pony", "Warhorse"),
			EquipmentReference.MOUNTS.map { it.item }
		)

		assertEquals(
			listOf(
				"Carriage",
				"Cart",
				"Chariot",
				"Feed",
				"Saddle, Exotic",
				"Saddle, Military",
				"Saddle, Riding",
				"Sled",
				"Stabling",
				"Wagon"
			),
			EquipmentReference.TACK_AND_DRAWN_ITEMS.map { it.item }
		)

		assertEquals(
			listOf(
				"Airship",
				"Galley",
				"Keelboat",
				"Longship",
				"Rowboat",
				"Sailing Ship",
				"Warship"
			),
			EquipmentReference.LARGE_VEHICLES.map { it.ship }
		)

		assertEquals(
			listOf("Feed", "Stabling"),
			EquipmentReference.TACK_AND_DRAWN_ITEMS.filter { it.item == "Feed" || it.item == "Stabling" }
				.map { it.item }
		)
		assertEquals(
			listOf("Ale", "Bread", "Cheese", "Inn Stay", "Meal", "Wine, Common", "Wine, Fine"),
			EquipmentReference.FOOD_DRINK_AND_LODGING_TABLE.rows.map { it.first() }
		)
		assertEquals(
			listOf("Common", "Uncommon", "Rare", "Very Rare", "Legendary", "Artifact"),
			EquipmentReference.MAGIC_ITEM_RARITIES_AND_VALUES_TABLE.rows.map { it.first() }
		)

		val gamingSet = EquipmentReference.OTHER_TOOLS.first { it.name == "Gaming Set" }
		assertEquals(
			listOf("Dice", "Dragonchess", "Playing Cards", "Three-Dragon Ante"),
			gamingSet.variants
		)

		val musicalInstrument =
			EquipmentReference.OTHER_TOOLS.first { it.name == "Musical Instrument" }
		assertEquals(
			listOf(
				"Bagpipes",
				"Drum",
				"Dulcimer",
				"Flute",
				"Horn",
				"Lute",
				"Lyre",
				"Pan Flute",
				"Shawm",
				"Viol"
			), musicalInstrument.variants
		)
	}

	@Test
	fun weapons_includeStructuredEntriesAcrossCategories() {
		assertEquals(38, EquipmentReference.WEAPONS.size)

		val dagger = EquipmentReference.WEAPONS.first { it.name == "Dagger" }
		assertEquals("Simple Melee", dagger.category)
		assertEquals("1d4 Piercing", dagger.damage)
		assertTrue(dagger.properties.contains("Finesse"))
		assertEquals("Nick", dagger.mastery)

		val longbow = EquipmentReference.WEAPONS.first { it.name == "Longbow" }
		assertEquals("Martial Ranged", longbow.category)
		assertTrue(longbow.properties.contains("Heavy"))
		assertEquals("Slow", longbow.mastery)

		val musket = EquipmentReference.WEAPONS.first { it.name == "Musket" }
		assertEquals("500 GP", musket.cost)
		assertTrue(musket.properties.contains("Loading"))
	}

	@Test
	fun tools_includeStructuredArtisanAndOtherEntries() {
		assertEquals(17, EquipmentReference.ARTISANS_TOOLS.size)
		assertEquals(8, EquipmentReference.OTHER_TOOLS.size)
		assertEquals(25, EquipmentReference.ALL_TOOLS.size)

		val calligrapher =
			EquipmentReference.ARTISANS_TOOLS.first { it.name == "Calligrapher's Supplies" }
		assertEquals(ToolCategory.ARTISANS, calligrapher.category)
		assertEquals("Dexterity", calligrapher.ability)
		assertEquals("10 GP", calligrapher.cost)
		assertEquals(listOf("Ink"), calligrapher.craft)

		val gamingSet = EquipmentReference.OTHER_TOOLS.first { it.name == "Gaming Set" }
		assertEquals(ToolCategory.OTHER, gamingSet.category)
		assertEquals("Varies", gamingSet.cost)
		assertTrue(gamingSet.variants.contains("Dragonchess"))
		assertTrue(gamingSet.notes.any { it.contains("Three-Dragon Ante") })
		assertTrue(gamingSet.notes.contains("Variants require separate proficiency"))

		val thievesTools = EquipmentReference.OTHER_TOOLS.first { it.name == "Thieves' Tools" }
		assertEquals("Dexterity", thievesTools.ability)
		assertTrue(thievesTools.utilize.contains("Pick a lock (DC 15)"))
	}

	@Test
	fun weaponsTable_isDerivedFromStructuredWeaponList() {
		val expectedRows = EquipmentReference.WEAPONS.map { weapon ->
			listOf(
				weapon.name,
				weapon.category,
				weapon.damage,
				weapon.properties.joinToString().ifBlank { "—" },
				weapon.mastery,
				weapon.weight,
				weapon.cost
			)
		}

		assertEquals(expectedRows, EquipmentReference.WEAPONS_TABLE.rows)
		assertTrue(
			EquipmentReference.WEAPONS_TABLE.rows.contains(
				listOf(
					"Dagger",
					"Simple Melee",
					"1d4 Piercing",
					"Finesse, Light, Thrown (Range 20/60)",
					"Nick",
					"1 lb.",
					"2 GP"
				)
			)
		)
		assertTrue(
			EquipmentReference.WEAPONS_TABLE.rows.contains(
				listOf(
					"Musket",
					"Martial Ranged",
					"1d12 Piercing",
					"Ammunition (Range 40/120; Bullet), Loading, Two-Handed",
					"Slow",
					"10 lb.",
					"500 GP"
				)
			)
		)
	}

	@Test
	fun armor_includesStructuredEntriesAcrossCategories() {
		val expectedArmorNames = setOf(
			"Padded Armor",
			"Leather Armor",
			"Studded Leather Armor",
			"Hide Armor",
			"Chain Shirt",
			"Scale Mail",
			"Breastplate",
			"Half Plate Armor",
			"Ring Mail",
			"Chain Mail",
			"Splint Armor",
			"Plate Armor",
			"Shield"
		)

		assertEquals(13, EquipmentReference.ARMOR.size)
		assertEquals(expectedArmorNames, EquipmentReference.ARMOR.map { it.name }.toSet())

		val leatherArmor = EquipmentReference.ARMOR.first { it.name == "Leather Armor" }
		assertEquals("Light (1 minute to don or doff)", leatherArmor.categoryDonDoff)
		assertEquals("11 + Dex modifier", leatherArmor.armorClass)
		assertEquals("10 lb.", leatherArmor.weight)

		val chainMail = EquipmentReference.ARMOR.first { it.name == "Chain Mail" }
		assertEquals("Str 13", chainMail.strength)
		assertEquals("Disadvantage", chainMail.stealth)

		val shield = EquipmentReference.ARMOR.first { it.name == "Shield" }
		assertEquals("Shield (Utilize action to don or doff)", shield.categoryDonDoff)
		assertEquals("+2", shield.armorClass)
	}

	@Test
	fun armorTable_isDerivedFromStructuredArmorList() {
		val expectedRows = EquipmentReference.ARMOR.map { armor ->
			listOf(
				armor.name,
				armor.categoryDonDoff,
				armor.armorClass,
				armor.strength,
				armor.stealth,
				armor.weight,
				armor.cost
			)
		}

		assertEquals(expectedRows, EquipmentReference.ARMOR_TABLE.rows)
		assertTrue(
			EquipmentReference.ARMOR_TABLE.rows.contains(
				listOf(
					"Leather Armor",
					"Light (1 minute to don or doff)",
					"11 + Dex modifier",
					"—",
					"—",
					"10 lb.",
					"10 GP"
				)
			)
		)
		assertTrue(
			EquipmentReference.ARMOR_TABLE.rows.contains(
				listOf(
					"Shield",
					"Shield (Utilize action to don or doff)",
					"+2",
					"—",
					"—",
					"6 lb.",
					"10 GP"
				)
			)
		)
	}

	@Test
	fun adventuringGear_includesStructuredEntriesAndDetailLookups() {
		assertEquals(81, EquipmentReference.ADVENTURING_GEAR.size)

		val backpack = EquipmentReference.ADVENTURING_GEAR.first { it.name == "Backpack" }
		assertEquals("5 lb.", backpack.weight)
		assertEquals("2 GP", backpack.cost)
		assertTrue(backpack.body.orEmpty().contains("saddlebag"))

		val ammunition = EquipmentReference.ADVENTURING_GEAR.first { it.name == "Ammunition" }
		assertEquals("Varies", ammunition.weight)
		assertTrue(ammunition.body.orEmpty().contains("weapon that has the Ammunition property"))

		val spellScroll = EquipmentReference.ADVENTURING_GEAR.first { it.name == "Spell Scroll" }
		assertTrue(spellScroll.body.orEmpty().contains("magic item"))
		assertTrue(spellScroll.body.orEmpty().contains("written language"))

		assertFalse(EquipmentReference.ADVENTURING_GEAR.any { it.name == "Spell Scroll (Cantrip)" })
		assertFalse(EquipmentReference.ADVENTURING_GEAR.any { it.name == "Spell Scroll (Level 1)" })
	}

	@Test
	fun adventuringGearTable_isDerivedFromStructuredGearList() {
		val expectedRows = EquipmentReference.ADVENTURING_GEAR.map { gear ->
			listOf(gear.name, gear.weight, gear.cost)
		}

		assertEquals(expectedRows, EquipmentReference.ADVENTURING_GEAR_TABLE.rows)
		assertTrue(
			EquipmentReference.ADVENTURING_GEAR_TABLE.rows.contains(
				listOf(
					"Backpack",
					"5 lb.",
					"2 GP"
				)
			)
		)
		assertTrue(
			EquipmentReference.ADVENTURING_GEAR_TABLE.rows.contains(
				listOf(
					"Spell Scroll",
					"—",
					"Varies"
				)
			)
		)
	}

	@Test
	fun ammunition_includesStructuredEntriesAndDerivedTable() {
		assertEquals(5, EquipmentReference.AMMUNITION.size)

		val arrows = EquipmentReference.AMMUNITION.first { it.type == "Arrows" }
		assertEquals("20", arrows.amount)
		assertEquals("Quiver", arrows.storage)
		assertEquals("1 GP", arrows.cost)

		val firearmBullets = EquipmentReference.AMMUNITION.first { it.type == "Bullets, Firearm" }
		assertEquals("Pouch", firearmBullets.storage)
		assertEquals("2 lb.", firearmBullets.weight)

		val expectedRows = EquipmentReference.AMMUNITION.map { ammunition ->
			listOf(
				ammunition.type,
				ammunition.amount,
				ammunition.storage,
				ammunition.weight,
				ammunition.cost
			)
		}

		assertEquals(expectedRows, EquipmentReference.AMMUNITION_TABLE.rows)
		assertTrue(
			EquipmentReference.AMMUNITION_TABLE.rows.contains(
				listOf(
					"Needles",
					"50",
					"Pouch",
					"1 lb.",
					"1 GP"
				)
			)
		)
	}

	@Test
	fun focuses_includeStructuredEntriesAndDerivedTables() {
		assertEquals(11, EquipmentReference.FOCUSES.size)

		val arcaneStaff = EquipmentReference.FOCUSES.first { it.name == "Staff" }
		assertEquals(FocusGroup.ARCANE, arcaneStaff.group)
		assertTrue(arcaneStaff.usage.contains("Sorcerer, Warlock, or Wizard"))
		assertTrue(arcaneStaff.notes.contains("Also functions as a Quarterstaff."))

		val druidicSprig = EquipmentReference.FOCUSES.first { it.name == "Sprig of mistletoe" }
		assertEquals(FocusGroup.DRUIDIC, druidicSprig.group)
		assertEquals("1 GP", druidicSprig.cost)

		val emblem = EquipmentReference.FOCUSES.first { it.name == "Emblem" }
		assertEquals(FocusGroup.HOLY_SYMBOL, emblem.group)
		assertTrue(emblem.usage.contains("Cleric or Paladin"))
		assertTrue(emblem.notes.contains("Can be borne on fabric or a Shield."))

		val expectedArcaneRows = EquipmentReference.FOCUSES.filter { it.group == FocusGroup.ARCANE }
			.map { listOf(it.name, it.weight, it.cost) }
		val expectedDruidicRows =
			EquipmentReference.FOCUSES.filter { it.group == FocusGroup.DRUIDIC }
				.map { listOf(it.name, it.weight, it.cost) }
		val expectedHolyRows =
			EquipmentReference.FOCUSES.filter { it.group == FocusGroup.HOLY_SYMBOL }
				.map { listOf(it.name, it.weight, it.cost) }

		assertEquals(expectedArcaneRows, EquipmentReference.ARCANE_FOCUSES_TABLE.rows)
		assertEquals(expectedDruidicRows, EquipmentReference.DRUIDIC_FOCUSES_TABLE.rows)
		assertEquals(expectedHolyRows, EquipmentReference.HOLY_SYMBOLS_TABLE.rows)
	}

	@Test
	fun mountsAndVehicles_includeStructuredEntriesAndDerivedTables() {
		assertEquals(7, EquipmentReference.MOUNTS.size)
		assertEquals(10, EquipmentReference.TACK_AND_DRAWN_ITEMS.size)
		assertEquals(7, EquipmentReference.LARGE_VEHICLES.size)
		assertEquals(
			setOf("Camel", "Elephant", "Horse", "Mastiff", "Mule", "Pony", "Warhorse"),
			EquipmentReference.MOUNTS.map { it.item }.toSet()
		)
		assertEquals(
			setOf(
				"Carriage",
				"Cart",
				"Chariot",
				"Feed",
				"Saddle, Exotic",
				"Saddle, Military",
				"Saddle, Riding",
				"Sled",
				"Stabling",
				"Wagon"
			),
			EquipmentReference.TACK_AND_DRAWN_ITEMS.map { it.item }.toSet()
		)
		assertEquals(
			setOf(
				"Airship",
				"Galley",
				"Keelboat",
				"Longship",
				"Rowboat",
				"Sailing Ship",
				"Warship"
			),
			EquipmentReference.LARGE_VEHICLES.map { it.ship }.toSet()
		)

		val warhorse = EquipmentReference.MOUNTS.first { it.item == "Warhorse" }
		assertEquals("540 lb.", warhorse.carryingCapacity)
		assertEquals("400 GP", warhorse.cost)

		val carriage = EquipmentReference.TACK_AND_DRAWN_ITEMS.first { it.item == "Carriage" }
		assertEquals("600 lb.", carriage.weight)
		assertEquals("100 GP", carriage.cost)

		val airship = EquipmentReference.LARGE_VEHICLES.first { it.ship == "Airship" }
		assertEquals("8 mph", airship.speed)
		assertEquals("10", airship.crew)
		assertEquals("40,000 GP", airship.cost)

		val expectedMountRows =
			EquipmentReference.MOUNTS.map { listOf(it.item, it.carryingCapacity, it.cost) }
		val expectedTackRows =
			EquipmentReference.TACK_AND_DRAWN_ITEMS.map { listOf(it.item, it.weight, it.cost) }
		val expectedLargeVehicleRows = EquipmentReference.LARGE_VEHICLES.map {
			listOf(
				it.ship,
				it.speed,
				it.crew,
				it.passengers,
				it.cargoTons,
				it.ac,
				it.hp,
				it.damageThreshold,
				it.cost
			)
		}

		assertEquals(expectedMountRows, EquipmentReference.MOUNTS_AND_OTHER_ANIMALS_TABLE.rows)
		assertEquals(expectedTackRows, EquipmentReference.TACK_AND_DRAWN_VEHICLES_TABLE.rows)
		assertEquals(
			expectedLargeVehicleRows,
			EquipmentReference.AIRBORNE_AND_WATERBORNE_VEHICLES_TABLE.rows
		)
		assertTrue(EquipmentReference.ALL_TABLES.contains(EquipmentReference.MOUNTS_AND_OTHER_ANIMALS_TABLE))
		assertTrue(EquipmentReference.ALL_TABLES.contains(EquipmentReference.TACK_AND_DRAWN_VEHICLES_TABLE))
		assertTrue(EquipmentReference.ALL_TABLES.contains(EquipmentReference.AIRBORNE_AND_WATERBORNE_VEHICLES_TABLE))
	}

	@Test
	fun lifestyleAndServiceTables_remainSurfacedInEquipmentReference() {
		assertEquals(
			listOf(
				listOf("Wretched", "—"),
				listOf("Squalid", "1 SP"),
				listOf("Poor", "2 SP"),
				listOf("Modest", "1 GP"),
				listOf("Comfortable", "2 GP"),
				listOf("Wealthy", "4 GP"),
				listOf("Aristocratic", "10 GP+")
			),
			EquipmentReference.LIFESTYLES_TABLE.rows
		)
		assertEquals(
			listOf(
				listOf("Ale", "4 CP"),
				listOf("Bread", "2 CP"),
				listOf("Cheese", "1 SP"),
				listOf("Inn Stay", "Varies"),
				listOf("Meal", "3 CP"),
				listOf("Wine, Common", "2 SP"),
				listOf("Wine, Fine", "10 GP")
			),
			EquipmentReference.FOOD_DRINK_AND_LODGING_TABLE.rows
		)
		assertEquals(
			listOf(
				listOf("Skilled", "2 GP per day"),
				listOf("Untrained", "2 SP per day")
			),
			EquipmentReference.HIRELINGS_TABLE.rows
		)

		assertTrue(EquipmentReference.ALL_TABLES.contains(EquipmentReference.LIFESTYLES_TABLE))
		assertTrue(EquipmentReference.ALL_TABLES.contains(EquipmentReference.FOOD_DRINK_AND_LODGING_TABLE))
		assertTrue(EquipmentReference.ALL_TABLES.contains(EquipmentReference.HIRELINGS_TABLE))
	}

	@Test
	fun magicItemTables_areStructuredAndDerived() {
		assertEquals(
			listOf(
				listOf("Armor", "+1 Leather Armor, +1 Shield"),
				listOf("Potions", "Potion of Healing"),
				listOf("Rings", "Ring of Invisibility"),
				listOf("Rods", "Immovable Rod"),
				listOf("Scrolls", "Spell Scroll"),
				listOf("Staffs", "Staff of Striking"),
				listOf("Wands", "Wand of Fireballs"),
				listOf("Weapons", "+1 Ammunition, +1 Longsword"),
				listOf("Wondrous Items", "Bag of Holding, Boots of Elvenkind")
			),
			EquipmentReference.MAGIC_ITEM_CATEGORIES_TABLE.rows
		)

		assertTrue(
			EquipmentReference.POTION_MISCIBILITY_TABLE.rows.contains(
				listOf(
					"01",
					"Both potions lose their effects, and the mixture creates a magical explosion in a 5-foot-radius Sphere centered on itself. Each creature in that area takes 4d10 Force damage."
				)
			)
		)
		assertTrue(
			EquipmentReference.MAGIC_ITEM_RARITIES_AND_VALUES_TABLE.rows.contains(
				listOf(
					"Artifact",
					"Priceless"
				)
			)
		)
		assertTrue(
			EquipmentReference.MAGIC_ITEM_TOOLS_TABLE.rows.contains(
				listOf(
					"Scroll",
					"Calligrapher’s Supplies"
				)
			)
		)
		assertTrue(
			EquipmentReference.MAGIC_ITEM_CRAFTING_TIME_AND_COST_TABLE.rows.contains(
				listOf(
					"Legendary",
					"250 days",
					"100,000 GP"
				)
			)
		)
		assertTrue(
			EquipmentReference.SENTIENT_ITEM_ALIGNMENT_TABLE.rows.contains(
				listOf(
					"97–00",
					"Chaotic Evil"
				)
			)
		)
		assertTrue(
			EquipmentReference.SENTIENT_ITEM_COMMUNICATION_TABLE.rows.contains(
				listOf(
					"10",
					"The item speaks one or more languages and can communicate telepathically with any creature that carries or wields it."
				)
			)
		)
		assertTrue(
			EquipmentReference.SENTIENT_ITEM_SENSES_TABLE.rows.contains(
				listOf(
					"4",
					"Hearing and Darkvision out to 120 feet"
				)
			)
		)
		assertTrue(
			EquipmentReference.SENTIENT_ITEM_SPECIAL_PURPOSE_TABLE.rows.contains(
				listOf(
					"9",
					"Soulmate Seeker. The item seeks another sentient magic item, perhaps one similar to itself."
				)
			)
		)

		assertTrue(EquipmentReference.ALL_TABLES.contains(EquipmentReference.MAGIC_ITEM_CATEGORIES_TABLE))
		assertTrue(EquipmentReference.ALL_TABLES.contains(EquipmentReference.POTION_MISCIBILITY_TABLE))
		assertTrue(EquipmentReference.ALL_TABLES.contains(EquipmentReference.MAGIC_ITEM_RARITIES_AND_VALUES_TABLE))
		assertTrue(EquipmentReference.ALL_TABLES.contains(EquipmentReference.MAGIC_ITEM_TOOLS_TABLE))
		assertTrue(EquipmentReference.ALL_TABLES.contains(EquipmentReference.MAGIC_ITEM_CRAFTING_TIME_AND_COST_TABLE))
		assertTrue(EquipmentReference.ALL_TABLES.contains(EquipmentReference.SENTIENT_ITEM_ALIGNMENT_TABLE))
		assertTrue(EquipmentReference.ALL_TABLES.contains(EquipmentReference.SENTIENT_ITEM_COMMUNICATION_TABLE))
		assertTrue(EquipmentReference.ALL_TABLES.contains(EquipmentReference.SENTIENT_ITEM_SENSES_TABLE))
		assertTrue(EquipmentReference.ALL_TABLES.contains(EquipmentReference.SENTIENT_ITEM_SPECIAL_PURPOSE_TABLE))
	}

	@Test
	fun magicItemsAtoZ_includeProvidedExcerptEntriesAndEmbeddedTables() {
		assertEquals(252, EquipmentReference.MAGIC_ITEMS_A_TO_Z.size)

		val adamantineArmor =
			EquipmentReference.MAGIC_ITEMS_A_TO_Z.first { it.name == "Adamantine Armor" }
		assertEquals(
			"Armor (Any Medium or Heavy, Except Hide Armor), Uncommon",
			adamantineArmor.subtitle
		)
		assertTrue(adamantineArmor.body.contains("Critical Hit against you becomes a normal hit"))

		val apparatus =
			EquipmentReference.MAGIC_ITEMS_A_TO_Z.first { it.name == "Apparatus of the Crab" }
		assertTrue(apparatus.body.contains("AC 20"))
		assertEquals(listOf("Apparatus of the Crab Levers"), apparatus.tables.map { it.title })

		val bagOfTricks = EquipmentReference.MAGIC_ITEMS_A_TO_Z.first { it.name == "Bag of Tricks" }
		assertEquals(
			listOf("Gray Bag of Tricks", "Rust Bag of Tricks", "Tan Bag of Tricks"),
			bagOfTricks.tables.map { it.title }
		)

		val cubeOfForce = EquipmentReference.MAGIC_ITEMS_A_TO_Z.first { it.name == "Cube of Force" }
		assertTrue(cubeOfForce.tables.single().rows.contains(listOf("Wall of Force", "5")))

		val dimensionalShackles =
			EquipmentReference.MAGIC_ITEMS_A_TO_Z.first { it.name == "Dimensional Shackles" }
		assertTrue(dimensionalShackles.body.contains("DC 30 Strength (Athletics) check"))

		val dragonOrb = EquipmentReference.MAGIC_ITEMS_A_TO_Z.first { it.name == "Dragon Orb" }
		assertEquals("Wondrous Item, Artifact (Requires Attunement)", dragonOrb.subtitle)
		assertEquals(listOf("Dragon Orb Spells"), dragonOrb.tables.map { it.title })

		val elementalGem =
			EquipmentReference.MAGIC_ITEMS_A_TO_Z.first { it.name == "Elemental Gem" }
		assertTrue(
			elementalGem.tables.single().rows.contains(
				listOf(
					"Red corundum",
					"Fire Elemental"
				)
			)
		)

		val iounStone = EquipmentReference.MAGIC_ITEMS_A_TO_Z.first { it.name == "Ioun Stone" }
		assertTrue(iounStone.tables.single().rows.any {
			it.first() == "Mastery" && it.last().contains("Proficiency Bonus")
		})

		val necklaceOfPrayerBeads =
			EquipmentReference.MAGIC_ITEMS_A_TO_Z.first { it.name == "Necklace of Prayer Beads" }
		assertTrue(
			necklaceOfPrayerBeads.tables.single().rows.contains(
				listOf(
					"20",
					"Bead of Wind Walking",
					"Wind Walk"
				)
			)
		)

		val potionsOfHealing =
			EquipmentReference.MAGIC_ITEMS_A_TO_Z.first { it.name == "Potions of Healing" }
		assertTrue(
			potionsOfHealing.tables.single().rows.contains(
				listOf(
					"Supreme Potion of Healing",
					"10d4 + 20",
					"Very Rare"
				)
			)
		)

		val ringOfResistance =
			EquipmentReference.MAGIC_ITEMS_A_TO_Z.first { it.name == "Ring of Resistance" }
		assertTrue(
			ringOfResistance.tables.single().rows.contains(
				listOf(
					"10",
					"Thunder",
					"Spinel"
				)
			)
		)

		val spellScroll = EquipmentReference.MAGIC_ITEMS_A_TO_Z.first { it.name == "Spell Scroll" }
		assertTrue(spellScroll.tables.single().rows.contains(listOf("9", "Legendary", "19", "+11")))

		val wandOfWonder =
			EquipmentReference.MAGIC_ITEMS_A_TO_Z.first { it.name == "Wand of Wonder" }
		assertTrue(wandOfWonder.tables.single().rows.any {
			it.first() == "98–00" && it.last().contains("Petrified")
		})
	}

	@Test
	fun toolTables_areDerivedFromStructuredToolLists() {
		val expectedArtisanRows = EquipmentReference.ARTISANS_TOOLS.map { tool ->
			listOf(
				tool.name.replace("'", "’"),
				tool.ability,
				tool.weight,
				tool.cost,
				tool.utilize.joinToString(", or "),
				tool.craft.joinToString().ifBlank { "—" }
			)
		}
		val expectedOtherRows = EquipmentReference.OTHER_TOOLS.map { tool ->
			listOf(
				tool.name.replace("'", "’"),
				tool.ability,
				tool.weight,
				if (tool.variants.isNotEmpty()) tool.variants.joinToString() else tool.cost,
				tool.utilize.joinToString(", or "),
				when {
					tool.craft.isNotEmpty() -> tool.craft.joinToString()
					tool.notes.isNotEmpty() -> tool.notes.joinToString()
					else -> "—"
				}
			)
		}

		assertEquals(expectedArtisanRows, EquipmentReference.ARTISANS_TOOLS_TABLE.rows)
		assertEquals(expectedOtherRows, EquipmentReference.OTHER_TOOLS_TABLE.rows)
		assertTrue(
			EquipmentReference.ARTISANS_TOOLS_TABLE.rows.contains(
				listOf(
					"Calligrapher’s Supplies",
					"Dexterity",
					"5 lb.",
					"10 GP",
					"Write text with impressive flourishes that guard against forgery (DC 15)",
					"Ink"
				)
			)
		)
		assertTrue(
			EquipmentReference.OTHER_TOOLS_TABLE.rows.any {
				it.first() == "Gaming Set" &&
					it.last().contains("Variants require separate proficiency") &&
					it.last().contains("Three-Dragon Ante")
			}
		)
		assertFalse(EquipmentReference.FOCUSES.any { it.name.contains("(") })
	}
}
