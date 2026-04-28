package io.github.velyene.loreweaver.data.mapper

import io.github.velyene.loreweaver.data.entities.CharacterEntity
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.util.CharacterParty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
	fun characterEntryToEntity_preservesMonsterTypeAndChallengeRating_forNonAdventurers() {
		val entity = CharacterEntry(
			name = "Ancient White Dragon",
			type = "Dragon",
			party = CharacterParty.MONSTERS,
			hp = 333,
			maxHp = 333,
			ac = 20,
			challengeRating = 20.0
		).toEntity()

		assertEquals("Dragon", entity.type)
		assertEquals(20.0, entity.challengeRating, 0.0)
		assertFalse(entity.isPlayerCharacter)
		assertEquals("Dragon", entity.toDomain().type)
		assertEquals(20.0, entity.toDomain().challengeRating, 0.0)
	}
}
