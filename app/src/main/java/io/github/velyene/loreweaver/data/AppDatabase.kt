/*
 * FILE: AppDatabase.kt
 *
 * TABLE OF CONTENTS:
 * 1. Room database declaration
 * 2. DAO accessors
 * 3. Singleton, migrations, and builder helpers
 */

package io.github.velyene.loreweaver.data

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.velyene.loreweaver.data.dao.CampaignDao
import io.github.velyene.loreweaver.data.dao.CharacterDao
import io.github.velyene.loreweaver.data.dao.EncounterDao
import io.github.velyene.loreweaver.data.dao.LogDao
import io.github.velyene.loreweaver.data.dao.NoteDao
import io.github.velyene.loreweaver.data.dao.SessionDao
import io.github.velyene.loreweaver.data.entities.CampaignEntity
import io.github.velyene.loreweaver.data.entities.CharacterEntity
import io.github.velyene.loreweaver.data.entities.EncounterEntity
import io.github.velyene.loreweaver.data.entities.LogEntryEntity
import io.github.velyene.loreweaver.data.entities.NoteEntity
import io.github.velyene.loreweaver.data.entities.SessionEntity

@Database(
	entities = [
		CampaignEntity::class,
		EncounterEntity::class,
		CharacterEntity::class,
		SessionEntity::class,
		NoteEntity::class,
		LogEntryEntity::class
	],
	version = 15,
	exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
	abstract fun campaignDao(): CampaignDao
	abstract fun encounterDao(): EncounterDao
	abstract fun characterDao(): CharacterDao
	abstract fun sessionDao(): SessionDao
	abstract fun noteDao(): NoteDao
	abstract fun logDao(): LogDao

	companion object {
		const val DATABASE_NAME = "loreweaver_database"

		@Volatile
		private var INSTANCE: AppDatabase? = null

		val MIGRATION_14_15 = object : Migration(14, 15) {
			override fun migrate(db: SupportSQLiteDatabase) {
				db.execSQL("ALTER TABLE characters ADD COLUMN experiencePoints INTEGER NOT NULL DEFAULT 0")
				db.execSQL("ALTER TABLE characters ADD COLUMN inventoryStateJson TEXT NOT NULL DEFAULT '{}'")
				db.execSQL("ALTER TABLE campaigns ADD COLUMN inventoryStateJson TEXT NOT NULL DEFAULT '{}'")
				db.execSQL("ALTER TABLE session_records ADD COLUMN rewardReviewJson TEXT")
			}
		}

		val MIGRATION_13_14 = object : Migration(13, 14) {
			override fun migrate(db: SupportSQLiteDatabase) {
				db.execSQL("ALTER TABLE encounters ADD COLUMN generationSettingsJson TEXT NOT NULL DEFAULT '{}'")
				db.execSQL("ALTER TABLE encounters ADD COLUMN generationDetailsJson TEXT")
			}
		}

		val MIGRATION_12_13 = object : Migration(12, 13) {
			override fun migrate(db: SupportSQLiteDatabase) {
				db.execSQL("ALTER TABLE encounters ADD COLUMN rewardTemplateJson TEXT NOT NULL DEFAULT '{}'")
			}
		}

		val MIGRATION_11_12 = object : Migration(11, 12) {
			override fun migrate(db: SupportSQLiteDatabase) {
				db.execSQL("ALTER TABLE session_records ADD COLUMN rewardsJson TEXT")
			}
		}

		val MIGRATION_10_11 = object : Migration(10, 11) {
			override fun migrate(db: SupportSQLiteDatabase) {
				db.execSQL("ALTER TABLE encounters ADD COLUMN participantsJson TEXT NOT NULL DEFAULT '[]'")
				db.execSQL("ALTER TABLE encounters ADD COLUMN activeLogJson TEXT NOT NULL DEFAULT '[]'")
			}
		}

		private val MIGRATION_9_10 = object : Migration(9, 10) {
			override fun migrate(db: SupportSQLiteDatabase) {
				db.execSQL(
					"CREATE INDEX IF NOT EXISTS `index_session_records_date` ON `session_records` (`date`)"
				)
				db.execSQL(
					"CREATE INDEX IF NOT EXISTS `index_session_records_encounterId_date` ON `session_records` (`encounterId`, `date`)"
				)
			}
		}

		private val MIGRATION_8_9 = object : Migration(8, 9) {
			override fun migrate(db: SupportSQLiteDatabase) {
				db.execSQL("ALTER TABLE session_records ADD COLUMN isCompleted INTEGER NOT NULL DEFAULT 0")
				db.execSQL("ALTER TABLE session_records ADD COLUMN encounterResult TEXT")
			}
		}

		private val MIGRATION_7_8 = object : Migration(7, 8) {
			override fun migrate(db: SupportSQLiteDatabase) {
				db.execSQL("ALTER TABLE characters ADD COLUMN challengeRating REAL NOT NULL DEFAULT 0.0")
			}
		}

		private val MIGRATION_6_7 = object : Migration(6, 7) {
			override fun migrate(db: SupportSQLiteDatabase) {
				db.execSQL("ALTER TABLE characters ADD COLUMN hasInspiration INTEGER NOT NULL DEFAULT 0")
				db.execSQL("ALTER TABLE characters ADD COLUMN spellSlotsJson TEXT NOT NULL DEFAULT '{}'")
			}
		}

		private val MIGRATION_5_6 = object : Migration(5, 6) {
			override fun migrate(db: SupportSQLiteDatabase) {
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
			}
		}

		private val MIGRATION_4_5 = object : Migration(4, 5) {
			override fun migrate(db: SupportSQLiteDatabase) {
				// Add new columns to characters table introduced in the version 5 expansion
				db.execSQL("ALTER TABLE characters ADD COLUMN tempHp INTEGER NOT NULL DEFAULT 0")
				db.execSQL("ALTER TABLE characters ADD COLUMN mana INTEGER NOT NULL DEFAULT 0")
				db.execSQL("ALTER TABLE characters ADD COLUMN maxMana INTEGER NOT NULL DEFAULT 0")
				db.execSQL("ALTER TABLE characters ADD COLUMN stamina INTEGER NOT NULL DEFAULT 0")
				db.execSQL("ALTER TABLE characters ADD COLUMN maxStamina INTEGER NOT NULL DEFAULT 0")
				db.execSQL("ALTER TABLE characters ADD COLUMN speed INTEGER NOT NULL DEFAULT 30")
				db.execSQL("ALTER TABLE characters ADD COLUMN initiative INTEGER NOT NULL DEFAULT 0")
				db.execSQL("ALTER TABLE characters ADD COLUMN level INTEGER NOT NULL DEFAULT 1")
				db.execSQL("ALTER TABLE characters ADD COLUMN strength INTEGER NOT NULL DEFAULT 10")
				db.execSQL("ALTER TABLE characters ADD COLUMN dexterity INTEGER NOT NULL DEFAULT 10")
				db.execSQL("ALTER TABLE characters ADD COLUMN constitution INTEGER NOT NULL DEFAULT 10")
				db.execSQL("ALTER TABLE characters ADD COLUMN intelligence INTEGER NOT NULL DEFAULT 10")
				db.execSQL("ALTER TABLE characters ADD COLUMN wisdom INTEGER NOT NULL DEFAULT 10")
				db.execSQL("ALTER TABLE characters ADD COLUMN charisma INTEGER NOT NULL DEFAULT 10")
				db.execSQL("ALTER TABLE characters ADD COLUMN party TEXT NOT NULL DEFAULT 'Adventurers'")
				db.execSQL("ALTER TABLE characters ADD COLUMN status TEXT NOT NULL DEFAULT ''")
				db.execSQL("ALTER TABLE characters ADD COLUMN deathSaveSuccesses INTEGER NOT NULL DEFAULT 0")
				db.execSQL("ALTER TABLE characters ADD COLUMN deathSaveFailures INTEGER NOT NULL DEFAULT 0")
				db.execSQL("ALTER TABLE characters ADD COLUMN saveProficiencies TEXT NOT NULL DEFAULT '[]'")
				db.execSQL("ALTER TABLE characters ADD COLUMN resourcesJson TEXT NOT NULL DEFAULT '[]'")
				db.execSQL("ALTER TABLE characters ADD COLUMN hitDieType INTEGER NOT NULL DEFAULT 8")
				db.execSQL("ALTER TABLE characters ADD COLUMN hitDiceCurrent INTEGER NOT NULL DEFAULT 1")
				db.execSQL("ALTER TABLE characters ADD COLUMN activeConditions TEXT NOT NULL DEFAULT '[]'")
				db.execSQL("ALTER TABLE characters ADD COLUMN actionsJson TEXT NOT NULL DEFAULT '[]'")
				db.execSQL("ALTER TABLE characters ADD COLUMN proficiencies TEXT NOT NULL DEFAULT '[]'")
				db.execSQL("ALTER TABLE characters ADD COLUMN inventory TEXT NOT NULL DEFAULT '[]'")
				db.execSQL("ALTER TABLE characters ADD COLUMN isPlayerCharacter INTEGER NOT NULL DEFAULT 0")
				db.execSQL("ALTER TABLE characters ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0")
			}
		}

		internal fun isDebuggableBuild(context: Context): Boolean {
			return (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
		}

		internal fun createDatabaseBuilder(
			context: Context,
			databaseName: String = DATABASE_NAME,
			isDebuggableBuild: Boolean = isDebuggableBuild(context)
		): RoomDatabase.Builder<AppDatabase> {
			val builder = Room.databaseBuilder(
				context.applicationContext,
				AppDatabase::class.java,
				databaseName
			)
				.addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8)

			// Local/dev installs can legitimately open a newer on-device database after switching
			// branches or testing older builds. Allow destructive downgrade fallback only in debug
			// so development does not get stuck on missing downgrade paths, while release builds
			// still fail fast unless an explicit production-safe migration policy is added.
			if (isDebuggableBuild) {
				builder.fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
			}

			return builder
		}

		internal fun resetInstanceForTesting() {
			INSTANCE?.close()
			INSTANCE = null
		}

		fun getDatabase(context: Context): AppDatabase {
			return INSTANCE ?: synchronized(this) {
			val instance = Room.databaseBuilder(
				context.applicationContext,
				AppDatabase::class.java,
				DATABASE_NAME
			)
				.addMigrations(
					MIGRATION_4_5,
					MIGRATION_5_6,
					MIGRATION_6_7,
					MIGRATION_7_8,
					MIGRATION_8_9,
					MIGRATION_9_10,
					MIGRATION_10_11,
								MIGRATION_11_12,
								MIGRATION_12_13,
														MIGRATION_13_14,
														MIGRATION_14_15
				)
				.build()
				INSTANCE = instance
				instance
			}
		}
	}
}
