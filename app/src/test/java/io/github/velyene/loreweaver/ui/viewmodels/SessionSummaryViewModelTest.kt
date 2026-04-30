/*
 * FILE: SessionSummaryViewModelTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. buildSessionSummary mapper tests
 * 2. SessionSummaryViewModel state tests
 * 3. Test doubles
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.MainDispatcherRule
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterSnapshot
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.repository.CampaignsRepository
import io.github.velyene.loreweaver.domain.repository.CharactersRepository
import io.github.velyene.loreweaver.domain.repository.EncountersRepository
import io.github.velyene.loreweaver.domain.repository.SessionsRepository
import io.github.velyene.loreweaver.domain.use_case.GetCampaignByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetCharactersUseCase
import io.github.velyene.loreweaver.domain.use_case.GetEncounterByIdUseCase
import io.github.velyene.loreweaver.domain.use_case.GetRecentSessionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SessionSummaryViewModelTest {
	@get:Rule
	val mainDispatcherRule = MainDispatcherRule()

	@Test
	fun buildSessionSummary_derivesVictoryAndPersistentStatuses() {
		val session = SessionRecord(
			encounterId = "encounter-1",
			title = "Bridge Clash",
			log = listOf("Round 1 begins", "Kael used Strike on Goblin for 8 damage."),
			snapshot = EncounterSnapshot(
				combatants = listOf(
					combatant(id = "hero-1", name = "Kael", hp = 14, maxHp = 22, initiative = 16),
					combatant(id = "hero-2", name = "Mira", hp = 9, maxHp = 18, initiative = 12),
					combatant(id = "enemy-1", name = "Goblin", hp = 0, maxHp = 7, initiative = 15)
				),
				currentTurnIndex = 0,
				currentRound = 3
			)
		)
		val encounter = Encounter(
			id = "encounter-1",
			campaignId = "campaign-1",
			name = "Bridge Clash",
			notes = "The bridge is slick with rain."
		)
		val campaign = Campaign(id = "campaign-1", title = "Stormroad")
		val characters = listOf(
			CharacterEntry(id = "hero-1", name = "Kael", party = "Adventurers", activeConditions = setOf("Poisoned")),
			CharacterEntry(id = "hero-2", name = "Mira", party = "Adventurers"),
			CharacterEntry(id = "enemy-1", name = "Goblin", party = "Monsters")
		)

		val summary = buildSessionSummary(
			appText = fakeAppText,
			session = session,
			encounter = encounter,
			campaign = campaign,
			characters = characters
		)

		assertEquals(EncounterResult.VICTORY, summary.result)
		assertEquals(3, summary.totalRounds)
		assertEquals(2, summary.survivingPlayers.size)
		assertEquals(1, summary.defeatedEnemies.size)
		assertEquals("Stormroad", summary.campaignTitle)
		assertEquals("The bridge is slick with rain.", summary.notesSummary)
		assertEquals(1, summary.persistentStatuses.size)
		assertTrue(summary.persistentStatuses.single().conditions.contains("Poisoned"))
		assertEquals("HP: 14/22", summary.survivingPlayers.first().hpLabel)
		assertEquals(2, summary.logSummary.size)
	}

	@Test
	fun buildSessionSummary_derivesEndedEarlyWhenEnemiesStillRemain() {
		val session = SessionRecord(
			encounterId = "encounter-2",
			title = "Ambush",
			snapshot = EncounterSnapshot(
				combatants = listOf(
					combatant(id = "hero-1", name = "Kael", hp = 3, maxHp = 22, initiative = 16),
					combatant(id = "enemy-1", name = "Goblin", hp = 4, maxHp = 7, initiative = 15)
				),
				currentTurnIndex = 0,
				currentRound = 2
			)
		)
		val summary = buildSessionSummary(
			appText = fakeAppText,
			session = session,
			encounter = null,
			campaign = null,
			characters = listOf(
				CharacterEntry(id = "hero-1", name = "Kael", party = "Adventurers")
			)
		)

		assertEquals(EncounterResult.ENDED_EARLY, summary.result)
		assertEquals("Ambush", summary.encounterName)
		assertEquals(1, summary.survivingPlayers.size)
		assertEquals(0, summary.defeatedEnemies.size)
	}

	@Test
	fun init_withoutRecentSnapshot_surfacesLocalizedNonRecoverableMessage() = runTest {
		val viewModel = SessionSummaryViewModel(
			getRecentSessionUseCase = GetRecentSessionUseCase(FakeSessionsRepository(recentSession = null)),
			getEncounterByIdUseCase = GetEncounterByIdUseCase(FakeEncountersRepository()),
			getCampaignByIdUseCase = GetCampaignByIdUseCase(FakeCampaignsRepository()),
			getCharactersUseCase = GetCharactersUseCase(FakeCharactersRepository()),
			appText = fakeAppText
		)

		advanceUntilIdle()

		assertEquals("No recent encounter summary is available.", viewModel.uiState.value.error)
		assertNull(viewModel.uiState.value.summary)
		assertNull(viewModel.uiState.value.onRetry)
		assertTrue(!viewModel.uiState.value.isLoading)
	}

	@Test
	fun init_whenLoadFails_surfacesLocalizedRecoverableError() = runTest {
		val viewModel = SessionSummaryViewModel(
			getRecentSessionUseCase = GetRecentSessionUseCase(
				FakeSessionsRepository(recentSessionException = IllegalStateException("boom"))
			),
			getEncounterByIdUseCase = GetEncounterByIdUseCase(FakeEncountersRepository()),
			getCampaignByIdUseCase = GetCampaignByIdUseCase(FakeCampaignsRepository()),
			getCharactersUseCase = GetCharactersUseCase(FakeCharactersRepository()),
			appText = fakeAppText
		)

		advanceUntilIdle()

		assertEquals("Failed to load encounter summary.", viewModel.uiState.value.error)
		assertNull(viewModel.uiState.value.summary)
		assertNotNull(viewModel.uiState.value.onRetry)
		assertTrue(!viewModel.uiState.value.isLoading)
	}

	private fun combatant(
		id: String,
		name: String,
		hp: Int,
		maxHp: Int,
		initiative: Int
	): CombatantState {
		return CombatantState(
			characterId = id,
			name = name,
			initiative = initiative,
			currentHp = hp,
			maxHp = maxHp
		)
	}
}

private class FakeSessionsRepository(
	private val recentSession: SessionRecord? = null,
	private val recentSessionException: Exception? = null
) : SessionsRepository {
	override fun getSessionsForEncounter(encounterId: String): Flow<List<SessionRecord>> = flowOf(emptyList())

	override fun getAllSessions(): Flow<List<SessionRecord>> = flowOf(emptyList())

	override suspend fun insertSessionRecord(session: SessionRecord) = Unit

	override suspend fun getRecentSession(): SessionRecord? {
		recentSessionException?.let { throw it }
		return recentSession
	}
}

private class FakeCampaignsRepository(
	private val campaign: Campaign? = null
) : CampaignsRepository {
	override fun getAllCampaigns(): Flow<List<Campaign>> = flowOf(emptyList())

	override suspend fun getCampaignById(id: String): Campaign? = campaign

	override suspend fun insertCampaign(campaign: Campaign) = Unit
}

private class FakeEncountersRepository(
	private val encounter: Encounter? = null
) : EncountersRepository {
	override fun getEncountersForCampaign(campaignId: String): Flow<List<Encounter>> = flowOf(emptyList())

	override suspend fun getEncounterById(encounterId: String): Encounter? = encounter

	override suspend fun insertEncounter(encounter: Encounter) = Unit

	override suspend fun addCombatantsToEncounter(encounterId: String, combatants: List<CombatantState>) = Unit

	override suspend fun getActiveEncounter(): Encounter? = encounter

	override suspend fun setActiveEncounter(encounterId: String) = Unit
}

private class FakeCharactersRepository(
	private val characters: List<CharacterEntry> = emptyList()
) : CharactersRepository {
	override fun getAllCharacters(): Flow<List<CharacterEntry>> = flowOf(characters)

	override suspend fun getCharacterById(id: String): CharacterEntry? = characters.firstOrNull { it.id == id }

	override suspend fun insertCharacter(character: CharacterEntry) = Unit

	override suspend fun updateCharacter(character: CharacterEntry) = Unit

	override suspend fun deleteCharacter(character: CharacterEntry) = Unit
}

