package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.model.CharacterAction
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CharacterResource

internal enum class ActionHubSection {
	ACTIONS,
	SPELLS,
	ITEMS,
}

internal data class ActionHubEntry(
	val id: String,
	val title: String,
	val summary: String,
	val description: String,
	val typeLabel: String,
	val costLabel: String,
	val useNotes: String,
	val actionIndex: Int? = null,
	val spellLevel: Int? = null,
	val itemIndex: Int? = null,
)

internal fun buildActionHubEntries(
	character: CharacterEntry,
	section: ActionHubSection,
): List<ActionHubEntry> {
	return when (section) {
		ActionHubSection.ACTIONS -> character.actions.mapIndexed(::toActionEntry) +
			character.resources.mapIndexed(::toResourceEntry)

		ActionHubSection.SPELLS -> character.spellSlots.toSortedMap().map { (level, slots) ->
			toSpellEntry(level, slots.first, slots.second)
		} + character.spells.mapIndexed(::toPreparedSpellEntry)

		ActionHubSection.ITEMS -> character.inventory.mapIndexed(::toItemEntry)
	}
}

private fun toActionEntry(index: Int, action: CharacterAction): ActionHubEntry {
	val attackSummary = buildList {
		if (action.isAttack) add("Atk ${signedValue(action.attackBonus)}")
		if (action.damageDice.isNotBlank()) add(action.damageDice)
	}.joinToString(" • ")

	return ActionHubEntry(
		id = "action-$index",
		title = action.name.ifBlank { "Unnamed Action" },
		summary = attackSummary.ifBlank { fallbackActionSummary(action) },
		description = buildActionDescription(action),
		typeLabel = if (action.isAttack) "Attack" else "Action",
		costLabel = "1 action",
		useNotes = action.notes.ifBlank {
			if (action.isAttack) {
				"Use the attack and damage buttons for quick turn resolution."
			} else {
				"Resolve this ability manually based on table context."
			}
		},
		actionIndex = index,
	)
}

private fun fallbackActionSummary(action: CharacterAction): String {
	return if (action.notes.isNotBlank()) action.notes else "Ready to use on this turn."
}

private fun buildActionDescription(action: CharacterAction): String {
	return buildList {
		if (action.isAttack) add("Attack bonus ${signedValue(action.attackBonus)}")
		if (action.damageDice.isNotBlank()) add("Damage ${action.damageDice}")
		if (action.notes.isNotBlank()) add(action.notes)
	}.joinToString(". ").ifBlank { "No additional action details recorded." }
}

private fun toResourceEntry(index: Int, resource: CharacterResource): ActionHubEntry {
	return ActionHubEntry(
		id = "resource-$index",
		title = resource.name.ifBlank { "Resource" },
		summary = "${resource.current} / ${resource.max} available",
		description = "Tracks ${resource.name.ifBlank { "resource" }} usage for this character.",
		typeLabel = "Resource",
		costLabel = "Varies",
		useNotes = "Adjust this tracker from the builder or related mechanics as needed.",
	)
}

private fun toSpellEntry(level: Int, current: Int, max: Int): ActionHubEntry {
	return ActionHubEntry(
		id = "spell-$level",
		title = "Level $level Slot",
		summary = "$current / $max slots ready",
		description = "Tracks remaining level $level spell slots for this character.",
		typeLabel = "Spell Slot",
		costLabel = "1 level $level slot",
		useNotes = "Spend or recover slots with the controls on this row.",
		spellLevel = level,
	)
}

private fun toPreparedSpellEntry(index: Int, spellName: String): ActionHubEntry {
	val trimmedName = spellName.trim().ifBlank { "Prepared Spell" }
	return ActionHubEntry(
		id = "prepared-spell-$index",
		title = trimmedName,
		summary = "Prepared / known spell",
		description = "Saved spell entry for $trimmedName.",
		typeLabel = "Spell",
		costLabel = "Varies by slot",
		useNotes = "Cast using an appropriate available spell slot or class feature at the table.",
	)
}

private fun toItemEntry(index: Int, itemName: String): ActionHubEntry {
	val trimmedName = itemName.trim().ifBlank { "Inventory Item" }
	return ActionHubEntry(
		id = "item-$index",
		title = trimmedName,
		summary = inferItemSummary(trimmedName),
		description = "Stored inventory entry for $trimmedName.",
		typeLabel = inferItemType(trimmedName),
		costLabel = inferItemCost(trimmedName),
		useNotes = inferItemUseNotes(trimmedName),
		itemIndex = index,
	)
}

private fun inferItemSummary(itemName: String): String {
	return when {
		itemName.contains("Potion", ignoreCase = true) -> "Consumable support item"
		itemName.contains("Scroll", ignoreCase = true) -> "Single-use magic support"
		itemName.contains("Pack", ignoreCase = true) -> "Adventure gear bundle"
		else -> "Inventory option"
	}
}

private fun inferItemType(itemName: String): String {
	return when {
		itemName.contains("Potion", ignoreCase = true) -> "Consumable"
		itemName.contains("Scroll", ignoreCase = true) -> "Magic Item"
		itemName.contains("Sword", ignoreCase = true) || itemName.contains("Bow", ignoreCase = true) || itemName.contains("Dagger", ignoreCase = true) -> "Weapon"
		itemName.contains("Armor", ignoreCase = true) || itemName.contains("Shield", ignoreCase = true) -> "Armor"
		else -> "Item"
	}
}

private fun inferItemCost(itemName: String): String {
	return when {
		itemName.contains("Potion", ignoreCase = true) -> "1 use"
		itemName.contains("Scroll", ignoreCase = true) -> "1 cast"
		else -> "Manual"
	}
}

private fun inferItemUseNotes(itemName: String): String {
	return when {
		itemName.contains("Potion", ignoreCase = true) -> "Mark off the item after use and apply its healing or effect manually."
		itemName.contains("Scroll", ignoreCase = true) -> "Spend the scroll when cast and resolve the spell from your table reference."
		else -> "Use according to table context and campaign rulings."
	}
}

private fun signedValue(value: Int): String {
	return if (value >= 0) "+$value" else value.toString()
}

