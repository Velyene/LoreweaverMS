package io.github.velyene.loreweaver.domain.util

object MonsterReferenceDataHtoN {
	val ENTRIES: List<MonsterReferenceEntry> = listOf(
		monster(
			name = "Guardian Naga",
			subtitle = "Large Celestial, Lawful Good",
			ac = "18",
			initiative = "+4 (14)",
			hp = "136 (16d10 + 48)",
			speed = "40 ft., Climb 40 ft., Swim 40 ft.",
			cr = "10 (XP 5,900; PB +4)",
			body = """
				STR 19 (+4 save +4), DEX 18 (+4 save +8), CON 16 (+3 save +7), INT 16 (+3 save +7), WIS 19 (+4 save +8), CHA 18 (+4 save +8)
				Skills: Arcana +11, History +11, Religion +11
				Immunities: Poison; Charmed, Paralyzed, Poisoned, Restrained
				Senses: Darkvision 60 ft.; Passive Perception 14
				Languages: Celestial, Common
				Trait: Celestial Restoration. If the naga dies, it returns to life in 1d6 days and regains all its Hit Points unless Dispel Evil and Good is cast on its remains.
				Actions: Multiattack. The naga makes two Bite attacks. It can replace any attack with a use of Poisonous Spittle. Bite. Melee Attack Roll: +8, reach 10 ft. Hit: 17 (2d12 + 4) Piercing damage plus 22 (4d10) Poison damage. Poisonous Spittle. Constitution Saving Throw: DC 16, one creature the naga can see within 60 feet. Failure: 31 (7d8) Poison damage, and the target has the Blinded condition until the start of the naga's next turn. Success: Half damage only. Spellcasting. The naga casts one of the following spells, requiring no Somatic or Material components and using Wisdom as the spellcasting ability (spell save DC 16): At Will: Thaumaturgy. 1/Day Each: Clairvoyance, Cure Wounds (level 6 version), Flame Strike (level 6 version), Geas, True Seeing.
			"""
		),
		monster(
			name = "Guard",
			subtitle = "Medium or Small Humanoid, Neutral",
			group = "Guards",
			ac = "16",
			initiative = "+1 (11)",
			hp = "11 (2d8 + 2)",
			speed = "30 ft.",
			cr = "1/8 (XP 25; PB +2)",
			body = """
				STR 13 (+1 save +1), DEX 12 (+1 save +1), CON 12 (+1 save +1), INT 10 (+0 save +0), WIS 11 (+0 save +0), CHA 10 (+0 save +0)
				Skills: Perception +2
				Gear: Chain Shirt, Shield, Spear
				Senses: Passive Perception 12
				Languages: Common
				Actions: Spear. Melee or Ranged Attack Roll: +3, reach 5 ft. or range 20/60 ft. Hit: 4 (1d6 + 1) Piercing damage.
			"""
		),
		monster(
			name = "Guard Captain",
			subtitle = "Medium or Small Humanoid, Neutral",
			group = "Guards",
			ac = "18",
			initiative = "+4 (14)",
			hp = "75 (10d8 + 30)",
			speed = "30 ft.",
			cr = "4 (XP 1,100; PB +2)",
			body = """
				STR 18 (+4 save +4), DEX 14 (+2 save +2), CON 16 (+3 save +3), INT 12 (+1 save +1), WIS 14 (+2 save +2), CHA 13 (+1 save +1)
				Skills: Athletics +6, Perception +4
				Gear: Breastplate, Javelins (6), Longsword, Shield
				Senses: Passive Perception 14
				Languages: Common
				Actions: Multiattack. The guard makes two attacks, using Javelin or Longsword in any combination. Javelin. Melee or Ranged Attack Roll: +6, reach 5 ft. or range 30/120 ft. Hit: 14 (3d6 + 4) Piercing damage. Longsword. Melee Attack Roll: +6, reach 5 ft. Hit: 15 (2d10 + 4) Slashing damage.
			"""
		),
		monster(
			name = "Half-Dragon",
			subtitle = "Medium Dragon, Neutral",
			ac = "18",
			initiative = "+5 (15)",
			hp = "105 (14d8 + 42)",
			speed = "40 ft.",
			cr = "5 (XP 1,800; PB +3)",
			body = """
				STR 19 (+4 save +4), DEX 14 (+2 save +5), CON 16 (+3 save +3), INT 10 (+0 save +0), WIS 15 (+2 save +5), CHA 14 (+2 save +2)
				Skills: Athletics +7, Perception +5, Stealth +5
				Resistances: Damage type chosen for the Draconic Origin trait below
				Senses: Blindsight 10 ft., Darkvision 60 ft.; Passive Perception 15
				Languages: Common, Draconic
				Trait: Draconic Origin. The half-dragon is related to a type of dragon associated with one of the following damage types (GM's choice): Acid, Cold, Fire, Lightning, or Poison. This choice affects other aspects of the stat block.
				Actions: Multiattack. The half-dragon makes two Claw attacks. Claw. Melee Attack Roll: +7, reach 10 ft. Hit: 6 (1d4 + 4) Slashing damage plus 7 (2d6) damage of the type chosen for the Draconic Origin trait. Dragon's Breath (Recharge 5-6). Dexterity Saving Throw: DC 14, each creature in a 30-foot Cone. Failure: 28 (8d6) damage of the type chosen for the Draconic Origin trait. Success: Half damage.
				Bonus Actions: Leap. The half-dragon jumps up to 30 feet by spending 10 feet of movement.
			"""
		),
		monster(
			name = "Harpy",
			subtitle = "Medium Monstrosity, Chaotic Evil",
			ac = "11",
			initiative = "+1 (11)",
			hp = "38 (7d8 + 7)",
			speed = "20 ft., Fly 40 ft.",
			cr = "1 (XP 200; PB +2)",
			body = """
				STR 12 (+1 save +1), DEX 13 (+1 save +1), CON 12 (+1 save +1), INT 7 (-2 save -2), WIS 10 (+0 save +0), CHA 13 (+1 save +1)
				Senses: Passive Perception 10
				Languages: Common
				Actions: Claw. Melee Attack Roll: +3, reach 5 ft. Hit: 6 (2d4 + 1) Slashing damage. Luring Song. The harpy sings a magical melody, which lasts until the harpy's Concentration ends on it. Wisdom Saving Throw: DC 11, each Humanoid and Giant in a 300-foot Emanation originating from the harpy when the song starts. Failure: The target has the Charmed condition until the song ends and repeats the save at the end of each of its turns. While Charmed, the target has the Incapacitated condition and ignores the Luring Song of other harpies. If the target is more than 5 feet from the harpy, the target moves on its turn toward the harpy by the most direct route, trying to get within 5 feet of the harpy. It doesn't avoid Opportunity Attacks; however, before moving into damaging terrain (such as lava or a pit) and whenever it takes damage from a source other than the harpy, the target repeats the save. Success: The target is immune to this harpy's Luring Song for 24 hours.
			"""
		),
		monster(
			name = "Hell Hound",
			subtitle = "Medium Fiend, Lawful Evil",
			ac = "15",
			initiative = "+1 (11)",
			hp = "58 (9d8 + 18)",
			speed = "50 ft.",
			cr = "3 (XP 700; PB +2)",
			body = """
				STR 17 (+3 save +3), DEX 12 (+1 save +1), CON 14 (+2 save +2), INT 6 (-2 save -2), WIS 13 (+1 save +1), CHA 6 (-2 save -2)
				Skills: Perception +5
				Immunities: Fire
				Senses: Darkvision 60 ft.; Passive Perception 15
				Languages: Understands Infernal but can't speak
				Trait: Pack Tactics. The hound has Advantage on an attack roll against a creature if at least one of the hound's allies is within 5 feet of the creature and the ally doesn't have the Incapacitated condition.
				Actions: Multiattack. The hound makes two Bite attacks. Bite. Melee Attack Roll: +5, reach 5 ft. Hit: 7 (1d8 + 3) Piercing damage plus 3 (1d6) Fire damage. Fire Breath (Recharge 5-6). Dexterity Saving Throw: DC 12, each creature in a 15-foot Cone. Failure: 17 (5d6) Fire damage. Success: Half damage.
			"""
		),
		monster(
			name = "Hezrou",
			subtitle = "Large Fiend (Demon), Chaotic Evil",
			ac = "18",
			initiative = "+6 (16)",
			hp = "157 (15d10 + 75)",
			speed = "30 ft.",
			cr = "8 (XP 3,900; PB +3)",
			body = """
				STR 19 (+4 save +7), DEX 17 (+3 save +3), CON 20 (+5 save +8), INT 5 (-3 save -3), WIS 12 (+1 save +4), CHA 13 (+1 save +1)
				Resistances: Cold, Fire, Lightning
				Immunities: Poison; Poisoned
				Senses: Darkvision 120 ft.; Passive Perception 11
				Languages: Abyssal; telepathy 120 ft.
				Traits: Demonic Restoration. If the hezrou dies outside the Abyss, its body dissolves into ichor, and it gains a new body instantly, reviving with all its Hit Points somewhere in the Abyss. Magic Resistance. The hezrou has Advantage on saving throws against spells and other magical effects. Stench. Constitution Saving Throw: DC 16, any creature that starts its turn in a 10-foot Emanation originating from the hezrou. Failure: The target has the Poisoned condition until the start of its next turn.
				Actions: Multiattack. The hezrou makes three Rend attacks. Rend. Melee Attack Roll: +7, reach 5 ft. Hit: 6 (1d4 + 4) Slashing damage plus 9 (2d8) Poison damage.
				Bonus Actions: Leap. The hezrou jumps up to 30 feet by spending 10 feet of movement.
			"""
		),
		monster(
			name = "Hill Giant",
			subtitle = "Huge Giant, Chaotic Evil",
			ac = "13",
			initiative = "+2 (12)",
			hp = "105 (10d12 + 40)",
			speed = "40 ft.",
			cr = "5 (XP 1,800; PB +3)",
			body = """
				STR 21 (+5 save +5), DEX 8 (-1 save -1), CON 19 (+4 save +4), INT 5 (-3 save -3), WIS 9 (-1 save -1), CHA 6 (-2 save -2)
				Skills: Perception +2
				Senses: Passive Perception 12
				Languages: Giant
				Actions: Multiattack. The giant makes two attacks, using Tree Club or Trash Lob in any combination. Tree Club. Melee Attack Roll: +8, reach 10 ft. Hit: 18 (3d8 + 5) Bludgeoning damage. If the target is a Large or smaller creature, it has the Prone condition. Trash Lob. Ranged Attack Roll: +8, range 60/240 ft. Hit: 16 (2d10 + 5) Bludgeoning damage, and the target has the Poisoned condition until the end of its next turn.
			"""
		),
		monster(
			name = "Hippogriff",
			subtitle = "Large Monstrosity, Unaligned",
			ac = "11",
			initiative = "+1 (11)",
			hp = "26 (4d10 + 4)",
			speed = "40 ft., Fly 60 ft.",
			cr = "1 (XP 200; PB +2)",
			body = """
				STR 17 (+3 save +3), DEX 13 (+1 save +1), CON 13 (+1 save +1), INT 2 (-4 save -4), WIS 12 (+1 save +1), CHA 8 (-1 save -1)
				Skills: Perception +5
				Senses: Passive Perception 15
				Languages: None
				Trait: Flyby. The hippogriff doesn't provoke an Opportunity Attack when it flies out of an enemy's reach.
				Actions: Multiattack. The hippogriff makes two Rend attacks. Rend. Melee Attack Roll: +5, reach 5 ft. Hit: 7 (1d8 + 3) Slashing damage.
			"""
		),
		monster(
			name = "Hobgoblin Warrior",
			subtitle = "Medium Fey (Goblinoid), Lawful Evil",
			group = "Hobgoblins",
			ac = "18",
			initiative = "+3 (13)",
			hp = "11 (2d8 + 2)",
			speed = "30 ft.",
			cr = "1/2 (XP 100; PB +2)",
			body = """
				STR 13 (+1 save +1), DEX 12 (+1 save +1), CON 12 (+1 save +1), INT 10 (+0 save +0), WIS 10 (+0 save +0), CHA 9 (-1 save -1)
				Gear: Half Plate Armor, Longbow, Longsword, Shield
				Senses: Darkvision 60 ft.; Passive Perception 10
				Languages: Common, Goblin
				Trait: Pack Tactics. The hobgoblin has Advantage on an attack roll against a creature if at least one of the hobgoblin's allies is within 5 feet of the creature and the ally doesn't have the Incapacitated condition.
				Actions: Longsword. Melee Attack Roll: +3, reach 5 ft. Hit: 12 (2d10 + 1) Slashing damage. Longbow. Ranged Attack Roll: +3, range 150/600 ft. Hit: 5 (1d8 + 1) Piercing damage plus 7 (3d4) Poison damage.
			"""
		),
		monster(
			name = "Hobgoblin Captain",
			subtitle = "Medium Fey (Goblinoid), Lawful Evil",
			group = "Hobgoblins",
			ac = "17",
			initiative = "+4 (14)",
			hp = "58 (9d8 + 18)",
			speed = "30 ft.",
			cr = "3 (XP 700; PB +2)",
			body = """
				STR 15 (+2 save +2), DEX 14 (+2 save +2), CON 14 (+2 save +2), INT 12 (+1 save +1), WIS 10 (+0 save +0), CHA 13 (+1 save +1)
				Gear: Greatsword, Half Plate Armor, Longbow
				Senses: Darkvision 60 ft.; Passive Perception 10
				Languages: Common, Goblin
				Trait: Aura of Authority. While in a 10-foot Emanation originating from the hobgoblin, the hobgoblin and its allies have Advantage on attack rolls and saving throws, provided the hobgoblin doesn't have the Incapacitated condition.
				Actions: Multiattack. The hobgoblin makes two attacks, using Greatsword or Longbow in any combination. Greatsword. Melee Attack Roll: +4, reach 5 ft. Hit: 9 (2d6 + 2) Slashing damage plus 3 (1d6) Poison damage. Longbow. Ranged Attack Roll: +4, range 150/600 ft. Hit: 6 (1d8 + 2) Piercing damage plus 5 (2d4) Poison damage.
			"""
		),
		monster(
			name = "Homunculus",
			subtitle = "Tiny Construct, Neutral",
			ac = "13",
			initiative = "+2 (12)",
			hp = "4 (1d4 + 2)",
			speed = "20 ft., Fly 40 ft.",
			cr = "0 (XP 10; PB +2)",
			body = """
				STR 4 (-3 save -3), DEX 15 (+2 save +2), CON 14 (+2 save +2), INT 10 (+0 save +0), WIS 10 (+0 save +2), CHA 7 (-2 save +0)
				Immunities: Poison; Charmed, Poisoned
				Senses: Darkvision 60 ft.; Passive Perception 10
				Languages: Understands Common plus one other language but can't speak
				Trait: Telepathic Bond. While the homunculus is on the same plane of existence as its master, the two of them can communicate telepathically with each other.
				Actions: Bite. Melee Attack Roll: +4, reach 5 ft. Hit: 1 Piercing damage, and the target is subjected to the following effect. Constitution Saving Throw: DC 12. Failure: The target has the Poisoned condition until the end of the homunculus's next turn. Failure by 5 or More: The target has the Poisoned condition for 1 minute. While Poisoned, the target has the Unconscious condition, which ends early if the target takes any damage.
			"""
		),
		monster(
			name = "Horned Devil",
			subtitle = "Large Fiend (Devil), Lawful Evil",
			ac = "18",
			initiative = "+7 (17)",
			hp = "199 (19d10 + 95)",
			speed = "30 ft., Fly 60 ft.",
			cr = "11 (XP 7,200; PB +4)",
			body = """
				STR 22 (+6 save +10), DEX 17 (+3 save +7), CON 21 (+5 save +5), INT 12 (+1 save +1), WIS 16 (+3 save +7), CHA 18 (+4 save +8)
				Resistances: Cold
				Immunities: Fire, Poison; Poisoned
				Senses: Darkvision 150 ft. (unimpeded by magical Darkness); Passive Perception 13
				Languages: Infernal; telepathy 120 ft.
				Traits: Diabolical Restoration. If the devil dies outside the Nine Hells, its body disappears in sulfurous smoke, and it gains a new body instantly, reviving with all its Hit Points somewhere in the Nine Hells. Magic Resistance. The devil has Advantage on saving throws against spells and other magical effects.
				Actions: Multiattack. The devil makes three attacks, using Searing Fork or Hurl Flame in any combination. It can replace one attack with a use of Infernal Tail. Searing Fork. Melee Attack Roll: +10, reach 10 ft. Hit: 15 (2d8 + 6) Piercing damage plus 9 (2d8) Fire damage. Hurl Flame. Ranged Attack Roll: +8, range 150 ft. Hit: 26 (5d8 + 4) Fire damage. If the target is a flammable object that isn't being worn or carried, it starts burning. Infernal Tail. Dexterity Saving Throw: DC 17, one creature the devil can see within 10 feet. Failure: 10 (1d8 + 6) Necrotic damage, and the target receives an infernal wound if it doesn't have one. While wounded, the target loses 10 (3d6) Hit Points at the start of each of its turns. The wound closes after 1 minute, after a spell restores Hit Points to the target, or after the target or a creature within 5 feet of it takes an action to stanch the wound, doing so by succeeding on a DC 17 Wisdom (Medicine) check.
			"""
		),
		monster(
			name = "Hydra",
			subtitle = "Huge Monstrosity, Unaligned",
			ac = "15",
			initiative = "+4 (14)",
			hp = "184 (16d12 + 80)",
			speed = "40 ft., Swim 40 ft.",
			cr = "8 (XP 3,900; PB +3)",
			body = """
				STR 20 (+5 save +5), DEX 12 (+1 save +1), CON 20 (+5 save +5), INT 2 (-4 save -4), WIS 10 (+0 save +0), CHA 7 (-2 save -2)
				Skills: Perception +6
				Immunities: Blinded, Charmed, Deafened, Frightened, Stunned, Unconscious
				Senses: Darkvision 60 ft.; Passive Perception 16
				Languages: None
				Traits: Hold Breath. The hydra can hold its breath for 1 hour. Multiple Heads. The hydra has five heads. Whenever the hydra takes 25 damage or more on a single turn, one of its heads dies. The hydra dies if all its heads are dead. At the end of each of its turns when it has at least one living head, the hydra grows two heads for each of its heads that died since its last turn, unless it has taken Fire damage since its last turn. The hydra regains 20 Hit Points when it grows new heads. Reactive Heads. For each head the hydra has beyond one, it gets an extra Reaction that can be used only for Opportunity Attacks.
				Actions: Multiattack. The hydra makes as many Bite attacks as it has heads. Bite. Melee Attack Roll: +8, reach 10 ft. Hit: 10 (1d10 + 5) Piercing damage.
			"""
		),
		monster(
			name = "Ice Devil",
			subtitle = "Large Fiend (Devil), Lawful Evil",
			ac = "18",
			initiative = "+7 (17)",
			hp = "228 (24d10 + 96)",
			speed = "40 ft.",
			cr = "14 (XP 11,500; PB +5)",
			body = """
				STR 21 (+5 save +5), DEX 14 (+2 save +7), CON 18 (+4 save +9), INT 18 (+4 save +4), WIS 15 (+2 save +7), CHA 18 (+4 save +9)
				Skills: Insight +7, Perception +7, Persuasion +9
				Immunities: Cold, Fire, Poison; Poisoned
				Senses: Blindsight 120 ft.; Passive Perception 17
				Languages: Infernal; telepathy 120 ft.
				Traits: Diabolical Restoration. If the devil dies outside the Nine Hells, its body disappears in sulfurous smoke, and it gains a new body instantly, reviving with all its Hit Points somewhere in the Nine Hells. Magic Resistance. The devil has Advantage on saving throws against spells and other magical effects.
				Actions: Multiattack. The devil makes three Ice Spear attacks. It can replace one attack with a Tail attack. Ice Spear. Melee or Ranged Attack Roll: +10, reach 5 ft. or range 30/120 ft. Hit: 14 (2d8 + 5) Piercing damage plus 10 (3d6) Cold damage. Until the end of its next turn, the target can't take a Bonus Action or Reaction, its Speed decreases by 10 feet, and it can move or take one action on its turn, not both. Hit or Miss: The spear magically returns to the devil's hand immediately after a ranged attack. Tail. Melee Attack Roll: +10, reach 10 ft. Hit: 15 (3d6 + 5) Bludgeoning damage plus 18 (4d8) Cold damage. Ice Wall (Recharge 6). The devil casts Wall of Ice (level 8 version), requiring no spell components and using Intelligence as the spellcasting ability (spell save DC 17).
			"""
		),
		monster(
			name = "Imp",
			subtitle = "Tiny Fiend (Devil), Lawful Evil",
			ac = "13",
			initiative = "+3 (13)",
			hp = "21 (6d4 + 6)",
			speed = "20 ft., Fly 40 ft.",
			cr = "1 (XP 200; PB +2)",
			body = """
				STR 6 (-2 save -2), DEX 17 (+3 save +3), CON 13 (+1 save +1), INT 11 (+0 save +0), WIS 12 (+1 save +1), CHA 14 (+2 save +2)
				Skills: Deception +4, Insight +3, Stealth +5
				Resistances: Cold
				Immunities: Fire, Poison; Poisoned
				Senses: Darkvision 120 ft. (unimpeded by magical Darkness); Passive Perception 11
				Languages: Common, Infernal
				Trait: Magic Resistance. The imp has Advantage on saving throws against spells and other magical effects.
				Actions: Sting. Melee Attack Roll: +5, reach 5 ft. Hit: 6 (1d6 + 3) Piercing damage plus 7 (2d6) Poison damage. Invisibility. The imp casts Invisibility on itself, requiring no spell components and using Charisma as the spellcasting ability. Shape-Shift. The imp shape-shifts to resemble a rat (Speed 20 ft.), a raven (20 ft., Fly 60 ft.), or a spider (20 ft., Climb 20 ft.), or it returns to its true form. Its game statistics are the same in each form, except for its Speed. Any equipment it is wearing or carrying isn't transformed.
			"""
		),
		monster(
			name = "Incubus",
			subtitle = "Medium Fiend, Neutral Evil",
			ac = "15",
			initiative = "+3 (13)",
			hp = "66 (12d8 + 12)",
			speed = "30 ft., Fly 60 ft.",
			cr = "4 (XP 1,100; PB +2)",
			body = """
				STR 8 (-1 save -1), DEX 17 (+3 save +3), CON 13 (+1 save +1), INT 15 (+2 save +2), WIS 12 (+1 save +1), CHA 20 (+5 save +5)
				Skills: Deception +9, Insight +5, Perception +5, Persuasion +9, Stealth +7
				Resistances: Cold, Fire, Poison, Psychic
				Senses: Darkvision 60 ft.; Passive Perception 15
				Languages: Abyssal, Common, Infernal; telepathy 60 ft.
				Trait: Succubus Form. When the incubus finishes a Long Rest, it can shape-shift into a Succubus, using that stat block instead of this one. Any equipment it is wearing or carrying isn't transformed.
				Actions: Multiattack. The incubus makes two Restless Touch attacks. Restless Touch. Melee Attack Roll: +7, reach 5 ft. Hit: 15 (3d6 + 5) Psychic damage, and the target is cursed for 24 hours or until the incubus dies. Until the curse ends, the target gains no benefit from finishing Short Rests. Spellcasting. The incubus casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 15): At Will: Disguise Self, Etherealness. 1/Day Each: Dream, Hypnotic Pattern.
				Bonus Actions: Nightmare (Recharge 6). Wisdom Saving Throw: DC 15, one creature the incubus can see within 60 feet. Failure: If the target has 20 Hit Points or fewer, it has the Unconscious condition for 1 hour, until it takes damage, or until a creature within 5 feet of it takes an action to wake it. Otherwise, the target takes 18 (4d8) Psychic damage.
			"""
		),
		monster(
			name = "Invisible Stalker",
			subtitle = "Large Elemental, Neutral",
			ac = "14",
			initiative = "+7 (22)",
			hp = "97 (13d10 + 26)",
			speed = "50 ft., Fly 50 ft. (hover)",
			cr = "6 (XP 2,300; PB +3)",
			body = """
				STR 16 (+3 save +3), DEX 19 (+4 save +4), CON 14 (+2 save +2), INT 10 (+0 save +0), WIS 15 (+2 save +2), CHA 11 (+0 save +0)
				Skills: Perception +8, Stealth +10
				Resistances: Bludgeoning, Piercing, Slashing
				Immunities: Poison; Exhaustion, Grappled, Paralyzed, Petrified, Poisoned, Prone, Restrained, Unconscious
				Senses: Darkvision 60 ft.; Passive Perception 18
				Languages: Common, Primordial (Auran)
				Traits: Air Form. The stalker can enter an enemy's space and stop there. It can move through a space as narrow as 1 inch without expending extra movement to do so. Invisibility. The stalker has the Invisible condition.
				Actions: Multiattack. The stalker makes three Wind Swipe attacks. It can replace one attack with a use of Vortex. Wind Swipe. Melee Attack Roll: +7, reach 5 ft. Hit: 11 (2d6 + 4) Force damage. Vortex. Constitution Saving Throw: DC 14, one Large or smaller creature in the stalker's space. Failure: 7 (1d8 + 3) Thunder damage, and the target has the Grappled condition (escape DC 13). Until the grapple ends, the target can't cast spells with a Verbal component and takes 7 (2d6) Thunder damage at the start of each of the stalker's turns.
			"""
		),
		monster(
			name = "Iron Golem",
			subtitle = "Large Construct, Unaligned",
			ac = "20",
			initiative = "+9 (19)",
			hp = "252 (24d10 + 120)",
			speed = "30 ft.",
			cr = "16 (XP 15,000; PB +5)",
			body = """
				STR 24 (+7 save +7), DEX 9 (-1 save -1), CON 20 (+5 save +5), INT 3 (-4 save -4), WIS 11 (+0 save +0), CHA 1 (-5 save -5)
				Immunities: Fire, Poison, Psychic; Charmed, Exhaustion, Frightened, Paralyzed, Petrified, Poisoned
				Senses: Darkvision 120 ft.; Passive Perception 10
				Languages: Understands Common plus two other languages but can't speak
				Traits: Fire Absorption. Whenever the golem is subjected to Fire damage, it regains a number of Hit Points equal to the Fire damage dealt. Immutable Form. The golem can't shape-shift. Magic Resistance. The golem has Advantage on saving throws against spells and other magical effects.
				Actions: Multiattack. The golem makes two attacks, using Bladed Arm or Fiery Bolt in any combination. Bladed Arm. Melee Attack Roll: +12, reach 10 ft. Hit: 20 (3d8 + 7) Slashing damage plus 10 (3d6) Fire damage. Fiery Bolt. Ranged Attack Roll: +10, range 120 ft. Hit: 36 (8d8) Fire damage. Poison Breath (Recharge 6). Constitution Saving Throw: DC 18, each creature in a 60-foot Cone. Failure: 55 (10d10) Poison damage. Success: Half damage.
			"""
		),
		monster(
			name = "Knight",
			subtitle = "Medium or Small Humanoid, Neutral",
			ac = "18",
			initiative = "+0 (10)",
			hp = "52 (8d8 + 16)",
			speed = "30 ft.",
			cr = "3 (XP 700; PB +2)",
			body = """
				STR 16 (+3 save +3), DEX 11 (+0 save +0), CON 14 (+2 save +4), INT 11 (+0 save +0), WIS 11 (+0 save +2), CHA 15 (+2 save +2)
				Immunities: Frightened
				Gear: Greatsword, Heavy Crossbow, Plate Armor
				Senses: Passive Perception 10
				Languages: Common plus one other language
				Actions: Multiattack. The knight makes two attacks, using Greatsword or Heavy Crossbow in any combination. Greatsword. Melee Attack Roll: +5, reach 5 ft. Hit: 10 (2d6 + 3) Slashing damage plus 4 (1d8) Radiant damage. Heavy Crossbow. Ranged Attack Roll: +2, range 100/400 ft. Hit: 11 (2d10) Piercing damage plus 4 (1d8) Radiant damage.
				Reactions: Parry. Trigger: The knight is hit by a melee attack roll while holding a weapon. Response: The knight adds 2 to its AC against that attack, possibly causing it to miss.
			"""
		),
		monster(
			name = "Kobold Warrior",
			subtitle = "Small Dragon, Neutral",
			group = "Kobold",
			ac = "14",
			initiative = "+2 (12)",
			hp = "7 (3d6 - 3)",
			speed = "30 ft.",
			cr = "1/8 (XP 25; PB +2)",
			body = """
				STR 7 (-2 save -2), DEX 15 (+2 save +2), CON 9 (-1 save -1), INT 8 (-1 save -1), WIS 7 (-2 save -2), CHA 8 (-1 save -1)
				Gear: Daggers (3)
				Senses: Darkvision 60 ft.; Passive Perception 8
				Languages: Common, Draconic
				Traits: Pack Tactics. The kobold has Advantage on an attack roll against a creature if at least one of the kobold's allies is within 5 feet of the creature and the ally doesn't have the Incapacitated condition. Sunlight Sensitivity. While in sunlight, the kobold has Disadvantage on ability checks and attack rolls.
				Actions: Dagger. Melee or Ranged Attack Roll: +4, reach 5 ft. or range 20/60 ft. Hit: 4 (1d4 + 2) Piercing damage.
			"""
		),
		monster(
			name = "Kraken",
			subtitle = "Gargantuan Monstrosity (Titan), Chaotic Evil",
			ac = "18",
			initiative = "+14 (24)",
			hp = "481 (26d20 + 208)",
			speed = "30 ft., Swim 120 ft.",
			cr = "23 (XP 50,000, or 62,000 in lair; PB +7)",
			body = """
				STR 30 (+10 save +17), DEX 11 (+0 save +7), CON 26 (+8 save +15), INT 22 (+6 save +6), WIS 18 (+4 save +11), CHA 20 (+5 save +5)
				Skills: History +13, Perception +11
				Immunities: Cold, Lightning; Frightened, Grappled, Paralyzed, Restrained
				Senses: Truesight 120 ft.; Passive Perception 21
				Languages: Understands Abyssal, Celestial, Infernal, and Primordial but can't speak; telepathy 120 ft.
				Traits: Amphibious. The kraken can breathe air and water. Legendary Resistance (4/Day, or 5/Day in Lair). If the kraken fails a saving throw, it can choose to succeed instead. Siege Monster. The kraken deals double damage to objects and structures.
				Actions: Multiattack. The kraken makes two Tentacle attacks and uses Fling, Lightning Strike, or Swallow. Tentacle. Melee Attack Roll: +17, reach 30 ft. Hit: 24 (4d6 + 10) Bludgeoning damage. The target has the Grappled condition (escape DC 20) from one of ten tentacles, and it has the Restrained condition until the grapple ends. Fling. The kraken throws a Large or smaller creature Grappled by it to a space it can see within 60 feet of itself that isn't in the air. Dexterity Saving Throw: DC 25, the creature thrown and each creature in the destination space. Failure: 18 (4d8) Bludgeoning damage, and the target has the Prone condition. Success: Half damage only. Lightning Strike. Dexterity Saving Throw: DC 23, one creature the kraken can see within 120 feet. Failure: 33 (6d10) Lightning damage. Success: Half damage. Swallow. Dexterity Saving Throw: DC 25, one creature Grappled by the kraken (it can have up to four creatures swallowed at a time). Failure: 23 (3d8 + 10) Piercing damage. If the target is Large or smaller, it is swallowed and no longer Grappled. A swallowed creature has the Restrained condition, has Total Cover against attacks and other effects outside the kraken, and takes 24 (7d6) Acid damage at the start of each of its turns. If the kraken takes 50 damage or more on a single turn from a creature inside it, the kraken must succeed on a DC 25 Constitution saving throw at the end of that turn or regurgitate all swallowed creatures, each of which falls in a space within 10 feet of the kraken with the Prone condition. If the kraken dies, any swallowed creature no longer has the Restrained condition and can escape from the corpse using 15 feet of movement, exiting Prone.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the kraken can expend a use to take one of the following actions. The kraken regains all expended uses at the start of each of its turns. Storm Bolt. The kraken uses Lightning Strike. Toxic Ink. Constitution Saving Throw: DC 23, each creature in a 15-foot Emanation originating from the kraken while it is underwater. Failure: The target has the Blinded and Poisoned conditions until the end of the kraken's next turn. The kraken then moves up to its Speed. Failure or Success: The kraken can't take this action again until the start of its next turn.
			"""
		),
		monster(
			name = "Lamia",
			subtitle = "Large Fiend, Chaotic Evil",
			ac = "13",
			initiative = "+1 (11)",
			hp = "97 (13d10 + 26)",
			speed = "40 ft.",
			cr = "4 (XP 1,100; PB +2)",
			body = """
				STR 16 (+3 save +3), DEX 13 (+1 save +1), CON 15 (+2 save +2), INT 14 (+2 save +2), WIS 15 (+2 save +2), CHA 16 (+3 save +3)
				Skills: Deception +7, Insight +4, Stealth +5
				Senses: Darkvision 60 ft.; Passive Perception 12
				Languages: Abyssal, Common
				Actions: Multiattack. The lamia makes two Claw attacks. It can replace one attack with a use of Corrupting Touch. Claw. Melee Attack Roll: +5, reach 5 ft. Hit: 7 (1d8 + 3) Slashing damage plus 7 (2d6) Psychic damage. Corrupting Touch. Wisdom Saving Throw: DC 13, one creature the lamia can see within 5 feet. Failure: 13 (3d8) Psychic damage, and the target is cursed for 1 hour. Until the curse ends, the target has the Charmed and Poisoned conditions. Spellcasting. The lamia casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 13): At Will: Disguise Self (can appear as a Large or Medium biped), Minor Illusion. 1/Day Each: Geas, Major Image, Scrying.
				Bonus Actions: Leap. The lamia jumps up to 30 feet by spending 10 feet of movement.
			"""
		),
		monster(
			name = "Lemure",
			subtitle = "Medium Fiend (Devil), Lawful Evil",
			ac = "9",
			initiative = "-3 (7)",
			hp = "9 (2d8)",
			speed = "20 ft.",
			cr = "0 (XP 10; PB +2)",
			body = """
				STR 10 (+0 save +0), DEX 5 (-3 save -3), CON 11 (+0 save +0), INT 1 (-5 save -5), WIS 11 (+0 save +0), CHA 3 (-4 save -4)
				Resistances: Cold
				Immunities: Fire, Poison; Charmed, Frightened, Poisoned
				Senses: Darkvision 120 ft. (unimpeded by magical Darkness); Passive Perception 10
				Languages: Understands Infernal but can't speak
				Trait: Hellish Restoration. If the lemure dies in the Nine Hells, it revives with all its Hit Points in 1d10 days unless it is killed by a creature under the effects of a Bless spell or its remains are sprinkled with Holy Water.
				Actions: Vile Slime. Melee Attack Roll: +2, reach 5 ft. Hit: 2 (1d4) Poison damage.
			"""
		),
		monster(
			name = "Lich",
			subtitle = "Medium Undead (Wizard), Neutral Evil",
			ac = "20",
			initiative = "+17 (27)",
			hp = "315 (42d8 + 126)",
			speed = "30 ft.",
			cr = "21 (XP 33,000, or 41,000 in lair; PB +7)",
			body = """
				STR 11 (+0 save +0), DEX 16 (+3 save +10), CON 16 (+3 save +10), INT 21 (+5 save +12), WIS 14 (+2 save +9), CHA 16 (+3 save +3)
				Skills: Arcana +19, History +12, Insight +9, Perception +9
				Resistances: Cold, Lightning
				Immunities: Necrotic, Poison; Charmed, Exhaustion, Frightened, Paralyzed, Poisoned
				Gear: Component Pouch
				Senses: Truesight 120 ft.; Passive Perception 19
				Languages: All
				Traits: Legendary Resistance (4/Day, or 5/Day in Lair). If the lich fails a saving throw, it can choose to succeed instead. Spirit Jar. If destroyed, the lich reforms in 1d10 days if it has a spirit jar, reviving with all its Hit Points. The new body appears in an unoccupied space within the lich's lair.
				Actions: Multiattack. The lich makes three attacks, using Eldritch Burst or Paralyzing Touch in any combination. Eldritch Burst. Melee or Ranged Attack Roll: +12, reach 5 ft. or range 120 ft. Hit: 31 (4d12 + 5) Force damage. Paralyzing Touch. Melee Attack Roll: +12, reach 5 ft. Hit: 15 (3d6 + 5) Cold damage, and the target has the Paralyzed condition until the start of the lich's next turn. Spellcasting. The lich casts one of the following spells, using Intelligence as the spellcasting ability (spell save DC 20): At Will: Detect Magic, Detect Thoughts, Dispel Magic, Fireball (level 5 version), Invisibility, Lightning Bolt (level 5 version), Mage Hand, Prestidigitation. 2/Day Each: Animate Dead, Dimension Door, Plane Shift. 1/Day Each: Chain Lightning, Finger of Death, Power Word Kill, Scrying.
				Reactions: Protective Magic. The lich casts Counterspell or Shield in response to the spell's trigger, using the same spellcasting ability as Spellcasting.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the lich can expend a use to take one of the following actions. The lich regains all expended uses at the start of each of its turns. Deathly Teleport. The lich teleports up to 60 feet to an unoccupied space it can see, and each creature within 10 feet of the space it left takes 11 (2d10) Necrotic damage. Disrupt Life. Constitution Saving Throw: DC 20, each creature that isn't an Undead in a 20-foot Emanation originating from the lich. Failure: 31 (9d6) Necrotic damage. Success: Half damage. Failure or Success: The lich can't take this action again until the start of its next turn. Frightening Gaze. The lich casts Fear, using the same spellcasting ability as Spellcasting. The lich can't take this action again until the start of its next turn.
			"""
		),
		monster(
			name = "Mage",
			subtitle = "Medium or Small Humanoid (Wizard), Neutral",
			group = "Mages",
			ac = "15",
			initiative = "+2 (12)",
			hp = "81 (18d8)",
			speed = "30 ft.",
			cr = "6 (XP 2,300; PB +3)",
			body = """
				STR 9 (-1 save -1), DEX 14 (+2 save +2), CON 11 (+0 save +0), INT 17 (+3 save +6), WIS 12 (+1 save +4), CHA 11 (+0 save +0)
				Skills: Arcana +6, History +6, Perception +4
				Gear: Wand
				Senses: Passive Perception 14
				Languages: Common plus three other languages
				Actions: Multiattack. The mage makes three Arcane Burst attacks. Arcane Burst. Melee or Ranged Attack Roll: +6, reach 5 ft. or range 120 ft. Hit: 16 (3d8 + 3) Force damage. Spellcasting. The mage casts one of the following spells, using Intelligence as the spellcasting ability (spell save DC 14): At Will: Detect Magic, Light, Mage Armor (included in AC), Mage Hand, Prestidigitation. 2/Day Each: Fireball (level 4 version), Invisibility. 1/Day Each: Cone of Cold, Fly.
				Bonus Actions: Misty Step (3/Day). The mage casts Misty Step, using the same spellcasting ability as Spellcasting.
				Reactions: Protective Magic (3/Day). The mage casts Counterspell or Shield in response to the spell's trigger, using the same spellcasting ability as Spellcasting.
			"""
		),
		monster(
			name = "Archmage",
			subtitle = "Medium or Small Humanoid (Wizard), Neutral",
			group = "Mages",
			ac = "17",
			initiative = "+7 (17)",
			hp = "170 (31d8 + 31)",
			speed = "30 ft.",
			cr = "12 (XP 8,000; PB +4)",
			body = """
				STR 10 (+0 save +0), DEX 14 (+2 save +2), CON 12 (+1 save +1), INT 20 (+5 save +9), WIS 15 (+2 save +6), CHA 16 (+3 save +3)
				Skills: Arcana +13, History +9, Perception +6
				Immunities: Psychic; Charmed (with Mind Blank)
				Gear: Wand
				Senses: Passive Perception 16
				Languages: Common plus five other languages
				Trait: Magic Resistance. The archmage has Advantage on saving throws against spells and other magical effects.
				Actions: Multiattack. The archmage makes four Arcane Burst attacks. Arcane Burst. Melee or Ranged Attack Roll: +9, reach 5 ft. or range 150 ft. Hit: 27 (4d10 + 5) Force damage. Spellcasting. The archmage casts one of the following spells, using Intelligence as the spellcasting ability (spell save DC 17): At Will: Detect Magic, Detect Thoughts, Disguise Self, Invisibility, Light, Mage Armor (included in AC), Mage Hand, Prestidigitation. 2/Day Each: Fly, Lightning Bolt (level 7 version). 1/Day Each: Cone of Cold (level 9 version), Mind Blank (cast before combat), Scrying, Teleport.
				Bonus Actions: Misty Step (3/Day). The mage casts Misty Step, using the same spellcasting ability as Spellcasting.
				Reactions: Protective Magic (3/Day). The archmage casts Counterspell or Shield in response to the spell's trigger, using the same spellcasting ability as Spellcasting.
			"""
		),
		monster(
			name = "Magmin",
			subtitle = "Small Elemental, Chaotic Neutral",
			ac = "14",
			initiative = "+2 (12)",
			hp = "13 (3d6 + 3)",
			speed = "30 ft.",
			cr = "1/2 (XP 100; PB +2)",
			body = """
				STR 7 (-2 save -2), DEX 15 (+2 save +2), CON 12 (+1 save +1), INT 8 (-1 save -1), WIS 11 (+0 save +0), CHA 10 (+0 save +0)
				Immunities: Fire
				Senses: Darkvision 60 ft.; Passive Perception 10
				Languages: Primordial (Ignan)
				Trait: Death Burst. The magmin explodes when it dies. Dexterity Saving Throw: DC 11, each creature in a 10-foot Emanation originating from the magmin. Failure: 7 (2d6) Fire damage. Success: Half damage.
				Actions: Touch. Melee Attack Roll: +4, reach 5 ft. Hit: 7 (2d4 + 2) Fire damage. If the target is a creature or a flammable object that isn't being worn or carried, it starts burning.
				Bonus Actions: Ignited Illumination. The magmin sets itself ablaze or extinguishes its flames. While ablaze, the magmin sheds Bright Light in a 10-foot radius and Dim Light for an additional 10 feet.
			"""
		),
		monster(
			name = "Manticore",
			subtitle = "Large Monstrosity, Lawful Evil",
			ac = "14",
			initiative = "+3 (13)",
			hp = "68 (8d10 + 24)",
			speed = "30 ft., Fly 50 ft.",
			cr = "3 (XP 700; PB +2)",
			body = """
				STR 17 (+3 save +3), DEX 16 (+3 save +3), CON 17 (+3 save +3), INT 7 (-2 save -2), WIS 12 (+1 save +1), CHA 8 (-1 save -1)
				Senses: Darkvision 60 ft.; Passive Perception 11
				Languages: Common
				Actions: Multiattack. The manticore makes three attacks, using Rend or Tail Spike in any combination. Rend. Melee Attack Roll: +5, reach 5 ft. Hit: 7 (1d8 + 3) Slashing damage. Tail Spike. Ranged Attack Roll: +5, range 100/200 ft. Hit: 7 (1d8 + 3) Piercing damage.
			"""
		),
		monster(
			name = "Marilith",
			subtitle = "Large Fiend (Demon), Chaotic Evil",
			ac = "16",
			initiative = "+10 (20)",
			hp = "220 (21d10 + 105)",
			speed = "40 ft., Climb 40 ft.",
			cr = "16 (XP 15,000; PB +5)",
			body = """
				STR 18 (+4 save +9), DEX 20 (+5 save +5), CON 20 (+5 save +10), INT 18 (+4 save +4), WIS 16 (+3 save +8), CHA 20 (+5 save +10)
				Skills: Perception +8
				Resistances: Cold, Fire, Lightning
				Immunities: Poison; Poisoned
				Senses: Truesight 120 ft.; Passive Perception 18
				Languages: Abyssal; telepathy 120 ft.
				Traits: Demonic Restoration. If the marilith dies outside the Abyss, its body dissolves into ichor, and it gains a new body instantly, reviving with all its Hit Points somewhere in the Abyss. Magic Resistance. The marilith has Advantage on saving throws against spells and other magical effects. Reactive. The marilith can take one Reaction on every turn of combat.
				Actions: Multiattack. The marilith makes six Pact Blade attacks and uses Constrict. Pact Blade. Melee Attack Roll: +10, reach 5 ft. Hit: 10 (1d10 + 5) Slashing damage plus 7 (2d6) Necrotic damage. Constrict. Strength Saving Throw: DC 17, one Medium or smaller creature the marilith can see within 5 feet. Failure: 15 (2d10 + 4) Bludgeoning damage. The target has the Grappled condition (escape DC 14), and it has the Restrained condition until the grapple ends.
				Bonus Actions: Teleport (Recharge 5-6). The marilith teleports up to 120 feet to an unoccupied space it can see.
				Reactions: Parry. Trigger: The marilith is hit by a melee attack roll while holding a weapon. Response: The marilith adds 5 to its AC against that attack, possibly causing it to miss.
			"""
		),
		monster(
			name = "Medusa",
			subtitle = "Medium Monstrosity, Lawful Evil",
			ac = "15",
			initiative = "+6 (16)",
			hp = "127 (17d8 + 51)",
			speed = "30 ft.",
			cr = "6 (XP 2,300; PB +3)",
			body = """
				STR 10 (+0 save +0), DEX 17 (+3 save +3), CON 16 (+3 save +3), INT 12 (+1 save +1), WIS 13 (+1 save +4), CHA 15 (+2 save +2)
				Skills: Deception +5, Perception +4, Stealth +6
				Senses: Darkvision 150 ft.; Passive Perception 14
				Languages: Common plus one other language
				Actions: Multiattack. The medusa makes two Claw attacks and one Snake Hair attack, or it makes three Poison Ray attacks. Claw. Melee Attack Roll: +6, reach 5 ft. Hit: 10 (2d6 + 3) Slashing damage. Snake Hair. Melee Attack Roll: +6, reach 5 ft. Hit: 5 (1d4 + 3) Piercing damage plus 14 (4d6) Poison damage. Poison Ray. Ranged Attack Roll: +5, range 150 ft. Hit: 11 (2d8 + 2) Poison damage.
				Bonus Actions: Petrifying Gaze (Recharge 5-6). Constitution Saving Throw: DC 13, each creature in a 30-foot Cone. If the medusa sees its reflection in the Cone, the medusa must make this save. First Failure: The target has the Restrained condition and repeats the save at the end of its next turn if it is still Restrained, ending the effect on itself on a success. Second Failure: The target has the Petrified condition instead of the Restrained condition.
			"""
		),
		monster(
			name = "Dust Mephit",
			subtitle = "Small Elemental, Neutral Evil",
			group = "Mephits",
			ac = "12",
			initiative = "+2 (12)",
			hp = "17 (5d6)",
			speed = "30 ft., Fly 30 ft.",
			cr = "1/2 (XP 100; PB +2)",
			body = """
				STR 5 (-3 save -3), DEX 14 (+2 save +2), CON 10 (+0 save +0), INT 9 (-1 save -1), WIS 11 (+0 save +0), CHA 10 (+0 save +0)
				Skills: Perception +2, Stealth +4
				Vulnerabilities: Fire
				Immunities: Poison; Exhaustion, Poisoned
				Senses: Darkvision 60 ft.; Passive Perception 12
				Languages: Primordial (Auran, Terran)
				Trait: Death Burst. The mephit explodes when it dies. Dexterity Saving Throw: DC 10, each creature in a 5-foot Emanation originating from the mephit. Failure: 5 (2d4) Bludgeoning damage. Success: Half damage.
				Actions: Claw. Melee Attack Roll: +4, reach 5 ft. Hit: 4 (1d4 + 2) Slashing damage. Blinding Breath (Recharge 6). Dexterity Saving Throw: DC 10, each creature in a 15-foot Cone. Failure: The target has the Blinded condition until the end of the mephit's next turn. Sleep (1/Day). The mephit casts the Sleep spell, requiring no spell components and using Charisma as the spellcasting ability (spell save DC 10).
			"""
		),
		monster(
			name = "Ice Mephit",
			subtitle = "Small Elemental, Neutral Evil",
			group = "Mephits",
			ac = "11",
			initiative = "+1 (11)",
			hp = "21 (6d6)",
			speed = "30 ft., Fly 30 ft.",
			cr = "1/2 (XP 100; PB +2)",
			body = """
				STR 7 (-2 save -2), DEX 13 (+1 save +1), CON 10 (+0 save +0), INT 9 (-1 save -1), WIS 11 (+0 save +0), CHA 12 (+1 save +1)
				Skills: Perception +2, Stealth +3
				Vulnerabilities: Fire
				Immunities: Cold, Poison; Exhaustion, Poisoned
				Senses: Darkvision 60 ft.; Passive Perception 12
				Languages: Primordial (Aquan, Auran)
				Trait: Death Burst. The mephit explodes when it dies. Constitution Saving Throw: DC 10, each creature in a 5-foot Emanation originating from the mephit. Failure: 5 (2d4) Cold damage. Success: Half damage.
				Actions: Claw. Melee Attack Roll: +3, reach 5 ft. Hit: 3 (1d4 + 1) Slashing damage plus 2 (1d4) Cold damage. Fog Cloud (1/Day). The mephit casts Fog Cloud, requiring no spell components and using Charisma as the spellcasting ability. Frost Breath (Recharge 6). Constitution Saving Throw: DC 10, each creature in a 15-foot Cone. Failure: 7 (3d4) Cold damage. Success: Half damage.
			"""
		),
		monster(
			name = "Magma Mephit",
			subtitle = "Small Elemental, Neutral Evil",
			group = "Mephits",
			ac = "11",
			initiative = "+1 (11)",
			hp = "18 (4d6 + 4)",
			speed = "30 ft., Fly 30 ft.",
			cr = "1/2 (XP 100; PB +2)",
			body = """
				STR 8 (-1 save -1), DEX 12 (+1 save +1), CON 12 (+1 save +1), INT 7 (-2 save -2), WIS 10 (+0 save +0), CHA 10 (+0 save +0)
				Skills: Stealth +3
				Vulnerabilities: Cold
				Immunities: Fire, Poison; Exhaustion, Poisoned
				Senses: Darkvision 60 ft.; Passive Perception 10
				Languages: Primordial (Ignan, Terran)
				Trait: Death Burst. The mephit explodes when it dies. Dexterity Saving Throw: DC 11, each creature in a 5-foot Emanation originating from the mephit. Failure: 7 (2d6) Fire damage. Success: Half damage.
				Actions: Claw. Melee Attack Roll: +3, reach 5 ft. Hit: 3 (1d4 + 1) Slashing damage plus 3 (1d6) Fire damage. Fire Breath (Recharge 6). Dexterity Saving Throw: DC 11, each creature in a 15-foot Cone. Failure: 7 (2d6) Fire damage. Success: Half damage.
			"""
		),
		monster(
			name = "Steam Mephit",
			subtitle = "Small Elemental, Neutral Evil",
			group = "Mephits",
			ac = "10",
			initiative = "+0 (10)",
			hp = "17 (5d6)",
			speed = "30 ft., Fly 30 ft.",
			cr = "1/4 (XP 50; PB +2)",
			body = """
				STR 5 (-3 save -3), DEX 11 (+0 save +0), CON 10 (+0 save +0), INT 11 (+0 save +0), WIS 10 (+0 save +0), CHA 12 (+1 save +1)
				Skills: Stealth +2
				Immunities: Fire, Poison; Exhaustion, Poisoned
				Senses: Darkvision 60 ft.; Passive Perception 10
				Languages: Primordial (Aquan, Ignan)
				Traits: Blurred Form. Attack rolls against the mephit are made with Disadvantage unless the mephit has the Incapacitated condition. Death Burst. The mephit explodes when it dies. Dexterity Saving Throw: DC 10, each creature in a 5-foot Emanation originating from the mephit. Failure: 5 (2d4) Fire damage. Success: Half damage.
				Actions: Claw. Melee Attack Roll: +2, reach 5 ft. Hit: 2 (1d4) Slashing damage plus 2 (1d4) Fire damage. Steam Breath (Recharge 6). Constitution Saving Throw: DC 10, each creature in a 15-foot Cone. Failure: 5 (2d4) Fire damage, and the target's Speed decreases by 10 feet until the end of the mephit's next turn. Success: Half damage only. Failure or Success: Being underwater doesn't grant Resistance to this Fire damage.
			"""
		),
		monster(
			name = "Merfolk Skirmisher",
			subtitle = "Medium Elemental, Neutral",
			group = "Merfolk",
			ac = "11",
			initiative = "+1 (11)",
			hp = "11 (2d8 + 2)",
			speed = "10 ft., Swim 40 ft.",
			cr = "1/8 (XP 25; PB +2)",
			body = """
				STR 10 (+0 save +0), DEX 13 (+1 save +1), CON 12 (+1 save +1), INT 11 (+0 save +0), WIS 14 (+2 save +2), CHA 12 (+1 save +1)
				Senses: Passive Perception 12
				Languages: Common, Primordial (Aquan)
				Trait: Amphibious. The merfolk can breathe air and water.
				Actions: Ocean Spear. Melee or Ranged Attack Roll: +2, reach 5 ft. or range 20/60 ft. Hit: 3 (1d6) Piercing damage plus 2 (1d4) Cold damage. If the target is a creature, its Speed decreases by 10 feet until the end of its next turn. Hit or Miss: The spear magically returns to the merfolk's hand immediately after a ranged attack.
			"""
		),
		monster(
			name = "Merrow",
			subtitle = "Large Monstrosity, Chaotic Evil",
			ac = "13",
			initiative = "+2 (12)",
			hp = "45 (6d10 + 12)",
			speed = "10 ft., Swim 40 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 18 (+4 save +4), DEX 15 (+2 save +2), CON 15 (+2 save +2), INT 8 (-1 save -1), WIS 10 (+0 save +0), CHA 9 (-1 save -1)
				Senses: Darkvision 60 ft.; Passive Perception 10
				Languages: Abyssal, Primordial (Aquan)
				Trait: Amphibious. The merrow can breathe air and water.
				Actions: Multiattack. The merrow makes two attacks, using Bite, Claw, or Harpoon in any combination. Bite. Melee Attack Roll: +6, reach 5 ft. Hit: 6 (1d4 + 4) Piercing damage, and the target has the Poisoned condition until the end of the merrow's next turn. Claw. Melee Attack Roll: +6, reach 5 ft. Hit: 9 (2d4 + 4) Slashing damage. Harpoon. Melee or Ranged Attack Roll: +6, reach 5 ft. or range 20/60 ft. Hit: 11 (2d6 + 4) Piercing damage. If the target is a Large or smaller creature, the merrow pulls the target up to 15 feet straight toward itself.
			"""
		),
		monster(
			name = "Mimic",
			subtitle = "Medium Monstrosity, Neutral",
			ac = "12",
			initiative = "+3 (13)",
			hp = "58 (9d8 + 18)",
			speed = "20 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 17 (+3 save +3), DEX 12 (+1 save +1), CON 15 (+2 save +2), INT 5 (-3 save -3), WIS 13 (+1 save +1), CHA 8 (-1 save -1)
				Skills: Stealth +5
				Immunities: Acid; Prone
				Senses: Darkvision 60 ft.; Passive Perception 11
				Languages: None
				Trait: Adhesive (Object Form Only). The mimic adheres to anything that touches it. A Huge or smaller creature adhered to the mimic has the Grappled condition (escape DC 13). Ability checks made to escape this grapple have Disadvantage.
				Actions: Bite. Melee Attack Roll: +5 (with Advantage if the target is Grappled by the mimic), reach 5 ft. Hit: 7 (1d8 + 3) Piercing damage—or 12 (2d8 + 3) Piercing damage if the target is Grappled by the mimic—plus 4 (1d8) Acid damage. Pseudopod. Melee Attack Roll: +5, reach 5 ft. Hit: 7 (1d8 + 3) Bludgeoning damage plus 4 (1d8) Acid damage. If the target is a Large or smaller creature, it has the Grappled condition (escape DC 13). Ability checks made to escape this grapple have Disadvantage.
				Bonus Actions: Shape-Shift. The mimic shape-shifts to resemble a Medium or Small object while retaining its game statistics, or it returns to its true blob form. Any equipment it is wearing or carrying isn't transformed.
			"""
		),
		monster(
			name = "Minotaur of Baphomet",
			subtitle = "Large Monstrosity, Chaotic Evil",
			ac = "14",
			initiative = "+0 (10)",
			hp = "85 (10d10 + 30)",
			speed = "40 ft.",
			cr = "3 (XP 700; PB +2)",
			body = """
				STR 18 (+4 save +4), DEX 11 (+0 save +0), CON 16 (+3 save +3), INT 6 (-2 save -2), WIS 16 (+3 save +3), CHA 9 (-1 save -1)
				Skills: Perception +7, Survival +7
				Senses: Darkvision 60 ft.; Passive Perception 17
				Languages: Abyssal
				Actions: Abyssal Glaive. Melee Attack Roll: +6, reach 10 ft. Hit: 10 (1d12 + 4) Slashing damage plus 10 (3d6) Necrotic damage. Gore (Recharge 5-6). Melee Attack Roll: +6, reach 5 ft. Hit: 18 (4d6 + 4) Piercing damage. If the target is a Large or smaller creature and the minotaur moved 10+ feet straight toward it immediately before the hit, the target takes an extra 10 (3d6) Piercing damage and has the Prone condition.
			"""
		),
		monster(
			name = "Mummy",
			subtitle = "Medium or Small Undead, Lawful Evil",
			group = "Mummies",
			ac = "11",
			initiative = "-1 (9)",
			hp = "58 (9d8 + 18)",
			speed = "20 ft.",
			cr = "3 (XP 700; PB +2)",
			body = """
				STR 16 (+3 save +3), DEX 8 (-1 save -1), CON 15 (+2 save +2), INT 6 (-2 save -2), WIS 12 (+1 save +3), CHA 12 (+1 save +1)
				Vulnerabilities: Fire
				Immunities: Necrotic, Poison; Charmed, Exhaustion, Frightened, Paralyzed, Poisoned
				Senses: Darkvision 60 ft.; Passive Perception 11
				Languages: Common plus two other languages
				Actions: Multiattack. The mummy makes two Rotting Fist attacks and uses Dreadful Glare. Rotting Fist. Melee Attack Roll: +5, reach 5 ft. Hit: 8 (1d10 + 3) Bludgeoning damage plus 10 (3d6) Necrotic damage. If the target is a creature, it is cursed. While cursed, the target can't regain Hit Points, its Hit Point maximum doesn't return to normal when finishing a Long Rest, and its Hit Point maximum decreases by 10 (3d6) every 24 hours that elapse. A creature dies and turns to dust if reduced to 0 Hit Points by this attack. Dreadful Glare. Wisdom Saving Throw: DC 11, one creature the mummy can see within 60 feet. Failure: The target has the Frightened condition until the end of the mummy's next turn. Success: The target is immune to this mummy's Dreadful Glare for 24 hours.
			"""
		),
		monster(
			name = "Mummy Lord",
			subtitle = "Medium or Small Undead (Cleric), Lawful Evil",
			group = "Mummies",
			ac = "17",
			initiative = "+10 (20)",
			hp = "187 (25d8 + 75)",
			speed = "30 ft.",
			cr = "15 (XP 13,000, or 15,000 in lair; PB +5)",
			body = """
				STR 18 (+4 save +4), DEX 10 (+0 save +0), CON 17 (+3 save +3), INT 11 (+0 save +5), WIS 19 (+4 save +9), CHA 16 (+3 save +3)
				Skills: History +5, Perception +9, Religion +5
				Vulnerabilities: Fire
				Immunities: Necrotic, Poison; Charmed, Exhaustion, Frightened, Paralyzed, Poisoned
				Senses: Truesight 60 ft.; Passive Perception 19
				Languages: Common plus three other languages
				Traits: Legendary Resistance (3/Day, or 4/Day in Lair). If the mummy fails a saving throw, it can choose to succeed instead. Magic Resistance. The mummy has Advantage on saving throws against spells and other magical effects. Undead Restoration. If destroyed, the mummy gains a new body in 24 hours if its heart is intact, reviving with all its Hit Points. The new body appears in an unoccupied space within the mummy's lair. The heart is a Tiny object that has AC 17, HP 10, and Immunity to all damage except Fire.
				Actions: Multiattack. The mummy makes one Rotting Fist or Channel Negative Energy attack, and it uses Dreadful Glare. Rotting Fist. Melee Attack Roll: +9, reach 5 ft. Hit: 15 (2d10 + 4) Bludgeoning damage plus 10 (3d6) Necrotic damage. If the target is a creature, it is cursed. While cursed, the target can't regain Hit Points, it gains no benefit from finishing a Long Rest, and its Hit Point maximum decreases by 10 (3d6) every 24 hours that elapse. A creature dies and turns to dust if reduced to 0 Hit Points by this attack. Channel Negative Energy. Ranged Attack Roll: +9, range 60 ft. Hit: 25 (6d6 + 4) Necrotic damage. Dreadful Glare. Wisdom Saving Throw: DC 17, one creature the mummy can see within 60 feet. Failure: 25 (6d6 + 4) Psychic damage, and the target has the Paralyzed condition until the end of the mummy's next turn. Spellcasting. The mummy casts one of the following spells, requiring no Material components and using Wisdom as the spellcasting ability (spell save DC 17, +9 to hit with spell attacks): At Will: Dispel Magic, Thaumaturgy. 1/Day Each: Animate Dead, Harm, Insect Plague (level 7 version).
				Reactions: Whirlwind of Sand. Trigger: The mummy is hit by an attack roll. Response: The mummy adds 2 to its AC against the attack, possibly causing the attack to miss, and the mummy teleports up to 60 feet to an unoccupied space it can see. Each creature of its choice that it can see within 5 feet of its destination space has the Blinded condition until the end of the mummy's next turn.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the mummy can expend a use to take one of the following actions. The mummy regains all expended uses at the start of each of its turns. Dread Command. The mummy casts Command (level 2 version), using the same spellcasting ability as Spellcasting. The mummy can't take this action again until the start of its next turn. Glare. The mummy uses Dreadful Glare. The mummy can't take this action again until the start of its next turn. Necrotic Strike. The mummy makes one Rotting Fist or Channel Negative Energy attack.
			"""
		),
		monster(
			name = "Nalfeshnee",
			subtitle = "Large Fiend (Demon), Chaotic Evil",
			ac = "18",
			initiative = "+5 (15)",
			hp = "184 (16d10 + 96)",
			speed = "20 ft., Fly 30 ft.",
			cr = "13 (XP 10,000; PB +5)",
			body = """
				STR 21 (+5 save +5), DEX 10 (+0 save +0), CON 22 (+6 save +11), INT 19 (+4 save +9), WIS 12 (+1 save +6), CHA 15 (+2 save +7)
				Resistances: Cold, Fire, Lightning
				Immunities: Poison; Frightened, Poisoned
				Senses: Truesight 120 ft.; Passive Perception 11
				Languages: Abyssal; telepathy 120 ft.
				Traits: Demonic Restoration. If the nalfeshnee dies outside the Abyss, its body dissolves into ichor, and it gains a new body instantly, reviving with all its Hit Points somewhere in the Abyss. Magic Resistance. The nalfeshnee has Advantage on saving throws against spells and other magical effects.
				Actions: Multiattack. The nalfeshnee makes three Rend attacks. Rend. Melee Attack Roll: +10, reach 10 ft. Hit: 16 (2d10 + 5) Slashing damage plus 11 (2d10) Force damage. Teleport. The nalfeshnee teleports up to 120 feet to an unoccupied space it can see.
				Bonus Actions: Horror Nimbus (Recharge 5-6). Wisdom Saving Throw: DC 15, each creature in a 15-foot Emanation originating from the nalfeshnee. Failure: 28 (8d6) Psychic damage, and the target has the Frightened condition for 1 minute, until it takes damage, or until it ends its turn with the nalfeshnee out of line of sight. Success: The target is immune to this nalfeshnee's Horror Nimbus for 24 hours.
				Reactions: Pursuit. Trigger: Another creature the nalfeshnee can see ends its move within 120 feet of the nalfeshnee. Response: The nalfeshnee uses Teleport, but its destination space must be within 10 feet of the triggering creature.
			"""
		),
		monster(
			name = "Night Hag",
			subtitle = "Medium Fiend, Neutral Evil",
			ac = "17",
			initiative = "+5 (15)",
			hp = "112 (15d8 + 45)",
			speed = "30 ft.",
			cr = "5 (XP 1,800; PB +3)",
			body = """
				STR 18 (+4 save +4), DEX 15 (+2 save +2), CON 16 (+3 save +3), INT 16 (+3 save +3), WIS 14 (+2 save +2), CHA 16 (+3 save +3)
				Skills: Deception +6, Insight +5, Perception +5, Stealth +5
				Resistances: Cold, Fire
				Immunities: Charmed
				Senses: Darkvision 120 ft.; Passive Perception 15
				Languages: Abyssal, Common, Infernal, Primordial
				Traits: Coven Magic. While within 30 feet of at least two hag allies, the hag can cast one of the following spells, requiring no Material components, using the spell's normal casting time, and using Intelligence as the spellcasting ability (spell save DC 14): Augury, Find Familiar, Identify, Locate Object, Scrying, or Unseen Servant. The hag must finish a Long Rest before using this trait to cast that spell again. Magic Resistance. The hag has Advantage on saving throws against spells and other magical effects. Soul Bag. The hag has a soul bag. While holding or carrying the bag, the hag can use its Nightmare Haunting action. The bag has AC 15, HP 20, and Resistance to all damage. The bag turns to dust if reduced to 0 Hit Points. If the bag is destroyed, any souls the bag is holding are released. The hag can create a new bag after 7 days.
				Actions: Multiattack. The hag makes two Claw attacks. Claw. Melee Attack Roll: +7, reach 5 ft. Hit: 13 (2d8 + 4) Slashing damage. Nightmare Haunting (1/Day; Requires Soul Bag). While on the Ethereal Plane, the hag casts Dream, using the same spellcasting ability as Spellcasting. Only the hag can serve as the spell's messenger, and the target must be a creature the hag can see on the Material Plane. The spell fails and is wasted if the target is under the effect of the Protection from Evil and Good spell or within a Magic Circle spell. If the target takes damage from the Dream spell, the target's Hit Point maximum decreases by an amount equal to that damage. If the spell kills the target, its soul is trapped in the hag's soul bag, and the target can't be raised from the dead until its soul is released. Spellcasting. The hag casts one of the following spells, requiring no Material components and using Intelligence as the spellcasting ability (spell save DC 14): At Will: Detect Magic, Etherealness, Magic Missile (level 4 version). 2/Day Each: Phantasmal Killer, Plane Shift (self only).
				Bonus Actions: Shape-Shift. The hag shape-shifts into a Small or Medium Humanoid, or it returns to its true form. Other than its size, its game statistics are the same in each form. Any equipment it is wearing or carrying isn't transformed.
			"""
		),
		monster(
			name = "Nightmare",
			subtitle = "Large Fiend, Neutral Evil",
			ac = "13",
			initiative = "+2 (12)",
			hp = "68 (8d10 + 24)",
			speed = "60 ft., Fly 90 ft. (hover)",
			cr = "3 (XP 700; PB +2)",
			body = """
				STR 18 (+4 save +4), DEX 15 (+2 save +2), CON 16 (+3 save +3), INT 10 (+0 save +0), WIS 13 (+1 save +1), CHA 15 (+2 save +2)
				Immunities: Fire
				Senses: Passive Perception 11
				Languages: Understands Abyssal, Common, and Infernal but can't speak
				Traits: Confer Fire Resistance. The nightmare can grant Resistance to Fire damage to a rider while it is on the nightmare. Illumination. The nightmare sheds Bright Light in a 10-foot radius and Dim Light for an additional 10 feet.
				Actions: Hooves. Melee Attack Roll: +6, reach 5 ft. Hit: 13 (2d8 + 4) Bludgeoning damage plus 10 (3d6) Fire damage. Ethereal Stride. The nightmare and up to three willing creatures within 5 feet of it teleport to the Ethereal Plane from the Material Plane or vice versa.
			"""
		),
		monster(
			name = "Noble",
			subtitle = "Medium or Small Humanoid, Neutral",
			ac = "15",
			initiative = "+1 (11)",
			hp = "9 (2d8)",
			speed = "30 ft.",
			cr = "1/8 (XP 25; PB +2)",
			body = """
				STR 11 (+0 save +0), DEX 12 (+1 save +1), CON 11 (+0 save +0), INT 12 (+1 save +1), WIS 14 (+2 save +2), CHA 16 (+3 save +3)
				Skills: Deception +5, Insight +4, Persuasion +5
				Gear: Breastplate, Rapier
				Senses: Passive Perception 12
				Languages: Common plus two other languages
				Actions: Rapier. Melee Attack Roll: +3, reach 5 ft. Hit: 5 (1d8 + 1) Piercing damage.
				Reactions: Parry. Trigger: The noble is hit by a melee attack roll while holding a weapon. Response: The noble adds 2 to its AC against that attack, possibly causing it to miss.
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

