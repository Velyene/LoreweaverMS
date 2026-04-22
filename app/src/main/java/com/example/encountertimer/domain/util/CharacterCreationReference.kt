@file:Suppress("kotlin:S1192")

/*
 * FILE: CharacterCreationReference.kt
 *
 * TABLE OF CONTENTS:
 * 1. Character creation data models
 * 2. Background, language, and feat reference datasets
 * 3. Text sections for guided character creation
 * 4. Race and subrace reference datasets
 * 5. Aggregate CharacterCreationReference accessors
 */

package com.example.encountertimer.domain.util

/**
 * Data class for character creation steps.
 */
data class CharacterCreationStep(
	val number: Int,
	val title: String,
	val content: String,
	val example: String? = null
)

/**
 * Data class for an ability score summary.
 */
data class AbilityScoreSummary(
	val ability: String,
	val measures: String,
	val importantFor: String,
	val racialIncreases: String
)

/**
 * Data class for a structured background summary.
 */
data class BackgroundReference(
	val name: String,
	val abilityScores: List<String>,
	val feat: String,
	val skillProficiencies: List<String>,
	val toolProficiency: String,
	val equipmentOptions: List<String>
)

/**
 * Data class for a structured language reference entry.
 */
data class LanguageReference(
	val name: String,
	val group: String,
	val roll: String? = null
)

/**
 * Data class for a structured feat reference entry.
 */
data class FeatReference(
	val name: String,
	val category: String,
	val prerequisite: String? = null,
	val benefits: List<String>,
	val repeatable: Boolean = false
)

/**
 * Data class for chapter text sections in character creation.
 */
data class CharacterCreationTextSection(
	val title: String,
	val body: String
)

/**
 * Data class for racial traits and feature summaries.
 */
data class RacialTraitReference(
	val name: String,
	val description: String
)

/**
 * Data class for a playable subrace summary.
 */
data class SubraceReference(
	val name: String,
	val overview: String,
	val abilityScoreIncrease: String? = null,
	val traits: List<RacialTraitReference> = emptyList()
)

/**
 * Data class for a race summary in the character creation chapter.
 */
data class RaceReference(
	val name: String,
	val overview: String,
	val personality: String,
	val society: String,
	val adventurers: String,
	val names: String,
	val abilityScoreIncrease: String,
	val age: String,
	val size: String,
	val speed: String,
	val languages: String,
	val traits: List<RacialTraitReference>,
	val subraces: List<SubraceReference> = emptyList(),
	val notes: List<String> = emptyList()
)

/**
 * Utility object for character creation reference data.
 */
@Suppress("kotlin:S1192", "DuplicateStringLiteralInspection")
object CharacterCreationReference {
	private const val DARKVISION_DESCRIPTION =
		"You can see in dim light and darkness within 60 feet, perceiving darkness in shades of gray."
	private const val STANDARD_SPEED_30 = "30 feet."
	private const val BACKGROUND_GOLD_OPTION = "B: 50 GP."
	private const val FIGHTING_STYLE_CATEGORY = "Fighting Style"
	private const val FIGHTING_STYLE_PREREQUISITE = "Fighting Style Feature"
	private const val EPIC_BOON_CATEGORY = "Epic Boon"
	private const val LEVEL_19_PREREQUISITE = "Level 19+"
	private const val EPIC_BOON_ABILITY_SCORE_INCREASE =
		"Ability Score Increase. Increase one ability score of your choice by 1, to a maximum of 30."

	val RACE_CHAPTER_SECTIONS = listOf(
		CharacterCreationTextSection(
			title = "Chapter 2: Species",
			body = (
				"The great cities of many fantasy worlds bring many peoples together: dwarves, elves, " +
					"halflings, humans, and rarer folk such as dragonborn, goliaths, orcs, and tieflings. " +
					"Your ancestry shapes your character’s natural gifts, appearance, culture, and place in the world.\n\n" +
					"Most adventuring parties are built from the common peoples, but less common species can add unusual " +
					"history, social tension, or magical flavor to a campaign. Adventurers often stand apart from the " +
					"usual expectations of their people, so a species choice is also a prompt for backstory and personality."
			)
		),
		CharacterCreationTextSection(
			title = "Choosing a Species",
			body = (
				"Choose a species that supports the kind of hero you want to play. A halfling naturally suits a stealthy " +
					"rogue, a dwarf excels as a resilient front-line warrior, and an elf often complements arcane study " +
					"or graceful skirmishing.\n\n" +
					"A species affects ability scores, movement, senses, languages, and signature features. It also offers " +
					"roleplaying cues about appearance, family, customs, and the ways others in the world may respond " +
					"to your character."
			)
		),
		CharacterCreationTextSection(
			title = "Species Traits",
			body = (
				"Most species entries include the same core trait categories: ability score increases, age, size, " +
					"speed, languages, and sometimes notable variants. Those traits define the baseline assumptions " +
					"for your character, while your class and background build on top of them.\n\n" +
					"Some peoples also present recognizable cultural or mystical branches. These variants keep the " +
					"parent species’ broad identity while adding a few details that nudge a character toward a " +
					"particular fantasy archetype."
			)
		),
		CharacterCreationTextSection(
			title = "Less Common Species",
			body = (
				"Dragonborn, gnomes, goliaths, orcs, and tieflings are less common than dwarves, elves, halflings, " +
					"and humans. In major cosmopolitan cities they may blend in, but in villages or isolated regions " +
					"they often draw curiosity, suspicion, or awe.\n\n" +
					"That social friction can be useful story material. A less common species can highlight questions of " +
					"identity, belonging, prejudice, ambition, or inherited destiny."
			)
		)
	)

	val RACES = listOf(
		RaceReference(
			name = "Dwarf",
			overview =
				"Bold and hardy folk known for stonecraft, metalwork, clan loyalty, and endurance that rivals " +
					"larger peoples.",
			personality =
				"Dwarves are deliberate, loyal, and stubborn. Long lives give them long memories, strong traditions, " +
					"and a powerful sense of justice and grievance.",
			society =
				"Dwarven kingdoms are carved beneath mountains, with clan identity at the center of law, honor, " +
					"religion, and craftsmanship. Clanless life is considered a terrible fate.",
			adventurers =
				"Dwarves adventure for treasure, ancestral duty, divine calling, revenge, or to restore lost clan glory " +
					"and recover storied heirlooms.",
			names =
				"Dwarven names are handed down through clans and treated as inherited honors rather than purely " +
					"personal labels.",
			abilityScoreIncrease = "Constitution +2",
			age = "Young until about 50; average lifespan around 350 years.",
			size = "Medium; usually 4 to 5 feet tall and about 150 pounds.",
			speed = "25 feet; heavy armor does not reduce this speed.",
			languages = "Common and Dwarvish.",
			traits = listOf(
				RacialTraitReference("Darkvision", DARKVISION_DESCRIPTION),
				RacialTraitReference(
					"Dwarven Resilience",
					"You have advantage on saves against poison and resistance to poison damage."
				),
				RacialTraitReference(
					"Dwarven Combat Training",
					"You are proficient with the battleaxe, handaxe, light hammer, and warhammer."
				),
				RacialTraitReference(
					"Tool Proficiency",
					"Choose smith’s tools, brewer’s supplies, or mason’s tools."
				),
				RacialTraitReference(
					"Stonecunning",
					"History checks related to stonework treat you as proficient and let you add double your proficiency bonus."
				)
			),
			subraces = listOf(
				SubraceReference(
					name = "Hill Dwarf",
					overview = "Hill dwarves are wise, keen-sensed, and especially resilient.",
					abilityScoreIncrease = "Wisdom +1",
					traits = listOf(
						RacialTraitReference(
							"Dwarven Toughness",
							"Your hit point maximum increases by 1, and it increases by 1 again whenever you gain a level."
						)
					)
				),
				SubraceReference(
					name = "Mountain Dwarf",
					overview = "Mountain dwarves are strong, hardy, and shaped by rugged terrain and martial tradition.",
					abilityScoreIncrease = "Strength +2",
					traits = listOf(
						RacialTraitReference(
							"Dwarven Armor Training",
							"You are proficient with light and medium armor."
						)
					)
				)
			),
			notes = listOf(
				"Duergar, or gray dwarves, are Underdark dwarves famed for grim discipline and innate magic such " +
					"as invisibility and temporary growth."
			)
		),
		RaceReference(
			name = "Elf",
			overview =
				"Otherworldly, graceful, and long-lived people closely tied to beauty, magic, art, and the natural " +
					"world.",
			personality =
				"Elves tend to be curious, patient, and emotionally controlled. They are slow to form deep bonds but " +
					"equally slow to forget injury or loyalty.",
			society =
				"Many elves live in hidden woodland realms or elegant cities where magic and artistry replace brute " +
					"industry. Contact with outsiders is often limited but not always hostile.",
			adventurers =
				"Elves adventure from wanderlust, moral purpose, martial discipline, magical ambition, or the desire " +
					"to experience a wider world across centuries of life.",
			names =
				"Elves often begin with child names, then choose an adult name after about a century; family names " +
					"usually draw from Elvish words or poetic imagery.",
			abilityScoreIncrease = "Dexterity +2",
			age = "Adulthood is usually claimed around age 100; lifespan can reach about 750 years.",
			size = "Medium; generally under 5 to over 6 feet tall with slender builds.",
			speed = STANDARD_SPEED_30,
			languages = "Common and Elvish.",
			traits = listOf(
				RacialTraitReference("Darkvision", DARKVISION_DESCRIPTION),
				RacialTraitReference("Keen Senses", "You are proficient in the Perception skill."),
				RacialTraitReference(
					"Fey Ancestry",
					"You have advantage on saves against being charmed, and magic can’t put you to sleep."
				),
				RacialTraitReference(
					"Trance",
					"You meditate deeply for 4 hours instead of sleeping, gaining the same long-rest benefit as 8 hours of sleep."
				)
			),
			subraces = listOf(
				SubraceReference(
					name = "High Elf",
					overview = "High elves are keen-minded and steeped in the fundamentals of arcane study.",
					abilityScoreIncrease = "Intelligence +1",
					traits = listOf(
						RacialTraitReference(
							"Elf Weapon Training",
							"You are proficient with the longsword, shortsword, shortbow, and longbow."
						),
						RacialTraitReference(
							"Cantrip",
							"You know one wizard cantrip, using Intelligence as your spellcasting ability for it."
						),
						RacialTraitReference(
							"Extra Language",
							"You can speak, read, and write one additional language of your choice."
						)
					)
				),
				SubraceReference(
					name = "Wood Elf",
					overview = "Wood elves are intuitive, swift, and at home in deep forests and natural cover.",
					abilityScoreIncrease = "Wisdom +1",
					traits = listOf(
						RacialTraitReference(
							"Elf Weapon Training",
							"You are proficient with the longsword, shortsword, shortbow, and longbow."
						),
						RacialTraitReference(
							"Fleet of Foot",
							"Your base walking speed increases to 35 feet."
						),
						RacialTraitReference(
							"Mask of the Wild",
							"You can attempt to hide when lightly obscured by natural phenomena such as foliage, " +
								"heavy rain, mist, or falling snow."
						)
					)
				),
				SubraceReference(
					name = "Drow",
					overview =
						"Dark elves are Underdark exiles or schemers with keen senses, dangerous magic, and a " +
							"fraught reputation on the surface.",
					abilityScoreIncrease = "Charisma +1",
					traits = listOf(
						RacialTraitReference(
							"Superior Darkvision",
							"Your darkvision extends to 120 feet."
						),
						RacialTraitReference(
							"Sunlight Sensitivity",
							"Bright sunlight hinders your attack rolls and Wisdom (Perception) checks that rely on sight."
						),
						RacialTraitReference(
							"Drow Magic",
							"You know dancing lights, then later gain access to faerie fire and darkness once per long rest."
						),
						RacialTraitReference(
							"Drow Weapon Training",
							"You are proficient with rapiers, shortswords, and hand crossbows."
						)
					)
				)
			)
		),
		RaceReference(
			name = "Halfling",
			overview = "Small, practical, cheerful folk who value comfort, loyalty, and the quiet pleasures of home.",
			personality =
				"Halflings are friendly, curious, generous, and rarely cruel. They prefer simple joys over " +
					"ostentation and often meet danger with surprising courage.",
			society =
				"Most halflings live in peaceful rural communities or travel in wagons and riverboats. They " +
					"rarely build empires, but they fit well into the societies of larger peoples.",
			adventurers =
				"Halflings adventure to protect their communities, support friends, satisfy wanderlust, or see " +
					"a world much larger than themselves.",
			names =
				"Halflings usually keep a personal name, a family name, and sometimes a nickname that may last " +
					"for generations.",
			abilityScoreIncrease = "Dexterity +2",
			age = "Reach adulthood around 20 and often live into the middle of their second century.",
			size = "Small; about 3 feet tall and around 40 pounds.",
			speed = "25 feet.",
			languages = "Common and Halfling.",
			traits = listOf(
				RacialTraitReference(
					"Lucky",
					"When you roll a 1 on an attack roll, ability check, or saving throw, you can reroll the die and use the new roll."
				),
				RacialTraitReference(
					"Brave",
					"You have advantage on saving throws against being frightened."
				),
				RacialTraitReference(
					"Halfling Nimbleness",
					"You can move through the space of any creature larger than you."
				)
			),
			subraces = listOf(
				SubraceReference(
					name = "Lightfoot",
					overview = "Lightfoots are sociable wanderers who hide easily and blend into mixed communities.",
					abilityScoreIncrease = "Charisma +1",
					traits = listOf(
						RacialTraitReference(
							"Naturally Stealthy",
							"You can attempt to hide when obscured by a creature at least one size larger than you."
						)
					)
				),
				SubraceReference(
					name = "Stout",
					overview = "Stout halflings are tougher than most and are famous for resisting poison.",
					abilityScoreIncrease = "Constitution +1",
					traits = listOf(
						RacialTraitReference(
							"Stout Resilience",
							"You have advantage on saves against poison and resistance to poison damage."
						)
					)
				)
			)
		),
		RaceReference(
			name = "Human",
			overview =
				"Adaptable, ambitious, and varied, humans are the most widespread and culturally diverse " +
					"of the common peoples.",
			personality =
				"Human temperaments vary enormously, but they are often defined by ambition, " +
					"adaptability, innovation, and a desire to leave a lasting mark within a short life.",
			society =
				"Human cultures span empires, temples, libraries, merchant houses, and villages. Their " +
					"institutions often preserve tradition longer than any individual human lifespan can.",
			adventurers =
				"Humans frequently become adventurers to pursue glory, wealth, belief, discovery, " +
					"influence, or causes larger than themselves.",
			names =
				"Human names usually reflect family history, language, homeland, and social class. You " +
					"can draw from real-world naming traditions or invent naming patterns that fit your " +
					"campaign setting.",
			abilityScoreIncrease = "All ability scores +1",
			age = "Reach adulthood in the late teens and usually live less than a century.",
			size = "Medium; typically from about 5 feet to well over 6 feet tall.",
			speed = STANDARD_SPEED_30,
			languages = "Common plus one extra language of your choice.",
			traits = listOf(
				RacialTraitReference(
					"Adaptable Heritage",
					"Humans are mechanically simple but flexible, with a broad spread of ability score " +
						"improvements and language options."
				)
			),
			notes = listOf(
				"Variant human replaces the normal ability increases with +1 to two different abilities, " +
					"one skill proficiency, and one feat.",
				"Human ethnic groups are excellent inspiration for names, appearance, clothing, and cultural background."
			)
		),
		RaceReference(
			name = "Dragonborn",
			overview = "Proud draconic humanoids who value honor, self-mastery, and clan identity above almost everything else.",
			personality =
				"Dragonborn tend to be disciplined, proud, and deeply invested in personal excellence. " +
					"Failure stings sharply, and honor matters more than comfort.",
			society =
				"Clan is central to dragonborn identity. Each individual reflects the reputation of the " +
					"whole clan, and obligations to family and station are taken seriously.",
			adventurers =
				"Dragonborn often adventure to prove themselves, uphold clan honor, perfect a discipline, " +
					"or forge an identity beyond military duty or inherited expectation.",
			names =
				"Dragonborn place clan names first, then personal names. Childhood names or nicknames " +
					"are often descriptive and intimate.",
			abilityScoreIncrease = "Strength +2, Charisma +1",
			age = "Mature quickly, reaching adulthood around 15, and live to about 80.",
			size = "Medium; often well over 6 feet tall and strongly built.",
			speed = STANDARD_SPEED_30,
			languages = "Common and Draconic.",
			traits = listOf(
				RacialTraitReference(
					"Draconic Ancestry",
					"Choose a dragon type. It determines your breath weapon’s damage type, shape, and " +
						"saving throw, as well as your damage resistance."
				),
				RacialTraitReference(
					"Breath Weapon",
					"As an action, exhale destructive energy. It starts at 2d6 damage, scales with level, " +
						"and refreshes after a short or long rest."
				),
				RacialTraitReference(
					"Damage Resistance",
					"You resist the damage type tied to your draconic ancestry."
				)
			),
			notes = listOf(
				"Black and copper ancestry grant acid; blue and bronze grant lightning; brass, gold, " +
					"and red grant fire; green grants poison; silver and white grant cold.",
				"Some settings feature draconians: corrupted dragonborn-like soldiers created from metallic dragon eggs."
			)
		),
		RaceReference(
			name = "Gnome",
			overview =
				"Curious, bright, and energetic inventors who fill long lives with discovery, humor, and " +
					"delighted experimentation.",
			personality =
				"Gnomes are talkative, inventive, and quick to laugh. Even serious work is often " +
					"approached with wonder, bold ideas, and playful resilience.",
			society =
				"Gnomes favor hidden burrows in wooded hills, bright workshops, and close-knit " +
					"communities that balance clever illusion with practical craftsmanship.",
			adventurers =
				"Gnomes take to adventuring for curiosity, exploration, invention, or the promise of " +
					"unusual treasures and experiences.",
			names =
				"Gnomes collect many names from family and friends, then choose whichever personal " +
					"name, clan name, and nickname are the most fun to use with outsiders.",
			abilityScoreIncrease = "Intelligence +2",
			age = "Usually settle into adult life by around 40 and can live 350 to nearly 500 years.",
			size = "Small; typically 3 to 4 feet tall and about 40 pounds.",
			speed = "25 feet.",
			languages = "Common and Gnomish.",
			traits = listOf(
				RacialTraitReference(
					"Darkvision",
					"You can see in dim light and darkness within 60 feet, perceiving darkness in shades of gray."
				),
				RacialTraitReference(
					"Gnome Cunning",
					"You have advantage on Intelligence, Wisdom, and Charisma saving throws against magic."
				)
			),
			subraces = listOf(
				SubraceReference(
					name = "Forest Gnome",
					overview = "Forest gnomes are secretive woodland tricksters with a stronger tie to illusion and small animals.",
					abilityScoreIncrease = "Dexterity +1",
					traits = listOf(
						RacialTraitReference(
							"Natural Illusionist",
							"You know the minor illusion cantrip, using Intelligence as your spellcasting ability."
						),
						RacialTraitReference(
							"Speak with Small Beasts",
							"You can communicate simple ideas to Small or smaller beasts through sounds and gestures."
						)
					)
				),
				SubraceReference(
					name = "Rock Gnome",
					overview = "Rock gnomes are the classic tinkerers of the race, blending practicality with eccentric invention.",
					abilityScoreIncrease = "Constitution +1",
					traits = listOf(
						RacialTraitReference(
							"Artificer’s Lore",
							"History checks related to magic items, alchemy, or technological devices can add double your proficiency bonus."
						),
						RacialTraitReference(
							"Tinker",
							"With tinker’s tools, time, and materials, you can build tiny clockwork toys, fire starters, or music boxes."
						)
					)
				)
			),
			notes = listOf(
				"Deep gnomes, or svirfneblin, are a secretive Underdark subrace shaped by harsh subterranean life."
			)
		),
		RaceReference(
			name = "Goliath",
			overview =
				"Towering highland folk shaped by giant heritage, endurance, and a culture that prizes " +
					"self-reliance, competition, and communal survival.",
			personality =
				"Goliaths often measure themselves against hardship and value honest effort, personal " +
					"merit, and the stories that prove someone has endured.",
			society =
				"Many goliath communities dwell in high mountains or windswept plateaus where each " +
					"member contributes to survival. Status usually follows deeds, resilience, and " +
					"generosity rather than inherited rank.",
			adventurers =
				"Goliaths adventure to test themselves, bring honor to their people, repay debts, or seek " +
					"challenges worthy of retelling for years.",
			names =
				"Goliath names often combine a personal name with a descriptive byname earned through a " +
					"defining deed, flaw, or triumph.",
			abilityScoreIncrease = "Strength +2, Constitution +1",
			age = "Mature in the late teens and commonly live less than a century.",
			size = "Medium; often 7 feet tall or more, with broad and powerful frames.",
			speed = STANDARD_SPEED_30,
			languages = "Common and Giant.",
			traits = listOf(
				RacialTraitReference(
					"Natural Athlete",
					"You are proficient in the Athletics skill."
				),
				RacialTraitReference(
					"Stone's Endurance",
					"When you take damage, you can use a reaction to reduce it by 1d12 + your Constitution " +
						"modifier once per short or long rest."
				),
				RacialTraitReference(
					"Powerful Build",
					"You count as one size larger when determining the weight you can carry, drag, or lift."
				),
				RacialTraitReference(
					"Mountain Born",
					"You are naturally acclimated to high altitude, and you have resistance to cold damage."
				)
			)
		),
		RaceReference(
			name = "Orc",
			overview =
				"Hardy, forceful people whose culture prizes action, survival, and the strength to protect " +
					"kin and claim a place in a harsh world.",
			personality =
				"Orcs are often direct, passionate, and intensely loyal. They may be quick to anger, but " +
					"they are just as quick to laugh, boast, or throw themselves into danger for " +
					"companions.",
			society =
				"Orc communities vary widely, from nomadic war bands to settled clans and frontier " +
					"societies, but many honor courage, personal strength, and ties of family or tribe.",
			adventurers =
				"Orcs adventure to win glory, protect their people, escape narrow expectations, or turn " +
					"fierce ambition toward a cause bigger than themselves.",
			names =
				"Orc names are often short, forceful, and easy to shout across a battlefield, though many " +
					"orcs use names shaped by the culture that raised them.",
			abilityScoreIncrease = "Strength +2, Constitution +1",
			age = "Reach adulthood in the mid teens and rarely live beyond 80.",
			size = "Medium; generally tall, broad-shouldered, and physically imposing.",
			speed = STANDARD_SPEED_30,
			languages = "Common and Orc.",
			traits = listOf(
				RacialTraitReference("Darkvision", DARKVISION_DESCRIPTION),
				RacialTraitReference(
					"Adrenaline Rush",
					"You can surge into danger with a burst of speed and temporary resilience, giving " +
						"yourself extra momentum when the fight turns urgent."
				),
				RacialTraitReference(
					"Powerful Build",
					"You count as one size larger when determining the weight you can carry, drag, or lift."
				),
				RacialTraitReference(
					"Relentless Endurance",
					"When you are reduced to 0 hit points but not killed outright, you can drop to 1 hit " +
						"point instead once per long rest."
				)
			)
		),
		RaceReference(
			name = "Tiefling",
			overview =
				"Humanoid descendants of infernal bargains, tieflings bear visible signs of fiendish " +
					"heritage and often endure suspicion for it.",
			personality =
				"Tieflings are not defined by their bloodline, but many become guarded, self-reliant, or " +
					"fiercely defiant after years of mistrust and social pressure.",
			society =
				"Tieflings most often live in small minorities within human settlements, especially in " +
					"rougher quarters where outsiders and strivers gather.",
			adventurers =
				"Adventure lets tieflings define themselves on their own terms, turning stigma into power, " +
					"freedom, or hard-won respect.",
			names =
				"Tieflings may use local cultural names, inherited Infernal names, or self-chosen virtue " +
					"names such as Hope, Quest, or Reverence.",
			abilityScoreIncrease = "Intelligence +1, Charisma +2",
			age = "Mature at about the same rate as humans and usually live a little longer.",
			size = "Medium; generally similar in height and build to humans.",
			speed = STANDARD_SPEED_30,
			languages = "Common and Infernal.",
			traits = listOf(
				RacialTraitReference("Darkvision", DARKVISION_DESCRIPTION),
				RacialTraitReference("Hellish Resistance", "You have resistance to fire damage."),
				RacialTraitReference(
					"Infernal Legacy",
					"You know thaumaturgy, later gain hellish rebuke once per long rest at 3rd level, and " +
						"darkness once per long rest at 5th level using Charisma."
				)
			)
		)
	)

	val INTRODUCTION = """
		Character creation starts with a sheet of some kind: a printed form, a digital tracker,
		or plain paper. From there, you choose a class, define an origin, assign ability scores,
		pick an alignment, and fill in the practical details that turn the concept into a playable
		adventurer.

		As you build, keep both rules and story in view. A class shapes tactics, an origin shapes
		identity, ability scores highlight strengths and weaknesses, and the final details determine
		what the character can do the moment play begins.

		The sections below are designed as a table-side checklist and rules digest. They summarize
		the main creation flow, the common number lookups, and the advancement rules that matter
		once the campaign moves beyond level 1.
	""".trimIndent()

	val CHARACTER_CREATION_SECTIONS = listOf(
		CharacterCreationTextSection(
			title = "Choose a Character Sheet",
			body =
				"Use whatever sheet format helps you track the character best: printed, digital, or " +
					"handwritten. The important part is having a clear place for class, origin, abilities, " +
					"proficiencies, equipment, attacks, spells, and advancement notes."
		),
		CharacterCreationTextSection(
			title = "Step 1: Choose Class",
			body =
				"Your class defines your adventuring role, favored tactics, signature talents, and many " +
					"of your starting proficiencies. Record the class, your starting level, your " +
					"Experience Points, and any armor training or subclass choice that applies at your " +
					"starting level."
		),
		CharacterCreationTextSection(
			title = "Step 2: Character Origin",
			body =
				"Origin combines background, ancestry, and languages. Your background supplies early " +
					"training, proficiencies, a feat, and starting equipment, while your species determines " +
					"traits such as size, speed, and distinctive inherited abilities."
		),
		CharacterCreationTextSection(
			title = "Imagine the Past and Present",
			body =
				"Use background and species to sketch the life the character had before adventuring. " +
					"Consider family, childhood friendships, pets, love, guilds or faiths, and the event " +
					"or motivation that pushed the character toward danger and discovery."
		),
		CharacterCreationTextSection(
			title = "Choose Languages",
			body =
				"Most player characters know Common plus two additional languages chosen or rolled from " +
					"the standard list. Some classes and features grant more languages, and rare languages " +
					"are useful for planar, clandestine, or mystical stories."
		),
		CharacterCreationTextSection(
			title = "Step 3: Ability Scores",
			body =
				"Generate six numbers with the standard array, random rolls, or point cost. Then assign " +
					"them to the six abilities with your class priorities in mind, apply your " +
					"background-based increases, and calculate the matching modifiers."
		),
		CharacterCreationTextSection(
			title = "Step 4: Alignment",
			body =
				"Alignment is a short description of the character's moral outlook and attitude toward " +
					"order. It is a roleplaying tool rather than a cage, so use it to guide choices " +
					"instead of letting it erase nuance."
		),
		CharacterCreationTextSection(
			title = "Step 5: Character Creation Details",
			body =
				"Finish by recording class features, saving throws, skill totals, passive Perception, hit " +
					"points, Hit Dice, initiative, Armor Class, attack lines, and spellcasting numbers. " +
					"This is the step that makes the sheet ready for immediate play."
		),
		CharacterCreationTextSection(
			title = "Level Advancement",
			body =
				"Experience Points raise total character level. Advancing increases hit points, Hit Dice, " +
					"class features, and sometimes proficiency bonus or ability-based values. Any sheet " +
					"entry that depends on an improved modifier or proficiency bonus should be updated when " +
					"the level changes."
		),
		CharacterCreationTextSection(
			title = "Gaining a Level",
			body =
				"When the character levels up, choose whether to stay in the same class or multiclass, " +
					"increase hit points, add the new Hit Die, record new class features, update " +
					"proficiency-dependent totals, and recalculate anything affected by improved abilities. " +
					"If Constitution rises, the hit point maximum increases for every level the character " +
					"already has."
		),
		CharacterCreationTextSection(
			title = "Tiers of Play",
			body =
				"Levels 1 to 4 are apprentice-tier adventuring, levels 5 to 10 are seasoned heroics, " +
					"levels 11 to 16 reach region-shaping power, and levels 17 to 20 represent world-class " +
					"or multiversal stakes. The tone of the campaign often changes as the group moves " +
					"between these tiers."
		),
		CharacterCreationTextSection(
			title = "Starting at Higher Levels",
			body =
				"A Game Master can begin a campaign above level 1. In that case, build the character " +
					"normally, then apply the advancement steps up to the starting level, beginning with the " +
					"minimum Experience Points for that level and any extra gear or magic items the campaign " +
					"allows."
		),
		CharacterCreationTextSection(
			title = "Multiclassing",
			body =
				"Multiclassing lets a character gain levels in more than one class. To qualify, the " +
					"character must meet the primary-ability prerequisites for both the current class and " +
					"the new class, and some benefits—such as proficiencies, Extra Attack, spell slots, " +
					"and alternative Armor Class formulas—follow special multiclass rules."
		),
		CharacterCreationTextSection(
			title = "Spellcasting Across Classes",
			body =
				"When multiple classes grant spellcasting, prepared spells are tracked by class, cantrips " +
					"scale from total character level, and combined spell slots come from the multiclass " +
					"spellcaster table. Warlock pact slots remain distinct but can still be used to cast " +
					"prepared spells from other spellcasting classes."
		)
	)

	val STEPS = listOf(
		CharacterCreationStep(
			number = 1,
			title = "Choose a Class",
			content = """
				Choose the class that best matches the role you want to play. Your class determines
				core tactics, many of your proficiencies, your level 1 hit point formula, and whether
				spellcasting is part of your starting toolkit.

				Record your level and Experience Points as part of the same step. Most campaigns begin
				at level 1 with 0 XP, though some groups start higher and may require you to note a
				subclass immediately.

				If the class grants armor training, write that down now as well so you know what
				protective gear you can use effectively.
			""".trimIndent(),
			example =
				"A player decides their hero is a fighter. They record level 1, 0 XP, the fighter's " +
					"armor and weapon training, and any starting class features that must be chosen before " +
					"play begins."
		),
		CharacterCreationStep(
			number = 2,
			title = "Determine Origin",
			content = """
				Origin combines background, species, and languages. Background contributes
				proficiencies, a feat, and part of your starting equipment. Species supplies traits
				such as size, speed, and distinctive inherited features.

				Record your background's feat, skill proficiencies, tool proficiency, and starting
				equipment. Then note the species traits and choose at least three languages in total,
				usually Common plus two more.

				This is also a good point to imagine the character's life before adventure: mentors,
				friends, organizations, loyalties, and the reason the character now seeks danger or
				opportunity.
			""".trimIndent(),
			example =
				"The player gives the fighter a soldier background, chooses a species, notes the feat " +
					"and proficiencies, records speed and size, and picks Common, Dwarvish, and Goblin " +
					"as starting languages."
		),
		CharacterCreationStep(
			number = 3,
			title = "Determine Ability Scores",
			content = """
				Generate six scores with the standard array, random rolls, or point cost. Assign them
				to the six abilities according to your class priorities and the kind of hero you want
				to play.

				After assigning the scores, apply your background-based increases: raise one listed
				ability by 2 and another by 1, or raise all three listed abilities by 1. No increase
				from this step can push a score above 20.

				Once the scores are final, calculate the matching modifiers and record them beside the abilities.
			""".trimIndent(),
			example =
				"Using the standard array, the player puts the highest score in Strength for the " +
					"fighter, keeps Constitution high for durability, then uses the background increases " +
					"to strengthen the build without sacrificing a useful secondary ability."
		),
		CharacterCreationStep(
			number = 4,
			title = "Choose an Alignment",
			content = """
				Alignment is a quick summary of the character's ethical outlook and relationship to
				rules, freedom, or duty. It helps you roleplay consistently without replacing the
				character's individual nuance.

				Many groups assume player characters are not evil-aligned unless the Game Master says
				otherwise. Choose an alignment that fits the tone of the campaign and the story you want
				to tell.
			""".trimIndent(),
			example =
				"The player decides the fighter is Lawful Good: disciplined, duty-driven, and committed " +
					"to protecting ordinary people even when it costs something personally."
		),
		CharacterCreationStep(
			number = 5,
			title = "Fill in Details",
			content = """
				Use your earlier choices to finish the sheet. Record class features, saving throw
				modifiers, skill totals, passive Perception, hit points, Hit Dice, initiative, Armor
				Class, weapon attacks, damage, spell save DCs, spell attack bonuses, spell slots, and
				prepared spells if relevant.

				This is also the moment to name the character, define appearance and personality, and
				make sure the sheet is ready for the first encounter without extra table math.
			""".trimIndent(),
			example =
				"The player fills in AC, initiative, attack bonuses, passive Perception, and hit points, " +
					"then chooses a name and appearance so the character is fully table-ready."
		)
	)

	val ABILITY_SCORES = listOf(
		AbilityScoreSummary(
			"Strength",
			"Natural athleticism, bodily power",
			"Barbarian, fighter, paladin",
			"Mountain dwarf (+2), Dragonborn (+2), Goliath (+2), Orc (+2), Human (+1)"
		),
		AbilityScoreSummary(
			"Dexterity",
			"Physical agility, reflexes, balance, poise",
			"Monk, ranger, rogue",
			"Elf (+2), Halfling (+2), Forest gnome (+1), Human (+1)"
		),
		AbilityScoreSummary(
			"Constitution",
			"Health, stamina, vital force",
			"Everyone",
			"Dwarf (+2), Goliath (+1), Stout halfling (+1), Rock gnome (+1), Orc (+1), Human (+1)"
		),
		AbilityScoreSummary(
			"Intelligence",
			"Mental acuity, information recall, analytical skill",
			"Wizard",
			"High elf (+1), Gnome (+2), Tiefling (+1), Human (+1)"
		),
		AbilityScoreSummary(
			"Wisdom",
			"Awareness, intuition, insight",
			"Cleric, druid",
			"Hill dwarf (+1), Wood elf (+1), Human (+1)"
		),
		AbilityScoreSummary(
			"Charisma",
			"Confidence, eloquence, leadership",
			"Bard, sorcerer, warlock",
			"Drow (+1), Lightfoot halfling (+1), Dragonborn (+1), Tiefling (+2), Human (+1)"
		)
	)

	val BACKGROUNDS = listOf(
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

	val STANDARD_LANGUAGES = listOf(
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

	val RARE_LANGUAGES = listOf(
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

	val FEATS = listOf(
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


	val ABILITY_MODIFIERS = mapOf(
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

	val CHARACTER_CREATION_TABLES = listOf(
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
			rows = BACKGROUNDS.flatMap { background ->
				background.abilityScores.map { ability ->
					listOf(ability, background.name)
				}
			}
		),
		ReferenceTable(
			title = "Standard Languages",
			columns = listOf("Roll", "Language"),
			rows = STANDARD_LANGUAGES.map { language ->
				listOf(language.roll.orEmpty(), language.name)
			}
		),
		ReferenceTable(
			title = "Rare Languages",
			columns = listOf("Language A", "Language B"),
			rows = RARE_LANGUAGES.chunked(2).map { pair ->
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

	val LANGUAGE_NOTES = listOf(
		"Primordial includes the Aquan, Auran, Ignan, and Terran dialects. Speakers of one " +
			"dialect can usually communicate with speakers of the others.",
		"Knowledge of a language means the character can speak it, read it, and write it."
	)

	val ALIGNMENT_SUMMARIES = listOf(
		"Lawful Good: does what is right while honoring duty, order, or custom.",
		"Neutral Good: tries to help others without feeling tightly bound by rules.",
		"Chaotic Good: follows conscience first and resists oppressive expectations.",
		"Lawful Neutral: values law, discipline, or a code above emotional impulse.",
		"Neutral: avoids strong moral extremes and acts according to circumstance.",
		"Chaotic Neutral: prizes freedom and impulse over structure or obligation.",
		"Lawful Evil: pursues selfish goals through order, hierarchy, or strict codes.",
		"Neutral Evil: harms others without scruple when it serves personal desires.",
		"Chaotic Evil: embraces cruelty, destruction, and unrestrained malice.",
		"Unaligned creatures usually lack the rational self-awareness needed for moral or ethical alignment."
	)

	val TRINKETS = listOf(
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

	val BEYOND_FIRST_LEVEL = """
		As your character goes on adventures and overcomes challenges, he or she gains
		experience, represented by experience points. A character who reaches a specified
		experience point total advances in capability. This advancement is called gaining a level.

		Class Features and Hit Dice
		When your character gains a level, his or her class often grants additional features, as
		detailed in the class description. Some of these features allow you to increase your
		ability scores, either increasing two scores by 1 each or increasing one score by 2. You
		can’t increase an ability score above 20. In addition, every character's proficiency bonus
		increases at certain levels.

		Each time you gain a level, you gain 1 additional Hit Die. Roll that Hit Die, add your
		Constitution modifier to the roll, and add the total (minimum of 1) to your hit point
		maximum. Alternatively, you can use the fixed value shown in your class entry, which is
		the average result of the die roll (rounded up).

		When your Constitution modifier increases by 1, your hit point maximum increases by 1 for
		each level you have attained. For example, if your 7th-level fighter has a Constitution
		score of 17, when he reaches 8th level, he increases his Constitution score from 17 to 18,
		thus increasing his Constitution modifier from +3 to +4. His hit point maximum then
		increases by 8.
	""".trimIndent()
}
