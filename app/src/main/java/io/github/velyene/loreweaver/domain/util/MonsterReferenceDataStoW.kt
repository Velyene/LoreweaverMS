/*
 * FILE: MonsterReferenceDataStoW.kt
 *
 * TABLE OF CONTENTS:
 * 1. Public monster dataset object and entry list
 * 2. Monster entries S-W
 */

package io.github.velyene.loreweaver.domain.util

object MonsterReferenceDataStoW {
	val ENTRIES: List<MonsterReferenceEntry> = listOf(
		monster(
			name = "Sahuagin Warrior",
			subtitle = "Medium Fiend, Lawful Evil",
			group = "Sahuagin",
			ac = "12",
			initiative = "+0 (10)",
			hp = "22 (4d8 + 4)",
			speed = "30 ft., Swim 40 ft.",
			cr = "1/2 (XP 100; PB +2)",
			body = """
				STR 13 (+1 save +1), DEX 11 (+0 save +0), CON 12 (+1 save +1), INT 12 (+1 save +1), WIS 13 (+1 save +1), CHA 9 (-1 save -1)
				Skills: Perception +5
				Resistances: Acid, Cold
				Senses: Darkvision 120 ft.; Passive Perception 15
				Languages: Sahuagin
				Traits: Blood Frenzy. The sahuagin has Advantage on attack rolls against any creature that doesn't have all its Hit Points. Limited Amphibiousness. The sahuagin can breathe air and water, but it must be submerged at least once every 4 hours to avoid suffocating outside water. Shark Telepathy. The sahuagin can magically control sharks within 120 feet of itself, using a special telepathy.
				Actions: Multiattack. The sahuagin makes two Claw attacks. Claw. Melee Attack Roll: +3, reach 5 ft. Hit: 4 (1d6 + 1) Slashing damage.
				Bonus Actions: Aquatic Charge. The sahuagin swims up to its Swim Speed straight toward an enemy it can see.
			"""
		),
		monster(
			name = "Salamander",
			subtitle = "Large Elemental, Neutral Evil",
			ac = "15",
			initiative = "+2 (12)",
			hp = "90 (12d10 + 24)",
			speed = "30 ft., Climb 30 ft.",
			cr = "5 (XP 1,800; PB +3)",
			body = """
				STR 18 (+4 save +4), DEX 14 (+2 save +2), CON 15 (+2 save +2), INT 11 (+0 save +0), WIS 10 (+0 save +0), CHA 12 (+1 save +1)
				Vulnerabilities: Cold
				Immunities: Fire
				Senses: Darkvision 60 ft.; Passive Perception 10
				Languages: Primordial (Ignan)
				Trait: Fire Aura. At the end of each of the salamander's turns, each creature of the salamander's choice in a 5-foot Emanation originating from the salamander takes 7 (2d6) Fire damage.
				Actions: Multiattack. The salamander makes two Flame Spear attacks. It can replace one attack with a use of Constrict. Flame Spear. Melee or Ranged Attack Roll: +7, reach 5 ft. or range 20/60 ft. Hit: 13 (2d8 + 4) Piercing damage plus 7 (2d6) Fire damage. Hit or Miss: The spear magically returns to the salamander's hand immediately after a ranged attack. Constrict. Strength Saving Throw: DC 15, one Large or smaller creature the salamander can see within 10 feet. Failure: 11 (2d6 + 4) Bludgeoning damage plus 7 (2d6) Fire damage. The target has the Grappled condition (escape DC 14), and it has the Restrained condition until the grapple ends.
			"""
		),
		monster(
			name = "Satyr",
			subtitle = "Medium Fey, Chaotic Neutral",
			ac = "13",
			initiative = "+3 (13)",
			hp = "31 (7d8)",
			speed = "40 ft.",
			cr = "1/2 (XP 100; PB +2)",
			body = """
				STR 12 (+1 save +1), DEX 16 (+3 save +3), CON 11 (+0 save +0), INT 12 (+1 save +1), WIS 10 (+0 save +0), CHA 14 (+2 save +2)
				Skills: Perception +2, Performance +6, Stealth +5
				Senses: Passive Perception 12
				Languages: Common, Elvish, Sylvan
				Trait: Magic Resistance. The satyr has Advantage on saving throws against spells and other magical effects.
				Actions: Hooves. Melee Attack Roll: +5, reach 5 ft. Hit: 5 (1d4 + 3) Bludgeoning damage. If the target is a Medium or smaller creature, the satyr pushes the target up to 10 feet straight away from itself. Mockery. Wisdom Saving Throw: DC 12, one creature the satyr can see within 90 feet. Failure: 5 (1d6 + 2) Psychic damage.
			"""
		),
		monster(
			name = "Scout",
			subtitle = "Medium or Small Humanoid, Neutral",
			ac = "13",
			initiative = "+2 (12)",
			hp = "16 (3d8 + 3)",
			speed = "30 ft.",
			cr = "1/2 (XP 100; PB +2)",
			body = """
				STR 11 (+0 save +0), DEX 14 (+2 save +2), CON 12 (+1 save +1), INT 11 (+0 save +0), WIS 13 (+1 save +1), CHA 11 (+0 save +0)
				Skills: Nature +4, Perception +5, Stealth +6, Survival +5
				Gear: Leather Armor, Longbow, Shortsword
				Senses: Passive Perception 15
				Languages: Common plus one other language
				Actions: Multiattack. The scout makes two attacks, using Shortsword and Longbow in any combination. Shortsword. Melee Attack Roll: +4, reach 5 ft. Hit: 5 (1d6 + 2) Piercing damage. Longbow. Ranged Attack Roll: +4, range 150/600 ft. Hit: 6 (1d8 + 2) Piercing damage.
			"""
		),
		monster(
			name = "Sea Hag",
			subtitle = "Medium Fey, Chaotic Evil",
			ac = "14",
			initiative = "+1 (11)",
			hp = "52 (7d8 + 21)",
			speed = "30 ft., Swim 40 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 16 (+3 save +3), DEX 13 (+1 save +1), CON 16 (+3 save +3), INT 12 (+1 save +1), WIS 12 (+1 save +1), CHA 13 (+1 save +1)
				Senses: Darkvision 60 ft.; Passive Perception 11
				Languages: Common, Giant, Primordial (Aquan)
				Traits: Amphibious. The hag can breathe air and water. Coven Magic. While within 30 feet of at least two hag allies, the hag can cast one of the following spells, requiring no Material components, using the spell's normal casting time, and using Intelligence as the spellcasting ability (spell save DC 11): Augury, Find Familiar, Identify, Locate Object, Scrying, or Unseen Servant. The hag must finish a Long Rest before using this trait to cast that spell again. Vile Appearance. Wisdom Saving Throw: DC 11, any Beast or Humanoid that starts its turn within 30 feet of the hag and can see the hag's true form. Failure: The target has the Frightened condition until the start of its next turn. Success: The target is immune to this hag's Vile Appearance for 24 hours.
				Actions: Claw. Melee Attack Roll: +5, reach 5 ft. Hit: 10 (2d6 + 3) Slashing damage. Death Glare (Recharge 5-6). Wisdom Saving Throw: DC 11, one Frightened creature the hag can see within 30 feet. Failure: If the target has 20 Hit Points or fewer, it drops to 0 Hit Points. Otherwise, the target takes 13 (3d8) Psychic damage. Illusory Appearance. The hag casts Disguise Self, using Constitution as the spellcasting ability (spell save DC 13). The spell's duration is 24 hours.
			"""
		),
		monster(
			name = "Shadow",
			subtitle = "Medium Undead, Chaotic Evil",
			ac = "12",
			initiative = "+2 (12)",
			hp = "27 (5d8 + 5)",
			speed = "40 ft.",
			cr = "1/2 (XP 100; PB +2)",
			body = """
				STR 6 (-2 save -2), DEX 14 (+2 save +2), CON 13 (+1 save +1), INT 6 (-2 save -2), WIS 10 (+0 save +0), CHA 8 (-1 save -1)
				Skills: Stealth +6
				Vulnerabilities: Radiant
				Resistances: Acid, Cold, Fire, Lightning, Thunder
				Immunities: Necrotic, Poison; Exhaustion, Frightened, Grappled, Paralyzed, Petrified, Poisoned, Prone, Restrained, Unconscious
				Senses: Darkvision 60 ft.; Passive Perception 10
				Languages: None
				Traits: Amorphous. The shadow can move through a space as narrow as 1 inch without expending extra movement to do so. Sunlight Weakness. While in sunlight, the shadow has Disadvantage on d20 Tests.
				Actions: Draining Swipe. Melee Attack Roll: +4, reach 5 ft. Hit: 5 (1d6 + 2) Necrotic damage, and the target's Strength score decreases by 1d4. The target dies if this reduces that score to 0. If a Humanoid is slain by this attack, a Shadow rises from the corpse 1d4 hours later.
				Bonus Actions: Shadow Stealth. While in Dim Light or Darkness, the shadow takes the Hide action.
			"""
		),
		monster(
			name = "Shambling Mound",
			subtitle = "Large Plant, Unaligned",
			ac = "15",
			initiative = "-1 (9)",
			hp = "110 (13d10 + 39)",
			speed = "30 ft., Swim 20 ft.",
			cr = "5 (XP 1,800; PB +3)",
			body = """
				STR 18 (+4 save +4), DEX 8 (-1 save -1), CON 16 (+3 save +3), INT 5 (-3 save -3), WIS 10 (+0 save +0), CHA 5 (-3 save -3)
				Skills: Stealth +3
				Resistances: Cold, Fire
				Immunities: Lightning; Deafened, Exhaustion
				Senses: Blindsight 60 ft.; Passive Perception 10
				Languages: None
				Trait: Lightning Absorption. Whenever the shambling mound is subjected to Lightning damage, it regains a number of Hit Points equal to the Lightning damage dealt.
				Actions: Multiattack. The shambling mound makes three Charged Tendril attacks. It can replace one attack with a use of Engulf. Charged Tendril. Melee Attack Roll: +7, reach 10 ft. Hit: 7 (1d6 + 4) Bludgeoning damage plus 5 (2d4) Lightning damage. If the target is a Medium or smaller creature, the shambling mound pulls the target 5 feet straight toward itself. Engulf. Strength Saving Throw: DC 15, one Medium or smaller creature within 5 feet. Failure: The target is pulled into the shambling mound's space and has the Grappled condition (escape DC 14). Until the grapple ends, the target has the Blinded and Restrained conditions, and it takes 10 (3d6) Lightning damage at the start of each of its turns. When the shambling mound moves, the Grappled target moves with it, costing it no extra movement. The shambling mound can have only one creature Grappled by this action at a time.
			"""
		),
		monster(
			name = "Shield Guardian",
			subtitle = "Large Construct, Unaligned",
			ac = "17",
			initiative = "-1 (9)",
			hp = "142 (15d10 + 60)",
			speed = "30 ft.",
			cr = "7 (XP 2,900; PB +3)",
			body = """
				STR 18 (+4 save +4), DEX 8 (-1 save -1), CON 18 (+4 save +4), INT 7 (-2 save -2), WIS 10 (+0 save +0), CHA 3 (-4 save -4)
				Immunities: Poison; Charmed, Exhaustion, Frightened, Paralyzed, Petrified, Poisoned
				Senses: Blindsight 10 ft., Darkvision 60 ft.; Passive Perception 10
				Languages: Understands commands given in any language but can't speak
				Traits: Bound. The guardian is magically bound to an amulet. While the guardian and its amulet are on the same plane of existence, the amulet's wearer can telepathically call the guardian to travel to it, and the guardian knows the distance and direction to the amulet. If the guardian is within 60 feet of the amulet's wearer, half of any damage the wearer takes (round up) is transferred to the guardian. Regeneration. The guardian regains 10 Hit Points at the start of each of its turns if it has at least 1 Hit Point. Spell Storing. A spellcaster who wears the guardian's amulet can cause the guardian to store one spell of level 4 or lower. To do so, the wearer must cast the spell on the guardian while within 5 feet of it. The spell has no effect but is stored within the guardian. Any previously stored spell is lost when a new spell is stored. The guardian can cast the spell stored with any parameters set by the original caster, requiring no spell components and using the caster's spellcasting ability. The stored spell is then lost.
				Actions: Multiattack. The guardian makes two Fist attacks. Fist. Melee Attack Roll: +7, reach 10 ft. Hit: 11 (2d6 + 4) Bludgeoning damage plus 7 (2d6) Force damage.
				Reactions: Protection. Trigger: An attack roll hits the wearer of the guardian's amulet while the wearer is within 5 feet of the guardian. Response: The wearer gains a +5 bonus to AC, including against the triggering attack and possibly causing it to miss, until the start of the guardian's next turn.
			"""
		),
		monster(
			name = "Silver Dragon Wyrmling",
			subtitle = "Medium Dragon (Metallic), Lawful Good",
			group = "Silver Dragons",
			ac = "17",
			initiative = "+2 (12)",
			hp = "45 (6d8 + 18)",
			speed = "30 ft., Fly 60 ft.",
			cr = "2 (450 XP; PB +2)",
			body = """
				STR 19 (+4 save +4), DEX 10 (+0 save +2), CON 17 (+3 save +3), INT 12 (+1 save +1), WIS 11 (+0 save +2), CHA 15 (+2 save +2)
				Skills: Perception +4, Stealth +2
				Immunities: Cold
				Senses: Blindsight 10 ft., Darkvision 60 ft.; Passive Perception 14
				Languages: Draconic
				Actions: Multiattack. The dragon makes two Rend attacks. Rend. Melee Attack Roll: +6, reach 5 ft. Hit: 9 (1d10 + 4) Piercing damage. Cold Breath (Recharge 5-6). Constitution Saving Throw: DC 13, each creature in a 15-foot Cone. Failure: 18 (4d8) Cold damage. Success: Half damage. Paralyzing Breath. Constitution Saving Throw: DC 13, each creature in a 15-foot Cone. First Failure: The target has the Incapacitated condition until the end of its next turn, when it repeats the save. Second Failure: The target has the Paralyzed condition, and it repeats the save at the end of each of its turns, ending the effect on itself on a success. After 1 minute, it succeeds automatically.
			"""
		),
		monster(
			name = "Young Silver Dragon",
			subtitle = "Large Dragon (Metallic), Lawful Good",
			group = "Silver Dragons",
			ac = "18",
			initiative = "+4 (14)",
			hp = "168 (16d10 + 80)",
			speed = "40 ft., Fly 80 ft.",
			cr = "9 (XP 5,000; PB +4)",
			body = """
				STR 23 (+6 save +6), DEX 10 (+0 save +4), CON 21 (+5 save +5), INT 14 (+2 save +2), WIS 11 (+0 save +4), CHA 19 (+4 save +4)
				Skills: History +6, Perception +8, Stealth +4
				Immunities: Cold
				Senses: Blindsight 30 ft., Darkvision 120 ft.; Passive Perception 18
				Languages: Common, Draconic
				Actions: Multiattack. The dragon makes three Rend attacks. It can replace one attack with a use of Paralyzing Breath. Rend. Melee Attack Roll: +10, reach 10 ft. Hit: 15 (2d8 + 6) Slashing damage. Cold Breath (Recharge 5-6). Constitution Saving Throw: DC 17, each creature in a 30-foot Cone. Failure: 49 (11d8) Cold damage. Success: Half damage. Paralyzing Breath. Constitution Saving Throw: DC 17, each creature in a 30-foot Cone. First Failure: The target has the Incapacitated condition until the end of its next turn, when it repeats the save. Second Failure: The target has the Paralyzed condition, and it repeats the save at the end of each of its turns, ending the effect on itself on a success. After 1 minute, it succeeds automatically.
			"""
		),
		monster(
			name = "Adult Silver Dragon",
			subtitle = "Huge Dragon (Metallic), Lawful Good",
			group = "Silver Dragons",
			ac = "19",
			initiative = "+10 (20)",
			hp = "216 (16d12 + 112)",
			speed = "40 ft., Fly 80 ft.",
			cr = "16 (XP 15,000, or 18,000 in lair; PB +5)",
			body = """
				STR 27 (+8 save +8), DEX 10 (+0 save +5), CON 25 (+7 save +7), INT 16 (+3 save +3), WIS 13 (+1 save +6), CHA 22 (+6 save +6)
				Skills: History +8, Perception +11, Stealth +5
				Immunities: Cold
				Senses: Blindsight 60 ft., Darkvision 120 ft.; Passive Perception 21
				Languages: Common, Draconic
				Trait: Legendary Resistance (3/Day, or 4/Day in Lair). If the dragon fails a saving throw, it can choose to succeed instead.
				Actions: Multiattack. The dragon makes three Rend attacks. It can replace one attack with a use of (A) Paralyzing Breath or (B) Spellcasting to cast Ice Knife. Rend. Melee Attack Roll: +13, reach 10 ft. Hit: 17 (2d8 + 8) Slashing damage plus 4 (1d8) Cold damage. Cold Breath (Recharge 5-6). Constitution Saving Throw: DC 20, each creature in a 60-foot Cone. Failure: 54 (12d8) Cold damage. Success: Half damage. Paralyzing Breath. Constitution Saving Throw: DC 20, each creature in a 60-foot Cone. First Failure: The target has the Incapacitated condition until the end of its next turn, when it repeats the save. Second Failure: The target has the Paralyzed condition, and it repeats the save at the end of each of its turns, ending the effect on itself on a success. After 1 minute, it succeeds automatically. Spellcasting. The dragon casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 19, +11 to hit with spell attacks): At Will: Detect Magic, Hold Monster, Ice Knife, Shapechange (Beast or Humanoid form only, no Temporary Hit Points gained from the spell, and no Concentration or Temporary Hit Points required to maintain the spell). 1/Day Each: Ice Storm (level 5 version), Zone of Truth.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the dragon can expend a use to take one of the following actions. The dragon regains all expended uses at the start of each of its turns. Chill. The dragon uses Spellcasting to cast Hold Monster. The dragon can't take this action again until the start of its next turn. Cold Gale. Dexterity Saving Throw: DC 19, each creature in a 60-foot-long, 10-foot-wide Line. Failure: 14 (4d6) Cold damage, and the target is pushed up to 30 feet straight away from the dragon. Success: Half damage only. Failure or Success: The dragon can't take this action again until the start of its next turn. Pounce. The dragon moves up to half its Speed, and it makes one Rend attack.
			"""
		),
		monster(
			name = "Ancient Silver Dragon",
			subtitle = "Gargantuan Dragon (Metallic), Lawful Good",
			group = "Silver Dragons",
			ac = "22",
			initiative = "+14 (24)",
			hp = "468 (24d20 + 216)",
			speed = "40 ft., Fly 80 ft.",
			cr = "23 (XP 50,000, or 62,000 in lair; PB +7)",
			body = """
				STR 30 (+10 save +10), DEX 10 (+0 save +7), CON 29 (+9 save +9), INT 18 (+4 save +4), WIS 15 (+2 save +9), CHA 26 (+8 save +8)
				Skills: History +11, Perception +16, Stealth +7
				Immunities: Cold
				Senses: Blindsight 60 ft., Darkvision 120 ft.; Passive Perception 26
				Languages: Common, Draconic
				Trait: Legendary Resistance (4/Day, or 5/Day in Lair). If the dragon fails a saving throw, it can choose to succeed instead.
				Actions: Multiattack. The dragon makes three Rend attacks. It can replace one attack with a use of (A) Paralyzing Breath or (B) Spellcasting to cast Ice Knife (level 2 version). Rend. Melee Attack Roll: +17, reach 15 ft. Hit: 19 (2d8 + 10) Slashing damage plus 9 (2d8) Cold damage. Cold Breath (Recharge 5-6). Constitution Saving Throw: DC 24, each creature in a 90-foot Cone. Failure: 67 (15d8) Cold damage. Success: Half damage. Paralyzing Breath. Constitution Saving Throw: DC 24, each creature in a 90-foot Cone. First Failure: The target has the Incapacitated condition until the end of its next turn, when it repeats the save. Second Failure: The target has the Paralyzed condition, and it repeats the save at the end of each of its turns, ending the effect on itself on a success. After 1 minute, it succeeds automatically. Spellcasting. The dragon casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 23, +15 to hit with spell attacks): At Will: Detect Magic, Hold Monster, Ice Knife (level 2 version), Shapechange (Beast or Humanoid form only, no Temporary Hit Points gained from the spell, and no Concentration or Temporary Hit Points required to maintain the spell). 1/Day Each: Control Weather, Ice Storm (level 7 version), Teleport, Zone of Truth.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the dragon can expend a use to take one of the following actions. The dragon regains all expended uses at the start of each of its turns. Chill. The dragon uses Spellcasting to cast Hold Monster. The dragon can't take this action again until the start of its next turn. Cold Gale. Dexterity Saving Throw: DC 23, each creature in a 60-foot-long, 10-foot-wide Line. Failure: 14 (4d6) Cold damage, and the target is pushed up to 30 feet straight away from the dragon. Success: Half damage only. Failure or Success: The dragon can't take this action again until the start of its next turn. Pounce. The dragon moves up to half its Speed, and it makes one Rend attack.
			"""
		),
		monster(
			name = "Skeleton",
			subtitle = "Medium Undead, Lawful Evil",
			group = "Skeletons",
			ac = "14",
			initiative = "+3 (13)",
			hp = "13 (2d8 + 4)",
			speed = "30 ft.",
			cr = "1/4 (XP 50; PB +2)",
			body = """
				STR 10 (+0 save +0), DEX 16 (+3 save +3), CON 15 (+2 save +2), INT 6 (-2 save -2), WIS 8 (-1 save -1), CHA 5 (-3 save -3)
				Vulnerabilities: Bludgeoning
				Immunities: Poison; Exhaustion, Poisoned
				Gear: Shortbow, Shortsword
				Senses: Darkvision 60 ft.; Passive Perception 9
				Languages: Understands Common plus one other language but can't speak
				Actions: Shortsword. Melee Attack Roll: +5, reach 5 ft. Hit: 6 (1d6 + 3) Piercing damage. Shortbow. Ranged Attack Roll: +5, range 80/320 ft. Hit: 6 (1d6 + 3) Piercing damage.
			"""
		),
		monster(
			name = "Warhorse Skeleton",
			subtitle = "Large Undead, Lawful Evil",
			group = "Skeletons",
			ac = "13",
			initiative = "+1 (11)",
			hp = "22 (3d10 + 6)",
			speed = "60 ft.",
			cr = "1/2 (XP 100; PB +2)",
			body = """
				STR 18 (+4 save +4), DEX 12 (+1 save +1), CON 15 (+2 save +2), INT 2 (-4 save -4), WIS 8 (-1 save -1), CHA 5 (-3 save -3)
				Vulnerabilities: Bludgeoning
				Immunities: Poison; Exhaustion, Poisoned
				Senses: Darkvision 60 ft.; Passive Perception 9
				Languages: None
				Actions: Hooves. Melee Attack Roll: +6, reach 5 ft. Hit: 7 (1d6 + 4) Bludgeoning damage. If the target is a Large or smaller creature and the skeleton moved 20+ feet straight toward it immediately before the hit, the target has the Prone condition.
			"""
		),
		monster(
			name = "Minotaur Skeleton",
			subtitle = "Large Undead, Lawful Evil",
			group = "Skeletons",
			ac = "12",
			initiative = "+0 (10)",
			hp = "45 (6d10 + 12)",
			speed = "40 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 18 (+4 save +4), DEX 11 (+0 save +0), CON 15 (+2 save +2), INT 6 (-2 save -2), WIS 8 (-1 save -1), CHA 5 (-3 save -3)
				Vulnerabilities: Bludgeoning
				Immunities: Poison; Exhaustion, Poisoned
				Senses: Darkvision 60 ft.; Passive Perception 9
				Languages: Understands Abyssal but can't speak
				Actions: Gore. Melee Attack Roll: +6, reach 5 ft. Hit: 11 (2d6 + 4) Piercing damage. If the target is a Large or smaller creature and the skeleton moved 20+ feet straight toward it immediately before the hit, the target takes an extra 9 (2d8) Piercing damage and has the Prone condition. Slam. Melee Attack Roll: +6, reach 5 ft. Hit: 15 (2d10 + 4) Bludgeoning damage.
			"""
		),
		monster(
			name = "Solar",
			subtitle = "Large Celestial (Angel), Lawful Good",
			ac = "21",
			initiative = "+20 (30)",
			hp = "297 (22d10 + 176)",
			speed = "50 ft., Fly 150 ft. (hover)",
			cr = "21 (XP 33,000; PB +7)",
			body = """
				STR 26 (+8 save +8), DEX 22 (+6 save +6), CON 26 (+8 save +8), INT 25 (+7 save +7), WIS 25 (+7 save +7), CHA 30 (+10 save +10)
				Skills: Perception +14
				Immunities: Poison, Radiant; Charmed, Exhaustion, Frightened, Poisoned
				Senses: Truesight 120 ft.; Passive Perception 24
				Languages: All; telepathy 120 ft.
				Traits: Divine Awareness. The solar knows if it hears a lie. Exalted Restoration. If the solar dies outside Mount Celestia, its body disappears, and it gains a new body instantly, reviving with all its Hit Points somewhere in Mount Celestia. Legendary Resistance (4/Day). If the solar fails a saving throw, it can choose to succeed instead. Magic Resistance. The solar has Advantage on saving throws against spells and other magical effects.
				Actions: Multiattack. The solar makes two Flying Sword attacks. It can replace one attack with a use of Slaying Bow. Flying Sword. Melee or Ranged Attack Roll: +15, reach 10 ft. or range 120 ft. Hit: 22 (4d6 + 8) Slashing damage plus 36 (8d8) Radiant damage. Hit or Miss: The sword magically returns to the solar's hand or hovers within 5 feet of the solar immediately after a ranged attack. Slaying Bow. Dexterity Saving Throw: DC 21, one creature the solar can see within 600 feet. Failure: If the creature has 100 Hit Points or fewer, it dies. It otherwise takes 24 (4d8 + 6) Piercing damage plus 36 (8d8) Radiant damage. Spellcasting. The solar casts one of the following spells, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 25): At Will: Detect Evil and Good. 1/Day Each: Commune, Control Weather, Dispel Evil and Good, Resurrection.
				Bonus Actions: Divine Aid (3/Day). The solar casts Cure Wounds (level 2 version), Lesser Restoration, or Remove Curse, using the same spellcasting ability as Spellcasting.
				Legendary Actions: Legendary Action Uses: 3. Immediately after another creature's turn, the solar can expend a use to take one of the following actions. The solar regains all expended uses at the start of each of its turns. Blinding Gaze. Constitution Saving Throw: DC 25, one creature the solar can see within 120 feet. Failure: The target has the Blinded condition for 1 minute. Failure or Success: The solar can't take this action again until the start of its next turn. Radiant Teleport. The solar teleports up to 60 feet to an unoccupied space it can see. Dexterity Saving Throw: DC 25, each creature in a 10-foot Emanation originating from the solar at its destination space. Failure: 11 (2d10) Radiant damage. Success: Half damage.
			"""
		),
		monster(
			name = "Specter",
			subtitle = "Medium Undead, Chaotic Evil",
			ac = "12",
			initiative = "+2 (12)",
			hp = "22 (5d8)",
			speed = "30 ft., Fly 50 ft. (hover)",
			cr = "1 (XP 200; PB +2)",
			body = """
				STR 1 (-5 save -5), DEX 14 (+2 save +2), CON 11 (+0 save +0), INT 10 (+0 save +0), WIS 10 (+0 save +0), CHA 11 (+0 save +0)
				Resistances: Acid, Bludgeoning, Cold, Fire, Lightning, Piercing, Slashing, Thunder
				Immunities: Necrotic, Poison; Charmed, Exhaustion, Grappled, Paralyzed, Petrified, Poisoned, Prone, Restrained, Unconscious
				Senses: Darkvision 60 ft.; Passive Perception 10
				Languages: Understands Common plus one other language but can't speak
				Traits: Incorporeal Movement. The specter can move through other creatures and objects as if they were Difficult Terrain. It takes 5 (1d10) Force damage if it ends its turn inside an object. Sunlight Sensitivity. While in sunlight, the specter has Disadvantage on ability checks and attack rolls.
				Actions: Life Drain. Melee Attack Roll: +4, reach 5 ft. Hit: 7 (2d6) Necrotic damage. If the target is a creature, its Hit Point maximum decreases by an amount equal to the damage taken.
			"""
		),
		monster(
			name = "Sphinx of Wonder",
			subtitle = "Tiny Celestial, Lawful Good",
			group = "Sphinxes",
			ac = "13",
			initiative = "+3 (13)",
			hp = "24 (7d4 + 7)",
			speed = "20 ft., Fly 40 ft.",
			cr = "1 (XP 200; PB +2)",
			body = """
				STR 6 (-2 save -2), DEX 17 (+3 save +3), CON 13 (+1 save +1), INT 15 (+2 save +2), WIS 12 (+1 save +1), CHA 11 (+0 save +0)
				Skills: Arcana +4, Religion +4, Stealth +5
				Resistances: Necrotic, Psychic, Radiant
				Senses: Darkvision 60 ft.; Passive Perception 11
				Languages: Celestial, Common
				Trait: Magic Resistance. The sphinx has Advantage on saving throws against spells and other magical effects.
				Actions: Rend. Melee Attack Roll: +5, reach 5 ft. Hit: 5 (1d4 + 3) Slashing damage plus 7 (2d6) Radiant damage.
				Reactions: Burst of Ingenuity (2/Day). Trigger: The sphinx or another creature within 30 feet makes an ability check or a saving throw. Response: The sphinx adds 2 to the roll.
			"""
		),
		monster(
			name = "Sphinx of Lore",
			subtitle = "Large Celestial, Lawful Neutral",
			group = "Sphinxes",
			ac = "17",
			initiative = "+10 (20)",
			hp = "170 (20d10 + 60)",
			speed = "40 ft., Fly 60 ft.",
			cr = "11 (XP 7,200, or 8,400 in lair; PB +4)",
			body = """
				STR 18 (+4 save +4), DEX 15 (+2 save +2), CON 16 (+3 save +3), INT 18 (+4 save +4), WIS 18 (+4 save +4), CHA 18 (+4 save +4)
				Skills: Arcana +12, History +12, Perception +8, Religion +12
				Resistances: Necrotic, Radiant
				Immunities: Psychic; Charmed, Frightened
				Senses: Truesight 120 ft.; Passive Perception 18
				Languages: Celestial, Common
				Traits: Inscrutable. No magic can observe the sphinx remotely or detect its thoughts without its permission. Wisdom (Insight) checks made to ascertain its intentions or sincerity are made with Disadvantage. Legendary Resistance (3/Day, or 4/Day in Lair). If the sphinx fails a saving throw, it can choose to succeed instead.
				Actions: Multiattack. The sphinx makes three Claw attacks. Claw. Melee Attack Roll: +8, reach 5 ft. Hit: 14 (3d6 + 4) Slashing damage. Mind-Rending Roar (Recharge 5-6). Wisdom Saving Throw: DC 16, each enemy in a 300-foot Emanation originating from the sphinx. Failure: 35 (10d6) Psychic damage, and the target has the Incapacitated condition until the start of the sphinx's next turn. Spellcasting. The sphinx casts one of the following spells, requiring no Material components and using Intelligence as the spellcasting ability (spell save DC 16): At Will: Detect Magic, Identify, Mage Hand, Minor Illusion, Prestidigitation. 1/Day Each: Dispel Magic, Legend Lore, Locate Object, Plane Shift, Remove Curse, Tongues.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the sphinx can expend a use to take one of the following actions. The sphinx regains all expended uses at the start of each of its turns. Arcane Prowl. The sphinx can teleport up to 30 feet to an unoccupied space it can see, and it makes one Claw attack. Weight of Years. Constitution Saving Throw: DC 16, one creature the sphinx can see within 120 feet. Failure: The target gains 1 Exhaustion level. While the target has any Exhaustion levels, it appears 3d10 years older. Failure or Success: The sphinx can't take this action again until the start of its next turn.
			"""
		),
		monster(
			name = "Sphinx of Valor",
			subtitle = "Large Celestial, Lawful Neutral",
			group = "Sphinxes",
			ac = "17",
			initiative = "+12 (22)",
			hp = "199 (19d10 + 95)",
			speed = "40 ft., Fly 60 ft.",
			cr = "17 (XP 18,000, or 20,000 in lair; PB +6)",
			body = """
				STR 22 (+6 save +6), DEX 10 (+0 save +6), CON 20 (+5 save +11), INT 16 (+3 save +9), WIS 23 (+6 save +12), CHA 18 (+4 save +4)
				Skills: Arcana +9, Perception +12, Religion +15
				Resistances: Necrotic, Radiant
				Immunities: Psychic; Charmed, Frightened
				Senses: Truesight 120 ft.; Passive Perception 22
				Languages: Celestial, Common
				Traits: Inscrutable. No magic can observe the sphinx remotely or detect its thoughts without its permission. Wisdom (Insight) checks made to ascertain its intentions or sincerity are made with Disadvantage. Legendary Resistance (3/Day, or 4/Day in Lair). If the sphinx fails a saving throw, it can choose to succeed instead.
				Actions: Multiattack. The sphinx makes two Claw attacks and uses Roar. Claw. Melee Attack Roll: +12, reach 5 ft. Hit: 20 (4d6 + 6) Slashing damage. Roar (3/Day). The sphinx emits a magical roar. Whenever it roars, the roar has a different effect, as detailed below (the sequence resets when it takes a Long Rest): First Roar. Wisdom Saving Throw: DC 20, each enemy in a 500-foot Emanation originating from the sphinx. Failure: The target has the Frightened condition for 1 minute. Second Roar. Wisdom Saving Throw: DC 20, each enemy in a 500-foot Emanation originating from the sphinx. Failure: The target has the Paralyzed condition, and it repeats the save at the end of each of its turns, ending the effect on itself on a success. After 1 minute, it succeeds automatically. Third Roar. Constitution Saving Throw: DC 20, each enemy in a 500-foot Emanation originating from the sphinx. Failure: 44 (8d10) Thunder damage, and the target has the Prone condition. Success: Half damage only. Spellcasting. The sphinx casts one of the following spells, requiring no Material components and using Wisdom as the spellcasting ability (spell save DC 20): At Will: Detect Evil and Good, Thaumaturgy. 1/Day Each: Detect Magic, Dispel Magic, Greater Restoration, Heroes' Feast, Zone of Truth.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the sphinx can expend a use to take one of the following actions. The sphinx regains all expended uses at the start of each of its turns. Arcane Prowl. The sphinx can teleport up to 30 feet to an unoccupied space it can see, and it makes one Claw attack. Weight of Years. Constitution Saving Throw: DC 16, one creature the sphinx can see within 120 feet. Failure: The target gains 1 Exhaustion level. While the target has any Exhaustion levels, it appears 3d10 years older. Failure or Success: The sphinx can't take this action again until the start of its next turn.
			"""
		),
		monster(
			name = "Spirit Naga",
			subtitle = "Large Fiend, Chaotic Evil",
			ac = "17",
			initiative = "+3 (13)",
			hp = "135 (18d10 + 36)",
			speed = "40 ft.",
			cr = "8 (XP 3,900; PB +3)",
			body = """
				STR 18 (+4 save +4), DEX 17 (+3 save +6), CON 14 (+2 save +5), INT 16 (+3 save +3), WIS 15 (+2 save +5), CHA 16 (+3 save +6)
				Immunities: Poison; Charmed, Poisoned
				Senses: Darkvision 60 ft.; Passive Perception 12
				Languages: Abyssal, Common
				Trait: Fiendish Restoration. If it dies, the naga returns to life in 1d6 days and regains all its Hit Points. Only a Wish spell can prevent this trait from functioning.
				Actions: Multiattack. The naga makes three attacks, using Bite or Necrotic Ray in any combination. Bite. Melee Attack Roll: +7, reach 10 ft. Hit: 7 (1d6 + 4) Piercing damage plus 14 (4d6) Poison damage. Necrotic Ray. Ranged Attack Roll: +6, range 60 ft. Hit: 21 (6d6) Necrotic damage. Spellcasting. The naga casts one of the following spells, requiring no Somatic or Material components and using Intelligence as the spellcasting ability (spell save DC 14): At Will: Detect Magic, Mage Hand, Minor Illusion, Water Breathing. 2/Day Each: Detect Thoughts, Dimension Door, Hold Person (level 3 version), Lightning Bolt (level 4 version).
			"""
		),
		monster(
			name = "Sprite",
			subtitle = "Tiny Fey, Neutral Good",
			ac = "15",
			initiative = "+4 (14)",
			hp = "10 (4d4)",
			speed = "10 ft., Fly 40 ft.",
			cr = "1/4 (XP 50; PB +2)",
			body = """
				STR 3 (-4 save -4), DEX 18 (+4 save +4), CON 10 (+0 save +0), INT 14 (+2 save +2), WIS 13 (+1 save +1), CHA 11 (+0 save +0)
				Skills: Perception +3, Stealth +8
				Senses: Passive Perception 13
				Languages: Common, Elvish, Sylvan
				Actions: Needle Sword. Melee Attack Roll: +6, reach 5 ft. Hit: 6 (1d4 + 4) Piercing damage. Enchanting Bow. Ranged Attack Roll: +6, range 40/160 ft. Hit: 1 Piercing damage, and the target has the Charmed condition until the start of the sprite's next turn. Heart Sight. Charisma Saving Throw: DC 10, one creature within 5 feet the sprite can see (Celestials, Fiends, and Undead automatically fail the save). Failure: The sprite knows the target's emotions and alignment. Invisibility. The sprite casts Invisibility on itself, requiring no spell components and using Charisma as the spellcasting ability.
			"""
		),
		monster(
			name = "Spy",
			subtitle = "Medium or Small Humanoid, Neutral",
			ac = "12",
			initiative = "+4 (14)",
			hp = "27 (6d8)",
			speed = "30 ft., Climb 30 ft.",
			cr = "1 (XP 200; PB +2)",
			body = """
				STR 10 (+0 save +0), DEX 15 (+2 save +2), CON 10 (+0 save +0), INT 12 (+1 save +1), WIS 14 (+2 save +2), CHA 16 (+3 save +3)
				Skills: Deception +5, Insight +4, Investigation +5, Perception +6, Sleight of Hand +4, Stealth +6
				Gear: Hand Crossbow, Shortsword, Thieves' Tools
				Senses: Passive Perception 16
				Languages: Common plus one other language
				Actions: Shortsword. Melee Attack Roll: +4, reach 5 ft. Hit: 5 (1d6 + 2) Piercing damage plus 7 (2d6) Poison damage. Hand Crossbow. Ranged Attack Roll: +4, range 30/120 ft. Hit: 5 (1d6 + 2) Piercing damage plus 7 (2d6) Poison damage.
				Bonus Actions: Cunning Action. The spy takes the Dash, Disengage, or Hide action.
			"""
		),
		monster(
			name = "Stirge",
			subtitle = "Tiny Monstrosity, Unaligned",
			ac = "13",
			initiative = "+3 (13)",
			hp = "5 (2d4)",
			speed = "10 ft., Fly 40 ft.",
			cr = "1/8 (XP 25; PB +2)",
			body = """
				STR 4 (-3 save -3), DEX 16 (+3 save +3), CON 11 (+0 save +0), INT 2 (-4 save -4), WIS 8 (-1 save -1), CHA 6 (-2 save -2)
				Senses: Darkvision 60 ft.; Passive Perception 9
				Languages: None
				Actions: Proboscis. Melee Attack Roll: +5, reach 5 ft. Hit: 6 (1d6 + 3) Piercing damage, and the stirge attaches to the target. While attached, the stirge can't make Proboscis attacks, and the target takes 5 (2d4) Necrotic damage at the start of each of the stirge's turns. The stirge can detach itself by spending 5 feet of its movement. The target or a creature within 5 feet of it can detach the stirge as an action.
			"""
		),
		monster(
			name = "Stone Giant",
			subtitle = "Huge Giant, Neutral",
			ac = "17",
			initiative = "+5 (15)",
			hp = "126 (11d12 + 55)",
			speed = "40 ft.",
			cr = "7 (XP 2,900; PB +3)",
			body = """
				STR 23 (+6 save +6), DEX 15 (+2 save +5), CON 20 (+5 save +8), INT 10 (+0 save +0), WIS 12 (+1 save +4), CHA 9 (-1 save -1)
				Skills: Athletics +12, Perception +4, Stealth +5
				Senses: Darkvision 60 ft.; Passive Perception 14
				Languages: Giant
				Actions: Multiattack. The giant makes two attacks, using Stone Club or Boulder in any combination. Stone Club. Melee Attack Roll: +9, reach 15 ft. Hit: 22 (3d10 + 6) Bludgeoning damage. Boulder. Ranged Attack Roll: +9, range 60/240 ft. Hit: 15 (2d8 + 6) Bludgeoning damage. If the target is a Large or smaller creature, it has the Prone condition.
				Reactions: Deflect Missile (Recharge 5-6). Trigger: The giant is hit by a ranged attack roll and takes Bludgeoning, Piercing, or Slashing damage from it. Response: The giant reduces the damage it takes from the attack by 11 (1d10 + 6), and if that damage is reduced to 0, the giant can redirect some of the attack's force. Dexterity Saving Throw: DC 17, one creature the giant can see within 60 feet. Failure: 11 (1d10 + 6) Force damage.
			"""
		),
		monster(
			name = "Stone Golem",
			subtitle = "Large Construct, Unaligned",
			ac = "18",
			initiative = "+3 (13)",
			hp = "220 (21d10 + 105)",
			speed = "30 ft.",
			cr = "10 (XP 5,900; PB +4)",
			body = """
				STR 22 (+6 save +6), DEX 9 (-1 save -1), CON 20 (+5 save +5), INT 3 (-4 save -4), WIS 11 (+0 save +0), CHA 1 (-5 save -5)
				Immunities: Poison, Psychic; Charmed, Exhaustion, Frightened, Paralyzed, Petrified, Poisoned
				Senses: Darkvision 120 ft.; Passive Perception 10
				Languages: Understands Common plus two other languages but can't speak
				Traits: Immutable Form. The golem can't shape-shift. Magic Resistance. The golem has Advantage on saving throws against spells and other magical effects.
				Actions: Multiattack. The golem makes two attacks, using Slam or Force Bolt in any combination. Slam. Melee Attack Roll: +10, reach 5 ft. Hit: 15 (2d8 + 6) Bludgeoning damage plus 9 (2d8) Force damage. Force Bolt. Ranged Attack Roll: +9, range 120 ft. Hit: 22 (4d10) Force damage.
				Bonus Actions: Slow (Recharge 5-6). The golem casts the Slow spell, requiring no spell components and using Constitution as the spellcasting ability (spell save DC 17).
			"""
		),
		monster(
			name = "Storm Giant",
			subtitle = "Huge Giant, Chaotic Good",
			ac = "16",
			initiative = "+7 (17)",
			hp = "230 (20d12 + 100)",
			speed = "50 ft., Fly 25 ft. (hover), Swim 50 ft.",
			cr = "13 (XP 10,000; PB +5)",
			body = """
				STR 29 (+9 save +14), DEX 14 (+2 save +2), CON 20 (+5 save +10), INT 16 (+3 save +3), WIS 20 (+5 save +10), CHA 18 (+4 save +9)
				Skills: Arcana +8, Athletics +14, History +8, Perception +10
				Resistances: Cold
				Immunities: Lightning, Thunder
				Senses: Darkvision 120 ft., Truesight 30 ft.; Passive Perception 20
				Languages: Common, Giant
				Trait: Amphibious. The giant can breathe air and water.
				Actions: Multiattack. The giant makes two attacks, using Storm Sword or Thunderbolt in any combination. Storm Sword. Melee Attack Roll: +14, reach 10 ft. Hit: 23 (4d6 + 9) Slashing damage plus 13 (3d8) Lightning damage. Thunderbolt. Ranged Attack Roll: +14, range 500 ft. Hit: 22 (2d12 + 9) Lightning damage, and the target has the Blinded and Deafened conditions until the start of the giant's next turn. Lightning Storm (Recharge 5-6). Dexterity Saving Throw: DC 18, each creature in a 10-foot-radius, 40-foot-high Cylinder originating from a point the giant can see within 500 feet. Failure: 55 (10d10) Lightning damage. Success: Half damage. Spellcasting. The giant casts one of the following spells, requiring no Material components and using Wisdom as the spellcasting ability (spell save DC 18): At Will: Detect Magic, Light. 1/Day: Control Weather.
			"""
		),
		monster(
			name = "Succubus",
			subtitle = "Medium Fiend, Neutral Evil",
			ac = "15",
			initiative = "+3 (13)",
			hp = "71 (13d8 + 13)",
			speed = "30 ft., Fly 60 ft.",
			cr = "4 (XP 1,100; PB +2)",
			body = """
				STR 8 (-1 save -1), DEX 17 (+3 save +3), CON 13 (+1 save +1), INT 15 (+2 save +2), WIS 12 (+1 save +1), CHA 20 (+5 save +5)
				Skills: Deception +9, Insight +5, Perception +5, Persuasion +9, Stealth +7
				Resistances: Cold, Fire, Poison, Psychic
				Senses: Darkvision 60 ft.; Passive Perception 15
				Languages: Abyssal, Common, Infernal; telepathy 60 ft.
				Trait: Incubus Form. When the succubus finishes a Long Rest, it can shape-shift into an Incubus, using that stat block instead of this one.
				Actions: Multiattack. The succubus makes one Fiendish Touch attack and uses Charm or Draining Kiss. Fiendish Touch. Melee Attack Roll: +7, reach 5 ft. Hit: 16 (2d10 + 5) Psychic damage. Charm. The succubus casts Dominate Person (level 8 version), requiring no spell components and using Charisma as the spellcasting ability (spell save DC 15). Draining Kiss. Constitution Saving Throw: DC 15, one creature Charmed by the succubus within 5 feet. Failure: 13 (3d8) Psychic damage. Success: Half damage. Failure or Success: The target's Hit Point maximum decreases by an amount equal to the damage taken.
				Bonus Actions: Shape-Shift. The succubus shape-shifts into a Medium or Small Humanoid, or it returns to its true form. Its game statistics are the same in each form, except its Fly Speed is available only in its true form. Any equipment it is wearing or carrying isn't transformed.
			"""
		),
		monster(
			name = "Tarrasque",
			subtitle = "Gargantuan Monstrosity (Titan), Unaligned",
			ac = "25",
			initiative = "+18 (28)",
			hp = "697 (34d20 + 340)",
			speed = "60 ft., Burrow 40 ft., Climb 60 ft.",
			cr = "30 (XP 155,000; PB +9)",
			body = """
				STR 30 (+10 save +10), DEX 11 (+0 save +9), CON 30 (+10 save +10), INT 3 (-4 save +5), WIS 11 (+0 save +9), CHA 11 (+0 save +9)
				Skills: Perception +9
				Resistances: Bludgeoning, Piercing, Slashing
				Immunities: Fire, Poison; Charmed, Deafened, Frightened, Paralyzed, Poisoned
				Senses: Blindsight 120 ft.; Passive Perception 19
				Languages: None
				Traits: Legendary Resistance (6/Day). If the tarrasque fails a saving throw, it can choose to succeed instead. Magic Resistance. The tarrasque has Advantage on saving throws against spells and other magical effects. Reflective Carapace. If the tarrasque is targeted by a Magic Missile spell or a spell that requires a ranged attack roll, roll 1d6. On a 1-5, the tarrasque is unaffected. On a 6, the tarrasque is unaffected and reflects the spell, turning the caster into the target. Siege Monster. The tarrasque deals double damage to objects and structures.
				Actions: Multiattack. The tarrasque makes one Bite attack and three other attacks, using Claw or Tail in any combination. Bite. Melee Attack Roll: +19, reach 15 ft. Hit: 36 (4d12 + 10) Piercing damage, and the target has the Grappled condition (escape DC 20). Until the grapple ends, the target has the Restrained condition and can't teleport. Claw. Melee Attack Roll: +19, reach 15 ft. Hit: 28 (4d8 + 10) Slashing damage. Tail. Melee Attack Roll: +19, reach 30 ft. Hit: 23 (3d8 + 10) Bludgeoning damage. If the target is a Huge or smaller creature, it has the Prone condition. Thunderous Bellow (Recharge 5-6). Constitution Saving Throw: DC 27, each creature and each object that isn't being worn or carried in a 150-foot Cone. Failure: 78 (12d12) Thunder damage, and the target has the Deafened and Frightened conditions until the end of its next turn. Success: Half damage only.
				Bonus Actions: Swallow. Strength Saving Throw: DC 27, one Large or smaller creature Grappled by the tarrasque (it can have up to six creatures swallowed at a time). Failure: The target is swallowed, and the Grappled condition ends. A swallowed creature has the Blinded and Restrained conditions and can't teleport, it has Total Cover against attacks and other effects outside the tarrasque, and it takes 56 (16d6) Acid damage at the start of each of the tarrasque's turns. If the tarrasque takes 60 damage or more on a single turn from a creature inside it, the tarrasque must succeed on a DC 20 Constitution saving throw at the end of that turn or regurgitate all swallowed creatures, each of which falls in a space within 10 feet of the tarrasque and has the Prone condition. If the tarrasque dies, any swallowed creature no longer has the Restrained condition and can escape from the corpse using 20 feet of movement, exiting Prone.
				Legendary Actions: Legendary Action Uses: 3. Immediately after another creature's turn, the tarrasque can expend a use to take one of the following actions. The tarrasque regains all expended uses at the start of each of its turns. Onslaught. The tarrasque moves up to half its Speed, and it makes one Claw or Tail attack. World-Shaking Movement. The tarrasque moves up to its Speed. At the end of this movement, the tarrasque creates an instantaneous shock wave in a 60-foot Emanation originating from itself. Creatures in that area lose Concentration and, if Medium or smaller, have the Prone condition. The tarrasque can't take this action again until the start of its next turn.
			"""
		),
		monster(
			name = "Tough",
			subtitle = "Medium or Small Humanoid, Neutral",
			group = "Toughs",
			ac = "12",
			initiative = "+1 (11)",
			hp = "32 (5d8 + 10)",
			speed = "30 ft.",
			cr = "1/2 (XP 100; PB +2)",
			body = """
				STR 15 (+2 save +2), DEX 12 (+1 save +1), CON 14 (+2 save +2), INT 10 (+0 save +0), WIS 10 (+0 save +0), CHA 11 (+0 save +0)
				Gear: Heavy Crossbow, Leather Armor, Mace
				Senses: Passive Perception 10
				Languages: Common
				Trait: Pack Tactics. The tough has Advantage on an attack roll against a creature if at least one of the tough's allies is within 5 feet of the creature and the ally doesn't have the Incapacitated condition.
				Actions: Mace. Melee Attack Roll: +4, reach 5 ft. Hit: 5 (1d6 + 2) Bludgeoning damage. Heavy Crossbow. Ranged Attack Roll: +3, range 100/400 ft. Hit: 6 (1d10 + 1) Piercing damage.
			"""
		),
		monster(
			name = "Tough Boss",
			subtitle = "Medium or Small Humanoid, Neutral",
			group = "Toughs",
			ac = "16",
			initiative = "+2 (12)",
			hp = "82 (11d8 + 33)",
			speed = "30 ft.",
			cr = "4 (XP 1,100; PB +2)",
			body = """
				STR 17 (+3 save +5), DEX 14 (+2 save +2), CON 16 (+3 save +5), INT 11 (+0 save +0), WIS 10 (+0 save +0), CHA 11 (+0 save +2)
				Gear: Chain Mail, Heavy Crossbow, Warhammer
				Senses: Passive Perception 10
				Languages: Common plus one other language
				Trait: Pack Tactics. The tough has Advantage on an attack roll against a creature if at least one of the tough's allies is within 5 feet of the creature and the ally doesn't have the Incapacitated condition.
				Actions: Multiattack. The tough makes two attacks, using Warhammer or Heavy Crossbow in any combination. Warhammer. Melee Attack Roll: +5, reach 5 ft. Hit: 12 (2d8 + 3) Bludgeoning damage. If the target is a Large or smaller creature, the tough pushes the target up to 10 feet straight away from itself. Heavy Crossbow. Ranged Attack Roll: +4, range 100/400 ft. Hit: 13 (2d10 + 2) Piercing damage.
			"""
		),
		monster(
			name = "Treant",
			subtitle = "Huge Plant, Chaotic Good",
			ac = "16",
			initiative = "+3 (13)",
			hp = "138 (12d12 + 60)",
			speed = "30 ft.",
			cr = "9 (XP 5,000; PB +4)",
			body = """
				STR 23 (+6 save +6), DEX 8 (-1 save -1), CON 21 (+5 save +5), INT 12 (+1 save +1), WIS 16 (+3 save +3), CHA 12 (+1 save +1)
				Vulnerabilities: Fire
				Resistances: Bludgeoning, Piercing
				Senses: Passive Perception 13
				Languages: Common, Druidic, Elvish, Sylvan
				Trait: Siege Monster. The treant deals double damage to objects and structures.
				Actions: Multiattack. The treant makes two Slam attacks. Slam. Melee Attack Roll: +10, reach 5 ft. Hit: 16 (3d6 + 6) Bludgeoning damage. Hail of Bark. Ranged Attack Roll: +10, range 180 ft. Hit: 28 (4d10 + 6) Piercing damage. Animate Trees (1/Day). The treant magically animates up to two trees it can see within 60 feet of itself. Each tree uses the Treant stat block, except it has Intelligence and Charisma scores of 1, it can't speak, and it lacks this action. The tree takes its turn immediately after the treant on the same Initiative count, and it obeys the treant. A tree remains animate for 1 day or until it dies, the treant dies, or it is more than 120 feet from the treant. The tree then takes root if possible.
			"""
		),
		monster(
			name = "Troll",
			subtitle = "Large Giant, Chaotic Evil",
			ac = "15",
			initiative = "+1 (11)",
			hp = "94 (9d10 + 45)",
			speed = "30 ft.",
			cr = "5 (XP 1,800; PB +3)",
			body = """
				STR 18 (+4 save +4), DEX 13 (+1 save +1), CON 20 (+5 save +5), INT 7 (-2 save -2), WIS 9 (-1 save -1), CHA 7 (-2 save -2)
				Skills: Perception +5
				Senses: Darkvision 60 ft.; Passive Perception 15
				Languages: Giant
				Traits: Loathsome Limbs (4/Day). If the troll ends any turn Bloodied and took 15+ Slashing damage during that turn, one of the troll's limbs is severed, falls into the troll's space, and becomes a Troll Limb. The limb acts immediately after the troll's turn. The troll has 1 Exhaustion level for each missing limb, and it grows replacement limbs the next time it regains Hit Points. Regeneration. The troll regains 15 Hit Points at the start of each of its turns. If the troll takes Acid or Fire damage, this trait doesn't function on the troll's next turn. The troll dies only if it starts its turn with 0 Hit Points and doesn't regenerate.
				Actions: Multiattack. The troll makes three Rend attacks. Rend. Melee Attack Roll: +7, reach 10 ft. Hit: 11 (2d6 + 4) Slashing damage.
				Bonus Actions: Charge. The troll moves up to half its Speed straight toward an enemy it can see.
			"""
		),
		monster(
			name = "Troll Limb",
			subtitle = "Small Giant, Chaotic Evil",
			ac = "13",
			initiative = "+1 (11)",
			hp = "14 (4d6)",
			speed = "20 ft.",
			cr = "1/2 (XP 100; PB +2)",
			body = """
				STR 18 (+4 save +4), DEX 12 (+1 save +1), CON 10 (+0 save +0), INT 1 (-5 save -5), WIS 9 (-1 save -1), CHA 1 (-5 save -5)
				Senses: Darkvision 60 ft.; Passive Perception 9
				Languages: None
				Traits: Regeneration. The limb regains 5 Hit Points at the start of each of its turns. If the limb takes Acid or Fire damage, this trait doesn't function on the limb's next turn. The limb dies only if it starts its turn with 0 Hit Points and doesn't regenerate. Troll Spawn. The limb uncannily has the same senses as a whole troll. If the limb isn't destroyed within 24 hours, roll 1d12. On a 12, the limb turns into a Troll. Otherwise, the limb withers away.
				Actions: Rend. Melee Attack Roll: +6, reach 5 ft. Hit: 9 (2d4 + 4) Slashing damage.
			"""
		),
		monster(
			name = "Unicorn",
			subtitle = "Large Celestial, Lawful Good",
			ac = "12",
			initiative = "+8 (18)",
			hp = "97 (13d10 + 26)",
			speed = "50 ft.",
			cr = "5 (XP 1,800; PB +3)",
			body = """
				STR 18 (+4 save +4), DEX 14 (+2 save +2), CON 15 (+2 save +2), INT 11 (+0 save +0), WIS 17 (+3 save +3), CHA 16 (+3 save +3)
				Immunities: Poison; Charmed, Paralyzed, Poisoned
				Senses: Darkvision 60 ft.; Passive Perception 13
				Languages: Celestial, Elvish, Sylvan; telepathy 120 ft.
				Traits: Legendary Resistance (3/Day). If the unicorn fails a saving throw, it can choose to succeed instead. Magic Resistance. The unicorn has Advantage on saving throws against spells and other magical effects.
				Actions: Multiattack. The unicorn makes one Hooves attack and one Radiant Horn attack. Hooves. Melee Attack Roll: +7, reach 5 ft. Hit: 11 (2d6 + 4) Bludgeoning damage. Radiant Horn. Melee Attack Roll: +7, reach 5 ft. Hit: 9 (1d10 + 4) Radiant damage. Spellcasting. The unicorn casts one of the following spells, requiring no spell components and using Charisma as the spellcasting ability (spell save DC 14): At Will: Detect Evil and Good, Druidcraft. 1/Day Each: Calm Emotions, Dispel Evil and Good, Entangle, Pass without Trace, Word of Recall.
				Bonus Actions: Unicorn's Blessing (3/Day). The unicorn touches another creature with its horn and casts Cure Wounds or Lesser Restoration on that creature, using the same spellcasting ability as Spellcasting.
				Legendary Actions: Legendary Action Uses: 3. Immediately after another creature's turn, the unicorn can expend a use to take one of the following actions. The unicorn regains all expended uses at the start of each of its turns. Charging Horn. The unicorn moves up to half its Speed without provoking Opportunity Attacks, and it makes one Radiant Horn attack. Shimmering Shield. The unicorn targets itself or one creature it can see within 60 feet of itself. The target gains 10 (3d6) Temporary Hit Points, and its AC increases by 2 until the end of the unicorn's next turn. The unicorn can't take this action again until the start of its next turn.
			"""
		),
		monster(
			name = "Vampire Familiar",
			subtitle = "Medium or Small Humanoid, Neutral Evil",
			group = "Vampires",
			ac = "15",
			initiative = "+5 (15)",
			hp = "65 (10d8 + 20)",
			speed = "30 ft., Climb 30 ft.",
			cr = "3 (XP 700; PB +2)",
			body = """
				STR 17 (+3 save +3), DEX 16 (+3 save +5), CON 15 (+2 save +2), INT 10 (+0 save +0), WIS 10 (+0 save +2), CHA 14 (+2 save +2)
				Skills: Perception +4, Persuasion +4, Stealth +7
				Resistances: Necrotic
				Immunities: Charmed (except from its vampire master)
				Gear: Daggers (10)
				Senses: Darkvision 60 ft.; Passive Perception 14
				Languages: Common plus one other language
				Trait: Vampiric Connection. While the familiar and its vampire master are on the same plane of existence, the vampire can communicate with the familiar telepathically, and the vampire can perceive through the familiar's senses.
				Actions: Multiattack. The familiar makes two Umbral Dagger attacks. Umbral Dagger. Melee or Ranged Attack Roll: +5, reach 5 ft. or range 20/60 ft. Hit: 5 (1d4 + 3) Piercing damage plus 7 (3d4) Necrotic damage. If the target is reduced to 0 Hit Points by this attack, the target becomes Stable but has the Poisoned condition for 1 hour. While it has the Poisoned condition, the target has the Paralyzed condition.
				Bonus Actions: Deathless Agility. The familiar takes the Dash or Disengage action.
			"""
		),
		monster(
			name = "Vampire Spawn",
			subtitle = "Medium or Small Undead, Neutral Evil",
			group = "Vampires",
			ac = "16",
			initiative = "+3 (13)",
			hp = "90 (12d8 + 36)",
			speed = "30 ft.",
			cr = "5 (XP 1,800; PB +3)",
			body = """
				STR 16 (+3 save +3), DEX 16 (+3 save +6), CON 16 (+3 save +3), INT 11 (+0 save +0), WIS 10 (+0 save +3), CHA 12 (+1 save +1)
				Skills: Perception +3, Stealth +6
				Resistances: Necrotic
				Senses: Darkvision 60 ft.; Passive Perception 13
				Languages: Common plus one other language
				Traits: Spider Climb. The vampire can climb difficult surfaces, including along ceilings, without needing to make an ability check. Vampire Weakness. The vampire has these weaknesses: Forbiddance. The vampire can't enter a residence without an invitation from an occupant. Running Water. The vampire takes 20 Acid damage if it ends its turn in running water. Stake to the Heart. The vampire is destroyed if a weapon that deals Piercing damage is driven into the vampire's heart while the vampire has the Incapacitated condition. Sunlight. The vampire takes 20 Radiant damage if it starts its turn in sunlight. While in sunlight, it has Disadvantage on attack rolls and ability checks.
				Actions: Multiattack. The vampire makes two Claw attacks and uses Bite. Claw. Melee Attack Roll: +6, reach 5 ft. Hit: 8 (2d4 + 3) Slashing damage. If the target is a Medium or smaller creature, it has the Grappled condition (escape DC 13) from one of two claws. Bite. Constitution Saving Throw: DC 14, one creature within 5 feet that is willing or that has the Grappled, Incapacitated, or Restrained condition. Failure: 5 (1d4 + 3) Piercing damage plus 10 (3d6) Necrotic damage. The target's Hit Point maximum decreases by an amount equal to the Necrotic damage taken, and the vampire regains Hit Points equal to that amount.
				Bonus Actions: Deathless Agility. The vampire takes the Dash or Disengage action.
			"""
		),
		monster(
			name = "Vampire",
			subtitle = "Medium or Small Undead, Lawful Evil",
			group = "Vampires",
			ac = "16",
			initiative = "+14 (24)",
			hp = "195 (23d8 + 92)",
			speed = "40 ft., Climb 40 ft.",
			cr = "13 (XP 10,000, or 11,500 in lair; PB +5)",
			body = """
				STR 18 (+4 save +4), DEX 18 (+4 save +9), CON 18 (+4 save +9), INT 17 (+3 save +3), WIS 15 (+2 save +7), CHA 18 (+4 save +9)
				Skills: Perception +7, Stealth +9
				Resistances: Necrotic
				Senses: Darkvision 120 ft.; Passive Perception 17
				Languages: Common plus two other languages
				Traits: Legendary Resistance (3/Day, or 4/Day in Lair). If the vampire fails a saving throw, it can choose to succeed instead. Misty Escape. If the vampire drops to 0 Hit Points outside its resting place, the vampire uses Shape-Shift to become mist (no action required). If it can't use Shape-Shift, it is destroyed. While it has 0 Hit Points in mist form, it can't return to its vampire form, and it must reach its resting place within 2 hours or be destroyed. Once in its resting place, it returns to its vampire form and has the Paralyzed condition until it regains any Hit Points, and it regains 1 Hit Point after spending 1 hour there. Spider Climb. The vampire can climb difficult surfaces, including along ceilings, without needing to make an ability check. Vampire Weakness. The vampire has these weaknesses: Forbiddance. The vampire can't enter a residence without an invitation from an occupant. Running Water. The vampire takes 20 Acid damage if it ends its turn in running water. Stake to the Heart. If a weapon that deals Piercing damage is driven into the vampire's heart while the vampire has the Incapacitated condition in its resting place, the vampire has the Paralyzed condition until the weapon is removed. Sunlight. The vampire takes 20 Radiant damage if it starts its turn in sunlight. While in sunlight, it has Disadvantage on attack rolls and ability checks.
				Actions: Multiattack (Vampire Form Only). The vampire makes two Grave Strike attacks and uses Bite. Grave Strike (Vampire Form Only). Melee Attack Roll: +9, reach 5 ft. Hit: 8 (1d8 + 4) Bludgeoning damage plus 7 (2d6) Necrotic damage. If the target is a Large or smaller creature, it has the Grappled condition (escape DC 14) from one of two hands. Bite (Bat or Vampire Form Only). Constitution Saving Throw: DC 17, one creature within 5 feet that is willing or that has the Grappled, Incapacitated, or Restrained condition. Failure: 6 (1d4 + 4) Piercing damage plus 13 (3d8) Necrotic damage. The target's Hit Point maximum decreases by an amount equal to the Necrotic damage taken, and the vampire regains Hit Points equal to that amount. A Humanoid reduced to 0 Hit Points by this damage and then buried rises the following sunset as a Vampire Spawn under the vampire's control.
				Bonus Actions: Charm (Recharge 5-6). The vampire casts Charm Person, requiring no spell components and using Charisma as the spellcasting ability (spell save DC 17), and the duration is 24 hours. The Charmed target is a willing recipient of the vampire's Bite, the damage of which doesn't end the spell. When the spell ends, the target is unaware it was Charmed by the vampire. Shape-Shift. If the vampire isn't in sunlight or running water, it shape-shifts into a Tiny bat (Speed 5 ft., Fly Speed 30 ft.) or a Medium cloud of mist (Speed 5 ft., Fly Speed 20 ft. [hover]), or it returns to its vampire form. Anything it is wearing transforms with it. While in bat form, the vampire can't speak. Its game statistics, other than its size and Speed, are unchanged. While in mist form, the vampire can't take any actions, speak, or manipulate objects. It is weightless and can enter an enemy's space and stop there. If air can pass through a space, the mist can do so, but it can't pass through liquid. It has Resistance to all damage, except the damage it takes from sunlight.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the vampire can expend a use to take one of the following actions. The vampire regains all expended uses at the start of each of its turns. Beguile. The vampire casts Command, requiring no spell components and using Charisma as the spellcasting ability (spell save DC 17). The vampire can't take this action again until the start of its next turn. Deathless Strike. The vampire moves up to half its Speed, and it makes one Grave Strike attack.
			"""
		),
		monster(
			name = "Vrock",
			subtitle = "Large Fiend (Demon), Chaotic Evil",
			ac = "15",
			initiative = "+2 (12)",
			hp = "152 (16d10 + 64)",
			speed = "40 ft., Fly 60 ft.",
			cr = "6 (XP 2,300; PB +3)",
			body = """
				STR 17 (+3 save +3), DEX 15 (+2 save +5), CON 18 (+4 save +4), INT 8 (-1 save -1), WIS 13 (+1 save +4), CHA 8 (-1 save +2)
				Resistances: Cold, Fire, Lightning
				Immunities: Poison; Poisoned
				Senses: Darkvision 120 ft.; Passive Perception 11
				Languages: Abyssal; telepathy 120 ft.
				Traits: Demonic Restoration. If the vrock dies outside the Abyss, its body dissolves into ichor, and it gains a new body instantly, reviving with all its Hit Points somewhere in the Abyss. Magic Resistance. The vrock has Advantage on saving throws against spells and other magical effects.
				Actions: Multiattack. The vrock makes two Shred attacks. Shred. Melee Attack Roll: +6, reach 5 ft. Hit: 10 (2d6 + 3) Piercing damage plus 10 (3d6) Poison damage. Spores (Recharge 6). Constitution Saving Throw: DC 15, each creature in a 20-foot Emanation originating from the vrock. Failure: The target has the Poisoned condition and repeats the save at the end of each of its turns, ending the effect on itself on a success. While Poisoned, the target takes 5 (1d10) Poison damage at the start of each of its turns. Emptying a flask of Holy Water on the target ends the effect early. Stunning Screech (1/Day). Constitution Saving Throw: DC 15, each creature in a 20-foot Emanation originating from the vrock (demons succeed automatically). Failure: 10 (3d6) Thunder damage, and the target has the Stunned condition until the end of the vrock's next turn.
			"""
		),
		monster(
			name = "Warrior Infantry",
			subtitle = "Medium or Small Humanoid, Neutral",
			group = "Warriors",
			ac = "13",
			initiative = "+0 (10)",
			hp = "9 (2d8)",
			speed = "30 ft.",
			cr = "1/8 (XP 25; PB +2)",
			body = """
				STR 13 (+1 save +1), DEX 11 (+0 save +0), CON 11 (+0 save +0), INT 8 (-1 save -1), WIS 11 (+0 save +0), CHA 8 (-1 save -1)
				Gear: Chain Shirt, Spear
				Senses: Passive Perception 10
				Languages: Common
				Trait: Pack Tactics. The warrior has Advantage on an attack roll against a creature if at least one of the warrior's allies is within 5 feet of the creature and the ally doesn't have the Incapacitated condition.
				Actions: Spear. Melee or Ranged Attack Roll: +3, reach 5 ft. or range 20/60 ft. Hit: 4 (1d6 + 1) Piercing damage.
			"""
		),
		monster(
			name = "Warrior Veteran",
			subtitle = "Medium or Small Humanoid, Neutral",
			group = "Warriors",
			ac = "17",
			initiative = "+3 (13)",
			hp = "65 (10d8 + 20)",
			speed = "30 ft.",
			cr = "3 (XP 700; PB +2)",
			body = """
				STR 16 (+3 save +3), DEX 13 (+1 save +1), CON 14 (+2 save +2), INT 10 (+0 save +0), WIS 11 (+0 save +0), CHA 10 (+0 save +0)
				Skills: Athletics +5, Perception +2
				Gear: Greatsword, Heavy Crossbow, Splint Armor
				Senses: Passive Perception 12
				Languages: Common plus one other language
				Actions: Multiattack. The warrior makes two Greatsword or Heavy Crossbow attacks. Greatsword. Melee Attack Roll: +5, reach 5 ft. Hit: 10 (2d6 + 3) Slashing damage. Heavy Crossbow. Ranged Attack Roll: +3, range 100/400 ft. Hit: 12 (2d10 + 1) Piercing damage.
				Reactions: Parry. Trigger: The warrior is hit by a melee attack roll while holding a weapon. Response: The warrior adds 2 to its AC against that attack, possibly causing it to miss.
			"""
		),
		monster(
			name = "Water Elemental",
			subtitle = "Large Elemental, Neutral",
			ac = "14",
			initiative = "+2 (12)",
			hp = "114 (12d10 + 48)",
			speed = "30 ft., Swim 90 ft.",
			cr = "5 (XP 1,800; PB +3)",
			body = """
				STR 18 (+4 save +4), DEX 14 (+2 save +2), CON 18 (+4 save +4), INT 5 (-3 save -3), WIS 10 (+0 save +0), CHA 8 (-1 save -1)
				Resistances: Acid, Fire
				Immunities: Poison; Exhaustion, Grappled, Paralyzed, Petrified, Poisoned, Prone, Restrained, Unconscious
				Senses: Darkvision 60 ft.; Passive Perception 10
				Languages: Primordial (Aquan)
				Traits: Freeze. If the elemental takes Cold damage, its Speed decreases by 20 feet until the end of its next turn. Water Form. The elemental can enter an enemy's space and stop there. It can move through a space as narrow as 1 inch without expending extra movement to do so.
				Actions: Multiattack. The elemental makes two Slam attacks. Slam. Melee Attack Roll: +7, reach 5 ft. Hit: 13 (2d8 + 4) Bludgeoning damage. If the target is a Medium or smaller creature, it has the Prone condition. Whelm (Recharge 4-6). Strength Saving Throw: DC 15, each creature in the elemental's space. Failure: 22 (4d8 + 4) Bludgeoning damage. If the target is a Large or smaller creature, it has the Grappled condition (escape DC 14). Until the grapple ends, the target has the Restrained condition, is suffocating unless it can breathe water, and takes 9 (2d8) Bludgeoning damage at the start of each of the elemental's turns. The elemental can grapple one Large creature or up to two Medium or smaller creatures at a time with Whelm. As an action, a creature within 5 feet of the elemental can pull a creature out of it by succeeding on a DC 14 Strength (Athletics) check. Success: Half damage only.
			"""
		),
		monster(
			name = "Werebear",
			subtitle = "Medium or Small Monstrosity (Lycanthrope), Neutral Good",
			ac = "15",
			initiative = "+3 (13)",
			hp = "135 (18d8 + 54)",
			speed = "30 ft., 40 ft. (bear form only), Climb 30 ft. (bear form only)",
			cr = "5 (XP 1,800; PB +3)",
			body = """
				STR 19 (+4 save +4), DEX 10 (+0 save +0), CON 17 (+3 save +3), INT 11 (+0 save +0), WIS 12 (+1 save +1), CHA 12 (+1 save +1)
				Skills: Perception +7
				Gear: Handaxes (4)
				Senses: Darkvision 60 ft.; Passive Perception 17
				Languages: Common (can't speak in bear form)
				Actions: Multiattack. The werebear makes two attacks, using Handaxe or Rend in any combination. It can replace one attack with a Bite attack. Bite (Bear or Hybrid Form Only). Melee Attack Roll: +7, reach 5 ft. Hit: 17 (2d12 + 4) Piercing damage. If the target is a Humanoid, it is subjected to the following effect. Constitution Saving Throw: DC 14. Failure: The target is cursed. If the cursed target drops to 0 Hit Points, it instead becomes a Werebear under the GM's control and has 10 Hit Points. Success: The target is immune to this werebear's curse for 24 hours. Handaxe (Humanoid or Hybrid Form Only). Melee or Ranged Attack Roll: +7, reach 5 ft or range 20/60 ft. Hit: 14 (3d6 + 4) Slashing damage. Rend (Bear or Hybrid Form Only). Melee Attack Roll: +7, reach 5 ft. Hit: 13 (2d8 + 4) Slashing damage.
				Bonus Actions: Shape-Shift. The werebear shape-shifts into a Large bear-humanoid hybrid form or a Large bear, or it returns to its true humanoid form. Its game statistics, other than its size, are the same in each form. Any equipment it is wearing or carrying isn't transformed.
			"""
		),
		monster(
			name = "Wereboar",
			subtitle = "Medium or Small Monstrosity (Lycanthrope), Neutral Evil",
			ac = "15",
			initiative = "+2 (12)",
			hp = "97 (15d8 + 30)",
			speed = "30 ft., 40 ft. (boar form only)",
			cr = "4 (XP 1,100; PB +2)",
			body = """
				STR 17 (+3 save +3), DEX 10 (+0 save +0), CON 15 (+2 save +2), INT 10 (+0 save +0), WIS 11 (+0 save +0), CHA 8 (-1 save -1)
				Skills: Perception +2
				Gear: Javelins (6)
				Senses: Passive Perception 12
				Languages: Common (can't speak in boar form)
				Actions: Multiattack. The wereboar makes two attacks, using Javelin or Tusk in any combination. It can replace one attack with a Gore attack. Gore (Boar or Hybrid Form Only). Melee Attack Roll: +5, reach 5 ft. Hit: 12 (2d8 + 3) Piercing damage. If the target is a Humanoid, it is subjected to the following effect. Constitution Saving Throw: DC 12. Failure: The target is cursed. If the cursed target drops to 0 Hit Points, it instead becomes a Wereboar under the GM's control and has 10 Hit Points. Success: The target is immune to this wereboar's curse for 24 hours. Javelin (Humanoid or Hybrid Form Only). Melee or Ranged Attack Roll: +5, reach 5 ft. or range 30/120 ft. Hit: 13 (3d6 + 3) Piercing damage. Tusk (Boar or Hybrid Form Only). Melee Attack Roll: +5, reach 5 ft. Hit: 10 (2d6 + 3) Piercing damage. If the target is a Medium or smaller creature and the wereboar moved 20+ feet straight toward it immediately before the hit, the target takes an extra 7 (2d6) Piercing damage and has the Prone condition.
				Bonus Actions: Shape-Shift. The wereboar shape-shifts into a Medium boar-humanoid hybrid or a Small boar, or it returns to its true humanoid form. Its game statistics, other than its size, are the same in each form. Any equipment it is wearing or carrying isn't transformed.
			"""
		),
		monster(
			name = "Wererat",
			subtitle = "Medium or Small Monstrosity (Lycanthrope), Lawful Evil",
			ac = "13",
			initiative = "+3 (13)",
			hp = "60 (11d8 + 11)",
			speed = "30 ft., Climb 30 ft.",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 10 (+0 save +0), DEX 16 (+3 save +3), CON 12 (+1 save +1), INT 11 (+0 save +0), WIS 10 (+0 save +0), CHA 8 (-1 save -1)
				Skills: Perception +4, Stealth +5
				Gear: Hand Crossbow
				Senses: Darkvision 60 ft.; Passive Perception 14
				Languages: Common (can't speak in rat form)
				Actions: Multiattack. The wererat makes two attacks, using Scratch or Hand Crossbow in any combination. It can replace one attack with a Bite attack. Bite (Rat or Hybrid Form Only). Melee Attack Roll: +5, reach 5 ft. Hit: 8 (2d4 + 3) Piercing damage. If the target is a Humanoid, it is subjected to the following effect. Constitution Saving Throw: DC 11. Failure: The target is cursed. If the cursed target drops to 0 Hit Points, it instead becomes a Wererat under the GM's control and has 10 Hit Points. Success: The target is immune to this wererat's curse for 24 hours. Scratch. Melee Attack Roll: +5, reach 5 ft. Hit: 6 (1d6 + 3) Slashing damage. Hand Crossbow (Humanoid or Hybrid Form Only). Ranged Attack Roll: +5, range 30/120 ft. Hit: 6 (1d6 + 3) Piercing damage.
				Bonus Actions: Shape-Shift. The wererat shape-shifts into a Medium rat-humanoid hybrid or a Small rat, or it returns to its true humanoid form. Its game statistics, other than its size, are the same in each form. Any equipment it is wearing or carrying isn't transformed.
			"""
		),
		monster(
			name = "Weretiger",
			subtitle = "Medium or Small Monstrosity (Lycanthrope), Neutral",
			ac = "12",
			initiative = "+2 (12)",
			hp = "120 (16d8 + 48)",
			speed = "30 ft., 40 ft. (tiger form only)",
			cr = "4 (XP 1,100; PB +2)",
			body = """
				STR 17 (+3 save +3), DEX 15 (+2 save +2), CON 16 (+3 save +3), INT 10 (+0 save +0), WIS 13 (+1 save +1), CHA 11 (+0 save +0)
				Skills: Perception +5, Stealth +4
				Gear: Longbow
				Senses: Darkvision 60 ft.; Passive Perception 15
				Languages: Common (can't speak in tiger form)
				Actions: Multiattack. The weretiger makes two attacks, using Scratch or Longbow in any combination. It can replace one attack with a Bite attack. Bite (Tiger or Hybrid Form Only). Melee Attack Roll: +5, reach 5 ft. Hit: 12 (2d8 + 3) Piercing damage. If the target is a Humanoid, it is subjected to the following effect. Constitution Saving Throw: DC 13. Failure: The target is cursed. If the cursed target drops to 0 Hit Points, it instead becomes a Weretiger under the GM's control and has 10 Hit Points. Success: The target is immune to this weretiger's curse for 24 hours. Scratch. Melee Attack Roll: +5, reach 5 ft. Hit: 10 (2d6 + 3) Slashing damage. Longbow (Humanoid or Hybrid Form Only). Ranged Attack Roll: +4, range 150/600 ft. Hit: 11 (2d8 + 2) Piercing damage.
				Bonus Actions: Prowl (Tiger or Hybrid Form Only). The weretiger moves up to its Speed without provoking Opportunity Attacks. At the end of this movement, the weretiger can take the Hide action. Shape-Shift. The weretiger shape-shifts into a Large tiger-humanoid hybrid or a Large tiger, or it returns to its true humanoid form. Its game statistics, other than its size, are the same in each form. Any equipment it is wearing or carrying isn't transformed.
			"""
		),
		monster(
			name = "Werewolf",
			subtitle = "Medium or Small Monstrosity (Lycanthrope), Chaotic Evil",
			ac = "15",
			initiative = "+4 (14)",
			hp = "71 (11d8 + 22)",
			speed = "30 ft., 40 ft. (wolf form only)",
			cr = "3 (XP 700; PB +2)",
			body = """
				STR 16 (+3 save +3), DEX 14 (+2 save +2), CON 14 (+2 save +2), INT 10 (+0 save +0), WIS 11 (+0 save +0), CHA 10 (+0 save +0)
				Skills: Perception +4, Stealth +4
				Gear: Longbow
				Senses: Darkvision 60 ft.; Passive Perception 14
				Languages: Common (can't speak in wolf form)
				Trait: Pack Tactics. The werewolf has Advantage on an attack roll against a creature if at least one of the werewolf's allies is within 5 feet of the creature and the ally doesn't have the Incapacitated condition.
				Actions: Multiattack. The werewolf makes two attacks, using Scratch or Longbow in any combination. It can replace one attack with a Bite attack. Bite (Wolf or Hybrid Form Only). Melee Attack Roll: +5, reach 5 ft. Hit: 12 (2d8 + 3) Piercing damage. If the target is a Humanoid, it is subjected to the following effect. Constitution Saving Throw: DC 12. Failure: The target is cursed. If the cursed target drops to 0 Hit Points, it instead becomes a Werewolf under the GM's control and has 10 Hit Points. Success: The target is immune to this werewolf's curse for 24 hours. Scratch. Melee Attack Roll: +5, reach 5 ft. Hit: 10 (2d6 + 3) Slashing damage. Longbow (Humanoid or Hybrid Form Only). Ranged Attack Roll: +4, range 150/600 ft. Hit: 11 (2d8 + 2) Piercing damage.
				Bonus Actions: Shape-Shift. The werewolf shape-shifts into a Large wolf-humanoid hybrid or a Medium wolf, or it returns to its true humanoid form. Its game statistics, other than its size, are the same in each form. Any equipment it is wearing or carrying isn't transformed.
			"""
		),
		monster(
			name = "White Dragon Wyrmling",
			subtitle = "Medium Dragon (Chromatic), Chaotic Evil",
			group = "White Dragons",
			ac = "16",
			initiative = "+2 (12)",
			hp = "32 (5d8 + 10)",
			speed = "30 ft., Burrow 15 ft., Fly 60 ft., Swim 30 ft.",
			cr = "2 (450 XP; PB +2)",
			body = """
				STR 14 (+2 save +2), DEX 10 (+0 save +2), CON 14 (+2 save +2), INT 5 (-3 save -3), WIS 10 (+0 save +2), CHA 11 (+0 save +0)
				Skills: Perception +4, Stealth +2
				Immunities: Cold
				Senses: Blindsight 10 ft., Darkvision 60 ft.; Passive Perception 14
				Languages: Draconic
				Trait: Ice Walk. The dragon can move across and climb icy surfaces without needing to make an ability check. Additionally, Difficult Terrain composed of ice or snow doesn't cost it extra movement.
				Actions: Multiattack. The dragon makes two Rend attacks. Rend. Melee Attack Roll: +4, reach 5 ft. Hit: 6 (1d8 + 2) Slashing damage plus 2 (1d4) Cold damage. Cold Breath (Recharge 5-6). Constitution Saving Throw: DC 12, each creature in a 15-foot Cone. Failure: 22 (5d8) Cold damage. Success: Half damage.
			"""
		),
		monster(
			name = "Young White Dragon",
			subtitle = "Large Dragon (Chromatic), Chaotic Evil",
			group = "White Dragons",
			ac = "17",
			initiative = "+3 (13)",
			hp = "123 (13d10 + 52)",
			speed = "40 ft., Burrow 20 ft., Fly 80 ft., Swim 40 ft.",
			cr = "6 (2,300 XP; PB +3)",
			body = """
				STR 18 (+4 save +4), DEX 10 (+0 save +3), CON 18 (+4 save +4), INT 6 (-2 save -2), WIS 11 (+0 save +3), CHA 12 (+1 save +1)
				Skills: Perception +6, Stealth +3
				Immunities: Cold
				Senses: Blindsight 30 ft., Darkvision 120 ft.; Passive Perception 16
				Languages: Common, Draconic
				Trait: Ice Walk. The dragon can move across and climb icy surfaces without needing to make an ability check. Additionally, Difficult Terrain composed of ice or snow doesn't cost it extra movement.
				Actions: Multiattack. The dragon makes three Rend attacks. Rend. Melee Attack Roll: +7, reach 10 ft. Hit: 9 (2d4 + 4) Slashing damage plus 2 (1d4) Cold damage. Cold Breath (Recharge 5-6). Constitution Saving Throw: DC 15, each creature in a 30-foot Cone. Failure: 40 (9d8) Cold damage. Success: Half damage.
			"""
		),
		monster(
			name = "Adult White Dragon",
			subtitle = "Huge Dragon (Chromatic), Chaotic Evil",
			group = "White Dragons",
			ac = "18",
			initiative = "+10 (20)",
			hp = "200 (16d12 + 96)",
			speed = "40 ft., Burrow 30 ft., Fly 80 ft., Swim 40 ft.",
			cr = "13 (XP 10,000, or 11,500 in lair; PB +5)",
			body = """
				STR 22 (+6 save +6), DEX 10 (+0 save +5), CON 22 (+6 save +6), INT 8 (-1 save -1), WIS 12 (+1 save +6), CHA 12 (+1 save +1)
				Skills: Perception +11, Stealth +5
				Immunities: Cold
				Senses: Blindsight 60 ft., Darkvision 120 ft.; Passive Perception 21
				Languages: Common, Draconic
				Traits: Ice Walk. The dragon can move across and climb icy surfaces without needing to make an ability check. Additionally, Difficult Terrain composed of ice or snow doesn't cost it extra movement. Legendary Resistance (3/Day, or 4/Day in Lair). If the dragon fails a saving throw, it can choose to succeed instead.
				Actions: Multiattack. The dragon makes three Rend attacks. Rend. Melee Attack Roll: +11, reach 10 ft. Hit: 13 (2d6 + 6) Slashing damage plus 4 (1d8) Cold damage. Cold Breath (Recharge 5-6). Constitution Saving Throw: DC 19, each creature in a 60-foot Cone. Failure: 54 (12d8) Cold damage. Success: Half damage.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the dragon can expend a use to take one of the following actions. The dragon regains all expended uses at the start of each of its turns. Freezing Burst. Constitution Saving Throw: DC 14, each creature in a 30-foot-radius Sphere centered on a point the dragon can see within 120 feet. Failure: 7 (2d6) Cold damage, and the target's Speed is 0 until the end of the target's next turn. Failure or Success: The dragon can't take this action again until the start of its next turn. Frightful Presence. The dragon casts Fear, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 14). The dragon can't take this action again until the start of its next turn. Pounce. The dragon moves up to half its Speed, and it makes one Rend attack.
			"""
		),
		monster(
			name = "Ancient White Dragon",
			subtitle = "Gargantuan Dragon (Chromatic), Chaotic Evil",
			group = "White Dragons",
			ac = "20",
			initiative = "+12 (22)",
			hp = "333 (18d20 + 144)",
			speed = "40 ft., Burrow 40 ft., Fly 80 ft., Swim 40 ft.",
			cr = "20 (XP 25,000, or 33,000 in lair; PB +6)",
			body = """
				STR 26 (+8 save +8), DEX 10 (+0 save +6), CON 26 (+8 save +8), INT 10 (+0 save +0), WIS 13 (+1 save +7), CHA 18 (+4 save +4)
				Skills: Perception +13, Stealth +6
				Immunities: Cold
				Senses: Blindsight 60 ft., Darkvision 120 ft.; Passive Perception 23
				Languages: Common, Draconic
				Traits: Ice Walk. The dragon can move across and climb icy surfaces without needing to make an ability check. Additionally, Difficult Terrain composed of ice or snow doesn't cost it extra movement. Legendary Resistance (4/Day, or 5/Day in Lair). If the dragon fails a saving throw, it can choose to succeed instead.
				Actions: Multiattack. The dragon makes three Rend attacks. Rend. Melee Attack Roll: +14, reach 15 ft. Hit: 17 (2d8 + 8) Slashing damage plus 7 (2d6) Cold damage. Cold Breath (Recharge 5-6). Constitution Saving Throw: DC 22, each creature in a 90-foot Cone. Failure: 63 (14d8) Cold damage. Success: Half damage.
				Legendary Actions: Legendary Action Uses: 3 (4 in Lair). Immediately after another creature's turn, the dragon can expend a use to take one of the following actions. The dragon regains all expended uses at the start of each of its turns. Freezing Burst. Constitution Saving Throw: DC 20, each creature in a 30-foot-radius Sphere centered on a point the dragon can see within 120 feet. Failure: 14 (4d6) Cold damage, and the target's Speed is 0 until the end of the target's next turn. Failure or Success: The dragon can't take this action again until the start of its next turn. Frightful Presence. The dragon casts Fear, requiring no Material components and using Charisma as the spellcasting ability (spell save DC 18). The dragon can't take this action again until the start of its next turn. Pounce. The dragon moves up to half its Speed, and it makes one Rend attack.
			"""
		),
		monster(
			name = "Wight",
			subtitle = "Medium Undead, Neutral Evil",
			ac = "14",
			initiative = "+4 (14)",
			hp = "82 (11d8 + 33)",
			speed = "30 ft.",
			cr = "3 (XP 700; PB +2)",
			body = """
				STR 15 (+2 save +2), DEX 14 (+2 save +2), CON 16 (+3 save +3), INT 10 (+0 save +0), WIS 13 (+1 save +1), CHA 15 (+2 save +2)
				Skills: Perception +3, Stealth +4
				Resistances: Necrotic
				Immunities: Poison; Exhaustion, Poisoned
				Gear: Studded Leather Armor
				Senses: Darkvision 60 ft.; Passive Perception 13
				Languages: Common plus one other language
				Trait: Sunlight Sensitivity. While in sunlight, the wight has Disadvantage on ability checks and attack rolls.
				Actions: Multiattack. The wight makes two attacks, using Necrotic Sword or Necrotic Bow in any combination. It can replace one attack with a use of Life Drain. Necrotic Sword. Melee Attack Roll: +4, reach 5 ft. Hit: 6 (1d8 + 2) Slashing damage plus 4 (1d8) Necrotic damage. Necrotic Bow. Ranged Attack Roll: +4, range 150/600 ft. Hit: 6 (1d8 + 2) Piercing damage plus 4 (1d8) Necrotic damage. Life Drain. Constitution Saving Throw: DC 13, one creature within 5 feet. Failure: 6 (1d8 + 2) Necrotic damage, and the target's Hit Point maximum decreases by an amount equal to the damage taken. A Humanoid slain by this attack rises 24 hours later as a Zombie under the wight's control, unless the Humanoid is restored to life or its body is destroyed. The wight can have no more than twelve zombies under its control at a time.
			"""
		),
		monster(
			name = "Will-o'-Wisp",
			subtitle = "Tiny Undead, Chaotic Evil",
			ac = "19",
			initiative = "+9 (19)",
			hp = "27 (11d4)",
			speed = "5 ft., Fly 50 ft. (hover)",
			cr = "2 (XP 450; PB +2)",
			body = """
				STR 1 (-5 save -5), DEX 28 (+9 save +9), CON 10 (+0 save +0), INT 13 (+1 save +1), WIS 14 (+2 save +2), CHA 11 (+0 save +0)
				Resistances: Acid, Bludgeoning, Cold, Fire, Necrotic, Piercing, Slashing
				Immunities: Lightning, Poison; Exhaustion, Grappled, Paralyzed, Petrified, Poisoned, Prone, Restrained, Unconscious
				Senses: Darkvision 120 ft.; Passive Perception 12
				Languages: Common plus one other language
				Traits: Ephemeral. The wisp can't wear or carry anything. Illumination. The wisp sheds Bright Light in a 20-foot radius and Dim Light for an additional 20 feet. Incorporeal Movement. The wisp can move through other creatures and objects as if they were Difficult Terrain. It takes 5 (1d10) Force damage if it ends its turn inside an object.
				Actions: Shock. Melee Attack Roll: +4, reach 5 ft. Hit: 11 (2d8 + 2) Lightning damage.
				Bonus Actions: Consume Life. Constitution Saving Throw: DC 10, one living creature the wisp can see within 5 feet that has 0 Hit Points. Failure: The target dies, and the wisp regains 10 (3d6) Hit Points. Vanish. The wisp and its light have the Invisible condition until the wisp's Concentration ends on this effect, which ends early immediately after the wisp makes an attack roll or uses Consume Life.
			"""
		),
		monster(
			name = "Winter Wolf",
			subtitle = "Large Monstrosity, Neutral Evil",
			ac = "13",
			initiative = "+1 (11)",
			hp = "75 (10d10 + 20)",
			speed = "50 ft.",
			cr = "3 (XP 700; PB +2)",
			body = """
				STR 18 (+4 save +4), DEX 13 (+1 save +1), CON 14 (+2 save +2), INT 7 (-2 save -2), WIS 12 (+1 save +1), CHA 8 (-1 save -1)
				Skills: Perception +5, Stealth +5
				Immunities: Cold
				Senses: Passive Perception 15
				Languages: Common, Giant
				Trait: Pack Tactics. The wolf has Advantage on an attack roll against a creature if at least one of the wolf's allies is within 5 feet of the creature and the ally doesn't have the Incapacitated condition.
				Actions: Bite. Melee Attack Roll: +6, reach 5 ft. Hit: 11 (2d6 + 4) Piercing damage. If the target is a Large or smaller creature, it has the Prone condition. Cold Breath (Recharge 5-6). Constitution Saving Throw: DC 12, each creature in a 15-foot Cone. Failure: 18 (4d8) Cold damage. Success: Half damage.
			"""
		),
		monster(
			name = "Worg",
			subtitle = "Large Fey, Neutral Evil",
			ac = "13",
			initiative = "+1 (11)",
			hp = "26 (4d10 + 4)",
			speed = "50 ft.",
			cr = "1/2 (XP 100; PB +2)",
			body = """
				STR 16 (+3 save +3), DEX 13 (+1 save +1), CON 13 (+1 save +1), INT 7 (-2 save -2), WIS 11 (+0 save +0), CHA 8 (-1 save -1)
				Skills: Perception +4
				Senses: Darkvision 60 ft.; Passive Perception 14
				Languages: Goblin, Worg
				Actions: Bite. Melee Attack Roll: +5, reach 5 ft. Hit: 7 (1d8 + 3) Piercing damage, and the next attack roll made against the target before the start of the worg's next turn has Advantage.
			"""
		),
		monster(
			name = "Wraith",
			subtitle = "Medium or Small Undead, Neutral Evil",
			ac = "13",
			initiative = "+3 (13)",
			hp = "67 (9d8 + 27)",
			speed = "5 ft., Fly 60 ft. (hover)",
			cr = "5 (XP 1,800; PB +3)",
			body = """
				STR 6 (-2 save -2), DEX 16 (+3 save +3), CON 16 (+3 save +3), INT 12 (+1 save +1), WIS 14 (+2 save +2), CHA 15 (+2 save +2)
				Resistances: Acid, Bludgeoning, Cold, Fire, Piercing, Slashing
				Immunities: Necrotic, Poison; Charmed, Exhaustion, Grappled, Paralyzed, Petrified, Poisoned, Prone, Restrained, Unconscious
				Senses: Darkvision 60 ft.; Passive Perception 12
				Languages: Common plus two other languages
				Traits: Incorporeal Movement. The wraith can move through other creatures and objects as if they were Difficult Terrain. It takes 5 (1d10) Force damage if it ends its turn inside an object. Sunlight Sensitivity. While in sunlight, the wraith has Disadvantage on ability checks and attack rolls.
				Actions: Life Drain. Melee Attack Roll: +6, reach 5 ft. Hit: 21 (4d8 + 3) Necrotic damage. If the target is a creature, its Hit Point maximum decreases by an amount equal to the damage taken. Create Specter. The wraith targets a Humanoid corpse within 10 feet of itself that has been dead for no longer than 1 minute. The target's spirit rises as a Specter in the space of its corpse or in the nearest unoccupied space. The specter is under the wraith's control. The wraith can have no more than seven specters under its control at a time.
			"""
		),
		monster(
			name = "Wyvern",
			subtitle = "Large Dragon, Unaligned",
			ac = "14",
			initiative = "+0 (10)",
			hp = "127 (15d10 + 45)",
			speed = "30 ft., Fly 80 ft.",
			cr = "6 (XP 2,300; PB +3)",
			body = """
				STR 19 (+4 save +4), DEX 10 (+0 save +0), CON 16 (+3 save +3), INT 5 (-3 save -3), WIS 12 (+1 save +1), CHA 6 (-2 save -2)
				Skills: Perception +4
				Senses: Darkvision 120 ft.; Passive Perception 14
				Languages: None
				Actions: Multiattack. The wyvern makes one Bite attack and one Sting attack. Bite. Melee Attack Roll: +7, reach 5 ft. Hit: 13 (2d8 + 4) Piercing damage. Sting. Melee Attack Roll: +7, reach 10 ft. Hit: 11 (2d6 + 4) Piercing damage plus 24 (7d6) Poison damage, and the target has the Poisoned condition until the start of the wyvern's next turn.
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


