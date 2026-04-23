package com.example.loreweaver.di

import android.content.Context
import android.content.SharedPreferences
import com.example.loreweaver.data.AppDatabase
import com.example.loreweaver.data.dao.CampaignDao
import com.example.loreweaver.data.dao.CharacterDao
import com.example.loreweaver.data.dao.EncounterDao
import com.example.loreweaver.data.dao.LogDao
import com.example.loreweaver.data.dao.NoteDao
import com.example.loreweaver.data.dao.SessionDao
import com.example.loreweaver.data.repository.CampaignRepositoryImpl
import com.example.loreweaver.data.repository.ReferencePreferencesRepositoryImpl
import com.example.loreweaver.domain.repository.CampaignRepository
import com.example.loreweaver.domain.repository.CampaignsRepository
import com.example.loreweaver.domain.repository.CharactersRepository
import com.example.loreweaver.domain.repository.EncountersRepository
import com.example.loreweaver.domain.repository.LogsRepository
import com.example.loreweaver.domain.repository.NotesRepository
import com.example.loreweaver.domain.repository.ReferencePreferencesRepository
import com.example.loreweaver.domain.repository.SessionsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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
		return context.getSharedPreferences("reference_preferences", Context.MODE_PRIVATE)
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
