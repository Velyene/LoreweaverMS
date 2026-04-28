package io.github.velyene.loreweaver.domain.util

object MonsterReferenceDataAtoC {
	val ENTRIES: List<MonsterReferenceEntry> = listOf(
		monster(
			name = "Aboleth",
			subtitle = "Large Aberration, Lawful Evil",
			ac = "17",
			initiative = "+7 (17)",
			hp = "150 (20d10 + 40)",
			speed = "10 ft., Swim 40 ft.",
			cr = "10 (XP 5,900, or 7,200 in lair; PB +4)",
			body = """
				STR 21 (+5 save +5), DEX 9 (-1 save +3), CON 15 (+2 save +6), INT 18 (+4 save +8), WIS 15 (+2 save +6), CHA 18 (+4 save +4)
				Skills: History +12, Perception +10
				Senses: Darkvision 120 ft.; Passive Perception 20
				Languages: Deep Speech; telepathy 120 ft.
				Traits: Amphibious. The aboleth can breathe air and water. Eldritch Restoration. If destroyed, the aboleth gains a new body in 5d10 days, reviving with all its Hit Points in the Far Realm or another location chosen by the GM. Legendary Resistance (3/Day, or 4/Day in Lair). If the aboleth fails a saving throw, it can choose to succeed instead. Mucus Cloud. While underwater, the aboleth is surrounded by mucus. Constitution Saving Throw: DC 14, each creature in a 5-foot Emanation originating from the aboleth at the end of the aboleth's turn. Failure: The target is cursed. Until the curse ends, the target's skin becomes slimy, the target can breathe air and water, and it can't regain Hit Points unless it is underwater. While the cursed creature is outside a body of water, the creature takes 6 (1d12) Acid damage at the end of every 10 minutes unless moisture is applied to its skin before those minutes have passed. Probing Telepathy. If a creature the aboleth can see communicates telepathically with the aboleth, the aboleth learns the creature's greatest desires.
				Actions: Multiattack. The aboleth makes two Tentacle attacks and uses either Consume Memories or Dominate Mind if available. Tentacle. Melee Attack Roll: +9, reach 15 ft. Hit: 12 (2d6 + 5) Bludgeoning damage. If the target is a Large or smaller creature, it has the Grappled condition (escape DC 14) from one of four tentacles. Consume Memories. Intelligence Saving Throw: DC 16, one creature within 30 feet that is Charmed or Grappled by the aboleth. Failure: 10 (3d6) Psychic damage. Success: Half damage. Failure or Success: The aboleth gains the target's memories if the target is a Humanoid and is reduced to 0 Hit Points by this action. Dominate Mind (2/Day). Wisdom Saving Throw: DC 16, one creature the aboleth can see within 30 feet. Failure: The target has the Charmed condition until the aboleth dies or is on a different plane of existence from the target. While Charmed, the target acts as an ally to the aboleth and is under its control while within 60 feet of it. In addition, the aboleth and the target can communicate telepathically with each other over any distance. The target repeats the save whenever it takes damage as well as after every 24 hours it spends at least 1 mile away from the aboleth, ending the effect on itself on a success.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the aboleth can expend a use to take one of the following actions. The aboleth regains all expended uses at the start of each of its turns. Lash. The aboleth makes one Tentacle attack. Psychic Drain. If the aboleth has at least one creature Charmed or Grappled, it uses Consume Memories and regains 5 (1d10) Hit Points.
			"""
		),
		monster(
			name = "Air Elemental",
			subtitle = "Large Elemental, Neutral",
			ac = "15",
			initiative = "+5 (15)",
			hp = "90 (12d10 + 24)",
			speed = "10 ft., Fly 90 ft. (hover)",
			cr = "5 (XP 1,800; PB +3)",
			body = """
				STR 14 (+2 save +2), DEX 20 (+5 save +5), CON 14 (+2 save +2), INT 6 (-2 save -2), WIS 10 (+0 save +0), CHA 6 (-2 save -2)
				Resistances: Bludgeoning, Lightning, Piercing, Slashing
				Immunities: Poison, Thunder; Exhaustion, Grappled, Paralyzed, Petrified, Poisoned, Prone, Restrained, Unconscious
				Senses: Darkvision 60 ft.; Passive Perception 10
				Languages: Primordial (Auran)
				Trait: Air Form. The elemental can enter a creature's space and stop there. It can move through a space as narrow as 1 inch without expending extra movement to do so.
				Actions: Multiattack. The elemental makes two Thunderous Slam attacks. Thunderous Slam. Melee Attack Roll: +8, reach 10 ft. Hit: 14 (2d8 + 5) Thunder damage. Whirlwind (Recharge 4-6). Strength Saving Throw: DC 13, one Medium or smaller creature in the elemental's space. Failure: 24 (4d10 + 2) Thunder damage, and the target is pushed up to 20 feet straight away from the elemental and has the Prone condition. Success: Half damage only.
			"""
		),
		monster(
			name = "Animated Armor",
			subtitle = "Medium Construct, Unaligned",
			group = "Animated Objects",
			ac = "18",
			initiative = "+2 (12)",
			hp = "33 (6d8 + 6)",
			speed = "25 ft.",
			cr = "1 (XP 200; PB +2)",
			body = """
				STR 14 (+2 save +2), DEX 11 (+0 save +0), CON 13 (+1 save +1), INT 1 (-5 save -5), WIS 3 (-4 save -4), CHA 1 (-5 save -5)
				Immunities: Poison, Psychic; Charmed, Deafened, Exhaustion, Frightened, Paralyzed, Petrified, Poisoned
				Senses: Blindsight 60 ft.; Passive Perception 6
				Languages: None
				Actions: Multiattack. The armor makes two Slam attacks. Slam. Melee Attack Roll: +4, reach 5 ft. Hit: 5 (1d6 + 2) Bludgeoning damage.
			"""
		),
		monster(
			name = "Animated Flying Sword",
			subtitle = "Small Construct, Unaligned",
			group = "Animated Objects",
			ac = "17",
			initiative = "+4 (14)",
			hp = "14 (4d6)",
			speed = "5 ft., Fly 50 ft. (hover)",
			cr = "1/4 (XP 50; PB +2)",
			body = """
				STR 12 (+1 save +1), DEX 15 (+2 save +4), CON 11 (+0 save +0), INT 1 (-5 save -5), WIS 5 (-3 save -3), CHA 1 (-5 save -5)
				Immunities: Poison, Psychic; Charmed, Deafened, Exhaustion, Frightened, Paralyzed, Petrified, Poisoned
				Senses: Blindsight 60 ft.; Passive Perception 7
				Languages: None
				Actions: Slash. Melee Attack Roll: +4, reach 5 ft. Hit: 6 (1d8 + 2) Slashing damage.
			"""
		),
		monster(
			name = "Animated Rug of Smothering",
			subtitle = "Large Construct, Unaligned",
			group = "Animated Objects",
			ac = "12",
			initiative = "+4 (14)",
			hp = "27 (5d10)",
			speed = "10 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 17 (+3 save +3), DEX 14 (+2 save +2), CON 10 (+0 save +0), INT 1 (-5 save -5), WIS 3 (-4 save -4), CHA 1 (-5 save -5)
				Immunities: Poison, Psychic; Charmed, Deafened, Exhaustion, Frightened, Paralyzed, Petrified, Poisoned
				Senses: Blindsight 60 ft.; Passive Perception 6
				Languages: None
				Actions: Smother. Melee Attack Roll: +5, reach 5 ft. Hit: 10 (2d6 + 3) Bludgeoning damage. If the target is a Medium or smaller creature, the rug can give it the Grappled condition (escape DC 13) instead of dealing damage. Until the grapple ends, the target has the Blinded and Restrained conditions, is suffocating, and takes 10 (2d6 + 3) Bludgeoning damage at the start of each of its turns. The rug can smother only one creature at a time. While grappling the target, the rug can't take this action, the rug halves the damage it takes (round down), and the target takes the same amount of damage.
			"""
		),
		monster(
			name = "Ankheg",
			subtitle = "Large Monstrosity, Unaligned",
			ac = "14",
			initiative = "+0 (10)",
			hp = "45 (6d10 + 12)",
			speed = "30 ft., Burrow 10 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 17 (+3 save +3), DEX 11 (+0 save +0), CON 14 (+2 save +2), INT 1 (-5 save -5), WIS 13 (+1 save +1), CHA 6 (-2 save -2)
				Senses: Darkvision 60 ft., Tremorsense 60 ft.; Passive Perception 11
				Languages: None
				Trait: Tunneler. The ankheg can burrow through solid rock at half its Burrow Speed and leaves a 10-foot-diameter tunnel in its wake.
				Actions: Bite. Melee Attack Roll: +5 (with Advantage if the target is Grappled by the ankheg), reach 5 ft. Hit: 10 (2d6 + 3) Slashing damage plus 3 (1d6) Acid damage. If the target is a Large or smaller creature, it has the Grappled condition (escape DC 13). Acid Spray (Recharge 6). Dexterity Saving Throw: DC 12, each creature in a 30-foot-long, 5-foot-wide Line. Failure: 14 (4d6) Acid damage. Success: Half damage.
			"""
		),
		monster(
			name = "Assassin",
			subtitle = "Medium or Small Humanoid, Neutral",
			ac = "16",
			initiative = "+10 (20)",
			hp = "97 (15d8 + 30)",
			speed = "30 ft.",
			cr = "8 (XP 3,900; PB +3)",
			body = """
				STR 11 (+0 save +0), DEX 18 (+4 save +7), CON 14 (+2 save +2), INT 16 (+3 save +6), WIS 11 (+0 save +0), CHA 10 (+0 save +0)
				Skills: Acrobatics +7, Perception +6, Stealth +10
				Resistances: Poison
				Gear: Light Crossbow, Shortsword, Studded Leather Armor
				Senses: Passive Perception 16
				Languages: Common, Thieves' Cant
				Trait: Evasion. If the assassin is subjected to an effect that allows it to make a Dexterity saving throw to take only half damage, the assassin instead takes no damage if it succeeds on the save and only half damage if it fails. It can't use this trait if it has the Incapacitated condition.
				Actions: Multiattack. The assassin makes three attacks, using Shortsword or Light Crossbow in any combination. Shortsword. Melee Attack Roll: +7, reach 5 ft. Hit: 7 (1d6 + 4) Piercing damage plus 17 (5d6) Poison damage, and the target has the Poisoned condition until the start of the assassin's next turn. Light Crossbow. Ranged Attack Roll: +7, range 80/320 ft. Hit: 8 (1d8 + 4) Piercing damage plus 21 (6d6) Poison damage.
				Bonus Actions: Cunning Action. The assassin takes the Dash, Disengage, or Hide action.
			"""
		),
		monster(
			name = "Awakened Shrub",
			subtitle = "Small Plant, Neutral",
			group = "Awakened Plants",
			ac = "9",
			initiative = "-1 (9)",
			hp = "10 (3d6)",
			speed = "20 ft.",
			cr = "0 (XP 10; PB +2)",
			body = """
				STR 3 (-4 save -4), DEX 8 (-1 save -1), CON 11 (+0 save +0), INT 10 (+0 save +0), WIS 10 (+0 save +0), CHA 6 (-2 save -2)
				Vulnerabilities: Fire
				Resistances: Piercing
				Senses: Passive Perception 10
				Languages: Common plus one other language
				Actions: Rake. Melee Attack Roll: +1, reach 5 ft. Hit: 1 Slashing damage.
			"""
		),
		monster(
			name = "Awakened Tree",
			subtitle = "Huge Plant, Neutral",
			group = "Awakened Plants",
			ac = "13",
			initiative = "-2 (8)",
			hp = "59 (7d12 + 14)",
			speed = "20 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 19 (+4 save +4), DEX 6 (-2 save -2), CON 15 (+2 save +2), INT 10 (+0 save +0), WIS 10 (+0 save +0), CHA 7 (-2 save -2)
				Vulnerabilities: Fire
				Resistances: Bludgeoning, Piercing
				Senses: Passive Perception 10
				Languages: Common plus one other language
				Actions: Slam. Melee Attack Roll: +6, reach 10 ft. Hit: 14 (3d6 + 4) Bludgeoning damage.
			"""
		),
		monster(
			name = "Axe Beak",
			subtitle = "Large Monstrosity, Unaligned",
			ac = "11",
			initiative = "+1 (11)",
			hp = "19 (3d10 + 3)",
			speed = "50 ft.",
			cr = "1/4 (XP 50; PB +2)",
			body = """
				STR 14 (+2 save +2), DEX 12 (+1 save +1), CON 12 (+1 save +1), INT 2 (-4 save -4), WIS 10 (+0 save +0), CHA 5 (-3 save -3)
				Senses: Passive Perception 10
				Languages: None
				Actions: Beak. Melee Attack Roll: +4, reach 5 ft. Hit: 6 (1d8 + 2) Slashing damage.
			"""
		),
		monster(
			name = "Azer Sentinel",
			subtitle = "Medium Elemental, Lawful Neutral",
			ac = "17",
			initiative = "+1 (11)",
			hp = "39 (6d8 + 12)",
			speed = "30 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 17 (+3 save +3), DEX 12 (+1 save +1), CON 15 (+2 save +4), INT 12 (+1 save +1), WIS 13 (+1 save +1), CHA 10 (+0 save +0)
				Immunities: Fire, Poison; Poisoned
				Senses: Passive Perception 11
				Languages: Primordial (Ignan)
				Traits: Fire Aura. At the end of each of the azer's turns, each creature of the azer's choice in a 5-foot Emanation originating from the azer takes 5 (1d10) Fire damage unless the azer has the Incapacitated condition. Illumination. The azer sheds Bright Light in a 10-foot radius and Dim Light for an additional 10 feet.
				Actions: Burning Hammer. Melee Attack Roll: +5, reach 5 ft. Hit: 8 (1d10 + 3) Bludgeoning damage plus 3 (1d6) Fire damage.
			"""
		),
		monster(
			name = "Balor",
			subtitle = "Huge Fiend (Demon), Chaotic Evil",
			ac = "19",
			initiative = "+14 (24)",
			hp = "287 (23d12 + 138)",
			speed = "40 ft., Fly 80 ft.",
			cr = "19 (XP 22,000; PB +6)",
			body = """
				STR 26 (+8 save +8), DEX 15 (+2 save +2), CON 22 (+6 save +12), INT 20 (+5 save +5), WIS 16 (+3 save +9), CHA 22 (+6 save +6)
				Skills: Perception +9
				Resistances: Cold, Lightning
				Immunities: Fire, Poison; Charmed, Frightened, Poisoned
				Senses: Truesight 120 ft.; Passive Perception 19
				Languages: Abyssal; telepathy 120 ft.
				Traits: Death Throes. The balor explodes when it dies. Dexterity Saving Throw: DC 20, each creature in a 30-foot Emanation originating from the balor. Failure: 31 (9d6) Fire damage plus 31 (9d6) Force damage. Success: Half damage. Failure or Success: If the balor dies outside the Abyss, it gains a new body instantly, reviving with all its Hit Points somewhere in the Abyss. Fire Aura. At the end of each of the balor's turns, each creature in a 5-foot Emanation originating from the balor takes 13 (3d8) Fire damage. Legendary Resistance (3/Day). If the balor fails a saving throw, it can choose to succeed instead. Magic Resistance. The balor has Advantage on saving throws against spells and other magical effects.
				Actions: Multiattack. The balor makes one Flame Whip attack and one Lightning Blade attack. Flame Whip. Melee Attack Roll: +14, reach 30 ft. Hit: 18 (3d6 + 8) Force damage plus 17 (5d6) Fire damage. If the target is a Huge or smaller creature, the balor pulls the target up to 25 feet straight toward itself, and the target has the Prone condition. Lightning Blade. Melee Attack Roll: +14, reach 10 ft. Hit: 21 (3d8 + 8) Force damage plus 22 (4d10) Lightning damage, and the target can't take Reactions until the start of the balor's next turn.
				Bonus Actions: Teleport. The balor teleports itself or a willing demon within 10 feet of itself up to 60 feet to an unoccupied space the balor can see.
			"""
		),
		monster(
			name = "Bandit",
			subtitle = "Medium or Small Humanoid, Neutral",
			group = "Bandits",
			ac = "12",
			initiative = "+1 (11)",
			hp = "11 (2d8 + 2)",
			speed = "30 ft.",
			cr = "1/8 (XP 25; PB +2)",
			body = """
				STR 11 (+0 save +0), DEX 12 (+1 save +1), CON 12 (+1 save +1), INT 10 (+0 save +0), WIS 10 (+0 save +0), CHA 10 (+0 save +0)
				Gear: Leather Armor, Light Crossbow, Scimitar
				Senses: Passive Perception 10
				Languages: Common, Thieves' Cant
				Actions: Scimitar. Melee Attack Roll: +3, reach 5 ft. Hit: 4 (1d6 + 1) Slashing damage. Light Crossbow. Ranged Attack Roll: +3, range 80/320 ft. Hit: 5 (1d8 + 1) Piercing damage.
			"""
		),
		monster(
			name = "Bandit Captain",
			subtitle = "Medium or Small Humanoid, Neutral",
			group = "Bandits",
			ac = "15",
			initiative = "+3 (13)",
			hp = "52 (8d8 + 16)",
			speed = "30 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 15 (+2 save +4), DEX 16 (+3 save +5), CON 14 (+2 save +2), INT 14 (+2 save +2), WIS 11 (+0 save +2), CHA 14 (+2 save +2)
				Skills: Athletics +4, Deception +4
				Gear: Pistol, Scimitar, Studded Leather Armor
				Senses: Passive Perception 10
				Languages: Common, Thieves' Cant
				Actions: Multiattack. The bandit makes two attacks, using Scimitar and Pistol in any combination. Scimitar. Melee Attack Roll: +5, reach 5 ft. Hit: 6 (1d6 + 3) Slashing damage. Pistol. Ranged Attack Roll: +5, range 30/90 ft. Hit: 8 (1d10 + 3) Piercing damage.
				Reactions: Parry. Trigger: The bandit is hit by a melee attack roll while holding a weapon. Response: The bandit adds 2 to its AC against that attack, possibly causing it to miss.
			"""
		),
		monster(
			name = "Barbed Devil",
			subtitle = "Medium Fiend (Devil), Lawful Evil",
			ac = "15",
			initiative = "+3 (13)",
			hp = "110 (13d8 + 52)",
			speed = "30 ft., Climb 30 ft.",
			cr = "5 (XP 1,800; PB +3)",
			body = """
				STR 16 (+3 save +6), DEX 17 (+3 save +3), CON 18 (+4 save +7), INT 12 (+1 save +1), WIS 14 (+2 save +5), CHA 14 (+2 save +5)
				Skills: Deception +5, Insight +5, Perception +8
				Resistances: Cold
				Immunities: Fire, Poison; Poisoned
				Senses: Darkvision 120 ft. (unimpeded by magical Darkness); Passive Perception 18
				Languages: Infernal; telepathy 120 ft.
				Traits: Barbed Hide. At the start of each of its turns, the devil deals 5 (1d10) Piercing damage to any creature it is grappling or any creature grappling it. Diabolical Restoration. If the devil dies outside the Nine Hells, its body disappears in sulfurous smoke, and it gains a new body instantly, reviving with all its Hit Points somewhere in the Nine Hells. Magic Resistance. The devil has Advantage on saving throws against spells and other magical effects.
				Actions: Multiattack. The devil makes one Claws attack and one Tail attack, or it makes two Hurl Flame attacks. Claws. Melee Attack Roll: +6, reach 5 ft. Hit: 10 (2d6 + 3) Piercing damage. If the target is a Large or smaller creature, it has the Grappled condition (escape DC 13) from both claws. Tail. Melee Attack Roll: +6, reach 10 ft. Hit: 14 (2d10 + 3) Slashing damage. Hurl Flame. Ranged Attack Roll: +5, range 150 ft. Hit: 17 (5d6) Fire damage. If the target is a flammable object that isn't being worn or carried, it starts burning.
			"""
		),
		monster(
			name = "Basilisk",
			subtitle = "Medium Monstrosity, Unaligned",
			ac = "15",
			initiative = "-1 (9)",
			hp = "52 (8d8 + 16)",
			speed = "20 ft.",
			cr = "3 (XP 700; PB +2)",
			body = """
				STR 16 (+3 save +3), DEX 8 (-1 save -1), CON 15 (+2 save +2), INT 2 (-4 save -4), WIS 8 (-1 save -1), CHA 7 (-2 save -2)
				Senses: Darkvision 60 ft.; Passive Perception 9
				Languages: None
				Actions: Bite. Melee Attack Roll: +5, reach 5 ft. Hit: 10 (2d6 + 3) Piercing damage plus 7 (2d6) Poison damage.
				Bonus Actions: Petrifying Gaze (Recharge 4-6). Constitution Saving Throw: DC 12, each creature in a 30-foot Cone. If the basilisk sees its reflection in the Cone, the basilisk must make this save. First Failure: The target has the Restrained condition and repeats the save at the end of its next turn if it is still Restrained, ending the effect on itself on a success. Second Failure: The target has the Petrified condition instead of the Restrained condition.
			"""
		),
		monster(
			name = "Bearded Devil",
			subtitle = "Medium Fiend (Devil), Lawful Evil",
			ac = "13",
			initiative = "+2 (12)",
			hp = "58 (9d8 + 18)",
			speed = "30 ft.",
			cr = "3 (XP 700; PB +2)",
			body = """
				STR 16 (+3 save +5), DEX 15 (+2 save +2), CON 15 (+2 save +4), INT 9 (-1 save -1), WIS 11 (+0 save +0), CHA 14 (+2 save +4)
				Resistances: Cold
				Immunities: Fire, Poison; Frightened, Poisoned
				Senses: Darkvision 120 ft. (unimpeded by magical Darkness); Passive Perception 10
				Languages: Infernal; telepathy 120 ft.
				Trait: Magic Resistance. The devil has Advantage on saving throws against spells and other magical effects.
				Actions: Multiattack. The devil makes one Beard attack and one Infernal Glaive attack. Beard. Melee Attack Roll: +5, reach 5 ft. Hit: 7 (1d8 + 3) Piercing damage, and the target has the Poisoned condition until the start of the devil's next turn. Until this poison ends, the target can't regain Hit Points. Infernal Glaive. Melee Attack Roll: +5, reach 10 ft. Hit: 8 (1d10 + 3) Slashing damage. If the target is a creature and doesn't already have an infernal wound, it is subjected to the following effect. Constitution Saving Throw: DC 12. Failure: The target receives an infernal wound. While wounded, the target loses 5 (1d10) Hit Points at the start of each of its turns. The wound closes after 1 minute, after a spell restores Hit Points to the target, or after the target or a creature within 5 feet of it takes an action to stanch the wound, doing so by succeeding on a DC 12 Wisdom (Medicine) check.
			"""
		),
		monster(
			name = "Behir",
			subtitle = "Huge Monstrosity, Neutral Evil",
			ac = "17",
			initiative = "+3 (13)",
			hp = "168 (16d12 + 64)",
			speed = "50 ft., Climb 50 ft.",
			cr = "11 (XP 7,200; PB +4)",
			body = """
				STR 23 (+6 save +6), DEX 16 (+3 save +3), CON 18 (+4 save +4), INT 7 (-2 save -2), WIS 14 (+2 save +2), CHA 12 (+1 save +1)
				Skills: Perception +6, Stealth +7
				Immunities: Lightning
				Senses: Darkvision 90 ft.; Passive Perception 16
				Languages: Draconic
				Actions: Multiattack. The behir makes one Bite attack and uses Constrict. Bite. Melee Attack Roll: +10, reach 10 ft. Hit: 19 (2d12 + 6) Piercing damage plus 11 (2d10) Lightning damage. Constrict. Strength Saving Throw: DC 18, one Large or smaller creature the behir can see within 5 feet. Failure: 28 (5d8 + 6) Bludgeoning damage. The target has the Grappled condition (escape DC 16), and it has the Restrained condition until the grapple ends. Lightning Breath (Recharge 5-6). Dexterity Saving Throw: DC 16, each creature in a 90-foot-long, 5-foot-wide Line. Failure: 66 (12d10) Lightning damage. Success: Half damage.
				Bonus Actions: Swallow. Dexterity Saving Throw: DC 18, one Large or smaller creature Grappled by the behir (the behir can have only one creature swallowed at a time). Failure: The behir swallows the target, which is no longer Grappled. While swallowed, a creature has the Blinded and Restrained conditions, has Total Cover against attacks and other effects outside the behir, and takes 21 (6d6) Acid damage at the start of each of the behir's turns. If the behir takes 30 damage or more on a single turn from the swallowed creature, the behir must succeed on a DC 14 Constitution saving throw at the end of that turn or regurgitate the creature, which falls in a space within 10 feet of the behir and has the Prone condition. If the behir dies, a swallowed creature is no longer Restrained and can escape from the corpse by using 15 feet of movement, exiting Prone.
			"""
		),
		monster(
			name = "Berserker",
			subtitle = "Medium or Small Humanoid, Neutral",
			ac = "13",
			initiative = "+1 (11)",
			hp = "67 (9d8 + 27)",
			speed = "30 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 16 (+3 save +3), DEX 12 (+1 save +1), CON 17 (+3 save +3), INT 9 (-1 save -1), WIS 11 (+0 save +0), CHA 9 (-1 save -1)
				Gear: Greataxe, Hide Armor
				Senses: Passive Perception 10
				Languages: Common
				Trait: Bloodied Frenzy. While Bloodied, the berserker has Advantage on attack rolls and saving throws.
				Actions: Greataxe. Melee Attack Roll: +5, reach 5 ft. Hit: 9 (1d12 + 3) Slashing damage.
			"""
		),
		monster(
			name = "Black Dragon Wyrmling",
			subtitle = "Medium Dragon (Chromatic), Chaotic Evil",
			group = "Black Dragons",
			ac = "17",
			initiative = "+4 (14)",
			hp = "33 (6d8 + 6)",
			speed = "30 ft., Fly 60 ft., Swim 30 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 15 (+2 save +2), DEX 14 (+2 save +4), CON 13 (+1 save +1), INT 10 (+0 save +0), WIS 11 (+0 save +2), CHA 13 (+1 save +1)
				Skills: Perception +4, Stealth +4
				Immunities: Acid
				Senses: Blindsight 10 ft., Darkvision 60 ft.; Passive Perception 14
				Languages: Draconic
				Trait: Amphibious. The dragon can breathe air and water.
				Actions: Multiattack. The dragon makes two Rend attacks. Rend. Melee Attack Roll: +4, reach 5 ft. Hit: 5 (1d6 + 2) Slashing damage plus 2 (1d4) Acid damage. Acid Breath (Recharge 5-6). Dexterity Saving Throw: DC 11, each creature in a 15-foot-long, 5-foot-wide Line. Failure: 22 (5d8) Acid damage. Success: Half damage.
			"""
		),
		monster(
			name = "Young Black Dragon",
			subtitle = "Large Dragon (Chromatic), Chaotic Evil",
			group = "Black Dragons",
			ac = "18",
			initiative = "+5 (15)",
			hp = "127 (15d10 + 45)",
			speed = "40 ft., Fly 80 ft., Swim 40 ft.",
			cr = "7 (XP 2,900; PB +3)",
			body = """
				STR 19 (+4 save +4), DEX 14 (+2 save +5), CON 17 (+3 save +3), INT 12 (+1 save +1), WIS 11 (+0 save +3), CHA 15 (+2 save +2)
				Skills: Perception +6, Stealth +5
				Immunities: Acid
				Senses: Blindsight 30 ft., Darkvision 120 ft.; Passive Perception 16
				Languages: Common, Draconic
				Trait: Amphibious. The dragon can breathe air and water.
				Actions: Multiattack. The dragon makes three Rend attacks. Rend. Melee Attack Roll: +7, reach 10 ft. Hit: 9 (2d4 + 4) Slashing damage plus 3 (1d6) Acid damage. Acid Breath (Recharge 5-6). Dexterity Saving Throw: DC 14, each creature in a 30-foot-long, 5-foot-wide Line. Failure: 49 (14d6) Acid damage. Success: Half damage.
			"""
		),
		monster(
			name = "Adult Black Dragon",
			subtitle = "Huge Dragon (Chromatic), Chaotic Evil",
			group = "Black Dragons",
			ac = "19",
			initiative = "+12 (22)",
			hp = "195 (17d12 + 85)",
			speed = "40 ft., Fly 80 ft., Swim 40 ft.",
			cr = "14 (XP 11,500, or 13,000 in lair; PB +5)",
			body = """
				STR 23 (+6 save +6), DEX 14 (+2 save +7), CON 21 (+5 save +5), INT 14 (+2 save +2), WIS 13 (+1 save +6), CHA 19 (+4 save +4)
				Skills: Perception +11, Stealth +7
				Immunities: Acid
				Senses: Blindsight 60 ft., Darkvision 120 ft.; Passive Perception 21
				Languages: Common, Draconic
				Traits: Amphibious. The dragon can breathe air and water. Legendary Resistance (3/Day, or 4/Day in Lair). If the dragon fails a saving throw, it can choose to succeed instead.
				Actions: Multiattack. The dragon makes three Rend attacks. It can replace one attack with a use of Spellcasting to cast Acid Arrow (level 3 version). Rend. Melee Attack Roll: +11, reach 10 ft. Hit: 13 (2d6 + 6) Slashing damage plus 4 (1d8) Acid damage. Acid Breath (Recharge 5-6). Dexterity Saving Throw: DC 18, each creature in a 60-foot-long, 5-foot-wide Line. Failure: 54 (12d8) Acid damage. Success: Half damage. Spellcasting. The dragon casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 17, +9 to hit with spell attacks): At Will: Acid Arrow (level 3 version), Detect Magic, Fear. 1/Day Each: Speak with Dead, Vitriolic Sphere.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the dragon can expend a use to take one of the following actions. The dragon regains all expended uses at the start of each of its turns. Cloud of Insects. Dexterity Saving Throw: DC 17, one creature the dragon can see within 120 feet. Failure: 22 (4d10) Poison damage, and the target has Disadvantage on saving throws to maintain Concentration until the end of its next turn. Failure or Success: The dragon can't take this action again until the start of its next turn. Frightful Presence. The dragon uses Spellcasting to cast Fear. The dragon can't take this action again until the start of its next turn. Pounce. The dragon moves up to half its Speed, and it makes one Rend attack.
			"""
		),
		monster(
			name = "Ancient Black Dragon",
			subtitle = "Gargantuan Dragon (Chromatic), Chaotic Evil",
			group = "Black Dragons",
			ac = "22",
			initiative = "+16 (26)",
			hp = "367 (21d20 + 147)",
			speed = "40 ft., Fly 80 ft., Swim 40 ft.",
			cr = "21 (XP 33,000, or 41,000 in lair; PB +7)",
			body = """
				STR 27 (+8 save +8), DEX 14 (+2 save +9), CON 25 (+7 save +7), INT 16 (+3 save +3), WIS 15 (+2 save +9), CHA 22 (+6 save +6)
				Skills: Perception +16, Stealth +9
				Immunities: Acid
				Senses: Blindsight 60 ft., Darkvision 120 ft.; Passive Perception 26
				Languages: Common, Draconic
				Traits: Amphibious. The dragon can breathe air and water. Legendary Resistance (4/Day, or 5/Day in Lair). If the dragon fails a saving throw, it can choose to succeed instead.
				Actions: Multiattack. The dragon makes three Rend attacks. It can replace one attack with a use of Spellcasting to cast Acid Arrow (level 4 version). Rend. Melee Attack Roll: +15, reach 15 ft. Hit: 17 (2d8 + 8) Slashing damage plus 9 (2d8) Acid damage. Acid Breath (Recharge 5-6). Dexterity Saving Throw: DC 22, each creature in a 90-foot-long, 10-foot-wide Line. Failure: 67 (15d8) Acid damage. Success: Half damage. Spellcasting. The dragon casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 21, +13 to hit with spell attacks): At Will: Acid Arrow (level 4 version), Detect Magic, Fear. 1/Day Each: Create Undead, Speak with Dead, Vitriolic Sphere (level 5 version).
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the dragon can expend a use to take one of the following actions. The dragon regains all expended uses at the start of each of its turns. Cloud of Insects. Dexterity Saving Throw: DC 21, one creature the dragon can see within 120 feet. Failure: 33 (6d10) Poison damage, and the target has Disadvantage on saving throws to maintain Concentration until the end of its next turn. Failure or Success: The dragon can't take this action again until the start of its next turn. Frightful Presence. The dragon uses Spellcasting to cast Fear. The dragon can't take this action again until the start of its next turn. Pounce. The dragon moves up to half its Speed, and it makes one Rend attack.
			"""
		),
		monster(
			name = "Black Pudding",
			subtitle = "Large Ooze, Unaligned",
			ac = "7",
			initiative = "-3 (7)",
			hp = "68 (8d10 + 24)",
			speed = "20 ft., Climb 20 ft.",
			cr = "4 (XP 1,100; PB +2)",
			body = """
				STR 16 (+3 save +3), DEX 5 (-3 save -3), CON 16 (+3 save +3), INT 1 (-5 save -5), WIS 6 (-2 save -2), CHA 1 (-5 save -5)
				Immunities: Acid, Cold, Lightning, Slashing; Charmed, Deafened, Exhaustion, Frightened, Grappled, Prone, Restrained
				Senses: Blindsight 60 ft.; Passive Perception 8
				Languages: None
				Traits: Amorphous. The pudding can move through a space as narrow as 1 inch without expending extra movement to do so. Corrosive Form. A creature that hits the pudding with a melee attack roll takes 4 (1d8) Acid damage. Nonmagical ammunition is destroyed immediately after hitting the pudding and dealing any damage. Any nonmagical weapon takes a cumulative -1 penalty to attack rolls immediately after dealing damage to the pudding and coming into contact with it. The weapon is destroyed if the penalty reaches -5. The penalty can be removed by casting the Mending spell on the weapon. In 1 minute, the pudding can eat through 2 feet of nonmagical wood or metal. Spider Climb. The pudding can climb difficult surfaces, including along ceilings, without needing to make an ability check.
				Actions: Dissolving Pseudopod. Melee Attack Roll: +5, reach 10 ft. Hit: 17 (4d6 + 3) Acid damage. Nonmagical armor worn by the target takes a -1 penalty to the AC it offers. The armor is destroyed if the penalty reduces its AC to 10. The penalty can be removed by casting the Mending spell on the armor.
				Reactions: Split. Trigger: While the pudding is Large or Medium and has 10+ Hit Points, it becomes Bloodied or is subjected to Lightning or Slashing damage. Response: The pudding splits into two new Black Puddings. Each new pudding is one size smaller than the original pudding and acts on its Initiative. The original pudding's Hit Points are divided evenly between the new puddings (round down).
			"""
		),
		monster(
			name = "Blink Dog",
			subtitle = "Medium Fey, Lawful Good",
			ac = "13",
			initiative = "+3 (13)",
			hp = "22 (4d8 + 4)",
			speed = "40 ft.",
			cr = "1/4 (XP 50; PB +2)",
			body = """
				STR 12 (+1 save +1), DEX 17 (+3 save +3), CON 12 (+1 save +1), INT 10 (+0 save +0), WIS 13 (+1 save +1), CHA 11 (+0 save +0)
				Skills: Perception +5, Stealth +5
				Senses: Darkvision 60 ft.; Passive Perception 15
				Languages: Blink Dog; understands Elvish and Sylvan but can't speak them
				Actions: Bite. Melee Attack Roll: +5, reach 5 ft. Hit: 5 (1d4 + 3) Piercing damage.
				Bonus Actions: Teleport (Recharge 4-6). The dog teleports up to 40 feet to an unoccupied space it can see.
			"""
		),
		monster(
			name = "Blue Dragon Wyrmling",
			subtitle = "Medium Dragon (Chromatic), Lawful Evil",
			group = "Blue Dragons",
			ac = "17",
			initiative = "+2 (12)",
			hp = "65 (10d8 + 20)",
			speed = "30 ft., Burrow 15 ft., Fly 60 ft.",
			cr = "3 (XP 700; PB +2)",
			body = """
				STR 17 (+3 save +3), DEX 10 (+0 save +2), CON 15 (+2 save +2), INT 12 (+1 save +1), WIS 11 (+0 save +2), CHA 15 (+2 save +2)
				Skills: Perception +4, Stealth +2
				Immunities: Lightning
				Senses: Blindsight 10 ft., Darkvision 60 ft.; Passive Perception 14
				Languages: Draconic
				Actions: Multiattack. The dragon makes two Rend attacks. Rend. Melee Attack Roll: +5, reach 5 ft. Hit: 8 (1d10 + 3) Slashing damage plus 3 (1d6) Lightning damage. Lightning Breath (Recharge 5-6). Dexterity Saving Throw: DC 12, each creature in a 30-foot-long, 5-foot-wide Line. Failure: 21 (6d6) Lightning damage. Success: Half damage.
			"""
		),
		monster(
			name = "Young Blue Dragon",
			subtitle = "Large Dragon (Chromatic), Lawful Evil",
			group = "Blue Dragons",
			ac = "18",
			initiative = "+4 (14)",
			hp = "152 (16d10 + 64)",
			speed = "40 ft., Burrow 20 ft., Fly 80 ft.",
			cr = "9 (XP 5,000; PB +4)",
			body = """
				STR 21 (+5 save +5), DEX 10 (+0 save +4), CON 19 (+4 save +4), INT 14 (+2 save +2), WIS 13 (+1 save +5), CHA 17 (+3 save +3)
				Skills: Perception +9, Stealth +4
				Immunities: Lightning
				Senses: Blindsight 30 ft., Darkvision 120 ft.; Passive Perception 19
				Languages: Common, Draconic
				Actions: Multiattack. The dragon makes three Rend attacks. Rend. Melee Attack Roll: +9, reach 10 ft. Hit: 12 (2d6 + 5) Slashing damage plus 5 (1d10) Lightning damage. Lightning Breath (Recharge 5-6). Dexterity Saving Throw: DC 16, each creature in a 60-foot-long, 5-foot-wide Line. Failure: 55 (10d10) Lightning damage. Success: Half damage.
			"""
		),
		monster(
			name = "Adult Blue Dragon",
			subtitle = "Huge Dragon (Chromatic), Lawful Evil",
			group = "Blue Dragons",
			ac = "19",
			initiative = "+10 (20)",
			hp = "212 (17d12 + 102)",
			speed = "40 ft., Burrow 30 ft., Fly 80 ft.",
			cr = "16 (XP 15,000, or 18,000 in lair; PB +5)",
			body = """
				STR 25 (+7 save +7), DEX 10 (+0 save +5), CON 23 (+6 save +6), INT 16 (+3 save +3), WIS 15 (+2 save +7), CHA 20 (+5 save +5)
				Skills: Perception +12, Stealth +5
				Immunities: Lightning
				Senses: Blindsight 60 ft., Darkvision 120 ft.; Passive Perception 22
				Languages: Common, Draconic
				Trait: Legendary Resistance (3/Day, or 4/Day in Lair). If the dragon fails a saving throw, it can choose to succeed instead.
				Actions: Multiattack. The dragon makes three Rend attacks. It can replace one attack with a use of Spellcasting to cast Shatter. Rend. Melee Attack Roll: +12, reach 10 ft. Hit: 16 (2d8 + 7) Slashing damage plus 5 (1d10) Lightning damage. Lightning Breath (Recharge 5-6). Dexterity Saving Throw: DC 19, each creature in a 90-foot-long, 5-foot-wide Line. Failure: 60 (11d10) Lightning damage. Success: Half damage. Spellcasting. The dragon casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 18): At Will: Detect Magic, Invisibility, Mage Hand, Shatter. 1/Day Each: Scrying, Sending.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the dragon can expend a use to take one of the following actions. The dragon regains all expended uses at the start of each of its turns. Cloaked Flight. The dragon uses Spellcasting to cast Invisibility on itself, and it can fly up to half its Fly Speed. The dragon can't take this action again until the start of its next turn. Sonic Boom. The dragon uses Spellcasting to cast Shatter. The dragon can't take this action again until the start of its next turn. Tail Swipe. The dragon makes one Rend attack.
			"""
		),
		monster(
			name = "Ancient Blue Dragon",
			subtitle = "Gargantuan Dragon (Chromatic), Lawful Evil",
			group = "Blue Dragons",
			ac = "22",
			initiative = "+14 (24)",
			hp = "481 (26d20 + 208)",
			speed = "40 ft., Burrow 40 ft., Fly 80 ft.",
			cr = "23 (XP 50,000, or 62,000 in lair; PB +7)",
			body = """
				STR 29 (+9 save +9), DEX 10 (+0 save +7), CON 27 (+8 save +8), INT 18 (+4 save +4), WIS 17 (+3 save +10), CHA 25 (+7 save +7)
				Skills: Perception +17, Stealth +7
				Immunities: Lightning
				Senses: Blindsight 60 ft., Darkvision 120 ft.; Passive Perception 27
				Languages: Common, Draconic
				Trait: Legendary Resistance (4/Day, or 5/Day in Lair). If the dragon fails a saving throw, it can choose to succeed instead.
				Actions: Multiattack. The dragon makes three Rend attacks. It can replace one attack with a use of Spellcasting to cast Shatter (level 3 version). Rend. Melee Attack Roll: +16, reach 15 ft. Hit: 18 (2d8 + 9) Slashing damage plus 11 (2d10) Lightning damage. Lightning Breath (Recharge 5-6). Dexterity Saving Throw: DC 23, each creature in a 120-foot-long, 10-foot-wide Line. Failure: 88 (16d10) Lightning damage. Success: Half damage. Spellcasting. The dragon casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 22): At Will: Detect Magic, Invisibility, Mage Hand, Shatter (level 3 version). 1/Day Each: Scrying, Sending.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the dragon can expend a use to take one of the following actions. The dragon regains all expended uses at the start of each of its turns. Cloaked Flight. The dragon uses Spellcasting to cast Invisibility on itself, and it can fly up to half its Fly Speed. The dragon can't take this action again until the start of its next turn. Sonic Boom. The dragon uses Spellcasting to cast Shatter (level 3 version). The dragon can't take this action again until the start of its next turn. Tail Swipe. The dragon makes one Rend attack.
			"""
		),
		monster(
			name = "Bone Devil",
			subtitle = "Large Fiend (Devil), Lawful Evil",
			ac = "16",
			initiative = "+7 (17)",
			hp = "161 (17d10 + 68)",
			speed = "40 ft., Fly 40 ft.",
			cr = "9 (XP 5,000; PB +4)",
			body = """
				STR 18 (+4 save +8), DEX 16 (+3 save +3), CON 18 (+4 save +4), INT 13 (+1 save +5), WIS 14 (+2 save +6), CHA 16 (+3 save +7)
				Skills: Deception +7, Insight +6
				Resistances: Cold
				Immunities: Fire, Poison; Poisoned
				Senses: Darkvision 120 ft. (unimpeded by magical Darkness); Passive Perception 12
				Languages: Infernal; telepathy 120 ft.
				Traits: Diabolical Restoration. If the devil dies outside the Nine Hells, its body disappears in sulfurous smoke, and it gains a new body instantly, reviving with all its Hit Points somewhere in the Nine Hells. Magic Resistance. The devil has Advantage on saving throws against spells and other magical effects.
				Actions: Multiattack. The devil makes two Claw attacks and one Infernal Sting attack. Claw. Melee Attack Roll: +8, reach 10 ft. Hit: 13 (2d8 + 4) Slashing damage. Infernal Sting. Melee Attack Roll: +8, reach 10 ft. Hit: 15 (2d10 + 4) Piercing damage plus 18 (4d8) Poison damage, and the target has the Poisoned condition until the start of the devil's next turn. While Poisoned, the target can't regain Hit Points.
			"""
		),
		monster(
			name = "Brass Dragon Wyrmling",
			subtitle = "Medium Dragon (Metallic), Chaotic Good",
			group = "Brass Dragons",
			ac = "15",
			initiative = "+2 (12)",
			hp = "22 (4d8 + 4)",
			speed = "30 ft., Burrow 15 ft., Fly 60 ft.",
			cr = "1 (XP 200; PB +2)",
			body = """
				STR 15 (+2 save +2), DEX 10 (+0 save +2), CON 13 (+1 save +1), INT 10 (+0 save +0), WIS 11 (+0 save +2), CHA 13 (+1 save +1)
				Skills: Perception +4, Stealth +2
				Immunities: Fire
				Senses: Blindsight 10 ft., Darkvision 60 ft.; Passive Perception 14
				Languages: Draconic
				Actions: Rend. Melee Attack Roll: +4, reach 5 ft. Hit: 7 (1d10 + 2) Slashing damage. Fire Breath (Recharge 5-6). Dexterity Saving Throw: DC 11, each creature in a 20-foot-long, 5-foot-wide Line. Failure: 14 (4d6) Fire damage. Success: Half damage. Sleep Breath. Constitution Saving Throw: DC 11, each creature in a 15-foot Cone. Failure: The target has the Incapacitated condition until the end of its next turn, at which point it repeats the save. Second Failure: The target has the Unconscious condition for 1 minute. This effect ends for the target if it takes damage or a creature within 5 feet of it takes an action to wake it.
			"""
		),
		monster(
			name = "Young Brass Dragon",
			subtitle = "Large Dragon (Metallic), Chaotic Good",
			group = "Brass Dragons",
			ac = "17",
			initiative = "+3 (13)",
			hp = "110 (13d10 + 39)",
			speed = "40 ft., Burrow 20 ft., Fly 80 ft.",
			cr = "6 (XP 2,300; PB +3)",
			body = """
				STR 19 (+4 save +4), DEX 10 (+0 save +3), CON 17 (+3 save +3), INT 12 (+1 save +1), WIS 11 (+0 save +3), CHA 15 (+2 save +2)
				Skills: Perception +6, Persuasion +5, Stealth +3
				Immunities: Fire
				Senses: Blindsight 30 ft., Darkvision 120 ft.; Passive Perception 16
				Languages: Common, Draconic
				Actions: Multiattack. The dragon makes three Rend attacks. It can replace two attacks with a use of Sleep Breath. Rend. Melee Attack Roll: +7, reach 10 ft. Hit: 15 (2d10 + 4) Slashing damage. Fire Breath (Recharge 5-6). Dexterity Saving Throw: DC 14, each creature in a 40-foot-long, 5-foot-wide Line. Failure: 38 (11d6) Fire damage. Success: Half damage. Sleep Breath. Constitution Saving Throw: DC 14, each creature in a 30-foot Cone. Failure: The target has the Incapacitated condition until the end of its next turn, at which point it repeats the save. Second Failure: The target has the Unconscious condition for 1 minute. This effect ends for the target if it takes damage or a creature within 5 feet of it takes an action to wake it.
			"""
		),
		monster(
			name = "Adult Brass Dragon",
			subtitle = "Huge Dragon (Metallic), Chaotic Good",
			group = "Brass Dragons",
			ac = "18",
			initiative = "+10 (20)",
			hp = "172 (15d12 + 75)",
			speed = "40 ft., Burrow 30 ft., Fly 80 ft.",
			cr = "13 (XP 10,000, or 11,500 in lair; PB +5)",
			body = """
				STR 23 (+6 save +6), DEX 10 (+0 save +5), CON 21 (+5 save +5), INT 14 (+2 save +2), WIS 13 (+1 save +6), CHA 17 (+3 save +3)
				Skills: History +7, Perception +11, Persuasion +8, Stealth +5
				Immunities: Fire
				Senses: Blindsight 60 ft., Darkvision 120 ft.; Passive Perception 21
				Languages: Common, Draconic
				Trait: Legendary Resistance (3/Day, or 4/Day in Lair). If the dragon fails a saving throw, it can choose to succeed instead.
				Actions: Multiattack. The dragon makes three Rend attacks. It can replace one attack with a use of (A) Sleep Breath or (B) Spellcasting to cast Scorching Ray. Rend. Melee Attack Roll: +11, reach 10 ft. Hit: 17 (2d10 + 6) Slashing damage plus 4 (1d8) Fire damage. Fire Breath (Recharge 5-6). Dexterity Saving Throw: DC 18, each creature in a 60-foot-long, 5-foot-wide Line. Failure: 45 (10d8) Fire damage. Success: Half damage. Sleep Breath. Constitution Saving Throw: DC 18, each creature in a 60-foot Cone. Failure: The target has the Incapacitated condition until the end of its next turn, at which point it repeats the save. Second Failure: The target has the Unconscious condition for 10 minutes. This effect ends for the target if it takes damage or a creature within 5 feet of it takes an action to wake it. Spellcasting. The dragon casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 16): At Will: Detect Magic, Minor Illusion, Scorching Ray, Shapechange (Beast or Humanoid form only, no Temporary Hit Points gained from the spell, and no Concentration or Temporary Hit Points required to maintain the spell), Speak with Animals. 1/Day Each: Detect Thoughts, Control Weather.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the dragon can expend a use to take one of the following actions. The dragon regains all expended uses at the start of each of its turns. Blazing Light. The dragon uses Spellcasting to cast Scorching Ray. Pounce. The dragon moves up to half its Speed, and it makes one Rend attack. Scorching Sands. Dexterity Saving Throw: DC 16, one creature the dragon can see within 120 feet. Failure: 27 (6d8) Fire damage, and the target's Speed is halved until the end of its next turn. Failure or Success: The dragon can't take this action again until the start of its next turn.
			"""
		),
		monster(
			name = "Ancient Brass Dragon",
			subtitle = "Gargantuan Dragon (Metallic), Chaotic Good",
			group = "Brass Dragons",
			ac = "20",
			initiative = "+12 (22)",
			hp = "332 (19d20 + 133)",
			speed = "40 ft., Burrow 40 ft., Fly 80 ft.",
			cr = "20 (XP 25,000, or 33,000 in lair; PB +6)",
			body = """
				STR 27 (+8 save +8), DEX 10 (+0 save +6), CON 25 (+7 save +7), INT 16 (+3 save +3), WIS 15 (+2 save +8), CHA 22 (+6 save +6)
				Skills: History +9, Perception +14, Persuasion +12, Stealth +6
				Immunities: Fire
				Senses: Blindsight 60 ft., Darkvision 120 ft.; Passive Perception 24
				Languages: Common, Draconic
				Trait: Legendary Resistance (4/Day, or 5/Day in Lair). If the dragon fails a saving throw, it can choose to succeed instead.
				Actions: Multiattack. The dragon makes three Rend attacks. It can replace one attack with a use of (A) Sleep Breath or (B) Spellcasting to cast Scorching Ray (level 3 version). Rend. Melee Attack Roll: +14, reach 15 ft. Hit: 19 (2d10 + 8) Slashing damage plus 7 (2d6) Fire damage. Fire Breath (Recharge 5-6). Dexterity Saving Throw: DC 21, each creature in a 90-foot-long, 5-foot-wide Line. Failure: 58 (13d8) Fire damage. Success: Half damage. Sleep Breath. Constitution Saving Throw: DC 21, each creature in a 90-foot Cone. Failure: The target has the Incapacitated condition until the end of its next turn, at which point it repeats the save. Second Failure: The target has the Unconscious condition for 10 minutes. This effect ends for the target if it takes damage or a creature within 5 feet of it takes an action to wake it. Spellcasting. The dragon casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 20): At Will: Detect Magic, Minor Illusion, Scorching Ray (level 3 version), Shapechange (Beast or Humanoid form only, no Temporary Hit Points gained from the spell, and no Concentration or Temporary Hit Points required to maintain the spell), Speak with Animals. 1/Day Each: Control Weather, Detect Thoughts.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the dragon can expend a use to take one of the following actions. The dragon regains all expended uses at the start of each of its turns. Blazing Light. The dragon uses Spellcasting to cast Scorching Ray (level 3 version). Pounce. The dragon moves up to half its Speed, and it makes one Rend attack. Scorching Sands. Dexterity Saving Throw: DC 20, one creature the dragon can see within 120 feet. Failure: 36 (8d8) Fire damage, and the target's Speed is halved until the end of its next turn. Failure or Success: The dragon can't take this action again until the start of its next turn.
			"""
		),
		monster(
			name = "Bronze Dragon Wyrmling",
			subtitle = "Medium Dragon (Metallic), Lawful Good",
			group = "Bronze Dragons",
			ac = "15",
			initiative = "+2 (12)",
			hp = "39 (6d8 + 12)",
			speed = "30 ft., Fly 60 ft., Swim 30 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 17 (+3 save +3), DEX 10 (+0 save +2), CON 15 (+2 save +2), INT 12 (+1 save +1), WIS 11 (+0 save +2), CHA 15 (+2 save +2)
				Skills: Perception +4, Stealth +2
				Immunities: Lightning
				Senses: Blindsight 10 ft., Darkvision 60 ft.; Passive Perception 14
				Languages: Draconic
				Trait: Amphibious. The dragon can breathe air and water.
				Actions: Multiattack. The dragon makes two Rend attacks. Rend. Melee Attack Roll: +5, reach 5 ft. Hit: 8 (1d10 + 3) Slashing damage. Lightning Breath (Recharge 5-6). Dexterity Saving Throw: DC 12, each creature in a 40-foot-long, 5-foot-wide Line. Failure: 16 (3d10) Lightning damage. Success: Half damage. Repulsion Breath. Strength Saving Throw: DC 12, each creature in a 30-foot Cone. Failure: The target is pushed up to 30 feet straight away from the dragon and has the Prone condition.
			"""
		),
		monster(
			name = "Young Bronze Dragon",
			subtitle = "Large Dragon (Metallic), Lawful Good",
			group = "Bronze Dragons",
			ac = "17",
			initiative = "+3 (13)",
			hp = "142 (15d10 + 60)",
			speed = "40 ft., Fly 80 ft., Swim 40 ft.",
			cr = "8 (XP 3,900; PB +3)",
			body = """
				STR 21 (+5 save +5), DEX 10 (+0 save +3), CON 19 (+4 save +4), INT 14 (+2 save +2), WIS 13 (+1 save +4), CHA 17 (+3 save +3)
				Skills: Insight +4, Perception +7, Stealth +3
				Immunities: Lightning
				Senses: Blindsight 30 ft., Darkvision 120 ft.; Passive Perception 17
				Languages: Common, Draconic
				Trait: Amphibious. The dragon can breathe air and water.
				Actions: Multiattack. The dragon makes three Rend attacks. It can replace one attack with a use of Repulsion Breath. Rend. Melee Attack Roll: +8, reach 10 ft. Hit: 16 (2d10 + 5) Slashing damage. Lightning Breath (Recharge 5-6). Dexterity Saving Throw: DC 15, each creature in a 60-foot-long, 5-foot-wide Line. Failure: 49 (9d10) Lightning damage. Success: Half damage. Repulsion Breath. Strength Saving Throw: DC 15, each creature in a 30-foot Cone. Failure: The target is pushed up to 40 feet straight away from the dragon and has the Prone condition.
			"""
		),
		monster(
			name = "Adult Bronze Dragon",
			subtitle = "Huge Dragon (Metallic), Lawful Good",
			group = "Bronze Dragons",
			ac = "18",
			initiative = "+10 (20)",
			hp = "212 (17d12 + 102)",
			speed = "40 ft., Fly 80 ft., Swim 40 ft.",
			cr = "15 (XP 13,000, or 15,000 in lair; PB +5)",
			body = """
				STR 25 (+7 save +7), DEX 10 (+0 save +5), CON 23 (+6 save +6), INT 16 (+3 save +3), WIS 15 (+2 save +7), CHA 20 (+5 save +5)
				Skills: Insight +7, Perception +12, Stealth +5
				Immunities: Lightning
				Senses: Blindsight 60 ft., Darkvision 120 ft.; Passive Perception 22
				Languages: Common, Draconic
				Traits: Amphibious. The dragon can breathe air and water. Legendary Resistance (3/Day, or 4/Day in Lair). If the dragon fails a saving throw, it can choose to succeed instead.
				Actions: Multiattack. The dragon makes three Rend attacks. It can replace one attack with a use of (A) Repulsion Breath or (B) Spellcasting to cast Guiding Bolt (level 2 version). Rend. Melee Attack Roll: +12, reach 10 ft. Hit: 16 (2d8 + 7) Slashing damage plus 5 (1d10) Lightning damage. Lightning Breath (Recharge 5-6). Dexterity Saving Throw: DC 19, each creature in a 90-foot-long, 5-foot-wide Line. Failure: 55 (10d10) Lightning damage. Success: Half damage. Repulsion Breath. Strength Saving Throw: DC 19, each creature in a 30-foot Cone. Failure: The target is pushed up to 60 feet straight away from the dragon and has the Prone condition. Spellcasting. The dragon casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 17, +10 to hit with spell attacks): At Will: Detect Magic, Guiding Bolt (level 2 version), Shapechange (Beast or Humanoid form only, no Temporary Hit Points gained from the spell, and no Concentration or Temporary Hit Points required to maintain the spell), Speak with Animals, Thaumaturgy. 1/Day Each: Detect Thoughts, Water Breathing.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the dragon can expend a use to take one of the following actions. The dragon regains all expended uses at the start of each of its turns. Guiding Light. The dragon uses Spellcasting to cast Guiding Bolt (level 2 version). Pounce. The dragon moves up to half its Speed, and it makes one Rend attack. Thunderclap. Constitution Saving Throw: DC 17, each creature in a 20-foot-radius Sphere centered on a point the dragon can see within 90 feet. Failure: 10 (3d6) Thunder damage, and the target has the Deafened condition until the end of its next turn.
			"""
		),
		monster(
			name = "Ancient Bronze Dragon",
			subtitle = "Gargantuan Dragon (Metallic), Lawful Good",
			group = "Bronze Dragons",
			ac = "22",
			initiative = "+14 (24)",
			hp = "444 (24d20 + 192)",
			speed = "40 ft., Fly 80 ft., Swim 40 ft.",
			cr = "22 (XP 41,000, or 50,000 in lair; PB +7)",
			body = """
				STR 29 (+9 save +9), DEX 10 (+0 save +7), CON 27 (+8 save +8), INT 18 (+4 save +4), WIS 17 (+3 save +10), CHA 25 (+7 save +7)
				Skills: Insight +10, Perception +17, Stealth +7
				Immunities: Lightning
				Senses: Blindsight 60 ft., Darkvision 120 ft.; Passive Perception 27
				Languages: Common, Draconic
				Traits: Amphibious. The dragon can breathe air and water. Legendary Resistance (4/Day, or 5/Day in Lair). If the dragon fails a saving throw, it can choose to succeed instead.
				Actions: Multiattack. The dragon makes three Rend attacks. It can replace one attack with a use of (A) Repulsion Breath or (B) Spellcasting to cast Guiding Bolt (level 2 version). Rend. Melee Attack Roll: +16, reach 15 ft. Hit: 18 (2d8 + 9) Slashing damage plus 9 (2d8) Lightning damage. Lightning Breath (Recharge 5-6). Dexterity Saving Throw: DC 23, each creature in a 120-foot-long, 10-foot-wide Line. Failure: 82 (15d10) Lightning damage. Success: Half damage. Repulsion Breath. Strength Saving Throw: DC 23, each creature in a 30-foot Cone. Failure: The target is pushed up to 60 feet straight away from the dragon and has the Prone condition. Spellcasting. The dragon casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 22, +14 to hit with spell attacks): At Will: Detect Magic, Guiding Bolt (level 2 version), Shapechange (Beast or Humanoid form only, no Temporary Hit Points gained from the spell, and no Concentration or Temporary Hit Points required to maintain the spell), Speak with Animals, Thaumaturgy. 1/Day Each: Detect Thoughts, Control Water, Scrying, Water Breathing.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the dragon can expend a use to take one of the following actions. The dragon regains all expended uses at the start of each of its turns. Guiding Light. The dragon uses Spellcasting to cast Guiding Bolt (level 2 version). Pounce. The dragon moves up to half its Speed, and it makes one Rend attack. Thunderclap. Constitution Saving Throw: DC 22, each creature in a 20-foot-radius Sphere centered on a point the dragon can see within 120 feet. Failure: 13 (3d8) Thunder damage, and the target has the Deafened condition until the end of its next turn.
			"""
		),
		monster(
			name = "Bugbear Stalker",
			subtitle = "Medium Fey (Goblinoid), Chaotic Evil",
			group = "Bugbears",
			ac = "15",
			initiative = "+2 (12)",
			hp = "65 (10d8 + 20)",
			speed = "30 ft.",
			cr = "3 (XP 700; PB +2)",
			body = """
				STR 17 (+3 save +3), DEX 14 (+2 save +2), CON 14 (+2 save +4), INT 11 (+0 save +0), WIS 12 (+1 save +3), CHA 11 (+0 save +0)
				Skills: Stealth +6, Survival +3
				Gear: Chain Shirt, Javelins (6), Morningstar
				Senses: Darkvision 60 ft.; Passive Perception 11
				Languages: Common, Goblin
				Trait: Abduct. The bugbear needn't spend extra movement to move a creature it is grappling.
				Actions: Multiattack. The bugbear makes two Javelin or Morningstar attacks. Javelin. Melee or Ranged Attack Roll: +5, reach 10 ft. or range 30/120 ft. Hit: 13 (3d6 + 3) Piercing damage. Morningstar. Melee Attack Roll: +5 (with Advantage if the target is Grappled by the bugbear), reach 10 ft. Hit: 12 (2d8 + 3) Piercing damage.
				Bonus Actions: Quick Grapple. Dexterity Saving Throw: DC 13, one Medium or smaller creature the bugbear can see within 10 feet. Failure: The target has the Grappled condition (escape DC 13).
			"""
		),
		monster(
			name = "Bugbear Warrior",
			subtitle = "Medium Fey (Goblinoid), Chaotic Evil",
			group = "Bugbears",
			ac = "14",
			initiative = "+2 (12)",
			hp = "33 (6d8 + 6)",
			speed = "30 ft.",
			cr = "1 (XP 200; PB +2)",
			body = """
				STR 15 (+2 save +2), DEX 14 (+2 save +2), CON 13 (+1 save +1), INT 8 (-1 save -1), WIS 11 (+0 save +0), CHA 9 (-1 save -1)
				Skills: Stealth +6, Survival +2
				Gear: Hide Armor, Light Hammers (3)
				Senses: Darkvision 60 ft.; Passive Perception 10
				Languages: Common, Goblin
				Trait: Abduct. The bugbear needn't spend extra movement to move a creature it is grappling.
				Actions: Grab. Melee Attack Roll: +4, reach 10 ft. Hit: 9 (2d6 + 2) Bludgeoning damage. If the target is a Medium or smaller creature, it has the Grappled condition (escape DC 12). Light Hammer. Melee or Ranged Attack Roll: +4 (with Advantage if the target is Grappled by the bugbear), reach 10 ft. or range 20/60 ft. Hit: 9 (3d4 + 2) Bludgeoning damage.
			"""
		),
		monster(
			name = "Bulette",
			subtitle = "Large Monstrosity, Unaligned",
			ac = "17",
			initiative = "+0 (10)",
			hp = "94 (9d10 + 45)",
			speed = "40 ft., Burrow 40 ft.",
			cr = "5 (XP 1,800; PB +3)",
			body = """
				STR 19 (+4 save +4), DEX 11 (+0 save +0), CON 21 (+5 save +5), INT 2 (-4 save -4), WIS 10 (+0 save +0), CHA 5 (-3 save -3)
				Skills: Perception +6
				Senses: Darkvision 60 ft., Tremorsense 120 ft.; Passive Perception 16
				Languages: None
				Actions: Multiattack. The bulette makes two Bite attacks. Bite. Melee Attack Roll: +7, reach 5 ft. Hit: 17 (2d12 + 4) Piercing damage. Deadly Leap. The bulette spends 5 feet of movement to jump to a space within 15 feet that contains one or more Large or smaller creatures. Dexterity Saving Throw: DC 15, each creature in the bulette's destination space. Failure: 19 (3d12) Bludgeoning damage, and the target has the Prone condition. Success: Half damage, and the target is pushed 5 feet straight away from the bulette.
				Bonus Actions: Leap. The bulette jumps up to 30 feet by spending 10 feet of movement.
			"""
		),
		monster(
			name = "Centaur Trooper",
			subtitle = "Large Fey, Neutral Good",
			ac = "16",
			initiative = "+2 (12)",
			hp = "45 (6d10 + 12)",
			speed = "50 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 18 (+4 save +4), DEX 14 (+2 save +2), CON 14 (+2 save +2), INT 9 (-1 save -1), WIS 13 (+1 save +1), CHA 11 (+0 save +0)
				Skills: Athletics +6, Perception +3
				Gear: Breastplate, Longbow, Pike
				Senses: Passive Perception 13
				Languages: Elvish, Sylvan
				Actions: Multiattack. The centaur makes two attacks, using Pike or Longbow in any combination. Pike. Melee Attack Roll: +6, reach 10 ft. Hit: 9 (1d10 + 4) Piercing damage. Longbow. Ranged Attack Roll: +4, range 150/600 ft. Hit: 6 (1d8 + 2) Piercing damage.
				Bonus Actions: Trampling Charge (Recharge 5-6). The centaur moves up to its Speed without provoking Opportunity Attacks and can move through the spaces of Medium or smaller creatures. Each creature whose space the centaur enters is targeted once by the following effect. Strength Saving Throw: DC 14. Failure: 7 (1d6 + 4) Bludgeoning damage, and the target has the Prone condition.
			"""
		),
		monster(
			name = "Chain Devil",
			subtitle = "Medium Fiend (Devil), Lawful Evil",
			ac = "15",
			initiative = "+5 (15)",
			hp = "85 (10d8 + 40)",
			speed = "30 ft.",
			cr = "8 (XP 3,900; PB +3)",
			body = """
				STR 18 (+4 save +4), DEX 15 (+2 save +2), CON 18 (+4 save +7), INT 11 (+0 save +0), WIS 12 (+1 save +4), CHA 14 (+2 save +2)
				Resistances: Bludgeoning, Cold, Piercing, Slashing
				Immunities: Fire, Poison; Poisoned
				Senses: Darkvision 120 ft. (unimpeded by magical Darkness); Passive Perception 11
				Languages: Infernal; telepathy 120 ft.
				Traits: Diabolical Restoration. If the devil dies outside the Nine Hells, its body disappears in sulfurous smoke, and it gains a new body instantly, reviving with all its Hit Points somewhere in the Nine Hells. Magic Resistance. The devil has Advantage on saving throws against spells and other magical effects.
				Actions: Multiattack. The devil makes two Chain attacks and uses Conjure Infernal Chain. Chain. Melee Attack Roll: +7, reach 10 ft. Hit: 11 (2d6 + 4) Slashing damage. If the target is a Large or smaller creature, it has the Grappled condition (escape DC 14) from one of two chains, and it has the Restrained condition until the grapple ends. Conjure Infernal Chain. The devil conjures a fiery chain to bind a creature. Dexterity Saving Throw: DC 15, one creature the devil can see within 60 feet. Failure: 9 (2d4 + 4) Fire damage, and the target has the Restrained condition until the end of the devil's next turn, at which point the chain disappears. If the target is Large or smaller, the devil moves the target up to 30 feet straight toward itself. Success: The chain disappears.
				Reactions: Unnerving Gaze. Trigger: A creature the devil can see starts its turn within 30 feet of the devil and can see the devil. Response—Wisdom Saving Throw: DC 15, the triggering creature. Failure: The target has the Frightened condition until the end of its turn. Success: The target is immune to this devil's Unnerving Gaze for 24 hours.
			"""
		),
		monster(
			name = "Chimera",
			subtitle = "Large Monstrosity, Chaotic Evil",
			ac = "14",
			initiative = "+0 (10)",
			hp = "114 (12d10 + 48)",
			speed = "30 ft., Fly 60 ft.",
			cr = "6 (XP 2,300; PB +3)",
			body = """
				STR 19 (+4 save +4), DEX 11 (+0 save +0), CON 19 (+4 save +4), INT 3 (-4 save -4), WIS 14 (+2 save +2), CHA 10 (+0 save +0)
				Skills: Perception +8
				Senses: Darkvision 60 ft.; Passive Perception 18
				Languages: Understands Draconic but can't speak
				Actions: Multiattack. The chimera makes one Ram attack, one Bite attack, and one Claw attack. It can replace the Claw attack with a use of Fire Breath if available. Bite. Melee Attack Roll: +7, reach 5 ft. Hit: 11 (2d6 + 4) Piercing damage, or 18 (4d6 + 4) Piercing damage if the chimera had Advantage on the attack roll. Claw. Melee Attack Roll: +7, reach 5 ft. Hit: 7 (1d6 + 4) Slashing damage. Ram. Melee Attack Roll: +7, reach 5 ft. Hit: 10 (1d12 + 4) Bludgeoning damage. If the target is a Medium or smaller creature, it has the Prone condition. Fire Breath (Recharge 5-6). Dexterity Saving Throw: DC 15, each creature in a 15-foot Cone. Failure: 31 (7d8) Fire damage. Success: Half damage.
			"""
		),
		monster(
			name = "Chuul",
			subtitle = "Large Aberration, Chaotic Evil",
			ac = "16",
			initiative = "+0 (10)",
			hp = "76 (9d10 + 27)",
			speed = "30 ft., Swim 30 ft.",
			cr = "4 (XP 1,100; PB +2)",
			body = """
				STR 19 (+4 save +4), DEX 10 (+0 save +0), CON 16 (+3 save +3), INT 5 (-3 save -3), WIS 11 (+0 save +0), CHA 5 (-3 save -3)
				Skills: Perception +4
				Immunities: Poison; Poisoned
				Senses: Darkvision 60 ft.; Passive Perception 14
				Languages: Understands Deep Speech but can't speak
				Traits: Amphibious. The chuul can breathe air and water. Sense Magic. The chuul senses magic within 120 feet of itself. This trait otherwise works like the Detect Magic spell but isn't itself magical.
				Actions: Multiattack. The chuul makes two Pincer attacks and uses Paralyzing Tentacles. Pincer. Melee Attack Roll: +6, reach 10 ft. Hit: 9 (1d10 + 4) Bludgeoning damage. If the target is a Large or smaller creature, it has the Grappled condition (escape DC 14) from one of two pincers. Paralyzing Tentacles. Constitution Saving Throw: DC 13, one creature Grappled by the chuul. Failure: The target has the Poisoned condition and repeats the save at the end of each of its turns, ending the effect on itself on a success. After 1 minute, it succeeds automatically. While Poisoned, the target has the Paralyzed condition.
			"""
		),
		monster(
			name = "Clay Golem",
			subtitle = "Large Construct, Unaligned",
			ac = "14",
			initiative = "+3 (13)",
			hp = "123 (13d10 + 52)",
			speed = "30 ft.",
			cr = "9 (XP 5,000; PB +4)",
			body = """
				STR 20 (+5 save +5), DEX 9 (-1 save -1), CON 18 (+4 save +4), INT 3 (-4 save -4), WIS 8 (-1 save -1), CHA 1 (-5 save -5)
				Resistances: Bludgeoning, Piercing, Slashing
				Immunities: Acid, Poison, Psychic; Charmed, Exhaustion, Frightened, Paralyzed, Petrified, Poisoned
				Senses: Darkvision 60 ft.; Passive Perception 9
				Languages: Common plus one other language
				Traits: Acid Absorption. Whenever the golem is subjected to Acid damage, it takes no damage and instead regains a number of Hit Points equal to the Acid damage dealt. Berserk. Whenever the golem starts its turn Bloodied, roll 1d6. On a 6, the golem goes berserk. On each of its turns while berserk, the golem attacks the nearest creature it can see. If no creature is near enough to move to and attack, the golem attacks an object. Once the golem goes berserk, it continues to be berserk until it is destroyed or it is no longer Bloodied. Immutable Form. The golem can't shape-shift. Magic Resistance. The golem has Advantage on saving throws against spells and other magical effects.
				Actions: Multiattack. The golem makes two Slam attacks, or it makes three Slam attacks if it used Hasten this turn. Slam. Melee Attack Roll: +9, reach 5 ft. Hit: 10 (1d10 + 5) Bludgeoning damage plus 6 (1d12) Acid damage, and the target's Hit Point maximum decreases by an amount equal to the Acid damage taken.
				Bonus Actions: Hasten (Recharge 5-6). The golem takes the Dash and Disengage actions.
			"""
		),
		monster(
			name = "Cloaker",
			subtitle = "Large Aberration, Chaotic Neutral",
			ac = "14",
			initiative = "+5 (15)",
			hp = "91 (14d10 + 14)",
			speed = "10 ft., Fly 40 ft.",
			cr = "8 (XP 3,900; PB +3)",
			body = """
				STR 17 (+3 save +3), DEX 15 (+2 save +2), CON 12 (+1 save +1), INT 13 (+1 save +1), WIS 14 (+2 save +2), CHA 7 (-2 save -2)
				Skills: Stealth +5
				Immunities: Frightened
				Senses: Darkvision 120 ft.; Passive Perception 12
				Languages: Deep Speech, Undercommon
				Trait: Light Sensitivity. While in Bright Light, the cloaker has Disadvantage on attack rolls.
				Actions: Multiattack. The cloaker makes one Attach attack and two Tail attacks. Attach. Melee Attack Roll: +6, reach 5 ft. Hit: 13 (3d6 + 3) Piercing damage. If the target is a Large or smaller creature, the cloaker attaches to it. While the cloaker is attached, the target has the Blinded condition, and the cloaker can't make Attach attacks against other targets. In addition, the cloaker halves the damage it takes (round down), and the target takes the same amount of damage. The cloaker can detach itself by spending 5 feet of movement. The target or a creature within 5 feet of it can take an action to try to detach the cloaker, doing so by succeeding on a DC 14 Strength (Athletics) check. Tail. Melee Attack Roll: +6, reach 10 ft. Hit: 8 (1d10 + 3) Slashing damage.
				Bonus Actions: Moan. Wisdom Saving Throw: DC 13, each creature in a 60-foot Emanation originating from the cloaker. Failure: The target has the Frightened condition until the end of the cloaker's next turn. Success: The target is immune to this cloaker's Moan for the next 24 hours. Phantasms (Recharge after a Short or Long Rest). The cloaker casts the Mirror Image spell, requiring no spell components and using Wisdom as the spellcasting ability. The spell ends early if the cloaker starts or ends its turn in Bright Light.
			"""
		),
		monster(
			name = "Cloud Giant",
			subtitle = "Huge Giant, Neutral",
			ac = "14",
			initiative = "+4 (14)",
			hp = "200 (16d12 + 96)",
			speed = "40 ft., Fly 20 ft. (hover)",
			cr = "9 (XP 5,000; PB +4)",
			body = """
				STR 27 (+8 save +8), DEX 10 (+0 save +0), CON 22 (+6 save +10), INT 12 (+1 save +1), WIS 16 (+3 save +7), CHA 16 (+3 save +3)
				Skills: Insight +7, Perception +11
				Senses: Passive Perception 21
				Languages: Common, Giant
				Actions: Multiattack. The giant makes two attacks, using Thunderous Mace or Thundercloud in any combination. It can replace one attack with a use of Spellcasting to cast Fog Cloud. Thunderous Mace. Melee Attack Roll: +12, reach 10 ft. Hit: 21 (3d8 + 8) Bludgeoning damage plus 7 (2d6) Thunder damage. Thundercloud. Ranged Attack Roll: +12, range 240 ft. Hit: 18 (3d6 + 8) Thunder damage, and the target has the Incapacitated condition until the end of its next turn. Spellcasting. The giant casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 15): At Will: Detect Magic, Fog Cloud, Light. 1/Day Each: Control Weather, Gaseous Form, Telekinesis.
				Bonus Actions: Misty Step. The giant casts the Misty Step spell, using the same spellcasting ability as Spellcasting.
			"""
		),
		monster(
			name = "Cockatrice",
			subtitle = "Small Monstrosity, Unaligned",
			ac = "11",
			initiative = "+1 (11)",
			hp = "22 (5d6 + 5)",
			speed = "20 ft., Fly 40 ft.",
			cr = "1/2 (XP 100; PB +2)",
			body = """
				STR 6 (-2 save -2), DEX 12 (+1 save +1), CON 12 (+1 save +1), INT 2 (-4 save -4), WIS 13 (+1 save +1), CHA 5 (-3 save -3)
				Immunities: Petrified
				Senses: Darkvision 60 ft.; Passive Perception 11
				Languages: None
				Actions: Petrifying Bite. Melee Attack Roll: +3, reach 5 ft. Hit: 3 (1d4 + 1) Piercing damage. If the target is a creature, it is subjected to the following effect. Constitution Saving Throw: DC 11. First Failure: The target has the Restrained condition. The target repeats the save at the end of its next turn if it is still Restrained, ending the effect on itself on a success. Second Failure: The target has the Petrified condition, instead of the Restrained condition, for 24 hours.
			"""
		),
		monster(
			name = "Commoner",
			subtitle = "Medium or Small Humanoid, Neutral",
			ac = "10",
			initiative = "+0 (10)",
			hp = "4 (1d8)",
			speed = "30 ft.",
			cr = "0 (XP 10; PB +2)",
			body = """
				STR 10 (+0 save +0), DEX 10 (+0 save +0), CON 10 (+0 save +0), INT 10 (+0 save +0), WIS 10 (+0 save +0), CHA 10 (+0 save +0)
				Gear: Club
				Senses: Passive Perception 10
				Languages: Common
				Trait: Training. The commoner has proficiency in one skill of the GM's choice and has Advantage whenever it makes an ability check using that skill.
				Actions: Club. Melee Attack Roll: +2, reach 5 ft. Hit: 2 (1d4) Bludgeoning damage.
			"""
		),
		monster(
			name = "Copper Dragon Wyrmling",
			subtitle = "Medium Dragon (Metallic), Chaotic Good",
			group = "Copper Dragons",
			ac = "16",
			initiative = "+3 (13)",
			hp = "22 (4d8 + 4)",
			speed = "30 ft., Climb 30 ft., Fly 60 ft.",
			cr = "1 (XP 200; PB +2)",
			body = """
				STR 15 (+2 save +2), DEX 12 (+1 save +3), CON 13 (+1 save +1), INT 14 (+2 save +2), WIS 11 (+0 save +2), CHA 13 (+1 save +1)
				Skills: Perception +4, Stealth +3
				Immunities: Acid
				Senses: Blindsight 10 ft., Darkvision 60 ft.; Passive Perception 14
				Languages: Draconic
				Actions: Rend. Melee Attack Roll: +4, reach 5 ft. Hit: 7 (1d10 + 2) Slashing damage. Acid Breath (Recharge 5-6). Dexterity Saving Throw: DC 11, each creature in a 20-foot-long, 5-foot-wide Line. Failure: 18 (4d8) Acid damage. Success: Half damage. Slowing Breath. Constitution Saving Throw: DC 11, each creature in a 15-foot Cone. Failure: The target can't take Reactions; its Speed is halved; and it can take either an action or a Bonus Action on its turn, not both. This effect lasts until the end of its next turn.
			"""
		),
		monster(
			name = "Young Copper Dragon",
			subtitle = "Large Dragon (Metallic), Chaotic Good",
			group = "Copper Dragons",
			ac = "17",
			initiative = "+4 (14)",
			hp = "119 (14d10 + 42)",
			speed = "40 ft., Climb 40 ft., Fly 80 ft.",
			cr = "7 (XP 2,900; PB +3)",
			body = """
				STR 19 (+4 save +4), DEX 12 (+1 save +4), CON 17 (+3 save +3), INT 16 (+3 save +3), WIS 13 (+1 save +4), CHA 15 (+2 save +2)
				Skills: Deception +5, Perception +7, Stealth +4
				Immunities: Acid
				Senses: Blindsight 30 ft., Darkvision 120 ft.; Passive Perception 17
				Languages: Common, Draconic
				Actions: Multiattack. The dragon makes three Rend attacks. It can replace one attack with a use of Slowing Breath. Rend. Melee Attack Roll: +7, reach 10 ft. Hit: 15 (2d10 + 4) Slashing damage. Acid Breath (Recharge 5-6). Dexterity Saving Throw: DC 14, each creature in a 40-foot-long, 5-foot-wide Line. Failure: 40 (9d8) Acid damage. Success: Half damage. Slowing Breath. Constitution Saving Throw: DC 14, each creature in a 30-foot Cone. Failure: The target can't take Reactions; its Speed is halved; and it can take either an action or a Bonus Action on its turn, not both. This effect lasts until the end of its next turn.
			"""
		),
		monster(
			name = "Adult Copper Dragon",
			subtitle = "Huge Dragon (Metallic), Chaotic Good",
			group = "Copper Dragons",
			ac = "18",
			initiative = "+11 (21)",
			hp = "184 (16d12 + 80)",
			speed = "40 ft., Climb 40 ft., Fly 80 ft.",
			cr = "14 (XP 11,500, or 13,000 in lair; PB +5)",
			body = """
				STR 23 (+6 save +6), DEX 12 (+1 save +6), CON 21 (+5 save +5), INT 18 (+4 save +4), WIS 15 (+2 save +7), CHA 18 (+4 save +4)
				Skills: Deception +9, Perception +12, Stealth +6
				Immunities: Acid
				Senses: Blindsight 60 ft., Darkvision 120 ft.; Passive Perception 22
				Languages: Common, Draconic
				Trait: Legendary Resistance (3/Day, or 4/Day in Lair). If the dragon fails a saving throw, it can choose to succeed instead.
				Actions: Multiattack. The dragon makes three Rend attacks. It can replace one attack with a use of (A) Slowing Breath or (B) Spellcasting to cast Mind Spike (level 4 version). Rend. Melee Attack Roll: +11, reach 10 ft. Hit: 17 (2d10 + 6) Slashing damage plus 4 (1d8) Acid damage. Acid Breath (Recharge 5-6). Dexterity Saving Throw: DC 18, each creature in an 60-foot-long, 5-foot-wide Line. Failure: 54 (12d8) Acid damage. Success: Half damage. Slowing Breath. Constitution Saving Throw: DC 18, each creature in a 60-foot Cone. Failure: The target can't take Reactions; its Speed is halved; and it can take either an action or a Bonus Action on its turn, not both. This effect lasts until the end of its next turn. Spellcasting. The dragon casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 17): At Will: Detect Magic, Mind Spike (level 4 version), Minor Illusion, Shapechange (Beast or Humanoid form only, no Temporary Hit Points gained from the spell, and no Concentration or Temporary Hit Points required to maintain the spell). 1/Day Each: Greater Restoration, Major Image.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the dragon can expend a use to take one of the following actions. The dragon regains all expended uses at the start of each of its turns. Giggling Magic. Charisma Saving Throw: DC 17, one creature the dragon can see within 90 feet. Failure: 24 (7d6) Psychic damage. Until the end of its next turn, the target rolls 1d6 whenever it makes an ability check or attack roll and subtracts the number rolled from the d20 Test. Failure or Success: The dragon can't take this action again until the start of its next turn. Mind Jolt. The dragon uses Spellcasting to cast Mind Spike (level 4 version). The dragon can't take this action again until the start of its next turn. Pounce. The dragon moves up to half its Speed, and it makes one Rend attack.
			"""
		),
		monster(
			name = "Ancient Copper Dragon",
			subtitle = "Gargantuan Dragon (Metallic), Chaotic Good",
			group = "Copper Dragons",
			ac = "21",
			initiative = "+15 (25)",
			hp = "367 (21d20 + 147)",
			speed = "40 ft., Climb 40 ft., Fly 80 ft.",
			cr = "21 (XP 33,000, or 41,000 in lair; PB +7)",
			body = """
				STR 27 (+8 save +8), DEX 12 (+1 save +8), CON 25 (+7 save +7), INT 20 (+5 save +5), WIS 17 (+3 save +10), CHA 22 (+6 save +6)
				Skills: Deception +13, Perception +17, Stealth +8
				Immunities: Acid
				Senses: Blindsight 60 ft., Darkvision 120 ft.; Passive Perception 27
				Languages: Common, Draconic
				Trait: Legendary Resistance (4/Day, or 5/Day in Lair). If the dragon fails a saving throw, it can choose to succeed instead.
				Actions: Multiattack. The dragon makes three Rend attacks. It can replace one attack with a use of (A) Slowing Breath or (B) Spellcasting to cast Mind Spike (level 5 version). Rend. Melee Attack Roll: +15, reach 15 ft. Hit: 19 (2d10 + 8) Slashing damage plus 9 (2d8) Acid damage. Acid Breath (Recharge 5-6). Dexterity Saving Throw: DC 22, each creature in an 90-foot-long, 10-foot-wide Line. Failure: 63 (14d8) Acid damage. Success: Half damage. Slowing Breath. Constitution Saving Throw: DC 22, each creature in a 90-foot Cone. Failure: The target can't take Reactions; its Speed is halved; and it can take either an action or a Bonus Action on its turn, not both. This effect lasts until the end of its next turn. Spellcasting. The dragon casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 21): At Will: Detect Magic, Mind Spike (level 5 version), Minor Illusion, Shapechange (Beast or Humanoid form only, no Temporary Hit Points gained from the spell, and no Concentration or Temporary Hit Points required to maintain the spell). 1/Day Each: Greater Restoration, Major Image, Project Image.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the dragon can expend a use to take one of the following actions. The dragon regains all expended uses at the start of each of its turns. Giggling Magic. Charisma Saving Throw: DC 21, one creature the dragon can see within 120 feet. Failure: 31 (9d6) Psychic damage. Until the end of its next turn, the target rolls 1d8 whenever it makes an ability check or attack roll and subtracts the number rolled from the d20 Test. Failure or Success: The dragon can't take this action again until the start of its next turn. Mind Jolt. The dragon uses Spellcasting to cast Mind Spike (level 5 version). The dragon can't take this action again until the start of its next turn. Pounce. The dragon moves up to half its Speed, and it makes one Rend attack.
			"""
		),
		monster(
			name = "Couatl",
			subtitle = "Medium Celestial, Lawful Good",
			ac = "19",
			initiative = "+5 (15)",
			hp = "60 (8d8 + 24)",
			speed = "30 ft., Fly 90 ft.",
			cr = "4 (XP 1,100; PB +2)",
			body = """
				STR 16 (+3 save +3), DEX 20 (+5 save +5), CON 17 (+3 save +5), INT 18 (+4 save +4), WIS 20 (+5 save +7), CHA 18 (+4 save +4)
				Resistances: Bludgeoning, Piercing, Slashing
				Immunities: Psychic, Radiant
				Senses: Truesight 120 ft.; Passive Perception 15
				Languages: All; telepathy 120 ft.
				Trait: Shielded Mind. The couatl's thoughts can't be read by any means, and other creatures can communicate with it telepathically only if it allows them.
				Actions: Bite. Melee Attack Roll: +7, reach 5 ft. Hit: 11 (1d12 + 5) Piercing damage, and the target has the Poisoned condition until the end of the couatl's next turn. Constrict. Strength Saving Throw: DC 15, one Medium or smaller creature the couatl can see within 5 feet. Failure: 8 (1d6 + 5) Bludgeoning damage. The target has the Grappled condition (escape DC 13), and it has the Restrained condition until the grapple ends. Spellcasting. The couatl casts one of the following spells, requiring no spell components and using Wisdom as the spellcasting ability (spell save DC 15): At Will: Detect Evil and Good, Detect Magic, Detect Thoughts, Shapechange (Beast or Humanoid form only, no Temporary Hit Points gained from the spell, and no Concentration or Temporary Hit Points required to maintain the spell). 1/Day Each: Create Food and Water, Dream, Greater Restoration, Scrying, Sleep.
				Bonus Actions: Divine Aid (2/Day). The couatl casts Bless, Lesser Restoration, or Sanctuary, requiring no spell components and using the same spellcasting ability as Spellcasting.
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

