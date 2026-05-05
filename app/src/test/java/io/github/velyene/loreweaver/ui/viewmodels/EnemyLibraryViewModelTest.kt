package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.domain.util.MonsterReferenceCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EnemyLibraryViewModelTest {

	@Test
	fun stageMonster_usesReferenceStatsForEncounterSetupDraft() {
		val viewModel = EnemyLibraryViewModel()
		val goblin = requireNotNull(
			MonsterReferenceCatalog.ALL.firstOrNull { monster ->
				monster.statRows.any { it.first == "HP" } && monster.statRows.any { it.first == "Initiative" }
			},
		)
		val expectedHp = Regex("\\d+").find(
			requireNotNull(goblin.statRows.firstOrNull { it.first == "HP" }?.second),
		)?.value?.toInt()
		val expectedInitiative = Regex("[+-]?\\d+").find(
			requireNotNull(goblin.statRows.firstOrNull { it.first == "Initiative" }?.second),
		)?.value?.toInt()

		viewModel.stageMonster(goblin.name)

		val stagedEnemy = viewModel.uiState.value.stagedEnemies.single()
		assertEquals(goblin.name, stagedEnemy.name)
		assertEquals(expectedHp, stagedEnemy.hp)
		assertEquals(expectedInitiative, stagedEnemy.initiative)
		assertEquals(1, stagedEnemy.quantity)
		assertFalse(stagedEnemy.isTemporary)
		assertEquals(io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver.slugFor(goblin.name), stagedEnemy.catalogKey)
	}

	@Test
	fun stageTemporaryEnemy_createsEditableTemporaryDraftAndQuantityCanBeAdjusted() {
		val viewModel = EnemyLibraryViewModel()

		viewModel.stageTemporaryEnemy(name = "Clockwork Hound", hp = 18, initiative = 3, quantity = 2)
		val stagedEnemy = viewModel.uiState.value.stagedEnemies.single()

		assertEquals("Clockwork Hound", stagedEnemy.name)
		assertEquals(18, stagedEnemy.hp)
		assertEquals(3, stagedEnemy.initiative)
		assertEquals(2, stagedEnemy.quantity)
		assertTrue(stagedEnemy.isTemporary)
		assertEquals(null, stagedEnemy.catalogKey)
		assertEquals(2, viewModel.uiState.value.stagedEnemyTotalCount)

		viewModel.incrementStagedEnemy(stagedEnemy.key)
		assertEquals(3, viewModel.uiState.value.stagedEnemies.single().quantity)

		viewModel.decrementStagedMonster(stagedEnemy.key)
		assertEquals(2, viewModel.uiState.value.stagedEnemies.single().quantity)
	}

	@Test
	fun stageTemporaryEnemy_trimsNameAndCoercesMinimumHpAndQuantity() {
		val viewModel = EnemyLibraryViewModel()

		viewModel.stageTemporaryEnemy(name = "  Scout Drone  ", hp = 0, initiative = 4, quantity = 0)

		val stagedEnemy = viewModel.uiState.value.stagedEnemies.single()
		assertEquals("Scout Drone", stagedEnemy.name)
		assertEquals(1, stagedEnemy.hp)
		assertEquals(1, stagedEnemy.quantity)
		assertTrue(stagedEnemy.isTemporary)
	}
}



