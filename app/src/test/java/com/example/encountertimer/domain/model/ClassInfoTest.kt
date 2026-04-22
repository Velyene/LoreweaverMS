package com.example.encountertimer.domain.model

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
		assertEquals("Fighter", normalizeClassName("Warrior"))
		assertEquals("Wizard", normalizeClassName("Mage"))
		assertEquals("Fighter", normalizeClassName("Enemy"))
		assertEquals("Fighter", normalizeClassName("Unknown"))
		assertEquals("Rogue", normalizeClassName("rogue"))
	}

	@Test
	fun classInfoFor_returnsCanonicalAllowedClassInfoForLegacyAlias() {
		val classInfo = classInfoFor("Mage")

		assertNotNull(classInfo)
		assertEquals("Wizard", classInfo?.displayName)
	}
}

