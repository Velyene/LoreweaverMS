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
class SessionRewardsMigration11To12Test {

	private lateinit var context: Context
	private lateinit var helper: SupportSQLiteOpenHelper
	private lateinit var database: SupportSQLiteDatabase
	private val databaseName = "session_rewards_migration_11_12_test.db"

	@Before
	fun setUp() {
		context = ApplicationProvider.getApplicationContext()
		context.deleteDatabase(databaseName)
		helper = FrameworkSQLiteOpenHelperFactory().create(
			SupportSQLiteOpenHelper.Configuration.builder(context)
				.name(databaseName)
				.callback(
					object : SupportSQLiteOpenHelper.Callback(11) {
						override fun onCreate(db: SupportSQLiteDatabase) {
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
	fun migration11To12_addsRewardsJsonColumnWithNullDefault() {
		database.execSQL(
			"INSERT INTO session_records (id, encounterId, title, date, logJson, snapshotJson, reuseFlag, isCompleted, encounterResult) VALUES ('session-1', 'encounter-1', 'Bridge Clash', 12345, '[]', NULL, 0, 1, 'VICTORY')"
		)

		AppDatabase.MIGRATION_11_12.migrate(database)

		val tableInfo = database.query("PRAGMA table_info(`session_records`)")
		val columnNames = buildList {
			while (tableInfo.moveToNext()) {
				add(tableInfo.getString(tableInfo.getColumnIndexOrThrow("name")))
			}
		}
		tableInfo.close()

		assertTrue(columnNames.contains("rewardsJson"))

		val cursor = database.query("SELECT rewardsJson FROM session_records WHERE id = 'session-1'")
		assertTrue(cursor.moveToFirst())
		assertEquals(true, cursor.isNull(cursor.getColumnIndexOrThrow("rewardsJson")))
		cursor.close()
	}
}

