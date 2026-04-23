package com.example.loreweaver.ui.screens

import com.example.loreweaver.domain.model.ClassInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterFormCallbacksTest {

	@Test
	fun validateCharacterForm_withBlankNameAndInvalidHp_setsBothErrors() {
		val validation = validateCharacterForm(name = "   ", hp = "abc")

		assertTrue(validation.nameError)
		assertTrue(validation.hpError)
		assertFalse(validation.isValid)
	}

	@Test
	fun buildCharacterEntry_withInvalidInputs_usesExpectedFallbacks() {
		val classInfo = ClassInfo(
			displayName = "Wizard",
			hitDie = 6,
			primaryStats = listOf("INT", "CON"),
			defaultSaveProficiencies = setOf("INT", "WIS"),
			defaultSpellSlotsL1 = mapOf(1 to 2)
		)
		val formState = CharacterFormState(
			name = "Meris",
			maxHp = "invalid",
			mana = "invalid",
			maxMana = "invalid",
			stamina = "invalid",
			maxStamina = "invalid",
			ac = "invalid",
			speed = "invalid",
			initiative = "invalid",
			level = "invalid",
			challengeRating = "invalid",
			str = "99",
			dex = "0",
			con = "invalid",
			intell = "-1",
			wis = "31",
			cha = "invalid",
			inventoryText = "Rope\n\nTorch",
			hitDieType = "invalid"
		)

		val builtCharacter = buildCharacterEntry(
			existingCharacter = null,
			formState = formState,
			classInfo = classInfo,
			hp = 12
		)

		assertEquals(12, builtCharacter.hp)
		assertEquals(12, builtCharacter.maxHp)
		assertEquals(0, builtCharacter.mana)
		assertEquals(0, builtCharacter.maxMana)
		assertEquals(0, builtCharacter.stamina)
		assertEquals(0, builtCharacter.maxStamina)
		assertEquals(10, builtCharacter.ac)
		assertEquals(30, builtCharacter.speed)
		assertEquals(0, builtCharacter.initiative)
		assertEquals(1, builtCharacter.level)
		assertEquals(0.0, builtCharacter.challengeRating, 0.0)
		assertEquals(30, builtCharacter.strength)
		assertEquals(1, builtCharacter.dexterity)
		assertEquals(10, builtCharacter.constitution)
		assertEquals(1, builtCharacter.intelligence)
		assertEquals(30, builtCharacter.wisdom)
		assertEquals(10, builtCharacter.charisma)
		assertEquals(listOf("Rope", "Torch"), builtCharacter.inventory)
		assertEquals(8, builtCharacter.hitDieType)
		assertEquals(1, builtCharacter.hitDiceCurrent)
		assertEquals(mapOf(1 to (2 to 2)), builtCharacter.spellSlots)
	}

	@Test
	fun buildCharacterEntry_withExistingCharacter_preservesRuntimeFields() {
		val existingCharacter = com.example.loreweaver.domain.model.CharacterEntry(
			id = "character-1",
			hitDiceCurrent = 3,
			deathSaveSuccesses = 2,
			deathSaveFailures = 1,
			activeConditions = setOf("Poisoned"),
			spellSlots = mapOf(1 to (1 to 2))
		)
		val formState = CharacterFormState(
			name = "Updated Hero",
			level = "5",
			hitDieType = "10"
		)
		val classInfo = ClassInfo(
			displayName = "Paladin",
			hitDie = 10,
			primaryStats = listOf("STR", "CHA"),
			defaultSaveProficiencies = setOf("WIS", "CHA"),
			defaultSpellSlotsL1 = mapOf(1 to 2)
		)

		val builtCharacter = buildCharacterEntry(
			existingCharacter = existingCharacter,
			formState = formState,
			classInfo = classInfo,
			hp = 20
		)

		assertEquals("character-1", builtCharacter.id)
		assertEquals(3, builtCharacter.hitDiceCurrent)
		assertEquals(2, builtCharacter.deathSaveSuccesses)
		assertEquals(1, builtCharacter.deathSaveFailures)
		assertEquals(setOf("Poisoned"), builtCharacter.activeConditions)
		assertEquals(mapOf(1 to (1 to 2)), builtCharacter.spellSlots)
	}
}
