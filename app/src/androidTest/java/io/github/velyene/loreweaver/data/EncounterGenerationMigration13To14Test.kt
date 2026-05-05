package io.github.velyene.loreweaver.data

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EncounterGenerationMigration13To14Test {

	private lateinit var context: Context
	private lateinit var helper: SupportSQLiteOpenHelper
	private lateinit var database: SupportSQLiteDatabase
	private val databaseName = "encounter_generation_migration_13_14_test.db"

	@Before
	fun setUp() {
		context = ApplicationProvider.getApplicationContext()
		context.deleteDatabase(databaseName)
		helper = FrameworkSQLiteOpenHelperFactory().create(
			SupportSQLiteOpenHelper.Configuration.builder(context)
				.name(databaseName)
				.callback(
					object : SupportSQLiteOpenHelper.Callback(13) {
						override fun onCreate(db: SupportSQLiteDatabase) {
							db.execSQL(
								"""
								CREATE TABLE IF NOT EXISTS `encounters` (
									`id` TEXT NOT NULL,
									`campaignId` TEXT NOT NULL,
									`name` TEXT NOT NULL,
									`notes` TEXT NOT NULL,
									`isActive` INTEGER NOT NULL,
									`currentRound` INTEGER NOT NULL,
									`currentTurnIndex` INTEGER NOT NULL,
									`participantsJson` TEXT NOT NULL,
									`activeLogJson` TEXT NOT NULL,
									`rewardTemplateJson` TEXT NOT NULL,
									`createdAt` INTEGER NOT NULL,
									PRIMARY KEY(`id`)
								)
								""".trimIndent()
							)
							db.execSQL("CREATE INDEX IF NOT EXISTS `index_encounters_campaignId` ON `encounters` (`campaignId`)")
						}

						override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
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
	fun migration13To14_addsGenerationColumnsWithDefaults() {
		database.execSQL(
			"INSERT INTO encounters (id, campaignId, name, notes, isActive, currentRound, currentTurnIndex, participantsJson, activeLogJson, rewardTemplateJson, createdAt) VALUES ('encounter-1', 'campaign-1', 'Bridge Clash', '', 0, 1, 0, '[]', '[]', '{}', 12345)"
		)

		AppDatabase.MIGRATION_13_14.migrate(database)

		val tableInfo = database.query("PRAGMA table_info(`encounters`)")
		val columnNames = buildList {
			while (tableInfo.moveToNext()) {
				add(tableInfo.getString(tableInfo.getColumnIndexOrThrow("name")))
			}
		}
		tableInfo.close()

		assertTrue(columnNames.contains("generationSettingsJson"))
		assertTrue(columnNames.contains("generationDetailsJson"))

		val cursor = database.query("SELECT generationSettingsJson, generationDetailsJson FROM encounters WHERE id = 'encounter-1'")
		assertTrue(cursor.moveToFirst())
		assertEquals("{}", cursor.getString(cursor.getColumnIndexOrThrow("generationSettingsJson")))
		assertTrue(cursor.isNull(cursor.getColumnIndexOrThrow("generationDetailsJson")))
		cursor.close()
	}
}

