package io.github.velyene.loreweaver.ui.screens

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.util.CharacterParty
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CharacterListAccessibilityTest {

	@get:Rule
	val composeRule = createComposeRule()

	private val context: Context
		get() = ApplicationProvider.getApplicationContext()

	private fun character(
		name: String = "Goblin Scout",
		type: String = "Skirmisher",
		hp: Int = 7,
		maxHp: Int = 12,
		ac: Int = 15,
		initiative: Int = 14,
		party: String = CharacterParty.MONSTERS
	) = CharacterEntry(
		id = "character-1",
		name = name,
		type = type,
		hp = hp,
		maxHp = maxHp,
		ac = ac,
		initiative = initiative,
		party = party
	)

	@Test
	fun characterListTopBar_searchFieldUsesResourceLabel() {
		composeRule.setContent {
			MaterialTheme {
				CharacterListTopBar(
					searchState = SearchState(
						isActive = true,
						query = "",
						onQueryChange = {},
						onToggle = {},
						selectedPartyFilter = null,
						onPartyFilterChange = {}
					),
					combatState = CombatState(
						showInitiativeOrder = false,
						onToggle = {},
						sortByInitiative = true,
						onToggleSort = {},
						currentTurnIndex = 0,
						onNextTurn = {},
						roundCount = 1,
						charactersSize = 0
					),
					onBack = {}
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.search_characters_label)).assertExists()
	}

	@Test
	fun characterItem_usesFormattedContentDescription() {
		val character = character(name = "Aria", type = "Wizard", hp = 12, maxHp = 20, ac = 15, party = CharacterParty.ADVENTURERS)
		val contentDescription = context.getString(
			R.string.character_accessibility_desc,
			character.name,
			character.type,
			character.hp,
			character.maxHp,
			character.ac,
			""
		)

		composeRule.setContent {
			MaterialTheme {
				CharacterItem(
					character = character,
					onClick = {},
					onUpdateHP = {},
					onDelete = {}
				)
			}
		}

		composeRule.onNodeWithContentDescription(contentDescription).assertExists()
	}

	@Test
	fun initiativeItem_usesCurrentTurnStateDescription() {
		val character = character()
		val currentTurnStatus = context.getString(R.string.current_turn)

		composeRule.setContent {
			MaterialTheme {
				InitiativeItem(
					character = character,
					onClick = {},
					onUpdateHP = {},
					isActiveTurn = true
				)
			}
		}

		composeRule.onNode(
			hasAnyDescendant(hasText(character.name))
				.and(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, currentTurnStatus)),
			useUnmergedTree = true
		).assertExists()
	}
}

