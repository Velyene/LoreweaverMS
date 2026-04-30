/*
 * FILE: AppDatabaseDowngradeInstrumentedTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Debug downgrade fallback tests
 * 2. Database seeding and inspection helpers
 * 3. Cleanup
 */

package io.github.velyene.loreweaver.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.data.repository.CampaignRepositoryImpl
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class AppDatabaseDowngradeInstrumentedTest {

	private lateinit var context: Context
	private val managedDatabaseNames = listOf(
		AppDatabase.DATABASE_NAME,
		"release-downgrade-fallback-test"
	)

	@Before
	fun setUp() {
		context = ApplicationProvider.getApplicationContext()
		cleanupDatabases()
	}

	@After
	fun tearDown() {
		cleanupDatabases()
	}

	@Test
	fun getDatabase_debugBuildRecoversFromNewerOnDeviceSchema() = runBlocking {
		seedHigherVersionDatabase(AppDatabase.DATABASE_NAME)
		AppDatabase.resetInstanceForTesting()

		val database = AppDatabase.getDatabase(context)
		try {
			database.openHelper.writableDatabase

			assertFalse(defaultDatabaseTableExists("debug_only_table"))
			assertTrue(defaultDatabaseTableExists("characters"))

			val repository = CampaignRepositoryImpl(
				database.campaignDao(),
				database.encounterDao(),
				database.characterDao(),
				database.sessionDao(),
				database.noteDao(),
				database.logDao()
			)
			repository.insertCharacter(CharacterEntry(name = "Downgrade Survivor", hp = 8, maxHp = 8))

			val characters = repository.getAllCharacters().first()
			assertTrue(characters.any { it.name == "Downgrade Survivor" })
		} finally {
			AppDatabase.resetInstanceForTesting()
		}
	}

	@Test
	fun createDatabaseBuilder_nonDebuggableBuildStillRejectsDowngrade() {
		val databaseName = "release-downgrade-fallback-test"
		seedHigherVersionDatabase(databaseName)

		val thrown = assertThrows(IllegalStateException::class.java) {
			val database = AppDatabase.createDatabaseBuilder(
				context = context,
				databaseName = databaseName,
				isDebuggableBuild = false
			).build()
			try {
				database.openHelper.writableDatabase
			} finally {
				database.close()
			}
		}

		assertTrue(thrown.message.orEmpty().contains("migration from 11 to 8", ignoreCase = true))
	}

	private fun cleanupDatabases() {
		AppDatabase.resetInstanceForTesting()
		managedDatabaseNames.forEach(context::deleteDatabase)
	}

	private fun seedHigherVersionDatabase(databaseName: String) {
		context.deleteDatabase(databaseName)
		val databasePath = context.getDatabasePath(databaseName)
		databasePath.parentFile?.mkdirs()

		SQLiteDatabase.openOrCreateDatabase(databasePath, null).use { database ->
			database.execSQL(
				"CREATE TABLE IF NOT EXISTS debug_only_table (id INTEGER PRIMARY KEY, value TEXT NOT NULL)"
			)
			database.execSQL("INSERT INTO debug_only_table (value) VALUES ('stale data')")
			database.execSQL("PRAGMA user_version = 11")
		}
	}

	private fun defaultDatabaseTableExists(tableName: String): Boolean {
		val databasePath: File = context.getDatabasePath(AppDatabase.DATABASE_NAME)
		SQLiteDatabase.openDatabase(databasePath.path, null, SQLiteDatabase.OPEN_READONLY).use { database ->
			database.rawQuery(
				"SELECT name FROM sqlite_master WHERE type = 'table' AND name = ?",
				arrayOf(tableName)
			).use { cursor ->
				return cursor.moveToFirst()
			}
		}
	}
}

