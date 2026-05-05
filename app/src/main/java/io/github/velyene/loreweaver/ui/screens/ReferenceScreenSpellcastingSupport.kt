package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.ReferenceTable
import io.github.velyene.loreweaver.domain.util.SpellSlotTable
import io.github.velyene.loreweaver.domain.util.SpellcasterType
import io.github.velyene.loreweaver.domain.util.SpellcastingReference

internal fun filterSpellcastingRules(query: String): List<Pair<String, String>> {
	return SpellcastingReference.getSpellcastingRules()
		.entries
		.filter { matchesQuery(query, it.key, it.value) }
		.map { it.toPair() }
}

internal fun filterSpellcastingTables(query: String): List<ReferenceTable> {
	return SpellcastingReference.getSpellcastingTables().filter { it.matchesQuery(query) }
}

internal fun List<SpellSlotTable>.filterSpellSlotsByQuery(query: String): List<SpellSlotTable> {
	return filter { table ->
		query.isBlank() ||
			"level ${table.characterLevel}".contains(query, ignoreCase = true) ||
			table.characterLevel.toString() == query
	}
}

internal fun formatSignedNumber(value: Int): String = if (value >= 0) "+$value" else value.toString()

internal fun buildSpellcastingShareText(
	spellcasterType: SpellcasterType,
	abilityModifier: Int,
	proficiencyBonus: Int,
	bonus: Int,
	saveDc: Int,
	attackBonus: Int,
	slotTable: SpellSlotTable?
): String = buildString {
	appendLine("Spellcasting Reference")
	appendLine("Spellcaster: ${spellcasterType.toDisplayLabel()}")
	appendLine(SpellcastingReference.getSpellcasterDescription(spellcasterType))
	appendLine()
	appendLine("Ability Mod: ${formatSignedNumber(abilityModifier)}")
	appendLine("Proficiency Bonus: ${formatSignedNumber(proficiencyBonus)}")
	appendLine("Misc Bonus: ${formatSignedNumber(bonus)}")
	appendLine("Spell Save DC: $saveDc")
	appendLine("Spell Attack Bonus: ${formatSignedNumber(attackBonus)}")
	appendLine()
	if (slotTable != null) {
		appendLine("Spell Slots at Level ${slotTable.characterLevel}")
		appendLine("Cantrips: ${slotTable.cantrips}")
		listOf(
			slotTable.slot1,
			slotTable.slot2,
			slotTable.slot3,
			slotTable.slot4,
			slotTable.slot5,
			slotTable.slot6,
			slotTable.slot7,
			slotTable.slot8,
			slotTable.slot9
		)
			.mapIndexedNotNull { index, value ->
				if (value > 0) "${SpellcastingReference.getSpellLevelName(index + 1)} Ã—$value" else null
			}
			.forEach { appendLine(it) }
	}
}.trim()

