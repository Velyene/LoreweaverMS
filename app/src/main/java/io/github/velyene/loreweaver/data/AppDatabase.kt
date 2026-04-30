package io.github.velyene.loreweaver.data

import android.content.Context
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
	version = 11,
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
		@Volatile
		private var INSTANCE: AppDatabase? = null

		private val MIGRATION_10_11 = object : Migration(10, 11) {
			override fun migrate(db: SupportSQLiteDatabase) {
				db.execSQL("ALTER TABLE characters ADD COLUMN persistentConditions TEXT NOT NULL DEFAULT '[]'")
				db.execSQL("UPDATE characters SET persistentConditions = activeConditions")
				db.execSQL("UPDATE characters SET activeConditions = '[]'")
			}
		}

		private val MIGRATION_9_10 = object : Migration(9, 10) {
			override fun migrate(db: SupportSQLiteDatabase) {
				db.execSQL("ALTER TABLE characters ADD COLUMN spells TEXT NOT NULL DEFAULT '[]'")
			}
		}

		private val MIGRATION_8_9 = object : Migration(8, 9) {
			override fun migrate(db: SupportSQLiteDatabase) {
				db.execSQL("ALTER TABLE characters ADD COLUMN species TEXT NOT NULL DEFAULT ''")
				db.execSQL("ALTER TABLE characters ADD COLUMN background TEXT NOT NULL DEFAULT ''")
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

		fun getDatabase(context: Context): AppDatabase {
			return INSTANCE ?: synchronized(this) {
				val instance = Room.databaseBuilder(
					context.applicationContext,
					AppDatabase::class.java,
					"loreweaver_database"
				)
					.addMigrations(
						MIGRATION_4_5,
						MIGRATION_5_6,
						MIGRATION_6_7,
						MIGRATION_7_8,
						MIGRATION_8_9,
						MIGRATION_9_10,
						MIGRATION_10_11
					)
					.build()
				INSTANCE = instance
				instance
			}
		}
	}
}
