package com.example.loreweaver.ui.screens

import com.example.loreweaver.domain.util.CharacterCreationReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceScreenFeatSearchTest {

	@Test
	fun featSearch_matchesStructuredFeatFields() {
		val magicInitiate = CharacterCreationReference.FEATS.first { it.name == "Magic Initiate" }
		val grappler = CharacterCreationReference.FEATS.first { it.name == "Grappler" }
		val archery = CharacterCreationReference.FEATS.first { it.name == "Archery" }
		val truesight = CharacterCreationReference.FEATS.first { it.name == "Boon of Truesight" }

		assertTrue(magicInitiate.matchesSearchQuery("Origin"))
		assertTrue(magicInitiate.matchesSearchQuery("Two Cantrips"))
		assertTrue(grappler.matchesSearchQuery("Strength or Dexterity 13+"))
		assertTrue(archery.matchesSearchQuery("Ranged weapons"))
		assertTrue(truesight.matchesSearchQuery("60 feet"))
		assertTrue(magicInitiate.matchesSearchQuery("Repeatable"))
	}

	@Test
	fun filterCharacterCreationFeats_returnsExpectedMatchesForQuery() {
		assertEquals(
			listOf("Magic Initiate"),
			filterCharacterCreationFeats("Two Cantrips").map { it.name })
		assertEquals(
			listOf("Archery"),
			filterCharacterCreationFeats("Ranged weapons").map { it.name })
		assertEquals(
			listOf("Boon of Spell Recall"),
			filterCharacterCreationFeats("Spellcasting Feature").map { it.name })
		assertEquals(
			CharacterCreationReference.FEATS.filter { it.category == "Epic Boon" }.map { it.name },
			filterCharacterCreationFeats("Epic Boon").map { it.name }
		)
		assertTrue(filterCharacterCreationFeats("Nonexistent Feat Term").isEmpty())
	}

	@Test
	fun visibleCharacterCreationFeats_respectsSubsectionWhenQueryBlank() {
		assertEquals(
			CharacterCreationReference.FEATS.filter { it.category == "Origin" }.map { it.name },
			visibleCharacterCreationFeats(
				"",
				CharacterCreationSubsection.SPECIES_ORIGIN
			).map { it.name }
		)
		assertEquals(
			CharacterCreationReference.FEATS.filter { it.category == "General" }.map { it.name },
			visibleCharacterCreationFeats("", CharacterCreationSubsection.ABILITIES).map { it.name }
		)
		assertEquals(
			CharacterCreationReference.FEATS.filter { it.category == "Fighting Style" }
				.map { it.name },
			visibleCharacterCreationFeats("", CharacterCreationSubsection.CLASSES).map { it.name }
		)
		assertEquals(
			CharacterCreationReference.FEATS.filter { it.category == "Epic Boon" }.map { it.name },
			visibleCharacterCreationFeats(
				"",
				CharacterCreationSubsection.ADVANCEMENT
			).map { it.name }
		)
		assertTrue(visibleCharacterCreationFeats("", CharacterCreationSubsection.FLAVOR).isEmpty())
	}

	@Test
	fun visibleCharacterCreationFeats_usesAllSubsectionForActiveSearch() {
		assertEquals(
			listOf("Boon of Fate"),
			visibleCharacterCreationFeats(
				"Improve Fate",
				CharacterCreationSubsection.SPECIES_ORIGIN
			).map { it.name }
		)
	}
}
