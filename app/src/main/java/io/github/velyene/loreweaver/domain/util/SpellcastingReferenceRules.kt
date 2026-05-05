/*
 * FILE: SpellcastingReferenceRules.kt
 *
 * TABLE OF CONTENTS:
 * 1. Spellcasting Rules Dataset
 */

package io.github.velyene.loreweaver.domain.util

internal object SpellcastingReferenceRules {
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

	fun getSpellLevelName(level: Int): String {
		return when (level) {
			0 -> "Cantrip"
			1 -> "1st Level"
			2 -> "2nd Level"
			3 -> "3rd Level"
			else -> "${level}th Level"
		}
	}

	fun getComponentDescription(component: SpellComponent): String {
		return when (component) {
			SpellComponent.VERBAL ->
				"Chanting esoteric words in a normal speaking voice. A gagged creature or one in magical silence " +
					"canâ€™t provide this component."

			SpellComponent.SOMATIC ->
				"A forceful gesticulation or intricate set of gestures. The caster must use at least one hand to " +
					"perform them."

			SpellComponent.MATERIAL ->
				"Particular materials named by the spell. If they have no listed cost and are not consumed, a " +
					"component pouch or spellcasting focus can substitute for them if the caster has a feature that " +
					"allows it."
		}
	}

	fun getCastingTimeDescription(castingTime: CastingTime): String {
		return when (castingTime) {
			CastingTime.ACTION -> "Most spells require the Magic action to cast."
			CastingTime.BONUS_ACTION ->
				"Some spells are cast as a Bonus Action, often in response to a trigger defined in the spell. " +
					"Even then, you can expend only one spell slot on your turn."

			CastingTime.REACTION -> "Cast in response to a trigger defined in the spellâ€™s Casting Time entry."
			CastingTime.RITUAL ->
				"A Ritual takes 10 minutes longer than normal to cast and doesnâ€™t expend a spell slot, but the " +
					"caster must have the spell prepared."

			CastingTime.MINUTES ->
				"A longer casting time requires the Magic action on each of your turns and Concentration throughout " +
					"the casting. If Concentration is broken, the spell fails without expending its slot."

			CastingTime.HOURS ->
				"Very long castings follow the same ongoing-casting pattern as minute-long spells but over much " +
					"longer intervals."
		}
	}

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
				"Class table. Most spellcasting monsters donâ€™t change their lists of prepared spells, but the GM is " +
				"free to alter them.",
			"Always-Prepared Spells" to
				"Certain features might give you a spell that you always have prepared. If you also have a list of " +
				"prepared spells that you can change, a spell that you always have prepared doesnâ€™t count against " +
				"the number of spells on that list.",
			"Casting Spells" to
				"Each spell description has a series of entries that provide the details needed to cast the spell. The " +
				"following sections explain each of those entries, which follow a spellâ€™s name.",
			"Spell Level" to
				"Every spell has a level from 0 to 9, which is indicated in a spellâ€™s description. A spellâ€™s level is " +
				"an indicator of how powerful it is. Cantripsâ€”simple spells that can be cast almost by roteâ€”are " +
				"level 0. The rules for each spellcasting class say when its members gain access to spells of certain " +
				"levels.",
			"Spell Slots" to
				"Spellcasting is taxing, so a spellcaster can cast only a limited number of level 1+ spells before " +
				"resting. Spell slots are the main way a spellcasterâ€™s magical potential is represented. When you " +
				"cast a spell, you expend a slot of that spellâ€™s level or higher. A level 1 spell fits into a slot of " +
				"any size, but a level 2 spell fits only into a slot that is at least level 2. Finishing a Long Rest " +
				"restores any expended spell slots.",
			"Casting without Slots" to
				"There are several ways to cast a spell without expending a spell slot. Cantrips are cast without a " +
				"spell slot. Ritual spells can be cast following the normal rules or as a Ritual, which takes 10 " +
				"minutes longer but doesnâ€™t expend a spell slot; to do so, the spellcaster must have the spell " +
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
				"If a spell is on a classâ€™s spell list, the class name appears in parentheses after the spellâ€™s " +
				"school of magic.",
			"Casting Time" to
				"A spellâ€™s Casting Time entry specifies whether it requires the Magic action, a Bonus Action, a " +
				"Reaction, or a longer time such as 1 minute or more.",
			"One Spell with a Spell Slot per Turn" to
				"On a turn, you can expend only one spell slot to cast a spell. You canâ€™t cast one slotted spell " +
				"with the Magic action and another slotted spell with a Bonus Action on the same turn.",
			"Reaction and Bonus Action Triggers" to
				"Reaction spells and some Bonus Action spells are cast in response to triggers defined in the " +
				"spellâ€™s Casting Time entry.",
			"Longer Casting Times" to
				"A spell with a casting time of 1 minute or more requires the Magic action on each of your " +
				"turns and Concentration throughout the casting. If Concentration breaks, the spell fails and " +
				"no slot is expended.",
			"Range" to
				"A spellâ€™s range determines how far from the caster the spellâ€™s effect can originate. " +
				"Common forms are distance, Touch, and Self.",
			"Components" to
				"Spells can require Verbal, Somatic, and Material components. If the caster canâ€™t " +
				"provide one or more required components, the spell canâ€™t be cast.",
			"Duration" to
				"A spellâ€™s duration might be Instantaneous, require Concentration, or last for a set " +
				"span of rounds, minutes, hours, or longer. Time-span spells you cast can usually be " +
				"dismissed with no action if you arenâ€™t Incapacitated.",
			"Identifying an Ongoing Spell" to
				"You can try to identify a non-instantaneous spell by its observable effects while its " +
				"duration is ongoing. This requires the Study action and a successful DC 15 " +
				"Intelligence (Arcana) check.",
			"Targets" to
				"A spellâ€™s description explains whether it targets creatures, objects, or something " +
				"else. To target something, you usually need a clear path to it, so it canâ€™t be behind " +
				"Total Cover.",
			"Saving Throws" to
				"Spell save DC = 8 + your spellcasting ability modifier + your Proficiency Bonus. The " +
				"spell says which ability is used and what happens on a success or failure.",
			"Attack Rolls" to "Spell attack modifier = your spellcasting ability modifier + your Proficiency Bonus.",
			"Combining Spell Effects" to
				"Effects of different spells add together while their durations overlap. Effects of the " +
				"same spell cast multiple times donâ€™t combine; only the most potent or most recent " +
				"overlapping effect applies."
		)
	}

	fun getSpellcastingTables(): List<ReferenceTable> {
		return listOf(SpellcastingReferenceData.spellPreparationByClassTable)
	}

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

	fun getSpellcastingTips(): List<String> {
		return listOf(
			"A 1st-level spell fits into any slot, but a 2nd-level spell needs a 2nd-level slot or higher",
			"Finishing a long rest restores all expended spell slots",
			"You canâ€™t concentrate on two spells at once",
			"On your turn, you can expend only one spell slot to cast a spell",
			"Ritual casting adds 10 minutes and doesnâ€™t use a spell slot, but the spell must be prepared",
			"Material components with a cost must be provided (can't use focus)",
			"Consumed material components must be provided for each casting",
			"You can target yourself with a creature-of-your-choice spell unless it requires a hostile " +
				"creature or says otherwise",
			"Invalid targets still waste the spell slot if a slotted spell was cast",
			"The same spell doesnâ€™t stack with itself; use the most potent or most recent overlapping effect"
		)
	}

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
}
