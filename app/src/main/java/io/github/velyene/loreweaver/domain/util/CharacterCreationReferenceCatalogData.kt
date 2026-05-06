/*
 * FILE: CharacterCreationReferenceCatalogData.kt
 *
 * TABLE OF CONTENTS:
 * 1. Character Creation Catalog Dataset
 */

@file:Suppress("kotlin:S1192")

package io.github.velyene.loreweaver.domain.util

@Suppress("DuplicateStringLiteralInspection")
internal object CharacterCreationReferenceCatalogData {
	private const val BACKGROUND_GOLD_OPTION = "B: 50 GP."
	private const val FIGHTING_STYLE_CATEGORY = "Fighting Style"
	private const val FIGHTING_STYLE_PREREQUISITE = "Fighting Style Feature"
	private const val EPIC_BOON_CATEGORY = "Epic Boon"
	private const val LEVEL_19_PREREQUISITE = "Level 19+"
	private const val EPIC_BOON_ABILITY_SCORE_INCREASE =
		"Ability Score Increase. Increase one ability score of your choice by 1, to a maximum of 30."

	val backgrounds = listOf(
		BackgroundReference(
			name = "Acolyte",
			abilityScores = listOf("Intelligence", "Wisdom", "Charisma"),
			feat = "Magic Initiate",
			skillProficiencies = listOf("Insight", "Religion"),
			toolProficiency = "Calligrapher's Supplies",
			equipmentOptions = listOf(
				"A: Calligrapher's Supplies, Book (Prayers), Holy Symbol, Parchment, Robe, and 8 GP.",
				BACKGROUND_GOLD_OPTION
			)
		),
		BackgroundReference(
			name = "Criminal",
			abilityScores = listOf("Dexterity", "Constitution", "Intelligence"),
			feat = "Alert",
			skillProficiencies = listOf("Sleight of Hand", "Stealth"),
			toolProficiency = "Thieves' Tools",
			equipmentOptions = listOf(
				"A: 2 Daggers, Thieves' Tools, Crowbar, Pouch, Dark Common Clothes, and 16 GP.",
				BACKGROUND_GOLD_OPTION
			)
		),
		BackgroundReference(
			name = "Sage",
			abilityScores = listOf("Constitution", "Intelligence", "Wisdom"),
			feat = "Magic Initiate",
			skillProficiencies = listOf("Arcana", "History"),
			toolProficiency = "Calligrapher's Supplies",
			equipmentOptions = listOf(
				"A: Calligrapher's Supplies, Book, Bottle of Ink, Ink Pen, Parchment, Robe, and 8 GP.",
				BACKGROUND_GOLD_OPTION
			)
		),
		BackgroundReference(
			name = "Soldier",
			abilityScores = listOf("Strength", "Dexterity", "Constitution"),
			feat = "Savage Attacker",
			skillProficiencies = listOf("Athletics", "Intimidation"),
			toolProficiency = "Gaming Set",
			equipmentOptions = listOf(
				"A: Spear, Shortbow, 20 Arrows, Gaming Set, Healer's Kit, Traveler's Clothes, and 14 GP.",
				BACKGROUND_GOLD_OPTION
			)
		)
	)

	val standardLanguages = listOf(
		LanguageReference(name = "Common", group = "Standard", roll = "—"),
		LanguageReference(name = "Common Sign Language", group = "Standard", roll = "1"),
		LanguageReference(name = "Draconic", group = "Standard", roll = "2"),
		LanguageReference(name = "Dwarvish", group = "Standard", roll = "3–4"),
		LanguageReference(name = "Elvish", group = "Standard", roll = "5–6"),
		LanguageReference(name = "Giant", group = "Standard", roll = "7"),
		LanguageReference(name = "Gnomish", group = "Standard", roll = "8"),
		LanguageReference(name = "Goblin", group = "Standard", roll = "9"),
		LanguageReference(name = "Halfling", group = "Standard", roll = "10–11"),
		LanguageReference(name = "Orc", group = "Standard", roll = "12")
	)

	val rareLanguages = listOf(
		LanguageReference(name = "Abyssal", group = "Rare"),
		LanguageReference(name = "Primordial", group = "Rare"),
		LanguageReference(name = "Celestial", group = "Rare"),
		LanguageReference(name = "Sylvan", group = "Rare"),
		LanguageReference(name = "Deep Speech", group = "Rare"),
		LanguageReference(name = "Thieves' Cant", group = "Rare"),
		LanguageReference(name = "Druidic", group = "Rare"),
		LanguageReference(name = "Undercommon", group = "Rare"),
		LanguageReference(name = "Infernal", group = "Rare")
	)

	val feats = listOf(
		FeatReference(
			name = "Alert",
			category = "Origin",
			benefits = listOf(
				"Initiative Proficiency. When you roll Initiative, you can add your Proficiency Bonus to the roll.",
				"Initiative Swap. Immediately after you roll Initiative, you can swap your Initiative " +
					"with that of one willing ally in the same combat, provided neither of you is " +
					"Incapacitated."
			)
		),
		FeatReference(
			name = "Magic Initiate",
			category = "Origin",
			benefits = listOf(
				"Two Cantrips. Learn two cantrips of your choice from the Cleric, Druid, or Wizard " +
					"spell list and choose Intelligence, Wisdom, or Charisma as the spellcasting ability " +
					"for this feat.",
				"Level 1 Spell. Choose a level 1 spell from the same list; it is always prepared, can " +
					"be cast once without a spell slot per Long Rest, and can also be cast with any spell " +
					"slots you have.",
				"Spell Change. Whenever you gain a new level, you can replace one chosen spell with a " +
					"different spell of the same level from the chosen spell list."
			),
			repeatable = true
		),
		FeatReference(
			name = "Savage Attacker",
			category = "Origin",
			benefits = listOf(
				"Once per turn when you hit a target with a weapon, you can roll the weapon's damage " +
					"dice twice and use either roll against the target."
			)
		),
		FeatReference(
			name = "Skilled",
			category = "Origin",
			benefits = listOf(
				"Gain proficiency in any combination of three skills or tools of your choice."
			),
			repeatable = true
		),
		FeatReference(
			name = "Ability Score Improvement",
			category = "General",
			prerequisite = "Level 4+",
			benefits = listOf(
				"Increase one ability score of your choice by 2, or increase two ability scores of " +
					"your choice by 1; this feat can't increase an ability score above 20."
			),
			repeatable = true
		),
		FeatReference(
			name = "Grappler",
			category = "General",
			prerequisite = "Level 4+, Strength or Dexterity 13+",
			benefits = listOf(
				"Ability Score Increase. Increase your Strength or Dexterity score by 1, to a maximum of 20.",
				"Punch and Grab. Once per turn, when you hit a creature with an Unarmed Strike as " +
					"part of the Attack action, you can use both the Damage and Grapple options.",
				"Attack Advantage. You have Advantage on attack rolls against a creature Grappled by you.",
				"Fast Wrestler. You don't spend extra movement to move a creature Grappled by you if it is your size or smaller."
			)
		),
		FeatReference(
			name = "Archery",
			category = FIGHTING_STYLE_CATEGORY,
			prerequisite = FIGHTING_STYLE_PREREQUISITE,
			benefits = listOf(
				"You gain a +2 bonus to attack rolls you make with Ranged weapons."
			)
		),
		FeatReference(
			name = "Defense",
			category = FIGHTING_STYLE_CATEGORY,
			prerequisite = FIGHTING_STYLE_PREREQUISITE,
			benefits = listOf(
				"While you're wearing Light, Medium, or Heavy armor, you gain a +1 bonus to Armor Class."
			)
		),
		FeatReference(
			name = "Great Weapon Fighting",
			category = FIGHTING_STYLE_CATEGORY,
			prerequisite = FIGHTING_STYLE_PREREQUISITE,
			benefits = listOf(
				"When you roll damage for an attack you make with a Melee weapon held in two hands, " +
					"you can treat any 1 or 2 on a damage die as a 3; the weapon must have the " +
					"Two-Handed or Versatile property."
			)
		),
		FeatReference(
			name = "Two-Weapon Fighting",
			category = FIGHTING_STYLE_CATEGORY,
			prerequisite = FIGHTING_STYLE_PREREQUISITE,
			benefits = listOf(
				"When you make an extra attack from using a weapon with the Light property, you can " +
					"add your ability modifier to that attack's damage if you aren't already adding it."
			)
		),
		FeatReference(
			name = "Boon of Combat Prowess",
			category = EPIC_BOON_CATEGORY,
			prerequisite = LEVEL_19_PREREQUISITE,
			benefits = listOf(
				EPIC_BOON_ABILITY_SCORE_INCREASE,
				"Peerless Aim. When you miss with an attack roll, you can hit instead; once you use " +
					"this benefit, you can't use it again until the start of your next turn."
			)
		),
		FeatReference(
			name = "Boon of Dimensional Travel",
			category = EPIC_BOON_CATEGORY,
			prerequisite = LEVEL_19_PREREQUISITE,
			benefits = listOf(
				EPIC_BOON_ABILITY_SCORE_INCREASE,
				"Blink Steps. Immediately after you take the Attack action or the Magic action, you can " +
					"teleport up to 30 feet to an unoccupied space you can see."
			)
		),
		FeatReference(
			name = "Boon of Fate",
			category = EPIC_BOON_CATEGORY,
			prerequisite = LEVEL_19_PREREQUISITE,
			benefits = listOf(
				EPIC_BOON_ABILITY_SCORE_INCREASE,
				"Improve Fate. When you or another creature within 60 feet of you succeeds on or fails " +
					"a D20 Test, you can roll 2d4 and apply the total as a bonus or penalty; once used, " +
					"this benefit refreshes when you roll Initiative or finish a Short or Long Rest."
			)
		),
		FeatReference(
			name = "Boon of Irresistible Offense",
			category = EPIC_BOON_CATEGORY,
			prerequisite = LEVEL_19_PREREQUISITE,
			benefits = listOf(
				"Ability Score Increase. Increase your Strength or Dexterity score by 1, to a maximum of 30.",
				"Overcome Defenses. The Bludgeoning, Piercing, and Slashing damage you deal always ignores Resistance.",
				"Overwhelming Strike. When you roll a 20 on the d20 for an attack roll, you can deal " +
					"extra damage equal to the ability score increased by this feat, and the damage type " +
					"matches the attack."
			)
		),
		FeatReference(
			name = "Boon of Spell Recall",
			category = EPIC_BOON_CATEGORY,
			prerequisite = "Level 19+, Spellcasting Feature",
			benefits = listOf(
				"Ability Score Increase. Increase your Intelligence, Wisdom, or Charisma score by 1, to a maximum of 30.",
				"Free Casting. Whenever you cast a spell with a level 1–4 spell slot, roll 1d4; if the " +
					"result matches the slot's level, the slot isn't expended."
			)
		),
		FeatReference(
			name = "Boon of the Night Spirit",
			category = EPIC_BOON_CATEGORY,
			prerequisite = LEVEL_19_PREREQUISITE,
			benefits = listOf(
				EPIC_BOON_ABILITY_SCORE_INCREASE,
				"Merge with Shadows. While within Dim Light or Darkness, you can give yourself the " +
					"Invisible condition as a Bonus Action until you take an action, Bonus Action, or " +
					"Reaction.",
				"Shadowy Form. While within Dim Light or Darkness, you have Resistance to all damage except Psychic and Radiant."
			)
		),
		FeatReference(
			name = "Boon of Truesight",
			category = EPIC_BOON_CATEGORY,
			prerequisite = LEVEL_19_PREREQUISITE,
			benefits = listOf(
				EPIC_BOON_ABILITY_SCORE_INCREASE,
				"Truesight. You have Truesight with a range of 60 feet."
			)
		)
	)

	val languageNotes = listOf(
		"Primordial includes the Aquan, Auran, Ignan, and Terran dialects. Speakers of one " +
			"dialect can usually communicate with speakers of the others.",
		"Knowledge of a language means the character can speak it, read it, and write it."
	)

	val trinkets = listOf(
		"A mummified goblin hand.",
		"A crystal that glows faintly in moonlight.",
		"A gold coin minted in an unknown land.",
		"A diary written in a language you cannot read.",
		"A brass ring that never tarnishes.",
		"An old chess piece made of glass.",
		"A pair of knucklebone dice marked with a skull where the six would be.",
		"A tiny idol of a nightmare creature that causes uneasy dreams nearby.",
		"A lock of someone's hair tied with thread.",
		"The deed to land in a place you do not recognize.",
		"A one-ounce block of an unknown material.",
		"A small cloth doll stuck with needles.",
		"A tooth from an unidentified beast.",
		"An enormous scale that might have come from a dragon.",
		"A bright green feather.",
		"An old divination card that bears your likeness.",
		"A glass orb filled with moving smoke.",
		"A one-pound egg with a vivid red shell.",
		"A pipe that blows bubbles instead of smoke.",
		"A glass jar holding a bit of flesh in preserving fluid.",
		"A gnome-made music box that plays a half-remembered childhood tune.",
		"A wooden statuette of a smug halfling.",
		"A brass orb etched with strange runes.",
		"A multicolored stone disk.",
		"A silver icon shaped like a raven.",
		"A bag holding forty-seven teeth, one badly rotten.",
		"A shard of obsidian that always feels warm.",
		"A dragon talon on a leather cord.",
		"A pair of old socks.",
		"A blank book whose pages reject ink, chalk, graphite, and paint.",
		"A silver badge shaped like a five-pointed star.",
		"A knife that once belonged to a relative.",
		"A glass vial full of nail clippings.",
		"A metal spark-maker with two tiny cups on one end.",
		"A white sequined glove sized for a human.",
		"A vest with one hundred tiny pockets.",
		"A weightless stone.",
		"A sketch of a goblin.",
		"An empty perfume vial that still smells sweet.",
		"A gemstone that looks like coal to everyone but you.",
		"A scrap cut from an old banner.",
		"A rank insignia from a lost legion.",
		"A silver bell without a clapper.",
		"A mechanical canary inside a lamp.",
		"A tiny chest carved to look like it has many feet.",
		"A dead sprite inside a clear bottle.",
		"A sealed metal can that sloshes or rattles with something inside.",
		"A glass orb of water containing a clockwork goldfish.",
		"A silver spoon engraved with the letter M.",
		"A whistle carved from gold-colored wood.",
		"A dead scarab as large as your hand.",
		"Two toy soldiers, one missing its head.",
		"A small box of mismatched buttons.",
		"A candle that refuses to light.",
		"A miniature cage with no door.",
		"An old key.",
		"An indecipherable treasure map.",
		"The hilt of a broken sword.",
		"A rabbit's foot.",
		"A glass eye.",
		"A cameo of a hideous face.",
		"A silver skull the size of a coin.",
		"An alabaster mask.",
		"A cone of sticky black incense that reeks.",
		"A nightcap said to bring pleasant dreams.",
		"A single caltrop made from bone.",
		"A gold monocle frame without a lens.",
		"A one-inch cube with each face a different color.",
		"A crystal doorknob.",
		"A packet full of pink dust.",
		"Part of a beautiful song written on two scraps of parchment.",
		"A silver teardrop earring containing a real teardrop.",
		"An eggshell painted with scenes of misery.",
		"A folding fan painted with a sleepy cat.",
		"A set of bone pipes.",
		"A pressed four-leaf clover inside a book on etiquette.",
		"A parchment sketch of a mechanical contraption.",
		"An ornate scabbard that fits no blade you own.",
		"An invitation to a party where a murder took place.",
		"A bronze pentacle etched with a rat's head.",
		"A purple handkerchief embroidered with the name of a famous archmage.",
		"Half a floor plan for a temple, castle, or other grand structure.",
		"A folded cloth that becomes a stylish cap when opened.",
		"A bank receipt from a far-off city.",
		"A diary missing seven pages.",
		"An empty silver snuffbox marked 'dreams'.",
		"An iron holy symbol devoted to an unknown god.",
		"A book about a legendary hero with the final chapter missing.",
		"A vial of dragon blood.",
		"An ancient arrow of elven make.",
		"A needle that never bends.",
		"An ornate brooch of dwarven design.",
		"An empty wine bottle labeled Red Dragon Crush.",
		"A glazed mosaic tile with many colors.",
		"A petrified mouse.",
		"A black pirate flag showing a dragon skull and crossbones.",
		"A tiny mechanical crab or spider that moves when unobserved.",
		"A jar of lard labeled Griffon Grease.",
		"A wooden box with a ceramic base containing a two-headed worm.",
		"A metal urn containing a hero's ashes."
	)
}
