/*
 * FILE: SpellcastingReference.kt
 *
 * TABLE OF CONTENTS:
 * 1. Spell classification enums (SchoolOfMagic, SpellComponent, AreaOfEffect, CastingTime)
 * 2. Caster-type models (SpellcasterType, SpellSlotTable, SpellPreparationReferenceEntry)
 * 3. SpellcastingReference singleton — slot tables, caster rules, and spell economy data
 */

package com.example.loreweaver.domain.util

/**
 * Schools of magic.
 */
enum class SchoolOfMagic {
	ABJURATION,      // Protective, barriers
	CONJURATION,     // Transportation, summoning
	DIVINATION,      // Information, secrets
	ENCHANTMENT,     // Mind control, influence
	EVOCATION,       // Energy manipulation, damage
	ILLUSION,        // Deception, sensory tricks
	NECROMANCY,      // Life and death
	TRANSMUTATION    // Change properties
}

/**
 * Spell components.
 */
enum class SpellComponent {
	VERBAL,    // (V) Mystic words
	SOMATIC,   // (S) Gestures
	MATERIAL   // (M) Objects/focus
}

/**
 * Area of effect shapes.
 */
enum class AreaOfEffect {
	CONE,
	CUBE,
	CYLINDER,
	EMANATION,
	LINE,
	SPHERE,
	SELF
}

/**
 * Casting time categories.
 */
enum class CastingTime {
	ACTION,
	BONUS_ACTION,
	REACTION,
	RITUAL,        // +10 minutes
	MINUTES,
	HOURS
}

/**
 * Spellcasting class / progression families supported by the reference calculator.
 */
enum class SpellcasterType {
	BARD,
	CLERIC,
	DRUID,
	SORCERER,
	WIZARD,
	PALADIN,
	RANGER,
	WARLOCK
}

/**
 * Data class for spell slot progression.
 */
data class SpellSlotTable(
	val characterLevel: Int,
	val cantrips: Int,
	val slot1: Int = 0,
	val slot2: Int = 0,
	val slot3: Int = 0,
	val slot4: Int = 0,
	val slot5: Int = 0,
	val slot6: Int = 0,
	val slot7: Int = 0,
	val slot8: Int = 0,
	val slot9: Int = 0
)

data class SpellPreparationReferenceEntry(
	val className: String,
	val changeWhenYou: String,
	val numberOfSpells: String
)

/**
 * Utility object for spellcasting reference.
 */
object SpellcastingReference {
	// Private constants extracted to satisfy SonarQube S1192
	private const val WHEN_GAIN_LEVEL = "Gain a level"
	private const val WHEN_LONG_REST = "Finish a Long Rest"

	val SPELL_PREPARATION_BY_CLASS = listOf(
		SpellPreparationReferenceEntry("Bard", WHEN_GAIN_LEVEL, "One"),
		SpellPreparationReferenceEntry("Cleric", WHEN_LONG_REST, "Any"),
		SpellPreparationReferenceEntry("Druid", WHEN_LONG_REST, "Any"),
		SpellPreparationReferenceEntry("Paladin", WHEN_LONG_REST, "One"),
		SpellPreparationReferenceEntry("Ranger", WHEN_LONG_REST, "One"),
		SpellPreparationReferenceEntry("Sorcerer", WHEN_GAIN_LEVEL, "One"),
		SpellPreparationReferenceEntry("Warlock", WHEN_GAIN_LEVEL, "One"),
		SpellPreparationReferenceEntry("Wizard", WHEN_LONG_REST, "Any")
	)

	val SPELL_PREPARATION_BY_CLASS_TABLE = ReferenceTable(
		title = "Spell Preparation by Class",
		columns = listOf("Class", "Change When You…", "Number of Spells"),
		rows = SPELL_PREPARATION_BY_CLASS.map { entry ->
			listOf(entry.className, entry.changeWhenYou, entry.numberOfSpells)
		}
	)

	private val FULL_CASTER_BASE_SLOTS = listOf(
		SpellSlotTable(1, 0, slot1 = 2),
		SpellSlotTable(2, 0, slot1 = 3),
		SpellSlotTable(3, 0, slot1 = 4, slot2 = 2),
		SpellSlotTable(4, 0, slot1 = 4, slot2 = 3),
		SpellSlotTable(5, 0, slot1 = 4, slot2 = 3, slot3 = 2),
		SpellSlotTable(6, 0, slot1 = 4, slot2 = 3, slot3 = 3),
		SpellSlotTable(7, 0, slot1 = 4, slot2 = 3, slot3 = 3, slot4 = 1),
		SpellSlotTable(8, 0, slot1 = 4, slot2 = 3, slot3 = 3, slot4 = 2),
		SpellSlotTable(9, 0, slot1 = 4, slot2 = 3, slot3 = 3, slot4 = 3, slot5 = 1),
		SpellSlotTable(10, 0, slot1 = 4, slot2 = 3, slot3 = 3, slot4 = 3, slot5 = 2),
		SpellSlotTable(11, 0, slot1 = 4, slot2 = 3, slot3 = 3, slot4 = 3, slot5 = 2, slot6 = 1),
		SpellSlotTable(12, 0, slot1 = 4, slot2 = 3, slot3 = 3, slot4 = 3, slot5 = 2, slot6 = 1),
		SpellSlotTable(
			13,
			0,
			slot1 = 4,
			slot2 = 3,
			slot3 = 3,
			slot4 = 3,
			slot5 = 2,
			slot6 = 1,
			slot7 = 1
		),
		SpellSlotTable(
			14,
			0,
			slot1 = 4,
			slot2 = 3,
			slot3 = 3,
			slot4 = 3,
			slot5 = 2,
			slot6 = 1,
			slot7 = 1
		),
		SpellSlotTable(
			15,
			0,
			slot1 = 4,
			slot2 = 3,
			slot3 = 3,
			slot4 = 3,
			slot5 = 2,
			slot6 = 1,
			slot7 = 1,
			slot8 = 1
		),
		SpellSlotTable(
			16,
			0,
			slot1 = 4,
			slot2 = 3,
			slot3 = 3,
			slot4 = 3,
			slot5 = 2,
			slot6 = 1,
			slot7 = 1,
			slot8 = 1
		),
		SpellSlotTable(
			17,
			0,
			slot1 = 4,
			slot2 = 3,
			slot3 = 3,
			slot4 = 3,
			slot5 = 2,
			slot6 = 1,
			slot7 = 1,
			slot8 = 1,
			slot9 = 1
		),
		SpellSlotTable(
			18,
			0,
			slot1 = 4,
			slot2 = 3,
			slot3 = 3,
			slot4 = 3,
			slot5 = 3,
			slot6 = 1,
			slot7 = 1,
			slot8 = 1,
			slot9 = 1
		),
		SpellSlotTable(
			19,
			0,
			slot1 = 4,
			slot2 = 3,
			slot3 = 3,
			slot4 = 3,
			slot5 = 3,
			slot6 = 2,
			slot7 = 1,
			slot8 = 1,
			slot9 = 1
		),
		SpellSlotTable(
			20,
			0,
			slot1 = 4,
			slot2 = 3,
			slot3 = 3,
			slot4 = 3,
			slot5 = 3,
			slot6 = 2,
			slot7 = 2,
			slot8 = 1,
			slot9 = 1
		)
	)

	private val HALF_CASTER_BASE_SLOTS = listOf(
		SpellSlotTable(1, 0),
		SpellSlotTable(2, 0, slot1 = 2),
		SpellSlotTable(3, 0, slot1 = 3),
		SpellSlotTable(4, 0, slot1 = 3),
		SpellSlotTable(5, 0, slot1 = 4, slot2 = 2),
		SpellSlotTable(6, 0, slot1 = 4, slot2 = 2),
		SpellSlotTable(7, 0, slot1 = 4, slot2 = 3),
		SpellSlotTable(8, 0, slot1 = 4, slot2 = 3),
		SpellSlotTable(9, 0, slot1 = 4, slot2 = 3, slot3 = 2),
		SpellSlotTable(10, 0, slot1 = 4, slot2 = 3, slot3 = 2),
		SpellSlotTable(11, 0, slot1 = 4, slot2 = 3, slot3 = 3),
		SpellSlotTable(12, 0, slot1 = 4, slot2 = 3, slot3 = 3),
		SpellSlotTable(13, 0, slot1 = 4, slot2 = 3, slot3 = 3, slot4 = 1),
		SpellSlotTable(14, 0, slot1 = 4, slot2 = 3, slot3 = 3, slot4 = 1),
		SpellSlotTable(15, 0, slot1 = 4, slot2 = 3, slot3 = 3, slot4 = 2),
		SpellSlotTable(16, 0, slot1 = 4, slot2 = 3, slot3 = 3, slot4 = 2),
		SpellSlotTable(17, 0, slot1 = 4, slot2 = 3, slot3 = 3, slot4 = 3, slot5 = 1),
		SpellSlotTable(18, 0, slot1 = 4, slot2 = 3, slot3 = 3, slot4 = 3, slot5 = 1),
		SpellSlotTable(19, 0, slot1 = 4, slot2 = 3, slot3 = 3, slot4 = 3, slot5 = 2),
		SpellSlotTable(20, 0, slot1 = 4, slot2 = 3, slot3 = 3, slot4 = 3, slot5 = 2)
	)

	private val WARLOCK_BASE_SLOTS = listOf(
		SpellSlotTable(1, 0, slot1 = 1),
		SpellSlotTable(2, 0, slot1 = 2),
		SpellSlotTable(3, 0, slot2 = 2),
		SpellSlotTable(4, 0, slot2 = 2),
		SpellSlotTable(5, 0, slot3 = 2),
		SpellSlotTable(6, 0, slot3 = 2),
		SpellSlotTable(7, 0, slot4 = 2),
		SpellSlotTable(8, 0, slot4 = 2),
		SpellSlotTable(9, 0, slot5 = 2),
		SpellSlotTable(10, 0, slot5 = 2),
		SpellSlotTable(11, 0, slot5 = 3),
		SpellSlotTable(12, 0, slot5 = 3),
		SpellSlotTable(13, 0, slot5 = 3),
		SpellSlotTable(14, 0, slot5 = 3),
		SpellSlotTable(15, 0, slot5 = 3),
		SpellSlotTable(16, 0, slot5 = 3),
		SpellSlotTable(17, 0, slot5 = 4),
		SpellSlotTable(18, 0, slot5 = 4),
		SpellSlotTable(19, 0, slot5 = 4),
		SpellSlotTable(20, 0, slot5 = 4)
	)

	private val SPELLCASTER_SLOT_TABLES = mapOf(
		SpellcasterType.BARD to applyCantripProgression(
			FULL_CASTER_BASE_SLOTS,
			listOf(2, 2, 2, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4)
		),
		SpellcasterType.CLERIC to applyCantripProgression(
			FULL_CASTER_BASE_SLOTS,
			listOf(3, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5)
		),
		SpellcasterType.DRUID to applyCantripProgression(
			FULL_CASTER_BASE_SLOTS,
			listOf(2, 2, 2, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4)
		),
		SpellcasterType.SORCERER to applyCantripProgression(
			FULL_CASTER_BASE_SLOTS,
			listOf(4, 4, 4, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6)
		),
		SpellcasterType.WIZARD to applyCantripProgression(
			FULL_CASTER_BASE_SLOTS,
			listOf(3, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5)
		),
		SpellcasterType.PALADIN to HALF_CASTER_BASE_SLOTS,
		SpellcasterType.RANGER to HALF_CASTER_BASE_SLOTS,
		SpellcasterType.WARLOCK to applyCantripProgression(
			WARLOCK_BASE_SLOTS,
			listOf(2, 2, 2, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4)
		)
	)

	/**
	 * Get school of magic description.
	 */
	fun getSchoolDescription(school: SchoolOfMagic): String {
		return when (school) {
			SchoolOfMagic.ABJURATION -> "Prevents or reverses harmful effects."
			SchoolOfMagic.CONJURATION -> "Transports creatures or objects."
			SchoolOfMagic.DIVINATION -> "Reveals information."
			SchoolOfMagic.ENCHANTMENT -> "Influences minds."
			SchoolOfMagic.EVOCATION -> "Channels energy to create effects that are often destructive."
			SchoolOfMagic.ILLUSION -> "Deceives the mind or senses."
			SchoolOfMagic.NECROMANCY -> "Manipulates life and death."
			SchoolOfMagic.TRANSMUTATION -> "Transforms creatures or objects."
		}
	}

	/**
	 * Get spell level name.
	 */
	fun getSpellLevelName(level: Int): String {
		return when (level) {
			0 -> "Cantrip"
			1 -> "1st Level"
			2 -> "2nd Level"
			3 -> "3rd Level"
			else -> "${level}th Level"
		}
	}

	/**
	 * Get component description.
	 */
	fun getComponentDescription(component: SpellComponent): String {
		return when (component) {
			SpellComponent.VERBAL ->
				"Chanting esoteric words in a normal speaking voice. A gagged creature or one in magical silence " +
					"can’t provide this component."

			SpellComponent.SOMATIC ->
				"A forceful gesticulation or intricate set of gestures. The caster must use at least one hand to " +
					"perform them."

			SpellComponent.MATERIAL ->
				"Particular materials named by the spell. If they have no listed cost and are not consumed, a " +
					"component pouch or spellcasting focus can substitute for them if the caster has a feature that " +
					"allows it."
		}
	}

	/**
	 * Get casting time description.
	 */
	fun getCastingTimeDescription(castingTime: CastingTime): String {
		return when (castingTime) {
			CastingTime.ACTION -> "Most spells require the Magic action to cast."
			CastingTime.BONUS_ACTION ->
				"Some spells are cast as a Bonus Action, often in response to a trigger defined in the spell. " +
					"Even then, you can expend only one spell slot on your turn."

			CastingTime.REACTION -> "Cast in response to a trigger defined in the spell’s Casting Time entry."
			CastingTime.RITUAL ->
				"A Ritual takes 10 minutes longer than normal to cast and doesn’t expend a spell slot, but the " +
					"caster must have the spell prepared."

			CastingTime.MINUTES ->
				"A longer casting time requires the Magic action on each of your turns and Concentration throughout " +
					"the casting. If Concentration is broken, the spell fails without expending its slot."

			CastingTime.HOURS ->
				"Very long castings follow the same ongoing-casting pattern as minute-long spells but over much " +
					"longer intervals."
		}
	}

	/**
	 * Per-caster spell slot tables accessible via getSpellSlotsForCaster / getSpellSlotTableForCaster.
	 */

	fun getSpellcasterDescription(type: SpellcasterType): String {
		return when (type) {
			SpellcasterType.BARD -> "Full caster. Charisma-based spontaneous spellcasting with bardic cantrip progression."
			SpellcasterType.CLERIC -> "Full caster. Prepared divine spellcasting with the cleric cantrip progression."
			SpellcasterType.DRUID -> "Full caster. Prepared primal spellcasting with druid cantrip progression."
			SpellcasterType.SORCERER ->
				"Full caster. Charisma-based spontaneous casting with the highest baseline cantrip count."

			SpellcasterType.WIZARD -> "Full caster. Intelligence-based prepared casting with wizard cantrip progression."
			SpellcasterType.PALADIN ->
				"Half caster. Spell slots begin at level 2; paladins do not gain cantrips from base class " +
					"spellcasting."

			SpellcasterType.RANGER ->
				"Half caster. Spell slots begin at level 2; rangers do not gain cantrips from base class " +
					"spellcasting."

			SpellcasterType.WARLOCK ->
				"Pact Magic. Warlocks recover slots on a short rest and all pact slots are cast at the same level."
		}
	}

	fun getSpellSlotsForCaster(type: SpellcasterType, level: Int): SpellSlotTable? {
		return SPELLCASTER_SLOT_TABLES[type]?.find { it.characterLevel == level }
	}

	fun getSpellSlotTableForCaster(type: SpellcasterType): List<SpellSlotTable> {
		return SPELLCASTER_SLOT_TABLES[type].orEmpty()
	}

	/**
	 * Core spellcasting rules.
	 */
	fun getSpellcastingRules(): Map<String, String> {
		return mapOf(
			"Gaining Spells" to
				"Before you can cast a spell, you must have the spell prepared in your mind or have access to the " +
				"spell from a magic item, such as a Spell Scroll. Your features specify which spells you have access " +
				"to, whether you always have certain spells prepared, and whether you can change the list of spells " +
				"you have prepared.",
			"Preparing Spells" to
				"If you have a list of level 1+ spells you prepare, your spellcasting feature specifies when you can " +
				"change the list and the number of spells you can change, as summarized in the Spell Preparation by " +
				"Class table. Most spellcasting monsters don’t change their lists of prepared spells, but the GM is " +
				"free to alter them.",
			"Always-Prepared Spells" to
				"Certain features might give you a spell that you always have prepared. If you also have a list of " +
				"prepared spells that you can change, a spell that you always have prepared doesn’t count against " +
				"the number of spells on that list.",
			"Casting Spells" to
				"Each spell description has a series of entries that provide the details needed to cast the spell. The " +
				"following sections explain each of those entries, which follow a spell’s name.",
			"Spell Level" to
				"Every spell has a level from 0 to 9, which is indicated in a spell’s description. A spell’s level is " +
				"an indicator of how powerful it is. Cantrips—simple spells that can be cast almost by rote—are " +
				"level 0. The rules for each spellcasting class say when its members gain access to spells of certain " +
				"levels.",
			"Spell Slots" to
				"Spellcasting is taxing, so a spellcaster can cast only a limited number of level 1+ spells before " +
				"resting. Spell slots are the main way a spellcaster’s magical potential is represented. When you " +
				"cast a spell, you expend a slot of that spell’s level or higher. A level 1 spell fits into a slot of " +
				"any size, but a level 2 spell fits only into a slot that is at least level 2. Finishing a Long Rest " +
				"restores any expended spell slots.",
			"Casting without Slots" to
				"There are several ways to cast a spell without expending a spell slot. Cantrips are cast without a " +
				"spell slot. Ritual spells can be cast following the normal rules or as a Ritual, which takes 10 " +
				"minutes longer but doesn’t expend a spell slot; to do so, the spellcaster must have the spell " +
				"prepared. Some special abilities let characters or monsters cast specific spells without a slot, " +
				"often with another limit such as uses per day. Spell Scrolls and some other magic items also let you " +
				"cast a spell without expending a slot.",
			"Using a Higher-Level Spell Slot" to
				"When a spellcaster casts a spell using a slot of a higher level than the spell, the spell takes on the " +
				"higher level for that casting. For instance, if a Wizard casts Magic Missile using a level 2 slot, " +
				"that Magic Missile is level 2. Effectively, the spell expands to fill the slot it is put into.",
			"Casting in Armor" to
				"You must have training with any armor you are wearing to cast spells while wearing it. You are " +
				"otherwise too hampered by the armor for spellcasting.",
			"Class Spell Lists" to
				"If a spell is on a class’s spell list, the class name appears in parentheses after the spell’s " +
				"school of magic.",
			"Casting Time" to
				"A spell’s Casting Time entry specifies whether it requires the Magic action, a Bonus Action, a " +
				"Reaction, or a longer time such as 1 minute or more.",
			"One Spell with a Spell Slot per Turn" to
				"On a turn, you can expend only one spell slot to cast a spell. You can’t cast one slotted spell " +
				"with the Magic action and another slotted spell with a Bonus Action on the same turn.",
			"Reaction and Bonus Action Triggers" to
				"Reaction spells and some Bonus Action spells are cast in response to triggers defined in the " +
				"spell’s Casting Time entry.",
			"Longer Casting Times" to
				"A spell with a casting time of 1 minute or more requires the Magic action on each of your " +
				"turns and Concentration throughout the casting. If Concentration breaks, the spell fails and " +
				"no slot is expended.",
			"Range" to
				"A spell’s range determines how far from the caster the spell’s effect can originate. " +
				"Common forms are distance, Touch, and Self.",
			"Components" to
				"Spells can require Verbal, Somatic, and Material components. If the caster can’t " +
				"provide one or more required components, the spell can’t be cast.",
			"Duration" to
				"A spell’s duration might be Instantaneous, require Concentration, or last for a set " +
				"span of rounds, minutes, hours, or longer. Time-span spells you cast can usually be " +
				"dismissed with no action if you aren’t Incapacitated.",
			"Identifying an Ongoing Spell" to
				"You can try to identify a non-instantaneous spell by its observable effects while its " +
				"duration is ongoing. This requires the Study action and a successful DC 15 " +
				"Intelligence (Arcana) check.",
			"Targets" to
				"A spell’s description explains whether it targets creatures, objects, or something " +
				"else. To target something, you usually need a clear path to it, so it can’t be behind " +
				"Total Cover.",
			"Saving Throws" to
				"Spell save DC = 8 + your spellcasting ability modifier + your Proficiency Bonus. The " +
				"spell says which ability is used and what happens on a success or failure.",
			"Attack Rolls" to "Spell attack modifier = your spellcasting ability modifier + your Proficiency Bonus.",
			"Combining Spell Effects" to
				"Effects of different spells add together while their durations overlap. Effects of the " +
				"same spell cast multiple times don’t combine; only the most potent or most recent " +
				"overlapping effect applies."
		)
	}

	fun getSpellcastingTables(): List<ReferenceTable> {
		return listOf(SPELL_PREPARATION_BY_CLASS_TABLE)
	}

	/**
	 * Area of effect rules.
	 */
	fun getAreaOfEffectRules(area: AreaOfEffect): String {
		return when (area) {
			AreaOfEffect.CONE ->
				"Extends in chosen direction from point of origin. Width at any point equals distance from " +
					"origin. Point of origin not included unless you decide otherwise."

			AreaOfEffect.CUBE ->
				"Point of origin lies anywhere on a face of the cubic effect. Cube size expressed as length " +
					"of each side. Point of origin not included unless you decide otherwise."

			AreaOfEffect.CYLINDER ->
				"Point of origin is center of circle on ground or at spell height. Energy expands from " +
					"origin to perimeter, then shoots up/down to cylinder height. Point of origin is " +
					"included."

			AreaOfEffect.EMANATION ->
				"An area that extends outward from a creature or object and moves with that source for the " +
					"duration unless the spell says otherwise."

			AreaOfEffect.LINE ->
				"Extends in straight path from point of origin up to its length. Has defined width. Point " +
					"of origin not included unless you decide otherwise."

			AreaOfEffect.SPHERE ->
				"Extends outward from selected point of origin. Size expressed as radius in feet. Point of " +
					"origin is included."

			AreaOfEffect.SELF ->
				"Effect originates from and affects only the caster, or creates cone/line originating from " +
					"caster."
		}
	}

	/**
	 * Concentration breaking conditions.
	 */
	fun getConcentrationBreakers(): List<String> {
		return listOf(
			"Casting another concentration spell",
			"Taking damage (DC 10 or half damage taken, whichever is higher)",
			"Being incapacitated",
			"Dying",
			"Environmental phenomena (GM discretion)",
			"Failing to maintain concentration during a spell with a long casting time"
		)
	}

	/**
	 * Calculate spell save DC.
	 */
	fun calculateSpellSaveDC(spellcastingMod: Int, proficiencyBonus: Int, bonuses: Int = 0): Int {
		return 8 + spellcastingMod + proficiencyBonus + bonuses
	}

	/**
	 * Calculate spell attack bonus.
	 */
	fun calculateSpellAttackBonus(
		spellcastingMod: Int,
		proficiencyBonus: Int,
		bonuses: Int = 0
	): Int {
		return spellcastingMod + proficiencyBonus + bonuses
	}


	/**
	 * Get spellcasting tips.
	 */
	fun getSpellcastingTips(): List<String> {
		return listOf(
			"A 1st-level spell fits into any slot, but a 2nd-level spell needs a 2nd-level slot or higher",
			"Finishing a long rest restores all expended spell slots",
			"You can’t concentrate on two spells at once",
			"On your turn, you can expend only one spell slot to cast a spell",
			"Ritual casting adds 10 minutes and doesn’t use a spell slot, but the spell must be prepared",
			"Material components with a cost must be provided (can't use focus)",
			"Consumed material components must be provided for each casting",
			"You can target yourself with a creature-of-your-choice spell unless it requires a hostile " +
				"creature or says otherwise",
			"Invalid targets still waste the spell slot if a slotted spell was cast",
			"The same spell doesn’t stack with itself; use the most potent or most recent overlapping effect"
		)
	}

	/**
	 * Setting-neutral primer on how spellcasting can be framed in a campaign.
	 */
	fun getMagicTheoryOverview(): String {
		return """
			Magic in Play

			Many fantasy settings describe magic as an unseen force that can be studied, invoked,
			bargained with, or prayed for. The exact explanation changes from one world to another,
			but the game rules work even if your table never settles on a single theory.

			Arcane spellcasters often approach magic as a craft or discipline. They learn patterns,
			symbols, sounds, gestures, and materials that reliably produce supernatural effects.

			Divine and primal spellcasters may explain the same effects differently. Their magic can
			come from devotion, sacred vows, spirits, the natural world, or another source of
			supernatural authority chosen by the campaign.

			At the table, spell slots, components, concentration, and duration are the practical tools
			that keep magic understandable. Those rules matter more than any single piece of world lore.

			If you want a stronger flavor, decide how your world talks about magical energy, what
			common people believe about spellcasters, and how rare or dangerous magic feels in play.
		""".trimIndent()
	}

	private fun applyCantripProgression(
		baseSlotTables: List<SpellSlotTable>,
		cantripProgression: List<Int>
	): List<SpellSlotTable> {
		return baseSlotTables.mapIndexed { index, slotTable ->
			slotTable.copy(cantrips = cantripProgression.getOrElse(index) { slotTable.cantrips })
		}
	}
}
