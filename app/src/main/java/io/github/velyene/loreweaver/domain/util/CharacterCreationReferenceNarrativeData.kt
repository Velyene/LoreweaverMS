/*
 * FILE: CharacterCreationReferenceNarrativeData.kt
 *
 * TABLE OF CONTENTS:
 * 1. Character Creation Narrative Dataset
 */

@file:Suppress("kotlin:S1192")

package io.github.velyene.loreweaver.domain.util

internal object CharacterCreationReferenceNarrativeData {
	val introduction = """
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

	val characterCreationSections = listOf(
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

	val steps = listOf(
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

	val abilityScores = listOf(
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

	val alignmentSummaries = listOf(
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

	val beyondFirstLevel = """
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
