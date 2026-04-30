/*
 * FILE: CampaignRepositoryInstrumentedTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. In-memory database setup
 * 2. Repository snapshot persistence test
 * 3. Teardown
 */

package io.github.velyene.loreweaver

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.data.AppDatabase
import io.github.velyene.loreweaver.data.repository.CampaignRepositoryImpl
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Condition
import io.github.velyene.loreweaver.domain.model.DurationType
import io.github.velyene.loreweaver.domain.model.EncounterSnapshot
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
}
