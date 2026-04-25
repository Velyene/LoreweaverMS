package io.github.velyene.loreweaver.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ClassInfoTest {

	@Test
	fun allClasses_matchAllowedSrdBaseClassesExactly() {
		assertEquals(
			listOf(
				"Barbarian",
				"Bard",
				"Cleric",
				"Druid",
				"Fighter",
				"Monk",
				"Paladin",
				"Ranger",
				"Rogue",
				"Sorcerer",
				"Warlock",
				"Wizard"
			),
			ALL_CLASSES.map { it.displayName }
		)
	}

	@Test
	fun normalizeClassName_mapsLegacyCustomNamesIntoAllowedClasses() {
		assertEquals(CLASS_FIGHTER, normalizeClassName("Warrior"))
		assertEquals(CLASS_WIZARD, normalizeClassName("Mage"))
		assertEquals(CLASS_FIGHTER, normalizeClassName("Enemy"))
		assertEquals(CLASS_FIGHTER, normalizeClassName("Unknown"))
		assertEquals("Rogue", normalizeClassName("rogue"))
	}

	@Test
	fun classInfoFor_returnsCanonicalAllowedClassInfoForLegacyAlias() {
		val classInfo = classInfoFor("Mage")

		assertNotNull(classInfo)
		assertEquals(CLASS_WIZARD, classInfo?.displayName)
	}
}
