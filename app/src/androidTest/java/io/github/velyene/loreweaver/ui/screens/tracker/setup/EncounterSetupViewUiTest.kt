/*
 * FILE: EncounterSetupViewUiTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Encounter Setup Launch and Party Tests
 * 2. Enemy and Generation Workflow Tests
 * 3. Imported Library Draft Tests
 * 4. Test Data Helpers
 */

package io.github.velyene.loreweaver.ui.screens.tracker.setup

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.EncounterGenerationSettings
import io.github.velyene.loreweaver.domain.model.EncounterGenerationSourceFilter
import io.github.velyene.loreweaver.ui.theme.LoreweaverTheme
import io.github.velyene.loreweaver.ui.viewmodels.InitiativeMode
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EncounterSetupViewUiTest {

	@get:Rule
	val composeRule = createComposeRule()

	private val context: Context = ApplicationProvider.getApplicationContext()

	@Test
	fun encounterSetupView_requiresPartyBeforeStartingAndSelectionEnablesLaunch() {
		val partyMember = samplePartyMember()
		var combatants by mutableStateOf<List<CombatantState>>(emptyList())
		var startClicks = 0

		composeRule.setContent {
			LoreweaverTheme {
				EncounterSetupView(
					state = EncounterSetupViewState(
						notes = "",
						combatants = combatants,
						availablePartyMembers = listOf(partyMember),
						encounterDifficulty = null
					),
					callbacks = EncounterSetupCallbacks(
						onNotesChange = {},
						onStart = { startClicks++ },
						partyActions = EncounterSetupPartyActions(
							onAddEntireParty = {
								combatants = listOf(partyMember.toCombatant())
							},
							onClearPartyMembers = { combatants = emptyList() },
							onTogglePartyMember = { character ->
								combatants = if (combatants.any { it.characterId == character.id }) {
									combatants.filterNot { it.characterId == character.id }
								} else {
									combatants + character.toCombatant()
								}
							}
						),
						onAddEnemies = { _, _, _, _ -> },
						onUpdateCombatantInitiative = { _, _ -> },
						onMoveCombatantUp = {},
						onMoveCombatantDown = {},
						onRemoveCombatant = { characterId ->
							combatants = combatants.filterNot { it.characterId == characterId }
						}
					)
				)
			}
		}

		composeRule.onNodeWithTag(ENCOUNTER_SETUP_LIST_TAG).performScrollToIndex(7)
		composeRule.onNodeWithText(context.getString(R.string.encounter_setup_action_bar_missing_party)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.start_encounter_button)).assertIsNotEnabled()

		composeRule.onNodeWithTag(ENCOUNTER_SETUP_LIST_TAG).performScrollToIndex(4)
		composeRule.onNodeWithText(partyMember.name).performClick()

		composeRule.onNodeWithText(context.getString(R.string.encounter_party_member_selected)).assertIsDisplayed()
		composeRule.onNodeWithTag(ENCOUNTER_SETUP_LIST_TAG).performScrollToIndex(7)
		composeRule
			.onNodeWithText(context.getString(R.string.encounter_setup_action_bar_ready, 1, 0))
			.assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.start_encounter_button)).assertIsEnabled()
		composeRule.onNodeWithText(context.getString(R.string.start_encounter_button)).performClick()

		assertEquals(1, startClicks)
	}

	@Test
	fun encounterSetupView_presetChipAddsAutoNumberedEnemyWave() {
		val partyMember = samplePartyMember()
		var combatants by mutableStateOf<List<CombatantState>>(listOf(partyMember.toCombatant()))

		composeRule.setContent {
			LoreweaverTheme {
				EncounterSetupView(
					state = EncounterSetupViewState(
						notes = "Bridge bottleneck.",
						combatants = combatants,
						availablePartyMembers = listOf(partyMember),
						encounterDifficulty = null
					),
					callbacks = EncounterSetupCallbacks(
						onNotesChange = {},
						onStart = {},
						partyActions = EncounterSetupPartyActions(
							onAddEntireParty = {},
							onClearPartyMembers = {},
							onTogglePartyMember = {}
						),
						onAddEnemies = { name, hp, initiative, quantity ->
							val existingCount = combatants.count { it.name.startsWith(name) }
							val additions = (1..quantity).map { index ->
								CombatantState(
									characterId = "$name-${existingCount + index}",
									name = if (quantity > 1) "$name ${existingCount + index}" else name,
									initiative = initiative,
									currentHp = hp,
									maxHp = hp
								)
							}
							combatants = combatants + additions
						},
						onUpdateCombatantInitiative = { _, _ -> },
						onMoveCombatantUp = {},
						onMoveCombatantDown = {},
						onRemoveCombatant = { characterId ->
							combatants = combatants.filterNot { it.characterId == characterId }
						}
					)
				)
			}
		}

		composeRule.onNodeWithTag(ENCOUNTER_SETUP_LIST_TAG).performScrollToIndex(5)
		composeRule.onNodeWithText("Goblin ×4 • HP 7 • Init 14").performScrollTo().performClick()

		composeRule.onNodeWithText("Goblin 1").performScrollTo().assertIsDisplayed()
		composeRule.onNodeWithText("Goblin 4").performScrollTo().assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_enemies_title)).performScrollTo().assertIsDisplayed()
	}

	@Test
	fun encounterSetupView_sourceFilterChipsUpdateGenerationSettings() {
		val partyMember = samplePartyMember()
		var generationSettings by mutableStateOf(EncounterGenerationSettings())

		composeRule.setContent {
			LoreweaverTheme {
				EncounterSetupView(
					state = EncounterSetupViewState(
						notes = "Forest edge.",
						combatants = listOf(partyMember.toCombatant()),
						availablePartyMembers = listOf(partyMember),
						encounterDifficulty = null,
						generationSettings = generationSettings
					),
					callbacks = EncounterSetupCallbacks(
						onNotesChange = {},
						onStart = {},
						partyActions = EncounterSetupPartyActions(
							onAddEntireParty = {},
							onClearPartyMembers = {},
							onTogglePartyMember = {}
						),
						onAddEnemies = { _, _, _, _ -> },
						onUpdateGenerationSettings = { generationSettings = it },
						onGenerateEncounter = {},
						onUpdateCombatantInitiative = { _, _ -> },
						onMoveCombatantUp = {},
						onMoveCombatantDown = {},
						onRemoveCombatant = {}
					)
				)
			}
		}

		composeRule.onNodeWithTag(ENCOUNTER_SETUP_LIST_TAG).performScrollToIndex(5)
		composeRule.onNodeWithText("SRD Only").performScrollTo().assertIsDisplayed()
		composeRule.onNodeWithText("Custom Only").performScrollTo().assertIsDisplayed()
		composeRule.onNodeWithText("Both").performScrollTo().assertIsDisplayed()

		composeRule.onNodeWithText("Both").performClick()

		assertEquals(EncounterGenerationSourceFilter.BOTH, generationSettings.sourceFilter)
	}

	@Test
	fun encounterSetupView_rendersDmStepFlowHeadings() {
		val partyMember = samplePartyMember()

		composeRule.setContent {
			LoreweaverTheme {
				EncounterSetupView(
					state = EncounterSetupViewState(
						encounterName = "Goblin Ambush",
						startFresh = true,
						notes = "Bridge choke point.",
						combatants = listOf(partyMember.toCombatant()),
						availablePartyMembers = listOf(partyMember),
						encounterDifficulty = null,
					),
					callbacks = EncounterSetupCallbacks(
						onNotesChange = {},
						onStart = {},
						partyActions = EncounterSetupPartyActions(
							onAddEntireParty = {},
							onClearPartyMembers = {},
							onTogglePartyMember = {},
						),
						onAddEnemies = { _, _, _, _ -> },
						onUpdateCombatantInitiative = { _, _ -> },
						onMoveCombatantUp = {},
						onMoveCombatantDown = {},
						onRemoveCombatant = {},
					),
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.encounter_setup_step_format, 1, context.getString(R.string.encounter_setup_info_step_title))).assertIsDisplayed()
		composeRule.onNodeWithTag(ENCOUNTER_SETUP_LIST_TAG).performScrollToIndex(4)
		composeRule.onNodeWithText(context.getString(R.string.encounter_setup_step_format, 2, context.getString(R.string.encounter_party_members_title))).assertIsDisplayed()
		composeRule.onNodeWithTag(ENCOUNTER_SETUP_LIST_TAG).performScrollToIndex(3)
		composeRule.onNodeWithText(context.getString(R.string.encounter_location_terrain_label)).performScrollTo().assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_setup_operator_brief_title)).performScrollTo().assertIsDisplayed()
		composeRule.onNodeWithTag(ENCOUNTER_SETUP_LIST_TAG).performScrollToIndex(7)
		composeRule.onNodeWithText(context.getString(R.string.encounter_setup_step_format, 5, context.getString(R.string.encounter_setup_launch_step_title))).assertIsDisplayed()
	}

	@Test
	fun encounterSetupView_showsEnemyLibraryImportSummaryWhenFreshSetupIsHydrated() {
		val partyMember = samplePartyMember()

		composeRule.setContent {
			LoreweaverTheme {
				EncounterSetupView(
					state = EncounterSetupViewState(
						encounterName = "Bridge Clash",
						startFresh = true,
						notes = "",
						combatants = listOf(
							CombatantState(
								characterId = "goblin-1",
								name = "Goblin",
								initiative = 2,
								currentHp = 7,
								maxHp = 7,
							),
						),
						availablePartyMembers = listOf(partyMember),
						encounterDifficulty = null,
						importedLibraryEnemyCount = 3,
						importedTemporaryEnemyCount = 1,
					),
					callbacks = EncounterSetupCallbacks(
						onNotesChange = {},
						onStart = {},
						partyActions = EncounterSetupPartyActions(
							onAddEntireParty = {},
							onClearPartyMembers = {},
							onTogglePartyMember = {},
						),
						onAddEnemies = { _, _, _, _ -> },
						onUpdateCombatantInitiative = { _, _ -> },
						onMoveCombatantUp = {},
						onMoveCombatantDown = {},
						onRemoveCombatant = {},
					),
				)
			}
		}

		composeRule.onNodeWithTag(ENCOUNTER_SETUP_LIST_TAG).performScrollToIndex(5)
		composeRule.onNodeWithText(context.getString(R.string.encounter_setup_imported_staging_title)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_setup_imported_staging_summary_with_temp, 3, 1)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.encounter_setup_imported_staging_supporting_text_with_temp)).assertIsDisplayed()
	}

	@Test
	fun encounterSetupView_initiativeModeChipsUpdateSelection() {
		val partyMember = samplePartyMember()
		var initiativeMode by mutableStateOf(InitiativeMode.MANUAL)

		composeRule.setContent {
			LoreweaverTheme {
				EncounterSetupView(
					state = EncounterSetupViewState(
						notes = "",
						combatants = listOf(partyMember.toCombatant()),
						availablePartyMembers = listOf(partyMember),
						encounterDifficulty = null,
						initiativeMode = initiativeMode,
					),
					callbacks = EncounterSetupCallbacks(
						onNotesChange = {},
						onInitiativeModeChange = { initiativeMode = it },
						onStart = {},
						partyActions = EncounterSetupPartyActions(
							onAddEntireParty = {},
							onClearPartyMembers = {},
							onTogglePartyMember = {},
						),
						onAddEnemies = { _, _, _, _ -> },
						onUpdateCombatantInitiative = { _, _ -> },
						onMoveCombatantUp = {},
						onMoveCombatantDown = {},
						onRemoveCombatant = {},
					),
				)
			}
		}

		composeRule.onNodeWithTag(ENCOUNTER_SETUP_LIST_TAG).performScrollToIndex(6)
		composeRule.onNodeWithText(context.getString(R.string.initiative_mode_auto_sort)).performScrollTo().performClick()

		assertEquals(InitiativeMode.AUTO_SORT, initiativeMode)
		composeRule.onNodeWithText(context.getString(R.string.initiative_mode_auto_sort_description)).assertIsDisplayed()
	}

	@Test
	fun encounterSetupView_encounterNameFieldUpdatesName() {
		val partyMember = samplePartyMember()
		var encounterName by mutableStateOf("")

		composeRule.setContent {
			LoreweaverTheme {
				EncounterSetupView(
					state = EncounterSetupViewState(
						encounterName = encounterName,
						notes = "",
						combatants = listOf(partyMember.toCombatant()),
						availablePartyMembers = listOf(partyMember),
						encounterDifficulty = null,
					),
					callbacks = EncounterSetupCallbacks(
						onEncounterNameChange = { encounterName = it },
						onNotesChange = {},
						onStart = {},
						partyActions = EncounterSetupPartyActions(
							onAddEntireParty = {},
							onClearPartyMembers = {},
							onTogglePartyMember = {},
						),
						onAddEnemies = { _, _, _, _ -> },
						onUpdateCombatantInitiative = { _, _ -> },
						onMoveCombatantUp = {},
						onMoveCombatantDown = {},
						onRemoveCombatant = {},
					),
				)
			}
		}

		composeRule.onNodeWithTag(ENCOUNTER_SETUP_NAME_FIELD_TAG).performTextInput("Ashen Ruins")
		assertEquals("Ashen Ruins", encounterName)

		composeRule.onNodeWithTag(ENCOUNTER_SETUP_NAME_FIELD_TAG).performTextClearance()
		composeRule.onNodeWithTag(ENCOUNTER_SETUP_NAME_FIELD_TAG).performTextInput("Ruined Chapel")
		assertEquals("Ruined Chapel", encounterName)
	}

	@Test
	fun encounterSetupView_locationTerrainFieldUpdatesValue() {
		val partyMember = samplePartyMember()
		var locationTerrain by mutableStateOf("")

		composeRule.setContent {
			LoreweaverTheme {
				EncounterSetupView(
					state = EncounterSetupViewState(
						locationTerrain = locationTerrain,
						notes = "",
						combatants = listOf(partyMember.toCombatant()),
						availablePartyMembers = listOf(partyMember),
						encounterDifficulty = null,
					),
					callbacks = EncounterSetupCallbacks(
						onLocationTerrainChange = { locationTerrain = it },
						onNotesChange = {},
						onStart = {},
						partyActions = EncounterSetupPartyActions(
							onAddEntireParty = {},
							onClearPartyMembers = {},
							onTogglePartyMember = {},
						),
						onAddEnemies = { _, _, _, _ -> },
						onUpdateCombatantInitiative = { _, _ -> },
						onMoveCombatantUp = {},
						onMoveCombatantDown = {},
						onRemoveCombatant = {},
					),
				)
			}
		}

		composeRule.onNodeWithTag(ENCOUNTER_SETUP_LOCATION_FIELD_TAG).performTextInput("Ash-covered bridge with narrow stairs")
		assertEquals("Ash-covered bridge with narrow stairs", locationTerrain)
	}

	private fun samplePartyMember(): CharacterEntry {
		return CharacterEntry(
			id = "aria-1",
			name = "Aria",
			party = "Adventurers",
			level = 3,
			hp = 20,
			maxHp = 20,
			initiative = 2
		)
	}

	private fun CharacterEntry.toCombatant(): CombatantState {
		return CombatantState(
			characterId = id,
			name = name,
			initiative = initiative,
			currentHp = hp,
			maxHp = maxHp
		)
	}
}

