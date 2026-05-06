package io.github.velyene.loreweaver.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import com.google.gson.Gson

internal data class EnemyLibraryEncounterSetupDraft(
	val stagedEnemies: List<StagedEnemyItem> = emptyList(),
) {
	val totalEnemyCount: Int
		get() = stagedEnemies.sumOf(StagedEnemyItem::quantity)

	val temporaryEnemyCount: Int
		get() = stagedEnemies.filter(StagedEnemyItem::isTemporary).sumOf(StagedEnemyItem::quantity)
}

private data class EnemyLibraryEncounterSetupDraftEnvelope(
	val version: Int = ENEMY_LIBRARY_SETUP_DRAFT_VERSION,
	val stagedEnemies: List<StagedEnemyItem> = emptyList(),
)

internal const val ENEMY_LIBRARY_SETUP_DRAFT_KEY = "enemy_library_setup_draft"
private const val ENEMY_LIBRARY_SETUP_DRAFT_VERSION = 1

internal fun stashEnemyLibraryEncounterSetupDraft(
	savedStateHandle: SavedStateHandle,
	stagedEnemies: List<StagedEnemyItem>,
	gson: Gson = Gson(),
) {
	val payload = EnemyLibraryEncounterSetupDraftEnvelope(
		stagedEnemies = stagedEnemies.map { it.copy() },
	)
	savedStateHandle[ENEMY_LIBRARY_SETUP_DRAFT_KEY] = gson.toJson(payload)
}

internal fun consumeEnemyLibraryEncounterSetupDraft(
	savedStateHandle: SavedStateHandle?,
	gson: Gson = Gson(),
): EnemyLibraryEncounterSetupDraft {
	val payload = savedStateHandle?.remove<String>(ENEMY_LIBRARY_SETUP_DRAFT_KEY).orEmpty()
	if (payload.isBlank()) return EnemyLibraryEncounterSetupDraft()

	return runCatching {
		val decoded = gson.fromJson(payload, EnemyLibraryEncounterSetupDraftEnvelope::class.java)
		if (decoded.version != ENEMY_LIBRARY_SETUP_DRAFT_VERSION) {
			EnemyLibraryEncounterSetupDraft()
		} else {
			EnemyLibraryEncounterSetupDraft(
				stagedEnemies = decoded.stagedEnemies.map { it.copy() },
			)
		}
	}.getOrElse {
		EnemyLibraryEncounterSetupDraft()
	}
}

