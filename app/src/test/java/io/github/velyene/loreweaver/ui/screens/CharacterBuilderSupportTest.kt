package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.model.ATTRIBUTE_CHARISMA
import io.github.velyene.loreweaver.domain.model.ATTRIBUTE_CONSTITUTION
import io.github.velyene.loreweaver.domain.model.ClassInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterBuilderSupportTest {

	@Test
	fun buildCharacterSuggestions_returnsClassSpellsAndEquipmentForSpellcaster() {
		val suggestions = buildCharacterSuggestions(
			classInfo = ClassInfo(
				displayName = "Wizard",
				hitDie = 6,
				primaryStats = listOf("INT", ATTRIBUTE_CONSTITUTION),
				defaultSaveProficiencies = setOf("INT", "WIS"),
				defaultSpellSlotsL1 = mapOf(1 to 2),
			),
			backgroundName = "Sage",
		)

		assertEquals(listOf("INT", ATTRIBUTE_CONSTITUTION), suggestions.recommendedAbilities)
		assertTrue(suggestions.suggestedSpells.contains("Magic Missile"))
		assertTrue(suggestions.suggestedEquipment.contains("Spellbook"))
		assertTrue(suggestions.backgroundEquipment.isNotEmpty())
	}

	@Test
	fun buildCharacterSuggestions_returnsFallbackEquipmentForUnknownClass() {
		val suggestions = buildCharacterSuggestions(
			classInfo = ClassInfo(
				displayName = "Warlord",
				hitDie = 10,
				primaryStats = listOf("STR", ATTRIBUTE_CHARISMA),
				defaultSaveProficiencies = emptySet(),
				defaultSpellSlotsL1 = emptyMap(),
			),
			backgroundName = "",
		)

		assertEquals(
			listOf("Explorer's Pack", "Potion of Healing", "Backup weapon"),
			suggestions.suggestedEquipment,
		)
	}
}

