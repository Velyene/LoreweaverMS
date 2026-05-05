/*
 * FILE: MonsterReferenceDataHtoN.kt
 *
 * TABLE OF CONTENTS:
 * 1. Object: MonsterReferenceDataHtoN
 * 2. Value: entriesHtoK
 * 3. Value: ENTRIES
 * 4. Function: monster
 */

package io.github.velyene.loreweaver.domain.util

object MonsterReferenceDataHtoN {
	private val entriesHtoK: List<MonsterReferenceEntry> = listOf(
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
		)
	)

	val ENTRIES: List<MonsterReferenceEntry> = entriesHtoK + MonsterReferenceDataLtoN.ENTRIES
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
