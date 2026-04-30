package io.github.velyene.loreweaver.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import io.github.velyene.loreweaver.domain.model.ALL_CLASSES
import io.github.velyene.loreweaver.domain.model.classInfoFor
import io.github.velyene.loreweaver.domain.util.CharacterCreationReference
import io.github.velyene.loreweaver.ui.theme.LoreweaverTheme
import org.junit.Rule
import org.junit.Test

class CharacterFormScreenTest {
	@get:Rule
	val composeRule = createComposeRule()

	@Test
	fun identityStep_withSelectedClass_showsSuggestedAbilitiesSpellsAndItems() {
		val formState = CharacterFormState(
			name = "Mira",
			type = "Wizard",
			background = "Sage",
		)

		composeRule.setContent {
			LoreweaverTheme(darkTheme = true) {
				CharacterFormContent(
					screenTitle = "Create Character",
					classes = ALL_CLASSES.map { it.displayName },
					speciesOptions = CharacterCreationReference.RACES.map { it.name },
					backgroundOptions = CharacterCreationReference.BACKGROUNDS.map { it.name },
					formState = formState,
					classInfo = classInfoFor(formState.type),
					callbacks = CharacterFormCallbacks(),
					currentSection = CharacterBuilderSection.IDENTITY,
					onSectionSelected = {},
					onStepBack = {},
					onStepNext = {},
				)
			}
		}

		composeRule.onNodeWithText("Recommended Abilities").assertIsDisplayed()
		composeRule.onNodeWithText("• INT").assertIsDisplayed()
		composeRule.onNodeWithText("Suggested Spells").assertIsDisplayed()
		composeRule.onNodeWithText("• Magic Missile").assertIsDisplayed()
		composeRule.onNodeWithText("Suggested Equipment").assertIsDisplayed()
		composeRule.onNodeWithText("• Spellbook").assertIsDisplayed()
	}

	@Test
	fun statusStep_showsSeparateEncounterAndPersistentConditionSections() {
		val formState = CharacterFormState(
			encounterConditions = setOf("Blinded"),
			persistentConditions = setOf("Blessed"),
			status = "Concentrating on Fly.",
		)

		composeRule.setContent {
			LoreweaverTheme(darkTheme = true) {
				CharacterFormContent(
					screenTitle = "Edit Character",
					classes = ALL_CLASSES.map { it.displayName },
					speciesOptions = CharacterCreationReference.RACES.map { it.name },
					backgroundOptions = CharacterCreationReference.BACKGROUNDS.map { it.name },
					formState = formState,
					classInfo = classInfoFor(formState.type),
					callbacks = CharacterFormCallbacks(),
					currentSection = CharacterBuilderSection.STATUS_AND_NOTES,
					onSectionSelected = {},
					onStepBack = {},
					onStepNext = {},
				)
			}
		}

		composeRule.onNodeWithText("Encounter Conditions").assertIsDisplayed()
		composeRule.onNodeWithText("Blinded").assertIsDisplayed()
		composeRule.onNodeWithText("Persistent Conditions").assertIsDisplayed()
		composeRule.onNodeWithText("Blessed").assertIsDisplayed()
		composeRule.onNodeWithText("Encounter Status Notes").assertIsDisplayed()
		composeRule.onNodeWithText("Concentrating on Fly.").assertIsDisplayed()
	}
}

