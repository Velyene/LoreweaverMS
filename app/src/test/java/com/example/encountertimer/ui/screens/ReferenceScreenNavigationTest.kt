package com.example.encountertimer.ui.screens

import com.example.encountertimer.domain.util.CharacterCreationReference
import com.example.encountertimer.domain.util.CoreRulesReference
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceScreenNavigationTest {

	@Test
	fun characterCreationSubsections_routeSpeciesOriginContent() {
		assertTrue(
			CharacterCreationSubsection.SPECIES_ORIGIN.matches(
				CharacterCreationReference.RACE_CHAPTER_SECTIONS.first()
			)
		)
		assertTrue(CharacterCreationSubsection.SPECIES_ORIGIN.matches(CharacterCreationReference.STEPS[1]))
		assertTrue(CharacterCreationSubsection.SPECIES_ORIGIN.matches())
		assertTrue(
			CharacterCreationSubsection.SPECIES_ORIGIN.matches(
				CharacterCreationReference.FEATS.first { it.name == "Alert" }
			)
		)
		assertTrue(CharacterCreationSubsection.SPECIES_ORIGIN.showsBackgrounds())
		assertTrue(CharacterCreationSubsection.SPECIES_ORIGIN.showsFeats())
		assertTrue(CharacterCreationSubsection.SPECIES_ORIGIN.showsLanguageNotes())
		assertTrue(
			CharacterCreationSubsection.SPECIES_ORIGIN.matches(
				CharacterCreationReference.CHARACTER_CREATION_TABLES.first { it.title == "Standard Languages" }
			)
		)
		assertFalse(CharacterCreationSubsection.SPECIES_ORIGIN.showsPlayableClasses())
		assertFalse(CharacterCreationSubsection.ABILITIES.matches())
		assertFalse(CharacterCreationSubsection.ABILITIES.showsLanguageNotes())
		assertFalse(
			CharacterCreationSubsection.ADVANCEMENT.matches(
				CharacterCreationReference.FEATS.first { it.name == "Alert" }
			)
		)
	}

	@Test
	fun characterCreationSubsections_routeClassAndAdvancementContent() {
		assertTrue(CharacterCreationSubsection.CLASSES.matches(CharacterCreationReference.STEPS.first()))
		assertTrue(
			CharacterCreationSubsection.CLASSES.matches(
				CharacterCreationReference.FEATS.first { it.name == "Archery" }
			)
		)
		assertTrue(
			CharacterCreationSubsection.ABILITIES.matches(
				CharacterCreationReference.FEATS.first { it.name == "Grappler" }
			)
		)
		assertTrue(
			CharacterCreationSubsection.ADVANCEMENT.matches(
				CharacterCreationReference.CHARACTER_CREATION_SECTIONS.first { it.title == "Multiclassing" }
			)
		)
		assertTrue(
			CharacterCreationSubsection.ADVANCEMENT.matches(
				CharacterCreationReference.FEATS.first { it.name == "Boon of Fate" }
			)
		)
		assertTrue(
			CharacterCreationSubsection.ADVANCEMENT.matches(
				CharacterCreationReference.CHARACTER_CREATION_TABLES.first { it.title == "Character Advancement" }
			)
		)
	}

	@Test
	fun coreRulesSubtabs_routeSectionsAndTables() {
		assertTrue(
			CoreRulesSubtab.FUNDAMENTALS.matches(
				CoreRulesReference.SECTIONS.first { it.title == "D20 Tests" }
			)
		)
		assertTrue(
			CoreRulesSubtab.ADVENTURING.matches(
				CoreRulesReference.SECTIONS.first { it.title == "Travel and Marching Order" }
			)
		)
		assertTrue(
			CoreRulesSubtab.COMBAT.matches(
				CoreRulesReference.SECTIONS.first { it.title == "Damage, Healing, and Dying" }
			)
		)
		assertTrue(
			CoreRulesSubtab.COMBAT.matches(
				CoreRulesReference.SECTIONS.first { it.title == "Monster Stat Blocks" }
			)
		)
		assertTrue(CoreRulesSubtab.QUICK_TABLES.matches())
		assertFalse(CoreRulesSubtab.QUICK_TABLES.matches(CoreRulesReference.SECTIONS.first()))
		assertTrue(CoreRulesSubtab.GLOSSARY.matchesGlossary())
		assertTrue(CoreRulesSubtab.GLOSSARY.matchesGlossaryTable())
		assertTrue(CoreRulesSubtab.GLOSSARY.showsGlossary())
		assertFalse(CoreRulesSubtab.COMBAT.showsGlossary())
	}
}

