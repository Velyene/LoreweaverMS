package com.example.loreweaver.domain.repository

import kotlinx.coroutines.flow.Flow

interface ReferencePreferencesRepository {
	val favoriteTrapNames: Flow<Set<String>>
	val favoritePoisonNames: Flow<Set<String>>
	val favoriteDiseaseNames: Flow<Set<String>>

	suspend fun toggleTrapFavorite(name: String)
	suspend fun togglePoisonFavorite(name: String)
	suspend fun toggleDiseaseFavorite(name: String)
}
