package io.github.velyene.loreweaver.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.velyene.loreweaver.data.AppDatabase
import io.github.velyene.loreweaver.data.dao.CampaignDao
import io.github.velyene.loreweaver.data.dao.CharacterDao
import io.github.velyene.loreweaver.data.dao.EncounterDao
import io.github.velyene.loreweaver.data.dao.LogDao
import io.github.velyene.loreweaver.data.dao.NoteDao
import io.github.velyene.loreweaver.data.dao.SessionDao
import io.github.velyene.loreweaver.data.repository.CampaignRepositoryImpl
import io.github.velyene.loreweaver.data.repository.ReferencePreferencesRepositoryImpl
import io.github.velyene.loreweaver.domain.repository.CampaignRepository
import io.github.velyene.loreweaver.domain.repository.CampaignsRepository
import io.github.velyene.loreweaver.domain.repository.CharactersRepository
import io.github.velyene.loreweaver.domain.repository.EncountersRepository
import io.github.velyene.loreweaver.domain.repository.LogsRepository
import io.github.velyene.loreweaver.domain.repository.NotesRepository
import io.github.velyene.loreweaver.domain.repository.ReferencePreferencesRepository
import io.github.velyene.loreweaver.domain.repository.SessionsRepository
import io.github.velyene.loreweaver.ui.util.AndroidAppText
import io.github.velyene.loreweaver.ui.util.AppText
import io.github.velyene.loreweaver.ui.viewmodels.AndroidCombatTextProvider
import io.github.velyene.loreweaver.ui.viewmodels.CombatTextProvider
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
	const val REFERENCE_PREFERENCES_NAME = "reference_preferences"

	@Provides
	@Singleton
	fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
		return AppDatabase.getDatabase(context)
	}

	@Provides
	fun provideCampaignDao(db: AppDatabase): CampaignDao = db.campaignDao()

	@Provides
	fun provideEncounterDao(db: AppDatabase): EncounterDao = db.encounterDao()

	@Provides
	fun provideCharacterDao(db: AppDatabase): CharacterDao = db.characterDao()

	@Provides
	fun provideSessionDao(db: AppDatabase): SessionDao = db.sessionDao()

	@Provides
	fun provideNoteDao(db: AppDatabase): NoteDao = db.noteDao()

	@Provides
	fun provideLogDao(db: AppDatabase): LogDao = db.logDao()

	@Provides
	@Singleton
	fun provideReferenceSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
		return context.getSharedPreferences(REFERENCE_PREFERENCES_NAME, Context.MODE_PRIVATE)
	}

	@Provides
	@Singleton
	fun provideReferencePreferencesRepository(
		sharedPreferences: SharedPreferences
	): ReferencePreferencesRepository {
		return ReferencePreferencesRepositoryImpl(sharedPreferences)
	}

	@Provides
	@Singleton
	fun provideCombatTextProvider(@ApplicationContext context: Context): CombatTextProvider {
		return AndroidCombatTextProvider(context)
	}

	@Provides
	@Singleton
	fun provideAppText(@ApplicationContext context: Context): AppText {
		return AndroidAppText(context)
	}

	@Provides
	@Singleton
	fun provideCampaignRepositoryImpl(
		campaignDao: CampaignDao,
		encounterDao: EncounterDao,
		characterDao: CharacterDao,
		sessionDao: SessionDao,
		noteDao: NoteDao,
		logDao: LogDao
	): CampaignRepositoryImpl {
		return CampaignRepositoryImpl(
			campaignDao,
			encounterDao,
			characterDao,
			sessionDao,
			noteDao,
			logDao
		)
	}

	@Provides
	@Singleton
	fun provideCampaignRepository(repository: CampaignRepositoryImpl): CampaignRepository = repository

	@Provides
	@Singleton
	fun provideCampaignsRepository(repository: CampaignRepositoryImpl): CampaignsRepository = repository

	@Provides
	@Singleton
	fun provideEncountersRepository(repository: CampaignRepositoryImpl): EncountersRepository = repository

	@Provides
	@Singleton
	fun provideSessionsRepository(repository: CampaignRepositoryImpl): SessionsRepository = repository

	@Provides
	@Singleton
	fun provideNotesRepository(repository: CampaignRepositoryImpl): NotesRepository = repository

	@Provides
	@Singleton
	fun provideCharactersRepository(repository: CampaignRepositoryImpl): CharactersRepository = repository

	@Provides
	@Singleton
	fun provideLogsRepository(repository: CampaignRepositoryImpl): LogsRepository = repository
}
