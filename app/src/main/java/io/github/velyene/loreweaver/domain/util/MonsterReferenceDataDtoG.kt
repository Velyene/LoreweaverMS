package io.github.velyene.loreweaver.domain.util

object MonsterReferenceDataDtoG {
	val ENTRIES: List<MonsterReferenceEntry> = listOf(
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
				STR 25 (+7 save +7), DEX 9 (-1 save +3), CON 23 (+6 save +10), INT 10 (+0 save +0), WIS 14 (+2 save +2), CHA 13 (+1 save +5)
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
		),
		monster(
			name = "Gargoyle",
			subtitle = "Medium Elemental, Chaotic Evil",
			ac = "15",
			initiative = "+2 (12)",
			hp = "67 (9d8 + 27)",
			speed = "30 ft., Fly 60 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 15 (+2 save +2), DEX 11 (+0 save +0), CON 16 (+3 save +3), INT 6 (-2 save -2), WIS 11 (+0 save +0), CHA 7 (-2 save -2)
				Skills: Stealth +4
				Immunities: Poison; Exhaustion, Petrified, Poisoned
				Senses: Darkvision 60 ft.; Passive Perception 10
				Languages: Primordial (Terran)
				Trait: Flyby. The gargoyle doesn't provoke an Opportunity Attack when it flies out of an enemy's reach.
				Actions: Multiattack. The gargoyle makes two Claw attacks. Claw. Melee Attack Roll: +4, reach 5 ft. Hit: 7 (2d4 + 2) Slashing damage.
			"""
		),
		monster(
			name = "Gelatinous Cube",
			subtitle = "Large Ooze, Unaligned",
			ac = "6",
			initiative = "-4 (6)",
			hp = "63 (6d10 + 30)",
			speed = "15 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 14 (+2 save +2), DEX 3 (-4 save -4), CON 20 (+5 save +5), INT 1 (-5 save -5), WIS 6 (-2 save -2), CHA 1 (-5 save -5)
				Immunities: Acid; Blinded, Charmed, Deafened, Exhaustion, Frightened, Prone
				Senses: Blindsight 60 ft.; Passive Perception 8
				Languages: None
				Traits: Ooze Cube. The cube fills its entire space and is transparent. Other creatures can enter that space, but a creature that does so is subjected to the cube's Engulf and has Disadvantage on the saving throw. Creatures inside the cube have Total Cover, and the cube can hold one Large creature or up to four Medium or Small creatures inside itself at a time. As an action, a creature within 5 feet of the cube can pull a creature or an object out of the cube by succeeding on a DC 12 Strength (Athletics) check, and the puller takes 10 (3d6) Acid damage. Transparent. Even when the cube is in plain sight, a creature must succeed on a DC 15 Wisdom (Perception) check to notice the cube if the creature hasn't witnessed the cube move or otherwise act.
				Actions: Pseudopod. Melee Attack Roll: +4, reach 5 ft. Hit: 12 (3d6 + 2) Acid damage. Engulf. The cube moves up to its Speed without provoking Opportunity Attacks. The cube can move through the spaces of Large or smaller creatures if it has room inside itself to contain them. Dexterity Saving Throw: DC 12, each creature whose space the cube enters for the first time during this move. Failure: 10 (3d6) Acid damage, and the target is engulfed. An engulfed target is suffocating, can't cast spells with a Verbal component, has the Restrained condition, and takes 10 (3d6) Acid damage at the start of each of the cube's turns. When the cube moves, the engulfed target moves with it. An engulfed target can try to escape by taking an action to make a DC 12 Strength (Athletics) check. On a successful check, the target escapes and enters the nearest unoccupied space. Success: Half damage, and the target moves to an unoccupied space within 5 feet of the cube. If there is no unoccupied space, the target fails the save instead.
			"""
		),
		monster(
			name = "Ghast",
			subtitle = "Medium Undead, Chaotic Evil",
			ac = "13",
			initiative = "+3 (13)",
			hp = "36 (8d8)",
			speed = "30 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 16 (+3 save +3), DEX 17 (+3 save +3), CON 10 (+0 save +0), INT 11 (+0 save +0), WIS 10 (+0 save +2), CHA 8 (-1 save -1)
				Resistances: Necrotic
				Immunities: Poison; Charmed, Exhaustion, Poisoned
				Senses: Darkvision 60 ft.; Passive Perception 10
				Languages: Common
				Trait: Stench. Constitution Saving Throw: DC 10, any creature that starts its turn in a 5-foot Emanation originating from the ghast. Failure: The target has the Poisoned condition until the start of its next turn. Success: The target is immune to this ghast's Stench for 24 hours.
				Actions: Bite. Melee Attack Roll: +5, reach 5 ft. Hit: 7 (1d8 + 3) Piercing damage plus 9 (2d8) Necrotic damage. Claw. Melee Attack Roll: +5, reach 5 ft. Hit: 10 (2d6 + 3) Slashing damage. If the target is a non-Undead creature, it is subjected to the following effect. Constitution Saving Throw: DC 10. Failure: The target has the Paralyzed condition until the end of its next turn.
			"""
		),
		monster(
			name = "Ghost",
			subtitle = "Medium Undead, Neutral",
			ac = "11",
			initiative = "+1 (11)",
			hp = "45 (10d8)",
			speed = "5 ft., Fly 40 ft. (hover)",
			cr = "4 (XP 1,100; PB +2)",
			body = """
				STR 7 (-2 save -2), DEX 13 (+1 save +1), CON 10 (+0 save +0), INT 10 (+0 save +0), WIS 12 (+1 save +1), CHA 17 (+3 save +3)
				Resistances: Acid, Bludgeoning, Cold, Fire, Lightning, Piercing, Slashing, Thunder
				Immunities: Necrotic, Poison; Charmed, Exhaustion, Frightened, Grappled, Paralyzed, Petrified, Poisoned, Prone, Restrained
				Senses: Darkvision 60 ft.; Passive Perception 11
				Languages: Common plus one other language
				Traits: Ethereal Sight. The ghost can see 60 feet into the Ethereal Plane when it is on the Material Plane. Incorporeal Movement. The ghost can move through other creatures and objects as if they were Difficult Terrain. It takes 5 (1d10) Force damage if it ends its turn inside an object.
				Actions: Multiattack. The ghost makes two Withering Touch attacks. Withering Touch. Melee Attack Roll: +5, reach 5 ft. Hit: 19 (3d10 + 3) Necrotic damage. Etherealness. The ghost casts the Etherealness spell, requiring no spell components and using Charisma as the spellcasting ability. The ghost is visible on the Material Plane while on the Border Ethereal and vice versa, but it can't affect or be affected by anything on the other plane. Horrific Visage. Wisdom Saving Throw: DC 13, each creature in a 60-foot Cone that can see the ghost and isn't an Undead. Failure: 10 (2d6 + 3) Psychic damage, and the target has the Frightened condition until the start of the ghost's next turn. Success: The target is immune to this ghost's Horrific Visage for 24 hours. Possession (Recharge 6). Charisma Saving Throw: DC 13, one Humanoid the ghost can see within 5 feet. Failure: The target is possessed by the ghost; the ghost disappears, and the target has the Incapacitated condition and loses control of its body. The ghost now controls the body, but the target retains awareness. The ghost can't be targeted by any attack, spell, or other effect, except ones that specifically target Undead. The ghost's game statistics are the same, except it uses the possessed target's Speed, as well as the target's Strength, Dexterity, and Constitution modifiers. The possession lasts until the body drops to 0 Hit Points or the ghost leaves as a Bonus Action. When the possession ends, the ghost appears in an unoccupied space within 5 feet of the target, and the target is immune to this ghost's Possession for 24 hours. Success: The target is immune to this ghost's Possession for 24 hours.
			"""
		),
		monster(
			name = "Ghoul",
			subtitle = "Medium Undead, Chaotic Evil",
			ac = "12",
			initiative = "+2 (12)",
			hp = "22 (5d8)",
			speed = "30 ft.",
			cr = "1 (XP 200; PB +2)",
			body = """
				STR 13 (+1 save +1), DEX 15 (+2 save +2), CON 10 (+0 save +0), INT 7 (-2 save -2), WIS 10 (+0 save +0), CHA 6 (-2 save -2)
				Immunities: Poison; Charmed, Exhaustion, Poisoned
				Senses: Darkvision 60 ft.; Passive Perception 10
				Languages: Common
				Actions: Multiattack. The ghoul makes two Bite attacks. Bite. Melee Attack Roll: +4, reach 5 ft. Hit: 5 (1d6 + 2) Piercing damage plus 3 (1d6) Necrotic damage. Claw. Melee Attack Roll: +4, reach 5 ft. Hit: 4 (1d4 + 2) Slashing damage. If the target is a creature that isn't an Undead or elf, it is subjected to the following effect. Constitution Saving Throw: DC 10. Failure: The target has the Paralyzed condition until the end of its next turn.
			"""
		),
		monster(
			name = "Gibbering Mouther",
			subtitle = "Medium Aberration, Chaotic Neutral",
			ac = "9",
			initiative = "-1 (9)",
			hp = "52 (7d8 + 21)",
			speed = "20 ft., Swim 20 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 10 (+0 save +0), DEX 8 (-1 save -1), CON 16 (+3 save +3), INT 3 (-4 save -4), WIS 10 (+0 save +0), CHA 6 (-2 save -2)
				Immunities: Prone
				Senses: Darkvision 60 ft.; Passive Perception 10
				Languages: None
				Traits: Aberrant Ground. The ground in a 10-foot Emanation originating from the mouther is Difficult Terrain. Gibbering. The mouther babbles incoherently while it doesn't have the Incapacitated condition. Wisdom Saving Throw: DC 10, any creature that starts its turn within 20 feet of the mouther while it is babbling. Failure: The target rolls 1d8 to determine what it does during the current turn: 1-4, the target does nothing. 5-6, the target takes no action or Bonus Action and uses all its movement to move in a random direction. 7-8, the target makes a melee attack against a randomly determined creature within its reach or does nothing if it can't make such an attack.
				Actions: Bite. Melee Attack Roll: +2, reach 5 ft. Hit: 7 (2d6) Piercing damage. If the target is a Medium or smaller creature, it has the Prone condition. The target dies if it is reduced to 0 Hit Points by this attack. Its body is then absorbed into the mouther, leaving only equipment behind. Blinding Spittle (Recharge 5-6). Dexterity Saving Throw: DC 10, each creature in a 10-foot-radius Sphere centered on a point within 30 feet. Failure: 7 (2d6) Radiant damage, and the target has the Blinded condition until the end of the mouther's next turn.
			"""
		),
		monster(
			name = "Glabrezu",
			subtitle = "Large Fiend (Demon), Chaotic Evil",
			ac = "17",
			initiative = "+6 (16)",
			hp = "189 (18d10 + 90)",
			speed = "40 ft.",
			cr = "9 (XP 5,000; PB +4)",
			body = """
				STR 20 (+5 save +9), DEX 15 (+2 save +2), CON 21 (+5 save +9), INT 19 (+4 save +4), WIS 17 (+3 save +7), CHA 16 (+3 save +7)
				Skills: Deception +7, Perception +7
				Resistances: Cold, Fire, Lightning
				Immunities: Poison; Poisoned
				Senses: Truesight 120 ft.; Passive Perception 17
				Languages: Abyssal; telepathy 120 ft.
				Traits: Demonic Restoration. If the glabrezu dies outside the Abyss, its body dissolves into ichor, and it gains a new body instantly, reviving with all its Hit Points somewhere in the Abyss. Magic Resistance. The glabrezu has Advantage on saving throws against spells and other magical effects.
				Actions: Multiattack. The glabrezu makes two Pincer attacks and uses Pummel or Spellcasting. Pincer. Melee Attack Roll: +9, reach 10 ft. Hit: 16 (2d10 + 5) Slashing damage. If the target is a Medium or smaller creature, it has the Grappled condition (escape DC 15) from one of two pincers. Pummel. Dexterity Saving Throw: DC 17, one creature Grappled by the glabrezu. Failure: 15 (3d6 + 5) Bludgeoning damage. Success: Half damage. Spellcasting. The glabrezu casts one of the following spells, requiring no Material components and using Intelligence as the spellcasting ability (spell save DC 16): At Will: Darkness, Detect Magic, Dispel Magic. 1/Day Each: Confusion, Fly, Power Word Stun.
			"""
		),
		monster(
			name = "Gladiator",
			subtitle = "Medium or Small Humanoid, Neutral",
			ac = "16",
			initiative = "+5 (15)",
			hp = "112 (15d8 + 45)",
			speed = "30 ft.",
			cr = "5 (XP 1,800; PB +3)",
			body = """
				STR 18 (+4 save +7), DEX 15 (+2 save +5), CON 16 (+3 save +6), INT 10 (+0 save +0), WIS 12 (+1 save +4), CHA 15 (+2 save +2)
				Skills: Athletics +10, Performance +5
				Gear: Shield, Spears (3), Studded Leather Armor
				Senses: Passive Perception 11
				Languages: Common
				Actions: Multiattack. The gladiator makes three Spear attacks. It can replace one attack with a use of Shield Bash. Spear. Melee or Ranged Attack Roll: +7, reach 5 ft. or range 20/60 ft. Hit: 11 (2d6 + 4) Piercing damage. Shield Bash. Strength Saving Throw: DC 15, one creature within 5 feet that the gladiator can see. Failure: 9 (2d4 + 4) Bludgeoning damage. If the target is a Medium or smaller creature, it has the Prone condition.
				Reactions: Parry. Trigger: The gladiator is hit by a melee attack roll while holding a weapon. Response: The gladiator adds 3 to its AC against that attack, possibly causing it to miss.
			"""
		),
		monster(
			name = "Gnoll Warrior",
			subtitle = "Medium Fiend, Chaotic Evil",
			group = "Gnoll",
			ac = "15",
			initiative = "+1 (11)",
			hp = "27 (6d8)",
			speed = "30 ft.",
			cr = "1/2 (XP 100; PB +2)",
			body = """
				STR 14 (+2 save +2), DEX 12 (+1 save +1), CON 11 (+0 save +0), INT 6 (-2 save -2), WIS 10 (+0 save +0), CHA 7 (-2 save -2)
				Senses: Darkvision 60 ft.; Passive Perception 10
				Languages: Gnoll
				Actions: Rend. Melee Attack Roll: +4, reach 5 ft. Hit: 5 (1d6 + 2) Piercing damage. Bone Bow. Ranged Attack Roll: +3, range 150/600 ft. Hit: 6 (1d10 + 1) Piercing damage.
				Bonus Actions: Rampage (1/Day). Immediately after dealing damage to a creature that is already Bloodied, the gnoll moves up to half its Speed, and it makes one Rend attack.
			"""
		),
		monster(
			name = "Goblin Minion",
			subtitle = "Small Fey (Goblinoid), Chaotic Neutral",
			group = "Goblins",
			ac = "12",
			initiative = "+2 (12)",
			hp = "7 (2d6)",
			speed = "30 ft.",
			cr = "1/8 (XP 25; PB +2)",
			body = """
				STR 8 (-1 save -1), DEX 15 (+2 save +2), CON 10 (+0 save +0), INT 10 (+0 save +0), WIS 8 (-1 save -1), CHA 8 (-1 save -1)
				Skills: Stealth +6
				Gear: Daggers (3)
				Senses: Darkvision 60 ft.; Passive Perception 9
				Languages: Common, Goblin
				Actions: Dagger. Melee or Ranged Attack Roll: +4, reach 5 ft. or range 20/60 ft. Hit: 4 (1d4 + 2) Piercing damage.
				Bonus Actions: Nimble Escape. The goblin takes the Disengage or Hide action.
			"""
		),
		monster(
			name = "Goblin Warrior",
			subtitle = "Small Fey (Goblinoid), Chaotic Neutral",
			group = "Goblins",
			ac = "15",
			initiative = "+2 (12)",
			hp = "10 (3d6)",
			speed = "30 ft.",
			cr = "1/4 (XP 50; PB +2)",
			body = """
				STR 8 (-1 save -1), DEX 15 (+2 save +2), CON 10 (+0 save +0), INT 10 (+0 save +0), WIS 8 (-1 save -1), CHA 8 (-1 save -1)
				Skills: Stealth +6
				Gear: Leather Armor, Scimitar, Shield, Shortbow
				Senses: Darkvision 60 ft.; Passive Perception 9
				Languages: Common, Goblin
				Actions: Scimitar. Melee Attack Roll: +4, reach 5 ft. Hit: 5 (1d6 + 2) Slashing damage, plus 2 (1d4) Slashing damage if the attack roll had Advantage. Shortbow. Ranged Attack Roll: +4, range 80/320 ft. Hit: 5 (1d6 + 2) Piercing damage, plus 2 (1d4) Piercing damage if the attack roll had Advantage.
				Bonus Actions: Nimble Escape. The goblin takes the Disengage or Hide action.
			"""
		),
		monster(
			name = "Goblin Boss",
			subtitle = "Small Fey (Goblinoid), Chaotic Neutral",
			group = "Goblins",
			ac = "17",
			initiative = "+2 (12)",
			hp = "21 (6d6)",
			speed = "30 ft.",
			cr = "1 (XP 200; PB +2)",
			body = """
				STR 10 (+0 save +0), DEX 15 (+2 save +2), CON 10 (+0 save +0), INT 10 (+0 save +0), WIS 8 (-1 save -1), CHA 10 (+0 save +0)
				Skills: Stealth +6
				Gear: Chain Shirt, Scimitar, Shield, Shortbow
				Senses: Darkvision 60 ft.; Passive Perception 9
				Languages: Common, Goblin
				Actions: Multiattack. The goblin makes two attacks, using Scimitar or Shortbow in any combination. Scimitar. Melee Attack Roll: +4, reach 5 ft. Hit: 5 (1d6 + 2) Slashing damage, plus 2 (1d4) Slashing damage if the attack roll had Advantage. Shortbow. Ranged Attack Roll: +4, range 80/320 ft. Hit: 5 (1d6 + 2) Piercing damage, plus 2 (1d4) Piercing damage if the attack roll had Advantage.
				Bonus Actions: Nimble Escape. The goblin takes the Disengage or Hide action.
				Reactions: Redirect Attack. Trigger: A creature the goblin can see makes an attack roll against it. Response: The goblin chooses a Small or Medium ally within 5 feet of itself. The goblin and that ally swap places, and the ally becomes the target of the attack instead.
			"""
		),
		monster(
			name = "Gold Dragon Wyrmling",
			subtitle = "Medium Dragon (Metallic), Lawful Good",
			group = "Gold Dragons",
			ac = "17",
			initiative = "+4 (14)",
			hp = "60 (8d8 + 24)",
			speed = "30 ft., Fly 60 ft., Swim 30 ft.",
			cr = "3 (700 XP; PB +2)",
			body = """
				STR 19 (+4 save +4), DEX 14 (+2 save +4), CON 17 (+3 save +3), INT 14 (+2 save +2), WIS 11 (+0 save +2), CHA 16 (+3 save +3)
				Skills: Perception +4, Stealth +4
				Immunities: Fire
				Senses: Blindsight 10 ft., Darkvision 60 ft.; Passive Perception 14
				Languages: Draconic
				Trait: Amphibious. The dragon can breathe air and water.
				Actions: Multiattack. The dragon makes two Rend attacks. Rend. Melee Attack Roll: +6, reach 5 ft. Hit: 9 (1d10 + 4) Slashing damage. Fire Breath (Recharge 5-6). Dexterity Saving Throw: DC 13, each creature in a 15-foot Cone. Failure: 22 (4d10) Fire damage. Success: Half damage. Weakening Breath. Strength Saving Throw: DC 13, each creature that isn't currently affected by this breath in a 15-foot Cone. Failure: The target has Disadvantage on Strength-based d20 Tests and subtracts 2 (1d4) from its damage rolls. It repeats the save at the end of each of its turns, ending the effect on itself on a success. After 1 minute, it succeeds automatically.
			"""
		),
		monster(
			name = "Young Gold Dragon",
			subtitle = "Large Dragon (Metallic), Lawful Good",
			group = "Gold Dragons",
			ac = "18",
			initiative = "+6 (16)",
			hp = "178 (17d10 + 85)",
			speed = "40 ft., Fly 80 ft., Swim 40 ft.",
			cr = "10 (XP 5,900; PB +4)",
			body = """
				STR 23 (+6 save +6), DEX 14 (+2 save +6), CON 21 (+5 save +5), INT 16 (+3 save +3), WIS 13 (+1 save +5), CHA 20 (+5 save +5)
				Skills: Insight +5, Perception +9, Persuasion +9, Stealth +6
				Immunities: Fire
				Senses: Blindsight 30 ft., Darkvision 120 ft.; Passive Perception 19
				Languages: Common, Draconic
				Trait: Amphibious. The dragon can breathe air and water.
				Actions: Multiattack. The dragon makes three Rend attacks. It can replace one attack with a use of Weakening Breath. Rend. Melee Attack Roll: +10, reach 10 ft. Hit: 17 (2d10 + 6) Slashing damage. Fire Breath (Recharge 5-6). Dexterity Saving Throw: DC 17, each creature in a 30-foot Cone. Failure: 55 (10d10) Fire damage. Success: Half damage. Weakening Breath. Strength Saving Throw: DC 17, each creature that isn't currently affected by this breath in a 30-foot Cone. Failure: The target has Disadvantage on Strength-based d20 Tests and subtracts 3 (1d6) from its damage rolls. It repeats the save at the end of each of its turns, ending the effect on itself on a success. After 1 minute, it succeeds automatically.
			"""
		),
		monster(
			name = "Adult Gold Dragon",
			subtitle = "Huge Dragon (Metallic), Lawful Good",
			group = "Gold Dragons",
			ac = "19",
			initiative = "+14 (24)",
			hp = "243 (18d12 + 126)",
			speed = "40 ft., Fly 80 ft., Swim 40 ft.",
			cr = "17 (XP 18,000, or 20,000 in lair; PB +6)",
			body = """
				STR 27 (+8 save +8), DEX 14 (+2 save +8), CON 25 (+7 save +7), INT 16 (+3 save +3), WIS 15 (+2 save +8), CHA 24 (+7 save +7)
				Skills: Insight +8, Perception +14, Persuasion +13, Stealth +8
				Immunities: Fire
				Senses: Blindsight 60 ft., Darkvision 120 ft.; Passive Perception 24
				Languages: Common, Draconic
				Traits: Amphibious. The dragon can breathe air and water. Legendary Resistance (3/Day, or 4/Day in Lair). If the dragon fails a saving throw, it can choose to succeed instead.
				Actions: Multiattack. The dragon makes three Rend attacks. It can replace one attack with a use of (A) Spellcasting to cast Guiding Bolt (level 2 version) or (B) Weakening Breath. Rend. Melee Attack Roll: +14, reach 10 ft. Hit: 17 (2d8 + 8) Slashing damage plus 4 (1d8) Fire damage. Fire Breath (Recharge 5-6). Dexterity Saving Throw: DC 21, each creature in a 60-foot Cone. Failure: 66 (12d10) Fire damage. Success: Half damage. Spellcasting. The dragon casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 21, +13 to hit with spell attacks): At Will: Detect Magic, Guiding Bolt (level 2 version), Shapechange (Beast or Humanoid form only, no Temporary Hit Points gained from the spell, and no Concentration or Temporary Hit Points required to maintain the spell). 1/Day Each: Flame Strike, Zone of Truth. Weakening Breath. Strength Saving Throw: DC 21, each creature that isn't currently affected by this breath in a 60-foot Cone. Failure: The target has Disadvantage on Strength-based d20 Tests and subtracts 3 (1d6) from its damage rolls. It repeats the save at the end of each of its turns, ending the effect on itself on a success. After 1 minute, it succeeds automatically.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the dragon can expend a use to take one of the following actions. The dragon regains all expended uses at the start of each of its turns. Banish. Charisma Saving Throw: DC 21, one creature the dragon can see within 120 feet. Failure: 10 (3d6) Force damage, and the target has the Incapacitated condition and is transported to a harmless demiplane until the start of the dragon's next turn, at which point it reappears in an unoccupied space of the dragon's choice within 120 feet of the dragon. Failure or Success: The dragon can't take this action again until the start of its next turn. Guiding Light. The dragon uses Spellcasting to cast Guiding Bolt (level 2 version). Pounce. The dragon moves up to half its Speed, and it makes one Rend attack.
			"""
		),
		monster(
			name = "Ancient Gold Dragon",
			subtitle = "Gargantuan Dragon (Metallic), Lawful Good",
			group = "Gold Dragons",
			ac = "22",
			initiative = "+16 (26)",
			hp = "546 (28d20 + 252)",
			speed = "40 ft., Fly 80 ft., Swim 40 ft.",
			cr = "24 (XP 62,000, or 75,000 in lair; PB +7)",
			body = """
				STR 30 (+10 save +10), DEX 14 (+2 save +9), CON 29 (+9 save +9), INT 18 (+4 save +4), WIS 17 (+3 save +10), CHA 28 (+9 save +9)
				Skills: Insight +10, Perception +17, Persuasion +16, Stealth +9
				Immunities: Fire
				Senses: Blindsight 60 ft., Darkvision 120 ft.; Passive Perception 27
				Languages: Common, Draconic
				Traits: Amphibious. The dragon can breathe air and water. Legendary Resistance (4/Day, or 5/Day in Lair). If the dragon fails a saving throw, it can choose to succeed instead.
				Actions: Multiattack. The dragon makes three Rend attacks. It can replace one attack with a use of (A) Spellcasting to cast Guiding Bolt (level 4 version) or (B) Weakening Breath. Rend. Melee Attack Roll: +17 to hit, reach 15 ft. Hit: 19 (2d8 + 10) Slashing damage plus 9 (2d8) Fire damage. Fire Breath (Recharge 5-6). Dexterity Saving Throw: DC 24, each creature in a 90-foot Cone. Failure: 71 (13d10) Fire damage. Success: Half damage. Spellcasting. The dragon casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 24, +16 to hit with spell attacks): At Will: Detect Magic, Guiding Bolt (level 4 version), Shapechange (Beast or Humanoid form only, no Temporary Hit Points gained from the spell, and no Concentration or Temporary Hit Points required to maintain the spell). 1/Day Each: Flame Strike (level 6 version), Word of Recall, Zone of Truth. Weakening Breath. Strength Saving Throw: DC 24, each creature that isn't currently affected by this breath in a 90-foot Cone. Failure: The target has Disadvantage on Strength-based d20 Tests and subtracts 5 (1d10) from its damage rolls. It repeats the save at the end of each of its turns, ending the effect on itself on a success. After 1 minute, it succeeds automatically.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the dragon can expend a use to take one of the following actions. The dragon regains all expended uses at the start of each of its turns. Banish. Charisma Saving Throw: DC 24, one creature the dragon can see within 120 feet. Failure: 24 (7d6) Force damage, and the target has the Incapacitated condition and is transported to a harmless demiplane until the start of the dragon's next turn, at which point it reappears in an unoccupied space of the dragon's choice within 120 feet of the dragon. Failure or Success: The dragon can't take this action again until the start of its next turn. Guiding Light. The dragon uses Spellcasting to cast Guiding Bolt (level 4 version). Pounce. The dragon moves up to half its Speed, and it makes one Rend attack.
			"""
		),
		monster(
			name = "Gorgon",
			subtitle = "Large Construct, Unaligned",
			ac = "19",
			initiative = "+0 (10)",
			hp = "114 (12d10 + 48)",
			speed = "40 ft.",
			cr = "5 (XP 1,800; PB +3)",
			body = """
				STR 20 (+5 save +5), DEX 11 (+0 save +0), CON 18 (+4 save +4), INT 2 (-4 save -4), WIS 12 (+1 save +1), CHA 7 (-2 save -2)
				Skills: Perception +7
				Immunities: Exhaustion, Petrified
				Senses: Darkvision 60 ft.; Passive Perception 17
				Languages: None
				Actions: Gore. Melee Attack Roll: +8, reach 5 ft. Hit: 18 (2d12 + 5) Piercing damage. If the target is a Large or smaller creature and the gorgon moved 20+ feet straight toward it immediately before the hit, the target has the Prone condition. Petrifying Breath (Recharge 5-6). Constitution Saving Throw: DC 15, each creature in a 30-foot Cone. First Failure: The target has the Restrained condition and repeats the save at the end of its next turn if it is still Restrained, ending the effect on itself on a success. Second Failure: The target has the Petrified condition instead of the Restrained condition.
				Bonus Actions: Trample. Dexterity Saving Throw: DC 16, one creature within 5 feet that has the Prone condition. Failure: 16 (2d10 + 5) Bludgeoning damage. Success: Half damage.
			"""
		),
		monster(
			name = "Gray Ooze",
			subtitle = "Medium Ooze, Unaligned",
			ac = "9",
			initiative = "-2 (13)",
			hp = "22 (3d8 + 9)",
			speed = "10 ft., Climb 10 ft.",
			cr = "1/2 (XP 100; PB +2)",
			body = """
				STR 12 (+1 save +1), DEX 6 (-2 save -2), CON 16 (+3 save +3), INT 1 (-5 save -5), WIS 6 (-2 save -2), CHA 2 (-4 save -4)
				Skills: Stealth +2
				Resistances: Acid, Cold, Fire
				Immunities: Blinded, Charmed, Deafened, Exhaustion, Frightened, Grappled, Prone, Restrained
				Senses: Blindsight 60 ft.; Passive Perception 8
				Languages: None
				Traits: Amorphous. The ooze can move through a space as narrow as 1 inch without expending extra movement to do so. Corrosive Form. Nonmagical ammunition is destroyed immediately after hitting the ooze and dealing any damage. Any nonmagical weapon takes a cumulative -1 penalty to attack rolls immediately after dealing damage to the ooze and coming into contact with it. The weapon is destroyed if the penalty reaches -5. The penalty can be removed by casting the Mending spell on the weapon. The ooze can eat through 2-inch-thick, nonmagical metal or wood in 1 round.
				Actions: Pseudopod. Melee Attack Roll: +3, reach 5 ft. Hit: 10 (2d8 + 1) Acid damage. Nonmagical armor worn by the target takes a -1 penalty to the AC it offers. The armor is destroyed if the penalty reduces its AC to 10. The penalty can be removed by casting the Mending spell on the armor.
			"""
		),
		monster(
			name = "Green Dragon Wyrmling",
			subtitle = "Medium Dragon (Chromatic), Lawful Evil",
			group = "Green Dragons",
			ac = "17",
			initiative = "+3 (13)",
			hp = "38 (7d8 + 7)",
			speed = "30 ft., Fly 60 ft., Swim 30 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 15 (+2 save +2), DEX 12 (+1 save +3), CON 13 (+1 save +1), INT 14 (+2 save +2), WIS 11 (+0 save +2), CHA 13 (+1 save +1)
				Skills: Perception +4, Stealth +3
				Immunities: Poison; Poisoned
				Senses: Blindsight 10 ft., Darkvision 60 ft.; Passive Perception 14
				Languages: Draconic
				Trait: Amphibious. The dragon can breathe air and water.
				Actions: Multiattack. The dragon makes two Rend attacks. Rend. Melee Attack Roll: +4, reach 5 ft. Hit: 7 (1d10 + 2) Slashing damage plus 3 (1d6) Poison damage. Poison Breath (Recharge 5-6). Constitution Saving Throw: DC 11, each creature in a 15-foot Cone. Failure: 21 (6d6) Poison damage. Success: Half damage.
			"""
		),
		monster(
			name = "Young Green Dragon",
			subtitle = "Large Dragon (Chromatic), Lawful Evil",
			group = "Green Dragons",
			ac = "18",
			initiative = "+4 (14)",
			hp = "136 (16d10 + 48)",
			speed = "40 ft., Fly 80 ft., Swim 40 ft.",
			cr = "8 (XP 3,900; PB +3)",
			body = """
				STR 19 (+4 save +4), DEX 12 (+1 save +4), CON 17 (+3 save +3), INT 16 (+3 save +3), WIS 13 (+1 save +4), CHA 15 (+2 save +2)
				Skills: Deception +5, Perception +7, Stealth +4
				Immunities: Poison; Poisoned
				Senses: Blindsight 30 ft., Darkvision 120 ft.; Passive Perception 17
				Languages: Common, Draconic
				Trait: Amphibious. The dragon can breathe air and water.
				Actions: Multiattack. The dragon makes three Rend attacks. Rend. Melee Attack Roll: +7, reach 10 ft. Hit: 11 (2d6 + 4) Slashing damage plus 7 (2d6) Poison damage. Poison Breath (Recharge 5-6). Constitution Saving Throw: DC 14, each creature in a 30-foot Cone. Failure: 42 (12d6) Poison damage. Success: Half damage.
			"""
		),
		monster(
			name = "Adult Green Dragon",
			subtitle = "Huge Dragon (Chromatic), Lawful Evil",
			group = "Green Dragons",
			ac = "19",
			initiative = "+11 (21)",
			hp = "207 (18d12 + 90)",
			speed = "40 ft., Fly 80 ft., Swim 40 ft.",
			cr = "15 (XP 13,000, or 15,000 in lair; PB +5)",
			body = """
				STR 23 (+6 save +6), DEX 12 (+1 save +6), CON 21 (+5 save +5), INT 18 (+4 save +4), WIS 15 (+2 save +7), CHA 18 (+4 save +4)
				Skills: Deception +9, Perception +12, Persuasion +9, Stealth +6
				Immunities: Poison; Poisoned
				Senses: Blindsight 60 ft., Darkvision 120 ft.; Passive Perception 22
				Languages: Common, Draconic
				Traits: Amphibious. The dragon can breathe air and water. Legendary Resistance (3/Day, or 4/Day in Lair). If the dragon fails a saving throw, it can choose to succeed instead.
				Actions: Multiattack. The dragon makes three Rend attacks. It can replace one attack with a use of Spellcasting to cast Mind Spike (level 3 version). Rend. Melee Attack Roll: +11, reach 10 ft. Hit: 15 (2d8 + 6) Slashing damage plus 7 (2d6) Poison damage. Poison Breath (Recharge 5-6). Constitution Saving Throw: DC 18, each creature in a 60-foot Cone. Failure: 56 (16d6) Poison damage. Success: Half damage. Spellcasting. The dragon casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 17): At Will: Detect Magic, Mind Spike (level 3 version). 1/Day: Geas.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the dragon can expend a use to take one of the following actions. The dragon regains all expended uses at the start of each of its turns. Mind Invasion. The dragon uses Spellcasting to cast Mind Spike (level 3 version). Noxious Miasma. Constitution Saving Throw: DC 17, each creature in a 20-foot-radius Sphere centered on a point the dragon can see within 90 feet. Failure: 7 (2d6) Poison damage, and the target takes a -2 penalty to AC until the end of its next turn. Failure or Success: The dragon can't take this action again until the start of its next turn. Pounce. The dragon moves up to half its Speed, and it makes one Rend attack.
			"""
		),
		monster(
			name = "Ancient Green Dragon",
			subtitle = "Gargantuan Dragon (Chromatic), Lawful Evil",
			group = "Green Dragons",
			ac = "21",
			initiative = "+15 (25)",
			hp = "402 (23d20 + 161)",
			speed = "40 ft., Fly 80 ft., Swim 40 ft.",
			cr = "22 (XP 41,000, or 50,000 in lair; PB +7)",
			body = """
				STR 27 (+8 save +8), DEX 12 (+1 save +8), CON 25 (+7 save +7), INT 20 (+5 save +5), WIS 17 (+3 save +10), CHA 22 (+6 save +6)
				Skills: Deception +13, Perception +17, Persuasion +13, Stealth +8
				Immunities: Poison; Poisoned
				Senses: Blindsight 60 ft., Darkvision 120 ft.; Passive Perception 27
				Languages: Common, Draconic
				Traits: Amphibious. The dragon can breathe air and water. Legendary Resistance (4/Day, or 5/Day in Lair). If the dragon fails a saving throw, it can choose to succeed instead.
				Actions: Multiattack. The dragon makes three Rend attacks. It can replace one attack with a use of Spellcasting to cast Mind Spike (level 5 version). Rend. Melee Attack Roll: +15, reach 15 ft. Hit: 17 (2d8 + 8) Slashing damage plus 10 (3d6) Poison damage. Poison Breath (Recharge 5-6). Constitution Saving Throw: DC 22, each creature in a 90-foot Cone. Failure: 77 (22d6) Poison damage. Success: Half damage. Spellcasting. The dragon casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 21): At Will: Detect Magic, Mind Spike (level 5 version). 1/Day Each: Geas, Modify Memory.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the dragon can expend a use to take one of the following actions. The dragon regains all expended uses at the start of each of its turns. Mind Invasion. The dragon uses Spellcasting to cast Mind Spike (level 5 version). Noxious Miasma. Constitution Saving Throw: DC 21, each creature in a 30-foot-radius Sphere centered on a point the dragon can see within 90 feet. Failure: 17 (5d6) Poison damage, and the target takes a -2 penalty to AC until the end of its next turn. Failure or Success: The dragon can't take this action again until the start of its next turn. Pounce. The dragon moves up to half its Speed, and it makes one Rend attack.
			"""
		),
		monster(
			name = "Green Hag",
			subtitle = "Medium Fey, Neutral Evil",
			ac = "17",
			initiative = "+1 (11)",
			hp = "82 (11d8 + 33)",
			speed = "30 ft., Swim 30 ft.",
			cr = "3 (XP 700; PB +2)",
			body = """
				STR 18 (+4 save +4), DEX 12 (+1 save +1), CON 16 (+3 save +3), INT 13 (+1 save +1), WIS 14 (+2 save +2), CHA 14 (+2 save +2)
				Skills: Arcana +5, Deception +4, Perception +4, Stealth +3
				Senses: Darkvision 60 ft.; Passive Perception 14
				Languages: Common, Elvish, Sylvan
				Traits: Amphibious. The hag can breathe air and water. Coven Magic. While within 30 feet of at least two hag allies, the hag can cast one of the following spells, requiring no Material components, using the spell's normal casting time, and using Intelligence as the spellcasting ability (spell save DC 11): Augury, Find Familiar, Identify, Locate Object, Scrying, or Unseen Servant. The hag must finish a Long Rest before using this trait to cast that spell again. Mimicry. The hag can mimic animal sounds and humanoid voices. A creature that hears the sounds can tell they are imitations only with a successful DC 14 Wisdom (Insight) check.
				Actions: Multiattack. The hag makes two Claw attacks. Claw. Melee Attack Roll: +6, reach 5 ft. Hit: 8 (1d8 + 4) Slashing damage plus 3 (1d6) Poison damage. Spellcasting. The hag casts one of the following spells, requiring no Material components and using Wisdom as the spellcasting ability (spell save DC 12, +4 to hit with spell attacks): At Will: Dancing Lights, Disguise Self (24-hour duration), Invisibility (self only, and the hag leaves no tracks while Invisible), Minor Illusion, Ray of Sickness (level 3 version).
			"""
		),
		monster(
			name = "Grick",
			subtitle = "Medium Aberration, Unaligned",
			ac = "14",
			initiative = "+2 (12)",
			hp = "54 (12d8)",
			speed = "30 ft., Climb 30 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 14 (+2 save +2), DEX 14 (+2 save +2), CON 11 (+0 save +0), INT 3 (-4 save -4), WIS 14 (+2 save +2), CHA 5 (-3 save -3)
				Skills: Stealth +4
				Senses: Darkvision 60 ft.; Passive Perception 12
				Languages: None
				Actions: Multiattack. The grick makes one Beak attack and one Tentacles attack. Beak. Melee Attack Roll: +4, reach 5 ft. Hit: 9 (2d6 + 2) Piercing damage. Tentacles. Melee Attack Roll: +4, reach 5 ft. Hit: 7 (1d10 + 2) Slashing damage. If the target is a Medium or smaller creature, it has the Grappled condition (escape DC 12) from all four tentacles.
			"""
		),
		monster(
			name = "Griffon",
			subtitle = "Large Monstrosity, Unaligned",
			ac = "12",
			initiative = "+2 (12)",
			hp = "59 (7d10 + 21)",
			speed = "30 ft., Fly 80 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 18 (+4 save +4), DEX 15 (+2 save +2), CON 16 (+3 save +3), INT 2 (-4 save -4), WIS 13 (+1 save +1), CHA 8 (-1 save -1)
				Skills: Perception +5
				Senses: Darkvision 60 ft.; Passive Perception 15
				Languages: None
				Actions: Multiattack. The griffon makes two Rend attacks. Rend. Melee Attack Roll: +6, reach 5 ft. Hit: 8 (1d8 + 4) Piercing damage. If the target is a Medium or smaller creature, it has the Grappled condition (escape DC 14) from both of the griffon's front claws.
			"""
		),
		monster(
			name = "Grimlock",
			subtitle = "Medium Aberration, Neutral Evil",
			ac = "11",
			initiative = "+1 (11)",
			hp = "11 (2d8 + 2)",
			speed = "30 ft., Climb 30 ft.",
			cr = "1/4 (XP 50; PB +2)",
			body = """
				STR 16 (+3 save +3), DEX 12 (+1 save +1), CON 12 (+1 save +1), INT 9 (-1 save -1), WIS 8 (-1 save -1), CHA 6 (-2 save -2)
				Skills: Athletics +5, Perception +3, Stealth +5
				Senses: Blindsight 30 ft.; Passive Perception 13
				Languages: None
				Actions: Bone Cudgel. Melee Attack Roll: +5, reach 5 ft. Hit: 6 (1d6 + 3) Bludgeoning damage plus 2 (1d4) Psychic damage.
			"""
		)
	)
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


