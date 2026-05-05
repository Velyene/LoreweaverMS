/*
 * FILE: CampaignRepositoryInstrumentedTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Class: CampaignRepositoryInstrumentedTest
 * 2. Function: createDb
 * 3. Value: context
 * 4. Function: closeDb
 * 5. Function: insertAndGetCharacter
 * 6. Value: character
 * 7. Value: allCharacters
 * 8. Value: loaded
 */

package io.github.velyene.loreweaver

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.data.AppDatabase
import io.github.velyene.loreweaver.data.repository.CampaignRepositoryImpl
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Condition
import io.github.velyene.loreweaver.domain.model.DurationType
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterSnapshot
import io.github.velyene.loreweaver.domain.model.EncounterStatus
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.repository.CampaignRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Instrumented test for CampaignRepository using an in-memory database.
 * Replaces the old CharacterStorageInstrumentedTest.
 */
@RunWith(AndroidJUnit4::class)
class CampaignRepositoryInstrumentedTest {

	private lateinit var db: AppDatabase
	private lateinit var repository: CampaignRepository

	@Before
	fun createDb() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
			.allowMainThreadQueries()
			.build()

		repository = CampaignRepositoryImpl(
			db.campaignDao(),
			db.encounterDao(),
			db.characterDao(),
			db.sessionDao(),
			db.noteDao(),
			db.logDao()
		)
	}

	@After
	@Throws(IOException::class)
	fun closeDb() {
		db.close()
	}

	@Test
	@Throws(Exception::class)
	fun insertAndGetCharacter() = runBlocking {
		val character = CharacterEntry(name = "Thorin", type = "Fighter", hp = 30, maxHp = 30)
		repository.insertCharacter(character)

		val allCharacters = repository.getAllCharacters().first()
		assertTrue(allCharacters.any { it.name == "Thorin" })

		val loaded = repository.getCharacterById(character.id)
		assertNotNull(loaded)
		assertEquals("Thorin", loaded?.name)
	}

	@Test
	fun updateCharacter() = runBlocking {
		val character = CharacterEntry(name = "Thorin", hp = 30, maxHp = 30)
		repository.insertCharacter(character)

		val updated = character.copy(hp = 20)
		repository.updateCharacter(updated)

		val loaded = repository.getCharacterById(character.id)
		assertEquals(20, loaded?.hp)
	}

	@Test
	fun deleteCharacter() = runBlocking {
		val character = CharacterEntry(name = "Thorin")
		repository.insertCharacter(character)

		repository.deleteCharacter(character)

		val allCharacters = repository.getAllCharacters().first()
		assertTrue(allCharacters.isEmpty())
	}

	@Test
	fun deleteCampaign_detachesLinkedSessionsBeforeCascadeDeletesEncounters() = runBlocking {
		val campaign = Campaign(id = "campaign-1", title = "Shadows of Emberfall")
		val encounter = Encounter(id = "encounter-1", campaignId = campaign.id, name = "Bridge Ambush")
		val session = SessionRecord(
			encounterId = encounter.id,
			title = "Bridge Ambush Recap",
			date = 1234L,
			log = listOf("Goblin takes 3 damage"),
		)

		repository.insertCampaign(campaign)
		repository.insertEncounter(encounter)
		repository.insertSessionRecord(session)

		repository.deleteCampaign(campaign)

		assertEquals(null, repository.getCampaignById(campaign.id))
		assertTrue(repository.getEncountersForCampaign(campaign.id).first().isEmpty())

		val recentSession = repository.getRecentSession()
		assertNotNull(recentSession)
		assertEquals(session.id, recentSession?.id)
		assertEquals(null, recentSession?.encounterId)
		assertTrue(repository.getAllSessions().first().any { it.id == session.id && it.encounterId == null })
	}

	@Test
	fun deleteEncounter_detachesLinkedSessionsAndRemovesEncounter() = runBlocking {
		val campaign = Campaign(id = "campaign-1", title = "Shadows of Emberfall")
		val encounter = Encounter(id = "encounter-1", campaignId = campaign.id, name = "Bridge Ambush")
		val session = SessionRecord(
			encounterId = encounter.id,
			title = "Bridge Ambush Recap",
			date = 5678L,
			log = listOf("Hero takes cover"),
		)

		repository.insertCampaign(campaign)
		repository.insertEncounter(encounter)
		repository.insertSessionRecord(session)

		repository.deleteEncounter(encounter)

		assertEquals(null, repository.getEncounterById(encounter.id))
		val recentSession = repository.getRecentSession()
		assertNotNull(recentSession)
		assertEquals(session.id, recentSession?.id)
		assertEquals(null, recentSession?.encounterId)
		assertTrue(repository.getAllSessions().first().any { it.id == session.id && it.encounterId == null })
	}

	@Test
	fun updateActiveEncounter_preservesActiveLookupAndPersistsUpdatedName() = runBlocking {
		val campaign = Campaign(id = "campaign-1", title = "Shadows of Emberfall")
		val encounter = Encounter(
			id = "encounter-active",
			campaignId = campaign.id,
			name = "Bridge Ambush",
			status = EncounterStatus.ACTIVE,
			currentRound = 2
		)

		repository.insertCampaign(campaign)
		repository.insertEncounter(encounter)
		repository.setActiveEncounter(encounter.id)
		repository.updateEncounter(encounter.copy(name = "Bridge Ambush Revised"))

		val updatedEncounter = repository.getEncounterById(encounter.id)
		val activeEncounter = repository.getActiveEncounter()

		assertEquals("Bridge Ambush Revised", updatedEncounter?.name)
		assertEquals(EncounterStatus.ACTIVE, updatedEncounter?.status)
		assertEquals(encounter.id, activeEncounter?.id)
		assertEquals("Bridge Ambush Revised", activeEncounter?.name)
	}

	@Test
	fun deleteActiveEncounter_clearsActiveLookupAndDetachesLinkedSessions() = runBlocking {
		val campaign = Campaign(id = "campaign-1", title = "Shadows of Emberfall")
		val encounter = Encounter(
			id = "encounter-active",
			campaignId = campaign.id,
			name = "Bridge Ambush",
			status = EncounterStatus.ACTIVE
		)
		val session = SessionRecord(
			encounterId = encounter.id,
			title = "Bridge Ambush Recap",
			date = 9_999L,
			log = listOf("Hero takes cover")
		)

		repository.insertCampaign(campaign)
		repository.insertEncounter(encounter)
		repository.setActiveEncounter(encounter.id)
		repository.insertSessionRecord(session)

		repository.deleteEncounter(encounter)

		assertEquals(null, repository.getEncounterById(encounter.id))
		assertEquals(null, repository.getActiveEncounter())
		assertTrue(repository.getAllSessions().first().any { it.id == session.id && it.encounterId == null })
	}

	@Test
	fun setActiveEncounter_blankIdClearsExistingActiveEncounter() = runBlocking {
		val campaign = Campaign(id = "campaign-1", title = "Shadows of Emberfall")
		val encounter = Encounter(
			id = "encounter-active",
			campaignId = campaign.id,
			name = "Bridge Ambush"
		)

		repository.insertCampaign(campaign)
		repository.insertEncounter(encounter)
		repository.setActiveEncounter(encounter.id)
		assertEquals(encounter.id, repository.getActiveEncounter()?.id)

		repository.setActiveEncounter("")

		assertEquals(null, repository.getActiveEncounter())
	}

	@Test
	fun activeEncounter_roundTripsPersistedParticipantsAndActiveLog() = runBlocking {
		val campaign = Campaign(id = "campaign-1", title = "Shadows of Emberfall")
		val encounter = Encounter(
			id = "encounter-live",
			campaignId = campaign.id,
			name = "Bridge Ambush",
			notes = "Fog covers the bridge.",
			status = EncounterStatus.ACTIVE,
			currentRound = 3,
			currentTurnIndex = 1,
			participants = listOf(
				CombatantState(
					characterId = "hero-1",
					name = "Hero",
					initiative = 15,
					currentHp = 12,
					maxHp = 12,
					tempHp = 5
				),
				CombatantState(
					characterId = "goblin-1",
					name = "Goblin",
					initiative = 10,
					currentHp = 4,
					maxHp = 7,
					conditions = listOf(
						Condition(
							name = "Poisoned",
							duration = 2,
							durationType = DurationType.ROUNDS,
							addedOnRound = 1
						)
					)
				)
			),
			activeLog = listOf(
				"Goblin takes 3 damage (4/7 HP)",
				"Goblin is now Poisoned (2 rounds)"
			)
		)

		repository.insertCampaign(campaign)
		repository.insertEncounter(encounter)
		repository.setActiveEncounter(encounter.id)

		val activeEncounter = repository.getActiveEncounter()
		assertNotNull(activeEncounter)
		assertEquals(encounter.name, activeEncounter?.name)
		assertEquals(encounter.notes, activeEncounter?.notes)
		assertEquals(3, activeEncounter?.currentRound)
		assertEquals(1, activeEncounter?.currentTurnIndex)
		assertEquals(encounter.activeLog, activeEncounter?.activeLog)
		assertEquals(2, activeEncounter?.participants?.size)
		assertEquals(5, activeEncounter?.participants?.first()?.tempHp)
		assertEquals(4, activeEncounter?.participants?.get(1)?.currentHp)
		assertEquals("Poisoned", activeEncounter?.participants?.get(1)?.conditions?.single()?.name)
	}

	@Test
	fun insertSessionRecord_roundTripsCombatSnapshotState() = runBlocking {
		val session = SessionRecord(
			encounterId = "encounter-restore",
			title = "Ruined Gate Resume",
			date = 1_715_000_000_000,
			log = listOf(
				"Goblin takes 3 damage (4/7 HP)",
				"Goblin is now Poisoned (2 rounds)"
			),
			snapshot = EncounterSnapshot(
				combatants = listOf(
					CombatantState(
						characterId = "hero-1",
						name = "Hero",
						initiative = 15,
						currentHp = 12,
						maxHp = 12
					),
					CombatantState(
						characterId = "goblin-1",
						name = "Goblin",
						initiative = 10,
						currentHp = 4,
						maxHp = 7,
						conditions = listOf(
							Condition(
								name = "Poisoned",
								duration = 2,
								durationType = DurationType.ROUNDS,
								addedOnRound = 1
							)
						)
					)
				),
				currentTurnIndex = 1,
				currentRound = 2
			)
		)

		repository.insertSessionRecord(session)

		val recentSession = repository.getRecentSession()
		val encounterSessions = repository.getSessionsForEncounter("encounter-restore").first()

		assertNotNull(recentSession)
		assertEquals(1, encounterSessions.size)
		assertEquals(session.id, recentSession?.id)
		assertEquals("Ruined Gate Resume", recentSession?.title)
		assertEquals(1_715_000_000_000, recentSession?.date)
		assertEquals(session.log, recentSession?.log)

		val snapshot = recentSession?.snapshot
		assertNotNull(snapshot)
		assertEquals(1, snapshot?.currentTurnIndex)
		assertEquals(2, snapshot?.currentRound)
		assertEquals(2, snapshot?.combatants?.size)
		assertEquals(listOf("hero-1", "goblin-1"), snapshot?.combatants?.map { it.characterId })
		assertEquals(12, snapshot?.combatants?.first()?.currentHp)
		assertEquals(4, snapshot?.combatants?.get(1)?.currentHp)

		val condition = snapshot?.combatants?.get(1)?.conditions?.single()
		assertNotNull(condition)
		assertEquals("Poisoned", condition?.name)
		assertEquals(2, condition?.duration)
		assertEquals(DurationType.ROUNDS, condition?.durationType)
		assertEquals(1, condition?.addedOnRound)
	}

	@Test
	fun getRecentSessionForEncounter_prefersMatchingEncounterOverGlobalNewest() = runBlocking {
		val activeEncounterSession = SessionRecord(
			encounterId = "encounter-active",
			title = "Clocktower Siege",
			date = 100L,
			snapshot = EncounterSnapshot(
				combatants = emptyList(),
				currentTurnIndex = 0,
				currentRound = 3
			)
		)
		val newerOtherEncounterSession = SessionRecord(
			encounterId = "encounter-other",
			title = "Harbor Ambush",
			date = 200L,
			snapshot = EncounterSnapshot(
				combatants = emptyList(),
				currentTurnIndex = 0,
				currentRound = 9
			)
		)

		repository.insertSessionRecord(activeEncounterSession)
		repository.insertSessionRecord(newerOtherEncounterSession)

		val globalRecentSession = repository.getRecentSession()
		val encounterRecentSession = repository.getRecentSessionForEncounter("encounter-active")

		assertEquals(newerOtherEncounterSession.id, globalRecentSession?.id)
		assertEquals(activeEncounterSession.id, encounterRecentSession?.id)
		assertEquals(3, encounterRecentSession?.snapshot?.currentRound)
	}

	@Test
	fun getSessionById_returnsDetachedSessionAfterEncounterDelete() = runBlocking {
		val campaign = Campaign(id = "campaign-1", title = "Shadows of Emberfall")
		val encounter = Encounter(id = "encounter-1", campaignId = campaign.id, name = "Bridge Ambush")
		val session = SessionRecord(
			id = "session-1",
			encounterId = encounter.id,
			title = "Bridge Ambush Recap",
			date = 6_789L,
			log = listOf("Hero takes cover"),
		)

		repository.insertCampaign(campaign)
		repository.insertEncounter(encounter)
		repository.insertSessionRecord(session)
		repository.deleteEncounter(encounter)

		val loadedSession = repository.getSessionById(session.id)
		assertNotNull(loadedSession)
		assertEquals(session.id, loadedSession?.id)
		assertEquals(session.title, loadedSession?.title)
		assertEquals(null, loadedSession?.encounterId)
	}

	@Test
	fun sessionRecordIndexes_existForRecentSessionQueries() = runBlocking {
		val cursor = db.openHelper.readableDatabase.query(
			"SELECT name FROM sqlite_master WHERE type = 'index' AND tbl_name = 'session_records'"
		)
		val indexNames = buildSet {
			while (cursor.moveToNext()) {
				add(cursor.getString(0))
			}
		}
		cursor.close()

		assertTrue(indexNames.contains("index_session_records_date"))
		assertTrue(indexNames.contains("index_session_records_encounterId_date"))
	}
}
