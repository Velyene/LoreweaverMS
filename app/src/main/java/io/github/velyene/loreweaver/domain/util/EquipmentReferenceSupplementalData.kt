@file:Suppress("kotlin:S1192")

/*
 * FILE: EquipmentReferenceSupplementalData.kt
 *
 * TABLE OF CONTENTS:
 * 1. Ammunition and ammunition table data
 * 2. Focus entries and focus tables
 * 3. Mount, tack, and vehicle reference data
 * 4. Lifestyle, food, lodging, and hireling tables
 */

package io.github.velyene.loreweaver.domain.util

internal object EquipmentReferenceSupplementalData {
	val AMMUNITION = listOf(
		AmmunitionReferenceEntry("Arrows", "20", "Quiver", "1 lb.", "1 GP"),
		AmmunitionReferenceEntry("Bolts", "20", "Case", "1Â½ lb.", "1 GP"),
		AmmunitionReferenceEntry("Bullets, Firearm", "10", "Pouch", "2 lb.", "3 GP"),
		AmmunitionReferenceEntry("Bullets, Sling", "20", "Pouch", "1Â½ lb.", "4 CP"),
		AmmunitionReferenceEntry("Needles", "50", "Pouch", "1 lb.", "1 GP")
	)

	val AMMUNITION_TABLE = ReferenceTable(
		title = "Ammunition",
		columns = listOf("Type", "Amount", "Storage", "Weight", "Cost"),
		rows = AMMUNITION.map { listOf(it.type, it.amount, it.storage, it.weight, it.cost) }
	)

	val FOCUSES = listOf(
		FocusReferenceEntry(FocusGroup.ARCANE, "Crystal", "1 lb.", "10 GP", "A Sorcerer, Warlock, or Wizard can use this as an Arcane Focus."),
		FocusReferenceEntry(FocusGroup.ARCANE, "Orb", "3 lb.", "20 GP", "A Sorcerer, Warlock, or Wizard can use this as an Arcane Focus."),
		FocusReferenceEntry(FocusGroup.ARCANE, "Rod", "2 lb.", "10 GP", "A Sorcerer, Warlock, or Wizard can use this as an Arcane Focus."),
		FocusReferenceEntry(FocusGroup.ARCANE, "Staff", "4 lb.", "5 GP", "A Sorcerer, Warlock, or Wizard can use this as an Arcane Focus.", listOf("Also functions as a Quarterstaff.")),
		FocusReferenceEntry(FocusGroup.ARCANE, "Wand", "1 lb.", "10 GP", "A Sorcerer, Warlock, or Wizard can use this as an Arcane Focus."),
		FocusReferenceEntry(FocusGroup.DRUIDIC, "Sprig of mistletoe", "â€”", "1 GP", "A Druid or Ranger can use this as a Druidic Focus."),
		FocusReferenceEntry(FocusGroup.DRUIDIC, "Wooden Staff", "4 lb.", "5 GP", "A Druid or Ranger can use this as a Druidic Focus.", listOf("Also functions as a Quarterstaff.")),
		FocusReferenceEntry(FocusGroup.DRUIDIC, "Yew wand", "1 lb.", "10 GP", "A Druid or Ranger can use this as a Druidic Focus."),
		FocusReferenceEntry(FocusGroup.HOLY_SYMBOL, "Amulet", "1 lb.", "5 GP", "A Cleric or Paladin can use this as a Holy Symbol.", listOf("Can be displayed on or alongside a Shield.")),
		FocusReferenceEntry(FocusGroup.HOLY_SYMBOL, "Emblem", "â€”", "5 GP", "A Cleric or Paladin can use this as a Holy Symbol.", listOf("Can be borne on fabric or a Shield.")),
		FocusReferenceEntry(FocusGroup.HOLY_SYMBOL, "Reliquary", "2 lb.", "5 GP", "A Cleric or Paladin can use this as a Holy Symbol.", listOf("Often carried openly with a Shield or worn prominently.")),
	)

	val ARCANE_FOCUSES_TABLE = ReferenceTable(
		title = "Arcane Focuses",
		columns = listOf("Name", "Weight", "Cost"),
		rows = FOCUSES.filter { it.group == FocusGroup.ARCANE }.map { listOf(it.name, it.weight, it.cost) }
	)

	val DRUIDIC_FOCUSES_TABLE = ReferenceTable(
		title = "Druidic Focuses",
		columns = listOf("Name", "Weight", "Cost"),
		rows = FOCUSES.filter { it.group == FocusGroup.DRUIDIC }.map { listOf(it.name, it.weight, it.cost) }
	)

	val HOLY_SYMBOLS_TABLE = ReferenceTable(
		title = "Holy Symbols",
		columns = listOf("Name", "Weight", "Cost"),
		rows = FOCUSES.filter { it.group == FocusGroup.HOLY_SYMBOL }.map { listOf(it.name, it.weight, it.cost) }
	)

	val MOUNTS = listOf(
		MountReferenceEntry("Camel", "480 lb.", "50 GP"),
		MountReferenceEntry("Elephant", "1,320 lb.", "200 GP"),
		MountReferenceEntry("Horse", "540 lb.", "50 GP"),
		MountReferenceEntry("Mastiff", "195 lb.", "25 GP"),
		MountReferenceEntry("Mule", "420 lb.", "8 GP"),
		MountReferenceEntry("Pony", "225 lb.", "30 GP"),
		MountReferenceEntry("Warhorse", "540 lb.", "400 GP")
	)

	val MOUNTS_AND_OTHER_ANIMALS_TABLE = ReferenceTable(
		title = "Mounts and Other Animals",
		columns = listOf("Animal", "Carrying Capacity", "Cost"),
		rows = MOUNTS.map { listOf(it.item, it.carryingCapacity, it.cost) }
	)

	val TACK_AND_DRAWN_ITEMS = listOf(
		TackDrawnReferenceEntry("Carriage", "600 lb.", "100 GP"),
		TackDrawnReferenceEntry("Cart", "200 lb.", "15 GP"),
		TackDrawnReferenceEntry("Chariot", "100 lb.", "250 GP"),
		TackDrawnReferenceEntry("Feed", "10 lb.", "5 CP per day"),
		TackDrawnReferenceEntry("Saddle, Exotic", "40 lb.", "60 GP"),
		TackDrawnReferenceEntry("Saddle, Military", "30 lb.", "20 GP"),
		TackDrawnReferenceEntry("Saddle, Riding", "25 lb.", "10 GP"),
		TackDrawnReferenceEntry("Sled", "300 lb.", "20 GP"),
		TackDrawnReferenceEntry("Stabling", "â€”", "5 SP per day"),
		TackDrawnReferenceEntry("Wagon", "400 lb.", "35 GP")
	)

	val TACK_AND_DRAWN_VEHICLES_TABLE = ReferenceTable(
		title = "Tack and Drawn Vehicles",
		columns = listOf("Item", "Weight", "Cost"),
		rows = TACK_AND_DRAWN_ITEMS.map { listOf(it.item, it.weight, it.cost) }
	)

	val LARGE_VEHICLES = listOf(
		LargeVehicleReferenceEntry("Airship", "8 mph", "10", "20", "1", "13", "300", "â€”", "40,000 GP"),
		LargeVehicleReferenceEntry("Galley", "4 mph", "80", "120", "150", "15", "500", "20", "30,000 GP"),
		LargeVehicleReferenceEntry("Keelboat", "1 mph", "1", "6", "0.5", "15", "100", "â€”", "3,000 GP"),
		LargeVehicleReferenceEntry("Longship", "3 mph", "40", "100", "10", "15", "300", "15", "10,000 GP"),
		LargeVehicleReferenceEntry("Rowboat", "1Â½ mph", "1", "3", "â€”", "11", "50", "â€”", "50 GP"),
		LargeVehicleReferenceEntry("Sailing Ship", "2 mph", "20", "20", "100", "15", "300", "15", "10,000 GP"),
		LargeVehicleReferenceEntry("Warship", "2Â½ mph", "60", "60", "200", "15", "500", "20", "25,000 GP")
	)

	val AIRBORNE_AND_WATERBORNE_VEHICLES_TABLE = ReferenceTable(
		title = "Airborne and Waterborne Vehicles",
		columns = listOf("Ship", "Speed", "Crew", "Passengers", "Cargo Tons", "AC", "HP", "Damage Threshold", "Cost"),
		rows = LARGE_VEHICLES.map { listOf(it.ship, it.speed, it.crew, it.passengers, it.cargoTons, it.ac, it.hp, it.damageThreshold, it.cost) }
	)

	val LIFESTYLES_TABLE = ReferenceTable(
		title = "Lifestyle Expenses",
		columns = listOf("Lifestyle", "Daily Cost"),
		rows = listOf(
			listOf("Wretched", "â€”"),
			listOf("Squalid", "1 SP"),
			listOf("Poor", "2 SP"),
			listOf("Modest", "1 GP"),
			listOf("Comfortable", "2 GP"),
			listOf("Wealthy", "4 GP"),
			listOf("Aristocratic", "10 GP+")
		)
	)

	val FOOD_DRINK_AND_LODGING_TABLE = ReferenceTable(
		title = "Food, Drink, and Lodging",
		columns = listOf("Item", "Cost"),
		rows = listOf(
			listOf("Ale", "4 CP"),
			listOf("Bread", "2 CP"),
			listOf("Cheese", "1 SP"),
			listOf("Inn Stay", "Varies"),
			listOf("Meal", "3 CP"),
			listOf("Wine, Common", "2 SP"),
			listOf("Wine, Fine", "10 GP")
		)
	)

	val HIRELINGS_TABLE = ReferenceTable(
		title = "Hirelings",
		columns = listOf("Type", "Pay"),
		rows = listOf(
			listOf("Skilled", "2 GP per day"),
			listOf("Untrained", "2 SP per day")
		)
	)

	val MAGIC_ITEM_CATEGORIES_TABLE = ReferenceTable(
		title = "Magic Item Categories",
		columns = listOf("Category", "Examples"),
		rows = listOf(
			listOf("Armor", "+1 Leather Armor, +1 Shield"),
			listOf("Potions", "Potion of Healing"),
			listOf("Rings", "Ring of Invisibility"),
			listOf("Rods", "Immovable Rod"),
			listOf("Scrolls", "Spell Scroll"),
			listOf("Staffs", "Staff of Striking"),
			listOf("Wands", "Wand of Fireballs"),
			listOf("Weapons", "+1 Ammunition, +1 Longsword"),
			listOf("Wondrous Items", "Bag of Holding, Boots of Elvenkind")
		)
	)

	val POTION_MISCIBILITY_TABLE = ReferenceTable(
		title = "Potion Miscibility",
		columns = listOf("d100", "Result"),
		rows = listOf(
			listOf("01", "Both potions lose their effects, and the mixture creates a magical explosion in a 5-foot-radius Sphere centered on itself. Each creature in that area takes 4d10 Force damage."),
			listOf("02â€“08", "Both potions lose their effects."),
			listOf("09â€“15", "Only one potion takes effect."),
			listOf("16â€“25", "Both potions take effect."),
			listOf("26â€“35", "One potionâ€™s effect is doubled."),
			listOf("36â€“90", "The mixture works normally."),
			listOf("91â€“99", "The mixture creates harmless but dramatic magical bubbles."),
			listOf("00", "The mixture becomes a permanent magical poison.")
		)
	)

	val MAGIC_ITEM_RARITIES_AND_VALUES_TABLE = ReferenceTable(
		title = "Magic Item Rarities and Values",
		columns = listOf("Rarity", "Value"),
		rows = listOf(
			listOf("Common", "50â€“100 GP"),
			listOf("Uncommon", "101â€“500 GP"),
			listOf("Rare", "501â€“5,000 GP"),
			listOf("Very Rare", "5,001â€“50,000 GP"),
			listOf("Legendary", "50,001+ GP"),
			listOf("Artifact", "Priceless")
		)
	)

	val MAGIC_ITEM_TOOLS_TABLE = ReferenceTable(
		title = "Magic Item Tools",
		columns = listOf("Item Type", "Tool"),
		rows = listOf(
			listOf("Armor", "Smithâ€™s Tools"),
			listOf("Potion", "Alchemistâ€™s Supplies or Herbalism Kit"),
			listOf("Ring", "Jewelerâ€™s Tools"),
			listOf("Rod", "Smithâ€™s Tools"),
			listOf("Scroll", "Calligrapherâ€™s Supplies"),
			listOf("Staff", "Woodcarverâ€™s Tools"),
			listOf("Wand", "Woodcarverâ€™s Tools"),
			listOf("Weapon", "Smithâ€™s Tools"),
			listOf("Wondrous Item", "Tinkerâ€™s Tools with a suitable nonmagical base item")
		)
	)

	val MAGIC_ITEM_CRAFTING_TIME_AND_COST_TABLE = ReferenceTable(
		title = "Magic Item Crafting Time and Cost",
		columns = listOf("Rarity", "Time", "Cost"),
		rows = listOf(
			listOf("Common", "5 days", "100 GP"),
			listOf("Uncommon", "20 days", "400 GP"),
			listOf("Rare", "60 days", "10,000 GP"),
			listOf("Very Rare", "120 days", "50,000 GP"),
			listOf("Legendary", "250 days", "100,000 GP")
		)
	)

	val SENTIENT_ITEM_ALIGNMENT_TABLE = ReferenceTable(
		title = "Sentient Itemâ€™s Alignment",
		columns = listOf("d100", "Alignment"),
		rows = listOf(
			listOf("01â€“15", "Lawful Good"),
			listOf("16â€“35", "Neutral Good"),
			listOf("36â€“50", "Chaotic Good"),
			listOf("51â€“63", "Lawful Neutral"),
			listOf("64â€“73", "Neutral"),
			listOf("74â€“85", "Chaotic Neutral"),
			listOf("86â€“89", "Lawful Evil"),
			listOf("90â€“96", "Neutral Evil"),
			listOf("97â€“00", "Chaotic Evil")
		)
	)

	val SENTIENT_ITEM_COMMUNICATION_TABLE = ReferenceTable(
		title = "Sentient Itemâ€™s Communication",
		columns = listOf("d10", "Communication"),
		rows = listOf(
			listOf("1", "The item communicates by transmitting emotion to the creature carrying or wielding it."),
			listOf("2â€“4", "The item can speak, read, and understand one or more languages."),
			listOf("5â€“7", "The item can speak telepathically with its wielder or bearer."),
			listOf("8â€“9", "The item can speak, read, and understand languages and communicate telepathically with its wielder or bearer."),
			listOf("10", "The item speaks one or more languages and can communicate telepathically with any creature that carries or wields it.")
		)
	)

	val SENTIENT_ITEM_SENSES_TABLE = ReferenceTable(
		title = "Sentient Itemâ€™s Senses",
		columns = listOf("d4", "Senses"),
		rows = listOf(
			listOf("1", "Hearing and normal vision out to 30 feet"),
			listOf("2", "Hearing and normal vision out to 60 feet"),
			listOf("3", "Hearing and Darkvision out to 30 feet"),
			listOf("4", "Hearing and Darkvision out to 120 feet")
		)
	)

	val SENTIENT_ITEM_SPECIAL_PURPOSE_TABLE = ReferenceTable(
		title = "Sentient Itemâ€™s Special Purpose",
		columns = listOf("d10", "Purpose"),
		rows = listOf(
			listOf("1", "Aligned. The item seeks to further the goals of creatures of a particular alignment."),
			listOf("2", "Bane. The item seeks the downfall of creatures of a particular kind."),
			listOf("3", "Creator Seeker. The item seeks its creator or the creatorâ€™s descendants."),
			listOf("4", "Destiny Seeker. The item is convinced it and its bearer have key roles in future events."),
			listOf("5", "Destroyer. The item craves destruction and goads its user to fight arbitrarily."),
			listOf("6", "Glory Seeker. The item seeks renown as the greatest magic item in the world by winning fame or notoriety for its user."),
			listOf("7", "Lore Seeker. The item craves knowledge or is determined to solve a mystery, learn a secret, or unravel a cryptic prophecy."),
			listOf("8", "Protector. The item seeks to defend a particular kind of creature."),
			listOf("9", "Soulmate Seeker. The item seeks another sentient magic item, perhaps one similar to itself."),
			listOf("10", "Templar. The item seeks to defend the servants and interests of a particular deity.")
		)
	)
}
