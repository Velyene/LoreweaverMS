package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.model.ATTRIBUTE_CONSTITUTION
import io.github.velyene.loreweaver.domain.model.ATTRIBUTE_DEXTERITY
import io.github.velyene.loreweaver.domain.model.ATTRIBUTE_INTELLIGENCE
import io.github.velyene.loreweaver.domain.model.ATTRIBUTE_STRENGTH
import io.github.velyene.loreweaver.domain.model.ATTRIBUTE_WISDOM
import io.github.velyene.loreweaver.domain.model.CLASS_FIGHTER
import io.github.velyene.loreweaver.domain.model.CLASS_WIZARD
import io.github.velyene.loreweaver.domain.model.CharacterAction
import io.github.velyene.loreweaver.domain.model.CharacterResource
import io.github.velyene.loreweaver.domain.model.ClassInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterFormMappingsTest {

	@Test
	fun withNewResource_usesProvidedName() {
		val updatedState = CharacterFormState().withNewResource("Spell Slots")

		assertEquals(1, updatedState.resources.size)
		assertEquals("Spell Slots", updatedState.resources.single().name)
		assertEquals(0, updatedState.resources.single().current)
		assertEquals(0, updatedState.resources.single().max)
	}

	@Test
	fun withNewAction_usesProvidedName() {
		val updatedState = CharacterFormState().withNewAction("Longsword")

		assertEquals(1, updatedState.actions.size)
		assertEquals("Longsword", updatedState.actions.single().name)
	}

	@Test
	fun resourceAndActionEditHelpers_applyIncomingValues() {
		val initialState = CharacterFormState(
			resources = listOf(CharacterResource(name = "Ki", current = 3, max = 5)),
			actions = listOf(CharacterAction(name = "Strike", attackBonus = 2, damageDice = "1d6"))
		)

		val updatedState = initialState
			.withResourceName(index = 0, resourceName = "Focus")
			.withResourceMax(index = 0, rawMax = "4")
			.withActionName(index = 0, actionName = "Arcane Bolt")
			.withActionAttackBonus(index = 0, rawAttackBonus = "7")
			.withActionDamage(index = 0, damageDice = "2d8+3")

		assertEquals("Focus", updatedState.resources.single().name)
		assertEquals(4, updatedState.resources.single().max)
		assertEquals(3, updatedState.resources.single().current)
		assertEquals("Arcane Bolt", updatedState.actions.single().name)
		assertEquals(7, updatedState.actions.single().attackBonus)
		assertEquals("2d8+3", updatedState.actions.single().damageDice)
	}

	@Test
	fun withResourceMax_clampsCurrentToNewMaximum() {
		val initialState = CharacterFormState(
			resources = listOf(CharacterResource(name = "Rage", current = 5, max = 5))
		)

		val updatedState = initialState.withResourceMax(index = 0, rawMax = "2")

		assertEquals(2, updatedState.resources.single().max)
		assertEquals(2, updatedState.resources.single().current)
	}

	@Test
	fun withAttribute_updatesOnlyRequestedAbility() {
		val updatedState = CharacterFormState().withAttribute(ATTRIBUTE_DEXTERITY, "14")

		assertEquals(CharacterFormConstants.DEFAULT_ATTRIBUTE, updatedState.str)
		assertEquals("14", updatedState.dex)
		assertEquals(CharacterFormConstants.DEFAULT_ATTRIBUTE, updatedState.con)
	}

	@Test
	fun quickBuild_withoutClassInfo_usesCleanGenericInventoryText() {
		val updatedState = CharacterFormState().quickBuild(classInfo = null)

		assertEquals(
			"Explorer's Pack\nStandard Equipment\n10 gp",
			updatedState.inventoryText
		)
		assertEquals("15", updatedState.str)
		assertEquals("14", updatedState.con)
		assertEquals("16", updatedState.ac)
	}

	@Test
	fun quickBuild_withClassInfo_includesClassNameInInventoryText() {
		val classInfo = ClassInfo(
			displayName = CLASS_WIZARD,
			hitDie = 6,
			primaryStats = listOf(ATTRIBUTE_INTELLIGENCE, ATTRIBUTE_CONSTITUTION),
			defaultSaveProficiencies = setOf(ATTRIBUTE_INTELLIGENCE, ATTRIBUTE_WISDOM),
			defaultSpellSlotsL1 = mapOf(1 to 2)
		)

		val updatedState = CharacterFormState().quickBuild(classInfo)

		assertEquals(
			"Explorer's Pack\nStandard Wizard Equipment\n10 gp",
			updatedState.inventoryText
		)
		assertEquals("12", updatedState.ac)
	}

	@Test
	fun quickBuild_withDexPrimary_usesDexArmorPreset() {
		val classInfo = ClassInfo(
			displayName = "Rogue",
			hitDie = 8,
			primaryStats = listOf(ATTRIBUTE_DEXTERITY, ATTRIBUTE_INTELLIGENCE),
			defaultSaveProficiencies = setOf(ATTRIBUTE_DEXTERITY, ATTRIBUTE_INTELLIGENCE),
			defaultSpellSlotsL1 = emptyMap()
		)

		val updatedState = CharacterFormState().quickBuild(classInfo)

		assertEquals("14", updatedState.ac)
	}

	@Test
	fun quickBuild_withDexAndStrPrimary_prefersDexArmorPreset() {
		val classInfo = ClassInfo(
			displayName = CLASS_FIGHTER,
			hitDie = 10,
			primaryStats = listOf(ATTRIBUTE_DEXTERITY, ATTRIBUTE_STRENGTH),
			defaultSaveProficiencies = setOf(ATTRIBUTE_STRENGTH, ATTRIBUTE_CONSTITUTION),
			defaultSpellSlotsL1 = emptyMap()
		)

		val updatedState = CharacterFormState().quickBuild(classInfo)

		assertEquals("14", updatedState.ac)
	}

	@Test
	fun generateRandomName_returnsTwoNonBlankNameParts() {
		val generatedName = generateRandomName()

		val nameParts = generatedName.split(" ")
		assertEquals(2, nameParts.size)
		assertTrue(nameParts.all { it.isNotBlank() })
	}
}
