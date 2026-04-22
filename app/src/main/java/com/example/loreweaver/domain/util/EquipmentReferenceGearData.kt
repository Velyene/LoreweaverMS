@file:Suppress("kotlin:S1192")

package com.example.loreweaver.domain.util

internal object EquipmentReferenceGearData {
	private val gearBodies = linkedMapOf(
		"Acid" to "When you take the Attack action, you can replace one of your attacks with throwing a vial of Acid.",
		"Alchemist’s Fire" to "When you take the Attack action, you can replace one of your attacks with throwing a flask of Alchemist’s Fire.",
		"Ammunition" to "Ammunition is required by a weapon that has the Ammunition property. The Ammunition table lists the different types, how much you get when you buy them, and the item typically used to store each type.",
		"Antitoxin" to "As a Bonus Action, you can drink a vial of Antitoxin to gain Advantage on saving throws to avoid or end the Poisoned condition for 1 hour.",
		"Arcane Focus" to "An Arcane Focus channels arcane magic for a Sorcerer, Warlock, or Wizard.",
		"Backpack" to "A Backpack holds up to 30 pounds within 1 cubic foot. It can also serve as a saddlebag.",
		"Bedroll" to "A Bedroll sleeps one Small or Medium creature and helps protect against extreme cold.",
		"Healer’s Kit" to "A Healer’s Kit contains bandages, salves, and splints. As a Utilize action, you can expend one use to stabilize an Unconscious creature.",
		"Holy Symbol" to "A Holy Symbol can serve as a Cleric or Paladin spellcasting focus.",
		"Potion of Healing" to "This magic item restores hit points when drunk. If potions are mixed together, consult the Potion Miscibility table.",
		"Spell Scroll" to "A Spell Scroll is a magic item inscribed in written language and encoded with a mystical cipher that holds stored magic."
	)

	val ADVENTURING_GEAR_DETAIL_SECTIONS = gearBodies.map { (name, body) ->
		CharacterCreationTextSection(title = name, body = body)
	}

	private val ADVENTURING_GEAR_ROWS = listOf(
		listOf("Acid", "1 lb.", "25 GP"),
		listOf("Alchemist’s Fire", "1 lb.", "50 GP"),
		listOf("Ammunition", "Varies", "Varies"),
		listOf("Antitoxin", "—", "50 GP"),
		listOf("Arcane Focus", "Varies", "Varies"),
		listOf("Backpack", "5 lb.", "2 GP"),
		listOf("Ball Bearings", "2 lb.", "1 GP"),
		listOf("Barrel", "70 lb.", "2 GP"),
		listOf("Basket", "2 lb.", "4 SP"),
		listOf("Bedroll", "7 lb.", "1 GP"),
		listOf("Bell", "—", "1 GP"),
		listOf("Blanket", "3 lb.", "5 SP"),
		listOf("Block and Tackle", "5 lb.", "1 GP"),
		listOf("Book", "5 lb.", "25 GP"),
		listOf("Bottle, Glass", "2 lb.", "2 GP"),
		listOf("Bucket", "2 lb.", "5 CP"),
		listOf("Burglar’s Pack", "42 lb.", "16 GP"),
		listOf("Caltrops", "2 lb.", "1 GP"),
		listOf("Candle", "—", "1 CP"),
		listOf("Case, Crossbow Bolt", "1 lb.", "1 GP"),
		listOf("Case, Map or Scroll", "1 lb.", "1 GP"),
		listOf("Chain", "10 lb.", "5 GP"),
		listOf("Chest", "25 lb.", "5 GP"),
		listOf("Climber’s Kit", "12 lb.", "25 GP"),
		listOf("Clothes, Fine", "6 lb.", "15 GP"),
		listOf("Clothes, Traveler’s", "4 lb.", "2 GP"),
		listOf("Component Pouch", "2 lb.", "25 GP"),
		listOf("Costume", "4 lb.", "5 GP"),
		listOf("Crowbar", "5 lb.", "2 GP"),
		listOf("Diplomat’s Pack", "39 lb.", "39 GP"),
		listOf("Druidic Focus", "Varies", "Varies"),
		listOf("Dungeoneer’s Pack", "55 lb.", "12 GP"),
		listOf("Entertainer’s Pack", "58½ lb.", "40 GP"),
		listOf("Explorer’s Pack", "55 lb.", "10 GP"),
		listOf("Flask", "1 lb.", "2 CP"),
		listOf("Grappling Hook", "4 lb.", "2 GP"),
		listOf("Healer’s Kit", "3 lb.", "5 GP"),
		listOf("Holy Symbol", "Varies", "Varies"),
		listOf("Holy Water", "1 lb.", "25 GP"),
		listOf("Hunting Trap", "25 lb.", "5 GP"),
		listOf("Ink", "—", "10 GP"),
		listOf("Ink Pen", "—", "2 CP"),
		listOf("Jug", "4 lb.", "2 CP"),
		listOf("Ladder", "25 lb.", "1 SP"),
		listOf("Lamp", "1 lb.", "5 SP"),
		listOf("Lantern, Bullseye", "2 lb.", "10 GP"),
		listOf("Lantern, Hooded", "2 lb.", "5 GP"),
		listOf("Lock", "1 lb.", "10 GP"),
		listOf("Magnifying Glass", "—", "100 GP"),
		listOf("Manacles", "6 lb.", "2 GP"),
		listOf("Map", "—", "1 GP"),
		listOf("Mirror", "1/2 lb.", "5 GP"),
		listOf("Net", "3 lb.", "1 GP"),
		listOf("Oil", "1 lb.", "1 SP"),
		listOf("Paper", "—", "2 SP"),
		listOf("Parchment", "—", "1 SP"),
		listOf("Perfume", "—", "5 GP"),
		listOf("Poison, Basic", "—", "100 GP"),
		listOf("Pole", "7 lb.", "5 CP"),
		listOf("Pot, Iron", "10 lb.", "2 GP"),
		listOf("Potion of Healing", "1/2 lb.", "50 GP"),
		listOf("Pouch", "1 lb.", "5 SP"),
		listOf("Priest’s Pack", "29 lb.", "33 GP"),
		listOf("Quiver", "1 lb.", "1 GP"),
		listOf("Ram, Portable", "35 lb.", "4 GP"),
		listOf("Rations", "2 lb.", "5 SP"),
		listOf("Robe", "4 lb.", "1 GP"),
		listOf("Rope", "5 lb.", "1 GP"),
		listOf("Sack", "1/2 lb.", "1 CP"),
		listOf("Scholar’s Pack", "22 lb.", "40 GP"),
		listOf("Shovel", "5 lb.", "2 GP"),
		listOf("Signal Whistle", "—", "5 CP"),
		listOf("Spell Scroll", "—", "Varies"),
		listOf("Spikes, Iron", "5 lb.", "1 GP"),
		listOf("Spyglass", "1 lb.", "1,000 GP"),
		listOf("String", "—", "1 SP"),
		listOf("Tent", "20 lb.", "2 GP"),
		listOf("Tinderbox", "1 lb.", "5 SP"),
		listOf("Torch", "1 lb.", "1 CP"),
		listOf("Vial", "—", "1 GP"),
		listOf("Waterskin", "5 lb. (full)", "2 SP")
	)

	val ADVENTURING_GEAR = ADVENTURING_GEAR_ROWS.map { row ->
		AdventuringGearEntry(
			name = row[0],
			weight = row[1],
			cost = row[2],
			body = gearBodies[row[0]]
		)
	}

	val ADVENTURING_GEAR_TABLE = ReferenceTable(
		title = "Adventuring Gear",
		columns = listOf("Item", "Weight", "Cost"),
		rows = ADVENTURING_GEAR.map { gear -> listOf(gear.name, gear.weight, gear.cost) }
	)
}
