/*
 * FILE: EnemyLibraryViewModel.kt
 *
 * TABLE OF CONTENTS:
 * 1. Staged Enemy and UI State Models
 * 2. Filter and Detail Actions
 * 3. Staging Actions
 * 4. Derived State and Mapping Helpers
 */

package io.github.velyene.loreweaver.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.velyene.loreweaver.domain.util.MonsterReferenceCatalog
import io.github.velyene.loreweaver.domain.util.MonsterReferenceEntry
import io.github.velyene.loreweaver.domain.util.ReferenceDetailContent
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class StagedEnemyItem(
	val key: String,
	val catalogKey: String? = null,
	val name: String,
	val challengeRating: String = "",
	val hp: Int = DEFAULT_STAGED_ENEMY_HP,
	val initiative: Int = DEFAULT_STAGED_ENEMY_INITIATIVE,
	val quantity: Int = 1,
	val isTemporary: Boolean = false,
)

data class EnemyLibraryUiState(
	val searchQuery: String = "",
	val selectedGroup: String? = null,
	val selectedCreatureType: String? = null,
	val selectedChallengeRating: String? = null,
	val monsters: List<MonsterReferenceEntry> = MonsterReferenceCatalog.ALL,
	val recentEnemies: List<MonsterReferenceEntry> = emptyList(),
	val stagedEnemies: List<StagedEnemyItem> = emptyList(),
	val stagedEnemyTotalCount: Int = 0,
	val selectedDetail: ReferenceDetailContent? = null,
)

@HiltViewModel
class EnemyLibraryViewModel @Inject constructor() : ViewModel() {
	private val _uiState = MutableStateFlow(EnemyLibraryUiState())
	val uiState: StateFlow<EnemyLibraryUiState> = _uiState.asStateFlow()

	fun updateSearchQuery(query: String) {
		_uiState.update {
			it.copy(searchQuery = query, selectedDetail = null).refilter()
		}
	}

	fun updateGroupFilter(group: String?) {
		_uiState.update {
			it.copy(selectedGroup = group, selectedDetail = null).refilter()
		}
	}

	fun updateCreatureTypeFilter(creatureType: String?) {
		_uiState.update {
			it.copy(selectedCreatureType = creatureType, selectedDetail = null).refilter()
		}
	}

	fun updateChallengeRatingFilter(challengeRating: String?) {
		_uiState.update {
			it.copy(selectedChallengeRating = challengeRating, selectedDetail = null).refilter()
		}
	}

	fun openMonsterDetail(monsterName: String) {
		_uiState.update {
			it.withRecentEnemy(monsterName).copy(selectedDetail = MonsterReferenceCatalog.resolve(monsterName))
		}
	}

	fun stageMonster(monsterName: String) {
		_uiState.update { state ->
			val key = ReferenceDetailResolver.slugFor(monsterName)
			val monster = MonsterReferenceCatalog.findEntry(key) ?: MonsterReferenceCatalog.findEntry(monsterName)
			if (monster == null) return@update state
			state
				.withRecentEnemy(monster.name)
				.withUpdatedStage(monster.toStagedEnemyItem(), delta = 1)
		}
	}

	fun stageTemporaryEnemy(name: String, hp: Int, initiative: Int, quantity: Int) {
		val trimmedName = name.trim()
		if (trimmedName.isBlank()) return
		_uiState.update { state ->
			state.withUpdatedStage(
				stagedEnemy = StagedEnemyItem(
					key = temporaryEnemyKey(name = trimmedName, hp = hp, initiative = initiative),
					catalogKey = null,
					name = trimmedName,
					challengeRating = "",
					hp = hp.coerceAtLeast(1),
					initiative = initiative,
					quantity = 0,
					isTemporary = true,
				),
				delta = quantity.coerceAtLeast(1),
			)
		}
	}

	fun incrementStagedEnemy(enemyKey: String) {
		_uiState.update { state ->
			val existing = state.stagedEnemies.firstOrNull { it.key == enemyKey } ?: return@update state
			state.withUpdatedStage(existing, delta = 1)
		}
	}

	fun removeStagedMonster(monsterKey: String) {
		_uiState.update { state ->
			state.copy(
				stagedEnemies = state.stagedEnemies.filterNot { it.key == monsterKey },
				stagedEnemyTotalCount = state.stagedEnemies
					.filterNot { it.key == monsterKey }
					.sumOf(StagedEnemyItem::quantity),
			)
		}
	}

	fun decrementStagedMonster(monsterKey: String) {
		_uiState.update { state ->
			val existing = state.stagedEnemies.firstOrNull { it.key == monsterKey } ?: return@update state
			state.withUpdatedStage(existing, delta = -1)
		}
	}

	fun clearStagedEnemies() {
		_uiState.update { it.copy(stagedEnemies = emptyList(), stagedEnemyTotalCount = 0) }
	}

	fun clearMonsterDetail() {
		_uiState.update { it.copy(selectedDetail = null) }
	}

	private fun EnemyLibraryUiState.withRecentEnemy(monsterName: String): EnemyLibraryUiState {
		val updatedRecent = listOf(monsterName) + recentEnemies.map(MonsterReferenceEntry::name)
		return copy(
			recentEnemies = updatedRecent
				.distinctBy { it.lowercase() }
				.mapNotNull { entryName -> MonsterReferenceCatalog.findEntry(entryName) }
				.take(MAX_RECENT_ENEMIES)
		)
	}

	private fun EnemyLibraryUiState.withUpdatedStage(
		stagedEnemy: StagedEnemyItem,
		delta: Int,
	): EnemyLibraryUiState {
		val key = stagedEnemy.key
		val updatedStage = stagedEnemies
			.associateBy(StagedEnemyItem::key)
			.toMutableMap()
			.apply {
				val currentQuantity = this[key]?.quantity ?: 0
				val nextQuantity = (currentQuantity + delta).coerceAtLeast(0)
				if (nextQuantity == 0) {
					remove(key)
				} else {
					this[key] = stagedEnemy.copy(quantity = nextQuantity)
				}
			}
			.values
			.sortedBy(StagedEnemyItem::name)
		return copy(
			stagedEnemies = updatedStage,
			stagedEnemyTotalCount = updatedStage.sumOf(StagedEnemyItem::quantity),
		)
	}

	private fun EnemyLibraryUiState.refilter(): EnemyLibraryUiState {
		return copy(
			monsters = MonsterReferenceCatalog.filter(
				query = searchQuery,
				group = selectedGroup,
				creatureType = selectedCreatureType,
				challengeRating = selectedChallengeRating,
			),
		)
	}

	private companion object {
		const val MAX_RECENT_ENEMIES = 6
	}
}

private fun MonsterReferenceEntry.toStagedEnemyItem(): StagedEnemyItem {
	return StagedEnemyItem(
		key = ReferenceDetailResolver.slugFor(name),
		catalogKey = ReferenceDetailResolver.slugFor(name),
		name = name,
		challengeRating = challengeRating,
		hp = statValue(label = MONSTER_STAT_HP, defaultValue = DEFAULT_STAGED_ENEMY_HP),
		initiative = signedStatValue(
			label = MONSTER_STAT_INITIATIVE,
			defaultValue = DEFAULT_STAGED_ENEMY_INITIATIVE,
		),
		quantity = 1,
		isTemporary = false,
	)
}

private fun MonsterReferenceEntry.statValue(label: String, defaultValue: Int): Int {
	return statRows.firstOrNull { (statLabel, _) -> statLabel == label }
		?.second
		?.let { value -> Regex("\\d+").find(value)?.value?.toIntOrNull() }
		?: defaultValue
}

private fun MonsterReferenceEntry.signedStatValue(label: String, defaultValue: Int): Int {
	return statRows.firstOrNull { (statLabel, _) -> statLabel == label }
		?.second
		?.let { value -> Regex("[+-]?\\d+").find(value)?.value?.toIntOrNull() }
		?: defaultValue
}

private fun temporaryEnemyKey(name: String, hp: Int, initiative: Int): String {
	return buildString {
		append("temp:")
		append(ReferenceDetailResolver.slugFor(name.trim()))
		append(':')
		append(hp.coerceAtLeast(1))
		append(':')
		append(initiative)
	}
}

private const val DEFAULT_STAGED_ENEMY_HP = 10
private const val DEFAULT_STAGED_ENEMY_INITIATIVE = 0
private const val MONSTER_STAT_HP = "HP"
private const val MONSTER_STAT_INITIATIVE = "Initiative"

