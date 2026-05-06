/*
 * FILE: CoreRulesReferenceGlossaryData.kt
 *
 * TABLE OF CONTENTS:
 * 1. Core Rules Glossary Dataset
 */

package io.github.velyene.loreweaver.domain.util
internal object CoreRulesReferenceGlossaryData {
val glossaryIntroduction =
"This glossary is organized for fast table lookups. Bracket tags group related rule families, search " +
"checks both terms and cross-references, and the entries stay concise so they work as a quick reminder " +
"rather than a full rulebook transcript."
val glossaryConventions = listOf(
"Bracket tags identify shared rule families such as actions, attitudes, conditions, hazards, and area shapes.",
"The word \"you\" always means the creature or object currently affected by the rule being read.",
"See also notes point toward nearby terms that are commonly used together at the table.",
"Only current shorthand and current rules terms are listed here; older terminology is intentionally omitted."
)
		val glossaryEntries = listOf(
		CoreGlossaryEntry(
			term = ABILITY_CHECK,
			summary = "A d20 test used to overcome uncertainty with an ability or one of its linked skills.",
			bullets = listOf(
				"Add the relevant ability modifier.",
				"Add proficiency when the creature is trained in the skill or tool involved.",
				"Compare the total to a DC."
			),
			seeAlso = listOf(D20_TEST, DIFFICULTY_CLASS, "Proficiency"),
			keywords = listOf("skill check")
		),
		CoreGlossaryEntry(
			term = "Ability Score and Modifier",
			summary = "Each of the six abilities has a score and a modifier; the modifier is what most rules add to d20 tests.",
			seeAlso = listOf(ABILITY_CHECK, SAVING_THROW, ATTACK_ROLL)
		),
		CoreGlossaryEntry(
			term = "Action",
			summary =
				"A creature normally gets one action on its turn and chooses it from basic actions or from " +
					"granted features.",
			bullets = listOf(
				"Common choices include Attack, Dash, Dodge, Help, Hide, Influence, Magic, Ready, " +
					"Search, Study, and Utilize.",
				"An action is separate from a bonus action or reaction."
			),
			seeAlso = listOf("Bonus Action", "Reaction", ATTACK_ACTION),
			keywords = listOf("turn action")
		),
		CoreGlossaryEntry(
			term = "Advantage",
			summary = "Roll two d20s and use the higher result.",
			bullets = listOf(
				"Multiple sources still give only one extra die.",
				"Advantage and disadvantage on the same roll cancel each other."
			),
			seeAlso = listOf("Disadvantage", "Heroic Inspiration")
		),
		CoreGlossaryEntry(
			term = AREA_OF_EFFECT,
			tag = AREA_OF_EFFECT,
			summary =
				"An effect can fill a defined shape from a chosen origin point, provided total cover does " +
					"not block the line to that spot.",
			bullets = listOf(
				"Common shapes are cone, cube, cylinder, emanation, line, and sphere.",
				"If the chosen origin lies behind a blocking obstacle, the effect begins on the near side instead."
			),
			seeAlso = listOf("Cover", "Target"),
			keywords = listOf("cone", "cube", "cylinder", "emanation", "line", "sphere")
		),
		CoreGlossaryEntry(
			term = "Alignment",
			summary =
				"A monster's listed alignment is a default roleplaying guide rather than an unbreakable " +
					"rule, and you can change it to fit the story.",
			bullets = listOf(
				"Neutral often signals that the individual creature could lean in different moral or " +
					"ethical directions."
			),
			seeAlso = listOf("Monster", STAT_BLOCK)
		),
		CoreGlossaryEntry(
			term = ARMOR_CLASS,
			summary = "Armor Class is the target number an attack roll must meet or exceed to hit.",
			bullets = listOf(
				"Base AC starts at 10 plus Dexterity modifier unless another formula replaces it.",
				"Only one base AC calculation applies at a time."
			),
			seeAlso = listOf(ATTACK_ROLL, "Cover")
		),
		CoreGlossaryEntry(
			term = "Attack",
			tag = "Action",
			summary = "Take one weapon attack or one unarmed strike as your action.",
			bullets = listOf(
				"You may equip or unequip one weapon before or after the attack made as part of this action.",
				"If a feature grants extra attacks, you can move between them."
			),
			seeAlso = listOf(ATTACK_ROLL, UNARMED_STRIKE, WEAPON_ATTACK)
		),
		CoreGlossaryEntry(
			term = ATTACK_ROLL,
			summary = "A d20 test made to hit with a weapon, an unarmed strike, or a spell attack.",
			seeAlso = listOf(ARMOR_CLASS, "Critical Hit", WEAPON_ATTACK)
		),
		CoreGlossaryEntry(
			term = "Attitude",
			tag = "Attitude",
			summary = "A creature's starting social stance is usually friendly, hostile, or indifferent.",
			bullets = listOf(
				"Attitude shapes whether influence checks gain advantage, suffer disadvantage, or proceed normally.",
				"Roleplay can change attitude before any roll happens."
			),
			seeAlso = listOf(
				FRIENDLY_ATTITUDE,
				"Hostile [Attitude]",
				"Indifferent [Attitude]",
				INFLUENCE_ACTION
			)
		),
		CoreGlossaryEntry(
			term = "Blinded",
			tag = "Condition",
			summary =
				"You cannot see, sight-based ability checks fail, attacks against you gain advantage, and " +
					"your attacks suffer disadvantage.",
			seeAlso = listOf("Darkness", "Heavily Obscured", "Invisible [Condition]")
		),
		CoreGlossaryEntry(
			term = "Bonus Action",
			summary = "A special extra action available only when a rule explicitly grants one.",
			bullets = listOf("You can take at most one bonus action on a turn."),
			seeAlso = listOf("Action", "Reaction")
		),
		CoreGlossaryEntry(
			term = BREAKING_OBJECTS,
			summary = "Objects use AC, hit points, and sometimes a damage threshold when attacks or spells try to destroy them.",
			bullets = listOf(
				"Very fragile objects may break automatically when the GM allows it.",
				"Objects ignore poison and psychic damage unless a rule says otherwise."
			),
			seeAlso = listOf("Damage Threshold", "Object")
		),
		CoreGlossaryEntry(
			term = "Burning",
			tag = "Hazard",
			summary = "A burning creature or object takes ongoing fire damage until the flames are put out.",
			bullets = listOf(
				"The damage is 1d4 fire at the start of each turn.",
				"Going prone to smother flames, dousing, submerging, or cutting off air can end it."
			),
			seeAlso = listOf("Hazard")
		),
		CoreGlossaryEntry(
			term = "Cantrip",
			summary = "A level 0 spell that is cast without spending a spell slot.",
			seeAlso = listOf("Spell", MAGIC_ACTION)
		),
		CoreGlossaryEntry(
			term = CHALLENGE_RATING,
			summary = "Challenge Rating estimates how threatening a monster is to a typical four-character party.",
			bullets = listOf("Actual danger still changes with terrain, numbers, and circumstances."),
			seeAlso = listOf(STAT_BLOCK, "Experience Points")
		),
		CoreGlossaryEntry(
			term = "Charmed",
			tag = "Condition",
			summary =
				"You cannot attack the charmer or target the charmer with damaging effects, and the " +
					"charmer has an edge in social interaction with you.",
			seeAlso = listOf(FRIENDLY_ATTITUDE, INFLUENCE_ACTION)
		),
		CoreGlossaryEntry(
			term = "Concentration",
			summary = "Some spells and effects stay active only while their creator keeps concentration.",
			bullets = listOf(
				"Starting another concentration effect ends the earlier one.",
				"Taking damage can force a Constitution save to maintain it.",
				"Being incapacitated or dying ends it immediately."
			),
			seeAlso = listOf(MAGIC_ACTION, SAVING_THROW, "Spell")
		),
		CoreGlossaryEntry(
			term = "Condition",
			tag = "Condition",
			summary = "A condition is a temporary game state with defined effects and its own ending rules.",
			bullets = listOf(
				"A condition usually does not stack with itself.",
				"Exhaustion is the main exception because it tracks levels."
			),
			seeAlso = listOf(
				EXHAUSTION_CONDITION,
				"Blinded [Condition]",
				UNCONSCIOUS_CONDITION
			)
		),
		CoreGlossaryEntry(
			term = "Cover",
			summary =
				"Cover protects a target behind an obstacle by raising AC, helping Dexterity saves, or " +
					"blocking direct targeting entirely.",
			bullets = listOf(
				"Only the best available degree of cover applies.",
				"Total cover blocks direct targeting."
			),
			seeAlso = listOf(ARMOR_CLASS, AREA_OF_EFFECT)
		),
		CoreGlossaryEntry(
			term = "Creature Type",
			summary =
				"A monster's creature type identifies what kind of being it is so spells, items, and " +
					"features can interact with it correctly.",
			bullets = listOf(
				"Tags in parentheses add extra categorization but usually do not carry rules by themselves.",
				"The common types include Aberration, Beast, Celestial, Construct, Dragon, Elemental, " +
					"Fey, Fiend, Giant, Humanoid, Monstrosity, Ooze, Plant, and Undead."
			),
			seeAlso = listOf("Monster", STAT_BLOCK),
			keywords = listOf("descriptive tags")
		),
		CoreGlossaryEntry(
			term = D20_TEST,
			summary = "Ability checks, attack rolls, and saving throws are all d20 tests.",
			seeAlso = listOf(ABILITY_CHECK, ATTACK_ROLL, SAVING_THROW)
		),
		CoreGlossaryEntry(
			term = "Damage Threshold",
			summary =
				"A creature or object with a damage threshold ignores any single hit or effect that does " +
					"not meet that minimum.",
			seeAlso = listOf(BREAKING_OBJECTS, "Damage")
		),
		CoreGlossaryEntry(
			term = DAMAGE_TYPES,
			summary = "Damage is labeled by type so resistance, vulnerability, and immunity can interact with it.",
			bullets = listOf(
				"Common types include fire, cold, lightning, force, bludgeoning, piercing, slashing, " +
					"poison, psychic, radiant, necrotic, acid, and thunder."
			),
			seeAlso = listOf("Resistance", "Vulnerability", "Immunity")
		),
		CoreGlossaryEntry(
			term = "Dash",
			tag = "Action",
			summary = "Dash grants extra movement equal to the speed you choose to use that turn.",
			seeAlso = listOf("Speed", "Movement")
		),
		CoreGlossaryEntry(
			term = DEATH_SAVING_THROW,
			summary = "A player character at 0 hit points usually makes a death save at the start of each turn.",
			seeAlso = listOf(HIT_POINTS, "Stable", UNCONSCIOUS_CONDITION),
			keywords = listOf("death save")
		),
		CoreGlossaryEntry(
			term = "Difficult Terrain",
			summary = "Each foot moved through difficult terrain costs one extra foot of movement.",
			bullets = listOf("The cost does not stack with itself; a space is either difficult terrain or it is not."),
			seeAlso = listOf("Speed", "Climbing", "Swimming")
		),
		CoreGlossaryEntry(
			term = DIFFICULTY_CLASS,
			summary = "A DC is the target number used for an ability check or a saving throw.",
			seeAlso = listOf(ABILITY_CHECK, SAVING_THROW)
		),
		CoreGlossaryEntry(
			term = "Disadvantage",
			summary = "Roll two d20s and use the lower result.",
			bullets = listOf(
				"Multiple sources still leave you with only two dice.",
				"Advantage and disadvantage on the same roll cancel."
			),
			seeAlso = listOf("Advantage")
		),
		CoreGlossaryEntry(
			term = "Dodge",
			tag = "Action",
			summary =
				"Until your next turn, attacks against you are hindered and Dexterity saves improve so " +
					"long as you can act and still move.",
			seeAlso = listOf("Action", "Incapacitated [Condition]")
		),
		CoreGlossaryEntry(
			term = "Exhaustion",
			tag = "Condition",
			summary =
				"Exhaustion builds in levels, weakening d20 tests and reducing speed until it becomes " +
					"lethal at level six.",
			bullets = listOf(
				"Each level applies a larger penalty to tests and movement.",
				"A long rest usually removes one level."
			),
			seeAlso = listOf(LONG_REST, "Hazard")
		),
		CoreGlossaryEntry(
			term = "Experience Points",
			summary =
				"A monster's XP value is based on its challenge rating and is awarded for defeating or " +
					"otherwise neutralizing that creature.",
			bullets = listOf(
				"Summoned monsters are still worth the XP listed in their own stat blocks unless a rule " +
					"says otherwise."
			),
			seeAlso = listOf(CHALLENGE_RATING, "Monster"),
			keywords = listOf("xp by challenge rating")
		),
		CoreGlossaryEntry(
			term = "Falling",
			tag = "Hazard",
			summary =
				"A fall usually deals 1d6 bludgeoning damage for every 10 feet, up to 20d6, and often " +
					"leaves the creature prone.",
			seeAlso = listOf("Prone [Condition]", "Flying")
		),
		CoreGlossaryEntry(
			term = "Friendly",
			tag = "Attitude",
			summary = "A friendly creature is inclined to help and is easier to influence.",
			seeAlso = listOf(ATTITUDE_ATTITUDE, INFLUENCE_ACTION)
		),
		CoreGlossaryEntry(
			term = "Gear",
			summary =
				"A monster's Gear entry lists retrievable equipment it can use proficiently, while " +
					"supernatural or highly specialized items mentioned elsewhere usually stop working for " +
					"others.",
			bullets = listOf(
				"A monster that needs ammunition for ranged attacks is assumed to carry enough " +
					"ammunition to make those attacks."
			),
			seeAlso = listOf("Monster", "Weapon")
		),
		CoreGlossaryEntry(
			term = "Grappled",
			tag = "Condition",
			summary = "A grapple sets your speed to 0 and makes it harder for you to attack targets other than the grappler.",
			bullets = listOf("The grappler can drag you at extra movement cost unless size differences make it easy."),
			seeAlso = listOf("Grappling", "Speed")
		),
		CoreGlossaryEntry(
			term = "Grappling",
			summary = "A grapple usually starts with an unarmed strike and applies the grappled condition on a failed save.",
			bullets = listOf(
				"A free hand or a listed body part is required.",
				"The target can escape with Athletics or Acrobatics against the escape DC."
			),
			seeAlso = listOf("Grappled [Condition]", UNARMED_STRIKE)
		),
		CoreGlossaryEntry(
			term = "Hazard",
			tag = "Hazard",
			summary = "A hazard is an environmental threat such as fire, starvation, dehydration, falling, or suffocation.",
			seeAlso = listOf("Burning [Hazard]", "Falling [Hazard]", "Suffocation [Hazard]")
		),
		CoreGlossaryEntry(
			term = "Help",
			tag = "Action",
			summary = "Help grants an ally an opening on a nearby attack or advantage on a relevant ability check.",
			seeAlso = listOf(ABILITY_CHECK, ATTACK_ROLL)
		),
		CoreGlossaryEntry(
			term = "Heroic Inspiration",
			summary = "Heroic Inspiration lets a player character reroll one die right after rolling it.",
			bullets = listOf(
				"If you already have it, a new grant is wasted unless you pass it to another player " +
					"character who lacks it."
			),
			seeAlso = listOf("Advantage")
		),
		CoreGlossaryEntry(
			term = "Hide",
			tag = "Action",
			summary = "Hide is a Dexterity (Stealth) check made while unseen or heavily protected from view.",
			bullets = listOf(
				"A successful hide leaves you effectively invisible until you give yourself away.",
				"Loud noise, attacking, or verbal spellcasting ends it."
			),
			seeAlso = listOf("Invisible [Condition]", "Heavily Obscured")
		),
		CoreGlossaryEntry(
			term = HIT_POINTS,
			summary = "Hit points measure how much damage a creature or object can take before it is destroyed or knocked down.",
			seeAlso = listOf("Healing", TEMPORARY_HIT_POINTS, DEATH_SAVING_THROW)
		),
		CoreGlossaryEntry(
			term = "Hostile",
			tag = "Attitude",
			summary = "A hostile creature resists your requests and is harder to influence.",
			seeAlso = listOf(ATTITUDE_ATTITUDE, INFLUENCE_ACTION)
		),
		CoreGlossaryEntry(
			term = "Immunity",
			summary = "Immunity means a damage type or condition does not affect you at all.",
			seeAlso = listOf("Resistance", "Vulnerability", "Condition")
		),
		CoreGlossaryEntry(
			term = "Incapacitated",
			tag = "Condition",
			summary = "You cannot take actions, bonus actions, or reactions; you also lose concentration and cannot speak.",
			seeAlso = listOf("Concentration", "Reaction")
		),
		CoreGlossaryEntry(
			term = "Indifferent",
			tag = "Attitude",
			summary = "An indifferent creature is neither inclined to help nor eager to hinder you.",
			seeAlso = listOf(ATTITUDE_ATTITUDE, INFLUENCE_ACTION)
		),
		CoreGlossaryEntry(
			term = "Influence",
			tag = "Action",
			summary =
				"Influence is the action for persuading, deceiving, threatening, amusing, or coaxing a " +
					"creature into cooperation.",
			bullets = listOf(
				"Willing creatures may comply without a roll.",
				"Hesitant creatures usually call for a Charisma or Wisdom check against a DC set by attitude and context."
			),
			seeAlso = listOf(ATTITUDE_ATTITUDE, FRIENDLY_ATTITUDE, "Hostile [Attitude]")
		),
		CoreGlossaryEntry(
			term = "Initiative",
			summary = "Initiative decides turn order in combat and is usually a Dexterity check.",
			bullets = listOf(
				"Monster stat blocks often show both the Initiative modifier and a fixed Initiative " +
					"score you can use instead of rolling."
			),
			seeAlso = listOf("Surprise", "Combat Sequence")
		),
		CoreGlossaryEntry(
			term = "Invisible",
			tag = "Condition",
			summary =
				"An invisible creature is concealed from effects that require sight, attacks with " +
					"advantage, and is attacked with disadvantage unless seen another way.",
			seeAlso = listOf("Hide [Action]", "Blindsight", "Truesight")
		),
		CoreGlossaryEntry(
			term = "Legendary Action",
			summary =
				"A legendary action is an action a monster can take immediately after another creature's " +
					"turn, spending from a limited pool that refreshes at the start of the monster's turn.",
			bullets = listOf("A monster cannot take legendary actions while incapacitated or otherwise unable to act."),
			seeAlso = listOf("Reaction", "Monster"),
			keywords = listOf("legendary actions")
		),
		CoreGlossaryEntry(
			term = "Limited Usage",
			summary =
				"Some monster features can be used only a fixed number of times per day, on a recharge " +
					"roll, or once before a short or long rest.",
			bullets = listOf(
				"X/Day restores after a Long Rest.",
				"Recharge X-Y returns when the listed d6 result appears at the start of the monster's " +
					"turn, or when it finishes a rest.",
				"Recharge after a Short or Long Rest restores only after one of those rests is completed."
			),
			seeAlso = listOf("Monster", SHORT_REST, LONG_REST),
			keywords = listOf("x/day", "recharge 5-6")
		),
		CoreGlossaryEntry(
			term = LONG_REST,
			summary =
				"A long rest is at least eight hours of extended downtime that restores hit points, spent " +
					"Hit Dice, and one level of exhaustion.",
			bullets = listOf(
				"You need at least 1 hit point to begin one.",
				"Major interruptions pause or end the rest unless it is resumed."
			),
			seeAlso = listOf(SHORT_REST, EXHAUSTION_CONDITION)
		),
		CoreGlossaryEntry(
			term = "Magic",
			tag = "Action",
			summary =
				"Magic is the action used to cast an action-time spell or activate a magical feature or " +
					"item that says so.",
			bullets = listOf(
				"Longer casting times require repeated Magic actions and ongoing concentration until the " +
					"casting finishes."
			),
			seeAlso = listOf("Spell", "Concentration", "Ritual")
		),
		CoreGlossaryEntry(
			term = "Monster",
			summary =
				"A monster is any creature run from a stat block, including beasts, foes, NPC opponents, " +
					"and unusual beings summoned or encountered in play.",
			bullets = listOf(
				"A monster stat block usually groups identity, defenses, movement, senses, challenge " +
					"rating, traits, and actions."
			),
			seeAlso = listOf(STAT_BLOCK, CHALLENGE_RATING, "Creature Type")
		),
		CoreGlossaryEntry(
			term = "Multiattack",
			summary =
				"Multiattack lets a creature make more than one listed attack, or combine listed attacks " +
					"and abilities, as part of the Attack action.",
			bullets = listOf(
				"When a monster has Multiattack, it usually relies on it during turns when a stronger " +
					"special option is unavailable."
			),
			seeAlso = listOf(ATTACK_ACTION, "Monster")
		),
		CoreGlossaryEntry(
			term = "Nonplayer Character",
			summary = "An NPC is a named, personality-driven creature run by the Game Master.",
			seeAlso = listOf("Monster")
		),
		CoreGlossaryEntry(
			term = "Object",
			summary =
				"An object is a distinct nonliving thing, while larger structures are usually treated as " +
					"multiple objects.",
			seeAlso = listOf(BREAKING_OBJECTS, "Size")
		),
		CoreGlossaryEntry(
			term = OPPORTUNITY_ATTACKS,
			summary = "Leaving a creature's reach with normal movement usually provokes a reactionary melee attack.",
			bullets = listOf("Teleportation and forced movement do not trigger it unless a rule says otherwise."),
			seeAlso = listOf("Reaction", "Reach", "Teleportation")
		),
		CoreGlossaryEntry(
			term = "Paralyzed",
			tag = "Condition",
			summary =
				"Paralysis incapacitates you, fixes your speed at 0, causes automatic Strength and " +
					"Dexterity save failures, and makes nearby hits into critical hits.",
			seeAlso = listOf("Incapacitated [Condition]", "Critical Hit")
		),
		CoreGlossaryEntry(
			term = "Passive Perception",
			summary = "Passive Perception is a creature's always-on awareness score, usually 10 plus its Perception bonus.",
			seeAlso = listOf("Search [Action]", "Wisdom (Perception)")
		),
		CoreGlossaryEntry(
			term = "Prone",
			tag = "Condition",
			summary = "A prone creature crawls or spends movement to stand, attacks poorly, and is easier to hit from nearby.",
			seeAlso = listOf("Speed", "Crawling")
		),
		CoreGlossaryEntry(
			term = "Proficiency",
			summary =
				"Proficiency lets you add your proficiency bonus to a d20 test that uses the trained " +
					"skill, save, weapon, spell, or tool.",
			bullets = listOf("The same proficiency bonus is not added to the same roll more than once."),
			seeAlso = listOf(ABILITY_CHECK, SAVING_THROW, WEAPON_ATTACK)
		),
		CoreGlossaryEntry(
			term = "Reaction",
			summary =
				"A reaction is a special response to a trigger and you can take only one before the start " +
					"of your next turn.",
			seeAlso = listOf(OPPORTUNITY_ATTACKS, "Ready [Action]")
		),
		CoreGlossaryEntry(
			term = "Ready",
			tag = "Action",
			summary = "Ready sets a trigger so you can use your reaction for a chosen action or movement later in the round.",
			bullets = listOf(
				"Readied spells are cast in advance, held with concentration, and released when the " +
					"trigger happens."
			),
			seeAlso = listOf("Reaction", "Concentration", MAGIC_ACTION)
		),
		CoreGlossaryEntry(
			term = "Resistance",
			summary =
				"Resistance halves damage of a listed type against you, rounding down, and each source " +
					"applies only once per hit.",
			seeAlso = listOf(DAMAGE_TYPES, "Vulnerability", "Immunity")
		),
		CoreGlossaryEntry(
			term = "Ritual",
			summary = "A prepared spell with the ritual tag can be cast more slowly without using a spell slot.",
			bullets = listOf("Ritual casting adds ten minutes and cannot heighten the spell with a higher-level slot."),
			seeAlso = listOf("Spell", MAGIC_ACTION)
		),
		CoreGlossaryEntry(
			term = "Round Down",
			summary = "Fractions from multiplication or division are rounded down unless a rule explicitly says otherwise.",
			seeAlso = listOf("Damage", "Resistance")
		),
		CoreGlossaryEntry(
			term = SAVING_THROW,
			summary =
				"A saving throw resists or avoids a rule-defined threat, and a creature may choose to fail " +
					"one voluntarily.",
			seeAlso = listOf(D20_TEST, DIFFICULTY_CLASS)
		),
		CoreGlossaryEntry(
			term = "Search",
			tag = "Action",
			summary = "Search uses Wisdom to notice something that is present but not obvious.",
			bullets = listOf(
				"Depending on the target, Insight, Medicine, Perception, or Survival may be the most " +
					"fitting skill."
			),
			seeAlso = listOf("Passive Perception", "Study [Action]")
		),
		CoreGlossaryEntry(
			term = SHORT_REST,
			summary = "A short rest is one hour of light downtime that allows Hit Dice spending and other short-rest recharges.",
			seeAlso = listOf(LONG_REST, HIT_POINTS)
		),
		CoreGlossaryEntry(
			term = "Speed",
			summary =
				"Speed is the distance a creature can move on its turn, and special speeds replace or mix " +
					"with normal movement as needed.",
			bullets = listOf(
				"When you switch between speeds, subtract distance already moved from the new speed to " +
					"see what remains."
			),
			seeAlso = listOf("Difficult Terrain", "Dash [Action]")
		),
		CoreGlossaryEntry(
			term = "Spell",
			summary = "A spell is a magical effect with defined components, range, duration, and casting time.",
			seeAlso = listOf("Spell Attack", "Spellcasting Focus", "Concentration")
		),
		CoreGlossaryEntry(
			term = "Spell Attack",
			summary = "A spell attack is an attack roll created by a spell or another magical effect.",
			seeAlso = listOf(ATTACK_ROLL, "Spell")
		),
		CoreGlossaryEntry(
			term = "Spellcasting Focus",
			summary = "A permitted focus can replace many non-costly, non-consumed material spell components.",
			seeAlso = listOf("Spell", MAGIC_ACTION)
		),
		CoreGlossaryEntry(
			term = "Stable",
			summary = "A stable creature has 0 hit points but no longer makes death saving throws.",
			seeAlso = listOf(DEATH_SAVING_THROW, UNCONSCIOUS_CONDITION)
		),
		CoreGlossaryEntry(
			term = STAT_BLOCK,
			summary =
				"A stat block is a monster's compact rules summary, gathering identity, defenses, " +
					"movement, senses, traits, actions, and challenge rating.",
			bullets = listOf(
				"Common entries include Armor Class, Hit Points, Speed, Initiative, ability scores, " +
					"saving throws, senses, and languages.",
				"Some stat blocks also list skills, resistances, vulnerabilities, immunities, gear, " +
					"bonus actions, reactions, or legendary actions."
			),
			seeAlso = listOf(CHALLENGE_RATING, "Monster", "Gear")
		),
		CoreGlossaryEntry(
			term = "Study",
			tag = "Action",
			summary = "Study uses Intelligence to recall knowledge or analyze clues, books, symbols, traps, or similar details.",
			bullets = listOf("Arcana, History, Investigation, Nature, and Religion are common skills for this action."),
			seeAlso = listOf("Search [Action]", ABILITY_CHECK)
		),
		CoreGlossaryEntry(
			term = "Suffocation",
			tag = "Hazard",
			summary = "When a creature runs out of breath or is choking, it gains exhaustion until it can breathe again.",
			seeAlso = listOf(EXHAUSTION_CONDITION, "Hazard")
		),
		CoreGlossaryEntry(
			term = "Target",
			summary = "A target is the creature or object chosen for an attack, save-forcing effect, spell, or similar rule.",
			seeAlso = listOf(ATTACK_ROLL, SAVING_THROW, "Spell")
		),
		CoreGlossaryEntry(
			term = "Telepathy",
			summary =
				"Telepathy is a magical ability that lets a creature communicate mentally with another " +
					"creature within a listed range.",
			seeAlso = listOf("Languages", "Monster"),
			keywords = listOf("mental communication")
		),
		CoreGlossaryEntry(
			term = "Teleportation",
			summary =
				"Teleportation moves you instantly without crossing intervening space, spending " +
					"movement, or provoking opportunity attacks.",
			bullets = listOf(
				"Worn and carried gear travels with you.",
				"If the destination is blocked, you appear in the nearest open space you choose."
			),
			seeAlso = listOf(OPPORTUNITY_ATTACKS, "Target")
		),
		CoreGlossaryEntry(
			term = TEMPORARY_HIT_POINTS,
			summary = "Temporary hit points absorb damage before real hit points, never stack together, and are not healing.",
			seeAlso = listOf(HIT_POINTS, "Healing")
		),
		CoreGlossaryEntry(
			term = UNARMED_STRIKE,
			summary =
				"An unarmed strike is a melee attack made with the body and can deal damage, start a " +
					"grapple, or shove a nearby target.",
			bullets = listOf("The grapple and shove options use a save DC based on Strength and proficiency."),
			seeAlso = listOf(ATTACK_ACTION, "Grappling")
		),
		CoreGlossaryEntry(
			term = "Unconscious",
			tag = "Condition",
			summary =
				"You become inert, prone, unaware, unable to move, and easy to hit; nearby hits are " +
					"automatic critical hits.",
			seeAlso = listOf(DEATH_SAVING_THROW, "Stable", "Prone [Condition]")
		),
		CoreGlossaryEntry(
			term = "Utilize",
			tag = "Action",
			summary =
				"Utilize is the action for using a nonmagical object when the object's rule says doing " +
					"so takes an action.",
			seeAlso = listOf("Action", BREAKING_OBJECTS)
		),
		CoreGlossaryEntry(
			term = "Vulnerability",
			summary = "Vulnerability doubles damage of a listed type against you, and each source applies only once per hit.",
			seeAlso = listOf("Resistance", "Immunity", DAMAGE_TYPES)
		),
		CoreGlossaryEntry(
			term = WEAPON_ATTACK,
			summary = "A weapon attack is an attack roll made with a weapon.",
			seeAlso = listOf(ATTACK_ROLL, "Weapon")
		),
		CoreGlossaryEntry(
			term = "Weapon",
			summary = "A weapon is an item in the simple or martial weapon categories.",
			seeAlso = listOf(WEAPON_ATTACK, ATTACK_ACTION)
		)
	)
val glossaryTables = listOf(CoreRulesReferenceTablesData.glossaryAbbreviationsTable)
}
