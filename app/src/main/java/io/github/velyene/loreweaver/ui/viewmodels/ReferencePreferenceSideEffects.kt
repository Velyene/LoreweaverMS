/*
 * FILE: ReferencePreferenceSideEffects.kt
 *
 * TABLE OF CONTENTS:
 * 1. Favorites observation side effects
 * 2. Preference toggle side effects
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.repository.ReferencePreferencesRepository
import io.github.velyene.loreweaver.ui.util.AppText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ReferencePreferenceSideEffects(
	private val referencePreferencesRepository: ReferencePreferencesRepository,
	private val appText: AppText,
	private val uiState: MutableStateFlow<ReferenceUiState>,
	private val scope: CoroutineScope,
	private val reapplyAppliedSearch: () -> Unit
) {
	fun observeFavorites() {
		scope.launch {
			uiState.update { it.beginLoading() }
			try {
				combine(
					referencePreferencesRepository.favoriteTrapNames,
					referencePreferencesRepository.favoritePoisonNames,
					referencePreferencesRepository.favoriteDiseaseNames
				) { trapFavorites, poisonFavorites, diseaseFavorites ->
					Triple(trapFavorites, poisonFavorites, diseaseFavorites)
				}.collect { (trapFavorites, poisonFavorites, diseaseFavorites) ->
					uiState.update {
						it.copy(
							favoriteTrapNames = trapFavorites,
							favoritePoisonNames = poisonFavorites,
							favoriteDiseaseNames = diseaseFavorites,
							isLoading = false
						)
					}
					reapplyAppliedSearch()
				}
			} catch (cancellationException: CancellationException) {
				throw cancellationException
			} catch (_: Exception) {
				uiState.update {
					it.withError(
						message = appText.getString(R.string.reference_preferences_load_error),
						onRetry = { observeFavorites() }
					)
				}
			}
		}
	}

	fun toggleTrapFavorite(trapName: String) {
		launchPreferenceToggle { referencePreferencesRepository.toggleTrapFavorite(trapName) }
	}

	fun togglePoisonFavorite(poisonName: String) {
		launchPreferenceToggle { referencePreferencesRepository.togglePoisonFavorite(poisonName) }
	}

	fun toggleDiseaseFavorite(diseaseName: String) {
		launchPreferenceToggle { referencePreferencesRepository.toggleDiseaseFavorite(diseaseName) }
	}

	private fun launchPreferenceToggle(action: suspend () -> Unit) {
		scope.launch {
			try {
				action()
				uiState.update { it.clearErrorState() }
			} catch (cancellationException: CancellationException) {
				throw cancellationException
			} catch (_: Exception) {
				uiState.update {
					it.withError(
						message = appText.getString(R.string.reference_preferences_update_error),
						onRetry = { launchPreferenceToggle(action) }
					)
				}
			}
		}
	}
}


