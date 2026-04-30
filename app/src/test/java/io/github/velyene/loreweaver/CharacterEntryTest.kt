/*
 * FILE: CharacterEntryTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Character construction and modifier calculations
 * 2. Damage and healing behavior
 * 3. Proficiency progression and rest recovery
 */

package io.github.velyene.loreweaver

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class CharacterEntryTest {

	@Test
	fun characterEntry_initialization() {
		val character = CharacterEntry(name = "Test Hero", hp = 10, maxHp = 10)
		assertEquals("Test Hero", character.name)
		assertEquals(10, character.hp)
		assertEquals(10, character.maxHp)
		assertNotNull(character.id)
	}

	@Test
	fun getModifier_calculatesCorrectly() {
		val character = CharacterEntry()
		assertEquals(-1, character.getModifier(8))
		assertEquals(-1, character.getModifier(9))
		assertEquals(0, character.getModifier(10))
		assertEquals(0, character.getModifier(11))
		assertEquals(1, character.getModifier(12))
		assertEquals(2, character.getModifier(14))
		assertEquals(3, character.getModifier(16))
		assertEquals(4, character.getModifier(18))
		assertEquals(5, character.getModifier(20))
	}

	@Test
	fun applyDamage_reducesHp() {
		val character = CharacterEntry(hp = 20, maxHp = 20)
		val damaged = character.applyDamage(5)
		assertEquals(15, damaged.hp)
	}

	@Test
	fun applyDamage_usesTempHpFirst() {
		val character = CharacterEntry(hp = 20, maxHp = 20, tempHp = 5)
		val damaged = character.applyDamage(7)
		assertEquals(0, damaged.tempHp)
		assertEquals(18, damaged.hp)
	}

	@Test
	fun applyHealing_increasesHpUpToMax() {
		val character = CharacterEntry(hp = 10, maxHp = 20)
		val healed = character.applyHealing(5)
		assertEquals(15, healed.hp)

		val overHealed = character.applyHealing(20)
		assertEquals(20, overHealed.hp)
	}

	@Test
	fun getProficiencyBonus_scalesWithLevel() {
		val char1 = CharacterEntry(level = 1)
		assertEquals(2, char1.getProficiencyBonus())

		val char5 = CharacterEntry(level = 5)
		assertEquals(3, char5.getProficiencyBonus())

		val char9 = CharacterEntry(level = 9)
		assertEquals(4, char9.getProficiencyBonus())

		val char13 = CharacterEntry(level = 13)
		assertEquals(5, char13.getProficiencyBonus())

		val char17 = CharacterEntry(level = 17)
		assertEquals(6, char17.getProficiencyBonus())
	}

	@Test
	fun longRest_restoresResources() {
		val character = CharacterEntry(
			level = 10,
			hp = 10,
			maxHp = 100,
			mana = 0,
			maxMana = 50,
			persistentConditions = setOf("Blessed"),
			activeConditions = setOf("Poisoned"),
			deathSaveFailures = 2,
			hitDiceCurrent = 2
		)

		val rested = character.longRest()

		assertEquals(100, rested.hp)
		assertEquals(50, rested.mana)
		assertEquals(0, rested.deathSaveFailures)
		assertEquals(setOf("Blessed"), rested.persistentConditions)
		assertEquals(emptySet<String>(), rested.activeConditions)
		// Recovers half of max (10/2 = 5). 2 + 5 = 7.
		assertEquals(7, rested.hitDiceCurrent)
	}

	@Test
	fun effectiveSpeed_usesCombinedEncounterAndPersistentConditions() {
		val character = CharacterEntry(
			speed = 30,
			persistentConditions = setOf("Restrained"),
			activeConditions = setOf("Prone")
		)

		assertEquals(0, character.effectiveSpeed)
	}

	@Test
	fun shortRest_restoresWarlockSpellSlots() {
		val warlock = CharacterEntry(
			type = "Warlock",
			spellSlots = mapOf(
				1 to (0 to 2),  // Level 1: 0/2 slots
				2 to (1 to 2)   // Level 2: 1/2 slots
			)
		)

		val rested = warlock.shortRest()

		// Warlock should recover all spell slots
		assertEquals(2, rested.spellSlots[1]?.first)
		assertEquals(2, rested.spellSlots[2]?.first)
	}

	@Test
	fun shortRest_doesNotRestoreNonWarlockSpellSlots() {
		val wizard = CharacterEntry(
			type = "Wizard",
			spellSlots = mapOf(
				1 to (0 to 4),  // Level 1: 0/4 slots
				3 to (2 to 3)   // Level 3: 2/3 slots
			)
		)

		val rested = wizard.shortRest()

		// Non-Warlock spell slots should NOT restore on short rest
		assertEquals(0, rested.spellSlots[1]?.first)
		assertEquals(2, rested.spellSlots[3]?.first)
	}

	@Test
	fun shortRest_restoresKiPoints() {
		val monk = CharacterEntry(
			type = "Monk",
			resources = listOf(
				io.github.velyene.loreweaver.domain.model.CharacterResource(
					"Ki Points",
					current = 0,
					max = 10
				),
				io.github.velyene.loreweaver.domain.model.CharacterResource(
					"Other Resource",
					current = 0,
					max = 5
				)
			)
		)

		val rested = monk.shortRest()

		assertEquals(10, rested.resources.find { it.name == "Ki Points" }?.current)
		// Non-short-rest resource should not restore
		assertEquals(0, rested.resources.find { it.name == "Other Resource" }?.current)
	}

	@Test
	fun shortRest_restoresFighterResources() {
		val fighter = CharacterEntry(
			type = "Fighter",
			resources = listOf(
				io.github.velyene.loreweaver.domain.model.CharacterResource(
					"Action Surge",
					current = 0,
					max = 1
				),
				io.github.velyene.loreweaver.domain.model.CharacterResource(
					"Second Wind",
					current = 0,
					max = 1
				)
			)
		)

		val rested = fighter.shortRest()

		assertEquals(1, rested.resources.find { it.name == "Action Surge" }?.current)
		assertEquals(1, rested.resources.find { it.name == "Second Wind" }?.current)
	}

	@Test
	fun shortRest_restoresBardicInspiration() {
		val bard = CharacterEntry(
			type = "Bard",
			resources = listOf(
				io.github.velyene.loreweaver.domain.model.CharacterResource(
					"Bardic Inspiration",
					current = 0,
					max = 5
				)
			)
		)

		val rested = bard.shortRest()

		assertEquals(5, rested.resources.find { it.name == "Bardic Inspiration" }?.current)
	}
}
