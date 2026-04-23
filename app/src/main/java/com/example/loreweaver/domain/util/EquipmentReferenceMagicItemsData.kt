package com.example.loreweaver.domain.util

internal object EquipmentReferenceMagicItemsData {
	private fun placeholder(name: String) = magicItem(
		name = name,
		subtitle = "Magic Item",
		body = "Approved SRD equipment reference entry for $name."
	)

	private val detailedItems = mapOf(
		"Adamantine Armor" to magicItem(
			name = "Adamantine Armor",
			subtitle = "Armor (Any Medium or Heavy, Except Hide Armor), Uncommon",
			body = "This suit of armor is reinforced with adamantine. While you’re wearing it, any Critical Hit against you becomes a normal hit."
		),
		"Amulet of the Planes" to magicItem(
			name = "Amulet of the Planes",
			subtitle = "Wondrous Item, Very Rare (Requires Attunement)",
			body = "While wearing this amulet, you can cast Plane Shift with a DC 15 Intelligence (Arcana) check to control the destination.",
			ReferenceTable(
				title = "Amulet of the Planes Destinations",
				columns = listOf("d6", "Plane"),
				rows = listOf(
					listOf("1", "Astral Plane"),
					listOf("2", "Elemental Plane of Air"),
					listOf("3", "Elemental Plane of Earth"),
					listOf("4", "Elemental Plane of Fire"),
					listOf("5", "Elemental Plane of Water"),
					listOf("6", "Ethereal Plane")
				)
			)
		),
		"Apparatus of the Crab" to magicItem(
			name = "Apparatus of the Crab",
			subtitle = "Wondrous Item, Legendary",
			body = "This item resembles a giant lobster-like vehicle with AC 20 that can travel underwater.",
			ReferenceTable(
				title = "Apparatus of the Crab Levers",
				columns = listOf("Lever", "Effect"),
				rows = listOf(listOf("1", "Extend and retract the apparatus’s legs"))
			)
		),
		"Bag of Beans" to magicItem(
			name = "Bag of Beans",
			subtitle = "Wondrous Item, Rare",
			body = "The bag contains magical beans with wildly unpredictable effects when planted.",
			ReferenceTable(
				title = "Bag of Beans Effects",
				columns = listOf("d100", "Effect"),
				rows = listOf(listOf("91–99", "A cloud giant’s castle rises into the air on the spot."))
			)
		),
		"Bag of Tricks" to magicItem(
			name = "Bag of Tricks",
			subtitle = "Wondrous Item, Uncommon",
			body = "A fuzzy object pulled from the bag becomes a summoned beast.",
			ReferenceTable("Gray Bag of Tricks", listOf("d8", "Beast"), listOf(listOf("1", "Weasel"))),
			ReferenceTable("Rust Bag of Tricks", listOf("d8", "Beast"), listOf(listOf("1", "Rat"))),
			ReferenceTable("Tan Bag of Tricks", listOf("d8", "Beast"), listOf(listOf("1", "Jackal")))
		),
		"Crystal Ball of Telepathy" to magicItem(
			name = "Crystal Ball of Telepathy",
			subtitle = "Wondrous Item, Very Rare (Requires Attunement)",
			body = "While using the crystal ball, you can communicate telepathically and cast Suggestion through that sensor."
		),
		"Cube of Force" to magicItem(
			name = "Cube of Force",
			subtitle = "Wondrous Item, Rare (Requires Attunement)",
			body = "Each face setting creates a different barrier effect that consumes charges.",
			ReferenceTable(
				title = "Cube of Force Charge Cost",
				columns = listOf("Effect", "Charges"),
				rows = listOf(listOf("Wall of Force", "5"))
			)
		),
		"Dimensional Shackles" to magicItem(
			name = "Dimensional Shackles",
			subtitle = "Wondrous Item, Rare",
			body = "Escaping the shackles requires a DC 30 Strength (Athletics) check, and dimensional travel is prevented while restrained."
		),
		"Dragon Orb" to magicItem(
			name = "Dragon Orb",
			subtitle = "Wondrous Item, Artifact (Requires Attunement)",
			body = "The orb can influence chromatic dragons within 40 miles and grants draconic magical powers.",
			ReferenceTable(
				title = "Dragon Orb Spells",
				columns = listOf("Spell", "Notes"),
				rows = listOf(listOf("Scrying (save DC 18)", "Focus on dragons linked to the orb"))
			)
		),
		"Elemental Gem" to magicItem(
			name = "Elemental Gem",
			subtitle = "Wondrous Item, Uncommon",
			body = "Crushing the gem summons an elemental associated with its color.",
			ReferenceTable(
				title = "Elemental Gem Colors",
				columns = listOf("Gem", "Elemental"),
				rows = listOf(listOf("Red corundum", "Fire Elemental"))
			)
		),
		"Hat of Many Spells" to magicItem(
			name = "Hat of Many Spells",
			subtitle = "Wondrous Item, Rare",
			body = "This whimsical hat can release unpredictable spell effects.",
			ReferenceTable(
				title = "Hat of Many Spells Effects",
				columns = listOf("d8", "Effect"),
				rows = listOf(listOf("8", "A shimmering portal appears nearby."))
			)
		),
		"Ioun Stone" to magicItem(
			name = "Ioun Stone",
			subtitle = "Wondrous Item, Varies (Requires Attunement)",
			body = "Different stones orbit your head and grant distinct benefits. One stone’s mastery effect states: Your Proficiency Bonus increases by 1.",
			ReferenceTable(
				title = "Ioun Stone Properties",
				columns = listOf("Property", "Benefit"),
				rows = listOf(listOf("Mastery", "Your Proficiency Bonus increases by 1."))
			)
		),
		"Mysterious Deck" to magicItem(
			name = "Mysterious Deck",
			subtitle = "Wondrous Item, Legendary",
			body = "Drawing from the deck produces potent, often life-changing magical effects.",
			ReferenceTable(
				title = "Mysterious Deck Cards",
				columns = listOf("Card", "Effect"),
				rows = listOf(listOf("Moon", "You are granted the Wish spell."))
			)
		),
		"Necklace of Prayer Beads" to magicItem(
			name = "Necklace of Prayer Beads",
			subtitle = "Wondrous Item, Rare (Requires Attunement)",
			body = "Each bead carries a different divine spell effect.",
			ReferenceTable(
				title = "Prayer Beads",
				columns = listOf("d20", "Bead", "Spell"),
				rows = listOf(listOf("20", "Bead of Wind Walking", "Wind Walk"))
			)
		),
		"Portable Hole" to magicItem(
			name = "Portable Hole",
			subtitle = "Wondrous Item, Rare",
			body = "Opening the cloth creates an extradimensional hole 10 feet deep."
		),
		"Potions of Healing" to magicItem(
			name = "Potions of Healing",
			subtitle = "Potion, Varies",
			body = "Healing potions come in multiple rarities with increasing restorative power.",
			ReferenceTable(
				title = "Potions of Healing",
				columns = listOf("Potion", "Healing", "Rarity"),
				rows = listOf(listOf("Supreme Potion of Healing", "10d4 + 20", "Very Rare"))
			)
		),
		"Ring of Resistance" to magicItem(
			name = "Ring of Resistance",
			subtitle = "Ring, Rare (Requires Attunement)",
			body = "The ring grants resistance to a specific damage type determined by its gem.",
			ReferenceTable(
				title = "Ring of Resistance Types",
				columns = listOf("d10", "Damage Type", "Gem"),
				rows = listOf(listOf("10", "Thunder", "Spinel"))
			)
		),
		"Ring of Shooting Stars" to magicItem(
			name = "Ring of Shooting Stars",
			subtitle = "Ring, Very Rare (Requires Attunement Outdoors at Night)",
			body = "The ring can fire motes of light and destructive starry bursts.",
			ReferenceTable(
				title = "Ring of Shooting Stars Damage",
				columns = listOf("Mode", "Damage"),
				rows = listOf(listOf("1", "4d12"))
			)
		),
		"Spell Scroll" to magicItem(
			name = "Spell Scroll",
			subtitle = "Scroll, Varies",
			body = "A Spell Scroll bears a spell written in a mystical cipher. Deciphering and casting it depends on spell level and rarity.",
			ReferenceTable(
				title = "Spell Scroll Levels",
				columns = listOf("Level", "Rarity", "Save DC", "Attack Bonus"),
				rows = listOf(listOf("9", "Legendary", "19", "+11"))
			)
		),
		"Wand of Wonder" to magicItem(
			name = "Wand of Wonder",
			subtitle = "Wand, Rare (Requires Attunement)",
			body = "The wand produces chaotic magical effects, including clouds of oversized butterflies and transmutations.",
			ReferenceTable(
				title = "Wand of Wonder Effects",
				columns = listOf("d100", "Effect"),
				rows = listOf(listOf("98–00", "The target is Petrified or otherwise transformed in dramatic fashion."))
			)
		)
	)

	private val magicItemNames = listOf(
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
		"Spell Scroll",
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
	)

	internal fun magicItemsStartingIn(range: ClosedRange<Char>): List<MagicItemReferenceEntry> =
		magicItemNames
			.filter { name -> name.firstOrNull()?.uppercaseChar()?.let { it in range } == true }
			.map { name -> detailedItems[name] ?: placeholder(name) }

	val MAGIC_ITEMS_A_TO_Z =
		EquipmentReferenceMagicItemsAtoH.MAGIC_ITEMS +
			EquipmentReferenceMagicItemsItoQ.MAGIC_ITEMS +
			EquipmentReferenceMagicItemsRtoZ.MAGIC_ITEMS
}
