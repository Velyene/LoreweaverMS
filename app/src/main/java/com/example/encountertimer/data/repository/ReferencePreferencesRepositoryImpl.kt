package com.example.encountertimer.data.repository

import android.content.SharedPreferences
import com.example.encountertimer.domain.repository.ReferencePreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class ReferencePreferencesRepositoryImpl @Inject constructor(
	private val sharedPreferences: SharedPreferences
) : ReferencePreferencesRepository {

	private val trapFavoritesFlow = MutableStateFlow(
		normalizeStoredNames(
			TRAP_FAVORITES_KEY, validNames = setOf(
				"Collapsing Roof",
				"Falling Net",
				"Fire-Casting Statue",
				"Hidden Pit",
				"Poisoned Darts",
				"Poisoned Needle",
				"Rolling Stone",
				"Spiked Pit"
			)
		)
	)
	private val poisonFavoritesFlow = MutableStateFlow(
		normalizeStoredNames(
			POISON_FAVORITES_KEY, validNames = setOf(
				"Assassin's Blood",
				"Burnt Othur Fumes",
				"Crawler Mucus",
				"Essence of Ether",
				"Malice",
				"Midnight Tears",
				"Oil of Taggit",
				"Pale Tincture",
				"Purple Worm Poison",
				"Serpent Venom",
				"Spider's Sting",
				"Torpor",
				"Truth Serum",
				"Wyvern Poison"
			)
		)
	)
	private val diseaseFavoritesFlow = MutableStateFlow(readSet(DISEASE_FAVORITES_KEY))

	override val favoriteTrapNames: Flow<Set<String>> = trapFavoritesFlow.asStateFlow()
	override val favoritePoisonNames: Flow<Set<String>> = poisonFavoritesFlow.asStateFlow()
	override val favoriteDiseaseNames: Flow<Set<String>> = diseaseFavoritesFlow.asStateFlow()

	override suspend fun toggleTrapFavorite(name: String) {
		trapFavoritesFlow.value = toggleStoredName(TRAP_FAVORITES_KEY, name)
	}

	override suspend fun togglePoisonFavorite(name: String) {
		poisonFavoritesFlow.value = toggleStoredName(POISON_FAVORITES_KEY, name)
	}

	override suspend fun toggleDiseaseFavorite(name: String) {
		diseaseFavoritesFlow.value = toggleStoredName(DISEASE_FAVORITES_KEY, name)
	}

	private fun toggleStoredName(key: String, name: String): Set<String> {
		val updated = readSet(key).toMutableSet().apply {
			if (!add(name)) remove(name)
		}
		sharedPreferences.edit { putStringSet(key, updated) }
		return updated
	}

	private fun readSet(key: String): Set<String> {
		return sharedPreferences.getStringSet(key, emptySet())?.toSet() ?: emptySet()
	}

	private fun normalizeStoredNames(
		key: String,
		validNames: Set<String>
	): Set<String> {
		val current = readSet(key)
		val normalized = current.filterTo(mutableSetOf()) { it in validNames }
		if (normalized != current) {
			sharedPreferences.edit { putStringSet(key, normalized) }
		}
		return normalized
	}

	private companion object {
		const val TRAP_FAVORITES_KEY = "favorite_trap_names"
		const val POISON_FAVORITES_KEY = "favorite_poison_names"
		const val DISEASE_FAVORITES_KEY = "favorite_disease_names"
	}
}

