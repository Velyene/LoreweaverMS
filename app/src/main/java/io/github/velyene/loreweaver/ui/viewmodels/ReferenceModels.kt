package io.github.velyene.loreweaver.ui.viewmodels

import androidx.annotation.Keep
import io.github.velyene.loreweaver.domain.util.DiseaseReference
import io.github.velyene.loreweaver.domain.util.DiseaseTemplate
import io.github.velyene.loreweaver.domain.util.MadnessDuration
import io.github.velyene.loreweaver.domain.util.MonsterReferenceCatalog
import io.github.velyene.loreweaver.domain.util.MonsterReferenceEntry
import io.github.velyene.loreweaver.domain.util.PoisonReference
import io.github.velyene.loreweaver.domain.util.PoisonTemplate
import io.github.velyene.loreweaver.domain.util.ReferenceDetailContent
import io.github.velyene.loreweaver.domain.util.SpellcasterType
import io.github.velyene.loreweaver.domain.util.TrapReference
import io.github.velyene.loreweaver.domain.util.TrapTemplate
import kotlinx.serialization.Serializable

/**
 * Reference categories
 */
@Keep
@Serializable
enum class ReferenceCategory {
	TRAPS,
	POISONS,
	DISEASES,
	SPELLCASTING,
	OBJECTS,
	MADNESS,
	MONSTERS,
	CORE_RULES,
	CHARACTER_CREATION
}

/**
 * UI state for reference screen
 */
data class ReferenceUiState(
	val selectedCategory: ReferenceCategory = ReferenceCategory.TRAPS,
	val searchQuery: String = "",
	val appliedSearchQuery: String = "",
	val isSearchPending: Boolean = false,
	val filteredTraps: List<TrapTemplate> = TrapReference.TEMPLATES,
	val filteredPoisons: List<PoisonTemplate> = PoisonReference.TEMPLATES,
	val filteredDiseases: List<DiseaseTemplate> = DiseaseReference.TEMPLATES,
	val filteredMonsters: List<MonsterReferenceEntry> = MonsterReferenceCatalog.ALL,
	val showFavoritesOnly: Boolean = false,
	val selectedMonsterGroup: String? = null,
	val selectedMonsterCreatureType: String? = null,
	val selectedMonsterChallengeRating: String? = null,
	val favoriteTrapNames: Set<String> = emptySet(),
	val favoritePoisonNames: Set<String> = emptySet(),
	val favoriteDiseaseNames: Set<String> = emptySet(),
	val selectedSpellcasterType: SpellcasterType = SpellcasterType.WIZARD,
	val spellcastingAbilityModifierInput: String = "4",
	val spellcastingProficiencyBonusInput: String = "4",
	val spellcastingBonusInput: String = "0",
	val spellcastingCasterLevelInput: String = "5",
	val selectedTrap: TrapTemplate? = null,
	val selectedPoison: PoisonTemplate? = null,
	val selectedDisease: DiseaseTemplate? = null,
	val selectedReferenceDetail: ReferenceDetailContent? = null,
	val selectedMadnessDuration: MadnessDuration = MadnessDuration.SHORT_TERM,
	val madnessLastRoll: Int? = null,
	val madnessLastResult: String? = null
)

