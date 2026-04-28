package io.github.velyene.loreweaver.domain.util

object MonsterReferenceDataOtoR {
	val ENTRIES: List<MonsterReferenceEntry> = listOf(
		monster(
			name = "Ochre Jelly",
			subtitle = "Large Ooze, Unaligned",
			ac = "8",
			initiative = "-2 (8)",
			hp = "52 (7d10 + 14)",
			speed = "20 ft., Climb 20 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 15 (+2 save +2), DEX 6 (-2 save -2), CON 14 (+2 save +2), INT 2 (-4 save -4), WIS 6 (-2 save -2), CHA 1 (-5 save -5)
				Resistances: Acid
				Immunities: Lightning, Slashing; Charmed, Deafened, Exhaustion, Frightened, Grappled, Prone, Restrained
				Senses: Blindsight 60 ft.; Passive Perception 8
				Languages: None
				Traits: Amorphous. The jelly can move through a space as narrow as 1 inch without expending extra movement to do so. Spider Climb. The jelly can climb difficult surfaces, including along ceilings, without needing to make an ability check.
				Actions: Pseudopod. Melee Attack Roll: +4, reach 5 ft. Hit: 12 (3d6 + 2) Acid damage.
				Reactions: Split. Trigger: While the jelly is Large or Medium and has 10+ Hit Points, it becomes Bloodied or is subjected to Lightning or Slashing damage. Response: The jelly splits into two new Ochre Jellies. Each new jelly is one size smaller than the original jelly and acts on its Initiative. The original jelly's Hit Points are divided evenly between the new jellies (round down).
			"""
		),
		monster(
			name = "Ogre",
			subtitle = "Large Giant, Chaotic Evil",
			ac = "11",
			initiative = "-1 (9)",
			hp = "68 (8d10 + 24)",
			speed = "40 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 19 (+4 save +4), DEX 8 (-1 save -1), CON 16 (+3 save +3), INT 5 (-3 save -3), WIS 7 (-2 save -2), CHA 7 (-2 save -2)
				Gear: Greatclub, Javelins (3)
				Senses: Darkvision 60 ft.; Passive Perception 8
				Languages: Common, Giant
				Actions: Greatclub. Melee Attack Roll: +6, reach 5 ft. Hit: 13 (2d8 + 4) Bludgeoning damage. Javelin. Melee or Ranged Attack Roll: +6, reach 5 ft. or range 30/120 ft. Hit: 11 (2d6 + 4) Piercing damage.
			"""
		),
		monster(
			name = "Ogre Zombie",
			subtitle = "Large Undead, Neutral Evil",
			group = "Zombies",
			ac = "8",
			initiative = "-2 (8)",
			hp = "85 (9d10 + 36)",
			speed = "30 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 19 (+4 save +4), DEX 6 (-2 save -2), CON 18 (+4 save +4), INT 3 (-4 save -4), WIS 6 (-2 save +0), CHA 5 (-3 save -3)
				Immunities: Poison; Exhaustion, Poisoned
				Senses: Darkvision 60 ft.; Passive Perception 8
				Languages: Understands Common and Giant but can't speak
				Trait: Undead Fortitude. If damage reduces the zombie to 0 Hit Points, it makes a Constitution saving throw (DC 5 plus the damage taken) unless the damage is Radiant or from a Critical Hit. On a successful save, the zombie drops to 1 Hit Point instead.
				Actions: Slam. Melee Attack Roll: +6, reach 5 ft. Hit: 13 (2d8 + 4) Bludgeoning damage.
			"""
		),
		monster(
			name = "Oni",
			subtitle = "Large Fiend, Lawful Evil",
			ac = "17",
			initiative = "+0 (10)",
			hp = "119 (14d10 + 42)",
			speed = "30 ft., Fly 30 ft. (hover)",
			cr = "7 (XP 2,900; PB +3)",
			body = """
				STR 19 (+4 save +4), DEX 11 (+0 save +3), CON 16 (+3 save +6), INT 14 (+2 save +2), WIS 12 (+1 save +4), CHA 15 (+2 save +5)
				Skills: Arcana +5, Deception +8, Perception +4
				Resistances: Cold
				Senses: Darkvision 60 ft.; Passive Perception 14
				Languages: Common, Giant
				Trait: Regeneration. The oni regains 10 Hit Points at the start of each of its turns if it has at least 1 Hit Point.
				Actions: Multiattack. The oni makes two Claw or Nightmare Ray attacks. It can replace one attack with a use of Spellcasting. Claw. Melee Attack Roll: +7, reach 10 ft. Hit: 10 (1d12 + 4) Slashing damage plus 9 (2d8) Necrotic damage. Nightmare Ray. Ranged Attack Roll: +5, range 60 ft. Hit: 9 (2d6 + 2) Psychic damage, and the target has the Frightened condition until the start of the oni's next turn. Shape-Shift. The oni shape-shifts into a Small or Medium Humanoid or a Large Giant, or it returns to its true form. Other than its size, its game statistics are the same in each form. Any equipment it is wearing or carrying isn't transformed. Spellcasting. The oni casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 13): 1/Day Each: Charm Person (level 2 version), Darkness, Gaseous Form, Sleep.
				Bonus Actions: Invisibility. The oni casts Invisibility on itself, requiring no spell components and using the same spellcasting ability as Spellcasting.
			"""
		),
		monster(
			name = "Otyugh",
			subtitle = "Large Aberration, Neutral",
			ac = "14",
			initiative = "+0 (10)",
			hp = "104 (11d10 + 44)",
			speed = "30 ft.",
			cr = "5 (XP 1,800; PB +3)",
			body = """
				STR 16 (+3 save +3), DEX 11 (+0 save +0), CON 19 (+4 save +7), INT 6 (-2 save -2), WIS 13 (+1 save +1), CHA 6 (-2 save -2)
				Senses: Darkvision 120 ft.; Passive Perception 11
				Languages: Otyugh; telepathy 120 ft. (doesn't allow the receiving creature to respond telepathically)
				Actions: Multiattack. The otyugh makes one Bite attack and two Tentacle attacks. Bite. Melee Attack Roll: +6, reach 5 ft. Hit: 12 (2d8 + 3) Piercing damage, and the target has the Poisoned condition. Whenever the Poisoned target finishes a Long Rest, it is subjected to the following effect. Constitution Saving Throw: DC 15. Failure: The target's Hit Point maximum decreases by 5 (1d10) and doesn't return to normal until the Poisoned condition ends on the target. Success: The Poisoned condition ends. Tentacle. Melee Attack Roll: +6, reach 10 ft. Hit: 12 (2d8 + 3) Piercing damage. If the target is a Medium or smaller creature, it has the Grappled condition (escape DC 13) from one of two tentacles. Tentacle Slam. Constitution Saving Throw: DC 14, each creature Grappled by the otyugh. Failure: 16 (3d8 + 3) Bludgeoning damage, and the target has the Stunned condition until the start of the otyugh's next turn. Success: Half damage only.
			"""
		),
		monster(
			name = "Owlbear",
			subtitle = "Large Monstrosity, Unaligned",
			ac = "13",
			initiative = "+1 (11)",
			hp = "59 (7d10 + 21)",
			speed = "40 ft., Climb 40 ft.",
			cr = "3 (XP 700; PB +2)",
			body = """
				STR 20 (+5 save +5), DEX 12 (+1 save +1), CON 17 (+3 save +3), INT 3 (-4 save -4), WIS 12 (+1 save +1), CHA 7 (-2 save -2)
				Skills: Perception +5
				Senses: Darkvision 60 ft.; Passive Perception 15
				Languages: None
				Actions: Multiattack. The owlbear makes two Rend attacks. Rend. Melee Attack Roll: +7, reach 5 ft. Hit: 14 (2d8 + 5) Slashing damage.
			"""
		),
		monster(
			name = "Pegasus",
			subtitle = "Large Celestial, Chaotic Good",
			ac = "12",
			initiative = "+2 (12)",
			hp = "59 (7d10 + 21)",
			speed = "60 ft., Fly 90 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 18 (+4 save +4), DEX 15 (+2 save +4), CON 16 (+3 save +5), INT 10 (+0 save +0), WIS 15 (+2 save +4), CHA 13 (+1 save +3)
				Skills: Perception +6
				Senses: Passive Perception 16
				Languages: Understands Celestial, Common, Elvish, and Sylvan but can't speak
				Actions: Hooves. Melee Attack Roll: +6, reach 5 ft. Hit: 7 (1d6 + 4) Bludgeoning damage plus 5 (2d4) Radiant damage.
			"""
		),
		monster(
			name = "Phase Spider",
			subtitle = "Large Monstrosity, Unaligned",
			ac = "14",
			initiative = "+3 (13)",
			hp = "45 (7d10 + 7)",
			speed = "30 ft., Climb 30 ft.",
			cr = "3 (XP 700; PB +2)",
			body = """
				STR 15 (+2 save +2), DEX 16 (+3 save +3), CON 12 (+1 save +1), INT 6 (-2 save -2), WIS 10 (+0 save +0), CHA 6 (-2 save -2)
				Skills: Stealth +7
				Senses: Darkvision 60 ft.; Passive Perception 10
				Languages: None
				Traits: Ethereal Sight. The spider can see 60 feet into the Ethereal Plane while on the Material Plane and vice versa. Spider Climb. The spider can climb difficult surfaces, including along ceilings, without needing to make an ability check. Web Walker. The spider ignores movement restrictions caused by webs, and the spider knows the location of any other creature in contact with the same web.
				Actions: Multiattack. The spider makes two Bite attacks. Bite. Melee Attack Roll: +5, reach 5 ft. Hit: 8 (1d10 + 3) Piercing damage plus 9 (2d8) Poison damage. If this damage reduces the target to 0 Hit Points, the target becomes Stable, and it has the Poisoned condition for 1 hour. While Poisoned, the target also has the Paralyzed condition.
				Bonus Actions: Ethereal Jaunt. The spider teleports from the Material Plane to the Ethereal Plane or vice versa.
			"""
		),
		monster(
			name = "Pirate",
			subtitle = "Medium or Small Humanoid, Neutral",
			group = "Pirates",
			ac = "14",
			initiative = "+5 (15)",
			hp = "33 (6d8 + 6)",
			speed = "30 ft.",
			cr = "1 (XP 200; PB +2)",
			body = """
				STR 10 (+0 save +0), DEX 16 (+3 save +5), CON 12 (+1 save +1), INT 8 (-1 save -1), WIS 12 (+1 save +1), CHA 14 (+2 save +4)
				Gear: Daggers (6), Leather Armor
				Senses: Passive Perception 11
				Languages: Common plus one other language
				Actions: Multiattack. The pirate makes two Dagger attacks. It can replace one attack with a use of Enthralling Panache. Dagger. Melee or Ranged Attack Roll: +5, reach 5 ft. or range 20/60 ft. Hit: 5 (1d4 + 3) Piercing damage. Enthralling Panache. Wisdom Saving Throw: DC 12, one creature the pirate can see within 30 feet. Failure: The target has the Charmed condition until the start of the pirate's next turn.
			"""
		),
		monster(
			name = "Pirate Captain",
			subtitle = "Medium or Small Humanoid, Neutral",
			group = "Pirates",
			ac = "17",
			initiative = "+7 (17)",
			hp = "84 (13d8 + 26)",
			speed = "30 ft.",
			cr = "6 (XP 2,300; PB +3)",
			body = """
				STR 10 (+0 save +3), DEX 18 (+4 save +7), CON 14 (+2 save +2), INT 10 (+0 save +0), WIS 14 (+2 save +5), CHA 17 (+3 save +6)
				Skills: Acrobatics +7, Perception +5
				Gear: Pistol, Rapier
				Senses: Passive Perception 15
				Languages: Common plus one other language
				Actions: Multiattack. The pirate makes three attacks, using Rapier or Pistol in any combination. Rapier. Melee Attack Roll: +7, reach 5 ft. Hit: 13 (2d8 + 4) Piercing damage, and the pirate has Advantage on the next attack roll it makes before the end of this turn. Pistol. Ranged Attack Roll: +7, range 30/90 ft. Hit: 15 (2d10 + 4) Piercing damage.
				Bonus Actions: Captain's Charm. Wisdom Saving Throw: DC 14, one creature the pirate can see within 30 feet. Failure: The target has the Charmed condition until the start of the pirate's next turn.
				Reactions: Riposte. Trigger: The pirate is hit by a melee attack roll while holding a weapon. Response: The pirate adds 3 to its AC against that attack, possibly causing it to miss. On a miss, the pirate makes one Rapier attack against the triggering creature if within range.
			"""
		),
		monster(
			name = "Pit Fiend",
			subtitle = "Large Fiend (Devil), Lawful Evil",
			ac = "21",
			initiative = "+14 (24)",
			hp = "337 (27d10 + 189)",
			speed = "30 ft., Fly 60 ft.",
			cr = "20 (XP 25,000; PB +6)",
			body = """
				STR 26 (+8 save +8), DEX 14 (+2 save +8), CON 24 (+7 save +7), INT 22 (+6 save +6), WIS 18 (+4 save +10), CHA 24 (+7 save +7)
				Skills: Perception +10, Persuasion +19
				Resistances: Cold
				Immunities: Fire, Poison; Poisoned
				Senses: Truesight 120 ft.; Passive Perception 20
				Languages: Infernal; telepathy 120 ft.
				Traits: Diabolical Restoration. If the pit fiend dies outside the Nine Hells, its body disappears in sulfurous smoke, and it gains a new body instantly, reviving with all its Hit Points somewhere in the Nine Hells. Fear Aura. The pit fiend emanates an aura in a 20-foot Emanation while it doesn't have the Incapacitated condition. Wisdom Saving Throw: DC 21, any enemy that starts its turn in the aura. Failure: The target has the Frightened condition until the start of its next turn. Success: The target is immune to this pit fiend's aura for 24 hours. Legendary Resistance (4/Day). If the pit fiend fails a saving throw, it can choose to succeed instead. Magic Resistance. The pit fiend has Advantage on saving throws against spells and other magical effects.
				Actions: Multiattack. The pit fiend makes one Bite attack, two Devilish Claw attacks, and one Fiery Mace attack. Bite. Melee Attack Roll: +14, reach 10 ft. Hit: 18 (3d6 + 8) Piercing damage. If the target is a creature, it must make the following saving throw. Constitution Saving Throw: DC 21. Failure: The target has the Poisoned condition. While Poisoned, the target can't regain Hit Points and takes 21 (6d6) Poison damage at the start of each of its turns, and it repeats the save at the end of each of its turns, ending the effect on itself on a success. After 1 minute, it succeeds automatically. Devilish Claw. Melee Attack Roll: +14, reach 10 ft. Hit: 26 (4d8 + 8) Necrotic damage. Fiery Mace. Melee Attack Roll: +14, reach 10 ft. Hit: 22 (4d6 + 8) Force damage plus 21 (6d6) Fire damage. Hellfire Spellcasting (Recharge 4-6). The pit fiend casts Fireball (level 5 version) twice, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 21). It can replace one Fireball with Hold Monster (level 7 version) or Wall of Fire.
			"""
		),
		monster(
			name = "Planetar",
			subtitle = "Large Celestial (Angel), Lawful Good",
			ac = "19",
			initiative = "+10 (20)",
			hp = "262 (21d10 + 147)",
			speed = "40 ft., Fly 120 ft. (hover)",
			cr = "16 (XP 15,000; PB +5)",
			body = """
				STR 24 (+7 save +12), DEX 20 (+5 save +5), CON 24 (+7 save +12), INT 19 (+4 save +4), WIS 22 (+6 save +11), CHA 25 (+7 save +12)
				Skills: Perception +11
				Resistances: Radiant
				Immunities: Charmed, Exhaustion, Frightened
				Senses: Truesight 120 ft.; Passive Perception 21
				Languages: All; telepathy 120 ft.
				Traits: Divine Awareness. The planetar knows if it hears a lie. Exalted Restoration. If the planetar dies outside Mount Celestia, its body disappears, and it gains a new body instantly, reviving with all its Hit Points somewhere in Mount Celestia. Magic Resistance. The planetar has Advantage on saving throws against spells and other magical effects.
				Actions: Multiattack. The planetar makes three Radiant Sword attacks or uses Holy Burst twice. Radiant Sword. Melee Attack Roll: +12, reach 10 ft. Hit: 14 (2d6 + 7) Slashing damage plus 18 (4d8) Radiant damage. Holy Burst. Dexterity Saving Throw: DC 20, each enemy in a 20-foot-radius Sphere centered on a point the planetar can see within 120 feet. Failure: 24 (7d6) Radiant damage. Success: Half damage. Spellcasting. The planetar casts one of the following spells, requiring no Material components and using Charisma as spellcasting ability (spell save DC 20): At Will: Detect Evil and Good. 1/Day Each: Commune, Control Weather, Dispel Evil and Good, Raise Dead.
				Bonus Actions: Divine Aid (2/Day). The planetar casts Cure Wounds, Invisibility, Lesser Restoration, or Remove Curse, using the same spellcasting ability as Spellcasting.
			"""
		),
		monster(
			name = "Priest Acolyte",
			subtitle = "Medium or Small Humanoid (Cleric), Neutral",
			group = "Priests",
			ac = "13",
			initiative = "+0 (10)",
			hp = "11 (2d8 + 2)",
			speed = "30 ft.",
			cr = "1/4 (XP 50; PB +2)",
			body = """
				STR 14 (+2 save +2), DEX 10 (+0 save +0), CON 12 (+1 save +1), INT 10 (+0 save +0), WIS 14 (+2 save +2), CHA 11 (+0 save +0)
				Skills: Medicine +4, Religion +2
				Gear: Chain Shirt, Holy Symbol, Mace
				Senses: Passive Perception 12
				Languages: Common
				Actions: Mace. Melee Attack Roll: +4, reach 5 ft. Hit: 5 (1d6 + 2) Bludgeoning damage plus 2 (1d4) Radiant damage. Radiant Flame. Ranged Attack Roll: +4, range 60 ft. Hit: 7 (2d6) Radiant damage. Spellcasting. The priest casts one of the following spells, using Wisdom as the spellcasting ability: At Will: Light, Thaumaturgy.
				Bonus Actions: Divine Aid (1/Day). The priest casts Bless, Healing Word, or Sanctuary, using the same spellcasting ability as Spellcasting.
			"""
		),
		monster(
			name = "Priest",
			subtitle = "Medium or Small Humanoid (Cleric), Neutral",
			group = "Priests",
			ac = "13",
			initiative = "+0 (10)",
			hp = "38 (7d8 + 7)",
			speed = "30 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 16 (+3 save +3), DEX 10 (+0 save +0), CON 12 (+1 save +1), INT 13 (+1 save +1), WIS 16 (+3 save +3), CHA 13 (+1 save +1)
				Skills: Medicine +7, Perception +5, Religion +5
				Gear: Chain Shirt, Holy Symbol, Mace
				Senses: Passive Perception 15
				Languages: Common plus one other language
				Actions: Multiattack. The priest makes two attacks, using Mace or Radiant Flame in any combination. Mace. Melee Attack Roll: +5, reach 5 ft. Hit: 6 (1d6 + 3) Bludgeoning damage plus 5 (2d4) Radiant damage. Radiant Flame. Ranged Attack Roll: +5, range 60 ft. Hit: 11 (2d10) Radiant damage. Spellcasting. The priest casts one of the following spells, using Wisdom as the spellcasting ability (spell save DC 13): At Will: Light, Thaumaturgy. 1/Day: Spirit Guardians.
				Bonus Actions: Divine Aid (3/Day). The priest casts Bless, Dispel Magic, Healing Word, or Lesser Restoration, using the same spellcasting ability as Spellcasting.
			"""
		),
		monster(
			name = "Pseudodragon",
			subtitle = "Tiny Dragon, Neutral Good",
			ac = "14",
			initiative = "+2 (12)",
			hp = "10 (3d4 + 3)",
			speed = "15 ft., Fly 60 ft.",
			cr = "1/4 (XP 50; PB +2)",
			body = """
				STR 6 (-2 save -2), DEX 15 (+2 save +2), CON 13 (+1 save +1), INT 10 (+0 save +0), WIS 12 (+1 save +1), CHA 10 (+0 save +0)
				Skills: Perception +5, Stealth +4
				Senses: Blindsight 10 ft., Darkvision 60 ft.; Passive Perception 15
				Languages: Understands Common and Draconic but can't speak
				Trait: Magic Resistance. The pseudodragon has Advantage on saving throws against spells and other magical effects.
				Actions: Multiattack. The pseudodragon makes two Bite attacks. Bite. Melee Attack Roll: +4, reach 5 ft. Hit: 4 (1d4 + 2) Piercing damage. Sting. Constitution Saving Throw: DC 12, one creature the pseudodragon can see within 5 feet. Failure: 5 (2d4) Poison damage, and the target has the Poisoned condition for 1 hour. Failure by 5 or More: While Poisoned, the target also has the Unconscious condition, which ends early if the target takes damage or a creature within 5 feet of it takes an action to wake it.
			"""
		),
		monster(
			name = "Purple Worm",
			subtitle = "Gargantuan Monstrosity, Unaligned",
			ac = "18",
			initiative = "+3 (13)",
			hp = "247 (15d20 + 90)",
			speed = "50 ft., Burrow 50 ft.",
			cr = "15 (XP 13,000; PB +5)",
			body = """
				STR 28 (+9 save +9), DEX 7 (-2 save -2), CON 22 (+6 save +11), INT 1 (-5 save -5), WIS 8 (-1 save +4), CHA 4 (-3 save -3)
				Senses: Blindsight 30 ft., Tremorsense 60 ft.; Passive Perception 9
				Languages: None
				Trait: Tunneler. The worm can burrow through solid rock at half its Burrow Speed and leaves a 10-foot-diameter tunnel in its wake.
				Actions: Multiattack. The worm makes one Bite attack and one Tail Stinger attack. Bite. Melee Attack Roll: +14, reach 10 ft. Hit: 22 (3d8 + 9) Piercing damage. If the target is a Large or smaller creature, it has the Grappled condition (escape DC 19), and it has the Restrained condition until the grapple ends. Tail Stinger. Melee Attack Roll: +14, reach 10 ft. Hit: 16 (2d6 + 9) Piercing damage plus 35 (10d6) Poison damage.
				Bonus Actions: Swallow. Strength Saving Throw: DC 19, one Large or smaller creature Grappled by the worm (it can have up to three creatures swallowed at a time). Failure: The target is swallowed by the worm, and the Grappled condition ends. A swallowed creature has the Blinded and Restrained conditions, has Total Cover against attacks and other effects outside the worm, and takes 17 (5d6) Acid damage at the start of each of the worm's turns. If the worm takes 30 damage or more on a single turn from a creature inside it, the worm must succeed on a DC 21 Constitution saving throw at the end of that turn or regurgitate all swallowed creatures, each of which falls in a space within 5 feet of the worm and has the Prone condition. If the worm dies, any swallowed creature no longer has the Restrained condition and can escape from the corpse using 20 feet of movement, exiting Prone.
			"""
		),
		monster(
			name = "Quasit",
			subtitle = "Tiny Fiend (Demon), Chaotic Evil",
			ac = "13",
			initiative = "+3 (13)",
			hp = "25 (10d4)",
			speed = "40 ft.",
			cr = "1 (XP 200; PB +2)",
			body = """
				STR 5 (-3 save -3), DEX 17 (+3 save +3), CON 10 (+0 save +0), INT 7 (-2 save -2), WIS 10 (+0 save +0), CHA 10 (+0 save +0)
				Skills: Stealth +5
				Resistances: Cold, Fire, Lightning
				Immunities: Poison; Poisoned
				Senses: Darkvision 120 ft.; Passive Perception 10
				Languages: Abyssal, Common
				Trait: Magic Resistance. The quasit has Advantage on saving throws against spells and other magical effects.
				Actions: Rend. Melee Attack Roll: +5, reach 5 ft. Hit: 5 (1d4 + 3) Slashing damage, and the target has the Poisoned condition until the start of the quasit's next turn. Invisibility. The quasit casts Invisibility on itself, requiring no spell components and using Charisma as the spellcasting ability. Scare (1/Day). Wisdom Saving Throw: DC 10, one creature within 20 feet. Failure: The target has the Frightened condition. At the end of each of its turns, the target repeats the save, ending the effect on itself on a success. After 1 minute, it succeeds automatically. Shape-Shift. The quasit shape-shifts to resemble a bat (Speed 10 ft., Fly 40 ft.), a centipede (40 ft., Climb 40 ft.), or a toad (40 ft., Swim 40 ft.), or it returns to its true form. Its game statistics are the same in each form, except for its Speed. Any equipment it is wearing or carrying isn't transformed.
			"""
		),
		monster(
			name = "Rakshasa",
			subtitle = "Medium Fiend, Lawful Evil",
			ac = "17",
			initiative = "+8 (18)",
			hp = "221 (26d8 + 104)",
			speed = "40 ft.",
			cr = "13 (XP 10,000; PB +5)",
			body = """
				STR 14 (+2 save +2), DEX 17 (+3 save +3), CON 18 (+4 save +4), INT 13 (+1 save +1), WIS 16 (+3 save +3), CHA 20 (+5 save +5)
				Skills: Deception +10, Insight +8, Perception +8
				Vulnerabilities: Piercing damage from weapons wielded by creatures under the effect of a Bless spell
				Immunities: Charmed, Frightened
				Senses: Truesight 60 ft.; Passive Perception 18
				Languages: Common, Infernal
				Traits: Greater Magic Resistance. The rakshasa automatically succeeds on saving throws against spells and other magical effects, and the attack rolls of spells automatically miss it. Without the rakshasa's permission, no spell can observe the rakshasa remotely or detect its thoughts, creature type, or alignment. Fiendish Restoration. If the rakshasa dies outside the Nine Hells, its body turns to ichor, and it gains a new body instantly, reviving with all its Hit Points somewhere in the Nine Hells.
				Actions: Multiattack. The rakshasa makes three Cursed Touch attacks. Cursed Touch. Melee Attack Roll: +10, reach 5 ft. Hit: 12 (2d6 + 5) Slashing damage plus 19 (3d12) Necrotic damage. If the target is a creature, it is cursed. While cursed, the target gains no benefit from finishing a Short or Long Rest. Baleful Command (Recharge 5-6). Wisdom Saving Throw: DC 18, each enemy in a 30-foot Emanation originating from the rakshasa. Failure: 28 (8d6) Psychic damage, and the target has the Frightened and Incapacitated conditions until the start of the rakshasa's next turn. Spellcasting. The rakshasa casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 18): At Will: Detect Magic, Detect Thoughts, Disguise Self, Mage Hand, Minor Illusion. 1/Day Each: Fly, Invisibility, Major Image, Plane Shift.
			"""
		),
		monster(
			name = "Red Dragon Wyrmling",
			subtitle = "Medium Dragon (Chromatic), Chaotic Evil",
			group = "Red Dragons",
			ac = "17",
			initiative = "+2 (12)",
			hp = "75 (10d8 + 30)",
			speed = "30 ft., Climb 30 ft., Fly 60 ft.",
			cr = "4 (XP 1,100; PB +2)",
			body = """
				STR 19 (+4 save +4), DEX 10 (+0 save +2), CON 17 (+3 save +3), INT 12 (+1 save +1), WIS 11 (+0 save +2), CHA 15 (+2 save +2)
				Skills: Perception +4, Stealth +2
				Immunities: Fire
				Senses: Blindsight 10 ft., Darkvision 60 ft.; Passive Perception 14
				Languages: Draconic
				Actions: Multiattack. The dragon makes two Rend attacks. Rend. Melee Attack Roll: +6, reach 5 ft. Hit: 9 (1d10 + 4) Slashing damage plus 3 (1d6) Fire damage. Fire Breath (Recharge 5-6). Dexterity Saving Throw: DC 13, each creature in a 15-foot Cone. Failure: 24 (7d6) Fire damage. Success: Half damage.
			"""
		),
		monster(
			name = "Young Red Dragon",
			subtitle = "Large Dragon (Chromatic), Chaotic Evil",
			group = "Red Dragons",
			ac = "18",
			initiative = "+4 (14)",
			hp = "178 (17d10 + 85)",
			speed = "40 ft., Climb 40 ft., Fly 80 ft.",
			cr = "10 (XP 5,900; PB +4)",
			body = """
				STR 23 (+6 save +6), DEX 10 (+0 save +4), CON 21 (+5 save +5), INT 14 (+2 save +2), WIS 11 (+0 save +4), CHA 19 (+4 save +4)
				Skills: Perception +8, Stealth +4
				Immunities: Fire
				Senses: Blindsight 30 ft., Darkvision 120 ft.; Passive Perception 18
				Languages: Common, Draconic
				Actions: Multiattack. The dragon makes three Rend attacks. Rend. Melee Attack Roll: +10, reach 10 ft. Hit: 13 (2d6 + 6) Slashing damage plus 3 (1d6) Fire damage. Fire Breath (Recharge 5-6). Dexterity Saving Throw: DC 17, each creature in a 30-foot Cone. Failure: 56 (16d6) Fire damage. Success: Half damage.
			"""
		),
		monster(
			name = "Adult Red Dragon",
			subtitle = "Huge Dragon (Chromatic), Chaotic Evil",
			group = "Red Dragons",
			ac = "19",
			initiative = "+12 (22)",
			hp = "256 (19d12 + 133)",
			speed = "40 ft., Climb 40 ft., Fly 80 ft.",
			cr = "17 (XP 18,000, or 20,000 in lair; PB +6)",
			body = """
				STR 27 (+8 save +8), DEX 10 (+0 save +6), CON 25 (+7 save +7), INT 16 (+3 save +3), WIS 13 (+1 save +7), CHA 23 (+6 save +6)
				Skills: Perception +13, Stealth +6
				Immunities: Fire
				Senses: Blindsight 60 ft., Darkvision 120 ft.; Passive Perception 23
				Languages: Common, Draconic
				Trait: Legendary Resistance (3/Day, or 4/Day in Lair). If the dragon fails a saving throw, it can choose to succeed instead.
				Actions: Multiattack. The dragon makes three Rend attacks. It can replace one attack with a use of Spellcasting to cast Scorching Ray. Rend. Melee Attack Roll: +14, reach 10 ft. Hit: 13 (1d10 + 8) Slashing damage plus 5 (2d4) Fire damage. Fire Breath (Recharge 5-6). Dexterity Saving Throw: DC 21, each creature in a 60-foot Cone. Failure: 59 (17d6) Fire damage. Success: Half damage. Spellcasting. The dragon casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 20, +12 to hit with spell attacks): At Will: Command (level 2 version), Detect Magic, Scorching Ray. 1/Day: Fireball.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the dragon can expend a use to take one of the following actions. The dragon regains all expended uses at the start of each of its turns. Commanding Presence. The dragon uses Spellcasting to cast Command (level 2 version). The dragon can't take this action again until the start of its next turn. Fiery Rays. The dragon uses Spellcasting to cast Scorching Ray. The dragon can't take this action again until the start of its next turn. Pounce. The dragon moves up to half its Speed, and it makes one Rend attack.
			"""
		),
		monster(
			name = "Ancient Red Dragon",
			subtitle = "Gargantuan Dragon (Chromatic), Chaotic Evil",
			group = "Red Dragons",
			ac = "22",
			initiative = "+14 (24)",
			hp = "507 (26d20 + 234)",
			speed = "40 ft., Climb 40 ft., Fly 80 ft.",
			cr = "24 (XP 62,000, or 75,000 in lair; PB +7)",
			body = """
				STR 30 (+10 save +10), DEX 10 (+0 save +7), CON 29 (+9 save +9), INT 18 (+4 save +4), WIS 15 (+2 save +9), CHA 27 (+8 save +8)
				Skills: Perception +16, Stealth +7
				Immunities: Fire
				Senses: Blindsight 60 ft., Darkvision 120 ft.; Passive Perception 26
				Languages: Common, Draconic
				Trait: Legendary Resistance (4/Day, or 5/Day in Lair). If the dragon fails a saving throw, it can choose to succeed instead.
				Actions: Multiattack. The dragon makes three Rend attacks. It can replace one attack with a use of Spellcasting to cast Scorching Ray (level 3 version). Rend. Melee Attack Roll: +17, reach 15 ft. Hit: 19 (2d8 + 10) Slashing damage plus 10 (3d6) Fire damage. Fire Breath (Recharge 5-6). Dexterity Saving Throw: DC 24, each creature in a 90-foot Cone. Failure: 91 (26d6) Fire damage. Success: Half damage. Spellcasting. The dragon casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 23, +15 to hit with spell attacks): At Will: Command (level 2 version), Detect Magic, Scorching Ray (level 3 version). 1/Day Each: Fireball (level 6 version), Scrying.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the dragon can expend a use to take one of the following actions. The dragon regains all expended uses at the start of each of its turns. Commanding Presence. The dragon uses Spellcasting to cast Command (level 2 version). The dragon can't take this action again until the start of its next turn. Fiery Rays. The dragon uses Spellcasting to cast Scorching Ray (level 3 version). The dragon can't take this action again until the start of its next turn. Pounce. The dragon moves up to half its Speed, and it makes one Rend attack.
			"""
		),
		monster(
			name = "Remorhaz",
			subtitle = "Huge Monstrosity, Unaligned",
			ac = "17",
			initiative = "+5 (15)",
			hp = "195 (17d12 + 85)",
			speed = "40 ft., Burrow 30 ft.",
			cr = "11 (XP 7,200; PB +4)",
			body = """
				STR 24 (+7 save +7), DEX 13 (+1 save +1), CON 21 (+5 save +5), INT 4 (-3 save -3), WIS 10 (+0 save +0), CHA 5 (-3 save -3)
				Immunities: Cold, Fire
				Senses: Darkvision 60 ft., Tremorsense 60 ft.; Passive Perception 10
				Languages: None
				Trait: Heat Aura. At the end of each of the remorhaz's turns, each creature in a 5-foot Emanation originating from the remorhaz takes 16 (3d10) Fire damage.
				Actions: Bite. Melee Attack Roll: +11, reach 10 ft. Hit: 18 (2d10 + 7) Piercing damage plus 14 (4d6) Fire damage. If the target is a Large or smaller creature, it has the Grappled condition (escape DC 17), and it has the Restrained condition until the grapple ends.
				Bonus Actions: Swallow. Strength Saving Throw: DC 19, one Large or smaller creature Grappled by the remorhaz (it can have up to two creatures swallowed at a time). Failure: The target is swallowed by the remorhaz, and the Grappled condition ends. A swallowed creature has the Blinded and Restrained conditions, it has Total Cover against attacks and other effects outside the remorhaz, and it takes 10 (3d6) Acid damage plus 10 (3d6) Fire damage at the start of each of the remorhaz's turns. If the remorhaz takes 30 damage or more on a single turn from a creature inside it, the remorhaz must succeed on a DC 15 Constitution saving throw at the end of that turn or regurgitate all swallowed creatures, each of which falls in a space within 5 feet of the remorhaz and has the Prone condition. If the remorhaz dies, any swallowed creature no longer has the Restrained condition and can escape from the corpse by using 15 feet of movement, exiting Prone.
			"""
		),
		monster(
			name = "Roc",
			subtitle = "Gargantuan Monstrosity, Unaligned",
			ac = "15",
			initiative = "+8 (18)",
			hp = "248 (16d20 + 80)",
			speed = "20 ft., Fly 120 ft.",
			cr = "11 (XP 7,200; PB +4)",
			body = """
				STR 28 (+9 save +9), DEX 10 (+0 save +4), CON 20 (+5 save +5), INT 3 (-4 save -4), WIS 10 (+0 save +4), CHA 9 (-1 save -1)
				Skills: Perception +8
				Senses: Passive Perception 18
				Languages: None
				Actions: Multiattack. The roc makes two Beak attacks. It can replace one attack with a Talons attack. Beak. Melee Attack Roll: +13, reach 10 ft. Hit: 28 (3d12 + 9) Piercing damage. Talons. Melee Attack Roll: +13, reach 5 ft. Hit: 23 (4d6 + 9) Slashing damage. If the target is a Huge or smaller creature, it has the Grappled condition (escape DC 19) from both talons, and it has the Restrained condition until the grapple ends.
				Bonus Actions: Swoop (Recharge 5-6). If the roc has a creature Grappled, the roc flies up to half its Fly Speed without provoking Opportunity Attacks and drops that creature.
			"""
		),
		monster(
			name = "Roper",
			subtitle = "Large Aberration, Neutral Evil",
			ac = "20",
			initiative = "+5 (15)",
			hp = "93 (11d10 + 33)",
			speed = "10 ft., Climb 20 ft.",
			cr = "5 (XP 1,800; PB +3)",
			body = """
				STR 18 (+4 save +4), DEX 8 (-1 save -1), CON 17 (+3 save +3), INT 7 (-2 save -2), WIS 16 (+3 save +3), CHA 6 (-2 save -2)
				Skills: Perception +6, Stealth +5
				Senses: Darkvision 60 ft.; Passive Perception 16
				Languages: None
				Trait: Spider Climb. The roper can climb difficult surfaces, including along ceilings, without needing to make an ability check.
				Actions: Multiattack. The roper makes two Tentacle attacks, uses Reel, and makes two Bite attacks. Bite. Melee Attack Roll: +7, reach 5 ft. Hit: 17 (3d8 + 4) Piercing damage. Tentacle. Melee Attack Roll: +7, reach 60 ft. Hit: The target has the Grappled condition (escape DC 14) from one of six tentacles, and the target has the Poisoned condition until the grapple ends. The tentacle can be damaged, freeing a creature it has Grappled when destroyed (AC 20, HP 10, Immunity to Poison and Psychic damage). Damaging the tentacle deals no damage to the roper, and a destroyed tentacle regrows at the start of the roper's next turn. Reel. The roper pulls each creature Grappled by it up to 30 feet straight toward it.
			"""
		),
		monster(
			name = "Rust Monster",
			subtitle = "Medium Monstrosity, Unaligned",
			ac = "14",
			initiative = "+1 (11)",
			hp = "33 (6d8 + 6)",
			speed = "40 ft.",
			cr = "1/2 (XP 100; PB +2)",
			body = """
				STR 13 (+1 save +1), DEX 12 (+1 save +1), CON 13 (+1 save +1), INT 2 (-4 save -4), WIS 13 (+1 save +1), CHA 6 (-2 save -2)
				Senses: Darkvision 60 ft.; Passive Perception 11
				Languages: None
				Trait: Iron Scent. The rust monster can pinpoint the location of ferrous metal within 30 feet of itself.
				Actions: Multiattack. The rust monster makes one Bite attack and uses Antennae twice. Bite. Melee Attack Roll: +3, reach 5 ft. Hit: 5 (1d8 + 1) Piercing damage. Antennae. The rust monster targets one nonmagical metal object—armor or a weapon—worn or carried by a creature within 5 feet of itself. Dexterity Saving Throw: DC 11, the creature with the object. Failure: The object takes a -1 penalty to the AC it offers (armor) or to its attack rolls (weapon). Armor is destroyed if the penalty reduces its AC to 10, and a weapon is destroyed if its penalty reaches -5. The penalty can be removed by casting the Mending spell on the armor or weapon. Destroy Metal. The rust monster touches a nonmagical metal object within 5 feet of itself that isn't being worn or carried. The touch destroys a 1-foot Cube of the object.
				Reactions: Reflexive Antennae. Trigger: An attack roll hits the rust monster. Response: The rust monster uses Antennae.
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

