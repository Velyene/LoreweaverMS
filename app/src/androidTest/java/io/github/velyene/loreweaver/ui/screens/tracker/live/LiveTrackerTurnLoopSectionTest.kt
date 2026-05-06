/*
 * FILE: LiveTrackerTurnLoopSectionTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Class: LiveTrackerTurnLoopSectionTest
 * 2. Value: composeRule
 * 3. Value: context
 * 4. Function: applyResult_requiresAmountBeforeDamageOrHealButtonsEnable
 * 5. Function: applyResult_quickAmountEnablesDamageAndHealButtons
 * 6. Function: applyResult_damageButtonInvokesCallbackWithoutAdvancingTurn
 * 7. Value: recordedType
 * 8. Value: recordedAmount
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.ui.theme.LoreweaverTheme
import io.github.velyene.loreweaver.ui.viewmodels.ActionResolutionType
import io.github.velyene.loreweaver.ui.viewmodels.CombatTurnStep
import io.github.velyene.loreweaver.ui.viewmodels.PendingTurnAction
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LiveTrackerTurnLoopSectionTest {

	@get:Rule
	val composeRule = createComposeRule()

	private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

	@Test
	fun applyResult_requiresAmountBeforeDamageOrHealButtonsEnable() {
		setApplyResultContent()

		composeRule.onNodeWithText(context.getString(R.string.encounter_result_miss_button)).assertIsEnabled()
		composeRule.onNodeWithText(context.getString(R.string.encounter_result_miss_end_button)).assertIsEnabled()
		composeRule.onNodeWithText(context.getString(R.string.encounter_result_damage_button)).assertIsNotEnabled()
		composeRule.onNodeWithText(context.getString(R.string.encounter_result_heal_button)).assertIsNotEnabled()
		composeRule.onNodeWithText(context.getString(R.string.encounter_result_damage_end_button)).assertIsNotEnabled()
		composeRule.onNodeWithText(context.getString(R.string.encounter_result_heal_end_button)).assertIsNotEnabled()
	}

	@Test
	fun applyResult_quickAmountEnablesDamageAndHealButtons() {
		setApplyResultContent()

		composeRule.onNodeWithText("10").performClick()

		composeRule.onNodeWithText(context.getString(R.string.encounter_result_damage_button)).assertIsEnabled()
		composeRule.onNodeWithText(context.getString(R.string.encounter_result_heal_button)).assertIsEnabled()
		composeRule.onNodeWithText(context.getString(R.string.encounter_result_damage_end_button)).assertIsEnabled()
		composeRule.onNodeWithText(context.getString(R.string.encounter_result_heal_end_button)).assertIsEnabled()
	}

	@Test
	fun applyResult_damageButtonInvokesCallbackWithoutAdvancingTurn() {
		var recordedType: ActionResolutionType? = null
		var recordedAmount: Int? = null
		var nextTurnCalls = 0
		setApplyResultContent(
			onApplyActionResult = { type, amount ->
				recordedType = type
				recordedAmount = amount
			},
			onNextTurn = { nextTurnCalls++ }
		)

		composeRule.onNodeWithText("10").performClick()
		composeRule.onNodeWithText(context.getString(R.string.encounter_result_damage_button)).performClick()

		assertEquals(ActionResolutionType.DAMAGE, recordedType)
		assertEquals(10, recordedAmount)
		assertEquals(0, nextTurnCalls)
	}

	@Test
	fun applyResult_damageEndButtonInvokesCallbackAndAdvancesTurn() {
		var recordedType: ActionResolutionType? = null
		var recordedAmount: Int? = null
		var nextTurnCalls = 0
		setApplyResultContent(
			onApplyActionResult = { type, amount ->
				recordedType = type
				recordedAmount = amount
			},
			onNextTurn = { nextTurnCalls++ }
		)

		composeRule.onNodeWithText("15").performClick()
		composeRule.onNodeWithText(context.getString(R.string.encounter_result_damage_end_button)).performClick()

		assertEquals(ActionResolutionType.DAMAGE, recordedType)
		assertEquals(15, recordedAmount)
		assertEquals(1, nextTurnCalls)
	}

	@Test
	fun previousTurnButton_invokesCallbackWhenHistoryIsAvailable() {
		var previousTurnCalls = 0
		composeRule.setContent {
			LoreweaverTheme {
				TurnLoopSection(
					participant = actorParticipant(),
					state = TurnLoopSectionState(
						turnStep = CombatTurnStep.SELECT_ACTION,
						canGoToPreviousTurn = true,
						pendingAction = null,
						selectedTarget = null,
						targetableParticipants = listOf(targetParticipant()),
					),
					callbacks = TurnLoopSectionCallbacks(
						onSelectAction = {},
						onSelectTarget = {},
						onApplyActionResult = { _, _ -> },
						onPreviousTurn = { previousTurnCalls++ },
						onNextTurn = {},
						onClearPendingTurn = {},
					),
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.encounter_previous_turn_button)).assertIsEnabled().performClick()
		assertEquals(1, previousTurnCalls)
	}

	private fun setApplyResultContent(
		onApplyActionResult: (ActionResolutionType, Int?) -> Unit = { _, _ -> },
		onNextTurn: () -> Unit = {}
	) {
		composeRule.setContent {
			LoreweaverTheme {
				TurnLoopSection(
					participant = actorParticipant(),
					state = TurnLoopSectionState(
						turnStep = CombatTurnStep.APPLY_RESULT,
						canGoToPreviousTurn = false,
						pendingAction = PendingTurnAction(
							name = "Strike",
							isAttack = true,
							allowsSelfTarget = false,
						),
						selectedTarget = targetParticipant(),
						targetableParticipants = listOf(targetParticipant()),
					),
					callbacks = TurnLoopSectionCallbacks(
						onSelectAction = {},
						onSelectTarget = {},
						onApplyActionResult = onApplyActionResult,
						onPreviousTurn = {},
						onNextTurn = onNextTurn,
						onClearPendingTurn = {},
					),
				)
			}
		}
	}

	private fun actorParticipant(): LiveParticipantUiModel {
		return LiveParticipantUiModel(
			combatant = CombatantState(
				characterId = "hero-1",
				name = "Hero",
				initiative = 15,
				currentHp = 12,
				maxHp = 12,
			),
			typeLabel = "Fighter",
			isPlayer = true,
			isEliminated = false,
			notes = "",
			persistentConditions = emptySet(),
			actionLabels = listOf("Strike"),
			resourceLines = emptyList(),
		)
	}

	private fun targetParticipant(): LiveParticipantUiModel {
		return LiveParticipantUiModel(
			combatant = CombatantState(
				characterId = "goblin-1",
				name = "Goblin",
				initiative = 10,
				currentHp = 7,
				maxHp = 7,
			),
			typeLabel = context.getString(R.string.encounter_turn_chip_enemy),
			isPlayer = false,
			isEliminated = false,
			notes = "",
			persistentConditions = emptySet(),
			actionLabels = listOf("Strike"),
			resourceLines = emptyList(),
		)
	}
}

