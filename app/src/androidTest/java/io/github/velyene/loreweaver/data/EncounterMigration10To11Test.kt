/*
 * FILE: EncounterMigration10To11Test.kt
 *
 * TABLE OF CONTENTS:
 * 1. Class: EncounterMigration10To11Test
 * 2. Value: databaseName
 * 3. Function: setUp
 * 4. Function: tearDown
 * 5. Function: migration10To11_addsEncounterCheckpointColumnsWithSafeDefaults
 * 6. Value: tableInfo
 * 7. Value: columnNames
 * 8. Value: cursor
 */

package io.github.velyene.loreweaver.data

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.data.repository.CampaignRepositoryImpl
import io.github.velyene.loreweaver.domain.model.EncounterStatus
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EncounterMigration10To11Test {

	private lateinit var context: Context
	private lateinit var helper: SupportSQLiteOpenHelper
	private lateinit var database: SupportSQLiteDatabase
	private val databaseName = "encounter_migration_10_11_test.db"

	@Before
	fun setUp() {
		context = ApplicationProvider.getApplicationContext()
		context.deleteDatabase(databaseName)
		helper = FrameworkSQLiteOpenHelperFactory().create(
			SupportSQLiteOpenHelper.Configuration.builder(context)
				.name(databaseName)
				.callback(
					object : SupportSQLiteOpenHelper.Callback(10) {
						override fun onCreate(db: SupportSQLiteDatabase) {
							db.execSQL(
								"""
								CREATE TABLE IF NOT EXISTS `campaigns` (
									`id` TEXT NOT NULL,
									`name` TEXT NOT NULL,
									`description` TEXT NOT NULL,
										`createdAt` INTEGER NOT NULL DEFAULT 0,
									PRIMARY KEY(`id`)
								)
								""".trimIndent()
							)
							db.execSQL(
								"""
									CREATE TABLE IF NOT EXISTS `characters` (
										`id` TEXT NOT NULL,
										`name` TEXT NOT NULL,
										`type` TEXT NOT NULL,
										`hp` INTEGER NOT NULL,
										`maxHp` INTEGER NOT NULL,
										`tempHp` INTEGER NOT NULL DEFAULT 0,
										`mana` INTEGER NOT NULL DEFAULT 0,
										`maxMana` INTEGER NOT NULL DEFAULT 0,
										`stamina` INTEGER NOT NULL DEFAULT 0,
										`maxStamina` INTEGER NOT NULL DEFAULT 0,
										`ac` INTEGER NOT NULL,
										`speed` INTEGER NOT NULL DEFAULT 30,
										`initiative` INTEGER NOT NULL DEFAULT 0,
										`level` INTEGER NOT NULL DEFAULT 1,
										`challengeRating` REAL NOT NULL DEFAULT 0.0,
										`strength` INTEGER NOT NULL DEFAULT 10,
										`dexterity` INTEGER NOT NULL DEFAULT 10,
										`constitution` INTEGER NOT NULL DEFAULT 10,
										`intelligence` INTEGER NOT NULL DEFAULT 10,
										`wisdom` INTEGER NOT NULL DEFAULT 10,
										`charisma` INTEGER NOT NULL DEFAULT 10,
										`notes` TEXT NOT NULL DEFAULT '',
										`party` TEXT NOT NULL DEFAULT 'Adventurers',
										`status` TEXT NOT NULL DEFAULT '',
										`deathSaveSuccesses` INTEGER NOT NULL DEFAULT 0,
										`deathSaveFailures` INTEGER NOT NULL DEFAULT 0,
										`saveProficiencies` TEXT NOT NULL DEFAULT '[]',
										`resourcesJson` TEXT NOT NULL DEFAULT '[]',
										`hitDieType` INTEGER NOT NULL DEFAULT 8,
										`hitDiceCurrent` INTEGER NOT NULL DEFAULT 1,
										`activeConditions` TEXT NOT NULL DEFAULT '[]',
										`actionsJson` TEXT NOT NULL DEFAULT '[]',
										`proficiencies` TEXT NOT NULL DEFAULT '[]',
										`inventory` TEXT NOT NULL DEFAULT '[]',
										`isPlayerCharacter` INTEGER NOT NULL DEFAULT 0,
										`hasInspiration` INTEGER NOT NULL DEFAULT 0,
										`spellSlotsJson` TEXT NOT NULL DEFAULT '{}',
										`createdAt` INTEGER NOT NULL DEFAULT 0,
										PRIMARY KEY(`id`)
									)
									""".trimIndent()
								)
								db.execSQL(
									"""
									CREATE TABLE IF NOT EXISTS `session_records` (
										`id` TEXT NOT NULL,
										`encounterId` TEXT,
										`title` TEXT NOT NULL,
										`date` INTEGER NOT NULL,
										`logJson` TEXT NOT NULL,
										`snapshotJson` TEXT,
										`reuseFlag` INTEGER NOT NULL DEFAULT 0,
										`isCompleted` INTEGER NOT NULL DEFAULT 0,
										`encounterResult` TEXT,
										PRIMARY KEY(`id`)
									)
									""".trimIndent()
								)
								db.execSQL(
									"CREATE INDEX IF NOT EXISTS `index_session_records_date` ON `session_records` (`date`)"
								)
								db.execSQL(
									"CREATE INDEX IF NOT EXISTS `index_session_records_encounterId_date` ON `session_records` (`encounterId`, `date`)"
								)
								db.execSQL(
									"""
									CREATE TABLE IF NOT EXISTS `notes` (
										`id` TEXT NOT NULL,
										`campaignId` TEXT NOT NULL,
										`subtype` TEXT NOT NULL DEFAULT 'General',
										`content` TEXT NOT NULL,
										`createdAt` INTEGER NOT NULL DEFAULT 0,
										`historicalEra` TEXT,
										`faction` TEXT,
										`attitude` TEXT,
										`region` TEXT,
										PRIMARY KEY(`id`),
										FOREIGN KEY(`campaignId`) REFERENCES `campaigns`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
									)
									""".trimIndent()
								)
								db.execSQL(
									"CREATE INDEX IF NOT EXISTS `index_notes_campaignId` ON `notes` (`campaignId`)"
								)
								db.execSQL(
									"""
									CREATE TABLE IF NOT EXISTS `adventure_logs` (
										`id` TEXT NOT NULL,
										`timestamp` INTEGER NOT NULL,
										`message` TEXT NOT NULL,
										`type` TEXT NOT NULL,
										PRIMARY KEY(`id`)
									)
									""".trimIndent()
								)
								db.execSQL(
									"""
								CREATE TABLE IF NOT EXISTS `encounters` (
									`id` TEXT NOT NULL,
									`campaignId` TEXT NOT NULL,
									`name` TEXT NOT NULL,
									`notes` TEXT NOT NULL DEFAULT '',
									`isActive` INTEGER NOT NULL DEFAULT 0,
									`currentRound` INTEGER NOT NULL DEFAULT 1,
									`currentTurnIndex` INTEGER NOT NULL DEFAULT 0,
									`createdAt` INTEGER NOT NULL DEFAULT 0,
									PRIMARY KEY(`id`),
									FOREIGN KEY(`campaignId`) REFERENCES `campaigns`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
								)
								""".trimIndent()
							)
							db.execSQL(
								"CREATE INDEX IF NOT EXISTS `index_encounters_campaignId` ON `encounters` (`campaignId`)"
							)
						}

						override fun onUpgrade(
							db: SupportSQLiteDatabase,
							oldVersion: Int,
							newVersion: Int,
						) = Unit
					}
				)
				.build()
		)
		database = helper.writableDatabase
	}

	@After
	fun tearDown() {
		runCatching { database.close() }
		runCatching { helper.close() }
		context.deleteDatabase(databaseName)
	}

	@Test
	fun migration10To11_addsEncounterCheckpointColumnsWithSafeDefaults() {
		seedLegacyEncounterRow()

		AppDatabase.MIGRATION_10_11.migrate(database)

		val tableInfo = database.query("PRAGMA table_info(`encounters`)")
		val columnNames = buildList {
			while (tableInfo.moveToNext()) {
				add(tableInfo.getString(tableInfo.getColumnIndexOrThrow("name")))
			}
		}
		tableInfo.close()

		assertTrue(columnNames.contains("participantsJson"))
		assertTrue(columnNames.contains("activeLogJson"))

		val cursor = database.query(
			"SELECT id, campaignId, name, notes, currentRound, currentTurnIndex, participantsJson, activeLogJson FROM encounters WHERE id = 'encounter-1'"
		)
		assertTrue(cursor.moveToFirst())
		assertEquals("encounter-1", cursor.getString(cursor.getColumnIndexOrThrow("id")))
		assertEquals("campaign-1", cursor.getString(cursor.getColumnIndexOrThrow("campaignId")))
		assertEquals("Bridge Ambush", cursor.getString(cursor.getColumnIndexOrThrow("name")))
		assertEquals("Fog covers the bridge.", cursor.getString(cursor.getColumnIndexOrThrow("notes")))
		assertEquals(3, cursor.getInt(cursor.getColumnIndexOrThrow("currentRound")))
		assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("currentTurnIndex")))
		assertEquals("[]", cursor.getString(cursor.getColumnIndexOrThrow("participantsJson")))
		assertEquals("[]", cursor.getString(cursor.getColumnIndexOrThrow("activeLogJson")))
		cursor.close()
	}

	@Test
	fun migration10To11_migratedEncounterMaterializesAsSafeDomainDefaults() = runBlocking {
		seedLegacyEncounterRow()
		database.close()
		helper.close()

		val roomDb = Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
			.addMigrations(AppDatabase.MIGRATION_10_11)
			.allowMainThreadQueries()
			.build()

		try {
			val repository = CampaignRepositoryImpl(
				roomDb.campaignDao(),
				roomDb.encounterDao(),
				roomDb.characterDao(),
				roomDb.sessionDao(),
				roomDb.noteDao(),
				roomDb.logDao()
			)

			val encounter = repository.getEncounterById("encounter-1")
			assertNotNull(encounter)
			assertEquals("encounter-1", encounter?.id)
			assertEquals("campaign-1", encounter?.campaignId)
			assertEquals("Bridge Ambush", encounter?.name)
			assertEquals("Fog covers the bridge.", encounter?.notes)
			assertEquals(EncounterStatus.ACTIVE, encounter?.status)
			assertEquals(3, encounter?.currentRound)
			assertEquals(1, encounter?.currentTurnIndex)
			assertTrue(encounter?.participants?.isEmpty() == true)
			assertTrue(encounter?.activeLog?.isEmpty() == true)
		} finally {
			roomDb.close()
		}
	}

	private fun seedLegacyEncounterRow() {
		database.execSQL(
			"INSERT INTO campaigns (id, name, description, createdAt) VALUES ('campaign-1', 'Shadows of Emberfall', 'A windswept frontier', 123456000)"
		)
		database.execSQL(
			"""
			INSERT INTO encounters (
				id,
				campaignId,
				name,
				notes,
				isActive,
				currentRound,
				currentTurnIndex,
				createdAt
			) VALUES (
				'encounter-1',
				'campaign-1',
				'Bridge Ambush',
				'Fog covers the bridge.',
				1,
				3,
				1,
				123456789
			)
			""".trimIndent()
		)
	}
}

