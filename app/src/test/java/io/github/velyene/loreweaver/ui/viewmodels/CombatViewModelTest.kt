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
				assertTrue(activeStatuses.any { it.contains("$HERO_NAME's Poisoned condition has expired") })
				assertTrue(activeStatuses.any { it.contains("Round 2 begins") })
			}
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
			updateCharacterUseCase = UpdateCharacterUseCase(repository)
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

	override suspend fun getCampaignById(id: String): Campaign? =
		campaignsFlow.value.firstOrNull { it.id == id }

	override suspend fun insertCampaign(campaign: Campaign) {
		campaignsFlow.value += campaign
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
		activeEncounterId = encounterId
		encountersById.replaceAll { _, encounter ->
			encounter.copy(status = if (encounter.id == encounterId) EncounterStatus.ACTIVE else EncounterStatus.PENDING)
		}
	}

	override suspend fun getRecentSession(): SessionRecord? =
		allSessionsFlow.value.maxByOrNull(SessionRecord::date)

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
