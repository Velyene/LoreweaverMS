/*
 * FILE: MonsterReferenceDataDtoG.kt
 *
 * TABLE OF CONTENTS:
 * 1. Object: MonsterReferenceDataDtoG
 * 2. Value: entriesDtoF
 * 3. Value: ENTRIES
 * 4. Function: monster
 */

package io.github.velyene.loreweaver.domain.util

object MonsterReferenceDataDtoG {
	private val entriesDtoF: List<MonsterReferenceEntry> = listOf(
		monster(
			name = "Swarm of Crawling Claws",
			subtitle = "Medium Swarm of Tiny Undead, Neutral Evil",
			group = "Crawling Claw",
			ac = "12",
			initiative = "+2 (12)",
			hp = "49 (11d8)",
			speed = "30 ft., Climb 30 ft.",
			cr = "3 (XP 700; PB +2)",
			body = """
				STR 14 (+2 save +2), DEX 14 (+2 save +2), CON 11 (+0 save +0), INT 5 (-3 save -3), WIS 10 (+0 save +0), CHA 4 (-3 save -3)
				Resistances: Bludgeoning, Piercing, Slashing
				Immunities: Necrotic, Poison; Charmed, Exhaustion, Frightened, Grappled, Incapacitated, Paralyzed, Petrified, Poisoned, Prone, Restrained, Stunned
				Senses: Blindsight 30 ft.; Passive Perception 10
				Languages: Understands Common but can't speak
				Trait: Swarm. The swarm can occupy another creature's space and vice versa, and the swarm can move through any opening large enough for a Tiny creature. The swarm can't regain Hit Points or gain Temporary Hit Points.
				Actions: Swarm of Grasping Hands. Melee Attack Roll: +4, reach 5 ft. Hit: 20 (4d8 + 2) Necrotic damage, or 11 (2d8 + 2) Necrotic damage if the swarm is Bloodied. If the target is a Medium or smaller creature, it has the Prone condition.
			"""
		),
		monster(
			name = "Cultist",
			subtitle = "Medium or Small Humanoid, Neutral",
			group = "Cultists",
			ac = "12",
			initiative = "+1 (11)",
			hp = "9 (2d8)",
			speed = "30 ft.",
			cr = "1/8 (XP 25; PB +2)",
			body = """
				STR 11 (+0 save +0), DEX 12 (+1 save +1), CON 10 (+0 save +0), INT 10 (+0 save +0), WIS 11 (+0 save +2), CHA 10 (+0 save +0)
				Skills: Deception +2, Religion +2
				Gear: Leather Armor, Sickle
				Senses: Passive Perception 10
				Languages: Common
				Actions: Ritual Sickle. Melee Attack Roll: +3, reach 5 ft. Hit: 3 (1d4 + 1) Slashing damage plus 1 Necrotic damage.
			"""
		),
		monster(
			name = "Cultist Fanatic",
			subtitle = "Medium or Small Humanoid, Neutral",
			group = "Cultists",
			ac = "13",
			initiative = "+2 (12)",
			hp = "44 (8d8 + 8)",
			speed = "30 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 11 (+0 save +0), DEX 14 (+2 save +2), CON 12 (+1 save +1), INT 10 (+0 save +0), WIS 14 (+2 save +4), CHA 13 (+1 save +1)
				Skills: Deception +3, Persuasion +3, Religion +2
				Gear: Holy Symbol, Leather Armor
				Senses: Passive Perception 12
				Languages: Common
				Actions: Pact Blade. Melee Attack Roll: +4, reach 5 ft. Hit: 6 (1d8 + 2) Slashing damage plus 7 (2d6) Necrotic damage. Spellcasting. The cultist casts one of the following spells, using Wisdom as the spellcasting ability (spell save DC 12, +4 to hit with spell attacks): At Will: Light, Thaumaturgy. 2/Day: Command. 1/Day: Hold Person.
				Bonus Actions: Spiritual Weapon (2/Day). The cultist casts the Spiritual Weapon spell, using the same spellcasting ability as Spellcasting.
			"""
		),
		monster(
			name = "Darkmantle",
			subtitle = "Small Aberration, Unaligned",
			ac = "11",
			initiative = "+3 (13)",
			hp = "22 (5d6 + 5)",
			speed = "10 ft., Fly 30 ft.",
			cr = "1/2 (XP 100; PB +2)",
			body = """
				STR 16 (+3 save +3), DEX 12 (+1 save +1), CON 13 (+1 save +1), INT 2 (-4 save -4), WIS 10 (+0 save +0), CHA 5 (-3 save -3)
				Skills: Stealth +3
				Senses: Blindsight 60 ft.; Passive Perception 10
				Languages: None
				Actions: Crush. Melee Attack Roll: +5, reach 5 ft. Hit: 6 (1d6 + 3) Bludgeoning damage, and the darkmantle attaches to the target. If the target is a Medium or smaller creature and the darkmantle had Advantage on the attack roll, it covers the target, which has the Blinded condition and is suffocating while the darkmantle is attached in this way. While attached to a target, the darkmantle can attack only the target but has Advantage on its attack rolls. Its Speed becomes 0, it can't benefit from any bonus to its Speed, and it moves with the target. A creature can take an action to try to detach the darkmantle from itself, doing so with a successful DC 13 Strength (Athletics) check. On its turn, the darkmantle can detach itself by using 5 feet of movement. Darkness Aura (1/Day). Magical Darkness fills a 15-foot Emanation originating from the darkmantle. This effect lasts while the darkmantle maintains Concentration on it, up to 10 minutes. Darkvision can't penetrate this area, and no light can illuminate it.
			"""
		),
		monster(
			name = "Death Dog",
			subtitle = "Medium Monstrosity, Neutral Evil",
			ac = "12",
			initiative = "+2 (12)",
			hp = "39 (6d8 + 12)",
			speed = "40 ft.",
			cr = "1 (XP 200; PB +2)",
			body = """
				STR 15 (+2 save +2), DEX 14 (+2 save +2), CON 14 (+2 save +2), INT 3 (-4 save -4), WIS 13 (+1 save +1), CHA 6 (-2 save -2)
				Skills: Perception +5, Stealth +4
				Immunities: Blinded, Charmed, Deafened, Frightened, Stunned, Unconscious
				Senses: Darkvision 120 ft.; Passive Perception 15
				Languages: None
				Actions: Multiattack. The death dog makes two Bite attacks. Bite. Melee Attack Roll: +4, reach 5 ft. Hit: 4 (1d4 + 2) Piercing damage. If the target is a creature, it is subjected to the following effect. Constitution Saving Throw: DC 12. First Failure: The target has the Poisoned condition. While Poisoned, the target's Hit Point maximum doesn't return to normal when finishing a Long Rest, and it repeats the save every 24 hours that elapse, ending the effect on itself on a success. Subsequent Failures: The Poisoned target's Hit Point maximum decreases by 5 (1d10).
			"""
		),
		monster(
			name = "Deva",
			subtitle = "Medium Celestial (Angel), Lawful Good",
			ac = "17",
			initiative = "+4 (14)",
			hp = "229 (27d8 + 108)",
			speed = "30 ft., Fly 90 ft. (hover)",
			cr = "10 (XP 5,900; PB +4)",
			body = """
				STR 18 (+4 save +4), DEX 18 (+4 save +4), CON 18 (+4 save +4), INT 17 (+3 save +3), WIS 20 (+5 save +9), CHA 20 (+5 save +9)
				Skills: Insight +9, Perception +9
				Resistances: Radiant
				Immunities: Charmed, Exhaustion, Frightened
				Senses: Darkvision 120 ft.; Passive Perception 19
				Languages: All; telepathy 120 ft.
				Traits: Exalted Restoration. If the deva dies outside Mount Celestia, its body disappears, and it gains a new body instantly, reviving with all its Hit Points somewhere in Mount Celestia. Magic Resistance. The deva has Advantage on saving throws against spells and other magical effects.
				Actions: Multiattack. The deva makes two Holy Mace attacks. Holy Mace. Melee Attack Roll: +8, reach 5 ft. Hit: 7 (1d6 + 4) Bludgeoning damage plus 18 (4d8) Radiant damage. Spellcasting. The deva casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 17): At Will: Detect Evil and Good, Shapechange (Beast or Humanoid form only, no Temporary Hit Points gained from the spell, and no Concentration or Temporary Hit Points required to maintain the spell). 1/Day Each: Commune, Raise Dead.
				Bonus Actions: Divine Aid (2/Day). The deva casts Cure Wounds, Lesser Restoration, or Remove Curse, using the same spellcasting ability as Spellcasting.
			"""
		),
		monster(
			name = "Djinni",
			subtitle = "Large Elemental (Genie), Neutral",
			ac = "17",
			initiative = "+2 (12)",
			hp = "218 (19d10 + 114)",
			speed = "30 ft., Fly 90 ft. (hover)",
			cr = "11 (XP 7,200; PB +4)",
			body = """
				STR 21 (+5 save +5), DEX 15 (+2 save +6), CON 22 (+6 save +6), INT 15 (+2 save +2), WIS 16 (+3 save +7), CHA 20 (+5 save +5)
				Immunities: Lightning, Thunder
				Senses: Darkvision 120 ft.; Passive Perception 13
				Languages: Primordial (Auran)
				Traits: Elemental Restoration. If the djinni dies outside the Elemental Plane of Air, its body dissolves into mist, and it gains a new body in 1d4 days, reviving with all its Hit Points somewhere on the Plane of Air. Magic Resistance. The djinni has Advantage on saving throws against spells and other magical effects. Wishes. The djinni has a 30 percent chance of knowing the Wish spell. If the djinni knows it, the djinni can cast it only on behalf of a non-genie creature who communicates a wish in a way the djinni can understand. If the djinni casts the spell for the creature, the djinni suffers none of the spell's stress. Once the djinni has cast it three times, the djinni can't do so again for 365 days.
				Actions: Multiattack. The djinni makes three attacks, using Storm Blade or Storm Bolt in any combination. Storm Blade. Melee Attack Roll: +9, reach 5 ft. Hit: 12 (2d6 + 5) Slashing damage plus 7 (2d6) Lightning damage. Storm Bolt. Ranged Attack Roll: +9, range 120 ft. Hit: 13 (3d8) Thunder damage. If the target is a Large or smaller creature, it has the Prone condition. Create Whirlwind. The djinni conjures a whirlwind at a point it can see within 120 feet. The whirlwind fills a 20-foot-radius, 60-foot-high Cylinder centered on that point. The whirlwind lasts until the djinni's Concentration on it ends. The djinni can move the whirlwind up to 20 feet at the start of each of its turns. Whenever the whirlwind enters a creature's space or a creature enters the whirlwind, that creature is subjected to the following effect. Strength Saving Throw: DC 17 (a creature makes this save only once per turn, and the djinni is unaffected). Failure: While in the whirlwind, the target has the Restrained condition and moves with the whirlwind. At the start of each of its turns, the Restrained target takes 21 (6d6) Thunder damage. At the end of each of its turns, the target repeats the save, ending the effect on itself on a success. Spellcasting. The djinni casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 17): At Will: Detect Evil and Good, Detect Magic. 2/Day Each: Create Food and Water (can create wine instead of water), Tongues, Wind Walk. 1/Day Each: Creation, Gaseous Form, Invisibility, Major Image, Plane Shift.
			"""
		),
		monster(
			name = "Doppelganger",
			subtitle = "Medium Monstrosity, Neutral",
			ac = "14",
			initiative = "+4 (14)",
			hp = "52 (8d8 + 16)",
			speed = "30 ft.",
			cr = "3 (XP 700; PB +2)",
			body = """
				STR 11 (+0 save +0), DEX 18 (+4 save +4), CON 14 (+2 save +2), INT 11 (+0 save +0), WIS 12 (+1 save +1), CHA 14 (+2 save +2)
				Skills: Deception +6, Insight +3
				Immunities: Charmed
				Senses: Darkvision 60 ft.; Passive Perception 11
				Languages: Common plus three other languages
				Actions: Multiattack. The doppelganger makes two Slam attacks and uses Unsettling Visage if available. Slam. Melee Attack Roll: +6 (with Advantage during the first round of each combat), reach 5 ft. Hit: 11 (2d6 + 4) Bludgeoning damage. Read Thoughts. The doppelganger casts Detect Thoughts, requiring no spell components and using Charisma as the spellcasting ability (spell save DC 12). Unsettling Visage (Recharge 6). Wisdom Saving Throw: DC 12, each creature in a 15-foot Emanation originating from the doppelganger that can see the doppelganger. Failure: The target has the Frightened condition and repeats the save at the end of each of its turns, ending the effect on itself on a success. After 1 minute, it succeeds automatically.
				Bonus Actions: Shape-Shift. The doppelganger shape-shifts into a Medium or Small Humanoid, or it returns to its true form. Its game statistics, other than its size, are the same in each form. Any equipment it is wearing or carrying isn't transformed.
			"""
		),
		monster(
			name = "Dragon Turtle",
			subtitle = "Gargantuan Dragon, Neutral",
			ac = "20",
			initiative = "+6 (16)",
			hp = "356 (23d20 + 115)",
			speed = "20 ft., Swim 50 ft.",
			cr = "17 (XP 18,000; PB +6)",
			body = """
				STR 25 (+7 save +7), DEX 10 (+0 save +0), CON 20 (+5 save +11), INT 10 (+0 save +0), WIS 12 (+1 save +7), CHA 12 (+1 save +1)
				Resistances: Fire
				Senses: Darkvision 120 ft.; Passive Perception 11
				Languages: Draconic, Primordial (Aquan)
				Trait: Amphibious. The dragon can breathe air and water.
				Actions: Multiattack. The dragon makes three Bite attacks. It can replace one attack with a Tail attack. Bite. Melee Attack Roll: +13, reach 15 ft. Hit: 23 (3d10 + 7) Piercing damage plus 7 (2d6) Fire damage. Being underwater doesn't grant Resistance to this Fire damage. Tail. Melee Attack Roll: +13, reach 15 ft. Hit: 18 (2d10 + 7) Bludgeoning damage. If the target is a Huge or smaller creature, it has the Prone condition. Steam Breath (Recharge 5-6). Constitution Saving Throw: DC 19, each creature in a 60-foot Cone. Failure: 56 (16d6) Fire damage. Success: Half damage. Failure or Success: Being underwater doesn't grant Resistance to this Fire damage.
			"""
		),
		monster(
			name = "Dretch",
			subtitle = "Small Fiend (Demon), Chaotic Evil",
			ac = "11",
			initiative = "+0 (10)",
			hp = "18 (4d6 + 4)",
			speed = "20 ft.",
			cr = "1/4 (XP 50; PB +2)",
			body = """
				STR 12 (+1 save +1), DEX 11 (+0 save +0), CON 12 (+1 save +1), INT 5 (-3 save -3), WIS 8 (-1 save -1), CHA 3 (-4 save -4)
				Resistances: Cold, Fire, Lightning
				Immunities: Poison; Poisoned
				Senses: Darkvision 60 ft.; Passive Perception 9
				Languages: Abyssal; telepathy 60 ft. (works only with creatures that understand Abyssal)
				Actions: Rend. Melee Attack Roll: +3, reach 5 ft. Hit: 4 (1d6 + 1) Slashing damage. Fetid Cloud (1/Day). Constitution Saving Throw: DC 11, each creature in a 10-foot Emanation originating from the dretch. Failure: The target has the Poisoned condition until the end of its next turn. While Poisoned, the creature can take either an action or a Bonus Action on its turn, not both, and it can't take Reactions.
			"""
		),
		monster(
			name = "Drider",
			subtitle = "Large Monstrosity, Chaotic Evil",
			ac = "19",
			initiative = "+4 (14)",
			hp = "123 (13d10 + 52)",
			speed = "30 ft., Climb 30 ft.",
			cr = "6 (XP 2,300; PB +3)",
			body = """
				STR 16 (+3 save +3), DEX 19 (+4 save +4), CON 18 (+4 save +4), INT 13 (+1 save +1), WIS 16 (+3 save +3), CHA 12 (+1 save +1)
				Skills: Perception +6, Stealth +10
				Senses: Darkvision 120 ft.; Passive Perception 16
				Languages: Elvish, Undercommon
				Traits: Spider Climb. The drider can climb difficult surfaces, including along ceilings, without needing to make an ability check. Sunlight Sensitivity. While in sunlight, the drider has Disadvantage on ability checks and attack rolls. Web Walker. The drider ignores movement restrictions caused by webs, and the drider knows the location of any other creature in contact with the same web.
				Actions: Multiattack. The drider makes three attacks, using Foreleg or Poison Burst in any combination. Foreleg. Melee Attack Roll: +7, reach 10 ft. Hit: 13 (2d8 + 4) Piercing damage. Poison Burst. Ranged Attack Roll: +6, range 120 ft. Hit: 13 (3d6 + 3) Poison damage.
				Bonus Actions: Magic of the Spider Queen (Recharge 5-6). The drider casts Darkness, Faerie Fire, or Web, requiring no Material components and using Wisdom as the spellcasting ability (spell save DC 14).
			"""
		),
		monster(
			name = "Druid",
			subtitle = "Medium or Small Humanoid (Druid), Neutral",
			ac = "13",
			initiative = "+1 (11)",
			hp = "44 (8d8 + 8)",
			speed = "30 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 10 (+0 save +0), DEX 12 (+1 save +1), CON 13 (+1 save +1), INT 12 (+1 save +1), WIS 16 (+3 save +3), CHA 11 (+0 save +0)
				Skills: Medicine +5, Nature +3, Perception +5
				Gear: Studded Leather Armor
				Senses: Passive Perception 15
				Languages: Common, Druidic, Sylvan
				Actions: Multiattack. The druid makes two attacks, using Vine Staff or Verdant Wisp in any combination. Vine Staff. Melee Attack Roll: +5, reach 5 ft. Hit: 7 (1d8 + 3) Bludgeoning damage plus 2 (1d4) Poison damage. Verdant Wisp. Ranged Attack Roll: +5, range 90 ft. Hit: 10 (3d6) Radiant damage. Spellcasting. The druid casts one of the following spells, using Wisdom as the spellcasting ability (spell save DC 13): At Will: Druidcraft, Speak with Animals. 2/Day Each: Entangle, Thunderwave. 1/Day Each: Animal Messenger, Longstrider, Moonbeam.
			"""
		),
		monster(
			name = "Dryad",
			subtitle = "Medium Fey, Neutral",
			ac = "16",
			initiative = "+1 (11)",
			hp = "22 (5d8)",
			speed = "30 ft.",
			cr = "1 (XP 200; PB +2)",
			body = """
				STR 10 (+0 save +0), DEX 12 (+1 save +1), CON 11 (+0 save +0), INT 14 (+2 save +2), WIS 15 (+2 save +2), CHA 18 (+4 save +4)
				Skills: Perception +4, Stealth +5
				Senses: Darkvision 60 ft.; Passive Perception 14
				Languages: Elvish, Sylvan
				Traits: Magic Resistance. The dryad has Advantage on saving throws against spells and other magical effects. Speak with Beasts and Plants. The dryad can communicate with Beasts and Plants as if they shared a language.
				Actions: Multiattack. The dryad makes one Vine Lash or Thorn Burst attack, and it can use Spellcasting to cast Charm Monster. Vine Lash. Melee Attack Roll: +6, reach 10 ft. Hit: 8 (1d8 + 4) Slashing damage. Thorn Burst. Ranged Attack Roll: +6, range 60 ft. Hit: 7 (1d6 + 4) Piercing damage. Spellcasting. The dryad casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 14): At Will: Animal Friendship, Charm Monster (lasts 24 hours; ends early if the dryad casts the spell again), Druidcraft. 1/Day Each: Entangle, Pass without Trace.
				Bonus Actions: Tree Stride. If within 5 feet of a Large or bigger tree, the dryad teleports to an unoccupied space within 5 feet of a second Large or bigger tree that is within 60 feet of the previous tree.
			"""
		),
		monster(
			name = "Earth Elemental",
			subtitle = "Large Elemental, Neutral",
			ac = "17",
			initiative = "-1 (9)",
			hp = "147 (14d10 + 70)",
			speed = "30 ft., Burrow 30 ft.",
			cr = "5 (XP 1,800; PB +3)",
			body = """
				STR 20 (+5 save +5), DEX 8 (-1 save -1), CON 20 (+5 save +5), INT 5 (-3 save -3), WIS 10 (+0 save +0), CHA 5 (-3 save -3)
				Vulnerabilities: Thunder
				Immunities: Poison; Exhaustion, Paralyzed, Petrified, Poisoned, Unconscious
				Senses: Darkvision 60 ft., Tremorsense 60 ft.; Passive Perception 10
				Languages: Primordial (Terran)
				Traits: Earth Glide. The elemental can burrow through nonmagical, unworked earth and stone. While doing so, the elemental doesn't disturb the material it moves through. Siege Monster. The elemental deals double damage to objects and structures.
				Actions: Multiattack. The elemental makes two attacks, using Slam or Rock Launch in any combination. Slam. Melee Attack Roll: +8, reach 10 ft. Hit: 14 (2d8 + 5) Bludgeoning damage. Rock Launch. Ranged Attack Roll: +8, range 60 ft. Hit: 8 (1d6 + 5) Bludgeoning damage. If the target is a Large or smaller creature, it has the Prone condition.
			"""
		),
		monster(
			name = "Efreeti",
			subtitle = "Large Elemental (Genie), Neutral",
			ac = "17",
			initiative = "+1 (11)",
			hp = "212 (17d10 + 119)",
			speed = "40 ft., Fly 60 ft. (hover)",
			cr = "11 (XP 7,200; PB +4)",
			body = """
				STR 22 (+6 save +6), DEX 12 (+1 save +1), CON 24 (+7 save +7), INT 16 (+3 save +3), WIS 15 (+2 save +6), CHA 19 (+4 save +8)
				Immunities: Fire
				Senses: Darkvision 120 ft.; Passive Perception 12
				Languages: Primordial (Ignan)
				Traits: Elemental Restoration. If the efreeti dies outside the Elemental Plane of Fire, its body dissolves into ash, and it gains a new body in 1d4 days, reviving with all its Hit Points somewhere on the Plane of Fire. Magic Resistance. The efreeti has Advantage on saving throws against spells and other magical effects. Wishes. The efreeti has a 30 percent chance of knowing the Wish spell. If the efreeti knows it, the efreeti can cast it only on behalf of a non-genie creature who communicates a wish in a way the efreeti can understand. If the efreeti casts the spell for the creature, the efreeti suffers none of the spell's stress. Once the efreeti has cast it three times, the efreeti can't do so again for 365 days.
				Actions: Multiattack. The efreeti makes three attacks, using Heated Blade or Hurl Flame in any combination. Heated Blade. Melee Attack Roll: +10, reach 5 ft. Hit: 13 (2d6 + 6) Slashing damage plus 13 (2d12) Fire damage. Hurl Flame. Ranged Attack Roll: +8, range 120 ft. Hit: 24 (7d6) Fire damage. Spellcasting. The efreeti casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 16): At Will: Detect Magic, Elementalism. 1/Day Each: Gaseous Form, Invisibility, Major Image, Plane Shift, Tongues, Wall of Fire (level 7 version).
			"""
		),
		monster(
			name = "Erinyes",
			subtitle = "Medium Fiend (Devil), Lawful Evil",
			ac = "18",
			initiative = "+7 (17)",
			hp = "178 (21d8 + 84)",
			speed = "30 ft., Fly 60 ft.",
			cr = "12 (XP 8,400; PB +4)",
			body = """
				STR 18 (+4 save +4), DEX 16 (+3 save +7), CON 18 (+4 save +8), INT 14 (+2 save +2), WIS 14 (+2 save +2), CHA 18 (+4 save +8)
				Skills: Perception +6, Persuasion +8
				Resistances: Cold
				Immunities: Fire, Poison; Poisoned
				Senses: Truesight 120 ft.; Passive Perception 16
				Languages: Infernal; telepathy 120 ft.
				Traits: Diabolical Restoration. If the erinyes dies outside the Nine Hells, its body disappears in sulfurous smoke, and it gains a new body instantly, reviving with all its Hit Points somewhere in the Nine Hells. Magic Resistance. The erinyes has Advantage on saving throws against spells and other magical effects. Magic Rope. The erinyes has a magic rope. While bearing it, the erinyes can use the Entangling Rope action. The rope has AC 20, HP 90, and Immunity to Poison and Psychic damage. The rope turns to dust if reduced to 0 Hit Points, if it is 5+ feet away from the erinyes for 1 hour or more, or if the erinyes dies. If the rope is damaged or destroyed, the erinyes can fully restore it when finishing a Short or Long Rest.
				Actions: Multiattack. The erinyes makes three Withering Sword attacks and can use Entangling Rope. Withering Sword. Melee Attack Roll: +8, reach 5 ft. Hit: 13 (2d8 + 4) Slashing damage plus 11 (2d10) Necrotic damage. Entangling Rope (Requires Magic Rope). Strength Saving Throw: DC 16, one creature the erinyes can see within 120 feet. Failure: 14 (4d6) Force damage, and the target has the Restrained condition until the rope is destroyed, the erinyes uses a Bonus Action to release the target, or the erinyes uses Entangling Rope again.
				Reactions: Parry. Trigger: The erinyes is hit by a melee attack roll while holding a weapon. Response: The erinyes adds 4 to its AC against that attack, possibly causing it to miss.
			"""
		),
		monster(
			name = "Ettercap",
			subtitle = "Medium Monstrosity, Neutral Evil",
			ac = "13",
			initiative = "+2 (12)",
			hp = "44 (8d8 + 8)",
			speed = "30 ft., Climb 30 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 14 (+2 save +2), DEX 15 (+2 save +2), CON 13 (+1 save +1), INT 7 (-2 save -2), WIS 12 (+1 save +1), CHA 8 (-1 save -1)
				Skills: Perception +3, Stealth +4, Survival +3
				Senses: Darkvision 60 ft.; Passive Perception 13
				Languages: None
				Traits: Spider Climb. The ettercap can climb difficult surfaces, including along ceilings, without needing to make an ability check. Web Walker. The ettercap ignores movement restrictions caused by webs, and the ettercap knows the location of any other creature in contact with the same web.
				Actions: Multiattack. The ettercap makes one Bite attack and one Claw attack. Bite. Melee Attack Roll: +4, reach 5 ft. Hit: 5 (1d6 + 2) Piercing damage plus 2 (1d4) Poison damage, and the target has the Poisoned condition until the start of the ettercap's next turn. Claw. Melee Attack Roll: +4, reach 5 ft. Hit: 7 (2d4 + 2) Slashing damage. Web Strand (Recharge 5-6). Dexterity Saving Throw: DC 12, one Large or smaller creature the ettercap can see within 30 feet. Failure: The target has the Restrained condition until the web is destroyed (AC 10; HP 5; Vulnerability to Fire damage; Immunity to Bludgeoning, Poison, and Psychic damage).
				Bonus Actions: Reel. The ettercap pulls one creature within 30 feet of itself that is Restrained by its Web Strand up to 25 feet straight toward itself.
			"""
		),
		monster(
			name = "Ettin",
			subtitle = "Large Giant, Chaotic Evil",
			ac = "12",
			initiative = "-1 (9)",
			hp = "85 (10d10 + 30)",
			speed = "40 ft.",
			cr = "4 (XP 1,100; PB +2)",
			body = """
				STR 21 (+5 save +5), DEX 8 (-1 save -1), CON 17 (+3 save +3), INT 6 (-2 save -2), WIS 10 (+0 save +0), CHA 8 (-1 save -1)
				Skills: Perception +4
				Immunities: Blinded, Charmed, Deafened, Frightened, Stunned, Unconscious
				Gear: Battleaxe, Morningstar
				Senses: Darkvision 60 ft.; Passive Perception 14
				Languages: Giant
				Actions: Multiattack. The ettin makes one Battleaxe attack and one Morningstar attack. Battleaxe. Melee Attack Roll: +7, reach 5 ft. Hit: 14 (2d8 + 5) Slashing damage. If the target is a Large or smaller creature, it has the Prone condition. Morningstar. Melee Attack Roll: +7, reach 5 ft. Hit: 14 (2d8 + 5) Piercing damage, and the target has Disadvantage on the next attack roll it makes before the end of its next turn.
			"""
		),
		monster(
			name = "Fire Elemental",
			subtitle = "Large Elemental, Neutral",
			ac = "13",
			initiative = "+3 (13)",
			hp = "93 (11d10 + 33)",
			speed = "50 ft.",
			cr = "5 (XP 1,800; PB +3)",
			body = """
				STR 10 (+0 save +0), DEX 17 (+3 save +3), CON 16 (+3 save +3), INT 6 (-2 save -2), WIS 10 (+0 save +0), CHA 7 (-2 save -2)
				Resistances: Bludgeoning, Piercing, Slashing
				Immunities: Fire, Poison; Exhaustion, Grappled, Paralyzed, Petrified, Poisoned, Prone, Restrained, Unconscious
				Senses: Darkvision 60 ft.; Passive Perception 10
				Languages: Primordial (Ignan)
				Traits: Fire Aura. At the end of each of the elemental's turns, each creature in a 10-foot Emanation originating from the elemental takes 5 (1d10) Fire damage. Creatures and flammable objects in the Emanation start burning. Fire Form. The elemental can move through a space as narrow as 1 inch without expending extra movement to do so, and it can enter a creature's space and stop there. The first time it enters a creature's space on a turn, that creature takes 5 (1d10) Fire damage. Illumination. The elemental sheds Bright Light in a 30-foot radius and Dim Light for an additional 30 feet. Water Susceptibility. The elemental takes 3 (1d6) Cold damage for every 5 feet the elemental moves in water or for every gallon of water splashed on it.
				Actions: Multiattack. The elemental makes two Burn attacks. Burn. Melee Attack Roll: +6, reach 5 ft. Hit: 10 (2d6 + 3) Fire damage. If the target is a creature or a flammable object, it starts burning.
			"""
		),
		monster(
			name = "Fire Giant",
			subtitle = "Huge Giant, Lawful Evil",
			ac = "18",
			initiative = "+3 (13)",
			hp = "162 (13d12 + 78)",
			speed = "30 ft.",
			cr = "9 (XP 5,000; PB +4)",
			body = """
				STR 25 (+7 save +7), DEX 9 (-1 save -1), CON 23 (+6 save +10), INT 10 (+0 save +0), WIS 14 (+2 save +2), CHA 13 (+1 save +5)
				Skills: Athletics +11, Perception +6
				Immunities: Fire
				Senses: Passive Perception 16
				Languages: Giant
				Actions: Multiattack. The giant makes two attacks, using Flame Sword or Hammer Throw in any combination. Flame Sword. Melee Attack Roll: +11, reach 10 ft. Hit: 21 (4d6 + 7) Slashing damage plus 10 (3d6) Fire damage. Hammer Throw. Ranged Attack Roll: +11, range 60/240 ft. Hit: 23 (3d10 + 7) Bludgeoning damage plus 4 (1d8) Fire damage, and the target is pushed up to 15 feet straight away from the giant and has Disadvantage on the next attack roll it makes before the end of its next turn.
			"""
		),
		monster(
			name = "Flesh Golem",
			subtitle = "Medium Construct, Neutral",
			ac = "9",
			initiative = "-1 (9)",
			hp = "127 (15d8 + 60)",
			speed = "30 ft.",
			cr = "5 (XP 1,800; PB +3)",
			body = """
				STR 19 (+4 save +4), DEX 9 (-1 save -1), CON 18 (+4 save +4), INT 6 (-2 save -2), WIS 10 (+0 save +0), CHA 5 (-3 save -3)
				Immunities: Lightning, Poison; Charmed, Exhaustion, Frightened, Paralyzed, Petrified, Poisoned
				Senses: Darkvision 60 ft.; Passive Perception 10
				Languages: Understands Common plus one other language but can't speak
				Traits: Aversion to Fire. If the golem takes Fire damage, it has Disadvantage on attack rolls and ability checks until the end of its next turn. Berserk. Whenever the golem starts its turn Bloodied, roll 1d6. On a 6, the golem goes berserk. On each of its turns while berserk, the golem attacks the nearest creature it can see. If no creature is near enough to move to and attack, the golem attacks an object. Once the golem goes berserk, it remains so until it is destroyed or it is no longer Bloodied. The golem's creator, if within 60 feet of the berserk golem, can try to calm it by taking an action to make a DC 15 Charisma (Persuasion) check; the golem must be able to hear its creator. If this check succeeds, the golem ceases being berserk until the start of its next turn, at which point it resumes rolling for the Berserk trait again if it is still Bloodied. Immutable Form. The golem can't shape-shift. Lightning Absorption. Whenever the golem is subjected to Lightning damage, it regains a number of Hit Points equal to the Lightning damage dealt. Magic Resistance. The golem has Advantage on saving throws against spells and other magical effects.
				Actions: Multiattack. The golem makes two Slam attacks. Slam. Melee Attack Roll: +7, reach 5 ft. Hit: 13 (2d8 + 4) Bludgeoning damage plus 4 (1d8) Lightning damage.
			"""
		),
		monster(
			name = "Frost Giant",
			subtitle = "Huge Giant, Neutral Evil",
			ac = "15",
			initiative = "+2 (12)",
			hp = "149 (13d12 + 65)",
			speed = "40 ft.",
			cr = "8 (XP 3,900; PB +3)",
			body = """
				STR 23 (+6 save +6), DEX 9 (-1 save -1), CON 21 (+5 save +8), INT 9 (-1 save -1), WIS 10 (+0 save +3), CHA 12 (+1 save +4)
				Skills: Athletics +9, Perception +3
				Immunities: Cold
				Senses: Passive Perception 13
				Languages: Giant
				Actions: Multiattack. The giant makes two attacks, using Frost Axe or Great Bow in any combination. Frost Axe. Melee Attack Roll: +9, reach 10 ft. Hit: 19 (2d12 + 6) Slashing damage plus 9 (2d8) Cold damage. Great Bow. Ranged Attack Roll: +9, range 150/600 ft. Hit: 17 (2d10 + 6) Piercing damage plus 7 (2d6) Cold damage, and the target's Speed decreases by 10 feet until the end of its next turn.
				Bonus Actions: War Cry (Recharge 5-6). The giant or one creature of its choice that can see or hear it gains 16 (2d10 + 5) Temporary Hit Points and has Advantage on attack rolls until the start of the giant's next turn.
			"""
		),
		monster(
			name = "Shrieker Fungus",
			subtitle = "Medium Plant, Unaligned",
			group = "Fungi",
			ac = "5",
			initiative = "-5 (5)",
			hp = "13 (3d8)",
			speed = "5 ft.",
			cr = "0 (XP 0; PB +2)",
			body = """
				STR 1 (-5 save -5), DEX 1 (-5 save -5), CON 10 (+0 save +0), INT 1 (-5 save -5), WIS 3 (-4 save -4), CHA 1 (-5 save -5)
				Immunities: Blinded, Charmed, Deafened, Frightened
				Senses: Blindsight 30 ft.; Passive Perception 6
				Languages: None
				Reactions: Shriek. Trigger: A creature or a source of Bright Light moves within 30 feet of the shrieker. Response: The shrieker emits a shriek audible within 300 feet of itself for 1 minute or until the shrieker dies.
			"""
		),
		monster(
			name = "Violet Fungus",
			subtitle = "Medium Plant, Unaligned",
			group = "Fungi",
			ac = "5",
			initiative = "-5 (5)",
			hp = "18 (4d8)",
			speed = "5 ft.",
			cr = "1/4 (XP 50; PB +2)",
			body = """
				STR 3 (-4 save -4), DEX 1 (-5 save -5), CON 10 (+0 save +0), INT 1 (-5 save -5), WIS 3 (-4 save -4), CHA 1 (-5 save -5)
				Immunities: Blinded, Charmed, Deafened, Frightened
				Senses: Blindsight 30 ft.; Passive Perception 6
				Languages: None
				Actions: Multiattack. The fungus makes two Rotting Touch attacks. Rotting Touch. Melee Attack Roll: +2, reach 10 ft. Hit: 4 (1d8) Necrotic damage.
			"""
		)
	)

	val ENTRIES: List<MonsterReferenceEntry> = entriesDtoF + MonsterReferenceDataG.ENTRIES
}

private fun monster(
	name: String,
	subtitle: String,
	ac: String,
	initiative: String,
	hp: String,
	speed: String,
	cr: String,
	body: String,
	group: String? = null
): MonsterReferenceEntry {
	return buildMonsterEntry(
		name = name,
		subtitle = subtitle,
		ac = ac,
		initiative = initiative,
		hp = hp,
		speed = speed,
		cr = cr,
		body = body,
		group = group
	)
}
