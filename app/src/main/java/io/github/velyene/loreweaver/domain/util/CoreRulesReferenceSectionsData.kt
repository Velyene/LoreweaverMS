/*
 * FILE: CoreRulesReferenceSectionsData.kt
 *
 * TABLE OF CONTENTS:
 * 1. Core Rules Section Dataset
 */

package io.github.velyene.loreweaver.domain.util

internal object CoreRulesReferenceSectionsData {
	val introduction =
		"This tab condenses the system loop used most often at the table: scene framing, d20 resolution, " +
			"movement, combat timing, and recovery. It is meant to be a fast rules digest rather than a full " +
			"rulebook replacement."

	val sections = listOf(
		CoreRuleSection(
			title = "General Principles",
			summary =
				"Specific features beat broad defaults, and fractions produced by multiplication or division are " +
					"rounded down unless a rule explicitly says otherwise.",
			bullets = listOf(
				"If a feature changes a baseline rule, follow the feature.",
				"Round down after multiplying or dividing, even on a one-half result.",
				"Treat unusual wording as intentional whenever it conflicts with a general rule."
			),
			keywords = listOf("exceptions supersede general rules", "round down")
		),
		CoreRuleSection(
			title = "Rhythm of Play",
			summary =
				"Play usually loops through the Game Master describing the scene, the players declaring actions, and " +
					"the Game Master narrating the outcome. Combat keeps the same pattern but formalizes it with turns " +
					"and rounds.",
			bullets = listOf(
				"The Game Master establishes the location, threats, and opportunities.",
				"Players explain what their characters attempt.",
				"The Game Master resolves the outcome, sometimes with a die roll when failure would matter."
			),
			keywords = listOf("scene", "players describe", "gm narrates")
		),
		CoreRuleSection(
			title = "D20 Tests",
			summary =
				"Ability checks, saving throws, and attack rolls all follow the same structure: roll a d20, add the " +
					"right modifiers, and compare the total to a target number.",
			bullets = listOf(
				"Roll one d20, or two and keep one when advantage or disadvantage applies.",
				"Add the relevant ability modifier.",
				"Add proficiency if the creature is trained in the skill, save, weapon, spell, or tool involved.",
				"Meet or beat the DC for checks and saves, or the Armor Class for attacks."
			),
			keywords = listOf("ability check", "saving throw", "attack roll", "dc", "armor class")
		),
		CoreRuleSection(
			title = "Ability Checks",
			summary =
				"Checks resolve uncertain tasks outside direct attacks, such as lifting gates, sneaking, " +
					"recalling lore, or reading a room.",
			bullets = listOf(
				"Strength handles force and athletics.",
				"Dexterity covers agility, balance, stealth, and precise motion.",
				"Constitution measures endurance and pushing through strain.",
				"Intelligence supports reasoning, memory, and study.",
				"Wisdom handles awareness, intuition, and perception.",
				"Charisma covers influence, deception, presence, and performance."
			),
			keywords = listOf("typical difficulty classes", "study", "search", "influence")
		),
		CoreRuleSection(
			title = "Saving Throws",
			summary =
				"Saving throws represent resisting or avoiding threats that target the body, senses, or mind. You can " +
					"also choose to fail a save voluntarily.",
			bullets = listOf(
				"Strength resists direct physical force.",
				"Dexterity avoids blasts, traps, and sudden danger.",
				"Constitution endures poison, disease, and other bodily hazards.",
				"Intelligence can pierce falsehoods such as illusions.",
				"Wisdom resists fear, charm, and other mental pressure.",
				"Charisma protects identity, will, and planar integrity."
			),
			keywords = listOf("save", "saving throw proficiency")
		),
		CoreRuleSection(
			title = "Attack Rolls and Armor Class",
			summary =
				"Weapon attacks usually use Strength in melee and Dexterity at range, while spell attacks use the " +
					"casting ability defined by the attacker. Base Armor Class starts at 10 plus Dexterity modifier " +
					"unless armor, a feature, or magic supplies another formula.",
			bullets = listOf(
				"A natural 20 on an attack is an automatic hit and a critical hit.",
				"A natural 1 on an attack always misses.",
				"Only one base Armor Class calculation can apply at a time."
			),
			keywords = listOf("critical hit", "natural 20", "natural 1", "base ac")
		),
		CoreRuleSection(
			title = "Advantage, Disadvantage, and Heroic Inspiration",
			summary =
				"Advantage means roll two d20s and keep the higher; disadvantage keeps the lower. They never stack, " +
					"and if both apply, they cancel. Heroic Inspiration lets you reroll one die after you see it.",
			bullets = listOf(
				"Even if several effects grant advantage, you still roll only two d20s.",
				"Even if several effects impose disadvantage, you still roll only two d20s.",
				"When a reroll applies to a roll with advantage or disadvantage, reroll only one of the dice.",
				"A character can hold only one instance of Heroic Inspiration at a time."
			),
			keywords = listOf("reroll", "inspiration")
		),
		CoreRuleSection(
			title = "Proficiency",
			summary =
				"Proficiency shows trained competence. Add the proficiency bonus once when a rule says the creature is " +
					"proficient in the relevant skill, save, tool, weapon, or spell attack.",
			bullets = listOf(
				"The same proficiency bonus cannot be added to the same roll more than once.",
				"A doubled or halved proficiency bonus is still modified only one time each way.",
				"Tool proficiency can combine with the related skill proficiency, granting advantage on the check when both apply."
			),
			keywords = listOf("expertise", "tool proficiency", "skill proficiency")
		),
		CoreRuleSection(
			title = "Actions, Bonus Actions, and Reactions",
			summary =
				"Creatures normally take one action on their turn, possibly one bonus action if a feature grants it, " +
					"and at most one reaction before the start of their next turn.",
			bullets = listOf(
				"You can do only one action at a time.",
				"A bonus action exists only when a feature, spell, or rule grants one.",
				"A reaction happens immediately after its trigger unless the rule says otherwise.",
				"If a reaction interrupts another turn, that turn resumes right after the reaction resolves."
			),
			keywords = listOf("one thing at a time", "bonus action", "reaction")
		),
		CoreRuleSection(
			title = "Social Interaction",
			summary =
				"Conversations can be resolved through roleplay alone or by checks when uncertainty matters. Nonplayer " +
					"creatures usually begin from a friendly, indifferent, or hostile attitude.",
			bullets = listOf(
				"Roleplay can improve or worsen an attitude before any roll happens.",
				"Use Influence when the outcome hinges on persuasion, deception, intimidation, performance, or animal handling.",
				"Let the character with the strongest relevant proficiency lead important exchanges."
			),
			keywords = listOf("friendly", "indifferent", "hostile", "roleplaying")
		),
		CoreRuleSection(
			title = "Exploration Basics",
			summary =
				"Exploration covers visibility, hidden dangers, object interaction, and environmental " +
					"obstacles while time matters less than in combat.",
			bullets = listOf(
				"Lightly obscured areas hinder sight-based Perception checks.",
				"Heavily obscured areas make sight fail as though the observer were blinded.",
				"Searching for something hidden works only when the character looks in the correct area.",
				"Breaking a fragile nonmagical object usually takes an action."
			),
			keywords = listOf("vision", "light", "darkness", "hiding", "objects", "hazards")
		),
		CoreRuleSection(
			title = "Travel and Marching Order",
			summary =
				"Long-distance movement uses travel pace rather than combat speed. Marching order decides who triggers " +
					"hazards first, who spots danger, and who starts closest when a fight erupts.",
			bullets = listOf(
				"Fast pace trades awareness for distance.",
				"Normal pace keeps steady movement but still hampers stealth.",
				"Slow pace sacrifices distance for better perception and survival awareness.",
				"Mounts can briefly double overland distance for about an hour before needing rest."
			),
			keywords = listOf("pace", "marching order", "vehicles")
		),
		CoreRuleSection(
			title = "Combat Sequence",
			summary =
				"Combat begins with positions, then initiative, then repeated turns in descending order until one side " +
					"is defeated, flees, or the fight otherwise ends.",
			bullets = listOf(
				"A round represents about six seconds.",
				"Initiative is a Dexterity check.",
				"A surprised combatant has disadvantage on initiative.",
				"Ties are sorted by the Game Master for monsters, by the players for their own characters, or by the " +
					"Game Master when both sides tie."
			),
			keywords = listOf("initiative", "surprise", "rounds", "turn order")
		),
		CoreRuleSection(
			title = "Movement and Position",
			summary =
				"On your turn, you can move up to your speed, split that movement around your action, and combine " +
					"walking with climbing, swimming, crawling, or jumping.",
			bullets = listOf(
				"Difficult terrain costs 1 extra foot for each foot moved.",
				"You can drop prone for free on your turn unless your speed is 0.",
				"You can move through allies, tiny creatures, incapacitated creatures, or creatures that are much " +
					"larger or smaller than you, but their spaces may count as difficult terrain.",
				"You cannot willingly end movement in another creature’s space."
			),
			keywords = listOf("difficult terrain", "prone", "creature size", "grid")
		),
		CoreRuleSection(
			title = "Making Attacks",
			summary =
				"Attacks follow a short structure: choose a target, determine modifiers such as cover and advantage, " +
					"then resolve the attack and its damage or special effect.",
			bullets = listOf(
				"Ranged attacks use a normal range and sometimes a longer range with disadvantage.",
				"Ranged attacks made while an enemy is adjacent are usually at disadvantage.",
				"Melee attacks usually reach 5 feet unless a feature or stat block extends that reach.",
				"Leaving a hostile creature’s reach can provoke an opportunity attack unless something prevents " +
					"it."
			),
			keywords = listOf(
				"reach",
				"opportunity attack",
				"cover",
				"ranged attacks in close combat"
			)
		),
		CoreRuleSection(
			title = "Special Combat Cases",
			summary =
				"Mounts, underwater fights, unseen targets, and unusual positioning all modify ordinary " +
					"attack and movement rules.",
			bullets = listOf(
				"If you attack a target you cannot see, the attack usually has disadvantage.",
				"If a creature cannot see you, your attacks against it usually have advantage.",
				"Controlled mounts act on your initiative and are limited to Dash, Disengage, and Dodge.",
				"Without a swim speed, many underwater weapon attacks are impaired, and fire damage is resisted underwater."
			),
			keywords = listOf("mounted combat", "underwater combat", "unseen attackers")
		),
		CoreRuleSection(
			title = "Monster Stat Blocks",
			summary =
				"A monster stat block is the creature's compact play reference, grouping identity, defenses, " +
					"movement, senses, challenge, and active abilities in one place.",
			bullets = listOf(
				"Name, size, creature type, tags, and alignment establish the monster's identity and default " +
					"roleplaying baseline.",
				"Armor Class, Hit Points, Speed, and Initiative summarize the creature's combat readiness at a " +
					"glance.",
				"Ability scores, saves, senses, languages, challenge rating, and optional details such as " +
					"skills, resistances, immunities, and gear appear when relevant.",
				"Traits, actions, bonus actions, reactions, and legendary actions list what the monster can do in play."
			),
			keywords = listOf(
				"monster",
				"stat block overview",
				"armor class",
				"hit points",
				"speed",
				"initiative",
				"senses",
				"languages",
				"cr"
			)
		),
		CoreRuleSection(
			title = "Running a Monster",
			summary =
				"To match a monster's listed threat, lean on its strongest limited features early, use " +
					"Multiattack whenever a bigger option is unavailable, and spend its extra action economy often.",
			bullets = listOf(
				"Use high-impact limited abilities such as breath weapons or once-per-day spells as quickly " +
					"and as often as the monster can.",
				"If a monster has Multiattack, default to it on turns when the creature is not using a stronger " +
					"special option.",
				"Bonus actions, reactions, and legendary actions are part of the monster's expected output, so " +
					"use them regularly when triggers appear."
			),
			keywords = listOf(
				"recharge",
				"multiattack",
				"legendary action",
				"bonus action",
				"reaction"
			)
		),
		CoreRuleSection(
			title = "Monster Attack and Usage Notation",
			summary =
				"Monster entries use short notation for attack rolls, save-based effects, spellcasting, " +
					"damage, and abilities with limited uses.",
			bullets = listOf(
				"Attack entries state whether the attack is melee or ranged, then list the attack bonus, " +
					"reach or range, and the result on a hit.",
				"Some attacks or effects also specify outcomes on a miss or regardless of whether they hit.",
				"Save-based effects name the save, give the DC, and explain the failed-save and " +
					"successful-save results; 'half damage only' means other riders are ignored on a " +
					"success.",
				"Spellcasting notes the monster's casting ability, save DC, attack bonus, and any component or usage exceptions.",
				"Limited-use features usually recharge by X/Day, by a Recharge roll, or after a Short or Long Rest."
			),
			keywords = listOf(
				"hit or miss",
				"saving throw effect",
				"damage notation",
				"spellcasting",
				"limited usage",
				"x/day"
			)
		),
		CoreRuleSection(
			title = "Damage, Healing, and Dying",
			summary =
				"Damage reduces hit points, critical hits roll the attack’s damage dice twice, and " +
					"creatures at 0 hit points either die outright or fall unconscious and begin death " +
					"saves.",
			bullets = listOf(
				"Half damage from a successful save is rounded down.",
				"Apply bonuses, penalties, and multipliers before resistance, then apply vulnerability last.",
				"Resistance and vulnerability do not stack with themselves.",
				"A stable creature at 0 hit points stops making death saves and recovers 1 hit point after 1d4 hours if left alone."
			),
			keywords = listOf(
				"critical hits",
				"resistance",
				"vulnerability",
				"immunity",
				"death saving throws",
				"massive damage"
			)
		),
		CoreRuleSection(
			title = TEMPORARY_HIT_POINTS,
			summary =
				"Temporary hit points are a separate buffer that is lost before real hit points, do not " +
					"stack together, and do not count as healing.",
			bullets = listOf(
				"Choose whether to keep your current temporary hit points or replace them with a new amount.",
				"They disappear after a long rest if they have not already been spent.",
				"Receiving temporary hit points while at 0 hit points does not restore consciousness."
			),
			keywords = listOf("temp hp", "temporary hp")
		)
	)
}
