package io.github.velyene.loreweaver.domain.util

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.EncounterDifficultyTarget
import io.github.velyene.loreweaver.domain.model.EncounterGenerationSettings
import io.github.velyene.loreweaver.domain.model.EncounterGenerationSourceFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class EncounterGenerationTest {

	@Test
	fun calculateEncounterPartyPowerEntries_usesPerCharacterBudgetsForMixedLevelParty() {
		val entries = calculateEncounterPartyPowerEntries(
			partyMembers = listOf(
				character("hero-1", 3),
				character("hero-2", 3),
				character("hero-3", 4),
				character("hero-4", 4)
			),
			difficultyTarget = EncounterDifficultyTarget.MODERATE
		)

		assertEquals(listOf(150, 150, 250, 250), entries.map { it.budgetXp })
		assertEquals(800, entries.sumOf { it.budgetXp })
	}

	@Test
	fun generateRandomEncounter_buildsEncounterNearPartyPowerWithAttemptsAndFinalLog() {
		val details = generateRandomEncounter(
			partyMembers = listOf(
				character("hero-1", 3),
				character("hero-2", 3),
				character("hero-3", 3),
				character("hero-4", 3)
			),
			settings = EncounterGenerationSettings(
				difficultyTarget = EncounterDifficultyTarget.MODERATE,
				maximumEnemyCr = 3.0,
				allowDuplicateEnemies = true,
				maximumEnemyQuantity = 4
			),
			random = Random(7),
			idFactory = { "enemy-${counter++}" }
		)

		assertEquals(600, details.partyPower)
		assertEquals(600, details.minimumTargetXp)
		assertEquals(630, details.maximumTargetXp)
		assertTrue(details.attempts.isNotEmpty())
		assertTrue(details.finalEnemies.isNotEmpty())
		assertTrue(details.finalTotalEnemyXp > 0)
		assertTrue(details.logLines.any { it.contains("Party power: 600 XP") })
		assertTrue(details.logLines.any { it.startsWith("Final:") })
	}

	@Test
	fun generateRandomEncounter_whenNoCandidateFits_returnsClosestMatchForReview() {
		val details = generateRandomEncounter(
			partyMembers = listOf(character("hero-1", 1)),
			settings = EncounterGenerationSettings(
				difficultyTarget = EncounterDifficultyTarget.CUSTOM,
				customTargetXp = 37,
				allowDuplicateEnemies = false,
				maximumEnemyCr = 0.125,
				maximumEnemyQuantity = 1
			),
			random = Random(3),
			idFactory = { "solo-${counter++}" }
		)

		assertTrue(details.requiresDmReview)
		assertFalse(details.finalEnemies.isEmpty())
		assertTrue(details.logLines.last().contains("Closest match"))
	}

	@Test
	fun generateRandomEncounter_customOnlySourceWithoutCustomCatalog_returnsReviewFallback() {
		val details = generateRandomEncounter(
			partyMembers = listOf(character("hero-1", 3), character("hero-2", 3)),
			settings = EncounterGenerationSettings(
				difficultyTarget = EncounterDifficultyTarget.MODERATE,
				sourceFilter = EncounterGenerationSourceFilter.CUSTOM_ONLY
			),
			random = Random(9),
			idFactory = { "custom-${counter++}" }
		)

		assertTrue(details.requiresDmReview)
		assertTrue(details.finalEnemies.isEmpty())
		assertTrue(details.logLines.any { it == "Source: Custom Only" })
		assertTrue(details.logLines.any { it.contains("No monsters matched the current filters") })
	}

	private fun character(id: String, level: Int): CharacterEntry {
		return CharacterEntry(
			id = id,
			name = id,
			party = CharacterParty.ADVENTURERS,
			level = level,
			hp = 10,
			maxHp = 10,
			ac = 12
		)
	}

	private companion object {
		var counter = 0
	}
}

