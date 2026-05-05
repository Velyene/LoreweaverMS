/*
 * FILE: CharacterCreationReferenceSpecies.kt
 *
 * TABLE OF CONTENTS:
 * 1. Object: CharacterCreationReferenceSpeciesData
 * 2. Value: raceChapterSections
 * 3. Value: races
 */

package io.github.velyene.loreweaver.domain.util

internal object CharacterCreationReferenceSpeciesData {
	private const val DARKVISION_DESCRIPTION =
		"You can see in dim light and darkness within 60 feet, perceiving darkness in shades of gray."
	private const val STANDARD_SPEED_30 = "30 feet."

	val raceChapterSections = listOf(
		CharacterCreationTextSection(
			title = "Chapter 2: Species",
			body = (
				"The great cities of many fantasy worlds bring many peoples together: dwarves, elves, " +
					"halflings, humans, and rarer folk such as dragonborn, goliaths, orcs, and tieflings. " +
					"Your ancestry shapes your characterâ€™s natural gifts, appearance, culture, and place in the world.\n\n" +
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
					"parent speciesâ€™ broad identity while adding a few details that nudge a character toward a " +
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

	val races = listOf(
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
					"Choose smithâ€™s tools, brewerâ€™s supplies, or masonâ€™s tools."
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
					"You have advantage on saves against being charmed, and magic canâ€™t put you to sleep."
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
					"Humans are mechanically simple but flexible, with a broad spread of ability score improvements and language options."
				)
			),
			notes = listOf(
				"Variant human replaces the normal ability increases with +1 to two different abilities, one skill proficiency, and one feat.",
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
					"Choose a dragon type. It determines your breath weaponâ€™s damage type, shape, and saving throw, as well as your damage resistance."
				),
				RacialTraitReference(
					"Breath Weapon",
					"As an action, exhale destructive energy. It starts at 2d6 damage, scales with level, and refreshes after a short or long rest."
				),
				RacialTraitReference(
					"Damage Resistance",
					"You resist the damage type tied to your draconic ancestry."
				)
			),
			notes = listOf(
				"Black and copper ancestry grant acid; blue and bronze grant lightning; brass, gold, and red grant fire; green grants poison; silver and white grant cold.",
				"Some settings feature draconians: corrupted dragonborn-like soldiers created from metallic dragon eggs."
			)
		),
		RaceReference(
			name = "Gnome",
			overview =
				"Curious, bright, and energetic inventors who fill long lives with discovery, humor, and delighted experimentation.",
			personality =
				"Gnomes are talkative, inventive, and quick to laugh. Even serious work is often approached with wonder, bold ideas, and playful resilience.",
			society =
				"Gnomes favor hidden burrows in wooded hills, bright workshops, and close-knit communities that balance clever illusion with practical craftsmanship.",
			adventurers =
				"Gnomes take to adventuring for curiosity, exploration, invention, or the promise of unusual treasures and experiences.",
			names =
				"Gnomes collect many names from family and friends, then choose whichever personal name, clan name, and nickname are the most fun to use with outsiders.",
			abilityScoreIncrease = "Intelligence +2",
			age = "Usually settle into adult life by around 40 and can live 350 to nearly 500 years.",
			size = "Small; typically 3 to 4 feet tall and about 40 pounds.",
			speed = "25 feet.",
			languages = "Common and Gnomish.",
			traits = listOf(
				RacialTraitReference("Darkvision", DARKVISION_DESCRIPTION),
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
							"Artificerâ€™s Lore",
							"History checks related to magic items, alchemy, or technological devices can add double your proficiency bonus."
						),
						RacialTraitReference(
							"Tinker",
							"With tinkerâ€™s tools, time, and materials, you can build tiny clockwork toys, fire starters, or music boxes."
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
				"Towering highland folk shaped by giant heritage, endurance, and a culture that prizes self-reliance, competition, and communal survival.",
			personality =
				"Goliaths often measure themselves against hardship and value honest effort, personal merit, and the stories that prove someone has endured.",
			society =
				"Many goliath communities dwell in high mountains or windswept plateaus where each member contributes to survival. Status usually follows deeds, resilience, and generosity rather than inherited rank.",
			adventurers =
				"Goliaths adventure to test themselves, bring honor to their people, repay debts, or seek challenges worthy of retelling for years.",
			names =
				"Goliath names often combine a personal name with a descriptive byname earned through a defining deed, flaw, or triumph.",
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
					"When you take damage, you can use a reaction to reduce it by 1d12 + your Constitution modifier once per short or long rest."
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
				"Hardy, forceful people whose culture prizes action, survival, and the strength to protect kin and claim a place in a harsh world.",
			personality =
				"Orcs are often direct, passionate, and intensely loyal. They may be quick to anger, but they are just as quick to laugh, boast, or throw themselves into danger for companions.",
			society =
				"Orc communities vary widely, from nomadic war bands to settled clans and frontier societies, but many honor courage, personal strength, and ties of family or tribe.",
			adventurers =
				"Orcs adventure to win glory, protect their people, escape narrow expectations, or turn fierce ambition toward a cause bigger than themselves.",
			names =
				"Orc names are often short, forceful, and easy to shout across a battlefield, though many orcs use names shaped by the culture that raised them.",
			abilityScoreIncrease = "Strength +2, Constitution +1",
			age = "Reach adulthood in the mid teens and rarely live beyond 80.",
			size = "Medium; generally tall, broad-shouldered, and physically imposing.",
			speed = STANDARD_SPEED_30,
			languages = "Common and Orc.",
			traits = listOf(
				RacialTraitReference("Darkvision", DARKVISION_DESCRIPTION),
				RacialTraitReference(
					"Adrenaline Rush",
					"You can surge into danger with a burst of speed and temporary resilience, giving yourself extra momentum when the fight turns urgent."
				),
				RacialTraitReference(
					"Powerful Build",
					"You count as one size larger when determining the weight you can carry, drag, or lift."
				),
				RacialTraitReference(
					"Relentless Endurance",
					"When you are reduced to 0 hit points but not killed outright, you can drop to 1 hit point instead once per long rest."
				)
			)
		),
		RaceReference(
			name = "Tiefling",
			overview =
				"Humanoid descendants of infernal bargains, tieflings bear visible signs of fiendish heritage and often endure suspicion for it.",
			personality =
				"Tieflings are not defined by their bloodline, but many become guarded, self-reliant, or fiercely defiant after years of mistrust and social pressure.",
			society =
				"Tieflings most often live in small minorities within human settlements, especially in rougher quarters where outsiders and strivers gather.",
			adventurers =
				"Adventure lets tieflings define themselves on their own terms, turning stigma into power, freedom, or hard-won respect.",
			names =
				"Tieflings may use local cultural names, inherited Infernal names, or self-chosen virtue names such as Hope, Quest, or Reverence.",
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
					"You know thaumaturgy, later gain hellish rebuke once per long rest at 3rd level, and darkness once per long rest at 5th level using Charisma."
				)
			)
		)
	)
}

