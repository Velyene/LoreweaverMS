package io.github.velyene.loreweaver.domain.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameplayToolboxSrdAuditTest {
	private companion object {
		const val ASSASSINS_BLOOD = "Assassin's Blood"
		const val ROLLING_STONE = "Rolling Stone"
	}

	private val excerptPoisonNames = setOf(
		ASSASSINS_BLOOD,
		"Burnt Othur Fumes",
		"Crawler Mucus",
		"Essence of Ether",
		"Malice",
		"Midnight Tears",
		"Oil of Taggit",
		"Pale Tincture",
		"Purple Worm Poison",
		"Serpent Venom",
		"Spider's Sting",
		"Torpor",
		"Truth Serum",
		"Wyvern Poison"
	)

	private val excerptTrapNames = setOf(
		"Collapsing Roof",
		"Falling Net",
		"Fire-Casting Statue",
		"Hidden Pit",
		"Poisoned Darts",
		"Poisoned Needle",
		ROLLING_STONE,
		"Spiked Pit"
	)

	private val excerptContagionNames = setOf(
		"Cackle Fever",
		"Sewer Plague",
		"Sight Rot"
	)

	@Test
	fun poisonTemplates_matchCanonicalProvidedExcerpt() {
		val currentPoisonNames = PoisonReference.TEMPLATES.map { it.name }.toSet()

		assertEquals(excerptPoisonNames, currentPoisonNames)
	}

	@Test
	fun trapTemplates_matchCanonicalProvidedExcerpt() {
		val currentTrapNames = TrapReference.TEMPLATES.map { it.name }.toSet()

		assertEquals(excerptTrapNames, currentTrapNames)
	}

	@Test
	fun poisonTemplates_matchCanonicalMechanicalBenchmarks() {
		val assassinBlood = PoisonReference.getTemplateByName(ASSASSINS_BLOOD)!!
		assertEquals(10, assassinBlood.saveDC)
		assertEquals("1d12", assassinBlood.damageOnFail)
		assertEquals("150 gp", assassinBlood.pricePerDose)

		val midnightTears = PoisonReference.getTemplateByName("Midnight Tears")!!
		assertEquals(17, midnightTears.saveDC)
		assertEquals("9d6", midnightTears.damageOnFail)
		assertEquals("Until midnight", midnightTears.duration)

		val purpleWorm = PoisonReference.getTemplateByName("Purple Worm Poison")!!
		assertEquals(21, purpleWorm.saveDC)
		assertEquals("10d6", purpleWorm.damageOnFail)
	}

	@Test
	fun trapTemplates_matchCanonicalMechanicalBenchmarks() {
		val collapsingRoof = TrapReference.getTemplateByName("Collapsing Roof")!!
		assertEquals(11, collapsingRoof.detectionDC)
		assertEquals(13, collapsingRoof.saveDC)
		assertEquals("2d10", collapsingRoof.damage)

		val fireCastingStatue = TrapReference.getTemplateByName("Fire-Casting Statue")!!
		assertEquals(10, fireCastingStatue.detectionDC)
		assertEquals(15, fireCastingStatue.disarmDC)
		assertEquals("2d10", fireCastingStatue.damage)

		val rollingStone = TrapReference.getTemplateByName(ROLLING_STONE)!!
		assertEquals(15, rollingStone.saveDC)
		assertEquals("10d10", rollingStone.damage)
	}

	@Test
	fun searchHelpers_matchCanonicalContentOnly() {
		val assassinBlood = PoisonReference.getTemplateByName(ASSASSINS_BLOOD)!!
		val rollingStone = TrapReference.getTemplateByName(ROLLING_STONE)!!

		assertTrue(PoisonReference.matchesSearchQuery(assassinBlood, ASSASSINS_BLOOD))
		assertTrue(PoisonReference.matchesSearchQuery(assassinBlood, "150 gp"))
		assertTrue(!PoisonReference.matchesSearchQuery(assassinBlood, "silent draught"))

		assertTrue(TrapReference.matchesSearchQuery(rollingStone, ROLLING_STONE))
		assertTrue(TrapReference.matchesSearchQuery(rollingStone, "strength (athletics)"))
		assertTrue(!TrapReference.matchesSearchQuery(rollingStone, "rolling sphere"))
	}

	@Test
	fun diseaseTemplates_matchProvidedContagionNames() {
		assertEquals(
			excerptContagionNames,
			DiseaseReference.TEMPLATES.map { it.name }.toSet()
		)
	}
}
