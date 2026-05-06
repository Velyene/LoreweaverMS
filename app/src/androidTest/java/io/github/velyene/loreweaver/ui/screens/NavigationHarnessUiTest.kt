/*
 * FILE: NavigationHarnessUiTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Campaign Detail Navigation Harness Tests
 * 2. Encounter Setup and Live Tracker Harness Tests
 * 3. Enemy Library Saved-State Handoff Harness Tests
 * 4. Reference Detail Harness Tests
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterSnapshot
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.util.CharacterParty
import io.github.velyene.loreweaver.domain.util.ReferenceDetailContent
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver
import io.github.velyene.loreweaver.ui.screens.tracker.live.LIVE_TRACKER_ROOT_TAG
import io.github.velyene.loreweaver.ui.screens.tracker.live.LiveTrackerCallbacks
import io.github.velyene.loreweaver.ui.screens.tracker.live.LiveTrackerView
import io.github.velyene.loreweaver.ui.screens.tracker.live.LiveTrackerViewState
import io.github.velyene.loreweaver.ui.screens.tracker.setup.ENCOUNTER_SETUP_LIST_TAG
import io.github.velyene.loreweaver.ui.screens.tracker.setup.ENCOUNTER_SETUP_ROOT_TAG
import io.github.velyene.loreweaver.ui.screens.tracker.setup.ENCOUNTER_SETUP_START_BUTTON_TAG
import io.github.velyene.loreweaver.ui.screens.tracker.setup.EncounterSetupCallbacks
import io.github.velyene.loreweaver.ui.screens.tracker.setup.EncounterSetupPartyActions
import io.github.velyene.loreweaver.ui.screens.tracker.setup.EncounterSetupView
import io.github.velyene.loreweaver.ui.screens.tracker.setup.EncounterSetupViewState
import io.github.velyene.loreweaver.ui.theme.LoreweaverTheme
import io.github.velyene.loreweaver.ui.viewmodels.CombatTurnStep
import io.github.velyene.loreweaver.ui.viewmodels.SessionDetailUiState
import io.github.velyene.loreweaver.ui.viewmodels.StagedEnemyItem
import io.github.velyene.loreweaver.ui.viewmodels.consumeEnemyLibraryEncounterSetupDraft
import io.github.velyene.loreweaver.ui.viewmodels.stashEnemyLibraryEncounterSetupDraft
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationHarnessUiTest {

	@get:Rule
	val composeRule = createComposeRule()

	@Test
	fun campaignDetailContent_clickingEncounterRowNavigatesToEncounterDestination() {
		val encounter = Encounter(
			id = "encounter-1",
			campaignId = "campaign-1",
			name = "Bridge Ambush"
		)

		composeRule.setContent {
			LoreweaverTheme {
				var selectedEncounterId by remember { mutableStateOf<String?>(null) }
				var selectedTab by remember { mutableStateOf(2) }

				if (selectedEncounterId == null) {
					CampaignDetailContent(
						state = CampaignDetailContentState(
							campaign = Campaign(
								id = "campaign-1",
								title = "Stormreach",
								description = "A fogbound frontier"
							),
							selectedTab = selectedTab,
							isLoading = false,
							notes = emptyList(),
							sessions = emptyList(),
							encounters = listOf(encounter)
						),
						actions = CampaignDetailActions(
							onEncounterClick = { selectedEncounterId = it },
							onSessionClick = {},
							onAddNote = { _, _, _ -> },
							onDeleteNote = {},
							onUpdateNote = {},
							onAddEncounter = {},
							onAddEncounterWithMonsters = { _, _ -> },
							onUpdateEncounter = { _, _ -> },
							onDeleteEncounter = {},
						),
						onSelectedTabChange = { selectedTab = it }
					)
				} else {
					CenteredEmptyState(message = "Encounter route: $selectedEncounterId")
				}
			}
		}

		composeRule.onNodeWithTag(encounterListItemTag(encounter.id)).assertIsDisplayed().performClick()
		composeRule.onNodeWithText("Encounter route: ${encounter.id}").assertIsDisplayed()
	}

	@Test
	fun campaignDetailContent_clickingSessionRowNavigatesToSessionDetailAndBackToCampaign() {
		val session = SessionRecord(
			id = "session-1",
			title = "Bridge Ambush Recap",
			date = 1234L,
			log = listOf("Hero drives the goblins back."),
			snapshot = EncounterSnapshot(
				combatants = listOf(CombatantState("hero-1", "Hero", 15, 12, 12)),
				currentTurnIndex = 0,
				currentRound = 2,
			)
		)

		composeRule.setContent {
			LoreweaverTheme {
				var route by remember { mutableStateOf("campaign") }
				var selectedTab by remember { mutableStateOf(1) }
				var selectedSessionId by remember { mutableStateOf<String?>(null) }

				when (route) {
					"campaign" -> CampaignDetailContent(
						state = CampaignDetailContentState(
							campaign = Campaign(
								id = "campaign-1",
								title = "Stormreach",
								description = "A fogbound frontier"
							),
							selectedTab = selectedTab,
							isLoading = false,
							notes = emptyList(),
							sessions = listOf(session),
							encounters = emptyList()
						),
						actions = CampaignDetailActions(
							onEncounterClick = {},
							onSessionClick = {
								selectedSessionId = it
								route = "session"
							},
							onAddNote = { _, _, _ -> },
							onDeleteNote = {},
							onUpdateNote = {},
							onAddEncounter = {},
							onAddEncounterWithMonsters = { _, _ -> },
							onUpdateEncounter = { _, _ -> },
							onDeleteEncounter = {},
						),
						onSelectedTabChange = { selectedTab = it }
					)

					"session" -> SessionDetailScreenRoute(
						sessionId = requireNotNull(selectedSessionId),
						uiState = SessionDetailUiState(
							isLoading = false,
							session = session,
							encounterName = "Bridge Ambush",
							campaignId = "campaign-1",
							campaignTitle = "Stormreach",
						),
						onBack = { route = "campaign" },
						onLoadSession = {},
						onOpenCampaign = { route = "campaign" }
					)
				}
			}
		}

		composeRule.onNodeWithTag(sessionHistoryListItemTag(session.id)).assertIsDisplayed().performClick()
		composeRule.onNodeWithText("Bridge Ambush", useUnmergedTree = true).assertIsDisplayed()
		composeRule.onNodeWithText("Stormreach").assertIsDisplayed()
		composeRule.onNodeWithText("Open Campaign").performClick()
		composeRule.onNodeWithTag(sessionHistoryListItemTag(session.id)).assertIsDisplayed()
	}

	@Test
	fun encounterSetupView_startActionNavigatesToLiveTrackerDestination() {
		val hero = testHero()

		composeRule.setContent {
			LoreweaverTheme {
				var route by remember { mutableStateOf("setup") }
				var combatants by remember { mutableStateOf(emptyList<CombatantState>()) }

				when (route) {
					"setup" -> EncounterSetupView(
						state = EncounterSetupViewState(
							notes = "",
							combatants = combatants,
							availablePartyMembers = listOf(hero),
							encounterDifficulty = null
						),
						callbacks = EncounterSetupCallbacks(
							onNotesChange = {},
							onStart = { route = "live" },
							partyActions = EncounterSetupPartyActions(
								onAddEntireParty = { combatants = listOf(hero.toCombatant()) },
								onClearPartyMembers = {
									combatants = combatants.filterNot { it.characterId == hero.id }
								},
								onTogglePartyMember = { character ->
									combatants = combatants.togglePartyMember(character)
								}
							),
							onAddEnemies = { name, hp, initiative, quantity ->
								repeat(quantity) { index ->
									combatants = combatants + CombatantState(
										characterId = "$name-$index",
										name = name,
										initiative = initiative,
										currentHp = hp,
										maxHp = hp,
									)
								}
							},
							onUpdateCombatantInitiative = { characterId, initiative ->
								combatants = combatants.map { combatant ->
									if (combatant.characterId == characterId) {
										combatant.copy(initiative = initiative)
									} else {
										combatant
									}
								}
							},
							onMoveCombatantUp = {},
							onMoveCombatantDown = {},
							onRemoveCombatant = { characterId ->
								combatants = combatants.filterNot { it.characterId == characterId }
							}
						)
					)

					"live" -> LiveTrackerView(
						state = LiveTrackerViewState(
							encounterName = "Bridge Ambush",
							encounterNotes = "",
							round = 1,
							combatants = combatants,
							availableCharacters = listOf(hero),
							turnIndex = 0,
							statuses = emptyList(),
							canGoToPreviousTurn = false,
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
							onEnd = { route = "setup" }
						)
					)
				}
			}
		}

		composeRule.onNodeWithTag(ENCOUNTER_SETUP_ROOT_TAG).assertIsDisplayed()
		composeRule.onNodeWithTag(ENCOUNTER_SETUP_LIST_TAG).performScrollToIndex(4)
		composeRule.onNodeWithText(hero.name).performClick()
		composeRule.onNodeWithTag(ENCOUNTER_SETUP_LIST_TAG).performScrollToIndex(7)
		composeRule.onNodeWithTag(ENCOUNTER_SETUP_START_BUTTON_TAG).assertIsEnabled().performClick()
		composeRule.onNodeWithTag(LIVE_TRACKER_ROOT_TAG).assertIsDisplayed()
	}

	@Test
	fun enemyLibraryHandoff_savedStatePayloadImportsIntoFreshSetupHarness() {
		val hero = testHero()

		composeRule.setContent {
			LoreweaverTheme {
				val savedStateHandle = remember { SavedStateHandle() }
				var route by remember { mutableStateOf("library") }

				when (route) {
					"library" -> EnemyLibraryContent(
						uiState = io.github.velyene.loreweaver.ui.viewmodels.EnemyLibraryUiState(
							stagedEnemies = listOf(
								StagedEnemyItem(
									key = "goblin",
									catalogKey = "goblin",
									name = "Goblin",
									hp = 7,
									initiative = 2,
									quantity = 2,
								),
							),
							stagedEnemyTotalCount = 2,
						),
						onBack = {},
						onSearchQueryChange = {},
						onGroupSelected = {},
						onCreatureTypeSelected = {},
						onChallengeRatingSelected = {},
						onOpenDetail = {},
						onStageMonster = {},
						onIncrementStagedEnemy = {},
						onAddTemporaryEnemy = { _, _, _, _ -> },
						onRemoveStagedMonster = {},
						onDecrementStagedMonster = {},
						onClearStagedEnemies = {},
						onOpenEncounterSetup = { stagedEnemies ->
							stashEnemyLibraryEncounterSetupDraft(savedStateHandle, stagedEnemies)
							route = "setup"
						},
						onCloseDetail = {},
					)

					"setup" -> {
						val importedDraft = remember(route) {
							consumeEnemyLibraryEncounterSetupDraft(savedStateHandle)
						}
						EncounterSetupView(
							state = EncounterSetupViewState(
								startFresh = true,
								notes = "",
								combatants = importedDraft.stagedEnemies.flatMap { stagedEnemy ->
									(1..stagedEnemy.quantity).map { index ->
										CombatantState(
											characterId = "${stagedEnemy.key}-$index",
											name = if (stagedEnemy.quantity > 1) "${stagedEnemy.name} $index" else stagedEnemy.name,
											initiative = stagedEnemy.initiative,
											currentHp = stagedEnemy.hp,
											maxHp = stagedEnemy.hp,
										)
									}
								},
								availablePartyMembers = listOf(hero),
								encounterDifficulty = null,
								importedLibraryEnemyCount = importedDraft.totalEnemyCount,
								importedTemporaryEnemyCount = importedDraft.temporaryEnemyCount,
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
			}
		}

		composeRule.onNodeWithText("Open encounter setup with staged enemies").performClick()
		composeRule.onNodeWithTag(ENCOUNTER_SETUP_LIST_TAG).performScrollToIndex(5)
		composeRule.onNodeWithText("Enemy Library Import").assertIsDisplayed()
		composeRule.onNodeWithText("2 staged enemies were imported into this setup.").assertIsDisplayed()
	}

	@Test
	fun statusChip_clickingOfficialConditionNavigatesToReferenceDetailView() {
		val conditionName = "Prone"

		composeRule.setContent {
			LoreweaverTheme {
				var selectedDetail by remember { mutableStateOf<ReferenceDetailContent?>(null) }

				if (selectedDetail == null) {
					StatusChipFlowRow(
						statuses = listOf(statusChipModel(conditionName)),
						onStatusClick = { status ->
							selectedDetail = resolveConditionDetail(status.name)
						}
					)
				} else {
					GenericReferenceDetailView(
						detail = requireNotNull(selectedDetail),
						onBack = { selectedDetail = null }
					)
				}
			}
		}

		composeRule.onNodeWithTag(statusChipTag(conditionName)).assertIsDisplayed().performClick()
		composeRule.onNodeWithText(conditionName).assertIsDisplayed()
	}

	private fun resolveConditionDetail(label: String): ReferenceDetailContent {
		val target = requireNotNull(ConditionConstants.referenceTargetFor(label))
		val detailCategory = requireNotNull(target.detailCategory)
		val detailSlug = requireNotNull(target.detailSlug)
		return requireNotNull(ReferenceDetailResolver.resolve(detailCategory, detailSlug))
	}

	private fun testHero(): CharacterEntry = CharacterEntry(
		id = "hero-1",
		name = "Hero",
		type = "Fighter",
		party = CharacterParty.ADVENTURERS,
		hp = 12,
		maxHp = 12,
		initiative = 15,
		level = 3,
		ac = 16,
	)

	private fun CharacterEntry.toCombatant(): CombatantState = CombatantState(
		characterId = id,
		name = name,
		initiative = initiative,
		currentHp = hp,
		maxHp = maxHp,
	)

	private fun List<CombatantState>.togglePartyMember(character: CharacterEntry): List<CombatantState> {
		return if (any { it.characterId == character.id }) {
			filterNot { it.characterId == character.id }
		} else {
			this + character.toCombatant()
		}
	}
}

