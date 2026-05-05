/*
 * FILE: LiveTrackerCompactParticipantsTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Enemy Panel Interaction Tests
 * 2. Battlefield Roster Grouping Tests
 * 3. Test Content Builders
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.ui.theme.LoreweaverTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LiveTrackerCompactParticipantsTest {

	@get:Rule
	val composeRule = createComposeRule()
	private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

	@Test
	fun enemyPanel_clickingSelectableCardFocusesAndTargetsCombatant() {
		var focusedId: String? = null
		var selectedId: String? = null
		setEnemyPanelContent(
			onFocusCombatant = { focusedId = it },
			onSelectTarget = { selectedId = it }
		)

		composeRule.onNode(hasText("Goblin") and hasClickAction()).performClick()

		assertEquals("goblin-1", focusedId)
		assertEquals("goblin-1", selectedId)
	}

	@Test
	fun enemyPanel_clickingNonSelectableCardStillFocusesWithoutTargeting() {
		var focusedId: String? = null
		var selectedId: String? = null
		setEnemyPanelContent(
			selectableTargetIds = emptySet(),
			onFocusCombatant = { focusedId = it },
			onSelectTarget = { selectedId = it }
		)

		composeRule.onNode(hasText("Goblin") and hasClickAction()).performClick()

		assertEquals("goblin-1", focusedId)
		assertEquals(null, selectedId)
	}

	@Test
	fun enemyPanel_quickHpButtonsInvokeHpChangeCallback() {
		var hpCharacterId: String? = null
		var hpDelta: Int? = null
		setEnemyPanelContent(
			onHpChange = { characterId, delta ->
				hpCharacterId = characterId
				hpDelta = delta
			}
		)

		composeRule.onNodeWithText("+5").assertExists()
		composeRule.onNodeWithText("+5").performClick()

		assertEquals("goblin-1", hpCharacterId)
		assertEquals(5, hpDelta)
	}

	@Test
	fun battlefieldRosterPanel_focusedEnemyShowsGroupedDmControls() {
		composeRule.setContent {
			LoreweaverTheme {
				BattlefieldRosterPanel(
					state = BattlefieldRosterPanelState(
						rosterParticipants = listOf(enemyParticipant()),
						selection = BattlefieldRosterSelectionState(
							currentParticipantId = null,
							selectedTargetId = null,
							focusedCombatantId = "goblin-1",
							isCompactBattleMode = true,
							selectableTargetIds = emptySet(),
						),
					),
					callbacks = BattlefieldRosterPanelCallbacks(
						onSelectTarget = {},
						onFocusCombatant = {},
						onHpChange = { _, _ -> },
						onSetHp = { _, _ -> },
						onMarkDefeated = {},
						onAddParticipantNote = { _, _ -> },
						onDuplicateEnemy = {},
						onRemoveCombatant = {},
						onAddCondition = { _, _, _, _ -> },
						onRemoveCondition = { _, _ -> },
					),
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.encounter_compact_controls_hp_title)).assertExists()
		composeRule.onNodeWithText(context.getString(R.string.encounter_compact_controls_status_notes_title)).assertExists()
		composeRule.onNodeWithText(context.getString(R.string.encounter_compact_controls_enemy_admin_title)).assertExists()
	}

	@Test
	fun battlefieldRosterPanel_collapseGroupHidesGroupedEnemiesUntilExpanded() {
		composeRule.setContent {
			LoreweaverTheme {
				BattlefieldRosterPanel(
					state = BattlefieldRosterPanelState(
						rosterParticipants = listOf(
							enemyParticipant(id = "goblin-1", name = "Goblin 1"),
							enemyParticipant(id = "goblin-2", name = "Goblin 2"),
						),
						selection = BattlefieldRosterSelectionState(
							currentParticipantId = null,
							selectedTargetId = null,
							focusedCombatantId = null,
							isCompactBattleMode = true,
							selectableTargetIds = emptySet(),
						),
					),
					callbacks = BattlefieldRosterPanelCallbacks(
						onSelectTarget = {},
						onFocusCombatant = {},
						onHpChange = { _, _ -> },
						onSetHp = { _, _ -> },
						onMarkDefeated = {},
						onAddParticipantNote = { _, _ -> },
						onDuplicateEnemy = {},
						onRemoveCombatant = {},
						onAddCondition = { _, _, _, _ -> },
						onRemoveCondition = { _, _ -> },
					),
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.encounter_enemy_group_collapse_action)).performClick()
		assertEquals(0, composeRule.onAllNodesWithText("Goblin 1").fetchSemanticsNodes().size)
		composeRule.onNodeWithText(context.getString(R.string.encounter_enemy_group_expand_action)).performClick()
		composeRule.onNodeWithText("Goblin 1").assertExists()
	}

	private fun setEnemyPanelContent(
		selectableTargetIds: Set<String> = setOf("goblin-1"),
		onSelectTarget: (String) -> Unit = {},
		onFocusCombatant: (String) -> Unit = {},
		onHpChange: (String, Int) -> Unit = { _, _ -> }
	) {
		composeRule.setContent {
			LoreweaverTheme {
				EnemyPanel(
					enemies = listOf(enemyParticipant()),
					currentParticipantId = null,
					selectedTargetId = null,
					focusedCombatantId = null,
					isCompactBattleMode = true,
					selectableTargetIds = selectableTargetIds,
					onSelectTarget = onSelectTarget,
					onFocusCombatant = onFocusCombatant,
					onHpChange = onHpChange,
				)
			}
		}
	}

	private fun enemyParticipant(): LiveParticipantUiModel {
		return enemyParticipant(id = "goblin-1", name = "Goblin")
	}

	private fun enemyParticipant(id: String, name: String): LiveParticipantUiModel {
		return LiveParticipantUiModel(
			combatant = CombatantState(
				characterId = id,
				name = name,
				initiative = 10,
				currentHp = 7,
				maxHp = 7,
			),
			typeLabel = "Goblin",
			isPlayer = false,
			isEliminated = false,
			notes = "",
			persistentConditions = emptySet(),
			actionLabels = listOf("Strike"),
			resourceLines = emptyList(),
		)
	}
}
