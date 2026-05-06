package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.CharacterCreationReference
import org.junit.Test

class ReferenceScreenFeatSearchTest {

	@Test
	fun featSearch_matchesStructuredFeatFields() {
		val magicInitiate = CharacterCreationReference.FEATS.first { it.name == "Magic Initiate" }
		val grappler = CharacterCreationReference.FEATS.first { it.name == "Grappler" }
		val archery = CharacterCreationReference.FEATS.first { it.name == "Archery" }
		val truesight = CharacterCreationReference.FEATS.first { it.name == "Boon of Truesight" }

		assertMatchesAll(magicInitiate::matchesQuery, "Origin", "Two Cantrips", "Repeatable")
		assertMatchesAll(grappler::matchesQuery, "Strength or Dexterity 13+")
		assertMatchesAll(archery::matchesQuery, "Ranged weapons")
		assertMatchesAll(truesight::matchesQuery, "60 feet")
	}

	@Test
	fun filterCharacterCreationFeats_returnsExpectedMatchesForQuery() {
		assertQueryResults(listOf("Magic Initiate"), "Two Cantrips", ::filterCharacterCreationFeats) { it.name }
		assertQueryResults(listOf("Archery"), "Ranged weapons", ::filterCharacterCreationFeats) { it.name }
		assertQueryResults(listOf("Boon of Spell Recall"), "Spellcasting Feature", ::filterCharacterCreationFeats) { it.name }
		assertQueryResults(
			CharacterCreationReference.FEATS.filter { it.category == "Epic Boon" }.map { it.name },
			"Epic Boon",
			::filterCharacterCreationFeats
		) { it.name }
		assertNoQueryResults("Nonexistent Feat Term", ::filterCharacterCreationFeats)
	}

	@Test
	fun visibleCharacterCreationFeats_respectsSubsectionWhenQueryBlank() {
		assertVisibleWhenBlankInSubsection(
			CharacterCreationReference.FEATS.filter { it.category == "Origin" }.map { it.name },
			CharacterCreationSubsection.SPECIES_ORIGIN,
			::visibleCharacterCreationFeats
		) { it.name }
		assertVisibleWhenBlankInSubsection(
			CharacterCreationReference.FEATS.filter { it.category == "General" }.map { it.name },
			CharacterCreationSubsection.ABILITIES,
			::visibleCharacterCreationFeats
		) { it.name }
		assertVisibleWhenBlankInSubsection(
			CharacterCreationReference.FEATS.filter { it.category == "Fighting Style" }
				.map { it.name },
			CharacterCreationSubsection.CLASSES,
			::visibleCharacterCreationFeats
		) { it.name }
		assertVisibleWhenBlankInSubsection(
			CharacterCreationReference.FEATS.filter { it.category == "Epic Boon" }.map { it.name },
			CharacterCreationSubsection.ADVANCEMENT,
			::visibleCharacterCreationFeats
		) { it.name }
		assertHiddenWhenBlankInSubsection(CharacterCreationSubsection.FLAVOR, ::visibleCharacterCreationFeats)
	}

	@Test
	fun visibleCharacterCreationFeats_usesAllSubsectionForActiveSearch() {
		assertVisibleForSearch(
			listOf("Boon of Fate"),
			"Improve Fate",
			CharacterCreationSubsection.SPECIES_ORIGIN,
			::visibleCharacterCreationFeats
		) { it.name }
	}
}
