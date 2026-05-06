/*
 * FILE: CharacterFormCallbacks.kt
 *
 * TABLE OF CONTENTS:
 * 1. CharacterFormCallbacks callback-holder class
 * 2. Factory function — wireCharacterFormCallbacks (binds ViewModel actions to form callbacks)
 */

package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.ClassInfo
import io.github.velyene.loreweaver.domain.model.InventoryItemType
import io.github.velyene.loreweaver.domain.model.classInfoFor
import io.github.velyene.loreweaver.ui.viewmodels.CharacterViewModel

internal class CharacterFormCallbacks {
	var onFormStateChange: (CharacterFormState) -> Unit = {}
	var onClassSelected: (String) -> Unit = {}
	var onRandomizeAttributes: () -> Unit = {}
	var onRandomName: () -> Unit = {}
	var onQuickBuild: () -> Unit = {}
	var onRecalcHp: () -> Unit = {}
	var onRecalcMana: () -> Unit = {}
	var onRecalcStamina: () -> Unit = {}
	var onToggleSkill: (String) -> Unit = {}
	var onResourceNameChange: (Int, String) -> Unit = { _, _ -> }
	var onResourceMaxChange: (Int, String) -> Unit = { _, _ -> }
	var onRemoveResource: (Int) -> Unit = {}
	var onAddResource: () -> Unit = {}
	var onInventoryChange: (String) -> Unit = {}
	var onHitDieChange: (String) -> Unit = {}
	var onActionNameChange: (Int, String) -> Unit = { _, _ -> }
	var onActionAttackBonusChange: (Int, String) -> Unit = { _, _ -> }
	var onActionDamageChange: (Int, String) -> Unit = { _, _ -> }
	var onActionManaCostChange: (Int, String) -> Unit = { _, _ -> }
	var onActionStaminaCostChange: (Int, String) -> Unit = { _, _ -> }
	var onActionSpellSlotLevelChange: (Int, String) -> Unit = { _, _ -> }
	var onActionResourceNameChange: (Int, String) -> Unit = { _, _ -> }
	var onActionResourceCostChange: (Int, String) -> Unit = { _, _ -> }
	var onActionItemNameChange: (Int, String) -> Unit = { _, _ -> }
	var onRemoveAction: (Int) -> Unit = {}
	var onAddAction: () -> Unit = {}
	var onSaveCharacter: () -> Unit = {}
}

internal fun createCharacterFormCallbacks(
	formState: CharacterFormState,
	onFormStateChange: (CharacterFormState) -> Unit,
	characterId: String?,
	characters: List<CharacterEntry>,
	classInfo: ClassInfo?,
	viewModel: CharacterViewModel,
	newResourceName: String,
	newActionName: String,
	onSave: () -> Unit
): CharacterFormCallbacks {
	fun updateFormState(updatedState: CharacterFormState) {
		onFormStateChange(updatedState)
	}

	return CharacterFormCallbacks().apply {
		this.onFormStateChange = onFormStateChange
		onClassSelected = { className ->
			updateFormState(formState.withSelectedClass(className))
		}
		onRandomizeAttributes = {
			val randomizedState = formState.randomizedAttributes()
			val randomizedClassInfo = classInfoFor(randomizedState.type)
			updateFormState(
				randomizedState
					.recalculatedHp(randomizedClassInfo)
					.recalculatedMana(randomizedClassInfo)
					.recalculatedStamina()
			)
		}
		onRandomName = {
			updateFormState(formState.copy(name = generateRandomName(), nameError = false))
		}
		onQuickBuild = {
			updateFormState(formState.quickBuild(classInfo))
		}
		onRecalcHp = {
			updateFormState(formState.recalculatedHp(classInfo))
		}
		onRecalcMana = {
			updateFormState(formState.recalculatedMana(classInfo))
		}
		onRecalcStamina = {
			updateFormState(formState.recalculatedStamina())
		}
		onToggleSkill = { skill ->
			updateFormState(formState.withToggledSkill(skill))
		}
		onResourceNameChange = { index, resourceName ->
			updateFormState(formState.withResourceName(index, resourceName))
		}
		onResourceMaxChange = { index, rawMax ->
			updateFormState(formState.withResourceMax(index, rawMax))
		}
		onRemoveResource = { index ->
			updateFormState(formState.withoutResource(index))
		}
		onAddResource = {
			updateFormState(formState.withNewResource(newResourceName))
		}
		onInventoryChange = { inventoryText ->
			updateFormState(formState.copy(inventoryText = inventoryText))
		}
		onHitDieChange = { hitDie ->
			if (hitDie.isDigitsOnlyInput()) {
				updateFormState(formState.copy(hitDieType = hitDie))
			}
		}
		onActionNameChange = { index, actionName ->
			updateFormState(formState.withActionName(index, actionName))
		}
		onActionAttackBonusChange = { index, rawAttackBonus ->
			updateFormState(formState.withActionAttackBonus(index, rawAttackBonus))
		}
		onActionDamageChange = { index, damageDice ->
			updateFormState(formState.withActionDamage(index, damageDice))
		}
		onActionManaCostChange = { index, rawManaCost ->
			updateFormState(formState.withActionManaCost(index, rawManaCost))
		}
		onActionStaminaCostChange = { index, rawStaminaCost ->
			updateFormState(formState.withActionStaminaCost(index, rawStaminaCost))
		}
		onActionSpellSlotLevelChange = { index, rawSpellSlotLevel ->
			updateFormState(formState.withActionSpellSlotLevel(index, rawSpellSlotLevel))
		}
		onActionResourceNameChange = { index, resourceName ->
			updateFormState(formState.withActionResourceName(index, resourceName))
		}
		onActionResourceCostChange = { index, rawResourceCost ->
			updateFormState(formState.withActionResourceCost(index, rawResourceCost))
		}
		onActionItemNameChange = { index, itemName ->
			updateFormState(formState.withActionItemName(index, itemName))
		}
		onRemoveAction = { index ->
			updateFormState(formState.withoutAction(index))
		}
		onAddAction = {
			updateFormState(formState.withNewAction(newActionName))
		}
		onSaveCharacter = saveCharacter@{
			val validation = validateCharacterForm(name = formState.name, hp = formState.hp)
			val validatedState = formState.withValidation(validation)
			updateFormState(validatedState)
			if (!validation.isValid) return@saveCharacter

			val hpInt = validatedState.hp.toIntOrNull() ?: return@saveCharacter
			val existingCharacter = characters.find { it.id == characterId }
			val newCharacter = buildCharacterEntry(
				existingCharacter = existingCharacter,
				formState = validatedState,
				classInfo = classInfo,
				hp = hpInt
			)

			if (existingCharacter != null) {
					viewModel.updateCharacter(newCharacter, onSuccess = onSave)
			} else {
					viewModel.addCharacter(newCharacter, onSuccess = onSave)
			}
		}
	}
}

internal data class CharacterFormValidation(
	val nameError: Boolean,
	val hpError: Boolean
) {
	val isValid: Boolean
		get() = !nameError && !hpError
}

internal fun validateCharacterForm(name: String, hp: String): CharacterFormValidation {
	return CharacterFormValidation(
		nameError = name.isBlank(),
		hpError = hp.toIntOrNull() == null
	)
}

internal fun validateCharacterBuilderSection(
	section: CharacterBuilderSection,
	formState: CharacterFormState
): CharacterFormValidation {
	return when (section) {
		CharacterBuilderSection.IDENTITY -> CharacterFormValidation(
			nameError = formState.name.isBlank(),
			hpError = formState.hpError
		)

		CharacterBuilderSection.CORE_STATS -> CharacterFormValidation(
			nameError = formState.nameError,
			hpError = formState.hp.toIntOrNull() == null
		)

		else -> validateCharacterForm(name = formState.name, hp = formState.hp)
	}
}

internal fun buildCharacterEntry(
	existingCharacter: CharacterEntry?,
	formState: CharacterFormState,
	classInfo: ClassInfo?,
	hp: Int
): CharacterEntry {
	val parsedMana = formState.mana.toIntOrNull() ?: CharacterFormConstants.DEFAULT_MANA.toInt()
	val parsedMaxMana =
		formState.maxMana.toIntOrNull() ?: CharacterFormConstants.DEFAULT_MANA.toInt()
	val parsedStamina = formState.stamina.toIntOrNull() ?: 0
	val parsedMaxStamina = formState.maxStamina.toIntOrNull() ?: 0
	val parsedAc = formState.ac.toIntOrNull() ?: CharacterFormConstants.DEFAULT_AC.toInt()
	val parsedSpeed = formState.speed.toIntOrNull() ?: CharacterFormConstants.DEFAULT_SPEED.toInt()
	val parsedInitiative =
		formState.initiative.toIntOrNull() ?: CharacterFormConstants.DEFAULT_INITIATIVE.toInt()
	val parsedLevel = formState.level.toIntOrNull() ?: CharacterFormConstants.DEFAULT_LEVEL.toInt()
	val parsedChallengeRating = formState.challengeRating.toDoubleOrNull()
		?: CharacterFormConstants.DEFAULT_CHALLENGE_RATING.toDouble()
	val parsedStrength = parseAttributeScore(formState.str)
	val parsedDexterity = parseAttributeScore(formState.dex)
	val parsedConstitution = parseAttributeScore(formState.con)
	val parsedIntelligence = parseAttributeScore(formState.intell)
	val parsedWisdom = parseAttributeScore(formState.wis)
	val parsedCharisma = parseAttributeScore(formState.cha)
	val parsedHitDieType =
		formState.hitDieType.toIntOrNull() ?: CharacterFormConstants.DEFAULT_HIT_DIE.toInt()
	val personalInventory = parseInventoryLines(formState.inventoryText)
	val equippedItems = parseInventoryLines(formState.equippedItemsText, InventoryItemType.EQUIPMENT)

	return CharacterEntry(
		id = existingCharacter?.id ?: java.util.UUID.randomUUID().toString(),
		name = formState.name,
		type = formState.type,
		party = formState.party,
		hp = hp,
		maxHp = formState.maxHp.toIntOrNull() ?: hp,
		mana = parsedMana,
		maxMana = parsedMaxMana,
		stamina = parsedStamina,
		maxStamina = parsedMaxStamina,
		ac = parsedAc,
		speed = parsedSpeed,
		initiative = parsedInitiative,
		level = parsedLevel,
		challengeRating = parsedChallengeRating,
		strength = parsedStrength,
		dexterity = parsedDexterity,
		constitution = parsedConstitution,
		intelligence = parsedIntelligence,
		wisdom = parsedWisdom,
		charisma = parsedCharisma,
		proficiencies = formState.selectedProficiencies,
		saveProficiencies = formState.selectedSaveProficiencies,
		inventory = personalInventory.map { it.name },
		inventoryState = (existingCharacter?.inventoryState ?: io.github.velyene.loreweaver.domain.model.CharacterInventoryState()).copy(
			personalInventory = personalInventory,
			equippedItems = equippedItems,
			currencyCp = formState.walletCp.toIntOrNull() ?: 0,
			carryingNotes = formState.carryingNotes
		),
		resources = formState.resources,
		hitDieType = parsedHitDieType,
		hitDiceCurrent = existingCharacter?.hitDiceCurrent ?: parsedLevel,
		deathSaveSuccesses = existingCharacter?.deathSaveSuccesses ?: 0,
		deathSaveFailures = existingCharacter?.deathSaveFailures ?: 0,
		persistentConditions = formState.persistentConditions,
		activeConditions = formState.encounterConditions,
		spellSlots = existingCharacter?.spellSlots
			?: classInfo?.defaultSpellSlotsL1?.mapValues { (_, max) -> max to max }
			?: emptyMap(),
		actions = formState.actions,
		status = formState.status,
		notes = formState.notes,
		species = formState.species,
		background = formState.background,
		spells = formState.spellsText.lineSequence().map(String::trim).filter { it.isNotBlank() }.toList()
	)
}

private fun parseAttributeScore(rawValue: String): Int {
	return rawValue.toIntOrNull()
		?.coerceIn(CharacterFormConstants.MIN_ATTRIBUTE, CharacterFormConstants.MAX_ATTRIBUTE)
		?: CharacterFormConstants.DEFAULT_ATTRIBUTE.toInt()
}
