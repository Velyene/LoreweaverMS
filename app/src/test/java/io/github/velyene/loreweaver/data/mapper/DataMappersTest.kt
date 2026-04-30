package io.github.velyene.loreweaver.data.mapper

import io.github.velyene.loreweaver.data.entities.CharacterEntity
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import org.junit.Assert.assertEquals
import org.junit.Test

class DataMappersTest {

	@Test
	fun characterEntityToDomain_normalizesLegacyCustomClassNames() {
		assertEquals(
			"Fighter",
			CharacterEntity(
				name = "Brute",
				type = "Warrior",
				hp = 12,
				maxHp = 12,
				ac = 13
			).toDomain().type
		)
		assertEquals(
			"Wizard",
			CharacterEntity(
				name = "Caster",
				type = "Mage",
				hp = 8,
				maxHp = 8,
				ac = 12
			).toDomain().type
		)
		assertEquals(
			"Fighter",
			CharacterEntity(
				name = "Monster",
				type = "Enemy",
				hp = 20,
				maxHp = 20,
				ac = 11
			).toDomain().type
		)
	}

	@Test
	fun characterEntryToEntity_normalizesLegacyCustomClassNames() {
		assertEquals("Wizard", CharacterEntry(name = "Caster", type = "Mage").toEntity().type)
		assertEquals("Fighter", CharacterEntry(name = "Monster", type = "Enemy").toEntity().type)
	}

	@Test
	fun characterIdentityFields_roundTripSpeciesBackgroundAndSplitConditions() {
		val entity = CharacterEntry(
			name = "Mira",
			type = "Wizard",
			species = "Elf",
			background = "Sage",
			spells = listOf("Magic Missile", "Shield"),
			persistentConditions = setOf("Blessed"),
			activeConditions = setOf("Poisoned")
		).toEntity()

		assertEquals("Elf", entity.species)
		assertEquals("Sage", entity.background)
		assertEquals(listOf("Magic Missile", "Shield"), entity.spells)
		assertEquals(setOf("Blessed"), entity.persistentConditions)
		assertEquals(setOf("Poisoned"), entity.activeConditions)
		assertEquals("Elf", entity.toDomain().species)
		assertEquals("Sage", entity.toDomain().background)
		assertEquals(listOf("Magic Missile", "Shield"), entity.toDomain().spells)
		assertEquals(setOf("Blessed"), entity.toDomain().persistentConditions)
		assertEquals(setOf("Poisoned"), entity.toDomain().activeConditions)
	}
}
