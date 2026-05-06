/*
 * FILE: SpellcastingReferenceData.kt
 *
 * TABLE OF CONTENTS:
 * 1. Spellcasting Reference Data
 */

package io.github.velyene.loreweaver.domain.util

internal object SpellcastingReferenceData {
	private const val WHEN_GAIN_LEVEL = "Gain a level"
	private const val WHEN_LONG_REST = "Finish a Long Rest"

	val spellPreparationByClass = listOf(
		SpellPreparationReferenceEntry("Bard", WHEN_GAIN_LEVEL, "One"),
		SpellPreparationReferenceEntry("Cleric", WHEN_LONG_REST, "Any"),
		SpellPreparationReferenceEntry("Druid", WHEN_LONG_REST, "Any"),
		SpellPreparationReferenceEntry("Paladin", WHEN_LONG_REST, "One"),
		SpellPreparationReferenceEntry("Ranger", WHEN_LONG_REST, "One"),
		SpellPreparationReferenceEntry("Sorcerer", WHEN_GAIN_LEVEL, "One"),
		SpellPreparationReferenceEntry("Warlock", WHEN_GAIN_LEVEL, "One"),
		SpellPreparationReferenceEntry("Wizard", WHEN_LONG_REST, "Any")
	)

	val spellPreparationByClassTable = ReferenceTable(
		title = "Spell Preparation by Class",
		columns = listOf("Class", "Change When You…", "Number of Spells"),
		rows = spellPreparationByClass.map { entry ->
			listOf(entry.className, entry.changeWhenYou, entry.numberOfSpells)
		}
	)

	private val fullCasterBaseSlots = listOf(
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

	private val halfCasterBaseSlots = listOf(
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

	private val warlockBaseSlots = listOf(
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

	private val spellcasterSlotTables = mapOf(
		SpellcasterType.BARD to applyCantripProgression(
			fullCasterBaseSlots,
			listOf(2, 2, 2, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4)
		),
		SpellcasterType.CLERIC to applyCantripProgression(
			fullCasterBaseSlots,
			listOf(3, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5)
		),
		SpellcasterType.DRUID to applyCantripProgression(
			fullCasterBaseSlots,
			listOf(2, 2, 2, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4)
		),
		SpellcasterType.SORCERER to applyCantripProgression(
			fullCasterBaseSlots,
			listOf(4, 4, 4, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6)
		),
		SpellcasterType.WIZARD to applyCantripProgression(
			fullCasterBaseSlots,
			listOf(3, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5)
		),
		SpellcasterType.PALADIN to halfCasterBaseSlots,
		SpellcasterType.RANGER to halfCasterBaseSlots,
		SpellcasterType.WARLOCK to applyCantripProgression(
			warlockBaseSlots,
			listOf(2, 2, 2, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4)
		)
	)

	fun getSpellSlotsForCaster(type: SpellcasterType, level: Int): SpellSlotTable? {
		return spellcasterSlotTables[type]?.find { it.characterLevel == level }
	}

	fun getSpellSlotTableForCaster(type: SpellcasterType): List<SpellSlotTable> {
		return spellcasterSlotTables[type].orEmpty()
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
