/*
 * FILE: CombatViewModelTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Turn progression and condition lifecycle behavior
 * 2. Status-log and condition mutation coverage
 * 3. Encounter difficulty recalculation
 * 4. Fake repository support helpers
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.MainDispatcherRule
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.CharacterAction
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterGenerationSettings
import io.github.velyene.loreweaver.domain.model.EncounterSnapshot
import io.github.velyene.loreweaver.domain.model.EncounterStatus
import io.github.velyene.loreweaver.domain.model.LogEntry
import io.github.velyene.loreweaver.domain.model.Note
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.repository.CampaignRepository
import io.github.velyene.loreweaver.domain.use_case.GetActiveEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCharactersUseCase
import io.github.velyene.loreweaver.domain.use_case.GetEncounterByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetSessionsForEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertLogUseCase
import io.github.velyene.loreweaver.domain.use_case.InsertSessionRecordUseCase
import io.github.velyene.loreweaver.domain.use_case.SetActiveEncounterUseCase
import io.github.velyene.loreweaver.domain.use_case.UpdateCharacterUseCase
import io.github.velyene.loreweaver.domain.util.DifficultyRating
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CombatViewModelTest {

	private companion object {
		const val HERO_ID = "hero-1"
		const val HERO_NAME = "Hero"
		const val GOBLIN_ID = "goblin-1"
		const val GOBLIN_NAME = "Goblin"
		const val MONSTER_ID = "monster-1"
	}

	@get:Rule
	val mainDispatcherRule = MainDispatcherRule()

	@Test
	fun nextTurn_advancesToNextCombatantWithinRound() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createViewModel(repository)
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
				assertTrue(activeStatuses.any { it.contains("$HERO_NAME ended turn.") })
			}
		}
	}

	@Test
	fun nextTurn_wrapsRound_decrementsAndExpiresConditions() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createViewModel(repository)
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
				assertTrue(activeStatuses.any { it.contains("$GOBLIN_NAME ended turn.") })
				assertTrue(activeStatuses.any { it.contains("$HERO_NAME's Poisoned condition has expired") })
				assertTrue(activeStatuses.any { it.contains("Round 2 begins") })
			}
		}
	}

	@Test
	fun previousTurn_restoresPriorTurnSnapshotIncludingHpAndStatuses() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createViewModel(repository)
			val hero = combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12)
			val goblin = combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 10, hp = 7)

			viewModel.addParty(listOf(hero, goblin))
			advanceUntilIdle()
			viewModel.startEncounter()
			advanceUntilIdle()
			viewModel.updateCombatantHp(GOBLIN_ID, -3)
			advanceUntilIdle()
			viewModel.nextTurn()
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals(1, currentTurnIndex)
				assertTrue(canGoToPreviousTurn)
				assertEquals(4, combatants.first { it.characterId == GOBLIN_ID }.currentHp)
			}

			viewModel.previousTurn()
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals(0, currentTurnIndex)
				assertEquals(1, currentRound)
				assertEquals(7, combatants.first { it.characterId == GOBLIN_ID }.currentHp)
				assertFalse(activeStatuses.any { it.contains("$GOBLIN_NAME takes 3 damage") })
				assertFalse(canGoToPreviousTurn)
			}
		}
	}

	@Test
	fun previousTurn_restoresRoundAfterManualAdvance() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createViewModel(repository)

			viewModel.addParty(listOf(combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12)))
			advanceUntilIdle()
			viewModel.startEncounter()
			advanceUntilIdle()
			viewModel.advanceRound()
			advanceUntilIdle()

			assertEquals(2, viewModel.uiState.value.currentRound)
			assertTrue(viewModel.uiState.value.activeStatuses.any { it.contains("DM advanced to round 2") })

			viewModel.previousTurn()
			advanceUntilIdle()

			assertEquals(1, viewModel.uiState.value.currentRound)
			assertFalse(viewModel.uiState.value.activeStatuses.any { it.contains("DM advanced to round 2") })
			assertFalse(viewModel.uiState.value.canGoToPreviousTurn)
		}
	}

	@Test
	fun updateCombatantHp_logsDamageAndDefeatedUnitEvent() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createViewModel(repository)
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
	fun updateCombatantHp_consumesTempHpBeforeReducingHp() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createViewModel(repository)
			val combatant = CombatantState(
				characterId = HERO_ID,
				name = HERO_NAME,
				initiative = 15,
				currentHp = 12,
				maxHp = 12,
				tempHp = 5
			)

			viewModel.addParty(listOf(combatant))
			advanceUntilIdle()
			viewModel.updateCombatantHp(HERO_ID, -7)

			with(viewModel.uiState.value.combatants.first()) {
				assertEquals(10, currentHp)
				assertEquals(0, tempHp)
			}
		}
	}

	@Test
	fun setCombatantHp_setsExactHpAndClearsTempHp() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createViewModel(repository)
			val combatant = CombatantState(
				characterId = HERO_ID,
				name = HERO_NAME,
				initiative = 15,
				currentHp = 12,
				maxHp = 12,
				tempHp = 4,
			)

			viewModel.addParty(listOf(combatant))
			advanceUntilIdle()
			viewModel.setCombatantHp(HERO_ID, 3)

			with(viewModel.uiState.value.combatants.first()) {
				assertEquals(3, currentHp)
				assertEquals(0, tempHp)
			}
			assertTrue(viewModel.uiState.value.activeStatuses.any { it.contains("$HERO_NAME HP set to 3/12") })
		}
	}

	@Test
	fun addParticipantNote_updatesLogAndPersistedCharacterNotes() {
		runTest {
			val repository = FakeCombatCampaignRepository().apply {
				setCharacters(
					listOf(
						CharacterEntry(
							id = HERO_ID,
							name = HERO_NAME,
							party = "Adventurers",
							hp = 12,
							maxHp = 12,
							initiative = 2,
						)
					)
				)
			}
			val viewModel = createViewModel(repository)

			viewModel.addParty(listOf(combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12)))
			advanceUntilIdle()
			viewModel.addParticipantNote(HERO_ID, "Holding the bridge line")
			advanceUntilIdle()

			assertTrue(viewModel.uiState.value.activeStatuses.any { it.contains("Note — $HERO_NAME: Holding the bridge line") })
			assertTrue(repository.getStoredCharacter(HERO_ID)?.notes?.contains("Holding the bridge line") == true)
		}
	}

	@Test
	fun duplicateEnemy_addsFreshNumberedEnemyCopy() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createViewModel(repository)

			viewModel.addEnemies(name = GOBLIN_NAME, hp = 7, initiative = 10, quantity = 2)
			advanceUntilIdle()
			val originalIds = viewModel.uiState.value.combatants.map { it.characterId }.toSet()

			val firstGoblinId = viewModel.uiState.value.combatants.first().characterId
			viewModel.duplicateEnemy(firstGoblinId)
			advanceUntilIdle()

			val combatants = viewModel.uiState.value.combatants
			assertEquals(3, combatants.size)
			assertTrue(combatants.any { it.name == "Goblin 3" && it.currentHp == 7 && it.characterId !in originalIds })
		}
	}

	@Test
	fun advanceRound_incrementsRoundAndResetsToFirstLivingCombatant() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createViewModel(repository)

			viewModel.addParty(
				listOf(
					combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12),
					combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 10, hp = 7),
				)
			)
			advanceUntilIdle()
			viewModel.nextTurn()
			advanceUntilIdle()

			viewModel.advanceRound()
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals(2, currentRound)
				assertEquals(0, currentTurnIndex)
				assertTrue(activeStatuses.any { it.contains("DM advanced to round 2") })
			}
		}
	}

	@Test
	fun addCondition_addsConditionAndStatusMessage() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createViewModel(repository)
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
			val viewModel = createViewModel(repository)
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
			val viewModel = createViewModel(repository)
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
	fun nextTurn_skipsDefeatedCombatantsInTurnOrder() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createViewModel(repository)
			val hero = combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12)
			val defeatedGoblin = combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 10, hp = 0)
			val wolf = combatant(id = "wolf-1", name = "Wolf", initiative = 8, hp = 11)

			viewModel.addParty(listOf(hero, defeatedGoblin, wolf))
			advanceUntilIdle()
			viewModel.nextTurn()

			with(viewModel.uiState.value) {
				assertEquals(2, currentTurnIndex)
				assertEquals("wolf-1", combatants[currentTurnIndex].characterId)
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
			val viewModel = createViewModel(repository)
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
			val viewModel = createViewModel(repository)

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
			val viewModel = createViewModel(repository)
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
			val viewModel = createViewModel(repository)
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
	fun applyActionResult_castSpellSpendsSpellSlotForActor() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			repository.setCharacters(
				listOf(
					CharacterEntry(
						id = HERO_ID,
						name = HERO_NAME,
						party = "Adventurers",
						spellSlots = mapOf(1 to (2 to 2)),
						actions = listOf(CharacterAction(name = "Cast Spell", isAttack = false, spellSlotLevel = 1))
					)
				)
			)
			val viewModel = createViewModel(repository)
			val hero = combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12)
			val goblin = combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 10, hp = 7)

			viewModel.addParty(listOf(hero, goblin))
			advanceUntilIdle()
			viewModel.selectAction("Cast Spell")
			viewModel.selectTarget(GOBLIN_ID)
			viewModel.applyActionResult(ActionResolutionType.DAMAGE, 4)
			advanceUntilIdle()

			assertEquals(1, repository.getStoredCharacter(HERO_ID)?.spellSlots?.get(1)?.first)
		}
	}

	@Test
	fun applyActionResult_useAbilitySpendsNamedResource() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			repository.setCharacters(
				listOf(
					CharacterEntry(
						id = HERO_ID,
						name = HERO_NAME,
						party = "Adventurers",
						resources = listOf(io.github.velyene.loreweaver.domain.model.CharacterResource("Ki", 2, 2)),
						actions = listOf(CharacterAction(name = "Use Ability", isAttack = false, resourceName = "Ki", resourceCost = 1))
					)
				)
			)
			val viewModel = createViewModel(repository)
			val hero = combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12)
			val goblin = combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 10, hp = 7)

			viewModel.addParty(listOf(hero, goblin))
			advanceUntilIdle()
			viewModel.selectAction("Use Ability")
			viewModel.selectTarget(HERO_ID)
			viewModel.applyActionResult(ActionResolutionType.HEAL, 3)
			advanceUntilIdle()

			assertEquals(1, repository.getStoredCharacter(HERO_ID)?.resources?.single { it.name == "Ki" }?.current)
		}
	}

	@Test
	fun nextTurn_enemyTurnAutoResolvesAgainstLowestHpPlayer() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			repository.setCharacters(
				listOf(
					CharacterEntry(id = HERO_ID, name = HERO_NAME, party = "Adventurers"),
					CharacterEntry(id = "hero-2", name = "Scout", party = "Adventurers"),
					CharacterEntry(
						id = GOBLIN_ID,
						name = GOBLIN_NAME,
						party = "Monsters",
						actions = listOf(CharacterAction(name = "Slash", damageDice = "1d4+2", isAttack = true))
					)
				)
			)
			val viewModel = createViewModel(repository)

			viewModel.addParty(
				listOf(
					combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12),
					combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 12, hp = 7),
					combatant(id = "hero-2", name = "Scout", initiative = 9, hp = 6)
				)
			)
			advanceUntilIdle()
			viewModel.startEncounter()
			advanceUntilIdle()
			viewModel.nextTurn()
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals("hero-2", combatants[currentTurnIndex].characterId)
				assertEquals(2, combatants.first { it.characterId == "hero-2" }.currentHp)
				assertTrue(activeStatuses.any { it.contains("$GOBLIN_NAME used Slash on Scout for 4 damage.") })
			}
		}
	}

	@Test
	fun nextTurn_enemyTurnPrefersHigherDamageAffordableAttack() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			repository.setCharacters(
				listOf(
					CharacterEntry(id = HERO_ID, name = HERO_NAME, party = "Adventurers"),
					CharacterEntry(
						id = GOBLIN_ID,
						name = GOBLIN_NAME,
						party = "Monsters",
						mana = 2,
						maxMana = 2,
						actions = listOf(
							CharacterAction(name = "Slash", damageDice = "1d4+1", isAttack = true),
							CharacterAction(name = "Shadow Bolt", damageDice = "1d10", isAttack = true, manaCost = 2)
						)
					)
				)
			)
			val viewModel = createViewModel(repository)

			viewModel.addParty(
				listOf(
					combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12),
					combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 12, hp = 7)
				)
			)
			advanceUntilIdle()
			viewModel.startEncounter()
			advanceUntilIdle()
			viewModel.nextTurn()
			advanceUntilIdle()

			assertEquals(8, viewModel.uiState.value.combatants.first { it.characterId == HERO_ID }.currentHp)
		}
	}

	@Test
	fun updateCombatantInitiative_reordersCombatantsWhenEncounterStarts() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createViewModel(repository)
			val hero = combatant(id = HERO_ID, name = HERO_NAME, initiative = 12, hp = 12)
			val goblin = combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 9, hp = 7)

			viewModel.addParty(listOf(hero, goblin))
			advanceUntilIdle()
			viewModel.updateCombatantInitiative(GOBLIN_ID, 18)
			viewModel.updateInitiativeMode(InitiativeMode.AUTO_SORT)
			viewModel.startEncounter()
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals(listOf(GOBLIN_ID, HERO_ID), combatants.map { it.characterId })
				assertEquals(18, combatants.first().initiative)
			}
		}
	}

	@Test
	fun moveCombatantUp_swapsVisibleOrderAndInitiativeWithPreviousCombatant() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createViewModel(repository)
			val hero = combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12)
			val goblin = combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 10, hp = 7)
			val wolf = combatant(id = "wolf-1", name = "Wolf", initiative = 7, hp = 11)

			viewModel.addParty(listOf(hero, goblin, wolf))
			advanceUntilIdle()
			viewModel.moveCombatantUp(GOBLIN_ID)
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals(listOf(GOBLIN_ID, HERO_ID, "wolf-1"), combatants.map { it.characterId })
				assertEquals(listOf(15, 10, 7), combatants.map { it.initiative })
			}
		}
	}

	@Test
	fun moveCombatantDown_bottomCombatantIsNoOp() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createViewModel(repository)
			val hero = combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12)
			val goblin = combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 10, hp = 7)

			viewModel.addParty(listOf(hero, goblin))
			advanceUntilIdle()
			viewModel.moveCombatantDown(GOBLIN_ID)
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals(listOf(HERO_ID, GOBLIN_ID), combatants.map { it.characterId })
				assertEquals(listOf(15, 10), combatants.map { it.initiative })
			}
		}
	}

	@Test
	fun addEnemies_batchesAutoNumberedEnemiesForSharedSetupFlow() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createViewModel(repository)

			viewModel.addEnemies(name = GOBLIN_NAME, hp = 7, initiative = 14, quantity = 3)
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals(3, combatants.size)
				assertEquals(listOf("Goblin 1", "Goblin 2", "Goblin 3"), combatants.map { it.name })
				assertTrue(combatants.all { it.initiative == 14 && it.currentHp == 7 && it.maxHp == 7 })
			}
		}
	}

	@Test
	fun loadEncounter_startFresh_hydratesEnemyLibraryStagingIntoSetupRoster() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createViewModel(repository)

			viewModel.loadEncounter(
				startFresh = true,
				stagedEnemies = listOf(
					StagedEnemyItem(
						key = "goblin",
						catalogKey = "goblin",
						name = "Goblin",
						hp = 7,
						initiative = 2,
						quantity = 2,
					),
					StagedEnemyItem(
						key = "temp:clockwork-hound:18:3",
						name = "Clockwork Hound",
						hp = 18,
						initiative = 3,
						quantity = 1,
						isTemporary = true,
					),
				),
			)
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertFalse(isCombatActive)
				assertEquals(listOf("Goblin 1", "Goblin 2", "Clockwork Hound"), combatants.map { it.name })
				assertEquals(listOf(7, 7, 18), combatants.map { it.currentHp })
				assertEquals(listOf(2, 2, 3), combatants.map { it.initiative })
				assertEquals(EncounterLifecycle.DRAFT, encounterLifecycle)
			}
		}
	}

	@Test
	fun loadEncounter_startFresh_clearsPreviouslyLoadedEncounterStateBeforeApplyingLibraryDraft() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			repository.setEncounter(
				Encounter(
					id = "encounter-stale",
					name = "Stale Encounter",
					notes = "Old battlefield notes.",
					status = EncounterStatus.ACTIVE,
					currentRound = 3,
					participants = listOf(
						combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12),
					),
				),
			)
			val viewModel = createViewModel(repository)

			viewModel.loadEncounter("encounter-stale")
			advanceUntilIdle()
			assertEquals("encounter-stale", viewModel.uiState.value.currentEncounterId)

			viewModel.loadEncounter(
				startFresh = true,
				stagedEnemies = listOf(
					StagedEnemyItem(
						key = "temp:ash-zombie:22:1",
						name = "Ash Zombie",
						hp = 22,
						initiative = 1,
						quantity = 1,
						isTemporary = true,
					),
				),
			)
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertFalse(isCombatActive)
				assertEquals(null, currentEncounterId)
				assertEquals("", currentEncounterName)
				assertEquals("", encounterNotes)
				assertEquals(EncounterLifecycle.DRAFT, encounterLifecycle)
				assertEquals(listOf("Ash Zombie"), combatants.map { it.name })
				assertEquals(listOf(22), combatants.map { it.currentHp })
				assertTrue(activeStatuses.isEmpty())
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
			val viewModel = createViewModel(repository)
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
			val viewModel = createViewModel(repository)
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
			val viewModel = createViewModel(repository)

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
			val viewModel = createViewModel(repository)
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
			val viewModel = createViewModel(repository)
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
			assertFalse(recentSession?.isCompleted == true)
			assertEquals(null, recentSession?.encounterResult)
		}
	}

	@Test
	fun endEncounter_persistsCompletedSessionMetadataAndClearsActiveEncounter() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			repository.setEncounter(
				Encounter(
					id = "encounter-complete",
					campaignId = "campaign-1",
					name = "Final Stand",
					status = EncounterStatus.ACTIVE
				)
			)
			repository.setCharacters(
				listOf(
					CharacterEntry(id = HERO_ID, name = HERO_NAME, party = "Adventurers"),
					CharacterEntry(id = GOBLIN_ID, name = GOBLIN_NAME, party = "Monsters", challengeRating = 0.25)
				)
			)
			val viewModel = createViewModel(repository)
			var completed = false

			viewModel.loadEncounter("encounter-complete")
			advanceUntilIdle()
			viewModel.addParty(
				listOf(
					combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12),
					combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 10, hp = 0)
				)
			)
			advanceUntilIdle()
			viewModel.endEncounter { completed = true }
			advanceUntilIdle()

			val recentSession = repository.getRecentSession()
			assertTrue(completed)
			assertEquals(null, repository.activeEncounterId)
			assertTrue(recentSession?.isCompleted == true)
			assertEquals("VICTORY", recentSession?.encounterResult)
			assertEquals(50, recentSession?.rewards?.experiencePoints)
			assertEquals(1, recentSession?.rewards?.participantCount)
			assertEquals("3 gp, 7 sp, 5 cp", recentSession?.rewards?.currencyReward)
			assertTrue(recentSession?.rewards?.rewardLog?.any { it.contains("Final Stand marked as a victory") } == true)
		}
	}

	@Test
	fun endEncounter_usesDmSelectedResultOverrideWhenProvided() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			repository.setEncounter(
				Encounter(
					id = "encounter-choice",
					campaignId = "campaign-1",
					name = "Bridge Retreat",
					status = EncounterStatus.ACTIVE
				)
			)
			repository.setCharacters(
				listOf(
					CharacterEntry(id = HERO_ID, name = HERO_NAME, party = "Adventurers"),
					CharacterEntry(id = GOBLIN_ID, name = GOBLIN_NAME, party = "Monsters", challengeRating = 0.25)
				)
			)
			val viewModel = createViewModel(repository)

			viewModel.loadEncounter("encounter-choice")
			advanceUntilIdle()
			viewModel.addParty(
				listOf(
					combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 10),
					combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 10, hp = 0)
				)
			)
			advanceUntilIdle()
			viewModel.endEncounter(result = EncounterResult.DEFEAT)
			advanceUntilIdle()

			val recentSession = repository.getRecentSession()
			assertEquals("DEFEAT", recentSession?.encounterResult)
			assertTrue(recentSession?.isCompleted == true)
			assertEquals(null, repository.activeEncounterId)
		}
	}

	@Test
	fun endEncounterToSummary_returnsPersistedCompletedSessionId() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			repository.setEncounter(
				Encounter(
					id = "encounter-summary-route",
					campaignId = "campaign-1",
					name = "Tower Escape",
					status = EncounterStatus.ACTIVE,
				)
			)
			repository.setCharacters(
				listOf(
					CharacterEntry(id = HERO_ID, name = HERO_NAME, party = "Adventurers"),
					CharacterEntry(id = GOBLIN_ID, name = GOBLIN_NAME, party = "Monsters", challengeRating = 0.25),
				)
			)
			val viewModel = createViewModel(repository)
			var completedSessionId: String? = null

			viewModel.loadEncounter("encounter-summary-route")
			advanceUntilIdle()
			viewModel.addParty(
				listOf(
					combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 10),
					combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 10, hp = 0),
				)
			)
			advanceUntilIdle()
			viewModel.endEncounterToSummary(result = EncounterResult.VICTORY) { sessionId ->
				completedSessionId = sessionId
			}
			advanceUntilIdle()

			val recentSession = repository.getRecentSession()
			assertNotNull(recentSession)
			assertEquals(recentSession?.id, completedSessionId)
			assertEquals("VICTORY", recentSession?.encounterResult)
		}
	}

	@Test
	fun resolvedCombatState_doesNotAutoCompleteUntilDmEndsEncounter() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			repository.setEncounter(
				Encounter(
					id = "encounter-waiting",
					campaignId = "campaign-1",
					name = "Hallway Skirmish",
					status = EncounterStatus.ACTIVE
				)
			)
			repository.setCharacters(
				listOf(
					CharacterEntry(id = HERO_ID, name = HERO_NAME, party = "Adventurers"),
					CharacterEntry(id = GOBLIN_ID, name = GOBLIN_NAME, party = "Monsters", challengeRating = 0.25)
				)
			)
			val viewModel = createViewModel(repository)

			viewModel.loadEncounter("encounter-waiting")
			advanceUntilIdle()
			viewModel.addParty(
				listOf(
					combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 10),
					combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 10, hp = 0)
				)
			)
			advanceUntilIdle()

			assertEquals("encounter-waiting", repository.activeEncounterId)
			assertNull(repository.getRecentSession())
			assertTrue(viewModel.uiState.value.isCombatActive)
			assertEquals(EncounterLifecycle.ACTIVE, viewModel.uiState.value.encounterLifecycle)
		}
	}

	@Test
	fun updateLocationTerrain_andNotes_encodeEncounterInfoWithoutChangingPersistenceSchema() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			repository.setEncounter(
				Encounter(
					id = "encounter-encoded-notes",
					name = "Ashen Ruins",
					status = EncounterStatus.PENDING,
				)
			)
			val viewModel = createViewModel(repository)

			viewModel.loadEncounter("encounter-encoded-notes")
			advanceUntilIdle()
			viewModel.addParty(listOf(combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12)))
			viewModel.updateLocationTerrain("Broken stairs, narrow approach")
			viewModel.updateNotes("Low visibility and falling ash.")
			viewModel.startEncounter("encounter-encoded-notes")
			advanceUntilIdle()

			val storedEncounter = requireNotNull(repository.getStoredEncounter("encounter-encoded-notes"))
			val parsed = parseEncounterInfo(storedEncounter.notes)

			assertEquals("Broken stairs, narrow approach", parsed.locationTerrain)
			assertEquals("Low visibility and falling ash.", parsed.notesBody)
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
			val initialViewModel = createViewModel(repository)
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
			val reloadedViewModel = createViewModel(repository)
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
	fun loadEncounter_completedSessionRestoresCompletedLifecycleInsteadOfPaused() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val finalSnapshotCombatant = combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 6)
			repository.setEncounter(
				Encounter(
					id = "encounter-completed",
					name = "Collapsed Bridge",
					notes = "The bridge finally breaks apart.",
					status = EncounterStatus.PENDING
				)
			)
			repository.setSessions(
				encounterId = "encounter-completed",
				sessions = listOf(
					SessionRecord(
						encounterId = "encounter-completed",
						title = "Completed Encounter",
						snapshot = EncounterSnapshot(
							combatants = listOf(finalSnapshotCombatant),
							currentTurnIndex = 0,
							currentRound = 4
						),
						isCompleted = true,
						encounterResult = "ENDED_EARLY"
					)
				)
			)
			val viewModel = createViewModel(repository)

			viewModel.loadEncounter("encounter-completed")
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertFalse(isCombatActive)
				assertEquals(EncounterLifecycle.COMPLETED, encounterLifecycle)
				assertEquals(listOf(finalSnapshotCombatant), combatants)
				assertEquals("The bridge finally breaks apart.", encounterNotes)
			}
		}
	}

	@Test
	fun loadEncounter_withoutActiveEncounterAfterPreviouslyLoadedEncounter_clearsStaleTrackerState() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			repository.setEncounter(
				Encounter(
					id = "encounter-active",
					name = "Clocktower Siege",
					status = EncounterStatus.ACTIVE,
					currentRound = 3,
					participants = listOf(
						combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12),
						combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 10, hp = 7)
					)
				)
			)
			val viewModel = createViewModel(repository)

			viewModel.loadEncounter()
			advanceUntilIdle()
			assertEquals("encounter-active", viewModel.uiState.value.currentEncounterId)
			assertTrue(viewModel.uiState.value.isCombatActive)

			repository.setActiveEncounter("")
			viewModel.loadEncounter()
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertFalse(isCombatActive)
				assertEquals(null, currentEncounterId)
				assertEquals("", currentEncounterName)
				assertEquals(EncounterLifecycle.DRAFT, encounterLifecycle)
				assertTrue(combatants.isEmpty())
				assertEquals(1, currentRound)
				assertEquals(0, currentTurnIndex)
				assertTrue(activeStatuses.isEmpty())
				assertEquals(null, encounterDifficulty)
				assertEquals(null, error)
			}
		}
	}

	@Test
	fun loadEncounter_afterColdRelaunch_restoresPersistedActiveCheckpoint() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			repository.setEncounter(
				Encounter(
					id = "encounter-live",
					campaignId = "campaign-1",
					name = "Ruined Gate",
					status = EncounterStatus.PENDING
				)
			)
			val initialViewModel = createViewModel(repository)
			val hero = combatant(id = HERO_ID, name = HERO_NAME, initiative = 15, hp = 12)
			val goblin = combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 10, hp = 7)

			initialViewModel.loadEncounter("encounter-live")
			advanceUntilIdle()
			initialViewModel.addParty(listOf(hero, goblin))
			initialViewModel.startEncounter("encounter-live")
			advanceUntilIdle()
			initialViewModel.updateCombatantHp(GOBLIN_ID, -3)
			initialViewModel.addCondition(GOBLIN_ID, "Poisoned", duration = 2)
			initialViewModel.updateTempHp(HERO_ID, 5)
			initialViewModel.nextTurn()
			initialViewModel.updateNotes("Rain turns the stones slick.")
			advanceUntilIdle()

			val relaunchedViewModel = createViewModel(repository)
			relaunchedViewModel.loadEncounter()
			advanceUntilIdle()

			with(relaunchedViewModel.uiState.value) {
				val restoredHero = combatants.first { it.characterId == HERO_ID }
				val restoredGoblin = combatants.first { it.characterId == GOBLIN_ID }

				assertTrue(isCombatActive)
				assertEquals(EncounterLifecycle.ACTIVE, encounterLifecycle)
				assertEquals("encounter-live", currentEncounterId)
				assertEquals("Ruined Gate", currentEncounterName)
				assertEquals("Rain turns the stones slick.", encounterNotes)
				assertEquals(1, currentRound)
				assertEquals(1, currentTurnIndex)
				assertEquals(5, restoredHero.tempHp)
				assertEquals(4, restoredGoblin.currentHp)
				assertEquals("Poisoned", restoredGoblin.conditions.single().name)
				assertEquals(2, restoredGoblin.conditions.single().duration)
				assertTrue(activeStatuses.any { it.contains("$GOBLIN_NAME takes 3 damage (4/7 HP)") })
				assertTrue(activeStatuses.any { it.contains("$GOBLIN_NAME is now Poisoned (2 rounds)") })
			}
		}
	}

	@Test
	fun nextTurn_expiresRoundDurationExactlyOnExpectedRoundAdvance() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createViewModel(repository)
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
				assertEquals(1, combatants.first { it.characterId == HERO_ID }.conditions.single().duration)
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
			with(viewModel.uiState.value) {
				assertEquals(1, currentTurnIndex)
				assertEquals(2, currentRound)
				assertTrue(combatants.first { it.characterId == HERO_ID }.conditions.isEmpty())
				assertTrue(activeStatuses.any { it.contains("$HERO_NAME's Blessed condition has expired") })
			}
		}
	}

	@Test
	fun startEncounter_enemyHealerPrioritizesHealingAnInjuredAlly() {
		runTest {
			val repository = FakeCombatCampaignRepository().apply {
				setCharacters(
					listOf(
						CharacterEntry(id = HERO_ID, name = HERO_NAME, party = "Adventurers"),
						CharacterEntry(
							id = "shaman-1",
							name = "Shaman",
							party = "Monsters",
							actions = listOf(CharacterAction(name = "Heal Ally", isAttack = false, damageDice = "1d8+2"))
						),
						CharacterEntry(id = "brute-1", name = "Brute", party = "Monsters")
					)
				)
			}
			val viewModel = createViewModel(repository)

			viewModel.addParty(
				listOf(
					combatant(id = HERO_ID, name = HERO_NAME, initiative = 12, hp = 18),
					combatant(id = "shaman-1", name = "Shaman", initiative = 18, hp = 10),
					CombatantState(
						characterId = "brute-1",
						name = "Brute",
						initiative = 9,
						currentHp = 2,
						maxHp = 10
					)
				)
			)
			advanceUntilIdle()
			viewModel.updateInitiativeMode(InitiativeMode.AUTO_SORT)

			viewModel.startEncounter()
			advanceUntilIdle()

			val healedBrute = viewModel.uiState.value.combatants.first { it.characterId == "brute-1" }
			assertTrue(healedBrute.currentHp > 2)
			assertTrue(viewModel.uiState.value.activeStatuses.any { it.contains("Heal Ally on Brute") })
		}
	}

	@Test
	fun startEncounter_enemyDebufferAppliesConditionBeforeAttacking() {
		runTest {
			val repository = FakeCombatCampaignRepository().apply {
				setCharacters(
					listOf(
						CharacterEntry(id = HERO_ID, name = HERO_NAME, party = "Adventurers"),
						CharacterEntry(
							id = "witch-1",
							name = "Witch",
							party = "Monsters",
							actions = listOf(
								CharacterAction(name = "Hex", isAttack = false),
								CharacterAction(name = "Claw", isAttack = true, damageDice = "1d6+2")
							)
						)
					)
				)
			}
			val viewModel = createViewModel(repository)

			viewModel.addParty(
				listOf(
					combatant(id = "witch-1", name = "Witch", initiative = 18, hp = 12),
					combatant(id = HERO_ID, name = HERO_NAME, initiative = 11, hp = 18)
				)
			)
			advanceUntilIdle()

			viewModel.startEncounter()
			advanceUntilIdle()

			val hero = viewModel.uiState.value.combatants.first { it.characterId == HERO_ID }
			assertTrue(hero.conditions.any { it.name == "Cursed" })
			assertTrue(viewModel.uiState.value.activeStatuses.any { it.contains("applying Cursed") })
		}
	}

	@Test
	fun startEncounter_enemyBufferAppliesBeneficialConditionToAllyBeforeAttacking() {
		runTest {
			val repository = FakeCombatCampaignRepository().apply {
				setCharacters(
					listOf(
						CharacterEntry(id = HERO_ID, name = HERO_NAME, party = "Adventurers"),
						CharacterEntry(
							id = "priest-1",
							name = "Priest",
							party = "Monsters",
							actions = listOf(CharacterAction(name = "Bless Ally", isAttack = false))
						),
						CharacterEntry(id = "brute-1", name = "Brute", party = "Monsters")
					)
				)
			}
			val viewModel = createViewModel(repository)

			viewModel.addParty(
				listOf(
					combatant(id = "priest-1", name = "Priest", initiative = 18, hp = 9),
					combatant(id = HERO_ID, name = HERO_NAME, initiative = 12, hp = 18),
					combatant(id = "brute-1", name = "Brute", initiative = 8, hp = 16)
				)
			)
			advanceUntilIdle()

			viewModel.startEncounter()
			advanceUntilIdle()

			val brute = viewModel.uiState.value.combatants.first { it.characterId == "brute-1" }
			assertTrue(brute.conditions.any { it.name == "Blessed" })
			assertTrue(viewModel.uiState.value.activeStatuses.any { it.contains("applying Blessed") })
		}
	}

	@Test
	fun generateEncounter_replacesManualEnemiesAndPersistsGeneratedDetailsOnStart() {
		runTest {
			val repository = FakeCombatCampaignRepository().apply {
				setEncounter(
					Encounter(
						id = "encounter-generated",
						campaignId = "campaign-1",
						name = "Forest Skirmish",
						status = EncounterStatus.PENDING
					)
				)
				setCharacters(
					listOf(
						CharacterEntry(
							id = HERO_ID,
							name = HERO_NAME,
							party = "Adventurers",
							level = 3,
							hp = 18,
							maxHp = 18,
							initiative = 2
						)
					)
				)
			}
			val viewModel = createViewModel(repository)

			viewModel.loadEncounter("encounter-generated")
			advanceUntilIdle()
			viewModel.addParty(listOf(combatant(id = HERO_ID, name = HERO_NAME, initiative = 12, hp = 18)))
			viewModel.addEnemy(name = "Placeholder", hp = 5, initiative = 6)
			viewModel.updateGenerationSettings(
				EncounterGenerationSettings(
					difficultyTarget = io.github.velyene.loreweaver.domain.model.EncounterDifficultyTarget.MODERATE,
					maximumEnemyCr = 1.0,
					maximumEnemyQuantity = 3,
					allowDuplicateEnemies = true
				)
			)
			advanceUntilIdle()

			viewModel.generateEncounter()
			advanceUntilIdle()
			assertTrue(viewModel.uiState.value.generationDetails?.finalEnemies?.isNotEmpty() == true)
			assertFalse(viewModel.uiState.value.combatants.any { it.name == "Placeholder" })

			viewModel.startEncounter("encounter-generated")
			advanceUntilIdle()

			val storedEncounter = repository.getStoredEncounter("encounter-generated")
			assertEquals(1.0, storedEncounter?.generationSettings?.maximumEnemyCr ?: 0.0, 0.0)
			assertTrue(storedEncounter?.generationDetails?.finalEnemies?.isNotEmpty() == true)
			assertTrue(storedEncounter?.participants?.any { it.characterId != HERO_ID } == true)
		}
	}

	@Test
	fun startEncounter_manualInitiativeModePreservesCurrentRosterOrder() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createViewModel(repository)

			viewModel.addParty(
				listOf(
					combatant(id = HERO_ID, name = HERO_NAME, initiative = 12, hp = 12),
					combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 18, hp = 7),
				)
			)
			advanceUntilIdle()
			viewModel.updateInitiativeMode(InitiativeMode.MANUAL)

			viewModel.startEncounter()
			advanceUntilIdle()

			assertEquals(listOf(HERO_ID, GOBLIN_ID), viewModel.uiState.value.combatants.map { it.characterId })
		}
	}

	@Test
	fun startEncounter_autoSortInitiativeModeSortsByCurrentInitiative() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val viewModel = createViewModel(repository)

			viewModel.addParty(
				listOf(
					combatant(id = HERO_ID, name = HERO_NAME, initiative = 12, hp = 12),
					combatant(id = GOBLIN_ID, name = GOBLIN_NAME, initiative = 18, hp = 7),
				)
			)
			advanceUntilIdle()
			viewModel.updateInitiativeMode(InitiativeMode.AUTO_SORT)

			viewModel.startEncounter()
			advanceUntilIdle()

			assertEquals(listOf(GOBLIN_ID, HERO_ID), viewModel.uiState.value.combatants.map { it.characterId })
		}
	}

	private fun createViewModel(repository: FakeCombatCampaignRepository): CombatViewModel {
		return CombatViewModel(
			getCharactersUseCase = GetCharactersUseCase(repository),
			getActiveEncounterUseCase = GetActiveEncounterUseCase(repository),
			getEncounterByIdUseCase = GetEncounterByIdUseCase(repository),
			getSessionsForEncounterUseCase = GetSessionsForEncounterUseCase(repository),
			insertEncounterUseCase = InsertEncounterUseCase(repository),
			setActiveEncounterUseCase = SetActiveEncounterUseCase(repository),
			insertLogUseCase = InsertLogUseCase(repository),
			insertSessionRecordUseCase = InsertSessionRecordUseCase(repository),
			updateCharacterUseCase = UpdateCharacterUseCase(repository),
			combatTextProvider = object : CombatTextProvider {
				override fun roundBeginsStatus(round: Int): String = "Round $round begins"

				override fun quickEncounterName(timestampMillis: Long): String {
					return "Quick Encounter $timestampMillis"
				}

				override fun encounterSessionTitle(timestampMillis: Long): String {
					return "Encounter Session - $timestampMillis"
				}
			}
		)
	}

	private fun combatant(id: String, name: String, initiative: Int, hp: Int): CombatantState {
		return CombatantState(
			characterId = id,
			name = name,
			initiative = initiative,
			currentHp = hp,
			maxHp = hp
		)
	}
}

private class FakeCombatCampaignRepository : CampaignRepository {
	private val campaignsFlow = MutableStateFlow<List<Campaign>>(emptyList())
	private val charactersFlow = MutableStateFlow<List<CharacterEntry>>(emptyList())
	private val allSessionsFlow = MutableStateFlow<List<SessionRecord>>(emptyList())
	private val logsFlow = MutableStateFlow<List<LogEntry>>(emptyList())
	private val encountersById = mutableMapOf<String, Encounter>()
	private val sessionsByEncounterId = mutableMapOf<String, MutableStateFlow<List<SessionRecord>>>()

	var activeEncounterId: String? = null

	override fun getAllCampaigns(): Flow<List<Campaign>> = campaignsFlow

	override fun getAllEncounters(): Flow<List<Encounter>> = flowOf(encountersById.values.toList())

	override suspend fun getCampaignById(id: String): Campaign? =
		campaignsFlow.value.firstOrNull { it.id == id }

	override suspend fun insertCampaign(campaign: Campaign) {
		campaignsFlow.value += campaign
	}

	override suspend fun updateCampaign(campaign: Campaign) {
		campaignsFlow.value = campaignsFlow.value.map { existing ->
			if (existing.id == campaign.id) campaign else existing
		}
	}

	override suspend fun deleteCampaign(campaign: Campaign) {
		campaignsFlow.value = campaignsFlow.value.filterNot { it.id == campaign.id }
	}

	override fun getEncountersForCampaign(campaignId: String): Flow<List<Encounter>> =
		flowOf(encountersById.values.filter { it.campaignId == campaignId })

	override suspend fun getEncounterById(encounterId: String): Encounter? = encountersById[encounterId]

	override suspend fun insertEncounter(encounter: Encounter) {
		encountersById[encounter.id] = encounter
		activeEncounterId = when {
			encounter.status == EncounterStatus.ACTIVE -> encounter.id
			activeEncounterId == encounter.id -> null
			else -> activeEncounterId
		}
	}

	override suspend fun updateEncounter(encounter: Encounter) {
		encountersById[encounter.id] = encounter
	}

	override suspend fun deleteEncounter(encounter: Encounter) {
		encountersById.remove(encounter.id)
		if (activeEncounterId == encounter.id) {
			activeEncounterId = null
		}
	}

	override suspend fun addCombatantsToEncounter(

		encounterId: String,
		combatants: List<CombatantState>

	) = Unit

	override fun getSessionsForEncounter(encounterId: String): Flow<List<SessionRecord>> =
		sessionsByEncounterId.getOrPut(encounterId) { MutableStateFlow(emptyList()) }

	override fun getAllSessions(): Flow<List<SessionRecord>> = allSessionsFlow

	override suspend fun insertSessionRecord(session: SessionRecord) {
		allSessionsFlow.value += session
		session.encounterId?.let { encounterId ->
			val flow = sessionsByEncounterId.getOrPut(encounterId) { MutableStateFlow(emptyList()) }
			flow.value += session
		}
	}

	override fun getNotesForCampaign(campaignId: String): Flow<List<Note>> = flowOf(emptyList())

	override suspend fun insertNote(note: Note) = Unit

	override suspend fun updateNote(note: Note) = Unit

	override suspend fun deleteNote(note: Note) = Unit

	override fun getAllCharacters(): Flow<List<CharacterEntry>> = charactersFlow

	override suspend fun getCharacterById(id: String): CharacterEntry? =
		charactersFlow.value.firstOrNull { it.id == id }

	override suspend fun insertCharacter(character: CharacterEntry) = Unit

	override suspend fun updateCharacter(character: CharacterEntry) {
		charactersFlow.value = charactersFlow.value.map { existing ->
			if (existing.id == character.id) character else existing
		}
	}

	override suspend fun deleteCharacter(character: CharacterEntry) = Unit

	override suspend fun getActiveEncounter(): Encounter? = activeEncounterId?.let(encountersById::get)

	override suspend fun setActiveEncounter(encounterId: String) {
		if (encounterId.isBlank()) {
			activeEncounterId = null
			encountersById.replaceAll { _, encounter ->
				encounter.copy(status = EncounterStatus.PENDING)
			}
			return
		}

		activeEncounterId = encounterId
		encountersById.replaceAll { _, encounter ->
			encounter.copy(status = if (encounter.id == encounterId) EncounterStatus.ACTIVE else EncounterStatus.PENDING)
		}
	}

	override suspend fun getRecentSession(): SessionRecord? =
		allSessionsFlow.value.maxByOrNull(SessionRecord::date)

	override suspend fun getSessionById(sessionId: String): SessionRecord? =
		allSessionsFlow.value.firstOrNull { it.id == sessionId }

	override suspend fun getRecentSessionForEncounter(encounterId: String): SessionRecord? =
		sessionsByEncounterId[encounterId]?.value?.maxByOrNull(SessionRecord::date)
			?: allSessionsFlow.value
				.filter { it.encounterId == encounterId }
				.maxByOrNull(SessionRecord::date)

	override fun getAllLogs(): Flow<List<LogEntry>> = logsFlow

	override suspend fun insertLog(log: LogEntry) {
		logsFlow.value += log
	}

	override suspend fun clearLogs() {
		logsFlow.value = emptyList()
	}

	fun setCharacters(characters: List<CharacterEntry>) {
		charactersFlow.value = characters
	}

	fun getStoredCharacter(characterId: String): CharacterEntry? =
		charactersFlow.value.firstOrNull { it.id == characterId }

	fun setEncounter(encounter: Encounter) {
		encountersById[encounter.id] = encounter
		if (encounter.status == EncounterStatus.ACTIVE) {
			activeEncounterId = encounter.id
		}
	}

	fun setSessions(encounterId: String, sessions: List<SessionRecord>) {
		sessionsByEncounterId.getOrPut(encounterId) { MutableStateFlow(emptyList()) }.value = sessions
		allSessionsFlow.value = sessions
	}

	fun getStoredEncounter(encounterId: String): Encounter? = encountersById[encounterId]
}
