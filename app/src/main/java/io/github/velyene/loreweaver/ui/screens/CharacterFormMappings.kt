/*
 * FILE: CharacterFormMappings.kt
 *
 * TABLE OF CONTENTS:
 * 1. CharacterEntry → CharacterFormState mapping (toFormState)
 * 2. CharacterFormState → CharacterEntry mapping (toCharacterEntry)
 * 3. Derived-field helpers for HP, mana, resources, spell slots, and actions
 */

package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.model.ATTRIBUTE_CHARISMA
import io.github.velyene.loreweaver.domain.model.ATTRIBUTE_CONSTITUTION
import io.github.velyene.loreweaver.domain.model.ATTRIBUTE_DEXTERITY
import io.github.velyene.loreweaver.domain.model.ATTRIBUTE_INTELLIGENCE
import io.github.velyene.loreweaver.domain.model.ATTRIBUTE_STRENGTH
import io.github.velyene.loreweaver.domain.model.ATTRIBUTE_WISDOM
import io.github.velyene.loreweaver.domain.model.CharacterAction
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CharacterResource
import io.github.velyene.loreweaver.domain.model.ClassInfo
import io.github.velyene.loreweaver.domain.model.calcMaxHp
import io.github.velyene.loreweaver.domain.model.calcMaxMana
import io.github.velyene.loreweaver.domain.model.classInfoFor
import io.github.velyene.loreweaver.domain.model.normalizeClassName
import io.github.velyene.loreweaver.domain.model.roll4d6DropLowest
import io.github.velyene.loreweaver.domain.util.CharacterParty

internal fun CharacterEntry.toFormState(): CharacterFormState {
	return CharacterFormState()
		.copy(
			name = name,
			type = if (party == CharacterParty.ADVENTURERS) normalizeClassName(type) else type.ifBlank { "Monster" },
			party = party,
			level = level.toString(),
			challengeRating = challengeRating.toString()
		)
		.copy(
			str = strength.toString(),
			dex = dexterity.toString(),
			con = constitution.toString(),
			intell = intelligence.toString(),
			wis = wisdom.toString(),
			cha = charisma.toString()
		)
		.copy(
			hp = hp.toString(),
			maxHp = maxHp.toString(),
			mana = mana.toString(),
			maxMana = maxMana.toString(),
			stamina = stamina.toString(),
			maxStamina = maxStamina.toString()
		)
		.copy(
			ac = ac.toString(),
			initiative = initiative.toString(),
			speed = speed.toString(),
			hitDieType = hitDieType.toString()
		)
		.copy(
			selectedProficiencies = proficiencies,
			selectedSaveProficiencies = saveProficiencies,
			inventoryText = inventory.joinToString("\n"),
			resources = resources,
			actions = actions,
			status = status,
			notes = notes
		)
}

internal fun CharacterFormState.recalculatedHp(classInfo: ClassInfo?): CharacterFormState {
	val hitDie = classInfo?.hitDie ?: (hitDieType.toIntOrNull() ?: 8)
	val calculated = calcMaxHp(
		hitDie,
		level.toIntOrNull() ?: 1,
		con.toIntOrNull() ?: 10
	)
	return copy(maxHp = calculated.toString(), hp = calculated.toString())
}

internal fun CharacterFormState.recalculatedMana(classInfo: ClassInfo?): CharacterFormState {
	val calculated = calcMaxMana(classInfo, level.toIntOrNull() ?: 1)
	return copy(maxMana = calculated.toString(), mana = calculated.toString())
}

internal fun CharacterFormState.recalculatedStamina(): CharacterFormState {
	return copy(maxStamina = con, stamina = con)
}

internal fun CharacterFormState.randomizedAttributes(): CharacterFormState {
	return copy(
		str = roll4d6DropLowest().toString(),
		dex = roll4d6DropLowest().toString(),
		con = roll4d6DropLowest().toString(),
		intell = roll4d6DropLowest().toString(),
		wis = roll4d6DropLowest().toString(),
		cha = roll4d6DropLowest().toString()
	)
}

private const val QUICK_BUILD_PACK = "Explorer's Pack"
private const val QUICK_BUILD_CURRENCY = "10 gp"
private const val QUICK_BUILD_STANDARD_PREFIX = "Standard"
private const val QUICK_BUILD_EQUIPMENT_SUFFIX = "Equipment"
private const val QUICK_BUILD_AC_DEX_PRIMARY = "14"
private const val QUICK_BUILD_AC_STR_PRIMARY = "16"
private const val QUICK_BUILD_AC_DEFAULT = "12"

private val QUICK_BUILD_DEFAULT_PRIMARY_STATS =
	listOf(ATTRIBUTE_STRENGTH, ATTRIBUTE_CONSTITUTION)

private val GENERATED_FIRST_NAMES = listOf(
	"Aelar", "Bram", "Caeldrim", "Dorn", "Eldon", "Fargrim", "Gael", "Hesk", "Ilyana",
	"Jandar", "Kaelen", "Lira", "Morn", "Naeris", "Orin", "Paelias", "Quinn", "Rurik",
	"Sylas", "Tharivol", "Umber", "Varis", "Wrenn", "Xander", "Yis", "Zook"
)

private val GENERATED_LAST_NAMES = listOf(
	"Amakiir", "Battlehammer", "Casilltenirra", "Dergosh", "Erenaeth", "Frostbeard",
	"Galanodel", "Holimion", "Ilphelkiir", "Lathalas", "Meliamne", "Nailo", "Ousstyl",
	"Xiloscient", "Siannodel", "Ironfist", "Stoneheart", "Swiftwind", "Shadowcloak"
)

internal fun generateRandomName(): String {
	return buildGeneratedName(
		firstName = GENERATED_FIRST_NAMES.random(),
		lastName = GENERATED_LAST_NAMES.random()
	)
}

internal fun CharacterFormState.quickBuild(classInfo: ClassInfo?): CharacterFormState {
	val standardArray = mutableListOf(15, 14, 13, 12, 10, 8)
	val primary = classInfo?.primaryStats ?: QUICK_BUILD_DEFAULT_PRIMARY_STATS
	val stats =
		mutableMapOf(
			ATTRIBUTE_STRENGTH to 10,
			ATTRIBUTE_DEXTERITY to 10,
			ATTRIBUTE_CONSTITUTION to 10,
			ATTRIBUTE_INTELLIGENCE to 10,
			ATTRIBUTE_WISDOM to 10,
			ATTRIBUTE_CHARISMA to 10
		)

	// Assign 15 and 14 to primary stats
	if (primary.isNotEmpty() && stats.containsKey(primary[0])) {
		stats[primary[0]] = standardArray.removeAt(0)
	}
	if (primary.size > 1 && stats.containsKey(primary[1])) {
		stats[primary[1]] = standardArray.removeAt(0)
	}

	// Assign the rest randomly
	standardArray.shuffle()
	stats.keys.forEach { key ->
		if (!primary.contains(key)) {
			stats[key] = standardArray.removeAt(0)
		}
	}

	val calculatedAc = determineQuickBuildArmorClass(primary)
	val calculatedInventory = buildQuickBuildInventoryText(classInfo)

	return copy(
		name = generateRandomName(),
		level = CharacterFormConstants.DEFAULT_LEVEL,
		str = stats[ATTRIBUTE_STRENGTH].toString(),
		dex = stats[ATTRIBUTE_DEXTERITY].toString(),
		con = stats[ATTRIBUTE_CONSTITUTION].toString(),
		intell = stats[ATTRIBUTE_INTELLIGENCE].toString(),
		wis = stats[ATTRIBUTE_WISDOM].toString(),
		cha = stats[ATTRIBUTE_CHARISMA].toString(),
		inventoryText = calculatedInventory,
		ac = calculatedAc,
		selectedSaveProficiencies = classInfo?.defaultSaveProficiencies ?: emptySet()
	).recalculatedHp(classInfo).recalculatedMana(classInfo).recalculatedStamina()
}

private fun buildGeneratedName(firstName: String, lastName: String): String {
	return "$firstName $lastName"
}

private fun determineQuickBuildArmorClass(primaryStats: List<String>): String {
	return when {
		primaryStats.contains(ATTRIBUTE_DEXTERITY) -> QUICK_BUILD_AC_DEX_PRIMARY
		primaryStats.contains(ATTRIBUTE_STRENGTH) -> QUICK_BUILD_AC_STR_PRIMARY
		else -> QUICK_BUILD_AC_DEFAULT
	}
}

private fun buildQuickBuildInventoryText(classInfo: ClassInfo?): String {
	val equipmentLine = listOfNotNull(
		QUICK_BUILD_STANDARD_PREFIX,
		classInfo?.displayName,
		QUICK_BUILD_EQUIPMENT_SUFFIX
	).joinToString(" ")

	return listOf(
		QUICK_BUILD_PACK,
		equipmentLine,
		QUICK_BUILD_CURRENCY
	).joinToString("\n")
}

internal fun CharacterFormState.withSelectedClass(className: String): CharacterFormState {
	val classInfo = classInfoFor(className)
	return if (classInfo != null) {
		copy(
			type = classInfo.displayName,
			hitDieType = classInfo.hitDie.toString(),
			selectedSaveProficiencies = classInfo.defaultSaveProficiencies
		)
	} else {
		copy(type = className)
	}
}

internal fun CharacterFormState.withValidation(validation: CharacterFormValidation): CharacterFormState {
	return copy(nameError = validation.nameError, hpError = validation.hpError)
}

internal fun CharacterFormState.withAttribute(
	attributeKey: String,
	value: String
): CharacterFormState {
	return when (attributeKey) {
		ATTRIBUTE_STRENGTH -> copy(str = value)
		ATTRIBUTE_DEXTERITY -> copy(dex = value)
		ATTRIBUTE_CONSTITUTION -> copy(con = value)
		ATTRIBUTE_INTELLIGENCE -> copy(intell = value)
		ATTRIBUTE_WISDOM -> copy(wis = value)
		ATTRIBUTE_CHARISMA -> copy(cha = value)
		else -> this
	}
}

internal fun CharacterFormState.withToggledSkill(skill: String): CharacterFormState {
	return copy(selectedProficiencies = toggleSelection(selectedProficiencies, skill))
}

internal fun CharacterFormState.withResourceName(
	index: Int,
	resourceName: String
): CharacterFormState {
	return copy(resources = resources.updateResourceAt(index) { copy(name = resourceName) })
}

internal fun CharacterFormState.withResourceMax(index: Int, rawMax: String): CharacterFormState {
	val maxValue = rawMax.toIntOrNull() ?: 0
	return copy(
		resources = resources.updateResourceAt(index) {
			copy(max = maxValue, current = current.coerceAtMost(maxValue))
		}
	)
}

internal fun CharacterFormState.withoutResource(index: Int): CharacterFormState {
	return copy(resources = resources.removeItem(index))
}

internal fun CharacterFormState.withNewResource(resourceName: String): CharacterFormState {
	return copy(resources = resources + CharacterResource(resourceName, 0, 0))
}

internal fun CharacterFormState.withActionName(index: Int, actionName: String): CharacterFormState {
	return copy(actions = actions.updateActionAt(index) { copy(name = actionName) })
}

internal fun CharacterFormState.withActionAttackBonus(
	index: Int,
	rawAttackBonus: String
): CharacterFormState {
	val attackBonus = rawAttackBonus.toIntOrNull() ?: 0
	return copy(
		actions = actions.updateActionAt(index) { copy(attackBonus = attackBonus) }
	)
}

internal fun CharacterFormState.withActionDamage(
	index: Int,
	damageDice: String
): CharacterFormState {
	return copy(actions = actions.updateActionAt(index) { copy(damageDice = damageDice) })
}

internal fun CharacterFormState.withoutAction(index: Int): CharacterFormState {
	return copy(actions = actions.removeItem(index))
}

internal fun CharacterFormState.withNewAction(actionName: String): CharacterFormState {
	return copy(actions = actions + CharacterAction(actionName))
}

internal fun String.toAttributeValueOrNull(): String? {
	return takeIf { it.isDigitsOnlyInput() }?.let(::clampAttribute)
}

internal fun String.isDigitsOnlyInput(): Boolean = all(Char::isDigit)

internal fun String.isSignedIntegerInput(): Boolean {
	return isEmpty() || this == "-" || trimStart('-').all(Char::isDigit)
}

internal fun <T> toggleSelection(values: Set<T>, value: T): Set<T> {
	return if (values.contains(value)) values - value else values + value
}

private fun clampAttribute(input: String): String {
	return (input.toIntOrNull() ?: CharacterFormConstants.DEFAULT_ATTRIBUTE.toInt())
		.coerceIn(CharacterFormConstants.MIN_ATTRIBUTE, CharacterFormConstants.MAX_ATTRIBUTE)
		.toString()
}

private fun <T> List<T>.removeItem(index: Int): List<T> =
	toMutableList().also { it.removeAt(index) }

private fun List<CharacterResource>.updateResourceAt(
	index: Int,
	transform: CharacterResource.() -> CharacterResource
): List<CharacterResource> {
	return toMutableList().also { items ->
		items[index] = items[index].transform()
	}
}

private fun List<CharacterAction>.updateActionAt(
	index: Int,
	transform: CharacterAction.() -> CharacterAction
): List<CharacterAction> {
	return toMutableList().also { items ->
		items[index] = items[index].transform()
	}
}
