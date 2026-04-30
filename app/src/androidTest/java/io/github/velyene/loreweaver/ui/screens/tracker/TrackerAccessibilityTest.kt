package io.github.velyene.loreweaver.ui.screens.tracker

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Condition
import io.github.velyene.loreweaver.ui.screens.tracker.live.CombatantConditionsRow
import io.github.velyene.loreweaver.ui.screens.tracker.live.CombatantHeaderRow
import io.github.velyene.loreweaver.ui.screens.tracker.setup.SetupRosterSection
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TrackerAccessibilityTest {

	@get:Rule
	val composeRule = createComposeRule()

	private val context: Context
		get() = ApplicationProvider.getApplicationContext()

	private fun combatant(
		name: String = "Goblin Scout",
		initiative: Int = 14,
		currentHp: Int = 7,
		maxHp: Int = 12,
		tempHp: Int = 0,
		conditions: List<Condition> = emptyList()
	) = CombatantState(
		characterId = "combatant-1",
		name = name,
		initiative = initiative,
		currentHp = currentHp,
		maxHp = maxHp,
		conditions = conditions,
		tempHp = tempHp
	)

	@Test
	fun setupRosterSection_usesFormattedRosterContentDescription() {
		val combatant = combatant()
		val summary = context.getString(R.string.combatant_setup_summary, combatant.maxHp, combatant.initiative)
		val rowDescription = context.getString(
			R.string.combatant_setup_content_description,
			combatant.name,
			summary
		)

		composeRule.setContent {
			MaterialTheme {
				SetupRosterSection(
					combatants = listOf(combatant),
					onRemoveCombatant = {}
				)
			}
		}

		composeRule.onNodeWithContentDescription(rowDescription).assertExists()
	}

	@Test
	fun combatantHeaderRow_usesFormattedStateDescription() {
		val combatant = combatant(tempHp = 2)
		val initiativeSummary = context.getString(R.string.combatant_initiative_summary, combatant.initiative)
		val hpSummary = context.getString(
			R.string.combatant_hp_label_with_temp,
			combatant.currentHp,
			combatant.maxHp,
			combatant.tempHp
		)
		val stateDescription = context.getString(
			R.string.combatant_header_state_description,
			initiativeSummary,
			hpSummary
		)

		composeRule.setContent {
			MaterialTheme {
				CombatantHeaderRow(
					combatant = combatant,
					isActive = false,
					onHpChange = { _, _ -> }
				)
			}
		}

		composeRule.onNode(
			hasAnyDescendant(hasText(combatant.name))
				.and(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, stateDescription)),
			useUnmergedTree = true
		).assertExists()
	}

	@Test
	fun combatantConditionsRow_usesFormattedStateAndRemoveDescriptions() {
		val poisoned = Condition(name = "Poisoned", duration = 3)
		val combatant = combatant(conditions = listOf(poisoned))
		val stateDescription = context.getString(
			R.string.conditions_state_description,
			"Poisoned (3)"
		)
		val removeDescription = context.getString(
			R.string.remove_condition_desc_with_name,
			poisoned.name
		)

		composeRule.setContent {
			MaterialTheme {
				CombatantConditionsRow(
					combatant = combatant,
					onRemoveCondition = { _, _ -> },
					onAddConditionClick = {}
				)
			}
		}

		composeRule.onNode(
			SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, stateDescription),
			useUnmergedTree = true
		).assertExists()
		composeRule.onNodeWithContentDescription(removeDescription).assertExists()
		composeRule.onNodeWithContentDescription(context.getString(R.string.add_condition_desc)).assertExists()
	}
}

