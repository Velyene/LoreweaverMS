package com.example.encountertimer.ui.viewmodels

import com.example.encountertimer.MainDispatcherRule
import com.example.encountertimer.domain.model.Campaign
import com.example.encountertimer.domain.model.CharacterEntry
import com.example.encountertimer.domain.model.CombatantState
import com.example.encountertimer.domain.model.Encounter
import com.example.encountertimer.domain.model.LogEntry
import com.example.encountertimer.domain.model.Note
import com.example.encountertimer.domain.model.SessionRecord
import com.example.encountertimer.domain.repository.CampaignRepository
import com.example.encountertimer.domain.use_case.AddCampaignUseCase
import com.example.encountertimer.domain.use_case.AddEncounterUseCase
import com.example.encountertimer.domain.use_case.AddMonstersToEncounterUseCase
import com.example.encountertimer.domain.use_case.AddNoteUseCase
import com.example.encountertimer.domain.use_case.DeleteNoteUseCase
import com.example.encountertimer.domain.use_case.GetAllSessionsUseCase
import com.example.encountertimer.domain.use_case.GetCampaignByIdUseCase
import com.example.encountertimer.domain.use_case.GetCampaignsUseCase
import com.example.encountertimer.domain.use_case.GetEncountersForCampaignUseCase
import com.example.encountertimer.domain.use_case.GetNotesForCampaignUseCase
import com.example.encountertimer.domain.use_case.GetSessionsForEncounterUseCase
import com.example.encountertimer.domain.use_case.UpdateNoteUseCase
import com.example.encountertimer.ui.util.CAMPAIGN_NOT_FOUND_MESSAGE
import com.example.encountertimer.ui.util.NOTE_TYPE_LORE
import com.example.encountertimer.ui.util.NOTE_TYPE_NPC
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
class CampaignViewModelTest {

	private companion object {
		const val CAMPAIGN_ID = "campaign-1"
	}

	@get:Rule
	val mainDispatcherRule = MainDispatcherRule()

	@Test
	fun selectCampaign_whenCampaignMissing_setsErrorAndRetry() {
		runTest {
			val repository = FakeCampaignRepository()
			val viewModel = createViewModel(repository)

			viewModel.selectCampaign("missing")
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertFalse(isLoading)
				assertEquals(CAMPAIGN_NOT_FOUND_MESSAGE, error)
				assertNotNull(onRetry)
			}
		}
	}

	@Test
	fun addNote_createsLoreNoteWithHistoricalEra() {
		runTest {
			val repository = FakeCampaignRepository()
			val viewModel = createViewModel(repository)

			viewModel.addNote(
				campaignId = CAMPAIGN_ID,
				content = "The empire fell in ash.",
				type = NOTE_TYPE_LORE,
				extra = "Age of Ruin"
			)
			advanceUntilIdle()

			val insertedNote = repository.insertedNotes.single()
			assertTrue(insertedNote is Note.Lore)
			insertedNote as Note.Lore
			assertEquals(CAMPAIGN_ID, insertedNote.campaignId)
			assertEquals("The empire fell in ash.", insertedNote.content)
			assertEquals("Age of Ruin", insertedNote.historicalEra)
		}
	}

	@Test
	fun addNote_createsNpcNoteAndDefaultsMissingAttitudeToNeutral() {
		runTest {
			val repository = FakeCampaignRepository()
			val viewModel = createViewModel(repository)

			viewModel.addNote(
				campaignId = CAMPAIGN_ID,
				content = "Suspicious quartermaster",
				type = NOTE_TYPE_NPC,
				extra = "Iron Circle"
			)
			advanceUntilIdle()

			val insertedNote = repository.insertedNotes.single()
			assertTrue(insertedNote is Note.NPC)
			insertedNote as Note.NPC
			assertEquals("Iron Circle", insertedNote.faction)
			assertEquals("Neutral", insertedNote.attitude)
		}
	}

	@Test
	fun selectCampaign_observesNotesForCampaign() {
		runTest {
			val repository = FakeCampaignRepository()
			val campaign = Campaign(id = CAMPAIGN_ID, title = "Stormreach")
			val notes = listOf(
				Note.General(campaignId = campaign.id, content = "Watch the harbor."),
				Note.Location(

					campaignId = campaign.id,
					content = "Ancient vault",
					region = "Cliffside"

				)
			)
			repository.setCampaigns(listOf(campaign))
			repository.setNotes(campaign.id, notes)
			val viewModel = createViewModel(repository)

			viewModel.selectCampaign(campaign.id)
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals(campaign, selectedCampaign)
				assertEquals(notes, this.notes)
				assertFalse(isLoading)
			}
		}
	}

	@Test
	fun selectCampaign_cancelsPreviousCampaignObserversBeforeSubscribingToNewOnes() {
		runTest {
			val repository = FakeCampaignRepository()
			val firstCampaign = Campaign(id = CAMPAIGN_ID, title = "Stormreach")
			val secondCampaign = Campaign(id = "campaign-2", title = "Ravenfall")
			val firstNotes = listOf(Note.General(campaignId = firstCampaign.id, content = "Harbor warning"))
			val secondNotes = listOf(Note.General(campaignId = secondCampaign.id, content = "Castle briefing"))
			repository.setCampaigns(listOf(firstCampaign, secondCampaign))
			repository.setNotes(firstCampaign.id, firstNotes)
			repository.setNotes(secondCampaign.id, secondNotes)
			val viewModel = createViewModel(repository)

			viewModel.selectCampaign(firstCampaign.id)
			advanceUntilIdle()
			assertEquals(firstNotes, viewModel.uiState.value.notes)

			viewModel.selectCampaign(secondCampaign.id)
			advanceUntilIdle()
			assertEquals(secondCampaign, viewModel.uiState.value.selectedCampaign)
			assertEquals(secondNotes, viewModel.uiState.value.notes)

			repository.setNotes(
				firstCampaign.id,
				listOf(Note.General(campaignId = firstCampaign.id, content = "Stale update"))
			)
			advanceUntilIdle()

			assertEquals(secondCampaign, viewModel.uiState.value.selectedCampaign)
			assertEquals(secondNotes, viewModel.uiState.value.notes)
		}
	}

	private fun createViewModel(repository: FakeCampaignRepository): CampaignViewModel {
		return CampaignViewModel(
			getCampaignsUseCase = GetCampaignsUseCase(repository),
			getCampaignByIdUseCase = GetCampaignByIdUseCase(repository),
			getEncountersForCampaignUseCase = GetEncountersForCampaignUseCase(repository),
			getNotesForCampaignUseCase = GetNotesForCampaignUseCase(repository),
			getSessionsForEncounterUseCase = GetSessionsForEncounterUseCase(repository),
			getAllSessionsUseCase = GetAllSessionsUseCase(repository),
			addCampaignUseCase = AddCampaignUseCase(repository),
			addEncounterUseCase = AddEncounterUseCase(repository),
			addMonstersToEncounterUseCase = AddMonstersToEncounterUseCase(repository),
			addNoteUseCase = AddNoteUseCase(repository),
			updateNoteUseCase = UpdateNoteUseCase(repository),
			deleteNoteUseCase = DeleteNoteUseCase(repository)
		)
	}
}

private class FakeCampaignRepository : CampaignRepository {
	private val campaignsFlow = MutableStateFlow<List<Campaign>>(emptyList())
	private val allSessionsFlow = MutableStateFlow<List<SessionRecord>>(emptyList())
	private val encountersByCampaign = mutableMapOf<String, MutableStateFlow<List<Encounter>>>()
	private val notesByCampaign = mutableMapOf<String, MutableStateFlow<List<Note>>>()
	private val sessionsByEncounter = mutableMapOf<String, MutableStateFlow<List<SessionRecord>>>()

	val insertedNotes = mutableListOf<Note>()

	override fun getAllCampaigns(): Flow<List<Campaign>> = campaignsFlow

	override suspend fun getCampaignById(id: String): Campaign? {
		return campaignsFlow.value.firstOrNull { it.id == id }
	}

	override suspend fun insertCampaign(campaign: Campaign) {
		campaignsFlow.value += campaign
	}

	override fun getEncountersForCampaign(campaignId: String): Flow<List<Encounter>> {
		return encountersByCampaign.getOrPut(campaignId) { MutableStateFlow(emptyList()) }
	}

	override suspend fun getEncounterById(encounterId: String): Encounter? {
		return encountersByCampaign.values
			.flatMap { it.value }
			.firstOrNull { it.id == encounterId }
	}

	override suspend fun insertEncounter(encounter: Encounter) {
		val campaignId = encounter.campaignId.orEmpty()
		val flow = encountersByCampaign.getOrPut(campaignId) { MutableStateFlow(emptyList()) }
		flow.value += encounter
	}

	override suspend fun addCombatantsToEncounter(

		encounterId: String,
		combatants: List<CombatantState>

	) = Unit

	override fun getSessionsForEncounter(encounterId: String): Flow<List<SessionRecord>> {
		return sessionsByEncounter.getOrPut(encounterId) { MutableStateFlow(emptyList()) }
	}

	override fun getAllSessions(): Flow<List<SessionRecord>> = allSessionsFlow

	override suspend fun insertSessionRecord(session: SessionRecord) {
		allSessionsFlow.value += session
		session.encounterId?.let { encounterId ->
			val flow = sessionsByEncounter.getOrPut(encounterId) { MutableStateFlow(emptyList()) }
			flow.value += session
		}
	}

	override fun getNotesForCampaign(campaignId: String): Flow<List<Note>> {
		return notesByCampaign.getOrPut(campaignId) { MutableStateFlow(emptyList()) }
	}

	override suspend fun insertNote(note: Note) {
		insertedNotes += note
		note.campaignId?.let { campaignId ->
			val flow = notesByCampaign.getOrPut(campaignId) { MutableStateFlow(emptyList()) }
			flow.value += note
		}
	}

	override suspend fun updateNote(note: Note) = Unit

	override suspend fun deleteNote(note: Note) = Unit

	override fun getAllCharacters(): Flow<List<CharacterEntry>> = flowOf(emptyList())

	override suspend fun getCharacterById(id: String): CharacterEntry? = null

	override suspend fun insertCharacter(character: CharacterEntry) = Unit

	override suspend fun updateCharacter(character: CharacterEntry) = Unit

	override suspend fun deleteCharacter(character: CharacterEntry) = Unit

	override suspend fun getActiveEncounter(): Encounter? = null

	override suspend fun setActiveEncounter(encounterId: String) = Unit

	override suspend fun getRecentSession(): SessionRecord? = null

	override fun getAllLogs(): Flow<List<LogEntry>> = flowOf(emptyList())

	override suspend fun insertLog(log: LogEntry) = Unit

	override suspend fun clearLogs() = Unit

	fun setCampaigns(campaigns: List<Campaign>) {
		campaignsFlow.value = campaigns
	}

	fun setNotes(campaignId: String, notes: List<Note>) {
		notesByCampaign.getOrPut(campaignId) { MutableStateFlow(emptyList()) }.value = notes
	}
}

