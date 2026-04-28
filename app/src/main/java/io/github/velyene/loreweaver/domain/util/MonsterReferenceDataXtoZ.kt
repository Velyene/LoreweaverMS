package io.github.velyene.loreweaver.domain.util

object MonsterReferenceDataXtoZ {
	val ENTRIES: List<MonsterReferenceEntry> = listOf(
		monster(
			name = "Xorn",
			subtitle = "Medium Elemental, Neutral",
			ac = "19",
			initiative = "+0 (10)",
			hp = "84 (8d8 + 48)",
			speed = "20 ft., Burrow 20 ft.",
			cr = "5 (XP 1,800; PB +3)",
			body = """
				STR 17 (+3 save +3), DEX 10 (+0 save +0), CON 22 (+6 save +6), INT 11 (+0 save +0), WIS 10 (+0 save +0), CHA 11 (+0 save +0)
				Skills: Perception +6, Stealth +6
				Immunities: Poison; Paralyzed, Petrified, Poisoned
				Senses: Darkvision 60 ft., Tremorsense 60 ft.; Passive Perception 16
				Languages: Primordial (Terran)
				Traits: Earth Glide. The xorn can burrow through nonmagical, unworked earth and stone. While doing so, the xorn doesn't disturb the material it moves through. Treasure Sense. The xorn can pinpoint the location of precious metals and stones within 60 feet of itself.
				Actions: Multiattack. The xorn makes one Bite attack and three Claw attacks. Bite. Melee Attack Roll: +6, reach 5 ft. Hit: 17 (4d6 + 3) Piercing damage. Claw. Melee Attack Roll: +6, reach 5 ft. Hit: 8 (1d10 + 3) Slashing damage.
				Bonus Actions: Charge. The xorn moves up to its Speed or Burrow Speed straight toward an enemy it can sense.
			"""
		),
		monster(
			name = "Zombie",
			subtitle = "Medium Undead, Neutral Evil",
			group = "Zombies",
			ac = "8",
			initiative = "-2 (8)",
			hp = "15 (2d8 + 6)",
			speed = "20 ft.",
			cr = "1/4 (XP 50; PB +2)",
			body = """
				STR 13 (+1 save +1), DEX 6 (-2 save -2), CON 16 (+3 save +3), INT 3 (-4 save -4), WIS 6 (-2 save +0), CHA 5 (-3 save -3)
				Immunities: Poison; Exhaustion, Poisoned
				Senses: Darkvision 60 ft.; Passive Perception 8
				Languages: Understands Common plus one other language but can't speak
				Trait: Undead Fortitude. If damage reduces the zombie to 0 Hit Points, it makes a Constitution saving throw (DC 5 plus the damage taken) unless the damage is Radiant or from a Critical Hit. On a successful save, the zombie drops to 1 Hit Point instead.
				Actions: Slam. Melee Attack Roll: +3, reach 5 ft. Hit: 5 (1d8 + 1) Bludgeoning damage.
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

