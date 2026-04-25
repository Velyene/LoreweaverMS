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
}
