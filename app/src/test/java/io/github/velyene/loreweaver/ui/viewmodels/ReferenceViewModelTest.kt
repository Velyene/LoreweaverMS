/*
 * FILE: ReferenceViewModelTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Category selection and favorites-filter behavior
 * 2. Debounced search and canonical reference filtering
 * 3. Madness, spellcasting, and navigation-detail helpers
 * 4. Fake preferences repository support helpers
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.MainDispatcherRule
import io.github.velyene.loreweaver.domain.util.MadnessDuration
import io.github.velyene.loreweaver.domain.util.MonsterReferenceCatalog
import io.github.velyene.loreweaver.domain.util.PoisonReference
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver
import io.github.velyene.loreweaver.domain.util.TrapReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
	private companion object {
		const val ROLLING_STONE = "Rolling Stone"
		const val ASSASSINS_BLOOD = "Assassin's Blood"
		const val BLINDED = "Blinded"
		const val ANCIENT_WHITE_DRAGON = "Ancient White Dragon"
		const val SWARM_OF_BATS = "Swarm of Bats"
		const val TYRANNOSAURUS_REX = "Tyrannosaurus Rex"
	}

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
			val repository = TestReferencePreferencesRepository()
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

			viewModel.search(ROLLING_STONE)
			advanceUntilIdle()
			assertFilteredLabels(listOf(ROLLING_STONE), viewModel.uiState.value.filteredTraps) { it.name }

			viewModel.search("+8 bonus")
			advanceUntilIdle()
			assertFilteredLabels(listOf(ROLLING_STONE), viewModel.uiState.value.filteredTraps) { it.name }
		}
	}

	@Test
	fun search_updatesTypedQueryImmediately_andAppliesResultsAfterDebounce() {
		runTest {
			val viewModel = createViewModel()
			advanceUntilIdle()

			viewModel.search(ROLLING_STONE)

			with(viewModel.uiState.value) {
				assertEquals(ROLLING_STONE, searchQuery)
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
				assertEquals(ROLLING_STONE, appliedSearchQuery)
				assertFalse(isSearchPending)
				assertFilteredLabels(listOf(ROLLING_STONE), filteredTraps) { it.name }
			}
		}
	}

	@Test
	fun clearSearch_cancelsPendingDebounce_andRestoresDefaultResultsImmediately() {
		runTest {
			val viewModel = createViewModel()

			viewModel.search(ROLLING_STONE)
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

			viewModel.search(ASSASSINS_BLOOD)
			advanceUntilIdle()
			assertFilteredLabels(listOf(ASSASSINS_BLOOD), viewModel.uiState.value.filteredPoisons) { it.name }

			viewModel.search("1d12")
			advanceUntilIdle()
			assertFilteredLabels(listOf(ASSASSINS_BLOOD), viewModel.uiState.value.filteredPoisons) { it.name }
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
				query = BLINDED,
				detailCategory = ReferenceDetailResolver.CATEGORY_CONDITIONS,
				detailSlug = "blinded"
			)
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals(ReferenceCategory.CORE_RULES, selectedCategory)
				assertEquals(BLINDED, searchQuery)
				assertEquals(BLINDED, selectedReferenceDetail?.title)
				assertEquals(

					ReferenceDetailResolver.CATEGORY_CONDITIONS,
					selectedReferenceDetail?.subtitle

				)
			}
		}
	}

	@Test
	fun initializeFromNavigation_setsMonsterDetailFromLocalCorpus() {
		runTest {
			val viewModel = createViewModel()

			viewModel.initializeFromNavigation(
				category = ReferenceCategory.MONSTERS,
				query = "aboleth",
				detailCategory = ReferenceDetailResolver.CATEGORY_MONSTERS,
				detailSlug = ReferenceDetailResolver.slugFor("Aboleth")
			)
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals(ReferenceCategory.MONSTERS, selectedCategory)
				assertEquals("aboleth", searchQuery)
				assertEquals("Aboleth", selectedReferenceDetail?.title)
			}
		}
	}

	@Test
	fun monsterFilters_limitFilteredMonsterStateByTypeAndCr() {
		runTest {
			val viewModel = createViewModel()

			viewModel.selectCategory(ReferenceCategory.MONSTERS)
			viewModel.updateMonsterCreatureTypeFilter("Dragon")
			viewModel.updateMonsterChallengeRatingFilter("20")
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals("Dragon", selectedMonsterCreatureType)
				assertEquals("20", selectedMonsterChallengeRating)
				assertContainsFilteredLabels(filteredMonsters, { it.name }, ANCIENT_WHITE_DRAGON)
				assertAllFiltered(filteredMonsters, "Expected all filtered monsters to be CR 20 dragons") {
					it.creatureType == "Dragon" && it.challengeRating == "20"
				}
			}
		}
	}

	@Test
	fun monsterGroupFilter_limitsFilteredMonsterStateToAnimalsIncludingSwarmAndDinosaurEntries() {
		runTest {
			val viewModel = createViewModel()

			viewModel.selectCategory(ReferenceCategory.MONSTERS)
			viewModel.updateMonsterGroupFilter(MonsterReferenceCatalog.ANIMAL_GROUP)
			advanceUntilIdle()

			with(viewModel.uiState.value) {
				assertEquals(MonsterReferenceCatalog.ANIMAL_GROUP, selectedMonsterGroup)
				assertContainsFilteredLabels(filteredMonsters, { it.name }, SWARM_OF_BATS, TYRANNOSAURUS_REX)
				assertAllFiltered(filteredMonsters, "Expected all filtered monsters to be in the Animals group") {
					it.group == MonsterReferenceCatalog.ANIMAL_GROUP
				}
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

}
