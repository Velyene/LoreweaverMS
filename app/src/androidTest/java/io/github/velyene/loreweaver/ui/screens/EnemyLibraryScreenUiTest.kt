/*
 * FILE: EnemyLibraryScreenUiTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Enemy Library Content Tests
 * 2. Temporary Enemy Dialog Tests
 * 3. Monster Catalog Test Helpers
 */

package io.github.velyene.loreweaver.ui.screens

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.util.MonsterReferenceCatalog
import io.github.velyene.loreweaver.domain.util.MonsterReferenceEntry
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.DEFAULT_ENEMY_HP
import io.github.velyene.loreweaver.ui.screens.CombatTrackerConstants.DEFAULT_ENEMY_INITIATIVE
import io.github.velyene.loreweaver.ui.theme.LoreweaverTheme
import io.github.velyene.loreweaver.ui.viewmodels.EnemyLibraryUiState
import io.github.velyene.loreweaver.ui.viewmodels.StagedEnemyItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EnemyLibraryScreenUiTest {

	@get:Rule
	val composeRule = createComposeRule()

	private val context: Context = ApplicationProvider.getApplicationContext()

	@Test
	fun enemyLibraryContent_showsMonsterListAndOpensDetail() {
		val monster = sampleMonsterEntry()
		var openedMonster: String? = null
		var stagedMonster: String? = null

		composeRule.setContent {
			LoreweaverTheme {
				EnemyLibraryContent(
					uiState = EnemyLibraryUiState(monsters = listOf(monster)),
					onBack = {},
					onSearchQueryChange = {},
					onGroupSelected = {},
					onCreatureTypeSelected = {},
					onChallengeRatingSelected = {},
					onOpenDetail = { openedMonster = it },
					onStageMonster = { stagedMonster = it },
					onIncrementStagedEnemy = {},
					onAddTemporaryEnemy = { _, _, _, _ -> },
					onRemoveStagedMonster = {},
					onDecrementStagedMonster = {},
					onClearStagedEnemies = {},
					onOpenEncounterSetup = {},
					onCloseDetail = {},
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.enemy_library_title)).assertIsDisplayed()
		composeRule.onNodeWithText(monster.name).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.enemy_library_stage_button)).performClick()
		composeRule.onNodeWithText(monster.name).performClick()

		assertNotNull(openedMonster)
		assertEquals(monster.name, stagedMonster)
	}

	@Test
	fun enemyLibraryContent_showsMonsterDetailWhenSelected() {
		val monsterDetail = sampleMonsterDetail()

		composeRule.setContent {
			LoreweaverTheme {
				EnemyLibraryContent(
					uiState = EnemyLibraryUiState(selectedDetail = monsterDetail),
					onBack = {},
					onSearchQueryChange = {},
					onGroupSelected = {},
					onCreatureTypeSelected = {},
					onChallengeRatingSelected = {},
					onOpenDetail = {},
					onStageMonster = {},
					onIncrementStagedEnemy = {},
					onAddTemporaryEnemy = { _, _, _, _ -> },
					onRemoveStagedMonster = {},
					onDecrementStagedMonster = {},
					onClearStagedEnemies = {},
					onOpenEncounterSetup = {},
					onCloseDetail = {},
				)
			}
		}

		composeRule.onNodeWithText(monsterDetail.title).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.reference_back_to_list)).assertIsDisplayed()
	}

	@Test
	fun enemyLibraryContent_showsRecentStagingAndHandoffPanels() {
		val monster = sampleMonsterEntry()

		composeRule.setContent {
			LoreweaverTheme {
				EnemyLibraryContent(
					uiState = EnemyLibraryUiState(
						monsters = listOf(monster),
						recentEnemies = listOf(monster),
						stagedEnemies = listOf(
							StagedEnemyItem(
								key = "goblin",
								catalogKey = "goblin",
								name = monster.name,
								challengeRating = monster.challengeRating,
								hp = 7,
								initiative = 2,
								quantity = 2,
							),
						),
						stagedEnemyTotalCount = 2,
					),
					onBack = {},
					onSearchQueryChange = {},
					onGroupSelected = {},
					onCreatureTypeSelected = {},
					onChallengeRatingSelected = {},
					onOpenDetail = {},
					onStageMonster = {},
					onIncrementStagedEnemy = {},
					onAddTemporaryEnemy = { _, _, _, _ -> },
					onRemoveStagedMonster = {},
					onDecrementStagedMonster = {},
					onClearStagedEnemies = {},
					onOpenEncounterSetup = {},
					onCloseDetail = {},
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.enemy_library_recent_title)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.enemy_library_staging_title)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.enemy_library_staging_total, 2)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.enemy_library_handoff_button)).assertIsDisplayed()
	}

	@Test
	fun enemyLibraryContent_handoffButtonSendsCurrentStagingToEncounterSetup() {
		val monster = sampleMonsterEntry()
		var handedOffEnemies: List<StagedEnemyItem> = emptyList()

		composeRule.setContent {
			LoreweaverTheme {
				EnemyLibraryContent(
					uiState = EnemyLibraryUiState(
						stagedEnemies = listOf(
							StagedEnemyItem(
								key = "goblin",
								catalogKey = "goblin",
								name = monster.name,
								challengeRating = monster.challengeRating,
								hp = 7,
								initiative = 2,
								quantity = 2,
							),
						),
						stagedEnemyTotalCount = 2,
					),
					onBack = {},
					onSearchQueryChange = {},
					onGroupSelected = {},
					onCreatureTypeSelected = {},
					onChallengeRatingSelected = {},
					onOpenDetail = {},
					onStageMonster = {},
					onIncrementStagedEnemy = {},
					onAddTemporaryEnemy = { _, _, _, _ -> },
					onRemoveStagedMonster = {},
					onDecrementStagedMonster = {},
					onClearStagedEnemies = {},
					onOpenEncounterSetup = { handedOffEnemies = it },
					onCloseDetail = {},
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.enemy_library_handoff_button)).performClick()

		assertEquals(1, handedOffEnemies.size)
		assertEquals(monster.name, handedOffEnemies.single().name)
		assertEquals(2, handedOffEnemies.single().quantity)
	}

	@Test
	fun enemyLibraryContent_temporaryEnemyDialogStagesManualDraft() {
		var stagedName: String? = null
		var stagedHp: Int? = null
		var stagedInitiative: Int? = null
		var stagedQuantity: Int? = null

		composeRule.setContent {
			LoreweaverTheme {
				EnemyLibraryContent(
					uiState = EnemyLibraryUiState(),
					onBack = {},
					onSearchQueryChange = {},
					onGroupSelected = {},
					onCreatureTypeSelected = {},
					onChallengeRatingSelected = {},
					onOpenDetail = {},
					onStageMonster = {},
					onIncrementStagedEnemy = {},
					onAddTemporaryEnemy = { name, hp, initiative, quantity ->
						stagedName = name
						stagedHp = hp
						stagedInitiative = initiative
						stagedQuantity = quantity
					},
					onRemoveStagedMonster = {},
					onDecrementStagedMonster = {},
					onClearStagedEnemies = {},
					onOpenEncounterSetup = {},
					onCloseDetail = {},
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.enemy_library_temporary_enemy_button)).performClick()
		composeRule.onNodeWithText(context.getString(R.string.enemy_library_temp_enemy_dialog_title)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.enemy_library_temp_enemy_dialog_supporting_text)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.name_label)).performTextInput("Clockwork Hound")
		composeRule.onNodeWithText(context.getString(R.string.add_button)).performClick()

		assertEquals("Clockwork Hound", stagedName)
		assertEquals(DEFAULT_ENEMY_HP, stagedHp)
		assertEquals(DEFAULT_ENEMY_INITIATIVE, stagedInitiative)
		assertEquals(1, stagedQuantity)
	}

	private fun sampleMonsterEntry(): MonsterReferenceEntry {
		return requireNotNull(MonsterReferenceCatalog.ALL.firstOrNull()) {
			"Monster catalog is empty"
		}
	}

	private fun sampleMonsterDetail(): io.github.velyene.loreweaver.domain.util.ReferenceDetailContent {
		val monsterName = MonsterReferenceCatalog.ALL
			.firstOrNull { monster -> MonsterReferenceCatalog.resolve(monster.name) != null }
			?.name
			?: error("No monster detail content is currently available")
		return requireNotNull(MonsterReferenceCatalog.resolve(monsterName))
	}
}
