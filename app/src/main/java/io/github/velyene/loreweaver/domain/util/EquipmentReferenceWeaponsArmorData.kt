@file:Suppress("kotlin:S1192")

package io.github.velyene.loreweaver.domain.util

internal object EquipmentReferenceWeaponsArmorData {
	private fun weapon(
		name: String,
		category: String,
		damage: String,
		properties: List<String>,
		mastery: String,
		weight: String,
		cost: String
	) = WeaponReferenceEntry(name, category, damage, properties, mastery, weight, cost)

	private fun armor(
		name: String,
		categoryDonDoff: String,
		armorClass: String,
		strength: String,
		stealth: String,
		weight: String,
		cost: String
	) = ArmorReferenceEntry(name, categoryDonDoff, armorClass, strength, stealth, weight, cost)

	val COIN_VALUES_TABLE = ReferenceTable(
		title = "Coin Values",
		columns = listOf("Coin", "Value in GP"),
		rows = listOf(
			listOf("Copper Piece (CP)", "1/100"),
			listOf("Silver Piece (SP)", "1/10"),
			listOf("Electrum Piece (EP)", "1/2"),
			listOf("Gold Piece (GP)", "1"),
			listOf("Platinum Piece (PP)", "10")
		)
	)

	val WEAPONS = listOf(
		weapon("Club", "Simple Melee", "1d4 Bludgeoning", listOf("Light"), "Slow", "2 lb.", "1 SP"),
		weapon("Dagger", "Simple Melee", "1d4 Piercing", listOf("Finesse", "Light", "Thrown (Range 20/60)"), "Nick", "1 lb.", "2 GP"),
		weapon("Greatclub", "Simple Melee", "1d8 Bludgeoning", listOf("Two-Handed"), "Push", "10 lb.", "2 SP"),
		weapon("Handaxe", "Simple Melee", "1d6 Slashing", listOf("Light", "Thrown (Range 20/60)"), "Vex", "2 lb.", "5 GP"),
		weapon("Javelin", "Simple Melee", "1d6 Piercing", listOf("Thrown (Range 30/120)"), "Slow", "2 lb.", "5 SP"),
		weapon("Light Hammer", "Simple Melee", "1d4 Bludgeoning", listOf("Light", "Thrown (Range 20/60)"), "Nick", "2 lb.", "2 GP"),
		weapon("Mace", "Simple Melee", "1d6 Bludgeoning", emptyList(), "Sap", "4 lb.", "5 GP"),
		weapon("Quarterstaff", "Simple Melee", "1d6 Bludgeoning", listOf("Versatile (1d8)"), "Topple", "4 lb.", "2 SP"),
		weapon("Sickle", "Simple Melee", "1d4 Slashing", listOf("Light"), "Nick", "2 lb.", "1 GP"),
		weapon("Spear", "Simple Melee", "1d6 Piercing", listOf("Thrown (Range 20/60)", "Versatile (1d8)"), "Sap", "3 lb.", "1 GP"),
		weapon("Dart", "Simple Ranged", "1d4 Piercing", listOf("Finesse", "Thrown (Range 20/60)"), "Vex", "1/4 lb.", "5 CP"),
		weapon("Light Crossbow", "Simple Ranged", "1d8 Piercing", listOf("Ammunition (Range 80/320; Bolt)", "Loading", "Two-Handed"), "Slow", "5 lb.", "25 GP"),
		weapon("Shortbow", "Simple Ranged", "1d6 Piercing", listOf("Ammunition (Range 80/320; Arrow)", "Two-Handed"), "Vex", "2 lb.", "25 GP"),
		weapon("Sling", "Simple Ranged", "1d4 Bludgeoning", listOf("Ammunition (Range 30/120; Bullet)"), "Slow", "—", "1 SP"),
		weapon("Battleaxe", "Martial Melee", "1d8 Slashing", listOf("Versatile (1d10)"), "Topple", "4 lb.", "10 GP"),
		weapon("Flail", "Martial Melee", "1d8 Bludgeoning", emptyList(), "Sap", "2 lb.", "10 GP"),
		weapon("Glaive", "Martial Melee", "1d10 Slashing", listOf("Heavy", "Reach", "Two-Handed"), "Graze", "6 lb.", "20 GP"),
		weapon("Greataxe", "Martial Melee", "1d12 Slashing", listOf("Heavy", "Two-Handed"), "Cleave", "7 lb.", "30 GP"),
		weapon("Greatsword", "Martial Melee", "2d6 Slashing", listOf("Heavy", "Two-Handed"), "Graze", "6 lb.", "50 GP"),
		weapon("Halberd", "Martial Melee", "1d10 Slashing", listOf("Heavy", "Reach", "Two-Handed"), "Cleave", "6 lb.", "20 GP"),
		weapon("Lance", "Martial Melee", "1d10 Piercing", listOf("Heavy", "Reach", "Two-Handed unless mounted"), "Topple", "6 lb.", "10 GP"),
		weapon("Longsword", "Martial Melee", "1d8 Slashing", listOf("Versatile (1d10)"), "Sap", "3 lb.", "15 GP"),
		weapon("Maul", "Martial Melee", "2d6 Bludgeoning", listOf("Heavy", "Two-Handed"), "Topple", "10 lb.", "10 GP"),
		weapon("Morningstar", "Martial Melee", "1d8 Piercing", emptyList(), "Sap", "4 lb.", "15 GP"),
		weapon("Pike", "Martial Melee", "1d10 Piercing", listOf("Heavy", "Reach", "Two-Handed"), "Push", "18 lb.", "5 GP"),
		weapon("Rapier", "Martial Melee", "1d8 Piercing", listOf("Finesse"), "Vex", "2 lb.", "25 GP"),
		weapon("Scimitar", "Martial Melee", "1d6 Slashing", listOf("Finesse", "Light"), "Nick", "3 lb.", "25 GP"),
		weapon("Shortsword", "Martial Melee", "1d6 Piercing", listOf("Finesse", "Light"), "Vex", "2 lb.", "10 GP"),
		weapon("Trident", "Martial Melee", "1d8 Piercing", listOf("Thrown (Range 20/60)", "Versatile (1d10)"), "Topple", "4 lb.", "5 GP"),
		weapon("War Pick", "Martial Melee", "1d8 Piercing", emptyList(), "Sap", "2 lb.", "5 GP"),
		weapon("Warhammer", "Martial Melee", "1d8 Bludgeoning", listOf("Versatile (1d10)"), "Push", "5 lb.", "15 GP"),
		weapon("Whip", "Martial Melee", "1d4 Slashing", listOf("Finesse", "Reach"), "Slow", "3 lb.", "2 GP"),
		weapon("Blowgun", "Martial Ranged", "1 Piercing", listOf("Ammunition (Range 25/100; Needle)", "Loading"), "Vex", "1 lb.", "10 GP"),
		weapon("Hand Crossbow", "Martial Ranged", "1d6 Piercing", listOf("Ammunition (Range 30/120; Bolt)", "Light", "Loading"), "Vex", "3 lb.", "75 GP"),
		weapon("Heavy Crossbow", "Martial Ranged", "1d10 Piercing", listOf("Ammunition (Range 100/400; Bolt)", "Heavy", "Loading", "Two-Handed"), "Slow", "18 lb.", "50 GP"),
		weapon("Longbow", "Martial Ranged", "1d8 Piercing", listOf("Ammunition (Range 150/600; Arrow)", "Heavy", "Two-Handed"), "Slow", "2 lb.", "50 GP"),
		weapon("Musket", "Martial Ranged", "1d12 Piercing", listOf("Ammunition (Range 40/120; Bullet)", "Loading", "Two-Handed"), "Slow", "10 lb.", "500 GP"),
		weapon("Pistol", "Martial Ranged", "1d10 Piercing", listOf("Ammunition (Range 30/90; Bullet)", "Loading"), "Vex", "3 lb.", "250 GP")
	)

	val WEAPONS_TABLE = ReferenceTable(
		title = "Weapons",
		columns = listOf("Name", "Category", "Damage", "Properties", "Mastery", "Weight", "Cost"),
		rows = WEAPONS.map { weapon ->
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
	)

	val ARMOR = listOf(
		armor("Padded Armor", "Light (1 minute to don or doff)", "11 + Dex modifier", "—", "Disadvantage", "8 lb.", "5 GP"),
		armor("Leather Armor", "Light (1 minute to don or doff)", "11 + Dex modifier", "—", "—", "10 lb.", "10 GP"),
		armor("Studded Leather Armor", "Light (1 minute to don or doff)", "12 + Dex modifier", "—", "—", "13 lb.", "45 GP"),
		armor("Hide Armor", "Medium (5 minutes to don or doff)", "12 + Dex modifier (max 2)", "—", "—", "12 lb.", "10 GP"),
		armor("Chain Shirt", "Medium (5 minutes to don or doff)", "13 + Dex modifier (max 2)", "—", "—", "20 lb.", "50 GP"),
		armor("Scale Mail", "Medium (5 minutes to don or doff)", "14 + Dex modifier (max 2)", "—", "Disadvantage", "45 lb.", "50 GP"),
		armor("Breastplate", "Medium (5 minutes to don or doff)", "14 + Dex modifier (max 2)", "—", "—", "20 lb.", "400 GP"),
		armor("Half Plate Armor", "Medium (5 minutes to don or doff)", "15 + Dex modifier (max 2)", "—", "Disadvantage", "40 lb.", "750 GP"),
		armor("Ring Mail", "Heavy (10 minutes to don, 5 minutes to doff)", "14", "—", "Disadvantage", "40 lb.", "30 GP"),
		armor("Chain Mail", "Heavy (10 minutes to don, 5 minutes to doff)", "16", "Str 13", "Disadvantage", "55 lb.", "75 GP"),
		armor("Splint Armor", "Heavy (10 minutes to don, 5 minutes to doff)", "17", "Str 15", "Disadvantage", "60 lb.", "200 GP"),
		armor("Plate Armor", "Heavy (10 minutes to don, 5 minutes to doff)", "18", "Str 15", "Disadvantage", "65 lb.", "1,500 GP"),
		armor("Shield", "Shield (Utilize action to don or doff)", "+2", "—", "—", "6 lb.", "10 GP")
	)

	val ARMOR_TABLE = ReferenceTable(
		title = "Armor",
		columns = listOf("Name", "Category / Don or Doff", "AC", "Strength", "Stealth", "Weight", "Cost"),
		rows = ARMOR.map { armor ->
			listOf(armor.name, armor.categoryDonDoff, armor.armorClass, armor.strength, armor.stealth, armor.weight, armor.cost)
		}
	)
}
