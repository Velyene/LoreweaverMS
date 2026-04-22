package com.example.encountertimer

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.encountertimer.data.AppDatabase
import com.example.encountertimer.data.repository.CampaignRepositoryImpl
import com.example.encountertimer.domain.model.CharacterEntry
import com.example.encountertimer.domain.repository.CampaignRepository
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
}
