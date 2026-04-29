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
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Encounter
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
import io.github.velyene.loreweaver.domain.util.DifficultyRating
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
	fun removeCombatant_recalculatesEncounterDifficultyFromActivePartyRosterOnly() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val heroOne = CharacterEntry(
				id = HERO_ID,
				name = "Aria",
				party = "Adventurers",
				level = 3,
				hp = 20,
				maxHp = 20
			)
			val heroTwo = CharacterEntry(
				id = "hero-2",
				name = "Bram",
				party = "Adventurers",
				level = 3,
				hp = 18,
				maxHp = 18
			)
			val monster = CharacterEntry(
				id = MONSTER_ID,
				name = "Ogre",
				party = "Monsters",
				challengeRating = 2.0,
				hp = 59,
				maxHp = 59
			)
			repository.setCharacters(listOf(heroOne, heroTwo, monster))
			val viewModel = createViewModel(repository)
			advanceUntilIdle()

			viewModel.addParty(
				listOf(
					combatant(id = heroOne.id, name = heroOne.name, initiative = 14, hp = heroOne.hp),
					combatant(id = heroTwo.id, name = heroTwo.name, initiative = 13, hp = heroTwo.hp),
					combatant(id = monster.id, name = monster.name, initiative = 8, hp = monster.hp),
				)
			)
			advanceUntilIdle()
			assertEquals(2, viewModel.uiState.value.encounterDifficulty?.partySize)

			viewModel.removeCombatant(heroTwo.id)
			advanceUntilIdle()

			assertEquals(1, viewModel.uiState.value.encounterDifficulty?.partySize)
		}
	}

	@Test
	fun addEnemy_countsManualEnemyInDifficultyEvenWithoutCharacterEntry() {
		runTest {
			val repository = FakeCombatCampaignRepository()
			val hero = CharacterEntry(
				id = HERO_ID,
				name = HERO_NAME,
				party = "Adventurers",
				level = 3,
				hp = 20,
				maxHp = 20
			)
			repository.setCharacters(listOf(hero))
			val viewModel = createViewModel(repository)
			advanceUntilIdle()

			viewModel.addParty(listOf(combatant(id = hero.id, name = hero.name, initiative = 15, hp = hero.hp)))
			advanceUntilIdle()
			viewModel.addEnemy(name = "Bandit Captain", hp = 65, initiative = 11)
			advanceUntilIdle()

			val difficulty = viewModel.uiState.value.encounterDifficulty
			assertNotNull(difficulty)
			assertEquals(1, difficulty?.partySize)
			assertEquals(1, difficulty?.monsterCount)
			assertEquals(0, difficulty?.totalMonsterXp)
			assertEquals(DifficultyRating.TRIVIAL, difficulty?.rating)
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
			insertSessionRecordUseCase = InsertSessionRecordUseCase(repository)
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

	override fun getAllCampaigns(): Flow<List<Campaign>> = campaignsFlow

	override suspend fun getCampaignById(id: String): Campaign? =
		campaignsFlow.value.firstOrNull { it.id == id }

	override suspend fun insertCampaign(campaign: Campaign) {
		campaignsFlow.value += campaign
	}

	override fun getEncountersForCampaign(campaignId: String): Flow<List<Encounter>> =
		flowOf(emptyList())

	override suspend fun getEncounterById(encounterId: String): Encounter? = null

	override suspend fun insertEncounter(encounter: Encounter) = Unit

	override suspend fun addCombatantsToEncounter(

		encounterId: String,
		combatants: List<CombatantState>

	) = Unit

	override fun getSessionsForEncounter(encounterId: String): Flow<List<SessionRecord>> =
		flowOf(emptyList())

	override fun getAllSessions(): Flow<List<SessionRecord>> = allSessionsFlow

	override suspend fun insertSessionRecord(session: SessionRecord) {
		allSessionsFlow.value += session
	}

	override fun getNotesForCampaign(campaignId: String): Flow<List<Note>> = flowOf(emptyList())

	override suspend fun insertNote(note: Note) = Unit

	override suspend fun updateNote(note: Note) = Unit

	override suspend fun deleteNote(note: Note) = Unit

	override fun getAllCharacters(): Flow<List<CharacterEntry>> = charactersFlow

	override suspend fun getCharacterById(id: String): CharacterEntry? =
		charactersFlow.value.firstOrNull { it.id == id }

	override suspend fun insertCharacter(character: CharacterEntry) = Unit

	override suspend fun updateCharacter(character: CharacterEntry) = Unit

	override suspend fun deleteCharacter(character: CharacterEntry) = Unit

	override suspend fun getActiveEncounter(): Encounter {
		return Encounter(
			id = "encounter-1",
			name = "Fallback",
			status = EncounterStatus.ACTIVE
		)
	}

	override suspend fun setActiveEncounter(encounterId: String) = Unit

	override suspend fun getRecentSession(): SessionRecord? = null

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
}
