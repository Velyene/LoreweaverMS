/*
 * FILE: LiveTrackerViewUiTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Live Tracker DM Surface Tests
 * 2. Test Content Builders
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.ui.screens.ADD_CONDITION_SHEET_TAG
import io.github.velyene.loreweaver.ui.theme.LoreweaverTheme
import io.github.velyene.loreweaver.ui.viewmodels.CombatTurnStep
import io.github.velyene.loreweaver.ui.viewmodels.encodeEncounterInfo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LiveTrackerViewUiTest {

	@get:Rule
	val composeRule = createComposeRule()

	private val context: Context = ApplicationProvider.getApplicationContext()

	@Test
	fun liveTrackerView_showsDmZonesForRosterAndRollingLog() {
		composeRule.setContent {
			LoreweaverTheme {
				LiveTrackerView(
					state = LiveTrackerViewState(
						encounterName = "Bridge Clash",
						encounterNotes = encodeEncounterInfo(
							locationTerrain = "Ashen bridge, low visibility",
							notesBody = "Ancient runes glow in the walls. The bridge shakes under each heavy blow.",
						),
						round = 2,
						combatants = listOf(
							CombatantState(
								characterId = "hero-1",
								name = "Aria",
								initiative = 15,
								currentHp = 18,
								maxHp = 20
							),
							CombatantState(
								characterId = "goblin-1",
								name = "Goblin",
								initiative = 11,
								currentHp = 4,
								maxHp = 7
							)
						),
						availableCharacters = listOf(
							CharacterEntry(
								id = "hero-1",
								name = "Aria",
								party = "Adventurers",
								type = "Fighter",
								notes = "Watch the bridge edge.",
								activeConditions = setOf("Poisoned"),
								hp = 18,
								maxHp = 20,
								initiative = 2
							)
						),
						turnIndex = 0,
						statuses = listOf("Aria takes cover.", "Goblin is bloodied!"),
						canGoToPreviousTurn = true,
						turnStep = CombatTurnStep.SELECT_ACTION,
						pendingAction = null,
						selectedTargetId = null
					),
					callbacks = LiveTrackerCallbacks(
						onSelectAction = {},
						onSelectTarget = {},
						onApplyActionResult = { _, _ -> },
						onClearPendingTurn = {},
						onPreviousTurn = {},
						onNextTurn = {},
						onAdvanceRound = {},
						onHpChange = { _, _ -> },
						onSetHp = { _, _ -> },
						onMarkDefeated = {},
						onAddParticipantNote = { _, _ -> },
						onDuplicateEnemy = {},
						onRemoveCombatant = {},
						onAddCondition = { _, _, _, _ -> },
						onRemoveCondition = { _, _ -> },
						onEnd = {}
					)
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.encounter_turn_timeline_summary, 2, "Aria -> Goblin")).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_battle_pulse_title)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_battle_pulse_select_action_message, "Aria")).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_active_panel_shared_tracker_supporting_text)).assertIsDisplayed()
		composeRule.onNodeWithTag(LIVE_TRACKER_CONTENT_LIST_TAG).performScrollToIndex(1)
		composeRule.onNodeWithText(context.getString(R.string.encounter_dm_priority_title)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_dm_priority_next_up, "Goblin")).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_danger_signals_title)).assertIsDisplayed()
		composeRule.onNodeWithText("• ${context.getString(R.string.encounter_danger_signal_persistent, 1)}").assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_dm_control_panel_title)).assertExists()
		composeRule.onNodeWithText(context.getString(R.string.encounter_dm_control_panel_supporting_text)).assertExists()
		composeRule.onNodeWithTag(LIVE_TRACKER_CONTENT_LIST_TAG).performScrollToIndex(2)
		composeRule.onNodeWithText(context.getString(R.string.encounter_secondary_party_title)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_secondary_party_supporting_text)).assertIsDisplayed()
		composeRule.onNodeWithTag(LIVE_TRACKER_CONTENT_LIST_TAG).performScrollToIndex(3)
		composeRule.onNodeWithText(context.getString(R.string.encounter_enemy_panel_title)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_enemy_panel_supporting_text)).assertIsDisplayed()
		composeRule.onNodeWithTag(LIVE_TRACKER_CONTENT_LIST_TAG).performScrollToIndex(4)
		composeRule.onNodeWithText(context.getString(R.string.encounter_battlefield_roster_title)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_battlefield_roster_supporting_text)).assertIsDisplayed()
		composeRule.onNodeWithText("Goblin").assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_participant_details_button)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_damage_heal_button)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_notes_expand_action)).performClick()
		composeRule.onNodeWithTag(LIVE_TRACKER_CONTENT_LIST_TAG).performScrollToIndex(5)
		composeRule.onNodeWithText(context.getString(R.string.encounter_location_terrain_label)).assertIsDisplayed()
		composeRule.onNodeWithText("Ashen bridge, low visibility").assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_notes_section_title)).assertIsDisplayed()
		composeRule.onNodeWithText("Ancient runes glow in the walls. The bridge shakes under each heavy blow.").assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_participant_details_button)).performClick()
		composeRule.onNodeWithTag(PARTICIPANT_DETAIL_SHEET_TAG).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_participant_detail_title, "Aria")).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_participant_detail_statuses_title)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.character_status_persistent_effects_title)).assertIsDisplayed()
		composeRule.onNodeWithText("Watch the bridge edge.").assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_apply_status_button)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_apply_status_button)).performClick()
		composeRule.onNodeWithTag(ADD_CONDITION_SHEET_TAG).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_apply_status_dialog_title, "Aria")).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.condition_picker_official_conditions)).assertIsDisplayed()
		composeRule.onNodeWithText("Poisoned").assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.cancel_button)).performClick()
		composeRule.onNodeWithText(context.getString(R.string.encounter_participant_details_button)).performClick()
		composeRule.onNodeWithText(context.getString(R.string.encounter_damage_heal_button)).performClick()
		composeRule.onNodeWithTag(HP_ADJUSTMENT_SHEET_TAG).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_hp_adjust_dialog_title, "Aria")).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_hp_adjust_dialog_supporting_text)).assertIsDisplayed()
		composeRule.onNodeWithText("-10").assertIsDisplayed()
		composeRule.onNodeWithText("+10").assertIsDisplayed()
		composeRule.onNodeWithTag(LIVE_TRACKER_CONTENT_LIST_TAG).performScrollToIndex(6)
		composeRule.onNodeWithText(context.getString(R.string.encounter_log_feed_supporting_text)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.combat_log_bullet, "Notes: Location / Terrain: Ashen bridge, low visibility Ancient runes glow in the walls. The bridge shakes under each heavy blow.")).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.combat_log_bullet, "Goblin is bloodied!")).assertIsDisplayed()
	}
}
