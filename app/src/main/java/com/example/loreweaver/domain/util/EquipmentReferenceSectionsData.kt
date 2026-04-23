@file:Suppress("kotlin:S1192")

package com.example.loreweaver.domain.util

internal object EquipmentReferenceSectionsData {
	val CHAPTER_SECTIONS = listOf(
		CharacterCreationTextSection(
			title = "Coins",
			body = "Coins come in different denominations. The Coin Values table lists coins and how much they’re worth relative to the Gold Piece, the game’s main coin."
		),
		CharacterCreationTextSection(
			title = "Weapons",
			body = "Weapons are organized by category, damage, properties, mastery, weight, and cost."
		),
		CharacterCreationTextSection(
			title = "Weapon Proficiency",
			body = "You add your Proficiency Bonus to attack rolls made with weapons you are proficient with."
		),
		CharacterCreationTextSection(
			title = "Armor",
			body = "Armor entries list category, Armor Class, Strength requirements, Stealth limitations, weight, and cost."
		),
		CharacterCreationTextSection(
			title = "Armor Training",
			body = "Without training, armor imposes disadvantages and prevents spellcasting."
		),
		CharacterCreationTextSection(
			title = "Tools",
			body = "A tool helps you make specialized ability checks, craft items, or both. A tool’s description includes cost, weight, ability, utilize options, and variants."
		),
		CharacterCreationTextSection(
			title = "Tool Proficiency",
			body = "Tool proficiency adds your Proficiency Bonus and can combine with skill proficiency for Advantage."
		),
		CharacterCreationTextSection(
			title = "Adventuring Gear",
			body = "The Adventuring Gear table lists common items, their weight, cost, and practical uses."
		),
		CharacterCreationTextSection(
			title = "Mounts and Vehicles",
			body = "Mounts carry gear and riders, while tack and drawn vehicles expand hauling and travel options."
		),
		CharacterCreationTextSection(
			title = "Large Vehicles",
			body = "Large vehicles list crew, passengers, cargo, durability, and operating costs."
		),
		CharacterCreationTextSection(
			title = "Lifestyle Expenses",
			body = "Lifestyle expenses summarize the cost of living, covering lodging, food, maintenance, and necessities."
		),
		CharacterCreationTextSection(
			title = "Magic Items",
			body = "Magic items are organized by category and rarity, with condensed searchable entries covering the approved SRD equipment excerpt."
		),
		CharacterCreationTextSection(
			title = "Magic Item Rules",
			body = "Magic item rules summarize rarity, activation, cursed items, and resilience guidance relevant to the approved excerpt."
		),
		CharacterCreationTextSection(
			title = "Activating a Magic Item",
			body = "Activating a magic item can involve wearing it, wielding it, taking a Utilize action, or otherwise following its specific instructions."
		),
		CharacterCreationTextSection(
			title = "Crafting Magic Items",
			body = "Crafting Magic Items uses the rarity-based time and cost guidance in the equipment reference tables."
		),
		CharacterCreationTextSection(
			title = "Sentient Magic Items",
			body = "Sentient Magic Items can have alignment, communication modes, senses, and special purposes."
		)
	)

	val WEAPON_PROPERTY_SECTIONS = listOf(
		CharacterCreationTextSection(
			title = "Ammunition",
			body = "Ammunition property weapons consume compatible ammunition such as Arrows, Bolts, Bullets, or Needles."
		),
		CharacterCreationTextSection(
			title = "Finesse",
			body = "A Finesse weapon lets you use Strength or Dexterity for attack and damage rolls."
		),
		CharacterCreationTextSection(
			title = "Heavy",
			body = "Heavy weapons are unwieldy for Small creatures."
		),
		CharacterCreationTextSection(
			title = "Loading",
			body = "Loading limits the number of attacks you can make with the weapon unless a rule says otherwise."
		),
		CharacterCreationTextSection(
			title = "Thrown",
			body = "Thrown weapons can be used to make ranged attacks using their listed range."
		),
		CharacterCreationTextSection(
			title = "Two-Handed",
			body = "Two-Handed weapons require two hands to attack with."
		)
	)

	val WEAPON_MASTERY_SECTIONS = listOf(
		CharacterCreationTextSection(
			title = "Graze",
			body = "Graze lets a heavy melee weapon still pressure a foe even on a near miss."
		),
		CharacterCreationTextSection(
			title = "Nick",
			body = "Nick enables quick off-hand follow-up attacks with light weapons such as a Dagger."
		),
		CharacterCreationTextSection(
			title = "Push",
			body = "Push can force a target backward on a hit."
		),
		CharacterCreationTextSection(
			title = "Sap",
			body = "Sap hampers a target’s next strike."
		),
		CharacterCreationTextSection(
			title = "Slow",
			body = "Slow can reduce a target’s Speed after a hit."
		),
		CharacterCreationTextSection(
			title = "Topple",
			body = "Topple can knock a target Prone."
		),
		CharacterCreationTextSection(
			title = "Vex",
			body = "Vex can help set up your next attack against a creature."
		)
	)
}
