/*
 * FILE: CombatViewModelTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Turn progression and condition lifecycle behavior
 * 2. Status-log and condition mutation coverage
 * 3. Encounter difficulty recalculation
 * 4. Shared test-support integration
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.MainDispatcherRule
import io.github.velyene.loreweaver.domain.model.CharacterAction
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterSnapshot
import io.github.velyene.loreweaver.domain.model.EncounterStatus
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.util.DifficultyRating
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CombatViewModelTest {
	@get:Rule
	val mainDispatcherRule = MainDispatcherRule()

	@Test
	fun nextTurn_advancesToNextCombatantWithinRound() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createCombatViewModel(repository)
			val combatants = listOf(
				combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12),
				combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 10, hp = 7)
			)

			viewModel.addParty(combatants)
			advanceUntilIdle()
			viewModel.nextTurn()

			with(viewModel.uiState.value) {
				assertEquals(1, currentTurnIndex)
				assertEquals(1, currentRound)
			}
		}
	}

	@Test
	fun nextTurn_wrapsRound_decrementsAndExpiresConditions() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createCombatViewModel(repository)
			val first = combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12)
			val second = combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 10, hp = 7)

			viewModel.addParty(listOf(first, second))
			advanceUntilIdle()
			viewModel.addCondition(characterId = HERO_ID, conditionName = "Poisoned", duration = 1)
			advanceUntilIdle()

			viewModel.nextTurn()
			viewModel.nextTurn()

			with(viewModel.uiState.value) {
				assertEquals(0, currentTurnIndex)
				assertEquals(2, currentRound)
				assertTrue(this.combatants.first { it.characterId == HERO_ID }.conditions.isEmpty())
				assertTrue(activeStatuses.any { it.contains("$HERO_NAME's Poisoned condition has expired") })
				assertTrue(activeStatuses.any { it.contains("Round 2 begins") })
			}
		}
	}

	@Test
	fun updateCombatantHp_logsDamageAndDefeatedUnitEvent() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createCombatViewModel(repository)
			val combatant = combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 5)

			viewModel.addParty(listOf(combatant))
			advanceUntilIdle()
			viewModel.updateCombatantHp(HERO_ID, -5)

			with(viewModel.uiState.value) {
				assertEquals(0, combatants.first().currentHp)
				assertTrue(activeStatuses.any { it.contains("$HERO_NAME takes 5 damage (0/5 HP)") })
				assertTrue(activeStatuses.any { it.contains("$HERO_NAME has been defeated") })
			}
		}
	}

	@Test
	fun addCondition_addsConditionAndStatusMessage() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createCombatViewModel(repository)
			val combatant = combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12)

			viewModel.addParty(listOf(combatant))
			advanceUntilIdle()
			viewModel.addCondition(characterId = HERO_ID, conditionName = "Blessed", duration = 3)

			val updatedCombatant = viewModel.uiState.value.combatants.first()
			val condition = updatedCombatant.conditions.single()
			assertEquals("Blessed", condition.name)
			assertEquals(3, condition.duration)
			assertEquals(1, condition.addedOnRound)
			assertTrue(

				viewModel.uiState.value.activeStatuses.last()

					.contains("$HERO_NAME is now Blessed (3 rounds)")

			)
		}
	}

	@Test
	fun removeCondition_removesConditionAndLogsStatus() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createCombatViewModel(repository)
			val combatant = combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12)

			viewModel.addParty(listOf(combatant))
			advanceUntilIdle()
			viewModel.addCondition(

				characterId = HERO_ID,
				conditionName = "Restrained",
				duration = 2

			)
			advanceUntilIdle()

			viewModel.removeCondition(characterId = HERO_ID, conditionName = "Restrained")

			assertTrue(viewModel.uiState.value.combatants.first().conditions.isEmpty())
			assertTrue(

				viewModel.uiState.value.activeStatuses.last()

					.contains("$HERO_NAME is no longer Restrained")

			)
		}
	}

	@Test
	fun removeCombatant_normalizesTurnStateWhenCurrentCombatantIsRemoved() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createCombatViewModel(repository)
			val hero = combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12)
			val goblin = combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 10, hp = 7)

			viewModel.addParty(listOf(hero, goblin))
			advanceUntilIdle()
			viewModel.nextTurn()
			viewModel.selectAction("Heal")
			advanceUntilIdle()

			viewModel.removeCombatant(GOBLIN_ID)
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals(listOf(HERO_ID), combatants.map { it.characterId })
				assertEquals(0, currentTurnIndex)
				assertEquals(CombatTurnStep.SELECT_ACTION, turnStep)
				assertEquals(null, pendingAction)
				assertEquals(null, selectedTargetId)
			}
		}
	}

	@Test
	fun addParty_recalculatesEncounterDifficultyFromAvailableCharacters() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val hero = CharacterEntry(
				id = HERO_ID,
				name = "Aria",
				party = "Adventurers",
				level = 3,
				hp = 20,
				maxHp = 20
			)
			val monster = CharacterEntry(
				id = MONSTER_ID,
				name = "Ogre",
				party = "Monsters",
				level = 1,
				challengeRating = 2.0,
				hp = 59,
				maxHp = 59
			)
			repository.setCharacters(listOf(hero, monster))
			val viewModel = createCombatViewModel(repository)
			advanceUntilIdle()

			viewModel.addParty(
				listOf(
					CombatantState(

						characterId = hero.id,
						name = hero.name,
						initiative = 14,
						currentHp = hero.hp,
						maxHp = hero.maxHp

					),
					CombatantState(

						characterId = monster.id,
						name = monster.name,
						initiative = 8,
						currentHp = monster.hp,
						maxHp = monster.maxHp

					)
				)
			)
			advanceUntilIdle()

			val difficulty = viewModel.uiState.value.encounterDifficulty
			assertNotNull(difficulty)
			assertEquals(1, difficulty?.partySize)
			assertEquals(1, difficulty?.monsterCount)
			assertEquals(450, difficulty?.totalMonsterXp)
			assertEquals(DifficultyRating.BEYOND_DEADLY, difficulty?.rating)
		}
	}

	@Test
	fun loadEncounter_normalizesOutOfBoundsTurnIndexFromStoredEncounter() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			repository.setEncounter(
				Encounter(
					id = "encounter-invalid-index",
					name = "Invalid Index Encounter",
					status = EncounterStatus.ACTIVE,
					currentTurnIndex = 5,
					currentRound = 2,
					participants = listOf(
						combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12),
						combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 10, hp = 7)
					)
				)
			)
			val viewModel = createCombatViewModel(repository)

			viewModel.loadEncounter("encounter-invalid-index")
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals(1, currentTurnIndex)
				assertEquals(GOBLIN_ID, combatants[currentTurnIndex].characterId)
				assertEquals(2, currentRound)
			}
		}
	}

	@Test
	fun selectAction_movesTurnIntoTargetSelection() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			repository.setCharacters(
				listOf(
					CharacterEntry(
						id = HERO_ID,
						name = HERO_NAME,
						party = "Adventurers",
						actions = listOf(CharacterAction(name = "Strike", isAttack = true))
					)
				)
			)
			val viewModel = createCombatViewModel(repository)
			val hero = combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12)
			val goblin = combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 10, hp = 7)

			viewModel.addParty(listOf(hero, goblin))
			advanceUntilIdle()
			viewModel.selectAction("Strike")

			with(viewModel.uiState.value) {
				assertEquals(CombatTurnStep.SELECT_TARGET, turnStep)
				assertEquals("Strike", pendingAction?.name)
				assertEquals(null, selectedTargetId)
			}
		}
	}

	@Test
	fun applyActionResult_damageUpdatesTargetAndReadiesTurnEnd() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			repository.setCharacters(
				listOf(
					CharacterEntry(
						id = HERO_ID,
						name = HERO_NAME,
						party = "Adventurers",
						actions = listOf(CharacterAction(name = "Strike", isAttack = true))
					)
				)
			)
			val viewModel = createCombatViewModel(repository)
			val hero = combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12)
			val goblin = combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 10, hp = 7)

			viewModel.addParty(listOf(hero, goblin))
			advanceUntilIdle()
			viewModel.selectAction("Strike")
			viewModel.selectTarget(GOBLIN_ID)
			viewModel.applyActionResult(ActionResolutionType.DAMAGE, 4)

			with(viewModel.uiState.value) {
				assertEquals(CombatTurnStep.READY_TO_END, turnStep)
				assertEquals(GOBLIN_ID, selectedTargetId)
				assertEquals(3, combatants.first { it.characterId == GOBLIN_ID }.currentHp)
				assertTrue(activeStatuses.any { it.contains("$HERO_NAME used Strike on $GOBLIN_NAME for 4 damage.") })
			}
		}
	}

	@Test
	fun addCondition_persistentConditionUpdatesCharacterState() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			repository.setCharacters(
				listOf(
					CharacterEntry(
						id = HERO_ID,
						name = HERO_NAME,
						party = "Adventurers"
					)
				)
			)
			val viewModel = createCombatViewModel(repository)
			viewModel.addParty(listOf(combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12)))
			advanceUntilIdle()

			viewModel.addCondition(
				characterId = HERO_ID,
				conditionName = "Exhaustion",
				duration = null,
				persistsAcrossEncounters = true
			)
			advanceUntilIdle()

			assertTrue(
				repository.getStoredCharacter(HERO_ID)?.activeConditions?.contains("Exhaustion") == true
			)
			assertTrue(
				viewModel.uiState.value.activeStatuses.any { it.contains("Exhaustion (persistent)") }
			)
		}
	}

	@Test
	fun removeCondition_persistentConditionClearsCharacterStateAndLogsStatus() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			repository.setCharacters(
				listOf(
					CharacterEntry(
						id = HERO_ID,
						name = HERO_NAME,
						party = "Adventurers",
						activeConditions = setOf("Exhaustion")
					)
				)
			)
			val viewModel = createCombatViewModel(repository)
			viewModel.addParty(listOf(combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12)))
			advanceUntilIdle()

			viewModel.removeCondition(characterId = HERO_ID, conditionName = "Exhaustion")
			advanceUntilIdle()

			assertTrue(
				repository.getStoredCharacter(HERO_ID)?.activeConditions?.contains("Exhaustion") == false
			)
			assertTrue(
				viewModel.uiState.value.activeStatuses.any { it.contains("$HERO_NAME is no longer Exhaustion") }
			)
		}
	}

	@Test
	fun addCondition_persistentConditionUpdatesUseLatestCharacterStateAcrossRapidWrites() {
		runTest {
			val repository = FakeCombatCampaignRepository().apply {
				updateCharacterDelayMillis = 1
				setCharacters(
					listOf(
						CharacterEntry(
							id = HERO_ID,
							name = HERO_NAME,
							party = "Adventurers"
						)
					)
				)
			}
			val viewModel = createCombatViewModel(repository)
			viewModel.addParty(listOf(combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12)))
			advanceUntilIdle()

			viewModel.addCondition(
				characterId = HERO_ID,
				conditionName = "Exhaustion",
				persistsAcrossEncounters = true
			)
			viewModel.addCondition(
				characterId = HERO_ID,
				conditionName = "Cursed",
				persistsAcrossEncounters = true
			)
			advanceUntilIdle()

			assertEquals(
				setOf("Exhaustion", "Cursed"),
				repository.getStoredCharacter(HERO_ID)?.activeConditions
			)
		}
	}

	@Test
	fun loadEncounter_pendingEncounterRestoresSetupState() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val draftCombatant = combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12)
			repository.setEncounter(
				Encounter(
					id = "encounter-draft",
					name = "Bridge Ambush",
					notes = "Fog covers the bridge.",
					status = EncounterStatus.PENDING,
					currentRound = 1,
					currentTurnIndex = 0
				)
			)
			repository.setSessions(
				encounterId = "encounter-draft",
				sessions = listOf(
					SessionRecord(
						encounterId = "encounter-draft",
						title = "Draft Setup",
						snapshot = EncounterSnapshot(
							combatants = listOf(draftCombatant),
							currentTurnIndex = 0,
							currentRound = 1
						)
					)
				)
			)
			val viewModel = createCombatViewModel(repository)

			viewModel.loadEncounter("encounter-draft")
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertFalse(isCombatActive)
				assertEquals("encounter-draft", currentEncounterId)
				assertEquals("Bridge Ambush", currentEncounterName)
				assertEquals("Fog covers the bridge.", encounterNotes)
				assertEquals(listOf(draftCombatant), combatants)
			}
		}
	}

	@Test
	fun startEncounter_existingEncounterPersistsSharedStateAndMarksActive() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			repository.setEncounter(
				Encounter(
					id = "encounter-existing",
					campaignId = "campaign-1",
					name = "Ruined Keep",
					status = EncounterStatus.PENDING
				)
			)
			val viewModel = createCombatViewModel(repository)
			val hero = combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12)

			viewModel.loadEncounter("encounter-existing")
			advanceUntilIdle()
			viewModel.addParty(listOf(hero))
			viewModel.updateNotes("Braziers line the walls.")
			viewModel.startEncounter("encounter-existing")
			advanceUntilIdle()

			val storedEncounter = repository.getStoredEncounter("encounter-existing")
			assertNotNull(storedEncounter)
			assertEquals(EncounterStatus.ACTIVE, storedEncounter?.status)
			assertEquals("Braziers line the walls.", storedEncounter?.notes)
			assertEquals(listOf(hero), storedEncounter?.participants)
			assertEquals("encounter-existing", repository.activeEncounterId)
			assertTrue(viewModel.uiState.value.isCombatActive)
		}
	}

	@Test
	fun saveAndPauseEncounter_persistsSnapshotAndClearsActiveEncounter() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			repository.setEncounter(
				Encounter(
					id = "encounter-active",
					campaignId = "campaign-1",
					name = "Boss Chamber",
					status = EncounterStatus.ACTIVE
				)
			)
			val viewModel = createCombatViewModel(repository)
			val hero = combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12)
			var completed = false

			viewModel.loadEncounter("encounter-active")
			advanceUntilIdle()
			viewModel.addParty(listOf(hero))
			viewModel.updateNotes("Final phase begins when the altar cracks.")
			viewModel.saveAndPauseEncounter { completed = true }
			advanceUntilIdle()

			val storedEncounter = repository.getStoredEncounter("encounter-active")
			val recentSession = repository.getRecentSession()
			assertTrue(completed)
			assertEquals(EncounterStatus.PENDING, storedEncounter?.status)
			assertEquals("Final phase begins when the altar cracks.", storedEncounter?.notes)
			assertEquals(null, repository.activeEncounterId)
			assertNotNull(recentSession)
			assertEquals("Boss Chamber", recentSession?.title)
			assertEquals(listOf(hero), recentSession?.snapshot?.combatants)
		}
	}

	@Test
	fun saveAndPauseEncounter_reloadRestoresHpTurnRoundAndConditionsFromSnapshot() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			repository.setEncounter(
				Encounter(
					id = "encounter-restore",
					campaignId = "campaign-1",
					name = "Ruined Gate",
					status = EncounterStatus.PENDING
				)
			)
			val initialViewModel = createCombatViewModel(repository)
			val hero = combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12)
			val goblin = combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 10, hp = 7)
			var completed = false

			initialViewModel.loadEncounter("encounter-restore")
			advanceUntilIdle()
			initialViewModel.addParty(listOf(hero, goblin))
			initialViewModel.updateNotes("Rain turns the stones slick.")
			initialViewModel.startEncounter("encounter-restore")
			advanceUntilIdle()
			initialViewModel.updateCombatantHp(GOBLIN_ID, -3)
			initialViewModel.nextTurn()
			initialViewModel.addCondition(GOBLIN_ID, "Poisoned", duration = 2)
			advanceUntilIdle()
			initialViewModel.saveAndPauseEncounter { completed = true }
			advanceUntilIdle()

			assertTrue(completed)
			val reloadedViewModel = createCombatViewModel(repository)
			reloadedViewModel.loadEncounter("encounter-restore")
			advanceUntilIdle()

			with(reloadedViewModel.uiState.value) {
				val restoredHero = combatants.first { it.characterId == HERO_ID }
				val restoredGoblin = combatants.first { it.characterId == GOBLIN_ID }
				val restoredCondition = restoredGoblin.conditions.single()

				assertFalse(isCombatActive)
				assertEquals(EncounterLifecycle.PAUSED, encounterLifecycle)
				assertEquals("encounter-restore", currentEncounterId)
				assertEquals("Ruined Gate", currentEncounterName)
				assertEquals("Rain turns the stones slick.", encounterNotes)
				assertEquals(1, currentTurnIndex)
				assertEquals(1, currentRound)
				assertEquals(12, restoredHero.currentHp)
				assertEquals(4, restoredGoblin.currentHp)
				assertEquals("Poisoned", restoredCondition.name)
				assertEquals(2, restoredCondition.duration)
				assertEquals(1, restoredCondition.addedOnRound)
				assertTrue(activeStatuses.any { it.contains("$GOBLIN_NAME takes 3 damage (4/7 HP)") })
				assertTrue(activeStatuses.any { it.contains("$GOBLIN_NAME is now Poisoned (2 rounds)") })
			}
		}
	}

	@Test
	fun nextTurn_expiresRoundDurationExactlyOnExpectedRoundAdvance() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createCombatViewModel(repository)
			val hero = combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12)
			val goblin = combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 10, hp = 7)

			viewModel.addParty(listOf(hero, goblin))
			advanceUntilIdle()
			viewModel.addCondition(characterId = HERO_ID, conditionName = "Blessed", duration = 2)
			advanceUntilIdle()

			viewModel.nextTurn()
			with(viewModel.uiState.value) {
				assertEquals(1, currentTurnIndex)
				assertEquals(1, currentRound)
				assertEquals(2, combatants.first { it.characterId == HERO_ID }.conditions.single().duration)
				assertFalse(activeStatuses.any { it.contains("Blessed condition has expired") })
			}

			viewModel.nextTurn()
			with(viewModel.uiState.value) {
				assertEquals(0, currentTurnIndex)
				assertEquals(2, currentRound)
				assertEquals(1, combatants.first { it.characterId == HERO_ID }.conditions.single().duration)
				assertFalse(activeStatuses.any { it.contains("Blessed condition has expired") })
			}

			viewModel.nextTurn()
			viewModel.nextTurn()
			with(viewModel.uiState.value) {
				assertEquals(0, currentTurnIndex)
				assertEquals(3, currentRound)
				assertTrue(combatants.first { it.characterId == HERO_ID }.conditions.isEmpty())
				assertTrue(activeStatuses.any { it.contains("$HERO_NAME's Blessed condition has expired") })
				assertTrue(activeStatuses.any { it.contains("Round 3 begins") })
			}
		}
	}

	@Test
	fun loadEncounter_restoresRoundTurnAndCombatantsFromSnapshot() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val encounter = Encounter(
				id = "encounter-restore",
				name = "Ruined Keep",
				status = EncounterStatus.ACTIVE
			)
			val snapshotCombatants = listOf(
				combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 9),
				combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 10, hp = 3)
			)
			val session = SessionRecord(
				encounterId = encounter.id,
				title = "Saved encounter",
				log = listOf("$HERO_NAME uses Strike!"),
				snapshot = EncounterSnapshot(
					combatants = snapshotCombatants,
					currentTurnIndex = 1,
					currentRound = 4
				)
			)

			repository.setEncounter(encounter)
			repository.setSessionsForEncounter(encounter.id, listOf(session))
			val viewModel = createCombatViewModel(repository)
			advanceUntilIdle()

			viewModel.loadEncounter(encounter.id)
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertTrue(isCombatActive)
				assertEquals(encounter.id, currentEncounterId)
				assertEquals(snapshotCombatants, combatants)
				assertEquals(1, currentTurnIndex)
				assertEquals(4, currentRound)
				assertEquals(session.log, activeStatuses)
			}
		}
	}
}
