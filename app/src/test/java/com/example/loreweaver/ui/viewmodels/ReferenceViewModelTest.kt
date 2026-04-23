package com.example.loreweaver.ui.viewmodels

import com.example.loreweaver.MainDispatcherRule
import com.example.loreweaver.domain.repository.ReferencePreferencesRepository
import com.example.loreweaver.domain.util.MadnessDuration
import com.example.loreweaver.domain.util.PoisonReference
import com.example.loreweaver.domain.util.ReferenceDetailResolver
import com.example.loreweaver.domain.util.TrapReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class ReferenceViewModelTest {

	@get:Rule
	val mainDispatcherRule = MainDispatcherRule()

	@Test
	fun selectCategory_resetsSearchSelections_andDisablesUnsupportedFavoritesFilter() {
		runTest {
			val viewModel = createViewModel()
			val trap = TrapReference.TEMPLATES.first()
			val poison = PoisonReference.TEMPLATES.first()

			viewModel.search("pit")
			viewModel.selectTrap(trap)
			viewModel.selectPoison(poison)
			viewModel.toggleFavoritesOnly()
			advanceUntilIdle()

			viewModel.selectCategory(ReferenceCategory.OBJECTS)
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals(ReferenceCategory.OBJECTS, selectedCategory)
				assertEquals("", searchQuery)
				assertNull(selectedTrap)
				assertNull(selectedPoison)
				assertNull(selectedDisease)
				assertFalse(showFavoritesOnly)
			}
		}
	}

	@Test
	fun selectCategory_keepsFavoritesFilterForSupportedCategories() {
		runTest {
			val viewModel = createViewModel()

			viewModel.toggleFavoritesOnly()
			advanceUntilIdle()
			assertTrue(viewModel.uiState.value.showFavoritesOnly)

			viewModel.selectCategory(ReferenceCategory.POISONS)
			advanceUntilIdle()

			assertTrue(viewModel.uiState.value.showFavoritesOnly)
		}
	}

	@Test
	fun favoritesOnly_filtersCurrentCategory_andRespondsToFavoriteUpdates() {
		runTest {
			val repository = FakeReferencePreferencesRepository()
			val viewModel = createViewModel(repository)
			val favoriteTrap = TrapReference.TEMPLATES.first()

			viewModel.toggleFavoritesOnly()
			advanceUntilIdle()
			assertTrue(viewModel.uiState.value.filteredTraps.isEmpty())

			repository.setTrapFavorites(setOf(favoriteTrap.name))
			advanceUntilIdle()

			assertEquals(setOf(favoriteTrap.name), viewModel.uiState.value.favoriteTrapNames)
			assertEquals(listOf(favoriteTrap), viewModel.uiState.value.filteredTraps)
		}
	}

	@Test
	fun search_usesExactCanonicalTrapContentOnly() {
		runTest {
			val viewModel = createViewModel()

			viewModel.search("Rolling Stone")
			advanceUntilIdle()
			assertEquals(

				listOf("Rolling Stone"),
				viewModel.uiState.value.filteredTraps.map { it.name })

			viewModel.search("+8 bonus")
			advanceUntilIdle()
			assertEquals(

				listOf("Rolling Stone"),
				viewModel.uiState.value.filteredTraps.map { it.name })
		}
	}

	@Test
	fun search_updatesTypedQueryImmediately_andAppliesResultsAfterDebounce() {
		runTest {
			val viewModel = createViewModel()
			advanceUntilIdle()

			viewModel.search("Rolling Stone")

			with(viewModel.uiState.value) {
				assertEquals("Rolling Stone", searchQuery)
				assertEquals("", appliedSearchQuery)
				assertTrue(isSearchPending)
				assertEquals(TrapReference.TEMPLATES.size, filteredTraps.size)
			}

			advanceTimeBy(249.milliseconds)
			assertEquals("", viewModel.uiState.value.appliedSearchQuery)
			assertTrue(viewModel.uiState.value.isSearchPending)

			advanceTimeBy(1.milliseconds)
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals("Rolling Stone", appliedSearchQuery)
				assertFalse(isSearchPending)
				assertEquals(listOf("Rolling Stone"), filteredTraps.map { it.name })
			}
		}
	}

	@Test
	fun clearSearch_cancelsPendingDebounce_andRestoresDefaultResultsImmediately() {
		runTest {
			val viewModel = createViewModel()

			viewModel.search("Rolling Stone")
			viewModel.clearSearch()
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals("", searchQuery)
				assertEquals("", appliedSearchQuery)
				assertFalse(isSearchPending)
				assertEquals(TrapReference.TEMPLATES.size, filteredTraps.size)
			}
		}
	}

	@Test
	fun search_usesExactCanonicalPoisonContentOnly() {
		runTest {
			val viewModel = createViewModel()

			viewModel.selectCategory(ReferenceCategory.POISONS)
			advanceUntilIdle()

			viewModel.search("Assassin's Blood")
			advanceUntilIdle()
			assertEquals(

				listOf("Assassin's Blood"),
				viewModel.uiState.value.filteredPoisons.map { it.name })

			viewModel.search("1d12")
			advanceUntilIdle()
			assertEquals(

				listOf("Assassin's Blood"),
				viewModel.uiState.value.filteredPoisons.map { it.name })
		}
	}

	@Test
	fun selectMadnessDuration_clearsLastRollAndResult() {
		runTest {
			val viewModel = createViewModel()

			viewModel.rollMadness()
			advanceUntilIdle()
			assertNotNull(viewModel.uiState.value.madnessLastRoll)
			assertNotNull(viewModel.uiState.value.madnessLastResult)

			viewModel.selectMadnessDuration(MadnessDuration.LONG_TERM)
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals(MadnessDuration.LONG_TERM, selectedMadnessDuration)
				assertNull(madnessLastRoll)
				assertNull(madnessLastResult)
			}
		}
	}

	@Test
	fun spellcastingInputs_areNormalized() {
		runTest {
			val viewModel = createViewModel()

			viewModel.updateSpellcastingAbilityModifierInput("--12a3")
			viewModel.updateSpellcastingBonusInput("abc-98765")
			viewModel.updateSpellcastingProficiencyBonusInput("12a34")
			viewModel.updateSpellcastingCasterLevelInput("9b87")
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals("-123", spellcastingAbilityModifierInput)
				assertEquals("9876", spellcastingBonusInput)
				assertEquals("12", spellcastingProficiencyBonusInput)
				assertEquals("98", spellcastingCasterLevelInput)
			}
		}
	}

	@Test
	fun initializeFromNavigation_setsCategorySearchAndLocalConditionDetail() {
		runTest {
			val viewModel = createViewModel()

			viewModel.initializeFromNavigation(
				category = ReferenceCategory.CORE_RULES,
				query = "Blinded",
				detailCategory = ReferenceDetailResolver.CATEGORY_CONDITIONS,
				detailSlug = "blinded"
			)
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals(ReferenceCategory.CORE_RULES, selectedCategory)
				assertEquals("Blinded", searchQuery)
				assertEquals("Blinded", selectedReferenceDetail?.title)
				assertEquals(

					ReferenceDetailResolver.CATEGORY_CONDITIONS,
					selectedReferenceDetail?.subtitle

				)
			}
		}
	}

	@Test
	fun openReferenceDetail_andClearReferenceDetail_updatesGenericDetailState() {
		runTest {
			val viewModel = createViewModel()

			viewModel.openReferenceDetail(ReferenceDetailResolver.CATEGORY_ARMOR, "shield")
			advanceUntilIdle()

			assertEquals("Shield", viewModel.uiState.value.selectedReferenceDetail?.title)

			viewModel.clearReferenceDetail()
			advanceUntilIdle()

			assertNull(viewModel.uiState.value.selectedReferenceDetail)
		}
	}

	private fun createViewModel(
		repository: FakeReferencePreferencesRepository = FakeReferencePreferencesRepository()
	): ReferenceViewModel = ReferenceViewModel(repository)
}

private class FakeReferencePreferencesRepository : ReferencePreferencesRepository {
	private val _favoriteTrapNames = MutableStateFlow(emptySet<String>())
	private val _favoritePoisonNames = MutableStateFlow(emptySet<String>())
	private val _favoriteDiseaseNames = MutableStateFlow(emptySet<String>())

	override val favoriteTrapNames: StateFlow<Set<String>> = _favoriteTrapNames
	override val favoritePoisonNames: StateFlow<Set<String>> = _favoritePoisonNames
	override val favoriteDiseaseNames: StateFlow<Set<String>> = _favoriteDiseaseNames

	override suspend fun toggleTrapFavorite(name: String) {
		_favoriteTrapNames.value = _favoriteTrapNames.value.toggle(name)
	}

	override suspend fun togglePoisonFavorite(name: String) {
		_favoritePoisonNames.value = _favoritePoisonNames.value.toggle(name)
	}

	override suspend fun toggleDiseaseFavorite(name: String) {
		_favoriteDiseaseNames.value = _favoriteDiseaseNames.value.toggle(name)
	}

	fun setTrapFavorites(names: Set<String>) {
		_favoriteTrapNames.value = names
	}

	private fun Set<String>.toggle(name: String): Set<String> {
		return if (name in this) this - name else this + name
	}
}
