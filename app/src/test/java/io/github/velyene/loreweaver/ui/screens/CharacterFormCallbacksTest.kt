package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.model.ClassInfo
import io.github.velyene.loreweaver.domain.model.CharacterAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterFormCallbacksTest {
	private companion object {
		const val INVALID_INPUT = "invalid"
	}

	@Test
	fun validateCharacterForm_withBlankNameAndInvalidHp_setsBothErrors() {
		val validation = validateCharacterForm(name = "   ", hp = "abc")

		assertTrue(validation.nameError)
		assertTrue(validation.hpError)
		assertFalse(validation.isValid)
	}

	@Test
	fun validateCharacterBuilderSection_identityOnlyRequiresName() {
		val validation = validateCharacterBuilderSection(
			section = CharacterBuilderSection.IDENTITY,
			formState = CharacterFormState(name = "", hp = "")
		)

		assertTrue(validation.nameError)
		assertFalse(validation.hpError)
	}

	@Test
	fun validateCharacterBuilderSection_coreStatsRequiresNumericHp() {
		val validation = validateCharacterBuilderSection(
			section = CharacterBuilderSection.CORE_STATS,
			formState = CharacterFormState(name = "Aria", hp = "abc")
		)

		assertFalse(validation.nameError)
		assertTrue(validation.hpError)
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
			maxHp = INVALID_INPUT,
			mana = INVALID_INPUT,
			maxMana = INVALID_INPUT,
			stamina = INVALID_INPUT,
			maxStamina = INVALID_INPUT,
			ac = INVALID_INPUT,
			speed = INVALID_INPUT,
			initiative = INVALID_INPUT,
			level = INVALID_INPUT,
			challengeRating = INVALID_INPUT,
			str = "99",
			dex = "0",
			con = INVALID_INPUT,
			intell = "-1",
			wis = "31",
			cha = INVALID_INPUT,
			inventoryText = "Rope\n\nTorch",
			hitDieType = INVALID_INPUT
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
		assertEquals("", builtCharacter.species)
		assertEquals("", builtCharacter.background)
	}

	@Test
	fun buildCharacterEntry_withExistingCharacter_preservesRuntimeFields() {
		val existingCharacter = io.github.velyene.loreweaver.domain.model.CharacterEntry(
			id = "character-1",
			hitDiceCurrent = 3,
			deathSaveSuccesses = 2,
			deathSaveFailures = 1,
			activeConditions = setOf("Poisoned"),
			persistentConditions = setOf("Blessed"),
			spellSlots = mapOf(1 to (1 to 2))
		)
		val formState = CharacterFormState(
			name = "Updated Hero",
			level = "5",
			hitDieType = "10",
			encounterConditions = setOf("Poisoned"),
			persistentConditions = setOf("Blessed"),
			species = "Elf",
			background = "Sage",
			spellsText = "Bless\nShield of Faith"
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
		assertEquals(setOf("Blessed"), builtCharacter.persistentConditions)
		assertEquals(mapOf(1 to (1 to 2)), builtCharacter.spellSlots)
		assertEquals("Elf", builtCharacter.species)
		assertEquals("Sage", builtCharacter.background)
		assertEquals(listOf("Bless", "Shield of Faith"), builtCharacter.spells)
	}

	@Test
	fun buildCharacterEntry_preservesCustomActionCostsFromFormState() {
		val formState = CharacterFormState(
			name = "Sister Vale",
			actions = listOf(
				CharacterAction(
					name = "Radiant Burst",
					attackBonus = 5,
					damageDice = "2d8",
					isAttack = true,
					manaCost = 3,
					staminaCost = 1,
					spellSlotLevel = 2,
					resourceName = "Channel Divinity",
					resourceCost = 1,
					itemName = "Holy Symbol"
				)
			)
		)

		val builtCharacter = buildCharacterEntry(
			existingCharacter = null,
			formState = formState,
			classInfo = null,
			hp = 18
		)

		with(builtCharacter.actions.single()) {
			assertEquals("Radiant Burst", name)
			assertEquals(3, manaCost)
			assertEquals(1, staminaCost)
			assertEquals(2, spellSlotLevel)
			assertEquals("Channel Divinity", resourceName)
			assertEquals(1, resourceCost)
			assertEquals("Holy Symbol", itemName)
		}
	}
}
